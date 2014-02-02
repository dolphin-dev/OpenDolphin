package open.dolphin.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.PatientModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */

@Path("patient")
public final class PatientResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of PatientsResource */
    public PatientResource() {
    }


    @GET
    @Path("name/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPatientsByName(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String name = param;

        List<PatientModel> patients = EJBLocator.getPatientServiceBean().getPatientsByName(fid, name);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(patients);
        debug(xml);

        return xml;
    }


    @GET
    @Path("kana/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPatientsByKana(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String kana = param;

        List<PatientModel> patients = EJBLocator.getPatientServiceBean().getPatientsByKana(fid, kana);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(patients);
        debug(xml);

        return xml;
    }
    

    @GET
    @Path("digit/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPatientsByDigit(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String digit = param;
        debug(fid);
        debug(digit);

        List<PatientModel> patients = EJBLocator.getPatientServiceBean().getPatientsByDigit(fid, digit);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(patients);
        debug(xml);
        
        return xml;
    }


    @GET
    @Path("id/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPatientById(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;

        PatientModel patient = EJBLocator.getPatientServiceBean().getPatientById(fid, pid);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(patient);
        debug(xml);

        return xml;
    }

    @GET
    @Path("pvt/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPatientsByPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pvtDate = param;

        List<PatientModel> list = EJBLocator.getPatientServiceBean().getPatientsByPvtDate(fid, pvtDate);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPatient(@Context HttpServletRequest servletReq, String xmlRep) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        PlistParser parser = new PlistParser();
        PatientModel patient = (PatientModel) parser.parse(xmlRep);
        patient.setFacilityId(fid);

        long pk = EJBLocator.getPatientServiceBean().addPatient(patient);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPatient(@Context HttpServletRequest servletReq, String xmlRep) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        PlistParser parser = new PlistParser();
        PatientModel patient = (PatientModel) parser.parse(xmlRep);
        patient.setFacilityId(fid);

        int cnt = EJBLocator.getPatientServiceBean().update(patient);
        String pkStr = String.valueOf(cnt);
        debug(pkStr);

        return pkStr;
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
