package open.dolphin.touch.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 *
 * @author kazushi Minagawa, Digital Globe Inc.
 */
@Named
@Stateless
public class IPhoneServiceBean {

    // ユーザー検索
    private static final String QUERY_USER_0 = "from UserModel u where u.userId=:userId";
    private static final String QUERY_USER = "from UserModel u where u.userId=:userId and u.password=:password";
    
    // 来院情報検索
    private static final String QUERY_PATIENT_VISIT_BY_PK = "from PatientVisitModel p where p.id=:pk";
    private static final String QUERY_PATIENT_VISIT_LIST = "from PatientVisitModel p where p.facilityId=:facilityId order by p.pvtDate";
    private static final String QUERY_PATIENT_VISIT_LIST_RANGE = "from PatientVisitModel p where p.facilityId=:facilityId and p.pvtDate between :start and :end order by p.pvtDate";
    private static final String QUERY_PATIENT_VISIT_LIST_BEFORE = "from PatientVisitModel p where p.facilityId=:facilityId and p.pvtDate < :before order by p.pvtDate";
    
    // 初診検索
    private static final String QUERY_FIRST_VISITOR_LIST = "from KarteBean k where k.patient.facilityId=:facilityId order by k.created desc";
    private static final String QUERY_FIRST_VISITOR_LIST_RANGE = "from KarteBean k where k.patient.facilityId=:facilityId and k.created between :start and :end order by k.created desc";
    
    // 患者検索
    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId like :pid order by p.patientId";
    private static final String QUERY_PATIENT_BY_PK = "from PatientModel p where p.id = :pk";
    private static final String QUERY_PATIENT_BY_NAME = "from PatientModel p where p.facilityId=:facilityId and p.fullName like :name  order by p.patientId";
    private static final String QUERY_PATIENT_BY_KANA = "from PatientModel p where p.facilityId=:facilityId and p.kanaName like :name  order by p.patientId";
    private static final String QUERY_PATIENT_BY_ID = "from PatientModel p where p.facilityId=:facilityId and p.patientId like :pid  order by p.patientId";
    
    // 健康保険検索
    private static final String QUERY_HEALTH_INSURANCE_BY_PK = "from HealthInsuranceModel h where h.patient.id = :pk";
    //private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    
    // カルテ検索
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    //private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F' order by m.started desc, d.confirmed desc";
    private static final String QUERY_MODULE_BY_ENTITY = "from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F' order by m.started desc, m.confirmed desc"; // chg funabashi 20131103
//s.oh^ 2014/07/29 スタンプ／シェーマ／添付のソート
    //private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    //private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id order by m.id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id order by i.id";
//s.oh$
    private static final String QUERY_SCHEMA = "from SchemaModel i where i.karte.id=:karteId and i.status='F' order by i.started desc";
    private static final String QUERY_FIRST_ENCOUNTER_0 = "from FirstEncounter0Model f where f.karte.id=:karteId";
    private static final String QUERY_FIRST_ENCOUNTER_1 = "from FirstEncounter1Model f where f.karte.id=:karteId";
	private static final String QUERY_DOCUMENT_LIST_BY_FACILITY = "from DocumentModel d where d.karte.patient.facilityId=:facilityId and d.status='F' order by d.started desc, d.confirmed desc";
    private static final String QUERY_DOCUMENT_LIST_BY_FACILITY_RANGE = "from DocumentModel d where d.karte.patient.facilityId=:facilityId and d.started between :start and :end and d.status='F' order by d.started desc, d.confirmed desc";
//s.oh^ 2013/09/19
    //private static final String QUERY_DOCUMENT_LIST_BY_KARTE = "from DocumentModel d where d.karte.id=:karteId and d.status='F' order by d.started desc";
    private static final String QUERY_DOCUMENT_LIST_BY_KARTE = "from DocumentModel d where d.karte.id=:karteId and d.status='F' order by d.started desc, d.confirmed desc";
//s.oh$
    
    private static final String QUERY_DIAGNOSIS = "from RegisteredDiagnosisModel r where r.karte.id=:karteId order by r.started desc";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL";
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL order by r.started desc";
    
    private static final String QUERY_ALLERGY_BY_KARTE_ID = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    
    // memo
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";
    
    // パラーメータ
    private static final String PK = "pk";
    private static final String FID = "fid";
    private static final String PID = "pid";
    private static final String ID = "id";
    private static final String KARTE_ID = "karteId";
    
    @PersistenceContext
    private EntityManager em;

    public UserModel getUser(String userId, String password) {

        UserModel user = (UserModel)
                em.createQuery(QUERY_USER)
                  .setParameter("userId", userId)
                  .setParameter("password", password)
                  .getSingleResult();

        if (user != null && user.getMemberType() != null) {
            return user;
        }

        return null;
    }
    
    public UserModel getUserById(String userId) {

        UserModel user = (UserModel)
                em.createQuery(QUERY_USER_0)
                  .setParameter("userId", userId)
                  .getSingleResult();

        if (user != null && user.getMemberType() != null) {
            return user;
        }

        return null;
    }
    
    public PatientVisitModel getPatientVisitByPk(long pk) {

        // PatientVisitModelを施設PKで検索する
        PatientVisitModel result =
                (PatientVisitModel)em.createQuery(QUERY_PATIENT_VISIT_BY_PK)
                                           .setParameter(PK, pk)
                                           .getSingleResult();
        return result;
    }

    public List<PatientVisitModel> getPatientVisit(String facilityId, int firstResult, int maxResult) {

        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>)em.createQuery(QUERY_PATIENT_VISIT_LIST)
                                           .setParameter("facilityId", facilityId)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        return result;
    }

    public List<PatientVisitModel> getPatientVisitRange(String facilityId, String start, String end, int firstResult, int MaxResult) {

        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>)em.createQuery(QUERY_PATIENT_VISIT_LIST_RANGE)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("start", start)
                                           .setParameter("end", end)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(MaxResult)
                                           .getResultList();

        return result;
    }

    public List<PatientVisitModel> getPatientVisitBefore(String facilityId, String before) {

        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>)em.createQuery(QUERY_PATIENT_VISIT_LIST_BEFORE)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("before", before)
                                           .getResultList();

        return result;
    }
    
    public List<KarteBean> getFirstVisitors(String facilityId, int firstResult, int maxResult) {

        List<KarteBean> list =
                (List<KarteBean>)em.createQuery(QUERY_FIRST_VISITOR_LIST)
                                           .setParameter("facilityId", facilityId)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        
        return list;
    }

    public List<KarteBean> getFirstVisitorsRange(String facilityId, Date start, Date end) {

        List<KarteBean> list =
                (List<KarteBean>)em.createQuery(QUERY_FIRST_VISITOR_LIST_RANGE)
                                   .setParameter("facilityId", facilityId)
                                   .setParameter("start", start)
                                   .setParameter("end", end)
                                   .getResultList();

        return list;
    }
    
    //--------------------------------------------------------------------------------
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
                em.createQuery(QUERY_HEALTH_INSURANCE_BY_PK)
                .setParameter(PK, pk)
                .getResultList();
        return ins;
    }
    
    //=======================================
    
    public List<DocumentModel> getDocumentListByFacility(String facilityId, int firstResult, int maxResult) {

        List<DocumentModel> list =
                (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_LIST_BY_FACILITY)
                                       .setParameter("facilityId", facilityId)
                                       .setFirstResult(firstResult)
                                       .setMaxResults(maxResult)
                                       .getResultList();
        return list;
    }

    public List<DocumentModel> getDocumentListByFacilityRange(String facilityId, Date start, Date end) {

        List<DocumentModel> list =
                (List<DocumentModel>)em.createQuery(QUERY_DOCUMENT_LIST_BY_FACILITY_RANGE)
                                       .setParameter("facilityId", facilityId)
                                       .setParameter("start", start)
                                       .setParameter("end", end)
                                       .getResultList();
        return list;
    }

    //-----------------------------------------
    // 患者
    //-----------------------------------------
    
    public PatientModel getPatientById(String fid,String pid) {

        // 患者レコードは FacilityId と patientId で複合キーになっている
        PatientModel bean
                = (PatientModel)em.createQuery(QUERY_PATIENT_BY_FID_PID)
                .setParameter(FID, fid)
                .setParameter(PID, pid)
                .getSingleResult();

        long pk = bean.getId();

        // Lazy Fetch の 基本属性を検索する
        // 患者の健康保険を取得する
        List<HealthInsuranceModel> insurances
                = (List<HealthInsuranceModel>)em.createQuery(QUERY_HEALTH_INSURANCE_BY_PK)
                .setParameter(PK, pk).getResultList();
        bean.setHealthInsurances(insurances);

        return bean;
    }
    
    public PatientModel getPatient(long pk) {

        PatientModel ret = (PatientModel) em.createQuery(QUERY_PATIENT_BY_PK)
                                            .setParameter("pk", pk)
                                            .getSingleResult();
        if (ret!=null) {
            setHealthInsurances(ret);
        }
        return ret;
    }

    public long getKartePKByPatientPK(long pk) {
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", pk)
                                       .getSingleResult();
        return karte.getId();
    }
    
    public PatientPackage getPatientPackage(long pk) {

        PatientPackage ret = new PatientPackage();

        PatientModel patient = (PatientModel) em.createQuery(QUERY_PATIENT_BY_PK)
                                            .setParameter("pk", pk)
                                            .getSingleResult();
        ret.setPatient(patient);

        // 健康保険を取得する
        List<HealthInsuranceModel> insurances = em.createQuery(QUERY_HEALTH_INSURANCE_BY_PK)
                                  .setParameter("pk", patient.getId())
                                  .getResultList();
        ret.setInsurances(insurances);

        // Allergy
        List<AllergyModel> allergies = new ArrayList<>();

        // カルテの PK を得る
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patient.getId())
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
            allergies.add(allergy);
        }
       
        ret.setAllergies(allergies);
        
        return ret;
    }

    public List<PatientModel> getPatientsByName(String facilityId, String name, int firstResult, int maxResult) {

        List<PatientModel> list =
                (List<PatientModel>)em.createQuery(QUERY_PATIENT_BY_NAME)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("name", name + "%")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        if (list.isEmpty()) {
            list = (List<PatientModel>)em.createQuery(QUERY_PATIENT_BY_NAME)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("name", "%" + name)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        }

        if (list.isEmpty()) {
            list = (List<PatientModel>)em.createQuery(QUERY_PATIENT_BY_ID)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("pid", name + "%")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        }
        
        setHealthInsurances(list);

        return list;
    }

   public List<PatientModel> getPatientsByKana(String facilityId, String name, int firstResult, int maxResult) {

        List<PatientModel> list =
                (List<PatientModel>)em.createQuery(QUERY_PATIENT_BY_KANA)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("name", name + "%")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        if (list.isEmpty()) {
            list = (List<PatientModel>)em.createQuery(QUERY_PATIENT_BY_KANA)
                                           .setParameter("facilityId", facilityId)
                                           .setParameter("name", "%" + name)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        }
        
        setHealthInsurances(list);

        return list;
    }
   
//minagawa^ 音声検索辞書作成    
    public int countPatients(String facilityId) {
        Long count = (Long)em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid")
                .setParameter("fid", facilityId).getSingleResult();
        return count.intValue();
    }
    
    public List<String> getAllPatientsWithKana(String facilityId, int firstResult, int maxResult) {
        List<String> list = em.createQuery("select p.kanaName from PatientModel p where p.facilityId=:fid order by p.kanaName")
                .setParameter("fid", facilityId)
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();
        return list;
    }
//minagawa$ 

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
            retList.add(allergy);
        }

        return retList;
   }


   //--------------------------------------------------
   // Module
   //--------------------------------------------------
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

    // QUERY_SCHEMA
    
    public List<SchemaModel> getSchema(long patientPk, int firstResult, int maxResult) {

        List<SchemaModel> retList;
        
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", patientPk)
                          .getSingleResult();

        retList = em.createQuery(QUERY_SCHEMA)
                    .setParameter("karteId", karte.getId())
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .getResultList();
        
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
    
//    public FirstEncounter0Model getFirstEncounter0Model(long patientPk) {
//
//        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
//                                       .setParameter("patientPk", patientPk)
//                                       .getSingleResult();
//
//        List<FirstEncounter0Model> ret = (List<FirstEncounter0Model>)
//                            em.createQuery(QUERY_FIRST_ENCOUNTER_0)
//                            .setParameter("karteId", karte.getId())
//                            .getResultList();
//        
//        if (ret != null && ret.size() > 0) {
//            return ret.get(0);
//        }
//
//        return null;
//    }
//    
//    public FirstEncounter1Model getFirstEncounter1Model(long patientPk) {
//
//        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
//                                       .setParameter("patientPk", patientPk)
//                                       .getSingleResult();
//
//        // カルテの PK を得る
//        long karteId = karte.getId();
//
//        List<FirstEncounter1Model> ret = em.createQuery(QUERY_FIRST_ENCOUNTER_1)
//                        .setParameter("karteId", karteId)
//                        .getResultList();
//
//        if (ret != null && ret.size() > 0) {
//            return ret.get(0);
//        }
//        
//        return null;
//    }

    public List<DocumentModel> getDocuments(long patientPk, int firstResult, int maxResult) {

        List<DocumentModel> ret;

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        // カルテの PK を得る
        long karteId = karte.getId();

        ret = em.createQuery(QUERY_DOCUMENT_LIST_BY_KARTE)
            .setParameter("karteId", karteId)
            .setFirstResult(firstResult)
            .setMaxResults(maxResult)
            .getResultList();
        
        for (DocumentModel doc : ret) {

            // module
            List<ModuleModel> modules =
                    em.createQuery(QUERY_MODULE_BY_DOCUMENT)
                      .setParameter("id", doc.getId())
                      .getResultList();

            doc.setModules(modules);

            // SchemaModel を取得する
            List<SchemaModel> images =
                    em.createQuery(QUERY_SCHEMA_BY_DOCUMENT)
                      .setParameter("id", doc.getId())
                      .getResultList();
            doc.setSchema(images);
        }

        return ret;
    }
    
    public DocumentModel getDocumentByPk(long docPk) {

        DocumentModel ret;

        ret = (DocumentModel) em.createQuery(QUERY_DOCUMENT_BY_PK)
                                       .setParameter(PK, docPk)
                                       .getSingleResult();
        
        // module
        List<ModuleModel> modules =
                em.createQuery(QUERY_MODULE_BY_DOCUMENT)
                  .setParameter(ID, ret.getId())
                  .getResultList();

        ret.setModules(modules);

        // SchemaModel を取得する
        List<SchemaModel> images =
                em.createQuery(QUERY_SCHEMA_BY_DOCUMENT)
                  .setParameter(ID, ret.getId())
                  .getResultList();
        ret.setSchema(images);
        
        // AttachmentModel を取得する
            List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
            .setParameter(ID, ret.getId())
            .getResultList();
            ret.setAttachment(attachments);
        
        return ret;
    }

    //=============================================]
    
    public List<RegisteredDiagnosisModel> getDiagnosis(long patientPk, int firstResult, int maxResult) {

        List<RegisteredDiagnosisModel> ret;

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();
        // カルテの PK を得る
        long karteId = karte.getId();

        // 疾患開始日の降順 i.e. 直近分
        ret = em.createQuery(QUERY_DIAGNOSIS)
                    .setParameter("karteId", karteId)
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .getResultList();
        
        return ret;
    }
    
    // Active 病名のみ
    public List<RegisteredDiagnosisModel> getActiveDiagnosis(long patientPk, int firstResult, int maxResult) {

        List<RegisteredDiagnosisModel> ret;

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        // 疾患開始日の降順 i.e. 直近分
        ret = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC)
                    .setParameter("karteId", karte.getId())
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .getResultList();
        
        return ret;
    }
    
    //-----------------------------------------------
    // レコード件数を取得する
    //-----------------------------------------------

    public Long getDiagnosisCount(long patientPk) {

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        // カルテの PK を得る
        long karteId = karte.getId();

        Long ret = (Long)em.createQuery("select count(*) from RegisteredDiagnosisModel r where r.karte.id=:karteId")
            .setParameter("karteId", karteId)
            .getSingleResult();

        return ret;
    }

    public Long getModuleCount(long patientPk, String entity) {

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        // カルテの PK を得る
        long karteId = karte.getId();

        Long ret;

        if (entity.equals("all")) {
            ret = (Long)em.createQuery("select count(*) from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity!=:entity and m.status='F'")
            .setParameter("karteId", karteId)
            .setParameter("entity", "progressCourse")
            .getSingleResult();

        } else {
            ret = (Long)em.createQuery("select count(*) from ModuleModel m where m.karte.id=:karteId and m.moduleInfo.entity=:entity and m.status='F'")
            .setParameter("karteId", karteId)
            .setParameter("entity", entity)
            .getSingleResult();
        }

        return ret;
    }
    
    public Long getLabTestCount(String facilityId, String patientId) {

        StringBuilder sb = new StringBuilder();
        sb.append(facilityId);
        sb.append(":");
        sb.append(patientId);
        String fidPid = sb.toString();

        Long ret = (Long)em.createQuery("select count(*) from NLaboModule l where l.patientId=:fidPid")
            .setParameter("fidPid", fidPid)
            .getSingleResult();

        return ret;
    }

    public Long getDocumentCount(long patientPk) {

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPk)
                                       .getSingleResult();

        // カルテの PK を得る
        long karteId = karte.getId();

        Long ret = (Long)em.createQuery("select count(*) from DocumentModel d where d.karte.id=:karteId and d.status='F'")
            .setParameter("karteId", karteId)
            .getSingleResult();

        return ret;
    }


    //==================================================
    // DEMO
    //==================================================
    
    public List<DemoPatient> getPatientVisitDemo(int firstResult, int maxResult) {

        List<DemoPatient> result = (List<DemoPatient>)em.createQuery("from DemoPatient p order by p.id")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        return result;
    }
    
    public List<DemoPatient> getPatientVisitRangeDemo(int firstResult, int maxResult) {

        List<DemoPatient> result = (List<DemoPatient>)em.createQuery("from DemoPatient p order by p.id")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        return result;
    }

    public List<DemoPatient> getFirstVisitorsDemo(int firstResult, int maxResult) {

        List<DemoPatient> result = (List<DemoPatient>)em.createQuery("from DemoPatient p order by p.id")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        return result;
    }

    public DemoPatient getPatientDemo(long id) {

        DemoPatient ret = (DemoPatient) em.find(DemoPatient.class, id);
        return ret;
    }

    public List<DemoPatient> getPatientsByNameDemo(String name, int firstResult, int maxResult) {

        List<DemoPatient> list =
                (List<DemoPatient>)em.createQuery("from DemoPatient p where p.name like :name")
                                           .setParameter("name", name + "%")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        if (list.isEmpty()) {
            list = (List<DemoPatient>)em.createQuery("from DemoPatient p where p.name like :name")
                                           .setParameter("name", "%" + name)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        }

        return list;
    }

   public List<DemoPatient> getPatientsByKanaDemo(String name, int firstResult, int maxResult) {

        List<DemoPatient> list =
                (List<DemoPatient>)em.createQuery("from DemoPatient p where p.kana like :name")
                                           .setParameter("name", name + "%")
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();

        if (list.isEmpty()) {
            list = (List<DemoPatient>)em.createQuery("from DemoPatient p where p.kana like :name")
                                           .setParameter("name", "%" + name)
                                           .setFirstResult(firstResult)
                                           .setMaxResults(maxResult)
                                           .getResultList();
        }

        return list;
    }

    public List<DemoDisease> getDiagnosisDemo() {

        List<DemoDisease> ret = em.createQuery("from DemoDisease").getResultList();

        return ret;
    }

    public List<DemoRp> getRpDemo() {

        List<DemoRp> ret = em.createQuery("from DemoRp").getResultList();

        return ret;
    }
    
    /*
     * 以下 VisitTouch1.0 関連
     */
    public VisitPackage getVisitPackage(long pvtPK, long patientPK, long docPK, int mode) {
        
        VisitPackage ret = new VisitPackage();
        
        if (pvtPK!=0L) {
            
            // 来院情報を取得する
            PatientVisitModel pvt = (PatientVisitModel)em.createQuery(QUERY_PATIENT_VISIT_BY_PK)
                    .setParameter(PK,pvtPK)
                    .getSingleResult();
            ret.setPatientVisitModel(pvt);
            
            // 健康保険を取得する
            setHealthInsurances(pvt.getPatientModel());
            
            // 以降の処理のためPKを設定する
            patientPK = pvt.getPatientModel().getId();
            
        } else if (patientPK!=0L) {
            
            // 患者情報を取得する
            PatientModel patient = (PatientModel) em.createQuery(QUERY_PATIENT_BY_PK)
                                            .setParameter(PK, patientPK).getSingleResult();
            // 健康保険を取得する
            setHealthInsurances(patient);
            
            ret.setPatientModel(patient);
        }
        
        // karteを取得する
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                                       .setParameter("patientPk", patientPK)
                                       .getSingleResult();
        // PKのみセットする
        ret.setKartePk(karte.getId());
        
        // Active病名を取得する
        List<RegisteredDiagnosisModel> disease = (List<RegisteredDiagnosisModel>)em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY)
                .setParameter("karteId", karte.getId()).getResultList();
        if (disease!=null && disease.size()>0) {
            ret.setDisease(disease);
        }

        // アレルギーを取得する
        List<AllergyModel> allergyList = new ArrayList();
        List<ObservationModel> observations =
                (List<ObservationModel>)em.createQuery(QUERY_ALLERGY_BY_KARTE_ID)
                              .setParameter("karteId", karte.getId())
                              .getResultList();
        if (observations!=null && observations.size()>0) {
            for (ObservationModel observation : observations) {
                AllergyModel allergy = new AllergyModel();
                allergy.setObservationId(observation.getId());
                allergy.setFactor(observation.getPhenomenon());
                allergy.setSeverity(observation.getCategoryValue());
                allergy.setIdentifiedDate(observation.confirmDateAsString());
                allergyList.add(allergy);
            }
            ret.setAllergies(allergyList);
        }
        
        // メモを取得する
        List<PatientMemoModel> memoList =
                    (List<PatientMemoModel>)em.createQuery(QUERY_PATIENT_MEMO)
                                              .setParameter(KARTE_ID, karte.getId())
                                              .getResultList();
        if (!memoList.isEmpty()) {
            ret.setPatientMemoModel(memoList.get(0));
        }
        
        // 文書を取得する    
        if (docPK!=0L && mode!=0) {
            DocumentModel document = getDocumentByPk(docPK);
            //document.toDetuch();
            
            // 前回処方を適用
            if (mode==1) {
                List<ModuleModel> rps = new ArrayList(10);
                List<ModuleModel> modules = document.getModules();
                if (modules!=null && modules.size()>0) {
                    for (ModuleModel module : modules) {
                        if (module.getModuleInfoBean()!=null && module.getModuleInfoBean().getEntity().equals(IInfoModel.ENTITY_MED_ORDER)) {
                            rps.add(module);
                        }
                    }
                }
                if (!rps.isEmpty()) {
                    document.setModules(rps);
//minagawa^ 処方がない場合は全コピーになってしまう                    
                } else {
                    document.setModules(null);
                }
//minagawa$                
                // 前回処方なのでシェーマを除く
                document.setSchema(null);
            // wholeCopy または modify
            // Progress Course を変換する    
            } else {
//minagawa^ Plan側テキストも取得する                
//                //ModuleModel soaProgressCourse=null;
//                List<ModuleModel> moduleList = new ArrayList(10);
//                List<ModuleModel> modules = document.getModules();
//                if (modules!=null && modules.size()>0) {
//                    for (ModuleModel module : modules) {
//                        //String entity = module.getModuleInfoBean().getEntity();
//                        String role = module.getModuleInfoBean().getStampRole();
//                        if (!role.equals(IInfoModel.ROLE_P_SPEC)) {
//                            moduleList.add(module);
//                        }
//                    }
//                }
//                if (!moduleList.isEmpty()) {
//                    document.setModules(moduleList);
//                }
//minagawa$                
            }
            ret.setDocumenModel(document);
        }
        
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
    
//-----------------------------------------------------------------------------
// EHT
//-----------------------------------------------------------------------------    
    public List<DocInfoModel> getDocInfoList(long ptPK) {
        
        // Karte
        KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE)
                          .setParameter("patientPk", ptPK)
                          .getSingleResult();
        
        // 文書履歴エントリーを取得しカルテに設定する
        List<DocumentModel> documents =
                (List<DocumentModel>)em.createQuery("from DocumentModel d where d.karte.id=:karteId and (d.status='F' or d.status='T') order by d.started desc")
                                       .setParameter(KARTE_ID, karte.getId())
                                       .getResultList();

        List<DocInfoModel> c = new ArrayList(documents.size());
        for (DocumentModel docBean : documents) {
            docBean.toDetuch();
            c.add(docBean.getDocInfoModel());
        }
        return c;
    }
}
