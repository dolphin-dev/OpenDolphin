package open.dolphin.order;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * EmptyState
 * 
 * @author Minagawa,Kazushi
 *
 */
public class MedEmptyState extends AbstractMedTableState {
	
	
	public MedEmptyState(JTable setTable, JButton deleteBtn, JButton clearBtn, JTextField stampNameField, JTextField adminField) {
		super(setTable, deleteBtn, clearBtn, stampNameField, adminField);
	}
	
	public void enter() {
		deleteBtn.setEnabled(false);
		clearBtn.setEnabled(false);
		stampNameField.setText(MedicineTablePanel.DEFAULT_STAMP_NAME);
	}
	
	public boolean isValidModel() {
		return false;
	}
}