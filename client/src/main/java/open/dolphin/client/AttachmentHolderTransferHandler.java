package open.dolphin.client;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.AttachmentModel;


/**
 * AttachmentHolderTransferHandler class.
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class AttachmentHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private KartePane soaPane;
    private AttachmentHolder attachmentHolder;

    public AttachmentHolderTransferHandler(KartePane soaPane, AttachmentHolder attachmentHolder) {
        this.soaPane = soaPane;
        this.attachmentHolder = attachmentHolder;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        AttachmentHolder source = (AttachmentHolder)c;
        KartePane context = source.getKartePane();
        context.setDrragedStamp(new ComponentHolder[]{source});
        context.setDraggedCount(1);
        AttachmentModel attachment = source.getAttachment();
        Transferable tr = new AttachmentTransferable(attachment);
        return tr;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        AttachmentHolder test = (AttachmentHolder)c;
        KartePane context = test.getKartePane();
        if (action == MOVE &&
                context.getDrragedStamp() != null &&
                context.getDraggedCount() == context.getDroppedCount()) {
            context.removeAttachment(test); // TODO 
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
//minagawa$    

    /**
     * スタンプをクリップボードへ転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        AttachmentHolder ah = (AttachmentHolder)comp;
        Transferable tr = createTransferable(comp);
        clip.setContents(tr, null);
        if (action == MOVE) {
            KartePane kartePane = ah.getKartePane();
            if (kartePane.getTextPane().isEditable()) {
                kartePane.removeAttachment(ah);
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return attachmentHolder;
    }

    @Override
    public void enter(ActionMap map) {
        attachmentHolder.setSelected(true);
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        boolean caCunt = (soaPane.getTextPane().isEditable());
        map.get(GUIConst.ACTION_CUT).setEnabled(caCunt);
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
    }

    @Override
    public void exit(ActionMap map) {
        attachmentHolder.setSelected(false);
    }
}
