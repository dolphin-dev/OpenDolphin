package open.dolphin.relay;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;

/**
 * フクダ電子心電図ファイリングFEV-70に患者情報を送る
 *
 * @author masuda, Masuda Naika
 */
public class FEV70Relay implements PropertyChangeListener {
    
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (!pce.getPropertyName().equals(ChartEventHandler.CHART_EVENT_PROP)) {
            return;
        }
        
        ChartEventModel evt = (ChartEventModel)pce.getNewValue();
        
        if (evt.getEventType()!=ChartEventModel.PVT_ADD) {
            return;
        }
        
        final PatientVisitModel model = evt.getPatientVisitModel();
        if (model==null) {
            return;
        }
        
        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                doRelay(model);
                return null;
            }
        };
        
        worker.execute();
    }

    private void doRelay(PatientVisitModel model) {
        
        try {
            // shared path
            String sharePath = Project.getString(Project.PVT_RELAY_DIRECTORY);
            
            if (!sharePath.endsWith(File.separator)) {
                sharePath = sharePath + File.separator;
            }

            // CSV data
            String patientId = model.getPatientModel().getPatientId();
            String patientName = model.getPatientModel().getFullName();
            String patientSex = "1";
            if (model.getPatientModel().getGender().toLowerCase().startsWith("f")) {
                patientSex = "2";
            }
            String patientBD = model.getPatientBirthday().replace("-", "/");

            StringBuilder sb = new StringBuilder();
            sb.append(patientId);
            sb.append(",");
            sb.append(patientName);
            sb.append(",");
            sb.append(patientSex);
            sb.append(",");
            sb.append(patientBD);
            sb.append(",,,,,,,,\n");
//minagawa^ mac jdk7            
//            String fileName = sharePath + "ID_" + patientId;
//            File oldFile = new File(fileName + ".cs_");
//            if (oldFile.exists()) {
//                oldFile.delete();
//            }
//            FileOutputStream fos = new FileOutputStream(fileName + ".cs_");
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            BufferedWriter bw = new BufferedWriter(osw);
//            bw.write(sb.toString());
//            bw.close();
//            osw.close();
//            oldFile = new File(fileName + ".CSV");
//            if (oldFile.exists()) {
//                oldFile.delete();
//            }
//            File objFile = new File(fileName + ".cs_");
//            objFile.renameTo(new File(fileName + ".CSV"));
            String pvtData = sb.toString();
            List<String> lineData = new ArrayList<>();
            lineData.add(pvtData);
            
            String fileName = "ID_" + patientId;
            String tmpName = fileName + ".cs_";
            String destName = fileName + ".CSV";
            
            Path destPath = Paths.get(sharePath, tmpName);
            Files.write(destPath, lineData, Charset.forName("SHIT-JIS"));
            Files.move(destPath, destPath.resolveSibling(destName));
//minagawa$            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
