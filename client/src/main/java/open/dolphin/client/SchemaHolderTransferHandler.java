package open.dolphin.client;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.SchemaModel;


/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class SchemaHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private KartePane soaPane;
    private SchemaHolder schemaHolder;

    public SchemaHolderTransferHandler(KartePane soaPane, SchemaHolder sh) {
        this.soaPane = soaPane;
        this.schemaHolder = sh;
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
//minagawa^ Paste problem    
//    public boolean canImport(JComponent c, DataFlavor[] flavors) {
//        return false;
//    }
    public boolean canImport(TransferHandler.TransferSupport support) {
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

    @Override
    public JComponent getComponent() {
        return schemaHolder;
    }

    @Override
    public void enter(ActionMap map) {
        schemaHolder.setSelected(true);
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        boolean caCunt = (soaPane.getTextPane().isEditable());
        map.get(GUIConst.ACTION_CUT).setEnabled(caCunt);
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
    }

    @Override
    public void exit(ActionMap map) {
        schemaHolder.setSelected(false);
    }
}
