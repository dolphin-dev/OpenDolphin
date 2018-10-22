package open.dolphin.rest;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserListConverter;
import open.dolphin.converter.UserModelConverter;
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
    public UserModelConverter getUser(@Context HttpServletRequest servletReq, @PathParam("userId") String userId) throws IOException {
        
//s.oh^ 脆弱性対応
        // ログインユーザと同一ユーザIDかチェック
        HttpServletRequest req = (HttpServletRequest)servletReq;
        if(!req.getHeader("userName").equals(userId)) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "Not the same user:{0},{1}", new Object[]{req.getHeader("userName"), userId});
            return null;
        }
//s.oh$

        UserModel result = userServiceBean.getUser(userId);
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(result);
        return conv;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserListConverter getAllUser(@Context HttpServletRequest servletReq) {
        
//s.oh^ 脆弱性対応
        // 管理者権限かチェック
        HttpServletRequest req = (HttpServletRequest)servletReq;
        if(!userServiceBean.isAdmin(req.getHeader("userName"), req.getHeader("password"))) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "Not an administrator authority:{0}", new Object[]{req.getHeader("userName")});
            return null;
        }
//s.oh$
        
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
        
//s.oh^ 脆弱性対応
        // 管理者権限かチェック
        HttpServletRequest req = (HttpServletRequest)servletReq;
        if(!userServiceBean.isAdmin(req.getHeader("userName"), req.getHeader("password"))) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "Not an administrator authority:{0}", new Object[]{req.getHeader("userName")});
            return "0";
        }
//s.oh$

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
        
        return cntStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putUser(@Context HttpServletRequest servletReq, String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);
        
//s.oh^ 脆弱性対応
        // 管理者権限かチェック
        HttpServletRequest req = (HttpServletRequest)servletReq;
        if(!userServiceBean.isAdmin(req.getHeader("userName"), req.getHeader("password"))) {
            // ログインユーザと同一ユーザIDかチェック
            if(!req.getHeader("userName").equals(model.getUserId())) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "User ID is different:{0},{1}", new Object[]{req.getHeader("userName"), model.getUserId()});
                return "0";
            }
            // ログインユーザの権限チェック
            if(userServiceBean.checkAuthority(req.getHeader("userName"), req.getHeader("password"), model.getRoles())) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "Illegal authority:{0}", new Object[]{req.getHeader("userName")});
                return "0";
            }
        }
//s.oh$
        
        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        roles.stream().forEach((role) -> {
            role.setUserModel(model);
        });

        int result = userServiceBean.updateUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{userId}")
    public void deleteUser(@Context HttpServletRequest servletReq, @PathParam("userId") String userId) {
        
//s.oh^ 脆弱性対応
        // 管理者権限かチェック
        HttpServletRequest req = (HttpServletRequest)servletReq;
        if(!userServiceBean.isAdmin(req.getHeader("userName"), req.getHeader("password"))) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "Not an administrator authority:{0}", new Object[]{req.getHeader("userName")});
            return;
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
    
//s.oh^ 脆弱性対応
    @GET
    @Path("/name/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserName(@PathParam("userId") String userId) throws IOException {
        return userServiceBean.getUserName(userId);
    }
//s.oh$
}
