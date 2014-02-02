package open.dolphin.rest;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * REST Web Service
 *
 * @author kazushi
 */

@Path("pvt")
public final class PVTResource extends AbstractResource {

    private boolean DEBUG;

    /** Creates a new instance of PatientsResource */
    public PVTResource() {
    }

    @GET
    @Path("{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        // 施設
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<PatientVisitModel> list;

        String[] params = param.split(CAMMA);
        if (params.length==4) {
            String pvtDate = params[0];
            int firstResult = Integer.parseInt(params[1]);
            String appoDateFrom = params[2];
            String appoDateTo = params[3];
            list = EJBLocator.getPVTServiceBean().getPvt(fid, pvtDate, firstResult, appoDateFrom, appoDateTo);
        } else {
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            int firstResult = Integer.parseInt(params[3]);
            String appoDateFrom = params[4];
            String appoDateTo = params[5];
            list = EJBLocator.getPVTServiceBean().getPvt(fid, did, unassigned, pvtDate, firstResult, appoDateFrom, appoDateTo);
        }

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPvt(@Context HttpServletRequest servletReq, String repXml) {

        PlistParser parser = new PlistParser();
        PatientVisitModel model = (PatientVisitModel) parser.parse(repXml);

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

        int result = EJBLocator.getPVTServiceBean().addPvt(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    
    @PUT
    @Path("{param}/")
    @Produces(MediaType.TEXT_PLAIN)
    public String putPvtState(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);
        
        int cnt = EJBLocator.getPVTServiceBean().updatePvtState(pvtPK, state);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Path("memo/{param}/")
    @Produces(MediaType.TEXT_PLAIN)
    public String putMemo(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        String memo = params[1];

        int cnt = EJBLocator.getPVTServiceBean().updateMemo(pvtPK, memo);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }


    @DELETE
    @Path("{pvtPK}/")
    public void deletePvt(@PathParam("pvtPK") String pkStr) {

        long pvtPK = Long.parseLong(pkStr);

        int cnt = EJBLocator.getPVTServiceBean().removePvt(pvtPK);

        debug(String.valueOf(cnt));
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
