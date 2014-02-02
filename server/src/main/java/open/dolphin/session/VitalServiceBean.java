/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.session;

import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
@Named
@Stateless
public class VitalServiceBean {

    private static final String QUERY_VITAL_BY_FPID = "from VitalModel v where v.facilityPatId=:fpid";
    private static final String QUERY_VITAL_BY_ID = "from VitalModel v where v.id=:id";

    private static final String ID = "id";
    private static final String FPID = "fpid";

    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;

    /**
     * バイタルを登録する。
     * @param add 登録するバイタル
     */
    public int addVital(VitalModel add) {
        em.persist(add);
        return 1;
    }

    /**
     * バイタル情報を更新する。
     * @param update 更新するVital detuched
     */
    public int updateVital(VitalModel update) {
        VitalModel current = (VitalModel) em.find(VitalModel.class, update.getId());
        if(current == null) {
            return 0;
        }
        em.merge(update);
        return 1;
    }

    /**
     * バイタルを検索する。
     * @param id 検索するバイタルID
     * @return 該当するバイタル
     */
    public VitalModel getVital(String id) {
        VitalModel vital
                = (VitalModel)em.createQuery(QUERY_VITAL_BY_ID)
                .setParameter(ID, Long.parseLong(id))
                .getSingleResult();

        return vital;
    }

    /**
     * バイタルを検索する。
     * @param fpid 検索する施設ID:患者ID
     * @return 該当するバイタル
     */
    public List<VitalModel> getPatVital(String fpid) {
        List<VitalModel> results
                = (List<VitalModel>)em.createQuery(QUERY_VITAL_BY_FPID)
                .setParameter(FPID, fpid)
                .getResultList();

        return results;
    }

    /**
     * バイタルを削除する。
     * @param id 削除するバイタルのID
     */
    public int removeVital(String id) {
        VitalModel remove = getVital(id);
        em.remove(remove);

        return 1;
    }
    
    public PatientModel getPatientByFpid(String fpid){
        
        String[] vals = fpid.split(":");
        PatientModel p = (PatientModel)em.createQuery("from PatientModel p where p.facilityId=:facilityId and p.patientId=:patientId")
                            .setParameter("facilityId",vals[0])
                            .setParameter("patientId", vals[1])
                            .getSingleResult();

        return p;
        
    }
}
