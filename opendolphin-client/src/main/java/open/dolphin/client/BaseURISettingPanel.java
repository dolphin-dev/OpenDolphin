package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.project.ProjectStub;

/**
 * Base URI を設定する画面。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class BaseURISettingPanel extends AbstractSettingPanel {
    
    private String ipAddressPattern = "[A-Za-z0-9.\\-_:/]*";
    private static final String ID = "hostSetting";
    private static final String TITLE = "サーバ";
    private static final String ICON = "ntwrk_16.gif";
    
    // 設定用の GUI components
    private JTextField facilityIdField;
    private JTextField userIdField;
    private JTextField baseURIField;
    
    // 画面用のモデル
    private ServerModel model;
    
    private StateMgr stateMgr;
    
    public BaseURISettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * サーバ設定画面を開始する。
     */
    @Override
    public void start() {
        
        // 画面モデルを生成し初期化する
        model = new ServerModel();
        model.populate(getProjectStub());
        
        // GUI を生成する
        initComponents();
        
        // コンテナで表示される
        bindModelToView();
    }
    
    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {
        
        String serverInfoText  = "接続設定";
        String facilityIdText  = "医療機関ID:";
        String userIdText      = "ユーザID:";
        String baseURIText     = "ベースURI:";

        // テキストフィールドを生成する
        facilityIdField = GUIFactory.createTextField(15, null, null, null);
        userIdField = GUIFactory.createTextField(15, null, null, null);
        baseURIField = GUIFactory.createTextField(15, null, null, null);
        
        // パターン制約を加える
        RegexConstrainedDocument hostDoc = new RegexConstrainedDocument(ipAddressPattern);
        baseURIField.setDocument(hostDoc);
        
        // サーバ情報パネル
        GridBagBuilder gb = new GridBagBuilder(serverInfoText);

        // 医療機関ID
        int row = 0;
        JLabel label = new JLabel(facilityIdText, SwingConstants.RIGHT);
        gb.add(label,           0, row, GridBagConstraints.EAST);
        gb.add(facilityIdField, 1, row, GridBagConstraints.WEST);

        // ユーザID
        row++;
        label = new JLabel(userIdText, SwingConstants.RIGHT);
        gb.add(label,       0, row, GridBagConstraints.EAST);
        gb.add(userIdField, 1, row, GridBagConstraints.WEST);

        // Base URI
        row++;
        label = new JLabel(baseURIText, SwingConstants.RIGHT);
        gb.add(label,            0, row, GridBagConstraints.EAST);
        gb.add(baseURIField, 1, row, GridBagConstraints.WEST);
        JPanel sip = gb.getProduct();
        
        // 全体レイアウト
        gb = new GridBagBuilder();
        gb.add(sip, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gb.add(new JLabel(""), 0, 3, GridBagConstraints.BOTH, 1.0, 1.0);
        setUI(gb.getProduct());
        
        // コンポーネントのリスナ接続を行う
        connect();
    }
    
    /**
     * コンポーネントのリスナ接続を行う。
     */
    private void connect() {
        
        stateMgr = new StateMgr();
        
        // TextField へ入力または削除があった場合、cutState へ checkState() を送る
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

        facilityIdField.getDocument().addDocumentListener(dl);
        userIdField.getDocument().addDocumentListener(dl);
        baseURIField.getDocument().addDocumentListener(dl);
        
        // IME OFF FocusAdapter
        facilityIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        baseURIField.addFocusListener(AutoRomanListener.getInstance());
        
        // Focus をサイクルさせる
        facilityIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userIdField.requestFocus();
            }
        });
        
        userIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                baseURIField.requestFocus();
            }
        });

        baseURIField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                facilityIdField.requestFocus();
            }
        });
        
        // ログインしている状態の場合、この設定はできないようにする
        if (isLoginState()) {
            userIdField.setEnabled(false);
            baseURIField.setEnabled(false);
            facilityIdField.setEnabled(false);
        }
    }
    
    /**
     * Model 値を表示する。
     */
    private void bindModelToView() {

        // 施設ID
        String val = model.getFacilityId();
        val = val != null ? val : "";
        facilityIdField.setText(val);
        
        // userId
        val = model.getUserId();
        val = val != null ? val : "";
        userIdField.setText(val);

        // base URI
        val = model.getBaseURI();
        val = val != null ? val : "";
        baseURIField.setText(val);
    }
    
    /**
     * Viewの値をモデルへ設定する。
     */
    private void bindViewToModel() {
        model.setFacilityId(facilityIdField.getText().trim());
        model.setUserId(userIdField.getText().trim());
        model.setBaseURI(baseURIField.getText().trim());
    }
    
    /**
     * 設定値を保存する。
     */
    @Override
    public void save() {

        // ViewToModel
        bindViewToModel();

        // Store Model to ProjectStub
        model.restore(getProjectStub());
    }
    
    /**
     * サーバ画面設定用のモデルクラス。
     */
    class ServerModel {

        private String facilityId;
        private String userId;
        private String baseURI;
        
        public ServerModel() {
        }
        
        /**
         * ProjectStub からポピュレイトする。
         */
        public void populate(ProjectStub stub) {

            // 施設IDを設定する
            setFacilityId(stub.getFacilityId());
                        
            // userId設定する
            setUserId(stub.getUserId());
            
            // baseURI を設定する
            setBaseURI(stub.getServerURI());
        }
        
        /**
         * ProjectStubへリストアする。
         */
        public void restore(ProjectStub stub) {
            
            // 施設IDを保存する
            stub.setFacilityId(getFacilityId());    // 1.3.6.1.4.1.9414.2.xxx

            // ユーザIDを保存する
            stub.setUserId(getUserId());            // local userId

            // baseURIを保存する
            stub.setServerURI(getBaseURI());
        }

        public String getBaseURI() {
            return baseURI;
        }

        public void setBaseURI(String ipAddress) {
            this.baseURI = ipAddress;
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
            boolean ok = true;
            ok = ok && (!facilityIdField.getText().trim().equals(""));
            ok = ok && (!userIdField.getText().trim().equals(""));
            ok = ok && (!baseURIField.getText().trim().equals(""));
           return ok;
        }
    }
}
