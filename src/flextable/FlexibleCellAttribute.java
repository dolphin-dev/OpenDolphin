package flextable;

import java.awt.*;
import java.io.Serializable;

/** 
 * Table cell descriptor model to control its span (how many
 * physical rows and columns it includes), colors, and font.
 * Original code courtesy of Nobuo Tamemasa
 */
public class FlexibleCellAttribute 
             implements CellAttribute,
                        Serializable {

    private static final long serialVersionUID = -5516690872270766205L;
	// The rowSize and columnSize values must be kept in
    // synch with the Table data; that is managed by the Table
    // model.
    protected int rowSize;
    protected int columnSize;
    protected int[][][] span;
    protected Color[][] foreground;
    protected Color[][] background;
    protected Font[][]  font;
    protected Color defaultBackground = null;
    protected Color defaultForeground = null;
    protected Font defaultFont = null;
  
    public FlexibleCellAttribute() {
        this(1,1);
    }
  
    public FlexibleCellAttribute(int numRows, int numColumns) {
        setSize(new Dimension(numColumns, numRows));
    }

    protected void initValue() {
        for( int i = 0; i < span.length; i++) {
            for( int j = 0; j < span[i].length; j++ ) {
                span[i][j][CellAttribute.COLUMN] = 1;
                span[i][j][CellAttribute.ROW] = 1;
                font[i][j] = defaultFont;
                foreground[i][j] = defaultForeground;
                background[i][j] = defaultBackground;
            }
        }
    }

    public int[] getSpan(int row, int column) {
        if (isOutOfBounds(row, column)) {
            int[] ret_code = {1,1};
            return ret_code;
        }
        return span[row][column];
    }

    public void setSpan(int[] span, int row, int column) {
        if (isOutOfBounds(row, column)) return;
        this.span[row][column] = span;
    }
      
    public boolean isVisible(int row, int column) {
        if ( isOutOfBounds(row, column) ) {
            return false;
        }
        if ( (span[row][column][CellAttribute.COLUMN] < 1) ||
             (span[row][column][CellAttribute.ROW]    < 1) ) {
            return false;
        }
        return true;
    }

    public void combine(int[] rows, int[] columns) {
        if ( isOutOfBounds(rows, columns) ) {
            return;
        }
        int rowSpan = rows.length;
        int columnSpan = columns.length;
        int startRow = rows[0];
        int startColumn = columns[0];
        for ( int i=0; i<rowSpan; i++ ) {
            for ( int j=0; j<columnSpan; j++ ) {
                int col = span[startRow +i][startColumn +j]
                    [CellAttribute.COLUMN];
                int row = span[startRow +i][startColumn +j]
                    [CellAttribute.ROW];
                if ( (col != 1) || (row != 1) ) {
                    //System.out.println("can't combine");
                    return ;
                }
            }
        }
        for ( int i=0,ii=0; i<rowSpan; i++,ii-- ) {
            for ( int j=0,jj=0; j<columnSpan; j++,jj-- ) {
                int row = startRow + i;
                int col = startColumn + j;
                span[row][col][CellAttribute.COLUMN] =
                    (columnSpan > 1) ? jj : 1;
                span[row][col][CellAttribute.ROW] = ii;
                //System.out.println("r " +ii +"  c " +jj);
            }
        }
        span[startRow][startColumn][CellAttribute.COLUMN] =
            columnSpan;
        span[startRow][startColumn][CellAttribute.ROW] =
            rowSpan;
    }

    public void split( int row, int column ) {
        if ( isOutOfBounds(row, column) ) {
            return;
        }
        int columnSpan =
            span[row][column][CellAttribute.COLUMN];
        int rowSpan =
            span[row][column][CellAttribute.ROW];
        for ( int i=0; i<rowSpan; i++) {
            for (int j=0; j<columnSpan; j++) {
                span[row +i][column +j][CellAttribute.COLUMN] =
                    1;
                span[row +i][column +j][CellAttribute.ROW] =
                    1;
            }
        }
    }

    public Color getForeground( int row, int column ) {
        return (isOutOfBounds(row, column)) ? null :
            foreground[row][column];
    }

    public void setForeground( Color color, int row,
                               int column ) {
        if ( !isOutOfBounds(row, column) ) {
            foreground[row][column] = color;
        }
    }

    public void setForeground( Color color, int[] rows,
                               int[] columns ) {
        if ( !isOutOfBounds(rows, columns) ) {
            setValues(foreground, color, rows, columns);
        }
    }

    public Color getBackground( int row, int column ) {
        return (isOutOfBounds(row, column)) ? null :
            background[row][column];
    }

    public void setBackground( Color color, int row,
                               int column ) {
        if ( !isOutOfBounds(row, column) ) {
            background[row][column] = color;
        }
    }

    public void setBackground( Color color, int[] rows,
                               int[] columns ) {
        if ( !isOutOfBounds(rows, columns) ) {
            setValues(background, color, rows, columns);
        }
    }

    public Font getFont( int row, int column ) {
        return (isOutOfBounds(row, column)) ? null :
            font[row][column];
    }

    public void setFont( Font font, int row, int column ) {
        if ( !isOutOfBounds(row, column) ) {
            this.font[row][column] = font;
        }
    }

    public void setFont( Font font, int[] rows,
                         int[] columns ) {
        if ( !isOutOfBounds(rows, columns) ) {
            setValues(this.font, font, rows, columns);
        }
    }

    public void addColumn() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows][numColumns + 1][2];
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                span[i][j][CellAttribute.COLUMN] =
                    oldSpan[i][j][CellAttribute.COLUMN];
                span[i][j][CellAttribute.ROW] =
                    oldSpan[i][j][CellAttribute.ROW];
            }
        }
        for (int i=0;i<numRows;i++) {
            span[i][numColumns][CellAttribute.COLUMN] = 1;
            span[i][numColumns][CellAttribute.ROW]    = 1;
        }

        Font[][] oldFont = font;
        font = new Font[numRows][numColumns + 1];
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                font[i][j] = oldFont[i][j];
            }
        }
        for (int i=0;i<numRows;i++) {
            font[i][numColumns] = defaultFont;
        }

        Color[][] oldColor = background;
        background = new Color[numRows][numColumns + 1];
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                background[i][j] = oldColor[i][j];
            }
        }
        for (int i=0;i<numRows;i++) {
            background[i][numColumns] = defaultBackground;
        }

        oldColor = foreground;
        foreground = new Color[numRows][numColumns + 1];
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numColumns;j++) {
                foreground[i][j] = oldColor[i][j];
            }
        }
        for (int i=0;i<numRows;i++) {
            foreground[i][numColumns] = defaultForeground;
        }
    }

    public void addRow() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = (numRows > 0) ? oldSpan[0].length :
            columnSize;
        span = new int[numRows + 1][numColumns][2];
        System.arraycopy(oldSpan,0,span,0,numRows);
        for (int i=0;i<numColumns;i++) {
            span[numRows][i][CellAttribute.COLUMN] = 1;
            span[numRows][i][CellAttribute.ROW]    = 1;
        }

        Font[][] oldFont = font;
        font = new Font[numRows + 1][numColumns];
        System.arraycopy(oldFont,0,font,0,numRows);
        for (int i=0;i<numColumns;i++) {
            font[numRows][i] = defaultFont;
        }

        Color[][] oldColor = background;
        background = new Color[numRows + 1][numColumns];
        System.arraycopy(oldColor,0,background,0,numRows);
        for (int i=0;i<numColumns;i++) {
            background[numRows][i] = defaultBackground;
        }

        oldColor = foreground;
        foreground = new Color[numRows + 1][numColumns];
        System.arraycopy(oldColor,0,foreground,0,numRows);
        for (int i=0;i<numColumns;i++) {
            foreground[numRows][i] = defaultForeground;
        }

        rowSize = numRows + 1;
    }

    public void insertRow( int row ) {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows + 1][numColumns][2];
        if (0 < row) {
            System.arraycopy(oldSpan,0,span,0,row);
        }
        System.arraycopy(oldSpan,0,span,row + 1,
                         numRows - row);
        for (int i=0;i<numColumns;i++) {
            span[row][i][CellAttribute.COLUMN] = 1;
            span[row][i][CellAttribute.ROW]    = 1;
        }

        Font[][] oldFont = font;
        font = new Font[numRows + 1][numColumns];
        if (0 < row) {
            System.arraycopy(oldFont,0,font,0,row);
        }
        System.arraycopy(oldFont,0,font,row + 1,
                         numRows - row);
        for (int i=0;i<numColumns;i++) {
            font[numRows][i] = defaultFont;
        }

        Color[][] oldColor = background;
        background = new Color[numRows + 1][numColumns];
        if (0 < row) {
            System.arraycopy(oldColor,0,background,0,row);
        }
        System.arraycopy(oldColor,0,background,row + 1,
                         numRows - row);
        for (int i=0;i<numColumns;i++) {
            background[numRows][i] = defaultBackground;
        }

        oldColor = foreground;
        foreground = new Color[numRows + 1][numColumns];
        if (0 < row) {
            System.arraycopy(oldColor,0,foreground,0,row);
        }
        System.arraycopy(oldColor,0,foreground,row + 1,
                         numRows - row);
        for (int i=0;i<numColumns;i++) {
            foreground[numRows][i] = defaultForeground;
        }
    }

    public Dimension getSize() {
        return new Dimension(rowSize, columnSize);
    }

    public void setSize( Dimension size ) {
        columnSize = size.width;
        rowSize = size.height;
        span = new int[rowSize][columnSize][2]; // COLUMN,ROW
        foreground = new Color[rowSize][columnSize];
        background = new Color[rowSize][columnSize];
        font = new Font[rowSize][columnSize];
        initValue();
    }

    protected boolean isOutOfBounds( int row, int column ) {
        return ( (row < 0) ||
                 (rowSize <= row) ||
                 (column < 0) ||
                 (columnSize <= column) );
    }

    protected boolean isOutOfBounds( int[] rows,
                                     int[] columns ) {
        for ( int i=0; i<rows.length; i++ ) {
            if ( (rows[i] < 0) ||
                 (rowSize <= rows[i]) ) {
                return true;
            }
        }
        for ( int i=0; i<columns.length; i++ ) {
            if ( (columns[i] < 0) ||
                 (columnSize <= columns[i]) ) {
                return true;
            }
        }
        return false;
    }

    protected void setValues(Object[][] target, Object value,
                             int[] rows, int[] columns) {
        for ( int i=0; i<rows.length; i++) {
            int row = rows[i];
            for ( int j=0; j<columns.length; j++ ) {
                int column = columns[j];
                target[row][column] = value;
            }
        }
    }

    /**
     * Report the default font for cells
     *
     * @return The default font for cells
     */
    public Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * Set the default font for cells
     *
     * @param font The default font for cells
     */
    public void setDefaultFont( Font font ) {
        defaultFont = font;
        for (int i=0;i<this.font.length;i++) {
            for (int j=0;j<this.font[0].length;j++) {
				if ( this.font[i][j] == null ) {
					this.font[i][j] = defaultFont;
				}
            }
        }
    }

    /**
     * Report the default background for cells
     *
     * @return The default background color for cells
     */
    public Color getDefaultBackground() {
        return defaultBackground;
    }

    /**
     * Set the default background for cells
     *
     * @param color The default background color for cells
     */
    public void setDefaultBackground( Color color ) {
        defaultBackground = color;
        for (int i=0;i<background.length;i++) {
            for (int j=0;j<background[0].length;j++) {
				if ( background[i][j] == null ) {
					background[i][j] = defaultBackground;
				}
            }
        }
    }

    /**
     * Report the default foreground for cells
     *
     * @return The default foreground color for cells
     */
    public Color getDefaultForeground() {
        return defaultForeground;
    }

    /**
     * Set the default foreground for cells
     *
     * @param color The default foreground color for cells
     */
    public void setDefaultForeground( Color color ) {
        defaultForeground = color;
        for (int i=0;i<foreground.length;i++) {
            for (int j=0;j<foreground[0].length;j++) {
				if ( foreground[i][j] == null ) {
					foreground[i][j] = defaultForeground;
				}
            }
        }
    }
}
