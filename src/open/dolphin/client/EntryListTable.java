/*
 * EntryListTable.java
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
import javax.swing.table.*;

import open.dolphin.dao.*;

import java.util.*;
import java.beans.*;
import netscape.ldap.*;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class EntryListTable extends JTable {
    
    public static final String COUNT_PROP = "countProp";
        
    String base;
    
    int scope;
    
    String filter;
    
    String[] attrs;
    
    String[] sortAttr;
    
    boolean[] ascend;
    
    EntryListTableModel model;
    
    PropertyChangeSupport boundSupport;
    
    
    public EntryListTable(EntryListTableModel model) {
        super(model);
        this.model = model;
        boundSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * カラム名と最初の行数からこのクラスを生成する
     */
    public EntryListTable(String[] columnNames, int startNumRows) {        
        this(new EntryListTableModel(columnNames,startNumRows));
    }

    /**
     * カラム幅を設定し合計の幅を返す
     */
    public int setColumnWidths( int[] widths ) {
        Enumeration en = getColumnModel().getColumns();
        int i = 0;
        int width = 0;
        while( en.hasMoreElements() ) {
            TableColumn col = (TableColumn)en.nextElement();
            col.setMinWidth( widths[i] );
            col.setPreferredWidth( widths[i] );
            col.setResizable( true );
            width += col.getPreferredWidth();
            i++;
        }
        sizeColumnsToFit( 0 );
        return width;
    }    
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
            
    public String getBase() {
        return base;
    }
    
    public void setBase(String value) {
        base = value;
    }
    
    public String getFilter() {
        return filter;
    }
    
    public void setFilter(String value) {
        filter = value;
    }
    
    public int getScope() {
        return scope;
    }
    
    public void setScope(int value) {
        scope = value;
    }
    
    public String[] getAttributesToFetch() {
        return attrs;
    }
    
    public void setAttributesToFetch(String[] value) {
        attrs = value;
    }
    
    public void setAttributesToSort(String[] sortAttr, boolean[] ascend) {     
        this.sortAttr = sortAttr;
        this.ascend = ascend;
    }
    
    /**
     * エントリ情報を行単位に表示する
     */
    public void fetchEntries() {
        /*System.out.println(base);
        System.out.println(scope);
        System.out.println(filter);*/
        model.clear();
        ArrayList entries = fetchEntries(base, scope, filter, attrs, sortAttr, ascend);
        if (entries != null) {
            model.addRows(entries);
            boundSupport.firePropertyChange(COUNT_PROP, new Integer(-1), new Integer(getDataSize()));
        }
    }
    
    /**
     * 指定行のDNを返す
     */
    public String getDN(int row) {
        return model.getDN(row);
    }
    
    /**
     * データをクリアする
     */
    public void clear() {
        model.clear();
    }
    
    /**
     * 実際の行数を返す
     */
    public int getDataSize() {
        return model.getSize();
    }
        
    protected boolean canSearch() {     
        return ((base == null) || 
                (filter == null) ||
                (attrs == null)) ? false : true;
    }
    
    protected ArrayList fetchEntries(String base, 
                                  int scope, 
                                  String filter, 
                                  String[] attrs,
                                  String[] sortAttr, boolean[] ascend) {
              
        LDAPConnection ld = DaoFactory.createConnection(this);                              
        if (ld == null) {
            return null;
        }
        ArrayList ret = null;
        
        try {                                          
            // Execute search
            LDAPSearchResults res = ld.search(base,
                                              scope,
                                              filter,
                                              attrs,   
                                              false);
            // Sorting
            if (sortAttr != null) {
                res.sort( new LDAPCompareAttrNames(sortAttr, ascend));
            }
            
            LDAPEntry entry;            
            LDAPAttribute attr;
            Enumeration enumVals;
            String[] data;
            int len = attrs.length;
            String dn;
            ret = new ArrayList();

            // Loop on results until complete
            while (res.hasMoreElements() ) {
                // Next directory entry
                entry = res.next();
                dn = entry.getDN();
                data = new String[len + 1];  // for DN
                data[len] = dn;              // store DN

                for (int i = 0; i < len; i++) {

                    attr = entry.getAttribute(attrs[i]);
                    if (attr == null) {
                        continue;
                    }
                    // Enumerate on values for this attribute
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        data[i] = (String)enumVals.nextElement();
                    }
                }
                ret.add(data);               
            }        
        }
        catch (LDAPException e) {
            log("Exception while fetchEntries: " + e.toString());
        }
        
        try {
            ld.disconnect();
        }
        catch (LDAPException le) {
        }
        
        return ret;
    }
    
    protected void log(String msg) {
    }
}