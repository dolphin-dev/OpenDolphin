/*
 * KumamotoDao.java
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

import java.sql.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class KumamotoDao extends SqlDaoBean {

    /** Creates new KumamotoDao */
    public KumamotoDao() {
    }
    
    public String fetchLocalId(String pid) {
        
        Connection conn = null;
        String localId = null;
        java.sql.Statement st= null;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("select localId from patient where pid = ");
            buf.append(addSingleQuote(pid));
            String sql = buf.toString();
            printTrace(sql);
            
            st= conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                localId = rs.getString(1);
            }
            
            rs.close();
                
        } catch (SQLException e) {
            processError(conn, localId, "SQLException while getting the LOcalID: " + e.toString());;
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return localId;
    }
}