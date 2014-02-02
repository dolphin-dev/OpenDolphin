/*
 * HierarchyPanel.java
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
package open.dolphin.order;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.infomodel.HierarchyEntry;


import java.beans.*;
import java.util.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class HierarchyPanel extends JPanel {

    private JList class1List;
    private JList class2List;
    private JList class3List;
    private Vector class1Vec;
    private Vector class2Vec;
    private Vector class3Vec;

    private String master;
    private String orderClassCode;
    private PropertyChangeSupport boundSupport;

    /** 
     * Creates new HierarchyListPanel 
     */
    public HierarchyPanel() {
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // Class11
        class1List = new JList();
        class1List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        class1List.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if (e.getValueIsAdjusting() == false) {
                    Object o = class1List.getSelectedValue();
                    if ( o != null) {
                        fetchClass2();
                    }
                }
            }
        });        

        // List2
        class2List = new JList();
        class2List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        class2List.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if (e.getValueIsAdjusting() == false) {
                    Object o = class2List.getSelectedValue();
                    if ( o != null) {
                        fetchClass3();
                    }
                }
            }
        });

        // List3
        class3List = new JList();
        class3List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        class3List.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if (e.getValueIsAdjusting() == false) {
                    Object o = class3List.getSelectedValue();
                    if ( o != null) {
                        fetchFinalEntries();
                    }
                }
            }
        });

        JScrollPane scroller = new JScrollPane(class1List, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller);

        scroller = new JScrollPane(class2List, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller);

        scroller = new JScrollPane(class3List, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller);  

        boundSupport = new PropertyChangeSupport(this);
    }

    public String getMaster() {
        return master;
    }
    
    public void setMaster(String val) {
        master = val;
    }
    
    public String getOrderClassCode() {
        return orderClassCode;
    }
    
    public void setOrderClassCode(String val) {
        orderClassCode = val;
    }    
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }

    public void setCellWidth(int[] width) {
        class1List.setFixedCellWidth(width[0]);
        class2List.setFixedCellWidth(width[1]);
        class3List.setFixedCellWidth(width[2]);
    }

    public void fetchClass1() {

        /*if (class1Vec != null) {
            class1Vec.clear();
        }
        SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
        class1Vec = dao.getClass1Hierarchy(master, orderClassCode);

        class1List.setListData(class1Vec);
        clearClass2();
        clearClass3();*/
    }

    private void fetchClass2() {

        /*if (class2Vec != null) {
            class2Vec.clear();
        }
        HierarchyEntry he = (HierarchyEntry)class1List.getSelectedValue();
        System.out.println("h1=" + he.getHierarchyCode1());
        SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
        class2Vec = dao.getClass2Hierarchy(master, he.getHierarchyCode1(), orderClassCode);

        class2List.setListData(class2Vec);
        clearClass3();*/
    }

    private void fetchClass3() {
        /*if (class3Vec != null) {
            class3Vec.clear();
        }   
        HierarchyEntry he = (HierarchyEntry)class2List.getSelectedValue();
        System.out.println("h2=" + he.getHierarchyCode2());
        SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
        class3Vec = dao.getClass3Hierarchy(master, he.getHierarchyCode2(), orderClassCode);
        class3List.setListData(class3Vec);*/
    }   

    private void fetchFinalEntries() {
        HierarchyEntry he = (HierarchyEntry)class3List.getSelectedValue();
        
        boundSupport.firePropertyChange("hierarchyProp", null, he.getHierarchyCode3());
    }

    private void clearAll() {
        clearClass1();
        clearClass2();
        clearClass3();
    }

    private void clearClass1() {
        if (class1Vec != null) {
            class1Vec.clear();
            class1List.setListData(class1Vec);
        }
    }

    private void clearClass2() {
        if (class2Vec != null) {
            class2Vec.clear();
            class2List.setListData(class2Vec);
        }
    } 

    private void clearClass3() {
        if (class3Vec != null) {
            class3Vec.clear();
            class3List.setListData(class3Vec);
        }
    }         
}  
