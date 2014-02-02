package open.dolphin.server;

import open.dolphin.client.MainService;

/**
 * @author Kazushi, Minagawa
 */
public interface PVTServer extends MainService {   
    
    public int getPort();
    
    public void setPort(int port);
    
    public String getEncoding();
    
    public void setEncoding(String enc);
}
