package open.dolphin.delegater;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import open.dolphin.project.Project;
import open.dolphin.util.HashUtil;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class JerseyClient {

    private static final JerseyClient instance = new JerseyClient();
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private String baseURI;
    private WebResource webResource;
    private String userName;
    private String password;

    private JerseyClient() {
    }

    public static JerseyClient getInstance() {
        return instance;
    }

    public void setUpAuthentication(String username, String password, boolean hashPass) {
        try {
            this.userName = username;
            this.password = hashPass ? password : HashUtil.MD5(password);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String uri) {

        String oldURI = baseURI;
        baseURI = uri;

        if (baseURI == null || baseURI.equals(oldURI)) {
            return;
        }

        int readTimeout = Project.getInt("jersey.read.timeout")*1000;

        Client client = Client.create();
        client.setReadTimeout(readTimeout);
        webResource = client.resource(baseURI);
    }


    public WebResource.Builder getResource(String path) {
        return webResource.path(path).header(USER_NAME, userName).header(PASSWORD, password);
    }
}
