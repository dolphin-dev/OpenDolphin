package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StampTreePopupAdapter
 *
 * @author  Kazushi Minagawa
 */
public class StampTreePopupAdapter extends MouseAdapter {
    
    public StampTreePopupAdapter() {
    }
    
    @Override
    public void mousePressed(MouseEvent evt) {
        maybePopup(evt);
    }
    
    @Override
    public void mouseReleased(MouseEvent evt) {
        maybePopup(evt);
    }
    
    private void maybePopup(MouseEvent evt) {
        
        if (evt.isPopupTrigger()) {
            
            // イベントソースの StampTree を取得する
            StampTree tree = (StampTree) evt.getSource();
            int x = evt.getX();
            int y = evt.getY();
            
            // クリック位置へのパスを得る
            TreePath destPath = tree.getPathForLocation(x, y);
            if (destPath == null) {
                return;
            }
            
            // クリック位置の Node を得る
            StampTreeNode node = (StampTreeNode) destPath.getLastPathComponent();
            
            if (node.isLeaf()) {
                // Leaf なので StampInfo 　を得る
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                
                // Editable
                if ( ! info.isEditable() ) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            
            // Popupする
            JPopupMenu popup = PopupMenuFactory.create("stampTree.pop", tree);
            popup.show(evt.getComponent(),x, y);
        }
    }
}