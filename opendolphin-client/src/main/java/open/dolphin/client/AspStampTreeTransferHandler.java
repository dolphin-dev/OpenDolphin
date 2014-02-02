package open.dolphin.client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * AspStampTreeTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public class AspStampTreeTransferHandler extends TransferHandler {
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        StampTree sourceTree = (StampTree) c;
        StampTreeNode dragNode = (StampTreeNode) sourceTree.getLastSelectedPathComponent();
        return new LocalStampTreeNodeTransferable(dragNode);
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    public boolean importData(JComponent c, Transferable tr) {
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
    
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        return false;
    }
}