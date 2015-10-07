package open.dolphin.system;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 * OIDRequester
 *
 * @author Minagawa,Kazushi
 *
 */
public class OIDGetter extends JPanel {
    
    public static final String NEXT_OID_PROP = "nextOidProp";
    
    private String helloReply;
    private final PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private OidTask task;
    private PropertyChangeListener pl;
    
    private JProgressBar bar;
    private JDialog progressDialog;
    
    private JButton comTest;
    
    public OIDGetter() {
        initialize();
        connect();
    }
    
    public String getHelloReply() {
        return helloReply;
    }
    
    public void setHelloReply(String oid) {
        helloReply = oid;
        boundSupport.firePropertyChange(NEXT_OID_PROP, "", helloReply);
    }
    
    public void addOidPropertyListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(NEXT_OID_PROP, l);
    }
    
    public void removeOidPropertyListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(NEXT_OID_PROP, l);
    }
    
    private void initialize() {
        
        try {
            InputStream in = this.getClass().getResourceAsStream("/open/dolphin/system/account-make-info.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ( (line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            JTextArea infoArea = new JTextArea();
            infoArea.setEditable(false);
            infoArea.setLineWrap(true);
            infoArea.setMargin(new Insets(10,10,10,10));
            infoArea.setText(sb.toString());
            JScrollPane scroller = new JScrollPane(infoArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(OIDGetter.class);
            btnPanel.add(new JLabel(bundle.getString("labelText.comTest")));
            comTest = new JButton(bundle.getString("actionText.comTest"));
            btnPanel.add(comTest);
            
            this.setLayout(new BorderLayout());
            this.add(scroller, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    private void connect() {
        comTest.addActionListener((ActionEvent e) -> {
            doTest();
        });
    }
    
    private void doTest() {
        
        task = new OidTask();
        pl = (PropertyChangeEvent evt) -> {
            if ("state".equals(evt.getPropertyName())) {
                if (SwingWorker.StateValue.DONE==evt.getNewValue()) {
                    stopProgress();
                } else if (SwingWorker.StateValue.STARTED==evt.getNewValue()) {
                    startProgress();
                }
            }
        };
        task.addPropertyChangeListener(pl);
        
        task.execute();
    }
    
    private void startProgress() {
        bar = new JProgressBar(0, 100);
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(OIDGetter.class);
        String note = bundle.getString("note.progress.comTesting");
        Object[] message = new Object[]{note, bar};
        JButton cancel = new JButton((String)UIManager.get("OptionPane.cancelButtonText"));
        cancel.addActionListener((ActionEvent ae) -> {
            task.cancel(true);
        });
        JOptionPane pane = new JOptionPane(
                message, 
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{cancel});
        
        String title = ClientContext.getFrameTitle(bundle.getString("title.optionPane"));
        title = ClientContext.getFrameTitle(title);
        Component c = SwingUtilities.getWindowAncestor(this);
        progressDialog = pane.createDialog(c, title);
        progressDialog.setModal(false);
        bar.setIndeterminate(true);
        progressDialog.setVisible(true);
    }
    
    private void stopProgress() {
        task.removePropertyChangeListener(pl);
    }
    
    class OidTask extends SwingWorker<String, Void> {
        
        @Override
        protected String doInBackground() throws Exception {
            SystemDelegater sdl = new SystemDelegater();
            String ret = sdl.hellow();
            return ret;
        }
        
        @Override
        public void done() {
            bar.setIndeterminate(false);
            progressDialog.setVisible(false);
            if (isCancelled()) {
                return;
            }
            try {
                String result = get();
                succeeded(result);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            } catch (ExecutionException ex) {
                failed(ex);
            }
        }
        
        protected void succeeded(String result) {
            Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(OIDGetter.class);
            String title = bundle.getString("title.optionPane");
            title = ClientContext.getFrameTitle(title);
            String msg = bundle.getString("instraction.proceedNextStep");
            JOptionPane.showMessageDialog(myParent, msg, title, JOptionPane.INFORMATION_MESSAGE);
            setHelloReply(result);
        }
        
        protected void failed(Throwable cause) {
            String errMsg = cause.getMessage();
            Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
            String title = ClientContext.getMyBundle(OIDGetter.class).getString("title.optionPane");
            title = ClientContext.getFrameTitle(title);
            JOptionPane.showMessageDialog(myParent, errMsg, title, JOptionPane.WARNING_MESSAGE);
            setHelloReply(null);
        }
    }
}
