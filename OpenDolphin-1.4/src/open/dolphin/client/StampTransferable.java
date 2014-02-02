package open.dolphin.client;

import java.awt.datatransfer.*;
import java.io.*;

import open.dolphin.infomodel.ModuleModel;
  
/**
 * Tranferable class of the Stamp.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class StampTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor stampFlavor = new DataFlavor(open.dolphin.infomodel.ModuleModel.class, "Stamp");
  
    public static final DataFlavor[] flavors = {StampTransferable.stampFlavor};
      
    private ModuleModel stamp;

    /** Creates new StampTransferable */
    public StampTransferable(ModuleModel stamp) {
    		this.stamp = stamp;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
    		return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor) {
    		return flavor.equals(stampFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(stampFlavor)) {
            return stamp;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public String toString() {
        return "StampTransferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}