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

/**
 * Factory class to create Data Access Object.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampDaoFactory {

    /** 
     * Creates new DaoFactory 
     */
    public StampDaoFactory() {
    }
    
    private static LDAPDaoBean createBean(Object o, String keyString) {
        LDAPDaoBean bean = null;
        
        try {
            String className = ClientContext.getString(keyString);
            bean = (LDAPDaoBean)Class.forName(className).newInstance();
        }
        catch (Exception e) {
            //assert false : e;
        }
        return bean;
    }
    
    public static Object createLocalDao(Object o, String keyString) {
        
        LDAPDaoBean bean = createBean(o, keyString);
        
        if (bean != null) {
            String host = Project.getHostAddress();
            int port = Project.getHostPort();
            String bindDN = "cn=Manager,o=Dolphin";
            String passwd = null;
            if (Project.getName().equals("debug")) {
                passwd = "hanagui";
            }
            else {
                passwd = "secret";
            }
            bean.setHost(host);
            bean.setPort(port);
            bean.setBindDN(bindDN);
            bean.setPasswd(passwd);
        }
        
        return (Object)bean;
    }
    
    public static Object createAspDao(Object o, String keyString) {
        
        LDAPDaoBean bean = createBean(o, keyString);
        
        if (bean != null) {
            bean.setHost("hakkoda.digital-globe.co.jp");
            bean.setPort(389);
            bean.setBindDN("cn=Manager,o=digital-globe");
            bean.setPasswd("hanagui+");
        }
        
        return (Object)bean;
    }
}