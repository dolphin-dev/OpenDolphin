package open.dolphin.impl.psearch;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.table.ListTableModel;


/**
 * MasterItemTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 *
 */
public final class PatientSearchTransferHandler extends TransferHandler {
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable sourceTable = (JTable)c;
        ListTableModel<PatientModel> tableModel = (ListTableModel<PatientModel>)sourceTable.getModel();
        int fromIndex = sourceTable.getSelectedRow();
        PatientModel dragItem = (PatientModel)tableModel.getObject(fromIndex);
        return dragItem != null ? new PatientTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
}
