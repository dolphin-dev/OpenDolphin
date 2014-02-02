/*
 * SqlTest.java
 * Copyright (C) 2003 Dolphin Project. All rights reserved.
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

import java.sql.*;
import java.io.*;

/**
 *
 * @author  kazm
 */
public class SqlTest {
    
    String dataBase;
    String host;
    int port;
    String user;
    String passwd;
    
    /** Creates a new instance of SqlTest */
    public SqlTest(String host, int port, String dataBase, String user, String passwd) {
        
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.user = user;
        this.passwd = passwd;
    }
    
    public Connection getConnection() {
    
        java.sql.Connection conn = null;
        
        try {
            
            StringBuffer buf = new StringBuffer();
            buf.append("jdbc:postgresql://");
            buf.append(host);
            buf.append(":");
            buf.append(port);
            buf.append("/");
            buf.append(dataBase);
            String url = buf.toString();
            System.out.println(url);
            
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url, user, passwd);
            
            System.out.println("Get Connection");
            
        } catch (Exception e) {
            
            System.out.println(e);
        }
        
        return conn;
    }
    
    public void createTable() {
        
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }
        
        Statement st = null;
        String sql = "create table test(itemName text, itemValue text, unit text, confirmDate text, code text, codeSystem text)";
        
        try {
            
            st = conn.createStatement();
            st.execute(sql);
           
            st.close();
            conn.close();
            System.out.println("OK");
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    public void dropTable(String table) {
        
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }
        
        Statement st = null;
        String sql = "drop table " + table;
        
        try {
            
            st = conn.createStatement();
            st.execute(sql);
           
            st.close();
            conn.close();
            System.out.println("OK");
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    public void vacuum() {
        
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }
        
        Statement st = null;
        String sql = "vacuum";
        
        try {
            
            st = conn.createStatement();
            st.execute(sql);
           
            st.close();
            conn.close();
            System.out.println("OK");
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    public void printTensu() {
        
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }
        
        Statement st = null;
        String sql = "select srycd,name from tbl_tensu where srycd like '6%' order by srycd";
        
        try {
            
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            PrintWriter writer = new PrintWriter(new FileWriter("D:/develop/tesu_dump.txt"));
            
            while (rs.next()) {
                
                writer.println(rs.getString(1) + "\t" + rs.getString(2));
            }
            
            writer.flush();
            st.close();
            conn.close();
            System.out.println("Print end");
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    public static void main(String[] args) {
        
        SqlTest test = new SqlTest("172.168.158.2", 5432, "dolphin", "dolphin", "");
        //test.printTensu();
        //test.createTable();
        //test.dropTable("test");
        test.vacuum();
        System.exit(1);
    }
    
}
