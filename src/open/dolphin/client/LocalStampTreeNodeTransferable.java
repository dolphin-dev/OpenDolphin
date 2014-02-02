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
 * Tranferable class of the StampTreeNode.
 *
 * @author  Kazushi Minagawa
 */ 
public class LocalStampTreeNodeTransferable implements Transferable {

    /** Data Flavor of this class */
    public static DataFlavor localStampTreeNodeFlavor;
    static {
    	try {
    		localStampTreeNodeFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=open.dolphin.client.StampTreeNode");
    	} catch (Exception e) {
    	}
    };
    	
    public static final DataFlavor[] flavors = {LocalStampTreeNodeTransferable.localStampTreeNodeFlavor};
    
    private StampTreeNode node;

    /** Creates new StampTreeTransferable */
    public LocalStampTreeNodeTransferable(StampTreeNode node) {
        this.node = node;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor)  {
        return flavor.equals(localStampTreeNodeFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(localStampTreeNodeFlavor)) {
            return node;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}