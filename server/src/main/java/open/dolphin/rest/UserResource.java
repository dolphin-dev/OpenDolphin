package open.dolphin.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserListConverter;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserList;
import open.dolphin.infomodel.UserModel;
import open.dolphin.session.UserServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("/user")
public class UserResource extends AbstractResource {

    @Inject
    private UserServiceBean userServiceBean;

    /** Creates a new instance of UserResource */
    public UserResource() {
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModelConverter getUser(@PathParam("userId") String userId) throws IOException {

        UserModel result = userServiceBean.getUser(userId);
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(result);
        return conv;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserListConverter getAllUser(@Context HttpServletRequest servletReq) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<UserModel> result = userServiceBean.getAllUser(fid);
        UserList list = new UserList();
        list.setList(result);

        UserListConverter conv = new UserListConverter();
        conv.setModel(list);
        
        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postUser(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);

        model.getFacilityModel().setFacilityId(fid);

        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = userServiceBean.addUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);
        
//s.oh^ Xronos連携(ユーザー連携)
        if(isXronosUserLink()) {
            StringBuilder sb = new StringBuilder();
            sb.append(model.getUserId().split(":")[1]).append(",");
            sb.append(model.getPassword()).append(",");
            sb.append(model.getSirName()).append(",");
            sb.append(model.getGivenName()).append(",");
            sb.append("0").append(",");
            sb.append(model.getUserId().split(":")[1]);
            sendUserInfo(sb.toString());
        }
//s.oh$

        return cntStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putUser(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);
        
//s.oh^ Xronos連携(ユーザー連携)
        if(isXronosUserLink()) {
            StringBuilder sb = new StringBuilder();
            sb.append(model.getUserId().split(":")[1]).append(",");
            sb.append(model.getPassword()).append(",");
            sb.append(model.getSirName()).append(",");
            sb.append(model.getGivenName()).append(",");
            sb.append("0").append(",");
            UserModel tmp = userServiceBean.getUserById(model.getId());
            sb.append((tmp != null) ? tmp.getUserId().split(":")[1] : model.getUserId().split(":")[1]);
            sendUserInfo(sb.toString());
        }
//s.oh$
        
        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = userServiceBean.updateUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{userId}")
    public void deleteUser(@PathParam("userId") String userId) {
        
//s.oh^ Xronos連携(ユーザー連携)
        if(isXronosUserLink()) {
            UserModel model = userServiceBean.getUser(userId);
            StringBuilder sb = new StringBuilder();
            sb.append(model.getUserId().split(":")[1]).append(",");
            sb.append(model.getPassword()).append(",");
            sb.append(model.getSirName()).append(",");
            sb.append(model.getGivenName()).append(",");
            sb.append("1").append(",");
            sb.append(model.getUserId().split(":")[1]);
            sendUserInfo(sb.toString());
        }
//s.oh$

        int result = userServiceBean.removeUser(userId);

        debug(String.valueOf(result));
    }

    @PUT
    @Path("/facility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putFacility(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);

        int result = userServiceBean.updateFacility(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }
    
//s.oh^ Xronos連携(ユーザー連携)
    @POST
    @Path("/modify/{param}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postXronosUser(@PathParam("param") String param) throws IOException {

        UserModel model = new UserModel();
        
        Logger.getLogger("open.dolphin").info("Xronos:" + param);
        
        // ユーザーID,パスワード,姓,名,0 or 1,元のユーザーID
        String[] vals = param.split(",");
        if(!isXronosUserLink() || vals.length != 6) {
            return "0";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        Properties config = new Properties();
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
        } catch (Exception e) {
        }
        String facilityid = config.getProperty("dolphin.facilityId", "");
        FacilityModel fm = userServiceBean.getFacilityInfo(facilityid);
        if(facilityid.length() <= 0 || fm == null) {
            return "0";
        }
        
        model.setUserId(fm.getFacilityId() + ":" + vals[5]);
        model.setPassword(vals[1]);
        model.setSirName(vals[2]);
        model.setGivenName(vals[3]);
        model.setCommonName(vals[2] + " " + vals[3]);
        model.setFacilityModel(fm);
        LicenseModel license = new LicenseModel();
        license.setLicense("doctor");
        license.setLicenseDesc("医師");
        license.setLicenseCodeSys("MML0026");
        model.setLicenseModel(license);
        DepartmentModel depart = new DepartmentModel();
        depart.setDepartment("01");
        depart.setDepartmentDesc("内科");
        depart.setDepartmentCodeSys("MML0028");
        model.setDepartmentModel(depart);
        model.setMemberType("ASP_MEMBER");
        model.setRegisteredDate(new Date());
        model.setEmail("xronos@mail");
        RoleModel role = new RoleModel();
        role.setRole("user");
        model.addRole(role);
        role.setUserModel(model);
        role.setUserId(model.getUserId());
        
        int result = userServiceBean.modifyUser(model, fm.getFacilityId() + ":" + vals[0], vals[4].equals("0") ? false : true);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }
    
    private boolean isXronosUserLink() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        Properties config = new Properties();
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
        } catch (Exception e) {
        }
        
        return config.getProperty("xronos.user.link", "false").equals("true") ? true : false;
    }
    
    private void sendUserInfo(String data) {
        Logger.getLogger("open.dolphin").info(data);
        
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        Properties config = new Properties();
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
        } catch (Exception e) {
        }
        
        String msg = "Xronos User Updated";
        int code = 0;
        HttpURLConnection connect = null;
        try {
            String urlStr = config.getProperty("xronos.user.url", "");
            URL url = new URL(urlStr);
            
            connect = (HttpURLConnection)url.openConnection();
            connect.setDoOutput(true);
            connect.setUseCaches(false);
            connect.setRequestMethod("POST");
            connect.setRequestProperty("Content-Type", MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF8");
            StringBuilder parameterBuf = new StringBuilder();
            //parameterBuf.append(URLEncoder.encode(data, "UTF-8"));
            parameterBuf.append(data);
            connect.setRequestProperty("Content-Length", "" + parameterBuf.length());
            
            PrintWriter printWriter = new PrintWriter(connect.getOutputStream());
            //PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
            printWriter.printf(parameterBuf.toString());
            printWriter.close();
            
            InputStream is = connect.getInputStream();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String s;
            StringBuilder resBuf = new StringBuilder();
            while((s = reader.readLine()) != null) {
                resBuf.append(s);
            }
            reader.close();
            code = connect.getResponseCode();
            connect.disconnect();
        }catch(IOException ex) {
            try{
                code = (connect != null) ? connect.getResponseCode() : -1;
            }catch(Exception ex_) {};
            msg = "Xronos IOError:" + ex.getMessage();
        }catch(Exception ex) {
            msg = "Xronos Error:" + ex.getMessage();
        }
        Logger.getLogger("open.dolphin").info(msg + ":" + String.valueOf(code));
    }
//s.oh$
}
