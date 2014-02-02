/*
 * Created on 2005/06/01
 *
 */
package open.dolphin.client;

import javax.swing.tree.DefaultMutableTreeNode;

import open.dolphin.project.ObjectBox;

/**
 * ObjectTreeNode
 * 
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ObjectTreeNode extends DefaultMutableTreeNode {
	
    private static final long serialVersionUID = -2595126726183270328L;

	public ObjectTreeNode(Object userObject) {
        
        super(userObject);
        
        // class 名を保持している場合は葉ノード
        // クラス名を保持していない場合はカテゴリ等を表す
        ObjectBox box = (ObjectBox)userObject;
        if (box.getClassName() != null) {
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
