package open.dolphin.impl.img;

import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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
    
    // From StackOverFlow
    private static DataFlavor nixFileDataFlavor;
    static {
        try {
           nixFileDataFlavor  = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (Exception e) {
        }
    }

    private final AbstractBrowser context;
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
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        Window parent = SwingUtilities.getWindowAncestor(context.getUI());
        String msg1 = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("waring.cannotMove");
        String msg2 = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("warning.cannotCopy");
        if(context.isScanning(parent, (context.dropIsMove()) ? msg1 : msg2)) {
            return false;
        }
//s.oh$

        try {
            // Drag & Drop されたファイルのリストを得る
            Transferable t = support.getTransferable();
            
            java.util.List<File> files = null;
            
            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                files = (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                
            } 
            //jdk1.6 nix
            else if (support.isDataFlavorSupported(nixFileDataFlavor)){
                files = getDropedFiles((String)t.getTransferData(nixFileDataFlavor));
            }
            
            List<File> allFiles = new ArrayList<>();

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
//s.oh^ 2014/05/30 PDF・画像タブの改善
                    //listAll(file, allFiles);
                    parseDirectory(file);
//s.oh$
                }
            }

            if (allFiles.size() > 0) {
                parseFiles(allFiles);
            }
            sourceTable = null;

            return true;

        } catch (UnsupportedFlavorException | IOException ufe) {
            ufe.printStackTrace(System.err);

        }
        sourceTable = null;
        return false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        canImport = canImport && ((JTable)support.getComponent()!=sourceTable);
        boolean isFile = (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(nixFileDataFlavor));
        canImport = canImport && isFile;
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
//s.oh^ 他プロセス連携
                if(context instanceof DefaultBrowserEx || context instanceof DefaultBrowser || context instanceof FCRBrowser) {
                    dirStr = context.getNowLocation();
                }
//s.oh$
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    boolean ok = dir.mkdirs();
                    if (!ok) {
                        String errMsg = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("error.cannotCreateDirectory");
                        throw new DolphinException(errMsg);
                    }
                    dir.setExecutable(true, false);
                    dir.setWritable(true, false);
                }

                for (File src : imageFiles) {
                    File dest = new File(dirStr, src.getName());
//s.oh^ 機能改善
                    boolean save = true;
                    if(dest.exists()) {
                        Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                        String question = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("question.overrideFile");
                        int ret = JOptionPane.showConfirmDialog(parent, question, ClientContext.getString("productString"), JOptionPane.OK_CANCEL_OPTION);
                        if(ret != JOptionPane.OK_OPTION) {
                            save = false;
                        }
                    }
                    if(save) {
                        FileChannel in = (new FileInputStream(src)).getChannel();
                        FileChannel out = (new FileOutputStream(dest)).getChannel();
                        in.transferTo(0, src.length(), out);
                        in.close();
                        out.close();
                        dest.setLastModified(src.lastModified());
                    }
//s.oh$
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
//s.oh^ 他プロセス連携
                    //context.scan(context.getImgLocation());
                    if(context instanceof DefaultBrowserEx || context instanceof DefaultBrowser || context instanceof FCRBrowser) {
                        context.scan(context.getNowLocation());
                    }else{
                        context.scan(context.getImgLocation());
                    }
//s.oh$
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
                } catch (ExecutionException ex) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    //String message = "ファイルをコピーできません。\n" + ex.getMessage();
                    String fmt = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("messageFormat.cannotCopy");
                    MessageFormat msf = new MessageFormat(fmt);
                    String message =msf.format(new Object[]{ex.getMessage()});
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
    
//s.oh^ 2014/05/30 PDF・画像タブの改善
    private void parseDirectory(final File srcDir) {

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
                if(context instanceof DefaultBrowserEx || context instanceof DefaultBrowser || context instanceof FCRBrowser) {
                    dirStr = context.getNowLocation();
                }
                File destDir = new File(dirStr, srcDir.getName());
                if(destDir.exists()) {
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    String question = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("question.overrideFolder");
                    int ret = JOptionPane.showConfirmDialog(parent, question, ClientContext.getString("productString"), JOptionPane.OK_CANCEL_OPTION);
                    if(ret != JOptionPane.OK_OPTION) {
                        return null;
                    }
                }
                
                copyDir(destDir, srcDir);
                
                if (context.dropIsMove()) {
                    removeDir(srcDir);
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if(context instanceof DefaultBrowserEx || context instanceof DefaultBrowser || context instanceof FCRBrowser) {
                        context.scan(context.getNowLocation());
                    }else{
                        context.scan(context.getImgLocation());
                    }
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
                } catch (ExecutionException ex) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    //String message = "ファイルをコピーできません。\n" + ex.getMessage();
                    String fmt = ClientContext.getMyBundle(ImageTableTransferHandler.class).getString("messageFormat.cannotCopy");
                    MessageFormat msf = new MessageFormat(fmt);
                    String message =msf.format(new Object[]{ex.getMessage()});
                    String title = ClientContext.getFrameTitle(context.getTitle());
                    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }
    
    private void copyDir(File destDir, File srcDir) throws FileNotFoundException, IOException {
        if(!destDir.exists() && !destDir.mkdirs()) {
            return;
        }
        File[] files = srcDir.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                File dir = new File(destDir, file.getName());
                copyDir(dir, file);
            }else{
                File destFile = new File(destDir, file.getName());
                FileChannel in = (new FileInputStream(file)).getChannel();
                FileChannel out = (new FileOutputStream(destFile)).getChannel();
                in.transferTo(0, file.length(), out);
                in.close();
                out.close();
                destFile.setLastModified(file.lastModified());
            }
        }
    }
    
    private void removeDir(File dir) {
        if(!dir.exists()) {
            return;
        }
        if(dir.isFile()) {
            dir.delete();
        }else if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(int i = 0; i < files.length; i++) {
                removeDir(files[i]);
            }
            dir.delete();
        }
    }
//s.oh$

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
    
    private List<File> getDropedFiles(String data) {
        
        List<File> files = new ArrayList<>(2);
        
        for(StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String token = st.nextToken().trim();
            
            if(token.startsWith("#") || token.isEmpty()) {
                // comment line, by RFC 2483
                continue;
            }
            try {
                files.add(new File(new URI(token)));
                
            } catch(Exception e) {
                e.printStackTrace(System.err);
            }
        }
        
        return files;
    }
}
