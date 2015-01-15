package open.dolphin.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc
 */
@Named
@Stateless
public class PatientServiceBean {

    // cancel status=64 を where 節へ追加
    private static final String QUERY_PATIENT_BY_PVTDATE = "from PatientVisitModel p where p.facilityId = :fid and p.pvtDate like :date and p.status!=64";
    private static final String QUERY_PATIENT_BY_NAME = "from PatientModel p where p.facilityId=:fid and p.fullName like :name";
    private static final String QUERY_PATIENT_BY_KANA = "from PatientModel p where p.facilityId=:fid and p.kanaName like :name";
    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId like :pid";
    private static final String QUERY_PATIENT_BY_TELEPHONE = "from PatientModel p where p.facilityId = :fid and (p.telephone like :number or p.mobilePhone like :number)";
    private static final String QUERY_PATIENT_BY_ZIPCODE = "from PatientModel p where p.facilityId = :fid and p.address.zipCode like :zipCode";
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
//s.oh^ 2014/08/19 施設患者一括表示機能
    private static final String QUERY_PATIENT_BY_APPMEMO = "from PatientModel p where p.facilityId = :fid and p.appMemo like :appMemo";
//s.oh$

    private static final String PK = "pk";
    private static final String FID = "fid";
    private static final String PID = "pid";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String ZIPCODE = "zipCode";
    private static final String DATE = "date";
    private static final String PERCENT = "%";
//s.oh^ 2014/08/19 施設患者一括表示機能
    private static final String APPMEMO = "appMemo";
//s.oh$

    @PersistenceContext
    private EntityManager em;
    
//masuda^
    @Inject
    private ChartEventServiceBean eventServiceBean;
//masuda$

    
    public List<PatientModel> getPatientsByName(String fid, String name) {

        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_NAME)
                .setParameter(FID, fid)
                .setParameter(NAME, name + PERCENT)
                .getResultList();

        // 後方一致検索を行う
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_NAME)
                .setParameter(FID, fid)
                .setParameter(NAME, PERCENT + name)
                .getResultList();
        }
        
//s.oh^ 2014/08/19 施設患者一括表示機能
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, name+PERCENT)
                .getResultList();
        }
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, PERCENT+name)
                .getResultList();
        }
//s.oh$
        
        //-----------------------------------
        // 患者の健康保険を取得する
        setHealthInsurances(ret);
        //-----------------------------------
        
//masuda^   最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }
//masuda$

        return ret;
    }

    
    public List<PatientModel> getPatientsByKana(String fid, String name) {

        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_KANA)
            .setParameter(FID, fid)
            .setParameter(NAME, name + PERCENT)
            .getResultList();

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_KANA)
                .setParameter(FID, fid)
                .setParameter(NAME, PERCENT + name)
                .getResultList();
        }
        
//s.oh^ 2014/08/19 施設患者一括表示機能
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, name+PERCENT)
                .getResultList();
        }
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, PERCENT+name)
                .getResultList();
        }
//s.oh$

        //-----------------------------------
        // 患者の健康保険を取得する
        setHealthInsurances(ret);
        //-----------------------------------
        
//masuda^   最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }
//masuda$
        
        return ret;
    }

    
    public List<PatientModel> getPatientsByDigit(String fid, String digit) {

        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_FID_PID)
            .setParameter(FID, fid)
            .setParameter(PID, digit+PERCENT)
            .getResultList();

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_TELEPHONE)
                .setParameter(FID, fid)
                .setParameter(NUMBER, digit+PERCENT)
                .getResultList();
        }

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_ZIPCODE)
                .setParameter(FID, fid)
                .setParameter(ZIPCODE, digit+PERCENT)
                .getResultList();
        }

        //-----------------------------------
        // 患者の健康保険を取得する
        setHealthInsurances(ret);
        //-----------------------------------
        
//masuda^   最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }
//masuda$

        return ret;
    }
    
    public List<PatientModel> getPatientsByPvtDate(String fid, String pvtDate) {

        List<PatientVisitModel> list =
                em.createQuery(QUERY_PATIENT_BY_PVTDATE)
                  .setParameter(FID, fid)
                  .setParameter(DATE, pvtDate+PERCENT)
                  .getResultList();

        List<PatientModel> ret = new ArrayList<PatientModel>();

        for (PatientVisitModel pvt : list) {
            PatientModel patient = pvt.getPatientModel();
            List<HealthInsuranceModel> insurances
                        = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                        .setParameter(PK, patient.getId()).getResultList();
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

    /**
     * 患者ID(BUSINESS KEY)を指定して患者オブジェクトを返す。
     *
     * @param patientId 施設内患者ID
     * @return 該当するPatientModel
     */
    
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
        setHealthInsurances(bean);

        return bean;
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
    
    /**
     * 仮保存カルテがある患者のリストを返す。
     */
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
//minagawa$    

    /**
     * 患者を登録する。
     * @param patient PatientModel
     * @return データベース Primary Key
     */
    public long addPatient(PatientModel patient) {
        em.persist(patient);
        long pk = patient.getId();
        return pk;
    }

    /**
     * 患者情報を更新する。
     * @param patient 更新する患者
     * @return 更新数
     */
    
    public int update(PatientModel patient) {
        em.merge(patient);
 //masuda^   患者情報が更新されたらPvtListも更新する必要あり
        updatePvtList(patient);
//masuda$       
        return 1;
    }
    
//masuda^
    // pvtListのPatientModelを更新し、クライアントにも通知する
    private void updatePvtList(PatientModel pm) {
        String fid = pm.getFacilityId();
        List<PatientVisitModel> pvtList = eventServiceBean.getPvtList(fid);
        for (PatientVisitModel pvt : pvtList) {
            if (pvt.getPatientModel().getId() == pm.getId()) {
//s.oh^ 2013/10/07 患者情報が正しく表示されない
                List<HealthInsuranceModel> him = pvt.getPatientModel().getHealthInsurances();
                if(pm.getHealthInsurances() == null) {
                    pm.setHealthInsurances(him);
                }
//s.oh$
                pvt.setPatientModel(pm);
                 // クライアントに通知
                String uuid = eventServiceBean.getServerUUID();
                ChartEventModel msg = new ChartEventModel(uuid);
                msg.setPatientModel(pm);
                msg.setFacilityId(fid);
                msg.setEventType(ChartEventModel.PM_MERGE);
                eventServiceBean.notifyEvent(msg);
            }
        }
    }
    
    private void setPvtDate(String fid, List<PatientModel> list) {
        
        final String sql =
                "from PatientVisitModel p where p.facilityId = :fid and p.patient.id = :patientPk "
                + "and p.status != :status order by p.pvtDate desc";
        
        for (PatientModel patient : list) {
            try {
                PatientVisitModel pvt = (PatientVisitModel) 
                        em.createQuery(sql)
                        .setParameter("fid", fid)
                        .setParameter("patientPk", patient.getId())
                        .setParameter("status", -1)
                        .setMaxResults(1)
                        .getSingleResult();
                patient.setPvtDate(pvt.getPvtDate());
            } catch (NoResultException e) {
            }
        }
    }
    
    public List<PatientModel> getPatientList(String fid, List<String> idList) {
        
        final String sql 
                = "from PatientModel p where p.facilityId = :fid and p.patientId in (:ids)";
        
        List<PatientModel> list = (List<PatientModel>)
                em.createQuery(sql)
                .setParameter("fid", fid)
                .setParameter("ids", idList)
                .getResultList();
        
        // 患者の健康保険を取得する。忘れがちｗ
        setHealthInsurances(list);
        
        return list;
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
                .setParameter(PK, pk)
                .getResultList();
        return ins;
    }

//masuda$
    
    // 検索件数が1000件超過
    public Long getPatientCount(String facilityId, String patientId) {
        Long ret = (Long)em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid and p.patientId like :pid")
                .setParameter("fid", facilityId)
                .setParameter("pid", patientId+"%")
                .getSingleResult();
        return ret;
    }
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public List<PatientModel> getAllPatient(String fid) {
        
        List<PatientModel> ret = em.createQuery("from PatientModel p where p.facilityId=:fid")
            .setParameter(FID, fid)
            .getResultList();
        
        setHealthInsurances(ret);
        
        return ret;
    }
//s.oh$
    
//s.oh^ 2014/10/01 患者検索(傷病名)
    public List<PatientModel> getCustom(String fid, String param) {
        List<PatientModel> ret = new ArrayList();
        
        final String DIAGNOSIS = "[D]";
        
        if(param.indexOf(DIAGNOSIS) == 0) {
            String val = param.substring(param.indexOf(DIAGNOSIS) + DIAGNOSIS.length());
            List<RegisteredDiagnosisModel> list = null;
            if(val.startsWith("*") && val.endsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", PERCENT+val+PERCENT)
                         .getResultList();
            }else if(val.startsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", PERCENT+val)
                         .getResultList();
            }else if(val.endsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", val+PERCENT)
                         .getResultList();
            }else{
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis=:val and d.status='F'")
                         .setParameter("val", val)
                         .getResultList();
            }
            HashMap<String, String> map = new HashMap(10,0.75f);
            for(RegisteredDiagnosisModel rdm : list) {
                KarteBean karte = (KarteBean)em.find(KarteBean.class, rdm.getKarte().getId());
                if(karte != null && karte.getPatient() != null) {
                    if(map.get(karte.getPatient().getPatientId())!=null) {
                        continue;
                    }else{
                        map.put(karte.getPatient().getPatientId(), "pid");
                    }
                    ret.add(karte.getPatient());
                }
            }
            map.clear();
        }
        
        this.setHealthInsurances(ret);
        
        return ret;
    }
//s.oh$
}
