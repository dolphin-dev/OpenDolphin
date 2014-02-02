package open.dolphin.mbean;

/**
 *
 * @author kazushi
 */
public interface PvtServiceMBean extends Runnable {
    
    public void startService() throws Exception;
    
    public void stopService();

    public void register();
    
}
