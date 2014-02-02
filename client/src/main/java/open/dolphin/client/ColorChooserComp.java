package open.dolphin.client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;

/**
 * ColorChooserLabel
 *
 * @author Minagawa,Kazushi
 */
public class ColorChooserComp extends JComponent implements MouseListener, MouseMotionListener {
    
    public static final String SELECTED_COLOR = "selectedColor";
    
    private Color[] colors;
    
    private Color[] colorStart;
    
    private Dimension size;
    
    private Color strokeColor = Color.DARK_GRAY;
    
    private int strokeWidth = 2;
    
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    
    private Color selected;
    
    private int index = -1;
    
    /**
     * Creates a new progress panel with default values
     */
    public ColorChooserComp() {
        colorStart = ClientContext.getColorArray("color.set.default.start");
        colors = ClientContext.getColorArray("color.set.default.end");
        size = ClientContext.getDimension("colorCooserComp.default.size");
        strokeWidth = ClientContext.getInt("colorChooserComp.stroke.width");
        this.setPreferredSize(new Dimension(2*size.width*colors.length + size.width, 2*size.height));
        this.addMouseListener(ColorChooserComp.this);
        this.addMouseMotionListener(ColorChooserComp.this);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Creates a new progress panel with default values
     */
    public ColorChooserComp(Dimension size, Color[] colors) {
        this.size = size;
        this.colors = colors;
        this.setPreferredSize(new Dimension(2*size.width*colors.length + size.width, 2*size.height));
        this.addMouseListener(ColorChooserComp.this);
        this.addMouseMotionListener(ColorChooserComp.this);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public Color getSelectedColor() {
        return selected;
    }
    
    public void setSelectedColor(Color selected) {
        this.selected = selected;
        boundSupport.firePropertyChange(SELECTED_COLOR, null, this.selected);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        repaint();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        index = -1;
        repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        if (index >= 0 && index < colors.length) {
            setSelectedColor(colors[index]);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        double dx = size.getWidth()*2;
        double offsetX = size.getWidth();
        double offsetY = (this.getPreferredSize().getHeight() - size.getHeight())/2;
        
        BasicStroke stroke = new BasicStroke(strokeWidth);
        
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        
        for (int i=0; i < colors.length; i++) {
            double x = offsetX + i * dx;
            double y = offsetY;
            Ellipse2D.Double body = new Ellipse2D.Double(x, y, size.getWidth(), size.getHeight());
            GradientPaint lightToDark = new GradientPaint((int)x, (int)y, colorStart[i], (int)x + size.width, (int)y + size.height, colors[i]);
            g2.setPaint(lightToDark);
            g2.fill(body);
            if (i == index) {
                g2.setColor(strokeColor);
                g2.setStroke(stroke);
                g2.draw(body);
                //g2.setColor(colors[i]);
                //g2.fill(body);
                //GradientPaint lightToDark = new GradientPaint((int)x, (int)y, Color.LIGHT_GRAY, (int)x, (int)y + size.height, colors[i]);
                //g2.setPaint(lightToDark);
                //g2.fill(body);
                //g2.setColor(Color.DARK_GRAY);
                //g2.setStroke(new BasicStroke(2));
                //g2.draw(body);
            } //else {
            //g2.setColor(colors[i]);
            //g2.fill(body);
            //}
        }
    }
}