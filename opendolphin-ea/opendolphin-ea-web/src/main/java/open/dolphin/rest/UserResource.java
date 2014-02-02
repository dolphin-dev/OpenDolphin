package open.dolphin.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */

@Path("user")
public final class UserResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of UserResource */
    public UserResource() {
    }

    @GET
    @Path("{userId}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getUser(@PathParam("userId") String userId) {

        UserModel result = EJBLocator.getUserServiceBean().getUser(userId);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getAllUser(@Context HttpServletRequest servletReq) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<UserModel> result = EJBLocator.getUserServiceBean().getAllUser(fid);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postUser(@Context HttpServletRequest servletReq, String repXml) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        PlistParser parser = new PlistParser();
        UserModel model = (UserModel) parser.parse(repXml);
        model.getFacilityModel().setFacilityId(fid);

        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = EJBLocator.getUserServiceBean().addUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putUser(String repXml) {

        PlistParser parser = new PlistParser();
        UserModel model = (UserModel) parser.parse(repXml);
        
        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = EJBLocator.getUserServiceBean().updateUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }


    @DELETE
    @Path("{userId}/")
    public void deleteUser(@PathParam("userId") String userId) {

        int result = EJBLocator.getUserServiceBean().removeUser(userId);

        debug(String.valueOf(result));
    }


    @PUT
    @Path("facility/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putFacility(String repXml) {

        PlistParser parser = new PlistParser();
        UserModel model = (UserModel) parser.parse(repXml);

        int result = EJBLocator.getUserServiceBean().updateFacility(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
