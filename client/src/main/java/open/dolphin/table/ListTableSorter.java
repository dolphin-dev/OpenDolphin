
package open.dolphin.table;

/**
 * ListTableSorter
 *
 * @author masuda, Masuda Naika
 */
public class ListTableSorter extends TableSorter {

    public ListTableSorter(ListTableModel tableModel) {
        super(tableModel);
    }

    // 対応するListTableModelを返す
    public Object getObject(int row) {

        if (row >= 0 && row < getTableModel().getRowCount()) {
            return ((ListTableModel) getTableModel()).getObject(modelIndex(row));
        }
        return null;

    }
}
