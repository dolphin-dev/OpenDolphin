package open.dolphin.img;

import java.awt.Window;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.client.ImageEntryTransferable;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class ImageTableTransferHandler extends TransferHandler {

    private DataFlavor fileFlavor;
    private ImageBrowserDoc context;
    private Logger logger;
    private boolean DEBUG;


    public ImageTableTransferHandler(ImageBrowserDoc context) {
        fileFlavor = DataFlavor.javaFileListFlavor;
        this.context = context;
        logger = ClientContext.getBootLogger();
        DEBUG = (logger.getLevel()==Level.DEBUG) ? true : false;
        if (DEBUG) {
            logger.debug("created ImageFileTransferHandler");
        }
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable imageTable = (JTable) c;
        int row = imageTable.getSelectedRow();
        int col = imageTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            ImageEntry entry = (ImageEntry) imageTable.getValueAt(row, col);
            if (entry != null) {
                Transferable tr = new ImageEntryTransferable(entry);
                return tr;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        
    }

    @Override
    public boolean importData(JComponent c, Transferable t) {

        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        try {
            if (hasFileFlavor(t.getTransferDataFlavors())) {

                // Drag & Drop されたファイルのリストを得る
                java.util.List<File> files = (java.util.List<File>) t.getTransferData(fileFlavor);
                List<File> allFiles = new ArrayList<File>();
                
                for (File file : files) {
                    if (!file.isDirectory()) {
                        String name = file.getName().toLowerCase();
                        for (int i = 0; i < ImageBrowserDoc.ACCEPT_FILE_TYPES.length; i++) {
                            if (name.endsWith(ImageBrowserDoc.ACCEPT_FILE_TYPES[i])) {
                                allFiles.add(file);
                            }
                        }
                    } else {
                        listAll(file, allFiles);
                    }
                }

                if (allFiles != null && allFiles.size() > 0) {
                    parseFiles(allFiles);
                }

                return true;
            }

        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace(System.err);

        } catch (IOException ieo) {
            ieo.printStackTrace(System.err);

        }
        return false;
    }

    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) {

        boolean ret = (hasFileFlavor(flavors)) ? true : false;
        if (DEBUG) {
            logger.debug("canImport = " + ret);
        }
        return ret;
    }

    private boolean hasFileFlavor(DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (fileFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    private void parseFiles(final List<File> imageFiles) {

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                String baseDir = context.getImageBase();
                if (DEBUG) {
                    logger.debug("image baser = " + baseDir);
                }
                if (baseDir == null) {
                    return null;
                }

                String patientId = context.getContext().getPatient().getPatientId();

                StringBuilder sb = new StringBuilder();
                sb.append(baseDir).append(File.separator).append(patientId);
                String dirStr = sb.toString();
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    boolean test = dir.mkdirs();
                    logger.debug("dir is not exist. create result is " + test);
                }

                byte[] readBuf = new byte[1024*10];
                int readLen = 0;
                FileInputStream fin = null;
                BufferedInputStream bufIn = null;
                FileOutputStream fout = null;
                BufferedOutputStream bufOut = null;

                for (File src : imageFiles) {

                    sb = new StringBuilder();
                    sb.append(dirStr).append(File.separator).append(src.getName());
                    File copy = new File(sb.toString());

                    fin = new FileInputStream(src);
                    bufIn = new BufferedInputStream(fin);

                    fout = new FileOutputStream(copy);
                    bufOut = new BufferedOutputStream(fout);

                    while (true) {
                        readLen = bufIn.read(readBuf);
                        if (readLen==-1) {
                            bufOut.flush();
                            break;
                        }
                        bufOut.write(readBuf, 0, readLen);
                    }

                    fout.close();
                    bufOut.close();
                    fin.close();
                    bufOut.close();
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    context.refresh();
                } catch (InterruptedException ex) {
                    logger.warn(ex);
                } catch (ExecutionException ex) {
                    logger.warn(ex);
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    String message = "ファイルをコピーできません。¥n" + ex.getMessage();
                    String title = ClientContext.getFrameTitle(context.getTitle());
                    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    //context.getProgressBar().setIndeterminate(true);
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    //context.getProgressBar().setIndeterminate(false);
                    //context.getProgressBar().setValue(0);
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }

    private void listAll(File dir, List<File> list) {

        File[] files = dir.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                listAll(f, list);
            } else {
                list.add(f);
            }
        }
    }
}
