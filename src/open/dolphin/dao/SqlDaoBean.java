/*
 * SqlDaoBean.java
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

import open.dolphin.client.ClientContext;


/**
 * SqlDaoBean
 *
 * @author  Kazushi Minagawa
 */
public class SqlDaoBean extends DaoBean {
    
    String dataBase;

    String driver;
    
    boolean trace = false;
    
    /** 
     * Creates a new instance of SqlDaoBean 
     */
    public SqlDaoBean() {
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {

        this.driver = driver;

        try {
            Class.forName(driver);
			  
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Couldn't find the driver!");
            System.out.println("Let's print a stack trace, and exit.");
            cnfe.printStackTrace();
            System.exit(1);
        }
    }

    public String getDatabase() {
        return dataBase;
    }

    public void setDatabase(String base) {
        dataBase = base;
    }

    protected String getURL() {
        StringBuffer buf = new StringBuffer();
        buf.append("jdbc:postgresql://");
        buf.append(host);
        buf.append(":");
        buf.append(port);
        buf.append("/");
        buf.append(dataBase);
        return buf.toString();
    }
     
    public boolean getTrace() {
        return trace;
    }
    
    public void setTrace(boolean b) {
        trace = b;
    }

    /*public Connection getConnection() {

        Connection con = null;

        try {
            con = DriverManager.getConnection(getURL(), user, passwd);
            System.out.println("Got connection");
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Exception at connection");
            System.out.println("Class Name=" + e.getClass().getName());
            System.out.println("Cause = " + e.getCause());
            System.out.println("Message = " + e.getMessage());
            System.out.println("toString = " + e);
        }
        return con;
    }*/
    
    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(getURL(), user, passwd);
    }

    public String addSingleQuote(String s) {
        StringBuilder buf = new StringBuilder();
        buf.append("'");
        buf.append(s);
        buf.append("'");
        return buf.toString();
    }

    /**
     * To make sql statement ('xxxx',)<br>
     */
    public String addSingleQuoteComa(String s) {
        StringBuilder buf = new StringBuilder();
        buf.append("'");
        buf.append(s);
        buf.append("',");
        return buf.toString();
    }
    
    public void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e) {
            	e.printStackTrace();
            }
        }
    }

    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void debug(String msg) {
    	if (ClientContext.isDebug()) {
    		System.out.println(msg);
    	}
    }
    
    protected void printTrace(String msg) {
        if (trace) {
            System.out.println(msg);
        }
    }
    
    protected void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    
}
