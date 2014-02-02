package open.dolphin.client;

import java.awt.datatransfer.*;
import java.io.IOException;
import open.dolphin.infomodel.AttachmentModel;

/**
 * Transferable class of the Attachment.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AttachmentTransferable implements Transferable, ClipboardOwner {
    
    /** Data Flavor of this class */
    public static DataFlavor attachmentFlavor = new DataFlavor(AttachmentModel.class, "Attachment");
    
    public static final DataFlavor[] flavors = {AttachmentTransferable.attachmentFlavor};
    
    private AttachmentModel attachment;
    
    /** Creates new AttachmentTransferable */
    public AttachmentTransferable(AttachmentModel attachment) {
        this.attachment = attachment;
    }
    
    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(attachmentFlavor) ? true : false;
    }
    
    @Override
    public synchronized Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
        
        if (flavor.equals(attachmentFlavor)) {
            return attachment;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    @Override
    public String toString() {
        return "Attachment Transferable";
    }
    
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}