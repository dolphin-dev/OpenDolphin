package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModelTransferable;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.table.ListTableModel;

/**
 * DiagnosisTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public class DiagnosisTransferHandler extends TransferHandler {
    
    private DiagnosisDocument parent;
    
    public DiagnosisTransferHandler(DiagnosisDocument parent) {
        super();
        this.parent = parent;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable sourceTable = (JTable)c;
        ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>)sourceTable.getModel();
        RegisteredDiagnosisModel dragItem = tableModel.getObject(sourceTable.getSelectedRow());
        return dragItem != null ? new InfoModelTransferable(dragItem) : null;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!canImport(support)) {
            return false;
        }
        
        if (support.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
            return importFromStampTree(support);
            
        } else if (support.isDataFlavorSupported(InfoModelTransferable.infoModelFlavor)) {
            return importFromInfoModel(support);
        }
        return false;
    }
   
    /**
     * StampTreeからの Drop/Paste をインポートする
     * @param support TransferHandler.TransferSupport
     * @return 成功した時 true
     */
    private boolean importFromStampTree(TransferHandler.TransferSupport support) {
        
        try {
            //JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();
            //int toIndex = dl.getRow();
            //boolean insertRow = dl.isInsertRow();   // StampTree->diagTable: false
            
            int toIndex = 0;
            Transferable t = support.getTransferable();

            // Dropされたノードを取得する
            StampTreeNode importNode = (StampTreeNode)t.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);

            // Import するイストを生成する
            ArrayList<ModuleInfoBean> importList = new ArrayList<ModuleInfoBean>(3);

            // 葉の場合
            if (importNode.isLeaf()) {
                ModuleInfoBean stampInfo = (ModuleInfoBean)importNode.getStampInfo();
                if (stampInfo.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) {
                    if (stampInfo.isSerialized()) {
                        importList.add(stampInfo);
                    } else {
                        parent.openEditor2();
                        return true;
                    }

                } else {
                    Toolkit.getDefaultToolkit().beep();
                    return false;
                }

            } else {
                // Dropされたノードの葉を列挙する
                Enumeration e = importNode.preorderEnumeration();
                while (e.hasMoreElements()) {
                    StampTreeNode node = (StampTreeNode)e.nextElement();
                    if (node.isLeaf()) {
                        ModuleInfoBean stampInfo = (ModuleInfoBean)node.getStampInfo();
                        if (stampInfo.isSerialized() && (stampInfo.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) ) {
                            importList.add(stampInfo);
                        }
                    }
                }
            }

            // まとめてデータベースからフェッチしインポートする
            if (!importList.isEmpty()) {
                parent.importStampList(importList, toIndex);
                return true;

            } else {
                return false;
            }
            
        } catch (Exception ioe) {
            ioe.printStackTrace(System.err);
        }
        
        return false;
    }
    
    /**
     * 傷病名Tableからの Paste をインポートする
     * @param support TransferHandler.TransferSupport
     * @return 成功した時 true
     */
    private boolean importFromInfoModel(TransferHandler.TransferSupport support) {
        try {
            RegisteredDiagnosisModel rd = (RegisteredDiagnosisModel)support.getTransferable().getTransferData(InfoModelTransferable.infoModelFlavor);
            JTable diagTable = (JTable)support.getComponent();
            ListTableModel<RegisteredDiagnosisModel> tableModel = (ListTableModel<RegisteredDiagnosisModel>)diagTable.getModel();
            int selectIndex = diagTable.getSelectedRow();
            if (selectIndex>=0 && selectIndex<tableModel.getObjectCount()) {
                tableModel.addObject(selectIndex, rd);
            } else {
                tableModel.addObject(0,rd);
            }
            return true;
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        return false;
    }
    
    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        
        boolean ok = support.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
        ok = ok || (!support.isDrop() && support.isDataFlavorSupported(InfoModelTransferable.infoModelFlavor));
        return ok;
    }
    
    @Override
    public boolean importData(JComponent c, Transferable t) {
        TransferHandler.TransferSupport support = new TransferHandler.TransferSupport(c,t);
        return importData(support);
    }
}
