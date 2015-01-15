package open.dolphin.session;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class UserServiceBean {

    private static final String QUERY_USER_BY_UID = "from UserModel u where u.userId=:uid";
    private static final String QUERY_USER_BY_FID_MEMBERTYPE = "from UserModel u where u.userId like :fid and u.memberType!=:memberType";

    private static final String UID = "uid";
    private static final String FID = "fid";
    private static final String MEMBER_TYPE = "memberType";
    private static final String MEMBER_TYPE_EXPIRED = "EXPIRED";

    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;

    
    public boolean authenticate(String userName, String password) {

        boolean ret = false;

        try {
            UserModel user = (UserModel)
                em.createQuery(QUERY_USER_BY_UID)
                  .setParameter(UID, userName)
                  .getSingleResult();
            if (user.getPassword().equals(password)) {
                ret = true;
            }

        } catch (Exception e) {
        }

        return ret;
    }

    /**
     * 施設管理者が院内Userを登録する。
     * @param add 登録するUser
     */
    
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
    
    public UserModel getUser(String uid) {
        UserModel user = (UserModel)
                em.createQuery(QUERY_USER_BY_UID)
                  .setParameter(UID, uid)
                  .getSingleResult();

        if (user.getMemberType() != null && user.getMemberType().equals(MEMBER_TYPE_EXPIRED)) {
            throw new SecurityException("Expired User");
        }
        return user;
    }

    /**
     * 施設内の全Userを取得する。
     *
     * @return 施設内ユーザリスト
     */
    
    public List<UserModel> getAllUser(String fid) {

        List<UserModel> results =
                (List<UserModel>)em.createQuery(QUERY_USER_BY_FID_MEMBERTYPE)
                                         .setParameter(FID, fid+":%")
                                         .setParameter(MEMBER_TYPE, MEMBER_TYPE_EXPIRED)
                                         .getResultList();
        return results;

//        Collection<UserModel> ret = new ArrayList<UserModel>();
//        for (Iterator iter = results.iterator(); iter.hasNext(); ) {
//            UserModel user = (UserModel) iter.next();
//            if (user.getMemberType() != null && (!user.getMemberType().equals("EXPIRED"))) {
//                ret.add(user);
//            }
//        }
//        return ret;
    }

    /**
     * User情報(パスワード等)を更新する。
     * @param update 更新するUser detuched
     */
    
    public int updateUser(UserModel update) {
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
    
    public int removeUser(String removeId) {

        //
        // 削除するユーザを得る
        //
        UserModel remove = getUser(removeId);

        // Stamp を削除する
        Collection<StampModel> stamps = (Collection<StampModel>) em.createQuery("from StampModel s where s.userId = :pk")
                                                                   .setParameter("pk", remove.getId())
                                                                   .getResultList();
        for (StampModel stamp : stamps) {
            em.remove(stamp);
        }

        // Subscribed Tree を削除する
        Collection<SubscribedTreeModel> subscribedTrees = (Collection<SubscribedTreeModel>)
                                                          em.createQuery("from SubscribedTreeModel s where s.user.id = :pk")
                                                            .setParameter("pk", remove.getId())
                                                            .getResultList();
        for (SubscribedTreeModel tree : subscribedTrees) {
            em.remove(tree);
        }

        // PublishedTree を削除する
        Collection<PublishedTreeModel> publishedTrees = (Collection<PublishedTreeModel>)
                                                         em.createQuery("from PublishedTreeModel p where p.user.id = :pk")
                                                           .setParameter("pk", remove.getId())
                                                           .getResultList();
        for (PublishedTreeModel tree : publishedTrees) {
            em.remove(tree);
        }

        // PersonalTreeを削除する
        Collection<StampTreeModel> stampTree = (Collection<StampTreeModel>) em.createQuery("from StampTreeModel s where s.user.id = :pk")
                                                      .setParameter("pk", remove.getId())
                                                      .getResultList();
        for (StampTreeModel tree : stampTree) {
            em.remove(tree);
        }

        //
        // ユーザを削除する
        //
        if (remove.getLicenseModel().getLicense().equals("doctor")) {
            StringBuilder sb = new StringBuilder();
            remove.setMemo(sb.toString());
            remove.setMemberType(MEMBER_TYPE_EXPIRED);
            remove.setPassword("c9dbeb1de83e60eb1eb3675fa7d69a02");
        } else {
            em.remove(remove);
        }

        return 1;
    }

    /**
     * 施設情報を更新する。
     * @param update 更新するUser detuched
     */
    
    public int updateFacility(UserModel update) {
        FacilityModel updateFacility = update.getFacilityModel();
        FacilityModel current = (FacilityModel) em.find(FacilityModel.class, updateFacility.getId());
        updateFacility.setMemberType(current.getMemberType());
        updateFacility.setRegisteredDate(current.getRegisteredDate());
        em.merge(updateFacility );
        return 1;
    }
    
//s.oh^ Xronos連携(ユーザー連携)
    public int modifyUser(UserModel add, String newPid, boolean remove) {
        int ret = 0;
        try {
            UserModel update = (UserModel)em.createQuery(QUERY_USER_BY_UID).setParameter(UID, add.getUserId()).getSingleResult();
            if(remove) {
                removeUser(update.getUserId());
                Logger.getLogger("open.dolphin").info("Xronos:Delete");
            }else{
                update.setMemberType("ASP_MEMBER");
                update.setUserId(newPid);
                update.setPassword(add.getPassword());
                update.setSirName(add.getSirName());
                update.setGivenName(add.getGivenName());
                update.setCommonName(add.getCommonName());
                em.merge(update);
                Logger.getLogger("open.dolphin").info("Xronos:Update");
            }
            return 1;
        } catch (NoResultException e) {
        }
        if(!remove) {
            em.persist(add);
            Logger.getLogger("open.dolphin").info("Xronos:Add");
            ret = 1;
        }
        return ret;
    }
    
    public UserModel getUserById(long id) {
        UserModel user = null;
        try{
            user = (UserModel)em.createQuery("from UserModel u where u.id=:pk").setParameter("pk", id).getSingleResult();
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "getUserById({0}):{1}", new Object[]{String.valueOf(id), e.getMessage()});
        }
        if(user != null && user.getMemberType() != null && user.getMemberType().equals(MEMBER_TYPE_EXPIRED)) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "MemberType = EXPIRED");
            return null;
        }
        return user;
    }
    
    public FacilityModel getFacilityInfo(String facilityid) {
        FacilityModel facility = null;
        try{
            facility = (FacilityModel)em.createQuery("from FacilityModel f where f.facilityId=:fid").setParameter("fid", facilityid).getSingleResult();
            Logger.getLogger("open.dolphin").log(Level.INFO, "Get FacilityModel:" + facilityid);
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "getFacilityInfo({0}):{1}", new Object[]{facilityid, e.getMessage()});
        }
        return facility;
    }
//s.oh$
}
