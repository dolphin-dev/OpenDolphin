package open.dolphin.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("karte")
public final class KarteResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of KarteResource */
    public KarteResource() {
    }


    @GET
    @Path("{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getKarte(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long patientPK = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);

        KarteBean result = EJBLocator.getKarteServiceBean().getKarte(patientPK, fromDate);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);
        
        return xml;
    }

    //-------------------------------------------------------

    @GET
    @Path("docinfo/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getDocumentList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);
        boolean includeModified = Boolean.parseBoolean(params[2]);

        List<DocInfoModel> result = EJBLocator.getKarteServiceBean().getDocumentList(karteId, fromDate, includeModified);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }


    @GET
    @Path("documents/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getDocuments(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        List<DocumentModel> result = EJBLocator.getKarteServiceBean().getDocuments(list);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }


    @POST
    @Path("document/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(String repXml) {

        PlistParser parser = new PlistParser();
        DocumentModel document = (DocumentModel) parser.parse(repXml);
        // 関係を構築する
        List<ModuleModel> modules = document.getModules();
        if (modules!=null && modules.size()>0) {
            for (ModuleModel m : modules) {
                m.setDocumentModel(document);
            }
        }
        List<SchemaModel> schemas = document.getSchema();
        if (schemas!=null && schemas.size()>0) {
            for (SchemaModel m : schemas) {
                m.setDocumentModel(document);
            }
        }

        long result = EJBLocator.getKarteServiceBean().addDocument(document);
        String pkStr = String.valueOf(result);
        debug(pkStr);

        return pkStr;
    }

    @POST
    @Path("document/pvt/{params}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(@PathParam("params") String param, String repXml) {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);

        PlistParser parser = new PlistParser();
        DocumentModel document = (DocumentModel) parser.parse(repXml);
        // 関係を構築する
        List<ModuleModel> modules = document.getModules();
        if (modules!=null && modules.size()>0) {
            for (ModuleModel m : modules) {
                m.setDocumentModel(document);
            }
        }
        List<SchemaModel> schemas = document.getSchema();
        if (schemas!=null && schemas.size()>0) {
            for (SchemaModel m : schemas) {
                m.setDocumentModel(document);
            }
        }

        long result = EJBLocator.getKarteServiceBean().addDocumentAndUpdatePVTState(document, pvtPK, state);
        String pkStr = String.valueOf(result);
        debug(pkStr);

        return pkStr;
    }


    @PUT
    @Path("document/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String putTitle(@PathParam("id") String idStr, String title) {

        long id = Long.parseLong(idStr);

        int result = EJBLocator.getKarteServiceBean().updateTitle(id, title);

        return String.valueOf(result);
    }


    @DELETE
    @Path("document/{id}/")
    public void deleteDocument(@PathParam("id") String idStr) {

        long id = Long.parseLong(idStr);

        int cnt = EJBLocator.getKarteServiceBean().deleteDocument(id);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);
    }

    //-------------------------------------------------------

    @GET
    @Path("modules/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getModules(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        String entity = params[1];

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 2;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        List<List> result = EJBLocator.getKarteServiceBean().getModules(karteId, entity, fromList, toList);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    @GET
    @Path("iamges/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getImages(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 1;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        List<List> result = EJBLocator.getKarteServiceBean().getImages(karteId, fromList, toList);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    @GET
    @Path("image/{id}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getImage(@PathParam("param") String idStr) {

        debug(idStr);
        long karteId = Long.parseLong(idStr);

        SchemaModel result = EJBLocator.getKarteServiceBean().getImage(karteId);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    //-------------------------------------------------------

    @GET
    @Path("diagnosis/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getDiagnosis(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);

        List<RegisteredDiagnosisModel> list = EJBLocator.getKarteServiceBean().getDiagnosis(karteId, fromDate);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }

    @POST
    @Path("diagnosis/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDiagnosis(String repXml) {
        
        PlistParser parser = new PlistParser();
        List<RegisteredDiagnosisModel> list = (List<RegisteredDiagnosisModel>) parser.parse(repXml);
        // 関係構築

        List<Long> result = EJBLocator.getKarteServiceBean().addDiagnosis(list);

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String text = sb.substring(0, sb.length()-1);
        debug(text);

        return text;
    }

    @PUT
    @Path("diagnosis/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putDiagnosis(String repXml) {

        PlistParser parser = new PlistParser();
        List<RegisteredDiagnosisModel> list = (List<RegisteredDiagnosisModel>) parser.parse(repXml);
        // 関係構築

        int result = EJBLocator.getKarteServiceBean().updateDiagnosis(list);
        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    @DELETE
    @Path("diagnosis/{param}/")
    public void deleteDiagnosis(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int result = EJBLocator.getKarteServiceBean().removeDiagnosis(list);

        debug(String.valueOf(result));
    }

    //-------------------------------------------------------

    @GET
    @Path("observations/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getObservations(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        String observation = params[1];
        String phenomenon = params[2];
        Date firstConfirmed = null;
        if (params.length==4) {
            firstConfirmed = parseDate(params[3]);
        }

        List<ObservationModel> list = EJBLocator.getKarteServiceBean().getObservations(karteId, observation, phenomenon, firstConfirmed);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }

    @POST
    @Path("observations/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postObservations(String repXml) {
        
        PlistParser parser = new PlistParser();
        List<ObservationModel> list = (List<ObservationModel>) parser.parse(repXml);
        // 関係構築

        List<Long> result = EJBLocator.getKarteServiceBean().addObservations(list);

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String text = sb.substring(0, sb.length()-1);
        debug(text);

        return text;
    }

    @PUT
    @Path("observations/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putObservations(String repXml) {
        
        PlistParser parser = new PlistParser();
        List<ObservationModel> list = (List<ObservationModel>) parser.parse(repXml);
        // 関係構築
        int result = EJBLocator.getKarteServiceBean().updateObservations(list);

        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    @DELETE
    @Path("observations/{param}/")
    public void deleteObservations(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int result = EJBLocator.getKarteServiceBean().removeObservations(list);

        debug(String.valueOf(result));
    }

    //-------------------------------------------------------

    @PUT
    @Path("memo/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPatientMemo(String repXml) {
        
        PlistParser parser = new PlistParser();
        PatientMemoModel memo = (PatientMemoModel) parser.parse(repXml);
        // 関係構築

        int result = EJBLocator.getKarteServiceBean().updatePatientMemo(memo);
        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    //-------------------------------------------------------

    @GET
    @Path("appo/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getAppoinmentList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 1;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        List<List> result = EJBLocator.getKarteServiceBean().getAppointmentList(karteId, fromList, toList);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
