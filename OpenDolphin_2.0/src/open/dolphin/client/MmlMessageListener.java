package open.dolphin.client;


/**
 * MML イベントリスナ。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public interface MmlMessageListener extends MainService {
    
    public String getCSGWPath();
    
    public void setCSGWPath(String val);
    
    public void mmlMessageEvent(MmlMessageEvent e);
    
}