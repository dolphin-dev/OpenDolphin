/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.touch.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteNumber;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LastDateCount;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientFreeDocumentModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.VitalModel;
import open.dolphin.touch.converter.IPhysicalModel;

/**
 *
 * @author kazushi
 */
@Named
@Stateless
public class EHTServiceBean {
    
    // 新規患者
    private static final String QUERY_FIRST_VISITOR_LIST = "from KarteBean k where k.patient.facilityId=:facilityId order by k.created desc";
    
    // 来院日検索
    private static final String QUERY_PATIENT_BY_PVTDATE = "from PatientVisitModel p where p.facilityId = :fid and p.pvtDate like :date and p.status!=64";
    
    // Karte
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    
    // Document & module
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    private static final String QUERY_DOCUMENT_BY_LINK_ID = "from DocumentModel d where d.linkId=:id";
    
//s.oh^ 2014/07/29 スタンプ／シェーマ／添付のソート
    //private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    //private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    //private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id order by m.id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id order by i.id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id order by a.id";
//s.oh$
//s.oh^ 2014/08/20 添付ファイルの別読
    private static final String QUERY_ATTACHMENT_BY_ID = "from AttachmentModel a where a.id=:id";
//s.oh$
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
    
    // funabashi^ salesforce 20131022
    private static final String QUERY_KARTE_BY_KARTE_ID = "from KarteBean k where k.id=:id";
    // funabashi$
    
    // バイタル対応
    private static final String QUERY_VITAL_BY_FPID = "from VitalModel v where v.facilityPatId=:fpid";
    private static final String QUERY_VITAL_BY_ID = "from VitalModel v where v.id=:id";
    private static final String ID = "id";
    private static final String FPID = "fpid";
    
    @PersistenceContext
    private EntityManager em;
    
    // 直近の新患リスト
    public List<PatientModel> getFirstVisitors(String facilityId, int firstResult, int maxResult) {

        List<KarteBean> list =
                (List<KarteBean>)em.createQuery(QUERY_FIRST_VISITOR_LIST)
                                           .setParameter("facilityId", facilityId)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        
        List<PatientModel> result = new ArrayList(list.size());
        
        for (KarteBean k : list) {
            PatientModel patient = k.getPatientModel();
//minagawa^  ios7 EHRTouch用          
            patient.setFirstVisited(k.getCreated());
//minagawa$            
            setHealthInsurances(patient);
            result.add(patient);
        }
        
        return result;
    }
    
    public List<PatientModel> getPatientsByPvtDate(String fid, String pvtDate) {

        List<PatientVisitModel> list =
                em.createQuery(QUERY_PATIENT_BY_PVTDATE)
                  .setParameter("fid", fid)
                  .setParameter("date", pvtDate+"%")
                  .getResultList();

        List<PatientModel> ret = new ArrayList<PatientModel>();

        for (PatientVisitModel pvt : list) {
            PatientModel patient = pvt.getPatientModel();
            List<HealthInsuranceModel> insurances
                        = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                        .setParameter("pk", patient.getId()).getResultList();
                patient.setHealthInsurances(insurances);
            ret.add(patient);
            
            // 患者の健康保険を取得する
            setHealthInsurances(patient);
//masuda^   最終受診日設定
           patient.setPvtDate(pvt.getPvtDate());
//masuda$        
        }
        return ret;
    }
    
    public List<PatientModel> getTmpKarte(String facilityId) {
        
        List<PatientModel> ret = new ArrayList();
        
        List<DocumentModel> list = (List<DocumentModel>)
        em.createQuery("from DocumentModel d where d.karte.patient.facilityId=:fid and d.status='T'")
                .setParameter("fid", facilityId)
                .getResultList();
        
        HashMap<String, String> map = new HashMap(10,0.75f);
        for (DocumentModel dm : list) {
            if (dm.getFirstConfirmed().after(dm.getConfirmed())) {
                continue;
            }
            KarteBean kb = dm.getKarte();
            PatientModel pm = kb.getPatient();
            if (map.get(pm.getPatientId())!=null) {
                continue;
            }
            map.put(pm.getPatientId(), "pid");
            ret.add(pm);
        }
        
        this.setHealthInsurances(ret);
        
        return ret;
    }
    
    // 初診日、最終カルテ記録日、カルテ枚数、検査数、画像数
    public LastDateCount getLastDateCount(long ptPK, String fidPid) {
        
        LastDateCount result = new LastDateCount();
        
        // Karte -> システム登録日
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                   .setParameter("patientPk", ptPK)
                                   .getSingleResult();
        result.setCreated(karte.getCreated());
        
        // 文書数
        Long docCount = (Long)em.createQuery("select count(*) from DocumentModel d where d.karte.id=:karteId and (d.status='F' or d.status = 'T')")
            .setParameter("karteId", karte.getId())
            .getSingleResult();
        result.setDocCount(docCount);
        
        if (docCount!=0L) {
            // 最終文書日
            Date lastDocDate = (Date)
                    em.createNativeQuery("select max(m.started) from d_document m where m.karte_id=:karteId and m.docType=:docType and (m.status = 'F' or m.status = 'T')")
                            .setParameter("karteId", karte.getId())
                            .setParameter("docType", IInfoModel.DOCTYPE_KARTE)
                            .getSingleResult();
            result.setLastDocDate(lastDocDate);
        }
        
        // ラボカウント
        Long labCount = (Long)em.createQuery("select count(*) from NLaboModule l where l.patientId=:fidPid")
            .setParameter("fidPid", fidPid)
            .getSingleResult();
        result.setLabCount(labCount);
        
        if (labCount!=0L) {
            // 最終ラボ報告日
            String lastLabDate = (String)
                    em.createNativeQuery("select max(m.sampleDate) from d_nlabo_module m where m.patientId=:fidPid")
                            .setParameter("fidPid", fidPid)
                            .getSingleResult();
            result.setLastLabDate(lastLabDate);
        }
        
        // シェーマ
        Long imageCount = (Long)em.createQuery("select count(*) from SchemaModel l where l.karte.id=:karteId and (l.status='F' or l.status = 'T')")
            .setParameter("karteId", karte.getId())
            .getSingleResult();
        result.setImageCount(imageCount);
        
        if (imageCount!=0L) {
            // 最終ラボ報告日
            Date lastImageDate = (Date)
                    em.createNativeQuery("select max(m.started) from d_image m where m.karte_id=:karteId and (m.status = 'F' or m.status = 'T')")
                            .setParameter("karteId", karte.getId())
                            .getSingleResult();
            result.setLastImageDate(lastImageDate);
        }
        
        // 病名数
        Long diagnosisCount = (Long)em.createQuery("select count(*) from RegisteredDiagnosisModel l where l.karte.id=:karteId")
            .setParameter("karteId", karte.getId())
            .getSingleResult();
        result.setDiagnosisCount(diagnosisCount);
        
        // アクティブ病名数
        Long activeCount = (Long)em.createQuery("select count(*) from RegisteredDiagnosisModel l where l.karte.id=:karteId and l.ended is NULL")
            .setParameter("karteId", karte.getId())
            .getSingleResult();
        result.setActiveDiagnosisCount(activeCount);
        
        return result;
    }
    
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
    
//s.oh^ 2014/04/03/サマリー対応
    public PatientFreeDocumentModel getPatientFreeDocument(String fpid) {
        
        PatientFreeDocumentModel ret = (PatientFreeDocumentModel)em.createQuery("from PatientFreeDocumentModel p where p.facilityPatId=:fpid")
                                        .setParameter(FPID, fpid)
                                        .getSingleResult();

        return ret;
    }
//s.oh$
    
    // Allergy
    public List<AllergyModel> getAllergies(long patientPk) {

       List<AllergyModel> retList = new ArrayList<AllergyModel>();

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
    
    // DocInfo List
    public List<DocInfoModel> getDocInfoList(long ptPK) {
        
        // Karte
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", ptPK)
                          .getSingleResult();
        
        // 文書履歴エントリーを取得しカルテに設定する
        List<DocumentModel> documents =
                (List<DocumentModel>)em.createQuery("from DocumentModel d where d.karte.id=:karteId and (d.status='F' or d.status='T') order by d.started desc")
                                       .setParameter("karteId", karte.getId())
                                       .getResultList();

        List<DocInfoModel> c = new ArrayList(documents.size());
        for (DocumentModel docBean : documents) {
            docBean.toDetuch();
            c.add(docBean.getDocInfoModel());
        }
        return c;
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
        List<String> list = new ArrayList<String>();
        
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
    
    // salesforce K.Funabashi 20131004 add
    public PatientModel getPatientByKarteId(long karteId){
        KarteBean karte = (KarteBean)em.createQuery(QUERY_KARTE_BY_KARTE_ID)
                                        .setParameter("id", karteId)
                                        .getSingleResult();
        return karte.getPatient();
    }
    // salesforce K.Funabashi 20131126 add
    public PatientModel getPatientByFpid(String fpid){
        
        String[] vals = fpid.split(":");
        PatientModel p = (PatientModel)em.createQuery("from PatientModel p where p.facilityId=:facilityId and p.patientId=:patientId")
                            .setParameter("facilityId",vals[0])
                            .setParameter("patientId", vals[1])
                            .getSingleResult();

        return p;
        
    }
    // salesforce K.Funabashi 20131126 add
    public ObservationModel getObservationByObservationId(long id){
        ObservationModel o = (ObservationModel)em.createQuery("from ObservationModel o where o.id=:id")
                                                .setParameter(ID, id)
                                                .getSingleResult();
        return o;
    }

    // バイタル対応
    public int addVital(VitalModel add) {
        em.persist(add);
        return 1;
    }
    public int updateVital(VitalModel update) {
        VitalModel current = (VitalModel) em.find(VitalModel.class, update.getId());
        if(current == null) {
            return 0;
        }
        em.merge(update);
        return 1;
    }
    public VitalModel getVital(String id) {
        VitalModel vital
                = (VitalModel)em.createQuery(QUERY_VITAL_BY_ID)
                .setParameter(ID, Long.parseLong(id))
                .getSingleResult();

        return vital;
    }
    public List<VitalModel> getPatVital(String fpid) {
        List<VitalModel> results
                = (List<VitalModel>)em.createQuery(QUERY_VITAL_BY_FPID)
                .setParameter(FPID, fpid)
                .getResultList();

        return results;
    }
    public int removeVital(String id) {
        VitalModel remove = getVital(id);
        em.remove(remove);

        return 1;
    }
    public List<Long> addObservations(List<ObservationModel> observations) {
        if (observations != null && observations.size() > 0) {
            List<Long> ret = new ArrayList<Long>(observations.size());
            for (ObservationModel model : observations) {
                em.persist(model);
                ret.add(new Long(model.getId()));
            }
            return ret;
        }
        return null;
    }
    public List<IPhysicalModel> getPhysicals(long karteId) {
        List<IPhysicalModel> ret = new ArrayList<IPhysicalModel>();
        List<ObservationModel> listH = (List<ObservationModel>)em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyHeight'")
                               .setParameter("karteId", karteId)
                               .getResultList();
        List<ObservationModel> listW = (List<ObservationModel>)em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='PhysicalExam' and o.phenomenon='bodyWeight'")
                               .setParameter("karteId", karteId)
                               .getResultList();
        if (listH != null && listW != null) {
            for (int i = 0; i < listH.size(); i++) {
                IPhysicalModel h = new IPhysicalModel();
                h.fromObservationModel(listH.get(i));
                String memo = h.getMemo();
                if (memo == null) {
                    memo = h.getIdentifiedDate();
                }
                
                // 
                // 体重のメモが一致するものを見つける
                //
                IPhysicalModel found = null;
                for (int j = 0; j < listW.size(); j++) {
                    IPhysicalModel w = new IPhysicalModel();
                    w.fromObservationModel(listW.get(j));
                    String memo2 = w.getMemo();
                    if (memo2 == null) {
                        memo2 = w.getIdentifiedDate();
                    }
                    if (memo2.equals(memo)) {
                        found = w;
                        IPhysicalModel m = new IPhysicalModel();
                        m.setHeightId(h.getHeightId());
                        m.setHeight(h.getHeight());
                        m.setWeightId(w.getWeightId());
                        m.setWeight(w.getWeight());
                        m.setIdentifiedDate(h.getIdentifiedDate());
                        m.setMemo(memo);
                        ret.add(m);
                        break;
                    }
                }
                
                if (found != null) {
                    // 一致する体重はリストから除く
                    listW.remove(found);
                } else {
                    // なければ身長のみを加える
                    ret.add(h);
                }
            }
            
            // 体重のリストが残っていればループする
            if (listW.size() > 0) {
                for (int i = 0; i < listW.size(); i++) {
                    IPhysicalModel m = new IPhysicalModel();
                    m.fromObservationModel(listW.get(i));
                    ret.add(m);
                }
            }
            
        } else if (listH != null) {
            // 身長だけの場合
            for (int i = 0; i < listH.size(); i++) {
                IPhysicalModel m = new IPhysicalModel();
                m.fromObservationModel(listH.get(i));
                ret.add(m);
            }
            
        } else if (listW != null) {
            // 体重だけの場合
            for (int i = 0; i < listW.size(); i++) {
                IPhysicalModel m = new IPhysicalModel();
                m.fromObservationModel(listW.get(i));
                ret.add(m);
            }
        }
        return ret;
    }
    public int removeObservations(List<Long> observations) {
        if (observations != null && observations.size() > 0) {
            int cnt = 0;
            for (Long id : observations) {
                ObservationModel model = (ObservationModel) em.find(ObservationModel.class, id);
                em.remove(model);
                cnt++;
            }
            return cnt;
        }
        return 0;
    }    
    
//s.oh^ 2014/08/20 添付ファイルの別読
    public AttachmentModel getAttachment(long pk) {
        try {
            AttachmentModel attachment = (AttachmentModel)em.createQuery(QUERY_ATTACHMENT_BY_ID)
                                            .setParameter(ID, pk)
                                            .getSingleResult();
            return attachment;
        } catch (NoResultException e) {
        }
        return null;
    }
//s.oh$

}
