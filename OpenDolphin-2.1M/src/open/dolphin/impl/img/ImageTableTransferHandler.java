package open.dolphin.impl.img;

import java.awt.Window;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.exception.DolphinException;
import open.dolphin.helper.FileListTransferable;

/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class ImageTableTransferHandler extends TransferHandler {

    private AbstractBrowser context;
    private JTable sourceTable;

    public ImageTableTransferHandler(AbstractBrowser context) {
        this.context = context;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        int row = sourceTable.getSelectedRow();
        int col = sourceTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            ImageEntry entry = (ImageEntry) sourceTable.getValueAt(row, col);
            if (entry != null) {
                File f = new File(entry.getPath());
                File[] files = new File[1];
                files[0] = f;
                Transferable tr = new FileListTransferable(files);
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
        sourceTable = null;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        try {
            // Drag & Drop されたファイルのリストを得る
            Transferable t = support.getTransferable();
            java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            List<File> allFiles = new ArrayList<File>();

            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    if (name.startsWith(".")) {
                        continue;
                    }

                    if (file.length()==0L) {
                        continue;
                    }

                    allFiles.add(file);

                } else {
                    listAll(file, allFiles);
                }
            }

            if (allFiles.size() > 0) {
                parseFiles(allFiles);
            }
            sourceTable = null;

            return true;

        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace(System.err);

        } catch (IOException ieo) {
            ieo.printStackTrace(System.err);

        }
        sourceTable = null;
        return false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        canImport = canImport && ((JTable)support.getComponent()!=sourceTable);
        canImport = canImport && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        return canImport;
    }

    private void parseFiles(final List<File> imageFiles) {

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                String baseDir = context.getImageBase();
                if (baseDir == null) {
                    return null;
                }

                String patientId = context.getContext().getPatient().getPatientId();
                StringBuilder sb = new StringBuilder();
                sb.append(baseDir).append(File.separator).append(patientId);
                String dirStr = sb.toString();
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        throw new DolphinException("画像用のディレクトリを作成できません。");
                    }
                }

                for (File src : imageFiles) {
                    File dest = new File(dirStr, src.getName());
                    FileChannel in = (new FileInputStream(src)).getChannel();
                    FileChannel out = (new FileOutputStream(dest)).getChannel();
                    in.transferTo(0, src.length(), out);
                    in.close();
                    out.close();
                    dest.setLastModified(src.lastModified());
                }

                if (context.dropIsMove()) {
                    while(imageFiles.size()>0) {
                        File delete = imageFiles.remove(0);
                        delete.delete();
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    context.scan();
                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    String message = "ファイルをコピーできません。\n" + ex.getMessage();
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
