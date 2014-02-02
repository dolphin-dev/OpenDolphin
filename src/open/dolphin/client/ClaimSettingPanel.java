/*
 * ClaimSettingPanel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import open.dolphin.project.ProjectStub;

/**
 * ClaimSettingPanel
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class ClaimSettingPanel extends AbstractSettingPanel {
    
    // GUI staff
    private JRadioButton sendClaimYes;
    private JRadioButton sendClaimNo;
    private JComboBox claimHostCombo;
    private JTextField claimAddressField;
    private JTextField claimPortField;
    private JCheckBox useAsPVTServer;
    
    /** 画面モデル */
    private ClaimModel model;
    
    private StateMgr stateMgr;
    
    
    public ClaimSettingPanel() {
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
        
        //
        // 診療行為送信ボタン
        //
        ButtonGroup bg1 = new ButtonGroup();
        sendClaimYes = GUIFactory.createRadioButton("送信する", null, bg1);
        sendClaimNo = GUIFactory.createRadioButton("送信しない", null, bg1);
        
        //
        // ホスト名、アドレス、ポート番号
        //
        String[] hostNames = ClientContext.getStringArray("settingDialog.claim.hostNames");
        claimHostCombo = new JComboBox(hostNames);
        claimAddressField = GUIFactory.createTextField(10, null, null, null);
        claimPortField = GUIFactory.createTextField(5, null, null, null);
        
        //
        // 受付受信ボタン
        //
        useAsPVTServer = GUIFactory.createCheckBox("このマシンでORCAからの受付情報を受信する", null);
        useAsPVTServer.setToolTipText("このマシンでORCAからの受付情報を受信する場合はチェックしてください");
        
        //
        // CLAIM（請求）送信情報
        //
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
        claimAddressField.getDocument().addDocumentListener(dl);
        claimPortField.getDocument().addDocumentListener(dl);
        
        //
        // IME OFF FocusAdapter
        //
        claimAddressField.addFocusListener(AutoRomanListener.getInstance());
        claimPortField.addFocusListener(AutoRomanListener.getInstance());
        
        // アクションリスナ
        ActionListener al = ProxyActionListener.create(stateMgr, "controlClaim");
        sendClaimYes.addActionListener(al);
        sendClaimNo.addActionListener(al);
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
        
        //
        // CLAIM ホストのIPアドレスを設定する
        //
        String val = model.getClaimAddress();
        val = val != null ? val : "";
        claimAddressField.setText(val);
        
        //
        // CLAIM ホストのポート番号を設定する
        //
        val = String.valueOf(model.getClaimPort());
        val = val != null ? val : "";
        claimPortField.setText(val);
        
        //
        // ホスト名
        //
        val = model.getClaimHostName();
        val = val != null ? val : "";
        claimHostCombo.setSelectedItem(val);
        
        //
        // 受付受信
        //
        useAsPVTServer.setSelected(model.isUseAsPVTServer());
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
        
        //
        // ホスト名を保存する
        //
        String val = (String)claimHostCombo.getSelectedItem();
        model.setClaimHostName(val);
        
        //
        // IPアドレスを保存する
        //
        val = claimAddressField.getText().trim();
        model.setClaimAddress(val);
        
        //
        // ポート番号を保存する
        //
        val = claimPortField.getText().trim();
        try {
            int port = Integer.parseInt(val);
            model.setClaimPort(port);
            
        } catch (NumberFormatException e) {
            model.setClaimPort(5001);
        }
        
        //
        // 受付受信を保存する
        //
        model.setUseAsPVTServer(useAsPVTServer.isSelected());
    }
    
    /**
     * 画面も出るクラス。
     */
    class ClaimModel {
        
        private boolean sendClaim;
        private String claimHostName;
        private String claimAddress;
        private int claimPort;
        private boolean useAsPvtServer;
        
        public void populate(ProjectStub stub) {
            
            // 診療行為送信
            setSendClaim(stub.getSendClaim());
            
            // CLAIM ホストのIPアドレス
            setClaimAddress(stub.getClaimAddress());
            
            // CLAIM ホストのポート番号
            setClaimPort(stub.getClaimPort());
            
            // ホスト名
            setClaimHostName(stub.getClaimHostName());
            
            // 受付受信
            setUseAsPVTServer(stub.getUseAsPVTServer());
        }
        
        public void restore(ProjectStub stub) {
            
            // 診療行為送信
            stub.setSendClaim(isSendClaim());
            
            // CLAIM ホストのIPアドレス
            stub.setClaimAddress(getClaimAddress());
            
            // CLAIM ホストのポート番号
            stub.setClaimPort(getClaimPort());
            
            // ホスト名
            stub.setClaimHostName(getClaimHostName());
            
            // 受付受信
            stub.setUseAsPVTServer(isUseAsPVTServer());
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
            
            claimHostCombo.setEnabled(b);
            claimPortField.setEnabled(b);
            
            this.checkState();
        }
        
        private boolean isValid() {
            
            //
            // 診療行為の送信を行う場合はアドレスとポートの値が必要である
            //
            if (sendClaimYes.isSelected()) {
                boolean claimAddrOk = (claimAddressField.getText().trim().equals("") == false) ? true : false;
                boolean claimPortOk = (claimPortField.getText().trim().equals("") == false) ? true : false;
                return (claimAddrOk && claimPortOk) ? true : false;
            }
            
            return true;
        }
    }
}
