package open.dolphin.order;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import open.dolphin.table.ObjectReflectTableModel;

/**
 * AbsractSetTableState
 * 
 * @author Kazushi Minagawa
 *
 */
public abstract class AbstractSetTableState {

    protected JTable setTable;
    protected JButton deleteBtn;
    protected JButton clearBtn;
    protected JTextField stampNameField;

    public AbstractSetTableState(JTable setTable, JButton deleteBtn, JButton clearBtn, JTextField stampNameField) {
        this.setTable = setTable;
        this.deleteBtn = deleteBtn;
        this.clearBtn = clearBtn;
        this.stampNameField = stampNameField;
    }

    public ObjectReflectTableModel getTableModel() {
        return (ObjectReflectTableModel) setTable.getModel();
    }

    public abstract void enter();

    public abstract boolean isValidModel();
}
