package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.stampbox.StampTreeNode;

/**
 * KartePaneTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class SOATransferHandler extends TransferHandler implements IKarteTransferHandler {
    
    private static DataFlavor nixFileDataFlavor;
    
    static {
        try {
           nixFileDataFlavor  = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (Exception e) {
        }
    }
    
    private KartePane soaPane;
    
    private JTextPane source;
    
    private boolean shouldRemove;
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public SOATransferHandler(KartePane soaPane) {
        this.soaPane = soaPane;
    }

    @Override
//minagawa^ Paste problem    
//public boolean importData(JComponent c, Transferable tr) {
    public boolean importData(TransferHandler.TransferSupport support) {

//        JTextPane tc = (JTextPane)c;
//        if (!canImport(c, tr.getTransferDataFlavors())) {
//            return false;
//        }
        JTextPane tc = (JTextPane)support.getComponent();
        if (!canImport(support)) {
            return false;
        }
        Transferable tr = support.getTransferable();
//minagawa$        

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
                StampTreeNode droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
                return doStampInfoDrop(droppedNode);

            } else if (tr.isDataFlavorSupported(ImageEntryTransferable.imageEntryFlavor)) {
                // シェーマボックスからのDnDを受け入れる
                ImageEntry entry = (ImageEntry) tr.getTransferData(ImageEntryTransferable.imageEntryFlavor);
                return doImageEntryDrop(entry);

            } else if (tr.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
                // Paneからのシェーマを受け入れる
                SchemaList list = (SchemaList) tr.getTransferData(SchemaListTransferable.schemaListFlavor);
                return doSchemaDrop(list);

            } else if (tr.isDataFlavorSupported(AttachmentTransferable.attachmentFlavor)) {
                // PaneからのAttachmentを受け入れる
                AttachmentModel attachment = (AttachmentModel)tr.getTransferData(AttachmentTransferable.attachmentFlavor);
                return doAttachmentDrop(attachment);

            } else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String str = (String) tr.getTransferData(DataFlavor.stringFlavor);
                tc.replaceSelection(str);
                shouldRemove = tc == source ? true : false;
                return true;

            } else if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                // Image File ならimportする
                List<File> files = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
                return doFileDrop(files);
                
            } else if (tr.isDataFlavorSupported(nixFileDataFlavor)) {
                // Image File ならimportする
                String data = (String)tr.getTransferData(nixFileDataFlavor);
                return doFileDropNix(data);
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
//minagawa^ Paste problem
//    @Override
//    public boolean canImport(JComponent c, DataFlavor[] flavors) {
//        JTextPane tc = (JTextPane) c;
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

    /**
     * Flavorリストのなかに受け入れられものがあるかどうかを返す。
     */
    protected boolean hasFlavor(DataFlavor[] flavors) {
        
        boolean ret = false;

        for (DataFlavor flavor : flavors) {
            // String ok
            if (DataFlavor.stringFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // StampTreeNode OK
            else if (LocalStampTreeNodeTransferable.localStampTreeNodeFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // Schema OK
            else if (SchemaListTransferable.schemaListFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // Attachment OK
            else if (AttachmentTransferable.attachmentFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // Image OK
            else if (ImageEntryTransferable.imageEntryFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // File OK
            else if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                ret = true;
                break;
            }
            // Unix|Linux File List maybe jdk1.6
            else if (nixFileDataFlavor.equals(flavor)) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    /**
     * DropされたModuleInfo(StampInfo)をインポートする。
     * @param tr Transferable
     * @return 成功した時 true
     */
    private boolean doStampInfoDrop(StampTreeNode droppedNode) {
        
        try {
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
//minagawa^ LSC Test            
            //ArrayList<ModuleInfoBean> addList = new ArrayList<ModuleInfoBean>(5);
            ArrayList<ModuleInfoBean> textList = new ArrayList(5);
            ArrayList<ModuleInfoBean> soaList = new ArrayList(5);
//            String role = null;
            while (e.hasMoreElements()) {
                StampTreeNode node = (StampTreeNode) e.nextElement();
                if (node.isLeaf()) {
                    ModuleInfoBean stampInfo = (ModuleInfoBean) node.getStampInfo();
                    if (!stampInfo.isSerialized()) {
                        continue;
                    }
                    if (stampInfo.getEntity().equals(IInfoModel.ROLE_TEXT)) {
                        textList.add(stampInfo);
                        
                    } else if (stampInfo.getEntity().equals(IInfoModel.ROLE_SOA)) {
                        soaList.add(stampInfo);
                    }
//                    if (stampInfo.isSerialized() && (!stampInfo.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) ) {
//                        if (role == null) {
//                            role = stampInfo.getStampRole();
//                        }
//                        addList.add(stampInfo);
//                    }
                }              
            }
            // まとめてデータベースからフェッチしインポートする
//            if (role.equals(IInfoModel.ROLE_TEXT)) {
//                soaPane.textStampInfoDropped(addList);
//            } else if (role.equals(IInfoModel.ROLE_SOA)) {
//                soaPane.stampInfoDropped(addList);
//            }
            if (!textList.isEmpty()) {
                soaPane.textStampInfoDropped(textList);
            }
            if (!soaList.isEmpty()) {
                soaPane.stampInfoDropped(soaList);
            }
//minagawa$                    
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
    private boolean doSchemaDrop(SchemaList list) {
        
        try {
            // Schemaリストを取得する
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
     * DropされたAttachmentをインポーオする。
     * @param attachment AttachmentModel
     * @return
     */
    private boolean doAttachmentDrop(AttachmentModel attachment) {
        
        try {
            soaPane.stampAttachment(attachment);
            if (soaPane.getDraggedCount() > 0 && soaPane.getDrragedStamp() != null) {
                soaPane.setDroppedCount(1);
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
    private boolean doImageEntryDrop(ImageEntry entry) {
        
        try {
            soaPane.imageEntryDropped(entry);
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * DropされたイメージFileをインポートする。
     */
    private boolean doFileDrop(List<File> files) {
        try {
            File file = files.get(0);
            soaPane.fileDropped(file);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * StackOverFlow: Linux maybe Unix File Drop
     * @param data uri-list
     * @return File の時 true
     */
    private boolean doFileDropNix(String data) {
        
        File file = null;
        
        for(StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String token = st.nextToken().trim();
            
            if(token.startsWith("#") || token.isEmpty()) {
                // comment line, by RFC 2483
                continue;
            }
            try {
                file = new File(new URI(token));
                break;
                
            } catch(Exception e) {
                e.printStackTrace(System.err);
            }
        }
        
        if (file!=null) {
            soaPane.fileDropped(file);
            return true;
        } else {
            return false;
        }
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

    @Override
    public JComponent getComponent() {
        return soaPane.getTextPane();
    }

    private boolean canPaste() {
        if (!soaPane.getTextPane().isEditable()) {
            return false;
        }
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return false;
        }
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
            t.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor) ||
            t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor) ||
            t.isDataFlavorSupported(ImageEntryTransferable.imageEntryFlavor) ||
            t.isDataFlavorSupported(AttachmentTransferable.attachmentFlavor) ||
            t.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
            t.isDataFlavorSupported(nixFileDataFlavor)) {
            return true;
        }
        return false;
    }

    @Override
    public void enter(ActionMap map) {
        // SOAPane がクリックされた状態
        if (soaPane.getTextPane().isEditable()) {
            map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
            map.get(GUIConst.ACTION_INSERT_TEXT).setEnabled(true);
            map.get(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(true);
            map.get(GUIConst.ACTION_ATTACHMENT).setEnabled(true);
        }
    }

    @Override
    public void exit(ActionMap map) {
    }
}
