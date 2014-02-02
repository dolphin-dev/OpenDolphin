package open.dolphin.impl.schema;

import java.awt.*;
import javax.swing.JComponent;

/**
 *
 * @author Kazushi Minagawa
 */
public class SchemaCanvas extends JComponent {
    
    private Image srcImg;
    private SchemaEditorImpl controller;
    private Insets margin;
    
    public SchemaCanvas() {
    }
    
    public SchemaCanvas(Image srcImg, Insets margin) {
        this();
        this.srcImg = srcImg;
        this.margin = margin;
        if (srcImg != null) {
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);
            if (margin != null) {
                width = margin.left + width + margin.right;
                height = margin.top + height + margin.bottom;
            }
            this.setPreferredSize(new Dimension(width, height));
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getSourceImg() != null) {
            int iWidth = getSourceImg().getWidth(null);
            int iHeight = getSourceImg().getHeight(null);
            int cx = margin.left;
            int cy = margin.top;
            g2d.drawImage(getSourceImg(), cx, cy, iWidth, iHeight, this);
        }
            
        if (getController() != null) {
            getController().draw(g2d);
        }
    }

    public SchemaEditorImpl getController() {
        return controller;
    }

    public void setController(SchemaEditorImpl controller) {
        this.controller = controller;
    }

    public Image getSourceImg() {
        return srcImg;
    }

    public void setSourceImg(Image srcImg) {
        this.srcImg = srcImg;
        if (srcImg != null) {
            this.setPreferredSize(new Dimension(srcImg.getWidth(null), srcImg.getHeight(null)));
        }
        this.repaint();
    }
}
