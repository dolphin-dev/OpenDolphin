/*
 * GCPKarte.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;


import open.dolphin.table.ObjectTableModel;

import org.jdom.*;

/**
 *
 * @author  kazm
 */
public class GUIBuilder {
    
    static final int[] SWING_CONSTANTS = {
        SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.LEADING, SwingConstants.TRAILING,
        SwingConstants.TOP, SwingConstants.BOTTOM
    };
    static final String[] SWING_CONSTANTS_STRING = {
        "LEFT", "CENTER", "RIGHT", "LEADING", "TRAILING", "TOP", "BOTTOM"
    };
    
    static final int[] VSB_POLICY = {
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.VERTICAL_SCROLLBAR_NEVER
    };
    static final int[] HSB_POLICY = {
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    };
    static final String[] POLICY_STRING = {
        "ASNEEDED", "ALWAYS", "NEVER"
    };
            
    HashMap hashMap;
    LinkedList containerList;
    int curDepth = -1;
    ButtonGroup bg;
    
    
    /** Creates a new instance of GCPKarte */
    public GUIBuilder() {
    }
        
    public JPanel getTopPanel() {
        JPanel ret = (JPanel)containerList.removeLast();
        containerList.clear();
        containerList = null;
        return ret;
    }
    
    public void buildStart(HashMap map) {
        containerList = new LinkedList();
        hashMap = map;
    }
    
    public void buildEnd() {
    }
         
    /**
     * JPanel を生成する
     */
    public void buildPanel(String layout, 
                            String titleBorder, 
                            String insets,
                            Element e) {
        
        JPanel panel = null;
        
        if (layout.equals("Grid")) {
            System.out.println("Gridlayout");
            String rows = e.getAttributeValue("rows");
            String cols = e.getAttributeValue("cols");
            String hgap = e.getAttributeValue("hgap");
            String vgap = e.getAttributeValue("vgap");

            panel = createPanel(layout, rows, cols, hgap, vgap, titleBorder, insets);
        
        } else {
            panel = createPanel(layout, titleBorder, insets);
        }
        
        addComponent(null, panel);
        
        // このパネルをスタックに積む
        pushContainer(panel);
    }
    
    
    /**
     * JTabbedPane を生成する
     */
    public void buildTabbedPane(String key, String tabPlacement, String layoutPolicy) {
        
        JTabbedPane tabbedPane = new JTabbedPane();
                
        addComponent(key, tabbedPane);
        
        // このTabbedPaneをスタックに積む
        pushContainer(tabbedPane);
    }    
    
    
    /**
     * TabbedPane に追加される JPanel を生成する
     */
    public void buildTabPanel(String tabTitle, 
                              String layout, 
                              String titleBorder, 
                              String insets) {
                
        JPanel panel = createPanel(layout, titleBorder, insets);
        
        // 現在の親コンテナ JTabbedPane にこのパネルを追加する
        JTabbedPane parent = (JTabbedPane)containerList.getFirst();
        parent.addTab(tabTitle, panel);
        
        // このパネルをスタックに積む
        pushContainer(panel);
    }
    
    
    /**
     * JScrollPane を生成する
     */
    public void buildScroller(String key, String vsbPolicy, String hsbPolicy) {
        
        JScrollPane scroller = new JScrollPane(stringToVsbPolicy(vsbPolicy), stringToHsbPolicy(hsbPolicy));
                
        addComponent(key, scroller);
        
        // このTScrollPaneをスタックに積む
        pushContainer(scroller);
    }    
    
    
    /**
     * JScrollPane に追加される JPanel を生成する
     */
    public void buildScrollerPanel(String layout) {
        
        JPanel panel = createPanel(layout, null, null);
        
        // 現在の親コンテナ JScrollPane にこのパネルを追加する
        JScrollPane parent = (JScrollPane)containerList.getFirst();
        parent.setViewportView(panel);
        
        // このパネルをスタックに積む
        pushContainer(panel);
    }
    
    
    /**
     * Button group パネルを生成する
     */
    public void buildButtons(String layout, String titleBorder, String insets) {
                                 
        JPanel panel = createPanel(layout, titleBorder, insets);
                    
        addComponent(null, panel);
        
        pushContainer(panel);
    }   
    
    /**
     * CheckBox group パネルを作成する
     */
    public void buildCheckBoxes(String layout, String titleBorder, String insets) {
                
        JPanel panel = createPanel(layout, titleBorder, insets);
        
        addComponent(null, panel);
        
        pushContainer(panel);
    }    
    
    
    /**
     * RadioButton group パネルを作成する
     */
    public void buildRadioButtons(String layout, String titleBorder,String insets) {
                
        JPanel panel = createPanel(layout, titleBorder, insets);
                
        addComponent(null, panel);
        
        pushContainer(panel);
        
        bg = new ButtonGroup();
    }    
    
    
    public void buildHGap(String width) {
        Component cmp = Box.createHorizontalStrut(stringToInt(width));
        addComponent(null, cmp);
    }
    
    public void buildHGlue() {
        Component cmp = Box.createHorizontalGlue();
        addComponent(null, cmp);
    }
    
    public void buildVGap(String height) {
        Component cmp = Box.createVerticalStrut(stringToInt(height));
        addComponent(null, cmp);
    }
    
    public void buildVGlue() {
        Component cmp = Box.createVerticalGlue();
        addComponent(null, cmp);
    }
    
    
    /**
     * JLabel を生成する
     */
    public void buildLabel(String key, String text, String iconStr, 
                           String alignStr, String hTextPos, String vTextPos,
                           String toolTipText) {
        
        JLabel label = null;
        
        if (text != null && iconStr != null) {
                    
            int align = JLabel.CENTER;
            
            if (alignStr != null) {
                align = stringToSwingConstants(alignStr);
            }
            
            label = new JLabel(text, createImageIcon(iconStr), align);
            
            if (hTextPos != null) {
                label.setHorizontalTextPosition(stringToSwingConstants(hTextPos));
            }
            
            if (vTextPos != null) {
                label.setVerticalTextPosition(stringToSwingConstants(vTextPos));
            }
            
        } else if (text != null) {
            
            if (alignStr != null) {
                label = new JLabel(text, stringToSwingConstants(alignStr));
                
            } else {
                label = new JLabel(text);
            }
            
        } else if (iconStr != null) {
            
            if (alignStr != null) {
                label = new JLabel(createImageIcon(iconStr), stringToSwingConstants(alignStr));
                
            } else {
                label = new JLabel(createImageIcon(iconStr));
            }
        }
        
        if (toolTipText != null) {
            label.setToolTipText(toolTipText);
        }
        
        addComponent(key, label);
    }
    
    
    /**
     * ラベル付/無しの JTextField を生成する
     */
    public void buildTextField(String key, String label, String value, String maxSize) {
        
        JTextField tf = new JTextField();
        
        tf.setMargin(new Insets(1,3,1,3));
        
        if (maxSize != null) {
            Dimension dim = stringToDimension(maxSize);
            tf.setPreferredSize(dim);
            tf.setMaximumSize(dim);
        }
        
        if (value != null) {
            tf.setText(value);
        }
                
        if (label != null) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            p.add(new JLabel(label));
            p.add(Box.createHorizontalStrut(7));
            p.add(tf);
            addComponent(null, p);
            hashMap.put(key, tf);
        
        } else {
            addComponent(key, tf);
        }
    }
    
  
    /**
     * ラベル付/無しの JTextArea を生成する
     */
    public void buildTextArea(String key, String label, String value, String insetStr) {
        
        JTextArea ta = new JTextArea();
        
        if (value != null) {
            ta.setText(value);
        }
        
        if (insetStr != null) {
            int[] insets = stringToIntArray(insetStr);
            ta.setMargin(new Insets(insets[0], insets[1], insets[2], insets[3]));
        }
                
        if (label != null) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.add(new JLabel(label));
            p.add(Box.createVerticalStrut(7));
            p.add(ta);
            addComponent(null, p);
            hashMap.put(key, ta);
        
        } else {
            addComponent(key, ta);
        }
    }    
    
    
    /**
     * JButton を生成する
     */
    public void buildButton(String key, String text, String iconSpec, String mnemonic, String enabled,
                            String hTextPos, String vTextPos, String toolTipText) {
                
        JButton btn = null;
        
        if (text != null && iconSpec != null) {
            
            ImageIcon icon = createImageIcon(iconSpec);
            btn = new JButton(text, icon);
            
        } else if (text != null) {
            
            if (text.equals("okText")) {
                text = (String)UIManager.get("OptionPane.okButtonText");

            } else if (text.equals("cancelText")) {
                text = (String)UIManager.get("OptionPane.cancelButtonText");
            }

            if (mnemonic != null) {
                text = text + "(" + mnemonic + ")";
            }     
       
            btn = new JButton(text);
            
        } else if (iconSpec != null) {
            
            ImageIcon icon = createImageIcon(iconSpec);
            btn = new JButton(icon);
        }
        
        if (mnemonic != null) {
            btn.setMnemonic(mnemonic.charAt(0));
        }
        
        if (enabled != null) {
            btn.setEnabled(stringToBool(enabled));
        }
        
        if (hTextPos != null) {
            btn.setHorizontalTextPosition(stringToSwingConstants(hTextPos));
        }

        if (vTextPos != null) {
            btn.setVerticalTextPosition(stringToSwingConstants(vTextPos));
        }
        
        if (toolTipText != null) {
            btn.setToolTipText(toolTipText);
        }
    
        addComponent(key, btn);
    }
    
    
    public void buildCheckBox(String key, String text) {
        JCheckBox cb = new JCheckBox(text);
        addComponent(key, cb);
    }
    
    
    public void buildRadioButton(String key, String text) {
        JRadioButton br = new JRadioButton(text);
        if (bg != null) {
            bg.add(br);
        }
        addComponent(key, br);
    }   
    
    
    public void buildComboBox(String key, String label, String itemLine) {
        
        String[] items = stringToStringArray(itemLine);
        
        JComboBox cb = new JComboBox(items);
        
        if (label != null) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            p.add(new JLabel(label));
            p.add(Box.createHorizontalStrut(7));
            p.add(cb);
            addComponent(null, p);
            hashMap.put(key, cb);
        
        } else {
            addComponent(key, cb);
        }
    }
    
    
    public void buildProgressBar(String key, String border, String min, String max) {
                
        JProgressBar pb = new JProgressBar();
        
        if (border != null) {
            pb.setBorderPainted(stringToBool(border));
        }
        
        if (min != null && max != null) {
            pb.setMinimum(stringToInt(min));
            pb.setMaximum(stringToInt(max));
        }
        addComponent(key, pb);
    }
    
    
    /**
     * ObjectTable を生成する
     */
    public void buildObjectTable(String key, String columnNames, String startNumRows) {
        
        JTable table = new JTable();
        
        String[] columns = stringToStringArray(columnNames);
        int numRows = stringToInt(startNumRows);
        ObjectTableModel model = new ObjectTableModel();
        model.setColumnNames(columns);
        model.setStartNumRows(numRows);
        
        table.setModel(model);
        
        addComponent(key, table);
    }
    
    
    /**
     * Object を生成する
     */
    public void buildObject(String key, String className) {
                
        try {
            
            Object o = Class.forName(className).newInstance();
            
            addComponent(key, (Component)o);
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    /**
     * JPanel を生成する
     */
    protected JPanel createPanel(String layout, 
                                String titleBorder, String insetStr) {
        
        JPanel panel = new JPanel();
        LayoutManager mgr = LayoutFactory.create(panel,layout, null, null, null, null);
        panel.setLayout(mgr);
              
        if (titleBorder != null && insetStr != null) {
            panel.setBorder(BorderFactory.createTitledBorder(titleBorder));
            JPanel p = new JPanel(new BorderLayout());
            p.add(panel);
            int[] insets = stringToIntArray(insetStr);
            p.setBorder(BorderFactory.createEmptyBorder(insets[0], insets[1], insets[2], insets[3]));
            panel = p;
            
        } else if (titleBorder != null) {
            panel.setBorder(BorderFactory.createTitledBorder(titleBorder));
            
        } else if (insetStr != null) {
            int[] insets = stringToIntArray(insetStr);
            panel.setBorder(BorderFactory.createEmptyBorder(insets[0], insets[1], insets[2], insets[3]));
        }
        
        return panel;
    }
    
    
    /**
     * JPanel を生成する
     */
    protected JPanel createPanel(String layout, 
                                String rows, String cols, String hgap, String vgap,
                                String titleBorder, String insetStr) {
        
        //System.out.println("Gridlayout");
        JPanel panel = new JPanel();
        LayoutManager mgr = LayoutFactory.create(panel,layout, rows, cols, hgap, vgap);
        panel.setLayout(mgr);
              
        if (titleBorder != null && insetStr != null) {
            panel.setBorder(BorderFactory.createTitledBorder(titleBorder));
            JPanel p = new JPanel(new BorderLayout());
            p.add(panel);
            int[] insets = stringToIntArray(insetStr);
            p.setBorder(BorderFactory.createEmptyBorder(insets[0], insets[1], insets[2], insets[3]));
            panel = p;
            
        } else if (titleBorder != null) {
            panel.setBorder(BorderFactory.createTitledBorder(titleBorder));
            
        } else if (insetStr != null) {
            int[] insets = stringToIntArray(insetStr);
            panel.setBorder(BorderFactory.createEmptyBorder(insets[0], insets[1], insets[2], insets[3]));
        }
        
        return panel;
    }
      
    
    /**
     * 親のコンテナ Panel に生成したコンポーネントを追加する。
     * また key が設定されている場合は HashMap に追加する。
     */
    protected void addComponent(String key, Component cmp) {
               
        // 親のコンテナに追加する
        if (containerList.size() > 0) {
            JPanel p = (JPanel)containerList.getFirst();
            p.add(cmp);
        }
        
        // HashMap へ登録する
        if (key != null) {
            hashMap.put(key, cmp);
        }
    }
    
        
    protected void pushContainer(Component conatiner) {
        containerList.addFirst(conatiner);
    }
    
    protected void buildEnd(int cmpType) {
        
        switch (cmpType) {
            case GUIDirector.CMP_PANEL:
            case GUIDirector.CMP_TABPANEL:
            case GUIDirector.CMP_SCROLLER_PANEL:
            case GUIDirector.CMP_TABBED_PANE:
            case GUIDirector.CMP_SCROLLER:
            case GUIDirector.CMP_BUTTONS:
            case GUIDirector.CMP_CHECK_BOXES:
            case GUIDirector.CMP_RADIO_BUTTONS:
                
                // コンテナを pop する
                // ただし TOP のパネルは　生成物して残しておく
                if (containerList.size() > 1) {
                    
                    containerList.removeFirst();
                }
                break;
        }
    }
    
    
    protected ImageIcon createImageIcon(String iconStr) {
        return new ImageIcon (this.getClass().getResource(iconStr));
    }
    
    protected String[] stringToStringArray(String line) {
             
        StringTokenizer st = new StringTokenizer(line, ",", true);
        
        String[] ret = new String[5];
        int count = 0;
        int state = 0;

        while (st.hasMoreTokens()) {

            if ( (count % 5) == 0 ) {
                String[] dest = new String[count + 5];
                System.arraycopy(ret, 0, dest, 0, count);
                ret = dest;
            }

            String token = st.nextToken();

            switch (state) {

                case 0:
                    if (token.equals(",")) {
                        token = null;

                    } else {
                        state = 1;
                    }
                    ret[count] = token;
                    count++;
                    break;

                case 1:
                    state = 0;
                    break;
            }
        }

        String[] ret2 = new String[count];
        System.arraycopy(ret, 0, ret2, 0, count);
        return ret2;
    }
    
    protected boolean stringToBool(String enabled) {
        return Boolean.valueOf(enabled).booleanValue();
    }    
              
    protected Insets stringToInsets(String s) {
                
        int[] ret = stringToIntArray(s);
        
        return new Insets(ret[0], ret[1], ret[2], ret[3]);
    }
    
    protected Dimension stringToDimension(String s) {
       
        int[] val = stringToIntArray(s);
        
        return new Dimension(val[0], val[1]);
    }    
        
    protected int[] stringToIntArray(String line) {
                
        String[] val = stringToStringArray(line);
        
        int len = val.length;
        int[] ret = new int[len];
        
        for (int i = 0; i < len; i++) {
            ret[i] = stringToInt(val[i]);
        }
        
        return ret;
    }
    
    protected int stringToInt(String s) {
        
        int ret = 0;
        
        try {
            ret =  Integer.parseInt(s);
            
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        return ret;
    }
    
    protected int findIndex(String[] list, String target) {
        
        int index = 0;
        
        for (int i = 0; i < list.length; i++) {
            if (target.equals(list[i])) {
                index = i;
                break;
            }
        }
        
        return index;
    }
    
    protected int stringToVsbPolicy(String s) {
        
        int index = 0;
        
        if (s != null) {
            s = s.toUpperCase();
            index = findIndex(POLICY_STRING, s);
        }
        
        return VSB_POLICY[index];
    }
    
    protected int stringToHsbPolicy(String s) {
        
        int index = 0;
        
        if (s != null) {
            s = s.toUpperCase();
            index = findIndex(POLICY_STRING, s);
        }
        
        return HSB_POLICY[index];
    }
    
    protected int stringToSwingConstants(String s) {
        
        int index = 0;
        
        if (s != null) {
            s = s.toUpperCase();
            index = findIndex(SWING_CONSTANTS_STRING, s);
        }
        
        return SWING_CONSTANTS[index];
    }    
    
    /*public void buildLabel(String key, String text, 
                          int gridx, int gridy, int gridwidth, int gridheight, 
                          int fill, int anchor, Insets insets) {
     
        JLabel label = new JLabel(text);
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.fill = fill;
        c.anchor = anchor;
        if (insets != null) {
            c.insets = insets;
        }
        gbl.setConstraints(label, c);
        
        addComponent(key, label);
    }*/
    
    /*public void buildTextField(String key, String value, DocumentListener dl,
                          int gridx, int gridy, int gridwidth, int gridheight, 
                          int fill, int anchor, Insets insets) {
     
        JTextField tf = new JTextField();
        if (value != null) {
            tf.setText(value);
        }
        if (dl != null) {
            tf.getDocument().addDocumentListener(dl);
        }
        
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.fill = fill;
        c.anchor = anchor;
        if (insets != null) {
            c.insets = insets;
        }
        gbl.setConstraints(tf, c);
        
        addComponent(key, tf);
    }*/
    
    /*public void buildButton(String key, String text, String mnemonic, boolean enabled, ActionListener al,
                          int gridx, int gridy, int gridwidth, int gridheight, 
                          int fill, int anchor, Insets insets) {
     
        System.out.println("button");
        System.out.println("key=" + key);
        System.out.println("text=" + text);
        System.out.println("enabled=" + enabled);
        System.out.println("x=" + gridx);
        System.out.println("y=" + gridy);
        System.out.println("wx=" + gridwidth);
        System.out.println("wy=" + gridheight);
        System.out.println("fill=" + fill);
        System.out.println("anchor=" + anchor);
        
        if (text.equals("okText")) {
            text = (String)UIManager.get("OptionPane.okButtonText");
            
        } else if (text.equals("cancelText")) {
            text = (String)UIManager.get("OptionPane.cancelButtonText");
        }
        
        if (mnemonic != null) {
            text = text + "(" + mnemonic + ")";
        }     
       
        JButton btn = new JButton(text);
        
        if (mnemonic != null) {
            btn.setMnemonic(mnemonic.charAt(0));
        } 
        
        btn.setEnabled(enabled);
        
        if (al != null) {
            btn.addActionListener(al);
        }
        
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.fill = fill;
        c.anchor = anchor;
        if (insets != null) {
            c.insets = insets;
        }
        gbl.setConstraints(btn, c);
        
        addComponent(key, btn);
        
        //System.out.println("end button");
    } */    
}