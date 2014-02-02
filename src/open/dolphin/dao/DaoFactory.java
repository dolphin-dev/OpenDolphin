/*
 * DaoFactory.java
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

import open.dolphin.client.*;
import open.dolphin.project.*;
import netscape.ldap.*;

/**
 * Factory class to create Data Access Object.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DaoFactory {
    
    /** 
     * Creates new DaoFactory 
     */
    public DaoFactory() {
    }
    
    /**
     * Creates DataAccessObject
     */
    public static Object create(Object o, String keyString) {
        
        LDAPDaoBean ret = null;
        String className = ClientContext.getString(keyString);
        
        try {            
            ret = (LDAPDaoBean)Class.forName(className).newInstance();
            ret.setHost(Project.getHostAddress());
            ret.setPort(Project.getHostPort());            
            //ret.setBindDN(Project.getAuthenticationDN());
            //ret.setPasswd(Project.getPasswd());
            ret.setBindDN("cn=Manager,o=Dolphin");
            if (Project.getName().equals("debug")) {
                ret.setPasswd("hanagui");
            }
            else {
                ret.setPasswd("secret");
            }

        }
        catch (Exception e) {
            //assert false : e;
        }
        return (Object)ret;
    }
        
    public static Object createMemberDao(Object o) {
        
        LDAPDaoBean ret = null;
        String className = ClientContext.getString("dao.member");
        
        try {            
            ret = (LDAPDaoBean)Class.forName(className).newInstance(); 
            ret.setHost(Project.getHostAddress());
            ret.setPort(5121);
            ret.setBindDN(Project.getAuthenticationDN());
            ret.setPasswd(Project.getPasswd());
            
        } catch (Exception e) {
            //assert false : e;
        }
        
        return (Object)ret;
    }    
    
    public static LDAPConnection createConnection(Object o) {
        
        LDAPConnection ld = new LDAPConnection();
        try {
            ld.connect(Project.getHostAddress(),Project.getHostPort(),Project.getAuthenticationDN(),Project.getPasswd());
        }
        catch (LDAPException e) {
            ld = null;
        }
        return ld;
    }
}