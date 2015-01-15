package open.dolphin.rest;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PatientListConverter;
import open.dolphin.converter.PatientModelConverter;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import static open.dolphin.rest.AbstractResource.CAMMA;
import static open.dolphin.rest.AbstractResource.getRemoteFacility;
import open.dolphin.session.PatientServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("/patient")
public class PatientResource extends AbstractResource {

    @Inject
    private PatientServiceBean patientServiceBean;

    /** Creates a new instance of PatientsResource */
    public PatientResource() {
    }

    @GET
    @Path("/name/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByName(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String name = param;

        List<PatientModel> result = patientServiceBean.getPatientsByName(fid, name);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/kana/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByKana(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String kana = param;

        List<PatientModel> result = patientServiceBean.getPatientsByKana(fid, kana);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/digit/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByDigit(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String digit = param;
        debug(fid);
        debug(digit);

        List<PatientModel> result = patientServiceBean.getPatientsByDigit(fid, digit);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/id/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientModelConverter getPatientById(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;

        PatientModel patient = patientServiceBean.getPatientById(fid, pid);
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);
        
        return conv;
    }

    @GET
    @Path("/pvt/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pvtDate = param;

        List<PatientModel> result = patientServiceBean.getPatientsByPvtDate(fid, pvtDate);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }
    
//minagawa^ 仮保存カルテ取得対応
    @GET
    @Path("/documents/status")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getDocumentsByStatus(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        List<PatientModel> result = patientServiceBean.getTmpKarte(fid);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    } 
//minagawa$

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPatient(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientModel patient = mapper.readValue(json, PatientModel.class);
        
        patient.setFacilityId(fid);

        long pk = patientServiceBean.addPatient(patient);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPatient(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientModel patient = mapper.readValue(json, PatientModel.class);

        patient.setFacilityId(fid);

        int cnt = patientServiceBean.update(patient);
        String pkStr = String.valueOf(cnt);
        debug(pkStr);

        return pkStr;
    }
    
    // 検索件数が1000件超過
    @GET
    @Path("/count/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPatientCount(@Context HttpServletRequest servletReq, @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;
        
        Long cnt = patientServiceBean.getPatientCount(fid, pid);
        String val = String.valueOf(cnt);
        
        return val;
    }
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getAllPatient(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        List<PatientModel> result = patientServiceBean.getAllPatient(fid);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    } 
//s.oh$
    
//s.oh^ 2014/10/01 患者検索(傷病名)
    @GET
    @Path("/custom/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getDocumentsByCustom(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        List<PatientModel> result = patientServiceBean.getCustom(fid, param);
        PatientList list = new PatientList();
        list.setList(result);
        
        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }
//s.oh$
}
