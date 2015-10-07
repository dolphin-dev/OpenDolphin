package open.dolphin.stampbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * DefaultStampTreeBuilder
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ASpStampTreeBuilder extends AbstractStampTreeBuilder {
    
    /** Control staffs */
    private StampTreeNode rootNode;
    private StampTreeNode node;
    private ModuleInfoBean info;
    private LinkedList<StampTreeNode> linkedList;
    private List<StampTree> products;
    
    // Logger
    private static final boolean DEBUG=false;
    private static final java.util.logging.Logger logger;
    static {
        logger = java.util.logging.Logger.getLogger(ASpStampTreeBuilder.class.getName());
        logger.setLevel(DEBUG ? Level.FINE : Level.INFO);
    }
    
    // Goddy conversion
    private final boolean goddyConversion = true;
    private boolean shidoParsing;
    private boolean zaitakuParsing;
    
    private final String convMatchInstraction;
    private final String convReplaceInstraction;
    private final String convMatchZaitaku;
    private final String treeNameFromEditor;
    
    /** Creates new DefaultStampTreeBuilder */
    public ASpStampTreeBuilder() {
        super();
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(ASpStampTreeBuilder.class);
        convMatchInstraction = bundle.getString("text.instraction.conversion");
        convReplaceInstraction = bundle.getString("text.replaceInstraction.conversion");
        convMatchZaitaku = bundle.getString("text.zaitaku.conversion");
        treeNameFromEditor = bundle.getString("treeName.fromEditor");
    }
    
    /**
     * Returns the product of this builder
     * @return vector that contains StampTree instances
     */
    @Override
    public List<StampTree> getProduct() {
        return products;
    }
    
    @Override
    public void buildStart() {
        products = new ArrayList<>();
        logger.fine("Build StampTree start");
    }
    
    @Override
    public void buildRoot(String name, String entity) {
        // New root
        logger.log(Level.FINE, "Root={0}", name);
        
        // Goddy conversion.
        if (name.equals(convMatchInstraction) && goddyConversion){
            name = convReplaceInstraction;
            shidoParsing = true;
        }
        else if (name.equals(convMatchZaitaku) && goddyConversion) {
            zaitakuParsing = true;
            return;
        }
        //--------------------------------------
        linkedList = new LinkedList<>();
        
        // TreeInfo を rootNode に保存する
        TreeInfo treeInfo = new TreeInfo();
        treeInfo.setName(name);
        treeInfo.setEntity(entity);
        rootNode = new StampTreeNode(treeInfo);
        linkedList.addFirst(rootNode);
    }
    
    @Override
    public void buildNode(String name) {
        // New node
        logger.log(Level.FINE, "Node={0}", name);
        
        node = new StampTreeNode(name);
        getCurrentNode().add(node);
        
        // Add the new node to be current node
        linkedList.addFirst(node);
    }
    
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
        
         // ASP Tree なのでエディタから発行を無視する
        if (name.equals(treeNameFromEditor) && (id == null) && (role.equals("p")) ) {
            return;
        }
        
        info = new ModuleInfoBean();
        info.setStampName(name);
        info.setStampRole(role);
        info.setEntity(entity);
        if (editable != null) {
            info.setEditable(Boolean.valueOf(editable));
        }
        if (memo != null) {
            info.setStampMemo(memo);
        }
        if ( id != null ) {
            info.setStampId(id);
        }
        
        // StampInfo から TreeNode を生成し現在のノードへ追加する
        node = new StampTreeNode(info);
        getCurrentNode().add(node);
    }
    
    @Override
    public void buildNodeEnd() {
        logger.fine("End node");
        linkedList.removeFirst();
    }
    
    @Override
    public void buildRootEnd() {
        
        if (shidoParsing && goddyConversion) {
            shidoParsing = false;
            return;
        }
        
        StampTree tree = new StampTree(new StampTreeModel(rootNode));
        products.add(tree);
        
        int pCount = products.size();
        logger.log(Level.FINE, "End root count={0}", String.valueOf(pCount));
    }
    
    @Override
    public void buildEnd() {
        if (DEBUG) {
            logger.fine("Build end");
        }
    }
    
    private StampTreeNode getCurrentNode() {
        return (StampTreeNode) linkedList.getFirst();
    }
}