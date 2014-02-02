/*
 * PluginConfigurationError.java
 *
 * Created on 2007/10/06, 9:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.plugin;

/**
 *
 * @author kazm
 */
public class PluginConfigurationError extends Error {
    
    /** Creates a new instance of PluginConfigurationError */
    public PluginConfigurationError(String msg) {
        super(msg);
    }
    
    public PluginConfigurationError(String msg, Throwable cause) {
	super(msg, cause);
    }
}
