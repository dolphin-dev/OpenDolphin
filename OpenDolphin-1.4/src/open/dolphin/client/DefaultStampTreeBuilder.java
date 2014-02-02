package open.dolphin.client;

import java.util.*;

import org.apache.log4j.Logger;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StampTree Builder クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultStampTreeBuilder extends AbstractStampTreeBuilder {
    
    /** XML文書で置換が必要な文字 */
    private static final String[] REPLACES = new String[] { "<", ">", "&", "'" ,"\""};
    
    /** 置換文字 */
    private static final String[] MATCHES = new String[] { "&lt;", "&gt;", "&amp;", "&apos;", "&quot;" };
    
    /** エディタから発行のスタンプ名 */
    private static final String FROM_EDITOR = "エディタから発行...";
    
    /** rootノードの名前 */
    private String rootName;
    
    /** エディタから発行があったかどうかのフラグ */
    private boolean hasEditor;
    
    /** StampTree のルートノード*/
    private StampTreeNode rootNode;
    
    /** StampTree のノード*/
    private StampTreeNode node;
    
    /** ノードの UserObject になる StampInfo */
    private ModuleInfoBean info;
    
    /** 制御用のリスト */
    private LinkedList<StampTreeNode> linkedList;
    
    /** 生成物 */
    private List<StampTree> products;
    
    /** Logger */
    private Logger logger;  // = ClientContext.getLogger("boot");
    
    /** 
     * Creates new DefaultStampTreeBuilder 
     */
    public DefaultStampTreeBuilder() {
    }
    
    /**
     * Returns the product of this builder
     * @return vector that contains StampTree instances
     */
    public List<StampTree> getProduct() {
        return products;
    }
    
    /**
     * build を開始する。
     */
    public void buildStart() {
        products = new ArrayList<StampTree>();
        if (logger != null) {
            logger.debug("Build StampTree start");
        }
    }
    
    /**
     * Root を生成する。
     * @param name root名
     * @param Stamptree の Entity
     */
    public void buildRoot(String name, String entity) {
        
        if (logger != null) {
            logger.debug("Root=" + name);
        }
        linkedList = new LinkedList<StampTreeNode>();
        
        //
        // TreeInfo を 生成し rootNode に保存する
        //
        TreeInfo treeInfo = new TreeInfo();
        treeInfo.setName(name);
        treeInfo.setEntity(entity);
        rootNode = new StampTreeNode(treeInfo);
        
        hasEditor = false;
        rootName = name;
        linkedList.addFirst(rootNode);
    }
    
    /**
     * ノードを生成する。
     * @param name ノード名
     */
    public void buildNode(String name) {
        
        if (logger != null) {
            logger.debug("Node=" + name);
        }
        
        //
        // Node を生成し現在のノードに加える
        //
        node = new StampTreeNode(toXmlText(name));
        getCurrentNode().add(node);
        
        //
        // このノードを first に加える
        //
        linkedList.addFirst(node);
    }
    
    /**
     * StampInfo を UserObject にするノードを生成する。
     * @param name ノード名
     * @param entity エンティティ
     * @param editable 編集可能かどうかのフラグ
     * @param memo メモ
     * @param id DB key
     */
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String id) {
        
        if (logger != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(",");
            sb.append(role);
            sb.append(",");
            sb.append(entity);
            sb.append(",");
            sb.append(editable);
            sb.append(",");
            sb.append(memo);
            sb.append(",");
            sb.append(id);
            logger.debug(sb.toString());
        }
        
        //
        // StampInfo を生成する
        //
        info = new ModuleInfoBean();
        info.setStampName(toXmlText(name));
        info.setStampRole(role);
        info.setEntity(entity);
        if (editable != null) {
            info.setEditable(Boolean.valueOf(editable).booleanValue());
        }
        if (memo != null) {
            info.setStampMemo(toXmlText(memo));
        }
        if ( id != null ) {
            info.setStampId(id);
        }
        
        //
        // StampInfo から TreeNode を生成し現在のノードへ追加する
        //
        node = new StampTreeNode(info);
        getCurrentNode().add(node);
        
        //
        // エディタから発行を持っているか
        //
        if (info.getStampName().equals(FROM_EDITOR) && (! info.isSerialized()) ) {
            hasEditor = true;
            info.setEditable(false);
        }
    }
    
    /**
     * Node の生成を終了する。
     */
    public void buildNodeEnd() {
        if (logger != null) {
            logger.debug("End node");
        }
        linkedList.removeFirst();
    }
    
    /**
     * Root Node の生成を終了する。 
     */
    public void buildRootEnd() {
        
        //
        // エディタから発行...を削除された場合に追加する処置
        //
        if ( (!hasEditor) && (getEntity(rootName) != null) ) {
            
            if	( getEntity(rootName).equals(IInfoModel.ENTITY_TEXT) || getEntity(rootName).equals(IInfoModel.ENTITY_PATH)) {
                //
                // テキストスタンプとパススタンプにはエディタから発行...はなし
                //
            } else {
                ModuleInfoBean si = new ModuleInfoBean();
                si.setStampName(FROM_EDITOR);
                si.setStampRole(IInfoModel.ROLE_P);
                si.setEntity(getEntity(rootName));
                si.setEditable(false);
                StampTreeNode sn = new StampTreeNode(si);
                rootNode.add(sn);
            }
        }
        
        //
        // StampTree を生成しプロダクトリストへ加える
        //
        StampTree tree = new StampTree(new StampTreeModel(rootNode));
        products.add(tree);
        
        if (logger != null) {
            int pCount = products.size();
            logger.debug("End root " + "count=" + pCount);
        }
    }
    
    /**
     * build を終了する。
     */
    public void buildEnd() {
        
        if (logger != null) {
            logger.debug("Build end");
        }
        
        //
        // ORCAセットを加える
        //
        boolean hasOrca = false;
        for (StampTree st : products) {
            String entity = st.getTreeInfo().getEntity();
            if (entity.equals(IInfoModel.ENTITY_ORCA)) {
                hasOrca = true;
            }
        }
        
        if (!hasOrca) {
            TreeInfo treeInfo = new TreeInfo();
            treeInfo.setName(IInfoModel.TABNAME_ORCA);
            treeInfo.setEntity(IInfoModel.ENTITY_ORCA);
            rootNode = new StampTreeNode(treeInfo);
            OrcaTree tree = new OrcaTree(new StampTreeModel(rootNode));
            products.add(IInfoModel.TAB_INDEX_ORCA, tree);
            if (logger != null) {
                logger.debug("ORCAセットを加えました");
            }
        }
    }
    
    /**
     * リストから先頭の StampTreeNode を取り出す。
     */
    private StampTreeNode getCurrentNode() {
        return (StampTreeNode) linkedList.getFirst();
    }
    
        
    /**
     * 特殊文字を変換する。
     */
    private String toXmlText(String text) {
        for (int i = 0; i < REPLACES.length; i++) {
            text = text.replaceAll(MATCHES[i], REPLACES[i]);
        }
        return text;
    }
}