package open.dolphin.client;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.project.Project;
import open.dolphin.util.BeanUtils;

/**
 * StampHolderTransferHandler
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class StampHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private KartePane pPane;
    private StampHolder stampHolder;

    public StampHolderTransferHandler(KartePane pPane, StampHolder sh) {
        this.pPane = pPane;
        this.stampHolder = sh;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        StampHolder source = (StampHolder) c;
        KartePane context = source.getKartePane();
        context.setDrragedStamp(new ComponentHolder[]{source});
        context.setDraggedCount(1);
        ModuleModel stamp = source.getStamp();
        OrderList list = new OrderList(new ModuleModel[]{stamp});
        Transferable tr = new OrderListTransferable(list);
        return tr;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    private void replaceStamp(final StampHolder target, final ModuleInfoBean stampInfo) {
        
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    StampDelegater sdl = new StampDelegater();
                    StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
                    final ModuleModel stamp = new ModuleModel();
                    if (getStamp != null) {
                        stamp.setModel((IInfoModel) BeanUtils.xmlDecode(getStamp.getStampBytes()));
                        stamp.setModuleInfoBean(stampInfo);
                    }
                    Runnable awt = new Runnable() {

                        @Override
                        public void run() {
                            target.importStamp(stamp);
                        }
                    };
                    EventQueue.invokeLater(awt);
                    
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private void confirmReplace(StampHolder target, ModuleInfoBean stampInfo) {
        
        Window w = SwingUtilities.getWindowAncestor(target);
        String replace = "置き換える";
        String cancel = "取消し";
        
         int option = JOptionPane.showOptionDialog(
                 w, 
                 "スタンプを置き換えますか?", 
                 "スタンプ Drag and Drop", 
                 JOptionPane.DEFAULT_OPTION, 
                 JOptionPane.QUESTION_MESSAGE, 
                 null, 
                 new String[]{replace, cancel}, replace);
         
         if (option == 0) {
             replaceStamp(target, stampInfo);
         }
    }

    @Override
    public boolean importData(JComponent c, Transferable tr) {

        if (canImport(c, tr.getTransferDataFlavors())) {
            
            final StampHolder target = (StampHolder) c;
            StampTreeNode droppedNode = null;

            try {
                droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return false;
            }
            
            if (droppedNode == null || (!droppedNode.isLeaf())) {
                return false;
            }

            final ModuleInfoBean stampInfo = (ModuleInfoBean) droppedNode.getStampInfo();
            String role = stampInfo.getStampRole();
            
            if (!role.equals(IInfoModel.ROLE_P)) {
                return false;
            }
            
            if (Project.getBoolean("replaceStamp", false)) {
                replaceStamp(target, stampInfo);
                
            } else {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        confirmReplace(target, stampInfo);
                    }
                };
                EventQueue.invokeLater(r);
            } 
            return true;
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable tr, int action) {
        StampHolder test = (StampHolder) c;
        KartePane context = test.getKartePane();
        if (action == MOVE &&
                context.getDrragedStamp() != null &&
                context.getDraggedCount() == context.getDroppedCount()) {
            context.removeStamp(test); // TODO 
        }
        context.setDrragedStamp(null);
        context.setDraggedCount(0);
        context.setDroppedCount(0);
    }

    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        StampHolder test = (StampHolder) c;
        JTextPane tc = (JTextPane) test.getKartePane().getTextPane();
        if (tc.isEditable() && hasFlavor(flavors)) {
            return true;
        }
        return false;
    }

    protected boolean hasFlavor(DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (LocalStampTreeNodeTransferable.localStampTreeNodeFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * スタンプをクリップボードへ転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        StampHolder sh = (StampHolder) comp;
        Transferable tr = createTransferable(comp);
        clip.setContents(tr, null);
        if (action == MOVE) {
            KartePane kartePane = sh.getKartePane();
            if (kartePane.getTextPane().isEditable()) {
                kartePane.removeStamp(sh);
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return stampHolder;
    }

    @Override
    public void enter(ActionMap map) {
        stampHolder.setSelected(true);
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        boolean canCut = (pPane.getTextPane().isEditable());
        map.get(GUIConst.ACTION_CUT).setEnabled(canCut);
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
    }

    @Override
    public void exit(ActionMap map) {
        stampHolder.setSelected(false);
    }
}
