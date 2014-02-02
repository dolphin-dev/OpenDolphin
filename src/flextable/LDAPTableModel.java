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

import java.util.*;
import netscape.ldap.*;

/**
 * Table model that gets its data from an LDAP entry. It
 * extends FlexibleTableModel to combine the name cells for
 * all value cells of the same attribute and to allow different
 * heights for individual rows.
 */
public class LDAPTableModel extends FlexibleTableModel {
        
    /**
     * Constructor reads and parses directory entry
     *
     * @param ldc Active connection to directory
     * @param dn DN of entry to read
     */
    public LDAPTableModel( LDAPConnection ldc, String dn) {
        super( _colHeaders, 1 );
        removeRow(0);
        
        _ldc = ldc;
        _dn = dn;
        setDN( dn );
    }
    
    /**
     * Constructor reads and parses directory entry
     *
     * @param ldc Active connection to directory
     * @param dn DN of entry to read
     */
    public LDAPTableModel( LDAPConnection ldc, String dn, Hashtable dic) {
        super( _colHeaders, 1 );
        removeRow(0);
        
        _ldc = ldc;
        _dn = dn;
        _attrDic = dic;
        setDN( dn );
    }

    /**
     * Reads and parses directory entry
     *
     * @param dn DN of entry to read
     */
    public void setDN( String dn ) {
        if ( _ldc == null ) {
            System.err.println( "No LDAP connection" );
            return;
        }
        _dn = dn;
        if ( _dn == null ) {
            return;
        }
        try {
            LDAPEntry entry = _ldc.read( dn );
            Enumeration attrs =
                entry.getAttributeSet().getAttributes();
            // Starting row of the next attribute
            int row = 0;
            String temp;
            String attrName = null;
            Vector v;
            LDAPAttribute attr;
            Enumeration vals;
            
            while( attrs.hasMoreElements() ) {
                
                attr =(LDAPAttribute)attrs.nextElement();
                
                int nVals = 0;
                vals = attr.getStringValues();
                
                while( (vals != null) && vals.hasMoreElements() ) {
                    // Each table row is a Vector with two
                    // elements                   
                    temp = attr.getName();
                    if (temp.equals("objectClass")) {
                        break;
                    }
                    
                    v = new Vector();
                    if (_attrDic != null) {
                        attrName = (String)_attrDic.get(temp);
                        if (attrName == null) {
                            attrName = temp;
                        }
                    }
                    else {
                        attrName = temp;
                    }
                        
                    v.addElement( attrName );
                    v.addElement( (String)vals.nextElement() );
                    nVals++;
                    addRow( v );
                }
                // Combine the name column for all values of
                // the same attribute
                if ( nVals > 1 ) {
                    combineRows( row, 0, nVals );
                }
                row += nVals;
            }
        } catch ( LDAPException e ) {
            System.err.println( "LDAPTableModel.setDN: " + e );
        }
    }

    /**
     * Ask the CellAttribute of this model to combine several
     * table cell rows into one
     *
     * @param row Starting row
     * @param col Column to combine
     * @param nRows Number of row cells to combine
     */
    protected void combineRows( int row, int col, int nRows ) {
        CellAttribute cellAtt =
            (CellAttribute)getCellAttribute();
        int[] r = new int[nRows];
        for( int i = 0; i < nRows; i++ ) {
            r[i] = row + i;
        }
        int[] c = { col };
        cellAtt.combine( r, c );
    }

    private Hashtable _attrDic;
    private LDAPConnection _ldc = null;
    private String _dn = null;
    final private static String[] _colHeaders =
        { "Attribute", "Value" };
}
