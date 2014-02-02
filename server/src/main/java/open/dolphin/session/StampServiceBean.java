package open.dolphin.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 *
 * @author kazushi, Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class StampServiceBean {

    private static final String QUERY_TREE_BY_USER_PK = "from StampTreeModel s where s.user.id=:userPK";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK = "from SubscribedTreeModel s where s.user.id=:userPK";
    private static final String QUERY_LOCAL_PUBLISHED_TREE = "from PublishedTreeModel p where p.publishType=:fid";
    private static final String QUERY_PUBLIC_TREE = "from PublishedTreeModel p where p.publishType='global'";
    private static final String QUERY_PUBLISHED_TREE_BY_ID = "from PublishedTreeModel p where p.id=:id";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK_TREE_ID = "from SubscribedTreeModel s where s.user.id=:userPK and s.treeId=:treeId";
    private static final String EXCEPTION_FIRST_COMMIT_WIN = "First Commit Win Exception";

    private static final String USER_PK = "userPK";
    private static final String FID = "fid";
    private static final String TREE_ID = "treeId";
    private static final String ID = "id";
    
    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;
    
    private int getNextVersion(String holdVersion, String dbVersion) {
        
        int newVersion = -1;
        
        if (holdVersion!=null && dbVersion!=null) {
            // 先勝ち 保持しているVersion=DB Version
            if (holdVersion.equals(dbVersion)) {
                newVersion = Integer.parseInt(holdVersion)+1;   // +1
            }
        } else if (holdVersion==null && dbVersion!=null) {
            // あってはいけない
            
        } else if (holdVersion!=null && dbVersion==null) {
            // あってはいけない
            
        } else if (holdVersion==null && dbVersion==null) {
            // 両方とも存在しないケース: 新規にTreeが保存される時
            newVersion = 0;
        }
        
        return newVersion;
    }

    /**
     * user個人のStampTreeを保存/更新する。
     * @param model 保存する StampTree
     * @return id
     */
    public long putTree(StampTreeModel model) {
        
        int vesion;
        
        try {        
            StampTreeModel exist =  (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, model.getUserModel().getId())
                      .getSingleResult();
            
            vesion = getNextVersion(model.getVersionNumber(), exist.getVersionNumber());
            
        } catch (NoResultException e) {
            vesion = 0; 
        } 
        
        if (vesion>=0) {
            // 保存
            model.setVersionNumber(String.valueOf(vesion));
            StampTreeModel saveOrUpdate = em.merge(model);
            return saveOrUpdate.getId();
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }
    
    // pk,versionNumber
    public String syncTree(StampTreeModel model) {
        
        int vesion;
        
        try {        
            StampTreeModel exist =  (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, model.getUserModel().getId())
                      .getSingleResult();
            
            vesion = getNextVersion(model.getVersionNumber(), exist.getVersionNumber());
            
        } catch (NoResultException e) {
            vesion = 0; 
        } 
        
        if (vesion>=0) {
            // 保存
            model.setVersionNumber(String.valueOf(vesion));
            StampTreeModel saveOrUpdate = em.merge(model);
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(saveOrUpdate.getId())).append(",").append(saveOrUpdate.getVersionNumber());
            return sb.toString();
            
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }
    
    // pk,versionNumber
    public void forceSyncTree(StampTreeModel model) {
        em.merge(model);
    }

    /**
     * User個人及びサブスクライブしているTreeを取得する。
     * @param userPk userId(DB key)
     * @return User個人及びサブスクライブしているTreeのリスト
     */
    public StampTreeHolder getTrees(long userPK) {

        StampTreeHolder ret = new StampTreeHolder();

        //-----------------------------------
        // パーソナルツリーを取得する
        //-----------------------------------
        List<StampTreeModel> list = (List<StampTreeModel>)
                em.createQuery(QUERY_TREE_BY_USER_PK)
                  .setParameter(USER_PK, userPK)
                  .getResultList();

        // 新規ユーザの場合
        if (list.isEmpty()) {
            return ret;
        }

        // 最初の Tree を追加
        StampTreeModel st = (StampTreeModel)list.remove(0);
        ret.setPersonalTree(st);

        // まだある場合 BUG
        if (list.size() > 0) {
            // 後は delete する
            for (int i=0; i < list.size(); i++) {
                st = (StampTreeModel) list.remove(0);
                em.remove(st);
            }
        }

        //--------------------------------------------------
        // ユーザがサブスクライブしているStampTreeのリストを取得する
        //--------------------------------------------------
        List<SubscribedTreeModel> subscribed =
            (List<SubscribedTreeModel>)em.createQuery(QUERY_SUBSCRIBED_BY_USER_PK)
                                         .setParameter(USER_PK, userPK)
                                         .getResultList();

        HashMap tmp = new HashMap(5, 0.8f);

        for (SubscribedTreeModel sm : subscribed) {

            // BUG 重複をチェックする
            if (tmp.get(sm.getTreeId()) == null) {

                // まだ存在しない場合
                tmp.put(sm.getTreeId(), "A");

                try {
                    PublishedTreeModel published = (PublishedTreeModel)em.find(PublishedTreeModel.class, sm.getTreeId());

                    if (published != null) {
                        ret.addSubscribedTree(published);

                    } else {
                        em.remove(sm);
                    }

                } catch (NoResultException e) {
                    em.remove(sm);
                }

            } else {
                // 重複してインポートしている場合に削除する
                em.remove(sm);
            }
        }

        return ret;
    }

    // version
    public String updatePublishedTree(StampTreeHolder h) {

        // 個人Tree
        StampTreeModel st = (StampTreeModel)h.getPersonalTree();
        
        // 公開Tree
        PublishedTreeModel pt = (PublishedTreeModel)h.getSubscribedList().get(0);
        
        //-----------------------------------------------------------------------
        // 個人Treeがsyncできないといけない
        //-----------------------------------------------------------------------
        int vesion;
        try {        
            StampTreeModel exist =  (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, st.getUserModel().getId())
                      .getSingleResult();
            
            vesion = getNextVersion(st.getVersionNumber(), exist.getVersionNumber());
            
        } catch (NoResultException e) {
            vesion = 0; 
        } 
        
        if (vesion>=0) {
            // 保存
            st.setVersionNumber(String.valueOf(vesion));
            StampTreeModel saveOrUpdate = em.merge(st);
            
            if (pt.getId()==0L) {
                pt.setId(st.getId());
                em.persist(pt);
            } else {
                em.merge(pt);
            }
        
            // versionNum
            return saveOrUpdate.getVersionNumber();
            
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * 公開したTreeを削除する。
     * @param id 削除するTreeのId
     * @return VersionNumber
     */
    public String cancelPublishedTree(StampTreeModel st) {
        
        //-----------------------------------------------------------------------
        // 個人Treeがsyncできないといけない
        //-----------------------------------------------------------------------
        int vesion;
        try {        
            StampTreeModel exist =  (StampTreeModel)
                    em.createQuery(QUERY_TREE_BY_USER_PK)
                      .setParameter(USER_PK, st.getUserModel().getId())
                      .getSingleResult();
            
            vesion = getNextVersion(st.getVersionNumber(), exist.getVersionNumber());
            
        } catch (NoResultException e) {
            vesion = 0; 
        } 
        
        if (vesion>=0) {
            // 保存
            st.setVersionNumber(String.valueOf(vesion));
            StampTreeModel saveOrUpdate = em.merge(st);
            
            //------------------------
            // 公開Treeを削除する
            //------------------------
            List<PublishedTreeModel> list = em.createQuery(QUERY_PUBLISHED_TREE_BY_ID)
                                              .setParameter(ID, st.getId())
                                              .getResultList();
            for (PublishedTreeModel m : list) {
                em.remove(m);
            }
        
            // versionNum
            return saveOrUpdate.getVersionNumber();
            
        } else {
            throw new RuntimeException(EXCEPTION_FIRST_COMMIT_WIN);
        }
    }

    /**
     * 公開されているStampTreeのリストを取得する。
     * @return ローカル及びパブリックTreeのリスト
     */
    
    public List<PublishedTreeModel> getPublishedTrees(String fid) {

        // ログインユーザの施設IDを取得する
        //String fid = SessionHelper.getCallersFacilityId(ctx);

        List<PublishedTreeModel> ret = new ArrayList<PublishedTreeModel>();

        // local に公開されているTreeを取得する
        // publishType=施設ID
        List locals = em.createQuery(QUERY_LOCAL_PUBLISHED_TREE)
        .setParameter(FID, fid)
        .getResultList();
        ret.addAll((List<PublishedTreeModel>) locals);

        // パブリックTeeを取得する
        List publics = em.createQuery(QUERY_PUBLIC_TREE)
        .getResultList();
        ret.addAll((List<PublishedTreeModel>) publics);

        return ret;
    }

    /**
     * 公開Treeにサブスクライブする。
     * @param addList サブスクライブする
     * @return
     */
    
    public List<Long> subscribeTrees(List<SubscribedTreeModel> addList) {

        List<Long> ret = new ArrayList<Long>();
        for (SubscribedTreeModel model : addList) {
            em.persist(model);
            ret.add(new Long(model.getId()));
        }
        return ret;
    }

    /**
     * 公開Treeにアンサブスクライブする。
     * @param ids アンサブスクライブするTreeのIdリスト
     * @return
     */
    
    public int unsubscribeTrees(List<Long> list) {

        int cnt = 0;

        int len = list.size();

        for (int i = 0; i < len; i+=2) {
            Long treeId = list.get(i);
            Long userPK = list.get(i+1);
            List<SubscribedTreeModel> removes = (List<SubscribedTreeModel>)
                    em.createQuery(QUERY_SUBSCRIBED_BY_USER_PK_TREE_ID)
                      .setParameter(USER_PK, userPK)
                      .setParameter(TREE_ID, treeId)
                      .getResultList();

            for (SubscribedTreeModel sm : removes) {
                em.remove(sm);
            }
            cnt++;
        }
        return cnt;
    }

    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    
    public List<String> putStamp(List<StampModel> list) {
        List<String> ret = new ArrayList<String>();
        for (StampModel model : list) {
            em.persist(model);
            ret.add(model.getId());
        }
        return ret;
    }

    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    
    public String putStamp(StampModel model) {
        //em.persist(model);
        em.merge(model);
        return model.getId();
    }

    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public StampModel getStamp(String stampId) {

        try {
            return (StampModel) em.find(StampModel.class, stampId);
        } catch (NoResultException e) {
        }

        return null;
    }

    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    
    public List<StampModel> getStamp(List<String> ids) {

        List<StampModel> ret = new ArrayList<StampModel>();

        try {
            for (String stampId : ids) {
                StampModel test = (StampModel) em.find(StampModel.class, stampId);
                ret.add(test);
            }
        } catch (Exception e) {
        }

        return ret;
    }

    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    
    public int removeStamp(String stampId) {
        StampModel exist = (StampModel) em.find(StampModel.class, stampId);
        em.remove(exist);
        return 1;
    }

    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id List
     * @return 削除件数
     */
    
    public int removeStamp(List<String> ids) {
        int cnt =0;
        for (String stampId : ids) {
            StampModel exist = (StampModel) em.find(StampModel.class, stampId);
            em.remove(exist);
            cnt++;
        }
        return cnt;
    }
}
