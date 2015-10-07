package open.orca.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * 2013/08/29
 * @author kazushi
 */
public class ORCAConnection {
    
    private static final ORCAConnection instane = new ORCAConnection();
    
    //@Resource(mappedName="java:jboss/datasources/ORCADS")
    //private DataSource ds;
    
    private String jdbcURL;
    private String user;
    private String password;
    
//minagawa^    
    private final Properties config;
//minagawa$
    
    public static ORCAConnection getInstance() {
        return instane;
    }
    
    private ORCAConnection() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        
        this.config = new Properties();

        try {
            // 読み込む
            FileInputStream fin = new FileInputStream(f);
            try (InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect")) {
                config.load(r);
            }

            String conn = config.getProperty("claim.conn");
            if (conn!=null && conn.equals("server")) {
                jdbcURL = config.getProperty("claim.jdbc.url");
                user = config.getProperty("claim.user");
                password = config.getProperty("claim.password");
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public Connection getConnection() {
        
        try {
            if (jdbcURL!=null && user!=null && password!=null) {
                Connection conn = DriverManager.getConnection(jdbcURL, user, password);         
                conn.setReadOnly(true);           
                return conn;
            } else {
                DataSource ds = (DataSource)InitialContext.doLookup("java:jboss/datasources/ORCADS");
                return ds.getConnection();
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
    
//minagawa^     
    public Properties getProperties() {
        return this.config;
    }
    
    public String getProperty(String prop) {
        return config.getProperty(prop);
    }
    
    public boolean isSendClaim() {
        String test = config.getProperty("claim.conn");         // connection type
        return test!=null && test.equals("server");
    }
//minagawa$    
}
