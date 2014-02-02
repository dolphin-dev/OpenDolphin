package open.dolphin.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import open.dolphin.dto.PatientVisitSpec;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * RemotePvtServiceImpl
 *
 * @author Minagawa,Kazushi
 *
 */
@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemotePvtService.class})
@RemoteBinding(jndiBinding="openDolphin/RemotePvtService")
public class  RemotePvtServiceImpl extends DolphinService implements RemotePvtService {
    
    private static final long serialVersionUID = -3889943133781444449L;
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 患者来院情報を登録する。
     * @param spec 来院情報を保持する DTO オブジェクト
     * @return 登録個数
     */
    public int addPvt(PatientVisitModel pvt) {
        
        PatientModel patient = pvt.getPatient();
        
        // CLAIM 送信の場合 facilityID がデータベースに登録されているものと異なる場合がある
        // 施設IDを認証にパスしたユーザの施設IDに設定する。
        String facilityId = getCallersFacilityId(ctx);
        pvt.setFacilityId(facilityId);
        patient.setFacilityId(facilityId);
        
        // 既存の患者かどうか調べる
        try {
            PatientModel exist = (PatientModel) em
                    .createQuery("from PatientModel p where p.facilityId = :fid and p.patientId = :pid")
                    .setParameter("fid", facilityId)
                    .setParameter("pid", patient.getPatientId())
                    .getSingleResult();
            
            //
            // 健康保険情報を更新する
            //
            Collection<HealthInsuranceModel> ins = patient.getHealthInsurances();
            if (ins != null && ins.size() > 0) {
            
                // 健康保険を更新する
                Collection old = em.createQuery("from HealthInsuranceModel h where h.patient.id = :pk")
                .setParameter("pk", exist.getId())
                .getResultList();

                // 現在の保険情報を削除する
                for (Iterator iter = old.iterator(); iter.hasNext(); ) {
                    HealthInsuranceModel model = (HealthInsuranceModel) iter.next();
                    em.remove(model);
                }

                // 新しい健康保険情報を登録する
                Collection<HealthInsuranceModel> newOne = patient.getHealthInsurances();
                for (HealthInsuranceModel model : newOne) {
                    model.setPatient(exist);
                    em.persist(model);
                }
            }
            
            // 名前を更新する 2007-04-12
            exist.setFamilyName(patient.getFamilyName());
            exist.setGivenName(patient.getGivenName());
            exist.setFullName(patient.getFullName());
            exist.setKanaFamilyName(patient.getKanaFamilyName());
            exist.setKanaGivenName(patient.getKanaGivenName());
            exist.setKanaName(patient.getKanaName());
            exist.setRomanFamilyName(patient.getRomanFamilyName());
            exist.setRomanGivenName(patient.getRomanGivenName());
            exist.setRomanName(patient.getRomanName());
            
            // 性別
            exist.setGender(patient.getGender());
            exist.setGenderDesc(patient.getGenderDesc());
            exist.setGenderCodeSys(patient.getGenderCodeSys());
            
            // Birthday
            exist.setBirthday(patient.getBirthday());
            
            // 住所、電話を更新する
            exist.setAddress(patient.getAddress());
            exist.setTelephone(patient.getTelephone());
            //exist.setMobilePhone(patient.getMobilePhone());
            
            // PatientVisit との関係を設定する
            pvt.setPatient(exist);
            
        } catch (NoResultException e) {
            // 新規患者であれば登録する
            // 患者属性は cascade=PERSIST で自動的に保存される
            em.persist(patient);
            
            // この患者のカルテを生成する
            KarteBean karte = new KarteBean();
            karte.setPatient(patient);
            karte.setCreated(new Date());
            em.persist(karte);
        }
        
        // 来院情報を登録する
        // CLAIM の仕様により患者情報のみを登録し、来院情報はない場合がある
        // それを pvtDate の属性で判断している
        if (pvt.getPvtDate() != null) {
            em.persist(pvt);
        }
        
        return 1;
    }
    
    /**
     * 施設の患者来院情報を取得する。
     * @param spec 検索仕様DTOオブジェクト
     * @return 来院情報のCollection
     */
    @SuppressWarnings("unchecked")
    public Collection<PatientVisitModel> getPvt(PatientVisitSpec spec) {
        
        String date = spec.getDate();
        if (!date.endsWith("%")) {
            date = date + "%";
        }
        int index = date.indexOf('%');
        Date theDate = ModelUtils.getDateAsObject(date.substring(0, index));
        int firstResult = spec.getSkipCount();
        String fid = getCallersFacilityId(ctx);
        
        String appoDateFrom = spec.getAppodateFrom();
        String appoDateTo = spec.getAppodateTo();
        boolean searchAppo = (appoDateFrom != null && appoDateTo != null) ? true : false;
        
        // PatientVisitModelを施設IDで検索する
        Collection result = em.createQuery("from PatientVisitModel p where p.facilityId = :fid and p.pvtDate >= :date order by p.pvtDate")
                              .setFirstResult(firstResult)
                              .setParameter("fid", fid)
                              .setParameter("date", date)
                              .getResultList();
        
        // 患者の基本データを取得する
        // 来院情報と患者は ManyToOne の関係である
        for (Iterator iter = result.iterator(); iter.hasNext(); ) {
            
            PatientVisitModel pvt = (PatientVisitModel) iter.next();
            PatientModel patient = pvt.getPatient();
            
            // 患者の健康保険を取得する
            Collection insurances = em.createQuery("from HealthInsuranceModel h where h.patient.id = :pk")
            .setParameter("pk", patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);
            
            // その他のIDを取得する
//            Collection otherIds = em.createQuery("from OtherIdModel o where o.patient.id = :pk")
//            .setParameter("pk", patient.getId()).getResultList();
//            patient.setOtherIds(otherIds);
            
            // 予約を検索する
            if (searchAppo) {
                KarteBean karte = (KarteBean)em.createQuery("from KarteBean k where k.patient.id = :pk")
                .setParameter("pk", patient.getId())
                .getSingleResult();
                // カルテの PK を得る
                long karteId = karte.getId();
                
                List c = em.createQuery("from AppointmentModel a where a.karte.id = :karteId and a.date = :date")
                .setParameter("karteId", karteId)
                .setParameter("date", theDate)
                .getResultList();
                if (c != null && c.size() > 0) {
                    AppointmentModel appo = (AppointmentModel) c.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }
        
        return result;
    }
    
    /**
     * 受付情報を削除する。
     * @param id 受付レコード
     * @return 削除件数
     */
    public int removePvt(long id) {
        
        try {
            PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(id));
            em.remove(exist);
            return 1;
        } catch (Exception e) {
        }
        return 0;
    }
    
    /**
     * 診察終了情報を書き込む。
     * @param pk レコードID
     * @param state 診察終了の時 1
     */
    public int updatePvtState(long pk, int state) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));
        exist.setState(state);
        return 1;
    }
}
