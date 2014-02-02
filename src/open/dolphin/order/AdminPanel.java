/*
 * AdminPanel.java
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

import open.dolphin.client.*;
import open.dolphin.dao.*;
import open.dolphin.infomodel.AdministrationEntry;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.prefs.*;

/**
 * Administration list panle.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AdminPanel extends JPanel {
    
    public static final String ADMIN_PROP = "adminProp";
    private static final int ADMIN_CELL_WIDTH   = 150;
    private static final int MEMO_CELL_WIDTH    = 150;
    
    private JComboBox adminCombo;
    private JList adminList;
    private JList adminCommentList;
    private Vector v2;
    private PropertyChangeSupport boundSupport;
    

    /** Creates new AdminPanel */
    public AdminPanel() {
        
        final Preferences prefs = ClientContext.getPreferences();
        int index = prefs.getInt("admin.selectedIndex", 2);
        SqlDolphinMasterDao dao = (SqlDolphinMasterDao)SqlDaoFactory.create(this, "dao.master.dolphin");
        
        // 用法階層を検索する
        Object[] adminClass = dao.getAdminClass();
        
        // 用法を検索する
        AdministrationEntry entry = (AdministrationEntry)adminClass[index];
        String h1Code = entry.getHierarchyCode1();
        v2 = dao.getAdministration(h1Code);
        
        // 用法コメントを検索する
        Object[] adminComment = dao.getAdminComment();
        
        // Component を生成する        
        adminCombo = new JComboBox(adminClass);
        adminCombo.setSelectedIndex(index);
        adminCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int index = adminCombo.getSelectedIndex();
                    prefs.putInt("admin.selectedIndex", index);
                    fetchAdministration(adminCombo.getSelectedItem()); 
                }
            }
        });
        
        adminList = new JList(v2);
        adminList.setFixedCellWidth(ADMIN_CELL_WIDTH);
        adminList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting() == false) {                    
                    // PostgreSQL 2002-11-23
                    AdministrationEntry ae = (AdministrationEntry)adminList.getSelectedValue();
                    if (ae == null) {
                        return;
                    }
                    AdminInfo info = new AdminInfo();
                    info.eventType = AdminInfo.TT_ADMIN;
                    info.adminCode = ae.getCode();
                    info.admin2 = ae.getAdminName();
                    info.classCode = ae.getClaimClassCode();
                    info.numberCode = ae.getNumberCode();
                    info.admin1 = ae.getDisplayName();
                    info.admin = info.admin1 + " " + info.admin2;
                    notifyAdminInfo(info);
                }
            }
        });
        
        JScrollPane scroller = new JScrollPane(adminList, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        
        // Layout      
        JPanel adp = new JPanel();
        adp.setLayout(new BorderLayout(0, 5));
        adp.add(adminCombo, BorderLayout.NORTH);
        adp.add(scroller, BorderLayout.CENTER);
        adp.setBorder(BorderFactory.createTitledBorder("用法"));
        
        // 用法コメントパネル       
        JPanel adc = new JPanel(new BorderLayout());
        
        adminCommentList = new JList(adminComment);
        adminCommentList.setFixedCellWidth(MEMO_CELL_WIDTH);
        adminCommentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminCommentList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting() == false) {
                    Object o = adminCommentList.getSelectedValue();
                    if ( o != null) {
                        AdminInfo info = new AdminInfo();
                        info.eventType = AdminInfo.TT_MEMO;
                        info.adminMemo =(String)o;
                        notifyAdminInfo(info);
                    }
                }
            }
        });
        JScrollPane scroller2 = new JScrollPane(adminCommentList, 
                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        adc.add(scroller2, BorderLayout.CENTER);
        adc.setBorder(BorderFactory.createTitledBorder("メモ"));
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(adp);
        this.add(Box.createHorizontalStrut(5));
        this.add(adc);
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }    
    
    private void notifyAdminInfo(AdminInfo info) {
        boundSupport.firePropertyChange(ADMIN_PROP, null, info);
    }
        
    private void fetchAdministration(Object o) {
     
        if (v2 != null) {
            v2.clear();
        }
        
        SqlDolphinMasterDao dao = (SqlDolphinMasterDao)SqlDaoFactory.create(this, "dao.master.dolphin");
        String h1Code = ((AdministrationEntry)o).getHierarchyCode1();
        v2 = dao.getAdministration(h1Code);
        
        adminList.setListData(v2);
    }
}