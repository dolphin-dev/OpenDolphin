package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import open.dolphin.table.ObjectReflectTableModel;


/**
 * MasterItemTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public class MasterItemTransferHandler extends TransferHandler {
    
    private static final long serialVersionUID = 4871088750931696219L;
    
    private DataFlavor masterItemFlavor = MasterItemTransferable.masterItemFlavor;
    
    private JTable sourceTable;
    //private MasterItem dragItem;
    //private MasterItem dropItem;
    private boolean shouldRemove;
    private int fromIndex;
    private int toIndex;
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ObjectReflectTableModel tableModel = (ObjectReflectTableModel) sourceTable.getModel();
        fromIndex = sourceTable.getSelectedRow();
        MasterItem dragItem = (MasterItem) tableModel.getObject(fromIndex);
        return dragItem != null ? new MasterItemTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                MasterItem dropItem = (MasterItem) t.getTransferData(masterItemFlavor);
                JTable dropTable = (JTable) c;
                ObjectReflectTableModel tableModel = (ObjectReflectTableModel) dropTable.getModel();
                toIndex = dropTable.getSelectedRow();
                shouldRemove = dropTable == sourceTable ? true : false;
                if (shouldRemove) {
                    tableModel.moveRow(fromIndex, toIndex);
                } else {
                    tableModel.addRow(toIndex, dropItem);
                }
                sourceTable.getSelectionModel().setSelectionInterval(toIndex, toIndex);
                return true;
            } catch (Exception ioe) {
            }
        }
        
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE && shouldRemove) {
//            ObjectReflectTableModel tableModel = (ObjectReflectTableModel) sourceTable.getModel();
//            tableModel.deleteRow(dragItem);
//            int index = tableModel.getIndex(dropItem);
//            if (index > -1) {
//                sourceTable.getSelectionModel().setSelectionInterval(index, index);
//            }
        }
        shouldRemove = false;
        fromIndex = -1;
        toIndex = -1;
    }
    
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        JTable dropTable = (JTable) c;
        ObjectReflectTableModel tableModel = (ObjectReflectTableModel) dropTable.getModel();
        if (tableModel.getObject(dropTable.getSelectedRow()) != null) {
            for (int i = 0; i < flavors.length; i++) {
                if (masterItemFlavor.equals(flavors[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
