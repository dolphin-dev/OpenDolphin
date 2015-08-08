package open.orca.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * 2013/08/29
 * @author kazushi
 */
public class ORCAConnection {
    
    private static final ORCAConnection instane = new ORCAConnection();
    
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
            Connection conn = DriverManager.getConnection(jdbcURL, user, password);
//minagawa^            
            conn.setReadOnly(true);
//minagawa$            
            return conn;
        } catch (Exception e) {
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
