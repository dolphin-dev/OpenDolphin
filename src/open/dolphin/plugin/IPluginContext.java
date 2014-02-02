/*
 * Created on 2005/06/29
 *
 */
package open.dolphin.plugin;

import java.util.Collection;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public interface IPluginContext extends Context {
	
	public Collection<PluginReference> listPluginReferences(String plugPoint) throws NamingException;
	
	public Collection listPluginNames(String plugPoint) throws NamingException;

}
