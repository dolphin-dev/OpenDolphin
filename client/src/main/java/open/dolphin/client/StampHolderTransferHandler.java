package open.dolphin.client;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.*;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.project.Project;
import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.util.BeanUtils;

/**
 * StampHolderTransferHandler
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class StampHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private final KartePane pPane;
    private final StampHolder stampHolder;

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
        
        Runnable r = () -> {
            try {
                StampDelegater sdl = new StampDelegater();
                StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
                final ModuleModel stamp = new ModuleModel();
                if (getStamp != null) {
                    stamp.setModel((IInfoModel) BeanUtils.xmlDecode(getStamp.getStampBytes()));
                    stamp.setModuleInfoBean(stampInfo);
                }
                Runnable awt = () -> {
                    target.importStamp(stamp);
                };
                EventQueue.invokeLater(awt);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private void confirmReplace(StampHolder target, ModuleInfoBean stampInfo) {
        
        Window w = SwingUtilities.getWindowAncestor(target);
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampHolderTransferHandler.class);
        String replace = bundle.getString("optionText.replace");
        String cancel = GUIFactory.getCancelButtonText();
        String question = bundle.getString("question.replaceStamp");
        String title = bundle.getString("title.optionPane");
        title = ClientContext.getFrameTitle(title);
        
         int option = JOptionPane.showOptionDialog(
                 w, 
                 question, 
                 title, 
                 JOptionPane.DEFAULT_OPTION, 
                 JOptionPane.QUESTION_MESSAGE, 
                 null, 
                 new String[]{replace, cancel}, replace);
         
         if (option==0) {
             replaceStamp(target, stampInfo);
         }
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {    

        if (canImport(support)) {    
            
            final StampHolder target = (StampHolder)support.getComponent();
            Transferable tr = support.getTransferable();
            StampTreeNode droppedNode;

            try {
                droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
                
            } catch (UnsupportedFlavorException | IOException e) {
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
                Runnable r = () -> {
                    confirmReplace(target, stampInfo);
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
//s.oh^ 2013/11/26 スクロールバーのリセット
            //context.removeStamp(test); // TODO 
            context.removeStamp(test, false);
//s.oh$
        }
        context.setDrragedStamp(null);
        context.setDraggedCount(0);
        context.setDroppedCount(0);
    }

    /**
     * インポート可能かどうかを返す。
     * @return 
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        StampHolder test = (StampHolder)support.getComponent();
        JTextPane tc = (JTextPane) test.getKartePane().getTextPane();
        boolean ok = tc.isEditable();
        ok = ok && hasFlavor(support.getDataFlavors());
        return ok;
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
//s.oh^ 2013/11/26 スクロールバーのリセット
                //kartePane.removeStamp(sh);
                kartePane.removeStamp(sh, true);
//s.oh$
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
