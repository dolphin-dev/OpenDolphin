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

import open.dolphin.infomodel.Schema;


import java.beans.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Junzo SATO
import java.awt.image.*;
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * スタンプのデータを保持するコンポーネントで TextPane に挿入される。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SchemaHolder extends JLabel 
implements IComponentHolder, DragGestureListener, DragSourceListener {

    static final Color FOREGROUND = Color.blue;
    static final Color BACKGROUND = Color.white;
    static final Color SELECTED_BORDER = Color.magenta;
    static final Color DESELECTED_BORDER = Color.white;
        
    Schema schema;
        
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    // to restrict the size of the component, 
    // setBounds and setSize are overridden.
    int fixedSize = 192;//160;/////////////////////////////////////////
    int fixedWidth = fixedSize;
    int fixedHeight = fixedSize;
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    boolean selected;

    int id;

    Position start;

    Position end;

    KartePane kartePane;
    
    Color foreGround = FOREGROUND;
    
    Color background = BACKGROUND;
    
    Color selectedBorder = SELECTED_BORDER;
    
    Color deSelectedBorder = DESELECTED_BORDER;
    
    DragSource dragSource;

    public SchemaHolder(KartePane kartePane, int id, Schema schema) {

        this.kartePane = kartePane;
        this.id = id;
        
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
        this.setBorder(BorderFactory.createLineBorder(deSelectedBorder)); 
        
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }
    
    public void setImageIcon(ImageIcon icon) {
        //schema.setIcon(icon);
        setIcon(adjustImageSize(icon, new Dimension(fixedWidth, fixedHeight)));
    } 

    public int getContentType() {
        return IComponentHolder.TT_IMAGE;
    }
    
    public int getId() {
        return id;
    }
    
    public Schema getSchema() {
    	return schema;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public boolean toggleSelection() {
    	
    	// 2004-02-14 DnD のためトグル選択を止める
		selected = selected ? true : true;
		Color c = selected ? selectedBorder : kartePane.getBackground();
		this.setBorder(BorderFactory.createLineBorder(c));

		return selected;
		
        /*if (selected) {
            this.setBorder(BorderFactory.createLineBorder(deSelectedBorder));
            selected = false;
        }
        else {
            this.setBorder(BorderFactory.createLineBorder(selectedBorder));
            selected = true;
        }
        return selected; */
    }

    public void setSelected(boolean b) {
        if (b) {
            this.setBorder(BorderFactory.createLineBorder(selectedBorder));
            selected = true;
        }
        else if (selected) {
            this.setBorder(BorderFactory.createLineBorder(deSelectedBorder));
            selected = false;
        }
    }

    public void edit(boolean b) {

        try {            
            final SchemaEditorDialog dlg = new SchemaEditorDialog((Frame)null, true, schema, b);
            dlg.addPropertyChangeListener(SchemaHolder.this);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dlg.run();
                }
           });
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }                
    }

    public void propertyChange(PropertyChangeEvent e) {
        
        Schema newSchema = (Schema)e.getNewValue();
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
            }
            else {
                w = dim.width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        else {
            return icon;
        }
    }
    
    public void dragGestureRecognized(DragGestureEvent event) {
        
        Transferable tr = kartePane.getImageTrain();
                
        if (tr != null) {
            try {
                SchemaList list = (SchemaList)tr.getTransferData(SchemaListTransferable.schemaListFlavor);
                if (list.schemaList.length == 1) {
                    Cursor cursor = DragSource.DefaultMoveDrop; //DefaultCopyDrop;
                    dragSource.startDrag(event, cursor, tr, this);
                }
                
            } catch (UnsupportedFlavorException e) {
                System.out.println("DEBUG UnsupportedException while getting the transfer data: " 
                                    + e.toString());
            } catch (IOException ie) {
                System.out.println("IOException while getting the transfer data: " 
                                    + ie.toString());
            }
        
        } /* else {
            
            SchemaList list = new SchemaList();
            Schema[] s = new Schema[1];
            s[0] = schema;
            list.schemaList = s;
            tr = new SchemaListTransferable(list);
            Cursor cursor = DragSource.DefaultMoveDrop; //DefaultCopyDrop;
            dragSource.startDrag(event, cursor, tr, this);
        }*/
    }

    public void dragDropEnd(DragSourceDropEvent event) { 
    }

    public void dragEnter(DragSourceDragEvent event) {
    }

    public void dragOver(DragSourceDragEvent event) {
    }
    
    public void dragExit(DragSourceEvent event) {
    }    

    public void dropActionChanged ( DragSourceDragEvent event) {
    }        
}