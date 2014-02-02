package open.dolphin.relay;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingWorker;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.impl.pvt.KanaToAscii;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa.
 */
public class PVTRelay implements PropertyChangeListener {
    
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String CSV_EXT = ".csv";
    private static final String TEMP_EXT = ".inp";
    
    private static boolean DEBUG = false;
    
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
    
    //  PatientVisitModel リレー
    private void doRelay(PatientVisitModel model) {
        
        try {
            // shared path
            String sharePath = Project.getString(Project.PVT_RELAY_DIRECTORY);
            
            if (!sharePath.endsWith(File.separator)) {
                sharePath = sharePath + File.separator;
            }
//minagawa^ mac jdk7            
//            // 出力先
//            File outputDir = new File(sharePath);
//            if (!outputDir.exists()) {
//                outputDir.mkdir();
//            }
//minagawa$            
            // csv data
            StringBuilder sb = new StringBuilder();
            sb.append(model.getPatientModel().getPatientId()).append(",");  // pid,
            sb.append(model.getPatientModel().getFullName()).append(",");   // name,
            sb.append(",");                                                 // ,
            KanaToAscii kanaToAscii = new KanaToAscii();
            String rm = kanaToAscii.CHGKanatoASCII(model.getPatientModel().getKanaName(), "");
            sb.append(rm).append(",");                                                // roman,    

            String g = model.getPatientModel().getGender();
            sb.append(ModelUtils.getGenderMFDesc(g)).append(",");           // F | M,

            String birth = model.getPatientModel().getBirthday();
            birth = birth.replaceAll("-", "");
            sb.append(birth);                                               // yyyyMMdd
            String line = sb.toString();
            if (DEBUG) {
                System.err.println(line);
            }

            // CSV temp file name
            sb = new StringBuilder();
            sb.append(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
            String fileNameWithoutExt = sb.toString();
            sb.append(TEMP_EXT);
            String tempName = sb.toString();
//minagawa^ macjdk7            
//            // File
//            File tmp = new File(outputDir,tempName);
//            
//            // Write to temp file
//            FileOutputStream out = new FileOutputStream(tmp);
//            BufferedOutputStream w = new BufferedOutputStream(out);
//            w.write(line.getBytes(Project.getString(Project.PVT_RELAY_ENCODING)));
//            w.flush();
//            w.close();
//            
//            // Rename
//            sb = new StringBuilder();
//            sb.append(fileNameWithoutExt);
//            sb.append(CSV_EXT);
//            String fileName = sb.toString();
//            File dest = new File(outputDir, fileName);
//
//            tmp.renameTo(dest);
            Path destPath = Paths.get(sharePath, tempName);
            Files.write(destPath, line.getBytes(Project.getString(Project.PVT_RELAY_ENCODING)));
            // rename
            sb = new StringBuilder();
            sb.append(fileNameWithoutExt);
            sb.append(CSV_EXT);
            String fileName = sb.toString();
            Files.move(destPath, destPath.resolveSibling(fileName));
//minagawa$            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
