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

    public SchemaHolderTransferHandler() {
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        SchemaHolder source = (SchemaHolder) c;
        KartePane context = source.getKartePane();
        context.setDrragedStamp(new ComponentHolder[]{source});
        context.setDraggedCount(1);
        SchemaModel schema = source.getSchema();
        SchemaList list = new SchemaList();
        list.schemaList = new SchemaModel[]{schema};
        Transferable tr = new SchemaListTransferable(list);
        return tr;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
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

    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        return false;
    }

    /**
	 * スタンプをクリップボードへ転送する。
	 */
    @Override
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
