package open.dolphin.rest14;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.session.SystemServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi
 */
@Path("/14/system")
public class SystemResource {
    
    @Inject
    private SystemServiceBean systemServiceBean;

    /** Creates a new instance of SystemResource */
    public SystemResource() {
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hellowDolphin() {
        return "Hellow, Dolphin";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addFacilityAdmin(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        UserModel user = mapper.readValue(json, UserModel.class);

        // 関係を構築する
        List<RoleModel> roles = user.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(user);
        }

        systemServiceBean.addFacilityAdmin(user);
    }
}
