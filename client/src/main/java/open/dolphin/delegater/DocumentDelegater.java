package open.dolphin.delegater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import open.dolphin.client.ClientContext;
import open.dolphin.converter.*;
import static open.dolphin.delegater.BusinessDelegater.CAMMA;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.infomodel.*;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Session と Document の送受信を行う Delegater クラス。
 *
 * @author Kazushi Minagawa
 *
 */
public final class DocumentDelegater extends BusinessDelegater {
    
    /**
     * 患者のカルテを取得する。
     * @param patientPK
     * @param fromDate 履歴の検索開始日
     * @return カルテ
     * @throws java.lang.Exception
     */
    public KarteBean getKarte(long patientPK, Date fromDate) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/");
        sb.append(String.valueOf(patientPK));
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate));
        String path = sb.toString();

        // GET
        KarteBean karte = getEasyJson(path, KarteBean.class);
        
        // reconnect
        List<PatientMemoModel> memoList = karte.getMemoList();
        if (memoList!=null && memoList.size()>0) {
            memoList.stream().forEach((pm) -> {
                pm.setKarteBean(karte);
            });
        }
        return karte;
    }
    
    /**
     * Documentを保存する。
     * @param karteModel KarteModel
     * @return Result Code
     * @throws java.lang.Exception
     */
    public long putKarte(DocumentModel karteModel) throws Exception {
        
        // 確定日、適合開始日、記録日、ステータスをDocInfo から DocumentModel(KarteEntry) に移す
        karteModel.toPersist();
        
        // PATH
        String path = "/karte/document";
        
        // Converter
        DocumentModelConverter conv = new DocumentModelConverter();
        conv.setModel(karteModel);

        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // POST
        String pkStr = postEasyJson(path, data, String.class);

        // PK
        return Long.parseLong(pkStr);       
    }
    
    /**
     * Documentを検索して返す。
     * @param ids
     * @return DocumentValue
     * @throws java.lang.Exception
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
        
        // GET
        DocumentList result  = getEasyJson(path, DocumentList.class);
        
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
    
    /**
     * 文書履歴を検索して返す。
     * @param spec DocumentSearchSpec 検索仕様
     * @return 
     * @throws java.lang.Exception
     * @re
     */
    public List getDocumentList(DocumentSearchSpec spec) throws Exception {
        
        switch (spec.getDocType()) {
            case IInfoModel.DOCTYPE_KARTE:
                return getKarteList(spec);
            case IInfoModel.DOCTYPE_LETTER:
                return getLetterList(spec);
            case IInfoModel.DOCTYPE_LETTER_REPLY:
                return getLetterReplyList(spec);
            case IInfoModel.DOCTYPE_LETTER_REPLY2:
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
        
        // GET
        DocInfoList result  = getEasyJson(path, DocInfoList.class);
        
        // List
        return result.getList();
    }

    private List<DocInfoModel> getLetterList(DocumentSearchSpec spec) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/odletter/list/");
        sb.append(spec.getKarteId());
        String path = sb.toString();
        
        // GET 
        LetterModuleList result  = getEasyJson(path, LetterModuleList.class);
        
        // List
        List<DocInfoModel> ret = new ArrayList<>(1);
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
                String TITLE_LETTER = ClientContext.getMyBundle(DocumentDelegater.class).getString("title.Letter");
                String TITLE_REPLY = ClientContext.getMyBundle(DocumentDelegater.class).getString("title.reply");
                String TITLE_CERTIFICATE = ClientContext.getMyBundle(DocumentDelegater.class).getString("title.certificate");
                
                sb = new StringBuilder();
                if (module.getTitle()!=null) {
                    sb.append(module.getTitle());
                    //Log.outputFuncLog(Log.LOG_LEVEL_3,"I",module.getTitle());
                } else if(module.getLetterType().equals(IInfoModel.CONSULTANT)) {
                    sb.append(TITLE_REPLY).append(module.getClientHospital());
                    //Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_REPLY,module.getClientHospital());
                } else if (module.getLetterType().equals(IInfoModel.CLIENT)) {
                    sb.append(TITLE_LETTER).append(module.getConsultantHospital());
                    //Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_LETTER,module.getClientHospital());
                } else if (module.getLetterType().equals(IInfoModel.MEDICAL_CERTIFICATE)) {
                    sb.append(TITLE_CERTIFICATE);
                    //Log.outputFuncLog(Log.LOG_LEVEL_3,"I",TITLE_CERTIFICATE);
                }
                docInfo.setTitle(sb.toString());
                docInfo.setHandleClass(module.getHandleClass());

                ret.add(docInfo);
            }
        } else {
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
     * @throws java.lang.Exception
     */
    public List<String> deleteDocument(long pk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/document/");
        sb.append(pk);
        String path = sb.toString();
        
        // DELETE
        try {
            StringList result = deleteEasy(path, StringList.class);
            return result.getList();
            
        } catch (Exception e) {
            String err = ClientContext.getMyBundle(DocumentDelegater.class).getString("error.cannotDelete");
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(err);
            throw new RuntimeException(err);
        }        
    }
    
    /**
     * 文書履歴のタイトルを変更する。
     * @param docInfo
     * @return 変更した件数
     * @throws java.lang.Exception
     */
    public int updateTitle(DocInfoModel docInfo) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/document/");
        sb.append(docInfo.getDocPk());
        String path = sb.toString();
        
        // body
        byte[] data = docInfo.getTitle().getBytes(UTF8);
        
        // PUT with text
        String cntStr = putEasyText(path, data, String.class);
        
        // Count
        return Integer.parseInt(cntStr);       
    }
    
    /**
     * Moduleを検索して返す。
     * @param spec ModuleSearchSpec 検索仕様
     * @return Module の Collection
     * @throws java.lang.Exception
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
        
        // GET
        ModuleListList result  = getEasyJson(path, ModuleListList.class);
        
        // List
        List<ModuleList> list = result.getList();
        
        // Return List
        List<List> retList = new ArrayList<>();
        
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
        return null;
    }
    
    /**
     * Imageを検索して返す。
     * @param spec ImageSearchSpec 検索仕様
     * @return Imageリストのリスト
     */
    public List<List> getImageList(ImageSearchSpec spec) {
        return null;
    }

    //---------------------------------------------------------------------------
    // 病名
    //---------------------------------------------------------------------------
    /**
     * 新規病名保存、更新病名更新、CLAIM送信を一括して実行する。
     * @param wrapper DiagnosisSendWrapper
     * @return 新規病名のPKリスト
     * @throws java.lang.Exception
     */
    public List<Long> postPutSendDiagnosis(DiagnosisSendWrapper wrapper) throws Exception {
        
        // PATH
        String path = "/karte/diagnosis/claim";
        
        // Converter
        DiagnosisSendWrapperConverter conv = new DiagnosisSendWrapperConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // POST
        String entityStr = postEasyJson(path, data, String.class);
        
        if (entityStr!=null) {
            String[] pks = entityStr.split(CAMMA);
            List<Long> list = new ArrayList<>(pks.length);
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
        
        // Wrapper
        RegisteredDiagnosisList list = new RegisteredDiagnosisList();
        list.setList(beans);
        
        // Converter
        RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
        conv.setModel(list);
        
        // JSON UTF8 byte 配列
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(list);
        
        // POST
        String entityStr = postEasyJson(path, data, String.class);
        
        // PK list
        String[] pks = entityStr.split(CAMMA);
        List<Long> retList = new ArrayList<>(pks.length);
        for (String str : pks) {
            retList.add(Long.parseLong(str));
        }
        return retList;
    }
    
    public int updateDiagnosis(List<RegisteredDiagnosisModel> beans) throws Exception {

        // PATH
        String path = "/karte/diagnosis";
        
        // Wrapper
        RegisteredDiagnosisList list = new RegisteredDiagnosisList();
        list.setList(beans);
        
        // Converter
        RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(list);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);
        
        // Count
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
        
        // DELETE resourceを指定する形
        deleteEasy(path);
        
        // Count
        return ids.size();
    }
    
    /**
     * Diagnosisを検索して返す。
     * @param karteId
     * @param fromDate
     * @param activeOnly
     * @return DiagnosisModel の Collection
     * @throws java.lang.Exception
     */
    public List<RegisteredDiagnosisModel> getDiagnosisList(long karteId, Date fromDate, boolean activeOnly) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/diagnosis/");
        sb.append(String.valueOf(karteId)).append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate)).append(CAMMA);
        sb.append(activeOnly);
        String path = sb.toString();
        
        // GET
        RegisteredDiagnosisList result = getEasyJson(path, RegisteredDiagnosisList.class);
        
        // List
        return result.getList();
    }

    
    public List<Long> addObservations(List<ObservationModel> observations) throws Exception {

        // PATH
        String path = "/karte/observations";
        
        // Wrapper
        ObservationList list = new ObservationList();
        list.setList(observations);
        
        // Converter
        ObservationListConverter conv = new ObservationListConverter();
        conv.setModel(list);
        
        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // POST
        String entityStr = postEasyJson(path, data, String.class);
        
        // PK List
        String[] pks = entityStr.split(CAMMA);
        List<Long> retList = new ArrayList<>(pks.length);
        for (String str : pks) {
            retList.add(Long.parseLong(str));
        }

        return retList;
    }
    
    public int updateObservations(List<ObservationModel> observations) throws Exception {

        // PATH
        String path = "/karte/observations/";
        
        // Wrapper
        ObservationList list = new ObservationList();
        list.setList(observations);
        
        // Converter
        ObservationListConverter conv = new ObservationListConverter();
        conv.setModel(list);
 
        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);
        
        // Count
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
        
        // DELETE
        deleteEasy(path);
        
        // Count
        return ids.size();
    }

    //-------------------------------------------------------------------------
    
    public int updatePatientMemo(PatientMemoModel pm) throws Exception {

        // PATH
        String path = "/karte/memo";
        
        // Converter
        PatientMemoModelConverter conv = new PatientMemoModelConverter();
        conv.setModel(pm);
        
        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);
        
        // Count
        return Integer.parseInt(entityStr);
    }
    
//s.oh^ 2014/04/03 サマリー対応
    public PatientFreeDocumentModel getPatientFreeDocument(String id) throws Exception {
        
        // PATH
        String path = "/karte/freedocument/" + id;
        
        // GET
        PatientFreeDocumentModel freeDoc = getEasyJson(path, PatientFreeDocumentModel.class);
        
        return freeDoc;
    }
    
    public int updatePatientFreeDocument(PatientFreeDocumentModel pfdm) throws Exception {

        // PATH
        String path = "/karte/freedocument";
        
        // Converter
        PatientFreeDocumentModelConverter conv = new PatientFreeDocumentModelConverter();
        conv.setModel(pfdm);

        // JSON
        ObjectMapper mapper = getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);
        
        // Count
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
        
        // GET
        AppoListList result = getEasyJson(path, AppoListList.class);
        
        // List
        List<AppoList> list = result.getList();
        
        // Return List
        List<List> retList = new ArrayList<>();
        
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
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public List<DocumentModel> getAllDocument(String pk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/karte/docinfo/all/");
        sb.append(pk);
        String path = sb.toString();
        
        // GET
        DocumentList result = getEasyJson(path, DocumentList.class);
        
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
        
        // GET
        AttachmentModel result = getEasyJson(path, AttachmentModel.class);

        // ImageIcon
        ImageIcon icon = ClientContext.getImageIconArias("icon_attachment");
        result.setIcon(icon);
        
        return result;
    }
//s.oh$
}