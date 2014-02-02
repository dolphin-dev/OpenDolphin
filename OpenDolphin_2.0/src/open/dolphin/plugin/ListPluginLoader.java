package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 *
 * @author Kazushi Minagawa.
 */
public class ListPluginLoader<S> implements Iterable<S> {
    
    private static final String PREFIX = "META-INF/plugins/";
    
    // ロードするプラグインのインターフェイス
    private Class<S> plugin;
    
    // クラスローダ
    private ClassLoader loader;
    
    // プロバイダキャッシュ
    private HashMap<String, S> providers;
    
    // 実際のプラグイン反復子
    private ActualIterator actualIterator;
    
    
    /** Creates a new instance of PluginLoader */
    private ListPluginLoader(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        providers = new HashMap<String, S>();
        actualIterator = new ActualIterator(plugin, loader);
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
    
    protected class IdValuePair {
        
        private String id;
        private String value;
        
        public IdValuePair(String id, String value) {
            this.id = id;
            this.value = value;
        }
        
        public String getId() {
            return id;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * プラグイン反復子の実際の機能を提供する内部クラス。
     */
    private class ActualIterator implements Iterator<S> {
    
        Class<S> plugin;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<IdValuePair> iterator;
        
        private ActualIterator(Class<S> plugin, ClassLoader loader) {
            
	    this.plugin = plugin;
	    this.loader = loader;
            
            try {
                String fullName = PREFIX + plugin.getName();        
                configs = loader.getResources(fullName);
                
            } catch (Exception x) {
                fail(plugin, "Error locating plugin configuration files", x);
            }
            
            try {
                
                ArrayList<IdValuePair> allPlugins = new ArrayList<IdValuePair>();
                
                while (configs.hasMoreElements()) {
                    
                    URL url = configs.nextElement();
                    InputStream in = url.openStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line = null;
                    while ((line=r.readLine()) != null) {
                        line = line.trim();
                        int index = line.indexOf("=");
                        if (index > 0) {
                            String id = line.substring(0, index++);
                            String value = line.substring(index);
                            allPlugins.add(new IdValuePair(id, value));
                        }
                    }
                    
                    r.close();
                    in.close();
                }
                
                iterator = allPlugins.iterator();  
                    
            } catch (IOException x) {
                fail(plugin, "Error reading plugin configuration files", x);
            }
	}
        
        @Override
        public boolean hasNext() {
            
            return iterator.hasNext();
        }
        
        @Override
        public S next() {
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            IdValuePair idValue = iterator.next();
            String className = idValue.getValue();
            String id = idValue.getId();
            
            try {
                S p = plugin.cast(Class.forName(className, true, loader).newInstance());
                providers.put(id, p);
		return p;
                
            } catch (ClassNotFoundException x) {
                fail(plugin, "Provider " + className + " not found");
            } catch (Throwable x) {
                fail(plugin, "Provider " + className + " could not be instantiated: " + x, x);
            }
            
            throw new Error();		// This cannot happen
        }
        
        @Override
        public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
    
    @Override
    public Iterator<S> iterator() {
        
	return new Iterator<S>() {

            @Override
	    public boolean hasNext() {
		return actualIterator.hasNext();
	    }

            @Override
	    public S next() {
		return actualIterator.next();
	    }

            @Override
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
    
    public HashMap<String, S> getProviders() {
        return providers;
    }
    
    public static <S> ListPluginLoader<S> load(Class<S> plugin, ClassLoader loader) {
	return new ListPluginLoader<S>(plugin, loader);
    }
    
    public static <S> ListPluginLoader<S> load(Class<S> plugin) {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	return ListPluginLoader.load(plugin, cl);
    }
}


































