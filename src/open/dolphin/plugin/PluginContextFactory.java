package open.dolphin.plugin;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import open.dolphin.plugin.helper.Log4JParser;
import open.dolphin.plugin.helper.PropertyParser;


/**
 * Dolphin Client のコンテキストクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PluginContextFactory implements InitialContextFactory {
    
    private final String RESOURCE_FILE 	= "/open/dolphin/resources/Dolphin.sjis.properties";
    private final String RESOURCE_ENCODING 	= "SHIFT_JIS";
    //private final String RESOURCE_FILE 		= "/open/dolphin/resources/Dolphin.properties";
    //private final String RESOURCE_ENCODING 	= "ASCII";
    private final String DOLPHIN_XML = "/open/dolphin/resources/dolphin.xml";
    
    @SuppressWarnings("unchecked")
    public Context getInitialContext(Hashtable env) throws NamingException {
        
        try {
            // 1. bindings hashtable
            Hashtable bindings = new Hashtable();
            
            // 2. application の resource を context にバインドする
            URL url = this.getClass().getResource(RESOURCE_FILE);
            PropertyParser parser = new PropertyParser();
            Hashtable ht = parser.parse(url, RESOURCE_ENCODING);
            hashCopy(ht, bindings);
            
            // 3. application の base directory を解決する
            String baseDir = (String)bindings.get("base.dir");
            if (baseDir != null) {
                if (baseDir.equals("user.dir") || baseDir.equals("user.home")) {
                    bindings.put("base.dir", System.getProperty(baseDir));
                }
            } else {
                bindings.put("base.dir", System.getProperty("user.dir"));
            }
            //System.out.println("base directory=" + baseDir);
            
            // 4. 変数 ${val} を解決する
            Enumeration en = bindings.keys();
            while (en.hasMoreElements()) {
                String key = (String)en.nextElement();
                String value = (String)bindings.get(key);
                value = resolve(value, bindings);
                bindings.put(key, value);
            }
            //debug(bindings);
            
            // 5. log config file をcontext にバインドする
            String logConfigFile = (String)bindings.get("log.config.file");
            File file = new File(logConfigFile);
            if (file.exists()) {
                //System.out.println("log4j.prop exists");
                url = file.toURL();
                Log4JParser log4jParser = new Log4JParser();
                ht = log4jParser.parse(url.toString(), null);
                hashCopy(ht, bindings);
            }
            
            // 6. Application preference をバインドする
            //String appliNode = (String)bindings.get("preference.application.node");
            //Preferences prefs = Preferences.userRoot().node(appliNode);
            //bindings.put("preferences.application",prefs);
            
            // 7. Plugin を読み込む
            PluginParser pluginParser = new PluginParser();
            url = this.getClass().getResource(DOLPHIN_XML);
            ht = pluginParser.parse(url, RESOURCE_ENCODING);
            hashCopy(ht, bindings);
            
            // context を返す
            PluginContextImpl impl = new PluginContextImpl(env, bindings);
            return impl;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new NamingException(e.toString());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void hashCopy(Hashtable from, Hashtable to) {
        if (from != null && to !=null) {
            Enumeration en = from.keys();
            Object key = null;
            while(en.hasMoreElements()) {
                key = (String)en.nextElement();
                to.put(key, from.get(key));
            }
        }
    }
    
    private String resolve(String line, Hashtable bindings) {
        
        int start = -1;
        int end = -1;
        
        // 再帰の終了条件 ${key} がないこと
        if ( ((start = line.indexOf("${")) >= 0) && ((end = line.indexOf("}", start+2)) > start) ) {
            
            String key = line.substring(start+2, end);
            String value = (String)bindings.get(key);
            if (value == null) {
                return line;
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(line.substring(0, start));
                sb.append(value);
                sb.append(line.substring(end+1));
                line = resolve(sb.toString(), bindings);
            }
        }
        
        return line;
    }
    
//    private void debug(Hashtable ht) {
//        if (ht != null) {
//            Enumeration enums = ht.keys();
//            while (enums.hasMoreElements()) {
//                String key = (String)enums.nextElement();
//                String value = (String)ht.get(key);
//                System.out.println(key + "=" + value);
//            }
//        }
//    }
}