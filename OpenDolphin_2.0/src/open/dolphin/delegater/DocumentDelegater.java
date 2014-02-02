package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.ws.rs.core.MediaType;

import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.util.BeanUtils;

/**
 * Session と Document の送受信を行う Delegater クラス。
 *
 * @author Kazushi Minagawa
 *
 */
public class  DocumentDelegater extends BusinessDelegater {

    private static final String TITLE_LETTER = "紹介状:";
    private static final String TITLE_REPLY = "返書:";
    private static final String TITLE_CERTIFICATE = "診断書";
    
    /**
     * 患者のカルテを取得する。
     * @param patientPk 患者PK
     * @param fromDate 履歴の検索開始日
     * @return カルテ
     */
    public KarteBean getKarte(long patientPK, Date fromDate) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/");
        sb.append(String.valueOf(patientPK));
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        KarteBean karte = (KarteBean) con.parse(entityStr);

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
    public long putKarte(DocumentModel karteModel) {

        // 確定日、適合開始日、記録日、ステータスを
        // DocInfo から DocumentModel(KarteEntry) に移す
        karteModel.toPersist();

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(karteModel);

        String path = "karte/document";
        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String pkStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, pkStr);
        }

        return Long.parseLong(pkStr);
    }

    public long putAndUpdatePVTState(DocumentModel karteModel, long pvtPK, int state) {

        // 確定日、適合開始日、記録日、ステータスを
        // DocInfo から DocumentModel(KarteEntry) に移す
        karteModel.toPersist();

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(karteModel);

        StringBuilder sb = new StringBuilder();
        sb.append("karte/document/pvt/").append(pvtPK).append(CAMMA).append(state);
        String path = sb.toString();
        
        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String pkStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, pkStr);
        }

        return Long.parseLong(pkStr);
    }
    
    /**
     * Documentを検索して返す。
     * @param id DocumentID
     * @return DocumentValue
     */
    public List<DocumentModel> getDocuments(List<Long> ids) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/documents/");
        for (Long l : ids) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<DocumentModel> list = (List<DocumentModel>) con.parse(entityStr);
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
        }
        return list;
    }
    
    /**
     * 文書履歴を検索して返す。
     * @param spec DocumentSearchSpec 検索仕様
     * @return DocInfoModel の Collection
     */
    public List getDocumentList(DocumentSearchSpec spec) {
        
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
    
    private List getKarteList(DocumentSearchSpec spec) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/docinfo/");
        sb.append(spec.getKarteId());
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(spec.getFromDate()));
        sb.append(CAMMA);
        sb.append(spec.isIncludeModifid());
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<DocInfoModel> list = (List<DocInfoModel>) con.parse(entityStr);
        return list;
    }

    private List<DocInfoModel> getLetterList(DocumentSearchSpec spec) {

        List<DocInfoModel> ret = new ArrayList<DocInfoModel>(1);

        StringBuilder sb = new StringBuilder();
        sb.append("odletter/list/");
        sb.append(spec.getKarteId());
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<LetterModule> list = (List<LetterModule>) con.parse(entityStr);
        if (list != null && list.size() > 0) {
            for (LetterModule module : list) {
                DocInfoModel docInfo = new DocInfoModel();
                docInfo.setDocPk(module.getId());
                docInfo.setDocType(IInfoModel.DOCTYPE_LETTER);
                docInfo.setDocId(String.valueOf(module.getId()));
                docInfo.setConfirmDate(module.getConfirmed());
                docInfo.setFirstConfirmDate(module.getConfirmed());
                sb = new StringBuilder();
                if (module.getTitle()!=null) {
                    sb.append(module.getTitle());
                } else if(module.getLetterType().equals(IInfoModel.CONSULTANT)) {
                    sb.append(TITLE_REPLY).append(module.getClientHospital());
                } else if (module.getLetterType().equals(IInfoModel.CLIENT)) {
                    sb.append(TITLE_LETTER).append(module.getConsultantHospital());
                } else if (module.getLetterType().equals(IInfoModel.MEDICAL_CERTIFICATE)) {
                    sb.append(TITLE_CERTIFICATE);
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
    
    private List<DocInfoModel> getLetterReplyList(DocumentSearchSpec spec) {

        List<DocInfoModel> ret = new ArrayList<DocInfoModel>(1);

        StringBuilder sb = new StringBuilder();
        sb.append("karte/letter/list/");
        sb.append(spec.getKarteId());
        sb.append(CAMMA);
        sb.append("TOUTOU_REPLY");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<TouTouReply> list = (List<TouTouReply>) con.parse(entityStr);
        if (list != null && list.size() > 0) {
            for (TouTouReply letter : list) {
                DocInfoModel docInfo = new DocInfoModel();
                docInfo.setDocPk(letter.getId());
                if (spec.getDocType().equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
                    docInfo.setDocType(IInfoModel.DOCTYPE_LETTER_REPLY);
                } else {
                    docInfo.setDocType(IInfoModel.DOCTYPE_LETTER_REPLY2);
                }
                docInfo.setDocId(String.valueOf(letter.getId()));
                docInfo.setConfirmDate(letter.getConfirmed());
                docInfo.setFirstConfirmDate(letter.getConfirmed());
                docInfo.setTitle(letter.getConsultantHospital());

                //-----------------------------------------------
                //docInfo.setHandleClass(letter.getHandleClass());
                //-----------------------------------------------

                ret.add(docInfo);
            }
        }
        return ret;
    }
    
    public LetterModel getLetter(long letterPk) {

        LetterModel ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append("karte/letter/");
        sb.append(letterPk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        TouTouLetter result = (TouTouLetter) con.parse(entityStr);
        byte[] bytes = result.getBeanBytes();
        ret = (LetterModel) BeanUtils.xmlDecode(bytes);
        ret.setId(result.getId());
        ret.setBeanBytes(null);
        return ret;
    }
    
    public LetterModel getLetterReply(long letterPk) {

        LetterModel ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append("karte/reply/");
        sb.append(letterPk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        TouTouReply result = (TouTouReply) con.parse(entityStr);
        byte[] bytes = result.getBeanBytes();
        ret = (LetterModel) BeanUtils.xmlDecode(bytes);
        ret.setId(result.getId());
        ret.setBeanBytes(null);
        return ret;
    }
    
    /**
     * ドキュメントを論理削除する。
     * @param pk 論理削除するドキュメントの prmary key
     * @return 削除件数
     */
    public int deleteDocument(long pk) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/document/");
        sb.append(pk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);

        int status = response.getStatus();

        // TODO
        return 1;
    }
    
    /**
     * 文書履歴のタイトルを変更する。
     * @param pk Document の pk
     * @return 変更した件数
     */
    public int updateTitle(DocInfoModel docInfo) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/document/");
        sb.append(docInfo.getDocPk());
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .type(MediaType.TEXT_PLAIN)
                .put(ClientResponse.class, docInfo.getTitle());

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
    }
    
    
    /**
     * Moduleを検索して返す。
     * @param spec ModuleSearchSpec 検索仕様
     * @return Module の Collection
     */
    public List<List> getModuleList(ModuleSearchSpec spec) {
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_REST);

        StringBuilder sb = new StringBuilder();
        sb.append("karte/modules/");
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

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<List> ret = (List<List>) con.parse(entityStr);
        for (List list : ret) {
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                ModuleModel module = (ModuleModel)iter.next();
                module.setModel((InfoModel)BeanUtils.xmlDecode(module.getBeanBytes()));
            }
        }
        return ret;
    }
    
    /**
     * イメージを取得する。
     * @param id 画像のId
     * @return SchemaModel
     */
    public SchemaModel getImage(long id) {

        StringBuilder sb = new StringBuilder();
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
        return model;
    }

    public List<SchemaModel> getS3Images(String facilityId, int first, int max) {

        StringBuilder sb = new StringBuilder();
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
        return list;
    }

    public void deleteS3Image(long pk) {
        StringBuilder sb = new StringBuilder();
        sb.append("karte/s3iamges/");
        sb.append(String.valueOf(pk));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }
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
    
    public List<Long> putDiagnosis(List<RegisteredDiagnosisModel> beans) {

        String path = "karte/diagnosis/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(beans);

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        String[] pks = entityStr.split(CAMMA);
        List<Long> list = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            list.add(Long.parseLong(str));
        }
        return list;
    }
    
    public int updateDiagnosis(List<RegisteredDiagnosisModel> beans) {

        String path = "karte/diagnosis/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(beans);

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
    }
    
    public int removeDiagnosis(List<Long> ids) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/diagnosis/");
        for (Long l : ids) {
            sb.append(l);
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);

        ClientResponse response = getResource(path)
                .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }

        return ids.size();
    }
    
    /**
     * Diagnosisを検索して返す。
     * @param spec DiagnosisSearchSpec 検索仕様
     * @return DiagnosisModel の Collection
     */
    public List<RegisteredDiagnosisModel> getDiagnosisList(long karteId, Date fromDate) {

        StringBuilder sb = new StringBuilder();
        sb.append("karte/diagnosis/");
        sb.append(String.valueOf(karteId));
        sb.append(CAMMA);
        sb.append(new SimpleDateFormat(DATE_TIME_FORMAT_REST).format(fromDate));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<RegisteredDiagnosisModel> list = (List<RegisteredDiagnosisModel>) con.parse(entityStr);
        return list;
    }

    
    public List<Long> addObservations(List<ObservationModel> observations) {

        String path = "/karte/observations/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(observations);

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        String[] pks = entityStr.split(CAMMA);
        List<Long> list = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            list.add(Long.parseLong(str));
        }

        return list;
    }
    
    public int updateObservations(List<ObservationModel> observations) {

        String path = "/karte/observations/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(observations);

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
    }
    
    public int removeObservations(List<Long> ids) {

        StringBuilder sb = new StringBuilder();
        sb.append("/karte/observations/");
        for (Long l : ids) {
            sb.append(l);
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);

        ClientResponse response = getResource(path)
                .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }

        return ids.size();
    }

    //-------------------------------------------------------------------------
    
    public int updatePatientMemo(PatientMemoModel pm) {

        String path = "/karte/memo/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(pm);

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }
        return Integer.parseInt(entityStr);
    }

    //-------------------------------------------------------------------------
    
    public List getAppoinmentList(ModuleSearchSpec spec) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_REST);

        StringBuilder sb = new StringBuilder();
        sb.append("karte/appo/");
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

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<List> ret = (List<List>) con.parse(entityStr);
        return ret;
    }


    public void updatePVTState(long pvtPK, int state) {

        StringBuilder sb = new StringBuilder();
        sb.append("pvt/");
        sb.append(pvtPK);
        sb.append(CAMMA);
        sb.append(state);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.TEXT_PLAIN)
                    .put(ClientResponse.class);

        int status = response.getStatus();

        if (DEBUG) {
            debug(status, "put response");
        }
    }
}