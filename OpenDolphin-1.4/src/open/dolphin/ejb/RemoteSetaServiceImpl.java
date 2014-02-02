package open.dolphin.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.CompositeImageModel;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.FirstEncounter2Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

/**
 * 瀬田クリニックサービス。
 */
@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteSetaService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteSetaService")
public class RemoteSetaServiceImpl extends DolphinService implements RemoteSetaService {
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /** Creates a new instance of RemoteSetaServiceImpl */
    public RemoteSetaServiceImpl() {
    }
    
    /**
     * 瀬田クリニック新患登録&受付。
     * @param patient PatientModel
     * @param model 瀬田クリニック固有の情報
     */
    @Override
    public Object[] saveOrUpdateAsPvt(PatientVisitModel pvt, FirstEncounter0Model model) {
       
        String facilityId = getCallersFacilityId(ctx);
        
        PatientModel patient = pvt.getPatient();
        patient.setFacilityId(facilityId);
        pvt.setFacilityId(facilityId);
        
        // 患者情報を保存する
        Object[] ret = saveOrUpdatePatient(patient, model);
        
        // PVT を永続化する
        em.merge(pvt);
        
        return ret;
    }
    
    /**
     * 瀬田クリニック新患登録。
     * @param patient PatientModel
     * @param model 瀬田クリニック固有の情報
     */
    @Override
    public Object[] saveOrUpdatePatient(PatientModel patient, FirstEncounter0Model model) {
        
        Object[] ret = new Object[3];
        String facilityId = getCallersFacilityId(ctx);
        patient.setFacilityId(facilityId);
        
        String test = patient.getPatientId();
        
        // 更新の場合
        if (test != null && (!test.equals(""))) {
            
            PatientModel exist = (PatientModel) em
                    .createQuery("from PatientModel p where p.facilityId = :fid and p.patientId = :pid")
                    .setParameter("fid", facilityId)
                    .setParameter("pid", patient.getPatientId())
                    .getSingleResult();
            
            // 更新する
            PatientModel update = em.merge(patient);
            
            KarteBean karte = (KarteBean) em
                       .createQuery("from KarteBean k where k.patient.id = :pk")
                       .setParameter("pk", update.getId())
                       .getSingleResult();

            // 関係を構築する
            model.setKarte(karte);
            FirstEncounter0Model updateF = em.merge(model);
            ret[0] = new Long(update.getId());
            ret[1] = new Long(updateF.getId());
            ret[2] = patient.getPatientId();
            
        } else {
            
            // 新規登録の場合
            // 患者IDを発番する
            java.math.BigInteger nextId = (java.math.BigInteger) em.createNativeQuery("select nextval('toutou_id') as n")
                                   .getSingleResult();
            
            // 新規患者なので永続化する
            // 患者IDを自動発番する
            Long lvalue = new Long(nextId.longValue());
            patient.setPatientId(paddId(lvalue));
            em.persist(patient);
            
            // この患者のカルテを生成する
            KarteBean karte = new KarteBean();
            karte.setPatient(patient);
            if (model.getConfirmed() != null) {
                karte.setCreated(model.getConfirmed());
            } else {
                karte.setCreated(new Date());
            }
            em.persist(karte);
            
            // 初診時情報を保存する
            // Creator は生成時につけてある
            model.setKarte(karte);
            em.persist(model);
            
            ret[0] = new Long(patient.getId()); // 患者PK
            ret[1] = new Long(model.getId());   // 初診時情報PK
            ret[2] = patient.getPatientId();    // 患者ID
        }
        
        return ret;
    }
    
    
    /**
     * 初診時情報を保存または更新する。 
     */
    @Override
    public long saveOrUpdateFirstEncounter(FirstEncounterModel model) {
        
        // 保存又は更新する
        FirstEncounterModel saveOrUpdate = em.merge(model);
        
        // 初診時情報2の時
        if (model instanceof FirstEncounter2Model) {
        
            // 画像が更新される可能性があるので、古いものは削除する
            List<CompositeImageModel> oldImages = (List<CompositeImageModel>) 
                    em.createQuery("from CompositeImageModel c where compositor = :cid")
                      .setParameter("cid", saveOrUpdate.getId())
                      .getResultList();
            if (oldImages != null && oldImages.size() > 0) {
                for (CompositeImageModel cid : oldImages) {
                    em.remove(cid);
                }
            }

            // 画像がついていれば保存する
            Collection<CompositeImageModel> newImages = ((FirstEncounter2Model)model).getCompositeImages();
            if (newImages != null && newImages.size() > 0) {
                for (CompositeImageModel cim : newImages) {
                    cim.setCompositor(saveOrUpdate.getId());
                    em.merge(cim);
                }
            }
        }
        
        return saveOrUpdate.getId();
    }
    
    
    /**
     * 初診時情報を取得する。
     * @param karteId karte PK
     * @param docType 種類
     */
    @Override
    public List<FirstEncounterModel> getFirstEncounter(long karteId, String docType) {
        
        if (docType.equals("SETA_0")) {
            List<FirstEncounterModel> ret = (List<FirstEncounterModel>)
                        em.createQuery("from FirstEncounter0Model f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
                        .getResultList();
            return ret;
            
        }
        else if (docType.equals("SETA_1")) {
            List<FirstEncounterModel> ret = (List<FirstEncounterModel>)
                        em.createQuery("from FirstEncounter1Model f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
                        .getResultList();
            return ret;
            
        } 
        else if (docType.equals("SETA_2")) {
            List<FirstEncounterModel> ret = (List<FirstEncounterModel>)
                        em.createQuery("from FirstEncounter2Model f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
                        .getResultList();
            if (ret != null && ret.size() > 0) {
                FirstEncounter2Model fe2 = (FirstEncounter2Model) ret.get(0);
                List<CompositeImageModel> images = (List<CompositeImageModel>)
                            em.createQuery("from CompositeImageModel c where compositor = :cid")
                            .setParameter("cid", fe2.getId())
                            .getResultList();
                fe2.setCompositeImages(images);
            }
            return ret;
        }
        
        return null;
    }
    
    /**
     * 紹介状を保存または更新する。
     */
    @Override
    public long saveOrUpdateLetter(LetterModel model) {
        LetterModel saveOrUpdate = em.merge(model);
        return saveOrUpdate.getId();
    }
    
    /**
     * 紹介状のリストを取得する。
     */
    @Override
    public List<LetterModel> getLetters(long karteId, String docType) {
        if (docType.equals("TOUTOU")) {
            List<LetterModel> ret = (List<LetterModel>)
                        em.createQuery("from TouTouLetter f where f.karte.id = :karteId")
                        .setParameter("karteId", karteId)
                        .getResultList();
            return ret;   
        }
        
        return null;
    }
    
    /**
     * 紹介状を取得する。
     */
    @Override
    public LetterModel getLetter(long letterPk) {
        
        LetterModel ret = (LetterModel)
                        em.createQuery("from TouTouLetter t where t.id = :id")
                        .setParameter("id", letterPk)
                        .getSingleResult();
        return ret;
    }
    
    /**
     * 
     * @return
     */
    @Override
    public String getNextPatientId() {
        
        Long next = (Long) em.createQuery("select nextval('toutou_id') as n")
                          .getSingleResult();
        
        String ret = String.valueOf(next);
        int length = ret.length();
        if (length < 5) {
            int n = 5-length;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append("0");
            }
            sb.append(ret);
            ret = sb.toString();
        }
        
        return ret;
        
    }
    
    private String paddId(Long id) {
        if (id != null) {
            String ret = String.valueOf(id);
            int length = ret.length();
            if (length < 5) {
                int n = 5-length;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; i++) {
                    sb.append("0");
                }
                sb.append(ret);
                ret = sb.toString();
            }

            return ret;
        }
        return null;
    }
}














