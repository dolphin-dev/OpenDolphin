/*
 * SchemaListTransferable.java
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
 * Transferable class of the Icon list.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class SchemaListTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor schemaListFlavor = new DataFlavor(open.dolphin.client.SchemaList.class, "Schema List");
  
    public static final DataFlavor[] flavors = {SchemaListTransferable.schemaListFlavor};
      
    private SchemaList list;

    /** Creates new SchemaListTransferable */
    public SchemaListTransferable(SchemaList list) {
        this.list = list;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
	return flavors;
    }
     
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return flavor.equals(schemaListFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(schemaListFlavor)) {
            return list;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public String toString() {
        return "Icon List Transferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}