/*
 * FileUtils.java
 *
 * Created on 2002/11/17, 23:22
 */

package jp.ac.kumamoto_u.kuh.fc.jsato;

import java.io.*;
/**
 *
 * @author  postgres
 */
public class FileUtils {
    
    /** Creates a new instance of FileUtils */
    private FileUtils() {
    }
    
    static public String fromFile(String filepath) {
        // Read a file contents as single string.
        
        String s = "";
        StringBuffer buf = new StringBuffer();
        try {
            File f = new File(filepath);
            FileInputStream fis = new FileInputStream(f);
            int c = -1;
            while ((c = fis.read()) != -1) {
                // for text file, we should cast int to char.
                buf.append((char)c);
            }
            s = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    
    static public void toFile(String filepath, String s) {
        // Write string  s to the file.
        try {
            File f = new File(filepath);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(s.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
