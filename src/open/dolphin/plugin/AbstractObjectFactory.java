/*
 * Created on 2005/06/20
 *
 */
package open.dolphin.plugin;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public abstract class AbstractObjectFactory implements ObjectFactory  {
	
	public static final Properties KEY_SYNTAX = new Properties();
	public static final Properties VALUE_SYNTAX = new Properties();
	
    static {
    	// Resource(Property) File ÇÃ key ïî
    	KEY_SYNTAX.put("jndi.syntax.direction", "left_to_right");
        KEY_SYNTAX.put("jndi.syntax.separator", ".");
        KEY_SYNTAX.put("jndi.syntax.ignorecase", "false");
        KEY_SYNTAX.put("jndi.syntax.escape", "\\");
        KEY_SYNTAX.put("jndi.syntax.beginquote", "'");
        
        // Resource(Property) File ÇÃ value ïî
        VALUE_SYNTAX.put("jndi.syntax.direction", "left_to_right");
        VALUE_SYNTAX.put("jndi.syntax.separator", ",");				// 	ÉJÉìÉ}ãÊêÿÇË
        VALUE_SYNTAX.put("jndi.syntax.ignorecase", "false");
        VALUE_SYNTAX.put("jndi.syntax.escape", "\\");
        VALUE_SYNTAX.put("jndi.syntax.beginquote", "'");
    }
    
	public AbstractObjectFactory() {
	}
	
    public abstract Object getObjectInstance(Object obj,
			    					Name name,
									Context ctx,
									Hashtable env) throws Exception;
    
    public static CompoundName getKeyCompoundName(Name name) throws InvalidNameException {
    	return new CompoundName(name.get(0), KEY_SYNTAX);
    }
    
    public static CompoundName getKeyCompoundName(String name) throws InvalidNameException {
    	return new CompoundName(name, KEY_SYNTAX);
    }
    
    public static CompoundName getValueCompoundName(String name) throws InvalidNameException {
    	return new CompoundName(name, VALUE_SYNTAX);
    }
}
