package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kazushi Minagawa
 * @param <S>
 */
public final class PluginLoader<S> {
    
    private static final String PREFIX = "META-INF/plugins/";
    
    private final Class<S> plugin;
    private final ClassLoader loader;
    private final List<String> providers;
    
    public PluginLoader(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        providers = new ArrayList<>();
        readAllEntries();
    }
    
    public Iterator<S> iterator() {
        
        return new Iterator<S>() {
            
            Iterator<String> iter = providers.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public S next() {
                
                if (iter.hasNext()) {
                    String clsName = iter.next();
                    
                    try {
                        S p = plugin.cast(Class.forName(clsName, true, loader).newInstance());
                        return p;
                        
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                    
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }
    
    private void readAllEntries() {
        try {
            try (InputStream in = loader.getResourceAsStream(PREFIX+plugin.getName()); 
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                String line;
                while ((line=reader.readLine())!=null) {
                    if (!line.startsWith("#")) {
                        String[] cmp = line.split("\\s* \\s*");
                        providers.add(cmp[0]);
                    }
                }
            }
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static <S> PluginLoader<S> load(Class<S> plugin, ClassLoader loader) {
	return new PluginLoader<>(plugin, loader);
    }
    
    public static <S> PluginLoader<S> load(Class<S> plugin) {
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	return PluginLoader.load(plugin, cl);
    }
}
