package open.dolphin.plugin;

/**
 *
 * @author Kazushi Minagawa.
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
