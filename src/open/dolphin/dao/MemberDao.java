/*
 * MemberDao.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.dao;

import java.util.*;

import open.dolphin.client.*;
import netscape.ldap.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MemberDao extends LDAPDaoBean {
    
    /** Creates a new instance of MemberDao */
    public MemberDao() {
    }
    
    public ArrayList getFacilityList() {
        
        LDAPConnection ld = null;
        
        ArrayList results = null;
        String baseDN = getDN("DolphinFacilities");
        String filter = "(&(objectClass=DolphinFacilities)(facilityId=*))";
        String[] attrs = new String[]{"facilityId", "o", "departmentId", "postalCode","st", "registeredAddress","telephoneNumber"};
        
        try {
            ld = getConnection();
            LDAPSearchResults res = ld.search(baseDN,
                                              LDAPConnection.SCOPE_ONE, 
                                              filter, 
                                              attrs, 
                                              false);
            
            while(res.hasMoreElements()) {
            
                LDAPEntry entry = res.next();
                FacilityProfileEntry profile = new FacilityProfileEntry();
                Enumeration enumVals;

                LDAPAttribute attr = entry.getAttribute("facilityId");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setFacilityId((String)enumVals.nextElement());
                    }
                }

                attr = entry.getAttribute("o");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setFacilityName((String)enumVals.nextElement());
                    }
                }
                
                attr = entry.getAttribute("departmentId");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setDepartmentId((String)enumVals.nextElement()); // MV
                    }
                }
                
                attr = entry.getAttribute("postalCode");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setPostalCode((String)enumVals.nextElement());
                    }
                }
                
                attr = entry.getAttribute("st");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setState((String)enumVals.nextElement());
                    }
                }
                
                attr = entry.getAttribute("registeredAddress");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setRegisteredAddress((String)enumVals.nextElement());
                    }
                }
                
                attr = entry.getAttribute("telephoneNumber");
                if (attr != null) {
                    enumVals = attr.getStringValues();
                    if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        profile.setState((String)enumVals.nextElement());
                    }
                }
                
                if (results == null) {
                    results = new ArrayList();
                }
                
                results.add(profile);
            }

        } catch (LDAPException e) {
            processError(ld, results, e.toString());
        }
        
        disconnect(ld);
        
        return results;
    }
}