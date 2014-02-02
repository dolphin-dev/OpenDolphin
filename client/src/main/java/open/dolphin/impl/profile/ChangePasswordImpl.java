package open.dolphin.impl.profile;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractMainTool;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ChangeProfile;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.RegexConstrainedDocument;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import open.dolphin.util.HashUtil;
import org.apache.log4j.Logger;

/**
 * ChangePasswordPlugin
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ChangePasswordImpl extends AbstractMainTool implements ChangeProfile {
    
    private static final String TITLE = "プロフィール変更";
    //private static int DEFAULT_WIDTH = 568;
    //private static int DEFAULT_HEIGHT = 300;
    private static final String PROGRESS_NOTE = "ユーザ情報を変更しています...";
    private static final String UPDATE_BTN_TEXT = "変更";
    private static final String CLOSE_BTN_TEXT = "閉じる";
    private static final String USER_ID_TEXT = "ユーザID:";
    private static final String PASSWORD_TEXT = "パスワード:";
    private static final String ORCA_ID_TEXT = "ORCA ID:";
    private static final String CONFIRM_TEXT = "確認:";
    private static final String SIR_NAME_TEXT = "姓:";
    private static final String GIVEN_NAME_TEXT = "名:";
    private static final String EMAIL_TEXT = "電子メール:";
    private static final String LISENCE_TEXT = "医療資格:";
    private static final String DEPT_TEXT = "診療科:";
//minagawa^ LSC Test    
    private static final String PASSWORD_ASSIST_1 = "パスワード(半角英数字と記号 _+-.#$&@ で";
    private static final String PASSWORD_ASSIST_2 = "文字以上";
    private static final String PASSWORD_ASSIST_3 = ")変更しない場合は空白にしておきます。";
//minagawa$    
    private static final String SUCCESS_MESSAGE = "ユーザ情報を変更しました。";
    private static final String DUMMY_PASSWORD = "";

    private static final String ORCA_ID_PREFIX = "1";
    
    private JFrame frame;
    protected JButton okButton;
    
    private Logger logger;

    // timerTask 関連
    private SimpleWorker worker;
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private int maxEstimation = 120*1000;   // 120 秒
    private int delay = 300;               // 300 mmsec
    
    /**
     * Creates a new instance of AddUserService
     */
    public ChangePasswordImpl() {
        setName(TITLE);
        logger = ClientContext.getBootLogger();
    }
    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    @Override
    public void start() {
        
        // Super Class で Frame を初期化する

        Runnable awt = new Runnable() {

            @Override
            public void run() {
                String title = ClientContext.getFrameTitle(getName());
                setFrame(new JFrame(title));
                getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                getFrame().addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        stop();
                    }
                });
        //        ComponentMemory cm = new ComponentMemory(getFrame(), new Point(0, 0),
        //                new Dimension(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)),
        //                this);
        //        cm.putCenter();

                ChangePasswordPanel cp = new ChangePasswordPanel();
                cp.get();
                getFrame().getContentPane().add(cp, BorderLayout.CENTER);
                getFrame().getRootPane().setDefaultButton(okButton);
                getFrame().pack();
                Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (size.width - getFrame().getPreferredSize().width) / 2;
                int y = (size.height - getFrame().getPreferredSize().height) / 3;
                getFrame().setLocation(x, y);
                getFrame().setVisible(true);
            }
        };

        SwingUtilities.invokeLater(awt);
    }
    
    @Override
    public void stop() {
        getFrame().setVisible(false);
        getFrame().dispose();
    }
    
    public void toFront() {
        if (getFrame() != null) {
            getFrame().toFront();
        }
    }
    
    /**
     * パスワード変更クラス。
     */
    protected class ChangePasswordPanel extends JPanel {
        
        private JTextField uid;                 // 利用者ID
        private JPasswordField userPassword1;   // パスワード1
        private JPasswordField userPassword2;   // パスワード2
        private JTextField orcaId;              // ORCA ID
        private JTextField sn;                  // 姓
        private JTextField givenName;           // 名
        private JTextField email;               // 電子メール
        private LicenseModel[] licenses;        // 職種(MML0026)
        private JComboBox licenseCombo;
        private DepartmentModel[] depts;        // 診療科(MML0028)
        private JComboBox deptCombo;
        private JTextField mayaku;              // 麻薬施用者免許番号    
        
        private JButton okButton;
        private JButton cancelButton;
        private boolean ok;
        
        private int[] userIdLength;
        private int[] passwordLength; // min,max
        
        
        public ChangePasswordPanel() {
            
            userIdLength = ClientContext.getIntArray("addUser.userId.length");
            passwordLength = ClientContext.getIntArray("addUser.password.length");
            
            // DocumentListener
            DocumentListener dl = new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                }
                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkButton();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkButton();
                }
            };
            
            // ユーザIDフィールドを生成する
            uid = createTextField(10, null, null, null);
            String pattern = ClientContext.getString("addUser.pattern.idPass");
            RegexConstrainedDocument userIdDoc = new RegexConstrainedDocument(pattern);
            uid.setDocument(userIdDoc);
//s.oh^ 不具合修正
            uid.enableInputMethods(false);
//s.oh$
            uid.getDocument().addDocumentListener(dl);
            uid.addFocusListener(AutoRomanListener.getInstance());
            uid.setToolTipText(pattern);
            
            // パスワードフィールドを設定する
            userPassword1 = createPassField(10, null, null, null);
            userPassword1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userPassword2.requestFocus();
                }
            });
            
            userPassword2 = createPassField(10, null, null, null);
            userPassword2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sn.requestFocus();
                }
            });
            RegexConstrainedDocument passwordDoc1 = new RegexConstrainedDocument(pattern);
            userPassword1.setDocument(passwordDoc1);
            userPassword1.setToolTipText(pattern);
            userPassword1.getDocument().addDocumentListener(dl);
            RegexConstrainedDocument passwordDoc2 = new RegexConstrainedDocument(pattern);
            userPassword2.setDocument(passwordDoc2);
            userPassword2.getDocument().addDocumentListener(dl);
            userPassword2.setToolTipText(pattern);
            userPassword1.addFocusListener(AutoRomanListener.getInstance());
            userPassword2.addFocusListener(AutoRomanListener.getInstance());

            // ORCA ID フィールドを生成する
            orcaId = createTextField(10, null, null, null);
            orcaId.getDocument().addDocumentListener(dl);
            orcaId.setToolTipText("ORCAでのIDを設定します。");
            orcaId.addFocusListener(AutoRomanListener.getInstance());
            
            // 姓
            sn = createTextField(10, null, null, dl);
            sn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    givenName.requestFocus();
                }
            });
            sn.addFocusListener(AutoKanjiListener.getInstance());
            
            // 名
            givenName = createTextField(10, null, null, dl);
            givenName.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userPassword1.requestFocus();
                }
            });
            givenName.addFocusListener(AutoKanjiListener.getInstance());
            
            // 電子メール
            email = createTextField(15, null, null, null);
            pattern = ClientContext.getString("addUser.pattern.email");
            RegexConstrainedDocument emailDoc = new RegexConstrainedDocument(pattern);
            email.setDocument(emailDoc);
            email.getDocument().addDocumentListener(dl);
            email.addFocusListener(AutoRomanListener.getInstance());
            
            // 医療資格
            licenses = ClientContext.getLicenseModel();
            licenseCombo = new JComboBox(licenses);
            boolean readOnly = Project.isReadOnly();    // == dc
            licenseCombo.setEnabled(!readOnly);
            //licenseCombo.setEnabled(true);
            
            // 診療科
            depts = ClientContext.getDepartmentModel();
            deptCombo = new JComboBox(depts);
            deptCombo.setEnabled(true);
            
            // 麻薬
            mayaku = createTextField(10, null, null, null);
            mayaku.getDocument().addDocumentListener(dl);
            mayaku.setToolTipText("ORCAでのIDを設定します。");
            mayaku.addFocusListener(AutoRomanListener.getInstance());
            
            // OK Btn
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changePassword();
                }
            };
            
            okButton = new JButton(UPDATE_BTN_TEXT);
            okButton.addActionListener(al);
            okButton.setEnabled(false);
            
            // Cancel Btn
            cancelButton = new JButton(CLOSE_BTN_TEXT);
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stop();
                }
            });
            
            // レイアウト
            JPanel content = new JPanel(new GridBagLayout());
            
            int x = 0;
            int y = 0;
            JLabel label = new JLabel(USER_ID_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, uid, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(PASSWORD_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, userPassword1, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel(CONFIRM_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, userPassword2, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);

            x = 0;
            y += 1;
            label = new JLabel(ORCA_ID_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, orcaId, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(SIR_NAME_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, sn, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel(GIVEN_NAME_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, givenName, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(EMAIL_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, email, x + 1, y, 2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(LISENCE_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, licenseCombo, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel(DEPT_TEXT, SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, deptCombo, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("麻薬施用者免許番号:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, mayaku, x + 1, y, 2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(" ", SwingConstants.RIGHT);
            constrain(content, label, x, y, 4, 1, GridBagConstraints.BOTH, GridBagConstraints.EAST);
            
            x = 0;
            y += 1;
            StringBuilder sb = new StringBuilder();
            sb.append(PASSWORD_ASSIST_1).append(passwordLength[0]);
//minagawa^ LSC Test            
            //sb.append(PASSWORD_ASSIST_2).append(passwordLength[1]);
            sb.append(PASSWORD_ASSIST_2);
//minagawa$            
            sb.append(PASSWORD_ASSIST_3);
            label = new JLabel(sb.toString());
            constrain(content, label, x, y, 4, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
            
            JPanel btnPanel;
            if (isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{cancelButton, okButton});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{okButton, cancelButton});
            }
            
            this.setLayout(new BorderLayout(0, 17));
            this.add(content, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        
        /**
         * GUI へ値を設定する。
         */
        public void get() {
            
            //-------------------------------------
            // UserModel を Project から設定する
            //-------------------------------------
            UserModel user = Project.getUserModel();
            uid.setText(user.idAsLocal());
            if (user.getOrcaId()!=null) {
                // 先頭の1 を除く
                orcaId.setText(user.getOrcaId().substring(1));
            }
            sn.setText(user.getSirName());
            givenName.setText(user.getGivenName());
            userPassword1.setText(DUMMY_PASSWORD);
            userPassword2.setText(DUMMY_PASSWORD);
            email.setText(user.getEmail());
            String license = user.getLicenseModel().getLicense();
            for (int i = 0; i < licenses.length; i++) {
                if (license.equals(licenses[i].getLicense())) {
                    licenseCombo.setSelectedIndex(i);
                    break;
                }
            }
            String deptStr = user.getDepartmentModel().getDepartment();
            for (int i = 0; i < depts.length; i++) {
                if (deptStr.equals(depts[i].getDepartment())) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // 麻薬
            mayaku.setText(user.getUseDrugId());
            
            checkButton();
        }
        
        /**
         * パスワードを変更する。
         */
        private void changePassword() {
            
            // 有効なパスワードでなければリターンする
            if (!passwordOk()) {
                return;
            }
            
            //-----------------------------
            // Project からユーザモデルを取得する
            //-----------------------------
            UserModel user = Project.getUserModel();
            
            //-----------------------------
            // 更新が成功するまでは変更しない
            //-----------------------------
            final UserModel updateModel = new UserModel();
            updateModel.setId(user.getId());
            updateModel.setFacilityModel(user.getFacilityModel());
            updateModel.setMemberType(user.getFacilityModel().getMemberType());
            
            //-----------------------------
            // ログインIDを設定する
            //-----------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(user.getFacilityModel().getFacilityId());
            sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
            sb.append(uid.getText().trim());
            String userId =  sb.toString();
            updateModel.setUserId(userId);
            
            //-----------------------------
            // パスワードを設定する
            //-----------------------------
            final String password = new String(userPassword1.getPassword());
            
            if (!password.equals(DUMMY_PASSWORD)) {
            
                String hashPass = HashUtil.MD5(password);
                updateModel.setPassword(hashPass);
                
            } else {
                //-----------------------------
                // パスワードは変更されていない
                //-----------------------------
                updateModel.setPassword(user.getPassword());
            }

            //-----------------------------
            // ORCAIDを設定する
            //-----------------------------
            String orId = orcaId.getText().trim();
            if (!orId.equals("")) {
                sb = new StringBuilder();
                sb.append(ORCA_ID_PREFIX).append(orId);
                updateModel.setOrcaId(sb.toString());
            }
            
            //-----------------------------
            // 姓名を設定する
            //-----------------------------
            String snSt = sn.getText().trim();
            updateModel.setSirName(snSt);
            String givenNameSt = givenName.getText().trim();
            updateModel.setGivenName(givenNameSt);
            updateModel.setCommonName(snSt + " " + givenNameSt);
            
            //-----------------------------
            // 電子メールを設定する
            //-----------------------------
            updateModel.setEmail(email.getText().trim());
            
            //-----------------------------
            // 麻薬施用者免許番号を設定する
            //-----------------------------
            updateModel.setUseDrugId(mayaku.getText().trim());
            
            //-----------------------------
            // 医療資格を設定する
            //-----------------------------
            int selected = licenseCombo.getSelectedIndex();
            updateModel.setLicenseModel(licenses[selected]);
            
            //-----------------------------
            // 診療科を設定する
            //-----------------------------
            selected = deptCombo.getSelectedIndex();
            updateModel.setDepartmentModel(depts[selected]);
            
            //-----------------------------
            // Roleを付け加える
            //-----------------------------
            Collection<RoleModel> roles = user.getRoles();
            for (RoleModel role : roles) {
                role.setUserId(user.getUserId());
                RoleModel updateRole = new RoleModel();
                updateRole.setId(role.getId());
                updateRole.setRole(role.getRole());
                updateRole.setUserModel(updateModel);
                updateRole.setUserId(updateModel.getUserId());
                updateModel.addRole(updateRole);
            }
            
            // タスクを実行する
            final UserDelegater udl = new UserDelegater();
            
            worker = new SimpleWorker<Void, Void>() {
        
                @Override
                protected Void doInBackground() throws Exception {
                    logger.debug("ChangePassword doInBackground");
                    int cnt = udl.updateUser(updateModel);
                    return null;
                }
                
                @Override
                protected void succeeded(Void result) {
                    logger.debug("ChangePassword succeeded");
                    Project.getProjectStub().setUserModel(updateModel);
                    Project.getProjectStub().setUserId(updateModel.idAsLocal());

                    //-------------------------------------
                    // Jersey Client
                    //-------------------------------------
                    /*JerseyClient jersy = JerseyClient.getInstance();
                    jersy.setUpAuthentication(updateModel.getUserId(), updateModel.getPassword(), true);*/

                    JOptionPane.showMessageDialog(getFrame(),
                            SUCCESS_MESSAGE,
                            ClientContext.getFrameTitle(getName()),
                            JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                protected void cancelled() {
                    logger.debug("ChangePassword cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    JOptionPane.showMessageDialog(getFrame(),
                                cause.getMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    logger.warn("ChangePassword failed");
                    logger.warn(cause.getCause());
                    logger.warn(cause.getMessage());
                }

                @Override
                protected void startProgress() {
                    delayCount = 0;
                    okButton.setEnabled(false);
                    taskTimer.start();
                }

                @Override
                protected void stopProgress() {
                    taskTimer.stop();
                    monitor.close();
                    okButton.setEnabled(true);
                    taskTimer = null;
                    monitor = null;
                }
            };

            Component c = getFrame();
            String message = null;
            String note = PROGRESS_NOTE;
            maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            delay = ClientContext.getInt("task.default.delay");
            monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

            taskTimer = new Timer(delay, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    delayCount++;

                    if (monitor.isCanceled() && (!worker.isCancelled())) {
                        worker.cancel(true);

                    } else {
                        monitor.setProgress(delayCount);
                    }
                }
            });

            worker.execute();
        }
        
        private boolean userIdOk() {
            
            String userId = uid.getText().trim();
            if (userId.equals("")) {
                return false;
            }
//minagawa^  LSC Test          
//            if (userId.length() < userIdLength[0] || userId.length() > userIdLength[1]) {
//                return false;
//            }
//            return true;          
            return (userId.length()>=userIdLength[0]);
//minagawa$            
        }
        
        /**
         * パスワードの有効性をチェックする。
         */
        private boolean passwordOk() {
            
            String passwd1 = new String(userPassword1.getPassword());
            String passwd2 = new String(userPassword2.getPassword());
            
            if (passwd1.equals(DUMMY_PASSWORD) && passwd2.equals(DUMMY_PASSWORD)) {
                return true;
            }
            
//minagawa^ LSC Test            
//            if ((passwd1.length() < passwordLength[0])
//            || (passwd1.length() > passwordLength[1])) {
//                return false;
//            }
//            
//            if ((passwd2.length() < passwordLength[0])
//            || (passwd2.length() > passwordLength[1])) {
//                return false;
//            }            
            if ((passwd1.length()<passwordLength[0]) || (passwd2.length()<passwordLength[0])) {
                return false;
            }
//minagawa$ 
            return passwd1.equals(passwd2) ? true : false;
        }
        
        /**
         * ボタンの enable/disable をコントロールする。
         */
        private void checkButton() {
            
            boolean uidOk = userIdOk();
            boolean passwordOk = passwordOk();
            boolean snOk = sn.getText().trim().equals("") ? false : true;
            boolean givenOk = givenName.getText().trim().equals("") ? false : true;
            boolean emailOk = email.getText().trim().equals("") ? false: true;
            
            boolean newOk = (uidOk && passwordOk && snOk && givenOk && emailOk) ? true : false;
            
            if (ok != newOk) {
                ok = newOk;
                okButton.setEnabled(ok);
            }
        }
    }
    
    /**
     * TextField を生成する。
     */
    private JTextField createTextField(int val, Insets margin, FocusAdapter fa, DocumentListener dl) {
        
        if (val == 0) {
            val = 30;
        }
        JTextField tf = new JTextField(val);
        
        if (margin == null) {
            margin = new Insets(1, 2, 1, 2);
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
    
    /**
     * パスワードフィールドを生成する。
     */
    private JPasswordField createPassField(int val, Insets margin, FocusAdapter fa, DocumentListener dl) {
        
        if (val == 0) {
            val = 30;
        }
        JPasswordField tf = new JPasswordField(val);
        
        if (margin == null) {
            margin = new Insets(1, 2, 1, 2);
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
    
    /**
     * GridBagLayout を使用してコンポーネントを配置する。
     */
    private void constrain(JPanel container, Component cmp, int x, int y,
            int width, int height, int fill, int anchor) {
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.fill = fill;
        c.anchor = anchor;
        c.insets = new Insets(0, 0, 5, 7);
        ((GridBagLayout) container.getLayout()).setConstraints(cmp, c);
        container.add(cmp);
    }
    
    /**
     * OSがmacかどうかを返す。
     * @return mac の時 true
     */
    private boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("mac") ? true : false;
    }
}
