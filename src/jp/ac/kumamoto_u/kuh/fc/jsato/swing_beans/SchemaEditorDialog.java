/*
 * SchemaEditorDialog.java
 *
 * Created on 2001/09/08, 11:36
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import open.dolphin.infomodel.*;

import java.awt.event.*;
import java.util.*;

import java.io.*;

//import javax.media.jai.*;
//import java.awt.image.renderable.*;

import com.sun.image.codec.jpeg.*;
import jp.ac.kumamoto_u.kuh.fc.jsato.*;

/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */

public class SchemaEditorDialog extends javax.swing.JDialog implements Runnable {
    //-----------------------------------------------
    // Runnable
    public void run() {
        // adjust location of the dialog on screen
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bnd = this.getBounds();
        //this.move(scrn.width/2 - bnd.width/2, scrn.height/2 - bnd.height/2);
        this.setLocation(scrn.width/2 - bnd.width/2, scrn.height/2 - bnd.height/2);
        
        //this.setVisible(true);
        this.show();
    }
    
    //-----------------------------------------------
    // Dragging Tool
    static final int FREEFORM = 0;
    static final int LINE = 1;
    static final int OVAL = 2;
    static final int RECT = 3;
    static final int POLY = 4;
    static final int FILLOVAL = 5; 
    static final int FILLRECT = 6;
    static final int FILLPOLY = 7;
    
    // Clicking Tool
    static final int CLICKING_TEXT = 8;
    static final int CLICKING_POLYLINE = 9;
    
    public boolean isClickingTool() {
        switch (toolID) {
            //==================================
            case CLICKING_TEXT:
            case CLICKING_POLYLINE:
                return true;
            //==================================
            default:
                return false;
        }
    }
    
    // Default Tool
    int toolID = FREEFORM;
    
    // this is a simple factory of OverlayImageTool
    public OverlayImageTool createTool(int id) {
        switch (id) {
            // Dragging Tool ---------------------------------------------------
            case FREEFORM:
                return new OverlayImageToolFreeform(overlayImagePanelBean1);
            case LINE:
                return new OverlayImageToolLine(overlayImagePanelBean1);
            case OVAL:
                return new OverlayImageToolOval(overlayImagePanelBean1);
            case RECT:
                return new OverlayImageToolRect(overlayImagePanelBean1);
            case POLY:
                return new OverlayImageToolPoly(overlayImagePanelBean1);
            case FILLOVAL:
                return new OverlayImageToolFillOval(overlayImagePanelBean1);
            case FILLRECT:
                return new OverlayImageToolFillRect(overlayImagePanelBean1);
            case FILLPOLY:
                return new OverlayImageToolFillPoly(overlayImagePanelBean1);
            default:
                return null;
        }
    }
    
    public OverlayImageClickingTool createClickingTool(int id) {
        switch (id) {
            // Clicking Tool ---------------------------------------------------
            case CLICKING_TEXT:
                return new OverlayImageClickingToolText(overlayImagePanelBean1);
            case CLICKING_POLYLINE:
                return new OverlayImageClickingToolPolyLine(overlayImagePanelBean1);
            default:
                return null;
        }
    }

    OverlayImageTool tool = null;
    OverlayImageClickingTool clickingTool = null;
    
    // this handler can use not only dragging but also clicking operation
    class DraggingToolMouseHandler implements MouseListener, MouseMotionListener {
        boolean dragging = false;
        int x, y;// current mouse point

        public DraggingToolMouseHandler() {
            x = 0;
            y = 0;
        }

        public Dimension calcTopLeft() {
            Dimension panelSize = overlayImagePanelBean1.getSize();
            Dimension imgSize = overlayImagePanelBean1.getSrcImgSize();
            int dw = (panelSize.width - imgSize.width)/2;
            int dh = (panelSize.height - imgSize.height)/2;
            //System.out.println("panel size: " + panelSize);
            //System.out.println("img size: " + imgSize);
            //System.out.println("(dw, dh) = (" + dw + ", " + dh + ")");
            return new Dimension(dw, dh);
        }

        public boolean isMouseInsideImage(int x, int y) {
            Dimension panelSize = overlayImagePanelBean1.getSize();
            Dimension imgSize = overlayImagePanelBean1.getSrcImgSize();
            int dw = (panelSize.width - imgSize.width)/2;
            int dh = (panelSize.height - imgSize.height)/2;
            boolean inside = (
                (dw <= x) && (x <= (dw + imgSize.width)) && 
                (dh <= y) && (y <= (dh + imgSize.height))
            );
            
            //System.out.println("inside: " + inside);
            return inside;
        }
        
        public Point adjustPoint(Point p) {
            Dimension d = calcTopLeft();
            return new Point(p.x - d.width, p.y - d.height);
        }
        
        //----------------------------------------------------------------------
        public void mouseEntered(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();
            //System.out.println("Entered: " + x + ", " + y);
        }

        public void mouseExited(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();
            //System.out.println("Exited: " + x + ", " + y);
        }

        public void mouseClicked(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();
            
            //////////////////////////////////
            if (isMouseInsideImage(x, y) == false) {
                return;
            }

            if (isClickingTool() == false) {
                return;
            }
            
            if (clickingTool == null) {
                clickingTool = createClickingTool(toolID);
                clickingTool.setPenSize(selectedPenSize);
            }
            
            if (evt.getClickCount() == 2) {
                //System.out.println("Double Clicked: " + x + ", " + y);
                if (true == clickingTool.doubleClicked(
                    adjustPoint(evt.getPoint()), 
                    colorPanelBean1.getPanelColor()
                )) {
                    // overlay is modified.
                    // enable the clear button
                    if (toolID != CLICKING_POLYLINE) {
                        btnClear.setEnabled(true);
                    }
                    
                    // dispose clickingTool
                    clickingTool = null;
                }
            } else if (evt.getClickCount() == 1) {
                //System.out.println("Clicked: " + x + ", " + y);
                if (true == clickingTool.clicked(
                    adjustPoint(evt.getPoint()), 
                    colorPanelBean1.getPanelColor()
                )) {
                    // overlay is modified.
                    // enable the clear button
                    btnClear.setEnabled(true);

                    // dispose clickingTool
                    clickingTool = null;
                }
            }
        }
        
        // press -> release -> click
        // press -> drag -> release
        public void mousePressed(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();     
            //System.out.println("Pressed: " + x + ", " + y);
        }

        public void mouseReleased(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();
            //System.out.println("Released: " + x + ", " + y);
            
            if (dragging == true) {
                // dragging has ended
                // call tool
                tool.stopDragging(
                    adjustPoint( evt.getPoint() )
                );
                
                tool = null;
                dragging = false;
                
                // overlay is modified.
                // enable the clear button
                btnClear.setEnabled(true);
            }
        }

        public void mouseMoved(MouseEvent evt) {
            x = evt.getX();
            y = evt.getY();
            //System.out.println("Moved: " + x + ", " + y);
        }
        
        public void mouseDragged(MouseEvent evt) {
            int lastx = x;
            int lasty = y;
            
            x = evt.getX();
            y = evt.getY();
            //System.out.println("Dragged: " + x + ", " + y);
            
            //===================================================
            if (isClickingTool()) {
                return;
            }
            if (clickingTool != null) {
                return;
            }
            //===================================================
            
            // call dragging tool
            if (dragging == false) {
                // dragging can start only within the image area
                if (isMouseInsideImage(x, y) == false) {
                    //System.out.println("Can't use drag tool outside of the image");
                    return;
                }
                // when the user draggs the mouse outside of the image and 
                // continues dragging inside, we can initiate tool here...
                // of course, dragging from within the image can:-)
                
                // initiate dragging tool
                dragging = true;
                
                if (tool == null) {
                    tool = createTool(toolID);
                    tool.setPenSize(selectedPenSize);
                }
                
                // start dragging from this point...but adjustment of location is required.
                
                tool.startDragging(
                    adjustPoint( evt.getPoint() ), 
                    colorPanelBean1.getPanelColor()
                );
            } else {
                // while using the tool by dragging the mouse...
                
                // in this case, I don't care about the location of mouse 
                // whether it is inside or outside of the image...
                tool.whileDragging(
                    adjustPoint( new Point(lastx, lasty) ),
                    adjustPoint( evt.getPoint() )
                );
            }
        }
    }
    
    DraggingToolMouseHandler draggingToolHandler = new DraggingToolMouseHandler();

    //--------------------------------------------------------------------------
    class MyCellRenderer extends JLabel implements ListCellRenderer {
        Icon[] icons = null;
        int selectedItem;

        public int getSelectedItem() {
           return selectedItem;
        }
       
        public MyCellRenderer(Icon[] icons) {
            this.icons = icons;
            selectedItem = 1;

            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(  JList list,
                                                           Object value,
                                                           int index,
                                                           boolean isSelected,
                                                           boolean hasFocus ) {
            // if the cell has text, write it
            if (value != null) {
                String txt =value.toString();
                setText(txt);
            } else {
                setText(String.valueOf(index));
            }

            // check index to render
            if ((index != -1) && (index <icons.length)) {
                // draw selected cell
                setIcon(icons[index]);
            } else {
                setIcon(icons[lastSelected]);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                selectedItem = index;
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }  
    }

    MyCellRenderer renderer;
    private int lastSelected = 1;
    Schema saveSchema;
    boolean editMode = true;
    
    /** Creates new form SchemaEditorDialog */
    //public SchemaEditorDialog(java.awt.Frame parent, boolean modal, Image img) {
    public SchemaEditorDialog(java.awt.Frame parent, boolean modal, Schema schema, boolean b) {
        super(parent, modal);
        
        saveSchema = schema;

        Image img = schema.getIcon().getImage();
        initComponents();
        
        // penSize
        Icon[] icons ={
            new ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/pen1.gif")),
            new ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/pen2.gif")),
            new ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/pen4.gif")),
            new ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/pen8.gif"))
        };

        renderer = new MyCellRenderer(icons);
        cmbPenSize.setRenderer(renderer);
        cmbPenSize.setSelectedIndex(2);

        clickedOk = false;

        try {
           MediaTracker tracker = new MediaTracker(rootPanel);
           tracker.addImage(img,0);
           tracker.waitForID(0);
        } catch (InterruptedException ie) {
           ie.printStackTrace();
        }

        // get image size 
        int[] size = overlayImagePanelBean1.calcImageSize(img);

        // note that if the loading image data is not completed,
        // image size becomes {-1, -1}.
        // you should wait for loading before getting the size
        if (size[0] == -1 || size[1] == -1) {
            System.out.println("Image is not loaded completely.");
        }     
        //System.out.println(size[0] + "," + size[1]);
        overlayImagePanelBean1.setPreferredSize(new Dimension(size[0],size[1]));
        overlayImagePanelBean1.setSrcImg(img);

        if (img != null) {
            btnOk.setEnabled(true);
            btnSave.setEnabled(true);
        }

        overlayImagePanelBean1.addMouseListener(draggingToolHandler);
        overlayImagePanelBean1.addMouseMotionListener(draggingToolHandler);

        //pack();
        // silly me!
        // if the design of the dialog is changed, the following code should be modified.
        int www = size[0];
        int hhh = size[1];
        if (www < 300) {
            www = 300;
        }
        if (hhh < 300) {
            hhh = 300;
        }
        ////////////////////////////////////////////////
        if (www > 512) {
            www = 512;
        }
        if (hhh > 512) {
            hhh = 512;
        }
        ///////////////////////////////////////////////

        this.setSize(
            jToolBar1.getSize().width + 32,//www + 32, 
            hhh + pnlCommand.getSize().height + jToolBar1.getSize().height + 40
        );

        overlayImagePanelBean1.setSize(size[0], size[1]);
        overlayImagePanelBean1.createOverlay();
        
        //---------------------------------------------------------------------------------------------------
        // get editMode...
        // if the mode is true, the image should be editted, otherwise the editor is used as an image viewer.
        editMode = b;        
        
        setupComboBox();
        
        if (saveSchema.getModel() != null) {
            if (saveSchema.getModel() instanceof open.dolphin.infomodel.ExtRef) {
                ExtRef er = (ExtRef)saveSchema.getModel();
                //
                //System.out.println("TITLE: " + er.getTitle());
                if (er.getTitle() != null) {
                    setExtRefTitle(er.getTitle());
                }
                //System.out.println("MEDICALROLE: " + er.getMedicalRole());
                if (er.getMedicalRole() != null) {
                    setExtRefMedicalRole(er.getMedicalRole());
                }
            }
        }
        
        //----------------------------------------------------------------------
        // When the editMode is false, the editor is used as an image viewer.
        // Components for editting the image is disabled.
        if (editMode == false) {
            jToolBar1.setVisible(false);
            extRefTitle.setEditable(false);
            if (extRefCombo != null) {
                extRefCombo.setEditable(false);
                extRefCombo.setEnabled(false);
                btnOk.setEnabled(false);
                btnCancel.setText("•Â‚¶‚é");
            }
        }
        //----------------------------------------------------------------------
    }

    JComboBox extRefCombo = null;
    Hashtable ht = null;
    Hashtable inv = null;
    String medicalRole = null;
    public String getExtRefTitle() {
        return extRefTitle.getText();
    }
    public void setExtRefTitle(String title) {
        extRefTitle.setText(title);
    }
    public String getExtRefMedicalRole() {
        return medicalRole;
    }
    public void setExtRefMedicalRole(String role) {
        if (role == null) return;
        
        if (extRefCombo == null || ht == null || inv == null) {
            setupComboBox();
        }
        if (ht.containsKey(role)) {
            //if (inv.containsKey(ht.get(role))) {
                extRefCombo.setSelectedItem(ht.get(role));
            //}
        }
    }
    
    public void setupComboBox() {
        // load MML0033 table
        ht = CSVParser.parseAsHashtableFromStream(
            this.getClass().getResourceAsStream(
                "/jp/ac/kumamoto_u/kuh/fc/jsato/mml_table/MML0033.csv"
            )
        );
        inv = CSVParser.getInverseTable(ht);
        
        Vector v = CSVParser.parseAsValueVectorFromStream(
            this.getClass().getResourceAsStream(
                "/jp/ac/kumamoto_u/kuh/fc/jsato/mml_table/MML0033.csv"
            )
        );        
        // drop first item
        v.removeElementAt(0);
        
        // create combo
        extRefCombo = new JComboBox(v);
        extRefCombo.setSelectedIndex(24);
        if (inv.containsKey(v.elementAt(24))) {
            medicalRole = (String)inv.get(v.elementAt(24));
            //System.out.println("Default: " + medicalRole);
        }
        
        extRefCombo.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource().equals(extRefCombo)) {
                        //System.out.println(
                        //    "SELECTED: " + extRefCombo.getSelectedItem()
                        //);
                        
                        if (inv.containsKey(extRefCombo.getSelectedItem())) {
                            //System.out.println(
                            //   "RESULT: " + inv.get(extRefCombo.getSelectedItem())
                            //);
                            medicalRole = (String)inv.get(extRefCombo.getSelectedItem());
                        }
                    }
                }
            }
        );        
        // add combo to the panel
        extRefPanel.add(extRefCombo);
    }
    
    public Image getEdittedImage() {
        return overlayImagePanelBean1.getSrcImg();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        toolGroup = new javax.swing.ButtonGroup();
        rootPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnFreeform = new javax.swing.JToggleButton();
        btnLine = new javax.swing.JToggleButton();
        btnPolyLine = new javax.swing.JToggleButton();
        btnOval = new javax.swing.JToggleButton();
        btnRect = new javax.swing.JToggleButton();
        btnPoly = new javax.swing.JToggleButton();
        btnFillOval = new javax.swing.JToggleButton();
        btnFillRect = new javax.swing.JToggleButton();
        btnFillPoly = new javax.swing.JToggleButton();
        btnText = new javax.swing.JToggleButton();
        colorPanelBean1 = new jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean();
        cmbPenSize = new javax.swing.JComboBox();
        btnClear = new javax.swing.JButton();
        pnlEditor = new javax.swing.JPanel();
        scrlView = new javax.swing.JScrollPane();
        overlayImagePanelBean1 = new jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean();
        extRefPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        extRefTitle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pnlCommand = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("\u30b7\u30a7\u30fc\u30de \u30a8\u30c7\u30a3\u30bf");
        setModal(true);
        setName("dlgSchemaEditor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        rootPanel.setLayout(new javax.swing.BoxLayout(rootPanel, javax.swing.BoxLayout.Y_AXIS));

        rootPanel.setMinimumSize(new java.awt.Dimension(600, 123));
        rootPanel.setPreferredSize(new java.awt.Dimension(600, 500));
        jToolBar1.setFloatable(false);
        jToolBar1.setMaximumSize(new java.awt.Dimension(600, 36));
        jToolBar1.setMinimumSize(new java.awt.Dimension(600, 36));
        btnFreeform.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FreeformOff.gif")));
        btnFreeform.setSelected(true);
        toolGroup.add(btnFreeform);
        btnFreeform.setActionCommand("btnFreeform");
        btnFreeform.setMaximumSize(new java.awt.Dimension(32, 32));
        btnFreeform.setMinimumSize(new java.awt.Dimension(32, 32));
        btnFreeform.setPreferredSize(new java.awt.Dimension(32, 32));
        btnFreeform.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FreeformOn.gif")));
        btnFreeform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFreeformActionPerformed(evt);
            }
        });

        jToolBar1.add(btnFreeform);

        btnLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/LineOff.gif")));
        toolGroup.add(btnLine);
        btnLine.setActionCommand("btnLine");
        btnLine.setMaximumSize(new java.awt.Dimension(32, 32));
        btnLine.setMinimumSize(new java.awt.Dimension(32, 32));
        btnLine.setPreferredSize(new java.awt.Dimension(32, 32));
        btnLine.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/LineOn.gif")));
        btnLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLineActionPerformed(evt);
            }
        });

        jToolBar1.add(btnLine);

        btnPolyLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/PolyLineOff.gif")));
        toolGroup.add(btnPolyLine);
        btnPolyLine.setActionCommand("btnPolyLine");
        btnPolyLine.setMaximumSize(new java.awt.Dimension(32, 32));
        btnPolyLine.setMinimumSize(new java.awt.Dimension(32, 32));
        btnPolyLine.setPreferredSize(new java.awt.Dimension(32, 32));
        btnPolyLine.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/PolyLineOn.gif")));
        btnPolyLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPolyLineActionPerformed(evt);
            }
        });

        jToolBar1.add(btnPolyLine);

        btnOval.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/OvalOff.gif")));
        toolGroup.add(btnOval);
        btnOval.setActionCommand("btnOval");
        btnOval.setMaximumSize(new java.awt.Dimension(32, 32));
        btnOval.setMinimumSize(new java.awt.Dimension(32, 32));
        btnOval.setPreferredSize(new java.awt.Dimension(32, 32));
        btnOval.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/OvalOn.gif")));
        btnOval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOvalActionPerformed(evt);
            }
        });

        jToolBar1.add(btnOval);

        btnRect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/RectOff.gif")));
        toolGroup.add(btnRect);
        btnRect.setActionCommand("btnRect");
        btnRect.setMaximumSize(new java.awt.Dimension(32, 32));
        btnRect.setMinimumSize(new java.awt.Dimension(32, 32));
        btnRect.setPreferredSize(new java.awt.Dimension(32, 32));
        btnRect.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/RectOn.gif")));
        btnRect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRectActionPerformed(evt);
            }
        });

        jToolBar1.add(btnRect);

        btnPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/PolyOff.gif")));
        toolGroup.add(btnPoly);
        btnPoly.setActionCommand("btnPoly");
        btnPoly.setMaximumSize(new java.awt.Dimension(32, 32));
        btnPoly.setMinimumSize(new java.awt.Dimension(32, 32));
        btnPoly.setPreferredSize(new java.awt.Dimension(32, 32));
        btnPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/PolyOn.gif")));
        btnPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPolyActionPerformed(evt);
            }
        });

        jToolBar1.add(btnPoly);

        btnFillOval.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillOvalOff.gif")));
        toolGroup.add(btnFillOval);
        btnFillOval.setActionCommand("btnFillOval");
        btnFillOval.setMaximumSize(new java.awt.Dimension(32, 32));
        btnFillOval.setMinimumSize(new java.awt.Dimension(32, 32));
        btnFillOval.setPreferredSize(new java.awt.Dimension(32, 32));
        btnFillOval.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillOvalOn.gif")));
        btnFillOval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFillOvalActionPerformed(evt);
            }
        });

        jToolBar1.add(btnFillOval);

        btnFillRect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillRectOff.gif")));
        toolGroup.add(btnFillRect);
        btnFillRect.setActionCommand("btnFillRect");
        btnFillRect.setMaximumSize(new java.awt.Dimension(32, 32));
        btnFillRect.setMinimumSize(new java.awt.Dimension(32, 32));
        btnFillRect.setPreferredSize(new java.awt.Dimension(32, 32));
        btnFillRect.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillRectOn.gif")));
        btnFillRect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFillRectActionPerformed(evt);
            }
        });

        jToolBar1.add(btnFillRect);

        btnFillPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillPolyOff.gif")));
        toolGroup.add(btnFillPoly);
        btnFillPoly.setActionCommand("btnFillPoly");
        btnFillPoly.setMaximumSize(new java.awt.Dimension(32, 32));
        btnFillPoly.setMinimumSize(new java.awt.Dimension(32, 32));
        btnFillPoly.setPreferredSize(new java.awt.Dimension(32, 32));
        btnFillPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/FillPolyOn.gif")));
        btnFillPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFillPolyActionPerformed(evt);
            }
        });

        jToolBar1.add(btnFillPoly);

        btnText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/TextOff.gif")));
        toolGroup.add(btnText);
        btnText.setActionCommand("btnText");
        btnText.setMaximumSize(new java.awt.Dimension(32, 32));
        btnText.setMinimumSize(new java.awt.Dimension(32, 32));
        btnText.setPreferredSize(new java.awt.Dimension(32, 32));
        btnText.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ToolIcons/TextOn.gif")));
        btnText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTextActionPerformed(evt);
            }
        });

        jToolBar1.add(btnText);

        colorPanelBean1.setMaximumSize(new java.awt.Dimension(32, 32));
        jToolBar1.add(colorPanelBean1);

        cmbPenSize.setMaximumRowCount(4);
        cmbPenSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "", "", "" }));
        cmbPenSize.setMaximumSize(new java.awt.Dimension(80, 32));
        cmbPenSize.setPreferredSize(new java.awt.Dimension(90, 27));
        cmbPenSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPenSizeActionPerformed(evt);
            }
        });

        jToolBar1.add(cmbPenSize);

        btnClear.setText("\u3084\u308a\u76f4\u3057");
        btnClear.setMaximumSize(new java.awt.Dimension(80, 26));
        btnClear.setMinimumSize(new java.awt.Dimension(80, 26));
        btnClear.setPreferredSize(new java.awt.Dimension(90, 26));
        btnClear.setEnabled(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jToolBar1.add(btnClear);

        rootPanel.add(jToolBar1);

        pnlEditor.setLayout(new javax.swing.BoxLayout(pnlEditor, javax.swing.BoxLayout.X_AXIS));

        pnlEditor.setMinimumSize(new java.awt.Dimension(600, 22));
        pnlEditor.setPreferredSize(new java.awt.Dimension(600, 465));
        scrlView.setDoubleBuffered(true);
        scrlView.setPreferredSize(new java.awt.Dimension(425, 465));
        scrlView.setViewportView(overlayImagePanelBean1);

        pnlEditor.add(scrlView);

        rootPanel.add(pnlEditor);

        extRefPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        extRefPanel.setMinimumSize(new java.awt.Dimension(600, 30));
        extRefPanel.setPreferredSize(new java.awt.Dimension(600, 30));
        jLabel1.setText("\u30bf\u30a4\u30c8\u30eb: ");
        extRefPanel.add(jLabel1);

        extRefTitle.setMinimumSize(new java.awt.Dimension(320, 20));
        extRefTitle.setPreferredSize(new java.awt.Dimension(320, 20));
        extRefPanel.add(extRefTitle);

        jLabel2.setText("\u7528\u9014: ");
        extRefPanel.add(jLabel2);

        rootPanel.add(extRefPanel);

        pnlCommand.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        pnlCommand.setMaximumSize(new java.awt.Dimension(32767, 35));
        pnlCommand.setMinimumSize(new java.awt.Dimension(600, 35));
        pnlCommand.setPreferredSize(new java.awt.Dimension(600, 35));
        btnSave.setText("JPEG\u5f62\u5f0f\u3067\u4fdd\u5b58...");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        pnlCommand.add(btnSave);

        btnOk.setText("\u30ab\u30eb\u30c6\u306b\u5c55\u958b");
        btnOk.setEnabled(false);
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        pnlCommand.add(btnOk);

        btnCancel.setText("\u53d6\u6d88\u3057");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        pnlCommand.add(btnCancel);

        rootPanel.add(pnlCommand);

        getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void changeTool(int currID, int newID) {
        if (currID == newID) {
            return;
        }
        
        if (isClickingTool()) {
            if (clickingTool != null) {
                clickingTool.interruptClickingTool();
            }
            clickingTool = null;
        }

        toolID = newID;
    }
    
    private void btnPolyLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPolyLineActionPerformed
        // Add your handling code here:
        changeTool(toolID, CLICKING_POLYLINE);

        btnClear.setEnabled(false);
    }//GEN-LAST:event_btnPolyLineActionPerformed

    private void btnTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTextActionPerformed
        // Add your handling code here:
        changeTool(toolID, CLICKING_TEXT);
    }//GEN-LAST:event_btnTextActionPerformed

    private void btnPolyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPolyActionPerformed
        // Add your handling code here:
        changeTool(toolID, POLY);
    }//GEN-LAST:event_btnPolyActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Add your handling code here:
        overlayImagePanelBean1.storeOverlay();//////////////
        
        // export the image to the file
        JFileChooser chooser = new JFileChooser();
        int selected = chooser.showSaveDialog(this);        
        if (selected == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            String format = "jpeg";
            Image image = overlayImagePanelBean1.getSrcImg();
            Dimension d = overlayImagePanelBean1.getSrcImgSize();
            // create buffered image 
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, this);
            
            // save buffered image to file
            try {
                //System.out.println("JPEGImageEncoder: " + path);
                FileOutputStream dest = new FileOutputStream(path);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(dest);
                encoder.encode(bf);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            //////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////
            // DEBUG
            /*
            String[] names = ImageIO.getReaderFormatNames();
            for (int k = 0; k < names.length; ++k) {
                System.out.println("writing " + path+"."+names[k]);
                File f = new File(path+"."+names[k]);
                try {
                    ImageIO.write(bf, names[k], f);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
             */
            //////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////
            
            // convert awt image to jai image
            //PlanarImage pi = JAI.create("awtimage", image);
            //if (pi == null) return;
            //System.out.println("PlanarImage is created.");
            
            // wrong :(
            //JAI.create("filestore", pi.getAsBufferedImage(),  path, format);///////////////////////
            
        } else if (selected == JFileChooser.CANCEL_OPTION) {
            //System.out.println("Cancel");
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    float selectedPenSize = 2.0f;
    private void cmbPenSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPenSizeActionPerformed
        // Add your handling code here:
        if (renderer == null) return;
        lastSelected = renderer.getSelectedItem();
        
        // change penSize of the tool
        switch (lastSelected) {
            case 0: selectedPenSize = 1.0f; break;
            case 1: selectedPenSize = 2.0f; break;
            case 2: selectedPenSize = 4.0f; break;
            case 3: selectedPenSize = 8.0f; break;
        }  
        // selectedPenSize is pass to the tool when it is created by dragging
    }//GEN-LAST:event_cmbPenSizeActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // Add your handling code here:
        overlayImagePanelBean1.removeOverlay();
        
        // disable the clear button by myself :-)
        btnClear.setEnabled(false);
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnFillRectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFillRectActionPerformed
        // Add your handling code here:
        changeTool(toolID, FILLRECT);
    }//GEN-LAST:event_btnFillRectActionPerformed

    private void btnFillOvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFillOvalActionPerformed
        // Add your handling code here:
        changeTool(toolID, FILLOVAL);
    }//GEN-LAST:event_btnFillOvalActionPerformed

    private void btnFillPolyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFillPolyActionPerformed
        // Add your handling code here:
        changeTool(toolID, FILLPOLY);
    }//GEN-LAST:event_btnFillPolyActionPerformed

    private void btnRectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRectActionPerformed
        // Add your handling code here:
        changeTool(toolID, RECT);
    }//GEN-LAST:event_btnRectActionPerformed

    private void btnOvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOvalActionPerformed
        // Add your handling code here:
        changeTool(toolID, OVAL);
    }//GEN-LAST:event_btnOvalActionPerformed

    private void btnLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLineActionPerformed
        // Add your handling code here:
        changeTool(toolID, LINE);
    }//GEN-LAST:event_btnLineActionPerformed

    private void btnFreeformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFreeformActionPerformed
        // Add your handling code here:
        changeTool(toolID, FREEFORM);
    }//GEN-LAST:event_btnFreeformActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Add your handling code here:
        
        // the user wants to close the dialog,
        // do the same tasks of the cancel button
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // Add your handling code here:
        // let this broadcast clickedOk property change
        setClickedOk(false);
        //setVisible(false);
        //dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        // Add your handling code here:
        
        overlayImagePanelBean1.storeOverlay();//////////////
        
        // let this broadcast clickedOk property change
        setClickedOk(true);
        //setVisible(false);
        //dispose();  
    }//GEN-LAST:event_btnOkActionPerformed

    /** Closes the dialog */
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        //Image img = Toolkit.getDefaultToolkit().getImage("c:\\photo.jpg");
        Image img = Toolkit.getDefaultToolkit().getImage(
            Toolkit.class.getResource("/open/dolphin/resources/schema/Img06.JPG")
        );
        Schema s = new Schema();
        s.setIcon(new ImageIcon(img));
        new SchemaEditorDialog(new javax.swing.JFrame(), true, s, true).setVisible(true);
    }

    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }    

    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /** Getter for property clickedOk.
     * @return Value of property clickedOk.
     */
    public boolean isClickedOk() {
        return clickedOk;
    }
    
    /** Setter for property dlgDone.
     * @param dlgDone New value of property dlgDone.
     */
    public void setClickedOk(boolean clickedOk) {
        boolean oldClickedOk = this.clickedOk;
        this.clickedOk = clickedOk;
        // because the event is not caught when the user canceled the dialog,
        // we pass 'this' instead of actual old value
        //propertyChangeSupport.firePropertyChange("clickedOk", 
            //this,//new Boolean(oldClickedOk)
            //new Boolean(clickedOk)
        //);
        if (clickedOk) {
            Image newImage = getEdittedImage();
            saveSchema.setIcon(new ImageIcon(newImage));

            //System.out.println("OK?: " + clickedOk);
            //System.out.println("TITLE: " + getExtRefTitle());
            //System.out.println("MEDICALROLE: " + getExtRefMedicalRole());
            ExtRef er = new ExtRef();
            er.setContentType("image/jpeg");
            er.setMedicalRole(getExtRefMedicalRole());
            er.setTitle(getExtRefTitle());
            er.setHref(saveSchema.getFileName());
            saveSchema.setModel(er);
            
            propertyChangeSupport.firePropertyChange("imageProp", null, saveSchema);
        }
               
        setVisible(false);
        dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnOval;
    private javax.swing.JComboBox cmbPenSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JButton btnClear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToggleButton btnFillPoly;
    private javax.swing.JButton btnSave;
    private jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean colorPanelBean1;
    private javax.swing.JPanel extRefPanel;
    private javax.swing.JToggleButton btnLine;
    private javax.swing.JScrollPane scrlView;
    private javax.swing.JPanel pnlCommand;
    private javax.swing.JToolBar jToolBar1;
    private jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean overlayImagePanelBean1;
    private javax.swing.JToggleButton btnFillOval;
    private javax.swing.ButtonGroup toolGroup;
    private javax.swing.JToggleButton btnFillRect;
    private javax.swing.JToggleButton btnPoly;
    private javax.swing.JToggleButton btnFreeform;
    private javax.swing.JToggleButton btnText;
    private javax.swing.JToggleButton btnRect;
    private javax.swing.JButton btnOk;
    private javax.swing.JToggleButton btnPolyLine;
    private javax.swing.JButton btnCancel;
    private javax.swing.JTextField extRefTitle;
    private javax.swing.JPanel pnlEditor;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property dlgDone. */
    private boolean clickedOk;    

    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
}
