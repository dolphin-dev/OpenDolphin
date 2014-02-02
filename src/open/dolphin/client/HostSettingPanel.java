package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
    
    private static final String DEFAULT_FACILITY_OID = "1.3.6.1.4.1.9414.10.1";
    
    private String ipAddressPattern = "[A-Za-z0-9.\\-_]*";
    private static final String ID = "hostSetting";
    private static final String TITLE = "サーバ";
    private static final String ICON = "ntwrk_24.gif";
   
    private JTextField userIdField;
    private JTextField hostAddressField;
    
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
        String ipAddressText   = "IPアドレス:";
        
        String userInfoText    = "ユーザ情報";
        String userIdText      = "ユーザID:";
        
        // テキストフィールドを生成する
        hostAddressField = GUIFactory.createTextField(10, null, null, null);
        userIdField = GUIFactory.createTextField(10, null, null, null);
        
        // パターン制約を加える
        RegexConstrainedDocument hostDoc = new RegexConstrainedDocument(ipAddressPattern);
        hostAddressField.setDocument(hostDoc);
        
        // サーバ情報パネル
        GridBagBuilder gb = new GridBagBuilder(serverInfoText);
        int row = 0;
        JLabel label = new JLabel(ipAddressText, SwingConstants.RIGHT);
        gb.add(label,            0, row, GridBagConstraints.EAST);
        gb.add(hostAddressField, 1, row, GridBagConstraints.WEST);
        JPanel sip = gb.getProduct();
        
        // ユーザ情報パネル
        gb = new GridBagBuilder(userInfoText);
        row = 0;
        label = new JLabel(userIdText, SwingConstants.RIGHT);
        gb.add(label,       0, row, GridBagConstraints.EAST);
        gb.add(userIdField, 1, row, GridBagConstraints.WEST);
        JPanel uip = gb.getProduct();
        
        // 全体レイアウト
        gb = new GridBagBuilder();
        gb.add(sip, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(uip, 0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(new JLabel(""), 0, 2, GridBagConstraints.BOTH, 1.0, 1.0);
        setUI(gb.getProduct());
        
        connect();
    }
    
    /**
     * コンポーネントのリスナ接続を行う。
     */
    private void connect() {
        
        stateMgr = new StateMgr();
        
        // TextField へ入力または削除があった場合、cutState へ checkState() を送る
        DocumentListener dl = ProxyDocumentListener.create(stateMgr, "checkState");
        hostAddressField.getDocument().addDocumentListener(dl);
        userIdField.getDocument().addDocumentListener(dl);
        
        //
        // IME OFF FocusAdapter
        //
        hostAddressField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addFocusListener(AutoRomanListener.getInstance());        
        
        hostAddressField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userIdField.requestFocus();
            }
        });
        
        userIdField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hostAddressField.requestFocus();
            }
        });
        
        // ログインしている状態の場合、この設定はできないようにする
        if (isLoginState()) {
            userIdField.setEnabled(false);
            hostAddressField.setEnabled(false);
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

        // UserType で分岐する
        Project.UserType userType = model.getUserType();

        switch (userType) {

            case FACILITY_USER:
                val = model.getIpAddress();
                val = val != null ? val : "";
                hostAddressField.setText(val);
                if (model.getPort() != 0) {
                    hostPort = model.getPort();
                }
                break;
        }
    }
    
    /**
     * Viewの値をモデルへ設定する。
     */
    private void bindViewToModel() {
        
        // 施設IDとユーザIDを保存する
        // 施設IDとユーザIDを保存する
        String facilityId = DEFAULT_FACILITY_OID;
        String userId = userIdField.getText().trim();
        String ipAddress = hostAddressField.getText().trim();
        model.setFacilityId(facilityId);
        model.setUserId(userId);
        model.setIpAddress(ipAddress);
        model.setUserType(Project.UserType.FACILITY_USER);
        model.setPort(hostPort);
    }
    
    /**
     * 設定値を保存する。
     */
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
        
        private boolean isValid() {
            
            boolean hostAddrOk = (hostAddressField.getText().trim().equals("") == false) ? true : false;
            boolean userIdOk = (userIdField.getText().trim().equals("") == false) ? true : false;
            
            return (hostAddrOk && userIdOk) ? true : false;
            
        }
    }
}
