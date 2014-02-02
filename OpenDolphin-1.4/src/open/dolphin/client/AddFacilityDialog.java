package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jboss.security.Util;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

import open.dolphin.ejb.RemoteSystemService;
import open.dolphin.infomodel.UserModel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * 医療機関と管理責任者を登録するクラス。
 *
 * @Author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class AddFacilityDialog extends JDialog implements ComponentListener, Runnable {
    
    public static final String ACCOUNT_INFO = "accountInfo";
    
    private enum AccountState {COM_TEST, AGREEMENT, ACCOUNT_INFO};
    
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton okBtn;
    private JButton cancelBtn;
    private JButton nextBtn;
    private JButton backBtn;
    
    private PropertyChangeSupport boundSupport;
    private ServerInfo serverInfo;
    
    private OIDGetter oidGetter;
    private AgreementPanel agreement;
    private AccountInfoPanel accountInfo;
    
    private AccountState state = AccountState.COM_TEST;
    private boolean comTestOk;
    private boolean agreementOk;
    private boolean accountInfoOk;
    
    private Logger logger;
    
    
    public AddFacilityDialog() {
        super((Frame)null, null, true);
        logger = ClientContext.getBootLogger();
        boundSupport = new PropertyChangeSupport(this);
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void run() {
        initialize();
        connect();
        cardLayout.show(cardPanel, "comTest");
        this.setVisible(true);
    }
    
    public void setServerInfo(ServerInfo info) {
        serverInfo = info;
        boundSupport.firePropertyChange(ACCOUNT_INFO, null, serverInfo);
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    private void initialize() {
        
        // リソースから値を取得する
        String windowTitleitle = ClientContext.getString("account.window.tile");

//        int windowWidth = ClientContext.getInt("account.window.width");
//        int windowHeight = ClientContext.getInt("account.window.height");

        int windowWidth = 741;
        int windowHeight = 613;

        String agreementRes = ClientContext.getString("account.agreement.resource");
        String agreementEnc = ClientContext.getString("account.agreement.encoding");
        
        String backBtnText = ClientContext.getString("account.backBtn.text");
        String nextBtnText = ClientContext.getString("account.nextBtn.text");
        String cancelBtnText = (String)UIManager.get("OptionPane.cancelButtonText");
        String addBtnText = ClientContext.getString("account.addBtn.text");
        
        // 通信テストパネルを生成する
        oidGetter = new OIDGetter();
        
        // 使用許諾パネルを生成する
        AgreementModel agreeModel = new AgreementModel();
        try {
            InputStreamReader ir = new InputStreamReader(ClientContext.getResourceAsStream(agreementRes), agreementEnc);
            BufferedReader reader = new BufferedReader(ir);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            agreeModel.setAgreeText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        agreement = new AgreementPanel(agreeModel);
        
        // アカウント情報パネルを生成する
        accountInfo = new AccountInfoPanel();
        
        // カードレアイウトへ配置する
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardLayout.addLayoutComponent(oidGetter, "comTest");
        cardLayout.addLayoutComponent(agreement, "agreement");
        cardLayout.addLayoutComponent(accountInfo, "accountInfo");
        cardPanel.add(oidGetter, "comTest");
        cardPanel.add(agreement, "agreement");
        cardPanel.add(accountInfo, "accountInfo");
        
        // 戻るボタンを生成する
        backBtn = new JButton(backBtnText);
        backBtn.setEnabled(false);
        
        // 次項ボタンを生成する
        nextBtn = new JButton(nextBtnText);
        nextBtn.setEnabled(false);
        
        // 登録ボタンを生成する
        okBtn = new JButton(addBtnText);
        okBtn.setEnabled(false);
        
        // キャンセルボタンを生成する
        cancelBtn = new JButton(cancelBtnText);
        
        // ボタンパネルを生成する
        JPanel btnPanel = null;
        if (ClientContext.isMac()) {
            btnPanel = GUIFactory.createCommandButtonPanel(
                    new JButton[]{backBtn, nextBtn, cancelBtn, okBtn});
            
        } else {
            btnPanel = GUIFactory.createCommandButtonPanel(
                    new JButton[]{backBtn, nextBtn, okBtn, cancelBtn});
        }
        
        // 全体を配置する
        JPanel content = new JPanel(new BorderLayout(0, 17));
        content.add(cardPanel, BorderLayout.CENTER);
        content.add(btnPanel,BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        // コンテントにする
        this.getContentPane().add(content, BorderLayout.CENTER);
        
        // Window の設定を行う
        this.setTitle(ClientContext.getFrameTitle(windowTitleitle));
        this.setSize(new Dimension(windowWidth, windowHeight));
        Dimension size = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int top = (size.height - windowHeight) / 3;
        int left = (size.width - windowWidth) / 2;
        this.setLocation(left, top);
    }
    
    /**
     * イベント接続を行う。
     */
    private void connect() {
        
        oidGetter.addOidPropertyListener(new OidListener());
        
        agreement.addAgreePropertyListener(new AgreementListener());
        
        accountInfo.addValidInfoPropertyListener(new AccountInfoListener());
        
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (state == AccountState.AGREEMENT) {
                    setState(AccountState.COM_TEST);
                    cardLayout.show(cardPanel, "comTest");
                } else if (state == AccountState.ACCOUNT_INFO) {
                    setState(AccountState.AGREEMENT);
                    cardLayout.show(cardPanel, "agreement");
                }
            }
        });
        
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (state == AccountState.COM_TEST) {
                    setState(AccountState.AGREEMENT);
                    cardLayout.show(cardPanel, "agreement");
                } else if (state == AccountState.AGREEMENT) {
                    setState(AccountState.ACCOUNT_INFO);
                    cardLayout.show(cardPanel, "accountInfo");
                }
            }
        });
        
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFacilityAdmin();
            }
        });
        
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        this.addComponentListener(this);
    }
    
    class OidListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String oid = (String)e.getNewValue();
            comTestOk = (oid != null && (!oid.equals("")) ) ? true : false;
            controlButton();
        }
    }
    
    class AgreementListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            boolean agree = ((Boolean)e.getNewValue()).booleanValue();
            agreementOk = agree;
            controlButton();
        }
    }
    
    class AccountInfoListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            boolean account = ((Boolean)e.getNewValue()).booleanValue();
            accountInfoOk = account;
            controlButton();
        }
    }
    
    private void setState(AccountState s) {
        state = s;
        controlButton();
    }
    
    private void controlButton() {
        
        backBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        okBtn.setEnabled(false);
        
        switch (state) {
            
            case COM_TEST:
                nextBtn.setEnabled(comTestOk);
                break;
                
            case AGREEMENT:
                backBtn.setEnabled(true);
                nextBtn.setEnabled(agreementOk);
                break;
                
            case ACCOUNT_INFO:
                backBtn.setEnabled(true);
                okBtn.setEnabled(accountInfoOk);
                break;
        }
    }
    
    private void close() {
        setVisible(false);
        dispose();
    }
    
    /**
     * 施設及び管理者アカウントを登録する。
     */
    private void addFacilityAdmin() {
        
        // 登録するユーザモデルを得る
        UserModel model = accountInfo.getModel();
        
        // Password の hash化を行う
        String userId = model.getUserId();
        String Algorithm = ClientContext.getString("addUser.password.hash.algorithm");
        String encoding = ClientContext.getString("addUser.password.hash.encoding");
        //String charset = ClientContext.getString("addUser.password.hash.charset");
        String charset = null;
        String hashPass = Util.createPasswordHash(Algorithm,encoding,charset,userId,model.getPassword());
        model.setPassword(hashPass);
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        
        AddFacilityTask task = new AddFacilityTask(app, model);
        
        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = "アカウント登録";
        String note = userId + "を登録しています...";
        Component c = SwingUtilities.getWindowAncestor(this);
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, 200, 60*1000);
        //taskMonitor.addPropertyChangeListener(w);
        
        appCtx.getTaskService().execute(task);
 
    }
    
    /**
     * AddFacilityTask
     */
    class AddFacilityTask extends Task<Void, Void> {
        
        private UserModel user;
        
        public AddFacilityTask(Application app, UserModel user) {
            super(app);
            this.user = user;
        }
       
        @Override
        protected Void doInBackground() throws Exception {
            
            String qid = "admin";
            String password = "secret";
            String securityDomain = "openDolphinSysAd";
            String providerURL = "jnp://localhost:1099";

            UsernamePasswordHandler h = new UsernamePasswordHandler(qid, password.toCharArray());
            LoginContext lc = new LoginContext(securityDomain, h);
            lc.login();

            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            props.setProperty("java.naming.provider.url",providerURL);
            props.setProperty("java.naming.factory.url.pkgs","org.jboss.namingrg.jnp.interfaces");
            InitialContext ctx = new InitialContext(props);

            RemoteSystemService service = (RemoteSystemService) ctx.lookup("openDolphin/RemoteSystemService");
            service.addFacilityAdmin(user);
            
            return null;
        }
        
        @Override
        protected void succeeded(Void result) {
            logger.debug("Task succeeded");
            okBtn.setEnabled(false);
                    
            // 成功メッセージを表示する
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getString("account.task.successMsg1"));
            sb.append("\n");
            sb.append(ClientContext.getString("account.task.successMsg2"));
            sb.append("\n");
            sb.append(ClientContext.getString("account.task.successMsg3"));
            sb.append("\n");
            sb.append(ClientContext.getString("account.task.successMsg4"));
            JOptionPane.showMessageDialog(
                    AddFacilityDialog.this,
                    sb.toString(),
                    AddFacilityDialog.this.getTitle(),
                    JOptionPane.INFORMATION_MESSAGE);

            // サーバアカウント情報を通知する
            ServerInfo info = new ServerInfo();
            info.setAdminId(user.getUserId());
            setServerInfo(info);

            AddFacilityDialog.this.setVisible(false);
            AddFacilityDialog.this.dispose();
        }
        
        @Override
        protected void failed(java.lang.Throwable cause) {
            logger.warn(cause.getMessage());
            
            String errMsg = null;
            
            if (cause instanceof javax.ejb.EJBAccessException) {
                StringBuilder sb = new StringBuilder();
                sb.append("システム設定エラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(cause));
                errMsg = sb.toString();
                
            } else if (cause instanceof javax.naming.CommunicationException) {
                StringBuilder sb = new StringBuilder();
                sb.append("ASPサーバに接続できません。");
                sb.append("\n");
                sb.append("ファイヤーウォール等がサービスを利用できない設定になっている可能性があります。");
                sb.append("\n");
                sb.append(appendExceptionInfo(cause));
                errMsg = sb.toString();
                
            } else if (cause instanceof javax.naming.NamingException) {
                StringBuilder sb = new StringBuilder();
                sb.append("アプリケーションエラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(cause));
                errMsg = sb.toString();
                
            } else if (cause instanceof LoginException) {
                StringBuilder sb = new StringBuilder();
                sb.append("セキュリティエラーが生じました。");
                sb.append("\n");
                sb.append("クライアントの環境が実行を許可されない設定になっている可能性があります。");
                sb.append("\n");
                sb.append(appendExceptionInfo(cause));
                errMsg = sb.toString();
                
            } else if (cause instanceof Exception) {
                StringBuilder sb = new StringBuilder();
                sb.append("予期しないエラー");
                sb.append("\n");
                sb.append(appendExceptionInfo(cause));
                errMsg = sb.toString();
            }
            
            String title = AddFacilityDialog.this.getTitle();
            JOptionPane.showMessageDialog(AddFacilityDialog.this, errMsg, title, JOptionPane.WARNING_MESSAGE);
            
        }

        @Override
        protected void interrupted(java.lang.InterruptedException e) {
            logger.warn(e.getMessage());
        }
        
        @Override
        protected void cancelled() {
            logger.debug("Task cancelled");
        }
        
        private String appendExceptionInfo(java.lang.Throwable cause) {
            StringBuilder sb = new StringBuilder();
            sb.append("例外クラス: ");
            sb.append(cause.getClass().getName());
            sb.append("\n");
            if (cause.getCause() != null) {
                sb.append("原因: ");
                sb.append(cause.getCause().getMessage());
                sb.append("\n");
            }
            if (cause.getMessage() != null) {
                sb.append("内容: ");
                sb.append(cause.getMessage());
                sb.append("\n");
            }
            return sb.toString();
        } 
    }
    
    public void componentMoved(java.awt.event.ComponentEvent componentEvent) {
        Point loc = getLocation();
        System.out.println(getTitle() + " : x=" + loc.x+ " y=" + loc.y);
    }
    
    public void componentResized(java.awt.event.ComponentEvent componentEvent) {
        int width = getWidth();
        int height = getHeight();
        System.out.println(getTitle() + " : width=" + width + " height=" + height);
    }
    
    public void componentShown(java.awt.event.ComponentEvent componentEvent) {
    }
    
    public void componentHidden(java.awt.event.ComponentEvent componentEvent) {
    }
}
