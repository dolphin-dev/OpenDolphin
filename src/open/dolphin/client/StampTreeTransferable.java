/*
 * StampTreeTransferable.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
    public static DataFlavor stampTreeNodeFlavor = new DataFlavor(open.dolphin.client.StampTreeNode.class,
                                                 "Stamp Tree Node");
    
    public static final DataFlavor[] flavors = {StampTreeTransferable.stampTreeNodeFlavor};
    
    private StampTreeNode node;

    /** Creates new StampTreeTransferable */
    public StampTreeTransferable(StampTreeNode node) {
        this.node = node;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor)  {
        return flavor.equals(stampTreeNodeFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(stampTreeNodeFlavor)) {
            return node;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public String toString() {
        return "StampTreeTransferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}