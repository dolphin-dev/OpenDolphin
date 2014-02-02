package open.dolphin.order;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * EmptyState
 * 
 * @author Minagawa,Kazushi
 *
 */
public class MedEmptyState extends AbstractMedTableState {

    public MedEmptyState(JTable setTable, JButton deleteBtn, JButton clearBtn, 
            JTextField stampNameField, JLabel stateLabel) {
        super(setTable, deleteBtn, clearBtn, stampNameField, stateLabel);
    }

    public void enter() {
        deleteBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        stampNameField.setText(MedicineTablePanel.DEFAULT_STAMP_NAME);
        stateLabel.setText("ˆã–ò•i‚ğ“ü—Í‚µ‚Ä‚­‚¾‚³‚¢B");
    }

    public boolean isValidModel() {
        return false;
    }
}
