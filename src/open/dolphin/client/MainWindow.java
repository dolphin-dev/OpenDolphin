/*
 * MainWindow.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2007 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import org.apache.log4j.Logger;

import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.plugin.*;
import open.dolphin.plugin.helper.ComponentMemory;
import open.dolphin.plugin.helper.MenuBarBuilder;
import open.dolphin.plugin.helper.WindowSupport;
import open.dolphin.plugin.helper.MenuSupport;
import open.dolphin.project.*;
import open.dolphin.server.PVTClientServer;
import open.dolphin.util.ReflectMonitor;

/**
 * アプリケーションのメインウインドウクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MainWindow {
    
    // Global Property関係
    private HashMap<String, Color> eventColorTable;
    
    // Window と Menu サポート
    private WindowSupport windowSupport;
    
    // Mediator
    private Mediator mediator;
    
    // 状態制御
    private StateManager stateMgr;
    
    // Plugin コンテナとして使用するもの
    // プラグポイント名
    private final String MY_PLUG_POINT = "mainWindow/comp";
    
    // アクティブになっているプラグインを管理するテーブル
    private Hashtable<String, IMainWindowPlugin> activeChildren;
    
    // TabbedPane に格納する plugin
    private ArrayList<PluginReference> children;
    
    // TabbedPaneでアクティブになっているプラグイン
    private Hashtable<String, IMainWindowPlugin> activeTabbedChildren;
    
    // pluginを格納する tabbedPane
    private JTabbedPane tabbedPane;
    
    // timerTask 関連
    private javax.swing.Timer taskTimer;
    
    // プラグインを lookup するためのコンテキスト
    private IPluginContext pluginCtx;
    
    // ロガー
    private Logger bootLogger;
    
    // プリンターセットアップはMainWindowのみで行い、設定された PageFormat各プラグインが使用する
    private PageFormat pageFormat;
    
    // 環境設定用の Properties
    private Properties saveEnv;
    
    // 受付受信サーバ
    private PVTClientServer pvtServer;
    
    /** BlockGlass */
    private BlockGlass blockGlass;
    
    /** StampBox */
    private StampBoxPlugin stampBox;
    
    
    /**
     * Creates new MainWindow
     */
    public MainWindow() {
        
        // プラグインコンテキストを取得する
        pluginCtx = ClientContext.getPluginContext();
        
        // ロガーを取得する
        bootLogger = ClientContext.getLogger("boot");
        
        // プロジェクトスタブを生成する
        Project.setProjectStub(new ProjectStub());
        
        // Mac Application Menu
        com.apple.eawt.Application fApplication = com.apple.eawt.Application.getApplication();
        fApplication.setEnabledPreferencesMenu(true);
        fApplication.addApplicationListener(
                new com.apple.eawt.ApplicationAdapter() {
            public void handleAbout(com.apple.eawt.ApplicationEvent e) {
                showAbout();
                e.setHandled(true);
            }
            public void handleOpenApplication(
                com.apple.eawt.ApplicationEvent e) {
            }
            public void handleOpenFile(com.apple.eawt.ApplicationEvent e) {
            }
            public void handlePreferences(
                    com.apple.eawt.ApplicationEvent e) {
                doPreference();
            }
            public void handlePrintFile(
                com.apple.eawt.ApplicationEvent e) {
            }
            public void handleQuit(com.apple.eawt.ApplicationEvent e) {
                processWindowClosing();
            }
        });
    }
    
    public JFrame getFrame() {
        return windowSupport.getFrame();
    }
    
    public PageFormat getPageFormat() {
        if (pageFormat == null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob != null) {
                pageFormat = printJob.defaultPage();
            }
        }
        return pageFormat;
    }
    
    public HashMap<String, Color> getEventColorTable() {
        return eventColorTable;
    }
    
    public void setEventColorTable(HashMap<String, Color> table) {
        eventColorTable = table;
    }
    
    /**
     * ブロックする。
     */
    public void block() {
        blockGlass.block();
    }
    
    /**
     * ブロックを解除する。
     */
    public void unblock() {
        blockGlass.unblock();
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        // ログインダイアログを表示し認証を行う
        LoginDialog login = new LoginDialog();
        login.addPropertyChangeListener("LOGIN_PROP", ProxyPropertyChangeListener.create(this, "loginResult"));
        login.start();
    }
    
    /**
     * 認証結果の通知を受け、処理の続行を決める。
     */
    public void loginResult(Object newValue) {
            
        LoginDialog.LoginStatus result = (LoginDialog.LoginStatus) newValue;
        
        switch (result) {
            case AUTHENTICATED:
                startApp();
                break;
            case NOT_AUTHENTICATED:
                System.exit(1);
                break;
            case CANCELD:
                System.exit(1);
                break;
        }
    }
    
    /**
     * アプリケーションを開始する。
     * バックグラウンドタスクとEDTタスクの接続を行う。
     */
    private void startApp() {
        
        ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(this, 
                         "startServices", 
                         (Class[]) null, 
                         (Object[]) null);
        rm.setMonitor(null, "OpenDolphin", "初期化しています...  ", 200, 60*1000);
        
        // ReflectMonitor の結果State property の束縛リスナを生成する
        PropertyChangeListener pl = new PropertyChangeListener() {
           
            public void propertyChange(PropertyChangeEvent e) {
                
                int state = ((Integer) e.getNewValue()).intValue();
                
                switch (state) {
                    
                    case ReflectMonitor.DONE:
                        // EDT からコールされる
                        start2(true);
                        break;
                        
                    case ReflectMonitor.TIME_OVER:
                        System.exit(1);
                        break;
                        
                    case ReflectMonitor.CANCELED:
                        System.exit(1);
                        break;
                }
            }
        };
        rm.addPropertyChangeListener(pl);
        
        rm.start();
    }
    
    /**
     * 起動時のバックグラウンドで実行されるべきタスクを行う。
     */
    public void startServices() {
        
        try {
            // MainWindow の TabbedPane に格納する plugin をlookupする
            Collection<PluginReference> c = pluginCtx.listPluginReferences(MY_PLUG_POINT);
            children = new ArrayList<PluginReference>(c);
            
            // プラグイン管理用のマップを生成する
            activeTabbedChildren = new Hashtable<String, IMainWindowPlugin>();
            activeChildren = new Hashtable<String, IMainWindowPlugin>();
            
            // StampBox を起動しStampTreeを読み込むまで行う
            // GUI を構築するstart()はEDTからコールする。
            stampBox = (StampBoxPlugin) pluginCtx.lookup(GUIConst.JNDI_STAMP_BOX);
            stampBox.setContext(this);
            stampBox.loadStampTrees();
            
            // 環境設定ダイアログで変更される場合があるので保存する
            saveEnv = new Properties();
            
            // PVT Sever を起動する
            if (Project.getUseAsPVTServer()) {
                pvtServer = new PVTClientServer();
                pvtServer.startService();
                saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_RUNNING);

            } else {
                saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_NOT_RUNNING);
            }

            // CLAIM送信を生成する
            if (Project.getSendClaim()) {
                createPlugin(GUIConst.JNDI_SEND_CLAIM);
                saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_RUNNING);

            } else {
                saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_NOT_RUNNING);
            }
            if (Project.getClaimAddress() != null) {
                saveEnv.put(GUIConst.ADDRESS_CLAIM, Project.getClaimAddress());
            }

            // MML送信を生成する
            if (Project.getSendMML()) {
                createPlugin(GUIConst.JNDI_SEND_MML);
                saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_RUNNING);

            } else {
                saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_NOT_RUNNING);
            }
            if (Project.getCSGWPath() != null) {
                saveEnv.put(GUIConst.CSGW_PATH, Project.getCSGWPath());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            bootLogger.fatal(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 認証成功後にスタートの次のフェーズを実行する。
     * @param loginState
     */
    private void start2(Boolean login) {
        
        // イベントカラーを定義する
        HashMap<String, Color> cTable = new HashMap<String, Color>(10, 0.75f);
        cTable.put("TODAY", ClientContext.getColor("color.TODAY_BACK"));
        cTable.put("BIRTHDAY", ClientContext.getColor("color.BIRTHDAY_BACK"));
        cTable.put("PVT", ClientContext.getColor("color.PVT"));
        cTable.put("DOC_HISTORY", ClientContext.getColor("color.PVT"));
        setEventColorTable(cTable);
        
        // StateMgr を生成する
        stateMgr = new StateManager();
        
        // 設定に必要な定数うぃコンテキストから取得する
        String windowTitle = ClientContext.getString("mainWindow.title");
        int defaultX = ClientContext.getInt("mainWindow.defaultX");
        int defaultY = ClientContext.getInt("mainWindow.defaultY");
        int defaultWidth = ClientContext.getInt("mainWindow.defaultWidth");
        int defaultHeight = ClientContext.getInt("mainWindow.defaultHeight");
        
        // WindowSupport を生成する この時点で Frame,WindowMenu を持つMenuBar が生成されている
        String title = ClientContext.getFrameTitle(windowTitle);
        windowSupport = WindowSupport.create(title);
        JFrame myFrame = windowSupport.getFrame();		// MainWindow の JFrame
        JMenuBar myMenuBar = windowSupport.getMenuBar();	// MainWindow の JMenuBar
        
        // Windowにこのクラス固有の設定をする
        Point loc = new Point(defaultX, defaultY);
        Dimension size = new Dimension(defaultWidth, defaultHeight);
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                processWindowClosing();
            }
        });
        ComponentMemory cm = new ComponentMemory(myFrame, loc, size, this);
        cm.setToPreferenceBounds();
        // BlockGlass を設定する
        blockGlass = new BlockGlass();
        myFrame.setGlassPane(blockGlass);
        
        // mainWindowのメニューを生成しメニューバーに追加する
        mediator = new Mediator(this);
        createMenuBar(myMenuBar, mediator);
        
        // mainWindowのコンテントGUIを生成しFrameに追加する
        tabbedPane = new JTabbedPane();
        JPanel content = new JPanel(new BorderLayout());
        content.add(tabbedPane, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        myFrame.getContentPane().add(content);
        
        // Pluginを生成する
        try {
            // PluginSpec を tab に格納する
            for (PluginReference plRef : children) {
                tabbedPane.addTab((String) plRef.getAddrContent(PluginReference.TITLE), null);
            }
            
            // Tab の切り替えで plugin が Factory で生成されるようにする
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            tabbedPane.addChangeListener(ProxyChangeListener.create(this, "tabSelectionChanged"));
            tabbedPane.setSelectedIndex(0);
            
            // StaeMagrを使用してメインウインドウの状態を制御する
            stateMgr.processLogin(login);
            
            // 可視化する
            stampBox.start();
            stampBox.getFrame().setVisible(true);
            windowSupport.getFrame().setVisible(true);
            
            // 受付リストを開始する
            WatingListPlugin watingList = (WatingListPlugin) getPlugin(GUIConst.JNDI_WATING_LIST);
            watingList.restartCheckTimer();
            
        } catch (Exception e) {
            bootLogger.fatal(e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * プラグインの遅延生成をタブの切り替えで処理する。
     */
    public void tabSelectionChanged() {
            
        try {
            int index = tabbedPane.getSelectedIndex();
            String key = String.valueOf(index);
            IMainWindowPlugin plugin = activeTabbedChildren.get(key);

            // まだ plugin が生成されていない場合
            if (plugin == null) {
                PluginReference plRef = children.get(index);
                plugin = (IMainWindowPlugin) pluginCtx.lookup(plRef.getJndiName());
                plugin.setContext(MainWindow.this);
                plugin.start();
                tabbedPane.setComponentAt(index, plugin.getUI());

                // この plugin の key を得、children にバインドする
                activeTabbedChildren.put(key, plugin);
                bootLogger.info("Plugin を生成しました: " + key);
            }
            // 既に生成されている場合
            else {
                plugin.enter();
            }

            // chain に加える
            mediator.addChain(plugin);

        } catch (Exception ex) {
            ex.printStackTrace();
            bootLogger.warn(ex.getMessage());
        }
    }

    
    // ////////////// プラグイン管理メソッド ///////////////////////////
    
    public Collection listChildren() {
        try {
            return pluginCtx.listPluginReferences(MY_PLUG_POINT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Collection listChildrenNames() {
        try {
            return pluginCtx.listPluginNames(MY_PLUG_POINT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * プラグインを生成し起動する。
     * @param jndiName 生成するプラグインの JNDI ネーム
     */
    public void createPlugin(String jndiName) {
        try {
            IMainWindowPlugin plugin = (IMainWindowPlugin) pluginCtx.lookup(jndiName);
            plugin.setContext(this);
            plugin.start();
        } catch (Exception e) {
            e.printStackTrace();
            bootLogger.warn(e.getMessage());
        }
    }
    
    public IMainWindowPlugin getPlugin(String jndiName) {
        return activeChildren.get(jndiName);
    }
    
    public void pluginStarted(IMainWindowPlugin plugin) {
        if (plugin != null) {
            String name = plugin.getJNDIname();
            bootLogger.info(name + " started");
            activeChildren.put(name, plugin);
        }
    }
    
    public void pluginStopped(IMainWindowPlugin plugin) {
        if (plugin != null) {
            String name = plugin.getJNDIname();
            bootLogger.info(name + " stopped");
            activeChildren.remove(plugin.getJNDIname());
            plugin = null;
        }
    }
    
    //////////////////////////////////////////////////////////
    
    /**
     * カルテをオープンする。
     * @param pvt 患者来院情報
     */
    public void openKarte(PatientVisitModel pvt) {
        
        try {
            IChart chart = (IChart) pluginCtx.lookup(GUIConst.JNDI_CHART);
            chart.setContext(MainWindow.this);
            chart.setPatientVisit(pvt);                 //
            chart.setReadOnly(Project.isReadOnly());    // RedaOnlyProp
            chart.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            bootLogger.warn(e.getMessage());
        }
    }
    
    /**
     * MainWindow のアクションを返す。
     * @param name Action名
     * @return Action
     */
    public Action getAction(String name) {
        return mediator.getAction(name);
    }
    
    // //////////////// メニューバーサポートの実装 /////////////////
    
    /**
     * アプリケーションのメニューバー及び ToolPanel を生成する。 アプリケーションメニューバー = MainWindowMB +
     * Charet/EditoFrame MB （Windows から見れば 二つの Windowの メニューが一つに合成されている）
     * 夫々のメニュー項目(Action)のターゲットは異なる。
     */
    public Object[] createMenuBar(JMenuBar menuBar, MenuSupport requester) {
        
        try {
            boolean mac = ClientContext.isMac();
            
            Hashtable<String, Action> mainActions = null;
            Hashtable<String, Action> editorActions = null;
            
            
            // Action は共通。MenuBarはウインドウ毎に生成する。
            // i.e. メインウインドウ、チャートウインドウにこれらのアクションを持つメニューバーを生成する。
            if (mainActions == null) {
                
                mainActions = new Hashtable<String, Action>();
                
                ReflectAction action = new ReflectAction("ページ設定...", this, GUIConst.PRINTER_SETUP);
                mainActions.put(GUIConst.PRINTER_SETUP, action);
                
                // Mac の場合、終了とアバウトはアプリケーションメニュー
                if (!mac) {
                    action = new ReflectAction("アバウト...", this, GUIConst.SHOW_ABOUT);
                    mainActions.put(GUIConst.SHOW_ABOUT, action);
                    
                    action = new ReflectAction("終了", this, GUIConst.EXIT);
                    mainActions.put(GUIConst.EXIT, action);
                }
                
                action = new ReflectAction("パスワード変更...", this, GUIConst.CHANGE_PASSWORD);
                mainActions.put(GUIConst.CHANGE_PASSWORD, action);
                
                action = new ReflectAction("ユーザ登録...", this, GUIConst.ADD_USER);
                mainActions.put(GUIConst.ADD_USER, action);
                
                action = new ReflectAction("アップデート確認...", this, GUIConst.UPDATE_SOFTWARE);
                mainActions.put(GUIConst.UPDATE_SOFTWARE, action);
                
                action = new ReflectAction("ドルフィンサポート", this, GUIConst.BROWSE_DOLPHIN_SUPPORT);
                mainActions.put(GUIConst.BROWSE_DOLPHIN_SUPPORT, action);
                
                action = new ReflectAction("ドルフィンプロジェクト", this, GUIConst.BROWSE_DOLPHIN_PROJECT);
                mainActions.put(GUIConst.BROWSE_DOLPHIN_PROJECT, action);
                
                action = new ReflectAction("MedXMLコンソーシアム", this, GUIConst.BROWSE_MEDXML);
                mainActions.put(GUIConst.BROWSE_MEDXML, action);
                
                action = new ReflectAction("環境設定", this, GUIConst.SET_KARTE_ENV);
                mainActions.put(GUIConst.SET_KARTE_ENV, action);
                
                action = new ReflectAction("スタンプボックス", this, GUIConst.SHOW_STAMP_BOX);
                mainActions.put(GUIConst.SHOW_STAMP_BOX, action);
                
                action = new ReflectAction("シェーマボックス", this, GUIConst.SHOW_SCHEMA_BOX);
                mainActions.put(GUIConst.SHOW_SCHEMA_BOX, action);
            }
            
            if (editorActions == null) {
                
                editorActions = new Hashtable<String, Action>();
                
                // サイズサブメニューアクションを生成する
                SubMenuAction subActtion = new SubMenuAction("サイズ");
                editorActions.put(GUIConst.ACTION_SIZE, subActtion);
                
                // スタイルサブメニューアクションを生成する
                subActtion = new SubMenuAction("スタイル");
                editorActions.put(GUIConst.ACTION_STYLE, subActtion);
                
                // 行揃えサブメニューアクションを生成する
                subActtion = new SubMenuAction("行揃え");
                editorActions.put(GUIConst.ACTION_ALIGNMENT, subActtion);
                
                // カラーサブメニューアクションを生成する
                subActtion = new SubMenuAction("カラー");
                editorActions.put(GUIConst.ACTION_COLOR, subActtion);
                
                if (requester instanceof ChartMediator) {
                
                    // Red
                    Action action = new StyledEditorKit.ForegroundAction("レッド", ClientContext.getColor("color.set.default.red"));
                    editorActions.put(GUIConst.ACTION_RED, action);

                    // OR
                    action = new StyledEditorKit.ForegroundAction("オレンジ", ClientContext.getColor("color.set.default.orange"));
                    editorActions.put(GUIConst.ACTION_ORANGE, action);

                    // Y
                    action = new StyledEditorKit.ForegroundAction("イェロー", ClientContext.getColor("color.set.default.yellow"));
                    editorActions.put(GUIConst.ACTION_YELLOW, action);

                    // Green
                    action = new StyledEditorKit.ForegroundAction("グリーン", ClientContext.getColor("color.set.default.green"));
                    editorActions.put(GUIConst.ACTION_GREEN, action);

                    // Blue
                    action = new StyledEditorKit.ForegroundAction("ブルー", ClientContext.getColor("color.set.default.blue"));
                    editorActions.put(GUIConst.ACTION_BLUE, action);

                    // Purpule
                    action = new StyledEditorKit.ForegroundAction("パープル", ClientContext.getColor("color.set.default.purpule"));
                    editorActions.put(GUIConst.ACTION_PURPLE, action);

                    // Gray
                    action = new StyledEditorKit.ForegroundAction("グレー", ClientContext.getColor("color.set.default.gray"));
                    editorActions.put(GUIConst.ACTION_GRAY, action);

                    // 9
                    action = new StyledEditorKit.FontSizeAction("9", 9);
                    editorActions.put(GUIConst.ACTION_S9, action);

                    // 10
                    action = new StyledEditorKit.FontSizeAction("10", 10);
                    editorActions.put(GUIConst.ACTION_S10, action);

                    // 12
                    action = new StyledEditorKit.FontSizeAction("12", 12);
                    editorActions.put(GUIConst.ACTION_S12, action);

                    // 14
                    action = new StyledEditorKit.FontSizeAction("14", 14);
                    editorActions.put(GUIConst.ACTION_S14, action);

                    // 18
                    action = new StyledEditorKit.FontSizeAction("18", 18);
                    editorActions.put(GUIConst.ACTION_S18, action);

                    // 24
                    action = new StyledEditorKit.FontSizeAction("24", 24);
                    editorActions.put(GUIConst.ACTION_S24, action);

                    // 36
                    action = new StyledEditorKit.FontSizeAction("36", 36);
                    editorActions.put(GUIConst.ACTION_S36, action);

                    // Bold
                    action = new StyledEditorKit.BoldAction();
                    editorActions.put(GUIConst.ACTION_BOLD, action);

                    // Italic
                    action = new StyledEditorKit.ItalicAction();
                    editorActions.put(GUIConst.ACTION_ITALIC, action);

                    // Underline
                    action = new StyledEditorKit.UnderlineAction();
                    editorActions.put(GUIConst.ACTION_UNDERLINE, action);

                    // Left
                    action = new StyledEditorKit.AlignmentAction("左揃え", StyleConstants.ALIGN_LEFT);
                    editorActions.put(GUIConst.ACTION_LEFT_ALIGN, action);

                    // Center
                    action = new StyledEditorKit.AlignmentAction("中央揃え", StyleConstants.ALIGN_CENTER);
                    editorActions.put(GUIConst.ACTION_CENTER_ALIGN, action);

                    // Right
                    action = new StyledEditorKit.AlignmentAction("右揃え", StyleConstants.ALIGN_RIGHT);
                    editorActions.put(GUIConst.ACTION_RIGHT_ALIGN, action);
                }
            }
            
            // Local Action 用のアクションテーブルを生成する
            Hashtable<String, Action> actions = new Hashtable<String, Action>();
            
            // Global Actionを追加する
            Enumeration<String> enums = mainActions.keys();
            while (enums.hasMoreElements()) {
                String key = enums.nextElement();
                actions.put(key, mainActions.get(key));
            }
            
            // エディタアクションを追加する
            enums = editorActions.keys();
            while (enums.hasMoreElements()) {
                String key = enums.nextElement();
                actions.put(key, editorActions.get(key));
            }
            
            // MenuTarget
            Hashtable<String, Object> menuTargets = new Hashtable<String, Object>(3, 1.0f);
            
            // Chain target を登録する
            menuTargets.put("chain", requester);
            
            // メニューリスナーを設定する
            // ex. display_listener は display Menu には MenuListener があることを指示する
            // ChartMediator の場合 挿入メニューとテキストメニューに menuListener を設定する
            if (requester instanceof ChartMediator) {
                menuTargets.put("insert_listener", requester);
                menuTargets.put("text_listener", requester);
            }
            
            // MenuBarBuilder を生成しメニューを構築する
            MenuBarBuilder builder = (MenuBarBuilder) pluginCtx.lookup(GUIConst.JNDI_MENUBAR_BUILDER);
            builder.setMenuBar(menuBar);
            builder.setActions(actions);
            builder.setTargets(menuTargets);
            builder.build(ClientContext.getMenuBarResource());
            
            // MenuBar生成を要求したWindowのmediator にActionの登録をさせる
            requester.registerActions(actions);
            
            Object[] ret = new Object[3];
            ret[0] = builder.getJMenuBar();
            ret[1] = builder.getToolPanel();
            builder.close();
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            bootLogger.warn(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    // ///////////// MainWindow が実行するメニューの実装 ///////////////////////
    
    /**
     * プリンターをセットアップする。
     */
    public void printerSetup() {
        
        Runnable r = new Runnable() {
            public void run() {
                PrinterJob printJob = PrinterJob.getPrinterJob();
                if (pageFormat != null) {
                    pageFormat = printJob.pageDialog(pageFormat);
                } else {
                    pageFormat = printJob.defaultPage();
                    pageFormat = printJob.pageDialog(pageFormat);
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * 認証結果の通知を受け、アプリケーションの状態を制御する
     */
    class ConnectListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            
            if (e.getPropertyName().equals("LOGIN_PROP")) {
                
                LoginDialog.LoginStatus result = ((LoginDialog.LoginStatus) e
                        .getNewValue());
                switch (result) {
                    case AUTHENTICATED:
                        stateMgr.processLogin(true);
                        break;
                    case NOT_AUTHENTICATED:
                        System.exit(1);
                        break;
                    case CANCELD:
                        break;
                }
            }
        }
    }
    
    /**
     * カルテの環境設定を行う。
     */
    public void setKarteEnviroment() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.start();
        //Thread t = new Thread(sd);
        //t.setPriority(Thread.NORM_PRIORITY);
        //t.start();
    }
    
    /**
     * CloseBox処理を行う。
     */
    public void processWindowClosing() {
        exit();
    }
    
    /**
     * 環境設定を行う。
     */
    public void doPreference() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.addPropertyChangeListener("SETTING_PROP", new PreferenceListener());
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject(null);
        sd.start();
        //Thread t = new Thread(sd);
        //t.setPriority(Thread.NORM_PRIORITY);
        //t.start();
    }
    
    /**
     * 環境設定のリスナクラス。環境設定が終了するとここへ通知される。
     */
    class PreferenceListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            
            if (e.getPropertyName().equals("SETTING_PROP")) {
                
                boolean valid = ((Boolean) e.getNewValue()).booleanValue();
                
                if (valid) {
                    
                    // 設定の変化を調べ、サービスの制御を行う
                    ArrayList<String> messages = new ArrayList<String>(2);
                    
                    // PvtServer
                    boolean oldRunning = saveEnv.getProperty(GUIConst.KEY_PVT_SERVER).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    boolean newRun = Project.getUseAsPVTServer();
                    boolean start = ( (!oldRunning) && newRun ) ? true : false;
                    boolean stop = ( (oldRunning) && (!newRun) ) ? true : false;
                    
                    if (start) {
                        pvtServer = new PVTClientServer();
                        //pvtServer.setUser(Project.getUserModel());
                        pvtServer.startService();
                        saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_RUNNING);
                        messages.add("受付受信を開始しました。");
                    }
                    else if (stop) {
                        pvtServer.stopService();
                        saveEnv.put(GUIConst.KEY_PVT_SERVER, GUIConst.SERVICE_NOT_RUNNING);
                        messages.add("受付受信を停止しました。");
                    }
                    
                    // SendClaim
                    oldRunning = saveEnv.getProperty(GUIConst.KEY_SEND_CLAIM).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    newRun = Project.getSendClaim();
                    start = ( (!oldRunning) && newRun ) ? true : false;
                    stop = ( (oldRunning) && (!newRun) ) ? true : false;
                    
                    boolean restart = false;
                    String oldAddress = saveEnv.getProperty(GUIConst.ADDRESS_CLAIM);
                    String newAddress = Project.getClaimAddress();
                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
                        restart = true;
                    }
                    
                    if (start) {
                        createPlugin(GUIConst.JNDI_SEND_CLAIM);
                        saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_RUNNING);
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信を開始しました。(送信アドレス=" + newAddress + ")");
                        
                    } else if (stop) {
                        SendClaimPlugin sendClaim = (SendClaimPlugin) getPlugin(GUIConst.JNDI_SEND_CLAIM);
                        sendClaim.stop();
                        saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_NOT_RUNNING);
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信を停止しました。");
                        
                    } else if (restart) {
                        SendClaimPlugin sendClaim = (SendClaimPlugin) getPlugin(GUIConst.JNDI_SEND_CLAIM);
                        sendClaim.stop();
                        createPlugin(GUIConst.JNDI_SEND_CLAIM);
                        saveEnv.put(GUIConst.KEY_SEND_CLAIM, GUIConst.SERVICE_RUNNING);
                        saveEnv.put(GUIConst.ADDRESS_CLAIM, newAddress);
                        messages.add("CLAIM送信をリスタートしました。(送信アドレス=" + newAddress + ")");
                    }
                    
                    // SendMML
                    oldRunning = saveEnv.getProperty(GUIConst.KEY_SEND_MML).equals(GUIConst.SERVICE_RUNNING) ? true : false;
                    newRun = Project.getSendMML();
                    start = ( (!oldRunning) && newRun ) ? true : false;
                    stop = ( (oldRunning) && (!newRun) ) ? true : false;
                    
                    restart = false;
                    oldAddress = saveEnv.getProperty(GUIConst.CSGW_PATH);
                    newAddress = Project.getCSGWPath();
                    if (oldAddress != null && newAddress != null && (!oldAddress.equals(newAddress)) && newRun) {
                        restart = true;
                    }
                    
                    if (start) {
                        createPlugin(GUIConst.JNDI_SEND_MML);
                        saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_RUNNING);
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信を開始しました。(送信アドレス=" + newAddress + ")");
                        
                    } else if (stop) {
                        SendMmlPlugin sendMml = (SendMmlPlugin) getPlugin(GUIConst.JNDI_SEND_MML);
                        sendMml.stop();
                        saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_NOT_RUNNING);
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信を停止しました。");
                        
                    } else if (restart) {
                        SendMmlPlugin sendMml = (SendMmlPlugin) getPlugin(GUIConst.JNDI_SEND_MML);
                        sendMml.stop();
                        createPlugin(GUIConst.JNDI_SEND_MML);
                        saveEnv.put(GUIConst.KEY_SEND_MML, GUIConst.SERVICE_RUNNING);
                        saveEnv.put(GUIConst.CSGW_PATH, newAddress);
                        messages.add("MML送信をリスタートしました。(送信アドレス=" + newAddress + ")");
                    }
                    
                    if (messages.size() > 0) {
                        String[] msgArray = messages.toArray(new String[messages.size()]);
                        Object msg = msgArray;
                        Component cmp = null;
                        String title = ClientContext.getString("settingDialog.title");
                        
                        JOptionPane.showMessageDialog(
                                cmp,
                                msg,
                                ClientContext.getFrameTitle(title),
                                JOptionPane.INFORMATION_MESSAGE
                                );
                    }
                }
            }
        }
    }
    
//    /**
//     * 終了処理を行う。
//     */
//    public void exit() {
//        
//        // 未保存のカルテがある場合は警告しリターンする
//        // カルテを保存または破棄してから再度実行する
//        boolean dirty = false;
//        
//        // Chart を調べる
//        ArrayList<ChartPlugin> allChart = ChartPlugin.getAllChart();
//        if (allChart != null && allChart.size() > 0) {
//            for (ChartPlugin chart : allChart) {
//                if (chart.isDirty()) {
//                    dirty = true;
//                    break;
//                }
//            }
//        }
//        
//        // 保存してないものがあればリターンする
//        if (dirty) {
//            alertDirty();
//            return;
//        }
//        
//        // EditorFrameのチェックを行う
//        java.util.List<IChart> allEditorFrames = EditorFrame.getAllEditorFrames();
//        if (allEditorFrames != null && allEditorFrames.size() > 0) {
//            for(IChart chart : allEditorFrames) {
//                if (chart.isDirty()) {
//                    dirty = true;
//                    break;
//                }
//            }
//        }
//        
//        if (dirty) {
//            alertDirty();
//            return;
//        }
//        
//        
//        //
//        // StoppingTask を集める
//        //
//        Vector<ILongTask> stoppingTasks = new Vector<ILongTask>();
//        ILongTask task = null;
//        
//        try {
//            Hashtable cloneMap = null;
//            synchronized (activeChildren) {
//                cloneMap = (Hashtable) activeChildren.clone();
//            }
//            Iterator iter = cloneMap.values().iterator();
//            while (iter != null && iter.hasNext()) {
//                IMainWindowPlugin pl = (IMainWindowPlugin) iter.next();
//                task = pl.getStoppingTask();
//                if (task != null) {
//                    stoppingTasks.add(task);
//                }
//            }
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            bootLogger.warn(ex.toString());
//        }
//        
//        // StoppingTask を一つのタイマ及び Progress Monitor で実行する
//        // 全てのタスクが終了したらアプリケーションの終了処理に移る
//        int cnt = stoppingTasks.size();
//        if (cnt == 0) {
//            doExit();
//            return; // Never come back
//        } else {
//            bootLogger.info(cnt + " 個の StoppingTask があります");
//        }
//        
//        // 一括して実行するためのTaskManagerを生成する
//        ILongTask[] longs = new AbstractLongTask[cnt];
//        for (int i = 0; i < cnt; i++) {
//            longs[i] = stoppingTasks.get(i);
//        }
//        final TaskManager taskMgr = new TaskManager(longs);
//        
//        // Progress Monitor を生成する
//        String exittingNote = ClientContext.getString("mainWindow.progressNote.exitting");
//        final ProgressMonitor monitor = new ProgressMonitor(null, null, exittingNote, 0, taskMgr.getLength());
//        
//        // 実行 Timer を生成する
//        taskTimer = new javax.swing.Timer(taskMgr.getDelay(),
//                new ActionListener() {
//            
//            public void actionPerformed(ActionEvent e) {
//                
//                if (taskMgr.isDone()) {
//                    
//                    // 終了処理を行う
//                    taskTimer.stop();
//                    monitor.close();
//                    
//                    // 実行結果を得る
//                    if (!taskMgr.getResult()) {
//                        
//                        bootLogger.warn("StoppingTask にエラーがあります");
//                        
//                        // エラーがある場合はダイアログを表示し、オプションを選択させる
//                        int option = doStoppingAlert(taskMgr.getCurTask());
//                        bootLogger.info("選択されたオプション = " + option);
//                        if (option == 1) {
//                            // 終了するを選んだ場合
//                            bootLogger.info("終了オプションが選ばれました");
//                            doExit();
//                        } else {
//                            bootLogger.info("キャンセルオプションが選ばれました");
//                        }
//                        
//                    } else {
//                        // エラーなし
//                        bootLogger.info("StoppingTask が終了しました");
//                        doExit();
//                    }
//                    
//                } else {
//                    // 現在値を更新する
//                    monitor.setProgress(taskMgr.getCurrent());
//                }
//            }
//        });
//        taskMgr.start();
//        taskTimer.start();
//    }
//    
//    /**
//     * 未保存のドキュメントがある場合の警告を表示する。
//     */
//    private void alertDirty() {
//        String msg0 = "未保存のドキュメントがあります。";
//        String msg1 = "保存または破棄した後に再度実行してください。";
//        String taskTitle = ClientContext.getString("mainWindow.exit.taskTitle");
//        JOptionPane.showMessageDialog(
//                        (Component) null,
//                        new Object[]{msg0, msg1},
//                        ClientContext.getFrameTitle(taskTitle),
//                        JOptionPane.INFORMATION_MESSAGE
//                        );
//    }
    
    
    public void exit() {
        
        final AppEnvSaver saver = new AppEnvSaver();
        
        saver.addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent e) {
                
                int state = ((Integer) e.getNewValue()).intValue();
                
                switch (state) {
                    case AppEnvSaver.NO_SAVE_CONDITION:
                        break;
                       
                    case AppEnvSaver.SAVE_ERROR:
                        int option = doStoppingAlert(saver.getErrorTask());
                        if (option == 1) {
                            // 終了するを選んだ場合
                            bootLogger.info("終了オプションが選ばれました");
                            doExit();
                        } else {
                            bootLogger.info("キャンセルオプションが選ばれました");
                        }
                        break;
                        
                    case AppEnvSaver.SAVE_DONE:
                        doExit();
                        break;
                }
            }
        });
        
        saver.save(activeChildren);
    }
    
    /**
     * 終了処理中にエラーが生じた場合の警告をダイアログを表示する。
     * @param errorTask エラーが生じたタスク
     * @return ユーザの選択値
     */
    private int doStoppingAlert(ILongTask errorTask) {
        
        // アプリケーションの環境保存中にエラーが生じました。
        String msg1 = ClientContext.getString("mainWindow.exit.errorMsg1");
        // このまま終了しますか?
        String msg2 = ClientContext.getString("mainWindow.exit.errorMsg2");
        // 終了する
        String exitOption = ClientContext.getString("mainWindow.exit.exitOption");
        // キャンセルする
        String cancelOption = ClientContext.getString("mainWindow.exit.cancelOption");
        // 環境保存
        String taskTitle = ClientContext.getString("mainWindow.exit.taskTitle");
        
        StringBuilder buf = new StringBuilder();
        buf.append(msg1);
        buf.append("\n");
        buf.append(errorTask.getMessage());
        buf.append("\n");
        buf.append(msg2);
        String msg = buf.toString();
        
        String title = ClientContext.getFrameTitle(taskTitle);
        
        String[] options = new String[] {cancelOption, exitOption};
        
        int option = JOptionPane.showOptionDialog(
                null, msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        return option;
    }
    
    /**
     * 最終的に終了する。
     */
    private void doExit() {
        
        try {
            Hashtable cloneMap = null;
            synchronized (activeChildren) {
                cloneMap = (Hashtable) activeChildren.clone();
            }
            Iterator iter = cloneMap.values().iterator();
            while (iter != null && iter.hasNext()) {
                IMainWindowPlugin pl = (IMainWindowPlugin) iter.next();
                pl.stop();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            bootLogger.warn(e.toString());
        }
        JFrame myFrame = windowSupport.getFrame();
        myFrame.setVisible(false);
        myFrame.dispose();
        bootLogger.info("アプリケーションを終了します");
        System.exit(1);
    }
    
    /**
     * ユーザのパスワードを変更する。
     *
     */
    public void changePassword() {
        IMainWindowPlugin plugin = (IMainWindowPlugin) getPlugin(GUIConst.JNDI_CHANGE_PASSWORD);
        if (plugin == null) {
            createPlugin(GUIConst.JNDI_CHANGE_PASSWORD);
        } else {
            plugin.toFront();
        }
    }
    
    /**
     * ユーザ登録を行う。管理者メニュー。
     *
     */
    public void addUser() {
        IMainWindowPlugin plugin = (IMainWindowPlugin) getPlugin(GUIConst.JNDI_ADD_USER);
        if (plugin == null) {
            createPlugin(GUIConst.JNDI_ADD_USER);
        } else {
            plugin.toFront();
        }
    }
    
    /**
     * OpenDolphin Clinet を更新する。
     */
    public void update1() {
        
        Logger logger = ClientContext.getLogger("boot");
        logger.info("ソフトウェア更新が選択されました");
        
        final DolphinUpdater updater = new DolphinUpdater();
        
        // Proxy を設定する
        String proxyHost = Project.getProxyHost();
        String proxyPort = String.valueOf(Project.getProxyPort());
        if ((proxyHost != null) && (proxyPort != null)) {
            updater.setProxyHost(proxyHost);
            updater.setProxyPort(proxyPort);
        }
        
        // Remote URLの最終更新日をチェックする
        ArrayList<String> urls = new ArrayList<String>();
        String tmp = ClientContext.getUpdateURL();
        logger.info("Remote resource = " + tmp);
        urls.add(tmp);
        
        ArrayList<String> localLastModified = new ArrayList<String>();
        tmp = String.valueOf(Project.getLastModify());
        localLastModified.add(tmp);
        
        ArrayList<String> remoteLastModified = updater.getLastModified(urls);
        
        // 結果を表示する
        String msg = null;
        String title = ClientContext.getString("updater.dialog.title");
        
        // 更新情報を取得できなっかた場合
        if (!updater.getResult()) {
            logger.info(msg);
            msg = ClientContext.getString("updater.msg.noConnection");
            JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 更新情報を取得できた場合
        final ArrayList<String> updateUrl = new ArrayList<String>();
        final ArrayList<String> updateLastModied = new ArrayList<String>();
        String remote = null;
        String local = null;
        for (int i = 0; i < localLastModified.size(); i++) {
            
            remote = remoteLastModified.get(i);
            local = localLastModified.get(i);
            logger.info((String) urls.get(i) + ": cachedLM=" + local
                    + " remoteLM=" + remote);
            
            // remote = 0L Jar file なし = 更新なし
            if ((!remote.equals("0")) && (!remote.equals(local))) {
                updateUrl.add(urls.get(i));
                updateLastModied.add(remote);
            }
        }
        
        // 更新可能なものがない場合
        if (updateUrl == null || updateUrl.size() == 0) {
            msg = ClientContext.getString("updater.msg.noUpdate");
            logger.info(msg);
            JOptionPane.showMessageDialog(null, msg, title,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        //
        // 更新可能なものがある場合
        //
        // mnimonic を表示させない
        Object[] cstOptions = new Object[]{"はい", "いいえ"};
        
        msg = ClientContext.getString("updater.msg.available");
        logger.info(msg);
        int select = JOptionPane.showOptionDialog(
                null,
                msg,
                ClientContext.getFrameTitle(title),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ClientContext.getImageIcon("favs_32.gif"),
                cstOptions,
                "はい");
        
        // YES を選択した場合
        if (select == 0) {
            
            //
            // ダウンロードする前に環境の保存を行う
            //
            final AppEnvSaver saver = new AppEnvSaver();
        
            saver.addPropertyChangeListener(new PropertyChangeListener() {
            
                public void propertyChange(PropertyChangeEvent e) {

                    int state = ((Integer) e.getNewValue()).intValue();

                    switch (state) {
                        case AppEnvSaver.NO_SAVE_CONDITION:
                            break;

                        case AppEnvSaver.SAVE_ERROR:
                            int option = doStoppingAlert(saver.getErrorTask());
                            if (option == 1) {
                                // 終了するを選んだ場合
                                bootLogger.info("終了オプションが選ばれました");
                                update3(updater, updateUrl, updateLastModied);
                            } else {
                                bootLogger.info("キャンセルオプションが選ばれました");
                            }
                            break;

                        case AppEnvSaver.SAVE_DONE:
                            update3(updater, updateUrl, updateLastModied);
                            break;
                    }
                }
            });
        
            saver.save(activeChildren);
        }
    }
    
    /**
     * 更新処理を行う。
     * @param updater DolphinUpdater
     * @param updateUrl
     * @param updateLastModied
     */
    private void update3(final DolphinUpdater updater, final ArrayList<String> updateUrl, final ArrayList<String> updateLastModied) {
        
        // 更新ファイルのバイト数を得る
        final Logger logger = ClientContext.getLogger("boot");
        updater.getContentLength(updateUrl);
        final int totalLength = updater.getTotalLength();
        logger.info("ダウンロードの合計バイト数 = " + totalLength);
        
        // モニタ及びタイマーの設定定数を得る
        int delay = ClientContext.getInt("task.default.delay");
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String updateMsg = ClientContext.getString("updater.msg.downloading");
        
        final ProgressMonitor monitor = new ProgressMonitor(null, null, updateMsg, 0, totalLength);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                if (monitor.isCanceled()) {
                    updater.cancel();
                    taskTimer.stop();
                    monitor.close();
                    logger.info("ダウンロードがキャンセルされました");
                    return;
                }
                
                if (!updater.done()) {
                    int current = updater.getCurrent();
                    String msg = updater.getMessage();
                    int done = (int) (((float) current / (float) totalLength) * 100);
                    StringBuilder buf = new StringBuilder();
                    buf.append(msg);
                    buf.append(" [");
                    buf.append(totalLength);
                    buf.append("バイト中 ");
                    buf.append(done);
                    buf.append("% 完了]");
                    monitor.setNote(buf.toString());
                    monitor.setProgress(updater.getCurrent());
                    return;
                }
                
                taskTimer.stop();
                monitor.close();
                
                if (updater.getResult()) {
                    logger.info("ダウンロードに成功しました");
                    update4(updateUrl, updater.getReadBytes(), updateLastModied);
                } else {
                    // warning
                    logger.warn("ダウンロードに失敗しました");
                }
            }
        });
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        updater.downLoad(updateUrl);
        taskTimer.start();
    }
    
    /**
     * 更新する。
     * @param url
     * @param readBytes
     * @param lastModified
     */
    private void update4(ArrayList<String> url, ArrayList<byte[]> readBytes, ArrayList<String> lastModified) {
        
        final Logger logger = ClientContext.getLogger("boot");
        int cnt = url.size();
        String resource = null;
        String urlString = null;
        boolean result = false;
        
        try {
            for (int i = 0; i < cnt; i++) {
                urlString = url.get(i);
                int index = urlString.lastIndexOf("/");
                resource = urlString.substring(index + 1);
                // resource = ClientContext.getLocation("lib") + File.separator + resource;
                // Version 1.2 の実装では OpenDolphin-1.2.jar のみを更新する
                resource = ClientContext.getLocation("dolphin.jar") + File.separator + resource;
                logger.info(resource + " を更新します");
                File dest = new File(resource);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
                out.write(readBytes.get(i));
                out.flush();
                out.close();
                
                Project.setLastModify(Long.parseLong(lastModified.get(i)));
            }
            result = true;
            
        } catch (Exception e) {
            // Show error message
            String errorMsg = ClientContext.getString("updater.msg.updateError");
            String title = ClientContext.getString("updater.task.titl");
            JOptionPane.showMessageDialog(
                    null,
                    errorMsg,
                    ClientContext.getFrameTitle(title),
                    JOptionPane.ERROR_MESSAGE);
        }
        
        if (result) {
            
            //ClientContextStub stub = (ClientContextStub) ClientContext.getClientContextStub();
            
            String title = ClientContext.getString("updater.task.title");
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getString("updater.msg.updateSuccess1"));
            sb.append("\n");
            sb.append(ClientContext.getString("updater.msg.updateSuccess2"));
            String msg = sb.toString();
            
            // Show succeeded message
            JOptionPane.showMessageDialog(
                    null,
                    msg,
                    ClientContext.getFrameTitle(title),
                    JOptionPane.INFORMATION_MESSAGE);
            
            System.exit(1);
        }
    }
    
    /**
     * ドルフィンサポートをオープンする。
     */
    public void browseDolphinSupport() {
        browseURL(ClientContext.getString("mainWindow.menu.dolphinSupportUrl"));
    }
    
    /**
     * ドルフィンプロジェクトをオープンする。
     */
    public void browseDolphinProject() {
        browseURL(ClientContext.getString("mainWindow.menu.dolphinUrl"));
    }
    
    /**
     * MedXMLをオープンする。
     */
    public void browseMedXml() {
        browseURL(ClientContext.getString("mainWindow.menu.medXmlUrl"));
    }
    
    /**
     * SGをオープンする。
     */
    public void browseSeaGaia() {
        browseURL(ClientContext.getString("mainWindow.menu.seaGaiaUrl"));
    }
    
    /**
     * URLをオープンする。
     * @param url URL
     */
    private void browseURL(String url) {
        
        try {
            if (ClientContext.isMac()) {
                ProcessBuilder builder = new ProcessBuilder("open", url);
                builder.start();
                
            } else if (ClientContext.isWin()) {
                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", url);
                builder.start();
                
            } else {
                // Unsupported OS
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * About を表示する。
     */
    public void showAbout() {
        AbstractProjectFactory f = Project.getProjectFactory();
        f.createAboutDialog();
    }
    
    /**
     * シェーマボックスを表示する。
     */
    public void showSchemaBox() {
        IMainWindowPlugin plugin = (IMainWindowPlugin) getPlugin(GUIConst.JNDI_SCHEMA_BOX);
        if (plugin == null) {
            createPlugin(GUIConst.JNDI_SCHEMA_BOX);
        } else {
            plugin.toFront();
        }
    }
    
    /**
     * スタンプボックスを表示する。
     */
    public void showStampBox() {
        IMainWindowPlugin plugin = (IMainWindowPlugin) getPlugin(GUIConst.JNDI_STAMP_BOX);
        if (plugin == null) {
            createPlugin(GUIConst.JNDI_STAMP_BOX);
        } else {
            plugin.toFront();
        }
    }
    
    // //////////////////////////////////////////////////////////////
    
    /**
     * Mediator
     */
    protected final class Mediator extends MenuSupport {
        
        public Mediator(Object owner) {
            super(owner);
        }
        
        // global property の制御
        public void menuSelected(MenuEvent e) {
        }
        
        public void registerActions(Hashtable<String, Action> actions) {
            super.registerActions(actions);
            // メインウインドウなので閉じるだけは無効にする
            getAction(GUIConst.ACTION_WINDOW_CLOSING).setEnabled(false);
        }
    }
    
    //////////////////// StateMgr ///////////////////////////////
    
    /**
     * MainWindowState
     */
    abstract class MainWindowState {
        
        public MainWindowState() {
            super();
        }
        
        public abstract void enter();
        
        public abstract boolean isLogin();
    }
    
    /**
     * LoginState
     */
    class LoginState extends MainWindowState {
        
        public LoginState() {
        }
        
        public boolean isLogin() {
            return true;
        }
        
        public void enter() {
            // Menuを制御する
            Action addUserAction = mediator.getAction(GUIConst.ADD_USER);
            boolean admin = false;
            Collection<RoleModel> roles = Project.getUserModel().getRoles();
            for (RoleModel model : roles) {
                if (model.getRole().equals(GUIConst.ROLE_ADMIN)) {
                    admin = true;
                    break;
                }
            }
            addUserAction.setEnabled(admin);
            
            mediator.getAction(GUIConst.CHANGE_PASSWORD).setEnabled(true);
            mediator.getAction(GUIConst.UPDATE_SOFTWARE).setEnabled(true);
        }
    }
    
    /**
     * LogoffState
     */
    class LogoffState extends MainWindowState {
        
        public LogoffState() {
        }
        
        public boolean isLogin() {
            return false;
        }
        
        public void enter() {
            mediator.getAction(GUIConst.CHANGE_PASSWORD).setEnabled(false);
            mediator.getAction(GUIConst.ADD_USER).setEnabled(false);
            mediator.getAction(GUIConst.UPDATE_SOFTWARE).setEnabled(false);
        }
    }
    
    /**
     * StateManager
     */
    class StateManager {
        
        private MainWindowState loginState = new LoginState();
        
        private MainWindowState logoffState = new LogoffState();
        
        private MainWindowState currentState = logoffState;
        
        public StateManager() {
        }
        
        public boolean isLogin() {
            return currentState.isLogin();
        }
        
        public void processLogin(boolean b) {
            currentState = b ? loginState : logoffState;
            currentState.enter();
        }
    }
}