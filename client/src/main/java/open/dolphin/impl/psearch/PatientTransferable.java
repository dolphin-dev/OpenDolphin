package open.dolphin.impl.psearch;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import open.dolphin.infomodel.PatientModel;

/**
 * Patient Transferable クラス。
 * @author Kazushi Minagawa.
 */
public final class PatientTransferable implements Transferable {

    public static final DataFlavor patientFlavor = new DataFlavor(open.dolphin.infomodel.PatientModel.class, "PatientModel");
    public static final DataFlavor[] flavors = {patientFlavor};
    private PatientModel patientModel;

    public PatientTransferable(PatientModel patientModel) {
        this.patientModel = patientModel;
    }

    @Override
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(patientFlavor) ? true : false;
    }

    @Override
    public synchronized Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {

        if (flavor.equals(patientFlavor)) {
            return patientModel;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
