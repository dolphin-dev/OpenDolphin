package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, inc.
 * @param <S>
 */
public class PluginLister<S> {
    
    private static final String PREFIX = "META-INF/plugins/";
    
    // ロードするプラグインのインターフェイス
    private final Class<S> plugin;
    
    // クラスローダ
    private final ClassLoader loader;
    
    /** Creates a new instance of PluginLoader */
    private PluginLister(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
    }
    
    public LinkedHashMap<String,String> getProviders() {
        
        try {
            LinkedHashMap<String,String> providers = new LinkedHashMap<>(10);
            
            String fullName = PREFIX + plugin.getName();
            Enumeration<URL> configs = loader.getResources(fullName);

            while (configs.hasMoreElements()) {

                URL url = configs.nextElement();
                try (InputStream in = url.openStream(); BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        line = line.trim();
                        Scanner s = new Scanner(line).useDelimiter("\\s*,\\s*");
                        String menu = s.next();
                        String cmd = s.next();
                        String value = s.next();
                        providers.put(cmd, value); 
                    }
                }
            }
            return providers;
            
        } catch (IOException ex) {
            Logger.getLogger(PluginLister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static <S> PluginLister<S> list(Class<S> plugin, ClassLoader loader) {
	return new PluginLister<>(plugin, loader);
    }
    
    public static <S> PluginLister<S> list(Class<S> plugin) {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	return PluginLister.list(plugin, cl);
    }
}


































