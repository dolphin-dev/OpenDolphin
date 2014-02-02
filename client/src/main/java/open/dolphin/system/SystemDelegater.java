package open.dolphin.system;

import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.UserModelConverter;
import open.dolphin.delegater.BusinessDelegater;
import open.dolphin.infomodel.UserModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SystemDelegater extends BusinessDelegater {

    private final String PATH = "/system";
    private final String BASE_URI = "http://localhost:8080/dolphin/openSource/14";
    private final String USER_ID = "1.3.6.1.4.1.9414.10.1:admin";
    private final String USER_PASSWORD = "admin";

    public SystemDelegater() {
    }

    /**
     * 通信テストを行う。
     * @return hellow
     * @throws Exception
     */
    public String hellow() throws Exception {
        
        // GET
        ClientRequest request = getRequest(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        ClientResponse<String> response = request.get(String.class);
        
        // Hellow
        String entityStr = getString(response);
        return entityStr;
    }

    /**
     * 施設ユーザーアカウントを登録する。
     * @param user 登録するユーザー
     * @throws Exception
     */
    public void addFacilityUser(UserModel user) throws Exception {
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes("UTF-8");
        
        // POST
        ClientRequest request = getRequest(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        // Check
        checkStatus(response);
    }
}
