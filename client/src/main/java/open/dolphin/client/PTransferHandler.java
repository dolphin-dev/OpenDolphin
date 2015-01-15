package open.dolphin.client;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import open.dolphin.util.Log;

/**
 * KartePaneTransferHandler
 * @author Minagawa,Kazushi
 */
public class PTransferHandler extends TransferHandler implements IKarteTransferHandler {
    
    // KartePane
    private KartePane pPane;
    
    private JTextPane source;
    
    private boolean shouldRemove;
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public PTransferHandler(KartePane pPane) {
        this.pPane = pPane;
    }
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
//    @Override
//    public boolean importData(JComponent c, Transferable tr) {
//
//        if (!canImport(c, tr.getTransferDataFlavors())) {
//            return false;
//        }
//        
//        JTextPane tc = (JTextPane)c;
//
//        if (tc.equals(source) &&
//                (tc.getCaretPosition() >= p0.getOffset()) &&
//                (tc.getCaretPosition() <= p1.getOffset())) {
//            shouldRemove = false;
//            return true;
//        }
//
//        try {
//            if (tr.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
//                // スタンプボックスからのスタンプをインポートする
//                shouldRemove = false;
//                StampTreeNode droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
//                return doStampInfoDrop(droppedNode);
//
//            } else if (tr.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
//                // KartePaneからのオーダスタンプをインポートする
//                OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
//                return doStampDrop(list);
//
//            } else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//                String str = (String) tr.getTransferData(DataFlavor.stringFlavor);
//                tc.replaceSelection(str);
//                shouldRemove = tc == source ? true : false;
//                return true;
//            }
//        } catch (UnsupportedFlavorException ufe) {
//        } catch (IOException ioe) {
//        }
//
//        return false;
//    }
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
                shouldRemove = tc == source ? true : false;
                return true;
            }
        } catch (UnsupportedFlavorException | IOException ufe) {
        }
        return false;
    }
//minagawa$    
    
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

//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
    /**
     * インポート可能かどうかを返す。
     */
//    @Override
//    public boolean canImport(JComponent c, DataFlavor[] flavors) {
//        JTextPane tc = (JTextPane)c;
//        if (tc.isEditable() && hasFlavor(flavors)) {
//            return true;
//        }
//        return false;
//    }
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {  
        JTextPane tc = (JTextPane)support.getComponent();
        boolean ok = tc.isEditable();
        ok = ok && hasFlavor(support.getDataFlavors());
        return ok;
    }
//minagawa$    

    /**
     * Flavorリストのなかに受け入れられものがあるかどうかを返す。
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
        Log.outputOperLogOper(chart, Log.LOG_LEVEL_0, "スタンプボックス → P", "スタンプボックスからのスタンプ追加");
        
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
                    if(stampInfo.getStampName().equals("エディタから発行...")) {
                        JOptionPane.showMessageDialog(null, "エディタから発行する場合は直接傷病名タブへ追加してください。", ClientContext.getString("productString"), JOptionPane.INFORMATION_MESSAGE);
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "エディタから発行する場合は直接傷病名タブへ追加してください。");
                    }else{
                        pPane.getParent().getContext().addDroppedDiagnosis(stampInfo);
                        showDiagnosisAddedMessage(stampInfo.getStampName());
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "病名の追加", stampInfo.getStampName());
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
            ArrayList<ModuleInfoBean> textList = new ArrayList<ModuleInfoBean>(2);
            ArrayList<ModuleInfoBean> stamptList = new ArrayList<ModuleInfoBean>(2);
            ArrayList<ModuleInfoBean> diagList = new ArrayList<ModuleInfoBean>(2);
            
//s.oh^ 2014/08/01 パス対応
            boolean radiology = false;
            if(Project.getBoolean("stamp.path.text.p")) {
                while(e.hasMoreElements()) {
                    StampTreeNode node = (StampTreeNode)e.nextElement();
                    if(node.isLeaf()) {
                        ModuleInfoBean stampInfo = (ModuleInfoBean)node.getStampInfo();
                        if(stampInfo.getEntity().equals(IInfoModel.ENTITY_RADIOLOGY_ORDER)) {
                            radiology = true;
                            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "パスに放射線が含まれている(テキスト→P)");
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
                            sb.append("他");
                            addedOther = true;
                        }
                    }
                    index++;
                }
                // message
                showDiagnosisAddedMessage(sb.toString());
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "追加した病名", sb.toString());
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
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
    //private boolean doStampDrop(OrderList list) {
    private boolean doStampDrop(OrderList list, boolean drop) {    
//minagawa$        
        Chart chart = null;
        ChartDocument doc = pPane.getParent();
        if(doc instanceof KarteEditor) {
            chart = ((KarteEditor)doc).getContext();
        }
        Log.outputOperLogOper(chart, Log.LOG_LEVEL_0, "P → P", "カルテからのスタンプ追加");
        
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
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
//                pPane.setDroppedCount(stamps.length);
                if (drop) {
                    pPane.setDroppedCount(stamps.length);
                } else {
                    pPane.setDraggedCount(0);
                    pPane.setDroppedCount(0);
                    pPane.setDrragedStamp(null);  
                }
//minagawa$                  
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
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
            t.isDataFlavorSupported(OrderListTransferable.orderListFlavor) ||
            t.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
            return true;
        }
        return false;
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
            JLabel msg1 = new JLabel("傷病名を追加しました。");
            JLabel msg2 = new JLabel("傷病名タブを開いて、保存してください。");
            JLabel msg3 = new JLabel(dicease);
//s.oh$
            final JCheckBox cb = new JCheckBox("今後このメッセージを表示しない");
            cb.setFont(new Font("Dialog", Font.PLAIN, 10));
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Project.setBoolean("show.diagnosis.added.message", !cb.isSelected());
                }
            });
            
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(pPane.getParent().getUI()),
//s.oh^ 2013/08/12 パスの傷病名対応
                    //new Object[]{msg1,msg2,msg3,cb},
                    new Object[]{msg1,msg2,msg3},
//s.oh$
                    ClientContext.getFrameTitle("パススタンプ"),
                    JOptionPane.INFORMATION_MESSAGE,
//minagawa^ Icon Server                    
                    //ClientContext.getImageIcon("about_32.gif"));
                    ClientContext.getImageIconArias("icon_info"));
//minagawa$            
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, ClientContext.getFrameTitle("パススタンプ"), msg1.getText(), msg2.getText());
        }
    }
}
