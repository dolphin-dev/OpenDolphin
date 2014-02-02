package flextable;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;


/**
 * Table that allows combining cells, assigning colors
 * and fonts to individual cells, and assigning unique heights
 * to each row.<BR>
 * Courtesy of Nobuo Tamemasa.
 */

public class FlexibleTable extends JTable {

    private static final long serialVersionUID = 4917194282653669152L;

	/**
     * Constructor taking a table model as parameter
     *
     * @parameter model A table model
     */
    public FlexibleTable( TableModel model ) {
        super(model);
        setUI(new FlexibleTableUI());
        setDefaultRenderer(
            new Object().getClass(),
            new FlexibleTableCellRenderer() );
        getTableHeader().setReorderingAllowed(false);
        setCellSelectionEnabled(true);
        setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        // Make the cell attribute
        // model use the defaults of the table
        FlexibleTableModel m = (FlexibleTableModel)model;
        CellAttribute cellAtt =
            (CellAttribute)m.getCellAttribute();
        cellAtt.setDefaultFont( getFont() );
        cellAtt.setDefaultBackground( getBackground() );
        cellAtt.setDefaultForeground( getForeground() );
    }
    
    /**
     * Get the size of a cell, taking into account variable
     * height
     *
     * @param row A row in or at a logical row
     * @param column A column in or at a logical column
     * @param includeSpacing <CODE>true</CODE> if margins
     * are to be included in the calculated size
     * @return The size of the table area containing the
     * specified row and column
     */
    protected Rectangle getVariableCellRect(
                                 int row,
                                 int column,
                                 boolean includeSpacing) {
        int index = 0;
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration en = getColumnModel().getColumns();
        TableColumn aColumn;

        Rectangle cellFrame = new Rectangle();
        cellFrame.height = getRowHeight(row) + rowMargin;
        int rowSpacing = getIntercellSpacing().height;
        int y = 0;
        for( int i = 0; i < row; i++ ) {
            y += getRowHeight(i) + rowSpacing;
        }
        cellFrame.y = y;

        while (en.hasMoreElements()) {
            aColumn = (TableColumn)en.nextElement();
            cellFrame.width =
                aColumn.getWidth() + columnMargin;

            if (index == column)
                break;

            cellFrame.x += cellFrame.width;
            index++;
        }

        if (!includeSpacing) {
            Dimension spacing = getIntercellSpacing();
            // This is not the same as grow(), it rounds
            // differently.
            cellFrame.setBounds(
                cellFrame.x + spacing.width/2,
                cellFrame.y +     spacing.height/2,
                cellFrame.width -  spacing.width,
                cellFrame.height - spacing.height);
        }
        return cellFrame;
    }

    public Rectangle getCellRect( int row, int column,
                                  boolean includeSpacing ) {
        Rectangle sRect =
            getVariableCellRect(row,column,includeSpacing);
//      System.out.println( "Rect for (" + row + "," +
//                          column +
//                          ", " + includeSpacing + ") = " +
//                          sRect );
        if ( (row <0) || (column<0) ||
             (getRowCount() <= row) ||
             (getColumnCount() <= column)) {
            return sRect;
        }
        FlexibleTableModel m = (FlexibleTableModel)getModel();
        CellAttribute cellAtt =
            (CellAttribute)m.getCellAttribute();
        int[] corner = { row, column };
        if ( !cellAtt.isVisible(row,column) ) {
            // Get the top left of the aggregated cell
            adjustRowColumn( corner, cellAtt, false );
            row = corner[0];
            column = corner[1];
        }
        int[] n = cellAtt.getSpan(row,column);

        int index = 0;
        int columnMargin = getColumnModel().getColumnMargin();
        Rectangle cellFrame = new Rectangle();
        cellFrame.y = 0;
        for( int i = 0; i < row; i++ ) {
            cellFrame.y += getRowHeight(i);
        }
        cellFrame.height = 0;
        for( int i = 0; i < n[CellAttribute.ROW]; i++ ) {
            cellFrame.height += getRowHeight(row+i);
        }
    
        Enumeration en = getColumnModel().getColumns();
        while (en.hasMoreElements()) {
            TableColumn aColumn =
                (TableColumn)en.nextElement();
            cellFrame.width = aColumn.getWidth() +
                columnMargin;
            if ( index == column ) {
                break;
            }
            cellFrame.x += cellFrame.width;
            index++;
        }
        for ( int i=0; i< n[CellAttribute.COLUMN]-1; i++ ) {
            TableColumn aColumn =
                (TableColumn)en.nextElement();
            cellFrame.width += aColumn.getWidth() +
                columnMargin;
        }

        if ( !includeSpacing ) {
            Dimension spacing = getIntercellSpacing();
            cellFrame.setBounds(
                cellFrame.x +      spacing.width/2,
                cellFrame.y +      spacing.height/2,
                cellFrame.width -  spacing.width,
                cellFrame.height - spacing.height);
        }
        return cellFrame;
    }

    /**
     * Make sure the corner pair really points to the
     * top row and left column of aggregate cells
     *
     * @param corner Array of row and column values (one each)
     * @param cellAtt Cell descriptor
     * @param doBoth <CODE>true</CODE> if both values are to be
     * adjusted even if only of them is not at the corner
     * position
     */
    private void adjustRowColumn( int[] corner,
                                  CellAttribute cellAtt,
                                  boolean doBoth) {
        int r = CellAttribute.ROW;
        int c = CellAttribute.COLUMN;
        int row = corner[r];
        int column = corner[c];
        if ( doBoth || cellAtt.getSpan(row,column)[c] < 1 ) {
            corner[c] += cellAtt.getSpan(row,column)[c];
        }
        if ( doBoth || cellAtt.getSpan(row,column)[r] < 1 ) {
            corner[r] += cellAtt.getSpan(row,column)[r];
        }
    }

    /**
     * Get the row and column at a particular point
     *
     * @param point X andy Y coordinates to check
     * @return Array of a row and a column value
     */
    private int[] rowColumnAtPoint(Point point) {
        int[] retValue = {-1,-1};
        int y = point.y;
        if( y < 0 ) {
            return retValue;
        }

        //int rowSpacing = getIntercellSpacing().height;
        int rowCount = getRowCount();
        int rowHeight = 0;
        int row = -1;
        for( int i = 0; i < rowCount; i++ ) {
            rowHeight += getRowHeight(i)/* + rowSpacing*/;
            if( y < rowHeight ) {
                row = i;
                break;
            }
        }
        if ( (row <0) ||
             (getRowCount() <= row) ) {
            return retValue;
        }
        int column =
            getColumnModel().getColumnIndexAtX(point.x);

        FlexibleTableModel m = (FlexibleTableModel)getModel();
        CellAttribute cellAtt =
            (CellAttribute)m.getCellAttribute();

        retValue[CellAttribute.COLUMN] = column;
        retValue[CellAttribute.ROW   ] = row;

        if ( !cellAtt.isVisible(row,column) ) {
            adjustRowColumn( retValue, cellAtt, false );
        }

//      System.out.println(
//          "Row/column for (" + point.x +
//          "," + point.y +
//          ") = " + retValue[CellAttribute.ROW] +
//          "," +
//          retValue[CellAttribute.COLUMN] );

        return retValue;
    }

    public int rowAtPoint(Point point) {
        return rowColumnAtPoint(point)[CellAttribute.ROW];
    }

    public int columnAtPoint(Point point) {
        return rowColumnAtPoint(point)[CellAttribute.COLUMN];
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        repaint();
    }

    /**
     * Must override a number of JTable methods to make sure
     * the right area is marked as needing repaint
     */
    public void valueChanged(ListSelectionEvent e) {
		if ( rowHeights == null ) {
			return;
		}
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        // Selection cleared.
        if (firstIndex == -1 && lastIndex == -1) {
            repaint();
        }
        Rectangle dirtyRegion =
            getCellRect(firstIndex, 0, false);
        int numCoumns = getColumnCount();
        int index = firstIndex;
        for (int i=0;i<numCoumns;i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        index = lastIndex;
        for (int i=0;i<numCoumns;i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        repaint(dirtyRegion);
    }

    public void tableChanged(TableModelEvent e) {
        if (e == null ||
            e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            // The whole thing changed
            clearSelection();

            if (getAutoCreateColumnsFromModel())
                createDefaultColumnsFromModel();

            resizeAndRepaint();
            if (tableHeader != null) {
                tableHeader.resizeAndRepaint();
            }
            return;
        }

        if (e.getType() == TableModelEvent.INSERT) {
            tableRowsInserted(e);
            return;
        }

        if (e.getType() == TableModelEvent.DELETE) {
            tableRowsDeleted(e);
            return;
        }

        int modelColumn = e.getColumn();
        int start = e.getFirstRow();
        int end = e.getLastRow();

        if (start == TableModelEvent.HEADER_ROW) {
            start = 0;
            end = Integer.MAX_VALUE;
        }

        Rectangle dirtyRegion;
        if (modelColumn == TableModelEvent.ALL_COLUMNS) {
            // 1 or more rows changed
            int w = getColumnModel().getTotalColumnWidth();
            dirtyRegion =
                new Rectangle(0, getCellRect(start,0,false).y,
                              w, 0);
        } else {
            // A cell or column of cells has changed.
            // Unlike the rest of the methods in the JTable,
            // the TableModelEvent uses the co-ordinate system
            // of the model instead of the view.
            // This is the only place in the JTable where this
            // "reverse mapping" is used.
            int column = convertColumnIndexToView(modelColumn);
            dirtyRegion = getCellRect(start, column, false);
        }

        // Now adjust the height of the dirty region according
        // to the value of "end". Check for Integer.MAX_VALUE
        // as this will cause an overflow.
        if (end != Integer.MAX_VALUE) {
            dirtyRegion.height = getCellRect(end+1,0,false).y -
                dirtyRegion.y;
            repaint(dirtyRegion.x, dirtyRegion.y,
                    dirtyRegion.width, dirtyRegion.height);
        } else {
            // In fact, if the end is Integer.MAX_VALUE we need
            // to revalidate anyway because the scrollbar may
            // need repainting.
            resizeAndRepaint();
        }
    }

    @SuppressWarnings("unchecked")
	private void tableRowsInserted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0)
            start = 0;

        // Move down row height info - for rows below the first
        // inserted row
        int rowCount = getRowCount();
        int rowsInserted = end - start + 1;
        for( int r = start; r < rowCount; r++ ) {
            Integer height =
                (Integer)rowHeights.get( new Integer(r) );
            if( height == null ) {
                continue;
            }
            rowHeights.put( new Integer( r+rowsInserted ),
                            height  );
        }
                             
        // 1 or more rows added, so we have to repaint from
        // the first new row to the end of the table. 
        // Everything shifts down.
        int w = getColumnModel().getTotalColumnWidth();
        Rectangle drawRect =
            new Rectangle(0, getCellRect(start,0,false).y,
                          w, 0);
        drawRect.height =
            getCellRect(rowCount,0,false).y - drawRect.y;

        // Adjust the selection to account for the new rows
        if (selectionModel != null) {
            if (end < 0)
                end = getRowCount()-1;
            int length = end - start + 1;

            selectionModel.insertIndexInterval(start, length,
                                               true);
        }
        revalidate();
        // PENDING(philip) Find a way to stop revalidate
        // calling repaint
        // repaint(drawRect);
    }

    /*
     * Invoked when rows have been removed from the table.
     *
     * @param e the TableModelEvent encapsulating the deletion
     */
    @SuppressWarnings("unchecked")
	private void tableRowsDeleted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0)
            start = 0;

        int deletedCount = end - start + 1;
        int previousRowCount = getRowCount() + deletedCount;
        
        // Remove any height information for deleted rows
        for( int i = start; i <= end; i++ ) {
            resetRowHeight(i);
        }
        // Move up row height info - for rows below the last
        // deleted row
        for( int r = end + 1; r < previousRowCount; r++ ) {
            Integer height =
                (Integer)rowHeights.get( new Integer(r) );
            if( height == null ) continue;
            rowHeights.put( new Integer( r-deletedCount ),
                            height  );
        }
                             
        // 1 or more rows added, so we have to repaint from the
        // first new row to the end of the table. Everything
        // shifts up.
        int w = getColumnModel().getTotalColumnWidth();
        Rectangle drawRect =
            new Rectangle(0, getCellRect(start,0,false).y,
                          w, 0);
        drawRect.height =
            getCellRect(previousRowCount,0,false).y -
            drawRect.y;

        // Adjust the selection to account for the new rows
        if (selectionModel != null) {
            if (end < 0)
                end = getRowCount()-1;
            selectionModel.removeIndexInterval(start, end);
        }
        revalidate();
        // PENDING(philip) Find a way to stop revalidate
        // calling repaint
        // repaint(drawRect);
    }

    public int getRowHeight( int row ) {
        Object o = rowHeights.get( new Integer(row) );
        return (o == null) ? getRowHeight() :
            ((Integer)o).intValue();
    }
             
    @SuppressWarnings("unchecked")
	public void setRowHeight( int row, int height ) {
        rowHeights.put( new Integer( row ),
                        new Integer( height ) );
        revalidate();
    }
             
    public void resetRowHeight( int row ) {
        rowHeights.remove( new Integer( row ) );
        revalidate();
    }
             
    public void resetRowHeight() {
        rowHeights.clear();
        revalidate();
    }

   /* private void dumpCellSpans() {
        FlexibleTableModel m = (FlexibleTableModel)getModel();
        CellAttribute cellAtt =
            (CellAttribute)m.getCellAttribute();
        System.out.println( "Cell spans" );
        for( int i = 0; i < getRowCount(); i++ ) {
            for( int j = 0; j < getColumnCount(); j++ ) {
                System.out.print(
                "\t\t" +
                cellAtt.getSpan(i,j)[CellAttribute.ROW] +
                "," + cellAtt.getSpan(i,j)[CellAttribute.COLUMN] );
            }
            System.out.print( '\n' );
        }
    }*/

    // Table of individual row heights
    private Hashtable rowHeights = new Hashtable();
}

