package open.dolphin.order;

import javax.swing.JButton;
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
	
	public MedTableStateMgr(MedicineTablePanel target, JTable medTable, JButton deleteBtn, JButton clearBtn, JTextField stampNameField, JTextField adminField) {
		this.target = target;
		emptyState = new MedEmptyState(medTable, deleteBtn, clearBtn, stampNameField, adminField);
		hasItemState = new MedHasItemState(medTable, deleteBtn, clearBtn, stampNameField, adminField);
		this.medTable = medTable;
	}
	
	public void checkState() {
		System.out.println("CheckState");
		ObjectReflectTableModel tableModel = (ObjectReflectTableModel) medTable.getModel();
		int cnt = tableModel.getObjectCount();
		if (cnt > 0 && curState != hasItemState) {
			curState = hasItemState;
			curState.enter();
		} else if (cnt == 0 && curState != emptyState) {
			curState = emptyState;
			curState.enter();
		}
		boolean newValid = curState.isValidModel();
		System.out.println("valid is " + newValid);
		if (newValid != validModel) {
			validModel = newValid;
			target.setValidModel(validModel);
		}
	}
}
