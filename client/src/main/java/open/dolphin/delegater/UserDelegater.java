package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.ActivityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.UserList;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
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
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path, userPK, password);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();
        
        //20130225
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","INFO",user.getUserId()+"/"+user.getCommonName()+"/"+user.getFacilityModel().getFacilityName());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
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
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
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
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserList list = mapper.readValue(br, UserList.class);
        br.close();
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // List
        return (ArrayList)list.getList();
    }
    
    public int addUser(UserModel userModel) throws Exception {
        
        // PATH
        String path = "/user";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RET",String.valueOf(cnt));
        
        return cnt;
    }
    
    public int updateUser(UserModel userModel) throws Exception {
        
        // PATH
        String path = "/user";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RET",String.valueOf(cnt));
        
        return cnt;
    }
    
    public int deleteUser(String uid) throws Exception {
        
        // PATH
        String path = "/user/"+uid;
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RET",String.valueOf(1));
        
        // Count
        return 1;
    }
    
    public int updateFacility(UserModel user) throws Exception {
        
        // PATH
        String path = "/user/facility";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RET",String.valueOf(cnt));
        
        return cnt;
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    public ActivityModel[] fetchActivities() throws Exception {
        
        // 集計終了 現在まで
        GregorianCalendar gcTo = new GregorianCalendar();
        
        // 開始日　（当月の１日）
        int year = gcTo.get(Calendar.YEAR);
        int month = gcTo.get(Calendar.MONTH);
        
        // PATH
        int numMonth = Project.getInt("activities.numMonth", 3);
        StringBuilder sb = new StringBuilder();
        sb.append("/hiuchi/activity/");
        sb.append(year).append(CAMMA).append(month).append(CAMMA).append(numMonth);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        //ResteasyWebTarget target = getWebTarget(path);
        //ActivityModel[] am = target.request(MediaType.APPLICATION_JSON).get(ActivityModel[].class);
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ActivityModel[] am = mapper.readValue(br, ActivityModel[].class);
        br.close();
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        return am;
    }
    
    public int checkLicense(String uid) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("/hiuchi/license");
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // body
        byte[] data = uid.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.TEXT_PLAIN, data);
        ClientResponse<String> response = request.post(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.TEXT_PLAIN,uid);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
//s.oh$
}
