/*
 * Created on 2005/07/01
 *
 */
package open.dolphin.plugin;

import javax.naming.RefAddr;
import javax.naming.Reference;

/**
 * PluginReference
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class PluginReference extends Reference implements Comparable {
    
    private static final long serialVersionUID = 8535400541348060429L;
    
    // Plugin Type
    public static final String MARKER = "marker";
    
    public static final String SIMPLE = "simple";
    
    public static final String COMPONENT = "component";
    
    public static final String FRAME = "frame";
    
    public static final String PARENT = "parent";
    
    // Address
    public static final String PLUG_POINT = "plugPoint";
    
    public static final String PLUGIN_NAME = "name";
    
    public static final String TYPE = "type";
    
    public static final String CHILD_NUMBER = "childNumber";
    
    public static final String TITLE = "title";
    
    public static final String ICON = "icon";
    
    public static final String SELECTED_ICON = "selectedIcon";
    
    public static final String INTF_NAME = "intfName";
    
    public PluginReference(String className, String factory, String factoryLocation) {
        super(className, factory, factoryLocation);
    }
    
    public String getJndiName() {
        return PluginParser.constructJndiName(
                (String) getAddrContent(PLUG_POINT),
                (String) getAddrContent(PLUGIN_NAME));
    }
    
    public boolean isPlugPoint(String plugPoint) {
        String myPoint = (String) getAddrContent(PLUG_POINT);
        return plugPoint.equals(myPoint);
    }
    
    public Object getAddrContent(String type) {
        RefAddr addr = get(type);
        return addr != null ? addr.getContent() : null;
    }
    
    public int compareTo(Object obj) {
        
        if (obj instanceof PluginReference) {
            
            PluginReference another = (PluginReference) obj;
            
            // plugPoint ‚ð”äŠr
            String myPoint = (String) getAddrContent(PLUG_POINT);
            String anotherPoint = (String) another.getAddrContent(PLUG_POINT);
            
            int result = myPoint.compareTo(anotherPoint);
            
            if (result != 0) {
                return result;
            }
            
            // childNumber ‚ð”äŠr
            String myChildNumber = (String) getAddrContent(CHILD_NUMBER);
            String anotherNumber = (String) another.getAddrContent(CHILD_NUMBER);
            
            if (myChildNumber == null && anotherNumber == null) {
                return 0;
            } else if (myChildNumber == null && anotherNumber != null) {
                return 1;
            } else if (myChildNumber != null && anotherNumber == null) {
                return -1;
            } else {
                return myChildNumber.compareTo(anotherNumber);
            }
        }
        return -1;
    }
    
    public String toString() {
        String title = (String) getAddrContent(TITLE);
        return title == null ? (String) getAddrContent(PLUGIN_NAME) : title;
    }
}
