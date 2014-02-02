package flextable;

import java.util.*;
import java.awt.*;
import java.io.Serializable;
import javax.swing.table.*;


/**
 * Table model that supports combining cells and defining
 * different heights for each row.<BR>
 * Courtesy of Nobuo Tamemasa.
 */

public class FlexibleTableModel extends DefaultTableModel
                                      implements Serializable {

    private static final long serialVersionUID = 622436455613930317L;

	/**
     *  Constructs a default DefaultTableModel which is a table of
     *  zero columns and zero rows.
     */
    public FlexibleTableModel() {
        super();
    }

    /**
     *  Constructor for <i>numRows</i> and
     *  <i>numColumns</i> of <b>null</b> object values.
     *
     * @param numRows    The number of rows the table holds
     * @param numColumns The number of columns the table holds
     */
    public FlexibleTableModel( int numRows,
                               int numColumns ) {
        super( numRows, numColumns );
        cellAtt =
            new FlexibleCellAttribute( numRows,numColumns );
    }

    /**
     * Constructs a DefaultTableModel with as many columns as
     * there are elements in <i>columnNames</i> and
     * <i>numRows</i> of <b>null</b> object values.  Each
     * column's name will be taken from the <i>columnNames</i>
     * vector.
     *
     * @param columnNames Vector containing the names of the
     * new columns. If null then the model has no columns.
     * @param numRows The number of rows the table holds
     */
    public FlexibleTableModel( Vector columnNames,
                               int numRows ) {
        super( columnNames, numRows );
        cellAtt =
            new FlexibleCellAttribute(numRows,columnNames.size());
    }

    public FlexibleTableModel( Object[] columnNames,
                               int numRows ) {
        this(convertToVector(columnNames), numRows);
    }

    public FlexibleTableModel( Vector data,
                               Vector columnNames ) {
        super( data, columnNames );
    }

    public FlexibleTableModel( Object[][] data,
                               Object[] columnNames ) {
        super( data, columnNames );
    }

    /**
     *  This replaces the current dataVector instance variable
     * with the new Vector of rows, <i>newData</i>.
     * <i>columnNames</i> are the names of the new columns.
     * The first name in <i>columnNames</i> is mapped to column
     * 0 in <i>newData</i>. Each row in <i>newData</i> is
     * adjusted to match the number of columns in
     * <i>columnNames</i> either by truncating the Vector if it
     * is too long, or adding null values if it is too short.
     *  <p>
     *
     * @param   newData         The new data vector
     * @param   columnNames     The names of the columns
     */
    public void setDataVector( Vector newData,
                               Vector columnNames ) {
        super.setDataVector( newData, columnNames );
        cellAtt =
            new FlexibleCellAttribute(
                dataVector.size(),
                columnIdentifiers.size());
    }

    public void addColumn(Object columnName, Vector columnData) {
        super.addColumn( columnName, columnData );
        cellAtt.addColumn();
    }

    public void addRow(Vector rowData) {
        super.addRow( rowData );
        cellAtt.addRow();
    }

    public void insertRow(int row, Vector rowData) {
        super.insertRow( row, rowData );
        cellAtt.insertRow(row);
    }

    public CellAttribute getCellAttribute() {
        return cellAtt;
    }

    public void setCellAttribute(CellAttribute newCellAtt) {
        int numColumns = getColumnCount();
        int numRows    = getRowCount();
        if ((newCellAtt.getSize().width  != numColumns) ||
            (newCellAtt.getSize().height != numRows)) {
            newCellAtt.setSize(new Dimension(numRows, numColumns));
        }
        cellAtt = newCellAtt;
        fireTableDataChanged();
    }
   
    private CellAttribute cellAtt;
}