/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm10.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteNumber;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
//import open.dolphin.infomodel.NurseProgressCourseModel;
import open.dolphin.infomodel.ObservationModel;
//import open.dolphin.infomodel.OndobanModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.VitalModel;

/**
 *
 * @author kazushi
 */
@Named
@Stateless
public class ADM10_EHTServiceBean {
    
    // Karte
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    
    // Document & module
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";
    
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F' order by m.started desc";
    
    // memo
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";
    
    // Allergy
    private static final String QUERY_ALLERGY_BY_KARTE_ID = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    
    // Diagnosis
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL order by r.started desc";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_DESC = "from RegisteredDiagnosisModel r where r.karte.id=:karteId order by r.started desc";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_OUTCOMEONLY_DESC = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is not NULL order by r.started desc";
    
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    
    // バイタル対応
    private static final String QUERY_VITAL_BY_ID = "from VitalModel v where v.id=:id";
    private static final String ID = "id";
    
    @PersistenceContext
    private EntityManager em;
    
    
    // 患者メモ
    public PatientMemoModel getPatientMemo(long ptPK) {
        
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", ptPK)
                                       .getSingleResult();
        
        // メモを取得する
        List<PatientMemoModel> memoList =
                    (List<PatientMemoModel>)em.createQuery(QUERY_PATIENT_MEMO)
                                              .setParameter("karteId", karte.getId())
                                              .getResultList();
        
        return (!memoList.isEmpty()) ? memoList.get(0) : null;
    }
    
    public int addPatientMemo(PatientMemoModel model) {
        //em.persist(model);
        if(model.getKarteBean() != null) {
            List<PatientMemoModel> memoList =
                        (List<PatientMemoModel>)em.createQuery(QUERY_PATIENT_MEMO)
                                                  .setParameter("karteId", model.getKarteBean().getId())
                                                  .getResultList();
            if(memoList.isEmpty()) {
                em.persist(model);
            }else{
                PatientMemoModel pmm = memoList.get(0);
                pmm.setMemo(model.getMemo());
                em.merge(pmm);
            }
        }else{
            em.persist(model);
        }
        return 1;
    }
    
    public int updatePatientMemo(PatientMemoModel model) {
        em.merge(model);
        return 1;
    }
    
    public int deletePatientMemo(PatientMemoModel model) {
        PatientMemoModel delete = em.find(PatientMemoModel.class, model.getId());
        em.remove(delete);
        return 1;
    }
    
    // Allergy
    public List<AllergyModel> getAllergies(long patientPk) {

       List<AllergyModel> retList = new ArrayList<>();

       KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        List<ObservationModel> observations =
                (List<ObservationModel>)em.createQuery(QUERY_ALLERGY_BY_KARTE_ID)
                              .setParameter("karteId", karte.getId())
                              .getResultList();

        for (ObservationModel observation : observations) {
            AllergyModel allergy = new AllergyModel();
            allergy.setObservationId(observation.getId());
            allergy.setFactor(observation.getPhenomenon());
            allergy.setSeverity(observation.getCategoryValue());
            allergy.setIdentifiedDate(observation.confirmDateAsString());
            allergy.setMemo(observation.getMemo());
            retList.add(allergy);
        }

        return retList;
    }
    
    public int addAllergy(ObservationModel model) {
        em.persist(model);
        return 1;
    }
    
    public int updateAllergy(ObservationModel model) {
        em.merge(model);
        return 1;
    }
    
    public int deleteAllergy(ObservationModel model) {
        ObservationModel target = em.find(ObservationModel.class, model.getId());
        em.remove(target);
        return 1;
    }

    // Active 病名のみ
    public List<RegisteredDiagnosisModel> getDiagnosis(long patientPk, boolean active, boolean outcomeOnly, int firstResult, int maxResult) {

        List<RegisteredDiagnosisModel> ret;
        
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        if (active) {
            // 疾患開始日の降順 i.e. 直近分
            ret = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC)
                        .setParameter("karteId", karte.getId())
                        .setFirstResult(firstResult)
                        .setMaxResults(maxResult)
                        .getResultList();
            
        } else if (outcomeOnly) {
            ret = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_OUTCOMEONLY_DESC)
                        .setParameter("karteId", karte.getId())
                        .setFirstResult(firstResult)
                        .setMaxResults(maxResult)
                        .getResultList();
               
        } else {
            ret = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_DESC)
                        .setParameter("karteId", karte.getId())
                        .setFirstResult(firstResult)
                        .setMaxResults(maxResult)
                        .getResultList();
        }
        
        return ret;
    }
    
    public int addDiagnosis(RegisteredDiagnosisModel model) {
        em.persist(model);
        return 1;
    }
    
    public int updateDiagnosis(RegisteredDiagnosisModel model) {
        em.merge(model);
        return 1;
    }
    
    public int deleteDiagnosis(RegisteredDiagnosisModel model) {
        RegisteredDiagnosisModel delete = em.find(RegisteredDiagnosisModel.class, model.getId());
        em.remove(delete);
        return 1;
    }
    
    // EHT Karte
    public KarteNumber getKarteNumber(long ptPK) {
        
        KarteNumber ret = new KarteNumber();
        
        // Karte
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", ptPK)
                                       .getSingleResult();
        
        ret.setKarteNumber(karte.getId());
        ret.setCreated(karte.getCreated());
        
        return ret;
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
    
    public List<String> deleteDocumentByPk(long id) {
        
        //----------------------------------------
        // 参照されているDocumentの場合は例外を投げる
        //----------------------------------------
        Collection refs = em.createQuery(QUERY_DOCUMENT_BY_LINK_ID)
        .setParameter("id", id).getResultList();
        if (refs != null && refs.size() >0) {
            RuntimeException ce = new RuntimeException("他のドキュメントから参照されているため削除できません。");
            throw ce;
        } 
        
        // 終了日
        Date ended = new Date();
        
        // 削除件数
        int cnt=0;
        
        // 削除リスト　文書ID
        List<String> list = new ArrayList<>();
        
        // Loop で削除
        while (true) {
            
            try {
                //-----------------------
                // 対象 Document を取得する
                //-----------------------
                DocumentModel delete = (DocumentModel)em.find(DocumentModel.class, id);
                
                //------------------------
                // 削除フラグをたてる
                //------------------------
                delete.setStatus(IInfoModel.STATUS_DELETE);
                delete.setEnded(ended);
                cnt++;
                list.add(delete.getDocInfoModel().getDocId());
                
                //------------------------------
                // 関連するモジュールに同じ処理を行う
                //------------------------------
                Collection deleteModules = em.createQuery(QUERY_MODULE_BY_DOCUMENT)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteModules.iterator(); iter.hasNext(); ) {
                    ModuleModel model = (ModuleModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連する画像に同じ処理を行う
                //------------------------------
                Collection deleteImages = em.createQuery(QUERY_SCHEMA_BY_DOCUMENT)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteImages.iterator(); iter.hasNext(); ) {
                    SchemaModel model = (SchemaModel) iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }

                //------------------------------
                // 関連するAttachmentに同じ処理を行う
                //------------------------------
                Collection deleteAttachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                .setParameter("id", id).getResultList();
                for (Iterator iter = deleteAttachments.iterator(); iter.hasNext(); ) {
                    AttachmentModel model = (AttachmentModel)iter.next();
                    model.setStatus(IInfoModel.STATUS_DELETE);
                    model.setEnded(ended);
                }
                
                // 削除したDocumentのlinkID を 削除するDocument id(PK) にしてLoopさせる
                id = delete.getLinkId();
                
            } catch (Exception e) {
                break;
            }
        }

        return list;
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
    
    //------------------------------------------------------------------------
    // Module 関連
    //------------------------------------------------------------------------ 
    public List<ModuleModel> getModules(long patientPk, String entity, int firstResult, int maxResult) {
        
        List<ModuleModel> retList;
        
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", patientPk)
                          .getSingleResult();

        if (entity.equals("all")) {

            retList = em.createQuery("from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity!=:entity and m.status='F' order by m.started desc")
                        .setParameter("karteId", karte.getId())
                        .setParameter("entity", "progressCourse")
                        .setFirstResult(firstResult)
                        .setMaxResults(maxResult)
                        .getResultList();

        } else {

            retList = em.createQuery(QUERY_MODULE_BY_ENTITY)
                        .setParameter("karteId", karte.getId())
                        .setParameter("entity", entity)
                        .setFirstResult(firstResult)
                        .setMaxResults(maxResult)
                        .getResultList();
        }
        
        return retList;
    }
    
    
    public List<NLaboModule> getLaboTest(String facilityId, String patientId, int firstResult, int maxResult) {

        StringBuilder sb = new StringBuilder();
        sb.append(facilityId);
        sb.append(":");
        sb.append(patientId);
        String fidPid = sb.toString();

        List<NLaboModule> ret = (List<NLaboModule>)
                        em.createQuery("from NLaboModule l where l.patientId=:fidPid order by l.sampleDate desc")
                          .setParameter("fidPid", fidPid)
                          .setFirstResult(firstResult)
                          .setMaxResults(maxResult)
                          .getResultList();

        for (NLaboModule m : ret) {

            List<NLaboItem> items = (List<NLaboItem>)
                            em.createQuery("from NLaboItem l where l.laboModule.id=:mid order by groupCode,parentCode,itemCode")
                              .setParameter("mid", m.getId())
                              .getResultList();
            m.setItems(items);
        }
        return ret;
    }
    
    public List<NLaboItem> getLaboTestItem(String facilityId, String patientId, int firstResult, int maxResult, String itemCode) {

        StringBuilder sb = new StringBuilder();
        sb.append(facilityId);
        sb.append(":");
        sb.append(patientId);
        String fidPid = sb.toString();

        List<NLaboItem> ret = (List<NLaboItem>)
                        em.createQuery("from NLaboItem l where l.patientId=:fidPid and l.itemCode=:itemCode order by l.sampleDate desc")
                          .setParameter("fidPid", fidPid)
                          .setParameter("itemCode", itemCode)
                          .setFirstResult(firstResult)
                          .setMaxResults(maxResult)
                          .getResultList();

        return ret;
    }
    
    //------------------------------------------------------------------------
    // Stamp 関連
    //------------------------------------------------------------------------    
    public IStampTreeModel getTrees(long userPK) {

        // パーソナルツリーを取得する
        List<StampTreeModel> list = (List<StampTreeModel>)
                em.createQuery("from StampTreeModel s where s.user.id=:userPK")
                  .setParameter("userPK", userPK)
                  .getResultList();

        // 新規ユーザの場合
        if (list.isEmpty()) {
            return null;
        }

        // 最初の Tree を取得
        IStampTreeModel ret = (StampTreeModel)list.remove(0);

        // まだある場合 BUG
        if (!list.isEmpty()) {
            // 後は delete する
            for (int i=0; i < list.size(); i++) {
                StampTreeModel st = (StampTreeModel)list.remove(0);
                em.remove(st);
            }
        }

        return ret;
    }
    
    public StampModel getStamp(String stampId) {

        try {
            return (StampModel)em.find(StampModel.class, stampId);
        } catch (NoResultException e) {
        }

        return null;
    }
    
    protected void setHealthInsurances(Collection<PatientModel> list) {
        if (list != null && !list.isEmpty()) {
            for (PatientModel pm : list) {
                setHealthInsurances(pm);
            }
        }
    }
    
    protected void setHealthInsurances(PatientModel pm) {
        if (pm != null) {
            List<HealthInsuranceModel> ins = getHealthInsurances(pm.getId());
            pm.setHealthInsurances(ins);
        }
    }

    protected List<HealthInsuranceModel> getHealthInsurances(long pk) {
        
        List<HealthInsuranceModel> ins =
                em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                .setParameter("pk", pk)
                .getResultList();
        return ins;
    }
    

    public VitalModel getVital(String id) {
        VitalModel vital
                = (VitalModel)em.createQuery(QUERY_VITAL_BY_ID)
                .setParameter(ID, Long.parseLong(id))
                .getSingleResult();

        return vital;
    }

////-----------------------------------------------------------------------------
//// 温度板
////-----------------------------------------------------------------------------     
//    public List<OndobanModel> getOndoban(long ptPK, Date fromDate, Date toDate) {
//        
//        // Karte
//        KarteBean karte;
//        karte = (KarteBean)
//                em.createQuery(QUERY_KARTE)
//                        .setParameter("patientPk", ptPK)
//                        .getSingleResult();
//        
//        // 文書履歴エントリーを取得しカルテに設定する
//        List<OndobanModel> result;
//        result = (List<OndobanModel>)em.createQuery("from OndobanModel o where o.karte.id=:karteId and o.started between :fromDate and :toDate")
//                .setParameter("karteId", karte.getId())
//                .setParameter("fromDate", fromDate)
//                .setParameter("toDate", toDate)
//                .getResultList();
//        return result;
//    }
//    
//    public List<Long> addOndoban(List<OndobanModel> observations) {
//        if (observations != null && observations.size() > 0) {
//            List<Long> ret = new ArrayList<>(observations.size());
//            for (OndobanModel model : observations) {
//                em.persist(model);
//                ret.add(model.getId());
//            }
//            return ret;
//        }
//        return null;
//    }
//    
//    public int updateOndoban(List<OndobanModel> observations) {
//        if (observations != null && observations.size() > 0) {
//            for (OndobanModel model : observations) {
//                em.merge(model);
//            }
//            return observations.size();
//        }
//        return 0;
//    }
//    
//    public int deleteOndoban(List<OndobanModel> observations) {
//        if (observations != null && observations.size() > 0) {
//            for (OndobanModel model : observations) {
//                OndobanModel delete = em.find(OndobanModel.class, model.getId());
//                em.remove(delete);
//            }
//            return observations.size();
//        }
//        return 0;
//    }
////-----------------------------------------------------------------------------
//// 看護記録
////-----------------------------------------------------------------------------     
//    public List<NurseProgressCourseModel> getNurseProgressCourse(long ptPK, int firstResult, int maxResult) {
//        
//        // Karte
//        KarteBean karte;
//        karte = (KarteBean)
//                em.createQuery(QUERY_KARTE)
//                        .setParameter("patientPk", ptPK)
//                        .getSingleResult();
//        
//        // started で降順にして取り出す
//        List<NurseProgressCourseModel> result;
//        result = (List<NurseProgressCourseModel>)em.createQuery("from NurseProgressCourseModel n where n.karte.id=:karteId order by n.started desc")
//                .setParameter("karteId", karte.getId())
//                .setFirstResult(firstResult)
//                .setMaxResults(maxResult)
//                .getResultList();
//        return result;
//    }
//    
//    public Long addNurseProgressCourse(NurseProgressCourseModel model) {
//        em.persist(model);
//        return model.getId();
//    }
//    
//    public int updateNurseProgressCourse(NurseProgressCourseModel model) {
//        em.merge(model);
//        return 1;
//    }
//    
//    public int deleteNurseProgressCourse(NurseProgressCourseModel model) {
//        NurseProgressCourseModel delete = em.find(NurseProgressCourseModel.class, model.getId());
//        em.remove(delete);
//        return 1;
//    }
}
