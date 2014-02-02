/*
 * KartePane.java
 * Copyright(C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2006 Digital Globe, Inc. All rights reserved.
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

import java.awt.im.InputSubset;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import open.dolphin.client.ChartMediator.CompState;
import open.dolphin.dao.SqlOrcaSetDao;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.util.BeanUtils;

import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.beans.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.SchemaEditorDialog;

/**
 * Karte Pane
 *
 * @author Kazushi Minagawa, Digital Globe, inc.
 */
public class KartePane implements PropertyChangeListener {
    
    /** SOA 及び P Pane の幅 */
    public static final int PANE_WIDTH = 345;
    
    /** SOA 及び P Pane の最大高さ */
    public static final int PANE_HEIGHT = 3*700;
    
    /** 2号カルテの分離線幅 */
    public static final int PANE_DIVIDER_WIDTH = 2;
    
    private static final int MARGIN_LEFT = 10;
    
    /** Pane の上マージン */
    private static final int MARGIN_TOP = 10;
    
    /** Pane の右マージン */
    private static final int MARGIN_RIGHT = 10;
    
    /** Pane の下マージン */
    private static final int MARGIN_BOTTOM = 10;
    
    
    private static final int TITLE_LENGTH = 15;
    
    private static final Color UNEDITABLE_COLOR = new Color(227, 250, 207);
    
    // JTextPane (このクラスはJTextPaneへのデコレータ的役割を担う)
    private JTextPane textPane;
    
    // SOA / P のロール
    private String myRole;
    
    // 相手のKartePane
    private KartePane myPartner;
    
    // このKartePaneのオーナ
    private KarteEditor parent;
    
    // StampHolderのTransferHandler
    private StampHolderTransferHandler stampHolderTransferHandler;
    
    // SchemaHolderのTransferHandler
    private SchemaHolderTransferHandler schemaHolderTransferHandler;
    
    private int stampId;
    
    // Dirty Flag
    private boolean dirty;
    
    // 初期化された時のDocumentの長さ
    private int initialLength;
    
    // ChartMediator(MenuSupport)
    private ChartMediator mediator;
    
    // このオブジェクトで生成する文書DocumentModelの文書ID
    private String docId;
    
    // 保存後及びブラウズ時の編集不可を表すカラー
    private Color uneditableColor = UNEDITABLE_COLOR;
    
    // このペインからDragg及びDroppされたスタンプの情報
    private IComponentHolder[] drragedStamp;
    private int draggedCount;
    private int droppedCount;
    
    //
    // Listeners
    //
    private ContextListener contextListener;
    private FocusCaretListener focusCaret;
    private DocumentListener dirtyListner;
    
    
    /** 
     * Creates new KartePane2 
     */
    public KartePane() {
        //
        // StyledDocumentを生成しJTextPaneを生成する
        //
        KarteStyledDocument doc = new KarteStyledDocument();
        setTextPane(new JTextPane(doc));
        
        //
        // 基本属性を設定する
        //
        getTextPane().setMinimumSize(new Dimension(PANE_WIDTH, PANE_WIDTH));
        getTextPane().setMaximumSize(new Dimension(PANE_WIDTH, PANE_HEIGHT));
        getTextPane().setMargin(new Insets(MARGIN_TOP, MARGIN_LEFT, MARGIN_BOTTOM, MARGIN_RIGHT));
        getTextPane().setAlignmentY(Component.TOP_ALIGNMENT);
        doc.setParent(this);
        stampHolderTransferHandler = new StampHolderTransferHandler();
        schemaHolderTransferHandler = new SchemaHolderTransferHandler();
    }
    
    /**
     * このPaneのオーナを設定する。
     * @param parent KarteEditorオーナ
     */
    protected void setParent(KarteEditor parent) {
        this.parent = parent;
    }
    
    /**
     * このPaneのオーナを返す。
     * @return KarteEditorオーナ
     */
    protected KarteEditor getParent() {
        return parent;
    }
    
    /**
     * 編集不可を表すカラーを設定する。
     * @param uneditableColor 編集不可を表すカラー
     */
    public void setUneditableColor(Color uneditableColor) {
        this.uneditableColor = uneditableColor;
    }
    
    /**
     * 編集不可を表すカラーを返す。
     * @return 編集不可を表すカラー
     */
    public Color getUneditableColor() {
        return uneditableColor;
    }
    
    /**
     * このPaneで生成するDocumentModelの文書IDを設定する。
     * @param docId 文書ID
     */
    protected void setDocId(String docId) {
        this.docId = docId;
    }
    
    /**
     * このPaneで生成するDocumentModelの文書IDを返す。
     * @return 文書ID
     */
    protected String getDocId() {
        return docId;
    }
    
    /**
     * ChartMediatorを設定する。
     * @param mediator ChartMediator
     */
    protected void setMediator(ChartMediator mediator) {
        this.mediator = mediator;
    }
    
    /**
     * ChartMediatorを返す。
     * @return ChartMediator
     */
    protected ChartMediator getMediator() {
        return mediator;
    }
    
    /**
     * パートナPaneを設定する。
     * @param myPartner パートナPane
     */
    protected void setMyPartner(KartePane myPartner) {
        this.myPartner = myPartner;
    }
    
    /**
     * パートナPaneを返す。
     * @return パートナPane
     */
    protected KartePane getMyPartner() {
        return myPartner;
    }
    
    /**
     * このPaneのロールを設定する。
     * @param myRole SOAまたはPのロール
     */
    public void setMyRole(String myRole) {
        this.myRole = myRole;
    }
    
    /**
     *  このPaneのロールを返す。
     * @return SOAまたはPのロール
     */
    public String getMyRole() {
        return myRole;
    }
    
    /**
     * JTextPaneを設定する。
     * @param textPane JTextPane
     */
    protected void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }
    
    /**
     * JTextPaneを返す。
     * @return JTextPane
     */
    protected JTextPane getTextPane() {
        return textPane;
    }
    
    /**
     * JTextPaneのStyledDocumentを返す。
     * @return JTextPaneのStyledDocument
     */
    protected KarteStyledDocument getDocument() {
        return (KarteStyledDocument) getTextPane().getDocument();
    }
    
    /**
     * 初期長を設定する。
     * @param Documentの初期長
     */
    protected void setInitialLength(int initialLength) {
        this.initialLength = initialLength;
    }
    
    /**
     * 初期長を返す。
     * @return Documentの初期長
     */
    protected int getInitialLength() {
        return initialLength;
    }
    
    /**
     * このPaneからDragされたスタンプ数を返す。
     * @return このPaneからDragされたスタンプ数
     */
    protected int getDraggedCount() {
        return draggedCount;
    }
    
    /**
     * このPaneからDragされたスタンプ数を設定する。
     * @param draggedCount このPaneからDragされたスタンプ数
     */
    protected void setDraggedCount(int draggedCount) {
        this.draggedCount = draggedCount;
    }
    
    /**
     * このPaneにDropされたスタンプ数を返す。
     * @return このPaneにDropされたスタンプ数
     */
    protected int getDroppedCount() {
        return droppedCount;
    }
    
    /**
     * このPaneにDropされたスタンプ数を設定する。
     * @param droppedCount このPaneにDropされたスタンプ数
     */
    protected void setDroppedCount(int droppedCount) {
        this.droppedCount = droppedCount;
    }
    
    /**
     * このPaneからDragされたスタンプを返す。
     * @return このPaneからDragされたスタンプ配列
     */
    protected IComponentHolder[] getDrragedStamp() {
        return drragedStamp;
    }
    
    /**
     * このPaneからDragされたスタンプを設定（記録）する。
     * @param drragedStamp このPaneからDragされたスタンプ配列
     */
    protected void setDrragedStamp(IComponentHolder[] drragedStamp) {
        this.drragedStamp = drragedStamp;
    }
    
    /**
     * 初期化する。
     * @param editable 編集可能かどうかのフラグ
     * @param mediator チャートメディエータ（実際にはメニューサポート）
     */
    public void init(boolean editable, ChartMediator mediator) {
        
        // Mediatorを保存する
        setMediator(mediator);
        
        // JTextPaneへアクションを登録する
        // Undo & Redo
        ActionMap map = getTextPane().getActionMap();
        KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        map.put(keystroke, mediator.getAction(GUIConst.ACTION_UNDO));
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        map.put(keystroke, mediator.getAction(GUIConst.ACTION_REDO));
        
        // Drag は editable に関係なく可能
        getTextPane().setDragEnabled(true);
        
        // Dirty 判定用の DocumentListener を設定する
        final KarteStyledDocument doc = getDocument();
        
        if (getTextPane().isEditable()) {
            
            // スタンプ挿入後が初期長になる
            setInitialLength(0);
            
            dirtyListner = new DocumentListener() {
                
                // JTextPaneへの挿入でdirtyかどうかを判定する
                public void insertUpdate(DocumentEvent e) {
                    boolean newDirty = doc.getLength() > getInitialLength() ? true : false;
                    if (newDirty != dirty) {
                        dirty = newDirty;
                        // KarteEditor へ通知する
                        getParent().setDirty(dirty);
                    }
                }
                
                // 削除が起こった時dirtyかどうかを判定する
                public void removeUpdate(DocumentEvent e) {
                    boolean newDirty = doc.getLength() > getInitialLength() ? true : false;
                    if (newDirty != dirty) {
                        dirty = newDirty;
                        // KarteEditor へ通知
                        getParent().setDirty(dirty);
                    }
                }
                
                public void changedUpdate(DocumentEvent e) {
                }
            };
            
            doc.addDocumentListener(dirtyListner);
        }
        
        // コンテキストメニュー用のリスナ設定する
        contextListener = new ContextListener(this, mediator);
        getTextPane().addMouseListener(contextListener);
        
        // Focusとキャレットのコントローラを生成する
        focusCaret = new FocusCaretListener(getTextPane(), mediator);
        
        // TextPaneにFocusListenerを追加する
        getTextPane().addFocusListener(focusCaret);
        
        // TextPaneにCaretListenerを追加する
        getTextPane().addCaretListener(focusCaret);
        
        // Editable Property を設定する
        setEditableProp(editable);
    }
    
    /**
     * リソースをくりあする。
     */
    public void clear() {
        
        if (dirtyListner != null) {
            getTextPane().getDocument().removeDocumentListener(dirtyListner);
            dirtyListner = null;
        }
        
        if (contextListener != null) {
            getTextPane().removeMouseListener(contextListener);
            contextListener = null;
        }
        
        if (focusCaret != null) {
            getTextPane().removeFocusListener(focusCaret);
            getTextPane().removeCaretListener(focusCaret);
            focusCaret = null;
        }
        
        try {
            KarteStyledDocument doc = getDocument();
            doc.remove(0, doc.getLength());
            doc = null;
        } catch (Exception e) {
            
        }
        
        setTextPane(null);
    }
    
    /**
     * FocusCaretListener
     */
    class FocusCaretListener implements FocusListener, CaretListener {
        
        private JTextPane myPane;
        private ChartMediator myMediator;
        private boolean hasSelection;
        private CompState curState;
        
        public FocusCaretListener(JTextPane myPane, ChartMediator mediator) {
            this.myPane = myPane;
            myMediator = mediator;
        }
        
        /**
         * Focusされた時のメニューを制御する。
         */
        public void focusGained(FocusEvent e) {
            //System.out.println(getMyRole() + " gained");
            //
            // 自動 IME on
            //
            myPane.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            // 
            // Mediatorの curCompo に設定する
            // Mediatorはフォーカスがある方のpaneを保存している
            //
            myMediator.setCurrentComponent(myPane);
            
            // Menuを制御する
            curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA : CompState.P;
            controlMenus();
        }
        
        /**
         * FocusLostの処理を行う。
         */
        public void focusLost(FocusEvent e) {
            //System.out.println(getMyRole() + " lost");
            hasSelection = false;
            //curState = CompState.NONE;
            //controlMenus();
        }
        
        /**
         * Caret更新時の処理を行う。
         */
        public void caretUpdate(CaretEvent e) {
            boolean newSelection =  (e.getDot() != e.getMark()) ? true : false;
            if (newSelection != hasSelection) {
                hasSelection = newSelection;
                // テキスト選択の状態へ遷移する
                if (hasSelection) {
                    curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA_TEXT : CompState.P_TEXT;
                } else {
                    curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA : CompState.P;
                }
                controlMenus();
            }
        }
        
        /**
         * メニューを制御する。
         *
         */
        private void controlMenus() {
            
            // 全メニューをdisableにする
            myMediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
            myMediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
            
            // 各Stateはenableになる条件だけを管理する
            switch (curState) {
                
                case NONE:
                    break;
                    
                case SOA:
                    // SOAPaneにFocusがありテキスト選択がない状態
                    if (myPane.isEditable()) {
                        myMediator.getAction(GUIConst.ACTION_PASTE).setEnabled(canPaste());
                        myMediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
                        myMediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(true);
                    }
                    break;
                    
                case SOA_TEXT:
                    // SOAPaneにFocusがありテキスト選択がある状態
                    myMediator.getAction(GUIConst.ACTION_CUT).setEnabled(myPane.isEditable());
                    myMediator.getAction(GUIConst.ACTION_COPY).setEnabled(true);
                    boolean pasteOk = (myPane.isEditable() && canPaste()) ? true : false;
                    myMediator.getAction(GUIConst.ACTION_PASTE).setEnabled(pasteOk);
                    break;
                    
                case P:
                    // PPaneにFocusがありテキスト選択がない状態
                    if (myPane.isEditable()) {
                        myMediator.getAction(GUIConst.ACTION_PASTE).setEnabled(canPaste());
                        myMediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
                        myMediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(true);
                    }
                    break;
                    
                case P_TEXT:
                    // PPaneにFocusがありテキスト選択がある状態
                    myMediator.getAction(GUIConst.ACTION_CUT).setEnabled(myPane.isEditable());
                    myMediator.getAction(GUIConst.ACTION_COPY).setEnabled(true);
                    pasteOk = (myPane.isEditable() && canPaste()) ? true : false;
                    myMediator.getAction(GUIConst.ACTION_PASTE).setEnabled(pasteOk);
                    break;
            }
        }
    }
    
    /**
     * KartePaneのコンテキストメニュークラス。
     */
    class ContextListener extends MouseAdapter {
        
        private KartePane context;
        private ChartMediator myMediator;
        
        public ContextListener(KartePane kartePane, ChartMediator mediator) {
            context = kartePane;
            myMediator = mediator;
        }
        
        private JPopupMenu createMenus() {
            final JPopupMenu contextMenu = new JPopupMenu();
            // cut, copy, paste メニューを追加する
            contextMenu.add(myMediator.getAction(GUIConst.ACTION_CUT));
            contextMenu.add(myMediator.getAction(GUIConst.ACTION_COPY));
            contextMenu.add(myMediator.getAction(GUIConst.ACTION_PASTE));
            // テキストカラーメニューを追加する
            if (context.getTextPane().isEditable()) {
                ColorChooserComp ccl = new ColorChooserComp();
                ccl.addPropertyChangeListener(ColorChooserComp.SELECTED_COLOR, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        Color selected = (Color) e.getNewValue();
                        Action action = new StyledEditorKit.ForegroundAction("selected", selected);
                        action.actionPerformed(new ActionEvent(context.getTextPane(), ActionEvent.ACTION_PERFORMED, "foreground"));
                        contextMenu.setVisible(false);
                    }
                });
                JLabel l = new JLabel("  カラー:");
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                p.add(l);
                p.add(ccl);
                contextMenu.add(p);
            } else {
                contextMenu.addSeparator();
            }
            
            // PPane の場合はStampMenuを追加する
            if (getMyRole().equals(IInfoModel.ROLE_P)) {
                //contextMenu.addSeparator();
                myMediator.addStampMenu(contextMenu, context);
            } else {
                // TextMenuを追加する
                myMediator.addTextMenu(contextMenu);
            }
            
            return contextMenu;
        }
        
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mabeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JPopupMenu contextMenu = createMenus();
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    /**hanagui+
     *
     * 編集可能かどうかの属性をTextPaneに設定する。
     * @param editable 編集可能なPaneの時true
     */
    protected void setEditableProp(boolean editable) {
        if (editable) {
            getDocument().addUndoableEditListener(getMediator());
            if (myRole.equals(IInfoModel.ROLE_SOA)) {
                SOACodeHelper helper = new SOACodeHelper(this, getMediator());
            } else {
                PCodeHelper helper = new PCodeHelper(this, getMediator());
            }
        } else {
            setBackgroundUneditable();
        }
        getTextPane().setEditable(editable);
    }
    
    /**
     * 背景を編集不可カラーに設定する。
     */
    protected void setBackgroundUneditable() {
        getTextPane().setBackground(getUneditableColor());
        getTextPane().setOpaque(true);
    }
    
    /**
     * ロールとパートナを設定する。
     * @param role このペインのロール
     * @param partner パートナ
     */
    protected void setRole(String role, KartePane partner) {
        setMyRole(role);
        setMyPartner(partner);
    }
    
    /**
     * Dirtyかどうかを返す。
     * @return dirty の時 true
     */
    protected boolean isDirty() {
        return getTextPane().isEditable() ? dirty : false;
    }
    
    /**
     * 保存時につけるドキュメントのタイトルをDocument Objectから抽出する。
     * @return 先頭から指定された長さを切り出した文字列
     */
    protected String getTitle() {
        try {
            KarteStyledDocument doc = getDocument();
            int len = doc.getLength();
            int freeTop = 0; // doc.getFreeTop();
            int freeLen = len - freeTop;
            freeLen = freeLen < TITLE_LENGTH ? freeLen : TITLE_LENGTH;
            return getTextPane().getText(freeTop, freeLen).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Documentの段落スタイルを設定する。
     * @param str スタイル
     */
    public void setLogicalStyle(String str) {
        getDocument().setLogicalStyle(str);
    }
    
    /**
     * Documentの段落論理スタイルをクリアする。
     */
    public void clearLogicalStyle() {
        getDocument().clearLogicalStyle();
    }
    
    /**
     * 段落を構成する。
     */
    public void makeParagraph() {
        getDocument().makeParagraph();
    }
    
    /**
     * Documentに文字列を挿入する。
     * @param str 挿入する文字列
     * @param attr 属性
     */
    public void insertFreeString(String s, AttributeSet a) {
        getDocument().insertFreeString(s,a);
    }
    
    /**
     * このペインに Stamp を挿入する。
     */
    public void stamp(final ModuleModel stamp) {
        if (stamp != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StampHolder h = new StampHolder(KartePane.this, stamp);
                    h.setTransferHandler(stampHolderTransferHandler);
                    KarteStyledDocument doc = getDocument();
                    doc.stamp(h);
                }
            });
        }
    }
    
    /**
     * このペインに Stamp を挿入する。
     */
    public void flowStamp(ModuleModel stamp) {
        if (stamp != null) {
            StampHolder h = new StampHolder(this, stamp);
            h.setTransferHandler(stampHolderTransferHandler);
            KarteStyledDocument doc = getDocument();
            doc.flowStamp(h);
        }
    }
    
    /**
     * このペインにシェーマを挿入する。
     * @param schema シェーマ
     */
    public void stampSchema(final SchemaModel schema) {
        if (schema != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    
                    SchemaHolder h = new SchemaHolder(KartePane.this, schema);
                    h.setTransferHandler(schemaHolderTransferHandler);
                    KarteStyledDocument doc = getDocument();
                    doc.stampSchema(h);
                }
            });
        }
    }
    
    /**
     * このペインにシェーマを挿入する。
     * @param schema  シェーマ
     */
    public void flowSchema(SchemaModel schema) {
        if (schema != null) {
            SchemaHolder h = new SchemaHolder(this, schema);
            h.setTransferHandler(schemaHolderTransferHandler);
            KarteStyledDocument doc = (KarteStyledDocument) getTextPane().getDocument();
            doc.flowSchema(h);
        }
    }
    
    /**
     * このペインに TextStamp を挿入する。
     */
    public void insertTextStamp(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                KarteStyledDocument doc = getDocument();
                doc.insertTextStamp(s);
            }
        });
    }
    
    /**
     * StampInfoがDropされた時、そのデータをペインに挿入する。
     * @param stampInfo ドロップされたスタンプ情報
     */
    public void stampInfoDropped(ModuleInfoBean stampInfo) {
        
        //
        // Drop された StampInfo の属性に応じて処理を振分ける
        //
        String entity = stampInfo.getEntity();
        //System.out.println(entity);
        
        String role = stampInfo.getStampRole();
        //System.out.println(role);
        
        
        //
        // 病名の場合は２号カルテペインには展開しない
        //
        if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        //
        // Text スタンプを挿入する
        //
        if (entity.equals(IInfoModel.ENTITY_TEXT)) {
            applyTextStamp(stampInfo);
            return;
        }
        
        //
        // ORCA 入力セットの場合
        //
        if (role.equals(IInfoModel.ROLE_ORCA_SET)) {
            //System.out.println("orca set dropped");
            applyOrcaSet(stampInfo);
            return;
        }
        
        //
        // データベースに保存されているスタンプを挿入する
        //
        if (stampInfo.isSerialized()) {
            //System.out.println("apply serialized stamp");
            applySerializedStamp(stampInfo);
            return;
        }
        
        //
        // Stamp エディタを起動する
        //
        ModuleModel stamp = new ModuleModel();
        stamp.setModuleInfo(stampInfo);
        
        StampEditorDialog stampEditor = new StampEditorDialog(entity, stamp);
        stampEditor.addPropertyChangeListener(StampEditorDialog.VALUE_PROP, this);
        stampEditor.start();
    }
    
    /**
     * StampInfoがDropされた時、そのデータをペインに挿入する。
     * @param addList スタンプ情報のリスト
     */
    public void stampInfoDropped(final ArrayList<ModuleInfoBean> addList) {
        
        
        Runnable serializedRunner = new Runnable() {
            
            public void run() {
                
                startAnimation();
                
                StampDelegater sdl = new StampDelegater();
                final List<StampModel> list = sdl.getStamp(addList);
                
                stopAnimation();
                
                if (list != null) {
                    //for (int i = list.size() -1; i > -1; i--) {
                    for (int i = 0; i < list.size(); i++) {
                        ModuleInfoBean stampInfo = addList.get(i);
                        StampModel theModel = list.get(i);
                        IInfoModel model = (IInfoModel) BeanUtils.xmlDecode(theModel.getStampBytes());
                        if (model != null) {
                            ModuleModel stamp = new ModuleModel();
                            stamp.setModel(model);
                            stamp.setModuleInfo(stampInfo);
                            stamp(stamp);
                        }
                    }
                }
            }
        };
        Thread t = new Thread(serializedRunner);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * TextStampInfo が Drop された時の処理を行なう。
     */
    public void textStampInfoDropped(final ArrayList<ModuleInfoBean> addList) {
        
        Runnable serializedRunner = new Runnable() {
            
            public void run() {
                
                startAnimation();
                
                StampDelegater sdl = new StampDelegater();
                final List<StampModel> list = sdl.getStamp(addList);
                
                stopAnimation();
                
                if (list != null) {
                    //for (int i = list.size() -1; i > -1; i--) {
                    for (int i = 0; i < list.size(); i++) {
                        StampModel theModel = list.get(i);
                        IInfoModel model = (IInfoModel) BeanUtils.xmlDecode(theModel.getStampBytes());
                        if (model != null) {
                            insertTextStamp(model.toString() + "\n");
                        }
                    }
                }
            }
        };
        Thread t = new Thread(serializedRunner);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * TextStamp をこのペインに挿入する。
     */
    private void applyTextStamp(final ModuleInfoBean stampInfo) {
        
        Runnable textRunner = new Runnable() {
            
            public void run() {
                
                startAnimation();
                
                String rdn = stampInfo.getStampId();
                IInfoModel model = null;
                
                StampDelegater sdl = new StampDelegater();
                StampModel getStamp = sdl.getStamp(rdn);
                
                if (getStamp != null) {
                    
                    try {
                        // String beanXml = getStamp.getStampXml();
                        // byte[] bytes = beanXml.getBytes("UTF-8");
                        byte[] bytes = getStamp.getStampBytes();
                        // XMLDecode
                        XMLDecoder d = new XMLDecoder(new BufferedInputStream(
                                new ByteArrayInputStream(bytes)));
                        
                        model = (IInfoModel) d.readObject();
                        
                    } catch (Exception e) {
                    }
                }
                
                stopAnimation();
                
                if (model != null) {
                    insertTextStamp(model.toString());
                }
            }
        };
        Thread t = new Thread(textRunner);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * 永続化されているスタンプを取得してこのペインに展開する。
     */
    private void applySerializedStamp(final ModuleInfoBean stampInfo) {
        
        Runnable serializedRunner = new Runnable() {
            
            public void run() {
                
                startAnimation();
                
                String rdn = stampInfo.getStampId();
                IInfoModel model = null;
                
                StampDelegater sdl = new StampDelegater();
                StampModel getStamp = sdl.getStamp(rdn);
                
                if (getStamp != null) {
                    model = (IInfoModel) BeanUtils.xmlDecode(getStamp.getStampBytes());
                }
                
                stopAnimation();
                
                final ModuleModel stamp = new ModuleModel();
                stamp.setModel(model);
                stamp.setModuleInfo(stampInfo);
                stamp(stamp);
            }
        };
        
        Thread t = new Thread(serializedRunner);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * ORCA の入力セットを取得してこのペインに展開する。
     */
    private void applyOrcaSet(final ModuleInfoBean stampInfo) {
        
        Runnable serializedRunner = new Runnable() {
            
            public void run() {
                
                startAnimation();
                
                String id = stampInfo.getStampId();
                
                SqlOrcaSetDao sdl = new SqlOrcaSetDao();
                List<ModuleModel> models = sdl.getStamp(stampInfo);
                
                stopAnimation();
                
                if (models != null) {
                    for (ModuleModel stamp : models) {
                        stamp(stamp);
                    }
                }
            }
        };
        
        Thread t = new Thread(serializedRunner);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start(); 
    }
    
    
    /**
     * ProgressBarアニメーションを開始する。
     */
    private void startAnimation() {
        Runnable awt = new Runnable() {
            public void run() {
                IChart context = (IChart) getParent().getContext();
                IStatusPanel sp = context.getStatusPanel();
                sp.start("スタンプを取得しています...");
            }
        };
        SwingUtilities.invokeLater(awt);
    }
    
    /**
     * ProgressBarをストップする。
     */
    private void stopAnimation() {
        Runnable awt = new Runnable() {
            public void run() {
                IChart context = (IChart) getParent().getContext();
                IStatusPanel sp = context.getStatusPanel();
                sp.stop("");
            }
        };
        SwingUtilities.invokeLater(awt);
    }
    
    /**
     * Schema が DnD された場合、シェーマエディタを開いて編集する。
     */
    public void myInsertImage(Image trImg) {
        
        try {
            ImageIcon org = new ImageIcon(trImg);
            
            // Size を判定する
            int maxImageWidth = ClientContext.getInt("image.max.width");
            int maxImageHeight = ClientContext.getInt("image.max.height");
            Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
            final Preferences pref = Preferences.userNodeForPackage(this.getClass());
            if (org.getIconWidth() > maxImageWidth || org.getIconHeight() > maxImageHeight) {
                if (pref.getBoolean("showImageSizeMessage", true)) {
                    String title = ClientContext.getFrameTitle("画像サイズについて");
                    JLabel msg1 = new JLabel("カルテに挿入する画像は、最大で " + maxImageWidth + " x " + maxImageHeight + " pixcel に制限しています。");
                    JLabel msg2 = new JLabel("そのため保存時には画像を縮小します。");
                    final JCheckBox cb = new JCheckBox("今後このメッセージを表示しない");
                    cb.setFont(new Font("Dialog", Font.PLAIN, 10));
                    cb.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            pref.putBoolean("showImageSizeMessage", !cb.isSelected());
                        }
                    });
                    JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
                    p1.add(msg1);
                    JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
                    p2.add(msg2);
                    JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
                    p3.add(cb);
                    JPanel box = new JPanel();
                    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
                    box.add(p1);
                    box.add(p2);
                    box.add(p3);
                    box.setBorder(BorderFactory.createEmptyBorder(0, 0, 11, 11));
                    Window parent = SwingUtilities.getWindowAncestor(getTextPane());
                    
                    JOptionPane.showMessageDialog(parent,
                            new Object[]{box},
                            ClientContext.getFrameTitle(getTitle()),
                            JOptionPane.INFORMATION_MESSAGE,
                            ClientContext.getImageIcon("about_32.gif"));
                    
                    
                }
            }
            
            SchemaModel schema = new SchemaModel();
            schema.setIcon(org);
            
            // IInfoModel として ExtRef を保持している
            ExtRefModel ref = new ExtRefModel();
            ref.setContentType("image/jpeg");
            ref.setTitle("Schema Image");
            schema.setExtRef(ref);
            
            stampId++;
            String fileName = getDocId() + "-" + stampId + ".jpg";
            schema.setFileName(fileName);
            ref.setHref(fileName);
            
            final SchemaEditorDialog dlg = new SchemaEditorDialog((Frame) null, true, schema, true);
            dlg.addPropertyChangeListener(KartePane.this);
            Runnable awt = new Runnable() {
                public void run() {
                    dlg.run();
                }
            };
            EventQueue.invokeLater(awt);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * StampEditor の編集が終了するとここへ通知される。
     * 通知されたスタンプをペインに挿入する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals("imageProp")) {
            
            SchemaModel schema = (SchemaModel) e.getNewValue();
            
            if (schema != null) {
                // 編集されたシェーマをこのペインに挿入する
                stampSchema(schema);
            }
            
        } else if (prop.equals(StampEditorDialog.VALUE_PROP)) {
            
            Object o = e.getNewValue();
            
            if (o != null) {
                // 編集された Stamp をこのペインに挿入する
                ModuleModel stamp = (ModuleModel) o;
                stamp(stamp);
            }
        }
    }
    
    /**
     * メニュー制御のため、ペースト可能かどうかを返す。
     * @return ペースト可能な時 true
     */
    protected boolean canPaste() {
        
        boolean ret = false;
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return false;
        }
        
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return true;
        }
        
        if (getMyRole().equals(IInfoModel.ROLE_P)) {
            if (t.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                ret = true;
            }
        } else {
            if (t.isDataFlavorSupported(StampListTransferable.stampListFlavor)
            || t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
                ret = true;
            }
        }
        return ret;
    }
    
    /**
     * このペインからスタンプを削除する。
     * @param sh 削除するスタンプのホルダ
     */
    public void removeStamp(StampHolder sh) {
        getDocument().removeStamp(sh.getStartPos(), 2);
    }
    
    /**
     * このペインからスタンプを削除する。
     * @param sh 削除するスタンプのホルダリスト
     */
    public void removeStamp(StampHolder[] sh) {
        if (sh != null && sh.length > 0) {
            for (int i = 0; i < sh.length; i++) {
                removeStamp(sh[i]);
            }
        }
    }
    
    /**
     * このペインからシェーマを削除する。
     * @param sh 削除するシェーマのホルダ
     */
    public void removeSchema(SchemaHolder sh) {
        getDocument().removeStamp(sh.getStartPos(), 2);
    }
    
    /**
     * このペインからシェーマを削除する。
     * @param sh 削除するシェーマのホルダリスト
     */
    public void removeSchema(SchemaHolder[] sh) {
        if (sh != null && sh.length > 0) {
            for (int i = 0; i < sh.length; i++) {
                removeSchema(sh[i]);
            }
        }
    }
}