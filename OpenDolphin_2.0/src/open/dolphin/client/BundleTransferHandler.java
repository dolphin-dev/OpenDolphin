package open.dolphin.client;

import java.io.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.text.*;
import open.dolphin.infomodel.BundleDolphin;

import open.dolphin.infomodel.ModuleModel;

/**
 * BundleTransferHandler
 * @author Minagawa,Kazushi
 */
public class BundleTransferHandler extends TransferHandler {
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public BundleTransferHandler() {
    }
    
    /**
     * DropされたFlavorをインポートする。
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!canImport(support)) {
            return false;
        }

        JTextComponent tc = (JTextComponent) support.getComponent();

        try {
            if (support.getTransferable().isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                return doStampDrop(tc, support.getTransferable());
                
            } else if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String str = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                tc.replaceSelection(str);
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
        JTextComponent source = (JTextComponent) c;
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
        if (action != MOVE) {
            return;
        }

        JTextComponent tc = (JTextComponent) c;
        
        if (tc.isEditable()) {
            if ((p0 != null) && (p1 != null) &&
                (p0.getOffset() != p1.getOffset())) {
                try {
                    tc.getDocument().remove(p0.getOffset(),
                            p1.getOffset() - p0.getOffset());
                } catch (BadLocationException e) {
                    System.out.println("Can't remove text from source.");
                }
            }
        }
    }
    
    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        JTextComponent tc = (JTextComponent) support.getComponent();
        boolean canImport = true;
        canImport = canImport && tc.isEditable();
        canImport = canImport && (support.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                                  support.isDataFlavorSupported(OrderListTransferable.orderListFlavor));
        return canImport;
    }
    
    /**
     * DropされたStamp(ModuleModel)をインポートする。
     * @param tr Transferable
     * @return インポートに成功した時 true
     */
    private boolean doStampDrop(JTextComponent tc, Transferable tr) {
        
        try {
            // スタンプのリストを取得する
            OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
            ModuleModel[] stamps = list.orderList;
            // pPaneにスタンプを挿入する
            for (int i = 0; i < stamps.length; i++) {
                if (stamps[i].getModel() instanceof BundleDolphin) {
                    BundleDolphin bd = (BundleDolphin)stamps[i].getModel();
                    tc.replaceSelection(bd.toString());
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
            JTextComponent pane = (JTextComponent) comp;
            if (pane.isEditable()) {
                pane.replaceSelection("");
            }
        }
    }
}
