package open.dolphin.impl.labrcv;

import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.LabResultParser;
import open.dolphin.delegater.LaboDelegater;
import open.dolphin.impl.falco.HL7Falco;
import open.dolphin.infomodel.PatientLiteModel;


/**
 * LaboTestFileTransferHandler
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class NLaboTestFileTransferHandler extends TransferHandler {

    private NLaboTestImporter context;
    
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
            java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            List<File> labFiles = new ArrayList<File>(files.size());

            // 拡張子が .DAT のファイルのみ queue へ追加する
            for (File file : files) {
                if (!file.isDirectory()) {
                    String ext = file.getName().toLowerCase();
                    if (ext.endsWith(".dat") || ext.endsWith(".dat2") || ext.endsWith(".hl7") || ext.endsWith(".txt")) {
                        labFiles.add(file);
                    }
                }
            }

            if (labFiles != null && labFiles.size() > 0) {
                parseFiles(labFiles);
            }

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
        canImport = canImport && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        return canImport;
    }

    private void parseFiles(final List<File> labFiles) {

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<List<NLaboImportSummary>, Void>() {

            @Override
            protected List<NLaboImportSummary> doInBackground() throws Exception {
                LabResultParser parse = null;
                List<NLaboImportSummary> allModules = new ArrayList<NLaboImportSummary>();
                for (File lab : labFiles) {

                    if (lab.getName().toLowerCase().endsWith(".dat")) {
                        parse = new NLabParser();
                    } else if (lab.getName().toLowerCase().endsWith(".dat2")) {
                        parse = new Dat2Parser();
                    } else if (lab.getName().toLowerCase().endsWith(".hl7")) {
                        parse = new HL7Falco();
                    } else if (lab.getName().toLowerCase().endsWith(".txt")) {
                        parse = new WolfParser();
                    }
                    List<NLaboImportSummary> dataList = parse.parse(lab);
                    allModules.addAll(dataList);
                }

                if (allModules!=null && allModules.size()>0) {

                    List<String> idList = new ArrayList<String>(allModules.size());
                    for (NLaboImportSummary sm : allModules) {
                        idList.add(sm.getPatientId());
                    }

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

                } catch (Exception e) {
                    String why;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    String message = "パースできないファイルがあります。¥n検査報告書フォーマットを確認してください。\n" + why;
                    String title = "ラボレシーバ";
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