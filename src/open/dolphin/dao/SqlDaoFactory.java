/*
 * SqlDaoFactory.java
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
import open.dolphin.plugin.IPluginContext;
import open.dolphin.project.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlDaoFactory {
    
    /** Creates a new instance of DaoFactory */
    public SqlDaoFactory() {
    }
    
    /**
     * Creates DataAccessObject
     */
    public static SqlDaoBean create(Object o, String keyString) {
        
        SqlDaoBean dao = null;
        
        try {            
            IPluginContext plCtx = ClientContext.getPluginContext();
            dao = (SqlDaoBean)plCtx.lookup(keyString);
            dao.setDriver("org.postgresql.Driver");
            
            if (keyString.equals("dao.master")) {
                dao.setHost(Project.getClaimAddress());
                dao.setPort(5432) ;
                dao.setDatabase("orca");
                dao.setUser("orca");
                dao.setPasswd("");
                      
            } else {
                dao.setHost(Project.getHostAddress());
                dao.setPort(5432) ;
                dao.setDatabase("dolphin");
                dao.setUser("dolphin");
                dao.setPasswd("");
            }
            
        } catch (Exception e) {
            //assert false : e;
            System.out.println(e);
            e.printStackTrace();
        }
        return dao;
    }
}
