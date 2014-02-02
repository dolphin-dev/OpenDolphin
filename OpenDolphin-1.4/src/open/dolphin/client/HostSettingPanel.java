/*
 * Created on 2005/06/01
 *
 */
package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import open.dolphin.helper.GridBagBuilder;
import open.dolphin.project.DolphinPrincipal;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;

/**
 * HostSettingPanel
 *
 * @author Kazushi Minagawa
 */
public class HostSettingPanel extends AbstractSettingPanel {
    
    private String ipAddressPattern = "[A-Za-z0-9.\\-_]*";
    private static final String ID = "hostSetting";
    private static final String TITLE = "サーバ";
    private static final String ICON = "ntwrk_16.gif";
    
    // 設定用の GUI components
    private JRadioButton aspMember;
    private JRadioButton facilityUser;
    private JTextField userIdField;
    private JTextField hostAddressField;
    private JTextField facilityIdField;
    private JButton registTesterBtn;
    
    // JBoss Server PORT
    private int hostPort = 1099;
    
    /** 画面用のモデル */
    private ServerModel model;
    
    private StateMgr stateMgr;
    
    public HostSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * サーバ設定画面を開始する。
     */
    @Override
    public void start() {
        
        //
        // 画面モデルを生成し初期化する
        //
        model = new ServerModel();
        model.populate(getProjectStub());
        
        //
        // GUI を生成する
        //
        initComponents();
        
        //
        // コンテナで表示される
        //
        bindModelToView();
    }
    
    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {
        
        String serverInfoText  = "サーバ情報";
        String serverStyleText = "利用形式:";
        String aspMemberText   = "ASP";
        String useLocaltext    = "院内サーバ";
        String ipAddressText   = "IPアドレス:";
        
        String userInfoText    = "ユーザ情報";
        String userIdText      = "ユーザID:";
        String facilityIdText  = "医療機関ID:";
        
        String initServerText  = "ASP評価の申し込み";
        String addSuperUserText = "アカウント作成";
        
        // テキストフィールドを生成する
        hostAddressField = GUIFactory.createTextField(10, null, null, null);
        facilityIdField = GUIFactory.createTextField(15, null, null, null);
        userIdField = GUIFactory.createTextField(10, null, null, null);
        
        // パターン制約を加える
        RegexConstrainedDocument hostDoc = new RegexConstrainedDocument(ipAddressPattern);
        hostAddressField.setDocument(hostDoc);
        
        // ボタングループを生成する
        ButtonGroup bg = new ButtonGroup();
        aspMember = GUIFactory.createRadioButton(aspMemberText, null, bg);
        facilityUser = GUIFactory.createRadioButton(useLocaltext, null, bg);
        
        // 管理者登録ボタン
        registTesterBtn = new JButton(addSuperUserText);
        
        // サーバ情報パネル
        GridBagBuilder gb = new GridBagBuilder(serverInfoText);
        int row = 0;
        JLabel label = new JLabel(serverStyleText, SwingConstants.RIGHT);
        JPanel panel = GUIFactory.createRadioPanel(new JRadioButton[]{aspMember,facilityUser});
        gb.add(label, 0, row, GridBagConstraints.EAST);
        gb.add(panel, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel(ipAddressText, SwingConstants.RIGHT);
        gb.add(label,            0, row, GridBagConstraints.EAST);
        gb.add(hostAddressField, 1, row, GridBagConstraints.WEST);
        JPanel sip = gb.getProduct();
        
        // ユーザ情報パネル
        gb = new GridBagBuilder(userInfoText);
        row = 0;
        label = new JLabel(userIdText, SwingConstants.RIGHT);
        gb.add(label,       0, row, GridBagConstraints.EAST);
        gb.add(userIdField, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel(facilityIdText, SwingConstants.RIGHT);
        gb.add(label,           0, row, GridBagConstraints.EAST);
        gb.add(facilityIdField, 1, row, GridBagConstraints.WEST);
        JPanel uip = gb.getProduct();
        
        // アカウント作成
        gb = new GridBagBuilder(initServerText);
        row = 0;
        label = new JLabel("");
        gb.add(label,           0, row, GridBagConstraints.EAST);
        gb.add(registTesterBtn, 1, row, GridBagConstraints.CENTER);
        JPanel iip = gb.getProduct();
        
        // 全体レイアウト
        gb = new GridBagBuilder();
        gb.add(sip, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(uip, 0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(iip, 0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(new JLabel(""), 0, 3, GridBagConstraints.BOTH, 1.0, 1.0);
        setUI(gb.getProduct());
        
        //
        // コンポーネントのリスナ接続を行う
        //
        connect();
    }
    
    /**
     * コンポーネントのリスナ接続を行う。
     */
    private void connect() {
        
        stateMgr = new StateMgr();
        
        // TextField へ入力または削除があった場合、cutState へ checkState() を送る
        //DocumentListener dl = ProxyDocumentListener.create(stateMgr, "checkState");
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }
        };

        hostAddressField.getDocument().addDocumentListener(dl);
        facilityIdField.getDocument().addDocumentListener(dl);
        userIdField.getDocument().addDocumentListener(dl);
        
        //
        // IME OFF FocusAdapter
        //
        hostAddressField.addFocusListener(AutoRomanListener.getInstance());
        facilityIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        
        // サーバの利用形態 ラジオボタンがクリックされたら　cutState へ checkState を送る
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlAddressField();
            }

        };
        aspMember.addActionListener(al);
        facilityUser.addActionListener(al);
        
        // 管理者登録ボタンがクリックされたら自身をPropertyChangeListener にし
        // 管理者登録ダイアログを別スレッドでスタートさせる
        registTesterBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                make5TestAccount();
            }
        });
        
        facilityIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostAddressField.requestFocus();
            }
        });
        
        hostAddressField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userIdField.requestFocus();
            }
        });
        
        userIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostAddressField.requestFocus();
            }
        });
        
        // ログインしている状態の場合、この設定はできないようにする
        if (isLoginState()) {
            facilityUser.setEnabled(false);
            aspMember.setEnabled(false);
            userIdField.setEnabled(false);
            hostAddressField.setEnabled(false);
            facilityIdField.setEnabled(false);
            registTesterBtn.setEnabled(false);
        }
    }
    
    /**
     * Model 値を表示する。
     */
    private void bindModelToView() {
                
        // userId設定する
        String val = model.getUserId();
        val = val != null ? val : "";
        userIdField.setText(val);

        // 施設IDを設定する
        val = model.getFacilityId();
        val = val != null ? val : "";
        facilityIdField.setText(val);

        // UserType で分岐する
        Project.UserType userType = model.getUserType();

        switch (userType) {
            case ASP_MEMBER:
                aspMember.doClick();
                break;

            case ASP_TESTER:
                aspMember.doClick();
                break;

            case FACILITY_USER:
                val = model.getIpAddress();
                val = val != null ? val : "";
                hostAddressField.setText(val);
                if (model.getPort() != 0) {
                    hostPort = model.getPort();
                }
                facilityUser.doClick();
                break;
        }
    }
    
    /**
     * Viewの値をモデルへ設定する。
     */
    private void bindViewToModel() {
        
        // 施設IDとユーザIDを保存する
        String facilityId = facilityIdField.getText().trim();
        String userId = userIdField.getText().trim();
        model.setFacilityId(facilityId);
        model.setUserId(userId);
        
        // メンバータイプを保存する
        if (aspMember.isSelected()) {
            model.setUserType(Project.UserType.ASP_MEMBER);
        } else if (facilityUser.isSelected()) {
            String val = hostAddressField.getText().trim();
            if (!val.equals("")) {
                model.setUserType(Project.UserType.FACILITY_USER);
                model.setIpAddress(val);
            }
        }
        
        model.setPort(hostPort);
    }
    
    /**
     * 5分間評価用のアカウントを作成する。
     */
    public void make5TestAccount() {
        AddFacilityDialog af = new AddFacilityDialog();
        //PropertyChangeListener pl = ProxyPropertyChangeListener.create(this, "newAccount", new Class[]{ServerInfo.class});
        PropertyChangeListener pl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                newAccount((ServerInfo) evt.getNewValue());
            }
        };
        af.addPropertyChangeListener(AddFacilityDialog.ACCOUNT_INFO, pl);
        Thread t = new Thread(af);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * 管理者登録ダイアログの結果を受け取り情報を表示する。
     */
    public void newAccount(ServerInfo info) {
        
        if (info != null) {
            facilityIdField.setText(info.getFacilityId());
            userIdField.setText(info.getAdminId());
            aspMember.doClick();
        }
    }
    
    /**
     * 設定値を保存する。
     */
    @Override
    public void save() {
        bindViewToModel();
        model.restore(getProjectStub());
    }
    
    /**
     * サーバ画面設定用のモデルクラス。
     */
    class ServerModel {
        
        private Project.UserType userType;
        private String ipAddress;
        private int port;
        private String facilityId;
        private String userId;
        
        public ServerModel() {
        }
        
        /**
         * ProjectStub からポピュレイトする。
         */
        public void populate(ProjectStub stub) {
                        
            // userId設定する
            setUserId(stub.getUserId());
            
            // 施設IDを設定する
            setFacilityId(stub.getFacilityId());
            
            // UserTypeを設定する
            setUserType(stub.getUserType());
            
            // IPAddressを設定する
            setIpAddress(stub.getHostAddress());
            
            // Portを設定する
            setPort(stub.getHostPort());
        }
        
        /**
         * ProjectStubへリストアする。
         */
        public void restore(ProjectStub stub) {
            
            // 施設IDとユーザIDを保存する
            stub.setFacilityId(getFacilityId());
            stub.setUserId(getUserId());
            
            // Principleを保存する
            DolphinPrincipal principal = new DolphinPrincipal();
            principal.setFacilityId(getFacilityId());
            principal.setUserId(getUserId());
            stub.setDolphinPrincipal(principal);
            
            // メンバータイプを保存する
            stub.setUserType(getUserType());
            
            // IPAddressを保存する
            stub.setHostAddress(getIpAddress());
            
            // Portを設定を保存する
            stub.setHostPort(getPort());
        }

        public Project.UserType getUserType() {
            return userType;
        }

        public void setUserType(Project.UserType userType) {
            this.userType = userType;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getFacilityId() {
            return facilityId;
        }

        public void setFacilityId(String facilityId) {
            this.facilityId = facilityId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * Mediator 的 StateMgr クラス。
     */
    class StateMgr {
        
        public void checkState() {
            
            AbstractSettingPanel.State newState = isValid() 
                                                ? AbstractSettingPanel.State.VALID_STATE 
                                                : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }
        
        public void controlAddressField() {
            
            if (aspMember.isSelected()) {
                hostAddressField.setText("");
                hostAddressField.setEnabled(false);
                
            } else if (facilityUser.isSelected()) {
                hostAddressField.setEnabled(true);
            }
            
            this.checkState();
        }
        
        private boolean isValid() {
            
            boolean hostAddrOk = isIPAddress(hostAddressField.getText().trim());
            boolean facilityIdOk = (facilityIdField.getText().trim().equals("") == false) ? true : false;
            boolean userIdOk = (userIdField.getText().trim().equals("") == false) ? true : false;
            
            if (facilityUser.isSelected()) {
                return (facilityIdOk && hostAddrOk && userIdOk) ? true : false;
            } else {
                return (facilityIdOk && userIdOk) ? true : false;
            }
        }
        
        private boolean isIPAddress(String test) {
            
            boolean ret = false;
            
            if (test != null) {
                test = test.replace('.', ':');
                String[] ips = test.split(":");
                if (ips.length == 4) {
                    try {
                        boolean num = true;
                        for (int i = 0; i < ips.length; i++) {
                            int a = Integer.parseInt(ips[i]);
                            if (a < 0 || a > 255) {
                                num = false;
                                break;
                            }
                        }
                        ret = num;
                        
                    } catch (Exception e) {
                    }
                }
            }
            
            return ret;
        }
    }
}
