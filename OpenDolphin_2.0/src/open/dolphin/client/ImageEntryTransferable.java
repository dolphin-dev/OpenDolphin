package open.dolphin.client;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transferable class of the ImageIcon.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ImageEntryTransferable implements Transferable, ClipboardOwner {
    
    /** Data Flavor of this class */
    public static DataFlavor imageEntryFlavor = new DataFlavor(ImageEntry.class, "Image Entry");
    
    public static final DataFlavor[] flavors = {ImageEntryTransferable.imageEntryFlavor};
    
    private ImageEntry entry;
    
    /** Creates new ImgeIconTransferable */
    public ImageEntryTransferable(ImageEntry entry) {
        this.entry = entry;
    }
    
    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(imageEntryFlavor) ? true : false;
    }
    
    @Override
    public synchronized Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException, IOException {
        
        if (flavor.equals(imageEntryFlavor)) {
            return entry;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    @Override
    public String toString() {
        return "Image Icon Transferable";
    }
    
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}