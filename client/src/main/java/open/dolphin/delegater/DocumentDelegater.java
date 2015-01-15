package open.dolphin.delegater;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.ws.rs.core.MediaType;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.converter.*;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.infomodel.*;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * Session と Document の送受信を行う Delegater クラス。
 *
 * @author Kazushi Minagawa
 *
 */
public final class DocumentDelegater extends BusinessDelegater {

    private static final String TITLE_LETTER = "紹介状:";
    private static final String TITLE_REPLY = "返書:";
    private static final String TITLE_CERTIFICATE = "診断書";
    
    /**
     * 患者のカルテを取得する。
     * @param patientPk 患者PK
     * @param fromDate 履歴の検索開始日
     * @return カルテ
     */
    public KarteBean getKarte(long patientPK, Date fromDate) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/");
        sb.append(String.valueOf(patientPK));
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate));
        String path = sb.toString();
        
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        BufferedReader br = getReader(response);

        // KarteBean
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KarteBean karte = mapper.readValue(br, KarteBean.class);
        br.close();
        
        // reconnect
        List<PatientMemoModel> memoList = karte.getMemoList();
        if (memoList!=null && memoList.size()>0) {
            for (PatientMemoModel pm : memoList) {
                pm.setKarteBean(karte);
            }
        }

        return karte;
    }
    
    /**
     * Documentを保存する。
     * @param karteModel KarteModel
     * @return Result Code
     */
    public long putKarte(DocumentModel karteModel) throws Exception {
        
        //20130228
        //karteModel.getDocInfoModel().getTitle();
        //Log.outputFuncLog(Log.LOG_LEVEL_3,"X", karteModel.getDocInfoModel().getPatientId());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I",karteModel.getDocInfoModel().getTitle());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I", karteModel.getDocInfoModel().getDocType());
        //Log.outputFuncLog(Log.LOG_LEVEL_3,"I", karteModel.getDocInfoModel().getAssignedDoctorName());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I", karteModel.getDocInfoModel().getConfirmDateTrimTime());
        //Log.outputFuncLog(Log.LOG_LEVEL_3,"X", karteModel.getDocInfoModel().getFirstConfirmDateTrimTime());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I", karteModel.getDocInfoModel().getHealthInsuranceDesc());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I", karteModel.getDocInfoModel().getDepartmentDesc());
        
        // 確定日、適合開始日、記録日、ステータスを
        // DocInfo から DocumentModel(KarteEntry) に移す
        karteModel.toPersist();
        
        // PATH
        String path = "/karte/document";
        
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        DocumentModelConverter conv = new DocumentModelConverter();
        conv.setModel(karteModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));

        // PK
        String entityStr = getString(response);
        long pk = Long.parseLong(entityStr);
        return pk;
    }
    
    public long putAndUpdatePVTState(DocumentModel karteModel, long pvtPK, int state) throws Exception {

        // 確定日、適合開始日、記録日、ステータスを
        // DocInfo から DocumentModel(KarteEntry) に移す
        karteModel.toPersist();
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/document/pvt/").append(pvtPK).append(CAMMA).append(state);
        String path = sb.toString();
        
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        DocumentModelConverter conv = new DocumentModelConverter();
        conv.setModel(karteModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));

        // PK
        String entityStr = getString(response);
        long pk = Long.parseLong(entityStr);
        return pk;
    }
    
    /**
     * Documentを検索して返す。
     * @param id DocumentID
     * @return DocumentValue
     */
    public List<DocumentModel> getDocuments(List<Long> ids) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/documents/");
        for (Long l : ids) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DocumentList result = mapper.readValue(br, DocumentList.class);
        br.close();
        
        // List
        List<DocumentModel> list = result.getList();

        for (DocumentModel doc : list) {
            Collection<ModuleModel> mc = doc.getModules();
            if (mc != null && (!mc.isEmpty())) {
                for (ModuleModel module : mc) {
                    module.setModel((InfoModel) BeanUtils.xmlDecode(module.getBeanBytes()));
                }
            }

            // JPEG byte をアイコンへ戻す
            Collection<SchemaModel> sc = doc.getSchema();
            if (sc != null && (!sc.isEmpty())) {
                for (SchemaModel schema : sc) {
                    ImageIcon icon = new ImageIcon(schema.getJpegByte());
                    schema.setIcon(icon);
                }
            }
            
            // Attachmentアイコンを設定する
            Collection<AttachmentModel> atts = doc.getAttachment();
            if (atts != null && (!atts.isEmpty())) {
                for (AttachmentModel am : atts) {
//minagawa^ Icon Server                    
                    //ImageIcon icon = ClientContext.getImageIcon(GUIConst.ICON_ATTACHMENT);
                    ImageIcon icon = ClientContext.getImageIconArias("icon_attachment");
//minagawa$                    
                    am.setIcon(icon);
                }
            }
        }
        return list;
    }
    
    /**
     * 文書履歴を検索して返す。
     * @param spec DocumentSearchSpec 検索仕様
     * @return DocInfoModel の Collection
     */
    public List getDocumentList(DocumentSearchSpec spec) throws Exception {
        
        if (spec.getDocType().equals(IInfoModel.DOCTYPE_KARTE)) {
            return getKarteList(spec);

        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER)) {
            return getLetterList(spec);

        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
            return getLetterReplyList(spec);

        } else if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY2)) {
            return getLetterReplyList(spec);
        }
        
        return null;
    }
    
    private List getKarteList(DocumentSearchSpec spec) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/docinfo/");
        sb.append(spec.getKarteId());
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(spec.getFromDate()));
        sb.append(CAMMA);
        sb.append(spec.isIncludeModifid());
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DocInfoList result = mapper.readValue(br, DocInfoList.class);
        br.close();
        
        // List
        return result.getList();
    }

    private List<DocInfoModel> getLetterList(DocumentSearchSpec spec) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/odletter/list/");
        sb.append(spec.getKarteId());
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LetterModuleList result = mapper.readValue(br, LetterModuleList.class);
        br.close();
       
        // List
        List<DocInfoModel> ret = new ArrayList<DocInfoModel>(1);
        List<LetterModule> list = result.getList();
        if (list != null && list.size() > 0) {
            for (LetterModule module : list) {
                DocInfoModel docInfo = new DocInfoModel();
                docInfo.setDocPk(module.getId());
                docInfo.setDocType(IInfoModel.DOCTYPE_LETTER);
                docInfo.setDocId(String.valueOf(module.getId()));
                docInfo.setConfirmDate(module.getConfirmed());
//minagawa^ LSC 1.4 bug fix 文書の印刷日付 2013/06/24
                //docInfo.setFirstConfirmDate(module.getConfirmed());
                docInfo.setFirstConfirmDate(module.getStarted());
//minagawa$                
                sb = new StringBuilder();
                if (module.getTitle()!=null) {
                    sb.append(module.getTitle());
                    Log.outputFuncLog(Log.LOG_LEVEL_3,"I",module.getTitle());
                } else if(module.getLetterType().equals(IInfoModel.CONSULTANT)) {
                    sb.append(TITLE_REPLY).append(module.getClientHospital());
                    Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_REPLY,module.getClientHospital());
                } else if (module.getLetterType().equals(IInfoModel.CLIENT)) {
                    sb.append(TITLE_LETTER).append(module.getConsultantHospital());
                    Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_LETTER,module.getClientHospital());
                } else if (module.getLetterType().equals(IInfoModel.MEDICAL_CERTIFICATE)) {
                    sb.append(TITLE_CERTIFICATE);
                    Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_CERTIFICATE);
                }
                docInfo.setTitle(sb.toString());
                docInfo.setHandleClass(module.getHandleClass());

                ret.add(docInfo);
            }
        } else {
            Log.outputFuncLog(Log.LOG_LEVEL_0,"E","parse no results");
            System.err.println("parse no results");
        }

        return ret;
    }
    
    private List<DocInfoModel> getLetterReplyList(DocumentSearchSpec spec) throws Exception {
        return null;
    }
    
    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの prmary key
     * @return 削除件数
     */
    public List<String> deleteDocument(long pk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/document/");
        sb.append(pk);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        if (response.getStatus()/100 != 2) {
            String err = "文書が他から参照されている等の理由により、削除できませんでした。";
            Log.outputFuncLog(Log.LOG_LEVEL_0,"E",err);
            
            ClientContext.getDelegaterLogger().warn(err);
            throw new RuntimeException(err);
        }
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringList result = mapper.readValue(br, StringList.class);
        br.close();

        return result.getList();
    }
    
    /**
     * 文書履歴のタイトルを変更する。
     * @param pk Document の pk
     * @return 変更した件数
     */
    public int updateTitle(DocInfoModel docInfo) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/document/");
        sb.append(docInfo.getDocPk());
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // body
        byte[] data = docInfo.getTitle().getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.TEXT_PLAIN, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.TEXT_PLAIN,docInfo.getTitle());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
    
    
    /**
     * Moduleを検索して返す。
     * @param spec ModuleSearchSpec 検索仕様
     * @return Module の Collection
     */
    public List<List> getModuleList(ModuleSearchSpec spec) throws Exception {
        
        // PATH
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_REST);
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/modules/");
        sb.append(String.valueOf(spec.getKarteId()));
        sb.append(CAMMA);
        sb.append(spec.getEntity());

        Date[] froms = spec.getFromDate();
        Date[] tos = spec.getToDate();

        int len = froms.length;

        for (int i = 0; i < len; i++) {
            sb.append(CAMMA);
            sb.append(sdf.format(froms[i]));
            sb.append(CAMMA);
            sb.append(sdf.format(tos[i]));
        }

        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        if (false) {
            String test = getString(response);
            System.err.println(test);
        }
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ModuleListList result = mapper.readValue(br, ModuleListList.class);
        br.close();
        
        // List
        List<ModuleList> list = result.getList();
        
        if (false) {
            System.err.println(list.size());
        }
        
        // Return List
        List<List> retList = new ArrayList<List>();
        
        // List
        for (ModuleList mlist : list) {
            List<ModuleModel> models = mlist.getList();
            if (models==null) {
                retList.add(new ArrayList());
                continue;
            }
            for (ModuleModel module : models) {
                module.setModel((InfoModel)BeanUtils.xmlDecode(module.getBeanBytes()));
            }
            retList.add(models);
        }
        
        return retList;
    }
    
    /**
     * イメージを取得する。
     * @param id 画像のId
     * @return SchemaModel
     */
    public SchemaModel getImage(long id) {

        /*StringBuilder sb = new StringBuilder();
        sb.append("karte/image/");
        sb.append(id);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser parser = new PlistParser();
        SchemaModel model = (SchemaModel) parser.parse(entityStr);
        byte[] bytes = model.getJpegByte();
        ImageIcon icon = new ImageIcon(bytes);
        if (icon != null) {
            model.setIcon(icon);
        }
        return model;*/
        return null;
    }

    public List<SchemaModel> getS3Images(String facilityId, int first, int max) {

        /*StringBuilder sb = new StringBuilder();
        sb.append("karte/s3iamges/");
        sb.append(facilityId).append(",");
        sb.append(String.valueOf(first)).append(",");
        sb.append(String.valueOf(max));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser parser = new PlistParser();
        List<SchemaModel> list = (List<SchemaModel>) parser.parse(entityStr);
        return list;*/
        return null;
    }

    public void deleteS3Image(long pk) {
        /*
        StringBuilder sb = new StringBuilder();
        sb.append("karte/s3iamges/");
        sb.append(String.valueOf(pk));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }*/
    }
    
    /**
     * Imageを検索して返す。
     * @param spec ImageSearchSpec 検索仕様
     * @return Imageリストのリスト
     */
    public List<List> getImageList(ImageSearchSpec spec) {
        
//        Logger logger = ClientContext.getLogger("boot");
//        logger.debug("search code = " + spec.getCode());
//        logger.debug("karte id = " + spec.getKarteId());
//        Date[] from = spec.getFromDate();
//        Date[] to = spec.getToDate();
//        for (int i = 0; i < from.length; i++) {
//            logger.debug(from[i] + " ~ " + to[i]);
//        }
        
//        List<List> ret= new ArrayList<List>(3);
//
//        try {
//            // 検索結果
//            List result = getService().getImages(spec);
//            //logger.debug("got result, count = " + result.size());
//
//            //System.out.println("got image list");
//
//            for (Iterator iter = result.iterator(); iter.hasNext(); ) {
//
//                // 抽出期間毎のリスト
//                List periodList = (List)iter.next();
//
//                // ImageEntry 用のリスト
//                List<ImageEntry> el = new ArrayList<ImageEntry>();
//
//                // 抽出期間をイテレートする
//                for (Iterator iter2 = periodList.iterator(); iter2.hasNext(); ) {
//                    // シェーマモデルをエントリに変換しリストに加える
//                    SchemaModel model = (SchemaModel)iter2.next();
//                    ImageEntry entry = getImageEntry(model, spec.getIconSize());
//                    el.add(entry);
//                }
//
//                // リターンリストへ追加する
//                ret.add(el);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//
//        return ret;

        return null;
    }

    //---------------------------------------------------------------------------
    // 病名
    //---------------------------------------------------------------------------
    /**
     * 新規病名保存、更新病名更新、CLAIM送信を一括して実行する。
     * @param wrapper DiagnosisSendWrapper
     * @return 新規病名のPKリスト
     */
    public List<Long> postPutSendDiagnosis(DiagnosisSendWrapper wrapper) throws Exception {
        
        // PATH
        String path = "/karte/diagnosis/claim";
        
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        DiagnosisSendWrapperConverter conv = new DiagnosisSendWrapperConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);   // UTF-8
        ClientResponse<String> response = request.post(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // PK list
        String entityStr = getString(response);
        if (entityStr!=null) {
            String[] pks = entityStr.split(CAMMA);
            List<Long> list = new ArrayList<Long>(pks.length);
            for (String str : pks) {
                list.add(Long.parseLong(str));
            }
            return list;
        }
        return null;
    }
    
    public List<Long> putDiagnosis(List<RegisteredDiagnosisModel> beans) throws Exception {

        // PATH
        String path = "/karte/diagnosis";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Wrapper
        RegisteredDiagnosisList list = new RegisteredDiagnosisList();
        list.setList(beans);
        
        // Converter
        RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // PK list
        String entityStr = getString(response);
        String[] pks = entityStr.split(CAMMA);
        List<Long> retList = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            retList.add(Long.parseLong(str));
        }
        return retList;
    }
    
    public int updateDiagnosis(List<RegisteredDiagnosisModel> beans) throws Exception {

        // PATH
        String path = "/karte/diagnosis";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Wrapper
        RegisteredDiagnosisList list = new RegisteredDiagnosisList();
        list.setList(beans);
        
        // Converter
        RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
    
    public int removeDiagnosis(List<Long> ids) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/diagnosis/");
        for (Long l : ids) {
            sb.append(l);
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Check
        checkStatus(response);

        // Count
        return ids.size();
    }
    
    /**
     * Diagnosisを検索して返す。
     * @param spec DiagnosisSearchSpec 検索仕様
     * @return DiagnosisModel の Collection
     */
    public List<RegisteredDiagnosisModel> getDiagnosisList(long karteId, Date fromDate, boolean activeOnly) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/diagnosis/");
        sb.append(String.valueOf(karteId)).append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate)).append(CAMMA);
        sb.append(activeOnly);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RegisteredDiagnosisList result = mapper.readValue(br, RegisteredDiagnosisList.class);
        br.close();

        // List
        return result.getList();
    }

    
    public List<Long> addObservations(List<ObservationModel> observations) throws Exception {

        // PATH
        String path = "/karte/observations";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Wrapper
        ObservationList list = new ObservationList();
        list.setList(observations);
        
        // Converter
        ObservationListConverter conv = new ObservationListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // PK list
        String entityStr = getString(response);
        String[] pks = entityStr.split(CAMMA);
        List<Long> retList = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            retList.add(Long.parseLong(str));
        }

        return retList;
    }
    
    public int updateObservations(List<ObservationModel> observations) throws Exception {

        // PATH
        String path = "/karte/observations/";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Wrapper
        ObservationList list = new ObservationList();
        list.setList(observations);
        
        // Converter
        ObservationListConverter conv = new ObservationListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
    
    public int removeObservations(List<Long> ids) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/observations/");
        for (Long l : ids) {
            sb.append(l);
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        // Check
        checkStatus(response);

        // Count
        return ids.size();
    }

    //-------------------------------------------------------------------------
    
    public int updatePatientMemo(PatientMemoModel pm) throws Exception {

        // PATH
        String path = "/karte/memo";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        PatientMemoModelConverter conv = new PatientMemoModelConverter();
        conv.setModel(pm);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
    
//s.oh^ 2014/04/03 サマリー対応
    public PatientFreeDocumentModel getPatientFreeDocument(String id) throws Exception {
        
        // PATH
        String path = "/karte/freedocument/" + id;
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientFreeDocumentModel freeDoc = mapper.readValue(br, PatientFreeDocumentModel.class);
        br.close();
        
        return freeDoc;
    }
    
    public int updatePatientFreeDocument(PatientFreeDocumentModel pfdm) throws Exception {

        // PATH
        String path = "/karte/freedocument";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // Converter
        PatientFreeDocumentModelConverter conv = new PatientFreeDocumentModelConverter();
        conv.setModel(pfdm);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
//s.oh$

    //-------------------------------------------------------------------------
    
    public List getAppoinmentList(ModuleSearchSpec spec) throws Exception {

        // PATH
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_REST);

        StringBuilder sb = new StringBuilder();
        sb.append("/karte/appo/");
        sb.append(String.valueOf(spec.getKarteId()));

        Date[] froms = spec.getFromDate();
        Date[] tos = spec.getToDate();

        int len = froms.length;

        for (int i = 0; i < len; i++) {
            sb.append(CAMMA);
            sb.append(sdf.format(froms[i]));
            sb.append(CAMMA);
            sb.append(sdf.format(tos[i]));
        }

        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        if (false) {
            String test = getString(response);
            System.err.println(test);
        }
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AppoListList result = mapper.readValue(br, AppoListList.class);
        br.close();
        
        // List
        List<AppoList> list = result.getList();
        
        if (false) {
            System.err.println(list.size());
        }
        
        // Return List
        List<List> retList = new ArrayList<List>();
        
        // List
        for (AppoList mlist : list) {
            List<AppointmentModel> models = mlist.getList();
            if (models==null) {
                retList.add(new ArrayList());
                continue;
            }
            retList.add(models);
        }
        
        return retList;
    }


    public void updatePVTState(long pvtPK, int state) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/pvt/");
        sb.append(pvtPK);
        sb.append(CAMMA);
        sb.append(state);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // PUT
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        
        // Check
        checkStatus(response);
    }
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public List<DocumentModel> getAllDocument(String pk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/docinfo/all/");
        sb.append(pk);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DocumentList result = mapper.readValue(br, DocumentList.class);
        br.close();
        
        // List
        List<DocumentModel> list = result.getList();

        for (DocumentModel doc : list) {
            Collection<ModuleModel> mc = doc.getModules();
            if (mc != null && (!mc.isEmpty())) {
                for (ModuleModel module : mc) {
                    module.setModel((InfoModel) BeanUtils.xmlDecode(module.getBeanBytes()));
                }
            }

            // JPEG byte をアイコンへ戻す
            Collection<SchemaModel> sc = doc.getSchema();
            if (sc != null && (!sc.isEmpty())) {
                for (SchemaModel schema : sc) {
                    ImageIcon icon = new ImageIcon(schema.getJpegByte());
                    schema.setIcon(icon);
                }
            }
            
            // Attachmentアイコンを設定する
            Collection<AttachmentModel> atts = doc.getAttachment();
            if (atts != null && (!atts.isEmpty())) {
                for (AttachmentModel am : atts) {
                    ImageIcon icon = ClientContext.getImageIconArias("icon_attachment");
                    am.setIcon(icon);
                }
            }
        }
        return list;
    }
//s.oh$
    
//s.oh^ 2014/08/20 添付ファイルの別読
    public AttachmentModel getAttachment(long id) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/attachment/");
        sb.append(id);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AttachmentModel result = mapper.readValue(br, AttachmentModel.class);
        br.close();
        
        ImageIcon icon = ClientContext.getImageIconArias("icon_attachment");
        result.setIcon(icon);
        
        return result;
    }
//s.oh$
}