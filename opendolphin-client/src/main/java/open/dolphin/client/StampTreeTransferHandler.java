package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModelTransferable;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 * StampTreeTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class StampTreeTransferHandler extends TransferHandler {

    // Drag元のStampTree
    private StampTree sourceTree;

    // Dragされているノード
    private StampTreeNode dragNode;

    // StampTreeNode Flavor
    private DataFlavor stampTreeNodeFlavor = LocalStampTreeNodeTransferable.localStampTreeNodeFlavor;

    // KartePaneからDropされるオーダのFlavor
    private DataFlavor orderFlavor = OrderListTransferable.orderListFlavor;

    // KartePaneからDropされるテキストFlavor
    private DataFlavor stringFlavor = DataFlavor.stringFlavor;
    
    // 病名エディタからDropされるRegisteredDiagnosis Flavor
    private DataFlavor infoModelFlavor = InfoModelTransferable.infoModelFlavor;

    /**
     * 選択されたノードでDragを開始する。
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTree = (StampTree) c;
        dragNode = (StampTreeNode) sourceTree.getLastSelectedPathComponent();
        return new LocalStampTreeNodeTransferable(dragNode);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    /**
     * DropされたFlavorをStampTreeにインポートする。
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        TreePath path = dl.getPath();
        int childIndex = dl.getChildIndex();
        StampTreeNode parentNode = (StampTreeNode)path.getLastPathComponent();
        StampTree target = (StampTree)support.getComponent();
        String targetEntity = target.getEntity();
        Transferable tr = support.getTransferable();

        try {
            if (support.isDataFlavorSupported(orderFlavor)) {
                OrderList list = (OrderList) tr.getTransferData(orderFlavor);
                ModuleModel droppedStamp = list.orderList[0];
                String droppedStampEntity = droppedStamp.getModuleInfoBean().getEntity();

                if (droppedStampEntity.equals(targetEntity)) {
                    return target.addStamp(parentNode, droppedStamp, childIndex);

                } else if (droppedStampEntity.equals(IInfoModel.ENTITY_LABO_TEST) &&
                           (targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER))) {
                    //-----------------------------------------
                    // drop が検体検査で受けが生体もしくは細菌の場合
                    // entity を受側に変更して受け入れる
                    //-----------------------------------------
                    droppedStamp.getModuleInfoBean().setEntity(targetEntity);
                    return target.addStamp(parentNode, droppedStamp, childIndex);

                } else if (droppedStampEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) &&
                               (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER))) {
                    //-----------------------------------------
                    // drop が生体検査で受けが検体もしくは細菌の場合
                    // entity を受側に変更して受け入れる
                    //-----------------------------------------
                    droppedStamp.getModuleInfoBean().setEntity(targetEntity);
                    return target.addStamp(parentNode, droppedStamp, childIndex);

                } else if (droppedStampEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER) &&
                           (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER))) {
                    //-----------------------------------------
                    // drop が細菌検査で受けが検体もしくは生体の場合
                    // entity を受側に変更して受け入れる
                    //-----------------------------------------
                    droppedStamp.getModuleInfoBean().setEntity(targetEntity);
                    return target.addStamp(parentNode, droppedStamp, childIndex);

                } else if (targetEntity.equals(IInfoModel.ENTITY_PATH)) {
                    //---------------------
                    // パス Tree の場合
                    //---------------------
                    return target.addStamp(parentNode, droppedStamp, childIndex);

                } else {
                    // Rootの最後に追加する
                    //return target.addStamp(droppedStamp, null);
                    // これがいいかどうか.....
                    return false;
                }

            } else if (support.isDataFlavorSupported(stringFlavor)) {
                //-----------------------------------------
                // KartePaneからDropされたテキストをインポートする
                //-----------------------------------------
                String text = (String) tr.getTransferData(stringFlavor);
                if (targetEntity.equals(IInfoModel.ENTITY_TEXT)) {
                    return target.addTextStamp(parentNode, text, childIndex);
                } else {
                    return false;
                }
            } else if (support.isDataFlavorSupported(infoModelFlavor)) {
                //----------------------------------------------
                // DiagnosisEditorからDropされた病名をインポートする
                //----------------------------------------------
                RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) tr.getTransferData(InfoModelTransferable.infoModelFlavor);
                if (targetEntity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {
                    return target.addDiagnosis(parentNode, rd, childIndex);
                } else {
                    return false;
                }

            } else if (support.isDataFlavorSupported(stampTreeNodeFlavor)) {
                //-----------------------------------------------
                // StampTree内のDnD, Dropされるノードを取得する
                //-----------------------------------------------
                StampTreeNode dropNode = (StampTreeNode)tr.getTransferData(stampTreeNodeFlavor);
                //------------------------------------------------------------------------
                // root までの親のパスのなかに自分がいるかどうかを判定する
                // Drop先が DragNode の子である時は DnD できない i.e 親が自分の子になることはできない
                //------------------------------------------------------------------------
                DefaultTreeModel model = (DefaultTreeModel)target.getModel();
                TreeNode[] parents = model.getPathToRoot(parentNode);
                boolean exist = false;
                for (TreeNode parent : parents) {
                    if (parent == (TreeNode) dropNode) {
                        exist = true;
                        Toolkit.getDefaultToolkit().beep();
                        break;
                    }
                }

                if (exist) {
                    return false;
                }

                //System.err.println("1:"+ childIndex);

                if (childIndex < 0) {
                    //return false;
                    childIndex = 0;
                }

                // dropNodeの親==parentNodeの場合
                // childIndexを補正する(dropNodeを最初に削除するため）
                if (dropNode.getParent()==parentNode) {
                    int cnt = parentNode.getChildCount();
                    for (int i = 0; i < cnt; i++) {
                        if (parentNode.getChildAt(i)==dropNode) {
                            childIndex = childIndex > i ? childIndex-1 : childIndex;
                            //System.err.println("2:"+ childIndex);
                            break;
                        }
                    }
                }

                // stampTreeNodeFlavorは参照のため最初に削除してから挿入する
                model.removeNodeFromParent(dropNode);
                model.insertNodeInto(dropNode, parentNode, childIndex);
                return true;

            } else {
                return false;
            }

        } catch (UnsupportedFlavorException ue) {
            ue.printStackTrace(System.err);

        } catch (IOException ie) {
            ie.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * DnD後、Dragしたノードを元のStamptreeから削除する。
     */
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
           return false;
        }
        if (support.isDataFlavorSupported(orderFlavor) ||
            support.isDataFlavorSupported(stringFlavor) ||
            support.isDataFlavorSupported(infoModelFlavor) ||
            support.isDataFlavorSupported(stampTreeNodeFlavor)) {
            return true;
        }
        return false;
    }
}
