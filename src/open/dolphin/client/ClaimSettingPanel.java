package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import open.dolphin.helper.GridBagBuilder;

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
    private static final String ICON = "calc_24.gif";
    
    // GUI staff
    private JRadioButton sendClaimYes;
    private JRadioButton sendClaimNo;
    private JComboBox claimHostCombo;
    private JCheckBox claim01;
    private JRadioButton v34;
    private JRadioButton v40;
    private JTextField jmariField;
    private JTextField claimAddressField;
    private JTextField claimPortField;
    private JCheckBox useAsPVTServer;
    
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
        
        // バージョン
        ButtonGroup bg2 = new ButtonGroup();
        v34 = GUIFactory.createRadioButton("3.4", null, bg2);
        v40 = GUIFactory.createRadioButton("4.0", null, bg2);
        
        // 01 小児科等
        claim01 = new JCheckBox("デフォルト01を使用");
        
        // JMARI、ホスト名、アドレス、ポート番号
        String[] hostNames = ClientContext.getStringArray("settingDialog.claim.hostNames");
        claimHostCombo = new JComboBox(hostNames);
        jmariField = GUIFactory.createTextField(10, null, null, null);
        jmariField.setToolTipText("医療機関コードの数字部分のみ12桁を入力してください。");
        claimAddressField = GUIFactory.createTextField(10, null, null, null);
        claimPortField = GUIFactory.createTextField(5, null, null, null);
        
        // 受付受信ボタン
        useAsPVTServer = GUIFactory.createCheckBox("このマシンでORCAからの受付情報を受信する", null);
        useAsPVTServer.setToolTipText("このマシンでORCAからの受付情報を受信する場合はチェックしてください");
        
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
        label = new JLabel("バージョン:");
        JPanel vPanel = GUIFactory.createRadioPanel(new JRadioButton[]{v34,v40});
        gbl.add(label,  0, row, GridBagConstraints.EAST);
        gbl.add(vPanel, 1, row, GridBagConstraints.WEST);
        
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
        gbl.add(useAsPVTServer, 0, 0, GridBagConstraints.CENTER);
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
        DocumentListener dl = ProxyDocumentListener.create(stateMgr, "checkState");  
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
        
        String ipPattern = "[A-Za-z0-9.]*";
        RegexConstrainedDocument ipDoc = new RegexConstrainedDocument(ipPattern);
        claimAddressField.setDocument(ipDoc);
        claimAddressField.getDocument().addDocumentListener(dl);
        claimAddressField.addFocusListener(AutoRomanListener.getInstance());
        
        // アクションリスナ
        ActionListener al = ProxyActionListener.create(stateMgr, "controlClaim");
        sendClaimYes.addActionListener(al);
        sendClaimNo.addActionListener(al);
        
        // バージョン制御
        ActionListener al2 = ProxyActionListener.create(stateMgr, "controlVersion");
        v34.addActionListener(al2);
        v40.addActionListener(al2);
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
        
        // バージョン 選択
        String ver = model.getVersion();
        if (ver.startsWith("4")) {
            v40.setSelected(true);
        } else {
            v34.setSelected(true);
        }
        
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
        useAsPVTServer.setSelected(model.isUseAsPVTServer());
        
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
        
        // バージョン
        if (v40.isSelected()) {
            model.setVersion("40");
        } else {
            model.setVersion("34");
        }
        
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
        private boolean claim01;
        
        public void populate(ProjectStub stub) {
            
            // 診療行為送信
            setSendClaim(stub.getSendClaim());
            
            // バージョン
            setVersion(stub.getOrcaVersion());
            
            // JMARI code
            setJmariCode(stub.getJMARICode());
            
            // CLAIM ホストのIPアドレス
            setClaimAddress(stub.getClaimAddress());
            
            // CLAIM ホストのポート番号
            setClaimPort(stub.getClaimPort());
            
            // ホスト名
            setClaimHostName(stub.getClaimHostName());
            
            // 受付受信
            setUseAsPVTServer(stub.getUseAsPVTServer());
            
            // 01 小児科等
            setClaim01(stub.isClaim01());
        }
        
        public void restore(ProjectStub stub) {
            
            // 診療行為送信
            stub.setSendClaim(isSendClaim());
            
            // バージョン
            stub.setOrcaVersion(getVersion());
            //System.out.println(stub.getOrcaVersion());
            
            // JMARI
            stub.setJMARICode(getJmariCode());
            //System.out.println(stub.getJMARICode());
            
            // CLAIM ホストのIPアドレス
            stub.setClaimAddress(getClaimAddress());
            
            // CLAIM ホストのポート番号
            stub.setClaimPort(getClaimPort());
            
            // ホスト名
            stub.setClaimHostName(getClaimHostName());
            
            // 受付受信
            stub.setUseAsPVTServer(isUseAsPVTServer());
            
            // 01 小児科
            stub.setClaim01(isClaim01());
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
        
        public int getClaimPort() {
            return claimPort;
        }
        
        public void setClaimPort(int claimPort) {
            this.claimPort = claimPort;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
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
            
            //claimHostCombo.setEnabled(b);
            claimPortField.setEnabled(b);
            
            this.checkState();
        }
        
        public void controlVersion() {
            
            boolean b = v40.isSelected();
            jmariField.setEnabled(b);
            this.checkState();
        }
        
        private boolean isValid() {
            
            boolean jmariOk = false;
            boolean claimAddrOk = false;
            boolean claimPortOk = false;
            
            if (v40.isSelected()) {
                String code = jmariField.getText().trim();
                if (!code.equals("") && code.length() == 12) {
                    jmariOk = true;
                }
            } else {
                jmariOk = true;
            }
            
            if (sendClaimYes.isSelected()) {
                claimAddrOk = (claimAddressField.getText().trim().equals("")) ? false : true;
                claimPortOk = (claimPortField.getText().trim().equals("")) ? false : true;
            } else {
                claimAddrOk = true;
                claimPortOk = true;
            }
            
            return (jmariOk && claimAddrOk && claimPortOk) ? true : false;
        }
    }
}
