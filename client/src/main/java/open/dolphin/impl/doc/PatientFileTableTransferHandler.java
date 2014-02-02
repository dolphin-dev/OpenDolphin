package open.dolphin.impl.doc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.PatientFileModel;

/**
 * PatientFileTableTransferHandler
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class PatientFileTableTransferHandler extends TransferHandler {
    
    // From StackOverFlow
    private static DataFlavor nixFileDataFlavor;
    static {
        try {
           nixFileDataFlavor  = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (Exception e) {
        }
    }

    private PatientDocFile context;

    public PatientFileTableTransferHandler(PatientDocFile context) {
        this.context = context;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable sourceTable = (JTable)c;
        int row = sourceTable.getSelectedRow();
        List<PatientFileModel> list = context.getDataList();
        
        if (list==null || row>=list.size() || row<0) {
            return null;
        }
        
        PatientFileModel model = list.get(row);
        
//        int col = sourceTable.getSelectedColumn();
//        if (row != -1 && col != -1) {
//            ImageEntry entry = (ImageEntry) sourceTable.getValueAt(row, col);
//            if (entry != null) {
//                File f = new File(entry.getPath());
//                File[] files = new File[1];
//                files[0] = f;
//                Transferable tr = new FileListTransferable(files);
//                return tr;
//            } else {
//                return null;
//            }
//        }
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
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

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
            
            List<File> allFiles = new ArrayList<File>();

            for (File file : files) {
                
                if (file.isDirectory() || file.getName().startsWith(".") || (file.length()==0L)) {
                    continue;
                }
                
                String name = file.getName();
                int index = name.indexOf(".");
                String ext = null;
                if (index>0) {
                    ext = name.substring(index+1);
                }
                    
                if (ext!=null && (!UserDocumentHelper.isImage(ext))) {
                    allFiles.add(file);
                }
            }

            context.fileDropped(allFiles);

            return true;

        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace(System.err);

        } catch (IOException ieo) {
            ieo.printStackTrace(System.err);

        }
        return false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        boolean isFile = (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(nixFileDataFlavor));
        canImport = canImport && isFile;
        return canImport;
    }
    
    private List<File> getDropedFiles(String data) {
        
        List<File> files = new ArrayList<File>(2);
        
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
