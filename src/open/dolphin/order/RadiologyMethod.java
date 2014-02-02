/*
 * RadiologyMethod.java
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

import open.dolphin.dao.*;
import open.dolphin.infomodel.RadiologyMethodEntry;

import java.awt.*;
import java.beans.*;
import java.util.*;

/**
 * Radiology method list.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class RadiologyMethod extends JPanel {
    
    public static final String RADIOLOGY_MEYTHOD_PROP = "radiologyProp";
    private static final int METHOD_CELL_WIDTH   = 120;
    private static final int COMMENT_CELL_WIDTH    = 140;
    
    private final JList methodList;
    private JList commentList;
    private Vector v2;
    private PropertyChangeSupport boundSupport;

    /** 
     * Creates new AdminPanel 
     */
    public RadiologyMethod() {
                
        boundSupport = new PropertyChangeSupport(this);
        SqlDolphinMasterDao dao = (SqlDolphinMasterDao)SqlDaoFactory.create(this, "dao.master.dolphin");
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
           
        // Method panel
        JPanel p1 = new JPanel(new BorderLayout());        
        Object[] methods = dao.getRadiologyMethod();
        methodList = new JList(methods);
        methodList.setFixedCellWidth(METHOD_CELL_WIDTH);
        methodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        methodList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting() == false) {
                    /*IdNamePair o = (IdNamePair)methodList.getSelectedValue();
                    if ( o != null) {
                        fetchComments(o); 
                    } */
                    RadiologyMethodEntry entry = (RadiologyMethodEntry)methodList.getSelectedValue();
                    if (entry == null) {
                        return;
                    }
                    fetchComments(entry.getHierarchyCode1());
                }
            }
        });
        
        JScrollPane scroller = new JScrollPane(methodList, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        p1.add(scroller);
        p1.setBorder(BorderFactory.createTitledBorder("éBâeï˚ñ@"));
        
        // Commet panel
        JPanel p2 = new JPanel(new BorderLayout());
        commentList = new JList();
        commentList.setFixedCellWidth(COMMENT_CELL_WIDTH);
        commentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting() == false) {
                    /*IdNamePair info = (IdNamePair)commentList.getSelectedValue();
                    if ( info != null) {
                        notifyComment(info);
                    }*/
                    RadiologyMethodEntry entry = (RadiologyMethodEntry)commentList.getSelectedValue();
                    if (entry == null) {
                        return;
                    }
                    notifyComment(entry.getName());
                }
            }
        });
        scroller = new JScrollPane(commentList, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        p2.add(scroller);
        p2.setBorder(BorderFactory.createTitledBorder("éBâeÉRÉÅÉìÉg"));

        // Add p1 and p2
        add(p1);
        add(p2);
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }    
    
    private void notifyComment(String cm) {
        boundSupport.firePropertyChange(RADIOLOGY_MEYTHOD_PROP, null, cm);
    }
        
    private void fetchComments(String h1) {
     
        if (v2 != null) {
            v2.clear();
        }
        SqlDolphinMasterDao dao = (SqlDolphinMasterDao)SqlDaoFactory.create(this, "dao.master.dolphin");
        v2 = dao.getRadiologyComments(h1);        
        commentList.setListData(v2);
    }
}