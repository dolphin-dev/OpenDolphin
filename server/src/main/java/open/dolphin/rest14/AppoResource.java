package open.dolphin.rest14;

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.AppoList;
import open.dolphin.session.AppoServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("/14/appo")
public class AppoResource extends AbstractResource {
    
    @Inject
    private AppoServiceBean appoServiceBean;

    /** Creates a new instance of AppoResource */
    public AppoResource() {
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putXml(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        AppoList list = mapper.readValue(json, AppoList.class);
        
        int count = appoServiceBean.putAppointments(list.getList());
        String cntStr = String.valueOf(count);
        debug(cntStr);

        return cntStr;
    }
}
