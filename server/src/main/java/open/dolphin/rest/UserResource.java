package open.dolphin.rest;

import java.io.IOException;
import java.util.List;
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
    public String putUser(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        UserModel model = mapper.readValue(json, UserModel.class);
        
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

        int result = userServiceBean.removeUser(userId);

        debug(String.valueOf(result));
    }

    @PUT
    @Path("/facility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putFacility(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        UserModel model = mapper.readValue(json, UserModel.class);

        int result = userServiceBean.updateFacility(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }
}
