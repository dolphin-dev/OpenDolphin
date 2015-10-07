package open.dolphin.touch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.StringListConverter;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.StringList;
import open.dolphin.infomodel.UserModel;
import open.dolphin.infomodel.VisitPackage;
import open.dolphin.session.ChartEventServiceBean;
import open.dolphin.session.KarteServiceBean;
import open.dolphin.touch.converter.IDocument;
import open.dolphin.touch.converter.IDocument2;
import open.dolphin.touch.converter.IMKDocument;
import open.dolphin.touch.converter.IMKDocument2;
import open.dolphin.touch.converter.IPatientList;
import open.dolphin.touch.converter.IPatientModel;
import open.dolphin.touch.converter.ISendPackage;
import open.dolphin.touch.converter.ISendPackage2;
import open.dolphin.touch.converter.IVisitPackage;
import open.dolphin.touch.session.IPhoneServiceBean;
import open.orca.rest.ORCAConnection;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Kazushi Minagawa.
 */
@Path("/jtouch")
public class JsonTouchResource extends open.dolphin.rest.AbstractResource {
    
    private static final String QUERY_FACILITYID_BY_1001
            ="select kanritbl from tbl_syskanri where kanricd='1001'";
    
    @Inject
    private IPhoneServiceBean iPhoneService;
    
    @Inject
    private KarteServiceBean karteService;
    
    @Inject
    private ChartEventServiceBean chartService;
    
//minagawa^ 2013/08/29
    //@Resource(mappedName="java:jboss/datasources/OrcaDS")
    //private DataSource ds;
//minagawa$
    
    @GET
    @Path("/user/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModelConverter getUserById(@PathParam("uid") String uid) {
        
        // 検索
        UserModel user = iPhoneService.getUserById(uid);
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);
        
        return conv;
    }
    
    @GET
    @Path("/patient/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public IPatientModel getPatientById(@Context HttpServletRequest servletReq, @PathParam("pid") String pid) {
        
        // ログインに成功しているユーザーの施設ID
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        // 検索
        PatientModel patient = iPhoneService.getPatientById(fid, pid);
        long kartePK = iPhoneService.getKartePKByPatientPK(patient.getId());
        
        // Converter
        IPatientModel model = new IPatientModel();
        model.setModel(patient);
        model.setKartePK(kartePK);
        
        return model;
    }
    
    @GET
    @Path("/patients/name/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public IPatientList getPatientsByNameOrId(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        //System.err.println("getPatientsByNameOrId");
        
        String [] params = param.split(",");
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String name = params[0];
        //System.err.println(name);
        int firstResult = params.length==3 ? Integer.parseInt(params[1]) : 0;
        int maxResult = params.length==3 ? Integer.parseInt(params[2]) :100;

        List<PatientModel> list;

        // ひらがなで始まっている場合はカナに変換する
        if (KanjiHelper.isHiragana(name.charAt(0))) {
            name = KanjiHelper.hiraganaToKatakana(name);
        }

        if (KanjiHelper.isKatakana(name.charAt(0))) {
            list = iPhoneService.getPatientsByKana(fid, name, firstResult, maxResult);

        } else {
            // 漢字で検索
            list = iPhoneService.getPatientsByName(fid, name, firstResult, maxResult);
        }
        
        //System.err.println(list.size());

        PatientList patients = new PatientList();
        patients.setList(list);
        IPatientList ipatients = new IPatientList();
        ipatients.setModel(patients);

        return ipatients;
    }
    
//minagawa^ 音声検索辞書作成
    @GET
    @Path("/patients/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPatientCount(@Context HttpServletRequest servletReq) {
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        int cnt = iPhoneService.countPatients(fid);
        return String.valueOf(cnt);
    }
    
    @GET
    @Path("/patients/dump/kana/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public StringListConverter getPatientsWithKana(@Context HttpServletRequest servletReq, @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String [] params = param.split(",");
        int first = Integer.parseInt(params[0]);
        int max = Integer.parseInt(params[1]);
        
        List<String> list = iPhoneService.getAllPatientsWithKana(fid, first, max);
        StringList strList = new StringList();
        strList.setList(list);
        
        StringListConverter conv = new StringListConverter();
        conv.setModel(strList);
        
        return conv;
    }
//minagawa$    
    
    @GET
    @Path("/visitpackage/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public IVisitPackage getVisitPackage(@PathParam("param") String param) {
        
        String[] params = param.split(",");
        
        long pvtPK = Long.parseLong(params[0]);
        long patientPK = Long.parseLong(params[1]);
        long docPK = Long.parseLong(params[2]);
        int mode = Integer.parseInt(params[3]);
        
        // VisitTouchでカルテ作成に必要なwrapperオブジェクト
        VisitPackage visit = iPhoneService.getVisitPackage(pvtPK, patientPK, docPK, mode);
        
        if (visit.getDocumenModel()!=null) {
            visit.getDocumenModel().toDetuch();
        }
        
        // 保健医療機関コードとJMARI番号
        String number = getFacilityCodeBy1001();
        visit.setNumber(number);
        
        // Converter
        IVisitPackage conv = new IVisitPackage();
        conv.setModel(visit);
        
        return conv;
    }
    
    @POST
    @Path("/sendPackage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postSendPackage(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        ISendPackage pkg = mapper.readValue(json, ISendPackage.class);
        
        long retPk = 0L;
        
        // カルテ文書
        DocumentModel model = pkg.documentModel();
        if (model!=null) {
 //minagawa^ VisitTouch 公費保険不具合        
            DocInfoModel docInfo = model.getDocInfoModel();
            PVTHealthInsuranceModel pvtIns = docInfo.getPVTHealthInsuranceModel();
            if (pvtIns!=null) {
                PVTPublicInsuranceItemModel[] arr;
                arr = pvtIns.getPVTPublicInsuranceItem();
                if (arr!=null && arr.length>0) {
                    List<PVTPublicInsuranceItemModel> list = new ArrayList(arr.length);
                    list.addAll(Arrays.asList(arr));
                    pvtIns.setPublicItems(list);
                }   
            }
//minagawa$      
            retPk = karteService.addDocument(model);
        }
        
        // 病名Wrapper
        DiagnosisSendWrapper wrapper = pkg.diagnosisSendWrapperModel();
        if (wrapper!=null) {
            karteService.postPutSendDiagnosis(wrapper);
        }
        
        // 削除病名
        List<String> deleted = pkg.deletedDiagnsis();
        if (deleted!=null) {
            List<Long> list = new ArrayList(deleted.size());
            for (String str : deleted) {
                list.add(Long.parseLong(str));
            }
            karteService.removeDiagnosis(list);
        }
        
        // Status更新
        ChartEventModel cvt = pkg.chartEventModel();
        if (cvt!=null) {
            chartService.processChartEvent(cvt);
        }
        
        return String.valueOf(retPk);
    }
    
    // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
    @POST
    @Path("/sendPackage2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postSendPackage2(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        ISendPackage2 pkg = mapper.readValue(json, ISendPackage2.class);
        
        long retPk = 0L;
        
        // カルテ文書
        DocumentModel model = pkg.documentModel();
        if (model!=null) {
 //minagawa^ VisitTouch 公費保険不具合        
            DocInfoModel docInfo = model.getDocInfoModel();
            PVTHealthInsuranceModel pvtIns = docInfo.getPVTHealthInsuranceModel();
            if (pvtIns!=null) {
                PVTPublicInsuranceItemModel[] arr;
                arr = pvtIns.getPVTPublicInsuranceItem();
                if (arr!=null && arr.length>0) {
                    List<PVTPublicInsuranceItemModel> list = new ArrayList(arr.length);
                    list.addAll(Arrays.asList(arr));
                    pvtIns.setPublicItems(list);
                }   
            }
//minagawa$      
            retPk = karteService.addDocument(model);
        }
        
        // 病名Wrapper
        DiagnosisSendWrapper wrapper = pkg.diagnosisSendWrapperModel();
        if (wrapper!=null) {
            karteService.postPutSendDiagnosis(wrapper);
        }
        
        // 削除病名
        List<String> deleted = pkg.deletedDiagnsis();
        if (deleted!=null) {
            List<Long> list = new ArrayList(deleted.size());
            for (String str : deleted) {
                list.add(Long.parseLong(str));
            }
            karteService.removeDiagnosis(list);
        }
        
        // Status更新
        ChartEventModel cvt = pkg.chartEventModel();
        if (cvt!=null) {
            chartService.processChartEvent(cvt);
        }
        
        return String.valueOf(retPk);
    }
    // S.Oh 2014/02/06 Add End
    
    @POST
    @Path("/document")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        IDocument document = mapper.readValue(json, IDocument.class);
        DocumentModel model = document.toModel();
        
        long pk = karteService.addDocument(model);
        return String.valueOf(pk);
    }
    
    // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
    @POST
    @Path("/document2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument2(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        IDocument2 document = mapper.readValue(json, IDocument2.class);
        DocumentModel model = document.toModel();
        
        long pk = karteService.addDocument(model);
        return String.valueOf(pk);
    }
    // S.Oh 2014/02/06 Add End
    
    @POST
    @Path("/mkdocument")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postMkDocument(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        IMKDocument document = mapper.readValue(json, IMKDocument.class);
        DocumentModel model = document.toModel();
        
        long pk = karteService.addDocument(model);
        return String.valueOf(pk);
    }
    
    // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
    @POST
    @Path("/mkdocument2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postMkDocument2(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        IMKDocument2 document = mapper.readValue(json, IMKDocument2.class);
        DocumentModel model = document.toModel();
        
        long pk = karteService.addDocument(model);
        return String.valueOf(pk);
    }
    // S.Oh 2014/02/06 Add End
    
    /**
     * 保健医療機関コードとJMARIコードを取得する。
     * @return 
     */
    private String getFacilityCodeBy1001() {
       
//s.oh^ 2013/10/17 ローカルORCA対応
        try {
            // custom.properties から 保健医療機関コードとJMARIコードを読む
            Properties config = new Properties();
            // コンフィグファイルを読み込む
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
            // JMARI code
            String jmari = config.getProperty("jamri.code");
            String hcfacility = config.getProperty("healthcarefacility.code");
            if(jmari != null && jmari.length() == 12 && hcfacility != null && hcfacility.length() == 10) {
                StringBuilder ret = new StringBuilder();
                ret.append(hcfacility);
                ret.append("JPN");
                ret.append(jmari);
                return ret.toString();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonTouchResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JsonTouchResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonTouchResource.class.getName()).log(Level.SEVERE, null, ex);
        }
//s.oh$
        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append(QUERY_FACILITYID_BY_1001);
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;
        
        StringBuilder ret = new StringBuilder();

        try {
//minagawa^ 2013/08/29
            //con = ds.getConnection();
            con = ORCAConnection.getInstance().getConnection();
//minagawa$
            ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String line = rs.getString(1);
                
                // 保険医療機関コード 10桁
                ret.append(line.substring(0, 10));
                
                // JMARIコード JPN+12桁 (total 15)
                int index = line.indexOf("JPN");
                if (index>0) {
                    ret.append(line.substring(index, index+15));
                }
            }
            rs.close();
            ps.close();
            con.close();
            con = null;

        } catch (Exception e) {
            e.printStackTrace(System.err);

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }

        return ret.toString();        
    }
//minagawa^    
    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
    
    private void warn(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
//minagawa$
}
