/*
 * Created on 2005/09/23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.client;

import java.awt.datatransfer.*;

import javax.swing.*;

import open.dolphin.infomodel.SchemaModel;


/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class SchemaHolderTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = -1293765478832142035L;
    
	public SchemaHolderTransferHandler() {
	}

    protected Transferable createTransferable(JComponent c) {
		SchemaHolder source = (SchemaHolder) c;
		KartePane context = source.getKartePane();
		context.setDrragedStamp(new IComponentHolder[]{source});
		context.setDraggedCount(1);
		SchemaModel schema = source.getSchema();
		SchemaList list = new SchemaList();
		list.schemaList = new SchemaModel[]{schema};
    		Transferable tr = new SchemaListTransferable(list);
    		return tr;
    }

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

    protected void exportDone(JComponent c, Transferable data, int action) {
    		SchemaHolder test = (SchemaHolder) c;
		KartePane context = test.getKartePane();
		if (action == MOVE && 
		    context.getDrragedStamp() != null &&
		    context.getDraggedCount() == context.getDroppedCount()) {
			context.removeSchema(test); // TODO 
		}
		context.setDrragedStamp(null);
		context.setDraggedCount(0);
		context.setDroppedCount(0);
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
    		return false;
    }
    
	/**
	 * スタンプをクリップボードへ転送する。
	 */
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
		SchemaHolder sh = (SchemaHolder) comp;
		Transferable tr = createTransferable(comp);
		clip.setContents(tr, null);
		if (action == MOVE) {
			KartePane kartePane = sh.getKartePane();
			if (kartePane.getTextPane().isEditable()) {
				kartePane.removeSchema(sh);
			}
		}
	}
}
