/*
 * ImageIconTransferable.java
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

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.*;

     
/**
 * Transferable class of the ImageIcon.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class ImageIconTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor imageIconFlavor = new DataFlavor(javax.swing.ImageIcon.class, "Image Icon");

    public static final DataFlavor[] flavors = {ImageIconTransferable.imageIconFlavor};
     
    private ImageIcon icon;

    /** Creates new ImgeIconTransferable */
    public ImageIconTransferable(ImageIcon icon) {
        this.icon = icon;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
	return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return flavor.equals(imageIconFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(imageIconFlavor)) {
            return icon;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public String toString() {
        return "Image Icon Transferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}