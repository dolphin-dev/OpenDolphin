package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.helper.MenuSupport;
import open.dolphin.project.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.StyledEditorKit;

import open.dolphin.infomodel.PVTHealthInsuranceModel;
import org.apache.log4j.Logger;

/**
 * Mediator class to control Karte Window Menu.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ChartMediator extends MenuSupport implements UndoableEditListener, ActionListener {
    
    protected enum CompState{NONE, SOA, SOA_TEXT, SCHEMA, P, P_TEXT, STAMP};
    
    private static final int[] FONT_SIZE = {10, 12, 14, 16, 18, 24, 36};
    
    private int curSize = 1;
    
    // ChartPlugin
    private Chart chart;
    
    // current KarteComposit
    private KarteComposite curKarteComposit;
    
    // Undo Manager
    private UndoManager undoManager;
    private Action undoAction;
    private Action redoAction;
    private Logger logger;
    private FocusPropertyChangeListener fpcl;
    
    class FocusPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            //logger.debug("focusManager propertyChange :" + prop);permanentFocusOwner,focusOwner
            if ("permanentFocusOwner".equals(prop)) {
                Component comp = (Component) e.getNewValue();
                if (comp instanceof JTextPane) {
                    Object obj = ((JTextPane) comp).getClientProperty("kartePane");
                    if (obj != null && obj instanceof KartePane) {
                        setCurKarteComposit((KarteComposite) obj);
                        logger.debug("focusOwner=kartepane");
                    }
                } else if (comp instanceof KarteComposite) {
                    setCurKarteComposit((KarteComposite) comp);
                    logger.debug("focusOwner=" + comp.getClass().getName());
                }
            }
        }
    }
    
    public ChartMediator(Object owner) {
        
        super(owner);
        logger = ClientContext.getBootLogger();
        chart = (Chart) owner;
        logger.debug("ChartMediator constractor");
        
        fpcl = new FocusPropertyChangeListener();
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(fpcl);
        
        undoManager = new UndoManager();
    }
    
    public void setCurKarteComposit(KarteComposite newComposit) {
        logger.debug("ChartMediator setCurKarteComposit");
        KarteComposite old = this.curKarteComposit;
        this.curKarteComposit = newComposit;
        if (old != curKarteComposit) {
            logger.debug("ChartMediator old != curKarteComposit");
            if (old != null) {
                old.exit(getActions());
            }
            enabledAction(GUIConst.ACTION_CUT, false);
            enabledAction(GUIConst.ACTION_COPY, false);
            enabledAction(GUIConst.ACTION_PASTE, false);
            enabledAction(GUIConst.ACTION_UNDO, false);
            enabledAction(GUIConst.ACTION_REDO, false);
            enabledAction(GUIConst.ACTION_INSERT_TEXT, false);
            enabledAction(GUIConst.ACTION_INSERT_SCHEMA, false);
            enabledAction(GUIConst.ACTION_INSERT_STAMP, false);
            if (curKarteComposit != null) {
                logger.debug("ChartMediator curKarteComposit != null");
                curKarteComposit.enter(getActions());
            }
        }
    }
    
    @Override
    public void registerActions(ActionMap map) {
        
        //printActions(map);
        
        super.registerActions(map);
        
        undoAction = map.get(GUIConst.ACTION_UNDO);
        redoAction = map.get(GUIConst.ACTION_REDO);
        
        // 昇順降順を Preference から取得し設定しておく
        boolean asc = Project.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
        if (asc) {
            Action a = map.get(GUIConst.ACTION_ASCENDING);
            JRadioButtonMenuItem rdi = (JRadioButtonMenuItem) a.getValue("menuItem");
            rdi.setSelected(true);
        } else {
            Action desc = map.get(GUIConst.ACTION_DESCENDING);
            JRadioButtonMenuItem rdi = (JRadioButtonMenuItem) desc.getValue("menuItem");
            rdi.setSelected(true);
        }
    }
    
    public void dispose() {
        logger.debug("ChartMediator dispose");
        KeyboardFocusManager focusManager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.removePropertyChangeListener(fpcl);
//        ActionMap actions = getActions();
//        Object[] keys = actions.keys();
//        for ( Object o : keys) {
//            Action a = actions.get(o);
//            if (a instanceof ReflectAction) {
//                ((ReflectAction)a).setTarget(null);
//            }
//        }
//        actions.clear();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
    public JComponent getCurrentComponent() {
        if (curKarteComposit != null) {
            return (JComponent) curKarteComposit.getComponent();
        }
        return null;
    }
    
    /**
     * メニューリスナの実装。
     * 挿入及びテキストメニューが選択された時の処理を行う。
     */
    @Override
    public void menuSelected(MenuEvent e) {
        
        // 挿入とテキストメニューにリスナが登録されている
        JMenu selectedMenu = (JMenu) e.getSource();
        String cmd = selectedMenu.getActionCommand();
        
        //
        // 挿入メニューの時
        // StampBox のツリーをメニューにする
        //
        if (cmd.equals(GUIConst.MENU_INSERT)) {
            
            selectedMenu.removeAll();
            
            // StampBox の全ツリーを取得する
            List<StampTree> trees = getStampBox().getAllTrees();
            
            // ツリーをイテレートする
            for (StampTree tree : trees) {
                
                // ツリーのエンティティを取得する
                String entity = tree.getEntity();
                
                if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {
                    // 傷病名の時、傷病名メニューを構築し追加する
                    selectedMenu.add(createDiagnosisMenu(tree));
                    selectedMenu.addSeparator();
                    
                } else if (entity.equals(IInfoModel.ENTITY_TEXT)) {
                    // テキストの時、テキストメニューを構築し追加する
                    selectedMenu.add(createTextMenu(tree));
                    selectedMenu.addSeparator();
                    
                } else {
                    // 通常のPオーダの時
                    selectedMenu.add(createStampMenu(tree));
                }
            }
            
            // 
        } 
        
        else if (cmd.equals(GUIConst.MENU_TEXT)) {
            //
            // テキストメニューの場合、スタイルを制御する
            //
            adjustStyleMenu();
        }
    }
    
    @Override
    public void menuDeselected(MenuEvent e) {
    }
    
    @Override
    public void menuCanceled(MenuEvent e) {
    }
    
    /**
     * フォーマット関連メニューを調整する。
     * @param kartePane
     */
    private void adjustStyleMenu() {
        
        boolean enabled = false;
        KartePane kartePane = null;
        if (getChain() instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) getChain();
            kartePane = editor.getSOAPane();
            enabled = (kartePane.getTextPane().isEditable()) ? true : false;
        }
        
        // サブメニューを制御する
        getAction("size").setEnabled(enabled);
        getAction("style").setEnabled(enabled);
        getAction("justify").setEnabled(enabled);
        getAction("color").setEnabled(enabled);
        
        // メニューアイテムを制御する
        //getAction(GUIConst.ACTION_RESET_STYLE).setEnabled(enabled);
        
        getAction("fontRed").setEnabled(enabled);
        getAction("fontOrange").setEnabled(enabled);
        getAction("fontYellow").setEnabled(enabled);
        getAction("fontGreen").setEnabled(enabled);
        getAction("fontBlue").setEnabled(enabled);
        getAction("fontPurple").setEnabled(enabled);
        getAction("fontGray").setEnabled(enabled);
        
        getAction("fontLarger").setEnabled(enabled);
        getAction("fontSmaller").setEnabled(enabled);
        getAction("fontStandard").setEnabled(enabled);
        
        getAction("fontBold").setEnabled(enabled);
        getAction("fontItalic").setEnabled(enabled);
        getAction("fontUnderline").setEnabled(enabled);
        
        getAction("leftJustify").setEnabled(enabled);
        getAction("centerJustify").setEnabled(enabled);
        getAction("rightJustify").setEnabled(enabled);
    }
    
    /**
     * スタンプTreeから傷病名メニューを構築する。
     * @param insertMenu テキストメニュー
     */
    private JMenu createDiagnosisMenu(StampTree stampTree) {
        
        //
        // chainの先頭がDiagnosisDocumentの時のみ使用可能とする
        //
        JMenu myMenu = null;
        DiagnosisDocument diagnosis = null;
        boolean enabled = false;
        Object obj = getChain();
        if (obj instanceof DiagnosisDocument) {
            diagnosis = (DiagnosisDocument) obj;
            enabled = true;
        }
        
        if (!enabled) {
            // cjainの先頭がDiagnosisでない場合はめにゅーをdisableにする
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);
            
        } else {
            // 傷病名Tree、テーブル、ハンドラからメニューを構築する
            JComponent comp = diagnosis.getDiagnosisTable();
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }
        return myMenu;
    }
    
    /**
     * スタンプTreeからテキストメニューを構築する。
     * @param insertMenu テキストメニュー
     */
    private JMenu createTextMenu(StampTree stampTree) {
        
        // chain の先頭が KarteEditor でかつ SOAane が編集可の場合のみメニューが使える
        JMenu myMenu = null;
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getSOAPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable()) ? true : false;
            }
        }
        
        if (!enabled) {
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);
            
        } else {
            //
            // TextTree、JTextPane、handler からメニューを構築する
            // PPane にも落とさなければならない TODO
            //JComponent comp = kartePane.getTextPane();
            // TransferHandler handler = comp.getTransferHandler();
            
            // 2007-03-31
            // 直近でフォーカスを得ているコンポーネント(JTextPan）へ挿入する
            //
            JComponent comp = getCurrentComponent();
            if (comp == null) {
                comp = kartePane.getTextPane();
            }
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }
        
        return myMenu;
    }
    
    /**
     * スタンプメニューを構築する。
     * @param insertMenu スタンプメニュー
     */
    private JMenu createStampMenu(StampTree stampTree) {
        
        // chain の先頭が KarteEditor でかつ Pane が編集可の場合のみメニューが使える
        JMenu myMenu = null;
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getPPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable()) ? true : false;
            }
        }
        
        if (!enabled) {
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);
            
        } else {
            // StampTree、JTextPane、Handler からメニューを構築する
            JComponent comp = kartePane.getTextPane();
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }
        return myMenu;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 引数のポップアップメニューへ傷病名メニューを追加する。
     * @param popup 傷病名メニューを追加するポップアップメニュー
     */
    public void addDiseaseMenu(JPopupMenu popup) {
        
        // Chainの先頭がDiagnosisDocumentの時のみ追加する
        boolean enabled = false;
        DiagnosisDocument diagnosis = null;
        Object obj = getChain();
        if (obj instanceof DiagnosisDocument) {
            diagnosis = (DiagnosisDocument) obj;
            enabled = true;
        }
        
        StampTree stampTree = getStampBox().getStampTree(IInfoModel.ENTITY_DIAGNOSIS);
        
        if (stampTree != null) {
        
            if (!enabled) {
                JMenu myMenu = new JMenu(stampTree.getTreeName());
                myMenu.setEnabled(false);
                popup.add(myMenu);
                return;
            } else {
                JComponent comp = diagnosis.getDiagnosisTable();
                TransferHandler handler = comp.getTransferHandler();
                StmapTreePopupBuilder builder = new StmapTreePopupBuilder();
                builder.build(stampTree, popup, comp, handler);
            }
        }
    }
    
    /**
     * 引数のポップアップメニューへテキストメニューを追加する。
     * @param popup テキストメニューを追加するポップアップメニュー
     */
    public void addTextMenu(JPopupMenu popup) {
        
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getSOAPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable()) ? true : false;
            }
        }
        
        StampTree stampTree = getStampBox().getStampTree(IInfoModel.ENTITY_TEXT);
        
        // ASP スタンプボックスで entity に対応する Tree がない場合がある
        if (stampTree != null) {
        
            if (!enabled) {
                JMenu myMenu = new JMenu(stampTree.getTreeName());
                myMenu.setEnabled(false);
                popup.add(myMenu);
                return;
                
            } else {
                JComponent comp = getCurrentComponent();
                if (comp == null) {
                    comp = kartePane.getTextPane();
                }
                TransferHandler handler = comp.getTransferHandler();
                StmapTreePopupBuilder builder = new StmapTreePopupBuilder();
                builder.build(stampTree, popup, comp, handler);
            }
        }
    }
    
    /**
     * PPane のコンテキストメニューまたはツールバーの stampIcon へスタンプメニューを追加する。
     * @param menu Ppane のコンテキストメニュー
     * @param kartePane PPnae
     */
    public void addStampMenu(JPopupMenu menu, final KartePane kartePane) {
        
        // 引数のPaneがPかつ編集可の時のみ追加する
        // コンテキストメニューなのでこれはOK
        if (kartePane != null && kartePane.getMyRole().equals(IInfoModel.ROLE_P) && kartePane.getTextPane().isEditable()) {
            
            StampBoxPlugin stampBox = getStampBox();

            List<StampTree> trees = stampBox.getAllTrees();
            
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            JComponent cmp = kartePane.getTextPane();
            TransferHandler handler = cmp.getTransferHandler();
            
            // StampBox内の全Treeをイテレートする
            for (StampTree stampTree : trees) {
                
                // 傷病名とテキストは別に作成するのでスキップする
                String entity = stampTree.getEntity();
                if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS) || entity.equals(IInfoModel.ENTITY_TEXT)) {
                    continue;
                }
                
                JMenu subMenu = builder.build(stampTree, cmp, handler);
                menu.add(subMenu);
            }   
        }
    }
    
    /**
     * 引数のポップアップメニューへスタンプメニューを追加する。
     * このメソッドはツールバーの stamp icon の actionPerformed からコールされる。
     * @param popup
     */
    public void addStampMenu(JPopupMenu popup) {
        
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getPPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable()) ? true : false;
            }
        }
        
        if (enabled) {
            addStampMenu(popup, kartePane);
        }
    }
    
    public StampTree getStampTree(String entity) {
        
        StampTree stampTree = getStampBox().getStampTree(entity);
        return stampTree;
    }
    
    public StampBoxPlugin getStampBox() {
        return (StampBoxPlugin) chart.getContext().getPlugin("stampBox");
    }
    
    public boolean hasTree(String entity) {
        StampBoxPlugin stBox = (StampBoxPlugin)chart.getContext().getPlugin("stampBox");
        StampTree tree = stBox.getStampTree(entity);
        return tree != null ? true : false;
    }
    
    public void applyInsurance(PVTHealthInsuranceModel hm) {
        
        Object target = getChain();
        if (target != null) {
            try {
                Method m = target.getClass().getMethod("applyInsurance", new Class[]{hm.getClass()});
                m.invoke(target, new Object[]{hm});
                
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
 
    
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public void cut() {
        if (curKarteComposit != null) {
            JComponent focusOwner = getCurrentComponent();
            if (focusOwner != null) {
                Action a = focusOwner.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
                if (a != null) {
                    a.actionPerformed(new ActionEvent(focusOwner,
                            ActionEvent.ACTION_PERFORMED,
                            null));
                    setCurKarteComposit(null);
                }
            }
        }
    }
    
    @Override
    public void copy() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    
    @Override
    public void paste() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    
    public void delete() {
    }
    
    public void resetStyle() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null && focusOwner instanceof JTextPane) {
            JTextPane pane = (JTextPane) focusOwner;
            pane.setCharacterAttributes(SimpleAttributeSet.EMPTY, true);
        }
    }
    
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
        updateUndoAction();
        updateRedoAction();
    }
    
    public void undo() {
        try {
            undoManager.undo();
            
        } catch (CannotUndoException ex) {
            ex.printStackTrace(System.err);
        }
        updateUndoAction();
        updateRedoAction();
    }
    
    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException ex) {
            ex.printStackTrace(System.err);
        }
        updateRedoAction();
        updateUndoAction();
    }
    
    private void updateUndoAction() {
        
        if(undoManager.canUndo()) {
            undoAction.setEnabled(true);
        } else {
            undoAction.setEnabled(false);
        }
    }
    
    private void updateRedoAction() {
        
        if(undoManager.canRedo()) {
            redoAction.setEnabled(true);
        } else {
            redoAction.setEnabled(false);
        }
    }
    
    public void fontLarger() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            if (curSize < 6) {
                curSize++;
            }
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
            if (curSize == 6) {
                enabledAction("fontLarger", false);
            }
        }
    }
    
    public void fontSmaller() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            if (curSize > 0) {
                curSize--;
            }
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
            if (curSize == 0) {
                enabledAction("fontSmaller", false);
            }
        }
    }
    
    public void fontStandard() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            curSize = 1;
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
            enabledAction("fontSmaller", true);
            enabledAction("fontLarger", true);
        }
    }
    
    public void fontBold() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-bold");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void fontItalic() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-italic");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    

    public void fontUnderline() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-underline");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void leftJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("left-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    
    public void centerJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("center-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }        
    }
    
    public void rightJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("right-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }         
    }
        
    private void colorAction(Color color) {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = new StyledEditorKit.ForegroundAction("color", color);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        "foreground"));
            }
        }  
    }
    
    public void fontRed() {
        colorAction(ClientContext.getColor("color.set.default.red"));
    }
    
    public void fontOrange() {
       colorAction(ClientContext.getColor("color.set.default.orange"));
    }
    
    public void fontYellow() {
        colorAction(ClientContext.getColor("color.set.default.yellow"));
    }
    
    public void fontGreen() {
        colorAction(ClientContext.getColor("color.set.default.green"));
    }
    
    public void fontBlue() {
        colorAction(ClientContext.getColor("color.set.default.blue"));
    }
    
    public void fontPurple() {
        colorAction(ClientContext.getColor("color.set.default.purpule"));
    }
    
    public void fontGray() {
        colorAction(ClientContext.getColor("color.set.default.gray"));
    }
    
    public void fontBlack() {
        colorAction(Color.BLACK);
    }
}