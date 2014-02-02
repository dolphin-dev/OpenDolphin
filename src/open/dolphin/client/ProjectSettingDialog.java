/*
 * LoginDialog.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import open.dolphin.plugin.IPluginContext;
import open.dolphin.plugin.PluginReference;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import open.dolphin.project.Project;

/**
 * 環境設定ダイアログ。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ProjectSettingDialog implements PropertyChangeListener  {
    
    // PlugPoint
    private static final String MY_PLUG_POINT = "mainWindow/setting";
    
    //
    // GUI
    //
    private JDialog dialog;
    private JPanel itemPanel;
    private JPanel cardPanel;
    private JPanel cmdPanel;
    private CardLayout cardLayout;
    private JButton okButton;
    private JButton cancelButton;
    
    //
    // 全体のモデル
    //
    private HashMap<String, AbstractSettingPanel> settingMap;
    private ArrayList<PluginReference> allSettings;
    private ArrayList<JToggleButton> allBtns;
    private String startSettingName;
    private boolean loginState;
    
    private PropertyChangeSupport boundSupport;
    private static final String SETTING_PROP = "SETTING_PROP";
    
    private boolean okState;
    
    private final int DEFAULT_WIDTH 	= 490;
    private final int DEFAULT_HEIGHT 	= 590;
    
    
    /**
     * Creates new ProjectSettingDialog
     */
    public ProjectSettingDialog() {
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public boolean getLoginState() {
        return loginState;
    }
    
    public void setLoginState(boolean  b) {
        loginState = b;
    }
    
    public boolean getValue() {
        return Project.getProjectStub().isValid();
    }
    
    public void notifyResult() {
        boolean valid = Project.getProjectStub().isValid() ? true : false;
        boundSupport.firePropertyChange(SETTING_PROP, ! valid, valid);
    }
    
    /**
     * オープン時に表示する設定画面をセットする。
     */
    public void setProject(String startSettingName) {
        this.startSettingName = startSettingName;
    }
    
    /**
     * 設定画面を開始する。
     */
    public void start() {
        
        Runnable r = new Runnable () {
            
            public void run() {
                       
                //
                // モデルを得る
                // 全ての設定プラグイン(Reference)を得、リストに格納する
                //
                try {
                    IPluginContext plCtx = ClientContext.getPluginContext();
                    Collection<PluginReference> c = plCtx.listPluginReferences(MY_PLUG_POINT);
                    allSettings = new ArrayList<PluginReference>(c);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // 設定パネル(AbstractSettingPanel)を格納する Hashtableを生成する
                // key=設定プラグインの名前 value=設定プラグイン
                settingMap = new HashMap<String, AbstractSettingPanel>();

                //
                // GUI を構築しモデルをバインドする
                //
                initComponents();

                //
                // オープン時に表示する設定画面を決定する
                //
                int index = 0;
                
                if (startSettingName != null) {
                    
                    for (PluginReference plRef : allSettings) {
                        if (startSettingName.equals((String) plRef.getAddrContent(PluginReference.PLUGIN_NAME))) {
                            break;
                        }
                        index++;
                    }   
                }
                
                //
                // ボタンを押して表示する
                //
                allBtns.get(index).doClick();
            }
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * GUI を構築する。
     */
    private void initComponents() {
        
        itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //itemPanel = new JPanel();
        //itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        
        //
        // 設定プラグインを起動するためのトグルボタンを生成し
        // パネルへ加える
        //
        allBtns = new ArrayList<JToggleButton>();
        ButtonGroup bg = new ButtonGroup();
        for (PluginReference plRef : allSettings) {
            String text = (String) plRef.getAddrContent(PluginReference.TITLE);
            String iconStr = (String) plRef.getAddrContent(PluginReference.ICON);
            ImageIcon icon = ClientContext.getImageIcon(iconStr);
            JToggleButton tb = new JToggleButton(text, icon);
            if (ClientContext.isWin()) {
                tb.setMargin(new Insets(0,0,0,0));
            }
            tb.setHorizontalTextPosition(SwingConstants.CENTER);
            tb.setVerticalTextPosition(SwingConstants.BOTTOM);
            itemPanel.add(tb);
            bg.add(tb);
            tb.setActionCommand((String) plRef.getAddrContent(PluginReference.PLUGIN_NAME));
            allBtns.add(tb);
        }
        
        //
        // 設定パネルのコンテナとなるカードパネル
        //
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // コマンドボタン
        String text = ClientContext.getString("settingDialog.saveButtonText");
        okButton = GUIFactory.createButton(text, null, null);
        okButton.setEnabled(false);
        
        // Cancel
        text =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = GUIFactory.createButton(text, "C", null);
        
        // 全体ダイアログのコンテントパネル
        JPanel panel = new JPanel(new BorderLayout(11,0));
        panel.add(itemPanel, BorderLayout.NORTH);
        panel.add(cardPanel,BorderLayout.CENTER);
        
        //
        // ダイアログを生成する
        //
        String title = ClientContext.getString("settingDialog.title");
        Object[] options = new Object[]{okButton, cancelButton};
        
        JOptionPane jop = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okButton);
        
        dialog = jop.createDialog((Frame) null, ClientContext.getFrameTitle(title));
        dialog.setResizable(true);
        dialog.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (size.width - DEFAULT_WIDTH) / 2;
        int y = (size.height - DEFAULT_HEIGHT) / 3;
        dialog.setLocation(x, y);
        
        //
        // イベント接続を行う
        //
        connect();
        
    }
    
    /**
     * GUI コンポーネントのイベント接続を行う。
     */
    private void connect() {
        
        //
        // 設定項目ボタンに追加するアクションリスナを生成する
        //
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                PluginReference thePlRef = null;
                String name = event.getActionCommand();
                for (PluginReference plRef : allSettings) {
                    String plName = (String) plRef.getAddrContent(PluginReference.PLUGIN_NAME);
                    if (plName.equals(name)) {
                        thePlRef = plRef;
                        break;
                    }
                }
                if (thePlRef != null) {
                    startSetting(thePlRef);
                }
            }
        };
        
        //
        // 全てのボタンにリスナを追加する
        //
        for (JToggleButton btn : allBtns) {
            btn.addActionListener(al);
        }
        
        // Save
        okButton.addActionListener(ProxyActionListener.create(this, "doOk"));
        okButton.setEnabled(false);
        
        // Cancel
        cancelButton.addActionListener(ProxyActionListener.create(this, "doCancel"));
        
        // Dialog
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        });
    }
    
    /**
     * 選択された項目(SettingPanel)の編集を開始する.
     */
    private void startSetting(final PluginReference plRef) {
        
        //
        // プラグイン名を取得しHashtableを検索する
        //
        final String name = (String) plRef.getAddrContent(PluginReference.PLUGIN_NAME);
        AbstractSettingPanel sp = settingMap.get(name);
        
        //
        // 既に生成されている場合はそれを表示する
        //
        if (sp != null) {
            adjustHeight(sp);
            cardLayout.show(cardPanel, name);
            return;
        }
        
        Runnable r = new Runnable() {
            
            public void run() {
            
                //
                // まだ生成されていない場合は
                // 選択された設定パネルを生成しカードに追加する
                try {
                    String jndiName = plRef.getJndiName();
                    IPluginContext plCtx = ClientContext.getPluginContext();
                    final AbstractSettingPanel settingPanel = (AbstractSettingPanel) plCtx.lookup(jndiName);
                    settingMap.put(name, settingPanel);
                    settingPanel.setContext(ProjectSettingDialog.this);
                    settingPanel.setProjectStub(Project.getProjectStub());
                    settingPanel.start();
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            cardPanel.add(settingPanel.getUI(), name);
                            adjustHeight(settingPanel);
                            cardLayout.show(cardPanel, name);
                            
                            if (!dialog.isVisible()) {
                                dialog.setVisible(true);
                            }
                        }
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private void adjustHeight(AbstractSettingPanel settingPanel) {
        
    }
    
    /**
     * SettingPanel の state が変化した場合に通知を受け、
     * 全てのカードをスキャンして OK ボタンをコントロールする。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        if (!prop.equals(AbstractSettingPanel.STATE_PROP)) {
            return;
        }
        
        //
        // 全てのカードをスキャンして OK ボタンをコントロールする
        //
        boolean newOk = true;
        Iterator<AbstractSettingPanel> iter = settingMap.values().iterator();
        int cnt = 0;
        while (iter.hasNext()) {
            cnt++;
            AbstractSettingPanel p = iter.next();
            if (p.getState().equals(AbstractSettingPanel.State.INVALID_STATE)) {
                newOk = false;
                break;
            }
        }
        
        if (okState != newOk) {
            okState = newOk;
            okButton.setEnabled(okState);
        }
    }
    
    public void doOk() {
        
        Iterator<AbstractSettingPanel> iter = settingMap.values().iterator();
        while (iter.hasNext()) {
            AbstractSettingPanel p = iter.next();
            p.save();
        }
        
        dialog.setVisible(false);
        dialog.dispose();
        notifyResult();
    }
    
    public void doCancel() {
        dialog.setVisible(false);
        dialog.dispose();
        notifyResult();
    }
}