/*
 * StampListTransferable.java
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

    public synchronized DataFlavor[] getTransferDataFlavors() {
	return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return flavor.equals(stampListFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(stampListFlavor)) {
            return list;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public String toString() {
        return "Stamp List Transferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}