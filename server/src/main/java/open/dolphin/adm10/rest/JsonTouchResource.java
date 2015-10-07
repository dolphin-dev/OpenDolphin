package open.dolphin.adm10.rest;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import open.dolphin.adm10.converter.IBundleModule;
import open.dolphin.adm10.converter.IOSHelper;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.infomodel.VisitPackage;
import open.dolphin.adm10.converter.IPatientList;
import open.dolphin.adm10.converter.ISendPackage;
import open.dolphin.adm10.converter.IVisitPackage;
import open.dolphin.adm10.session.ADM10_EHTServiceBean;
import open.dolphin.adm10.session.ADM10_IPhoneServiceBean;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.DrugInteractionModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.InteractionCodeList;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.session.ChartEventServiceBean;
import open.dolphin.session.KarteServiceBean;
import open.orca.rest.ORCAConnection;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Kazushi Minagawa.
 */
@Path("/10/adm/jtouch")
public class JsonTouchResource extends open.dolphin.rest.AbstractResource {
    
    private static final String QUERY_FACILITYID_BY_1001
            ="select kanritbl from tbl_syskanri where kanricd='1001'";
    
    @Inject
    private ADM10_IPhoneServiceBean iPhoneService;
    
    @Inject
    private ADM10_EHTServiceBean ehtService;
    
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
        
        //System.err.println(json);
        
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
            try (InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect")) {
                config.load(r);
            }
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

            try (ResultSet rs = ps.executeQuery()) {
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
            }
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
////minagawa^    
//    private void log(String msg) {
//        Logger.getLogger("open.dolphin").info(msg);
//    }
//    
//    private void warn(String msg) {
//        Logger.getLogger("open.dolphin").info(msg);
//    }
////minagawa$
    
    //---------------------------------------------------------------------------
    // EHT から引っ越し
    //---------------------------------------------------------------------------
        @GET
    @Path("/order/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput collectModules(final @PathParam("param") String param) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
               
                String [] params = param.split(",");
                
                long pk = Long.parseLong(params[0]);            // patientPK
                
                Date fromDate = IOSHelper.toDate(params[1]);    // fromDate
                Date toDate = IOSHelper.toDate(params[2]);      // tOdate
                
                List<String> entities;
                
                if (params.length>3)
                {
                    entities = new ArrayList(2);
                    for (int i=3; i <params.length; i++) {
                        entities.add(params[i]);                     // entity
                    }
                }else{
                    entities = new ArrayList(2);
                    entities.add(IInfoModel.ENTITY_MED_ORDER);
                }
                
                List<ModuleModel> list = ehtService.collectModules(pk, fromDate, toDate, entities);
                List<IBundleModule> result = new ArrayList(list.size());

                for (ModuleModel module : list) {
                    if (module.getModel() instanceof BundleDolphin) {
                        IBundleModule ib = new IBundleModule();
                        ib.fromModel(module);
                        BundleDolphin bd = (BundleDolphin)module.getModel();
                        ib.getModel().setOrderName(bd.getOrderName());
                        result.add(ib);
                    }else{
                        IBundleModule ib = new IBundleModule();
                        ib.fromModel(module);
                        result.add(ib);
                    }
                }
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(output, result);
            }
        };
    }
    
    @PUT
    @Path("/interaction")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput checkInteraction(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                ObjectMapper mapper = new ObjectMapper();
                InteractionCodeList input = mapper.readValue(json, InteractionCodeList.class);

                // 相互作用モデルのリスト
                List<DrugInteractionModel> ret = new ArrayList<DrugInteractionModel>();

                if (input.getCodes1() == null       || 
                        input.getCodes1().isEmpty() || 
                        input.getCodes2() == null   || 
                        input.getCodes2().isEmpty()) {
                    mapper = getSerializeMapper();
                    mapper.writeValue(os, ret);
                }

                // SQL文を作成
                StringBuilder sb = new StringBuilder();
                sb.append("select drugcd, drugcd2, TI.syojyoucd, syojyou ");
                sb.append("from tbl_interact TI inner join tbl_sskijyo TS on TI.syojyoucd = TS.syojyoucd ");
                sb.append("where (drugcd in (");
                sb.append(getCodes(input.getCodes1()));
                sb.append(") and drugcd2 in (");
                sb.append(getCodes(input.getCodes2()));
                sb.append("))");
                String sql = sb.toString();

                Connection con = null;
                Statement st = null;

                try {
                    con = getConnection();
                    st = con.createStatement();
                    try (ResultSet rs = st.executeQuery(sql)) {
                        while (rs.next()) {
                            ret.add(new DrugInteractionModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
                        }
                    }
                    closeStatement(st);
                    closeConnection(con);
                    mapper = getSerializeMapper();
                    mapper.writeValue(os, ret);
                    
                } catch (SQLException | IOException e) {
                    processError(e);
                    closeStatement(st);
                    closeConnection(con);
                    throw new WebApplicationException(e);
                }
            }
        };
    }    
//--------------------------------------------------------------------    
    
    @GET
    @Path("/stampTree/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getStampTree(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                long pk = Long.parseLong(param);
                String json = getTreeJson(pk);
                os.write(json.getBytes());
            }
        };
    }
    
    private String getTreeJson(long userPK) {
        
        IStampTreeModel treeModel = ehtService.getTrees(userPK);
        
        try {
            String treeXml = new String(treeModel.getTreeBytes(), "UTF-8");
            String json;
            try (BufferedReader reader = new BufferedReader(new StringReader(treeXml))) {
                JSONStampTreeBuilder builder = new JSONStampTreeBuilder();
                StampTreeDirector director = new StampTreeDirector(builder);
                json = director.build(reader);
            }
            return json;
        } catch (UnsupportedEncodingException ex) {
            //System.err.println("getTreeJson:" + ex.getMessage());
        } catch (IOException ex) {
            //System.err.println("getTreeJson:" + ex.getMessage());
        }
        return null;
    }
    
    @GET
    @Path("/stamp/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getStamp(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {

                StampModel stampModel = ehtService.getStamp(param);
                if (stampModel!=null) {
                    XMLDecoder d = new XMLDecoder(
                        new BufferedInputStream(
                        new ByteArrayInputStream(stampModel.getStampBytes())));
                    InfoModel model = (InfoModel)d.readObject();
                    JSONStampBuilder builder = new JSONStampBuilder();
                    String json = builder.build(model);
                    os.write(json.getBytes());
                } else {
                    os.write(null);
                }
            }
        };
    }
    
    // srycdのListからカンマ区切りの文字列を作る
    private String getCodes(Collection<String> srycdList){

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String srycd : srycdList){
            if (!first){
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(addSingleQuote(srycd));
        }
        return sb.toString();
    }
    
    private String addSingleQuote(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(str).append("'");
        return sb.toString();
    }
    
    private Connection getConnection() {
        return ORCAConnection.getInstance().getConnection();
    }
    
    private void closeStatement(java.sql.Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e) {
            	e.printStackTrace(System.err);
            }
        }
    }
    
    private void closeConnection(Connection c) {
        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    private void processError(Throwable e) {
        e.printStackTrace(System.err);
    }
}
