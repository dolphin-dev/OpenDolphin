package open.dolphin.client;

import javax.swing.*;
import javax.swing.text.*;

import open.dolphin.infomodel.SchemaModel;

import java.beans.*;
import java.awt.*;

import java.awt.event.MouseEvent;
import java.awt.image.*;
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
import java.util.Iterator;
import open.dolphin.plugin.PluginLoader;
import org.apache.log4j.Logger;

/**
 * スタンプのデータを保持するコンポーネントで TextPane に挿入される。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SchemaHolder extends AbstractComponentHolder implements ComponentHolder {
    
    private static final long serialVersionUID = 1777560751402251092L;
    private static final Color SELECTED_BORDER = new Color(255, 0, 153);
    
    private SchemaModel schema;
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    // to restrict the size of the component,
    // setBounds and setSize are overridden.
    private int fixedSize = 192;//160;/////////////////////////////////////////
    private int fixedWidth = fixedSize;
    private int fixedHeight = fixedSize;
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    private boolean selected;
    
    private Position start;
    
    private Position end;
    
    private KartePane kartePane;
    
    private Color selectedBorder = SELECTED_BORDER;
    
    private Logger logger;
    
    
    public SchemaHolder(KartePane kartePane, SchemaModel schema) {
        
        logger = ClientContext.getBootLogger();
        logger.debug("SchemaHolder constractor");
        
        this.kartePane = kartePane;
        
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // Junzo SATO
        // for simplicity, the acpect ratio of the fixed rect is set to 1.
        this.setSize(fixedWidth, fixedHeight);
        this.setMaximumSize(new Dimension(fixedWidth, fixedHeight));
        this.setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        this.setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        // adjustment for printer
        this.setDoubleBuffered(false);
        this.setOpaque(true);
        this.setBackground(Color.white);
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        
        this.schema = schema;
        this.setImageIcon(schema.getIcon());
        
    }
    
    public void setImageIcon(ImageIcon icon) {
        logger.debug("SchemaHolder setImageIcon");
        setIcon(adjustImageSize(icon, new Dimension(fixedWidth, fixedHeight)));
    }
    
    public int getContentType() {
        return ComponentHolder.TT_IMAGE;
    }
    
    public KartePane getKartePane() {
        return kartePane;
    }
    
    public SchemaModel getSchema() {
        return schema;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void enter(ActionMap map) {
        
        logger.debug("SchemaHolder enter");
        
//        ChartMediator mediator = kartePane.getMediator();
//        mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
//        mediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
                
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        
        if (kartePane.getTextPane().isEditable()) {
            map.get(GUIConst.ACTION_CUT).setEnabled(true);
        } else {
            map.get(GUIConst.ACTION_CUT).setEnabled(false);
        }
        
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
        
        setSelected(true);
    }
    
    public void exit(ActionMap map) {
        logger.debug("SchemaHolder exit");
        setSelected(false);
    }
    
    public Component getComponent() {
        return this;
    }
    
    public void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            popup.setFocusable(false);
            ChartMediator mediator = kartePane.getMediator();
            popup.add(mediator.getAction(GUIConst.ACTION_CUT));
            popup.add(mediator.getAction(GUIConst.ACTION_COPY));
            popup.add(mediator.getAction(GUIConst.ACTION_PASTE));
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    public void setSelected(boolean selected) {
        logger.debug("SchemaHolder setSelected " + selected);
        boolean old = this.selected;
        this.selected = selected;
        if (old != this.selected) {
            if (this.selected) {
                this.setBorder(BorderFactory.createLineBorder(selectedBorder));
            } else {
                this.setBorder(BorderFactory.createLineBorder(kartePane.getTextPane().getBackground()));
            }
        }
    }
    
    public void edit() {
        
        logger.debug("SchemaHolder edit");
        try {
            PluginLoader<SchemaEditor> loader = PluginLoader.load(SchemaEditor.class, ClientContext.getPluginClassLoader());
            Iterator<SchemaEditor> iter = loader.iterator();
            if (iter.hasNext()) {
                final SchemaEditor editor = iter.next();
                editor.setSchema(schema);
                editor.setEditable(kartePane.getTextPane().isEditable());
                editor.addPropertyChangeListener(SchemaHolder.this);
                Runnable awt = new Runnable() {

                    public void run() {
                        editor.start();
                    }
                };
                EventQueue.invokeLater(awt);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e);
        }
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        logger.debug("SchemaHolder propertyChange");
        SchemaModel newSchema = (SchemaModel)e.getNewValue();
        if (newSchema ==  null) {
            return;
        }
        
        schema = newSchema;
        setIcon(adjustImageSize(schema.getIcon(), new Dimension(fixedWidth, fixedHeight)));
    }
    
    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    public int getStartPos() {
        return start.getOffset();
    }
    
    public int getEndPos() {
        return end.getOffset();
    }
    
    /**
     * LDAP Programming with Java.
     */
    protected ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {
        logger.debug("SchemaHolder adjustImageSize");
        if ( (icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width) ) {
            Image img = icon.getImage();
            float hRatio = (float)icon.getIconHeight() / dim.height;
            float wRatio = (float)icon.getIconWidth() / dim.width;
            int h, w;
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int)(icon.getIconWidth() / hRatio);
            } else {
                w = dim.width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }
}