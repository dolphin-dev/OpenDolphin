package open.dolphin.client;

import java.awt.datatransfer.*;
import java.io.*;

/**
 * Tranferable class of the StampTree.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class StampTreeTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor stampTreeNodeFlavor 
    	= new DataFlavor(open.dolphin.client.StampTreeNode.class, "Stamp Tree Node");
    
    public static final DataFlavor[] flavors = {StampTreeTransferable.stampTreeNodeFlavor};
    
    private StampTreeNode node;

    /** Creates new StampTreeTransferable */
    public StampTreeTransferable(StampTreeNode node) {
        this.node = node;
    }

    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
     
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)  {
        return flavor.equals(stampTreeNodeFlavor) ? true : false;
    }

    @Override
    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(stampTreeNodeFlavor)) {
            return node;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public String toString() {
        return "StampTreeTransferable";
    }
  
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}