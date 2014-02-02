/*
 * SchemaHolder.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import javax.swing.*;
import javax.swing.text.*;

import open.dolphin.infomodel.SchemaModel;

import java.beans.*;
import java.awt.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Junzo SATO
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.*;
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * スタンプのデータを保持するコンポーネントで TextPane に挿入される。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SchemaHolder extends ComponentHolder implements IComponentHolder {
    
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
    
    
    public SchemaHolder(KartePane kartePane, SchemaModel schema) {
        
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
        //schema.setIcon(icon);
        setIcon(adjustImageSize(icon, new Dimension(fixedWidth, fixedHeight)));
    }
    
    public int getContentType() {
        return IComponentHolder.TT_IMAGE;
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
    
    public void focusGained(FocusEvent e) {
        //System.out.println("schema gained");
        ChartMediator mediator = kartePane.getMediator();
        mediator.setCurrentComponent(this);
        mediator.setCurrentComponent(this);
        mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
        mediator.enableMenus(new String[]{GUIConst.ACTION_COPY});
        mediator.enableMenus(new String[]{GUIConst.ACTION_COPY});
        if (kartePane.getTextPane().isEditable()) {
            mediator.enableMenus(new String[]{GUIConst.ACTION_CUT});
        } else {
            mediator.disableMenus(new String[]{GUIConst.ACTION_CUT});
        }
        mediator.disableMenus(new String[]{GUIConst.ACTION_PASTE});
        setSelected(true);
    }
    
    public void focusLost(FocusEvent e) {
        //System.out.println("schema lost");
        //	ChartMediator mediator = kartePane.getMediator();
        //	String[] menus = new String[]{"cut", "copy", "paste"};
        //	mediator.disableMenus(menus);
        setSelected(false);
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
        
        try {
            final SchemaEditorDialog dlg = new SchemaEditorDialog((Frame)null, true, schema, kartePane.getTextPane().isEditable());
            dlg.addPropertyChangeListener(SchemaHolder.this);
            SwingUtilities.invokeLater(new Runnable() {
                
                public void run() {
                    dlg.run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        
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
    
    // this is a trick for the print out functionality:-)
    class ColorFilter extends RGBImageFilter {
        public ColorFilter() {
            canFilterIndexColorModel = true;
        }
        public int filterRGB(int x, int y, int rgb) {
            DirectColorModel cm = (DirectColorModel)ColorModel.getRGBdefault();
            int a = cm.getAlpha(rgb);
            //int r = 255 - cm.getRed(rgb);
            //int g = 255 - cm.getGreen(rgb);
            //int b = 255 - cm.getBlue(rgb);
            int r = cm.getRed(rgb);
            int g = cm.getGreen(rgb);
            int b = cm.getBlue(rgb);
            
            return (a << 24 | r << 16 | g << 8 | b);
        }
    }
    
    /*public void paint(Graphics g) {
        super.paint(g);
     
        this.setForeground(Color.black);
        this.setBackground(Color.white);
     
        Dimension d = this.getSize();
        g.setColor(Color.white);
        //g.fillRect(0,0,d.width,d.height);
        g.fillRect(1,1,d.width-2,d.height-2);// leave margin
     
        // get current (latest) image
        ImageIcon icon = (ImageIcon)this.getIcon();
        if (icon == null) return;
     
        Image smallImg = icon.getImage();
        if (smallImg == null) return;
     
        int xx = 0, yy = 0, ww = 0, hh = 0;
        ww = smallImg.getWidth(this);
        hh = smallImg.getHeight(this);
        if (ww < 0 || hh < 0) return;
     
        if (ww > hh) {
            if (fixedWidth < ww) {
                hh = (int)(hh * ((float)fixedWidth / ww));
                ww = fixedWidth;
                yy = yy + (fixedHeight - hh)/2;
            } else {
                xx = xx + (fixedWidth - ww)/2;
                yy = yy + (fixedHeight - hh)/2;
            }
        } else {
            if (fixedHeight < hh) {
                ww = (int)(ww * ((float)fixedHeight / hh));
                hh = fixedHeight;
                xx = xx + (fixedWidth - ww)/2;
            } else {
                xx = xx + (fixedWidth - ww)/2;
                yy = yy + (fixedHeight - hh)/2;
            }
        }
     
        //----------------------------------------------------------------------
        // because jdk1.4 beta3 cannot print component with correct color of schema image,
        // I apply color filter here.
        // I know this is a cheap and dirty trick. But this works:-)
        Image src = smallImg;
        ImageFilter colorfilter = new ColorFilter();
        Image img = createImage(new FilteredImageSource(src.getSource(),colorfilter));
        g.drawImage(img, xx+1, yy+1, ww-2, hh-2, this);// leave margin
    }*/
    
    /**
     * LDAP Programming with Java.
     */
    protected ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {
        
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