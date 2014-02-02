package open.dolphin.client;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StmapTreeMenuBuilder
 *
 * @author Kazushi Minagawa
 */
public class StmapTreePopupBuilder {
    
    private static final Icon icon = ClientContext.getImageIcon("foldr_16.gif");
    
    private Hashtable<Object, JMenu> parents;
    private JPopupMenu popup;
    private DefaultMutableTreeNode rootNode;
    
    public StmapTreePopupBuilder() {
    }
    
    public void build(StampTree stampTree, JPopupMenu popup, JComponent cmp, TransferHandler handler) {
        
        if (parents == null) {
            parents = new Hashtable<Object, JMenu>(10, 0.75f);
        } else {
            parents.clear();
        }
        
        this.popup = popup;
        
        rootNode = (DefaultMutableTreeNode) stampTree.getModel().getRoot();
        Enumeration e = rootNode.preorderEnumeration();
        e.nextElement(); // consume root
        
        while (e.hasMoreElements()) {
            parseChildren((StampTreeNode) e.nextElement(), cmp, handler);
        }
    }
    
    private void parseChildren(StampTreeNode node, JComponent comp, TransferHandler handler) {
        
        if (!node.isLeaf()) {
            JMenu subMenu = new JMenu(node.getUserObject().toString());
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            if (parentNode == rootNode) {
                popup.add(subMenu);
            } else {
                JMenu parent = parents.get(node.getParent());
                parent.add(subMenu);
            }
            parents.put(node, subMenu);
            
            // ”z‰º‚ÌŽq‚ð‘S‚Ä—ñ‹“‚µJmenuItem‚É‚Ü‚Æ‚ß‚é
            JMenuItem item = new JMenuItem(node.getUserObject().toString());
            item.setIcon(icon);
            subMenu.add(item);
            
            if (comp != null && handler != null) {
                item.addActionListener(new TransferAction(comp, handler, new LocalStampTreeNodeTransferable(node)));
            } else {
                item.setEnabled(false);
            }
            
        } else {
            ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
            JMenuItem item = new JMenuItem(info.getStampName());
            if (comp != null && handler != null) {
                item.addActionListener(new TransferAction(comp, handler, new LocalStampTreeNodeTransferable(node)));
            } else {
                item.setEnabled(false);
            }
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            if (parentNode == rootNode) {
                popup.add(item);
            } else {
                JMenu parent = parents.get(node.getParent());
                parent.add(item);
            }
        }
    }
}














