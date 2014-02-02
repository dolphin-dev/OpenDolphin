package open.dolphin.ejb;

import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientModel;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteNLaboService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteNLaboService")
public class RemoteNLaboServiceImpl implements RemoteNLaboService {
    
    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;


    //
    // ログインユーザーの施設ID部分を返す。
    //
    private String getCallersFacilityId(SessionContext ctx) {
        String callerId = ctx.getCallerPrincipal().getName();
        int index = callerId.indexOf(":");
        return index > 0 ? callerId.substring(0, index) : callerId;
    }

    //
    // 患者IDを施設ID:患者IDの形にする。
    //
    private String getFidPid(String pid) {
        StringBuilder sb = new StringBuilder();
        sb.append(getCallersFacilityId(ctx));
        sb.append(":");
        sb.append(pid);
        return sb.toString();
    }


    @Override
    public PatientModel create(NLaboModule module) {

        String facilityId = this.getCallersFacilityId(ctx);
        String patientId = module.getPatientId();

        // 施設IDと LaboModule の患者IDで 患者を取得する
        PatientModel patient = (PatientModel) em
                .createQuery("from PatientModel p where p.facilityId=:fid and p.patientId=:pid")
                .setParameter("fid", facilityId)
                .setParameter("pid", patientId)
                .getSingleResult();

        // FacilityId:PatientId の形にする
        String fidPid = getFidPid(patientId);
        module.setPatientId(fidPid);

        // item の patientId を変更する
        Collection<NLaboItem> items = module.getItems();
        for (NLaboItem item : items) {
            item.setPatientId(fidPid);
        }

        //
        // patientId & 検体採取日 & ラボコード で key
        // これが一致しているモジュールは再報告として削除してから登録する。
        //
        String sampleDate = module.getSampleDate();
        String laboCode = module.getLaboCenterCode();

        NLaboModule exist = null;

        try {

            exist = (NLaboModule)
                    em.createQuery("from NLaboModule m where m.patientId=:fidPid and m.sampleDate=:sampleDate and m.laboCenterCode=:laboCode")
                      .setParameter("fidPid", fidPid)
                      .setParameter("sampleDate", sampleDate)
                      .setParameter("laboCode", laboCode)
                      .getSingleResult();

        } catch (Exception e) {
            exist = null;
        }

        // Cascade.TYPE=ALL
        if (exist != null) {
            em.remove(exist);
        }

        // 永続化する
        em.persist(module);

        return patient;
    }


    /**
     * ラボモジュールを検索する。
     * @param patientId     対象患者のID
     * @param firstResult   取得結果リストの最初の番号
     * @param maxResult     取得する件数の最大値
     * @return              ラボモジュールのリスト
     */
    @Override
    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult) {

        String fidPid = getFidPid(patientId);

        //
        // 検体採取日の降順で返す
        //
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


    /**
     * 指定された検査項目を検索する。
     * @param patientId     患者ID
     * @param firstResult   最初の結果
     * @param maxResult     戻す件数の最大値
     * @param itemCode      検索する検査項目コード
     * @return              検査項目コードが降順に格納されたリスト
     */
    @Override
    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, String itemCode) {

        String fidPid = getFidPid(patientId);

        List<NLaboItem> ret = (List<NLaboItem>)
                        em.createQuery("from NLaboItem l where l.patientId=:fidPid and l.itemCode=:itemCode order by l.sampleDate desc")
                          .setParameter("fidPid", fidPid)
                          .setParameter("itemCode", itemCode)
                          .setFirstResult(firstResult)
                          .setMaxResults(maxResult)
                          .getResultList();

        return ret;
    }
}
