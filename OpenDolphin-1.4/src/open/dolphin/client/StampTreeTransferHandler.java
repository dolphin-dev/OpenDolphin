package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
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
 * @author Minagawa,Kazushi
 *
 */
public class StampTreeTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1205897976539749194L;
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
     
    
    ;
    
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
    public boolean importData(JComponent c, Transferable tr) {

        if (canImport(c, tr.getTransferDataFlavors())) {

            try {
                // Dropを受けるStampTreeを取得する
                StampTree target = (StampTree) c;
                String targetEntity = target.getEntity();

                //
                // Drop位置のノードを取得する
                // DnD によって選択状態になっている
                //
                StampTreeNode selected = (StampTreeNode) target.getLastSelectedPathComponent();
                StampTreeNode newParent = null;

                if (selected != null) {
                    //
                    // Drop位置の親を取得する
                    //
                    newParent = (StampTreeNode) selected.getParent();

                } else {
                    // まだ一つもスタンプを持たない初期状態の
                    // TextStamp 等、root(表示されない)しか持たない場合は
                    // ここへくる
                    selected = null;
                    newParent = null;
                }

                //
                // FlavorがStampTreeNodeの時
                // StampTree 内の DnD
                //
                if (tr.isDataFlavorSupported(stampTreeNodeFlavor) && (selected != null)) {

                    // Dropされるノードを取得する
                    StampTreeNode dropNode = (StampTreeNode) tr.getTransferData(stampTreeNodeFlavor);

                    //
                    // root までの親のパスのなかに自分がいるかどうかを判定する
                    // Drop先が DragNode の子である時は DnD できない i.e 親が自分の子になることはできない
                    //
                    DefaultTreeModel model = (DefaultTreeModel) target.getModel();
                    TreeNode[] parents = model.getPathToRoot(selected);
                    boolean exist = false;
                    for (TreeNode parent : parents) {
                        if (parent == (TreeNode) dropNode) {
                            exist = true;
                            Toolkit.getDefaultToolkit().beep();
                            System.out.println("new Child is ancestor");
                            break;
                        }
                    }

                    if (exist) {
                        return true;
                    }

                    // newChild is ancestor のケース
                    if (newParent != dropNode) {

                        // Drag元のStampTreeとDropされるTreeが同じかどうかを判定する
                        // shouldRemove = (sourceTree == target) ? true : false;
                        // Tree内のDnDはLocalTransferable(参照)の故、挿入時点で元のスタンプを
                        // 常に削除する。DnD後の削除は行わない。
                        // shouldRemove = false;

                        if (selected.isLeaf()) {
                            //
                            // Drop位置のノードが葉の場合、その前に挿入する
                            //
                            int index = newParent.getIndex(selected);
                            //DefaultTreeModel model = (DefaultTreeModel) target.getModel();

                            try {
                                model.removeNodeFromParent(dropNode);
                                model.insertNodeInto(dropNode, newParent, index);
                                TreeNode[] path = model.getPathToRoot(dropNode);
                                ((JTree) target).setSelectionPath(new TreePath(path));

                            } catch (Exception e1) {
                                Toolkit.getDefaultToolkit().beep();
                                e1.printStackTrace();
                            }

                        } else if (dropNode != selected) {
                            //
                            // Drop位置のノードが子を持つ時、最後の子として挿入する
                            // 
                            try {
                                model.removeNodeFromParent(dropNode);
                                model.insertNodeInto(dropNode, selected, selected.getChildCount());
                                TreeNode[] path = model.getPathToRoot(dropNode);
                                ((JTree) target).setSelectionPath(new TreePath(path));

                            } catch (Exception ee) {
                                ee.printStackTrace();
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    }

                    return true;

                } else if (tr.isDataFlavorSupported(orderFlavor)) {
                    //
                    // KartePaneからDropされたオーダをインポートする
                    // 
                    OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
                    ModuleModel droppedStamp = list.orderList[0];

                    //
                    // 同一エンティティの場合、選択は必ず起っている
                    //
                    if (droppedStamp.getModuleInfo().getEntity().equals(targetEntity)) {

                        return target.addStamp(droppedStamp, selected);

                    } else if (targetEntity.equals(IInfoModel.ENTITY_PATH)) {
                        //
                        // パス Tree の場合
                        //
                        if (selected == null) {
                            selected = (StampTreeNode) target.getModel().getRoot();
                        }
                        return target.addStamp(droppedStamp, selected);

                    } else {
                        // Rootの最後に追加する
                        return target.addStamp(droppedStamp, null);
                    }

                } else if (tr.isDataFlavorSupported(stringFlavor)) {
                    //
                    // KartePaneからDropされたテキストをインポートする
                    // 
                    String text = (String) tr.getTransferData(DataFlavor.stringFlavor);
                    if (targetEntity.equals(IInfoModel.ENTITY_TEXT)) {
                        return target.addTextStamp(text, selected);
                    } else {
                        return target.addTextStamp(text, null);
                    }

                } else if (tr.isDataFlavorSupported(infoModelFlavor)) {
                    //
                    // DiagnosisEditorからDropされた病名をインポートする
                    // 
                    RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel) tr.getTransferData(InfoModelTransferable.infoModelFlavor);
                    if (targetEntity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {
                        return target.addDiagnosis(rd, selected);
                    } else {
                        return target.addDiagnosis(rd, null);
                    }

                } else {
                    return false;
                }

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
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
    public boolean canImport(JComponent c, DataFlavor[] flavors) {

        for (DataFlavor flavor : flavors) {
            if (stampTreeNodeFlavor.equals(flavor)) {
                return true;
            }
            if (orderFlavor.equals(flavor)) {
                return true;
            }
            if (stringFlavor.equals(flavor)) {
                return true;
            }
            if (infoModelFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
