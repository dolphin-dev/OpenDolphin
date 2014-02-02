/*
 * LDAPServerConnection.java
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
package open.dolphin.server;

import java.util.*;
import netscape.ldap.*;

/**
 * ServerConnection
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LDAPServerConnection {
    
    private static LDAPServerConnection instance = new LDAPServerConnection();

    private Hashtable env;
    private Vector pool;
    private int poolSize = 3;
        
    private LDAPServerConnection() {
        super();
    }

    public static LDAPServerConnection getInstance() {
        return instance;
    }

    public Hashtable getEnviroment() {
        return env;
    }
    
    public void setEnviroment(Hashtable h) {
        env = h;
        
        if (pool != null) {
            int size = pool.size();
            while (size > 0) {
                LDAPConnection ld = (LDAPConnection)pool.remove(size -1);
                disconnect(ld);
                size = pool.size();
            }
        }
    }
    
    public int getPoolSize() {
        return poolSize;
    }
    
    public void setPoolSize(int val) {
        
        if ( (pool != null) && (pool.size() > val) ) {
            int count = pool.size() - val;
            while (count > 0) {
                int size = pool.size();
                LDAPConnection ld = (LDAPConnection)pool.remove(size -1);
                disconnect(ld);
                count--;
            }
        }
        poolSize = val;
    }
    
    public synchronized LDAPConnection acquireConnection() {
        if (pool != null) {
            int size = pool.size();
            if ( size > 0 ) {
                return (LDAPConnection)pool.remove(size -1);
            }
        }
        return createConnection();
    }

    public synchronized void releaseConnection(LDAPConnection ld) {

        if (pool == null) {
            pool = new Vector(poolSize);
        }
        
        int size = pool.size();
        if (size < poolSize) {
            pool.add(ld);
        }
        else {            
            disconnect(ld);
        }   
    }
    
    private LDAPConnection createConnection() {
     
        if (env == null) {
            return null;
        }
        
        LDAPConnection ld = null;
        
        try {
            String host = (String)env.get("host");
            Integer i = (Integer)env.get("port");
            int port = i.intValue();
            String authId = (String)env.get("authid");
            String authPasswd = (String)env.get("authpw");
            ld = new LDAPConnection();
            ld.connect(host, port, authId, authPasswd);
        }
        catch (Exception e) {
            ld = null;
        }      
        return ld; 
    }
    
    private void disconnect(LDAPConnection ld) {
        try {
            ld.disconnect();
            ld = null;
        }
        catch (LDAPException e) {
        }
    }
    
    public Vector fetchDNs(String base, 
                               int scope, 
                               String filter) {
        
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return null;
        }
        
        Vector v = null;
        
        try {
            LDAPSearchResults res = ld.search(base, scope, filter, new String[] {LDAPv3.NO_ATTRS}, false);
            
            v = new Vector();
             
            while (res.hasMoreElements() ) {
                // Next directory entry
                LDAPEntry entry = res.next();
                v.add(entry.getDN());              
            }           
        }
        catch (LDAPException e) {
            System.out.println("Exception at fetchDNs: " + e.toString());
            v = null;
        }
        
        releaseConnection(ld);
        
        return v;       
    }
    
    public Vector fetchEntries(String base, 
                                  int scope, 
                                  String filter, 
                                  String[] attrs,
                                  String[] sortAttr, boolean[] ascend) {
             
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return null;
        }
        
        Vector v = null;
        
        try {
            LDAPSearchResults res = ld.search(base,
                                              scope,
                                              filter,
                                              attrs,   
                                              false);
            // Sorting
            if ( sortAttr != null) {
                res.sort( new LDAPCompareAttrNames(sortAttr, ascend));
            }

            // Loop on results until complete
            int len = attrs.length;
            v = new Vector();

            while (res.hasMoreElements() ) {
                // Next directory entry
                LDAPEntry entry = res.next();
                // dn = entry.getDN();
                String[] data = new String[len];

                for (int i = 0; i < len; i++) {

                    LDAPAttribute attr = entry.getAttribute(attrs[i]);
                    if (attr == null) {
                        continue;
                    }
                    // Enumerate on values for this attribute
                    Enumeration enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        data[i] = (String)enumVals.nextElement();
                    }
                }
                v.add(data);               
            }        
        }
        catch (Exception e) {
            System.out.println("Exception at fetchEntries: " + e.toString());
            v = null;
        }
        
        releaseConnection(ld);
        
        return v;
    }
    
    public String[] fetchEntry(String dn, String[] attrs) {
             
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return null;
        }
        
        String[] o = null;
        
        try {
            LDAPEntry entry = ld.read(dn, attrs);

            int len = attrs.length;
            o = new String[len];
            
            for (int i = 0; i < len; i++) {

                LDAPAttribute attr = entry.getAttribute(attrs[i]);
                if (attr == null) {
                    continue;
                }
                // Enumerate on values for this attribute
                Enumeration enumVals = attr.getStringValues();
                if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                    o[i] = (String)enumVals.nextElement();
                }
            }     
        }
        catch (Exception e) {
            System.out.println("Exception at fetchEntry: " + e.toString());
            o = null;
        }
        
        releaseConnection(ld);
        
        return o;
    }
    
    public byte[] fetchBinary(String dn, String bAttr) {
             
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return null;
        }
        
        byte[] ret = null;
        
        try {
            LDAPEntry entry = ld.read(dn, new String[]{bAttr});

            LDAPAttribute attr = entry.getAttribute(bAttr);
            if (attr != null) {
                // Enumerate on values for this attribute
                Enumeration enumVals = attr.getByteValues();
                if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                    ret = (byte[])enumVals.nextElement();
                }
            }
        }
        catch (Exception e) {
            System.out.println("Exception at fetchEntry: " + e.toString());
            ret = null;
        }
        
        releaseConnection(ld);
        
        return ret;
    }
    
    public Vector fetchBinaries(String base, 
                                  int scope, 
                                  String filter, 
                                  String bAttr) {
             
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return null;
        }
        
        Vector v = null;
        
        try {
            LDAPSearchResults res = ld.search(base,
                                              scope,
                                              filter,
                                              new String[]{bAttr},   
                                              false);

            v = new Vector();

            while (res.hasMoreElements() ) {

                LDAPEntry entry = res.next();
                LDAPAttribute attr = entry.getAttribute(bAttr);
                if (attr == null) {                   
                    v.add(null);
                    continue;
                }
                // Enumerate on values for this attribute
                Enumeration enumVals = attr.getByteValues();
                if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                    v.add(enumVals.nextElement());
                }          
            }        
        }
        catch (Exception e) {
            System.out.println("Exception at fetchEntries: " + e.toString());
            v = null;
        }
        
        releaseConnection(ld);
        
        return v;
    }
    
    public boolean isEntryExist(String dn) {
        
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return false;
        }
        boolean ret = false;
        
        try {
            LDAPEntry entry = ld.read(dn, new String[] {LDAPv3.NO_ATTRS});
            ret = true;            
        }
        catch (LDAPException e) {
            // No entry
        }
        
        releaseConnection(ld);
        
        return ret;
    }
    
    public void addEntry(String dn, String[] objectClasses, String[] attrs, String[] data) {
        
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return;
        }
        
        try {
            LDAPAttributeSet attSet = new LDAPAttributeSet();
            attSet.add(new LDAPAttribute("objectclass", objectClasses));
            
            int len = attrs.length;            
            for (int i = 0; i < len; i++) {
                if (data[i] != null) {
                    attSet.add(new LDAPAttribute(attrs[i],data[i]));
                }
            }
            LDAPEntry entry = new LDAPEntry(dn, attSet);
            ld.add(entry);
        }
        catch (LDAPException e) {
            System.out.println("Exception at addEntry: " + e.toString());
        }
        
        releaseConnection(ld);      
    }
    
    public void modifyEntry(String dn, String[] attrs, String[] data) {
        
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return;
        }
        
        try {
            LDAPModificationSet mods = new LDAPModificationSet();
            
            int len = attrs.length;            
            for (int i = 0; i < len; i++) {
                if (data[i] != null) {
                    mods.add(LDAPModification.REPLACE, new LDAPAttribute(attrs[i],data[i]));
                }
            }
            ld.modify(dn, mods);
        }
        catch (LDAPException e) {
            System.out.println("Exception at modifyEntry: " + e.toString());
        }
        
        releaseConnection(ld);      
    }  
    
    public void deleteEntry(String dn) {
        
        LDAPConnection ld = acquireConnection();
        
        if (ld == null) {
            return;
        }
        
        try {
            ld.delete(dn);
        }
        catch (LDAPException e) {
            System.out.println("Exception at deleteEntry: " + e.toString());
        }
        
        releaseConnection(ld);      
    }    
}