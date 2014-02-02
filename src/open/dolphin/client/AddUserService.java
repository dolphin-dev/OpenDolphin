/*
 * AddUserService.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
import open.dolphin.plugin.*;
import open.dolphin.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputSubset;
import java.util.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AddUserService extends AbstractFramePlugin {
	
	//private static Font f = new Font("Dialig", Font.PLAIN, 12);
	//private static FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
    
	private int DEFAULT_WIDTH           = 460;
	private int DEFAULT_HEIGHT          = 280;
	//private String DEFAULT_FACILITY_ID  = "JPN433010100001";
    
	/** Creates a new instance of AddUserService */
	public AddUserService() {
	}
    
	public void initComponent() {
        
		AddUserPanel ap = new AddUserPanel();
		FacilityInfoPanel fp = new FacilityInfoPanel();
		ModifyUserPanel mp = new ModifyUserPanel();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("施設情報", fp);
		tabbedPane.addTab("ユーザ登録", ap);
		tabbedPane.addTab("ユーザ削除", mp);
        
		JPanel p = new JPanel(new BorderLayout());
		p.add(tabbedPane);
       
		centerFrame(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT), p);
        
		fp.get();
	}
    
	protected class FacilityInfoPanel extends JPanel {
	
		private JTextField facilityName;
		private JTextField facilityId;
		private JTextField facilityOid;
		private JTextField zipField1;
		private JTextField zipField2;
		private JTextField addressField;
		private JTextField areaField;
		private JTextField cityField;
		private JTextField numberField;
		private JTextField faxAreaField;
		private JTextField faxCityField;
		private JTextField faxNumberField;
		private JButton updateBtn;
		private JButton clearBtn;
		private JButton closeBtn;
		private boolean hasInitialized;
		
		public FacilityInfoPanel() {
			
			// 生成
			FocusAdapter imeOn = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
					//getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};
			
			FocusAdapter imeOff = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					//tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
					tf.getInputContext().setCharacterSubsets(null);
				}
				
				public void focusLosted(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(null);
				}
			};
			
			DocumentListener dl = new DocumentListener() {

				public void changedUpdate(DocumentEvent e) {
				}

				public void insertUpdate(DocumentEvent e) {
					checkButton();
				}

				public void removeUpdate(DocumentEvent e) {
					checkButton();
				}
			};			
			
			facilityName = createTextField(30, null, imeOn, dl);
			facilityId = createTextField(30, null, imeOff, dl);
			facilityOid = createTextField(30, null, imeOff, dl);
			zipField1 = createTextField(3, null, imeOff, dl);
			zipField2 = createTextField(3, null, imeOff, dl);		
			addressField = createTextField(30, null, imeOn, dl);
			areaField = createTextField(3, null, imeOff, dl);
			cityField = createTextField(3, null, imeOff, dl);			
			numberField = createTextField(3, null, imeOff, dl);
			faxAreaField = createTextField(3, null, imeOff, dl);
			faxCityField = createTextField(3, null, imeOff, dl);
			faxNumberField = createTextField(3, null, imeOff, dl);
			
			updateBtn = new JButton("更新(U)");
			updateBtn.setEnabled(false);
			updateBtn.setMnemonic('U');
			updateBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
			
			clearBtn = new JButton("クリア(E)");
			clearBtn.setEnabled(false);
			clearBtn.setMnemonic('E');
			clearBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			});			
			
			closeBtn = new JButton("閉じる(C)");
			closeBtn.setMnemonic('C');
			closeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});
							
			
			// レイアウト
			JPanel content = new JPanel();			
			content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
			
			// 施設名
			content.add(createItemPanel("　　医療機関名:", facilityName));
			
			// 医療機関コード
			content.add(createItemPanel("医療機関コード:", facilityId));	

			// 医療機関 OID
			content.add(createItemPanel("　医療機関 OID:", facilityOid));	
			
			// 郵便番号
			content.add(createZipPanel("郵便番号:", zipField1, zipField2));
			
			// 住所
			content.add(createItemPanel("住  所:", addressField));
			
			// 電話番号
			content.add(createPhonePanel("電話番号:", areaField, cityField, numberField));
			
			// FAX番号
			content.add(createPhonePanel("FAX 番号:", faxAreaField, faxCityField, faxNumberField));
			
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			btnPanel.add(updateBtn);
			btnPanel.add(clearBtn);
			btnPanel.add(closeBtn);
			
			this.setLayout(new BorderLayout(0, 11));
			this.add(content, BorderLayout.CENTER);
			this.add(btnPanel, BorderLayout.SOUTH);
			this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));													
		}
		
		public void get() {
			UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
			UserProfileEntry profile = dao.getFacilityInfo("uid=DolphinFacilityInfo,ou=DolphinUsers,o=Dolphin");
			
			if (profile == null) {
				hasInitialized = true;
				return;	
			}
			
			if (profile.getCommonName() != null) {
				facilityName.setText(profile.getCommonName());
			}
			
			if (profile.getFacilityId() != null) {
				facilityId.setText(profile.getFacilityId());
			}
			
			if (profile.getFacilityOid() != null) {
				facilityOid.setText(profile.getFacilityOid());
			}
			
			if (profile.getPostalCode() != null) {
				String val = profile.getPostalCode();
				zipField1.setText(val.substring(0, 3));
				zipField2.setText(val.substring(3));
			}
			
			if (profile.getPostalAddress() != null) {
				addressField.setText(profile.getPostalAddress());
			}			
			
			if (profile.getTelephoneNumber() != null) {
				String val = profile.getTelephoneNumber();
				areaField.setText(val.substring(0, 3));
				cityField.setText(val.substring(3, 6));
				numberField.setText(val.substring(6));				
			}
			
			if (profile.getFacsimileTelephoneNumber() != null) {
				String val = profile.getFacsimileTelephoneNumber();
				faxAreaField.setText(val.substring(0, 3));
				faxCityField.setText(val.substring(3, 6));
				faxNumberField.setText(val.substring(6));				
			}															
			
			hasInitialized = true;
		}
		
		private void checkButton() {
			
			if (! hasInitialized) {
				return;
			}
			
			boolean nameEmpty = facilityName.getText().trim().equals("") ? true : false;
			boolean fidEmpty = facilityId.getText().trim().equals("") ? true : false;
			boolean oidEmpty = facilityOid.getText().trim().equals("") ? true : false;
			boolean zip1Empty = zipField1.getText().trim().equals("") ? true : false;
			boolean zip2Empty = zipField2.getText().trim().equals("") ? true : false;
			boolean addressEmpty = addressField.getText().trim().equals("") ? true : false;
			boolean areaEmpty = areaField.getText().trim().equals("") ? true : false;
			boolean cityEmpty = cityField.getText().trim().equals("") ? true : false;
			boolean numberEmpty = numberField.getText().trim().equals("") ? true : false;
			boolean faxAreaEmpty = faxAreaField.getText().trim().equals("") ? true : false;
			boolean faxCityEmpty = faxCityField.getText().trim().equals("") ? true : false;
			boolean faxNumberEmpty = faxNumberField.getText().trim().equals("") ? true : false;
			
			if (nameEmpty && fidEmpty && oidEmpty && zip1Empty &&
			zip2Empty && addressEmpty && areaEmpty &&
			cityEmpty && numberEmpty && faxAreaEmpty && faxCityEmpty && faxNumberEmpty) {
				
				if (clearBtn.isEnabled()) {
					clearBtn.setEnabled(false);
				}
			} else {
				if (! clearBtn.isEnabled()) {
					clearBtn.setEnabled(true);
				}
			}
			
			// 施設名フィールドが空の場合
			if (nameEmpty) {
				if (updateBtn.isEnabled()) {
					updateBtn.setEnabled(false);
				}
				return;
			}
			
			// 施設名フィールドは空ではない
			if (! updateBtn.isEnabled()) {
				updateBtn.setEnabled(true);
			}
		}
		
		private void clear() {
			facilityName.setText("");
			facilityId.setText("");
			facilityOid.setText("");
			zipField1.setText("");
			zipField2.setText("");
			addressField.setText("");
			areaField.setText("");
			cityField.setText("");		
			numberField.setText("");
			faxAreaField.setText("");
			faxCityField.setText("");
			faxNumberField.setText("");
		}
		
		private void update() {
			
			UserProfileEntry profile = new UserProfileEntry();
			profile.setUserId("DolphinFacilityInfo");
			
			String val = facilityName.getText().trim();
			if (! val.equals("")) {
				profile.setCommonName(val);
				profile.setSirName(val);
			}
			
			val = facilityId.getText().trim();
			if (! val.equals("")) {
				profile.setFacilityId(val);
			}
			
			val = facilityOid.getText().trim();
			if (! val.equals("")) {
				profile.setFacilityOid(val);
			}	
			
			val = zipField1.getText().trim();
			String val2 = zipField2.getText().trim();
			if ( (! val.equals("")) && (! val2.equals(""))) {
				profile.setPostalCode(val + val2);
			}
			
			val = addressField.getText().trim();
			if (! val.equals("")) {
				profile.setPostalAddress(val);
			}
			
			val = areaField.getText().trim();
			val2 = cityField.getText().trim();
			String val3 = numberField.getText().trim();
			if ( (! val.equals("")) && (! val2.equals("")) && (! val3.equals("")) ) {
				profile.setTelephoneNumber(val + val2 + val3);
			}
			
			val = faxAreaField.getText().trim();
			val2 = faxCityField.getText().trim();
			val3 = faxNumberField.getText().trim();
			if ( (! val.equals("")) && (! val2.equals("")) && (! val3.equals("")) ) {
				profile.setFacsimileTelephoneNumber(val + val2 + val3);
			}	
			
			UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
			dao.updateFacilityInfo(profile);													
			
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
		
		private JPanel createItemPanel(String label, JTextField tf) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(new JLabel(label));
			p.add(tf);
			return p;
		}
		
		private JPanel createZipPanel(String label, JTextField tf1, JTextField tf2) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(new JLabel(label));
			p.add(tf1);
			p.add(new JLabel("-"));
			p.add(tf2);
			return p;
		}
		
		private JPanel createPhonePanel(String label, JTextField tf1, JTextField tf2, JTextField tf3) {
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(new JLabel(label));
			p.add(tf1);
			p.add(new JLabel("-"));
			p.add(tf2);
			p.add(new JLabel("-"));
			p.add(tf3);
			return p;
		}						
	}
    
	protected class ModifyUserPanel extends JPanel {
        
		private ObjectTableModel tableModel;
		private JTable table;
		private JButton getButton;
		private JButton deleteButton;
		private JButton cancelButton;
        
		public ModifyUserPanel() {
            
			String[] columns = new String[]{
              
				"ユーザID", "姓", "名", "医療資格", "診療科"
			};
               
			// ユーザテーブル
			tableModel = new ObjectTableModel(columns, 7) {
            
				// 編集不可
				public boolean isCellEditable(int row, int col) {
					return false;
				}
            
				// オブジェクトをテーブルに表示する
				public Object getValueAt(int row, int col) {
                
					UserProfileEntry entry = (UserProfileEntry)getObject(row);
					if (entry == null) {
						return null;
					}
                
					String ret = null;
					String[] code = null;
					String[] str = null;
                
					switch (col) {
                    
						case 0:
							ret = entry.getUserId();
							break;
                        
						case 1:
							ret = entry.getSirName();
							break;
                        
						case 2:
							ret = entry.getGivenName();
							break;
                        
						case 3:
							code = ClientContext.getStringArray("settingDialog.license.code");
							int index = getIndex(code, entry.getLicenseCode());
							str = ClientContext.getStringArray("settingDialog.license.list");
							ret = str[index];
							break;
                        
						case 4:
							code = ClientContext.getStringArray("settingDialog.department.code");
							index = getIndex(code, entry.getDepartmentId());
							str = ClientContext.getStringArray("settingDialog.department.list");
							ret = str[index];
							break;
					}
					return ret;
				}
			};
            
			table = new JTable(tableModel);
			// Selection を設定する
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionAllowed(true);

			ListSelectionModel m = table.getSelectionModel();
			m.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false) {
						// 削除ボタンをコントロールする
						//controlDeleteButton();
						int index = table.getSelectedRow();
						UserProfileEntry entry = (UserProfileEntry)tableModel.getObject(index);
						if ( ! isLasManager(entry) ) {
							deleteButton.setEnabled(true);
						} else {
							deleteButton.setEnabled(false);
						}
					}
				}
			});
        
			// Layout
			JScrollPane scroller = new JScrollPane(table, 
									   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            
			getButton = new JButton("ユーザリスト(L)");
			getButton.setEnabled(true);
			getButton.setMnemonic('L');
			getButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getUsers();
				}
			});
            
			deleteButton = new JButton("削除(D)");
			deleteButton.setEnabled(false);
			deleteButton.setMnemonic('D');
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteUser();
				}
			});
                         
			cancelButton = new JButton("閉じる(C)");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});
			cancelButton.setMnemonic('C');
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(Box.createHorizontalGlue());
			p.add(getButton);
			p.add(Box.createHorizontalStrut(5));
			p.add(deleteButton);
			p.add(Box.createHorizontalStrut(5));
			p.add(cancelButton);
            
			this.setLayout(new BorderLayout(0, 17));
			this.add(scroller, BorderLayout.CENTER);
			this.add(p, BorderLayout.SOUTH);
			this.setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
		}
        
		private void getUsers() {
			UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
			ArrayList results = dao.getUsers();
			//if (results != null) {
				tableModel.setObjectList(results);
			//}
		}
        
		private boolean isLasManager(UserProfileEntry entry) {
			return (entry != null && entry.getUserId().equals("lasmanager")) ? true : false;
		}
        
		private void deleteUser() {
			int row = table.getSelectedRow();
			UserProfileEntry entry = (UserProfileEntry)tableModel.getObject(row);
			if ( ! isLasManager(entry)) {
				System.out.println(entry.getUserId());
				UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
				dao.deleteUser(entry.getUserId());
				ArrayList results = dao.getUsers();
				//if (results != null) {
					tableModel.setObjectList(results);
				//}
			}
		}
        
		private int getIndex(String[] code, String val) {
        
			int index = 0;

			for (int i = 0; i < code.length; i++) {
				if (code[i].equals(val)) {
					index = i;
					break;
				}
			}
			return index;
		}
	}
    
       
	protected class AddUserPanel extends JPanel {
        
		private JTextField uid;                 // 利用者ID
		private JPasswordField userPassword;    // パスワード
		private JPasswordField userPassword2;   // パスワード
		private JTextField sn;                  // 姓
		private JTextField givenName;           // 名
		private String cn;                      // 氏名(sn & ' ' & givenName),
		private JTextField licenceCode;         // 職種(MML0026)
		//private JTextField facilityId;          // 医療機関コード(ORCA医療機関コード)
		private JComboBox licenseCombo;
		private JComboBox departmentCombo;      // 診療科(MML0028)
		private String authority;               // LASに対する権限(admin:管理者,user:一般利用者)
		//JTextField mail;                      // メールアドレス
		//JTextField description;
		private JButton okButton;
		private JButton cancelButton;
		private boolean ok;
        
		public AddUserPanel() {
        
			//JPanel panel = new JPanel();
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			// DocumentListener
			DocumentListener dl = new DocumentListener() {

				public void changedUpdate(DocumentEvent e) {
				}

				public void insertUpdate(DocumentEvent e) {
					checkButton();
				}

				public void removeUpdate(DocumentEvent e) {
					checkButton();
				}
			};

			Dimension dim = new Dimension(180, 21);

			uid = new JTextField(10);
			uid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isValidUserId()) {
						userPassword.requestFocus();
					}
				}
			});
			this.add(createItemPanel("ユーザ　ID (半角英数 3~10文字):", uid, dl, dim));
			this.add(Box.createVerticalStrut(5));

			userPassword = new JPasswordField(10);
			userPassword.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isValidPassword2()) {
						userPassword2.requestFocus();
					}
				}
			});
			this.add(createItemPanel("パスワード (半角英数記 4~10文字):", userPassword, dl, dim));
			this.add(Box.createVerticalStrut(5));

			userPassword2 = new JPasswordField(10);
			userPassword2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isValidPassword2()) {
						sn.requestFocus();
					} else {
						userPassword.requestFocus();
					}
				}
			});
			this.add(createItemPanel("　　　パスワード確認:", userPassword2, dl, dim));
			this.add(Box.createVerticalStrut(5));

			sn = new JTextField(10);
			sn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					givenName.requestFocus();
				}
			});

			givenName = new JTextField(10);
			givenName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//facilityId.requestFocus();
					uid.requestFocus();
				}
			});
            
			this.add(createNamePanel(sn, givenName, dl, dim));
			this.add(Box.createVerticalStrut(5));
            
			/*facilityId = new JTextField(30);
			givenName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					uid.requestFocus();
				}
			});
			this.add(createItemPanel("医療機関コード:", facilityId, dl, dim));
			this.add(Box.createVerticalStrut(5));*/

			String[] str = ClientContext.getStringArray("settingDialog.license.list");
			licenseCombo = new JComboBox(str);
            
			str = ClientContext.getStringArray("settingDialog.department.list");
			departmentCombo = new JComboBox(str);
            
			this.add(createCodePanel(licenseCombo, departmentCombo, dim));
			this.add(Box.createVerticalStrut(17));

			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addUserEntry();
				}
			};

			okButton = new JButton("追加(A)");
			okButton.addActionListener(al);
			okButton.setMnemonic('A');
			okButton.setEnabled(false);
			cancelButton = new JButton("閉じる(C)");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});
			cancelButton.setMnemonic('C');
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			p.add(Box.createHorizontalGlue());
			p.add(okButton);
			p.add(Box.createHorizontalStrut(5));
			p.add(cancelButton);
			this.add(p);

			this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
		}
        
		private boolean passwordOk() {
			String passwd = new String(userPassword.getPassword());
			String passwd2 = new String(userPassword2.getPassword());
			return ( (! passwd.equals("")) && (! passwd2.equals("")) && passwd.equals(passwd2)) ? true : false;
		}

		private void addUserEntry() {
            
			if (! isValidUserId()) {
				return;
			}
            
			if (! isValidPassword2()) {
				return;
			}

			UserProfileEntry user = new UserProfileEntry();
			user.setUserId(uid.getText().trim());
			user.setPasswd(new String(userPassword.getPassword()));
			String snSt = sn.getText().trim();
			user.setSirName(snSt);
			String givenNameSt = givenName.getText().trim();
			user.setGivenName(givenNameSt);
			user.setCommonName(snSt + " " + givenNameSt);
            
			/*String val = facilityId.getText().trim();
			if (val.equals("")) {
				val = ClientContext.getResourceString("addUser.defaultFacilityId");
			}
			user.setFacilityId(val);*/

			String[] dic = ClientContext.getStringArray("settingDialog.license.code");
			int index = licenseCombo.getSelectedIndex();
			user.setLicenseCode(dic[index]);

			dic = ClientContext.getStringArray("settingDialog.department.code");
			index = departmentCombo.getSelectedIndex();
			user.setDepartmentId(dic[index]);

			user.setAuthority("user");

			//ClientContext.getLogger().info(user.toString());

			UserProfileDao dao = (UserProfileDao)DaoFactory.create(this, "dao.userProfile");
            
			if (dao != null) {

				boolean ret = dao.addUser(user);

				if (ret) {
                    
					showWarning(user.getUserId() + " を登録しました。");
					okButton.setEnabled(false);
                    
				} else {
					showWarning(dao.getErrorMessage());
					//ClientContext.getLogger().warning(dao.getErrorMessage());
				}
			}
		}
        
		private boolean isValidUserId() {
         
			String userId = uid.getText().trim();
			int len = userId.length();
			boolean ok = (len >= 4 && len <= 10) ? true : false;
            
			if (! ok) {
				showWarning("ユーザIDは半角の英数字で3文字以上10文字以内にしてください。");
			}
            
			return ok;
		}
        
		private boolean isValidPassword1() {
            
			String passwd = new String(userPassword.getPassword());
			int len = passwd.length();
			boolean ok = (len >= 4 && len <= 10) ? true : false;
            
			if (! ok) {
				showWarning("パスワードは半角の英数記号で4文字以上10文字以内にしてください。");
			}
            
			return ok;
		} 
        
		private boolean isValidPassword2() {
            
			String passwd2 = new String(userPassword2.getPassword());
			String passwd = new String(userPassword.getPassword());
			int len = passwd2.length();
			boolean ok = (len >= 4 && len <= 10) ? true : false;
            
			if (! ok) {
				showWarning("パスワードは半角の英数記号で4文字以上10文字以内にしてください。");
			} else if (! passwd2.equals(passwd)) {
				showWarning("パスワードが一致していません。");
				ok = false;
			} else {
				ok = true;
			}
            
			return ok;
		}         
        
		private void checkButton() {

			String passwd = new String(userPassword.getPassword());
			String passwd2 = new String(userPassword2.getPassword());
			boolean userEmpty = uid.getText().trim().equals("");
			boolean userPasswordEmpty = passwd.equals("");
			boolean userPassword2Empty = passwd2.equals("");
			boolean snEmpty = sn.getText().trim().equals("");
			boolean givenNameEmpty = givenName.getText().trim().equals("");

			boolean hasEmpty = (userEmpty || userPasswordEmpty || userPassword2Empty || snEmpty || givenNameEmpty) ? true : false;

			boolean newOk = (! hasEmpty) ? true : false;

			if (ok != newOk) {
				ok = newOk;
				okButton.setEnabled(ok);
			}   
		}        
	}
            
	private JPanel createItemPanel(String itemName, JTextField tf, DocumentListener dl, Dimension dim) {
        
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		p.add(new JLabel(itemName));
		p.add(tf);
		tf.getDocument().addDocumentListener(dl);
		//tf.setPreferredSize(dim);
		return p;
	}
    
	private JPanel createItemPanel(String itemName, JComboBox cb, Dimension dim) {
        
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		p.add(new JLabel(itemName));
		p.add(cb);
		//cb.setPreferredSize(dim);
		return p;
	}
    
	private JPanel createNamePanel(JTextField cn, JTextField gn, DocumentListener dl, Dimension dim) {
        
		//cn.setPreferredSize(dim);
		//gn.setPreferredSize(dim);
		cn.getDocument().addDocumentListener(dl);
		gn.getDocument().addDocumentListener(dl);
        
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 0));
		p.add(new JLabel("姓:"));
		p.add(cn);
		p.add(new JLabel("名:"));
		p.add(gn);
        
		return p;
	}
    
	private JPanel createCodePanel(JComboBox license, JComboBox dept, Dimension dim) {
		//license.setPreferredSize(dim);
		//dept.setPreferredSize(dim);    
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 0));
		p.add(new JLabel("医療資格:"));
		p.add(license);
		p.add(new JLabel("診療科:"));
		p.add(dept);
        
		return p;
	}
    
	private void showWarning(String msg) {
        
		JOptionPane.showMessageDialog(null,
									 msg,
									 "Hippocrates: ユーザ登録",
									 JOptionPane.WARNING_MESSAGE);
	}
}

