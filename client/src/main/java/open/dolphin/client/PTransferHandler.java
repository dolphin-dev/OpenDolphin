package open.dolphin.client;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.project.Project;
import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.stampbox.TreeInfo;

/**
 * KartePaneTransferHandler
 * @author Minagawa,Kazushi
 */
public class PTransferHandler extends TransferHandler implements IKarteTransferHandler {
    
    // KartePane
    private final KartePane pPane;
    
    private JTextPane source;
    
    private boolean shouldRemove;
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public PTransferHandler(KartePane pPane) {
        this.pPane = pPane;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!canImport(support)) {
            return false;
        }
        
        JTextPane tc = (JTextPane)support.getComponent();
        if (tc.equals(source) &&
                (tc.getCaretPosition() >= p0.getOffset()) &&
                (tc.getCaretPosition() <= p1.getOffset())) {
            shouldRemove = false;
            return true;
        }
        try {
            Transferable tr = support.getTransferable();
            if (tr.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
                // スタンプボックスからのスタンプをインポートする
                shouldRemove = false;
                StampTreeNode droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
                return doStampInfoDrop(droppedNode);

            } else if (tr.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                // KartePaneからのオーダスタンプをインポートする
                OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
                return doStampDrop(list, support.isDrop());

            } else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String str = (String) tr.getTransferData(DataFlavor.stringFlavor);
                tc.replaceSelection(str);
                shouldRemove = tc == source;
                return true;
            }
        } catch (UnsupportedFlavorException | IOException ufe) {
        }
        return false;
    }   
    
    // Create a Transferable implementation that contains the
    // selected text.
    @Override
    protected Transferable createTransferable(JComponent c) {
        source = (JTextPane) c;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        Document doc = source.getDocument();
        if (start == end) {
            return null;
        }
        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        } catch (BadLocationException e) {
        }
        String data = source.getSelectedText();
        return new StringSelection(data);
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    // Remove the old text if the action is a MOVE.
    // However, we do not allow dropping on top of the selected text,
    // so in that case do nothing.
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        JTextComponent tc = (JTextComponent) c;
        if (tc.isEditable() && (shouldRemove == true) && (action == MOVE)) {
            if ((p0 != null) && (p1 != null)
            && (p0.getOffset() != p1.getOffset())) {
                try {
                    tc.getDocument().remove(p0.getOffset(),
                            p1.getOffset() - p0.getOffset());
                } catch (BadLocationException e) {
                }
            }
        }
        shouldRemove = false;
        source = null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {  
        JTextPane tc = (JTextPane)support.getComponent();
        boolean ok = tc.isEditable();
        ok = ok && hasFlavor(support.getDataFlavors());
        return ok;
    }   

    /**
     * Flavorリストのなかに受け入れられものがあるかどうかを返す。
     * @param flavors
     * @return 
     */
    protected boolean hasFlavor(DataFlavor[] flavors) {

        for (DataFlavor flavor : flavors) {
            // String OK
            if (DataFlavor.stringFlavor.equals(flavor)) {
                return true;
            }
            // StampTreeNode(FromStampTree) OK
            if (LocalStampTreeNodeTransferable.localStampTreeNodeFlavor.equals(flavor)) {
                return true;
            }
            // OrderStamp List OK
            if (OrderListTransferable.orderListFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * DropされたModuleInfo(StampInfo)をインポートする。
     * @param tr Transferable
     * @return 成功した時 true
     */
    private boolean doStampInfoDrop(StampTreeNode droppedNode) {
        Chart chart = null;
        ChartDocument doc = pPane.getParent();
        if(doc instanceof KarteEditor) {
            chart = ((KarteEditor)doc).getContext();
        }
        
        try {
            // 葉の場合
            if (droppedNode.isLeaf()) {
                ModuleInfoBean stampInfo = (ModuleInfoBean)droppedNode.getStampInfo();
                String role = stampInfo.getStampRole();
                if (role.equals(IInfoModel.ROLE_P)) {
                    pPane.stampInfoDropped(stampInfo);
                } else if (role.equals(IInfoModel.ROLE_TEXT)) {
                    pPane.stampInfoDropped(stampInfo);
                } else if (role.equals(IInfoModel.ROLE_ORCA_SET)) {
                    pPane.stampInfoDropped(stampInfo);
                }
                // 病名も受け入れる
                else if (role.equals(IInfoModel.ROLE_DIAGNOSIS)) {
                    // contextへ追加（エクスポートしておく）
                    java.util.ResourceBundle bundle = ClientContext.getMyBundle(PTransferHandler.class);
                    String treeName = bundle.getString("treeName.fromEditor");
                    String message = bundle.getString("message.dropedEditor");
                    if(stampInfo.getStampName().equals(treeName)) {
                        JOptionPane.showMessageDialog(null, message, ClientContext.getString("productString"), JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        pPane.getParent().getContext().addDroppedDiagnosis(stampInfo);
                        showDiagnosisAddedMessage(stampInfo.getStampName());
                    }
                }
                return true;
            }
            
            // Path にふくまれているTextはSOA側へ挿入する
            // カルテのテンプレートと考える
            DefaultMutableTreeNode folderNode = (DefaultMutableTreeNode)droppedNode;
            StampTreeNode root = (StampTreeNode)folderNode.getRoot();
            TreeInfo info = (TreeInfo)root.getUserObject();
            String importEntity = info.getEntity();
            boolean pathTree = importEntity.contains(IInfoModel.ENTITY_PATH);
            
            // Dropされたノードの葉を列挙する
            Enumeration e = droppedNode.preorderEnumeration();
            
            // 種類別のリストに別ける
            ArrayList<ModuleInfoBean> textList = new ArrayList<>(2);
            ArrayList<ModuleInfoBean> stamptList = new ArrayList<>(2);
            ArrayList<ModuleInfoBean> diagList = new ArrayList<>(2);
            
//s.oh^ 2014/08/01 パス対応
            boolean radiology = false;
            if(Project.getBoolean("stamp.path.text.p")) {
                while(e.hasMoreElements()) {
                    StampTreeNode node = (StampTreeNode)e.nextElement();
                    if(node.isLeaf()) {
                        ModuleInfoBean stampInfo = (ModuleInfoBean)node.getStampInfo();
                        if(stampInfo.getEntity().equals(IInfoModel.ENTITY_RADIOLOGY_ORDER)) {
                            radiology = true;
                            break;
                        }
                    }
                }
                e = droppedNode.preorderEnumeration();
            }
//s.oh$
            
            while (e.hasMoreElements()) {
                StampTreeNode node = (StampTreeNode)e.nextElement();
                if (node.isLeaf()) {
                    ModuleInfoBean stampInfo = (ModuleInfoBean)node.getStampInfo();
                    String role = stampInfo.getStampRole();
                    
                    if (stampInfo.isSerialized() && (role.equals(IInfoModel.ROLE_TEXT)) ) {
                        // Text Stamp
//s.oh^ 2014/08/01 パス対応
                        //textList.add(stampInfo);
                        if(radiology) {
                            stamptList.add(stampInfo);
                        }else{
                            textList.add(stampInfo);
                        }
//s.oh$
                    }
                    
                    else if (stampInfo.isSerialized() && (role.equals(IInfoModel.ROLE_P))) {
                        // P Stamp
                        stamptList.add(stampInfo);
                    }
                    
                    else if (stampInfo.isSerialized() && (role.equals(IInfoModel.ROLE_DIAGNOSIS))) {
                        // 病名 Stamp
                        diagList.add(stampInfo);
                    }
                }
            }
            
            // カルテのテンプレートかどうかはpathで病名があるかどうか
//s.oh^ 2013/08/12 パスの傷病名対応
            //boolean hasDisease = (!diagList.isEmpty());
            boolean hasDisease = true;
//s.oh$
            boolean template = pathTree && hasDisease;
            
            // まとめてデータベースからフェッチしインポートする
            if (!textList.isEmpty()) {
                // テンプレートの場合はSOA
                if (template) {
                    KarteEditor ke = (KarteEditor)pPane.getParent();
                    ke.getSOAPane().textStampInfoDropped(textList);
                    
                } else {
                    pPane.textStampInfoDropped(textList);
                }
            }
            
            if (!stamptList.isEmpty()) {
                pPane.stampInfoDropped(stamptList);
            }
            
            // contextへエクスポートする
            if (!diagList.isEmpty()) {
                Chart context = pPane.getParent().getContext();
                StringBuilder sb = new StringBuilder();
                int index = 0;
                boolean addedOther = false;
                for (ModuleInfoBean b : diagList) {
                    context.addDroppedDiagnosis(b);
                    if (index==0) {
                        sb.append(b.getStampName());
                    } else {
                        if(!addedOther) {
                            sb.append(ClientContext.getMyBundle(PTransferHandler.class).getString("text.etc"));
                            addedOther = true;
                        }
                    }
                    index++;
                }
                // message
                showDiagnosisAddedMessage(sb.toString());
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * DropされたStamp(ModuleModel)をインポートする。
     * @param tr Transferable
     * @return インポートに成功した時 true
     */
    private boolean doStampDrop(OrderList list, boolean drop) {           
        Chart chart = null;
        ChartDocument doc = pPane.getParent();
        if(doc instanceof KarteEditor) {
            chart = ((KarteEditor)doc).getContext();
        }
        
        try {
            // スタンプのリストを取得する
            ModuleModel[] stamps = list.orderList;
            // pPaneにスタンプを挿入する
            for (int i = 0; i < stamps.length; i++) {
                pPane.stamp(stamps[i]);
            }
            // dragggされたスタンプがあるときdroppした数を設定する
            // これで同じpane内でのDnDを判定している
            if (pPane.getDraggedCount() > 0 && pPane.getDrragedStamp() != null) {
                if (drop) {
                    pPane.setDroppedCount(stamps.length);
                } else {
                    pPane.setDraggedCount(0);
                    pPane.setDroppedCount(0);
                    pPane.setDrragedStamp(null);  
                }           
            } 
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * クリップボードへデータを転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        super.exportToClipboard(comp, clip, action);
        // cut の場合を処理する
        if (action == MOVE) {
            JTextPane pane = (JTextPane) comp;
            if (pane.isEditable()) {
                pane.replaceSelection("");
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return pPane.getTextPane();
    }

    private boolean canPaste() {
        if (!pPane.getTextPane().isEditable()) {
            return false;
        }
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return false;
        }
        return t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                t.isDataFlavorSupported(OrderListTransferable.orderListFlavor) ||
                t.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
    }

    @Override
    public void enter(ActionMap map) {
        if (pPane.getTextPane().isEditable()) {
            map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
            map.get(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
            map.get(GUIConst.ACTION_INSERT_STAMP).setEnabled(true);
        }
    }

    @Override
    public void exit(ActionMap map) {
    }
    
    private void showDiagnosisAddedMessage(String dicease) {
        
//s.oh^ 2013/08/12 パスの傷病名対応
        //boolean show = Project.getBoolean("show.diagnosis.added.message", true);
        boolean show = true;
//s.oh$
        
        if (show) {
//s.oh^ 2013/08/12 パスの傷病名対応
            //JLabel msg1 = new JLabel("下記を傷病名タブに追加しました。");
            //JLabel msg2 = new JLabel(dicease);
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(PTransferHandler.class);
            String m1 = bundle.getString("message.addedDiagnosis1");
            String m2 = bundle.getString("message.addedDiagbosis2");
            String cbText = bundle.getString("confirm.noShowMessage");
            String paneTitle = bundle.getString("title.optionPane");
            
            JLabel msg1 = new JLabel(m1);
            JLabel msg2 = new JLabel(m2);
            JLabel msg3 = new JLabel(dicease);
//s.oh$
            final JCheckBox cb = new JCheckBox(cbText);
            cb.setFont(new Font("Dialog", Font.PLAIN, 10));
            cb.addActionListener((ActionEvent e) -> {
                Project.setBoolean("show.diagnosis.added.message", !cb.isSelected());
            });
            
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(pPane.getParent().getUI()),
//s.oh^ 2013/08/12 パスの傷病名対応
                    //new Object[]{msg1,msg2,msg3,cb},
                    new Object[]{msg1,msg2,msg3},
//s.oh$
                    ClientContext.getFrameTitle(paneTitle),
                    JOptionPane.INFORMATION_MESSAGE,
                    ClientContext.getImageIconArias("icon_info"));
        }
    }
}
