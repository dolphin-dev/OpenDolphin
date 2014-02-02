/*
 * SchemStockTable.java
 *
 * Created on 2002/06/20, 8:03
 */
package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import swingworker.*;

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.net.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import netscape.ldap.*;

import java.io.*;
import java.text.*;

import java.io.*;
import javax.media.jai.*;
import java.awt.image.*;
import java.awt.image.renderable.*;

public class SchemaStockTableLDAP extends JPanel implements DragSourceListener, DragGestureListener {
      
    private LDAPConnection conn = null;
    private int IMAGE_POSITION = 6;
    private String WHITE_DN = "white.jpg";
    //private JLabel debugLabel = new JLabel();
    
    //====================================================================================
    public synchronized LDAPConnection getConnection() {
        if (conn != null && conn.isConnected()) {
            return conn;
        }
        
        try {
            conn = new LDAPConnection();
            conn.connect("133.95.88.222", 389, "cn=Directory Manager,o=digital-globe","secret");
            //conn.connect("localhost", 389, "cn=Directory Manager,o=digital-globe","secret");
            //conn.connect("hakkoda.digital-globe.co.jp", 389, "cn=Manager,o=digital-globe","hanagui");
            //conn.connect("hakkoda.digital-globe.co.jp", 389, "cn=Manager,o=digital-globe","hanagui+");
            return conn;
        } catch (LDAPException e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    public synchronized void disconnectLDAP() {
        if (conn != null && conn.isConnected()) {
            try {
                conn.disconnect();
                conn = null;
            } catch (LDAPException e) {
                System.out.println(e.toString());
            }
        }
    }
    //====================================================================================
    // check the entry dn to see if it has any entries in it...
    public boolean hasChildren(String dn) {
        // get connection
        LDAPConnection ld = getConnection();
        if (ld == null) {
            System.out.println("*** Couldn't search DN. Connection is null.");
            return false;
        }
        
        String[] attrs = null;
        LDAPSearchResults results = null;
        try {
            results = ld.search(
                dn, 
                netscape.ldap.LDAPConnection.SCOPE_ONE,
                "(objectclass=*)",
                attrs, 
                false );
        } catch (LDAPException e) {
            disconnectLDAP();
            return false;
        }
        
        if (results.hasMoreElements() == false) {
            disconnectLDAP();
            return false;
        }
        
        disconnectLDAP();
        return true;
    }
    
    //JTable aTable = null;
    JLabel infoLabel = null;
    private static final int ROW_HEIGHT = 96;
    JScrollPane imageView = null;
    private JTable imageTable;
    private ImageTableModel model;
    private static final String [] columnNames = {"", ""};
    private DragSource dragSource;

    // get the number of entries under the entry
    public int countLeaves(String dn) {
        // get connection
        LDAPConnection ld = getConnection();
        if (ld == null) {
            System.out.println("*** Couldn't search DN. Connection is null.");
            return 0;
        }
        
        //----------------------------------------------------
        //String[] attrs = {"uid","cn","sn","description"};//null;
        String[] attrs = {"uid"};// it's assumed a leaf has "uid" as a RDN
        //----------------------------------------------------
        LDAPSearchResults results = null;
        try {
            results = ld.search(
                dn, 
                netscape.ldap.LDAPConnection.SCOPE_ONE, 
                "(objectclass=*)", 
                attrs, 
                false );
        } catch (LDAPException e) {
            disconnectLDAP();
            return 0;
        }
        
        if ( results.hasMoreElements() == false ) {
            disconnectLDAP();
            return 0;
        }
        
        LDAPEntry entry = null;
        String baseDN = null;
        int cnt = 0;
        while (results.hasMoreElements()) { 
            try {
                entry = (LDAPEntry)results.next();
                baseDN = entry.getDN();
                if (false == hasChildren(baseDN)) {
                    ++cnt;
                }
            } catch (LDAPException e) {
                disconnectLDAP();
                return 0;
            }
        }      

        disconnectLDAP();
        return cnt;
    }
    
    // 
    public void readLeaves(String dn) {
        // count the number of leaves in the directory: dn
        int cnt = countLeaves(dn);
//      System.out.println("readLeaves(): Number of leaves: " + cnt);
        task.setMessage(cnt + "個のシェーマ情報を取得中...");
        try {Thread.sleep(100);} catch (Exception e){}
        
        // get connection
        LDAPConnection ld = getConnection();
        if (ld == null) {
            task.setMessage("サーバーへの接続に失敗...");
            try {Thread.sleep(100);} catch (Exception e){}
            //System.out.println("*** Couldn't search DN. Connection is null.");
            return;
        }
        
        //-------------------------------------------------------------
        String[] attrs = {"uid","cn","sn","description","userPassword"/*"jpegPhoto"*/};
        //-------------------------------------------------------------
        LDAPSearchResults results = null;
        try {
            results = ld.search(
                dn,
                netscape.ldap.LDAPConnection.SCOPE_ONE, 
                "(objectclass=*)", 
                attrs, 
                false );
        } catch (LDAPException e) {
            //System.out.println("*** Directory was not found.");
            disconnectLDAP();
            return;
        }
        
        if (results.hasMoreElements() == false) {
            //System.out.println("*** Directory was not found.");
            disconnectLDAP();
            return;
        }
        
        //===========================================================================
        String[] colNames = {"DN", "項目名", "登録者", "コメント", "サムネイル画像"};
        Object[][] data = new Object[cnt][5];
        //===========================================================================
        
        LDAPEntry entry = null;
        String baseDN = null;
        int k = 0;
        while (results.hasMoreElements()) {
            try {
                entry = (LDAPEntry)results.next();
                baseDN = entry.getDN();
                if (false == hasChildren(baseDN)) {                    
                    LDAPAttributeSet findAttrs = entry.getAttributeSet();
                    Enumeration enumAttrs = findAttrs.getAttributes();
                    while (enumAttrs.hasMoreElements()) {
                        LDAPAttribute anAttr = (LDAPAttribute)enumAttrs.nextElement();
                        String attrName = anAttr.getName();

                        if (attrName.equals("userPassword"/*"jpegPhoto"*/)) {
                            byte[] imageData = null;
                            Enumeration enumVals = anAttr.getByteValues();
                            if (enumVals != null) {
                                while ( enumVals.hasMoreElements() ) {
                                    imageData = (byte[])enumVals.nextElement();
                                    if (imageData != null) {
                                        break;
                                    }
                                }
                            }

                            data[k][4] = null;
                            if (imageData != null) {
                                //System.out.println(imageData.length);
                                Image img = Toolkit.getDefaultToolkit().createImage(imageData);
                                /*
                                if (img != null) {
                                    System.out.println("Loaded Image: " + img.getWidth(this) + ", " + img.getHeight(this) );
                                }
                                data[k][4] = img;
                                 */
                                data[k][4] = new ImageIcon(img);
                            }
                            
                            /********************************************
                            // Maintenance
                            System.out.println("adding thumbnail...");
                            getConnection().modify(
                                baseDN,
                                new LDAPModification(
                                    LDAPModification.REPLACE,
                                    new LDAPAttribute("userPassword", 
                                        SchemaUtil.convertToJpegData(
                                            SchemaUtil.scaleImage((ImageIcon)data[k][4], 96),
                                            SchemaStockTableLDAP.this
                                        )
                                     )
                                )
                             );
                            ********************************************/
                        } else {

                           // get the first value of the attribute
                           Enumeration enumVals = anAttr.getStringValues();
                           String aVal = "";
                           if (enumVals != null) {
                              if (enumVals.hasMoreElements()) {
                                aVal = (String)enumVals.nextElement();
                              }
                           }

                           if (attrName.equals("uid")) {
                               data[k][0] = baseDN;//aVal;
                           } else if (attrName.equals("cn")) {
                               data[k][1] = aVal;
                           } else if (attrName.equals("sn")) {
                               data[k][2] = aVal;
                           } else if (attrName.equals("description")) {
                               data[k][3] = aVal;
                           }
                        }
                    }
                    
                    ++k;
                }
            } catch (LDAPException e) {
                disconnectLDAP();
                return;
            }
        }
        
        //===========================
        /*
        if (aTable != null) {
            aTable.removeAll();
        }
        aTable = new JTable(data, colNames);
        aTable.setEnabled(true);
        //jScrollPane3.setViewportView(aTable);
        */
        
        /*
        aTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int[] selectedRow = aTable.getSelectedRows();
                    String selectedDN = "";
                    if (selectedRow.length > 0) {
                        selectedDN = (String)aTable.getValueAt(selectedRow[0], 0);/////
                    }

                    System.out.println("calling readContents from valueChanged of the table.");
                    System.out.println(selectedDN);
                    //readContents(selectedDN);
                }
            }
        });
         */
        //===========================
        
        int num = k;
        if (num <= 0) return;
        
        int nEven = (int)num/2;
        
        int nRow = nEven;
        if (num%2 == 1) {
            nRow++;
        }
        
        Object[][] vectors = new Object[nRow][2];
        for (int i = 0; i < nEven; ++i) {
            vectors[i][0] = createImageVector(
                                ((ImageIcon)data[2 * i][4]).getImage(),
                                (String)data[2*i][1],
                                (String)data[2*i][2],
                                (String)data[2*i][3],
                                (String)data[2*i][0]);
            vectors[i][1] = createImageVector(
                                ((ImageIcon)data[2 * i + 1][4]).getImage(),
                                (String)data[2*i+1][1],
                                (String)data[2*i+1][2],
                                (String)data[2*i+1][3],
                                (String)data[2*i+1][0]);
        }
        if (num%2 == 1) {
            vectors[nRow-1][0] = createImageVector(
                                ((ImageIcon)data[k-1][4]).getImage(), 
                                (String)data[k-1][1], 
                                (String)data[k-1][2], 
                                (String)data[k-1][3],
                                (String)data[k-1][0]);
            vectors[nRow-1][1] = createVector(
                                getClass().getResource("/open/dolphin/resources/images/white.jpg"),
                                WHITE_DN,
                                "ドルフィン",
                                "白紙",
                                WHITE_DN);
        }
        
        // Create table
        model = new ImageTableModel(columnNames, 0);
        imageTable = new JTable(model);
        imageTable.setRowHeight(ROW_HEIGHT);
        imageTable.setCellSelectionEnabled(true);

        /*
        MouseListener action = (MouseListener)(GenericListener.create(
                                    MouseListener.class,
                                    "mouseClicked",
                                    this,
                                    "doMouseClick"));    
        imageTable.addMouseListener(action);
         */
        
        /*
        imageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int[] selectedRow = imageTable.getSelectedRows();
                    int[] selectedCol = imageTable.getSelectedColumns();
                    System.out.println("Selected {Row, Col} = {" + selectedRow[0] + ", " + selectedCol[0] + "}");

                }
            }
        });
         */

        // change the cell renderer to my own
        imageTable.setDefaultRenderer(model.getColumnClass(0), new MyCellRenderer());
        imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        /*
        if (imageView == null) {
            imageView = new JScrollPane();
        }
        imageView.setPreferredSize(new Dimension(360,480));
        this.add(imageView);
         */
        
        imageView.setViewportView(imageTable);
        //
        for (int i=0; i < vectors.length; ++i) {
            model.addRow(vectors[i]);
        }
        //
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(imageTable, DnDConstants.ACTION_COPY_OR_MOVE, this);
        
        disconnectLDAP();
    }
    
    private Vector createImageVector(Image img, String itemName, String author, String comment, String dn) {
        if (img == null) return null;
        // get the resized image
        Image dstImg = SchemaUtil.scaleImage(new ImageIcon(img), ROW_HEIGHT);
        if (dstImg == null) {
            img = null;
            return null;
        }

        // pack the two images to the vector
        Vector v = new Vector();
        v.addElement(dstImg);// small image (thumbnail) [0]
        v.addElement(img);// large image (original) [1]
        
        v.addElement(itemName);// [2]
        v.addElement(author);// [3]
        v.addElement(comment);// [4]
        v.addElement(dn);// [5]
        
        return v;
    }
    
    private Vector createVector(URL url, String itemName, String author, String comment, String dn) {
        // create buffered image from the url
        PlanarImage ri = null;
        ri = JAI.create("url", url);
        if (ri == null) {
            System.out.println("Couldn't load " + url.toString());
            return null;
        }
        BufferedImage bf = null;
        try {
            bf = ri.getAsBufferedImage();
        } catch (Exception e) {
            // if unsupported file is opened, this exception (RuntimeException) is thrown.
            System.out.println("Couldn't get as buffered image: " + url.toString());
            //e.printStackTrace();
            return null;
        }
        if (bf == null) {
            System.out.println("Couldn't get as buffered image: " + url.toString());
            return null;
        }
        ImageIcon srcIcon = new ImageIcon((Image)bf);

        // create the icon
        //ImageIcon srcIcon = new ImageIcon(file);// ( this is cheap solution to support JPEG and GIF )
        
        if (srcIcon == null) return null;
        // store original path
        srcIcon.setDescription(url.getPath());
        // get the image of the icon
        Image img = srcIcon.getImage();
        Image dstImg = SchemaUtil.scaleImage(srcIcon, ROW_HEIGHT);
        if (dstImg == null) {
            img = null;
            return null;
        }

        // pack the two images to the vector
        Vector v = new Vector();
        v.addElement(dstImg);// small image (thumbnail)
        v.addElement(img);// large image (original)
        
        v.addElement(itemName);
        v.addElement(author);
        v.addElement(comment);
        v.addElement(dn);
        
        return v;
    }
    
    //private String startH = "<html><p><div align='center'><table border>" + 
    //    "<tr bgcolor='blue'><th>Attribute</th><th>Value</th></tr>";
    private String startH = "<html><p><div align='center'><table border>";
    private String endH = "</table></div></p></html>";
    
    private String makeRow(String att, String val) {
        return "<tr><td>" + att + "</td><td>" + val + "</td></tr>";
    }

    public Image getJpegPhoto(String dn) {
        // get jpegPhoto data from the LDAP entry
        LDAPConnection ld = getConnection();
        if (ld == null) {
            task.setMessage("サーバへの接続に失敗...");
            try {Thread.sleep(100);} catch (Exception e){}
            //System.out.println("*** Couldn't search DN. Connection is null.");
            return null;
        }

        //System.out.println("Loading Jpeg from: " + dn);
        //task.setMessage(dn);

        String[] attrs = {"jpegPhoto"};
        try {
            LDAPEntry entry = ld.read(dn, attrs);
            LDAPAttribute attr = entry.getAttribute(attrs[0]);
            byte[] imageData = null;
            task.setMessage("画像を取得：" + dn);
            try {Thread.sleep(100);} catch (Exception e){}
            Enumeration enumVals = attr.getByteValues();
            if (enumVals != null) {
                while ( enumVals.hasMoreElements() ) {
                    imageData = (byte[])enumVals.nextElement();
                    if (imageData != null) {
                        break;
                    }
                }
            }
            task.setMessage("");
            try {Thread.sleep(100);} catch (Exception e){}
            disconnectLDAP();
            if (imageData != null) {
                return (Image)Toolkit.getDefaultToolkit().createImage(imageData);
            } else {
                return null;
            }
        } catch (LDAPException le) {
            le.printStackTrace();
        }
        
        task.setMessage("");
        try {Thread.sleep(100);} catch (Exception e){}
        disconnectLDAP();
        return null;
    }
    
    Image loadJpeg(Vector v) {
        ///////////////////////////////////////////
        // if the length of vector is 6, the image data 
        // is not downloaded yet...
        ///////////////////////////////////////////
        Image jpeg = null;
        String dn = (String)v.elementAt(5);
        if (dn != null) {
            if (dn.equals(WHITE_DN)) {
                task.setMessage("白紙画像を読み込み中...");
                //System.out.println("Getting white.jpg from resource...");
                jpeg = new ImageIcon(getClass().getResource("/open/dolphin/resources/images/white.jpg")).getImage();
                task.setMessage("");
            } else {
                // load image data in "jpegPhoto" and store it to v by adding at the last position                
                task.setMessage("シェーマ画像を取得中...");
                //System.out.println("Getting JPEG data from LDAP...");
                jpeg = getJpegPhoto(dn);
                task.setMessage("");
            }
        }
        return jpeg;
    }
    
    javax.swing.Timer loading_timer = null;
    SchemaSTLoadTask loading_task = null;
    
    public void doLoading(Vector v) {
        loading_task = new SchemaSTLoadTask(SchemaStockTableLDAP.this, v);

        progress.setMinimum(0);
        progress.setMaximum(loading_task.getLengthOfTask());
        progress.setValue(progress.getMinimum());
        status.setText("画像を取得します...");
        
        loading_timer = new javax.swing.Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progress.setValue(loading_task.getCurrent());
                status.setText(task.getMessage());
                
                if (loading_task.done()) {
                    //Toolkit.getDefaultToolkit().beep();
                    loading_timer.stop();
                    // stop indeterminate progress bar
                    progress.setIndeterminate(false);
                    progress.setValue(progress.getMinimum());
                    
                    status.setText("");
                }
            }
        });
        
        // start indeterminate progress bar
        progress.setIndeterminate(true);
        loading_task.go();
        loading_timer.start();
    }
    
    static int lastRow = -1;
    static int lastCol = -1;
    class MyCellRenderer extends JLabel implements TableCellRenderer {

        public MyCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            // make the thumbnail image and create the icon
            Vector v = (Vector)value;
            String info = startH;
            setHorizontalTextPosition(JLabel.RIGHT);
            setVerticalTextPosition(JLabel.CENTER);

            if ( v != null && v.size() > 0 ) {
                
                if (v.firstElement() != null ) {
                    ImageIcon icon = new ImageIcon((Image)v.firstElement());
                    setIcon(icon);
                }
                setText((String)v.elementAt(2));
                
                info = info + makeRow("項目名", (String)v.elementAt(2));
                info = info + makeRow("登録者", (String)v.elementAt(3));
                info = info + makeRow("コメント", (String)v.elementAt(4));
                
                if (isSelected && hasFocus) {
                    if (table.getSelectionModel().getValueIsAdjusting() == false) {
                        if (v.size() == IMAGE_POSITION) {
                            /////////////////////////////////////////////////////////////////////////////////////
                            // set null to the vector so that 
                            // the routines followed by this line 
                            // can work correctly in background thread
                            v.addElement(null);
                            doLoading(v);

                            /*
                            v.addElement(null);
                            Image jpg = loadJpeg(v);
                            v.remove(IMAGE_POSITION);
                            v.add(IMAGE_POSITION, jpg);
                             */
                            
                            //System.out.println("JPEG was added to v.size(): " + v.size());
                        }
                    }
                }
                
            } else {
                // 'unknown' icon is assigned
                ImageIcon icon = new ImageIcon(getClass().getResource("/open/dolphin/resources/images/unknown.jpg"));
                setIcon(icon);
                setText("unknown.jpg");
                
                info = info + makeRow("項目名", "unknown.jpg");
                info = info + makeRow("登録者", "ドルフィン");
                info = info + makeRow("コメント", "不明");
                
                if (isSelected && hasFocus) {
                    if (table.getSelectionModel().getValueIsAdjusting() == false) {
                        if (v.size() == IMAGE_POSITION) {
                            ///////////////////////////////////////////
                            // if the length of vector is 6, the image data 
                            // is not set yet...
                            ///////////////////////////////////////////
                            v.addElement(icon.getImage());
                        }
                    }
                }
            }

            info = info + endH;

            if (isSelected && hasFocus) {
                if (table.getSelectionModel().getValueIsAdjusting() == false) {
                    if (lastRow != row || lastCol != column) {
                        //System.out.println("The cell {" + row + ", " + column + "} was selected.");
                        infoLabel.setText(info);
                        lastRow = row;
                        lastCol = column;
                    }
                }
            }
            
            //if (v.size() == IMAGE_POSITION + 1 && v.elementAt(IMAGE_POSITION) != null) {
                //setBackground(Color.cyan);
                //setBackground(new Color((float)0.0, (float)0.55, (float)0.85));
            //} else {
                //setBackground(Color.lightGray);
                //setBackground(new Color((float)0.85, (float)0.85, (float)0.85));
            //}
            
            return this;
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            
            //int width = getWidth();
            //int height = getHeight();
            
            /*
            g.setColor(getBackground());
            g.fillRect(0, 0, width, height);
            g.setColor(getForeground());
             */
            
            /*
            int diameter = (int)Math.min(width, height)/6;
            int row = imageTable.getSelectedRow();
            int col = imageTable.getSelectedColumn();
            if (row != -1 && col != -1) {
                // get the vector at the drag target cell
                Vector v = (Vector)imageTable.getValueAt(row, col);
                if (v.size() == IMAGE_POSITION + 1 && v.elementAt(IMAGE_POSITION) != null) {
                    g.setColor(Color.blue);
                    g.fillOval(width - diameter, height - diameter, diameter, diameter);
                }
            }
             */
            
            /*
            int diameter = Math.min(width, height);
            int reloadCounter = 3;
            if (reloadCounter < 5) {
                g.fillArc((width - diameter) / 2, (height - diameter) / 2,
                          diameter, diameter, 90, -(reloadCounter * 90));
            } else {
                g.fillArc((width - diameter) / 2, (height - diameter) / 2,
                          diameter, diameter, 90,
                          (4 - reloadCounter % 4) * 90);
            }
             */
        }
    }
    
    JProgressBar progress = new JProgressBar();
    SchemaSTLSearchTask task = null;
    javax.swing.Timer timer = null;
    JLabel status = new JLabel();
    
    public void constructTable() {
        if (dragSource != null) {
            dragSource = null;
        }
        if (imageTable != null) {
            imageView.remove(imageTable);
            imageTable = null;
        }
        if (model != null) {
            model = null;
        }
        
        SchemaStockTableLDAP.this.validate();

        if (infoLabel != null) {
            infoLabel.setText(
                startH + makeRow("項目名","") + makeRow("登録者", "") + makeRow("コメント", "") + endH
            );
        }
        
        task.setMessage("シェーマ情報を取得中...");
        readLeaves("ou=Schema,ou=Library,o=digital-globe");
        //readLeaves("ou=Schema,ou=library,o=digital-globe");
        
        task.setMessage("");

        SchemaStockTableLDAP.this.validate();
    }
    
    public void startTableTask() {
        task = new SchemaSTLSearchTask(SchemaStockTableLDAP.this);
        progress.setMinimum(0);
        progress.setMaximum(task.getLengthOfTask());
        progress.setValue(progress.getMinimum());
        status.setText("テーブルを更新しています...");
        
        timer = new javax.swing.Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progress.setValue(task.getCurrent());
                status.setText(task.getMessage());
                
                if (task.done()) {
                    //Toolkit.getDefaultToolkit().beep();
                    timer.stop();
                    // stop indeterminate progress bar
                    progress.setIndeterminate(false);
                    progress.setValue(progress.getMinimum());                   
                    //btnSearch.setEnabled(true);
                    
                    status.setText("");
                }
            }
        });

        //btnSearch.setEnabled(false);
        // start indeterminate progress bar
        progress.setIndeterminate(true);
        task.go();
        timer.start();
    }
    
    public SchemaStockTableLDAP() {
        super();
        
        status.setPreferredSize(new Dimension(180,14));
        this.add(status);

        //setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        progress.setPreferredSize(new Dimension(180,14));
        this.add(progress);
        
        // create info label
        infoLabel = new JLabel(
            startH + 
            makeRow("項目名","") + 
            makeRow("登録者", "") + 
            makeRow("コメント", "") + endH
        );
        this.add(infoLabel);
        
        if (imageView == null) {
            imageView = new JScrollPane();
        }
        imageView.setPreferredSize(new Dimension(360,480));
        this.add(imageView);

        startTableTask();
        
        /*
        JScrollPane scrl = new JScrollPane();
        scrl.setViewportView(aTable);
        scrl.setPreferredSize(new Dimension(320,512));
        this.add(scrl);
         */
        
        //--------------------------------------------
        JButton btn = new JButton();
        btn.setText("更新");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {               
                        startTableTask();
                    }
                });
            }
        });
        this.add(btn);
        //--------------------------------------------
        
        //this.add(debugLabel);
     }
    
    ////////////////  Drag Support /////////////////////

    public void dragGestureRecognized(DragGestureEvent event) {
        
        try {
            int row = imageTable.getSelectedRow();
            int col = imageTable.getSelectedColumn();
            if (row != -1 && col != -1) {

                // get the vector at the drag target cell
                Vector v = (Vector)imageTable.getValueAt(row, col);
                // get the original image
                //Image large = (Image)v.elementAt(1);//lastElement();
                
                Image jpeg = null;
                if (v.size() == IMAGE_POSITION) {
                    return;
                    /*
                    ///////////////////////////////////////////
                    // if the length of vector is 6, the image data 
                    // is not downloaded yet...
                    ///////////////////////////////////////////
                    String dn = (String)v.elementAt(5);
                    if (dn != null) {
                        if (dn.equals(WHITE_DN)) {
                            System.out.println("Getting white.jpg from resource...");
                            jpeg = new ImageIcon(getClass().getResource("/open/dolphin/resources/images/white.jpg")).getImage();
                            v.addElement(jpeg);
                        } else {
                            // load image data in "jpegPhoto" and store it to v by adding at the last position
                            System.out.println("Getting JPEG data from LDAP...");
                            jpeg = getJpegPhoto(dn);
                            v.addElement(jpeg);
                        }
                    }
                     */
                }

                if (v.size() == IMAGE_POSITION + 1 && v.elementAt(IMAGE_POSITION) != null) {
                    jpeg = (Image)v.elementAt(IMAGE_POSITION);
                    //setBackground(Color.yellow);
                }// else {
                //    setBackground(Color.blue);
                //}
                
                if (jpeg == null) {
                    //System.out.println("image data is null");
                    return;
                }
                
                //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                //if (jpeg != null) {
                //    debugLabel.setIcon(new ImageIcon(jpeg));
                //}
                try{
                    Thread.sleep(100);
                } catch (Exception e) {
                    
                }
                
                // create transeferable image
                Transferable t = new ImageSelection(    
                    jpeg,
                    jpeg.getWidth(this),
                    jpeg.getHeight(this)
                );

                dragSource.startDrag(event, DragSource.DefaultCopyDrop, t, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dragDropEnd(DragSourceDropEvent event) { 
    }

    public void dragEnter(DragSourceDragEvent event) {
        DragSourceContext DolphinContext = event.getDragSourceContext(); 
        DolphinContext.setCursor(DragSource.DefaultCopyDrop);
    }

    public void dragOver(DragSourceDragEvent event) {
        DragSourceContext DolphinContext = event.getDragSourceContext(); 
        DolphinContext.setCursor(DragSource.DefaultCopyDrop);
    }
    
    public void dragExit(DragSourceEvent event) {
    }    

    public void dropActionChanged(DragSourceDragEvent event) {
    }
    
    /*
    public final void doMouseClick (MouseEvent e) {

        JTable tbl = (JTable) e.getSource ();
        int row = tbl.getSelectedRow ();
        int col = tbl.getSelectedColumn ();
        System.out.println("Clicked At {row, col} = {" + row + ", " + col + "}");
    }
     */
   
   /**
    * テーブルモデルクラス。
    */
    class ImageTableModel extends DefaultTableModel {
        public ImageTableModel(String[] columnNames, int rows) {
            super(columnNames, rows);
        }

        public Class getColumnClass(int col) {
            return javax.swing.ImageIcon.class;
        }

        /**
        * セルが編集可能かどうかを返す。
        */
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
}


