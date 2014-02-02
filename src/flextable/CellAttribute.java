package flextable;


import java.awt.*;

/**
 * Interface for table cell that can have unique font and colors
 */
public interface CellAttribute {

  public void addColumn();

  public void addRow();

  public void insertRow(int row);

  public Dimension getSize();

  public void setSize(Dimension size);

  public Font getFont(int row, int column);

  public void setFont(Font font, int row, int column);

  public void setFont(Font font, int[] rows, int[] columns);

  public int[] getSpan(int row, int column);

  public void setSpan(int[] span, int row, int column);
  
  public boolean isVisible(int row, int column);
  
  public void combine(int[] rows, int[] columns);

  public void split(int row, int column);

  public Color getForeground(int row, int column);

  public void setForeground(Color color, int row, int column);

  public void setForeground(Color color, int[] rows, int[] columns);

  public Color getBackground(int row, int column);

  public void setBackground(Color color, int row, int column);

  public void setBackground(Color color, int[] rows, int[] columns);

  public Font getDefaultFont();

  public void setDefaultFont( Font font );

  public Color getDefaultBackground();

  public void setDefaultBackground( Color color );

  public Color getDefaultForeground();

  public void setDefaultForeground( Color color );

  public final int ROW    = 0;
  public final int COLUMN = 1;
}
