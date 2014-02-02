package open.dolphin.client;

import open.dolphin.order.StampEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledEditorKit;

import open.dolphin.dao.SqlOrcaSetDao;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.util.BeanUtils;

import open.dolphin.client.ChartMediator.CompState;
import open.dolphin.helper.DBTask;
import open.dolphin.helper.ImageHelper;
import open.dolphin.order.AbstractStampEditor;
import open.dolphin.plugin.PluginLoader;
import org.apache.log4j.Logger;

/**
 * Karte Pane
 *
 * @author Kazushi Minagawa, Digital Globe, inc.
 */
public class KartePane implements DocumentListener, MouseListener,
        CaretListener, PropertyChangeListener, KarteComposite {

    // 文書に付けるタイトルを自動で取得する時の長さ

    private static final int TITLE_LENGTH = 15;
    // 編集不可時の背景色

    private static final Color UNEDITABLE_COLOR = new Color(227, 250, 207);
    // JTextPane

    private JTextPane textPane;
    // SOA または P のロール

    private String myRole;
    // この KartePaneのオーナ

    private ChartDocument parent;
    // StampHolderのTransferHandler

    private StampHolderTransferHandler stampHolderTransferHandler;
    // SchemaHolderのTransferHandler

    private SchemaHolderTransferHandler schemaHolderTransferHandler;
    private int stampId;
    // Dirty Flag

    private boolean dirty;
    // Selection Flag

    private boolean hasSelection;
    private CompState curState;
    // 初期化された時のDocumentの長さ

    private int initialLength;
    // ChartMediator(MenuSupport)

    private ChartMediator mediator;
    // このオブジェクトで生成する文書DocumentModelの文書ID

    private String docId;
    // 保存後及びブラウズ時の編集不可を表すカラー

    private Color uneditableColor = UNEDITABLE_COLOR;
    // このペインからDragg及びDroppされたスタンプの情報

    private ComponentHolder[] drragedStamp;
    private int draggedCount;
    private int droppedCount;
    
    Logger logger;

    /** 
     * Creates new KartePane2 
     */
    public KartePane() {
        logger = ClientContext.getBootLogger();
    }

    public void setMargin(Insets margin) {
        textPane.setMargin(margin);
    }

    public void setPreferredSize(Dimension size) {
        textPane.setPreferredSize(size);
    }

    public void setSize(Dimension size) {
        textPane.setMinimumSize(size);
        textPane.setMaximumSize(size);
    }

    /**
     * このPaneのオーナを設定する。
     * @param parent KarteEditorオーナ
     */
    public void setParent(ChartDocument parent) {
        this.parent = parent;
    }

    /**
     * このPaneのオーナを返す。
     * @return KarteEditorオーナ
     */
    public ChartDocument getParent() {
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
    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
        if (this.textPane != null) {
            KarteStyledDocument doc = new KarteStyledDocument();
            this.textPane.setDocument(doc);
            this.textPane.putClientProperty("kartePane", this);

            doc.setParent(this);
            stampHolderTransferHandler = new StampHolderTransferHandler();
            schemaHolderTransferHandler = new SchemaHolderTransferHandler();
        }
    }

    /**
     * JTextPaneを返す。
     * @return JTextPane
     */
    public JTextPane getTextPane() {
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
    public void setInitialLength(int initialLength) {
        this.initialLength = initialLength;
    }

    /**
     * 初期長を返す。
     * @return Documentの初期長
     */
    public int getInitialLength() {
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
    protected ComponentHolder[] getDrragedStamp() {
        return drragedStamp;
    }

    /**
     * このPaneからDragされたスタンプを設定（記録）する。
     * @param drragedStamp このPaneからDragされたスタンプ配列
     */
    protected void setDrragedStamp(ComponentHolder[] drragedStamp) {
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
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        map.put(keystroke, mediator.getAction(GUIConst.ACTION_CUT));
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        map.put(keystroke, mediator.getAction(GUIConst.ACTION_COPY));
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        map.put(keystroke, mediator.getAction(GUIConst.ACTION_PASTE));

        // Drag は editable に関係なく可能
        getTextPane().setDragEnabled(true);

        // リスナを登録する
        getTextPane().addMouseListener(this);
        getTextPane().addCaretListener(this);

        // Editable Property を設定する
        setEditableProp(editable);
    }

    public void setEditableProp(boolean editable) {
        getTextPane().setEditable(editable);
        if (editable) {
            getTextPane().getDocument().addDocumentListener(this);
            getTextPane().addFocusListener(AutoKanjiListener.getInstance());
            getTextPane().getDocument().addUndoableEditListener(mediator);
            if (myRole.equals(IInfoModel.ROLE_SOA)) {
                SOACodeHelper helper = new SOACodeHelper(this, getMediator());
            } else {
                PCodeHelper helper = new PCodeHelper(this, getMediator());
            }
            getTextPane().setBackground(Color.WHITE);
            getTextPane().setOpaque(true);
        } else {
            getTextPane().getDocument().removeDocumentListener(this);
            getTextPane().removeFocusListener(AutoKanjiListener.getInstance());
            getTextPane().getDocument().removeUndoableEditListener(mediator);
            setBackgroundUneditable();
        }
    }

    // JTextPaneへの挿入でdirtyかどうかを判定する
    @Override
    public void insertUpdate(DocumentEvent e) {
        boolean newDirty = getDocument().getLength() > getInitialLength() ? true : false;
        setDirty(newDirty);
    }

    // 削除が起こった時dirtyかどうかを判定する
    @Override
    public void removeUpdate(DocumentEvent e) {
        boolean newDirty = getDocument().getLength() > getInitialLength() ? true : false;
        setDirty(newDirty);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

//    public void focusGained(FocusEvent e) {
//        getTextPane().getInputContext().setCharacterSubsets(new Character.Subset[]{InputSubset.KANJI});
//    }
//
//    public void focusLost(FocusEvent e) {
//    }

    @Override
    public void caretUpdate(CaretEvent e) {
        boolean newSelection = (e.getDot() != e.getMark()) ? true : false;
        if (newSelection != hasSelection) {
            hasSelection = newSelection;

            // テキスト選択の状態へ遷移する
            if (hasSelection) {
                curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA_TEXT : CompState.P_TEXT;
            } else {
                curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA : CompState.P;
            }
            controlMenus(mediator.getActions());
        }
    }

    /**
     * リソースをclearする。
     */
    public void clear() {

        getTextPane().getDocument().removeDocumentListener(this);
        getTextPane().removeMouseListener(this);
        getTextPane().removeFocusListener(AutoKanjiListener.getInstance());
        getTextPane().removeCaretListener(this);

        try {
            KarteStyledDocument doc = getDocument();
            doc.remove(0, doc.getLength());
            doc = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTextPane(null);
    }

    /**
     * メニューを制御する。
     *
     */
    private void controlMenus(ActionMap map) {

        // 各Stateはenableになる条件だけを管理する
        switch (curState) {

            case NONE:
                break;

            case SOA:
                // SOAPaneにFocusがありテキスト選択がない状態
                if (getTextPane().isEditable()) {
                    map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
                    map.get(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
                    map.get(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(true);
                }
                break;

            case SOA_TEXT:
                // SOAPaneにFocusがありテキスト選択がある状態
                map.get(GUIConst.ACTION_CUT).setEnabled(getTextPane().isEditable());
                map.get(GUIConst.ACTION_COPY).setEnabled(true);
                boolean pasteOk = (getTextPane().isEditable() && canPaste()) ? true : false;
                map.get(GUIConst.ACTION_PASTE).setEnabled(pasteOk);
                break;

            case P:
                // PPaneにFocusがありテキスト選択がない状態
                if (getTextPane().isEditable()) {
                    map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
                    map.get(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
                    map.get(GUIConst.ACTION_INSERT_STAMP).setEnabled(true);
                }
                break;

            case P_TEXT:
                // PPaneにFocusがありテキスト選択がある状態
                map.get(GUIConst.ACTION_CUT).setEnabled(getTextPane().isEditable());
                map.get(GUIConst.ACTION_COPY).setEnabled(true);
                pasteOk = (getTextPane().isEditable() && canPaste()) ? true : false;
                map.get(GUIConst.ACTION_PASTE).setEnabled(pasteOk);
                break;
        }
    }

    @Override
    public void enter(ActionMap map) {
        curState = getMyRole().equals(IInfoModel.ROLE_SOA) ? CompState.SOA : CompState.P;
        controlMenus(map);
    }

    @Override
    public void exit(ActionMap map) {
    }

    @Override
    public Component getComponent() {
        return getTextPane();
    }

    protected JPopupMenu createMenus() {

        final JPopupMenu contextMenu = new JPopupMenu();

        // cut, copy, paste メニューを追加する
        contextMenu.add(mediator.getAction(GUIConst.ACTION_CUT));
        contextMenu.add(mediator.getAction(GUIConst.ACTION_COPY));
        contextMenu.add(mediator.getAction(GUIConst.ACTION_PASTE));

        // テキストカラーメニューを追加する
        if (getTextPane().isEditable()) {
            ColorChooserComp ccl = new ColorChooserComp();
            ccl.addPropertyChangeListener(ColorChooserComp.SELECTED_COLOR, new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    Color selected = (Color) e.getNewValue();
                    Action action = new StyledEditorKit.ForegroundAction("selected", selected);
                    action.actionPerformed(new ActionEvent(getTextPane(), ActionEvent.ACTION_PERFORMED, "foreground"));
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
            mediator.addStampMenu(contextMenu, this);
        } else {
            // TextMenuを追加する
            mediator.addTextMenu(contextMenu);
        }

        return contextMenu;
    }

    private void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu contextMenu = createMenus();
            contextMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mabeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mabeShowPopup(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
    public void setRole(String role) {
        setMyRole(role);
    //setMyPartner(partner);
    }

    /**
     * Dirtyかどうかを返す。
     * @return dirty の時 true
     */
    protected boolean isDirty() {
        return getTextPane().isEditable() ? dirty : false;
    }
    
    protected void setDirty(boolean newDirty) {
        if (newDirty != dirty) {
            dirty = newDirty;
            getParent().setDirty(dirty);
        }
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
        getDocument().insertFreeString(s, a);
    }

    /**
     * このペインに Stamp を挿入する。
     */
    public void stamp(final ModuleModel stamp) {
        if (stamp != null) {
            EventQueue.invokeLater(new Runnable() {

                @Override
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
            EventQueue.invokeLater(new Runnable() {

                @Override
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
        EventQueue.invokeLater(new Runnable() {

            @Override
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
        String role = stampInfo.getStampRole();

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
            applyOrcaSet(stampInfo);
            return;
        }

        //
        // データベースに保存されているスタンプを挿入する
        //
        if (stampInfo.isSerialized()) {
            applySerializedStamp(stampInfo);
            return;
        }

        //
        // Stamp エディタを起動する
        //
        ModuleModel stamp = new ModuleModel();
        stamp.setModuleInfo(stampInfo);
        new StampEditor(stamp, this);
    }

    /**
     * StampInfoがDropされた時、そのデータをペインに挿入する。
     * @param addList スタンプ情報のリスト
     */
    public void stampInfoDropped(final ArrayList<ModuleInfoBean> addList) {

        final StampDelegater sdl = new StampDelegater();
        
        DBTask task = new DBTask<List<StampModel>, Void>(parent.getContext()) {

            @Override
            protected List<StampModel> doInBackground() throws Exception {
                List<StampModel> list = sdl.getStamp(addList);
                return list;
            }
            
            @Override
            public void succeeded(List<StampModel> list) {
                logger.debug("stampInfoDropped succeeded");
                if (list != null) {
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
        
        task.execute();
    }

    /**
     * TextStampInfo が Drop された時の処理を行なう。
     */
    public void textStampInfoDropped(final ArrayList<ModuleInfoBean> addList) {
        
        final StampDelegater sdl = new StampDelegater();
        
        DBTask task = new DBTask<List<StampModel>, Void>(parent.getContext()) {

            @Override
            protected List<StampModel> doInBackground() throws Exception {
                List<StampModel> list = sdl.getStamp(addList);
                return list;
            }
            
            @Override
            public void succeeded(List<StampModel> list) {
                logger.debug("textStampInfoDropped succeeded");
                if (list != null) {
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
        
        task.execute();
    }

    /**
     * TextStamp をこのペインに挿入する。
     */
    private void applyTextStamp(final ModuleInfoBean stampInfo) {
        
        final StampDelegater sdl = new StampDelegater();
        
        DBTask task = new DBTask<StampModel, Void>(parent.getContext()) {

            @Override
            protected StampModel doInBackground() throws Exception {
                StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
                return getStamp;
            }
            
            @Override
            public void succeeded(StampModel result) {
                logger.debug("applyTextStamp succeeded");
                if (result != null) {
                    try {
                        byte[] bytes = result.getStampBytes();
                        XMLDecoder d = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(bytes)));

                        IInfoModel model = (IInfoModel) d.readObject();
                        if (model != null) {
                            insertTextStamp(model.toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        task.execute();
    }

    /**
     * 永続化されているスタンプを取得してこのペインに展開する。
     */
    private void applySerializedStamp(final ModuleInfoBean stampInfo) {
        
        final StampDelegater sdl = new StampDelegater();
        
        DBTask task = new DBTask<StampModel, Void>(parent.getContext()) {

            @Override
            protected StampModel doInBackground() throws Exception {
                StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
                return getStamp;
            }
            
            @Override
            public void succeeded(StampModel result) {
                logger.debug("applySerializedStamp succeeded");
                if (result != null) {
                    IInfoModel model = (IInfoModel) BeanUtils.xmlDecode(result.getStampBytes());
                    ModuleModel stamp = new ModuleModel();
                    stamp.setModel(model);
                    stamp.setModuleInfo(stampInfo);
                    stamp(stamp);
                }
            }
        };

        task.execute();
    }

    /**
     * ORCA の入力セットを取得してこのペインに展開する。
     */
    private void applyOrcaSet(final ModuleInfoBean stampInfo) {
        
        final SqlOrcaSetDao sdl = new SqlOrcaSetDao();
        
        DBTask task = new DBTask<List<ModuleModel>, Void>(parent.getContext()) {

            @Override
            protected List<ModuleModel> doInBackground() throws Exception {
                List<ModuleModel> models = sdl.getStamp(stampInfo);
                return models;
            }
            
            @Override
            public void succeeded(List<ModuleModel> models) {
                logger.debug("applyOrcaSet succeeded");
                if (models != null) {
                    for (ModuleModel stamp : models) {
                        stamp(stamp);
                    }
                }
            }
        };

        task.execute();
    }
    
    private void showMetaDataMessage() {
        
        Window w = SwingUtilities.getWindowAncestor(getTextPane());  
        JOptionPane.showMessageDialog(w,
                                      "画像のメタデータが取得できず、読み込むことができません。",
                                      ClientContext.getFrameTitle("画像インポート"),
                                      JOptionPane.WARNING_MESSAGE);
    }
    
    private boolean showMaxSizeMessage() {
        
        int maxImageWidth = ClientContext.getInt("image.max.width");
        int maxImageHeight = ClientContext.getInt("image.max.height");
        final Preferences pref = Preferences.userNodeForPackage(this.getClass());
        
        String title = ClientContext.getFrameTitle("画像サイズについて");
        JLabel msg1 = new JLabel("カルテに挿入する画像は、最大で " + maxImageWidth + " x " + maxImageHeight + " pixcel に制限しています。");
        JLabel msg2 = new JLabel("画像を縮小しカルテに展開しますか?");
        final JCheckBox cb = new JCheckBox("今後このメッセージを表示しない");
        cb.setFont(new Font("Dialog", Font.PLAIN, 10));
        cb.addActionListener(new ActionListener() {

            @Override
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
        Window w = SwingUtilities.getWindowAncestor(getTextPane());        

        int option = JOptionPane.showOptionDialog(w,
                            new Object[]{box},
                            ClientContext.getFrameTitle(title),
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            ClientContext.getImageIcon("about_32.gif"),
                            new String[]{"縮小する", "取消す"}, "縮小する");
        return option == 0 ? true : false;
    }
    
    private void showNoReaderMessage() {
        Window w = SwingUtilities.getWindowAncestor(getTextPane());  
        JOptionPane.showMessageDialog(w,
                                      "選択した画像を読むことができるリーダが存在しません。",
                                      ClientContext.getFrameTitle("画像インポート"),
                                      JOptionPane.WARNING_MESSAGE);
    }
    
    public void imageEntryDropped(final ImageEntry entry) {
        
        int width = entry.getWidth();
        int height = entry.getHeight();
        int maxImageWidth = ClientContext.getInt("image.max.width");
        int maxImageHeight = ClientContext.getInt("image.max.height");
        Preferences pref = Preferences.userNodeForPackage(this.getClass());
        boolean ok = true;
        
        if (width == 0 || height == 0) {
            Icon icon = entry.getImageIcon();
            width = icon.getIconWidth();
            height = icon.getIconHeight();
            if (width > maxImageWidth || height > maxImageHeight) {
                if (pref.getBoolean("showImageSizeMessage", true)) {
                    ok = showMaxSizeMessage();
                }
                //showMetaDataMessage();
                //ok = false;
            }
        } else if (width > maxImageWidth || height > maxImageHeight) {
            if (pref.getBoolean("showImageSizeMessage", true)) {
                ok = showMaxSizeMessage();
            }
        }

        if (!ok) {
            return;
        }        
        
        DBTask task = new DBTask<ImageIcon, Void>(parent.getContext()) {

            @Override
            protected ImageIcon doInBackground() throws Exception {
                
                URL url = new URL(entry.getUrl());
                BufferedImage importImage = ImageIO.read(url);
                
                int width = importImage.getWidth();
                int height = importImage.getHeight();
                int maxImageWidth = ClientContext.getInt("image.max.width");
                int maxImageHeight = ClientContext.getInt("image.max.height");
                
                if (width > maxImageWidth || height > maxImageHeight) {
                    importImage = ImageHelper.getFirstScaledInstance(importImage, maxImageWidth);
                }
                
                return new ImageIcon(importImage);
            }
            
            @Override
            public void succeeded(ImageIcon icon) {
               
                logger.debug("imageEntryDropped succeeded");
                
                if (icon != null) {
                    
                    SchemaModel schema = new SchemaModel();
                    schema.setIcon(icon);

                    // IInfoModel として ExtRef を保持している
                    ExtRefModel ref = new ExtRefModel();
                    ref.setContentType("image/jpeg");
                    ref.setTitle("Schema Image");
                    schema.setExtRef(ref);

                    stampId++;
                    String fileName = getDocId() + "-" + stampId + ".jpg";
                    schema.setFileName(fileName);
                    ref.setHref(fileName);
                    
                    PluginLoader<SchemaEditor> loader 
                        = PluginLoader.load(SchemaEditor.class, ClientContext.getPluginClassLoader());
                    Iterator<SchemaEditor> iter = loader.iterator();
                    if (iter.hasNext()) {
                        final SchemaEditor editor = iter.next();
                        editor.setSchema(schema);
                        editor.setEditable(true);
                        editor.addPropertyChangeListener(KartePane.this);
                        Runnable awt = new Runnable() {

                            @Override
                            public void run() {
                                editor.start();
                            }
                        };
                        EventQueue.invokeLater(awt);
                    }
                }
            }
        };
        
        task.execute();
    }

    /**
     * Schema が DnD された場合、シェーマエディタを開いて編集する。
     */
    public void insertImage(String path) {
        
        if (path == null) {
            return;
        }
        
        String suffix = path.toLowerCase();
        int index = suffix.lastIndexOf('.');
        if (index == 0) {
            showNoReaderMessage();
            return;
        }
        suffix = suffix.substring(index+1);
            
        Iterator readers = ImageIO.getImageReadersBySuffix(suffix);

        if (!readers.hasNext()) {
            showNoReaderMessage();
            return;
        }

        ImageReader reader = (ImageReader) readers.next();
        logger.debug("reader = " + reader.getClass().getName());
        int width = 0;
        int height = 0;
        String name = null;
        try {
            File file = new File(path);
            name = file.getName();
            reader.setInput(new FileImageInputStream(file), true);
            width = reader.getWidth(0);
            height = reader.getHeight(0);
            
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return;
        }
        reader = null;
        ImageEntry entry = new ImageEntry();
        entry.setPath(path);
        entry.setFileName(name);
        entry.setNumImages(1);
        entry.setWidth(width);
        entry.setHeight(height);
        imageEntryDropped(entry);
    }

    /**
     * StampEditor の編集が終了するとここへ通知される。
     * 通知されたスタンプをペインに挿入する。
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();

        if (prop.equals("imageProp")) {

            SchemaModel schema = (SchemaModel) e.getNewValue();

            if (schema != null) {
                // 編集されたシェーマをこのペインに挿入する
                stampSchema(schema);
            }

        } else if (prop.equals(AbstractStampEditor.VALUE_PROP)) {

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
            if (t.isDataFlavorSupported(StampListTransferable.stampListFlavor) || t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
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