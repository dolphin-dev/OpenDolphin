package open.dolphin.client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * AspStampTreeTransferHandler
 * 
 * @author Minagawa,Kazushi
 * 
 */
public class AspStampTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1205897976539749194L;

	protected Transferable createTransferable(JComponent c) {
		StampTree sourceTree = (StampTree) c;
		StampTreeNode dragNode = (StampTreeNode) sourceTree.getLastSelectedPathComponent();
		return new LocalStampTreeNodeTransferable(dragNode);
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public boolean importData(JComponent c, Transferable tr) {
		return false;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return false;
	}
}