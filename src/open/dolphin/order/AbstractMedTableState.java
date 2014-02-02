package open.dolphin.order;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import open.dolphin.table.ObjectReflectTableModel;

/**
 * AbsractSetTableState
 * 
 * @author Kazushi Minagawa
 *
 */
public abstract class AbstractMedTableState {

    protected JTable medTable;
    protected JButton deleteBtn;
    protected JButton clearBtn;
    protected JTextField stampNameField;
    protected JLabel stateLabel;

    public AbstractMedTableState(JTable medTable, JButton deleteBtn, JButton clearBtn, 
            JTextField stampNameField, JLabel stateLabel) {
        this.medTable = medTable;
        this.deleteBtn = deleteBtn;
        this.clearBtn = clearBtn;
        this.stampNameField = stampNameField;
        this.stateLabel = stateLabel;
    }

    public ObjectReflectTableModel getTableModel() {
        return (ObjectReflectTableModel) medTable.getModel();
    }

    public abstract void enter();

    public abstract boolean isValidModel();
}
