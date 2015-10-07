package open.dolphin.rest;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.*;
import open.dolphin.infomodel.*;
import open.dolphin.touch.converter.IPatientModel;
import open.dolphin.session.MmlServiceBean;

/**
 *
 * @author kazushi Minagawa
 */
@Path("/mml")
public class MmlResource extends AbstractResource {
    
    @Inject
    private MmlServiceBean mmlServiceBean;
    
       
    @GET
    @Path("/document/{param}")
    public String dumpFacilityDocumentsAsMML(@PathParam("param") String param) {
        
        String fid = param;
        
        final List<Long> docList = mmlServiceBean.getFacilityDocumentList(fid);
        if (docList.isEmpty()) {
            return "0";
        }
        final int cnt = docList.size();
        
         Runnable r = () -> {
             for (int i = 0; i < cnt; i++) {
                 mmlServiceBean.dumpDocumentToMML(i, docList.get(i));
             }
             System.err.println("MMLダンプ終了しました。");
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
        
        return String.valueOf(cnt);
    }
    
    @GET
    @Path("/patient/{param}")
    public String dumpFacilityPatientsDiagnosisAsMML(@PathParam("param") String param) {
        
        final String fid = param;
        
        final List<Long> list = mmlServiceBean.getFacilityPatientList(fid);
        if (list.isEmpty()) {
            return "0";
        }
        final int cnt = list.size();
        
        Runnable r = () -> {
            for (int i = 0; i < 1; i++) {
                mmlServiceBean.dumpPatientDiagnosisToMML(fid, i, list.get(i));
                //mmlServiceBean.patientToJSON(i, list.get(i).longValue());
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
        
        return String.valueOf(cnt);
    }
    
    //-------------------------------------------------------------------------
    // Patient JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/patient/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPatientList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityPatientList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/patient/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public IPatientModel dumpPatientAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        PatientModel pm = mmlServiceBean.getPatientByPK(pk);
        IPatientModel conv = new IPatientModel();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Disease JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/disease/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDiseaseList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityDiseaseList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/disease/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public RegisteredDiagnosisModelConverter dumpDiseaseAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        RegisteredDiagnosisModel pm = mmlServiceBean.getDiseaseByPK(pk);
        RegisteredDiagnosisModelConverter conv = new RegisteredDiagnosisModelConverter();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Memo JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/memo/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMemoList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityMemoList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/memo/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientMemoModelConverter dumpMemoAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        PatientMemoModel pm = mmlServiceBean.getMemoByPK(pk);
        PatientMemoModelConverter conv = new PatientMemoModelConverter();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Observation JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/observation/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getObservationList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityObservationList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/observation/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public ObservationModelConverter dumpObservationAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        ObservationModel pm = mmlServiceBean.getObservationByPK(pk);
        ObservationModelConverter conv = new ObservationModelConverter();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Karte JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/karte/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getKarteList(@PathParam("param") String param) {
        
        String fid = param;
        
        final List<Long> list = mmlServiceBean.getFacilityKarteList(fid);
        if (list.isEmpty()) {
            return "0";
        }
        final int cnt = list.size();
        
//         Runnable r = new Runnable() {
//
//            @Override
//            public void run() {
//                for (int i = 0; i < cnt; i++) {
//                    try {
//                        mmlServiceBean.getKarteByPK(i, list.get(i).longValue());
//                        
//                    } catch (Exception e) {
//                        
//                    }
//                }
//                System.err.println("JSONダンプ終了しました。");
//            }
//        };
//        Thread t = new Thread(r);
//        t.setPriority(Thread.NORM_PRIORITY);
//        t.start();
//        
//        return String.valueOf(cnt);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/karte/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentModelConverter dumpKarteAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        DocumentModel pm = mmlServiceBean.getKarteByPK(pk);
        DocumentModelConverter conv = new DocumentModelConverter();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Letter JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/letter/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLetterList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityLetterList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/letter/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public LetterModuleConverter dumpLetterAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        LetterModule pm = mmlServiceBean.getLetterByPK(pk);
        LetterModuleConverter conv = new LetterModuleConverter();
        conv.setModel(pm);
        return conv;
    }
    
    //--------------------------------------------------------------------------
    // Labtest JSON
    //-------------------------------------------------------------------------
    @GET
    @Path("/labtest/list/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLabtestList(@PathParam("param") String param) {
        
        String fid = param;
        
        List<Long> list = mmlServiceBean.getFacilityLabtestList(fid);
        
        StringBuilder sb = new StringBuilder();
        for (Long l : list) {
            sb.append(String.valueOf(l));
            sb.append(",");
        }
        
        int len = sb.length();
        sb.setLength(len-1);
        String ret = sb.toString();
        return ret;
    }
    
    @GET
    @Path("/labtest/json/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public NLaboModuleConverter dumpLabtestAsJSON(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        NLaboModule pm = mmlServiceBean.getLabtestByPK(pk);
        NLaboModuleConverter conv = new NLaboModuleConverter();
        conv.setModel(pm);
        return conv;
    }
}
