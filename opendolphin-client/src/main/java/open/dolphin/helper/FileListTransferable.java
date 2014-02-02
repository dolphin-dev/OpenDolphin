package open.dolphin.helper;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class FileListTransferable implements Transferable {
    
    private List<File> fileList;

    public FileListTransferable(File[] files) {
        fileList = new ArrayList<File>();
        fileList.addAll(Arrays.asList(files));
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.javaFileListFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor df) {
        return df.equals(DataFlavor.javaFileListFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor df) {
        if (!isDataFlavorSupported(df)) {
            return null;
        }
        return fileList;
    }
}
