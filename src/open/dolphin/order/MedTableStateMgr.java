package open.dolphin.order;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import open.dolphin.table.ObjectReflectTableModel;

/**
 * SetTableStateMgr
 * 
 * @author Minagawa,Kazushi
 *
 */
public class MedTableStateMgr {

    private MedEmptyState emptyState;
    private MedHasItemState hasItemState;
    private AbstractMedTableState curState;
    private MedicineTablePanel target;
    private JTable medTable;
    private boolean validModel;

    public MedTableStateMgr(MedicineTablePanel target, JTable medTable, 
            JButton deleteBtn, JButton clearBtn, 
            JTextField stampNameField, JLabel stateLabel) {
        this.target = target;
        emptyState = new MedEmptyState(medTable, deleteBtn, clearBtn, stampNameField, stateLabel);
        hasItemState = new MedHasItemState(medTable, deleteBtn, clearBtn, stampNameField, stateLabel);
        this.medTable = medTable;
    }

    public void checkState() {
        
        ObjectReflectTableModel tableModel = (ObjectReflectTableModel) medTable.getModel();
        int cnt = tableModel.getObjectCount();
        if (cnt > 0) {
            curState = hasItemState;
            curState.enter();
        } else if (cnt == 0) {
            curState = emptyState;
            curState.enter();
        }
        boolean newValid = curState.isValidModel();
        
        if (newValid != validModel) {
            validModel = newValid;
            target.setValidModel(validModel);
        }
    }
}
