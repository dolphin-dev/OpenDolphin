package open.dolphin.plugin;

import java.util.Collection;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;


/**
 * InitialPluginContext
 * 
 * @author Kazushi Minagawa
 *
 */
public class InitialPluginContext extends InitialContext implements IPluginContext {
	
	
	public InitialPluginContext(Hashtable environment) throws NamingException {
		super(true); // don't initialize yet
	
		// Clone environment and adjust
		Hashtable env = (environment == null) 
		              ? new Hashtable(11) 
		              : (Hashtable)environment.clone();
		init(env);
	}
	
	protected IPluginContext getDefaultInitFooCtx() throws NamingException {
		Context answer = getDefaultInitCtx();
		if (!(answer instanceof IPluginContext)) {
			throw new NoInitialContextException("Not an FooContext");
		}
		return (IPluginContext)answer;
	}

	@SuppressWarnings("unchecked")
	public Collection listPluginReferences(String name) throws NamingException {
		return getDefaultInitFooCtx().listPluginReferences(name);
	}
	
	public Collection listPluginNames(String name) throws NamingException {
		return getDefaultInitFooCtx().listPluginNames(name);
	}

}
