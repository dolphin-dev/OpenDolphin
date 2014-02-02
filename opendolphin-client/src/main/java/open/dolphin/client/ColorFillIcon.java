/*
 * ColorFillIcon.java
 *
 * Created on 2001/12/08, 13:23
 */
package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Core Java Foundation Class by Kim topley.
 */
public class ColorFillIcon implements Icon {

    /** Creates new ColorFillIcon */
    public ColorFillIcon(Color fill, int width, int height, int borderSize) {
        super();
        
        this.fillColor = fill;
        this.width = width;
        this.height = height;
        this.borderSize = borderSize;
        this.shadow = Color.black;
        this.fillWidth = width - 2 * borderSize;
        this.fillHeight = height - 2 * borderSize;
    }
    
    public ColorFillIcon(Color fill, int size) {
        this(fill, size, size, BORDER_SIZE);
    }
    
    public ColorFillIcon(Color fill) {
        this(fill, DEFAULT_SIZE, DEFAULT_SIZE, BORDER_SIZE);
    }
    
    public void setShadow(Color c) {
        shadow = c;
    }
    
    public void setFillColor(Color c) {
        fillColor = c;
    }
    
    @Override
    public int getIconWidth() {
        return width;
    }
    
    @Override
    public int getIconHeight() {
        return height;
    }
    
    @Override
    public void paintIcon(Component comp, Graphics g, int x, int y) {
        Color c = g.getColor();
        
        if(borderSize > 0) {
            g.setColor(shadow);
            for (int i = 0; i < borderSize; i++) {
                g.drawRect(x + i, y + i,
                           width - 2 * i - 1, height - 2 * i -1);
            }
        }
        
        g.setColor(fillColor);
        g.fillRect(x + borderSize, y + borderSize, fillWidth, fillHeight);
        g.setColor(c);
    }
    
    protected int width;
    protected int height;
    protected Color fillColor;
    protected Color shadow;
    protected int borderSize;
    protected int fillHeight;
    protected int fillWidth;
    
    public static final int BORDER_SIZE = 2;
    public static final int DEFAULT_SIZE = 32;

}
