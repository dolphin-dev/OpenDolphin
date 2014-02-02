/*
 * Created on 2005/09/23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.client;

import java.awt.datatransfer.*;

import javax.swing.*;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.util.BeanUtils;


/**
 * StampHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class StampHolderTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = -9182879162438446790L;
	
	public StampHolderTransferHandler() {
	}
	
    protected Transferable createTransferable(JComponent c) {
		StampHolder source = (StampHolder) c;
		KartePane context = source.getKartePane();
		context.setDrragedStamp(new IComponentHolder[]{source});
		context.setDraggedCount(1);
		ModuleModel stamp = source.getStamp();
		OrderList list = new OrderList(new ModuleModel[]{stamp});
	    	Transferable tr = new OrderListTransferable(list);
	    	return tr;
    }

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}
	
	public boolean importData(JComponent c, Transferable tr) {
		
		if (canImport(c, tr.getTransferDataFlavors())) {
			try {
				final StampHolder target = (StampHolder) c;
				StampTreeNode droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
				if (droppedNode.isLeaf()) {
					final ModuleInfoBean stampInfo = (ModuleInfoBean) droppedNode.getStampInfo();
					String role = stampInfo.getStampRole();
					if (role.equals(IInfoModel.ROLE_P)) {
						Runnable r = new Runnable() {
							public void run() {
								StampDelegater sdl = new StampDelegater();
								StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
								final ModuleModel stamp = new ModuleModel();
								if (getStamp != null) {
									stamp.setModel((IInfoModel) BeanUtils.xmlDecode(getStamp.getStampBytes()));
									stamp.setModuleInfo(stampInfo);
								}
								Runnable awt = new Runnable() {
									public void run() {
										target.importStamp(stamp);
									}
								};
								SwingUtilities.invokeLater(awt);
							}
						};
						Thread t = new Thread(r);
						t.setPriority(Thread.NORM_PRIORITY);
						t.start();
						return true;
					}
				}
			} catch (Exception e) {
			}
		}
		return false;
	}

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
}
