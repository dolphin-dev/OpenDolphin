package open.dolphin.delegater;

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
import org.codehaus.jackson.map.ObjectMapper;

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
        UserModel user = getEasy(path, userPK, password, MediaType.APPLICATION_JSON, UserModel.class);
        
        return user;
    }
    
    public UserModel getUser(String userPK) throws Exception {
        
        // PATH
        String path = "/user/"+userPK;
        
        // GET
        UserModel user = getEasyJson(path, UserModel.class);
        
        return user;
    }
    
    public ArrayList<UserModel> getAllUser() throws Exception {
        
        // PATH
        String path = "/user";
        
        // GET
        UserList list = getEasyJson(path, UserList.class);
        
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
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // POST
        String entityStr = postEasyJson(path, data, String.class);      
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
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);       
        int cnt = Integer.parseInt(entityStr);
   
        return cnt;
    }
    
    public int deleteUser(String uid) throws Exception {
        
        // PATH
        String path = "/user/"+uid;
        
        // DELETE
        deleteEasy(path);
        
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
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class); 
        int cnt = Integer.parseInt(entityStr);
        
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
        
        // GET
        ActivityModel[] am = getEasyJson(path, ActivityModel[].class);
        
        return am;
    }
    
    public int checkLicense(String uid) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("/hiuchi/license");
        String path = sb.toString();
        
        // body
        byte[] data = uid.getBytes(UTF8);

        // POST Text
        String entityStr = postEasyText(path, data, String.class);
        
        return Integer.parseInt(entityStr);
    }
//s.oh$
}
