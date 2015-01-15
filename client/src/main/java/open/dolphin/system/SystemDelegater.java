package open.dolphin.system;

import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.delegater.BusinessDelegater;
import open.dolphin.infomodel.UserModel;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SystemDelegater extends BusinessDelegater {

    private final String PATH = "/dolphin";
    private final String BASE_URI = "http://localhost:8080/dolphin/openSource";
    private final String USER_ID = "1.3.6.1.4.1.9414.70.1:dolphin";
    private final String USER_PASSWORD = "dolphin";

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
    
//s.oh^ 2014/07/08 クラウド0対応
    public void sendCloudZeroMail() throws Exception {
        String path = PATH + "/cloudzero/sendmail";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
    }
//s.oh$
}
