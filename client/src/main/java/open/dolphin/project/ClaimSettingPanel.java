package open.dolphin.project;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.RegexConstrainedDocument;
import open.dolphin.helper.GridBagBuilder;

/**
 * ClaimSettingPanel
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ClaimSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "claimSetting";
    private static final String TITLE = "レセコン";
//minagawa^ Icon Server    
    private static final String ICON = "icon_claim_settings_small";
//minagawa$    
    
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
    //Server-ORCA連携^
    private JRadioButton claimConnectionIsClient;
    private JRadioButton claimConnectionIsServer;
    //Server-ORCA連携$
//minagawa^ 受付定期チェック
    private JCheckBox pvtTimerCheck;
//minagawa$    
    
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
        
        // モデルを生成し初期化する
        model = new ClaimModel();
        model.populate(getProjectStub());
        
        // GUIを構築する
        initComponents();
        
        // bind する
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
        
        //Server-ORCA連携^
        bg1 = new ButtonGroup();
        claimConnectionIsClient = GUIFactory.createRadioButton("クライアント", null, bg1);
        claimConnectionIsServer = GUIFactory.createRadioButton("サーバー", null, bg1);
        //Server-ORCA連携$
        
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
        
//minagawa^ 定期的チェック
        pvtTimerCheck = GUIFactory.createCheckBox("受付を定期チェエクする（旧モード）", null);
//minagawa$          
        
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
        
        //Server-ORAC連携^ 
        JPanel receInfo = gbl.getProduct();
      
        gbl = new GridBagBuilder("レセコン連携");
        row = 0;
        label = new JLabel("レセコンとの接続:");
        JPanel tmp = GUIFactory.createRadioPanel(new JRadioButton[]{claimConnectionIsClient,claimConnectionIsServer});
        gbl.add(label, 0, row, GridBagConstraints.EAST);
        gbl.add(tmp,   1, row, GridBagConstraints.CENTER);
        
        row++;
        label = new JLabel("IPアドレス:");
        gbl.add(label,             0, row, GridBagConstraints.EAST);
        gbl.add(claimAddressField, 1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel("ポート番号:");
        gbl.add(label,          0, row, GridBagConstraints.EAST);
        gbl.add(claimPortField, 1, row, GridBagConstraints.WEST);
        //JPanel port = gbl.getProduct();
        
        // レセコンからの受付受信
        //gbl = new GridBagBuilder("受付情報の受信");
        row++;
        gbl.add(useAsPVTServer, 0, row, 2, 1, GridBagConstraints.EAST);
        
        row++;
        label = new JLabel("バインドアドレス(オプション):");
        gbl.add(label,          0, row, GridBagConstraints.EAST);
        gbl.add(bindAddress,    1, row, GridBagConstraints.WEST);
//minagawa^ 定期チェック
//        JPanel pvt = gbl.getProduct();
//        
//        JPanel connInfo = gbl.getProduct();
//        
//        // 全体レイアウト
//        gbl = new GridBagBuilder();
//        gbl.add(sendClaim, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
//        gbl.add(receInfo,  0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
//        gbl.add(connInfo,  0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
//        gbl.add(new JLabel(""), 0, 3, GridBagConstraints.BOTH,  1.0, 1.0);
//        setUI(gbl.getProduct());
        row++;
        gbl.add(pvtTimerCheck, 0, row, 2, 1, GridBagConstraints.EAST);
        
        JPanel connInfo = gbl.getProduct();
        
        // 全体レイアウト
        gbl = new GridBagBuilder();
        gbl.add(sendClaim, 0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(receInfo,  0, 1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(connInfo,  0, 2, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(new JLabel(""), 0, 3, GridBagConstraints.BOTH,  1.0, 1.0);
        setUI(gbl.getProduct());
//minagawa$ 
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

        // JMARI field
        String jmariPattern = "[0-9]*";
        RegexConstrainedDocument jmariDoc = new RegexConstrainedDocument(jmariPattern);
        jmariField.setDocument(jmariDoc);
        jmariField.getDocument().addDocumentListener(dl);
        jmariField.addFocusListener(AutoRomanListener.getInstance());
        
        // CLAIM port
        String portPattern = "[0-9]*";
        RegexConstrainedDocument portDoc = new RegexConstrainedDocument(portPattern);
        claimPortField.setDocument(portDoc);
        claimPortField.getDocument().addDocumentListener(dl);
        claimPortField.addFocusListener(AutoRomanListener.getInstance());
        
        // CLAIM address
        String ipPattern = "[A-Za-z0-9.\\-_]*";
        RegexConstrainedDocument ipDoc = new RegexConstrainedDocument(ipPattern);
        claimAddressField.setDocument(ipDoc);
        claimAddressField.getDocument().addDocumentListener(dl);
        claimAddressField.addFocusListener(AutoRomanListener.getInstance());
        
        // PVTServer bind address
        String ipPattern2 = "[A-Za-z0-9.\\-_]*";
        RegexConstrainedDocument ipDoc2 = new RegexConstrainedDocument(ipPattern2);
        bindAddress.setDocument(ipDoc2);
        bindAddress.getDocument().addDocumentListener(dl);
        bindAddress.addFocusListener(AutoRomanListener.getInstance());
        
        // 診療行為送信のアクションリスナ
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlClaim();
            }
        };
        sendClaimYes.addActionListener(al);
        sendClaimNo.addActionListener(al);
        
//Server-ORCA連携^
        claimConnectionIsServer.setEnabled(true);
        ActionListener al2 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controllConnection();
            }
        };
        claimConnectionIsClient.addActionListener(al2);
        claimConnectionIsServer.addActionListener(al2);
//Server-ORCA連携$
        
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
        
        // 診療行為送信を選択する
        if (model.isSendClaim()) {
            sendClaimYes.doClick();
        } else {
            sendClaimNo.doClick();
        }
        
        // JMARICode
        String jmari = model.getJmariCode();
        jmari = jmari != null ? jmari : "";
        if (!jmari.equals("") && jmari.startsWith("JPN")) {
            jmari = jmari.substring(3);
            jmariField.setText(jmari);
        }
        
//Server-ORCA連携^
        // doClickで状態を制御する
        if (model.isClientConnection()) {
            claimConnectionIsClient.doClick();
        } else {
            claimConnectionIsServer.doClick();
        }
//Server-ORCA連携$        
//minagawa^ 定期チェック
        if (model.isPvtTimerCheck()) {
            pvtTimerCheck.doClick();
        }
//minagawa$  
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
        if (model.isUseAsPVTServer()) {
            useAsPVTServer.doClick();
        }
        
        // バインドアドレス
        String bindAddr = model.getBindAddress();
        bindAddr = bindAddr != null ? bindAddr : "";
        bindAddress.setText(bindAddr);
        //bindAddress.setEnabled(tmp);
        
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
        
        //Server-ORCA連携^
        model.setClientConnection(claimConnectionIsClient.isSelected());
        //Server-ORCA連携$
//minagawa^ 定期チェック
        model.setPvtTimerCheck(pvtTimerCheck.isSelected());
//minagawa$        
        
        // IPアドレスを保存する
        val = claimAddressField.getText().trim();
        model.setClaimAddress(val);
        
        // ポート番号を保存する
        val = claimPortField.getText().trim();
        try {
            int port = Integer.parseInt(val);
            model.setClaimPort(port);
            
        } catch (NumberFormatException e) {
            model.setClaimPort(8210);
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
        //Server-ORCA連携^
        private boolean clientConnection;
        //Server-ORCA連携$
//minagawa^ 受付の定期チェック（旧モード）      
        private boolean pvtTimerCheck;
//minagawa$        
        
        public void populate(ProjectStub stub) {
            
            // 診療行為送信
            setSendClaim(Project.getBoolean(Project.SEND_CLAIM));
            
            // JMARI code
            setJmariCode(Project.getString(Project.JMARI_CODE));
            
//Server-ORCA連携^
            String test = stub.getString(Project.CLAIM_SENDER);
            boolean b = (test!=null && test.equals("client"));
            setClientConnection(b);
//Server-ORCA連携$
            
            // CLAIM ホストのIPアドレス
            setClaimAddress(Project.getString(Project.CLAIM_ADDRESS));
            
            // CLAIM ホストのポート番号
            setClaimPort(Project.getInt(Project.CLAIM_PORT));
            
            // ホスト名
            setClaimHostName(Project.getString(Project.CLAIM_HOST_NAME));
            
            // 受付受信
            setUseAsPVTServer(Project.getBoolean(Project.USE_AS_PVT_SERVER));
            
            // バインドアドレス
            setBindAddress(Project.getString(Project.CLAIM_BIND_ADDRESS));
            
            // 01 小児科等
            setClaim01(Project.getBoolean(Project.CLAIM_01));
            
//minagawa^ 受付の定期チェック（旧モード）              
            setPvtTimerCheck(Project.getBoolean(Project.PVT_TIMER_CHECK));
//minagawa$              
        }
        
        public void restore(ProjectStub stub) {
            
            // 診療行為送信
            Project.setBoolean(Project.SEND_CLAIM, isSendClaim());
            
            // JMARI
            Project.setString(Project.JMARI_CODE, getJmariCode());
            
            //Server-ORCA連携^
            String conn = isClientConnection() ? "client" : "server";
            Project.setString(Project.CLAIM_SENDER, conn);
            //Server-ORCA連携$
            
            // CLAIM ホストのIPアドレス
            Project.setString(Project.CLAIM_ADDRESS, getClaimAddress());
            
            // CLAIM ホストのポート番号
            Project.setInt(Project.CLAIM_PORT, getClaimPort());
            
            // ホスト名
            Project.setString(Project.CLAIM_HOST_NAME, getClaimHostName());
            
            // 受付受信
            Project.setBoolean(Project.USE_AS_PVT_SERVER, isUseAsPVTServer());
            
            // バインドアドレス
            Project.setString(Project.CLAIM_BIND_ADDRESS, getBindAddress());
            
            // 01 小児科
            Project.setBoolean(Project.CLAIM_01, isClaim01());
            
//minagawa^ 受付の定期チェック（旧モード）              
            Project.setBoolean(Project.PVT_TIMER_CHECK, isPvtTimerCheck());
//minagawa$              
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
        
        //Server-ORCA連携^
        public boolean isClientConnection() {
            return clientConnection;
        }
        
        public void setClientConnection(boolean b) {
            clientConnection=b;
        }
        //Server-ORCA連携$
        
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
        
//minagawa^ 定期チェック
        public boolean isPvtTimerCheck() {
            return pvtTimerCheck;
        }
        
        public void setPvtTimerCheck(boolean b) {
            this.pvtTimerCheck = b;
        }
//minagawa$        
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
        
        //Server-ORCA連携^
        public void controllConnection() {
            boolean b = claimConnectionIsClient.isSelected();
            claimAddressField.setEnabled(b);
            claimPortField.setEnabled(b);
            useAsPVTServer.setEnabled(b);
            bindAddress.setEnabled(b);
            this.checkState();
        }
        //Server-ORCA連携$
        
        public void controlBindAddress() {
            boolean b = useAsPVTServer.isSelected();
            bindAddress.setEnabled(b);
            this.checkState();
        }
        
        private boolean isValid() {
            
            //Server-ORCA連携^
            boolean jmariOk = true;
            boolean claimAddrOk = true;
            boolean claimPortOk = true;
            boolean bindAdrOk = true;
            
            // JMARI len==12
            String code = jmariField.getText().trim();
            jmariOk = jmariOk && (!code.equals("") && code.length()==12);
            
            if (claimConnectionIsClient.isSelected()) {
                claimAddrOk = claimAddrOk && isIPAddress(claimAddressField.getText().trim());
                claimPortOk = claimPortOk && isPort(claimPortField.getText().trim());
            }
            //Server-ORCA連携$
            
            return (jmariOk && claimAddrOk && claimPortOk && bindAdrOk);
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
