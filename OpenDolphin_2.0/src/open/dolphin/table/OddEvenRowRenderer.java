package open.dolphin.table;

import open.dolphin.client.*;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Kazushi Minagawa.
 */
public final class OddEvenRowRenderer extends JLabel implements TableCellRenderer {
    
    private static final Color DEFAULT_ODD_COLOR = ClientContext.getColor("color.odd");
    private static final Color DEFAULT_EVENN_COLOR = ClientContext.getColor("color.even");
    private Color oddColor;
    private Color evenColor;
    
    public OddEvenRowRenderer() {
        this(DEFAULT_ODD_COLOR,DEFAULT_EVENN_COLOR);
    }
    
    public OddEvenRowRenderer(Color oddColor, Color evenColor) {
        setOpaque(true);
        setOddColor(oddColor);
        setEvenColor(evenColor);
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column ) {
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
            
        } else {
            
            setForeground(table.getForeground());
            
            if ((row & (1)) == 0) {
                setBackground(getEvenColor());
            } else {
                setBackground(getOddColor());
            }
        }
        
        //-------------------------------------------------------
        if (value != null) {
            
            if (value instanceof java.lang.String) {
                this.setText((String) value);
                
            } else {
                this.setText(value.toString());
            }
            
        } else {
            this.setText("");
        }
        //-------------------------------------------------------
        
        return this;
    }
    
    /**
     * @param oddColor The oddColor to set.
     */
    public void setOddColor(Color oddColor) {
        this.oddColor = oddColor;
    }
    
    /**
     * @return Returns the oddColor.
     */
    public Color getOddColor() {
        return oddColor;
    }
    
    /**
     * @param evenColor The evenColor to set.
     */
    public void setEvenColor(Color evenColor) {
        this.evenColor = evenColor;
    }
    
    /**
     * @return Returns the evenColor.
     */
    public Color getEvenColor() {
        return evenColor;
    }
}
