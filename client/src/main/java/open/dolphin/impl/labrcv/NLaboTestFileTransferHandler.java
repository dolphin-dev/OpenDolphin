package open.dolphin.impl.labrcv;

import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.LabResultParser;
import open.dolphin.delegater.LaboDelegater;
import open.dolphin.infomodel.PatientLiteModel;


/**
 * LaboTestFileTransferHandler
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class NLaboTestFileTransferHandler extends TransferHandler {
    
    private final NLaboTestImporter context;
    
    public NLaboTestFileTransferHandler(NLaboTestImporter context) {
        this.context = context;
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
            
            if (files==null || files.isEmpty()) {
                return false;
            }
            
            List<Path> labFiles = new ArrayList<>(files.size());

            // 拡張子が .DAT のファイルのみ queue へ追加する
            for (File file : files) {
                if (!file.isDirectory()) {
                    String ext = file.getName().toLowerCase();
                    if (ext.endsWith(".dat") || 
                       ext.endsWith(".dat2") || 
                        ext.endsWith(".hl7") || 
                        ext.endsWith(".txt") ||
                            ext.endsWith(".csv")) {
                        labFiles.add(file.toPath());
                    }
                }
            }

            if (labFiles.size() > 0) {
                parseFiles(labFiles);
            }

            return true;
            
        } catch (UnsupportedFlavorException | IOException ufe) {
            ufe.printStackTrace(System.err);
            
        }
        return false;
    }
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        boolean isFile = support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        canImport = canImport && isFile;
        return canImport;
    }

    private void parseFiles(final List<Path> labFiles) {

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<List<NLaboImportSummary>, Void>() {

            @Override
            protected List<NLaboImportSummary> doInBackground() throws Exception {
                List<NLaboImportSummary> allModules = new ArrayList<>();
                for (Path lab : labFiles) {
                    LabResultParser parse = LabParserFactory.getParser(lab.getFileName().toString());
                    List<NLaboImportSummary> dataList = parse.parse(lab);
                    allModules.addAll(dataList);
                }

                if (allModules.size()>0) {

                    List<String> idList = new ArrayList<>(allModules.size());
                    allModules.stream().forEach((sm) -> {
                        idList.add(sm.getPatientId());
                    });

                    LaboDelegater laboDelegater = new LaboDelegater();
                    List<PatientLiteModel> pList = laboDelegater.getConstrainedPatients(idList);

                    for (int i = 0; i < allModules.size(); i++) {
                        NLaboImportSummary sm = allModules.get(i);
                        PatientLiteModel pl = pList.get(i);
                        if (pl!=null) {
                            sm.setKarteId(pl.getPatientId());
                            sm.setKarteBirthday(pl.getBirthday());
                            sm.setKarteKanaName(pl.getKanaName());
                            sm.setKarteName(pl.getFullName());
                            sm.setKarteSex(pl.getGenderDesc());
                        }
                    }
                }
                return allModules;
            }

            @Override
            protected void done() {

                try {
                    List<NLaboImportSummary> allModules = get();
                    context.getTableModel().setDataProvider(allModules);

                } catch (InterruptedException | ExecutionException e) {
                    String why;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    //String message = "パースできないファイルがあります。\n検査報告書フォーマットを確認してください。\n" + why;
                    String fmt = ClientContext.getMyBundle(NLaboTestFileTransferHandler.class).getString("messageFormat.cannotParseFile");
                    MessageFormat msf = new MessageFormat(fmt);
                    String message = msf.format(new Object[]{why});
                    String title = ClientContext.getMyBundle(NLaboTestFileTransferHandler.class).getString("title.optionPane.labReceiver");
                    JOptionPane.showMessageDialog(parent, message, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    context.getProgressBar().setIndeterminate(true);
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    context.getProgressBar().setIndeterminate(false);
                    context.getProgressBar().setValue(0);
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }
}