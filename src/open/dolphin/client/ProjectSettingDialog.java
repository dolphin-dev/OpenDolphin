/*
 * ServerSetting.java
 *
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 */
package open.dolphin.client;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import open.dolphin.project.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * プロジェクト設定ダイアログ。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ProjectSettingDialog {
    
	// Project
	private ProjectStub projectStub;
    
	private JTabbedPane tabbedPane;
       
	private JButton okButton;
	private JButton cancelButton;
    
	private boolean okState;
	private JDialog dialog;
    
	/** Creates new Login */
	public ProjectSettingDialog() {
		String title = ClientContext.getString("settingDialog.title");
		dialog = new JDialog((Frame)null, title, true);
		JPanel c = createComponent();
		c.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
		dialog.getContentPane().add(c, BorderLayout.CENTER);
		dialog.getRootPane().setDefaultButton(okButton);
		dialog.pack();
	}
    
	public void start() {
		Point loc = DesignFactory.getCenterLoc(dialog.getWidth(), dialog.getHeight());
		dialog.setLocation(loc.x, loc.y);
		dialog.show();
	}
    
	public void setProject(ProjectStub p) {
        
		projectStub = p;
        
		if (projectStub == null) {
			return;
		}
      
		projectStub.setMode(AbstractSettingPanel.TT_SET);
        
		int count = tabbedPane.getTabCount();
		for (int i = 0; i < count; i++) {
			AbstractSettingPanel setting = (AbstractSettingPanel)tabbedPane.getComponentAt(i);
			projectStub.accept(setting);
		}
	}
    
	public ProjectStub getValue() {
		return projectStub;
	}
    
	private JPanel createComponent() {
        
		// Project Panel
		AbstractSettingPanel projectPanel = new ProjectPanel();
		((JPanel)projectPanel).setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
		// Hospital Panel
		//AbstractSettingPanel hospitalPanel = new HospitalPanel();
		//((JPanel)hospitalPanel).setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
		// Dolphin Server (Host) Panel
		AbstractSettingPanel hostPanel = new HostPanel();
		((JPanel)hostPanel).setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
		// Claim Panel
		AbstractSettingPanel claimPanel = new ClaimPanel();
		((JPanel)claimPanel).setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
		// MML Panel
		AbstractSettingPanel mmlPanel = new MMLPanel(); //createMMLPanel();
		((JPanel)mmlPanel).setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
                     
		// Proxy Panel
		AbstractSettingPanel autoUpdatePanel = new AutomaticUpdatePanel();
		((JPanel)autoUpdatePanel).setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
		// Tab 構成
		tabbedPane = new JTabbedPane();
        
		// Project 設定
		String text = ClientContext.getString("settingDialog.tab.projectTab");
		tabbedPane.add(text, projectPanel);
        
		// 病院情報設定
		//text = ClientContext.getResourceString("settingDialog.tab.hospitalTab");
		//tabbedPane.add(text, hospitalPanel);
        
		// Dolphin Server
		text = ClientContext.getString("settingDialog.tab.serverTab");
		tabbedPane.add(text, hostPanel);
               
		// Claim Server
		text = ClientContext.getString("settingDialog.tab.claimTab");
		tabbedPane.add(text, claimPanel);
        
		// Sending MML
		text = ClientContext.getString("settingDialog.tab.mmlTab");
		tabbedPane.add(text, mmlPanel);
        
		// 自動更新 設定
		text = ClientContext.getString("settingDialog.tab.proxyTab");
		tabbedPane.add(text, autoUpdatePanel);
        
		JPanel info = new JPanel(new BorderLayout(0, 9));
		text = ClientContext.getString("settingDialog.instraction");
		info.add(new JLabel(text), BorderLayout.NORTH);
		info.add(new JLabel(ClientContext.getImageIcon("splash.jpg")), BorderLayout.CENTER);
        
		JPanel top = new JPanel();        
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		top.add(info);
		top.add(Box.createRigidArea(new Dimension(11, 0)));
		top.add(tabbedPane);
        
		//JPanel panel = new JPanel(new BorderLayout(0, 17));
		//panel.add(top, BorderLayout.CENTER);
        
		//JPanel p =  createButtonPanel();        
		//panel.add(p, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(top);
		panel.add(Box.createVerticalGlue());
		panel.add(Box.createVerticalStrut(17));

		JPanel p =  createButtonPanel();        
		panel.add(p);        
		return panel;  
	}
    
	private JPanel createButtonPanel() {
	   // Buttons
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    
		p.add(Box.createHorizontalGlue());
        
		// Save
		String text = ClientContext.getString("settingDialog.saveButtonText");
		okButton = new JButton(text);
		okButton.addActionListener(new ActionListener() {
            
			public void actionPerformed(ActionEvent e) {
				doOk();
			}
		});
		okButton.setEnabled(false);
		p.add(okButton);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
        
		// Cancel
		text =  (String)UIManager.get("OptionPane.cancelButtonText") + "(C)";
		cancelButton = new JButton(text);
		cancelButton.addActionListener(new ActionListener() {
            
			public void actionPerformed(ActionEvent e) {
				doCancel();
			}
		});
		cancelButton.setMnemonic('C');
		p.add(cancelButton);        

		return p;
	}
    
	private void checkButtons() {
        
		boolean newOk = true;
		int count = tabbedPane.getTabCount();
		for(int i = 0; i < count; i++) {
			AbstractSettingPanel p = (AbstractSettingPanel)tabbedPane.getComponentAt(i);
			if (!p.isOk()) {
				newOk = false;
				break;
			}
		}
        
		if (okState != newOk) {
			okState = newOk;
			okButton.setEnabled(okState);
		}
	}
            
	private void doOk() {
        
		if (projectStub == null) {
			projectStub  = new ProjectStub();
		}
        
		projectStub.setMode(AbstractSettingPanel.TT_GET);
        
		int count = tabbedPane.getTabCount();
		for (int i = 0; i < count; i++) {
			AbstractSettingPanel p = (AbstractSettingPanel)tabbedPane.getComponentAt(i);
			projectStub.accept(p);
		}
        
		// Save settings
		projectStub.setValid(true);
		ClientContext.storeProject(projectStub);
        
		dialog.setVisible(false);
		dialog.dispose();
	}
    
	private void doCancel() {
		dialog.setVisible(false);
		dialog.dispose();
	}    
    
	/**
	 * Project Information Panel
	 */
	class ProjectPanel extends AbstractSettingPanel {
        
		private JRadioButton kumamoto;
		private JRadioButton miyazaki;
    
		public ProjectPanel() {
            
			String text = ClientContext.getString("settingDialog.project.kumamotoName");
			kumamoto = new JRadioButton(text);
			text = ClientContext.getString("settingDialog.project.miyazakiName");
			miyazaki = new JRadioButton(text);
			this.add (miyazaki);
			this.add (kumamoto);
			ButtonGroup bg = new ButtonGroup();
			bg.add(miyazaki);
			bg.add(kumamoto);
			text = ClientContext.getString("settingDialog.project.borderTitle");
			this.setBorder(BorderFactory.createTitledBorder(text));
            
			// Sets default
			//miyazaki.setSelected(true);
		}
        
		public void getValues(ProjectStub stub) {
			String val = kumamoto.isSelected() ? "kumamoto" : "miyazaki";
			stub.setName(val);
		}
        
		public void setValues(ProjectStub stub) {
			String val = stub.getName();
			if (val != null && val.equals("kumamoto")) {
				kumamoto.setSelected(true);
                
			} else {
				miyazaki.setSelected(true);
			}
		}
	}

    
	/**
	 * Dolphin Server Information Panle
	 */
	class HostPanel extends AbstractSettingPanel {
        
		private JTextField hostAddressField;
		private JTextField hostPortField;
		private JTextField userIdField;
        
		public HostPanel() {
            
			// DocumentListener
			DocumentListener dl = new DocumentListener() {
          
				public void changedUpdate(DocumentEvent e) {
				}

				public void insertUpdate(DocumentEvent e) {
					checkButtons();
				}

				public void removeUpdate(DocumentEvent e) {
					checkButtons();
				}
			};
            
			// FocusAdapter
			FocusAdapter imeOff = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};
			
			// 生成
			hostAddressField = createTextField(10, null, imeOff, dl);
			hostPortField = createTextField(5, null, null, null);
			hostPortField.setEnabled(false);
			userIdField = createTextField(10, null, imeOff, dl);
			
			hostAddressField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					userIdField.requestFocus();
				}
			});
			
			/*hostPortField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					userIdField.requestFocus();
				}
			});*/
			
			userIdField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {  
					hostAddressField.requestFocus();
				}
			});
			 
			// レイアウト  
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
			// IP address
			String text = ClientContext.getString("settingDialog.server.ipLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, hostAddressField));
			this.add(Box.createVerticalStrut(11));

			// Port number
			text = ClientContext.getString("settingDialog.server.portLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, hostPortField));
			this.add(Box.createVerticalStrut(11));

			// User Id
			text = ClientContext.getString("settingDialog.server.userIdLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, userIdField));
			this.add(Box.createVerticalStrut(11));
			
			// Glue
			this.add(Box.createVerticalGlue());
			    
		}
        
		public boolean isOk() {
			boolean hostAddrEmpty = hostAddressField.getText().equals("") ? true : false;
			boolean hostPortEmpty = hostPortField.getText().equals("") ? true : false;
			boolean userIdEmpty = userIdField.getText().equals("") ? true : false;
            
			return (hostAddrEmpty || hostPortEmpty || userIdEmpty) ? false : true;
		}
        
		protected void getValues(ProjectStub stub) {
            
			String val = hostAddressField.getText().trim();
			stub.setHostAddress(val);
            
			val = hostPortField.getText().trim();
			try {
				int port = Integer.parseInt(val);
				stub.setHostPort(port);
                
			} catch (NumberFormatException e) {
				stub.setHostPort(389);
			}
            
			val = userIdField.getText().trim();
			stub.setUserId(val);
		}
        
		protected void setValues(ProjectStub stub) {
            
			String val = stub.getHostAddress();
			if (val != null) {
				hostAddressField.setText(val);
			}
			val = String.valueOf(stub.getHostPort());
			if (val != null) {
				hostPortField.setText(val);
			} //else {
				//hostPortField.setText("389");
			//}

			val = stub.getUserId();
			if (val != null) {
				userIdField.setText(val);
			}
		}
	}
    
	/**
	 * Claim Server Information Panel
	 */
	class ClaimPanel extends AbstractSettingPanel {
        
		private JRadioButton sendClaim;
		private JRadioButton sendNoClaim;
		private JRadioButton sendDiagnosis;
		private JRadioButton sendNoDiagnosis;
		private String claimHostName;
		private JComboBox claimHostCombo;
		private JTextField claimAddressField;
		private JTextField claimPortField;
               
		public ClaimPanel() {
            
			// DocumentListener
			DocumentListener dl = new DocumentListener() {
          
				public void changedUpdate(DocumentEvent e) {
				}

				public void insertUpdate(DocumentEvent e) {
					checkButtons();
				}

				public void removeUpdate(DocumentEvent e) {
					checkButtons();
				}
			};
            
			// FocusAdapter
			FocusAdapter imeOff = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};
			
			// CLAIM送信用ラジオボタンのアクションリスナ
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					controlClaim();
				}
			};
			
			ButtonGroup bg = new ButtonGroup();
			
			// 生成
			sendClaim = createRadioButton("する", al, bg);
			sendNoClaim = createRadioButton("しない", al, bg);
			bg = new ButtonGroup();
			sendDiagnosis = createRadioButton("する", null, bg);
			sendNoDiagnosis = createRadioButton("しない", null, bg);
			String[] hostNames = ClientContext.getStringArray("settingDialog.claim.hostNames"); //new String[]{"日医標準レセコン(ORCA)","富士通 SX"};
			claimHostCombo = new JComboBox(hostNames);
			claimAddressField = createTextField(10, null, imeOff, dl);
			claimPortField = createTextField(5, null, imeOff, dl);
			
			// レイアウト
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
			// CLAIM 送信する / しない
			this.add(createRadioPanel("CLAIM 送信", sendClaim, sendNoClaim));
			this.add(Box.createVerticalStrut(11));
            
			// 病名 送信　する / しない
			this.add(createRadioPanel("病名送信", sendDiagnosis, sendNoDiagnosis));
			this.add(Box.createVerticalStrut(11));

			// レセコン選択
			String text = ClientContext.getString("settingDialog.claim.hostNameLabel");
			this.add(createComboPanel(text, claimHostCombo));
			this.add(Box.createVerticalStrut(11));

			// CLAIM Server ip 
			text = ClientContext.getString("settingDialog.server.ipLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, claimAddressField));
			this.add(Box.createVerticalStrut(11));

			// Claim Port
			text = ClientContext.getString("settingDialog.server.portLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, claimPortField));
			
			this.add(Box.createVerticalGlue());
			            
			// Sets default
			//sendClaim.setSelected(true);
			//sendNoDiagnosis.setSelected(true);   
		}
        
		private void controlClaim() {
			boolean b = sendClaim.isSelected();
			sendDiagnosis.setEnabled(b);
			sendNoDiagnosis.setEnabled(b);
			claimHostCombo.setEnabled(b);
			claimAddressField.setEnabled(b);
			claimPortField.setEnabled(b);
		}
        
		public boolean isOk() {
            
			if (sendClaim.isSelected()) {
				boolean claimAddrEmpty = claimAddressField.getText().equals("") ? true : false;
				boolean claimPortEmpty = claimPortField.getText().equals("") ? true : false;
				return (claimAddrEmpty || claimPortEmpty) ? false : true;
			}
            
			return true;
		}
        
		protected void getValues(ProjectStub stub) {
            
			stub.setSendClaim(sendClaim.isSelected());
			stub.setSendDiagnosis(sendDiagnosis.isSelected());
            
			String val = (String)claimHostCombo.getSelectedItem();
			stub.setClaimHostName(val);
            
			val = claimAddressField.getText().trim();
			stub.setClaimAddress(val);
        
			val = claimPortField.getText().trim();
			try {
				int port = Integer.parseInt(val);
				stub.setClaimPort(port);
                
			} catch (NumberFormatException e) {
				stub.setClaimPort(5001);
			}
		}
        
		protected void setValues(ProjectStub stub) {
        	
			boolean sending = stub.getSendClaim();
			if (sending) {
				sendClaim.doClick();
                
			} else {
				sendNoClaim.doClick();
			}
			
			sendDiagnosis.setSelected(stub.getSendDiagnosis());        	
                    
			String val = stub.getClaimAddress();
			if (val != null) {
				claimAddressField.setText(val);
			}

			val = String.valueOf(stub.getClaimPort());
			if (val != null) {
				claimPortField.setText(val);
			} //else {
				//claimPortField.setText("5001");
			//}
            
			val = stub.getClaimHostName();
			if (val != null) {
				claimHostCombo.setSelectedItem(val);
			}
		}        
	}
    
	/**
	 * MML Version Setting Panel
	 */
	class MMLPanel extends AbstractSettingPanel {
        
		 // HOT センター送信関係
		private JRadioButton sendMML;
		private JRadioButton sendNoMML;
		private JRadioButton mml3;
		private JRadioButton mml23;
        
		// HigoMed 関係 2003-08-14
		//private JRadioButton allPatient;
		//private JRadioButton hasLocalIdPatient;
		private JTextField uploaderServer;
		private JTextField shareDirectory;
        
		public MMLPanel() {
        	
			// 生成
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					controlSendMml();
				}
			};
			
			// DocumentListener
			 DocumentListener dl = new DocumentListener() {
  
				 public void changedUpdate(DocumentEvent e) {
				 }

				 public void insertUpdate(DocumentEvent e) {
					 checkButtons();
				 }

				 public void removeUpdate(DocumentEvent e) {
					 checkButtons();
				 }
			 }; 
			 
			// FocusAdapter
			FocusAdapter imeOff = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};			 
        
			ButtonGroup bg = new ButtonGroup();
			sendMML = createRadioButton("する", al, bg);
			sendNoMML = createRadioButton("しない", al, bg);

			bg = new ButtonGroup();
			mml3 = createRadioButton("3.0", null, bg);
			mml23 = createRadioButton("2.3", null, bg);

			uploaderServer = createTextField(10, null, imeOff, dl);
			shareDirectory = createTextField(10, null, imeOff, dl);
            
			// レイアウト          
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
			this.add(createRadioPanel("センター送信", sendMML,sendNoMML));
			this.add(Box.createVerticalStrut(11));
            
			this.add(createRadioPanel("MML バージョン", mml3,mml23));
			this.add(Box.createVerticalStrut(11));

			this.add(createItemPanel("アップローダアドレス", SwingConstants.RIGHT, uploaderServer));
			this.add(Box.createVerticalStrut(11));
            
			this.add(createItemPanel("共有ディレクトリ", SwingConstants.RIGHT, shareDirectory));
			this.add(Box.createVerticalGlue());
            
			// Sets default
			//sendMML.setSelected(true);
			//mml23.setSelected(true);
		}
        
		private void controlSendMml() {
			boolean b = sendMML.isSelected();
			mml3.setEnabled(b);
			mml23.setEnabled(b);
			uploaderServer.setEnabled(b);
			shareDirectory.setEnabled(b);
		}
        
		protected void setValues(ProjectStub stub) {
            
			boolean sending = stub.getSendMML();
			//sendMML.setSelected(sending);
			if (sending) {
				sendMML.doClick();
			} else {
				sendNoMML.doClick();
			}
                    
			// V3 MML Version and Sending
			String val = stub.getMMLVersion();
			if (val != null && val.startsWith("2")) {
				//mml23.setSelected(true);
				mml23.doClick();
                
			} else {
				// Default = MML 3
				//mml3.setSelected(true);
				mml3.doClick();
			}
            
			// まだ設定されていない時のアップローダと送信ルールの設定
			// はにわとヒゴメド
			if (stub.getUploaderIPAddress() == null && 
				stub.getUploadShareDirectory() == null) {
                    
				// IP = Host , share = public    
				val = stub.getHostAddress();
				if (val != null && ! val.equals("")) {
					uploaderServer.setText(val);
					shareDirectory.setText("public");
				}
                
				// 送信ルールの決定
				/*val = stub.getName();
				if (val != null) {
					if (val.equals("kumamoto")) {
						hasLocalIdPatient.doClick();            // 地域IDを有する患者のみ
					} else if (val.equals("miyazaki")) {
						allPatient.doClick();                   // 全患者
					} else {
						allPatient.doClick();                   // 全患者
					}
				}*/
            
			} else {
            
				/*boolean useLocalId = stub.getUseLocalPatientId();
				if (useLocalId) {
					hasLocalIdPatient.doClick();

				} else {
					allPatient.doClick();
				}*/

				// アップローダアドレス
				val = stub.getUploaderIPAddress();
				if (val != null && ! val.equals("")) {
					uploaderServer.setText(val);
				}

				// 共有ディレクトリ
				val = stub.getUploadShareDirectory();
				if (val != null && ! val.equals("")) {
					shareDirectory.setText(val);
				}
			}
		}
        
		protected void getValues(ProjectStub stub) {
            
			// センター送信
			boolean b = sendMML.isSelected();
			stub.setSendMML(b);
            
			// MML バージョン
			String val = mml3.isSelected() ? "300" : "230";
			stub.setMMLVersion(val);
            
			// 送信ルール
			//b = hasLocalIdPatient.isSelected();
			//stub.setUseLocalPatientId(b);
            
			// アップローダアドレス
			val = uploaderServer.getText().trim();
			if (! val.equals("")) {
				stub.setUploaderIPAddress(val);
			}
            
			// 共有ディレクトリ
			val = shareDirectory.getText().trim();
			if (! val.equals("")) {
				stub.setUploadShareDirectory(val);
			}
		} 
        
		public boolean isOk() {
			if (sendMML.isSelected()) {
				boolean uploadAddrEmpty = uploaderServer.getText().trim().equals("") ? true : false;
				boolean shareEmpty = shareDirectory.getText().trim().equals("") ? true : false;
            
				return (uploadAddrEmpty || shareEmpty) ? false : true;
			} else {
				return true;
			}
		}
	}
    
	/**
	 * Automatic Update Panel
	 */
	class AutomaticUpdatePanel extends AbstractSettingPanel {
        
		private JRadioButton noProxyBtn;
		private JRadioButton useProxyBtn;
		private JTextField proxyServerField;
		private JTextField proxyPortField;
        
		public AutomaticUpdatePanel() {
        	
			// 生成
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					controlProxy();
				}
			};
			
			// DocumentListener
			 DocumentListener dl = new DocumentListener() {
  
				 public void changedUpdate(DocumentEvent e) {
				 }

				 public void insertUpdate(DocumentEvent e) {
					 checkButtons();
				 }

				 public void removeUpdate(DocumentEvent e) {
					 checkButtons();
				 }
			 }; 
			 
			// FocusAdapter
			FocusAdapter imeOff = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};				
    
			ButtonGroup bg = new ButtonGroup();
			String text = ClientContext.getString("settingDialog.proxy.noUseName");
			noProxyBtn = createRadioButton(text, al, bg);
            
			text = ClientContext.getString("settingDialog.proxy.useName");
			useProxyBtn = createRadioButton(text, al, bg);
			
			proxyServerField = createTextField(10, null, imeOff, dl);
			proxyPortField = createTextField(5, null, imeOff, dl);
			
			// レイアウト
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			text = ClientContext.getString("settingDialog.proxy.borderTitle");
			this.add(createRadioPanel(text, noProxyBtn, useProxyBtn));
			this.add(Box.createVerticalStrut(11));
						
			text = ClientContext.getString("settingDialog.proxy.ipLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, proxyServerField));
			this.add(Box.createVerticalStrut(11));
			
			text = ClientContext.getString("settingDialog.proxy.portLabel");
			this.add(createItemPanel(text, SwingConstants.RIGHT, proxyPortField));
			this.add(Box.createVerticalGlue());			

			//noProxyBtn.doClick();
		}
        
		private void controlProxy() {
			boolean b = useProxyBtn.isSelected();
			proxyServerField.setEditable(b);
			proxyPortField.setEditable(b);
			checkButtons();
		}
        
		protected void setValues(ProjectStub stub) {
        	
			if (stub.getUseProxy()) {
				useProxyBtn.doClick();
			} else {
				noProxyBtn.doClick();
			}
             
			String val = stub.getProxyHost();
			val = (val == null || val.equals("")) ? "" : val;
			proxyServerField.setText(val);
            
			int port = stub.getProxyPort();
			if (port != 0) {
				val = String.valueOf(stub.getProxyPort());
				proxyPortField.setText(val);
                
			} //else {
				//proxyPortField.setText("8080");
			//}
		}
        
		protected void getValues(ProjectStub stub) {
            
			boolean use = useProxyBtn.isSelected();
			stub.setUseProxy(use);
            
			if (use) {
				String val = proxyServerField.getText().trim();
				if (! val.equals("")) {
					stub.setProxyHost(val);
				}
				val = proxyPortField.getText().trim();
				if (! val.equals("")) {
					stub.setProxyPort(Integer.parseInt(val));
				}
			}
		}
        
		public boolean isOk() {
        
			boolean use = useProxyBtn.isSelected();
			if (use) {
				if ( !proxyServerField.getText().trim().equals("") && 
					 !proxyPortField.getText().trim().equals("") ) {
						 return true;
				} else {
					return false;
				}
			} else {
            
				return true;
			}
		}
	}
            
	protected final class DepartmentTableModel extends AbstractTableModel {
        
		Object[][] departments;
        
		public DepartmentTableModel() {
            
			String[] depts = ClientContext.getStringArray("settingDialog.department.list");
			int len = depts.length;
			departments = new Object[len][2];
            
			for (int i = 0; i < len; i++) {
				departments[i][0] = new Boolean(false);
				departments[i][1] = depts[i];
			}
		}
            
		public boolean isCellEditable(int row, int col) {
			return col == 0 ? true : false;
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return departments.length;
		}

		public Object getValueAt(int row, int col) {
			return departments[row][col];
		}

		public Class getColumnClass(int col) {
			return col == 0 ? java.lang.Boolean.class : java.lang.String.class;
		}

		public void setValueAt(Object o, int row, int col) {
			if (col != 0 || o == null) {
				return;
			}
			departments[row][col] = o;
			fireTableCellUpdated(row, col);
			checkButtons();
		}
        
		public void setSelected(ArrayList depts) {
            
			if (depts == null) {
				return;
			}
            
			int len = depts.size();
			String val = null;
			for (int i = 0; i < len; i++) {
				val = (String)depts.get(i);
				//System.out.println(val);
                
				for(int j = 0; j < departments.length; j++) {
					if (val.equals((String)departments[j][1])) {
						departments[j][0] = new Boolean(true);
						fireTableCellUpdated(j, 0);
						break;
					}
				}
			}
		}
        
		public boolean hasSelected() {
            
			int len = departments.length;
			boolean ret = false;
            
			for (int i = 0; i < len; i++) {
				boolean b = ((Boolean)departments[i][0]).booleanValue();
				if (b) {
					ret = true;
					break;
				}
			}
			return ret;
		}
        
		public String getSelectedDepartment() {
            
			int len = departments.length;
			StringBuffer buf = new StringBuffer();
            
			for (int i = 0; i < len; i++) {
				boolean b = ((Boolean)departments[i][0]).booleanValue();
				if (b) {
					if (i != 0) {
						buf.append(",");
					}
					buf.append((String)departments[i][1]);
				}
			}
            
			//System.out.println(buf.toString());
            
			return buf.length() > 0 ? buf.toString() : null;
		}
	}
    
	private JRadioButton createRadioButton(String text, ActionListener al, ButtonGroup bg) {
    	
		JRadioButton radio = new JRadioButton(text);
    	
		if (al != null) {
			radio.addActionListener(al);
		}
    	
		if (bg != null) {
			bg.add(radio);
		}
    	
		return radio;
	}
    
	private JTextField createTextField(int val, Insets margin, FocusAdapter fa, DocumentListener dl) {
		
		if (val == 0) {
			val = 30;
		}
		JTextField tf = new JTextField(val);
		
		if (margin != null) {
			margin = new Insets(2,5,2,5);
		}
		tf.setMargin(margin);
		
		if (dl != null) {
			tf.getDocument().addDocumentListener(dl);
		}
		
		if (fa != null) {
			tf.addFocusListener(fa);
		}
		
		return tf;
	}
	
	private JPanel createItemPanel(String label, int align, JTextField tf) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(new JLabel(label, align));
		p.add(Box.createHorizontalStrut(7));
		p.add(tf);
		p.setMaximumSize(p.getPreferredSize());
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(p);
		p2.add(Box.createHorizontalGlue());
		return p2;
	}
	
	private JPanel createRadioPanel(String label, JRadioButton b1, JRadioButton b2) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(new JLabel(label, SwingConstants.RIGHT));
		p.add(Box.createHorizontalStrut(7));
		p.add(b1);
		p.add(Box.createHorizontalStrut(5));
		p.add(b2);
		p.setMaximumSize(p.getPreferredSize());
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(p);
		p2.add(Box.createHorizontalGlue());
		return p2;
	}
	
	private JPanel createComboPanel(String label, JComboBox cmb) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(new JLabel(label, SwingConstants.RIGHT));
		p.add(Box.createHorizontalStrut(7));
		p.add(cmb);
		p.setMaximumSize(p.getPreferredSize());
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(p);
		p2.add(Box.createHorizontalGlue());
		return p2;
	}    
}