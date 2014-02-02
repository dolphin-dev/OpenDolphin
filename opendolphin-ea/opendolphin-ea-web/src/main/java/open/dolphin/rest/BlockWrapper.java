package open.dolphin.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * BlockWrapper
 *
 * @author Kazushi Minagawa, Digital Globe, Inc
 */
public final class BlockWrapper extends HttpServletRequestWrapper {
    
    private String remoteUser;


    public BlockWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }
    
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getShortUser() {
        return remoteUser.substring(17);
    }
}
