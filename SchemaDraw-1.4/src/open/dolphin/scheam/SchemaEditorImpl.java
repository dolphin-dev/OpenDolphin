
package open.dolphin.scheam;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.SchemaEditor;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.SchemaModel;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author kazm
 */
public class SchemaEditorImpl implements SchemaEditor {
    
    private static final float DEFAULT_LINE_WIDTH = 4.0f;
    private static final Color DEFAULT_FILL_COLOR = Color.RED;
    private static final float DEFAULT_ALPHA = 0.5f;
    private static final int DEFAULT_TEXT_SIZE = 24;
    private static final String DEFAULT_FONT_NAME = "Dialog";
    private static final String TITLE = "シェーマエディタ";
    //private static final String OK_BTN_TEXT = "カルテに展開";
    private static final String DEFAULT_ROLE = "参考図";
    private static final String DEFAULT_TITLE = "参考画像";
    
    private SchemaModel model;
    //protected BufferedImage srcImage;
    protected Image srcImage;
    private Insets margin;
    protected Vector<DrawingHolder> drawingList;
    private float lineWidth = DEFAULT_LINE_WIDTH;
    private Color fillColor = DEFAULT_FILL_COLOR;
    private float alpha = DEFAULT_ALPHA;
    private String fontName = DEFAULT_FONT_NAME;
    private int fontStyle = Font.BOLD;
    private int textSize = DEFAULT_TEXT_SIZE;
    private boolean outline = false;
    
    private SchemaCanvas canvas;
    private Baka view;
    private JPanel canvasPanel;
    private StateMgr stateMgr;
    private boolean editable;
    
    private PropertyChangeSupport boundSupport;
    
    public SchemaEditorImpl() {
    }
    
    public void setSchema(SchemaModel model) {
        this.model = model;
    }
    
    public void setEditable(boolean b) {
        this.editable = b;
    }
    
    public void start() {
        if (editable) {
            initComponents();
        } else {
            initComponentsUneditable();
        }
    }
    
    private BufferedImage createImage() {
        
        BufferedImage result = null;
        
        try {
            int width = srcImage.getWidth(null);
            int height = srcImage.getHeight(null);
            width = margin != null ? width + margin.left + margin.right : width;
            height = margin != null ? height + margin.top + margin.bottom : height;
            int x = margin != null ? margin.left : 0;
            int y = margin != null ? margin.top : 0;
            
            result = new BufferedImage(
                    width, 
                    height,
                    BufferedImage.TYPE_INT_BGR);
            
            Graphics2D g2 = result.createGraphics();
            Rectangle2D bounds = new Rectangle2D.Double(0, 0, width, height);
            g2.setPaint(Color.WHITE);
            g2.fill(bounds);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.drawImage(srcImage, x, y, srcImage.getWidth(null), srcImage.getHeight(null), null);
            
            for (DrawingHolder d : drawingList) {
                d.draw(g2);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    private void firePropertyChange(BufferedImage image) {
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            model.setIcon(icon);
            String text = view.getTitleFld().getText().trim();
            if (text.equals("")) {
                text = DEFAULT_TITLE;
            }
            model.getExtRef().setTitle(text);
            model.getExtRef().setMedicalRole((String) view.getRoleCombo().getSelectedItem());
            boundSupport.firePropertyChange("imageProp", null, model);
        } else {
            boundSupport.firePropertyChange("imageProp", model, null);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(l);
    }
    
    private void initComponentsUneditable() {
        
        srcImage =  model.getIcon().getImage();
        margin = new Insets(12, 12, 11, 11);
        
        canvas = new SchemaCanvas(srcImage, margin);
        canvas.setController(this);
        canvas.setBorder(BorderFactory.createEtchedBorder());
      
        view = new Baka((Frame) null, ClientContext.getFrameTitle(TITLE), true);
        canvasPanel = view.getCanvasPanel();
        canvasPanel.add(canvas);
        
        view.getSelectBtn().setEnabled(false);
        view.getLineBtn().setEnabled(false);
        view.getRectBtn().setEnabled(false);
        view.getOvalBtn().setEnabled(false);
        view.getOpenPathBtn().setEnabled(false);
        view.getPolyBtn().setEnabled(false);
        view.getRectFillBtn().setEnabled(false);
        view.getOvalFillBtn().setEnabled(false);
        view.getPolyFillBtn().setEnabled(false);
        view.getTextBtn().setEnabled(false);
       
        JComboBox widthCombo = view.getLineWidthCombo();
        Object[] lines = new Object[4];
        lines[0] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 1));
        lines[1] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 2));
        lines[2] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 4));
        lines[3] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 8));
        widthCombo.setModel(new DefaultComboBoxModel(lines));
        widthCombo.setEnabled(false);
        
        view.getColorBtn().setEnabled(false);
        view.getUndoBtn().setEnabled(false);
        view.getClearBtn().setEnabled(false);
        
        view.getColorBtn().setIcon(ShapeIconMaker.createRectFillIcon(DEFAULT_FILL_COLOR, new Dimension(32,32)));
        
        JTextField titleFld = view.getTitleFld();
        titleFld.setEditable(false);
        view.getRoleCombo().setEnabled(false);
        
        ExtRefModel extRef = model.getExtRef();
        if (extRef != null) {
            String text = extRef.getTitle();
            if (text != null && (!text.equals(""))) {
                titleFld.setText(text);
            }
            text = extRef.getMedicalRole();
            if (text != null && (!text.equals(""))) {
                view.getRoleCombo().setSelectedItem((String) text);
            }
        }
        
        view.getOkBtn().setEnabled(false);
        
        String canceltext = (String) UIManager.get("OptionPane.cancelButtonText");
        JButton cancelBtn = view.getCancelBtn();
        cancelBtn.setText(canceltext);
        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 close();
                 firePropertyChange(null);
            }
        });
        
        int height = canvas.getPreferredSize().height + 150;
        int width = view.getPreferredSize().width;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int offsetX = (dim.width - width) / 2;
        int offsetY = (dim.height - height) / 2;
        view.setBounds(offsetX, offsetY, width, height);
        
        view.setVisible(true);
    }
    
    private void initComponents() {
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Properties prop = null;
        try {
            prop = (Properties) appCtx.getLocalStorage().load("schemaEditorProp.xml");
        } catch (IOException e) {
        }
        if (prop != null) {
            try {
                setLineWidth(Float.parseFloat(prop.getProperty("lineWidth")));
                String line = prop.getProperty("fillColor");
                String[] rgb = line.split("\\s*,\\s*");
                int r = Integer.parseInt(rgb[0]);
                int g = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                setFillColor(new Color(r, g, b));
                setAlpha(Float.parseFloat(prop.getProperty("alpha")));
            } catch (Exception e) {
                e.printStackTrace(); // load failed
            }
        }
        
        srcImage =  model.getIcon().getImage();
        margin = new Insets(12, 12, 11, 11);
        
        canvas = new SchemaCanvas(srcImage, margin);
        canvas.setController(this);
        canvas.setBorder(BorderFactory.createEtchedBorder());
      
        view = new Baka((Frame) null, ClientContext.getFrameTitle(TITLE), true);
        canvasPanel = view.getCanvasPanel();
        canvasPanel.add(canvas);
        
        JToggleButton selectBtn = view.getSelectBtn();
        JToggleButton lineBtn = view.getLineBtn();
        JToggleButton rectBtn = view.getRectBtn();
        JToggleButton ellipseBtn = view.getOvalBtn();
        JToggleButton openPathBtn = view.getOpenPathBtn();

        JToggleButton polygonBtn = view.getPolyBtn();
        JToggleButton rectFillBtn = view.getRectFillBtn();
        JToggleButton ellipseFillBtn = view.getOvalFillBtn();
        JToggleButton polygonFillBtn = view.getPolyFillBtn();
        JToggleButton textBtn = view.getTextBtn();
        ButtonGroup bg = new ButtonGroup();
        bg.add(selectBtn);
        bg.add(lineBtn);
        bg.add(rectBtn);
        bg.add(ellipseBtn);
        bg.add(openPathBtn);
        bg.add(polygonBtn);
        bg.add(rectFillBtn);
        bg.add(ellipseFillBtn);
        bg.add(polygonFillBtn);
        bg.add(textBtn);
        
        JComboBox widthCombo = view.getLineWidthCombo();
        Object[] lines = new Object[4];
        lines[0] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 1));
        lines[1] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 2));
        lines[2] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 4));
        lines[3] = ShapeIconMaker.createRectFillIcon(Color.BLACK, new Dimension(30, 8));
        widthCombo.setModel(new DefaultComboBoxModel(lines));
        widthCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox cmb = (JComboBox) e.getSource();
                    int index = cmb.getSelectedIndex();
                    switch (index) {
                        case 0:
                            setLineWidth(1);
                            break;
                        case 1:
                            setLineWidth(2);
                            break;
                        case 2:
                            setLineWidth(4);
                            break; 
                        case 3:
                            setLineWidth(8);
                            break; 
                    }
                }
            }
        });
        
        float lw = getLineWidth();
        if (lw == 1.0f) {
            widthCombo.setSelectedIndex(0);
        } else if (lw == 2.0f) {
            widthCombo.setSelectedIndex(1);
        } else if (lw == 4.0f) {
            widthCombo.setSelectedIndex(2);
        } else if (lw == 8.0f) {
            widthCombo.setSelectedIndex(3);
        } else {
            // never
        }
        
        JButton colorBtn = view.getColorBtn();
        JButton undoBtn = view.getUndoBtn();
        JButton clearBtn = view.getClearBtn();
        
        colorBtn.setIcon(ShapeIconMaker.createRectFillIcon(getFillColor(), new Dimension(32,32)));
        
        canvas.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                stateMgr.mouseDown(e.getPoint());
            }

            public void mouseReleased(MouseEvent e) {
                stateMgr.mouseUp(e.getPoint());
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
            
        });
        
        canvas.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                stateMgr.mouseDragged(e.getPoint());
            }

            public void mouseMoved(MouseEvent e) {
            }
            
        });
        
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stateMgr.startSelect();
            }
        });
        
        lineBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startLine();
            }
        });
        
        rectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stateMgr.startRect();
            }
        });
        
        ellipseBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startEllipse();
            }
        });

        openPathBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startOpenPath();
            }
        });
        
        polygonBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startPolygon();
            }
        });
        
        rectFillBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stateMgr.startRectFill();
            }
        });
        
        ellipseFillBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startEllipseFill();
            }
        });
        
        polygonFillBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startPolygonFill();
            }
        });
        
        textBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.startText();
            }
        });
        
        colorBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                chooseColor();
            }
        });
        
        undoBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.undo();
            }
        });
        
        clearBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stateMgr.clear();
            }
        });
        
        JTextField titleFld = view.getTitleFld();
        titleFld.setText(DEFAULT_TITLE);
        titleFld.addFocusListener(AutoKanjiListener.getInstance());
        
        JComboBox roleCombo = view.getRoleCombo();
        roleCombo.setSelectedItem(DEFAULT_ROLE);
        
        ExtRefModel extRef = model.getExtRef();
        if (extRef != null) {
            String text = extRef.getTitle();
            if (text != null && (!text.equals(""))) {
                titleFld.setText(text);
            }
            text = extRef.getMedicalRole();
            if (text != null && (!text.equals(""))) {
                roleCombo.setSelectedItem((String) text);
            }
        }
        
        drawingList = new Vector(5);
        stateMgr = new StateMgr();
        
        JButton okBtn = view.getOkBtn();
        okBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                close();
                firePropertyChange(createImage());
            }
        });
        String canceltext = (String) UIManager.get("OptionPane.cancelButtonText");
        JButton cancelBtn = view.getCancelBtn();
        cancelBtn.setText(canceltext);
        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 close();
                firePropertyChange(null);
            }
        });
        
        int height = canvas.getPreferredSize().height + 150;
        int width = view.getPreferredSize().width;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int offsetX = (dim.width - width) / 2;
        int offsetY = (dim.height - height) / 2;
        view.setBounds(offsetX,offsetY,width, height);
        
        view.setVisible(true);
        
        rectBtn.doClick();
    }
    
    private void close() {
        try {
            ApplicationContext appCtx = ClientContext.getApplicationContext();
            Properties prop = new Properties();
            
            prop.setProperty("lineWidth", String.valueOf(getLineWidth()));
            
            StringBuffer buf = new StringBuffer();
            buf.append(String.valueOf(getFillColor().getRed()));
            buf.append(",");
            buf.append(String.valueOf(getFillColor().getGreen()));
            buf.append(",");
            buf.append(String.valueOf(getFillColor().getBlue()));
            prop.setProperty("fillColor", buf.toString());
            
            prop.setProperty("alpha", String.valueOf(getAlpha()));
            
            appCtx.getLocalStorage().save(prop, "schemaEditorProp.xml");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setVisible(false);
        view.dispose();
    }
    
    private void chooseColor() {
        
        Color newColor = JColorChooser.showDialog(view, "塗りつぶしカラー選択", getFillColor());
        if (newColor != null) {
            setFillColor(newColor);
            ImageIcon icon = ShapeIconMaker.createRectFillIcon(getFillColor(), new Dimension(32, 32));
            view.getColorBtn().setIcon(icon);
        }
    }
    
    public void draw(Graphics2D g2d) {
        if (stateMgr!= null) {
            stateMgr.draw(g2d);
        }
    }
    
    public void addShape(DrawingHolder s) {
        drawingList.add(s);
    }
    
    public DrawingHolder findDrawing(Point p) {
        DrawingHolder found = null;
        int cnt = drawingList.size();
        int last = cnt > 0 ? cnt -1 : 0;
        for (int i = last; i > -1; i--) {
            DrawingHolder d = drawingList.get(i);
            if (d.contains(p)) {
                found = d;
                break;
            }
        }
        return found;
    }
    
    public void removeLastShape() {
        DrawingHolder remove = drawingList.lastElement();
        drawingList.remove(remove);
    }
    
    public Stroke getStroke() {
        return new BasicStroke(getLineWidth());
    }
    
    public Paint getPaint() {
        return getFillColor();
    }
    
    public AlphaComposite getAlphaComposite() {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha());
    }
    
    public AlphaComposite getTextComposite() {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha());
    }
    
    public Font getFont() {
        return new Font(getFontName(), getFontStyle(), getTextSize());
    }
    
    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Color getTextColor() {
        return getFillColor();
    }
    
    public void setTextColor(Color textColor) {
        //this.textColor = textColor;
    }

    public float getTextAlpha() {
        return getAlpha();
    }
    
    public void setTextAlpha(float textAlpha) {
        //this.textAlpha = textAlpha;
    }
    
    public boolean isOutline() {
        return outline;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
    
    
    protected abstract class State {
        
        protected boolean first;
        protected Point start;
        protected Point end;
        
        public abstract void mouseDown(Point p);
        public abstract void mouseDragged(Point p);
        public abstract void mouseUp(Point p);
        
        public void draw(Graphics2D g2d) {
        }
        
        protected boolean isDragged() {
            
            if (start == null || end == null) {
                return false;
            }
            
            int x = Math.abs(start.x - end.x);
            int y = Math.abs(start.y - end.y);
            
            if (x > 5 || y > 5) {
                return true;
            }
            
            return false;
        }
    }
    
    class SelectState extends State {
        
        private DrawingHolder moving;

        @Override
        public void mouseDown(Point p) {
            moving = findDrawing(p);
            if (moving != null) {
                start = p;
                end = null;
            }
        }

        @Override
        public void mouseDragged(Point p) {
            if (moving != null) {
                end = p;
                if (isDragged()) {
                    moving.translate(end.getX() - start.getX(), end.getY() - start.getY());
                    canvas.repaint();
                    start = end;
                }
            }
        }

        @Override
        public void mouseUp(Point p) {
        }
    }
    
    class RectState extends State {

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                createAndAddShape();
                canvas.repaint();
            }
        }

        @Override
        public void mouseUp(Point p) {
        }
        
        private void createAndAddShape() {
            Rectangle2D shape = new Rectangle2D.Double();
            shape.setFrameFromDiagonal(start, end);
            if (first) {
                first = false;
            } else {
                removeLastShape();
            }
            AreaHolder sh = new AreaHolder(new Area(shape), getStroke(), getPaint(), getAlphaComposite(), false);
            addShape(sh);
        }
    }
    
    class EllipseState extends State {

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                createAndAddShape();
                canvas.repaint();
            }
        }

        @Override
        public void mouseUp(Point p) {
        }
        
        private void createAndAddShape() {
            Ellipse2D shape = new Ellipse2D.Double();
            shape.setFrameFromDiagonal(start, end);
            if (first) {
                first = false;
            } else {
                removeLastShape();
            }
            AreaHolder sh = new AreaHolder(new Area(shape), getStroke(), getPaint(), getAlphaComposite(), false);
            addShape(sh);
        }
    }    
        
    class PolygonState extends State {
        
        private GeneralPath gpath;

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
            gpath = null;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                if (first) {
                    gpath = new GeneralPath();
                    gpath.moveTo(start.x, start.y);
                    gpath.lineTo(end.x, end.y);
                    first = false;
                } else {
                    gpath.lineTo(end.x, end.y);
                }
                canvas.repaint();
                start = end;
            }
        }

        @Override
        public void mouseUp(Point p) {
            end = p;
            if (gpath != null) {
                gpath.closePath();
                canvas.repaint();
                AreaHolder sh = new AreaHolder(new Area(gpath), getStroke(), getPaint(), getAlphaComposite(), false);
                addShape(sh);
            }
            gpath = null;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            
            if (gpath == null) {
                return;
                
            }
            Stroke saveStroke = g2d.getStroke();
            Paint savePaint = g2d.getPaint();
            Composite saveComposite = g2d.getComposite();

            g2d.setStroke(getStroke());
            g2d.setComposite(getAlphaComposite());
            g2d.setPaint(getPaint());

            g2d.draw(gpath);

            g2d.setStroke(saveStroke); 
            g2d.setPaint(savePaint);
            g2d.setComposite(saveComposite);
        }
    }

    class OpenPathState extends State {

        private GeneralPath gpath;

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
            gpath = null;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                if (first) {
                    gpath = new GeneralPath();
                    gpath.moveTo(start.x, start.y);
                    gpath.lineTo(end.x, end.y);
                    first = false;
                } else {
                    gpath.lineTo(end.x, end.y);
                }
                canvas.repaint();
                start = end;
            }
        }

        @Override
        public void mouseUp(Point p) {
            end = p;
            if (gpath != null) {
                //gpath.closePath();
                canvas.repaint();
                //AreaHolder sh = new AreaHolder(new Area(gpath), getStroke(), getPaint(), getAlphaComposite(), false);
                OpenPathHolder sh = new OpenPathHolder(gpath, getStroke(), getPaint(), getAlphaComposite(), false);
                addShape(sh);
            }
            gpath = null;
        }

        @Override
        public void draw(Graphics2D g2d) {

            if (gpath == null) {
                return;

            }
            Stroke saveStroke = g2d.getStroke();
            Paint savePaint = g2d.getPaint();
            Composite saveComposite = g2d.getComposite();

            g2d.setStroke(getStroke());
            g2d.setComposite(getAlphaComposite());
            g2d.setPaint(getPaint());

            g2d.draw(gpath);

            g2d.setStroke(saveStroke);
            g2d.setPaint(savePaint);
            g2d.setComposite(saveComposite);
        }
    }
    
    class RectFillState extends State {

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                createAndAddShape();
                canvas.repaint();
            }
        }

        @Override
        public void mouseUp(Point p) {
        }
        
        private void createAndAddShape() {
            Rectangle2D shape = new Rectangle2D.Double();
            shape.setFrameFromDiagonal(start, end);
            if (first) {
                first = false;
            } else {
                removeLastShape();
            }
            AreaHolder sh = new AreaHolder(new Area(shape), null, getPaint(), getAlphaComposite(), true);
            addShape(sh);
        }
    }
    
    class EllipseFillState extends State {

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                createAndAddShape();
                canvas.repaint();
            }
        }

        @Override
        public void mouseUp(Point p) {
        }
        
        private void createAndAddShape() {
            Ellipse2D shape = new Ellipse2D.Double();
            shape.setFrameFromDiagonal(start, end);
            if (first) {
                first = false;
            } else {
                removeLastShape();
            }
            AreaHolder sh = new AreaHolder(new Area(shape), null, getPaint(), getAlphaComposite(), true);
            addShape(sh);
        }
    }
    
    class PolygonFillState extends State {
        
        private GeneralPath gpath;
        private boolean closed;

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
            gpath = null;
            closed = false;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                if (first) {
                    gpath = new GeneralPath();
                    gpath.moveTo(start.x, start.y);
                    gpath.lineTo(end.x, end.y);
                    first = false;
                } else {
                    gpath.lineTo(end.x, end.y);
                }
                canvas.repaint();
                start = end;
            }
        }

        @Override
        public void mouseUp(Point p) {
            end = p;
            if (gpath != null) {
                gpath.closePath();
                closed = true;
                canvas.repaint();
                AreaHolder sh = new AreaHolder(new Area(gpath), null, getPaint(), getAlphaComposite(), true);
                addShape(sh);
            }
            gpath = null;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            
            if (gpath == null) {
                return;
            }
            
            Stroke saveStroke = g2d.getStroke();
            Paint savePaint = g2d.getPaint();
            Composite saveComposite = g2d.getComposite();

            g2d.setComposite(getAlphaComposite());
            g2d.setPaint(getPaint());

            if (closed) {
                g2d.fill(gpath);
            } else {
                g2d.setStroke(getStroke());
                g2d.draw(gpath);
            }

            g2d.setStroke(saveStroke); 
            g2d.setPaint(savePaint);
            g2d.setComposite(saveComposite);
        }
    }
    
    class LineState extends State {

        @Override
        public void mouseDown(Point p) {
            start = p;
            end = null;
            first = true;
        }

        @Override
        public void mouseDragged(Point p) {
            end = p;
            if (isDragged()) {
                Line2D.Double shape = getShape();
                if (shape != null) {
                    if (first) {
                        first = false;
                    } else {
                        removeLastShape();
                    }
                    Line2DHolder sh = new Line2DHolder(shape, getStroke(), getPaint(), getAlphaComposite());
                    addShape(sh);
                    canvas.repaint();
                }
            }
        }

        @Override
        public void mouseUp(Point p) {
        }

        private Line2D.Double getShape() {
            return new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY());
        }
    }
    
    class TextState extends State {
        
        private String inputText;
        
        @Override
        public void mouseDown(Point p) {
            inputText = JOptionPane.showInputDialog(view, "テキストを入力してください。");
            if (inputText != null) {
                start = p;
                end = null;
                canvas.repaint();
            } else {
                start = null;
                end = null;
            }
        }

        @Override
        public void mouseDragged(Point p) {
        }

        @Override
        public void mouseUp(Point p) {
        }

        @Override
        public void draw(Graphics2D g2d) {
            
            if (inputText != null && start != null) {
                
                Stroke saveStroke = g2d.getStroke();
                Paint savePaint = g2d.getPaint();
                Composite saveComposite = g2d.getComposite();
                
                FontRenderContext ctx = g2d.getFontRenderContext();
                Font f = getFont();
                
                TextLayout layout = new TextLayout(inputText, f, ctx);
                AffineTransform trans = AffineTransform.getTranslateInstance(start.getX(), start.getY());
                Shape outLine = layout.getOutline(trans);
                
                g2d.setPaint(getTextColor());
                if (isOutline()) {
                    g2d.draw(outLine);
                } else {
                    g2d.fill(outLine);
                }
                
                g2d.setStroke(saveStroke); 
                g2d.setPaint(savePaint);
                g2d.setComposite(saveComposite);
                
                AreaHolder sh = new TextHolder(new Area(outLine), null, getTextColor(), getTextComposite(), !isOutline());
                addShape(sh);
                
                inputText = null;
            }
        }
    }
    
    class UndoState extends State {

        @Override
        public void mouseDown(Point p) {
        }

        @Override
        public void mouseDragged(Point p) {
        }

        @Override
        public void mouseUp(Point p) {
        }

        @Override
        public void draw(Graphics2D g2d) {
        }
    }
    
    class StateMgr {
        
        private State selectState;
        
        private State lineState;
        private State rectState;
        private State ellipseState;
        private State openPathState;
        private State polygonState;
        
        private State rectFillState;
        private State ellipseFillState;
        private State polygonFillState;
        
        private State textState;
        
        private State undoState;
        private State curState = rectFillState;
        private State savedState;
        
        public StateMgr() {
            selectState = new SelectState();
            
            lineState = new LineState();
            rectState = new RectState();
            ellipseState = new EllipseState();
            openPathState = new OpenPathState();
            polygonState = new PolygonState();
            
            rectFillState = new RectFillState();
            ellipseFillState = new EllipseFillState();
            polygonFillState = new PolygonFillState();
            
            textState = new TextState();
            undoState = new UndoState();
            
            curState = rectFillState; 
        }
        
        public void startSelect() {
            curState = selectState;
        }
    
        public void startLine() {
            curState = lineState;
        }
    
        public void startRect() {
            curState = rectState;
        }
        
        public void startEllipse() {
            curState = ellipseState;
        }

        public void startOpenPath() {
            curState = openPathState;
        }
        
        public void startPolygon() {
            curState = polygonState;
        }
        
        public void startRectFill() {
            curState = rectFillState;
        }
        
        public void startEllipseFill() {
            curState = ellipseFillState;
        }
        
        public void startPolygonFill() {
            curState = polygonFillState;
        }
        
        public void startText() {
            curState = textState;
        }
        
        public void mouseDown(Point p) {
            curState.mouseDown(p);
        }
        
        public void mouseDragged(Point p) {
            curState.mouseDragged(p);
        }
        
        public void mouseUp(Point p) {
            curState.mouseUp(p);
        }
        
        public void draw(Graphics2D g2d) {
            
            for (DrawingHolder d : drawingList) {
                d.draw(g2d);
            }
            
            if (curState == undoState) {
                curState = savedState;
            } else {
                curState.draw(g2d);
            }
        }
        
        public void undo() {
            DrawingHolder d = drawingList.lastElement();
            drawingList.remove(d);
            savedState = curState;
            curState = undoState;
            canvas.repaint();
        }
        
        public void clear() {
            drawingList.clear();
            savedState = curState;
            curState = undoState;
            canvas.repaint();
        }
    }
}
