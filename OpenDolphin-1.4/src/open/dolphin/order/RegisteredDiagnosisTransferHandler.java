package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.table.ListTableModel;


/**
 * RegisteredDiagnosisTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public class RegisteredDiagnosisTransferHandler extends TransferHandler {
    
    private DataFlavor registeredDiagnosisFlavor = RegisteredDiagnosisTransferable.registeredDiagnosisFlavor;
    
    private JTable sourceTable;
    private boolean shouldRemove;
    private int fromIndex;
    private int toIndex;
    private DiseaseEditor editor;
    
    public RegisteredDiagnosisTransferHandler(DiseaseEditor editor) {
        this.editor = editor;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>) sourceTable.getModel();
        fromIndex = sourceTable.getSelectedRow();
        RegisteredDiagnosisModel dragItem = tableModel.getObject(fromIndex);
        return dragItem != null ? new RegisteredDiagnosisTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                //RegisteredDiagnosisModel dropItem = (RegisteredDiagnosisModel) t.getTransferData(registeredDiagnosisFlavor);
                JTable dropTable = (JTable) c;
                ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>) dropTable.getModel();
                toIndex = dropTable.getSelectedRow();
                shouldRemove = dropTable == sourceTable ? true : false;
                if (shouldRemove) {
                    tableModel.moveRow(fromIndex, toIndex);
                    editor.reconstractDiagnosis();
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
        }
        shouldRemove = false;
        fromIndex = -1;
        toIndex = -1;
    }
    
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        JTable dropTable = (JTable) c;
        ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>) dropTable.getModel();
        if (tableModel.getObject(dropTable.getSelectedRow()) != null) {
            for (int i = 0; i < flavors.length; i++) {
                if (registeredDiagnosisFlavor.equals(flavors[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
