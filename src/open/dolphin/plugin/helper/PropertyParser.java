/*
 */
package open.dolphin.plugin.helper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class PropertyParser {
    
    private String[] COMMENTS = new String[]{"#", "!"};
    private String EQUAL = "=";
    
    
    public PropertyParser() {
    }
    
    @SuppressWarnings("unchecked")
    public Hashtable parse(URL url, String encoding) throws Exception {
        
        BufferedReader reader = null;
        Hashtable table = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
            
        } catch (Exception e) {
            throw (Exception)e;
        }
        
        try {
            table = new Hashtable();
            String line = null;
            String key = null;
            String val = null;
            int index = 0;
            
            while ((line = reader.readLine()) != null) {
                
                if (line.startsWith(COMMENTS[0]) || line.startsWith(COMMENTS[1])) {
                    continue;
                }
                
                index = line.indexOf(EQUAL);
                if (index > 0) {
                    key = line.substring(0, index).trim();
                    val = line.substring(index + 1).trim();
                    table.put(key, val);
                }
            }
            
            return table;
            
        } catch (IOException e) {
            throw (Exception)e;
            
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {}
        }
    }
}
