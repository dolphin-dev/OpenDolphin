
package open.dolphin.stampbox;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * stampBytesも含めたStampTreeXmlDirector
 *
 * based on StampTreeXmlDirector.java
 * @author masuda, Masuda Naika
 */

public class ExtendedStampTreeXmlDirector {

    private ExtendedStampTreeXmlBuilder builder;

    // Creates new ExtendedStampTreeXmlDirector
    public ExtendedStampTreeXmlDirector(ExtendedStampTreeXmlBuilder builder) {
        this.builder = builder;
    }

    /**
     * スタンプツリー全体をXMLにエンコードする。
     * @param allTrees StampTreeのリスト
     * @return XML
     */
    public String build(List<StampTree> allTrees) {

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
