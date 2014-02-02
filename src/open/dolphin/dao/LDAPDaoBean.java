/*
 * LDAPDaoBean.java
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
package open.dolphin.dao;

import java.util.*;
import netscape.ldap.*;


/**
 * LDAP DAO のベースクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LDAPDaoBean extends DaoBean {
          
    static HashMap dn;
    static {
        dn = new HashMap(9, 1.0f);
        
        dn.put("namingContext", "o=Dolphin");
        dn.put("authentication", "o=Dolphin");  // 暫定
        dn.put("dolphinUsers", "ou=DolphinUsers,o=Dolphin");
        dn.put("dolphinFacilities", "ou=DolphinFacilities,o=Dolphin");
        
        dn.put("aspStampTree", "ou=stampTree,ou=stamp,ou=library,o=digital-globe");
        dn.put("aspStampRepository", "ou=stampRepository,ou=stamp,ou=library,o=digital-globe");
    }   
    String bindDN;

    /** Creates new AspConnectionBean */
    public LDAPDaoBean() {
    }
    
    public static String getDN(String key) {
        //return DolphinContext.getResourceString(key);
        return (String)dn.get(key);
    }
    
    public static Iterator getDNs() {
        return dn.keySet().iterator();
    }  
        
    public String getBindDN() {
        return bindDN;
    }
    
    public void setBindDN(String val) {
        bindDN = val;
    }
        
    public final LDAPConnection getConnection() throws LDAPException {
        LDAPConnection ld = new LDAPConnection();
        ld.connect(host, port, bindDN, passwd);
        return ld;
    }    
    
    /*public final LDAPConnection getConnection() {
        
        LDAPConnection ld = new LDAPConnection();
        try {
            ld.connect(host, port, bindDN, passwd);
            
        } catch (LDAPException e) {
            errorMsg = e.toString();
            System.out.println(e);
            ld = null;
        }
        
        return ld;
    }*/
    
    public final void disconnect(LDAPConnection ld) {
        if (ld != null && ld.isConnected() ) {
            try {
                ld.disconnect();
            
            } catch (LDAPException e) {
                System.out.println(e);
            }
        }
        ld = null;
    }
            
    protected final void fetchEntries(String base,
                                      int scope, 
                                      String filter,
                                      String[] attrs, 
                                      String[] sortAttrs, 
                                      boolean[] ascend,
                                      ArrayList dataList) {
     
        LDAPConnection ld = null;
                                       
        try {
            ld = getConnection();
            LDAPSearchResults res = ld.search(base, scope, filter, attrs, false);
            
            if (sortAttrs != null) {
                res.sort( new LDAPCompareAttrNames(sortAttrs, ascend));
            }
            
            int len = attrs.length;

            // Loop on results until complete
            while (res.hasMoreElements() ) {
                // Next directory entry
                LDAPEntry entry = res.next();
                //System.out.println(entry.getDN());
                String[]  data = new String[len];

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
                dataList.add(data);               
            } 
        
        } catch (LDAPException e) {
            processError(ld, "dummy", "LDAPException while getting entries: " + e.toString());
        }
        disconnect(ld);
    }
    
    protected final void fetch1AEntries(String base, 
                                        int scope, 
                                        String filter, 
                                        String[] attrs, 
                                        String[] sortAttrs, 
                                        boolean[] ascend, 
                                        ArrayList list) {
                       
        LDAPConnection ld = null;

        try {      
            ld = getConnection();
            LDAPSearchResults res = ld.search(base, scope, filter, attrs, false);
           
            if (sortAttrs != null) {
                res.sort( new LDAPCompareAttrNames(sortAttrs, ascend));
            }

            while (res.hasMoreElements() ) {

                LDAPEntry entry = res.next();

                LDAPAttribute attr = entry.getAttribute(attrs[0]);
                if (attr != null) {
                    Enumeration enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        list.add(enumVals.nextElement());
                    }
                }
            }
        
        } catch (LDAPException e) {
            processError(ld, "dummy", "LDAPException while getting entries: " + e.toString());
        }
        disconnect(ld);
    }        
}