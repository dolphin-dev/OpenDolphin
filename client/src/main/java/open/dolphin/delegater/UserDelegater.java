package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.UserModelConverter;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.UserList;
import open.dolphin.infomodel.UserModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class UserDelegater extends BusinessDelegater {
    
    public UserModel login(String fid, String uid, String password) throws Exception {

        // User PK
        StringBuilder sb = new StringBuilder();
        sb.append(fid);
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(uid);
        String userPK = sb.toString();
        
        // PATH
        String path = "/user/"+userPK;

        // GET
        ClientRequest request = getRequest(path, userPK, password);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();
        
//        if (false) {
//            System.err.println(user.getUserId());
//            System.err.println(user.getCommonName());
//            System.err.println(user.getFacilityModel().getFacilityName());
//        }
        
        return user;
    }
    
    public UserModel getUser(String userPK) throws Exception {
        
        // PATH
        String path = "/user/"+userPK;

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();
        
//        if (false) {
//            System.err.println(user.getUserId());
//            System.err.println(user.getCommonName());
//            System.err.println(user.getFacilityModel().getFacilityName());
//        }
        
        return user;
    }
    
    public ArrayList<UserModel> getAllUser() throws Exception {
        
        // PATH
        String path = "/user";
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        UserList list = mapper.readValue(br, UserList.class);
        br.close();
        
        // List
        return (ArrayList)list.getList();
    }
    
    public int addUser(UserModel userModel) throws Exception {
        
        // PATH
        String path = "/user";
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
    
    public int updateUser(UserModel userModel) throws Exception {
        
        // PATH
        String path = "/user";
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
    
    public int deleteUser(String uid) throws Exception {
        
        // PATH
        String path = "/user/"+uid;
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        
        // Count
        return 1;
    }
    
    public int updateFacility(UserModel user) throws Exception {
        
        // PATH
        String path = "/user/facility";
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
}
