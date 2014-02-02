package open.dolphin.client;

import java.util.prefs.Preferences;

/**
 *
 * @author Minagawa,Kazushi
 */
public class Uninstall {
    
    private static final String DOLPHIN_NODE = "/open";
    
    /** Creates a new instance of Uninstall */
    public Uninstall() {
        
        clearPrefernces();
        
        deleteDirectories();
    }
    
    private void clearPrefernces() {
        
        try {
            Preferences prefs = Preferences.userRoot().node(DOLPHIN_NODE);
            prefs.removeNode();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void deleteDirectories() { 
    }
    
    public static void main(String[] args) {
        new Uninstall();
        System.exit(0);
    }
}
