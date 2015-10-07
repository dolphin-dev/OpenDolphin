package open.dolphin.adm20.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 *
 * @author kazushi Minagawa, Digital Globe Inc.
 */
@Named
@Stateless
public class ADM20_IPhoneServiceBean {

    // ユーザー検索
    private static final String QUERY_USER_0 = "from UserModel u where u.userId=:userId";
    
    // 来院情報検索
    private static final String QUERY_PATIENT_VISIT_BY_PK = "from PatientVisitModel p where p.id=:pk";
     
    // 患者検索
    private static final String QUERY_PATIENT_BY_PK = "from PatientModel p where p.id = :pk";
    private static final String QUERY_PATIENT_BY_NAME = "from PatientModel p where p.facilityId=:facilityId and p.fullName like :name  order by p.patientId";
    private static final String QUERY_PATIENT_BY_KANA = "from PatientModel p where p.facilityId=:facilityId and p.kanaName like :name  order by p.patientId";
    private static final String QUERY_PATIENT_BY_ID = "from PatientModel p where p.facilityId=:facilityId and p.patientId like :pid  order by p.patientId";
    
    // 健康保険検索
    private static final String QUERY_HEALTH_INSURANCE_BY_PK = "from HealthInsuranceModel h where h.patient.id = :pk";
    //private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    
    // カルテ検索
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_ALLERGY_BY_KARTE_ID = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL"; 
    
    // memo
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";
    
    // パラーメータ
    private static final String PK = "pk";
    private static final String ID = "id";
    private static final String KARTE_ID = "karteId";
    
    @PersistenceContext
    private EntityManager em;

    
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
}
