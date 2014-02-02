package open.dolphin.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 *
 * @author kazushi, Minagawa, Digital Globe, Inc.
 */
@Stateless
public class StampServiceBean implements StampServiceBeanLocal {

    private static final String QUERY_TREE_BY_USER_PK = "from StampTreeModel s where s.user.id=:userPK";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK = "from SubscribedTreeModel s where s.user.id=:userPK";
    private static final String QUERY_LOCAL_PUBLISHED_TREE = "from PublishedTreeModel p where p.publishType=:fid";
    private static final String QUERY_PUBLIC_TREE = "from PublishedTreeModel p where p.publishType='global'";
    private static final String QUERY_PUBLISHED_TREE_BY_ID = "from PublishedTreeModel p where p.id=:id";
    private static final String QUERY_SUBSCRIBED_BY_USER_PK_TREE_ID = "from SubscribedTreeModel s where s.user.id=:userPK and s.treeId=:treeId";

    private static final String USER_PK = "userPK";
    private static final String FID = "fid";
    private static final String TREE_ID = "treeId";
    private static final String ID = "id";
    
    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;

    /**
     * user個人のStampTreeを保存/更新する。
     * @param model 保存する StampTree
     * @return id
     */
    @Override
    public long putTree(StampTreeModel model) {

        StampTreeModel saveOrUpdate = em.merge(model);
        return saveOrUpdate.getId();
    }

    /**
     * User個人及びサブスクライブしているTreeを取得する。
     * @param userPk userId(DB key)
     * @return User個人及びサブスクライブしているTreeのリスト
     */
    @Override
    public List<IStampTreeModel> getTrees(long userPK) {

        List<IStampTreeModel> ret = new ArrayList<IStampTreeModel>();

        //
        // パーソナルツリーを取得する
        //
        List<StampTreeModel> list = (List<StampTreeModel>)
                em.createQuery(QUERY_TREE_BY_USER_PK)
                  .setParameter(USER_PK, userPK)
                  .getResultList();

        // 新規ユーザの場合
        if (list.isEmpty()) {
            return ret;
        }

        // 最初の Tree を追加
        StampTreeModel st = (StampTreeModel) list.remove(0);
        ret.add(st);

        // まだある場合 BUG
        if (list.size() > 0) {
            // 後は delete する
            for (int i=0; i < list.size(); i++) {
                st = (StampTreeModel) list.remove(0);
                em.remove(st);
            }
        }

        //
        // ユーザがサブスクライブしているStampTreeのリストを取得する
        //
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
                    PublishedTreeModel published = (PublishedTreeModel) em.find(PublishedTreeModel.class, sm.getTreeId());

                    if (published != null) {
                        ret.add(published);

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

    /**
     * まだ保存されていない個人用のTreeを保存し公開する。
     */
    @Override
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
        publishedModel.setUserModel(model.getUserModel());
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

    @Override
    public long saveAndPublishTree(List<IStampTreeModel> list) {

        StampTreeModel st = (StampTreeModel) list.get(0);
        PublishedTreeModel pt = (PublishedTreeModel) list.get(1);

        em.persist(st);

        pt.setId(st.getId());
        em.persist(pt);

        return pt.getId();
    }


    /**
     * 保存されている個人用のTreeを新規に公開する。
     * @param model 公開するStampTree
     */
    @Override
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
        publishedModel.setId(model.getId());                            // pk
        publishedModel.setUserModel(model.getUserModel());                        // UserModel
        publishedModel.setName(model.getName());                        // 名称
        publishedModel.setPublishType(model.getPublishType());          // 公開タイプ
        publishedModel.setCategory(model.getCategory());                // カテゴリ
        publishedModel.setPartyName(model.getPartyName());              // パーティー名
        publishedModel.setUrl(model.getUrl());                          // URL
        publishedModel.setDescription(model.getDescription());          // 説明
        publishedModel.setPublishedDate(model.getPublishedDate());      // 公開日
        publishedModel.setLastUpdated(model.getLastUpdated());          // 更新日
        publishedModel.setTreeBytes(publishBytes);                      // XML bytes

        //
        // 公開Treeを保存する
        //
        em.persist(publishedModel);

        return 1;
    }

    @Override
    public int updatePublishedTree(List<IStampTreeModel> list) {

        StampTreeModel st = (StampTreeModel) list.get(0);
        PublishedTreeModel pt = (PublishedTreeModel) list.get(1);

        em.merge(st);

        if (pt.getId()==0L) {
            pt.setId(st.getId());
            em.persist(pt);
        } else {
            em.merge(pt);
        }

        return 1;
    }

    /**
     * 公開しているTreeを更新する。
     * @param model 公開しているTree
     * @return 更新した数
     */
    @Override
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
        publishedModel.setUserModel(model.getUserModel());
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
    @Override
    public int cancelPublishedTree(StampTreeModel model) {

        //System.err.println("cancelPublishedTree id is " + model.getId());

        //
        // 公開属性を更新する
        //
        em.merge(model);

        //
        // 公開Treeを削除する
        //
        List<PublishedTreeModel> list = em.createQuery(QUERY_PUBLISHED_TREE_BY_ID)
                                          .setParameter(ID, model.getId())
                                          .getResultList();
        //System.err.println("PublishedTreeModel count is " + list.size());
        for (PublishedTreeModel m : list) {
           // System.err.println("remove id is " + m.getId());
            em.remove(m);
        }
        //PublishedTreeModel exist = (PublishedTreeModel) em.find(PublishedTreeModel.class, model.getId());
        //em.remove(exist);

        return 1;
    }

    /**
     * 公開されているStampTreeのリストを取得する。
     * @return ローカル及びパブリックTreeのリスト
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
