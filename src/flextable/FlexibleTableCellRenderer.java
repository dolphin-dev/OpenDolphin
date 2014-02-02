package flextable;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/*
 * TableCellRenderer implementation.<BR>
 * Gets font and colors from the CellAttribute of the
 * TableModel.<BR>
 * Courtesy of Nobuo Tamemasa.
 */
public class
FlexibleTableCellRenderer extends DefaultTableCellRenderer
                          implements TableCellRenderer {
    private static final long serialVersionUID = -3338014683012268156L;

	/**
     * Default constructor*/
    public FlexibleTableCellRenderer() {
		super();
    }

    public Component
        getTableCellRendererComponent( JTable table,
                                       Object value,
                                       boolean isSelected,
                                       boolean hasFocus,
                                       int row,
                                       int column ) {
        Color foreground = null;
        Color background = null;
        Font font = null;
        TableModel model = table.getModel();

        if ( model instanceof FlexibleTableModel ) {
            FlexibleTableModel m =
                (FlexibleTableModel)model;
            CellAttribute c =
                (CellAttribute)m.getCellAttribute();
            foreground = c.getForeground( row,column );
            background = c.getBackground( row,column );
            font = c.getFont( row,column );
// 			setForeground( foreground );
// 			setBackground( background );
// 			setFont( font );
        }

// 		return super.getTableCellRendererComponent(
// 			table, value, isSelected, hasFocus, row, column );

        if ( isSelected ) {
            if ( foreground == null ) {
                foreground = table.getSelectionForeground();
            }
            background = table.getSelectionBackground();
        } else {
            if ( foreground == null ) {
                foreground = table.getForeground();
            }
            if ( background == null ) {
                background = table.getBackground();
            }
        }
        setForeground( foreground );
        setBackground( background );
        setFont((font != null) ? font : table.getFont());
    
        if ( hasFocus ) {
            setBorder( UIManager.getBorder(
                "Table.focusCellHighlightBorder") );
            if ( table.isCellEditable(row, column) ) {
                setForeground((foreground != null) ? foreground
                              : UIManager.getColor(
                                  "Table.focusCellForeground") );
                setBackground( UIManager.getColor(
                    "Table.focusCellBackground") );
            }
        } else {
            setBorder( noFocusBorder );
        }
        setValue(value);        
        return this;

    }
}
