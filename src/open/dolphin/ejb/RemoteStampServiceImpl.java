package open.dolphin.ejb;

import java.util.ArrayList;
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

import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteStampService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteStampService")
public class RemoteStampServiceImpl extends DolphinService implements RemoteStampService {
    
    private static final long serialVersionUID = -9201185729129886533L;
    
    @Resource
    private SessionContext ctx;
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * user個人のStampTreeを保存/更新する。
     * @param model 保存する StampTree
     * @return id
     */
    public long putTree(StampTreeModel model) {
        
        StampTreeModel saveOrUpdate = em.merge(model);
        return saveOrUpdate.getId();
    }
    
    /**
     * User個人及びサブスクライブしているTreeを取得する。
     * @param userPk userId(DB key)
     * @return User個人及びサブスクライブしているTreeのリスト
     */
    public List<IStampTreeModel> getTrees(long userPk) {
        
        List<IStampTreeModel> ret = new ArrayList<IStampTreeModel>();
        boolean newUser = false;
        
        //
        // パーソナルツリーを取得する
        //
        try {
            StampTreeModel personal = (StampTreeModel) em.createQuery("from StampTreeModel s where s.user.id=:userPk")
            .setParameter("userPk", userPk)
            .getSingleResult();
            ret.add(personal);
            
        } catch (NoResultException ne) {
            // 新規ユーザの場合ここへくる
            newUser = true;
        }
        
        //
        // 新規ユーザの場合、空のリストを返す
        //
        if (newUser) {
            return ret;
        }
        
        //
        // ユーザがサブスクライブしているStampTreeのリストを取得する
        //
        List subscribed = em.createQuery("from SubscribedTreeModel s where s.user.id=:userPk")
        .setParameter("userPk", userPk)
        .getResultList();
        
        //
        // サブスクライブリストから公開Treeを取得する
        //
        for (Iterator iter=subscribed.iterator(); iter.hasNext(); ) {
            
            SubscribedTreeModel sm = (SubscribedTreeModel) iter.next();
            
            //
            // 公開Treeが削除されている場合
            // サブスクライブTreeも削除する
            //
            try {
                PublishedTreeModel published = (PublishedTreeModel) em.find(PublishedTreeModel.class, sm.getTreeId());
                
                if (published == null) {
                    //
                    // 公開Treeが削除されている場合
                    // サブスクライブリストレコードを削除する
                    //
                    em.remove(sm);
                    //System.out.println("published is null but no exception");
                } else {
                    //
                    // 公開Treeがあれば加える
                    //
                    ret.add(published);
                }
                
            } catch (NoResultException ne) {
                //
                // 削除されているのでサブスクライブリストからも除く
                // id の関係かここへ来ない
                //
                em.remove(sm);
                //System.out.println("removed subscribedTree");
            }
        }
        
        return ret;
    }
    
    /**
     * まだ保存されていない個人用のTreeを保存し公開する。
     */
    public long saveAndPublishTree(StampTreeModel model, byte[] publishBytes) {
        
        //
        // 最初に保存する
        //
        em.persist(model);
        
        //
        // 公開用Treeモデルを生成し値をコピーする
        // 公開Treeのid=個人用TreeのId
        //
        PublishedTreeModel publishedModel = new PublishedTreeModel();
        publishedModel.setId(model.getId());
        publishedModel.setUser(model.getUser());
        publishedModel.setName(model.getName());
        publishedModel.setPublishType(model.getPublishType());
        publishedModel.setCategory(model.getCategory());
        publishedModel.setPartyName(model.getPartyName());
        publishedModel.setUrl(model.getUrl());
        publishedModel.setDescription(model.getDescription());
        publishedModel.setPublishedDate(model.getPublishedDate());
        publishedModel.setLastUpdated(model.getLastUpdated());
        publishedModel.setTreeBytes(publishBytes);
        
        //
        // 公開Treeを保存する
        //
        em.persist(publishedModel);
        
        // id を返す
        return model.getId();
        
    }
    
    
    /**
     * 保存されている個人用のTreeを新規に公開する。
     * @param model 公開するStampTree
     */
    public int publishTree(StampTreeModel model, byte[] publishBytes) {
        
        //
        // 最初に更新する
        //
        em.merge(model);
        
        //
        // 公開用StampTreeModelを生成し値をコピーする
        // 公開Treeのid=個人用TreeのId
        //
        PublishedTreeModel publishedModel = new PublishedTreeModel();
        publishedModel.setId(model.getId());
        publishedModel.setUser(model.getUser());
        publishedModel.setName(model.getName());
        publishedModel.setPublishType(model.getPublishType());
        publishedModel.setCategory(model.getCategory());
        publishedModel.setPartyName(model.getPartyName());
        publishedModel.setUrl(model.getUrl());
        publishedModel.setDescription(model.getDescription());
        publishedModel.setPublishedDate(model.getPublishedDate());
        publishedModel.setLastUpdated(model.getLastUpdated());
        publishedModel.setTreeBytes(publishBytes);
        
        //
        // 公開Treeを保存する
        //
        em.persist(publishedModel);
        
        return 1;
    }
    
    /**
     * 公開しているTreeを更新する。
     * @param model 公開しているTree
     * @return 更新した数
     */
    public int updatePublishedTree(StampTreeModel model, byte[] publishBytes) {
        
        //
        // 最初に更新する
        //
        em.merge(model);
        
        //
        // 公開用Treeへコピーする
        //
        PublishedTreeModel publishedModel = new PublishedTreeModel();
        publishedModel.setId(model.getId());
        publishedModel.setUser(model.getUser());
        publishedModel.setName(model.getName());
        publishedModel.setPublishType(model.getPublishType());
        publishedModel.setCategory(model.getCategory());
        publishedModel.setPartyName(model.getPartyName());
        publishedModel.setUrl(model.getUrl());
        publishedModel.setDescription(model.getDescription());
        publishedModel.setPublishedDate(model.getPublishedDate());
        publishedModel.setLastUpdated(model.getLastUpdated());
        publishedModel.setTreeBytes(publishBytes);
        
        //
        // 公開Treeを更新する
        // 検索し値を設定するほうがいいのではないか?
        //
        em.merge(publishedModel);
        
        return 1;
        
    }
    
    /**
     * 公開したTreeを削除する。
     * @param id 削除するTreeのId
     * @return 削除した数
     */
    public int cancelPublishedTree(StampTreeModel model) {
        //
        // 公開属性を更新する
        //
        em.merge(model);
        
        //
        // 公開Treeを削除する
        //
        PublishedTreeModel exist = (PublishedTreeModel) em.find(PublishedTreeModel.class, model.getId());
        em.remove(exist);
        
        return 1;
    }
    
    /**
     * 公開されているStampTreeのリストを取得する。
     * @return ローカル及びパブリックTreeのリスト
     */
    @SuppressWarnings("unchecked")
    public List<PublishedTreeModel> getPublishedTrees() {
        
        // ログインユーザの施設IDを取得する
        String fid = this.getCallersFacilityId(ctx);
        
        List<PublishedTreeModel> ret = new ArrayList<PublishedTreeModel>();
        
        // local に公開されているTreeを取得する
        // publishType=施設ID
        List locals = em.createQuery("from PublishedTreeModel p where p.publishType=:fid")
        .setParameter("fid", fid)
        .getResultList();
        ret.addAll((List<PublishedTreeModel>) locals);
        
        // パブリックTeeを取得する
        List publics = em.createQuery("from PublishedTreeModel p where p.publishType='global'")
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
    public int unsubscribeTrees(List<SubscribedTreeModel> removeList) {
        
        int cnt = 0;
        
        for (SubscribedTreeModel model : removeList) {
            SubscribedTreeModel remove = (SubscribedTreeModel) em.createQuery("from SubscribedTreeModel s where s.user.id=:userPk and s.treeId=:treeId")
            .setParameter("userPk", model.getUser().getId())
            .setParameter("treeId", model.getTreeId())
            .getSingleResult();
            em.remove(remove);
            cnt++;
        }
        
        return cnt;
    }
    
    /**
     * ASP StampTreeを取得する。
     * @param managerId ASP TreeマネージャID
     * @return ASP提供のStampTree
     */
    public StampTreeModel getAspTree(String managerId) {
        
        StampTreeModel ret = null;
        
        try {
            ret =  (StampTreeModel)em.createQuery("from StampTreeModel s where s.userId = :mid")
            .setParameter("mid", managerId)
            .getSingleResult();
        } catch (NoResultException e) {
        }
        return ret;
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
        em.persist(model);
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
