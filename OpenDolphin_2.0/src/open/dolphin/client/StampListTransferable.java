package open.dolphin.client;

import java.awt.datatransfer.*;
import java.io.*;
     
/**
 * Transferable class of the PTrain.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class StampListTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor stampListFlavor = new DataFlavor(open.dolphin.client.StampList.class, "Stamp List");
  
    public static final DataFlavor[] flavors = {StampListTransferable.stampListFlavor};
    
    private StampList list;

    /** Create new StampListTransferable */
    public StampListTransferable(StampList list) {
        this.list = list;
    }

    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
	return flavors;
    }
     
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return flavor.equals(stampListFlavor) ? true : false;
    }

    @Override
    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(stampListFlavor)) {
            return list;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public String toString() {
        return "Stamp List Transferable";
    }
  
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}