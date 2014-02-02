package open.dolphin.rest14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.NLaboItemListConverter;
import open.dolphin.converter14.NLaboModuleListConverter;
import open.dolphin.converter14.PatientLiteListConverter;
import open.dolphin.converter14.PatientModelConverter;
import open.dolphin.infomodel.*;
import open.dolphin.session.NLabServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("/14/lab")
public class NLabResource extends AbstractResource {
    
    @Inject
    private NLabServiceBean nLabServiceBean;

    public NLabResource() {
    }

    @GET
    @Path("/module/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public NLaboModuleListConverter getLaboTest(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboModule> result = nLabServiceBean.getLaboTest(fidPid, firstResult, maxResult);
        NLaboModuleList list = new NLaboModuleList();
        list.setList(result);
        
        NLaboModuleListConverter conv = new NLaboModuleListConverter();
        conv.setModel(list);
        
        return conv;
    }

    @GET
    @Path("/item/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public NLaboItemListConverter getLaboTestItem(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);
        String itemCode = params[3];

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboItem> result = nLabServiceBean.getLaboTestItem(fidPid, firstResult, maxResult, itemCode);
        NLaboItemList list = new NLaboItemList();
        list.setList(result);
        
        NLaboItemListConverter conv = new NLaboItemListConverter();
        conv.setModel(list);
        
        return conv;
    }

    @GET
    @Path("/patient/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientLiteListConverter getConstrainedPatients(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        debug(param);
        String[] params = param.split(CAMMA);
        List<String> idList = new ArrayList<String>(params.length);
        idList.addAll(Arrays.asList(params));

        List<PatientLiteModel> result = nLabServiceBean.getConstrainedPatients(fid, idList);
        PatientLiteList list = new PatientLiteList();
        list.setList(result);
        
        PatientLiteListConverter conv = new PatientLiteListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Path("/module")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PatientModelConverter postNLaboTest(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        ObjectMapper mapper = new ObjectMapper();
        NLaboModule module = mapper.readValue(json, NLaboModule.class);
       
        List<NLaboItem> items = module.getItems();
        // 関係を構築する
        if (items!=null && items.size()>0) {
            for (NLaboItem item : items) {
                item.setLaboModule(module);
            }
        }
        
        PatientModel patient = nLabServiceBean.create(fid, module);
        
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);

        return conv;
    }
}
