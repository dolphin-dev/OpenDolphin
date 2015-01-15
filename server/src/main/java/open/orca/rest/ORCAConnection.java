/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    private static ORCAConnection instane = new ORCAConnection();
    
    private String jdbcURL;
    private String user;
    private String password;
    //private int port = 5432;
    
    public static ORCAConnection getInstance() {
        return instane;
    }
    
    private ORCAConnection() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        
        Properties config = new Properties();

        try {
            // 読み込む
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();

            String conn = config.getProperty("claim.conn");
            if (conn!=null && conn.equals("server")) {
                jdbcURL = config.getProperty("claim.jdbc.url");
                user = config.getProperty("claim.user");
                password = config.getProperty("claim.password");
            }
            
        } catch (Exception e) {
        }
    }
    
    public Connection getConnection() {
        
        try {
            //return DriverManager.getConnection(jdbcURL, user, password);
            Connection conn = DriverManager.getConnection(jdbcURL, user, password);
            conn.setReadOnly(true);
            return conn;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
}
