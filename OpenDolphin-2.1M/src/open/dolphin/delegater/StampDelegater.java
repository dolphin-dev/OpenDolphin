package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 * Stamp関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampDelegater extends BusinessDelegater {

    private static final String UTF8 = "UTF-8";
    private static final String RES_STAMP_TREE = "stampTree/";
    
    /**
     * StampTree を保存/更新する。
     * @param model 保存する StampTree
     * @return 保存個数
     */
    public long putTree(IStampTreeModel model) {
        try {
            model.setTreeBytes(model.getTreeXml().getBytes(UTF8)); // UTF-8 bytes
            //model.setTreeXml(null); DO NOT DO THIS!
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
        }

        PlistConverter con = new PlistConverter();
        String xmlRep = con.convert((StampTreeModel) model);

        // resource post
        String path = RES_STAMP_TREE;
        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, xmlRep);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        long pk = Long.parseLong(entityStr);
        return pk;
    }

    public List<IStampTreeModel> getTrees(long userPK) {
        
        ClientResponse response = getResource(RES_STAMP_TREE + String.valueOf(userPK))
            .accept(MediaType.APPLICATION_XML_TYPE)
            .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        List<IStampTreeModel> treeList = new ArrayList<IStampTreeModel>();
        PlistParser con = new PlistParser();
        List<IStampTreeModel> list = (List<IStampTreeModel>) con.parse(entityStr);

        for (IStampTreeModel model : list) {
            String treeXml;
            try {
                treeXml = new String(model.getTreeBytes(), UTF8);
                model.setTreeXml(treeXml);
                model.setTreeBytes(null);
                treeList.add(model);
            } catch (UnsupportedEncodingException ex) {
                logger.warn(ex.getMessage());
            }
        }

        return treeList;
    }
    
    
    /**
     * 個人用のStampTreeを保存し公開する。
     * @param model 個人用のStampTreeで公開するもの
     * @return id
     */
    public long saveAndPublishTree(StampTreeModel model, byte[] publishBytes) {
        try {
            model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
        }

        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
        List<IStampTreeModel> list = new ArrayList<IStampTreeModel>(2);
        list.add(model);
        list.add(publishedModel);

        PlistConverter con = new PlistConverter();
        String xmlRep = con.convert(list);

        StringBuilder sb = new StringBuilder();
        sb.append(RES_STAMP_TREE);
        sb.append("published");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .post(ClientResponse.class, xmlRep);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Long.parseLong(entityStr);
    }
    
    /**
     * 既に保存されている個人用のTreeを公開する。
     * @param model 既に保存されている個人用のTreeで公開するもの
     * @return 公開数
     */
    public int publishTree(StampTreeModel model, byte[] publishBytes) {
        try {
            model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
        }

        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
        List<IStampTreeModel> list = new ArrayList<IStampTreeModel>(2);
        list.add(model);
        list.add(publishedModel);

        PlistConverter con = new PlistConverter();
        String xmlRep = con.convert(list);

        StringBuilder sb = new StringBuilder();
        sb.append(RES_STAMP_TREE);
        sb.append("published");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, xmlRep);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
    }
    
    /**
     * 公開されているTreeを更新する。
     * @param model 更新するTree
     * @return 更新数
     */
    public int updatePublishedTree(StampTreeModel model, byte[] publishBytes) {
        try {
            model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
        }

        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
        List<IStampTreeModel> list = new ArrayList<IStampTreeModel>(2);
        list.add(model);
        list.add(publishedModel);

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(list);

        StringBuilder sb = new StringBuilder();
        sb.append(RES_STAMP_TREE);
        sb.append("published");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
    }
    
    /**
     * 公開されているTreeを削除する。
     * @param id 削除するTreeのID
     * @return 削除数
     */
    public int cancelPublishedTree(StampTreeModel model) {
        try {
            model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage());
        }

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(model);

        String path = "stampTree/published/cancel/";

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "put response");
        }

        return 1;
    }
    
    public List<PublishedTreeModel> getPublishedTrees() {

        String path = "stampTree/published";

        ClientResponse response = getResource(path)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }
        PlistParser parser = new PlistParser();
        List<PublishedTreeModel> ret = (List<PublishedTreeModel>) parser.parse(entityStr);
        return ret;
    }

    // 個人用StampTreeから公開用StampTreeを生成する。
    // byte[] publishBytes は公開されるカテゴリのみを含むサブセットバイト
    private PublishedTreeModel createPublishedTreeModel(StampTreeModel model, byte[] publishBytes) {
        PublishedTreeModel publishedModel = new PublishedTreeModel();
        publishedModel.setId(model.getId());                            // pk
        publishedModel.setUserModel(model.getUserModel());              // UserModel
        publishedModel.setName(model.getName());                        // 名称
        publishedModel.setPublishType(model.getPublishType());          // 公開タイプ
        publishedModel.setCategory(model.getCategory());                // カテゴリ
        publishedModel.setPartyName(model.getPartyName());              // パーティー名
        publishedModel.setUrl(model.getUrl());                          // URL
        publishedModel.setDescription(model.getDescription());          // 説明
        publishedModel.setPublishedDate(model.getPublishedDate());      // 公開日
        publishedModel.setLastUpdated(model.getLastUpdated());          // 更新日
        publishedModel.setTreeBytes(publishBytes);                      // XML bytes
        return publishedModel;
    }


    //---------------------------------------------------------------------------

    public List<Long> subscribeTrees(List<SubscribedTreeModel> subscribeList) {
        
        PlistConverter con = new PlistConverter();
        String repXml = con.convert(subscribeList);

        String path = "stampTree/subscribed";

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        String[] pks = entityStr.split(",");
        List<Long> ret = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            ret.add(Long.parseLong(str));
        }
        return ret;
    }
    
    
    public int unsubscribeTrees(List<SubscribedTreeModel> removeList) {

        StringBuilder sb = new StringBuilder();
        for (SubscribedTreeModel s : removeList) {
            sb.append(String.valueOf(s.getTreeId()));
            sb.append(CAMMA);
            sb.append(String.valueOf(s.getUserModel().getId()));
            sb.append(CAMMA);
        }
        String idList = sb.toString();
        idList = idList.substring(0, idList.length()-1);

        ClientResponse response = getResource("stampTree/subscribed/" + idList)
                    .accept(MediaType.TEXT_PLAIN)
                    .delete(ClientResponse.class);

        int status = response.getStatus();

        if (DEBUG) {
            debug(status, "delete response");
        }

        return 1;
    }
    

    //---------------------------------------------------------------------------

    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    public List<String> putStamp(List<StampModel> list) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(list);

        ClientResponse response = getResource("stamp/list/")
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        String[] params = entityStr.split(",");
        List<String> ret = new ArrayList<String>();
        ret.addAll(Arrays.asList(params));
        return ret;
    }
    
    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    public String putStamp(StampModel model) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(model);

        ClientResponse response = getResource("stamp/id/")
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return entityStr;
    }

    /**
     * Stampを置き換える。
     * @param model
     * @return
     * @throws Exception
     */
    public String replaceStamp(StampModel model) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(model);

        ClientResponse response = getResource("stamp/id/")
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return entityStr;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public StampModel getStamp(String stampId) {

        ClientResponse response = getResource("stamp/id/" + stampId)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        StampModel ret = (StampModel) con.parse(entityStr);
        return ret;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public List<StampModel> getStamp(List<ModuleInfoBean> list) {

        StringBuilder sb = new StringBuilder();
        for (ModuleInfoBean info : list) {
            sb.append(info.getStampId());
            sb.append(",");
        }
        String ids = sb.toString();
        ids = ids.substring(0, ids.length()-1);

        ClientResponse response = getResource("stamp/list/" + ids)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<StampModel> ret = (List<StampModel>) con.parse(entityStr);
        return ret;
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(String stampId) {

        ClientResponse response = getResource("stamp/id/" + stampId)
                    .accept(MediaType.TEXT_PLAIN)
                    .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }

        return 1;
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(List<String> ids) {

        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append(s);
            sb.append(",");
        }
        String idList = sb.toString();
        idList = idList.substring(0, idList.length()-1);
        
        ClientResponse response = getResource("stamp/list/" + idList)
                    .accept(MediaType.TEXT_PLAIN)
                    .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }

        return ids.size();
    }
}
