/*
 * SqlStampCheckDao.java
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
 * @author  kazm
 */
public class SqlStampCheckDao extends SqlDaoBean {
    
    /** Creates a new instance of SqlStampCheckDao */
    public SqlStampCheckDao() {
    }
    
    
    public boolean check(String code, String subclassCode) {
        
        Connection con = null;
        
        String table = null;
        
        if (subclassCode.equals("0")) {
            table = "treatment";
            
        } else if (subclassCode.equals("1")) {
            table = "tool_material";
            
        } else if (subclassCode.equals("2")) {
            table = "medicine";
        }
        
        Statement st = null;
        boolean result = true;

        // Constracts sql
        StringBuffer buf = new StringBuffer();
        buf.append("select code from ");
        buf.append(table);
        buf.append(" where code = ");
        buf.append(addSingleQuote(code));
        
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                result = true;
            }
            rs.close();
        
        } catch (SQLException e) {
            processError(con, "dummy", e.toString());
        }
        
        closeStatement(st);
        
        closeConnection(con);
        
        return result;
    }
}
