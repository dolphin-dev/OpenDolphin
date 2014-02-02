package open.dolphin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.PatientVisitModel;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.PatientModel;

@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemotePatientService.class})
@RemoteBinding(jndiBinding="openDolphin/RemotePatientService")
public class RemotePatientServiceImpl extends DolphinService implements RemotePatientService {
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 患者オブジェクトを取得する。
     * @param spec PatientSearchSpec 検索仕様
     * @return 患者オブジェクトの Collection
     */
    @Override
    public Collection getPatients(PatientSearchSpec spec) {
        
        Collection ret = null;
        String fid = getCallersFacilityId(ctx);
        
        switch (spec.getCode()) {
            
            case PatientSearchSpec.DATE_SEARCH:
                Collection c = em.createQuery("from PatientVisitModel p where p.facilityId = :fid and p.pvtDate like :date")
                                 .setParameter("fid", fid)
                                 .setParameter("date", spec.getDigit()+ "%")
                                 .getResultList();
                ret = new ArrayList();
                for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                    PatientVisitModel pvt = (PatientVisitModel) iter.next();
                    PatientModel patient = pvt.getPatient();
                    ret.add(patient);
                }
                
                break;
            
            case PatientSearchSpec.ID_SEARCH:
                String pid = spec.getPatientId();
                if (!pid.endsWith("%")) {
                    pid +="%";
                }
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.patientId like :pid")
                .setMaxResults(50)
                .setParameter("fid", fid)
                .setParameter("pid", pid)
                .getResultList();
                break;
                
            case PatientSearchSpec.NAME_SEARCH:
                String name = spec.getName();

                // 前方一致検索を行う
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.fullName like :name")
                .setParameter("fid", fid)
                .setParameter("name", name + "%")
                .getResultList();

                // 後方一致検索を行う
                if (ret.size() == 0) {
                    ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.fullName like :name")
                        .setParameter("fid", fid)
                        .setParameter("name", "%" + name)
                        .getResultList();
                }
                break;
                
            case PatientSearchSpec.KANA_SEARCH:
                name = spec.getName();

                // 姓に一致
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.kanaName like :name")
                .setParameter("fid", fid)
                .setParameter("name", name + "%")
                .getResultList();

                // 名前に一致
                if (ret.size() == 0) {
                    ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.kanaName like :name")
                        .setParameter("fid", fid)
                        .setParameter("name", "%" + name)
                        .getResultList();
                }
                break;
                
            case PatientSearchSpec.ROMAN_SEARCH:
                name = spec.getName();
                
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.romanName like :name")
                .setParameter("fid", fid)
                .setParameter("name", name + "%")
                .getResultList();
                
                if (ret.size() == 0) {
                    ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.romanName like :name")
                        .setParameter("fid", fid)
                        .setParameter("name", "%" + name)
                        .getResultList();
                }
                break;
                
            case PatientSearchSpec.TELEPHONE_SEARCH:
                String number = spec.getTelephone();
                if (!number.endsWith("%")) {
                    number += "%";
                }
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and (p.telephone like :number or p.mobilePhone like :number)")
                .setParameter("fid", fid)
                .setParameter("number", number)
                .getResultList();
                break;
                
            case PatientSearchSpec.ZIPCODE_SEARCH:
                String zipCode = spec.getZipCode();
                if (!zipCode.endsWith("%")) {
                    zipCode += "%";
                }
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.address.zipCode like :zipCode")
                .setParameter("fid", fid)
                .setParameter("zipCode", zipCode)
                .getResultList();
                break;
                
            case PatientSearchSpec.ADDRESS_SEARCH:
                String address = spec.getAddress();
                if (!address.endsWith("%")) {
                    address += "%";
                }
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.address.address like :address")
                .setParameter("fid", fid)
                .setParameter("address", address)
                .getResultList();
                break;
                
            case PatientSearchSpec.EMAIL_SEARCH:
                address = spec.getEmail();
                if (!address.endsWith("%")) {
                    address += "%";
                }
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.email like :address")
                .setParameter("fid", fid)
                .setParameter("email", address)
                .getResultList();
                break;
                
//            case PatientSearchSpec.OTHERID_SEARCH:
//                String otherId = spec.getOtherId();
//                if (!otherId.endsWith("%")) {
//                    otherId += "%";
//                }
//                ret = em.createQuery("from PatientModel p ,IN (p.otherIds) o where p.facilityId = :fid and o.otherId like :otherId")
//                .setParameter("fid", fid)
//                .setParameter("otherId", otherId)
//                .getResultList();
//                break;
                
            case PatientSearchSpec.DIGIT_SEARCH:
                String digit = spec.getDigit();
                if (!digit.endsWith("%")) {
                    digit += "%";
                }
                // Test ID
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.patientId like :pid")
                .setMaxResults(50)
                .setParameter("fid", fid)
                .setParameter("pid", digit)
                .getResultList();
                if (ret != null && ret.size() > 0) {
                    break;
                }
                // Test Telephone
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and (p.telephone like :number or p.mobilePhone like :number)")
                .setParameter("fid", fid)
                .setParameter("number", digit)
                .getResultList();
                if (ret != null && ret.size() > 0) {
                    break;
                }
                // Test ZipCode
                ret = em.createQuery("from PatientModel p where p.facilityId = :fid and p.address.zipCode like :zipCode")
                .setParameter("fid", fid)
                .setParameter("zipCode", digit)
                .getResultList();
                break;
        }
        
        // 結果がある場合は患者の基本属性を取得して返す
        if (ret != null && ret.size() > 0) {
            
            for (Iterator iter = ret.iterator(); iter.hasNext(); ) {
                
                PatientModel patient = (PatientModel)iter.next();
                
                // 患者の健康保険を取得する
                Collection insurances
                        = em.createQuery("from HealthInsuranceModel h where h.patient.id = :pk")
                        .setParameter("pk", patient.getId()).getResultList();
                patient.setHealthInsurances(insurances);
                
//                // その他のIDを取得する
//                Collection otherIds
//                        = em.createQuery("from OtherIdModel o where o.patient.id = :pk")
//                        .setParameter("pk", patient.getId()).getResultList();
//                patient.setOtherIds(otherIds);
            }
        }
        
        return ret != null ? ret : new ArrayList<PatientModel>(1);
    }
    
    /**
     * 患者ID(BUSINESS KEY)を指定して患者オブジェクトを返す。
     *
     * @param patientId 施設内患者ID
     * @return 該当するPatientModel
     */
    @Override
    public PatientModel getPatient(String patientId) {
        
        String facilityId = getCallersFacilityId(ctx);
        
        // 患者レコードは FacilityId と patientId で複合キーになっている
        PatientModel bean
                = (PatientModel)em.createQuery("from PatientModel p where p.facilityId = :fid and p.patientId = :pid")
                .setParameter("fid", facilityId)
                .setParameter("pid", patientId)
                .getSingleResult();
        
        long pk = bean.getId();
        
        // Lazy Fetch の 基本属性を検索する
        // 患者の健康保険を取得する
        Collection insurances
                = em.createQuery("from HealthInsuranceModel h where h.patient.id = :pk")
                .setParameter("pk", pk).getResultList();
        bean.setHealthInsurances(insurances);
        
        return bean;
    }
    
    /**
     * 患者を登録する。
     * @param patient PatientModel
     * @return データベース Primary Key
     */
    @Override
    public long addPatient(PatientModel patient) {
        String facilityId = getCallersFacilityId(ctx);
        patient.setFacilityId(facilityId);
        em.persist(patient);
        long pk = patient.getId();
        return pk;
    }
    
    /**
     * 患者情報を更新する。
     * @param patient 更新する患者
     * @return 更新数
     */
    @Override
    public int update(PatientModel patient) {
        em.merge(patient);
        return 1;
    }
}
