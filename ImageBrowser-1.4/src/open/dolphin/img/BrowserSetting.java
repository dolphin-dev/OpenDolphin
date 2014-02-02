package open.dolphin.img;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import open.dolphin.client.ClientContext;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author Kazushi Minagawa
 */
public class BrowserSetting {
    
    private Component parent;
    private ConfigView view;
    private JDialog dialog;
    private Properties properties;
   
    public BrowserSetting(Component parent, Properties properties) {
        this.parent = parent;
        this.properties = properties;
    }
    
    private String selectApp() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String app = chooser.getSelectedFile().getPath();
            return app;
        }
        return null;
    }
    
    @Action
    public void setBaseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dir = chooser.getSelectedFile().getPath();
            view.getBaseDirFld().setText(dir);
        }
    }
    
    @Action
    public void setJepgView() {
        String app = selectApp();
        if (app != null) {
            view.getJpegFld().setText(app);
        }
    }
    
    @Action
    public void setPdfView() {
        String app = selectApp();
        if (app != null) {
            view.getPdfFld().setText(app);
        }
    }
    
    @Action
    public void setDicomView() {
        String app = selectApp();
        if (app != null) {
            view.getDicomFld().setText(app);
        }
    }
    
    @Action
    public void saveProperties() {
        
        String spiVal = view.getColumnSpinner().getValue().toString();
        properties.setProperty("columnCount", spiVal);
        
        String boolVal = String.valueOf(view.getFileNameChkBox().isSelected());
        properties.setProperty("showFileName", boolVal);
        
        String baseVal = view.getBaseDirFld().getText().trim();
        properties.setProperty("baseDir", baseVal);
        
        properties.setProperty("jpegView", view.getJpegFld().getText().trim());
        properties.setProperty("pdfView", view.getPdfFld().getText().trim());
        properties.setProperty("dicomView", view.getDicomFld().getText().trim());
        
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    @Action
    public void cancel() {
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    public void start() {
        
        view = new ConfigView();
        
        String spiVal = properties.getProperty("columnCount");
        int spiInt = Integer.parseInt(spiVal);
        view.getColumnSpinner().setValue(new Integer(spiInt));
        
        Boolean boolVal = Boolean.parseBoolean(properties.getProperty("showFileName"));
        view.getFileNameChkBox().setSelected(boolVal.booleanValue());
        
        String baseDir = properties.getProperty("baseDir");
        view.getBaseDirFld().setText(baseDir);
        
        view.getJpegFld().setText(properties.getProperty("jpegView"));
        view.getPdfFld().setText(properties.getProperty("pdfView"));
        view.getDicomFld().setText(properties.getProperty("dicomView"));
        
        // Inject Actions 
        ApplicationContext ctx = ClientContext.getApplicationContext();
        ActionMap map = ctx.getActionMap(BrowserSetting.this);
        view.getBaseDirBtn().setAction(map.get("setBaseDirectory"));
        view.getJpegBtn().setAction(map.get("setJepgView"));
        view.getPdfBtn().setAction(map.get("setPdfView"));
        view.getDicomBtn().setAction(map.get("setDicomView"));
        
        JButton saveBtn = new JButton();
        saveBtn.setAction(map.get("saveProperties"));
        
        //String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        JButton cancelBtn = new JButton();
        cancelBtn.setAction(map.get("cancel"));
        
        Object[] options = new Object[]{saveBtn, cancelBtn};
        
        JOptionPane jop = new JOptionPane(
                view,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                saveBtn);
        
        dialog = jop.createDialog(parent, ClientContext.getFrameTitle("イメージブラウザ設定"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });
        dialog.setVisible(true);
    }
}
