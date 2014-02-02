package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import open.dolphin.util.*;

/**
 *
 * @author kazm
 */
public class PluginLoader<S> implements Iterable<S> {
    
    private static final String PREFIX = "META-INF/plugins/";
    
    // ロードするプラグインのインターフェイス
    private Class<S> plugin;
    
    // クラスローダ
    private ClassLoader loader;
    
    // 生成順のキャッシュプロバイダ
    private LinkedHashMap<String,S> providers = new LinkedHashMap<String,S>();
    
    // 現在の遅延lookp 反復子
    private LazyIterator lookupIterator;
    
    public void reload() {
        providers.clear();
        lookupIterator = new LazyIterator(plugin, loader);
    }
    
    /** Creates a new instance of PluginLoader */
    private PluginLoader(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        reload();
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
    
    private int parseLine(Class plugin, URL u, BufferedReader r, int lc, List<String> names) 
        throws IOException, PluginConfigurationError {
        
        String ln = r.readLine();
        
	if (ln == null) {
	    return -1;
	}
        
        int ci = ln.indexOf("#");
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                fail(plugin, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(plugin, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    fail(plugin, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            if (!providers.containsKey(ln) && !names.contains(ln)) {
                names.add(ln);
            }
        }
        
        return lc + 1;
    }

    private Iterator<String> parse(Class plugin, URL u) throws PluginConfigurationError {
        
        InputStream in = null;
	BufferedReader r = null;
	ArrayList<String> names = new ArrayList<String>();
        
        try {
            in = u.openStream();
	    r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
	    while ((lc = parseLine(plugin, u, r, lc, names)) >= 0);
            
        } catch (IOException x) {
            fail(plugin, "Error reading configuration file", x);
        } finally {
	    try {
		if (r != null) r.close();
		if (in != null) in.close();
	    } catch (IOException y) {
		fail(plugin, "Error closing configuration file", y);
	    }
	}
        
        return names.iterator();
    }
    
    
    private class LazyIterator implements Iterator<S> {
    
        Class<S> plugin;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<String> pending;
        String nextName;
        
        private LazyIterator(Class<S> plugin, ClassLoader loader) {
	    this.plugin = plugin;
	    this.loader = loader;
	}
        
        public boolean hasNext() {
            
            if (nextName != null){
                return true;
            }
            
            if (configs == null) {
                try {
                    String fullName = PREFIX + plugin.getName();
                    if (loader == null) {
                        configs = ClassLoader.getSystemResources(fullName);
                    } else {
                        configs = loader.getResources(fullName);
                    }
                    
                } catch (IOException x) {
                    fail(plugin, "Error locating configuration files", x);
                }
            }
            
            while ( (pending == null) || (!pending.hasNext()) ) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(plugin, configs.nextElement());
            }
            
            nextName = pending.next();
            return true;
        }
        
        public S next() {
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            String cn = nextName;
            nextName = null;
            
            try {
                S p = plugin.cast(Class.forName(cn, true, loader).newInstance());
                providers.put(cn, p);
		return p;
                
            } catch (ClassNotFoundException x) {
                fail(plugin, "Provider " + cn + " not found");
            } catch (Throwable x) {
                fail(plugin, "Provider " + cn + " could not be instantiated: " + x, x);
            }
            
            throw new Error();		// This cannot happen
        }
        
        public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
    
    public Iterator<S> iterator() {
        
	return new Iterator<S>() {

	    Iterator<Map.Entry<String,S>> knownProviders
		= providers.entrySet().iterator();

	    public boolean hasNext() {
		if (knownProviders.hasNext()) {
		    return true;
                }
		return lookupIterator.hasNext();
	    }

	    public S next() {
		if (knownProviders.hasNext()) {
		    return knownProviders.next().getValue();
                }
		return lookupIterator.next();
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }

	};
    }
    
    public LinkedHashMap<String,S> loadAll() {
        reload();
        Iterator<S> iter = iterator();
        while (iter.hasNext()) {
            iter.next();
        }
        return providers;
    }
    
    public static <S> PluginLoader<S> load(Class<S> plugin, ClassLoader loader) {
	return new PluginLoader<S>(plugin, loader);
    }
    
    public static <S> PluginLoader<S> load(Class<S> plugin) {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	return PluginLoader.load(plugin, cl);
    }
}