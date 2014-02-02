/*
 */
package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.StringRefAddr;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * dolphin.xml をパースしプラグポイントを JNDI名にして Context にバインドする。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PluginParser {
    
    private final String ELE_PLUGINS = "plugins";
    
    private final String ELE_PLUGIN = "plugin";
    
    private final String ATTR_POINT = "plugPoint";
    
    private final String ATTR_NUMBER = "childNumber";
    
    private final String ATTR_TYPE = "type";
    
    private final String ELE_NAME = "name";
    
    private final String ELE_TITLE = "title";
    
    private final String ELE_CLASS = "class";
    
    private final String ELE_INTF = "intf";
    
    private final String ELE_ICON = "icon";
    
    private final String ELE_SICON = "selectedIcon";
    
    private final String DEFAULT_FACTORY = "open.dolphin.plugin.PluginFactory";
    
    private String defaultFactory = DEFAULT_FACTORY;
    
    private boolean DEBUG;
    
    /**
     * JNDI name を生成する -> plugPoint.name
     */
    public static String constructJndiName(String plugPoint, String name) {
        StringBuilder buf = new StringBuilder();
        buf.append(plugPoint);
        buf.append(".");
        buf.append(name);
        return buf.toString().replace('/', '.');
        // '/' をNaming context が扱える . に変換する
    }
    
    public PluginParser() {
    }
    
    public PluginParser(String defaultFactory) {
        setDefaultFactory(defaultFactory);
    }
    
    public void setDefaultFactory(String defaultFactory) {
        this.defaultFactory = defaultFactory;
    }
    
    public Hashtable parse(URL url, String encoding) throws PluginException {
        
        try {
            BufferedReader r = encoding != null ? new BufferedReader(
                    new InputStreamReader(url.openStream(), encoding))
                    : new BufferedReader(
                    new InputStreamReader(url.openStream()));
            
            SAXBuilder docBuilder = new SAXBuilder();
            Document doc = docBuilder.build(r);
            
            Hashtable ret = new Hashtable();
            Element root = doc.getRootElement().getChild(ELE_PLUGINS);
            parsePlugin(root, ret);
            
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new PluginException(e.toString());
        }
    }
    
    /**
     * <plugins> 以下の要素 <plugin> をパースし PluginSpec を生成する。
     */
    @SuppressWarnings("unchecked")
    private void parsePlugin(Element root, Hashtable ht) throws PluginException {
        
        List children = root.getChildren();
        
        for (Iterator iterator = children.iterator(); iterator.hasNext(); ) {
            
            Element child = (Element) iterator.next();
            String ename = child.getName();
            
            if (ename.equals(ELE_PLUGIN)) {
                
                String plugPoint = child.getAttributeValue(ATTR_POINT); 	// plug point
                String number = child.getAttributeValue(ATTR_NUMBER); 		// child number;
                String type = child.getAttributeValue(ATTR_TYPE); 		// plugin type;
                String name = child.getChildTextTrim(ELE_NAME); 		// name
                String title = child.getChildTextTrim(ELE_TITLE); 		// tite
                String icon = child.getChildTextTrim(ELE_ICON); 		// icon
                String selectedIcon = child.getChildTextTrim(ELE_SICON); 	// selected icon
                String implName = child.getChildTextTrim(ELE_CLASS); 		// className
                String intfName = child.getChildTextTrim(ELE_INTF); 		// interface
                
                if (plugPoint == null) {
                    throw new PluginException("プラグポイントが定義されていません");
                }
                
                if (name == null) {
                    throw new PluginException("プラグイン名が定義されていません");
                }
                
                if (implName == null) {
                    throw new PluginException("実装クラス名が定義されていません");
                }
                
                type = type == null ? PluginReference.SIMPLE : type;
                
                // JNDI name を作成する
                String jndiName = PluginParser.constructJndiName(plugPoint, name);
                
                // Reference
                PluginReference plRef = new PluginReference(implName, defaultFactory, null);
                plRef.add(new StringRefAddr(ATTR_POINT, plugPoint));
                plRef.add(new StringRefAddr(ATTR_TYPE, type));
                plRef.add(new StringRefAddr(ELE_NAME, name));
                plRef.add(new StringRefAddr(ATTR_NUMBER, number));
                plRef.add(new StringRefAddr(ELE_TITLE, title));
                plRef.add(new StringRefAddr(ELE_ICON, icon));
                plRef.add(new StringRefAddr(ELE_SICON, selectedIcon));
                plRef.add(new StringRefAddr(ELE_INTF, intfName));
                debug(plRef);
                ht.put(jndiName, plRef);
            }
        }
    }
    
    private void debug(PluginReference ref) {
        if (DEBUG) {
            System.out.println(ref.toString());
        }
    }
}