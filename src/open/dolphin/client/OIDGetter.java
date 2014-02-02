package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import open.dolphin.ejb.RemoteSystemService;

import org.jboss.security.auth.callback.UsernamePasswordHandler;

/**
 * OIDRequester
 *
 * @author Minagawa,Kazushi
 *
 */
public class OIDGetter extends JPanel {
    
    private static final long serialVersionUID = 1666003906485274645L;
    
    public static final String NEXT_OID_PROP = "nextOidProp";
    private static final int MAX_ESTIMATION = 30*1000;
    private static final int DELAY = 200;
    private static final String PROGRESS_NOTE = "通信テストをしています...";
    private static final String SUCCESS_NOTE = "通信に成功しました。次項ボタンをクリックし次に進むことができます。";
    private static final String TASK_TITLE = "通信テスト";
    
    private String helloReply;
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private Timer taskTimer;
    private OidTask worker;
    
    private JButton comTest = new JButton(TASK_TITLE);
    
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
    
    public String getErrorMessage() {
        return worker.getErrorMessage();
    }
    
    public void addOidPropertyListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(NEXT_OID_PROP, l);
    }
    
    public void removeOidPropertyListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(NEXT_OID_PROP, l);
    }
    
    private void initialize() {
        
        try {
            InputStream in = ClientContext.getResourceAsStream("account-make-info.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "SHIFT_JIS"));
            String line = null;
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
            btnPanel.add(new JLabel("次のボタンをクリックし、通信できるかどうか確認してください。"));
            btnPanel.add(comTest);
            
            this.setLayout(new BorderLayout());
            this.add(scroller, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void connect() {
        comTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doTest();
            }
        });
    }
    
    private void doTest() {
        
        worker = new OidTask(MAX_ESTIMATION / DELAY);
        
        final ProgressMonitor monitor = new ProgressMonitor(null, null, PROGRESS_NOTE, 0, MAX_ESTIMATION / DELAY);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(300);
        monitor.setMillisToPopup(500);
        
        taskTimer = new Timer(DELAY, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                monitor.setProgress(worker.getCurrent());
                
                if (worker.isDone()) {
                    
                    taskTimer.stop();
                    monitor.close();
                    
                    if (worker.isNoError()) {
                        Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
                        String title = ClientContext.getFrameTitle(TASK_TITLE);
                        JOptionPane.showMessageDialog(myParent, SUCCESS_NOTE, title, JOptionPane.INFORMATION_MESSAGE);
                        setHelloReply(worker.getOid());
                        
                    } else {
                        String msg = worker.getErrorMessage();
                        Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
                        String title = ClientContext.getFrameTitle(TASK_TITLE);
                        JOptionPane.showMessageDialog(myParent, msg, title, JOptionPane.WARNING_MESSAGE);
                        setHelloReply(null);
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    StringBuilder sb = new StringBuilder();
                    sb.append("OIDの取得中にタイムアウトが生じました。");
                    sb.append("\n");
                    sb.append("サーバまたはネットワーク機器に障害が発生している可能性があります。");
                    worker.setErrorMessage(sb.toString());
                    String msg = worker.getErrorMessage();
                    Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
                    String title = ClientContext.getFrameTitle(TASK_TITLE);
                    JOptionPane.showMessageDialog(myParent, msg, title, JOptionPane.WARNING_MESSAGE);
                    setHelloReply(null);
                    setHelloReply(null);
                }
            }
        });
        worker.start();
        taskTimer.start();
    }
    
    class OidTask extends AbstractInfiniteTask {
        
        private String nextOid;
        private int errorCode;
        private String errorMessage;
        
        
        public OidTask(int taskLength) {
            setTaskLength(taskLength);
        }
        
        public boolean isNoError() {
            return errorCode == 0 ? true : false;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public String getOid() {
            return nextOid;
        }
        
        protected void doTask() {
            
            try {
                // SECURITY
                String qid = "minagawa";
                String password = "hanagui+";
                String securityDomain = "openDolphinSysAd";
                String providerURL = "jnp://210.153.124.60:1099";
                //String providerURL = "jnp://172.168.158.1:1099";
                //String providerURL = "jnp://localhost:1099";
                
                UsernamePasswordHandler h = new UsernamePasswordHandler(qid, password.toCharArray());
                LoginContext lc = new LoginContext(securityDomain, h);
                lc.login();
                
                Properties props = new Properties();
                props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
                props.setProperty("java.naming.provider.url",providerURL);
                props.setProperty("java.naming.factory.url.pkgs","org.jboss.namingrg.jnp.interfaces");
                InitialContext ctx = new InitialContext(props);
                
                RemoteSystemService service = (RemoteSystemService)ctx.lookup("openDolphin/RemoteSystemService");
                nextOid = service.helloDolphin();
                
            } catch (javax.ejb.EJBAccessException ee) {
                ee.printStackTrace();
                errorCode = -10;
                StringBuilder sb = new StringBuilder();
                sb.append("システム設定エラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(ee));
                setErrorMessage(sb.toString());
                
            } catch (javax.naming.CommunicationException ce) {
                ce.printStackTrace();
                errorCode = -20;
                StringBuilder sb = new StringBuilder();
                sb.append("ASPサーバに接続できません。");
                sb.append("\n");
                sb.append("ファイヤーウォール等がサービスを利用できない設定になっている可能性があります。");
                sb.append("\n");
                sb.append(appendExceptionInfo(ce));
                setErrorMessage(sb.toString());
                
            } catch (javax.naming.NamingException ne) {
                ne.printStackTrace();
                errorCode = -30;
                StringBuilder sb = new StringBuilder();
                sb.append("アプリケーションエラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(ne));
                setErrorMessage(sb.toString());
                
            } catch (LoginException le) {
                le.printStackTrace();
                errorCode = -40;
                StringBuilder sb = new StringBuilder();
                sb.append("セキュリティエラーが生じました。");
                sb.append("\n");
                sb.append("クライアントの環境が実行を許可されない設定になっている可能性があります。");
                sb.append("\n");
                sb.append(appendExceptionInfo(le));
                setErrorMessage(sb.toString());
                
            } catch (Exception oe) {
                oe.printStackTrace();
                errorCode = -50;
                StringBuilder sb = new StringBuilder();
                sb.append("予期しないエラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(oe));
                setErrorMessage(sb.toString());
            }
            
            setDone(true);
        }
    }
    
    private String appendExceptionInfo(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("例外クラス: ");
        sb.append(e.getClass().getName());
        sb.append("\n");
        if (e.getCause() != null) {
            sb.append("原因: ");
            sb.append(e.getCause().getMessage());
            sb.append("\n");
        }
        if (e.getMessage() != null) {
            sb.append("内容: ");
            sb.append(e.getMessage());
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
