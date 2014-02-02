/*
 * Created on 2005/06/01
 *
 */
package open.dolphin.plugin.helper;

import javax.swing.tree.DefaultMutableTreeNode;

import open.dolphin.plugin.PluginReference;

/**
 * PluginReferenceTreeNode
 * 
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class PluginReferenceTreeNode extends DefaultMutableTreeNode {
	
    private static final long serialVersionUID = -8868941741690163273L;

	public PluginReferenceTreeNode(Object userObject) {
        
        super(userObject);
        
        // class 名を保持している場合は葉ノード
        // クラス名を保持していない場合はカテゴリ等を表す
        PluginReference ref = (PluginReference)userObject;
        if (ref.getClassName() != null) {
            this.allowsChildren = false;
        }
    }
    
    /**
     * 葉かどうかを返す
     */
    public boolean isLeaf () {
        return (! this.allowsChildren);
    }
}
