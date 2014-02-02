package open.dolphin.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Director to build StampTree XML data.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTreeXmlDirector {
    
    private DefaultStampTreeXmlBuilder builder;
    
    /** 
     * Creates new StampTreeXmlDirector 
     */
    public StampTreeXmlDirector(DefaultStampTreeXmlBuilder builder) {
        
        super();
        this.builder = builder;
    }
    
    /**
     * スタンプツリー全体をXMLにエンコードする。
     * @param allTrees StampTreeのリスト
     * @return XML
     */
    public String build(ArrayList<StampTree> allTrees) {
        
        try {
            builder.buildStart();
            for (StampTree tree : allTrees) {
                lbuild(tree);
            }
            
            builder.buildEnd();
            return builder.getProduct();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return null;
    }
    
    /**
     * 一つのツリーをXMLにエンコードする
     * @param tree StampTree
     * @throws IOException
     */
    private void lbuild(StampTree tree) throws IOException {
        
        // ルートノードを取得しチャイルドのEnumerationを得る
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration e = rootNode.preorderEnumeration();
        StampTreeNode node = (StampTreeNode) e.nextElement();
        
        // ルートノードを書き出す
        builder.buildRoot(node);
        
        // 子を書き出す
        while (e.hasMoreElements()) {
            builder.buildNode((StampTreeNode) e.nextElement());
        }
        
        builder.buildRootEnd();
    }
}