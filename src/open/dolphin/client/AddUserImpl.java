package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import open.dolphin.delegater.BusinessDelegater;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.project.Project;
import open.dolphin.table.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;

/**
 * AddUserPlugin
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AddUserImpl extends AbstractMainTool implements AddUser {
    
    private static final String TITLE = "ユーザ管理";
    private static final String FACILITY_INFO = "施設情報";
    private static final String ADD_USER = "ユーザ登録";
    private static final String LIST_USER = "ユーザリスト";
    private static final String FACILITY_SUCCESS_MSG = "施設情報を更新しました。";
    private static final String ADD_USER_SUCCESS_MSG = "ユーザを登録しました。";
    private static final String DELETE_USER_SUCCESS_MSG = "ユーザを削除しました。";
    private static final String DELETE_OK_USER_ = "選択したユーザを削除します";
    private static int DEFAULT_WIDTH = 600;
    private static int DEFAULT_HEIGHT = 370;
    
    private JFrame frame;
    private ApplicationContext appCtx;
    private Application app;
    private TaskService taskService;
    private TaskMonitor taskMonitor;
    private Logger logger;
    
    /** Creates a new instance of AddUserService */
    public AddUserImpl() {
        setName(TITLE);
        appCtx = ClientContext.getApplicationContext();
        app = appCtx.getApplication();
        taskService = appCtx.getTaskService();
        taskMonitor = appCtx.getTaskMonitor();
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
        String title = ClientContext.getFrameTitle(getName());
        JFrame frm = new JFrame(title);
        setFrame(frm);
        frm.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
        ComponentMemory cm = new ComponentMemory(frm, new Point(0, 0),
                new Dimension(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)), this);
        cm.putCenter();
        
        // Component を生成する
        AddUserPanel ap = new AddUserPanel();
        FacilityInfoPanel fp = new FacilityInfoPanel();
        UserListPanel mp = new UserListPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(FACILITY_INFO, fp);
        tabbedPane.addTab(ADD_USER, ap);
        tabbedPane.addTab(LIST_USER, mp);
        fp.get();
        
        // Frame に加える
        getFrame().getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getFrame().setVisible(true);
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
     * 施設（医療機関）情報を変更するクラス。
     */
    protected class FacilityInfoPanel extends JPanel {
        
        // 施設情報フィールド
        //private JTextField facilityId;
        private JTextField facilityName;
        private JTextField zipField1;
        private JTextField zipField2;
        private JTextField addressField;
        private JTextField areaField;
        private JTextField cityField;
        private JTextField numberField;
        private JTextField urlField;
        
        // 更新等のボタン
        private JButton updateBtn;
        private JButton clearBtn;
        private JButton closeBtn;
        private boolean hasInitialized;
        
        public FacilityInfoPanel() {
            
            // GUI生成           
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
            
            facilityName = GUIFactory.createTextField(30, null, null, dl);
            zipField1 = GUIFactory.createTextField(3, null, null, dl);
            zipField2 = GUIFactory.createTextField(3, null, null, dl);
            addressField = GUIFactory.createTextField(30, null, null, dl);
            areaField = GUIFactory.createTextField(3, null, null, dl);
            cityField = GUIFactory.createTextField(3, null, null, dl);
            numberField = GUIFactory.createTextField(3, null, null, dl);
            urlField = GUIFactory.createTextField(30, null, null, dl);
            
            facilityName.addFocusListener(AutoKanjiListener.getInstance());
            zipField1.addFocusListener(AutoRomanListener.getInstance());
            zipField2.addFocusListener(AutoRomanListener.getInstance());
            addressField.addFocusListener(AutoKanjiListener.getInstance());
            areaField.addFocusListener(AutoRomanListener.getInstance());
            cityField.addFocusListener(AutoRomanListener.getInstance());
            numberField.addFocusListener(AutoRomanListener.getInstance());
            urlField.addFocusListener(AutoRomanListener.getInstance());
            
            updateBtn = new JButton("更新");
            updateBtn.setEnabled(false);
            updateBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    update();
                }
            });
            
            clearBtn = new JButton("戻す");
            clearBtn.setEnabled(false);
            clearBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    get();
                }
            });
            
            closeBtn = new JButton("閉じる");
            closeBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop();
                }
            });
            
            // レイアウト
            JPanel content = new JPanel(new GridBagLayout());
            
            int x = 0;
            int y = 0;
            JLabel label = new JLabel("医療機関名:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, facilityName, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("郵便番号:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, GUIFactory.createZipCodePanel(zipField1, zipField2), x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("住  所:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, addressField, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("電話番号:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, GUIFactory.createPhonePanel(areaField, cityField, numberField), x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("URL:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, urlField, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(" ", SwingConstants.RIGHT);
            constrain(content, label, x, y, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.EAST);
            
            JPanel btnPanel = null;
            if (isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{clearBtn, closeBtn, updateBtn});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{updateBtn, clearBtn, closeBtn});
            }
            
            this.setLayout(new BorderLayout(0, 11));
            this.add(content, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        
        public void get() {
            
            UserModel user = Project.getUserModel();
            FacilityModel facility = user.getFacilityModel();
            
            if (facility.getFacilityName() != null) {
                facilityName.setText(facility.getFacilityName());
            }
            
            if (facility.getZipCode() != null) {
                String val = facility.getZipCode();
                try {
                    StringTokenizer st = new StringTokenizer(val, "-");
                    if (st.hasMoreTokens()) {
                        zipField1.setText(st.nextToken());
                        zipField2.setText(st.nextToken());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (facility.getAddress() != null) {
                addressField.setText(facility.getAddress());
            }
            
            if (facility.getTelephone() != null) {
                String val = facility.getTelephone();
                try {
                    StringTokenizer st = new StringTokenizer(val, "-");
                    if (st.hasMoreTokens()) {
                        areaField.setText(st.nextToken());
                        cityField.setText(st.nextToken());
                        numberField.setText(st.nextToken());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (facility.getUrl() != null) {
                urlField.setText(facility.getUrl());
            }
            
            hasInitialized = true;
        }
        
        private void checkButton() {
            
            if (!hasInitialized) {
                return;
            }
            
            boolean nameEmpty = facilityName.getText().trim().equals("") ? true : false;
            boolean zip1Empty = zipField1.getText().trim().equals("") ? true : false;
            boolean zip2Empty = zipField2.getText().trim().equals("") ? true : false;
            boolean addressEmpty = addressField.getText().trim().equals("") ? true : false;
            boolean areaEmpty = areaField.getText().trim().equals("") ? true : false;
            boolean cityEmpty = cityField.getText().trim().equals("") ? true : false;
            boolean numberEmpty = numberField.getText().trim().equals("") ? true : false;
            
            if (nameEmpty && zip1Empty && zip2Empty && addressEmpty
                    && areaEmpty && cityEmpty && numberEmpty) {
                
                if (clearBtn.isEnabled()) {
                    clearBtn.setEnabled(false);
                }
            } else {
                if (!clearBtn.isEnabled()) {
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
            if (!updateBtn.isEnabled()) {
                updateBtn.setEnabled(true);
            }
        }
        
        private void update() {
            
            final UserModel user = Project.getUserModel();
            // ディタッチオブジェクトが必要である
            FacilityModel facility = user.getFacilityModel();
            
            // 医療機関コードは変更できない
            
            // 施設名
            String val = facilityName.getText().trim();
            if (!val.equals("")) {
                facility.setFacilityName(val);
            }
            
            // 郵便番号
            val = zipField1.getText().trim();
            String val2 = zipField2.getText().trim();
            if ((!val.equals("")) && (!val2.equals(""))) {
                facility.setZipCode(val + "-" + val2);
            }
            
            // 住所
            val = addressField.getText().trim();
            if (!val.equals("")) {
                facility.setAddress(val);
            }
            
            // 電話番号
            val = areaField.getText().trim();
            val2 = cityField.getText().trim();
            String val3 = numberField.getText().trim();
            if ((!val.equals("")) && (!val2.equals("")) && (!val3.equals(""))) {
                facility.setTelephone(val + "-" + val2 + "-" + val3);
            }
            
            // URL
            val = urlField.getText().trim();
            if (!val.equals("")) {
                facility.setUrl(val);
            }
            
            // 登録日
            // 変更しない
            
            // タスクを実行する
            final UserDelegater udl = new UserDelegater();
            
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            String updateMsg = ClientContext.getString("task.default.updateMessage");           
           
            Task task = new Task<Boolean, Void>(app) {
        
                protected Boolean doInBackground() throws Exception {
                    logger.debug("updateUser doInBackground");
                    int cnt = udl.updateFacility(user);
                    return cnt > 0 ? new Boolean(true) : new Boolean(false);
                }
                
                @Override
                protected void succeeded(Boolean result) {
                    logger.debug("updateUser succeeded");
                    if (result.booleanValue()) {
                        JOptionPane.showMessageDialog(getFrame(),
                                FACILITY_SUCCESS_MSG,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(getFrame(),
                                udl.getErrorMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                @Override
                protected void cancelled() {
                    logger.debug("updateUser cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    logger.warn("updateUser failed");
                    logger.warn(cause.getCause());
                    logger.warn(cause.getMessage());
                }

                @Override
                protected void interrupted(java.lang.InterruptedException e) {
                    logger.warn("updateUser interrupted");
                    logger.warn(e.getMessage());
                }
            };
            
            String message = null;
            Component c = getFrame();
            TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, updateMsg, delay, maxEstimation);
            taskMonitor.addPropertyChangeListener(w);

            taskService.execute(task);
        }
    }
    
    /**
     * ユーザリストを取得するクラス。名前がいけない。
     */
    protected class UserListPanel extends JPanel {
        
        private ObjectTableModel tableModel;
        private JTable table;
        private JButton getButton;
        private JButton deleteButton;
        private JButton cancelButton;
        
        public UserListPanel() {
            
            String[] columns = new String[] { "ユーザID", "姓", "名", "医療資格", "診療科" };
            
            // ユーザテーブル
            tableModel = new ObjectTableModel(columns, 7) {
                
                // 編集不可
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
                
                // オブジェクトをテーブルに表示する
                @Override
                public Object getValueAt(int row, int col) {
                    
                    UserModel entry = (UserModel) getObject(row);
                    if (entry == null) {
                        return null;
                    }
                    
                    String ret = null;
                    
                    switch (col) {
                        
                        case 0:
                            ret = entry.idAsLocal();
                            break;
                            
                        case 1:
                            ret = entry.getSirName();
                            break;
                            
                        case 2:
                            ret = entry.getGivenName();
                            break;
                            
                        case 3:
                            ret = entry.getLicenseModel().getLicenseDesc();
                            break;
                            
                        case 4:
                            ret = entry.getDepartmentModel().getDepartmentDesc();
                            break;
                    }
                    return ret;
                }
            };
            
            table = new JTable(tableModel);
            // Selection を設定する
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            table.setToolTipText(DELETE_OK_USER_);
            
            ListSelectionModel m = table.getSelectionModel();
            m.addListSelectionListener(new ListSelectionListener() {
                
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        // 削除ボタンをコントロールする
                        // 医療資格が other 以外は削除できない
                        int index = table.getSelectedRow();
                        UserModel entry = (UserModel) tableModel.getObject(index);
                        if (entry == null) {
                            return;
                        } else {
                            controleDelete(entry);
                        }
                    }
                }
            });
            
            // Layout
            JScrollPane scroller = new JScrollPane(table,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            getButton = new JButton("ユーザリスト");
            getButton.setEnabled(true);
            getButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getUsers();
                }
            });
            
            deleteButton = new JButton("削除");
            deleteButton.setEnabled(false);
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteUser();
                }
            });
            deleteButton.setToolTipText(DELETE_OK_USER_);
            
            cancelButton = new JButton("閉じる");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop();
                }
            });
            
            JPanel btnPanel = null;
            if (isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{deleteButton, cancelButton, getButton});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{getButton, deleteButton, cancelButton});
            }
            this.setLayout(new BorderLayout(0, 17));
            this.add(scroller, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        
        /**
         * 医療資格が other 以外は削除できない。
         * @param user
         */
        private void controleDelete(UserModel user) {          
            boolean isMe = user.getId() == Project.getUserModel().getId() ? true : false;
            deleteButton.setEnabled(!isMe);
        }
        
        /**
         * 施設内の全ユーザを取得する。
         */
        private void getUsers() {
            
            final UserDelegater udl = new UserDelegater();
            
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            String note = ClientContext.getString("task.default.searchMessage");
            
            Task task = new Task<ArrayList, Void>(app) {
        
                protected ArrayList doInBackground() throws Exception {
                    logger.debug("getUsers doInBackground");
                    ArrayList result = udl.getAllUser();
                    return result;
                }
                
                @Override
                protected void succeeded(ArrayList results) {
                    logger.debug("getUsers succeeded");
                    if (udl.getErrorCode() == BusinessDelegater.NO_ERROR) {
                        tableModel.setObjectList(results);
                    } else {
                        JOptionPane.showMessageDialog(getFrame(),
                                udl.getErrorMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                @Override
                protected void cancelled() {
                    logger.debug("getUsers cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    logger.warn("getUsers failed");
                    logger.warn(cause.getCause());
                    logger.warn(cause.getMessage());
                }

                @Override
                protected void interrupted(java.lang.InterruptedException e) {
                    logger.warn("getUsers interrupted");
                    logger.warn(e.getMessage());
                }
            };
            
            String message = null;
            Component c = getFrame();
            TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, delay, maxEstimation);
            taskMonitor.addPropertyChangeListener(w);

            taskService.execute(task);
        }
        
        /**
         * 選択したユーザを削除する。
         *
         */
        private void deleteUser() {
            
            int row = table.getSelectedRow();
            UserModel entry = (UserModel) tableModel.getObject(row);
            if (entry == null) {
                return;
            }
            
            //
            // 作成したドキュメントも削除するかどうかを選ぶ
            //
            boolean deleteDoc = true;
            if (entry.getLicenseModel().getLicense().equals("doctor")) {
                deleteDoc = false;
            }
            
            final UserDelegater udl = new UserDelegater();
            
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            String note = ClientContext.getString("task.default.deleteMessage");
            
            final String deleteId = entry.getUserId();
            
            Task task = new Task<ArrayList, Void>(app) {
        
                protected ArrayList doInBackground() throws Exception {
                    logger.debug("deleteUser doInBackground");
                    ArrayList result = null;
                    if (udl.removeUser(deleteId) > 0) {
                        result = udl.getAllUser();
                    } 
                    return result;
                }
                
                @Override
                protected void succeeded(ArrayList results) {
                    logger.debug("deleteUser succeeded");
                    if (udl.getErrorCode() == BusinessDelegater.NO_ERROR) {
                        tableModel.setObjectList(results);
                        JOptionPane.showMessageDialog(getFrame(),
                                DELETE_USER_SUCCESS_MSG,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(getFrame(),
                                udl.getErrorMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                @Override
                protected void cancelled() {
                    logger.debug("deleteUser cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    logger.warn("deleteUser failed");
                    logger.warn(cause.getCause());
                    logger.warn(cause.getMessage());
                }

                @Override
                protected void interrupted(java.lang.InterruptedException e) {
                    logger.warn("deleteUser interrupted");
                    logger.warn(e.getMessage());
                }
            };
            
            String message = null;
            Component c = getFrame();
            TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, delay, maxEstimation);
            taskMonitor.addPropertyChangeListener(w);

            taskService.execute(task);
        }
    }
    
    /**
     * 施設内ユーザ登録クラス。
     */
    protected class AddUserPanel extends JPanel {
        
        private JTextField uid; // 利用者ID
        private JPasswordField userPassword1; // パスワード
        private JPasswordField userPassword2; // パスワード
        private JTextField sn; // 姓
        private JTextField givenName; // 名
        // private String cn; // 氏名(sn & ' ' & givenName)
        private LicenseModel[] licenses; // 職種(MML0026)
        private JComboBox licenseCombo;
        private DepartmentModel[] depts; // 診療科(MML0028)
        private JComboBox deptCombo;
        // private String authority; // LASに対する権限(admin:管理者,user:一般利用者)
        private JTextField emailField; // メールアドレス
        
        // JTextField description;
        private JButton okButton;
        private JButton cancelButton;
        
        private boolean ok;
        
        // UserId と Password の長さ
        private int[] userIdLength; // min,max
        private int[] passwordLength; // min,max
        private String idPassPattern;
        private String usersRole; // user に与える role 名
        
        public AddUserPanel() {
            
            userIdLength = ClientContext.getIntArray("addUser.userId.length");
            passwordLength = ClientContext.getIntArray("addUser.password.length");
            usersRole = ClientContext.getString("addUser.user.roleName");
            idPassPattern = ClientContext.getString("addUser.pattern.idPass");
            
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
            
            uid = GUIFactory.createTextField(10, null, null, dl);
            uid.setDocument(new RegexConstrainedDocument(idPassPattern));
            uid.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userPassword1.requestFocus();
                }
            });
            uid.addFocusListener(AutoRomanListener.getInstance());
            
            userPassword1 = GUIFactory.createPassField(10, null, null, dl);
            userPassword1.setDocument(new RegexConstrainedDocument(idPassPattern));
            userPassword1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userPassword2.requestFocus();
                }
            });
            userPassword1.addFocusListener(AutoRomanListener.getInstance());
            
            userPassword2 = GUIFactory.createPassField(10, null, null, dl);
            userPassword2.setDocument(new RegexConstrainedDocument(idPassPattern));
            userPassword2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sn.requestFocus();
                }
            });
            userPassword2.addFocusListener(AutoRomanListener.getInstance());
            
            sn = GUIFactory.createTextField(10, null, null, dl);
            sn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    givenName.requestFocus();
                }
            });
            userPassword2.addFocusListener(AutoKanjiListener.getInstance());
            
            givenName = GUIFactory.createTextField(10, null, null, dl);
            givenName.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    emailField.requestFocus();
                }
            });
            givenName.addFocusListener(AutoKanjiListener.getInstance());
            
            emailField = GUIFactory.createTextField(15, null, null, dl);
            emailField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    uid.requestFocus();
                }
            });
            emailField.addFocusListener(AutoRomanListener.getInstance());
            
            licenses = ClientContext.getLicenseModel();
            licenseCombo = new JComboBox(licenses);
            
            depts = ClientContext.getDepartmentModel();
            deptCombo = new JComboBox(depts);
            
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addUserEntry();
                }
            };
            
            okButton = new JButton("追加");
            okButton.addActionListener(al);
            okButton.setEnabled(false);
            cancelButton = new JButton("閉じる");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop();
                }
            });
            
            // レイアウト
            JPanel content = new JPanel(new GridBagLayout());
            
            int x = 0;
            int y = 0;
            JLabel label = new JLabel("ユーザID:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, uid, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("パスワード:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, userPassword1, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel("確認:", SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, userPassword2, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("姓:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, sn, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel("名:", SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, givenName, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("医療資格:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, licenseCombo, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            label = new JLabel("診療科:", SwingConstants.RIGHT);
            constrain(content, label, x + 2, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, deptCombo, x + 3, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel("電子メール:", SwingConstants.RIGHT);
            constrain(content, label, x, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
            constrain(content, emailField, x + 1, y, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
            
            x = 0;
            y += 1;
            label = new JLabel(" ", SwingConstants.RIGHT);
            constrain(content, label, x, y, 4, 1, GridBagConstraints.BOTH, GridBagConstraints.EAST);
            
            x = 0;
            y += 1;
            label = new JLabel("ユーザID - 半角英数記で" + userIdLength[0] + "文字以上" + userIdLength[1] + "文字以内");
            constrain(content, label, x, y, 4, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
            x = 0;
            y += 1;
            label = new JLabel("パスワード - 半角英数記で" + passwordLength[0] + "文字以上" + passwordLength[1] + "文字以内");
            constrain(content, label, x, y, 4, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
            
            JPanel btnPanel = null;
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
        
        private void addUserEntry() {
            
            if (!userIdOk()) {
                return;
            }
            
            if (!passwordOk()) {
                return;
            }
            
            String userId = uid.getText().trim();
            String pass = new String(userPassword1.getPassword());
            UserModel loginUser = Project.getUserModel();
            String facilityId = loginUser.getFacilityModel().getFacilityId();
            
            String Algorithm = ClientContext.getString("addUser.password.hash.algorithm");
            String encoding = ClientContext.getString("addUser.password.hash.encoding");
            //String charset = ClientContext.getString("addUser.password.hash.charset");
            String charset = null;
            String hashPass = org.jboss.security.Util.createPasswordHash(Algorithm, encoding, charset, userId, pass);
            pass = null;
            
            final UserModel user = new UserModel();
            StringBuilder sb = new StringBuilder(facilityId);
            sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
            sb.append(userId);
            user.setUserId(sb.toString());
            user.setPassword(hashPass);
            user.setSirName(sn.getText().trim());
            user.setGivenName(givenName.getText().trim());
            user.setCommonName(user.getSirName() + " " + user.getGivenName());
            
            // 施設情報
            // 管理者のものを使用する
            user.setFacilityModel(Project.getUserModel().getFacilityModel());
            
            // 医療資格
            int index = licenseCombo.getSelectedIndex();
            user.setLicenseModel(licenses[index]);
            
            // 診療科
            index = deptCombo.getSelectedIndex();
            user.setDepartmentModel(depts[index]);
            
            // MemberType
            // 管理者のものを使用する
            user.setMemberType(Project.getUserModel().getMemberType());
            
            // RegisteredDate
            if (Project.getUserModel().getMemberType().equals("ASP_TESTER")) {
                user.setRegisteredDate(Project.getUserModel().getRegisteredDate());
            } else {
                user.setRegisteredDate(new Date());
            }
            
            // Email
            user.setEmail(emailField.getText().trim());
            
            // Role = user
            RoleModel rm = new RoleModel();
            rm.setRole(usersRole);
            user.addRole(rm);
            rm.setUser(user);
            rm.setUserId(user.getUserId()); // 必要
            
            // タスクを実行する
            final UserDelegater udl = new UserDelegater();
            
            int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
            int delay = ClientContext.getInt("task.default.delay");
            String addMsg = ClientContext.getString("task.default.addMessage");          
            
            Task task = new Task<Boolean, Void>(app) {
        
                protected Boolean doInBackground() throws Exception {
                    logger.debug("addUserEntry doInBackground");
                    int cnt = udl.putUser(user);
                    return cnt > 0 ? new Boolean(true) : new Boolean(false);
                }
                
                @Override
                protected void succeeded(Boolean results) {
                    logger.debug("addUserEntry succeeded");
                    if (results.booleanValue()) {
                        JOptionPane.showMessageDialog(getFrame(),
                                ADD_USER_SUCCESS_MSG,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(getFrame(),
                                udl.getErrorMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

                @Override
                protected void cancelled() {
                    logger.debug("addUserEntry cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    logger.warn("addUserEntry failed");
                    logger.warn(cause.getCause());
                    logger.warn(cause.getMessage());
                }

                @Override
                protected void interrupted(java.lang.InterruptedException e) {
                    logger.warn("addUserEntry interrupted");
                    logger.warn(e.getMessage());
                }
            };
            
            String message = null;
            Component c = getFrame();
            TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, addMsg, delay, maxEstimation);
            taskMonitor.addPropertyChangeListener(w);

            taskService.execute(task);
        }
        
        private boolean userIdOk() {
            
            String userId = uid.getText().trim();
            if (userId.equals("")) {
                return false;
            }
            
            int len = userId.length();
            return (len >= userIdLength[0] && len <= userIdLength[1]) ? true
                    : false;
        }
        
        private boolean passwordOk() {
            
            String passwd1 = new String(userPassword1.getPassword());
            String passwd2 = new String(userPassword2.getPassword());
            
            if (passwd1.equals("") || passwd2.equals("")) {
                return false;
            }
            
            if ((passwd1.length() < passwordLength[0])
            || (passwd1.length() > passwordLength[1])) {
                return false;
            }
            
            if ((passwd2.length() < passwordLength[0])
            || (passwd2.length() > passwordLength[1])) {
                return false;
            }
            
            return passwd1.equals(passwd2) ? true : false;
        }
        
        private void checkButton() {
            
            boolean userOk = userIdOk();
            boolean passwordOk = passwordOk();
            boolean snOk = sn.getText().trim().equals("") ? false : true;
            boolean givenOk = givenName.getText().trim().equals("") ? false : true;
            boolean emailOk = emailField.getText().trim().equals("") ? false : true;
            
            boolean newOk = (userOk && passwordOk && snOk && givenOk && emailOk) ? true
                    : false;
            
            if (ok != newOk) {
                ok = newOk;
                okButton.setEnabled(ok);
            }
        }
    }
    
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
     * タイムアウト警告表示を行う。
     */
    private void wraningTimeOut() {
        StringBuilder sb = new StringBuilder();
        sb.append(ClientContext.getString("task.timeoutMsg1"));
        sb.append("\n");
        sb.append(ClientContext.getString("task.timeoutMsg1"));
        JOptionPane.showMessageDialog(getFrame(),
                sb.toString(),
                ClientContext.getFrameTitle(getName()),
                JOptionPane.WARNING_MESSAGE);
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
