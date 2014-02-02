package flextable;

import java.util.*;
import java.io.Serializable;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;


/**
 * UI for rendering table that may have variable cell sizes.<BR>
 * Courtesy of Nobuo Tamemasa
 */
public class FlexibleTableUI extends BasicTableUI
                             implements Serializable {

	private static final long serialVersionUID = -3785867489547375693L;

	/**
	 * Calculate size of table based on a given width
	 *
	 * @param width Width of table (without margins)
	 * @return The total size of the table
	 */
	private Dimension createTableSize(long width) {
		int height = table.getCellRect(table.getRowCount(),
									   0, false).y;
		int totalMarginWidth =
			table.getColumnModel().getColumnMargin() * 
			table.getColumnCount(); 
		long widthWithMargin = Math.abs(width) + totalMarginWidth;
		if (widthWithMargin > Integer.MAX_VALUE) {
			widthWithMargin = Integer.MAX_VALUE;
		}
		return new Dimension((int)widthWithMargin, height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = table.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn)enumeration.nextElement();
			width = width + aColumn.getMinWidth();
		}
		return createTableSize(width);
	}

	/**
     * Return the preferred size of the table. The preferred height is the 
     * row height (plus inter-cell spacing) times the number of rows. 
     * The preferred width is the sum of the preferred widths of each column 
     * (plus inter-cell spacing).
     */
	public Dimension getPreferredSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = table.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn)enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createTableSize(width);
	}

	/**
     * Return the maximum size of the table. The maximum height is the 
     * row height (plus inter-cell spacing) times the number of rows. 
     * The maximum width is the sum of the maximum widths of each column 
     * (plus inter-cell spacing).
     */
	public Dimension getMaximumSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = table.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn)enumeration.nextElement();
			width = width + aColumn.getMaxWidth();
		}
		return createTableSize(width);
	}


	public void paint(Graphics g, JComponent c) {
		Rectangle oldClipBounds = g.getClipBounds();
		Rectangle clipBounds = new Rectangle(oldClipBounds);
		int tableWidth =
			table.getColumnModel().getTotalColumnWidth();
		clipBounds.width = Math.min(clipBounds.width, tableWidth);
		g.setClip(clipBounds);

		int firstIndex =
			table.rowAtPoint(new Point(0, clipBounds.y));
//		int lastIndex = lastVisibleRow(clipBounds);
		int lastIndex = table.getRowCount() - 1;

		int rowMargin = table.getRowMargin();
		FlexibleTable mtable = (FlexibleTable)table;
		int rowStart =
			table.getCellRect(firstIndex, 0, false).y;
		int rowHeight =
			mtable.getRowHeight(firstIndex) + rowMargin;
		Rectangle rowRect =
			new Rectangle( 0, rowStart,
						   tableWidth, rowHeight );

		for (int index = firstIndex; index <= lastIndex; index++) {
			if ( rowRect.intersects(clipBounds) ) {
				paintRow(g, index);
			}
			rowRect.y += mtable.getRowHeight(index) + rowMargin;
		}
		g.setClip(oldClipBounds);
	}

	private void paintRow(Graphics g, int row) {
		Rectangle rect = g.getClipBounds();
		boolean drawn  = false;
    
		FlexibleTableModel tableModel =
			(FlexibleTableModel)table.getModel();
		CellAttribute cellAtt =
			(CellAttribute)tableModel.getCellAttribute();
		//int numColumns = table.getColumnCount();

		int column = 0;
		Enumeration enumeration =
			table.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			//TableColumn aColumn =
				//(TableColumn)enumeration.nextElement();
			Rectangle cellRect =
				table.getCellRect(row,column,true);
			if ( cellRect.intersects(rect) ) {
				int cellRow = row;
				int cellColumn = column;
				int rSpan =
					cellAtt.getSpan(row,column)
					[CellAttribute.ROW];
				if ( rSpan < 0 ) {
					// This row is part of a previous one
					cellRow += rSpan;
				}
				int cSpan =
					cellAtt.getSpan(row,column)
					[CellAttribute.COLUMN];
				if ( cSpan < 0 ) {
					// This column is part of a previous one
					cellColumn += cSpan;
				}
				drawn = true;
				paintCell(g, cellRect, cellRow, cellColumn);
			} else {
				if ( drawn ) {
					break;
				}
			}
			column++;
		}
	}

	private void paintCell( Graphics g, Rectangle cellRect,
							int row, int column ) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth  =
			table.getColumnModel().getColumnMargin();

		Color c = g.getColor();
		g.setColor(table.getGridColor());
		g.drawRect(cellRect.x,cellRect.y,cellRect.width-1,cellRect.height-1);
		g.setColor(c);

		cellRect.setBounds( cellRect.x + spacingWidth/2,
							cellRect.y + spacingHeight/2,
							cellRect.width - spacingWidth,
							cellRect.height - spacingHeight);

		if ( table.isEditing() &&
			 (table.getEditingRow()==row) &&
			 (table.getEditingColumn()==column) ) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			TableCellRenderer renderer =
				table.getCellRenderer(row, column);
			Component component =
				table.prepareRenderer(renderer, row, column);

			if ( component.getParent() == null ) {
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table,
										cellRect.x, cellRect.y,
										cellRect.width,
										cellRect.height, true);
		}
		// Have to restore the cellRect back to it's orginal size
		cellRect.setBounds(cellRect.x - spacingWidth/2,
						   cellRect.y - spacingHeight/2,
						   cellRect.width + spacingWidth,
						   cellRect.height + spacingHeight);
	}

	/*private int lastVisibleRow(Rectangle clip) {
		int lastIndex =
			table.rowAtPoint(
				new Point(0, clip.y + clip.height - 1));
		// If the table does not have enough rows to fill the
		// view we'll get -1.
		// Replace this with the index of the last row.
		if (lastIndex == -1) {
			lastIndex = table.getRowCount() - 1;
		} else {
			FlexibleTableModel m =
				(FlexibleTableModel)table.getModel();
			CellAttribute cellAtt =
				(CellAttribute)m.getCellAttribute();
			lastIndex +=
				cellAtt.getSpan(lastIndex,0)[CellAttribute.ROW];
			if (lastIndex > table.getRowCount()) {
				lastIndex = table.getRowCount() - 1;
			}
		}
		return lastIndex;
	}*/
}
