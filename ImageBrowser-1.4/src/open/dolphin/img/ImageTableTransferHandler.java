package open.dolphin.img;



import java.awt.datatransfer.*;

import javax.swing.*;
import open.dolphin.client.ImageEntry;
import open.dolphin.client.ImageEntryTransferable;


/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class ImageTableTransferHandler extends TransferHandler {

    private static final long serialVersionUID = -1293765478832142035L;

    public ImageTableTransferHandler() {
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable imageTable = (JTable) c;
        int row = imageTable.getSelectedRow();
        int col = imageTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            ImageEntry entry = (ImageEntry) imageTable.getValueAt(row, col);
            if (entry != null) {
                Transferable tr = new ImageEntryTransferable(entry);
                return tr;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        return false;
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        
    }
}
