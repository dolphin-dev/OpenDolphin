package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.project.Project;

import open.dolphin.project.ProjectStub;

/**
 * ClaimSettingPanel
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ClaimSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "claimSetting";
    private static final String TITLE = "レセコン";
    private static final String ICON = "calc_16.gif";
    
    // GUI staff
    private JRadioButton sendClaimYes;
    private JRadioButton sendClaimNo;
    private JComboBox claimHostCombo;
    private JCheckBox claim01;
    private JTextField jmariField;
    private JTextField claimAddressField;
    private JTextField claimPortField;
    private JCheckBox useAsPVTServer;
    private JTextField bindAddress;
    
    /** 画面モデル */
    private ClaimModel model;
    
    private StateMgr stateMgr;
    
    
    public ClaimSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * GUI 及び State を生成する。
     */
    @Override
    public void start() {
        
        //
        // モデルを生成し初期化する
        //
        model = new ClaimModel();
        model.populate(getProjectStub());
        
        //
        // GUIを構築する
        //
        initComponents();
        
        //
        // bind する
        //
        bindModelToView();
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
     * GUIを構築する
     */
    private void initComponents() {
        
        // 診療行為送信ボタン
        ButtonGroup bg1 = new ButtonGroup();
        sendClaimYes = GUIFactory.createRadioButton("送信する", null, bg1);
        sendClaimNo = GUIFactory.createRadioButton("送信しない", null, bg1);
        
        // 01 小児科等
        claim01 = new JCheckBox("デフォルト01を使用");
        
        // JMARI、ホスト名、アドレス、ポート番号
        String[] hostNames = ClientContext.getStringArray("settingDialog.claim.hostNames");
        claimHostCombo = new JComboBox(hostNames);
        jmariField = GUIFactory.createTextField(10, null, null, null);
        jmariField.setToolTipText("医療機関コードの数字部分のみ12桁を入力してください。");
        claimAddressField = GUIFactory.createTextField(12, null, null, null);
        claimPortField = GUIFactory.createTextField(5, null, null, null);
        
        // 受付受信ボタン
        useAsPVTServer = GUIFactory.createCheckBox("このマシンでORCAからの受付情報を受信する", null);
        useAsPVTServer.setToolTipText("このマシンでORCAからの受付情報を受信する場合はチェックしてください");
        bindAddress = GUIFactory.createTextField(12, null, null, null);
        bindAddress.setToolTipText("複数ネットワークカードがある場合、受付受信サーバのバインドアドレスを入力してください");
        
        // CLAIM（請求）送信情報
        GridBagBuilder gbl = new GridBagBuilder("CLAIM（請求データ）送信");
        int row = 0;
        JLabel label = new JLabel("診療行為送信:");
        JPanel panel = GUIFactory.createRadioPanel(new JRadioButton[]{sendClaimYes,sendClaimNo});
        gbl.add(label, 0, row, GridBagConstraints.EAST);
        gbl.add(panel, 1, row, GridBagConstraints.CENTER);
        JPanel sendClaim = gbl.getProduct();
        
        // レセコン情報
        gbl = new GridBagBuilder("レセコン情報");
        row = 0;
        label = new JLabel("機種:");
        gbl.add(label,          0, row, GridBagConstraints.EAST);
        gbl.add(claimHostCombo, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("CLAIM診療科コード:");
        gbl.add(label,  0, row, GridBagConstraints.EAST);
        gbl.add(claim01,1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("医療機関ID:  JPN");
        gbl.add(label,      0, row, GridBagConstraints.EAST);
        gbl.add(jmariField, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("IPアドレス:");
        gbl.add(label,             0, row, GridBagConstraints.EAST);
        gbl.add(claimAddressField, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("ポート番号:");
        gbl.add(label,          0, row, GridBagConstraints.EAST);
        gbl.add(claimPortField, 1, row, GridBagConstraints.WEST);
        JPanel port = gbl.getProduct();
        
        // レセコンからの受付受信
        gbl = new GridBagBuilder("受付情報の受信");
        label = new JLabel("バインドアドレス(オプション):");
        gbl.add(useAsPVTServer, 0, 0, 2, 1, GridBagConstraints.EAST);
        gbl.add(label,          0, 1, GridBagConstraints.EAST);
        gbl.add(bindAddress,    1, 1, GridBagConstraints.WEST);
        JPanel pvt = gbl.getProduct();
        
        // 全体レイアウト
        gbl = new GridBagBuilder();
        gbl.add(sendClaim, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(port,      0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(pvt,       0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(new JLabel(""), 0, 3, GridBagConstraints.BOTH,  1.0, 1.0);
        setUI(gbl.getProduct());

        connect();       
    }
    
    /**
     * リスナを接続する。
     */
    private void connect() {
        
        stateMgr = new StateMgr();
        
        // DocumentListener
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

        String jmariPattern = "[0-9]*";
        RegexConstrainedDocument jmariDoc = new RegexConstrainedDocument(jmariPattern);
        jmariField.setDocument(jmariDoc);
        jmariField.getDocument().addDocumentListener(dl);
        jmariField.addFocusListener(AutoRomanListener.getInstance());
        
        String portPattern = "[0-9]*";
        RegexConstrainedDocument portDoc = new RegexConstrainedDocument(portPattern);
        claimPortField.setDocument(portDoc);
        claimPortField.getDocument().addDocumentListener(dl);
        claimPortField.addFocusListener(AutoRomanListener.getInstance());
        
        String ipPattern = "[A-Za-z0-9.\\-_]*";
        RegexConstrainedDocument ipDoc = new RegexConstrainedDocument(ipPattern);
        claimAddressField.setDocument(ipDoc);
        claimAddressField.getDocument().addDocumentListener(dl);
        claimAddressField.addFocusListener(AutoRomanListener.getInstance());
        
        String ipPattern2 = "[A-Za-z0-9.\\-_]*";
        RegexConstrainedDocument ipDoc2 = new RegexConstrainedDocument(ipPattern2);
        bindAddress.setDocument(ipDoc2);
        bindAddress.getDocument().addDocumentListener(dl);
        bindAddress.addFocusListener(AutoRomanListener.getInstance());
        
        // アクションリスナ
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlClaim();
            }
        };
        sendClaimYes.addActionListener(al);
        sendClaimNo.addActionListener(al);
        
        // バインドアドレス
        ActionListener al3 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlBindAddress();
            }
        };
        useAsPVTServer.addActionListener(al3);
    }
    
    /**
     * ModelToView
     */
    private void bindModelToView() {
        //
        // 診療行為送信を選択する
        //
        boolean sending = model.isSendClaim();
        sendClaimYes.setSelected(sending);
        sendClaimNo.setSelected(!sending);
        claimPortField.setEnabled(sending);
        
        // JMARICode
        String jmari = model.getJmariCode();
        jmari = jmari != null ? jmari : "";
        if (!jmari.equals("") && jmari.startsWith("JPN")) {
            jmari = jmari.substring(3);
            jmariField.setText(jmari);
        }
        
        // CLAIM ホストのIPアドレスを設定する
        String val = model.getClaimAddress();
        val = val != null ? val : "";
        claimAddressField.setText(val);
        
        // CLAIM ホストのポート番号を設定する
        val = String.valueOf(model.getClaimPort());
        val = val != null ? val : "";
        claimPortField.setText(val);
        
        // ホスト名
        val = model.getClaimHostName();
        val = val != null ? val : "";
        claimHostCombo.setSelectedItem(val);
        
        // 受付受信
        boolean tmp = model.isUseAsPVTServer();
        useAsPVTServer.setSelected(tmp);
        
        // バインドアドレス
        String bindAddr = model.getBindAddress();
        bindAddr = bindAddr != null ? bindAddr : "";
        bindAddress.setText(bindAddr);
        bindAddress.setEnabled(tmp);
        
        // 01 小児科
        claim01.setSelected(model.isClaim01());
        
    }
    
    /**
     * ViewToModel
     */
    private void bindViewToModel() {
        //
        // 診療行為送信、仮保存時、修正時、病名送信
        // の設定を保存する
        //
        model.setSendClaim(sendClaimYes.isSelected());
        
        // JMARI
        String jmari = jmariField.getText().trim();
        if (!jmari.equals("")) {
            model.setJmariCode("JPN"+jmari);
        } else {
            model.setJmariCode(null);
        }
        
        // ホスト名を保存する
        String val = (String)claimHostCombo.getSelectedItem();
        model.setClaimHostName(val);
        
        // IPアドレスを保存する
        val = claimAddressField.getText().trim();
        model.setClaimAddress(val);
        
        // ポート番号を保存する
        val = claimPortField.getText().trim();
        try {
            int port = Integer.parseInt(val);
            model.setClaimPort(port);
            
        } catch (NumberFormatException e) {
            model.setClaimPort(5001);
        }
        
        // 受付受信を保存する
        model.setUseAsPVTServer(useAsPVTServer.isSelected());
        
        // バインドアドレスを保存する
        val = bindAddress.getText().trim();
        model.setBindAddress(val);
        
        // 01 小児科
        model.setClaim01(claim01.isSelected());
    }
    
    /**
     * 画面も出るクラス。
     */
    class ClaimModel {
        
        private boolean sendClaim;
        private String claimHostName;
        private String version;
        private String jmariCode;
        private String claimAddress;
        private int claimPort;
        private boolean useAsPvtServer;
        private String bindAddress;
        private boolean claim01;
        
        public void populate(ProjectStub stub) {
            
            // 診療行為送信
            setSendClaim(Project.getBoolean(Project.SEND_CLAIM)); // stub.getSendClaim()
            
            // JMARI code
            setJmariCode(Project.getString(Project.JMARI_CODE)); // stub.getJMARICode()
            
            // CLAIM ホストのIPアドレス
            setClaimAddress(Project.getString(Project.CLAIM_ADDRESS));  // stub.getClaimAddress()
            
            // CLAIM ホストのポート番号
            setClaimPort(Project.getInt(Project.CLAIM_PORT)); // stub.getClaimPort()
            
            // ホスト名
            setClaimHostName(Project.getString(Project.CLAIM_HOST_NAME)); // stub.getClaimHostName()
            
            // 受付受信
            setUseAsPVTServer(Project.getBoolean(Project.USE_AS_PVT_SERVER));    // stub.getUseAsPVTServer()
            
            // バインドアドレス
            setBindAddress(Project.getString(Project.CLAIM_BIND_ADDRESS));   // stub.getBindAddress()
            
            // 01 小児科等
            setClaim01(Project.getBoolean(Project.CLAIM_01));   // stub.isClaim01()
        }
        
        public void restore(ProjectStub stub) {
            
            // 診療行為送信
            Project.setBoolean(Project.SEND_CLAIM, isSendClaim());  //stub.setSendClaim(isSendClaim());
            
            // JMARI
            Project.setString(Project.JMARI_CODE, getJmariCode());    //stub.setJMARICode(getJmariCode());
            
            // CLAIM ホストのIPアドレス
            Project.setString(Project.CLAIM_ADDRESS, getClaimAddress());        //stub.setClaimAddress(getClaimAddress());
            
            // CLAIM ホストのポート番号
            Project.setInt(Project.CLAIM_PORT, getClaimPort());        //stub.setClaimPort(getClaimPort());
            
            // ホスト名
            Project.setString(Project.CLAIM_HOST_NAME, getClaimHostName());        //stub.setClaimHostName(getClaimHostName());
            
            // 受付受信
            Project.setBoolean(Project.USE_AS_PVT_SERVER, isUseAsPVTServer());        //stub.setUseAsPVTServer(isUseAsPVTServer());
            
            // バインドアドレス
            Project.setString(Project.CLAIM_BIND_ADDRESS, getBindAddress());            //stub.setBindAddress(getBindAddress());
            
            // 01 小児科
            Project.setBoolean(Project.CLAIM_01, isClaim01());        //stub.setClaim01(isClaim01());
        }
        
        public boolean isSendClaim() {
            return sendClaim;
        }
        
        public void setSendClaim(boolean sendClaim) {
            this.sendClaim = sendClaim;
        }
        
        public boolean isUseAsPVTServer() {
            return useAsPvtServer;
        }
        
        public void setUseAsPVTServer(boolean useAsPvtServer) {
            this.useAsPvtServer = useAsPvtServer;
        }
        
        public String getClaimHostName() {
            return claimHostName;
        }
        
        public void setClaimHostName(String claimHostName) {
            this.claimHostName = claimHostName;
        }
        
        public String getClaimAddress() {
            return claimAddress;
        }
        
        public void setClaimAddress(String claimAddress) {
            this.claimAddress = claimAddress;
        }
        
        public String getBindAddress() {
            return bindAddress;
        }
        
        public void setBindAddress(String bindAddress) {
            this.bindAddress = bindAddress;
        }
        
        public int getClaimPort() {
            return claimPort;
        }
        
        public void setClaimPort(int claimPort) {
            this.claimPort = claimPort;
        }

        public String getJmariCode() {
            return jmariCode;
        }

        public void setJmariCode(String jmariCode) {
            this.jmariCode = jmariCode;
        }
        
        public boolean isClaim01() {
            return claim01;
        }
        
        public void setClaim01(boolean b) {
            this.claim01 = b;
        }
    }
    
    class StateMgr {
        
        public void checkState() {
            
            AbstractSettingPanel.State newState = isValid()
            ? AbstractSettingPanel.State.VALID_STATE
                    : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }
        
        public void controlClaim() {
            //
            // 診療行為の送信を行う場合のみ
            // 仮保存、修正、病名送信、ホスト選択、ポートがアクティブになる
            //
            boolean b = sendClaimYes.isSelected();
            
            claimPortField.setEnabled(b);
            
            this.checkState();
        }
        
        public void controlVersion() {
            boolean b = true;   //v40.isSelected();
            jmariField.setEnabled(b);
            this.checkState();
        }
        
        public void controlBindAddress() {
            boolean b = useAsPVTServer.isSelected();
            bindAddress.setEnabled(b);
            this.checkState();
        }
        
        private boolean isValid() {
            
            boolean jmariOk = false;
            boolean claimAddrOk = false;
            boolean claimPortOk = false;
            boolean bindAdrOk = false;
            
            String code = jmariField.getText().trim();
            if (!code.equals("") && code.length() == 12) {
                jmariOk = true;
            }
            
            if (sendClaimYes.isSelected()) {
                claimAddrOk = isIPAddress(claimAddressField.getText().trim());
                claimPortOk = isPort(claimPortField.getText().trim());
            } else {
                claimAddrOk = true;
                claimPortOk = true;
            }
            
            if (useAsPVTServer.isSelected()) {
                String test = bindAddress.getText().trim();
                if (test != null && (!test.equals(""))) {
                    bindAdrOk = isIPAddress(test);
                } else {
                    bindAdrOk = true;
                }
            } else {
                bindAdrOk = true;
            }
            
            return (jmariOk && claimAddrOk && claimPortOk && bindAdrOk) ? true : false;
        }

        private boolean isIPAddress(String test) {
            return (test == null || test.equals("")) ? false: true;
        }
        
        private boolean isPort(String test) {
            
            boolean ret = false;
            
            if (test != null) {
                try {
                    int port = Integer.parseInt(test);
                    ret = port < 0 || port > 65535 ? false : true;
                }catch (Exception e) {
                }
            }
            
            return ret;
        }
    }
}
