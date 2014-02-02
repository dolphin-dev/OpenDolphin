/*
 * SqlAppointSaverDao.java
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
import java.util.*;

import open.dolphin.client.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlAppointSaverDao  extends SqlDaoBean {
    
    /** Creates a new instance of SqlAppointSaverDao */
    public SqlAppointSaverDao() {
    }
    
    public void save(String pid, ArrayList results) {

        Connection conn = null;
        int size = results.size();
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            for (int i = 0; i < size; i++) {

                AppointEntry e = (AppointEntry)results.get(i);
                int state = e.getState();
                String appoName = e.getAppointName();

                if (state == AppointEntry.TT_NEW) {
                    // V‹K—\–ñ
                    add(conn, pid, e.getDate(), appoName, e.getAppointMemo());

                } else if (state == AppointEntry.TT_REPLACE &&  appoName != null) {
                    // •ÏX‚³‚ê‚½—\–ñ
                    modify(conn, e.getDN(), appoName, e.getAppointMemo());

                } else if (state == AppointEntry.TT_REPLACE && appoName == null) {
                    // Žæ‚èÁ‚³‚ê‚½—\–ñ
                    delete(conn, e.getDN());
                }
            }
            
            conn.commit();
        
        } catch (SQLException e) {
            rollback(conn);
            processError(conn, "dummy", "SQLException while saving appointment data:" + e.toString());
        }

        closeConnection(conn);
    }
    
    private void add(Connection conn, String pid, String date, String name, String memo) throws SQLException {
        
        PreparedStatement ps = conn.prepareStatement("insert into tbl_appointment values(?, ?, ?, ?)");
        ps.setString(1, pid);
        ps.setString(2, date);
        ps.setString(3, name);
        String val = memo != null ? memo : null;
        ps.setString(4, val);
        
        ps.executeUpdate();
    }
    
    private void modify(Connection conn, String oid, String name, String memo) throws SQLException {
        
        StringBuffer buf = new StringBuffer();
        buf.append("update tbl_appointment set appointname = ");
        buf.append(addSingleQuote(name));
        if (memo != null) {
            buf.append(", appointmemo = ");
            buf.append(addSingleQuote(memo));
        }
        buf.append(" where oid =");
        buf.append(addSingleQuote(oid));
        String sql = buf.toString();
        printTrace(sql);
        
        Statement st = conn.createStatement();
        st.executeUpdate(sql);
    }
    
    private void delete(Connection conn, String oid) throws SQLException {
        
        StringBuffer buf = new StringBuffer();
        buf.append("delete from tbl_appointment where oid = ");
        buf.append(addSingleQuote(oid));
        String sql = buf.toString();
        printTrace(sql);
        
        Statement st = conn.createStatement();
        st.executeUpdate(sql);
    }    
}