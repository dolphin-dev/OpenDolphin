package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, inc.
 */
public class PluginLister<S> {
    
    private static final String PREFIX = "META-INF/plugins/";
    
    // ロードするプラグインのインターフェイス
    private Class<S> plugin;
    
    // クラスローダ
    private ClassLoader loader;
    
    
    /** Creates a new instance of PluginLoader */
    private PluginLister(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
    }
    
    private static void fail(Class plugin, String msg, Throwable cause) throws PluginConfigurationError {
	throw new PluginConfigurationError(plugin.getName() + ": " + msg, cause);
    }

    private static void fail(Class plugin, String msg) throws PluginConfigurationError {
	throw new PluginConfigurationError(plugin.getName() + ": " + msg);
    }

    private static void fail(Class plugin, URL u, int line, String msg) throws PluginConfigurationError {
	fail(plugin, u + ":" + line + ": " + msg);
    }
    
    public LinkedHashMap<String,String> getProviders() {
        
        LinkedHashMap<String,String> providers = new LinkedHashMap<String, String>(10);

        try {
            String fullName = PREFIX + plugin.getName();
            Enumeration<URL> configs = loader.getResources(fullName);

            while (configs.hasMoreElements()) {

                URL url = configs.nextElement();
                InputStream in = url.openStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    Scanner s = new Scanner(line).useDelimiter("\\s*,\\s*");
                    String menu = s.next();
                    String cmd = s.next();
                    String value = s.next();
                    providers.put(cmd, value); 
                }

                r.close();
                in.close();
            }

        } catch (IOException x) {
            fail(plugin, "Error reading plugin configuration files", x);
        }
        
        return providers;
    }
    
    public static <S> PluginLister<S> list(Class<S> plugin, ClassLoader loader) {
	return new PluginLister<S>(plugin, loader);
    }
    
    public static <S> PluginLister<S> list(Class<S> plugin) {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	return PluginLister.list(plugin, cl);
    }
}


































