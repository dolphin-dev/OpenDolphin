package open.dolphin.adm20.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PHRKey;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;

/**
 *
 * @author kazushi, Minahawa
 */
@Named
@Stateless
public class AMD20_PHRServiceBean {
    
    @PersistenceContext
    private EntityManager em;

    public Long addOrUpdatePatient(PHRKey phrKey) {
        em.merge(phrKey);
        return phrKey.getId();
    }
    
    public PHRKey getPHRKey(String accessKey) {
        
        PHRKey phrKey;
        List<PHRKey> list = (List<PHRKey>)em.createQuery("from PHRKey p where p.accessKey=:accessKey")
                .setParameter("accessKey", accessKey)
                .getResultList();
        phrKey = (list!=null && list.size()==1) ? list.get(0) : null;
        return phrKey;
    }
    
    public PHRKey getPHRKeyByPatientId(String patientId) {
        
        PHRKey phrKey;
        List<PHRKey> list = (List<PHRKey>)em.createQuery("from PHRKey p where p.patientId=:patientId")
                .setParameter("patientId", patientId)
                .getResultList();
        phrKey = (list!=null && list.size()==1) ? list.get(0) : null;
        return phrKey;
    }
    
    public PatientModel getPatient(String fid, String pid) {
        PatientModel bean
                = (PatientModel)em.createQuery("from PatientModel p where p.facilityId=:fid and p.patientId=:pid")
                .setParameter("fid", fid)
                .setParameter("pid", pid)
                .getSingleResult();
        return bean;
    }
    
    public FacilityModel getFacility(String fid) {
        List<FacilityModel> fList = em.createQuery("from FacilityModel f where f.facilityId=:fid")
                .setParameter("fid", fid)
                .getResultList();
        FacilityModel facility = fList.size()>0 ? fList.get(0) : null;
        return facility;
    }
    
    public KarteBean getKarte(String fid, String pid) {
        
        PatientModel bean
                = (PatientModel)em.createQuery("from PatientModel p where p.facilityId=:fid and p.patientId=:pid")
                .setParameter("fid", fid)
                .setParameter("pid", pid)
                .getSingleResult();

        long pk = bean.getId();
        
        KarteBean karte = (KarteBean) em.createQuery("from KarteBean k where k.patient.id=:pk")
                                       .setParameter("pk", pk)
                                       .getSingleResult();
        return karte;
    }

    public List<DocumentModel> getDocuments(long karteId, Date since, int first, int max, String[] entities) {
        
        List<DocumentModel> ret;
        
        if (since!=null) {
            
            ret = em.createQuery("from DocumentModel d where d.karte.id=:kid and d.started > :since and d.status='F' order by d.started desc")
                    .setParameter("kid", karteId)
                    .setParameter("since", since)
                    .setFirstResult(first)          // 0
                    .setMaxResults(max)             // 3 に制限
                    .getResultList();
        } else {
            ret = em.createQuery("from DocumentModel d where d.karte.id=:kid and d.status='F' order by d.started desc")
                    .setParameter("kid", karteId)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        }
        
        if (ret==null|| ret.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("from ModuleModel m where m.document.id=:docId and (m.moduleInfo.entity='medOrder' or m.moduleInfo.entity='injectionOrder')");
        String ejbQL = sb.toString();
        
        ret.stream().forEach((doc) -> {
            List<ModuleModel> list2 = (List<ModuleModel>)em.createQuery(ejbQL)
                    .setParameter("docId", doc.getId())
                    .getResultList();
            doc.setModules(list2);
        });
        
        return ret;
    }
    
    public List<AllergyModel> getAllergies(long karteId) {

       List<AllergyModel> retList = new ArrayList<>();

        List<ObservationModel> observations =
                (List<ObservationModel>)em.createQuery("from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'")
                              .setParameter("karteId", karteId)
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
    
    public List<RegisteredDiagnosisModel> getDiagnosis(long karteId) {

        List<RegisteredDiagnosisModel> ret;
        
        // 疾患開始日の降順 i.e. 直近分
        ret = em.createQuery("from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL order by r.started desc")
                    .setParameter("karteId", karteId)
                    .getResultList();
        
        return ret;
    }
    
    public List<ModuleModel> getLastMedication(long karteId) {
        
        List<DocumentModel> list = em.createQuery("from DocumentModel d where d.karte.id=:kid and d.status='F' order by d.started desc")
                    .setParameter("kid", karteId)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList();
        
        String ejbQL = "from ModuleModel m where m.document.id=:docId and (m.moduleInfo.entity='medOrder' or m.moduleInfo.entity='injectionOrder')";
        
        List<ModuleModel> ret = new ArrayList<>();
        
        list.stream().forEach((doc) -> {
            List<ModuleModel> list2 = (List<ModuleModel>)em.createQuery(ejbQL)
                    .setParameter("docId", doc.getId())
                    .getResultList();
            ret.addAll(list2);
        });
        
        return ret;
    }
    
    public List<NLaboModule> getLabTest(String fid, String pid, String since, int first, int max) {

        StringBuilder sb = new StringBuilder();
        sb.append(fid);
        sb.append(":");
        sb.append(pid);
        String fidPid = sb.toString();
        
        List<FacilityModel> fList = em.createQuery("from FacilityModel f where f.facilityId=:fid")
                .setParameter("fid", fid)
                .getResultList();
        FacilityModel facility = fList.get(0);
        
        List<NLaboModule> ret;
        
        if (since!=null) {
            ret = (List<NLaboModule>)
                    em.createQuery("from NLaboModule l where l.patientId=:fidPid and l.sampleDate > :since order by l.sampleDate")
                            .setParameter("fidPid", fidPid)
                            .setParameter("since", since)
                            .setFirstResult(first)
                            .setMaxResults(max)
                            .getResultList();
            
        } else {
            ret = (List<NLaboModule>)
                    em.createQuery("from NLaboModule l where l.patientId=:fidPid order by l.sampleDate desc")
                            .setParameter("fidPid", fidPid)
                            .setFirstResult(first)
                            .setMaxResults(max)
                            .getResultList();
        }

        for (NLaboModule m : ret) {
            
            m.setFacilityId(facility.getFacilityId());
            m.setFacilityName(facility.getFacilityName());

            List<NLaboItem> items = (List<NLaboItem>)
                            em.createQuery("from NLaboItem l where l.laboModule.id=:mid order by l.groupCode,l.parentCode,l.itemCode")
                              .setParameter("mid", m.getId())
                              .getResultList();
            m.setItems(items);
        }
        return ret;
    }
    
    public List<NLaboModule> getLastLabTest(String fid, String pid) {
        StringBuilder sb = new StringBuilder();
        sb.append(fid);
        sb.append(":");
        sb.append(pid);
        String fidPid = sb.toString();
        
        List<NLaboModule> ret;
        
        ret = (List<NLaboModule>)
                    em.createQuery("from NLaboModule l where l.patientId=:fidPid order by l.sampleDate desc")
                            .setParameter("fidPid", fidPid)
                            .setFirstResult(0)
                            .setMaxResults(1)
                            .getResultList();
        
        for (NLaboModule m : ret) {

            List<NLaboItem> items = (List<NLaboItem>)
                            em.createQuery("from NLaboItem l where l.laboModule.id=:mid order by l.groupCode,l.parentCode,l.itemCode")
                              .setParameter("mid", m.getId())
                              .getResultList();
            m.setItems(items);
        }
        
        return ret;
    }
    
    public SchemaModel getImages(long karteId) {

        List images
                = em.createQuery("from SchemaModel s where s.karte.id =:karteId and s.status='F' order by s.started desc")
                .setParameter("karteId", karteId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        if (images!=null && images.size()>0) {
            return (SchemaModel)images.get(0);
        } else {
            return null;
        }
    }
}
