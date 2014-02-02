package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.table.ListTableModel;


/**
 * MasterItemTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 *
 */
public final class MasterItemTransferHandler extends TransferHandler {
    
    private DataFlavor masterItemFlavor = MasterItemTransferable.masterItemFlavor;
    
    private JTable sourceTable;
    private MasterItem dragItem;
    private boolean shouldRemove;
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) sourceTable.getModel();
        int fromIndex = sourceTable.getSelectedRow();
        dragItem = tableModel.getObject(fromIndex);
        return dragItem != null ? new MasterItemTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!canImport(support)) {
            return false;
        }

        try {
            JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();
            int toIndex = dl.getRow();
            if (dl.isInsertRow() && toIndex>-1) {
                Transferable t = support.getTransferable();
                MasterItem dropItem = (MasterItem) t.getTransferData(masterItemFlavor);
                JTable dropTable = (JTable) support.getComponent();
                ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) dropTable.getModel();
                shouldRemove = dropTable == sourceTable ? true : false;

                if (toIndex<tableModel.getObjectCount()) {
                    tableModel.addObject(toIndex, dropItem);
                } else {
                    tableModel.addObject(dropItem);
                }

                return true;
            }
        } catch (Exception ioe) {
            ioe.printStackTrace(System.err);
        }
        
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action==MOVE && shouldRemove && (dragItem!=null)) {
            ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) sourceTable.getModel();
            tableModel.delete(dragItem);
        }
        shouldRemove = false;
        dragItem = null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return (support.isDrop() && support.isDataFlavorSupported(masterItemFlavor))
                ? true
                : false;
    }
}
