package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.text.Position;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.plugin.PluginLoader;

/**
 * スタンプのデータを保持するコンポーネントで TextPane に挿入される。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SchemaHolder extends AbstractComponentHolder implements ComponentHolder {
    
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
    
    
    public SchemaHolder(KartePane kartePane, SchemaModel schema) {
        
        this.kartePane = kartePane;
        
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // Junzo SATO
        // for simplicity, the acpect ratio of the fixed rect is set to 1.
        this.setDoubleBuffered(false);
        this.setOpaque(true);
        this.setBackground(Color.white);
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        this.schema = schema;
        //this.setImageIcon(schema.getIcon());
        setIcon(adjustImageSize(schema.getIcon(), new Dimension(fixedWidth, fixedHeight)));
        
    }
    
    @Override
    public int getContentType() {
        return ComponentHolder.TT_IMAGE;
    }
    
    @Override
    public KartePane getKartePane() {
        return kartePane;
    }
    
    public SchemaModel getSchema() {
        return schema;
    }
    
    @Override
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    public void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            popup.setFocusable(false);
            ChartMediator mediator = kartePane.getMediator();
            popup.add(mediator.getAction(GUIConst.ACTION_CUT));
            popup.add(mediator.getAction(GUIConst.ACTION_COPY));
            popup.add(mediator.getAction(GUIConst.ACTION_PASTE));
            popup.addSeparator();
            
            // 右クリックで編集
            AbstractAction action = new AbstractAction("編集") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    edit();
                }
            };
            popup.add(action);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    @Override
    public void setSelected(boolean selected) {
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
    
    @Override
    public void edit() {
        try {
            PluginLoader<SchemaEditor> loader = PluginLoader.load(SchemaEditor.class);
            Iterator<SchemaEditor> iter = loader.iterator();
            if (iter.hasNext()) {
                final SchemaEditor editor = iter.next();
                editor.setSchema(schema);
                editor.setEditable(kartePane.getTextPane().isEditable());
                editor.addPropertyChangeListener(SchemaHolder.this);
                Runnable awt = new Runnable() {

                    @Override
                    public void run() {
                        editor.start();
                    }
                };
                EventQueue.invokeLater(awt);
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SchemaModel newSchema = (SchemaModel)e.getNewValue();
        if (newSchema ==  null) {
            return;
        }
        
        schema = newSchema;
        setIcon(adjustImageSize(schema.getIcon(), new Dimension(fixedWidth, fixedHeight)));
        this.kartePane.setDirty(true);
    }
    
    @Override
    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public int getStartPos() {
        return start.getOffset();
    }
    
    @Override
    public int getEndPos() {
        return end.getOffset();
    }
    
    /**
     * アスペクト比を保って画像とラベルのサイズを変更する。
     */
    protected ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {
        
        int newWidth = icon.getIconWidth();
        int newHeight = icon.getIconHeight();
        float width = (float)icon.getIconWidth();
        float height = (float)icon.getIconHeight();
        float dimW = (float)dim.width;
        float dimH = (float)dim.height;
        boolean needResize = false;
        
        // 縦長画像の時
        if (height>width) {
            
            // 画像の高さがラベルより大きい時、画像の高さを dim.height にする
            if (height>dimH) {
                newHeight = (int)dimH;
                float ratio = dimH/height;
                newWidth = (int)(ratio*width);
                needResize = true;
            }
            
        } // 横長画像の時
        else {
            
            // 画像の幅がラベルより大きい時、画像の幅を dim.width にする
            if (width>dimW) {
                newWidth = (int)dimW;
                float ratio = dimW/width;
                newHeight = (int)(ratio*height); 
                needResize = true;
            }
        }
        
        // ラベルのサイズ変更
        this.setSize(newWidth, newHeight);
        this.setMaximumSize(new Dimension(newWidth, newHeight));
        this.setMinimumSize(new Dimension(newWidth, newHeight));
        this.setPreferredSize(new Dimension(newWidth, newHeight));
        
        if (needResize) {
            Image img = icon.getImage();
            img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }
}