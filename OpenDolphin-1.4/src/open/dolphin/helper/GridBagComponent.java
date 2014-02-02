package open.dolphin.helper;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class GridBagComponent {
    
    private Component component;
    
    private int row;
    
    private int col;
    
    private int rowSpan = 1;
    
    private int colSpan = 1;
    
    private int anchor;
    
    private int fill = GridBagConstraints.NONE;
    
    private double rowWeight = 0.0;
    
    private double colWeight = 0.0;
    
    
    /** Creates a new instance of GridBagComponent */
    public GridBagComponent() {
    }
    
    public static void setConstrain(Component comp, int row, int col, int rowSpan, int colSpan, int anchor, int fill, List<GridBagComponent> list) {
        GridBagComponent gbc = new GridBagComponent();
        gbc.setComponent(comp);
        gbc.setRow(row);
        gbc.setCol(col);
        gbc.setRowSpan(rowSpan);
        gbc.setColSpan(colSpan);
        gbc.setAnchor(anchor);
        gbc.setFill(fill);
        list.add(gbc);
    }
    
    public static void setConstrain(Component comp, int row, int col, int rowSpan, int colSpan, int anchor, int fill, double roww, double colw, List<GridBagComponent> list) {
        GridBagComponent gbc = new GridBagComponent();
        gbc.setComponent(comp);
        gbc.setRow(row);
        gbc.setCol(col);
        gbc.setRowSpan(rowSpan);
        gbc.setColSpan(colSpan);
        gbc.setAnchor(anchor);
        gbc.setFill(fill);
        gbc.setRowWeight(roww);
        gbc.setColWeight(colw);
        list.add(gbc);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }
    
    public void setRowCol(int row,int col) {
        setRow(row);
        setCol(col);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getAnchor() {
        return anchor;
    }

    public void setAnchor(int anchor) {
        this.anchor = anchor;
    }

    public int getFill() {
        return fill;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public double getRowWeight() {
        return rowWeight;
    }

    public void setRowWeight(double rowWeight) {
        this.rowWeight = rowWeight;
    }

    public double getColWeight() {
        return colWeight;
    }

    public void setColWeight(double colWeight) {
        this.colWeight = colWeight;
    }
}
