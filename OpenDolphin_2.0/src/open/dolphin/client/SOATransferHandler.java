package open.dolphin.client;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.text.*;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.SchemaModel;

/**
 * KartePaneTransferHandler
 *
 * @author Minagawa,Kazushi
 */
public class SOATransferHandler extends TransferHandler {
    
    private static final long serialVersionUID = -7891004155072724783L;
    
    private KartePane soaPane;
    
    private DataFlavor stringFlavor = DataFlavor.stringFlavor;
    
    private JTextPane source;
    
    private boolean shouldRemove;
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public SOATransferHandler(KartePane soaPane) {
        this.soaPane = soaPane;
    }
    
    /**
     * DropされたFlavorをインポートする。
     */
    @Override
    public boolean importData(JComponent c, Transferable tr) {
        
        JTextPane tc = (JTextPane) c;
        
        if (!canImport(c, tr.getTransferDataFlavors())) {
            return false;
        }
        
        if (tc.equals(source) &&
                (tc.getCaretPosition() >= p0.getOffset()) &&
                (tc.getCaretPosition() <= p1.getOffset())) {
            shouldRemove = false;
            return true;
        }
        
        try {
            if (tr.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
                // StampTreeNodeを受け入れる
                shouldRemove = false;
                return doStampInfoDrop(tr);
                
            } else if (tr.isDataFlavorSupported(ImageEntryTransferable.imageEntryFlavor)) {
                // シェーマボックスからのDnDを受け入れる
                return doImageEntryDrop(tr);
                
            } else if (tr.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
                // Paneからのシェーマを受け入れる
                return doSchemaDrop(tr);
                
            } else if (tr.isDataFlavorSupported(stringFlavor)) {
                String str = (String) tr.getTransferData(stringFlavor);
                tc.replaceSelection(str);
                shouldRemove = tc == source ? true : false;
                return true;
            }
        } catch (UnsupportedFlavorException ufe) {
        } catch (IOException ioe) {
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
    
    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        JTextPane tc = (JTextPane) c;
        if (tc.isEditable() && hasFlavor(flavors)) {
            return true;
        }
        return false;
    }
    
    /**
     * Flavorリストのなかに受け入れられものがあるかどうかを返す。
     */
    protected boolean hasFlavor(DataFlavor[] flavors) {
        
        for (DataFlavor flavor : flavors) {
            // String ok
            if (stringFlavor.equals(flavor)) {
                return true;
            }
            // StampTreeNode OK
            if (LocalStampTreeNodeTransferable.localStampTreeNodeFlavor.equals(flavor)) {
                return true;
            }
            // Schema OK
            if (SchemaListTransferable.schemaListFlavor.equals(flavor)) {
                return true;
            }
            // Image OK
            if (ImageEntryTransferable.imageEntryFlavor.equals(flavor)) {
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
    private boolean doStampInfoDrop(Transferable tr) {
        
        try {
            // DropされたTreeNodeを取得する
            StampTreeNode droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
            
            // 葉の場合
            if (droppedNode.isLeaf()) {
                ModuleInfoBean stampInfo = (ModuleInfoBean) droppedNode.getStampInfo();
                String role = stampInfo.getStampRole();
                if (role.equals(IInfoModel.ROLE_TEXT)) {
                    soaPane.stampInfoDropped(stampInfo);
                } else if (role.equals(IInfoModel.ROLE_SOA)) {
                    soaPane.stampInfoDropped(stampInfo);
                }
                return true;
            }
            
            // Dropされたノードの葉を列挙する
            Enumeration e = droppedNode.preorderEnumeration();
            ArrayList<ModuleInfoBean> addList = new ArrayList<ModuleInfoBean>(5);
            String role = null;
            while (e.hasMoreElements()) {
                StampTreeNode node = (StampTreeNode) e.nextElement();
                if (node.isLeaf()) {
                    ModuleInfoBean stampInfo = (ModuleInfoBean) node.getStampInfo();
                    if (stampInfo.isSerialized() && (!stampInfo.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) ) {
                        if (role == null) {
                            role = stampInfo.getStampRole();
                        }
                        addList.add(stampInfo);
                    }
                }
            }
            
            // まとめてデータベースからフェッチしインポートする
            if (role.equals(IInfoModel.ROLE_TEXT)) {
                soaPane.textStampInfoDropped(addList);
            } else if (role.equals(IInfoModel.ROLE_SOA)) {
                soaPane.stampInfoDropped(addList);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * Dropされたシェーマをインポーオする。
     * @param tr
     * @return
     */
    private boolean doSchemaDrop(Transferable tr) {
        
        try {
            // Schemaリストを取得する
            SchemaList list = (SchemaList) tr.getTransferData(SchemaListTransferable.schemaListFlavor);
            SchemaModel[] schemas = list.schemaList;
            for (int i = 0; i < schemas.length; i++) {
                soaPane.stampSchema(schemas[i]);
            }
            if (soaPane.getDraggedCount() > 0 && soaPane.getDrragedStamp() != null) {
                soaPane.setDroppedCount(schemas.length);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * Dropされたイメージをインポートする。
     */
    private boolean doImageEntryDrop(final Transferable tr) {
        
        try {
            // Imageを取得する
            ImageEntry entry = (ImageEntry) tr.getTransferData(ImageEntryTransferable.imageEntryFlavor);
            soaPane.imageEntryDropped(entry);
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
        // cut の時 ...?
        if (action == MOVE) {
            JTextPane pane = (JTextPane) comp;
            if (pane.isEditable()) {
                pane.replaceSelection("");
            }
        }
    }
}
