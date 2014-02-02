/*
 * AuthenticationDao.java
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

import netscape.ldap.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AuthenticationDao extends LDAPDaoBean {
    
    /** Creates new AuthenticationDao */
    public AuthenticationDao() {
    }
    
    public AuthenticationDao(String host, int port, String userId, String bindDN, String passwd) {
        this();
        this.host = host;
        this.port = port;
        this.user = userId;
        this.bindDN = bindDN;
        this.passwd = passwd;
    }    
        
    public int authenticate() {
        
        int ret = 0;
        
        LDAPConnection ld = new LDAPConnection();
        boolean connected = false;
               
        try {
            ld.connect(host, port);
            connected = true;
            
        } catch (LDAPException e) {
        }
        
        if (! connected ) {
            return -1;
        }
        
        try {
            ld.authenticate(bindDN, passwd);
            ld.disconnect();
            ret = 1;
            
        } catch (LDAPException e2) {
            try {
                ld.disconnect();
                
            } catch (Exception e3) {
            }
        }
        
        return ret;           
    }
}