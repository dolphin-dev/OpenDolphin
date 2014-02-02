package open.dolphin.server;

import open.dolphin.client.MainService;

/**
 * @author Kazushi, Minagawa
 */
public interface PVTServer extends MainService {
    
    public String getBindAddress();
    
    public void setBindAddress(String bindAddress);
    
    public int getPort();
    
    public void setPort(int port);
    
    public String getEncoding();
    
    public void setEncoding(String enc);
}
