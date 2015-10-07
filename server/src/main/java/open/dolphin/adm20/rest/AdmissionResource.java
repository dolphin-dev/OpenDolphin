package open.dolphin.adm20.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import open.dolphin.adm20.session.ADM20_AdmissionServiceBean;
import open.dolphin.adm20.ICarePlanModel;
import open.dolphin.adm20.OTPHelper;
import open.dolphin.adm20.PlivoSender;
import open.dolphin.adm20.converter.IDocument;
import open.dolphin.adm20.converter.ILastDateCount30;
import open.dolphin.adm20.converter.INurseProgressCourse;
import open.dolphin.adm20.converter.IOSHelper;
import open.dolphin.adm20.converter.IOndobanModel30;
import open.dolphin.adm20.converter.ISendPackage;
import open.dolphin.adm20.session.ADM20_EHTServiceBean;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.CarePlanModel;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.DiagnosisSendWrapper;

import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.Factor2Code;
import open.dolphin.infomodel.Factor2Spec;
import open.dolphin.infomodel.LastDateCount30;
import open.dolphin.infomodel.NurseProgressCourseModel;
import open.dolphin.infomodel.OndobanModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.SMSMessage;
import open.dolphin.infomodel.UserModel;
import open.dolphin.session.ChartEventServiceBean;
import open.orca.rest.ORCAConnection;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;



/*DocumentModel kazushi Minagawa
 */
@Path("/20/adm")
public class AdmissionResource extends open.dolphin.rest.AbstractResource {
    
    @Inject
    private ADM20_AdmissionServiceBean admissionService;
    
    @Inject
    private ChartEventServiceBean chartService;
    
    @Inject
    private ADM20_EHTServiceBean ehtService;
    
    // VisitTouch2 Admission Model
    
    //  /10/eht/karteNumber/
    //  /10/eht/memo/
    //  /10/eht/allergy/
    //  /10/eht/diagnosis/
    //  /10/eht/progresscourse
    //  /10/eht/module/laboTest/
    //  /10/eht/item/
    //  /10/eht/ondoban/
    //  /10/eht/ondoban
    //  /10/eht/nurseProgressCourse
    
    //  /10/adm/sendPackage 重複 ???
    //---------------------------------------------------------------------------
    
    @GET
    @Path("/carePlan/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getCarePlans(final @Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                long ptPK = Long.parseLong(param);
                List<CarePlanModel> list = admissionService.getCarePlans(ptPK);
                List<ICarePlanModel> result = new ArrayList(list.size());
                for (CarePlanModel model : list) {
                    ICarePlanModel conv = new ICarePlanModel();
                    conv.fromModel(model);
                    result.add(conv);
                }
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @POST
    @Path("/carePlan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput postCarePlan(final @Context HttpServletRequest servletReq,final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ObjectMapper mapper = new ObjectMapper();
                ICarePlanModel conv = mapper.readValue(json, ICarePlanModel.class);
                long pk = admissionService.addCarePlan(conv.toModel());
                List<Long> result = new ArrayList(1);
                result.add(pk);
                mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @PUT
    @Path("/carePlan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput putCarePlan(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ObjectMapper mapper = new ObjectMapper();
                ICarePlanModel conv = mapper.readValue(json, ICarePlanModel.class);
                CarePlanModel model = conv.toModel();
                int cnt = admissionService.updateCarePlan(model);
                List<Integer> result = new ArrayList(1);
                result.add(cnt);
                mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @DELETE
    @Path("/carePlan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput deleteCarePlan(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ObjectMapper mapper = new ObjectMapper();
                ICarePlanModel model = mapper.readValue(json, ICarePlanModel.class);
                int cnt = admissionService.deleteCarePlan(model.toModel());
                List<Integer> result = new ArrayList(1);
                result.add(cnt);
                mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @GET
    @Path("/lastDateCount/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getLastDateCount(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                String [] params = param.split(",");
                long ptPK = Long.parseLong(params[0]);
                StringBuilder sb = new StringBuilder();
                sb.append(params[1]).append(":").append(params[2]);
                String fidPid = sb.toString();
                LastDateCount30 data = admissionService.getLastDateCount(ptPK, fidPid);
                ILastDateCount30 result = new ILastDateCount30();
                result.fromModel(data);
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @GET
    @Path("/docid/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getDocIdList(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                String[] params = param.split(",");
                long ptPK = Long.parseLong(params[0]);
                Date startDate = dateFromString(params[1]);
                Collection<Long> result = admissionService.getDocIdList(ptPK, startDate);
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    
    @GET
    @Path("/document/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getDocument(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                long docPK = Long.parseLong(param);
                DocumentModel doc = admissionService.getDocumentByPk(docPK);
                doc.toDetuch();
                IDocument conv = new IDocument();
                conv.fromModel(doc);
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, conv);
            }
        };
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
            retPk = admissionService.addDocument(model);
        }
        
        // 病名Wrapper
        DiagnosisSendWrapper wrapper = pkg.diagnosisSendWrapperModel();
        if (wrapper!=null) {
            admissionService.postPutSendDiagnosis(wrapper);
        }
        
        // 削除病名
        List<String> deleted = pkg.deletedDiagnsis();
        if (deleted!=null) {
            List<Long> list = new ArrayList(deleted.size());
            for (String str : deleted) {
                list.add(Long.parseLong(str));
            }
            admissionService.removeDiagnosis(list);
        }
        
        // Status更新
        ChartEventModel cvt = pkg.chartEventModel();
        if (cvt!=null) {
            chartService.processChartEvent(cvt);
        }
        
        return String.valueOf(retPk);
    }
    
    private Date dateFromString(String str) {
        
        if (str.length()>10) {
            str = str.substring(10);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(str);
        } catch (ParseException ex) {
            Logger.getLogger(AdmissionResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
//--------------------------------------------------------------------------------    
    /* VisitTouch 1.5 リソース
    -/jtouch/visitpackage/
    -/jtouch/user/
    -/touch/stampTree/
    -/touch/stamp/
    -/jtouch/patients/name/
    -/jtouch/sendPackage
    -/10/eht/order/
    /10/eht/interaction
    */
//--------------------------------------------------------------------------------  
    
    //--------------------------------------------------------------------------------------- 
    // 温度板対応
    //---------------------------------------------------------------------------------------
    @GET
    @Path("/ondoban/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getOndoban(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                // パラメータ抽出
                String [] params = param.split(",");
                long pk = Long.parseLong(params[0]);            // patientPK
                Date fromDate = IOSHelper.toDate(params[1]);    // fromDate
                Date toDate = IOSHelper.toDate(params[2]);      // tODate  
                
                // 検索
                List<OndobanModel> list = ehtService.getOndoban(pk, fromDate, toDate);
                
                // Converter
                List<IOndobanModel30> result = new ArrayList();
                for (OndobanModel m : list) {
                    IOndobanModel30 om = new IOndobanModel30();
                    om.fromModel(m);
                    result.add(om);
                }
                
                // 出力
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    @POST
    @Path("/ondoban")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput postOndoban(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IOndobanModel30[] array = mapper.readValue(json, IOndobanModel30[].class);
                
                ArrayList<OndobanModel> saveList = new ArrayList(array.length);
                for (IOndobanModel30 am : array) {
                    OndobanModel om = am.toModel();
                    saveList.add(om);
                }
                
                List<Long> pkList = ehtService.addOndoban(saveList);
                mapper = getSerializeMapper();
                mapper.writeValue(os, pkList);
            }
        };
    }
    @PUT
    @Path("/ondoban")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput updateOndoban(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IOndobanModel30[] array = mapper.readValue(json, IOndobanModel30[].class);
                
                ArrayList<OndobanModel> updateList = new ArrayList(array.length);
                for (IOndobanModel30 am : array) {
                    OndobanModel om = am.toModel();
                    updateList.add(om);
                }
                
                int cnt = ehtService.updateOndoban(updateList);
                mapper = getSerializeMapper();
                mapper.writeValue(os, String.valueOf(cnt));
            }
        };
    }
    @DELETE
    @Path("/ondoban")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput deleteOndoban(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IOndobanModel30[] array = mapper.readValue(json, IOndobanModel30[].class);
                
                ArrayList<OndobanModel> updateList = new ArrayList(array.length);
                for (IOndobanModel30 am : array) {
                    OndobanModel om = am.toModel();
                    updateList.add(om);
                }
                
                int cnt = ehtService.deleteOndoban(updateList);
                mapper = getSerializeMapper();
                mapper.writeValue(os, String.valueOf(cnt));
            }
        };
    }
    //--------------------------------------------------------------------------------------- 
    // 看護記録
    //---------------------------------------------------------------------------------------
    @GET
    @Path("/nurseProgressCourse/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getNurseProgressCourse(final @PathParam("param") String param) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                // パラメータ抽出
                String [] params = param.split(",");
                long pk = Long.parseLong(params[0]);            // patientPK
                int firstResult = Integer.parseInt(params[1]);
                int maxResult = Integer.parseInt(params[2]);
                
                // 検索
                List<NurseProgressCourseModel> list = ehtService.getNurseProgressCourse(pk, firstResult, maxResult);
                
                // Converter
                List<INurseProgressCourse> result = new ArrayList();
                for (NurseProgressCourseModel model : list) {
                    INurseProgressCourse conv = new INurseProgressCourse();
                    conv.fromModel(model);
                    result.add(conv);
                }
                
                // 出力
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, result);
            }
        };
    }
    @POST
    @Path("/nurseProgressCourse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput postNurseProgressCourse(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                INurseProgressCourse conv = mapper.readValue(json, INurseProgressCourse.class);
                
                NurseProgressCourseModel model = conv.toModel();
                Long pk = ehtService.addNurseProgressCourse(model);
                List<Long> pkList = new ArrayList(1);
                pkList.add(pk);
                
                mapper = getSerializeMapper();
                mapper.writeValue(os, pkList);
            }
        };
    }
    @PUT
    @Path("/nurseProgressCourse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput updateNurseProgressCourse(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                INurseProgressCourse conv = mapper.readValue(json, INurseProgressCourse.class);
                
                NurseProgressCourseModel model = conv.toModel();
                int cnt = ehtService.updateNurseProgressCourse(model);
                
                mapper = getSerializeMapper();
                mapper.writeValue(os, String.valueOf(cnt));
            }
        };
    }
    @DELETE
    @Path("/nurseProgressCourse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput deleteNurseProgressCourse(final String json) {
        
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                INurseProgressCourse conv = mapper.readValue(json, INurseProgressCourse.class);
                
                NurseProgressCourseModel model = conv.toModel();
                int cnt = ehtService.deleteNurseProgressCourse(model);
                
                mapper = getSerializeMapper();
                mapper.writeValue(os, String.valueOf(cnt));
            }
        };
    }
    
    @PUT
    @Path("/sms/message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput sendSMSMessage(final String json) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                //System.out.println(json);
                ObjectMapper mapper = new ObjectMapper();
                SMSMessage sms = mapper.readValue(json, SMSMessage.class);
                
                PlivoSender plivo = new PlivoSender();
                plivo.send(sms.getNumbers(), sms.getMessage());
                
                mapper = getSerializeMapper();
                mapper.writeValue(output, String.valueOf(sms.getNumbers().size()));
            }
        };
    }
    
    @PUT
    @Path("/factor2/code")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getFactor2Code(final String json) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                Factor2Code spec = mapper.readValue(json, Factor2Code.class);
                
                // One time password
                OTPHelper helper = new OTPHelper();
                long code = helper.getOTP();
                spec.setCode(String.valueOf(code));
                
                // persist temporaly
                ehtService.saveFactor2Code(spec);
                
                // ユーザーのモバイルへ送信
                List<String> numbers = new ArrayList(1);
                numbers.add(spec.getMobileNumber());
                
                PlivoSender plivo = new PlivoSender();
                plivo.send(numbers, String.valueOf(code));
                
                mapper = getSerializeMapper();
                mapper.writeValue(output, "1");
            }
        };
    }
    
    @PUT
    @Path("/factor2/device")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput putFactor2Device(final String json) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                
                ObjectMapper mapper = new ObjectMapper();
                Factor2Spec spec = mapper.readValue(json, Factor2Spec.class);
                
                // Backup key
                String bkey = new OTPHelper().getBackupKey();
                spec.setBackupKey(bkey);
                
                // 保存
                try {
                    ehtService.saveFactor2(spec);
                } catch (NoResultException ne) {
                    //System.err.println("NoResultException");
                    throw new WebApplicationException(ne, 404);
                } catch (Exception e) {
                    //System.err.println("Exception " + e.getClass());
                    throw new WebApplicationException(e, 404);
                }
                
                mapper = getSerializeMapper();
                mapper.writeValue(output, bkey);
            }
        };
    }
    
    @DELETE
    @Path("/factor2/auth/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput resetFactor2Auth(final @PathParam("param") String param) {
        
        return new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                
                long userPK = Long.parseLong(param);
                
                // 削除
                ehtService.resetFactor2Auth(userPK);
                
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(output, "1");
            }
        };
    }
    
    @PUT
    @Path("/user/factor2/device")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModelConverter getUserWithNewFactor2Device(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Factor2Spec spec = mapper.readValue(json, Factor2Spec.class);
        
        UserModel result = ehtService.getUserWithNewFactor2Device(spec);
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(result);
        return conv;
    }
    
    @PUT
    @Path("/user/factor2/backup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModelConverter getUserWithF2Backup(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Factor2Spec spec = mapper.readValue(json, Factor2Spec.class);
        
        UserModel result = ehtService.getUserWithF2Backup(spec);
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(result);
        return conv;
    }
//minagawa$  
    
    /**
     * 保健医療機関コードとJMARIコードを取得する。
     * @return 
     */
    private String getFacilityCodeBy1001() {
       
////s.oh^ 2013/10/17 ローカルORCA対応
//        try {
//            // custom.properties から 保健医療機関コードとJMARIコードを読む
//            Properties config = new Properties();
//            // コンフィグファイルを読み込む
//            StringBuilder sb = new StringBuilder();
//            sb.append(System.getProperty("jboss.home.dir"));
//            sb.append(File.separator);
//            sb.append("custom.properties");
//            File f = new File(sb.toString());
//            FileInputStream fin = new FileInputStream(f);
//            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
//            config.load(r);
//            r.close();
//            // JMARI code
//            String jmari = config.getProperty("jamri.code");
//            String hcfacility = config.getProperty("healthcarefacility.code");
//            if(jmari != null && jmari.length() == 12 && hcfacility != null && hcfacility.length() == 10) {
//                StringBuilder ret = new StringBuilder();
//                ret.append(hcfacility);
//                ret.append("JPN");
//                ret.append(jmari);
//                return ret.toString();
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(EHTResource.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(EHTResource.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(EHTResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
////s.oh$
        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append("select kanritbl from tbl_syskanri where kanricd='1001'");
        String sql = buf.toString();

        Connection con = null;
        PreparedStatement ps;
        
        StringBuilder ret = new StringBuilder();

        try {
            //con = ds.getConnection();
            con = getConnection();
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

        } catch (SQLException e) {
            e.printStackTrace(System.err);

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }

        return ret.toString();        
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
}
