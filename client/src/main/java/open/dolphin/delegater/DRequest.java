/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.delegater;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import javax.ws.rs.core.MediaType;
import open.dolphin.client.Dolphin;
import open.dolphin.project.Project;

/**
 * 2013/08/29
 * @author kazushi
 */
public class DRequest implements Callable {
    
    private static final String ACCEPT = "Accept";
    private static final String CLINET_UUID = "clientUUID";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    
    private URL request;

    public DRequest(URL request) {
        this.request = request;
    }

    @Override
    public DResponse call() throws Exception {
        URLConnection conn = request.openConnection();
        conn.setRequestProperty(ACCEPT, MediaType.APPLICATION_JSON);
        conn.setRequestProperty(USER_NAME, Project.getUserModel().getUserId());
        conn.setRequestProperty(PASSWORD, Project.getUserModel().getPassword());
        conn.setRequestProperty(CLINET_UUID, Dolphin.getInstance().getClientUUID());
        conn.setDoInput(true);
        conn.setReadTimeout(Integer.MAX_VALUE);
        return new DResponse(conn.getInputStream());
    }
}
