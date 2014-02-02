/*
 * Created on 2005/06/21
 *
 */
package open.dolphin.plugin.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import open.dolphin.plugin.ParseException;
import org.apache.log4j.BasicConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Log4JParser
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class Log4JParser {
    
    private static final String[] EXT_TO_PARSE = {"log4j.prop"};
    private static final String  TARGET_LINE = "log4j.logger.";
    private static final int TAGET_LENGTH = TARGET_LINE.length();
    private static final String[] COMENT_LINE = {"#", "!"};
    private static final String SEPARATER = "=";
    
    public Log4JParser() {
    }
    
    @SuppressWarnings("unchecked")
    public Hashtable parse(String urlSpec, String enc) throws ParseException {
        
        if (isTarget(urlSpec)) {
            
            BufferedReader reader = null;
            URL url = null;
            String path = null;
            
            try {
                url = new URL(urlSpec);
                path = url.getPath();
                //System.out.println(path);
                //path = url.getPath().substring(1);
                //System.out.println(path);
                reader = enc != null
                        ? new BufferedReader(new InputStreamReader(url.openStream(), enc))
                        : new BufferedReader(new InputStreamReader(url.openStream()));
            } catch (MalformedURLException e) {
                throw new ParseException(e.toString());
                
            } catch (IOException ie) {
                throw new ParseException(ie.toString());
            }
            
            Hashtable ret = new Hashtable();
            
            try {
                String line = null;
                String key = null;
                Logger value = null;
                int index = 0;
                
                while ((line = reader.readLine()) != null) {
                    
                    //System.out.println(line);
                    
                    if (isComent(line) || (index = line.indexOf(SEPARATER)) <=0) {
                        continue;
                    }
                    
                    
                    // log4j.logger. で始まるライン
                    if (isTargetLine(line)) {
                        // 012345678901234567
                        // log4j.logger.boot=
                        // target_length = 13
                        // index = 17
                        key = line.substring(TAGET_LENGTH, index).trim();
                        value = (Logger) Logger.getInstance(key);
                        ret.put(key, value);
                        
                    } 
                }
                
                // 設定ファイルでコンフィグレーションをする
                PropertyConfigurator.configure(path);
                
                return ret;
                
            } catch (IOException e) {
                throw new ParseException(e.toString());
            } finally {
                close(reader);
            }
        }
        
        return null;
        
    }
    
    private boolean isTarget(String urlSpec) {
        boolean ret = false;
        for (int i = 0; i < EXT_TO_PARSE.length; i++) {
            if (urlSpec.endsWith(EXT_TO_PARSE[i])) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    private boolean isTargetLine(String line) {
        return line.startsWith(TARGET_LINE);
    }
    
    private boolean isComent(String line) {
        boolean ret = false;
        for (int i = 0; i < COMENT_LINE.length; i++) {
            if (line.startsWith(COMENT_LINE[i])) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    private void close(BufferedReader r) {
        if (r != null) {
            try {
                r.close();
                r = null;
            } catch (IOException e) {
            }
        }
    }
}
