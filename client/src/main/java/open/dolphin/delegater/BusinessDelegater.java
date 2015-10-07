package open.dolphin.delegater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import open.dolphin.client.Dolphin;
import open.dolphin.exception.FirstCommitWinException;
import open.dolphin.project.Project;
import open.dolphin.util.HashUtil;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

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
    private static final String CLINET_UUID = "clientUUID";
    
    protected ResteasyWebTarget getWebTarget(String path, String userName, String password) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(userName, HashUtil.MD5(password)));
        client.register(new StatusChecker());
        ResteasyWebTarget target = client.target(uri);
        return target;
    }
    
    protected ResteasyWebTarget getWebTarget(String baseURI, String path, String userId, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseURI).append(path);
        String uri = sb.toString();
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(userId, HashUtil.MD5(password)));
        client.register(new StatusChecker());
        ResteasyWebTarget target = client.target(uri);
        return target;
    }
    
    protected ResteasyWebTarget getWebTarget(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(Project.getUserModel().getUserId(), Project.getUserModel().getPassword()));
        client.register(new StatusChecker());
        ResteasyWebTarget target = client.target(uri);
        return target;
    }
    
    protected ResteasyWebTarget getStamptreeWebTarget(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(Project.getUserModel().getUserId(), Project.getUserModel().getPassword()));
        // 先勝ち制御Filter
        client.register(new FirstCommitWinChecker());
        ResteasyWebTarget target = client.target(uri);
        return target;
    }
    
    protected ResteasyWebTarget getWebTargetSubscribe(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI()).append(path);
        String uri = sb.toString();
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilterLong(Project.getUserModel().getUserId(), Project.getUserModel().getPassword()));
        ResteasyWebTarget target = client.target(uri);
        return target;
    }
    
    protected <T> T getEasy(String path, String userName, String password, String mediaType, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path, userName, password);
        Cookie cookie = getProjectCookie(userName);
        T t = cookie!=null ? target.request(mediaType).cookie(cookie).get(cls) : target.request(mediaType).get(cls);
        return t;
    }
    
    private <T> T getEasy(String path, String mediaType, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request(mediaType).cookie(cookie).get(cls) : target.request(mediaType).get(cls);
        return t;
    }
    
    protected <T> T getEasyJson(String path, Class<T> cls) {
        return this.getEasy(path, MediaType.APPLICATION_JSON, cls);
    }
    
    protected <T> T getEasyText(String path, Class<T> cls) {
        return this.getEasy(path, MediaType.TEXT_PLAIN, cls);
    }
    
    protected <T> T postEasyJson(String path, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).post(Entity.json(data), cls) : target.request().post(Entity.json(data), cls);
        return t;
    }
    
    protected <T> T postEasyText(String path, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).post(Entity.text(data), cls) : target.request().post(Entity.text(data), cls);
        return t;
    }
    
    protected <T> T putEasyStampTree(String path, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getStamptreeWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).put(Entity.json(data), cls) : target.request().put(Entity.json(data), cls);
        return t;
    }
    
    protected <T> T putEasyJson(String path, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).put(Entity.json(data), cls) : target.request().put(Entity.json(data), cls);
        return t;
    }
    
    protected <T> T putEasyJson(String path, String mediaType, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request(mediaType).cookie(cookie).put(Entity.json(data), cls) : target.request(mediaType).put(Entity.json(data), cls);
        return t;
    }
    
    protected <T> T putEasyText(String path, byte[] data, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).put(Entity.text(data), cls) : target.request().put(Entity.text(data), cls);
        return t;
    }
    
    protected void deleteEasy(String path) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        Response res = cookie!=null ? target.request().cookie(cookie).delete() : target.request().delete();
        res.close();
    }
    
    protected <T> T deleteEasy(String path, Class<T> cls) {
        ResteasyWebTarget target = getWebTarget(path);
        Cookie cookie = getProjectCookie(Project.getUserModel().getUserId());
        T t = cookie!=null ? target.request().cookie(cookie).delete(cls) : target.request().delete(cls);
        return t;
    }
    
    protected Cookie getProjectCookie(String userId) {
//        int index = userId.indexOf(":");
//        try {
//            String fid = userId.substring(0, index);
//            String uid = userId.substring(index+1);
//            StringBuilder sb = new StringBuilder();
//            sb.append("facilityId=").append(fid);
//            sb.append("&").append("userId=").append(uid);
//            String value = sb.toString();
//            Cookie cookie = new Cookie("comAuth", URLEncoder.encode(value, UTF8));
//            return cookie;
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace(System.err);
//        }
        return null;
    }
    
    protected class AuthHeadersRequestFilter implements ClientRequestFilter {

        private final String userName;
        private final String password;

        public AuthHeadersRequestFilter(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add(USER_NAME, userName);
            requestContext.getHeaders().add(PASSWORD, password);
        }
    }
    
    protected class AuthHeadersRequestFilterLong implements ClientRequestFilter {

        private final String userName;
        private final String password;

        public AuthHeadersRequestFilterLong(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add(USER_NAME, userName);
            requestContext.getHeaders().add(PASSWORD, password);
            requestContext.getHeaders().add(CLINET_UUID, Dolphin.getInstance().getClientUUID());
        }
    }
    
    protected class StatusChecker implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext crc, ClientResponseContext crc1) throws IOException {
            int status = crc1.getStatus();
            debug(status);
            if (status/100!=2) {
                String err = "HTTP error code : " + status;
                throw new RuntimeException(err);
            }
        }
    }
    
    protected class FirstCommitWinChecker implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext crc, ClientResponseContext crc1) throws IOException {
            int status = crc1.getStatus();
            debug(status);
            if (status/100==2) {
                return;
            }
            // Commons
            String err = IOUtils.toString(crc1.getEntityStream(), UTF8);
            
            // First Commit Win?
            if (err!=null && err.contains("First Commit Win")) {
                throw new FirstCommitWinException("First Commit Win Exception");
            }
            else {
                String err2 = "HTTP error code : " + status;
                throw new RuntimeException(err2);
            }
        }
    }
    
    protected ObjectMapper getSerializeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
    
    protected ObjectMapper getDeserializeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
    
    protected BufferedReader getReader(Response response) throws Exception {
        InputStream in = response.readEntity(InputStream.class);
        return new BufferedReader(new InputStreamReader(in,UTF8));
    }
    
    protected String getString(Response response) throws Exception {
        String result = response.readEntity(String.class);
        response.close();
        return result;
    }
    
    protected void debug(int status, String entity) {
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.FINE, "status = {0}", status);
        java.util.logging.Logger.getLogger(this.getClass().getName()).fine(entity);
    }
    
    protected void debug(int status) {
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.FINE, "HTTP status = {0}", status);
    }
}
