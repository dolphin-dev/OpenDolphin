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
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import open.dolphin.infomodel.UserModel;

@Stateless
@SecurityDomain("openDolphin")
@Remote({RemoteUserService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteUserService")
public class RemoteUserServiceImpl extends DolphinService implements RemoteUserService {
    
    private static final String ASP_MEMBER = "ASP_MEMBER";
    private static final String QUEUE_JNDI = "queue/tutorial/example";
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 施設管理者が院内Userを登録する。
     * @param add 登録するUser
     */
    @RolesAllowed("admin")
    public int addUser(UserModel add) {
        
        try {
            // 既存ユーザの場合は例外をスローする
            getUser(add.getUserId());
            throw new EntityExistsException();
        } catch (NoResultException e) {
        }
        em.persist(add);
        return 1;
    }
    
    /**
     * Userを検索する。
     * @param userId 検索するユーザの複合キー
     * @return 該当するUser
     */
    @RolesAllowed("user")
    public UserModel getUser(String userId) {
        checkIdAsComposite(ctx, userId);
        UserModel user = (UserModel) em.createQuery("from UserModel u where u.userId = :uid")
        .setParameter("uid", userId)
        .getSingleResult();
        
        if (user.getMemberType() != null && user.getMemberType().equals("EXPIRED")) {
            throw new SecurityException("Expired User");
        }
        
        return user;
    }
    
    /**
     * 施設内の全Userを取得する。
     *
     * @return 施設内ユーザリスト
     */
    @SuppressWarnings("unchecked")
    @RolesAllowed("admin")
    public Collection<UserModel> getAllUser() {
        Collection results = em.createQuery("from UserModel u where u.userId like :fid")
        .setParameter("fid", getCallersFacilityId(ctx)+"%")
        .getResultList();
        
        Collection<UserModel> ret = new ArrayList<UserModel>();
        for (Iterator iter = results.iterator(); iter.hasNext(); ) {
            UserModel user = (UserModel) iter.next();
            if (user != null && user.getMemberType() != null && (!user.getMemberType().equals("EXPIRED"))) {
                ret.add(user);
            }
        }
        
        return ret;
    }
    
    /**
     * User情報(パスワード等)を更新する。
     * @param update 更新するUser detuched
     */
    @RolesAllowed("user")
    public int updateUser(UserModel update) {
        //checkFacility(ctx, update.getUserId());
        UserModel current = (UserModel) em.find(UserModel.class, update.getId());
        update.setMemberType(current.getMemberType());
        update.setRegisteredDate(current.getRegisteredDate());
        em.merge(update);
        return 1;
    }
    
    /**
     * Userを削除する。
     * @param removeId 削除するユーザのId
     */
    @RolesAllowed("admin")
    public int removeUser(String removeId) {
        
        UserModel remove = getUser(removeId);
        long removePk = remove.getId();
        
        // Stamp を削除する
        Collection<StampModel> stamps = (Collection<StampModel>) em.createQuery("from StampModel s where s.userId = :pk")
        .setParameter("pk", removePk)
        .getResultList();
        for (StampModel stamp : stamps) {
            em.remove(stamp);
        }
        
        // Subscribed Tree を削除する
        Collection<SubscribedTreeModel> subscribedTrees = (Collection<SubscribedTreeModel>)
        em.createQuery("from SubscribedTreeModel s where s.user.id = :pk")
        .setParameter("pk", removePk)
        .getResultList();
        for (SubscribedTreeModel tree : subscribedTrees) {
            em.remove(tree);
        }
        
        // PublishedTree を削除する
        Collection<PublishedTreeModel> publishedTrees = (Collection<PublishedTreeModel>)
        em.createQuery("from PublishedTreeModel p where p.user.id = :pk")
        .setParameter("pk", removePk)
        .getResultList();
        for (PublishedTreeModel tree : publishedTrees) {
            em.remove(tree);
        }
        
        // PersonalTreeを削除する
        try {
            StampTreeModel stampTree = (StampTreeModel) em.createQuery("from StampTreeModel s where s.user.id = :pk")
            .setParameter("pk", removePk)
            .getSingleResult();
            em.remove(stampTree);
        } catch (Exception e) {
            
        }
        
        //
        // ユーザを削除する
        //
        if (remove.getLicenseModel().getLicense().equals("doctor")) {
            StringBuilder sb = new StringBuilder();
            sb.append(new Date());
            String note = sb.toString();
            remove.setMemo(note);
            remove.setPassword("c9dbeb1de83e60eb1eb3675fa7d69a02");
            remove.setMemberType("EXPIRED");
        } else {
            em.remove(remove);
        }
            
        boolean deleteDoc = false;
        if (deleteDoc) {            
            
            //
            // Document, Module, Image (Cascade)
            //
            Collection<DocumentModel> documents = (Collection<DocumentModel>) 
                                    em.createQuery("from DocumentModel d where d.creator.id = :removeId")
                                      .setParameter("removeId", removePk).getResultList();

            System.out.println(documents.size() + " 件のドキュメントを削除します。");
            //
            // Document を削除すれば ModuleとImageはカスケード削除される
            //
            for (DocumentModel document : documents) {
                em.remove(document);
            }


            //
            // Diagnosis
            //
            Collection<RegisteredDiagnosisModel> rds = (Collection<RegisteredDiagnosisModel>) 
                                                        em.createQuery("from RegisteredDiagnosisModel d where d.creator.id = :removeId")
                                                          .setParameter("removeId", removePk)
                                                          .getResultList();
            System.out.println(rds.size() + " 件の傷病名を削除します。");
            for (RegisteredDiagnosisModel rd : rds) {
                em.remove(rd);
            } 


            //
            // Observation
            //
            Collection<ObservationModel> observations = (Collection<ObservationModel>) 
                                                        em.createQuery("from ObservationModel o where o.creator.id = :removeId")
                                                          .setParameter("removeId", removePk)
                                                          .getResultList();
            System.out.println(observations.size() + " 件の観測を削除します。");
            for (ObservationModel observation : observations) {
                em.remove(observation);
            }
            
            //
            // 患者メモ
            //
            Collection<PatientMemoModel> memos = (Collection<PatientMemoModel>) 
                                                        em.createQuery("from PatientMemoModel o where o.creator.id = :removeId")
                                                          .setParameter("removeId", removePk)
                                                          .getResultList();
            System.out.println(memos.size() + " 件の患者メモを削除します。");
            for (PatientMemoModel memo : memos) {
                em.remove(memo);
            }       
            
            
            //
            // 予約
            //
            Collection<AppointmentModel> appos = (Collection<AppointmentModel>) 
                                                        em.createQuery("from AppointmentModel o where o.creator.id = :removeId")
                                                          .setParameter("removeId", removePk)
                                                          .getResultList();
            System.out.println(appos.size() + " 件の予約を削除します。");
            for (AppointmentModel appo : appos) {
                em.remove(appo);
            }  
            
            
            //
            // ラボ
            //
            Collection<LaboModuleValue> labos = (Collection<LaboModuleValue>) 
                                                        em.createQuery("from LaboModuleValue o where o.creator.id = :removeId")
                                                          .setParameter("removeId", removePk)
                                                          .getResultList();
            System.out.println(labos.size() + " 件のラボを削除します。");
            for (LaboModuleValue lb : labos) {
                em.remove(lb);
            }              

            em.remove(remove);
            
        }
        
        return 1;
    }
    
    /**
     * 施設情報を更新する。
     * @param update 更新するUser detuched
     */
    @RolesAllowed("admin")
    public int updateFacility(UserModel update) {
        //checkFacility(ctx, update.getUserId());
        FacilityModel updateFacility = update.getFacilityModel();
        FacilityModel current = (FacilityModel) em.find(FacilityModel.class, updateFacility.getId());
        updateFacility.setMemberType(current.getMemberType());
        updateFacility.setRegisteredDate(current.getRegisteredDate());
        em.merge(updateFacility );
        return 1;
    }
}