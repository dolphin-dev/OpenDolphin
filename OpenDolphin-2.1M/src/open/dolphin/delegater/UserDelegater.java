package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class UserDelegater extends BusinessDelegater {

    private static final String USER = "user/";
    
    public UserModel login(String fid, String uid, String password) {

        StringBuilder sb = new StringBuilder();
        sb.append(fid);
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(uid);
        String fidUid = sb.toString();

        JerseyClient jersey = JerseyClient.getInstance();
        String baseURI = Project.getBaseURI();
        jersey.setBaseURI(baseURI);
        jersey.setUpAuthentication(fidUid, password, false);
        
        if (DEBUG) {
            System.out.println(baseURI);
            System.out.println(fidUid);
            System.out.println(password);
        }

        return getUser(fidUid);
    }
    
    public UserModel getUser(String userPK) {

        ClientResponse response = null;
        
        StringBuilder sb = new StringBuilder();
        sb.append(USER);
        sb.append(userPK);
        String path = sb.toString();

        response = getResource(path)
                   .accept(MediaType.APPLICATION_XML_TYPE)
                   .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        UserModel userModel = (UserModel) con.parse(entityStr);
        return userModel;
    }
    
    public ArrayList<UserModel> getAllUser() {

        ClientResponse response = null;
        String path = USER;

        response = getResource(path)
                   .accept(MediaType.APPLICATION_XML_TYPE)
                   .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        ArrayList<UserModel> list = (ArrayList<UserModel>) con.parse(entityStr);
        return list;
    }
    
    public int addUser(UserModel userModel) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(userModel);

        ClientResponse response = null;
        String path = USER;

        response = getResource(path)
                   .type(MediaType.APPLICATION_XML_TYPE)
                   .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
    
    public int updateUser(UserModel userModel) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(userModel);

        ClientResponse response = null;
        String path = USER;

        response = getResource(path)
                   .type(MediaType.APPLICATION_XML_TYPE)
                   .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
    
    public int deleteUser(String uid) {

        StringBuilder sb = new StringBuilder();
        sb.append(USER);
        sb.append(uid);
        sb.append("/");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.TEXT_PLAIN)
                    .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }

        return 1;
    }
    
    public int updateFacility(UserModel user) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(user);

        StringBuilder sb = new StringBuilder();
        sb.append(USER);
        sb.append("facility/");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        int cnt = Integer.parseInt(entityStr);
        return cnt;
    }
}
