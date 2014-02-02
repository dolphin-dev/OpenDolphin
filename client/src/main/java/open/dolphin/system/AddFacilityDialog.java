package open.dolphin.system;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.ServerInfo;
import open.dolphin.infomodel.UserModel;
import open.dolphin.util.HashUtil;
import org.apache.log4j.Logger;


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
    
    private AddFacilityTask task;
    private PropertyChangeListener pl;
    
    private JProgressBar bar;
    private JDialog progressDialog;
    
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
    
    @Override
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

        int windowWidth = 741;
        int windowHeight = 613;
        
        String backBtnText = ClientContext.getString("account.backBtn.text");
        String nextBtnText = ClientContext.getString("account.nextBtn.text");
        String cancelBtnText = (String)UIManager.get("OptionPane.cancelButtonText");
        String addBtnText = ClientContext.getString("account.addBtn.text");
        
        // 通信テストパネルを生成する
        oidGetter = new OIDGetter();
        
        // 使用許諾パネルを生成する
        AgreementModel agreeModel = new AgreementModel();
        try {
            InputStream ir = this.getClass().getResourceAsStream("/open/dolphin/system/asp-agreement.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ir, "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            agreeModel.setAgreeText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
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
        JPanel btnPanel;
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
            @Override
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
            @Override
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
            @Override
            public void actionPerformed(ActionEvent e) {
                addFacilityAdmin();
            }
        });
        
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        this.addComponentListener(this);
    }
    
    class OidListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String oid = (String)e.getNewValue();
            comTestOk = (oid != null && (!oid.equals("")) ) ? true : false;
            controlButton();
        }
    }
    
    class AgreementListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            boolean agree = ((Boolean)e.getNewValue()).booleanValue();
            agreementOk = agree;
            controlButton();
        }
    }
    
    class AccountInfoListener implements PropertyChangeListener {
        @Override
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
        String hashPass = HashUtil.MD5(model.getPassword());
        model.setPassword(hashPass);
        
        task = new AddFacilityTask(model);
        pl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    if (SwingWorker.StateValue.DONE==evt.getNewValue()) {
                        stopProgress2();
                    } else if (SwingWorker.StateValue.STARTED==evt.getNewValue()) {
                        startProgress2();
                    }
                }
            }
        };
        task.addPropertyChangeListener(pl);
        
        task.execute();
    }
    
    private void startProgress2() {
        String note = "アカウント情報を登録しています...";
        bar = new JProgressBar(0, 100);
        Object[] message = new Object[]{note, bar};
        JButton cancel = new JButton((String)UIManager.get("OptionPane.cancelButtonText"));
        cancel.setEnabled(false);
        JOptionPane pane = new JOptionPane(
                message, 
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{cancel});
        
        String title = ClientContext.getFrameTitle("アカウント登録");
        Component c = SwingUtilities.getWindowAncestor(this);
        progressDialog = pane.createDialog(c, title);
        progressDialog.setModal(false);
        bar.setIndeterminate(true);
        progressDialog.setVisible(true);
    }
    
    private void stopProgress2() {
        task.removePropertyChangeListener(pl);
    }
    
    /**
     * AddFacilityTask
     */
    class AddFacilityTask extends SwingWorker<Void, Void> {
        
        private UserModel user;
        
        public AddFacilityTask(UserModel user) {
            this.user = user;
        }
       
        @Override
        protected Void doInBackground() throws Exception {
            SystemDelegater sdl = new SystemDelegater();
            sdl.addFacilityUser(user);
            return null;
        }
        
        @Override
        protected void done() {
            bar.setIndeterminate(false);
            progressDialog.setVisible(false);
            if (isCancelled()) {
                return;
            }
            try {
                get();
                succeeded(null);
            } catch (InterruptedException ex) {
                interrupted(ex);
            } catch (ExecutionException ex) {
                failed(ex);
            }
        }
        
        protected void succeeded(Void result) {
            logger.debug("Task succeeded");
            okBtn.setEnabled(false);
                    
            // 成功メッセージを表示する
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getString("account.task.successMsg1")).append("\n");
            sb.append(ClientContext.getString("account.task.successMsg2")).append("\n");
            sb.append(ClientContext.getString("account.task.successMsg3")).append("\n");
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
        
        protected void failed(java.lang.Throwable cause) {

            logger.warn(cause.getMessage());
            
            String errMsg = cause.getMessage();
            String title = AddFacilityDialog.this.getTitle();
            JOptionPane.showMessageDialog(AddFacilityDialog.this, errMsg, title, JOptionPane.WARNING_MESSAGE);
        }

        protected void interrupted(java.lang.InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }
    
    @Override
    public void componentMoved(java.awt.event.ComponentEvent componentEvent) {
        Point loc = getLocation();
        System.out.println(getTitle() + " : x=" + loc.x+ " y=" + loc.y);
    }
    
    @Override
    public void componentResized(java.awt.event.ComponentEvent componentEvent) {
        int width = getWidth();
        int height = getHeight();
        System.out.println(getTitle() + " : width=" + width + " height=" + height);
    }
    
    @Override
    public void componentShown(java.awt.event.ComponentEvent componentEvent) {
    }
    
    @Override
    public void componentHidden(java.awt.event.ComponentEvent componentEvent) {
    }
}
