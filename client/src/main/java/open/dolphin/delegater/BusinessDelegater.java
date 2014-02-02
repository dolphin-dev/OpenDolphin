package open.dolphin.delegater;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import open.dolphin.client.ClientContext;
import open.dolphin.exception.FirstCommitWinException;
import open.dolphin.project.Project;
import open.dolphin.util.HashUtil;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Bsiness Delegater のルートクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class BusinessDelegater {
    
    protected static final String UTF8 = "UTF-8";
    protected static final String CAMMA = ",";
    protected static final String DATE_TIME_FORMAT_REST = "yyyy-MM-dd HH:mm:ss";
    protected static final String USER_NAME = "userName";
    protected static final String PASSWORD = "password";
    
    static {
        try {
            RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    protected boolean DEBUG = true;

    public BusinessDelegater() {
    }
    
    protected ClientRequest getRequest(String path, String userId, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        ClientRequest request = new ClientRequest(uri, getClientExecutor());
        request.header(USER_NAME, userId);
        request.header(PASSWORD, HashUtil.MD5(password));
        return request;
    }
    
    protected ClientRequest getRequest(String baseURI, String path, String userId, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseURI).append(path);
        String uri = sb.toString();
        ClientRequest request = new ClientRequest(uri, getClientExecutor());
        request.header(USER_NAME, userId);
        request.header(PASSWORD, HashUtil.MD5(password));
        return request;
    }
    
    protected ClientRequest getRequest(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        ClientRequest request = new ClientRequest(uri, getClientExecutor());
        request.header(USER_NAME, Project.getUserModel().getUserId());
        request.header(PASSWORD, Project.getUserModel().getPassword());
        return request;
    }
    
    protected BufferedReader getReader(ClientResponse<String> response) throws Exception {
        checkStatus(response);
        if (response.getEntity()!=null) {
            byte[] bytes = response.getEntity().getBytes(UTF8);
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes),UTF8));
        }
        return null;
    }
    
    protected String getString(ClientResponse<String> response) throws Exception {
        checkStatus(response);
        if (response.getEntity()!=null) {
            byte[] bytes = response.getEntity().getBytes(UTF8);
            return new String(bytes);
        }
        return null;
    }
    
    protected void checkStatus(ClientResponse<String> response) {
        if (response.getStatus()/100 != 2) {
            String err = "Failed : HTTP error code : " + response.getStatus();
            ClientContext.getDelegaterLogger().warn(err);
            throw new RuntimeException(err);
        }
    }
    
    protected void checkFirstCommitWin(ClientResponse<String> response) throws Exception {
        
        // No exception
        if (response.getStatus()/100==2) {
            return;
        }
        
        // First Commit Win Control
        if (response.getEntity()!=null) {
            byte[] bytes = response.getEntity().getBytes(UTF8);
            String test = new String(bytes);
            if (test.indexOf("First Commit Win")>=0) {
                //System.err.println("Exception is First Commit Win");
                throw new FirstCommitWinException("First Commit Win Exception");
            }
        }
        
        String err = "Failed : HTTP error code : " + response.getStatus();
        throw new RuntimeException(err);
    }

    protected void debug(int status, String entity) {
        System.err.println("---------------------------------------");
        System.err.println("status = " + status);
        System.err.println(entity);
    }
    
    private ClientExecutor getClientExecutor() {

        DefaultHttpClient defaultHttpClient;

        try {
            defaultHttpClient = new DefaultHttpClient();
            HttpParams params = defaultHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
            HttpConnectionParams.setSoTimeout(params, Integer.MAX_VALUE);

            return new ApacheHttpClient4Executor(defaultHttpClient);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return null;
    }
}
