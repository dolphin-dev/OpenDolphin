package open.dolphin.adm20.rest;

import java.io.OutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import open.dolphin.adm20.converter.IOSHelper;
import open.dolphin.adm20.mbean.IdentityService;
import open.dolphin.adm20.session.AMD20_PHRServiceBean;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PHRBundle;
import open.dolphin.infomodel.PHRClaimItem;
import open.dolphin.infomodel.PHRContainer;
import open.dolphin.infomodel.PHRCatch;
import open.dolphin.infomodel.PHRKey;
import open.dolphin.infomodel.PHRLabItem;
import open.dolphin.infomodel.PHRLabModule;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.orca.rest.ORCAConnection;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author kazushi Minagawa
 */
@Path("/20/adm/phr")
public class PHRResource extends open.dolphin.rest.AbstractResource {
    
    private static final int PHR_MAX_COUNT = 3;
    
    @Inject
    private AMD20_PHRServiceBean phrServiceBean;
    
    @Inject
    private IdentityService identityService;
    
    @GET
    @Path("/accessKey/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getPHRKeyByAccessKey(final @PathParam("param") String param) {
        
        return (OutputStream output) -> {
            // param=accessKey
            PHRKey phrKey = phrServiceBean.getPHRKey(param);
            if (phrKey==null) {
                throw new WebApplicationException(404);
            }
            ObjectMapper mapper = getSerializeMapper();
            mapper.writeValue(output, phrKey);
        };
    }
    
    @GET
    @Path("/patient/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getPHRKeyByPatientId(final @PathParam("param") String param) {
        
        return (OutputStream output) -> {
            // param=patientId
            PHRKey phrKey = phrServiceBean.getPHRKeyByPatientId(param);
            if (phrKey==null) {
                throw new WebApplicationException(404);
            }
            // Date- > String 変換 for iOS
            phrKey.dateToString();
            ObjectMapper mapper = getSerializeMapper();
            mapper.writeValue(output, phrKey);
        };
    }
    
    @PUT
    @Path("/accessKey")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput putPHRKey(final String json) {
        
        return (OutputStream output) -> {
            ObjectMapper mapper = new ObjectMapper();
            PHRKey model = mapper.readValue(json, PHRKey.class);
            // String -> Date 変換 for iOS
            model.stringToDate();
            // 登録日を設定する
            if (model.getRegistered()==null) {
                model.setRegistered(new Date());
            }
            Long pk = phrServiceBean.addOrUpdatePatient(model);
            mapper = getSerializeMapper();
            mapper.writeValue(output, pk);
        };
    }
    
    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getPHRData(final @PathParam("param") String param) {
        
        return (OutputStream output) -> {
            
            int rpRequest = 0;      // false ios
            String replyTo = null;  // replyTo
                    
            String [] params = param.split(",");
            String fid = params[0];                                     // 医療機関ID
            String pid = params[1];                                     // 患者ID
            String docSince = params.length>=3 ? params[2] : null;      // 最終処方（ドキュメント）日 これより後のデータをとる
            String labSince = params.length>=4 ? params[3] : null;      // 最終検査日  
            
            // 処方効能
            if (params.length == 6) {
               rpRequest = Integer.parseInt(params[4]);         // 効能の返事希望
               replyTo = params[5];                             // 返信先 mobile number
            }
            
            int first = 0;
            int max = PHR_MAX_COUNT;
            String[] entities = new String[]{"medOrder","injectionOrder"};
            
            List<PHRCatch> docList = getPHRDocList(fid, pid, docSince, first, max, entities, rpRequest, replyTo);
            List<PHRLabModule> labList = getPHRLabList(fid, pid, labSince, first, max);
            
            PHRContainer container = new PHRContainer();
            container.setDocList(docList);
            container.setLabList(labList);
            
            ObjectMapper mapper = getSerializeMapper();
            mapper.writeValue(output, container);
        };
    }
    
    @GET
    @Path("/allergy/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllergy(@Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;             // 患者ID  
        
        // Karte
        KarteBean karte = phrServiceBean.getKarte(fid, pid);
        List<AllergyModel> list = phrServiceBean.getAllergies(karte.getId());
        
        if (list.isEmpty()) {
            return "アレルギーの登録はありません。";
        }
        
        StringBuilder sb = new StringBuilder();
        for (AllergyModel allergy : list) {
            sb.append(allergy.getFactor());
            if (allergy.getSeverity()!=null && allergy.getSeverity().equals("severe")) {
                sb.append(" ").append(allergy.getSeverity());
            }
            sb.append("\n");
        }
        if (sb.length()>0) {
            int len = sb.length()-1;
            sb.setLength(len);
        }
        return sb.toString();
    }
    
    @GET
    @Path("/disease/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDisease(@Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;             // 患者ID  
        
        // Karte
        KarteBean karte = phrServiceBean.getKarte(fid,pid);
        List<RegisteredDiagnosisModel> list = phrServiceBean.getDiagnosis(karte.getId());
        
        if (list.isEmpty()) {
            return "病名の登録はありません。";
        }
        
        StringBuilder sb = new StringBuilder();
        for (RegisteredDiagnosisModel rd : list) {
            sb.append(rd.getDiagnosis()).append("\n");
        }
        if (sb.length()>0) {
            int len = sb.length()-1;
            sb.setLength(len);
        }
        return sb.toString();
    }
    
    @GET
    @Path("/medication/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLastMedication(@Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;             // 患者ID  
        
        // Karte
        KarteBean karte = phrServiceBean.getKarte(fid,pid);
        
        // Medication
        List<ModuleModel> modules = phrServiceBean.getLastMedication(karte.getId());
        
        if (modules.isEmpty()) {
            return "処方の登録はありません。";
        }
        
        // Text
        StringBuilder sb = new StringBuilder();
        int count = 0;
        
        for (ModuleModel mm : modules) {
            if (count==0) {
                String date = new SimpleDateFormat("yyyy年M月d日").format(mm.getStarted());
                sb.append(date).append("\n");
            }
            count++;
            
            // ClaimBundleをデコード バンドル属性セット
            ClaimBundle bundle = (ClaimBundle)IOSHelper.xmlDecode(mm.getBeanBytes());
            
            // ClaimItems
            ClaimItem[] items = bundle.getClaimItem();
            if (items!=null && items.length>0) {
                for (ClaimItem item : items) {
                    sb.append(item.getName()).append(" x ").append(item.getNumber()).append(item.getUnit()).append("\n");
                }
            }
            
            sb.append(bundle.getAdmin()).append(" x ").append(bundle.getBundleNumber()).append("\n");
        }
        
        if (sb.length()>0) {
            int len = sb.length()-1;
            sb.setLength(len);
        }
        return sb.toString();
    }
    
    @GET
    @Path("/labtest/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLastLabTest(@Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;             // 患者ID  

        // LabTestModule
        List<NLaboModule> list = phrServiceBean.getLastLabTest(fid, pid);
        
        if (list.isEmpty()) {
            return "検査の登録はありません。";
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (NLaboModule module : list) {

            // SampleDate 規格化して返す
            sb.append(normalizeSampleDate2(module.getSampleDate())).append("\n");
            
            List<NLaboItem> testItems = module.getItems();
            
            for (NLaboItem item : testItems) {
                
                sb.append(item.getItemName()).append("=").append(item.getValue());
                if (item.getUnit()!=null) {
                    sb.append(item.getUnit());
                }
                if (item.getAbnormalFlg()!=null) {
                    sb.append("(").append(item.getAbnormalFlg()).append(")");
                }
                
                sb.append("\n");
            }
        }
        
        if (sb.length()>0) {
            int len = sb.length()-1;
            sb.setLength(len);
        }
        
        return sb.toString();
    }
    
    @GET
    @Path("/abnormal/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAbnormalValue(@Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;             // 患者ID  

        // LabTestModule
        List<NLaboModule> list = phrServiceBean.getLastLabTest(fid, pid);
        
        if (list.isEmpty()) {
            return "検査の登録はありません。";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean hasAbnormalValue = false;
        
        for (NLaboModule module : list) {

            // SampleDate 規格化して返す
            sb.append(normalizeSampleDate2(module.getSampleDate())).append("\n");
            
            List<NLaboItem> testItems = module.getItems();
            
            for (NLaboItem item : testItems) {
                
                if (item.getAbnormalFlg()!=null) {
                    hasAbnormalValue = true;
                    sb.append(item.getItemName()).append("=").append(item.getValue());
                    if (item.getUnit()!=null) {
                        sb.append(item.getUnit());
                    }
                    sb.append("(").append(item.getAbnormalFlg()).append(")").append("\n");
                }
            }
        }
        
        if (!hasAbnormalValue) {
            return "異常値はありません。";
        }
        else  {
            int len = sb.length()-1;
            sb.setLength(len);
            return sb.toString();
        }
    }
    
    @GET
    @Path("/image/{param}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getImage(final @Context HttpServletRequest servletReq, final @PathParam("param") String param) {
        
        return (OutputStream os) -> {
            String fid = getRemoteFacility(servletReq.getRemoteUser());
            String pid = param;             // 患者ID
            
            // Karte
            KarteBean karte = phrServiceBean.getKarte(fid,pid);
            
            SchemaModel image = phrServiceBean.getImages(karte.getId());
            
            if (image!=null) {
                os.write(image.getJpegByte()); 
            }
        };
    }
    
    private List<PHRCatch> getPHRDocList(String fid, String pid, String docSince, int first, int max, String[] entities, int rpRequest, String replyTo) {
        
        // Dateへ変換
        Date sinceDate = (docSince!=null) ? startedFromString(docSince) : null;

        // Karte
        KarteBean karte = phrServiceBean.getKarte(fid,pid);

        // Document
        List<DocumentModel> list = phrServiceBean.getDocuments(karte.getId(), sinceDate, first, max, entities);

        // PHRDocument
        List<PHRCatch> result = new ArrayList(list.size());

        // Convert
        list.stream().forEach((doc) -> {
            PHRCatch phrCatch = new PHRCatch();
            result.add(phrCatch);
            doc.toDetuch();
            phrCatch.setCatchId(doc.getDocInfoModel().getDocId());              // CatchId=DocId
            phrCatch.setStarted(stringFromStarted(doc.getStarted()));
            phrCatch.setConfirmed(stringFromStarted(doc.getConfirmed()));
            phrCatch.setStatus(doc.getStatus());
            phrCatch.setPatientId(karte.getPatientModel().getPatientId());
            phrCatch.setPatientName(karte.getPatientModel().getFullName());
            phrCatch.setPatientSex(karte.getPatientModel().getGender());
            phrCatch.setPatientBirthday(karte.getPatientModel().getBirthday());
            phrCatch.setPhysicianId(doc.getUserModel().getUserId());
            phrCatch.setPhysicianName(doc.getUserModel().getCommonName());
            phrCatch.setDepartment(doc.getUserModel().getDepartmentModel().getDepartment());
            phrCatch.setDepartmentDesc(doc.getUserModel().getDepartmentModel().getDepartmentDesc());
            phrCatch.setLicense(doc.getUserModel().getLicenseModel().getLicense());
            phrCatch.setFacilityId(doc.getUserModel().getFacilityModel().getFacilityId());
            phrCatch.setFacilityName(doc.getUserModel().getFacilityModel().getFacilityName());
            phrCatch.setFacilityNumber(doc.getDocInfoModel().getJMARICode());
            
            // 処方効能
            phrCatch.setRpRequest(rpRequest);   // 0=false 1=true
            phrCatch.setRpReply(0);     // 0=false
            if (replyTo != null) {
                phrCatch.setRpReplyTo(replyTo);  // SMS 返信先
            }
            
            List<ModuleModel> modules = doc.getModules();

            if (!(modules==null || modules.isEmpty())) {
                
                modules.stream().forEach((mm) -> {
                    // PHRBundleを生成してリストへ追加
                    PHRBundle pcb = new PHRBundle();
                    phrCatch.addBundle(pcb);
                    
                    // CatchId
                    pcb.setCatchId(phrCatch.getCatchId());
                    
                    // bundleId  CoreData fetchResultController && uitableview で使用
                    //pcb.setBundleId(createModuleId(phrCatch.getCatchId(), mm.getModuleInfoBean().getStampNumber()));
                    pcb.setBundleId(createModuleId(phrCatch.getStarted(), modules.size()-mm.getModuleInfoBean().getStampNumber()));
                    //pcb.setBundleId(phrCatch.getStarted());
                    
                    // Entry情報
                    pcb.setStarted(stringFromStarted(mm.getStarted()));
                    pcb.setConfirmed(stringFromStarted(mm.getConfirmed()));
                    pcb.setStatus(mm.getStatus());
                    
                    // Module情報
                    pcb.setEnt(mm.getModuleInfoBean().getEntity());             // CoreData 制約 entity 使用不可のため
                    pcb.setRole(mm.getModuleInfoBean().getStampRole());
                    pcb.setNumber(mm.getModuleInfoBean().getStampNumber());
                    
                    // ClaimBundleをデコード バンドル属性セット
                    ClaimBundle bundle = (ClaimBundle)IOSHelper.xmlDecode(mm.getBeanBytes());
                    pcb.setAdmin(bundle.getAdmin());
                    pcb.setAdminCode(bundle.getAdminCode());
                    pcb.setAdminCodeSystem(bundle.getAdminCodeSystem());
                    pcb.setAdminMemo(bundle.getAdminMemo());
                    pcb.setBundleNumber(bundle.getBundleNumber());
                    pcb.setClsCode(bundle.getClassCode());                      // CoreData 制約
                    pcb.setClsCodeSystem(bundle.getClassCodeSystem());          // CoreData 制約
                    pcb.setClsName(bundle.getClassName());                      // CoreData 制約
                    pcb.setInsurance(bundle.getInsurance());
                    pcb.setMemo(bundle.getMemo());
                    pcb.setOrderName(pcb.getEnt());
                    // ClaimItems
                    ClaimItem[] items = bundle.getClaimItem();
                    if (items!=null && items.length>0) {
                        for (ClaimItem item : items) {

                            PHRClaimItem phrItem = new PHRClaimItem();
                            pcb.addPHRClaimItem(phrItem);

                            phrItem.setClsCode(item.getClassCode());            // CoreData 制約
                            phrItem.setClsCodeSystem(item.getClassCodeSystem());// CoreData 制約
                            phrItem.setCode(item.getCode());
                            phrItem.setCodeSystem(item.getCodeSystem());
                            phrItem.setMemo(item.getMemo());
                            phrItem.setName(item.getName());
                            phrItem.setQuantity(item.getNumber());              // ios=quantity od=number
                            phrItem.setNumberCode(item.getNumberCode());
                            phrItem.setNumberCodeSystem(item.getNumberCodeSystem());
                            phrItem.setUnit(item.getUnit());
                            phrItem.setYkzKbn(item.getYkzKbn());
                        }
                    }
                });
            }
        });
        
        return result;
    }
    
    private List<PHRLabModule> getPHRLabList(String fid, String pid, String labSince, int first, int max) {
        
        // Patient
        PatientModel patient = phrServiceBean.getPatient(fid, pid);
            
        // Facility
        FacilityModel facility = phrServiceBean.getFacility(fid);

        // LabTestModule
        List<NLaboModule> list = phrServiceBean.getLabTest(fid, pid, labSince, first, max);

        // PHRCatch 固有
        String jmariCode = getFacilityCodeBy1001();
        if (jmariCode!=null && !jmariCode.equals("")) {
            // JMARIのみ
            int index = jmariCode.indexOf("JPN");
            if (index>0) {
                jmariCode = jmariCode.substring(index, index+15);
            }
        } else {
            // Dolphinの医療機関コード
            jmariCode = fid;
        }

        List<PHRLabModule> result = new ArrayList(list.size());

        for (NLaboModule module : list) {

            PHRLabModule phrLabModule = new PHRLabModule();
            result.add(phrLabModule);

            // LabModuleのID
            String mId = createLabModuleId(module.getSampleDate(), jmariCode, pid, module.getLaboCenterCode());
            phrLabModule.setCatchId(mId);                                      // CatchId
            phrLabModule.setPatientName(patient.getFullName());
            phrLabModule.setPatientSex(patient.getGender());
            phrLabModule.setPatientBirthday(patient.getBirthday());
            phrLabModule.setFacilityId(facility.getFacilityId());
            phrLabModule.setFacilityName(facility.getFacilityName());
            phrLabModule.setFacilityNumber(jmariCode);

            phrLabModule.setLabCenterCode(module.getLaboCenterCode());
            // SampleDate 規格化して返す
            String sampleDate = normalizeSampleDate(module.getSampleDate());
            phrLabModule.setSampleDate(sampleDate);
            
            List<NLaboItem> testItems = module.getItems();
            
            // numOfItems ?
            if (module.getNumOfItems()!=null && !module.getNumOfItems().equals("")) {
                phrLabModule.setNumOfItems(module.getNumOfItems());
            } else {
                phrLabModule.setNumOfItems(String.valueOf(module.getNumOfItems()));
            }
            
            for (NLaboItem item : testItems) {
                PHRLabItem phrLabItem = new PHRLabItem();
                phrLabModule.addTestItem(phrLabItem);

                phrLabItem.setAbnormalFlg(item.getAbnormalFlg());
                phrLabItem.setComment1(item.getComment1());
                phrLabItem.setComment2(item.getComment2());
                phrLabItem.setCommentCode1(item.getCommentCode1());
                phrLabItem.setCommentCode2(item.getCommentCode2());
                phrLabItem.setDialysis(item.getDialysis());
                phrLabItem.setGroupCode(item.getGroupCode());
                phrLabItem.setGroupName(item.getGroupName());
                phrLabItem.setHemolysis(item.getHemolysis());
                phrLabItem.setItemCode(item.getItemCode());
                phrLabItem.setItemName(item.getItemName());
                phrLabItem.setLabCode(phrLabModule.getLabCenterCode());    // module から
                //System.err.println("LabCenterCode="+phrLabItem.getLaboCode());
                phrLabItem.setLipemia(item.getLipemia());
                phrLabItem.setMedisCode(item.getMedisCode());
                //phrLabItem.setModule_id(phrLabModule.getCatchId());
                phrLabItem.setNormalValue(item.getNormalValue());
                phrLabItem.setParentCode(item.getParentCode());
                phrLabItem.setReportStatus(item.getReportStatus());
                // SampleDate
                phrLabItem.setSampleDate(sampleDate);
                phrLabItem.setSortKey(item.getSortKey());
                phrLabItem.setSpecimenCode(item.getSpecimenCode());
                phrLabItem.setSpecimenName(item.getSpecimenName());
                phrLabItem.setUnit(item.getUnit());
                phrLabItem.setValue(item.getValue());
            }
        }
        return result;
    }
    
    private String createModuleId(String docId, int numberAsStamp) {
        StringBuilder sb = new StringBuilder();
        sb.append(docId).append("-").append(String.format("%03d", numberAsStamp));
        return sb.toString();
    }
    
    private String normalizeSampleDate(String sampleDate) {
        try {
            sampleDate = sampleDate.replaceAll(" ", "");
            sampleDate = sampleDate.replaceAll("T", "");
            sampleDate = sampleDate.replaceAll("/", "");
            sampleDate = sampleDate.replaceAll("-", "");
            sampleDate = sampleDate.replaceAll(":", "");
            
            if (sampleDate.length()=="yyyyMMdd".length()) {
                sampleDate += "000000";
            } else if (sampleDate.length()=="yyyyMMddHHmm".length()) {
                sampleDate += "00";
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d = sdf.parse(sampleDate);
            
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return sdf.format(d);
            
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    private String normalizeSampleDate2(String sampleDate) {
        try {
            sampleDate = sampleDate.replaceAll(" ", "");
            sampleDate = sampleDate.replaceAll("T", "");
            sampleDate = sampleDate.replaceAll("/", "");
            sampleDate = sampleDate.replaceAll("-", "");
            sampleDate = sampleDate.replaceAll(":", "");
            
            if (sampleDate.length()=="yyyyMMdd".length()) {
                sampleDate += "000000";
            } else if (sampleDate.length()=="yyyyMMddHHmm".length()) {
                sampleDate += "00";
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d = sdf.parse(sampleDate);
            
            sdf = new SimpleDateFormat("yyyy年M月d日");
            return sdf.format(d);
            
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    // PHRCatchに固有なモジュールキーを生成する
    private String createLabModuleId(String sampleDate, String jmariCode, String patientId, String labCode) {
       
        try {
            sampleDate = sampleDate.replaceAll(" ", "");
            sampleDate = sampleDate.replaceAll("T", "");
            sampleDate = sampleDate.replaceAll("/", "");
            sampleDate = sampleDate.replaceAll("-", "");
            sampleDate = sampleDate.replaceAll(":", "");
            
            if (sampleDate.length()=="yyyyMMdd".length()) {
                sampleDate += "000000";
            } else if (sampleDate.length()=="yyyyMMddHHmm".length()) {
                sampleDate += "00";
            }
            
            // jmariCode-patientId-labCod-sampleDate
            StringBuilder sb = new StringBuilder();
            sb.append(jmariCode).append(patientId).append(labCode).append(sampleDate);
            String key = sb.toString();
            key = key.replaceAll("\\.", "");
            
            // SHA1
            java.security.MessageDigest d;
            d = java.security.MessageDigest.getInstance("SHA-1");
            d.reset();
            d.update(key.getBytes());
            byte[] rowBytes = d.digest();
            
            // Base64
            String base64 = Base64.getEncoder().encodeToString(rowBytes);
            return base64;
            
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    private String stringFromStarted(Date d) {
        if (d==null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(d);
    }
    
    private Date startedFromString(String str) {
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(str);
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    /**
     * 保健医療機関コードとJMARIコードを取得する。
     * @return 
     */
    private String getFacilityCodeBy1001() {
        
        Connection con = getConnection();
        
        if (con==null) {
            return null;
        }
        
        // SQL 文
        StringBuilder buf = new StringBuilder();
        buf.append("select kanritbl from tbl_syskanri where kanricd='1001'");
        String sql = buf.toString();

        PreparedStatement ps;
        
        String ret = null;
        
        try {
            ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String line = rs.getString(1);
                StringBuilder sb = new StringBuilder();
                
                // 保険医療機関コード 10桁
                sb.append(line.substring(0, 10));
                
                // JMARIコード JPN+12桁 (total 15)
                int index = line.indexOf("JPN");
                if (index>0) {
                    sb.append(line.substring(index, index+15));
                }
                
                ret = sb.toString();
            }
            rs.close();
            ps.close();
            con.close();
            con = null;

        } catch (SQLException e) {
            e.printStackTrace(System.err);
            
        } catch (Exception e) {
            // Runtime

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }

        return  ret;        
    }
     
    private Connection getConnection() {
        return ORCAConnection.getInstance().getConnection();
    }
    
    //--------------------------------------------------------------------------
    @POST
    @Path("/identityToken")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getIdentityToken(final String json) {
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, json);
        
        JsonObject jso = getJsonObject(json);
        String nonce = jso.getString("nonce");
        String user = jso.getString("user");

        String token = identityService.getIdentityToken(nonce, user);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, token);
        
        return token;
    }
    
    private JsonObject getJsonObject(String jsonStr) {
        JsonObject jsonObject;
        try (StringReader sr = new StringReader(jsonStr); JsonReader jsonReader = Json.createReader(sr)) {
            jsonObject = jsonReader.readObject();
        }
        return jsonObject;
    }
    
    public String createConversation(String fid, String pid) {
        
        return null;
    }
}
