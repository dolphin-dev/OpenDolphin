/*
 * UUID.java
 *
 * Created on 2002/06/22, 12:27
 */

package jp.ac.kumamoto_u.kuh.fc.jsato;

import java.io.*;
import java.util.*;
/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class UUID {

    /** Creates new UUID */
    private UUID() {
    }

    static public String generateUUIDWindows() {
        /*
        String[] cmd = new String[3];
        cmd[0] = "command.com";
        cmd[1] = "/C" ;
        cmd[2] = "uuidgen";
        */
        
        String cmd = "uuidgen";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() > 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    static public String generateUUIDLinux() {
        String cmd = "uuidgen -t";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() >= 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    static public String generateUUIDMacOSX() {
        String cmd = "uuidgen";
        
        try {
            // invoke command and get result...
            // output strings are stored in the vector
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = null;
            Vector v = new Vector();
            while( (line = br.readLine()) != null ) {
                v.add(line);
                //System.out.println( line );
            }
            p.waitFor();

             // get result
            if (v.size() >= 0) {
                String s = (String)v.firstElement();
                s=s.trim();
                return s;
            } else {
                return "";
            }
        } catch( IOException ex ) {
            System.out.println(
                "*** An IO exception occurred while executing the cmd <" + cmd + ">:" + ex.getMessage() 
            );
            ex.printStackTrace();
            return "";
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }
    
    static public String generateUUID() {
        String osName = System.getProperty("os.name");
        if ( osName.equals("Windows 2000") ) {
            // it is assummed that the platform is WIndows2000
            return generateUUIDWindows();
        } else if ( osName.equals("Linux") ) {
            return generateUUIDLinux();
        } else if ( osName.equals("Mac OS X") ) {
            return generateUUIDMacOSX();
        } else {
            return "";
        }
    }
    
    static public void main(String args[]) {
        for (int i = 0; i < 10; ++i) {
            System.out.println(generateUUID().replaceAll("-",""));
        }
    }
}
