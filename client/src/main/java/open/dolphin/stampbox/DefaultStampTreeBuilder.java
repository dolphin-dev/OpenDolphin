package open.dolphin.stampbox;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StampTree Builder クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultStampTreeBuilder extends AbstractStampTreeBuilder {
    
    /** XML文書で置換が必要な文字 */
    //private static final String[] REPLACES = new String[] { "<", ">", "&", "'" ,"\""};
    private static final String[] REPLACES = new String[] { "&", "<", ">", "'" ,"\""};
    
    /** 置換文字 */
    //private static final String[] MATCHES = new String[] { "&lt;", "&gt;", "&amp;", "&apos;", "&quot;" };
    private static final String[] MATCHES = new String[] { "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" };
    
    /** エディタから発行のスタンプ名 */
 //   private static final String FROM_EDITOR = "エディタから発行...";
    
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
    
    // Goddy conversion
    private final boolean goddyConversion = true;
    private boolean shidoParsing;
    private boolean zaitakuParsing;
    
    private final String convMatchInstraction;
    private final String convReplaceInstraction;
    private final String convMatchZaitaku;
    private final String treeNameFromEditor;
    
    private ResourceBundle bundle;
    
    // Logger
    private static final boolean DEBUG=false;
    private static final java.util.logging.Logger logger;
    static {
        logger = java.util.logging.Logger.getLogger(DefaultStampTreeBuilder.class.getName());
        logger.setLevel(DEBUG ? Level.FINE : Level.INFO);
    }
    
    /** 
     * Creates new DefaultStampTreeBuilder 
     */
    public DefaultStampTreeBuilder() {
        super();
        
        // StampTreeRsource  treeName from entity
        bundle = java.util.ResourceBundle.getBundle("open.dolphin.stampbox.StampBoxResource");
        
        // Goody conversion
        ResourceBundle goodyBundle = ClientContext.getMyBundle(DefaultStampTreeBuilder.class);
        convMatchInstraction = goodyBundle.getString("text.instraction.conversion");             // 指導
        convMatchZaitaku = goodyBundle.getString("text.zaitaku.conversion");                     // 在宅
        convReplaceInstraction = goodyBundle.getString("text.replaceInstraction.conversion");    // 指導・在宅
        
        // stampEditor name
        treeNameFromEditor = goodyBundle.getString("treeName.fromEditor");                       // エディタから発行...
    }
    
    /**
     * Returns the product of this builder
     * @return vector that contains StampTree instances
     */
    @Override
    public List<StampTree> getProduct() {
        return products;
    }
    
    /**
     * build を開始する。
     */
    @Override
    public void buildStart() {
        products = new ArrayList<>();
        if (DEBUG) {
            logger.fine("Build StampTree start");
        }
    }
    
    /**
     * Root を生成する。
     * @param name root名
     * @param entity
     */
    @Override
    public void buildRoot(String name, String entity) {
        
        // Tree name from the entity
        String treeName = bundle.getString(entity);
        
        if (DEBUG) {
            String fmt = "Root={0}  entity={1}  treeName={2}";
            MessageFormat msf = new MessageFormat(fmt);
            String msg = msf.format(new Object[]{name, entity, treeName});
            logger.fine(msg);
        }
        //--------------------------------------
        // Goddy conversion.  指導と在宅のタブ（Tree）が別々 -> Dolphinは一つにまとめる
        if (name.equals(convMatchInstraction) && goddyConversion){
            name = convReplaceInstraction;
            shidoParsing = true;
        }
        else if (name.equals(convMatchZaitaku) && goddyConversion) {
            zaitakuParsing = true;
            return;
        }
        //--------------------------------------
        
        // i18n entity から Tree（タブ）名を決定する Goody版でない時
        // Goody版の時はもともと日本語なので変換不要
        if (!shidoParsing) {
            name = treeName;
        }
        
        linkedList = new LinkedList<>();
        
        //------------------------------------
        // TreeInfo を 生成し rootNode に保存する
        //------------------------------------
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
    @Override
    public void buildNode(String name) {
        
        if (DEBUG) {
            logger.fine("Node=" + name);
        }
        
        //------------------------------------
        // Node を生成し現在のノードに加える
        //------------------------------------
        node = new StampTreeNode(toXmlText(name));
        getCurrentNode().add(node);
        
        //------------------------------------
        // このノードを first に加える
        //------------------------------------
        linkedList.addFirst(node);
    }
    
    /**
     * StampInfo を UserObject にするノードを生成する。
     * @param name ノード名
     * @param role
     * @param entity
     * @param editable 編集可能かどうかのフラグ
     * @param memo メモ
     * @param id DB key
     */
    
    @Override
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String id) {
        
        if (DEBUG) {
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
            logger.fine(sb.toString());
        }
        
        //------------------------------------
        // StampInfo を生成する
        //------------------------------------
        info = new ModuleInfoBean();
        info.setStampName(toXmlText(name));
        info.setStampRole(role);
        info.setEntity(entity);
        if (editable != null) {
            info.setEditable(Boolean.valueOf(editable));
        }
        if (memo != null) {
            info.setStampMemo(toXmlText(memo));
        }
        if ( id != null ) {
            info.setStampId(id);
        }
        
        //-------------------------------------------------------------
        // Goddy Conversion: 在宅のエディタから発行は指導と重複するためパスする
        //-------------------------------------------------------------
        if (zaitakuParsing && info.getStampName().equals(treeNameFromEditor) && (!info.isSerialized())) {
            return;
        }
        
        //------------------------------------
        // StampInfo から TreeNode を生成し現在のノードへ追加する
        //------------------------------------
        node = new StampTreeNode(info);
        getCurrentNode().add(node);
        
        //------------------------------------
        // エディタから発行を持っているか
        //------------------------------------
        if (info.getStampName().equals(treeNameFromEditor) && (!info.isSerialized()) ) {
            hasEditor = true;
            info.setEditable(false);
        }
    }
    
    /**
     * Node の生成を終了する。
     */
    @Override
    public void buildNodeEnd() {
        if (DEBUG) {
            logger.fine("End node");
        }
        linkedList.removeFirst();
    }
    
    /**
     * Root Node の生成を終了する。 
     */
    @Override
    public void buildRootEnd() {
        
        // Goody版の指導をパースしている時 次の在宅とまとめる
        if (shidoParsing && goddyConversion) {
            shidoParsing = false;
            return;
        }
        
        //---------------------------------------------
        // エディタから発行...を削除された場合に追加する処置
        //---------------------------------------------
        if (!hasEditor) {

            String entity = getEntity(rootName);

            if (entity != null) {

                if ((!entity.equals(IInfoModel.ENTITY_TEXT)) && (!entity.equals(IInfoModel.ENTITY_PATH)) ) {
                    
                    //-------------------------------------------------
                    // テキストスタンプとパススタンプにはエディタから発行...はなし
                    //--------------------------------------------------
                    ModuleInfoBean si = new ModuleInfoBean();
                    si.setStampName(treeNameFromEditor);
                    si.setStampRole(IInfoModel.ROLE_P);
                    si.setEntity(getEntity(rootName));
                    si.setEditable(false);
                    StampTreeNode sn = new StampTreeNode(si);
                    rootNode.add(sn);
                }
            }
        }
        
        //---------------------------------------
        // StampTree を生成しプロダクトリストへ加える
        //---------------------------------------
        StampTree tree = new StampTree(new StampTreeModel(rootNode));
        products.add(tree);
        
        if (DEBUG) {
            int pCount = products.size();
            logger.log(Level.FINE, "End root count={0}", pCount);
        }
    }
    
    /**
     * build を終了する。
     */
    @Override
    public void buildEnd() {
        
        if (DEBUG) {
            logger.fine("Build end");
        }
        
        //-------------------------
        // ORCAセットを加える
        //-------------------------
        boolean hasOrca = false;
        for (StampTree st : products) {
            String entity = st.getTreeInfo().getEntity();
            if (entity.equals(IInfoModel.ENTITY_ORCA)) {
                hasOrca = true;
            }
        }
        
        if (!hasOrca) {
            TreeInfo treeInfo = new TreeInfo();
            treeInfo.setName(bundle.getString("TABNAME_ORCA"));
            treeInfo.setEntity(IInfoModel.ENTITY_ORCA);
            rootNode = new StampTreeNode(treeInfo);
            OrcaTree tree = new OrcaTree(new StampTreeModel(rootNode));
            products.add((int)bundle.getObject("TAB_INDEX_ORCA"), tree);
            if (DEBUG) {
                logger.fine("ORCAセットを加えました");
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