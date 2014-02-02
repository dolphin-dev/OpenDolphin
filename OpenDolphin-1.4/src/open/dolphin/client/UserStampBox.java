package open.dolphin.client;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import open.dolphin.infomodel.IInfoModel;

/**
 * UserStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class UserStampBox extends AbstractStampBox {
    
    private static final String BOX_INFO = "個人用スタンプボックス";
    
    /** テキストスタンプのタブ番号 */
    private int textIndex;
    
    /** パススタンプのタブ番号 */
    private int pathIndex;
    
    /** ORCA セットのタブ番号 */
    private int orcaIndex;

    /**
     * StampBox を構築する。
     */
    protected void buildStampBox() {
        
        try {
            //
            // Build stampTree
            //
            BufferedReader reader = new BufferedReader(new StringReader(stampTreeModel.getTreeXml()));
            DefaultStampTreeBuilder builder = new DefaultStampTreeBuilder();
            StampTreeDirector director = new StampTreeDirector(builder);
            List<StampTree> userTrees = director.build(reader);
            reader.close();
            stampTreeModel.setTreeXml(null);
            stampTreeModel.setTreeBytes(null);
            
            // StampTreeへ設定するPopupMenuとTransferHandlerを生成する
            StampTreePopupAdapter popAdapter = new StampTreePopupAdapter();
            StampTreeTransferHandler transferHandler = new StampTreeTransferHandler();
            
            // StampBox(TabbedPane) へリスト順に格納する
            // 一つのtabへ一つのtreeが対応
            int index = 0;
            for (StampTree stampTree : userTrees) {
                stampTree.setUserTree(true);
                stampTree.setTransferHandler(transferHandler);
                stampTree.setStampBox(getContext());
                StampTreePanel treePanel = new StampTreePanel(stampTree);
                this.addTab(stampTree.getTreeName(), treePanel);
                
                //
                // Text、Path、ORCA のタブ番号を保存する
                //
                if (stampTree.getEntity().equals(IInfoModel.ENTITY_TEXT)) {
                    textIndex = index;
                    stampTree.addMouseListener(popAdapter);
                } else if (stampTree.getEntity().equals(IInfoModel.ENTITY_PATH)) {
                    pathIndex = index;
                    stampTree.addMouseListener(popAdapter);
                } else if (stampTree.getEntity().equals(IInfoModel.ENTITY_ORCA)) {
                    orcaIndex = index;
                } else {
                    stampTree.addMouseListener(popAdapter);
                }
                
                index++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * 引数のタブ番号に対応するStampTreeにエディタから発行があるかどうかを返す。
     * @param index タブ番号
     * @return エディタから発行がある場合に true 
     */
    @Override
    public boolean isHasEditor(int index) {
        return (index == textIndex || index == pathIndex || index == orcaIndex) ? false : true;
    }

    @Override
    public void setHasNoEditorEnabled(boolean b) {
        this.setEnabledAt(textIndex, b);
        this.setEnabledAt(pathIndex, b);
        this.setEnabledAt(orcaIndex, b);
    }
    
    @Override
    public String getInfo() {
        return BOX_INFO;
    }
}