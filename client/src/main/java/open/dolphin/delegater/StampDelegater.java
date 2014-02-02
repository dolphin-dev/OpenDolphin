package open.dolphin.delegater;

import open.dolphin.converter14.StampTreeModelConverter;
import open.dolphin.converter14.StampTreeHolderConverter;
import open.dolphin.converter14.SubscribedTreeListConverter;
import open.dolphin.converter14.StampListConverter;
import open.dolphin.converter14.StampModelConverter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * Stamp関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampDelegater extends BusinessDelegater {
    
    private static final StampDelegater instance;

    static {
        instance = new StampDelegater();
    }

    //private static final String RES_STAMP       = "/stamp";
    private static final String RES_STAMP_TREE  = "/stamp/tree";
    private static final String RES_TREE_SYNC  = "/stamp/tree/sync";
    private static final String RES_TREE_FORCE_SYNC  = "/stamp/tree/forcesync";
    
    public static StampDelegater getInstance() {
        return instance;
    }
    
    /**
     * StampTree を保存/更新する。
     * @param model 保存する StampTree
     * @return treeのPK
     */
    public long putTree(IStampTreeModel model) throws Exception {
       
        // Converter
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8)); // UTF-8 bytes
        StampTreeModelConverter conv = new StampTreeModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(RES_STAMP_TREE);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        checkFirstCommitWin(response);
        
        // PK
        String entityStr = getString(response);
        long pk = Long.parseLong(entityStr);
        return pk;
    }
    
    /**
     * 現在のuserTreeとDBを同期化する。
     * @param model  StampTree
     * @return primaryPKとversionNumberのカンマ連結
     * @throws Exception 
     */
    public String syncTree(IStampTreeModel model) throws Exception {
       
        // Converter
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8)); // UTF-8 bytes
        StampTreeModelConverter conv = new StampTreeModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(RES_TREE_SYNC);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        checkFirstCommitWin(response);

        // PK,versionNumberの連結
        String entityStr = getString(response);
        return entityStr;
    }
    
    public void forceSyncTree(IStampTreeModel model) throws Exception {
       
        // Converter
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8)); // UTF-8 bytes
        StampTreeModelConverter conv = new StampTreeModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(RES_TREE_FORCE_SYNC);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        checkStatus(response);
    }

    public List<IStampTreeModel> getTrees(long userPK) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_STAMP_TREE).append("/").append(userPK);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        StampTreeHolder h = mapper.readValue(br, StampTreeHolder.class);
        br.close();
        
        // return List
        List<IStampTreeModel> retList = new ArrayList<IStampTreeModel>();
        
        // 個人用のtree
        if (h.getPersonalTree()!=null) {
            StampTreeModel model = h.getPersonalTree();
            String treeXml = new String(model.getTreeBytes(), UTF8);
            model.setTreeXml(treeXml);
            model.setTreeBytes(null);
            retList.add((IStampTreeModel)model);
        }
        
        // import(subscribed)している tree
        if (h.getSubscribedList()!=null) {
            List<PublishedTreeModel> inList = h.getSubscribedList();
            for (PublishedTreeModel model : inList) {
                String treeXml = new String(model.getTreeBytes(), UTF8);
                model.setTreeXml(treeXml);
                model.setTreeBytes(null);
                retList.add((IStampTreeModel)model);
            }
        }
        
        return retList;
    }
    
//    /**
//     * 個人用のStampTreeを保存し公開する。
//     * @param model 個人用のStampTreeで公開するもの
//     * @return id
//     */
//    public long saveAndPublishTree(StampTreeModel model, byte[] publishBytes) throws Exception {
//        
//        // PATH
//        StringBuilder sb = new StringBuilder();
//        sb.append("/stamp/published/tree");
//        String path = sb.toString();
//        
//        // Model
//        model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
//        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
//        
//        // Holder
//        StampTreeHolder h = new StampTreeHolder();
//        h.setPersonalTree(model);
//        h.addSubscribedTree(publishedModel);
//        
//        // Converter
//        StampTreeHolderConverter conv = new StampTreeHolderConverter();
//        conv.setModel(h);
//
//        // JSON
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(conv);
//        byte[] data = json.getBytes(UTF8);
//        
//        // POST
//        ClientRequest request = getRequest(path);
//        request.body(MediaType.APPLICATION_JSON, data);
//        ClientResponse<String> response = request.post(String.class);
//
//        // PK
//        String entityStr = getString(response);
//        return  Long.parseLong(entityStr);
//    }
    
    /**
     * 既に保存されている個人用のTreeを公開する。
     * @param model 既に保存されている個人用のTreeで公開するもの
     * @return 公開数
     */
    public String publishTree(StampTreeModel model, byte[] publishBytes) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/stamp/published/tree");
        String path = sb.toString();
        
        // Model
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
        
        // Holder
        StampTreeHolder h = new StampTreeHolder();
        h.setPersonalTree(model);
        h.addSubscribedTree(publishedModel);
        
        // Converter
        StampTreeHolderConverter conv = new StampTreeHolderConverter();
        conv.setModel(h);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        checkFirstCommitWin(response);

        // Version
        String entityStr = getString(response);
        return entityStr;
    }
    
    /**
     * 公開されているTreeを更新する。
     * @param model 更新するTree
     * @return 更新数
     */
    public String updatePublishedTree(StampTreeModel model, byte[] publishBytes) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/stamp/published/tree");
        String path = sb.toString();
        
        // Model
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        PublishedTreeModel publishedModel = createPublishedTreeModel(model, publishBytes);
        
        // Holder
        StampTreeHolder h = new StampTreeHolder();
        h.setPersonalTree(model);
        h.addSubscribedTree(publishedModel);
        
        // Converter
        StampTreeHolderConverter conv = new StampTreeHolderConverter();
        conv.setModel(h);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class); 
        
        checkFirstCommitWin(response);
        
        // Version
        String entityStr = getString(response);
        return entityStr;
    }
    
    /**
     * 公開されているTreeを削除する。
     * @param id 削除するTreeのID
     * @return 削除数
     */
    public String cancelPublishedTree(StampTreeModel model) throws Exception {
        
        // PATH
        String path = "/stamp/published/cancel";
        
        // Model
        model.setTreeBytes(model.getTreeXml().getBytes(UTF8));
        
        // Converter
        StampTreeModelConverter conv = new StampTreeModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        checkFirstCommitWin(response);
        
        // Version
        String entityStr = getString(response);
        return entityStr;
    }
    
    public List<PublishedTreeModel> getPublishedTrees() throws Exception {
        
        // PATH
        String path = "/stamp/published/tree";
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PublishedTreeList result = mapper.readValue(br, PublishedTreeList.class);
        br.close();
        
        // List
        return result.getList();
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

    public List<Long> subscribeTrees(List<SubscribedTreeModel> subscribeList) throws Exception {
        
        // PATH
        String path = "/stamp/subscribed/tree";
        
        // Wrapper
        SubscribedTreeList wrapper = new SubscribedTreeList();
        wrapper.setList(subscribeList);
        
        // Converter
        SubscribedTreeListConverter conv = new SubscribedTreeListConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class); 
        
        // PK list
        String entityStr = getString(response);
        String[] pks = entityStr.split(",");
        List<Long> ret = new ArrayList<Long>(pks.length);
        for (String str : pks) {
            ret.add(Long.parseLong(str));
        }
        return ret;
    }
    
    public int unsubscribeTrees(List<SubscribedTreeModel> removeList) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/stamp/subscribed/tree/");
        for (SubscribedTreeModel s : removeList) {
            sb.append(String.valueOf(s.getTreeId()));
            sb.append(CAMMA);
            sb.append(String.valueOf(s.getUserModel().getId()));
            sb.append(CAMMA);
        }
        String path = sb.toString();
        path = path.substring(0, path.length()-1);
        
        // DELETE
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.delete(String.class);
        
        // Check
        checkStatus(response);
        
        // Count
        return 1;
    }
    
    //---------------------------------------------------------------------------

    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    public List<String> putStamp(List<StampModel> list) throws Exception {
        
        // PATH
        String path = "/stamp/list";
        
        // Wrapper
        StampList wrapper = new StampList();
        wrapper.setList(list);
        
        // Converter
        StampListConverter conv = new StampListConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        // PK List
        String entityStr = getString(response);
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
    public String putStamp(StampModel model) throws Exception {
        
        // PATH
        String path = "/stamp/id";
        
        // Convereter
        StampModelConverter conv = new StampModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        // PK
        String entityStr = getString(response);
        return entityStr;
    }

    /**
     * Stampを置き換える。
     * @param model
     * @return
     * @throws Exception
     */
    public String replaceStamp(StampModel model) throws Exception {
        
        // PATH
        String path = "/stamp/id";
        
        // Converter
        StampModelConverter conv = new StampModelConverter();
        conv.setModel(model);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        // PK
        String entityStr = getString(response);
        return entityStr;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public StampModel getStamp(String stampId) throws Exception {
        
        // PATH
        String path = "/stamp/id/" + stampId;
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // StampModel
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        StampModel ret = mapper.readValue(br, StampModel.class);
        
        return ret;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public List<StampModel> getStamp(List<ModuleInfoBean> list) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        for (ModuleInfoBean info : list) {
            sb.append(info.getStampId());
            sb.append(",");
        }
        String ids = sb.toString();
        ids = ids.substring(0, ids.length()-1);
        String path = "/stamp/list/" + ids;
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        StampList result = mapper.readValue(br, StampList.class);
        
        // List
        return result.getList();
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(String stampId) throws Exception {
        
        // PATH
        String path = "/stamp/id/" + stampId;
        
        // DELETE
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.delete(String.class);
        
        // Check
        checkStatus(response);
        
        // Count
        return 1;
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(List<String> ids) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append(s);
            sb.append(",");
        }
        String idList = sb.toString();
        idList = idList.substring(0, idList.length()-1);
        String path = "/stamp/list/" + idList;
        
        // DELETE
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.delete(String.class);
        
        // Check
        checkStatus(response);
        
        // Count
        return ids.size();
    }
}
