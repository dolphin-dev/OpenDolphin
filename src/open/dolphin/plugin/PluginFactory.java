/*
 * Created on 2005/06/23
 *
 */
package open.dolphin.plugin;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

import open.dolphin.client.IMainWindowPlugin;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 * 
 */
public class PluginFactory extends AbstractObjectFactory {

	public Object getObjectInstance(Object obj, Name name, Context ctx,
			Hashtable env) throws Exception {

		if (obj instanceof PluginReference) {

			try {
				PluginReference ref = (PluginReference) obj;
				String implClass = ref.getClassName();
				
				Object ret = Class.forName(implClass).newInstance();
				
				if (ret instanceof IMainWindowPlugin) {
					((IMainWindowPlugin) ret).setTitle((String) ref
							.getAddrContent(PluginReference.TITLE));
					((IMainWindowPlugin) ret).setJNDIName(name.toString()
							.replace('.', '/'));
				}
				return ret;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}
}
