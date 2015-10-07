package open.dolphin.adm20.session;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.CarePlanModel;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LastDateCount30;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.ProgressCourse;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.msg.ClaimSender;
import open.dolphin.msg.DiagnosisSender;
import open.dolphin.adm20.converter.IOSHelper;
//import org.jboss.logging.Logger;

/**
 *
 * @author kazushi Minagawa
 */
@Named
@Stateless
public class ADM20_AdmissionServiceBean {
    
    // parameters
    private static final String PATIENT_PK = "patientPk";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String ID = "id";
    private static final String ENTITY = "entity";
    private static final String FID = "fid";
    private static final String PID = "pid";
    
    // KarteBean
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    
    // 入院カルテ
    private static final String NATIVE_QUERY_ADMISSION_KARTE = "select id from d_document where karte_id=? and started=? and admFlag='A' and status='F'";
    
    // doc ID
    private static final String NATIVE_QUERY_DOC_ID_BY_KARTE_ID = "select id from d_document where karte_id=? and (status='F' or status='T') order by started desc";
    
    // 健康保険
    private static final String QUERY_INSURANCE_BY_PATIENT_ID = "from HealthInsuranceModel h where h.patient.id=:id";
    
    // 文書
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    //private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";
    
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    //private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F' order by m.started desc";
    
    
    private static final String QUERY_MODULE_BY_DOC_ID = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOC_ID = "from SchemaModel i where i.document.id=:id";
    //private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    
    @PersistenceContext
    private EntityManager em;
    
    public KarteBean getKarte(long ptPK) {
        // Karte
        KarteBean karte;
        karte = (KarteBean)
                em.createQuery(QUERY_KARTE)
                        .setParameter("patientPk", ptPK)
                        .getSingleResult();
        return karte;
    }
    
    public List<ModuleModel> getLastModule(long patientPk, String entity) {
        // "select max(m.started) from d_document m where m.karte_id=:karteId and m.docType=:docType and (m.status = 'F' or m.status = 'T')"
        // from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F' order by m.started desc
        // "from ModuleModel m where m.karte.id=:karteId and m.started=:started and (d.status='F' or d.status='T')"
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", patientPk)
                          .getSingleResult();
        
        Date lastDocDate = (Date)
                em.createNativeQuery("select max(m.started) from d_module m where m.karte_id=:karteId and m.entity=:entity and (m.status = 'F' or m.status = 'T')")
                        .setParameter("karteId", karte.getId())
                        .setParameter("entity", entity)
                        .getSingleResult();
        
        List<ModuleModel> list2 = (List<ModuleModel>)em.createQuery("from ModuleModel m where m.karte.id=:karteId and m.started=:started and m.moduleInfo.entity=:entity and (m.status='F' or m.status='T')")
                                                     .setParameter("karteId", karte.getId())
                                                     .setParameter("started", lastDocDate)
                                                     .setParameter("entity", entity)
                                                     .getResultList();
        return list2;
    }
    
    //------------------------------------------------------------------------
    // 相互作用 関連
    //------------------------------------------------------------------------ 
    public List<ModuleModel> collectModules(long patientPk, Date fromDate, Date toDate, List<String> entities) {
        
        // 指定したentityのModuleModelを返す
        List<ModuleModel> ret;
        
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", patientPk)
                          .getSingleResult();
        
        if (entities!=null && entities.size()>0) {
            final String sql = "from ModuleModel m where m.karte.id = :karteId " +
                    "and m.started between :fromDate and :toDate and m.status='F' " +
                    "and m.moduleInfo.entity in (:entities)";
            ret = em.createQuery(sql)
                    .setParameter("karteId", karte.getId())
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .setParameter("entities", entities)
                    .getResultList();
        } else {
            final String sql = "from ModuleModel m where m.karte.id = :karteId " +
                    "and m.started between :fromDate and :toDate and m.status='F' ";
            ret = em.createQuery(sql)
                    .setParameter("karteId", karte.getId())
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        }
        
        return ret;
    }
    
    public List<CarePlanModel> getCarePlans(long ptPK) {
        
        // Karte
        KarteBean karte;
        karte = (KarteBean)
                em.createQuery(QUERY_KARTE)
                        .setParameter("patientPk", ptPK)
                        .getSingleResult();
        
        List<CarePlanModel> ret;
        ret = em.createQuery("from CarePlanModel c where c.karteId=:karteId and c.status=:status order by c.startDate")
                .setParameter("karteId", karte.getId())
                .setParameter("status", "A")
                .getResultList();
        
       return ret;
    }
    
    public Long addCarePlan(CarePlanModel model) {
        em.persist(model);
        return model.getId();
    }
    
    public int updateCarePlan(CarePlanModel model) {
        em.merge(model);
        return 1;
    }
    
    public int deleteCarePlan(CarePlanModel model) {
        CarePlanModel delete = em.find(CarePlanModel.class, model.getId());
        em.remove(delete);
        return 1;
    }
    
    public Collection<Long> getDocIdList(long ptPK) {
        
        // Karte
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", ptPK)
                          .getSingleResult();
        
        // 当日のカルテはあるのでそのまま文書履歴をかえす
        String sql = NATIVE_QUERY_DOC_ID_BY_KARTE_ID;
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, karte.getId());
        List<BigInteger> list = query.getResultList();
        Iterator<BigInteger> iter = list.iterator();

        Collection<Long> result = new ArrayList(list.size());

        while (iter.hasNext()) {
            long id = iter.next().longValue();
            result.add(id);
        }

        return result;
    }
    
    public Collection<Long> getDocIdList(long ptPK, Date startDate) {
        
        // Karte
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", ptPK)
                          .getSingleResult();
        
        // 文書リストを検索する
        String sql = NATIVE_QUERY_DOC_ID_BY_KARTE_ID;
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, karte.getId());
        List<BigInteger> list = query.getResultList();

        Collection<Long> result = new ArrayList(list.size());
        for (BigInteger bi : list) {
            long id = bi.longValue();
            result.add(id);
        }
        
        // 当日の入院カルテはあるか
        boolean hasAdmissionKarte;
        try {
            sql = NATIVE_QUERY_ADMISSION_KARTE;
            query = em.createNativeQuery(sql);
            query.setParameter(1, karte.getId());
            query.setParameter(2, startDate);
            BigInteger id = (BigInteger)query.getSingleResult();
            hasAdmissionKarte = (id.longValue()!=0L);
        } catch (Exception e) {
            hasAdmissionKarte = false;
        }
        
        //Logger.getLogger("open.dolphin").info("当日の入院カルテ="+hasAdmissionKarte);
        
        // 当日のカルテはあるのでそのまま文書履歴をかえす
        if (hasAdmissionKarte) {
            return result;
        }
        
        // 当日の入院カルテを作成する
        
        // CarePlanを検索する
        List<CarePlanModel> carePlanList;
                carePlanList = em.createQuery("from CarePlanModel c where c.karteId=:karteId and c.startDate<=:startDate and c.endDate>=:startDate")
                        .setParameter("karteId", karte.getId())
                        .setParameter("startDate", startDate)
                        .getResultList();
                
        //Logger.getLogger("open.dolphin").info("Careプランの数="+carePlanList.size());
        
        // 当日の入院カルテもケアプランもなし
        // ここへは来ないように患者情報属性で制御する  execption?
        if (carePlanList.isEmpty()) {
            return result;
        }
                
        // Care プランがあるので Documentを作成する
        DocumentModel schedule = new DocumentModel();
        
        // Care プランをモジュールに変換しDocumentへ加える
        for (CarePlanModel cm : carePlanList) {
            // CarePlan to ModuleModel
            ModuleModel module = cm.toModleModel();
            module.setBeanBytes(IOSHelper.toXMLBytes(module.getModel()));
            schedule.addModule(module);
        }
        
        //------------------------------------------------------------------------------        
        // Creator情報が必要
        CarePlanModel cm = carePlanList.get(0);
        String userId = cm.getUserId();
        UserModel user = (UserModel)em.createQuery("from UserModel u where u.userId=:userId").setParameter("userId", userId).getSingleResult();
        
        // 患者の診療科、保険情報...
        PatientModel patient = em.find(PatientModel.class, ptPK);
        // 患者の健康保険を取得する
        List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
        .setParameter("id", patient.getId()).getResultList();
        patient.setHealthInsurances(insurances);

        // 受け付けた保険をデコードする
        PVTHealthInsuranceModel pvtHealthInsurance=null;
        for (HealthInsuranceModel m : insurances) {
            XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(m.getBeanBytes())));
            pvtHealthInsurance = (PVTHealthInsuranceModel)d.readObject();
            break;
        }
        //--------------------------------------------------------------------------------
        
        Date now = new Date();
        
        // SOA
        StringBuilder sb = new StringBuilder();
        sb.append("<section>");
        sb.append("<paragraph>");
        sb.append("<content><text></text></content>");
        sb.append("</paragraph>");
        sb.append("</section>");
        ProgressCourse soaProgress = new ProgressCourse();
        soaProgress.setFreeText(sb.toString());
        ModuleModel soaSpecModule = new ModuleModel();
        soaSpecModule.setBeanBytes(IOSHelper.toXMLBytes(soaProgress));
        soaSpecModule.setConfirmed(now);
        soaSpecModule.setStarted(startDate);
        soaSpecModule.setRecorded(now);
        soaSpecModule.setStatus("A");
        soaSpecModule.setUserModel(user);
        soaSpecModule.setKarteBean(karte);
        soaSpecModule.getModuleInfoBean().setStampName("progressCourse");
        soaSpecModule.getModuleInfoBean().setStampRole("soaSpec");
        soaSpecModule.getModuleInfoBean().setEntity("progressCourse");
        soaSpecModule.getModuleInfoBean().setStampNumber(0);
        soaSpecModule.setDocumentModel(schedule);
        schedule.addModule(soaSpecModule);
        // P
        int number = 0;
        sb = new StringBuilder();
        sb.append("<section>");
        for(int i = 0; i < carePlanList.size(); i++) {
            if(i != 0)  {
                sb.append("<paragraph>");
                sb.append("<content><text>\n</text></content>");
                sb.append("</paragraph>");
            }
            sb.append("<paragraph>");
            sb.append("<component component=").append("\"").append(i).append("\"").append(" name=\"stampHolder\">").append("</component>");
            sb.append("<content><text></text></content>");
            sb.append("<content><text>\n</text></content>");
            sb.append("</paragraph>");
        }
        sb.append("</section>");
        ProgressCourse pProgress = new ProgressCourse();
        pProgress.setFreeText(sb.toString());
        ModuleModel pSpecModule = new ModuleModel();
        pSpecModule.setBeanBytes(IOSHelper.toXMLBytes(pProgress));
        pSpecModule.setConfirmed(startDate);
        pSpecModule.setStarted(now);
        pSpecModule.setRecorded(now);
        pSpecModule.setStatus("A");
        pSpecModule.setUserModel(user);
        pSpecModule.setKarteBean(karte);
        pSpecModule.getModuleInfoBean().setStampName("progressCourse");
        pSpecModule.getModuleInfoBean().setStampRole("pSpec");
        pSpecModule.getModuleInfoBean().setEntity("progressCourse");
        pSpecModule.getModuleInfoBean().setStampNumber(number++);
        pSpecModule.setDocumentModel(schedule);
        schedule.addModule(pSpecModule);

        //-----------------------------------------------------------------------
        // 必須項目 DocInfoを設定する
        //-----------------------------------------------------------------------
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        schedule.getDocInfoModel().setDocId(uuid);
        schedule.getDocInfoModel().setDocType(IInfoModel.DOCTYPE_KARTE);
        schedule.getDocInfoModel().setTitle("入院予定カルテ");
        schedule.getDocInfoModel().setPurpose(IInfoModel.PURPOSE_RECORD);
        //-----------------------------------------------------------------------
        // 入院カルテフラグ
        schedule.getDocInfoModel().setAdmFlag("A");
        //-----------------------------------------------------------------------
        
        sb = new StringBuilder();
        sb.append(user.getDepartmentModel().getDepartmentDesc()).append(",");   // 診療科名
        sb.append(user.getDepartmentModel().getDepartment()).append(",");       // 診療科コード : 受けと不一致、受信？
        sb.append(user.getCommonName()).append(",");                            // 担当医名
        //if (pvt.getDoctorId()!=null) {
            //sb.append(pvt.getDoctorId()).append(",");                         // 担当医コード: 受付でIDがある場合
        //} else 
        if (user.getOrcaId()!=null) {
            sb.append(user.getOrcaId()).append(",");                            // 担当医コード: ORCA ID がある場合
        } else {
            sb.append(user.getUserId()).append(",");                            // 担当医コード: ログインユーザーID
        }
        sb.append("JPN000000000000");                                           // JMARI
        schedule.getDocInfoModel().setDepartmentDesc(sb.toString());            // 上記をカンマ区切りで docInfo.departmentDesc へ設定
        schedule.getDocInfoModel().setDepartment(user.getDepartmentModel().getDepartment());    // 診療科コード 01 内科等
        
        // 施設名、ライセンス、患者情報
        schedule.getDocInfoModel().setFacilityName(user.getFacilityModel().getFacilityName());
        schedule.getDocInfoModel().setCreaterLisence(user.getLicenseModel().getLicense());
        schedule.getDocInfoModel().setPatientId(patient.getPatientId());
        schedule.getDocInfoModel().setPatientName(patient.getFullName());
        schedule.getDocInfoModel().setPatientGender(patient.getGenderDesc());

        // 健康保険を設定する-新規カルテダイアログで選択された保険をセットしている
        schedule.getDocInfoModel().setHealthInsurance(pvtHealthInsurance.getInsuranceClassCode());      // classCode
        schedule.getDocInfoModel().setHealthInsuranceDesc(pvtHealthInsurance.toString());               // 説明
        schedule.getDocInfoModel().setHealthInsuranceGUID(pvtHealthInsurance.getGUID());                // UUID
        schedule.getDocInfoModel().setPVTHealthInsuranceModel(pvtHealthInsurance);                      // 適用保険
        
        // 基本属性
        schedule.setStarted(startDate);
        schedule.setConfirmed(now);
        schedule.setRecorded(now);
        schedule.setKarteBean(karte);
        schedule.setUserModel(user);
        schedule.setStatus(IInfoModel.STATUS_FINAL);

        // 関係構築
        List<ModuleModel> modules = schedule.getModules();
        if (modules!=null) {
            for (ModuleModel module : modules) {
                module.setStarted(schedule.getStarted());
                module.setConfirmed(schedule.getConfirmed());
                module.setRecorded(schedule.getRecorded());
                module.setKarteBean(schedule.getKarteBean());
                module.setUserModel(user);
                module.setStatus(schedule.getStatus());
                module.setDocumentModel(schedule);
            }
        }

        // 永続化
        em.persist(schedule);
        //Logger.getLogger("open.dolphin").info("入院カルテ作成、保存終了");
        
        // 先頭に scheduleのIDを追加
        Collection<Long> newReult = new ArrayList(result.size()+1);
        newReult.add(schedule.getId());
        newReult.addAll(result);
        
        return newReult;
    }
    
       // Document
    public DocumentModel getDocumentByPk(long docPk) {

        DocumentModel ret;

        ret = (DocumentModel) em.createQuery(QUERY_DOCUMENT_BY_PK)
                                       .setParameter("pk", docPk)
                                       .getSingleResult();
        
        // module
        List<ModuleModel> modules =
                em.createQuery(QUERY_MODULE_BY_DOCUMENT)
                  .setParameter("id", ret.getId())
                  .getResultList();

        ret.setModules(modules);

        // SchemaModel を取得する
        List<SchemaModel> images =
                em.createQuery(QUERY_SCHEMA_BY_DOCUMENT)
                  .setParameter("id", ret.getId())
                  .getResultList();
        ret.setSchema(images);
        
        // AttachmentModel を取得する
            List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter("id", ret.getId())
            .getResultList();
            ret.setAttachment(attachments);
        
        return ret;
    }
    
    public LastDateCount30 getLastDateCount(long ptPK, String fidPid) {
        
        LastDateCount30 result = new LastDateCount30();
        
        // Karte -> システム登録日
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                   .setParameter("patientPk", ptPK)
                                   .getSingleResult();
        result.setCreated(karte.getCreated());
        
        // 文書数
        BigInteger cnt = (BigInteger)em.createNativeQuery("select count(*) from d_document d where d.karte_id=? and (d.status='F' or d.status = 'T')")
            .setParameter(1, karte.getId())
            .getSingleResult();
        result.setDocCount(cnt.longValue());
        
        if (result.getDocCount()!=0L) {
            // 最終文書日
            Date lastDocDate = (Date)
                    em.createNativeQuery("select max(m.started) from d_document m where m.karte_id=? and m.docType=? and (m.status = 'F' or m.status = 'T')")
                            .setParameter(1, karte.getId())
                            .setParameter(2, IInfoModel.DOCTYPE_KARTE)
                            .getSingleResult();
            result.setLastDocDate(lastDocDate);
        }
        
        // ラボカウント
        BigInteger labCount = (BigInteger)em.createNativeQuery("select count(*) from d_nlabo_module l where l.patientId=?")
            .setParameter(1, fidPid)
            .getSingleResult();
        result.setLabCount(labCount.longValue());
        
        if (result.getLabCount()!=0L) {
            // 最終ラボ報告日
            String lastLabDate = (String)
                    em.createNativeQuery("select max(m.sampleDate) from d_nlabo_module m where m.patientId=?")
                            .setParameter(1, fidPid)
                            .getSingleResult();
            result.setLastLabDate(lastLabDate);
        }
        
        // シェーマ
        BigInteger imageCount = (BigInteger)em.createNativeQuery("select count(*) from d_image l where l.karte_id=? and (l.status='F' or l.status = 'T')")
            .setParameter(1, karte.getId())
            .getSingleResult();
        result.setImageCount(imageCount.longValue());
        
        if (result.getImageCount()!=0L) {
            // 最終画像
            Date lastImageDate = (Date)
                    em.createNativeQuery("select max(m.started) from d_image m where m.karte_id=? and (m.status = 'F' or m.status = 'T')")
                            .setParameter(1, karte.getId())
                            .getSingleResult();
            result.setLastImageDate(lastImageDate);
        }
        
        // 病名数
        BigInteger diagnosisCount = (BigInteger)em.createNativeQuery("select count(*) from d_diagnosis l where l.karte_id=?")
            .setParameter(1, karte.getId())
            .getSingleResult();
        result.setDiagnosisCount(diagnosisCount.longValue());
        
        // アクティブ病名数
        BigInteger activeCount = (BigInteger)em.createNativeQuery("select count(*) from d_diagnosis l where l.karte_id=? and l.ended is NULL")
            .setParameter(1, karte.getId())
            .getSingleResult();
        result.setActiveDiagnosisCount(activeCount.longValue());
        
        // Allergy
        BigInteger allergyCount = (BigInteger)em.createNativeQuery("select count(*) from d_observation o where o.karte_id=? and o.observation='Allergy'")
                .setParameter(1, karte.getId())
                .getSingleResult();
        result.setAllergyCount(allergyCount.longValue());
        
        // 温度板
        Date ondoDate = (Date)em.createNativeQuery("select min(o.started) from d_ondoban o where o.karte_id=?")
                .setParameter(1, karte.getId())
                .getSingleResult();
        result.setOldestOndoDate(ondoDate);
        
        return result;       
    }
    
    /**
     * ドキュメント DocumentModel オブジェクトを保存する。
     * @param document 追加するDocumentModel オブジェクト
     * @return 追加した数
     */
    public long addDocument(DocumentModel document) {

        // 永続化する
        em.persist(document);

        // ID
        long id = document.getId();

        // 修正版の処理を行う
        long parentPk = document.getDocInfoModel().getParentPk();

        if (parentPk != 0L) {

            // 適合終了日を新しい版の確定日にする
            Date ended = document.getConfirmed();

            // オリジナルを取得し 終了日と status = M を設定する
            DocumentModel old = (DocumentModel)em.find(DocumentModel.class, parentPk);
            old.setEnded(ended);
            old.setStatus(IInfoModel.STATUS_MODIFIED);

            // 関連するモジュールとイメージに同じ処理を実行する
            Collection oldModules = em.createQuery(QUERY_MODULE_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldModules.iterator(); iter.hasNext(); ) {
                ModuleModel model = (ModuleModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }

            // Schema
            Collection oldImages = em.createQuery(QUERY_SCHEMA_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldImages.iterator(); iter.hasNext(); ) {
                SchemaModel model = (SchemaModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
            
            // Attachment
            Collection oldAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter(ID, parentPk).getResultList();
            for (Iterator iter = oldAttachments.iterator(); iter.hasNext(); ) {
                AttachmentModel model = (AttachmentModel)iter.next();
                model.setEnded(ended);
                model.setStatus(IInfoModel.STATUS_MODIFIED);
            }
        }
        
        //-------------------------------------------------------------
        // CLAIM送信
        //-------------------------------------------------------------
        if (!document.getDocInfoModel().isSendClaim()) {
            return id;
        }
        //Logger.getLogger("open.dolphin").info("KarteServiceBean will send claim");
        sendDocument(document);
        
        return id;
    }
    
        // JMS+MDB
    public void sendDocument(DocumentModel document) {
//s.oh^ 2014/01/23 ORCAとの接続対応
        Properties config = new Properties();
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        try {
            FileInputStream fin = new FileInputStream(f);
            try (InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect")) {
                config.load(r);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return;
        }
        String claimConn = config.getProperty("claim.conn");
        if(claimConn != null && claimConn.equals("server")) {
//s.oh$
//s.oh^ 2014/02/21 Claim送信方法の変更
            //Connection conn = null;
            //try {
            //    conn = connectionFactory.createConnection();
            //    Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            //
            //    ObjectMessage msg = session.createObjectMessage(document);
            //    MessageProducer producer = session.createProducer(queue);
            //    producer.send(msg);
            //
            //
            //} catch (Exception e) {
            //    e.printStackTrace(System.err);
            //    throw new RuntimeException(e.getMessage());
            //
            //} finally {
            //    if(conn != null) {
            //        try {
            //            conn.close();
            //        } catch (JMSException e) { 
            //        }
            //    }
            //}
            // ORCA CLAIM 送信パラメータ
            String host = config.getProperty("claim.host");
            int port = Integer.parseInt(config.getProperty("claim.send.port"));
            String enc = config.getProperty("claim.send.encoding");
            String facilityId = config.getProperty("dolphin.facilityId");
            java.util.logging.Logger.getLogger("open.dolphin").info("Document message has received. Sending ORCA will start(Not Que).");
            ClaimSender sender = new ClaimSender(host, port, enc);
            try {
                sender.send(document);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                java.util.logging.Logger.getLogger("open.dolphin").log(Level.WARNING, "Claim send error : {0}", ex.getMessage());
            }
//s.oh$
        }
    }
    
    /**
     * 新規病名保存、病名更新、CLAIM送信を一括して実行する。
     * @param wrapper DiagnosisSendWrapper
     * @return 新規病名のPKリスト
     */
    public List<Long> postPutSendDiagnosis(DiagnosisSendWrapper wrapper) {
        
//minagawa^ LSC 1.4 傷病名の削除 2013/06/24
        int cnt = 0;
        
        // 削除
        if (wrapper.getDeletedDiagnosis()!=null) {
            
            List<RegisteredDiagnosisModel> deletedList = wrapper.getDeletedDiagnosis();
            
            for (RegisteredDiagnosisModel bean : deletedList) {
                // ORCAの病名をインポート、Dolphinに登録しないで削除==0Lを除く
                if (bean.getId()!=0L) {
                    RegisteredDiagnosisModel delete = (RegisteredDiagnosisModel)em.find(RegisteredDiagnosisModel.class, bean.getId());
                    em.remove(delete);
                    cnt++;
                }
            }
        }
//minagawa$        
        
        // 更新
        if (wrapper.getUpdatedDiagnosis()!=null) {
            
            //int cnt = 0;
            List<RegisteredDiagnosisModel> updateList = wrapper.getUpdatedDiagnosis();
            
            for (RegisteredDiagnosisModel bean : updateList) {
                em.merge(bean);
                cnt++;
            }
        }
        
        // 永続化
        List<Long> ret = new ArrayList<>(3);
        if (wrapper.getAddedDiagnosis()!=null) {
            
            List<RegisteredDiagnosisModel> addList = wrapper.getAddedDiagnosis();
            
            for (RegisteredDiagnosisModel bean : addList) {
                em.persist(bean);
                ret.add(bean.getId());
            }
        }
        
        //-------------------------------------------------------------
        // CLAIM送信
        //-------------------------------------------------------------
        if (wrapper.getSendClaim() && wrapper.getConfirmDate()!=null) {
//s.oh^ 2014/01/23 ORCAとの接続対応
            Properties config = new Properties();
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            try {
                FileInputStream fin = new FileInputStream(f);
                InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
                config.load(r);
                r.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex.getMessage());
            }
            String claimConn = config.getProperty("claim.conn");
            if(claimConn != null && claimConn.equals("server")) {
//s.oh$
//s.oh^ 2014/02/21 Claim送信方法の変更
                //Connection conn = null;
                //try {
                //    conn = connectionFactory.createConnection();
                //    Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                //
                //    ObjectMessage msg = session.createObjectMessage(wrapper);
                //    MessageProducer producer = session.createProducer(queue);
                //    producer.send(msg);
                //
                //} catch (Exception e) {
                //    e.printStackTrace(System.err);
                //    throw new RuntimeException(e.getMessage());
                //
                //} 
                //finally {
                //    if(conn != null)
                //    {
                //        try
                //        {
                //        conn.close();
                //        }
                //        catch (JMSException e)
                //        { 
                //        }
                //    }
                //}
                String host = config.getProperty("claim.host");
                int port = Integer.parseInt(config.getProperty("claim.send.port"));
                String enc = config.getProperty("claim.send.encoding");
                java.util.logging.Logger.getLogger("open.dolphin").info("DiagnosisSendWrapper message has received. Sending ORCA will start(Not Que).");
                DiagnosisSender sender = new DiagnosisSender(host, port, enc);
                try {
                    sender.send(wrapper);
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    java.util.logging.Logger.getLogger("open.dolphin").warning("Diagnosis Claim send error : " + ex.getMessage());
                }
//s.oh$
            }
        }
        
        return ret;
    }
    
    /**
     * 傷病名を削除する。
     * @param removeList 削除する傷病名のidリスト
     * @return 削除数
     */
    public int removeDiagnosis(List<Long> removeList) {

        int cnt = 0;

        for (Long id : removeList) {
            RegisteredDiagnosisModel bean = (RegisteredDiagnosisModel) em.find(RegisteredDiagnosisModel.class, id);
            em.remove(bean);
            cnt++;
        }

        return cnt;
    }
}
