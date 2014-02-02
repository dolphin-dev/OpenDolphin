/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import open.dolphin.converter.VitalListConverter;
import open.dolphin.converter.VitalModelConverter;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.VitalList;
import open.dolphin.infomodel.VitalModel;
import open.dolphin.session.VitalServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
@Path("/vital")
public class VitalResource extends AbstractResource {

    @Inject
    private VitalServiceBean vitalServiceBean; 

    /** Creates a new instance of VitalResource */
    public VitalResource() {
    }

    @GET
    @Path("/id/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public VitalModelConverter getVital(@PathParam("param") String param) throws IOException {

        String id = param;

        VitalModel result = vitalServiceBean.getVital(id);
        VitalModelConverter conv = new VitalModelConverter();
        conv.setModel(result);
        return conv;
    }

    @GET
    @Path("/pat/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public VitalListConverter getPatVital(@Context HttpServletRequest servletReq, @PathParam("param") String param) {
        
        String pid = param;
        
        String fpid = getFidPid(servletReq.getRemoteUser(), pid);

        List<VitalModel> result = vitalServiceBean.getPatVital(fpid);
        VitalList list = new VitalList();
        list.setList(result);

        VitalListConverter conv = new VitalListConverter();
        conv.setModel(list);
        
        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postVital(@Context HttpServletRequest servletReq, String json) throws IOException {

        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VitalModel model = mapper.readValue(json, VitalModel.class);
        
        String fpid = getFidPid(servletReq.getRemoteUser(), model.getFacilityPatId());
        model.setFacilityPatId(fpid);

        int result = vitalServiceBean.addVital(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);
        
        return cntStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putVital(@Context HttpServletRequest servletReq, String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VitalModel model = mapper.readValue(json, VitalModel.class);
        
        String fpid = getFidPid(servletReq.getRemoteUser(), model.getFacilityPatId());
        model.setFacilityPatId(fpid);

        int result = vitalServiceBean.updateVital(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/id/{param}")
    public void deleteVital(@PathParam("param") String param) {

        
        int result = vitalServiceBean.removeVital(param);

        //debug(String.valueOf(result));
        
        
    }
}
