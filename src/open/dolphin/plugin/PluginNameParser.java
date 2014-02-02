/*
 * Created on 2005/06/20
 *
 */
package open.dolphin.plugin;

/**
 * PluginNameParser
 * 
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.CompoundName;
import javax.naming.NamingException;
import java.util.Properties;

public class PluginNameParser implements NameParser {

    static Properties syntax = new Properties();
    static {
    		syntax.put("jndi.syntax.direction", "left_to_right");
    		syntax.put("jndi.syntax.separator", ".");
    		syntax.put("jndi.syntax.ignorecase", "false");
    		syntax.put("jndi.syntax.escape", "\\");
    		syntax.put("jndi.syntax.beginquote", "'");
    }
    
    public Name parse(String name) throws NamingException {
        return new CompoundName(name, syntax);
    }
}

