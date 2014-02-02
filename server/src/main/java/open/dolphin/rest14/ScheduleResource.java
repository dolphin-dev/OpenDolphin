package open.dolphin.rest14;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.PatientVisitListConverter;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.session.ScheduleServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author kazushi Minagawa
 */
@Path("/14/schedule")
public class ScheduleResource extends AbstractResource {
    
    @Inject
    private ScheduleServiceBean scheduleService;
    
    @GET
    @Path("/pvt/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientVisitListConverter getPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        // 施設
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<PatientVisitModel> result;

        String[] params = param.split(CAMMA);
        if (params.length==1) {
            String pvtDate = params[0];
            result = scheduleService.getPvt(fid, null, null, pvtDate);
        } else {
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            result = scheduleService.getPvt(fid, did, unassigned, pvtDate);
        }
        
        PatientVisitList list = new PatientVisitList();
        list.setList(result);
        
        PatientVisitListConverter conv = new PatientVisitListConverter();
        conv.setModel(list);
        
        return conv;
    }
        
    @POST
    @Path("/document")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postScheduleAndSendClaim(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        PostSchedule schedule = mapper.readValue(json, PostSchedule.class);
        long pvtPK = schedule.getPvtPK();
        long phPK = schedule.getPhPK();
        Date date = schedule.getScheduleDate();
        boolean send = schedule.getSendClaim();
        debug(schedule.toString());
        
        int cnt = scheduleService.makeScheduleAndSend(pvtPK, phPK, date, send);
        
        return String.valueOf(cnt);
    }
    
    @DELETE
    @Path("/pvt/{param}")
    public void deletePvt(@PathParam("param") String param) throws Exception {

        String[] params = param.split(",");
        long pvtPK = Long.parseLong(params[0]);
        long ptPK = Long.parseLong(params[1]);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse(params[2]);

        int cnt = scheduleService.removePvt(pvtPK, ptPK, d);

        debug(String.valueOf(cnt));
    }
}
