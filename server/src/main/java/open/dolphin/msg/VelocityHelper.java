package open.dolphin.msg;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 *
 * @author kazushi
 */
public class VelocityHelper {
    
    static {
        
        try {
            // Velocity を初期化する
            Properties p = new Properties();
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator).append("templates");
            String resDir = sb.toString();
            // Resource(template) load directory
            p.setProperty("file.resource.loader.path", resDir);
            Velocity.init(p);
            
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }
    
    public static VelocityContext getContext() {
        return new VelocityContext();
    }
}
