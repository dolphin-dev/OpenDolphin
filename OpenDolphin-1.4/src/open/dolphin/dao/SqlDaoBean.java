package open.dolphin.dao;

import java.sql.*;
import open.dolphin.project.Project;



/**
 * SqlDaoBean
 *
 * @author  Kazushi Minagawa
 */
public class SqlDaoBean extends DaoBean {
    
    String dataBase;

    String driver;
    
    boolean trace = true;
    
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
            logger.warn("Couldn't find the driver!");
            logger.warn("Let's print a stack trace, and exit.");
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
        logger.debug(msg);
    }
    
    protected void printTrace(String msg) {
        if (trace) {
            logger.debug(msg);
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
    
    protected int getHospNum() {
        
        Connection con = null;
        Statement st = null;
        String sql = null;
        int hospNum = 1;
        String jmari = Project.getJMARICode();
        
        StringBuilder sb = new StringBuilder();
        sb.append("select hospnum, kanritbl from tbl_syskanri where kanricd='1001' and kanritbl like '%");
        sb.append(jmari);
        sb.append("%'");
        sql = sb.toString();
        
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                hospNum = rs.getInt(1);
            }
            
        }  catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(st);
        }
        
        return hospNum;
    }
}
