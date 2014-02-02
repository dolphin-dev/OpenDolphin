package open.dolphin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import open.dolphin.dto.AppointSpec;
import open.dolphin.dto.ModuleSearchSpec;
import open.dolphin.infomodel.AppointmentModel;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteAppoService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteAppoService")
public class RemoteAppoServiceImple extends DolphinService implements RemoteAppoService {
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 予約を保存、更新、削除する。
     * @param spec 予約情報の DTO
     */
    public void putAppointments(AppointSpec spec) {
        
        Collection added = spec.getAdded();
        Collection updated = spec.getUpdared();
        Collection removed = spec.getRemoved();
        AppointmentModel av = null;
        
        // 登録する
        if (added != null && added.size() > 0 ) {
            Iterator iter = added.iterator();
            while(iter.hasNext()) {
                av = (AppointmentModel)iter.next();
                checkIdAsComposite(ctx, av.getPatientId());
                em.persist(av);
            }
        }
        
        // 更新する
        if (updated != null && updated.size() > 0 ) {
            Iterator iter = updated.iterator();
            while(iter.hasNext()) {
                av = (AppointmentModel)iter.next();
                checkIdAsComposite(ctx, av.getPatientId());
                // av は分離オブジェクトである
                em.merge(av);
            }
        }
        
        // 削除
        if (removed != null && removed.size() > 0 ) {
            Iterator iter = removed.iterator();
            while(iter.hasNext()) {
                av = (AppointmentModel)iter.next();
                checkIdAsComposite(ctx, av.getPatientId());
                // 分離オブジェクトは remove に渡せないので対象を検索する
                AppointmentModel target = (AppointmentModel)em.find(AppointmentModel.class, av.getId());
                em.remove(target);
            }
        }
    }
    
    /**
     * 予約を検索する。
     * @param spec 検索仕様
     * @return 予約の Collection
     */
    public Collection getAppointmentList(ModuleSearchSpec spec) {
        
        // 検索する患者の Composite Key
        String pcid = checkIdAsComposite(ctx, spec.getPatientId());
        
        // 抽出期間は別けられている
        Date[] fromDate = spec.getFromDate();
        Date[] toDate = spec.getToDate();
        int len = fromDate.length;
        ArrayList<Collection> ret = new ArrayList<Collection>(len);
        
        // 抽出期間ごとに検索しコレクションに加える
        for (int i = 0; i < len; i++) {
            
            Collection c = em.createQuery("appoByPatient")
            .setParameter("pid", pcid)
            .setParameter("from", fromDate[i])
            .setParameter("to", toDate[i])
            .getResultList();
            ret.add(c);
        }
        
        return ret;
    }
}
