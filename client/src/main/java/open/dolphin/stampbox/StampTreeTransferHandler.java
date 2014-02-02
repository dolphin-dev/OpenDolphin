package open.dolphin.stampbox;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import open.dolphin.client.LocalStampTreeNodeTransferable;
import open.dolphin.client.OrderList;
import open.dolphin.client.OrderListTransferable;
import open.dolphin.infomodel.*;

/**
 * StampTreeTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class StampTreeTransferHandler extends TransferHandler {

    // Drag元のStampTree
    private StampTree sourceTree;

    // StampTreeNode Flavor
    private DataFlavor stampTreeNodeFlavor = LocalStampTreeNodeTransferable.localStampTreeNodeFlavor;

    // KartePaneからDropされるオーダのFlavor
    private DataFlavor orderFlavor = OrderListTransferable.orderListFlavor;

    // KartePaneからDropされるテキストFlavor
    private DataFlavor stringFlavor = DataFlavor.stringFlavor;
    
    // 病名エディタからDropされるRegisteredDiagnosis Flavor
    private DataFlavor infoModelFlavor = InfoModelTransferable.infoModelFlavor;

    
    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTree = (StampTree)c;
        StampTreeNode dragNode = (StampTreeNode)sourceTree.getLastSelectedPathComponent();
        return dragNode!=null ? new LocalStampTreeNodeTransferable(dragNode) : null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }
        
        StampTree target;
        String targetEntity;
        StampTreeNode parentNode;
        int childIndex;
        
        target = (StampTree)support.getComponent();
        targetEntity = target.getEntity();
        
        if (support.isDrop()) {
            JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
            TreePath path = dl.getPath();
            childIndex = dl.getChildIndex();
            parentNode = (StampTreeNode)path.getLastPathComponent();
        } else {
            // Paste
            StampTreeNode selected = target.getSelectedNode();
            parentNode = (StampTreeNode)selected.getParent();
            childIndex = parentNode.getIndex(selected);
        }
        
        Transferable tr = support.getTransferable();

        try {
            if (support.isDataFlavorSupported(orderFlavor)) {
                //----------------------------------------------
                // KartePaneからのオーダースタンプ
                //----------------------------------------------
                OrderList list = (OrderList)tr.getTransferData(orderFlavor);
                ModuleModel importStamp = list.orderList[0];   // ToDo multiple drag & drop
                
                // インポートするentity
                String importEntity = importStamp.getModuleInfoBean().getEntity();

                if (importEntity.equals(targetEntity)) {
                    //----------------------------------
                    // targetとdropが同じentityの場合
                    //----------------------------------
                    return target.addStamp(parentNode, importStamp, childIndex);
                    
                } else if (labtestRelated(importEntity, targetEntity)) {
                    //----------------------------------
                    // Labtest,Physiology,Bacteria間の相互入れ替え
                    //----------------------------------
                    importStamp.getModuleInfoBean().setEntity(targetEntity);    // entityをDrop先に変換する
                    return target.addStamp(parentNode, importStamp, childIndex);

                } else if (targetEntity.equals(IInfoModel.ENTITY_PATH)) {
                    //---------------------
                    // パス Tree の場合
                    //---------------------
                    return target.addStamp(parentNode, importStamp, childIndex);

                } else {
                    return false;
                }

            } else if (support.isDataFlavorSupported(stringFlavor)) {
                //-----------------------------------------
                // KartePaneからDropされたテキストをインポートする
                //-----------------------------------------
                String text = (String)tr.getTransferData(stringFlavor);
                if (targetEntity.equals(IInfoModel.ENTITY_TEXT) || targetEntity.equals(IInfoModel.ENTITY_PATH)) {
                    return target.addTextStamp(parentNode, text, childIndex);
                } else {
                    return false;
                }
                
            } else if (support.isDataFlavorSupported(infoModelFlavor)) {
                //------------------------------------------------------------------
                // DiagnosisEditorからDropされた病名をインポートする
                // 病名Treeに加え、PathTreeにもDrop可能とする->パススタンプに病名のセットが可能 
                //------------------------------------------------------------------
                RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel)tr.getTransferData(InfoModelTransferable.infoModelFlavor);
                if (targetEntity.equals(IInfoModel.ENTITY_DIAGNOSIS) ||
                    targetEntity.equals(IInfoModel.ENTITY_PATH)) {
                    return target.addDiagnosis(parentNode, rd, childIndex);
                } else {
                    return false;
                }

            } else if (support.isDataFlavorSupported(stampTreeNodeFlavor)) {
                //-----------------------------------------------
                // StampTreeNodeの Drop/Paste
                // isDrop()==true は同じTree内のみ(GUIで同じTree内でしかDnDできない）
                // paste(!isDrop()) の場合は 同じTree内のcopy/paste もしくは target=drop先が汎用の場合のみ
                //-----------------------------------------------
                StampTreeNode importNode = (StampTreeNode)tr.getTransferData(stampTreeNodeFlavor);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)importNode;
                StampTreeNode root = (StampTreeNode)node.getRoot();
                TreeInfo info = (TreeInfo)root.getUserObject();
                String importEntity = info.getEntity();
                
                //------------------------------------------------------------------------
                // root までの親のパスのなかに自分がいるかどうかを判定する
                // Drop先が DragNode の子である時は DnD できない i.e 親が自分の子になることはできない
                //------------------------------------------------------------------------
                DefaultTreeModel model = (DefaultTreeModel)target.getModel();
                TreeNode[] parents = model.getPathToRoot(parentNode);
                boolean exist = false;
                for (TreeNode parent : parents) {
                    if (parent == (TreeNode)importNode) {
                        exist = true;
                        Toolkit.getDefaultToolkit().beep();
                        break;
                    }
                }

                if (exist) {
                    return false;
                }

                if (childIndex < 0) {
                    childIndex = 0;
                }

                // dropNodeの親==parentNodeの場合
                // childIndexを補正する(dropNodeを最初に削除するため）
                if (importNode.getParent()==parentNode) {
                    int cnt = parentNode.getChildCount();
                    for (int i = 0; i < cnt; i++) {
                        if (parentNode.getChildAt(i)==importNode) {
                            childIndex = childIndex > i ? childIndex-1 : childIndex;
                            break;
                        }
                    }
                }
  
                // DnD in the same tree
                if (support.isDrop()) {
                    model.removeNodeFromParent(importNode);
                    model.insertNodeInto(importNode, parentNode, childIndex);
                    return true;
                
                } else if (importNode.isLeaf()) {
                    // pasteは leafのみ StampTreePopupAdapter
                    // copy paste の場合はcloneをpasteする
                    ModuleInfoBean dropBean = (ModuleInfoBean)importNode.getUserObject();
                    try {
                        ModuleInfoBean clone = (ModuleInfoBean)dropBean.clone();
                        if (labtestRelated(importEntity, targetEntity)) {
                            // LabTest, Physio, Bacteria の入れ替え
                            clone.setEntity(targetEntity);
                        }
                        StampTreeNode cloneNode = new StampTreeNode(clone);
                        model.insertNodeInto(cloneNode, parentNode, childIndex);
                        return true;
                        
                    } catch (CloneNotSupportedException ex) {
                        return false;
                    }
                    
                } else {    
                    return false;
                }
                     
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

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return (support.isDataFlavorSupported(orderFlavor) ||
                support.isDataFlavorSupported(stringFlavor) ||
                support.isDataFlavorSupported(infoModelFlavor) ||
                support.isDataFlavorSupported(stampTreeNodeFlavor));
    }
    
    // 検体検査、生体検査、細菌検査の相互入れ替え
    private boolean labtestRelated(String importEntity, String targetEntity) {
        
        boolean match = (importEntity.equals(IInfoModel.ENTITY_LABO_TEST) && 
                        (targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)||targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));
                
        match = match || (importEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) && 
                    (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST)||targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));

        match = match || (importEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER) && 
                    (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST)||targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)));
        
        return match;
    }
}
