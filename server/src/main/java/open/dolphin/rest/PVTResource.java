package open.dolphin.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PatientVisitListConverter;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.session.PVTServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi
 */
@Path("/pvt")
public class PVTResource extends AbstractResource {
    
    @Inject
    private PVTServiceBean pVTServiceBean;

    /** Creates a new instance of PatientsResource */
    public PVTResource() {
    }

    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientVisitListConverter getPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        // 施設
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<PatientVisitModel> result;

        String[] params = param.split(CAMMA);
        if (params.length==4) {
            String pvtDate = params[0];
            int firstResult = Integer.parseInt(params[1]);
            String appoDateFrom = params[2];
            String appoDateTo = params[3];
            result = pVTServiceBean.getPvt(fid, pvtDate, firstResult, appoDateFrom, appoDateTo);
        } else {
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            int firstResult = Integer.parseInt(params[3]);
            String appoDateFrom = params[4];
            String appoDateTo = params[5];
            result = pVTServiceBean.getPvt(fid, did, unassigned, pvtDate, firstResult, appoDateFrom, appoDateTo);
        }
        
        PatientVisitList list = new PatientVisitList();
        list.setList(result);
        
        PatientVisitListConverter conv = new PatientVisitListConverter();
        conv.setModel(list);
        
        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPvt(@Context HttpServletRequest servletReq, String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        PatientVisitModel model = mapper.readValue(json, PatientVisitModel.class);

        // 関係構築
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        model.setFacilityId(fid);
        model.getPatientModel().setFacilityId(fid);

        Collection<HealthInsuranceModel> c = model.getPatientModel().getHealthInsurances();
        if (c!= null && c.size() > 0) {
            for (HealthInsuranceModel hm : c) {
                hm.setPatient(model.getPatientModel());
            }
        }

        int result = pVTServiceBean.addPvt(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Path("/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putPvtState(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);
        
        int cnt = pVTServiceBean.updatePvtState(pvtPK, state);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Path("/memo/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putMemo(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        String memo = params[1];

        int cnt = pVTServiceBean.updateMemo(pvtPK, memo);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{pvtPK}")
    public void deletePvt(@PathParam("pvtPK") String pkStr) {

        long pvtPK = Long.parseLong(pkStr);

        int cnt = pVTServiceBean.removePvt(pvtPK);

        debug(String.valueOf(cnt));
    }
}
