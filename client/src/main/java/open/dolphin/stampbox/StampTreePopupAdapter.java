package open.dolphin.stampbox;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import open.dolphin.client.LocalStampTreeNodeTransferable;
import open.dolphin.client.OrderList;
import open.dolphin.client.OrderListTransferable;
import open.dolphin.client.ReflectAction;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModelTransferable;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;

/**
 * StampTreePopupAdapter
 *
 * @author  Kazushi Minagawa
 */
public class StampTreePopupAdapter extends MouseAdapter {
    
    private static final String[] POP_MENUS = {"新規フォルダ","名称変更","-","削 除"};
    private static final String[] POP_METHODS = {"createNewFolder","renameNode","-","deleteNode"};
    
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
            StampTree tree = (StampTree)evt.getSource();
            int x = evt.getX();
            int y = evt.getY();
            
            // クリック位置へのパスを得る
            TreePath destPath = tree.getPathForLocation(x, y);
            if (destPath == null) {
                return;
            }
            
            // クリック位置の Node を得る
            StampTreeNode node = (StampTreeNode)destPath.getLastPathComponent();
            
            // Copy
            boolean canCopy = true;
            
            // エディタから発行...はコピーできない
            if (node.isLeaf()) {
                // Leaf なので StampInfo 　を得る
                ModuleInfoBean info = (ModuleInfoBean)node.getUserObject();
                
                // Editable
                if (!info.isEditable() ) {
                    //Toolkit.getDefaultToolkit().beep();
                    //return;
                    canCopy = false;
                }
            }
            
            // Paste は厄介
            boolean canPaste = canPaste(tree.getEntity());
            
            // Popupする
            JPopupMenu popup = createPopuoMenu(tree, canCopy, canPaste);
            popup.show(evt.getComponent(),x, y);
        }
    }
    
    private JPopupMenu createPopuoMenu(final JTree tree, boolean canCopy, boolean canPaste) {
        
        
        AbstractAction copy = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Action a = tree.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                if (a != null) {
                    a.actionPerformed(new ActionEvent(tree,
                            ActionEvent.ACTION_PERFORMED,
                            null));
                }
            }
        };
        copy.setEnabled(canCopy);
        
        AbstractAction paste = new AbstractAction("ペースト") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Action a = tree.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                if (a != null) {
                    a.actionPerformed(new ActionEvent(tree,
                            ActionEvent.ACTION_PERFORMED,
                            null));
                }
            }
        };
        paste.setEnabled(canPaste);
        
        JPopupMenu popMenu = new JPopupMenu ();
        popMenu.add(new JMenuItem(copy));
        popMenu.add(new JMenuItem(paste));
        popMenu.addSeparator();
        
        for (int i = 0; i < POP_MENUS.length; i++) {
            
            String name = POP_MENUS[i];
            String method = POP_METHODS[i];
            
            if (name.equals("-")) {
                popMenu.addSeparator();
            }
            else {
                ReflectAction action = new ReflectAction(name, (Object)tree, method);
                JMenuItem item = new JMenuItem(action);
                popMenu.add(item);
            }
        }
        return popMenu;
    }
    
    /**
     * クリップボードのコンテントがPaste可能かどうかを返す。
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPaste(String targetEntity) {
        
        // Clipboard内のTransferable
        Transferable tr = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (tr==null) {
            return false;
        }
        
        // カルテペインからのペースト
        if (tr.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
            return canPasteOrder(tr, targetEntity); 
        }
        
        // Textペースト
        if (tr.isDataFlavorSupported(DataFlavor.stringFlavor) &&
            (targetEntity.equals(IInfoModel.ENTITY_TEXT)||targetEntity.equals(IInfoModel.ENTITY_PATH))){
            return true;
        }
        
        // 病名ペースト
        if (tr.isDataFlavorSupported(InfoModelTransferable.infoModelFlavor)) {
            boolean pasteOk = (targetEntity.equals(IInfoModel.ENTITY_DIAGNOSIS));
            pasteOk = pasteOk || (targetEntity.equals(IInfoModel.ENTITY_PATH));
            return pasteOk;
        }
        
        // StampTreeNodeペースト
        if (tr.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
            return canPasteTreeNode(tr, targetEntity);
        }
        
        return false;
    }
    
    /**
     * オーダーがペースト可能かどうかを返す。
     * @param tr オーダーを保持しているTransferable
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPasteOrder(Transferable tr, String targetEntity) {
        try {
            OrderList list = (OrderList)tr.getTransferData(OrderListTransferable.orderListFlavor);
            ModuleModel pasteStamp = list.orderList[0];   // ToDo multiple drag & drop
            String pasteEntity = pasteStamp.getModuleInfoBean().getEntity(); // testStamp
            // 同一entity
            boolean match = pasteEntity.equals(targetEntity);
        
            // 受けてがパスの場合
            match = match || (targetEntity.equals(IInfoModel.ENTITY_PATH));
            
            return match;
            
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        return false;
    }
    
    /**
     * StampTreeNodeがペースト可能かどうかを返す。
     * @param tr StampTreeNodeを保持しているTransferable
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPasteTreeNode(Transferable tr, String targetEntity) {
        try {
            StampTreeNode test = (StampTreeNode)tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
            
            // 葉以外はfalse
            if (!test.isLeaf()) {
                return false;
            }
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)test;
            StampTreeNode root = (StampTreeNode)node.getRoot();

            Object o = root.getUserObject();
            
            if (o!=null && o instanceof TreeInfo) {
                TreeInfo info = (TreeInfo)o;
                return entityMatch(info.getEntity(), targetEntity);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * Entity間のマッチングを返す。
     * @param pasteEntity ペーストするentity
     * @param targetEntity ペースト先のentity
     * @return ペースト可能な時 true
     */
    private boolean entityMatch(String pasteEntity, String targetEntity) {
        
        // 同一entity
        boolean match = pasteEntity.equals(targetEntity);
        
        // 受けてがパスの場合
        match = match || (targetEntity.equals(IInfoModel.ENTITY_PATH));
        
        // 検体検査 -> （生体検査 | 細菌検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_LABO_TEST) &&
                            (targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));
        
        // 生体検査 -> （検体検査 | 細菌検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) &&
                            (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));
        
        // 細菌検査 -> （検体検査 | 生体検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER) &&
                            (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)));
        
        return match;
    }
}