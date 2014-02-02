/*
 * MasterItemTransferHandler.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 * éæä≥ Transferable ÉNÉâÉXÅB
 * @author kazm
 */
public class RegisteredDiagnosisTransferable implements Transferable {
    
    public static DataFlavor registeredDiagnosisFlavor = new DataFlavor(open.dolphin.infomodel.RegisteredDiagnosisModel.class, "RegisteredDiagnosis");
    
    public static final DataFlavor[] flavors = {registeredDiagnosisFlavor};
    
    private RegisteredDiagnosisModel diagnosis;
    
    public RegisteredDiagnosisTransferable(RegisteredDiagnosisModel diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(registeredDiagnosisFlavor) ? true : false;
    }
    
    public synchronized Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException, IOException {
        
        if (flavor.equals(registeredDiagnosisFlavor)) {
            return diagnosis;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
}
