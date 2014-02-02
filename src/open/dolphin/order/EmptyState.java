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
public class EmptyState extends AbstractSetTableState {

    public EmptyState(JTable setTable, JButton deleteBtn, JButton clearBtn, JTextField stampNameField) {
        super(setTable, deleteBtn, clearBtn, stampNameField);
    }

    public void enter() {
        deleteBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        stampNameField.setText(ItemTablePanel.DEFAULT_STAMP_NAME);
    }

    public boolean isValidModel() {
        return false;
    }
}
