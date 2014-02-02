/*
 * LoginDialog.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
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

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.dao.*;
import open.dolphin.infomodel.*;
import open.dolphin.order.MMLTable;
import open.dolphin.project.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import swingworker.*;

/**
 * ログインダイアログ　クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LoginDialog {
    
	private ProjectStub projectStub;

	private JTextField userIdField;
	private JPasswordField passwdField;
	private JButton settingButton;
	private JButton loginButton;
	private JButton cancelButton;
	private boolean okState;
	private boolean result;
	private JDialog dialog;
	private JProgressBar progressBar;
	private javax.swing.Timer userProfileTimer;
    
	private boolean DEBUG = true;
	private boolean DEMO;
    
	/** Creates new LoginService  */
	public LoginDialog() {
	}
        
	public void start() {
        
		String title = ClientContext.getString("loginDialog.title");
		dialog = new JDialog((Frame)null, title, true);

		Component c = createComponent();
		dialog.getContentPane().add(c, BorderLayout.CENTER);
		dialog.getRootPane().setDefaultButton(loginButton);
		dialog.pack();
		Point loc = DesignFactory.getCenterLoc(dialog.getWidth(), dialog.getHeight());
		dialog.setLocation(loc.x, loc.y);
        
		// Project File を読み込んで ProjectStub class を生成する
		projectStub = ClientContext.loadProject();
        
		// Changed 2004-06-8
		if (projectStub.isValid()) {
			Project.setProjectStub(projectStub);
			userIdField.setText(projectStub.getUserId());
			passwdField.requestFocus();        	
		} else {
			doSettingDialog();
		}
        
		/*if (projectStub != null) {
			Project.setProjectStub(projectStub);
			userIdField.setText(projectStub.getUserId());
			passwdField.requestFocus();
            
		} else {
			// ProjectStub が存在しないため設定ダイアログをオープンする
			doSettingDialog();
		}*/
        
		dialog.show();
	}
    
	public boolean getResult() {
		return result;
	}
    
	private void doSettingDialog() {

		ProjectSettingDialog sd = new ProjectSettingDialog();
		sd.setProject(projectStub);
        
		sd.start();
		projectStub = sd.getValue();

		//if (projectStub != null) {
		if (projectStub.isValid()) {
			Project.setProjectStub(projectStub);
			userIdField.setText(projectStub.getUserId());
			passwdField.requestFocus();
		}
	}
                 
	private void tryLogin() {
                
		if ( projectStub == null ){
			showMessageDialog("Dolphin サーバが設定されていません。\n設定ボタンをクリックし、アドレスとポート番号を設定してください。");
			return;           
		}
        
		// Authentication staffs
		String userId = userIdField.getText().trim().toLowerCase();
		String passwd = new String(passwdField.getPassword());
		String host = projectStub.getHostAddress();
		int port = projectStub.getHostPort();
		String pName = projectStub.getName();
		//projectStub.setStampManager(false);
        
		DEBUG = ( userId.equals("manager") &&  passwd.equals("iruka00") ) ? true : false;
		DEMO = ( userId.equals("demo") &&  passwd.equals("demo") ) ? true : false;
        
		if (DEMO) {
			pName = "debug";
			projectStub.setName(pName);
            
		} else if (DEBUG) {
			pName = "debug";
			projectStub.setName(pName);
			//projectStub.setStampManager(true);
		}
                
		// AuthenticatinDao (Project に依存) を生成する
		AbstractProjectFactory factory = AbstractProjectFactory.getProjectFactory(pName);
		AuthenticationDao authentication = factory.createAuthentication(host, port, userId, passwd);
        
		StringBuffer buf = new StringBuffer();
		buf.append("認証を開始します : ");
		buf.append("ldap://");
		buf.append(host);
		buf.append(":");
		buf.append(String.valueOf(port));
		buf.append("//");
		buf.append(authentication.getBindDN());
		Logger logger = ClientContext.getLogger();
		logger.info(buf.toString());
        
		// Authenticate
		int retCode = authentication.authenticate();
        
		String msg = null;
        
		switch (retCode) {
            
			case -1:
				msg = "サーバに接続できません。";
				logger.warning(msg);
				showMessageDialog(msg);
				break;
                
			case 0:
				msg = "認証できません。";
				logger.warning(msg);
				showMessageDialog(msg);
				break;
                
			case 1:
				msg = "認証されました";
				logger.info(msg);
				
				userId = authentication.getUser();
				passwd = authentication.getPasswd();
				final String dn = authentication.getBindDN();
                
				// ProjectStub へ設定するがファイルに保存はされない
				projectStub.setAuthenticationDN(dn);
				projectStub.setUserId(userId);
				projectStub.setPasswd(passwd);
                
				final UserProfileTask userProfileTask = new UserProfileTask(userId, projectStub);
				//final UserProfileTask userProfileTask = new UserProfileTask(dn, projectStub);
				userProfileTimer = new javax.swing.Timer(200, new ActionListener() {
                    
					public void actionPerformed(ActionEvent e) {
                        
						if (userProfileTask.done()) {
							progressBar.setIndeterminate(false);
							progressBar.setValue(0);
							userProfileTimer.stop();
							result = true;         
							dialog.setVisible(false);
							dialog.dispose();
						}
					}
				});
				progressBar.setIndeterminate(true);
				userProfileTask.go();
				userProfileTimer.start();
				break;
		}
	}
    
	private void showMessageDialog(String msg) {
		JOptionPane.showMessageDialog(null,
									 msg,
									 "Hippocrates: ユーザ認証",
									 JOptionPane.WARNING_MESSAGE);
	}
    
	private void doCancel() {
		result = false;
		dialog.setVisible(false);
		dialog.dispose();
	}       
    
	private Component createComponent() {
                
		JPanel top = new JPanel(new BorderLayout(0,2));
		String imageFile = ClientContext.getString("splash.image");
		System.out.println(imageFile);
		top.add(new JLabel(ClientContext.getImageIcon(imageFile)), BorderLayout.NORTH);
		top.add(createIdPanel(), BorderLayout.SOUTH);
		progressBar = DesignFactory.createProgressBar();
		top.add(progressBar, BorderLayout.CENTER);
        
		JPanel bottom = createButtonPanel();
        
		JPanel panel = new JPanel(new BorderLayout(0,7));       
		panel.add(top, BorderLayout.NORTH);
		panel.add(bottom, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
		return panel;
	}
    
	private JPanel createIdPanel() {
                
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
                
		JPanel panel = new JPanel(new BorderLayout(0, 7));
		//panel.add(msgLabel, BorderLayout.NORTH);
        
		// Content
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        
		// UserID label
		String text = ClientContext.getString("loginDialog.userIdLabel");
		content.add(new JLabel(text));
        
		// Strut
		content.add(Box.createHorizontalStrut(7));
        
		// UserId Field
		userIdField = new JTextField();
		Dimension dim = new Dimension(150,21);
		userIdField.setPreferredSize(dim);
		userIdField.setMaximumSize(dim);
		userIdField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwdField.requestFocus();
			}
		});
		userIdField.getDocument().addDocumentListener(dl);
		content.add(userIdField);
        
		// Glue
		content.add(Box.createHorizontalGlue());
        
		// Passwd lable
		text = ClientContext.getString("loginDialog.passwdLabel");
		content.add(new JLabel(text));
        
		// Strut
		content.add(Box.createHorizontalStrut(7));
        
		// Passwd field
		passwdField = new JPasswordField(8);
		passwdField.setPreferredSize(dim);
		passwdField.setMaximumSize(dim);
		passwdField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userIdField.getText().equals("")) {
					userIdField.requestFocus();
				}
				else if (passwdField.getPassword().length != 0) {
					tryLogin();
				}
			}
		});
		passwdField.getDocument().addDocumentListener(dl);
		content.add(passwdField);
                
		content.setBorder(BorderFactory.createEmptyBorder(3, 12, 10, 11));
        
		panel.add(content, BorderLayout.SOUTH);
        
		StringBuffer buf = new StringBuffer();
		text = ClientContext.getString("dolphin.productName");
		buf.append(text);
		buf.append(" Ver. ");
		buf.append(ClientContext.getVersion());
		panel.setBorder(BorderFactory.createTitledBorder(buf.toString()));
        
		return panel;
	}
    
	private JPanel createButtonPanel() {
         
		// Content
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        
		// Glue
		content.add(Box.createHorizontalGlue());
        
		// Set button
		String text = ClientContext.getString("loginDialog.settingButtonText");
		settingButton = new JButton(text);
		settingButton.addActionListener(new ActionListener() {
            
			public void actionPerformed(ActionEvent e) {
				doSettingDialog();
			}
		});
		//settingButton.setMnemonic('S');
		content.add(settingButton);
        
		// Strut
		content.add(Box.createHorizontalStrut(5));
        
		// Login
		text = ClientContext.getString("loginDialog.loginButtonText");
		loginButton = new JButton(text);
		loginButton.addActionListener(new ActionListener() {
            
			public void actionPerformed(ActionEvent e) {
				tryLogin();
			}
		});
		loginButton.setEnabled(false);
		//loginButton.setMnemonic('L');
		content.add(loginButton);
        
		// Strut
		content.add(Box.createHorizontalStrut(5));
        
		// Cancel
		text =  (String)UIManager.get("OptionPane.cancelButtonText");
		cancelButton = new JButton(text);
		cancelButton.addActionListener(new ActionListener() {
            
			public void actionPerformed(ActionEvent e) {
				doCancel();
			}
		});
		//cancelButton.setMnemonic('C');
		content.add(cancelButton);        
       
		return content;
	}
       
	private void checkButtons() {
		boolean userEmpty = userIdField.getText().equals("") ? true : false;
		boolean passwdEmpty = passwdField.getPassword().length == 0 ? true : false;
        
		boolean newOKState = ( (userEmpty == false) && (passwdEmpty == false) ) ? true : false;
        
		if (newOKState != okState) {
			loginButton.setEnabled(newOKState);
			okState = newOKState;
		}
	}
        
	private void debugString(String s) {
		System.out.println(s);
	}
    
	protected class UserProfileTask {
        
		private ProjectStub stub;
		//private String theDN;
		private String uid;
		private String stateMessage;
		private boolean over;
        
		public UserProfileTask(String uid, ProjectStub stub) {
			//theDN = dn;
			this.uid = uid;
			this.stub = stub;
		}
        
		protected void go() {
			stateMessage = "ローカルサーバーへ接続中. . . . . . .";
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					return new ActualTask();
				}
			};
			worker.start(); 
		}

		protected boolean done() {
			return over;
		}

		protected String getMessage() {
			return stateMessage;
		}

		private class ActualTask {

			 /** Creates new ActualTask */
			ActualTask() {
				fetchUserInfo();
			}        
		}
    
		private void fetchUserInfo() {
            
			UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
			if (dao == null) {
				debug("UserProfileDao == null");
				return;
			}
            
			UserProfileEntry userProfile = new UserProfileEntry();
			userProfile.setUserId(uid);
			dao.fetch(userProfile);
                
			if (dao.getErrorMessage() != null) {
				debug(dao.getErrorMessage());
				return;
			}

			stub.setUserProfileEntry(userProfile);
            
			Logger logger = ClientContext.getLogger();
			logger.info("ユーザプロファイル" + "\n" + userProfile.toString());
            
			// Check canEdit prop
			boolean readOnly = Project.isReadOnly();
			if (readOnly) {
				logger.info("Read Only User");
			} else {
				logger.info("Read/Write User");
			}
            
			// Create creator
			Creator creator = new Creator();
			creator.setId(userProfile.getUserId());
			creator.setName(userProfile.getCommonName());
			creator.setLicense(userProfile.getLicenseCode());
			Organization o = new Organization();
			creator.setOrganization(o);
			o.setId(userProfile.getFacilityId());
			o.setName(userProfile.getFacilityName()); //o.setName(stub.getFacilityName());
			Address address = new Address();
			address.setAddress(userProfile.getPostalAddress()); //address.setAddress(stub.getFacilityAddress());
			address.setZipCode(userProfile.getPostalCode()); //address.setZipCode(stub.getFacilityZipCode());
			o.setAddress(address);
			//Phone
			String phoneNumber = userProfile.getTelephoneNumber();
			if (phoneNumber != null) {
				//StringTokenizer st = new StringTokenizer(phoneNumber, "-");
				//try {
					Phone phone = new Phone();
					phone.setArea(phoneNumber.substring(0,3));
					phone.setCity(phoneNumber.substring(3,6));
					phone.setNumber(phoneNumber.substring(6));
					o.addPhone(phone);
				//} catch (Exception e) {
					//o.setPhone(null);
				//}
			}
			
			String did = userProfile.getDepartmentId();
			OrganizationalUnit ou = new OrganizationalUnit();
			ou.setId(did);
			ou.setName(MMLTable.getDepartmentName(did));
			creator.setOrganizationalUnit(ou);
			
			// Project へ保存
			stub.setCreatorInfo(creator);
			over = true;
		}
	}
    
	private void debug(String msg) {
		if (ClientContext.isDebug()) {
			System.out.println(msg);
		}
	}    
}