/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public License
 * Version 1.0 (the "NPL"); you may not use this file except in
 * compliance with the NPL.  You may obtain a copy of the NPL at
 * http://www.mozilla.org/NPL/
 *
 * Software distributed under the NPL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the NPL
 * for the specific language governing rights and limitations under the
 * NPL.
 *
 * The Initial Developer of this code under the NPL is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1999 Netscape Communications Corporation.  All Rights
 * Reserved.
 */
package flextable;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import netscape.ldap.*;

/**
 * PropertyTable
 * A table that inherits the individual cell colors and
 * fonts, the cell aggregation, and the individual row
 * heights of FlexibleTable, and uses an LDAPTableModel
 * to populate itself with the contents of a directory
 * entry.
 *
 * @version 1.0
 * @author rweltman
 **/
public class PropertyTable extends FlexibleTable {
    /**
     * Default constructor. It does not yet have an LDAP
     * data model, and cannot display LDAP data until
     * setLDAPConnection is called with an active connection.
     */
    public PropertyTable() {
        this( new FlexibleTableModel() );
    }

    /**
     * Use any table model. If it is not an LDAPTableModel,
     * the object will not be able to display LDAP data
     * until setLDAPConnection is called.
     */
    public PropertyTable(TableModel model) {
        super( model );
        setToolTipsEnabled( _toolTipsEnabled );
        initialize();
    }

    /**
     * Constructor to read content from a directory
     * entry
     *
     * @param ldc An active directory connection
     * @param dn DN of the entry to read
     */
    public PropertyTable(LDAPConnection ldc, String dn, Hashtable ht) {
        this( new LDAPTableModel( ldc, dn, ht) );
        _ldc = ldc; 
        _dn = dn;
    }
    
    /**
     * Constructor to read content from a directory
     * entry
     *
     * @param ldc An active directory connection
     * @param dn DN of the entry to read
     */
    public PropertyTable(LDAPConnection ldc, String dn) {
        this( new LDAPTableModel( ldc, dn) );
        _ldc = ldc; 
        _dn = dn;
    }    

    /**
     * Prepare table properties
     */
    protected void initialize() {
        int width = setColumnWidths( _colWidths );
        int height = getCellRect(getRowCount(),
                                 0, false).y;
        setAutoResizeMode( AUTO_RESIZE_LAST_COLUMN );
   }

    /**
     * Set the DN of the model, repopulate the table from
     * the specified directory entry
     *
     * @param dn The DN of the entry to read
     */
    public void setDN( String dn ) {
        setModel( new LDAPTableModel( _ldc, dn ) );
        initialize();
    }

    /**
     * Report the DN of the entry currently being displayed
     *
     * @return The DN of the entry being displayed
     */
    public String getDN() {
        return _dn;
    }

    /**
     * Set the LDAP connection of the model and repopulate
     * the table using the latest DN.
     *
     * @param ldc An active directory connection
     */
    public void setLDAPConnection( LDAPConnection ldc ) {
        _ldc = ldc;
        setDN( _dn );
    }

    /**
     * Report the LDAP connection of the model
     *
     * @return The directory connection of the model
     */
    public LDAPConnection getLDAPConnection() {
        return _ldc;
    }

    /**
     * Enable or disable tooltips. By default they are
     * disabled.
     *
     * @param on <CODE>true</CODE> if tooltips are to be
     * displayed.
     */
    public void setToolTipsEnabled( boolean on ) {
        _toolTipsEnabled = on;
        if ( !_toolTipsEnabled ) {
            ToolTipManager.sharedInstance().
                unregisterComponent( this );
        } else {
            ToolTipManager.sharedInstance().
                unregisterComponent( this );
        }
    }

    /**
     * Report if tooltips are enabled or disabled. By default
     * they are disabled.
     *
     * @return <CODE>true</CODE> if tooltips are to be
     * displayed.
     */
    public boolean getToolTipsEnabled() {
        return _toolTipsEnabled;
    }

    /**
     * Override DefaultTableModel.isCellEditable to disallow
     * editing.
     *
     * @param row The row of the cell being queried
     * @param column The column of the cell being queried
     * @return <CODE>true</CODE> if the cell is editable
     */
    public boolean isCellEditable( int row,
                                   int column ) {
        if ( column == 0 ) {
            // Never allow editing the attribute name
            return false;
        }
        // For now, do not allow editing the values either
        return false;
    }

    /**
     * Tooltips cause repaint problems and they are truncated
     * by the dialog boundaries, but they may be useful in
     * some cases. The getToolTip methods extend those of
     * JComponent in order to provide appropriate text
     * for the particular cell.
     */
    protected int[] getToolTipCell( MouseEvent event ) {
        int col = columnAtPoint( event.getPoint() );
        if ( col < 1 ) {
            return null;
        }
        int row = rowAtPoint( event.getPoint() );
        Object o = getValueAt(row,col);
        if( o == null )
            return null;
        if( o.toString().equals("") )
            return null;
        return new int[] { row, col };
    }

    public String getToolTipText(MouseEvent event) {
        int[] cell = getToolTipCell( event );
        if ( cell == null ) {
            return null;
        }
        Object o = getValueAt(cell[0],cell[1]);
        return o.toString();
    }

    public Point getToolTipLocation(MouseEvent event) {
        int[] cell = getToolTipCell( event );
        if ( cell == null ) {
            return null;
        }
        Point pt = getCellRect(cell[0], cell[1], true).getLocation();
        pt.translate(-1,-2);
        return pt;
    }

    /**
     * Set the column minimum widths, and return the preferred
     * max total width. The default widths are { 150, 320 }.
     *
     * @param widths Array of widths for table. Only the first
     * two used if the table model is LDAPTableModel.
     * @return The sum of all widths.
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
    
    private LDAPConnection _ldc = null;
    private String _dn = "";
    final private static int[] _colWidths = { 150, 320 };
    private boolean _toolTipsEnabled = false;
}
