package open.dolphin.stampbox;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

/**
 * AspStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AspStampBox extends AbstractStampBox {
    
    /** Creates new StampBoxPlugin */
    public AspStampBox() {
    }
    
    @Override
    protected void buildStampBox() {
        
        try {
            // Build stampTree
            BufferedReader reader = new BufferedReader(new StringReader(stampTreeModel.getTreeXml()));
            ASpStampTreeBuilder builder = new ASpStampTreeBuilder();
            StampTreeDirector director = new StampTreeDirector(builder);
            List<StampTree> aspTrees = director.build(reader);
            reader.close();
            stampTreeModel.setTreeXml(null);
            
            // StampTreeに設定するポップアップメニューとトランスファーハンドラーを生成する
            AspStampTreeTransferHandler transferHandler = new AspStampTreeTransferHandler();
            
            // StampBox(TabbedPane) へリスト順に格納する
            for (StampTree stampTree : aspTrees) {
                stampTree.setTransferHandler(transferHandler);
                stampTree.setAsp(true);
                stampTree.setStampBox(getContext());
                StampTreePanel treePanel = new StampTreePanel(stampTree);
                this.addTab(stampTree.getTreeName(), treePanel);
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}