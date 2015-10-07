package open.dolphin.impl.profile;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import open.dolphin.client.AbstractMainTool;
import open.dolphin.client.AddUser;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.RegexConstrainedDocument;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.HashUtil;

/**
 * AddUserPlugin
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AddUserImpl extends AbstractMainTool implements AddUser {
    
    private final String TITLE;
    private final String FACILITY_INFO;
    private final String ADD_USER;
    private final String LIST_USER;
    
    private int index;
    private JFrame frame;

    // timerTask 関連
    private SimpleWorker worker;
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private int maxEstimation = 120*1000;   // 120 秒
    private int delay = 300;               // 300 mmsec
    
    /** Creates a new instance of AddUserService */
    public AddUserImpl() {
        super();
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
        TITLE = bundle.getString("title.window");
        FACILITY_INFO = bundle.getString("title.editFacilityINfo");
        ADD_USER = bundle.getString("title.addInternalUser");
        LIST_USER = bundle.getString("title.listUsers");
        
        setName(TITLE);
    }
    
    @Override
    public void setStartIndex(int index) {
        this.index = index;
    }
    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    @Override
    public void start() {

        Runnable awt = () -> {
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
            
            // Component を生成する
            AddUserPanel ap = new AddUserPanel();
            FacilityInfoPanel fp = new FacilityInfoPanel();
            UserListPanel mp = new UserListPanel();
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // 順番変更は可能か
            tabbedPane.addTab(FACILITY_INFO, fp);
            tabbedPane.addTab(ADD_USER, ap);
            tabbedPane.addTab(LIST_USER, mp);
            
            fp.get();
            tabbedPane.setSelectedIndex(index);
            
            // Frame に加える
            getFrame().getContentPane().add(tabbedPane, BorderLayout.CENTER);
            
            getFrame().pack();
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (size.width - getFrame().getPreferredSize().width) / 2;
            int y = (size.height - getFrame().getPreferredSize().height) / 3;
            getFrame().setLocation(x, y);
            
            getFrame().setVisible(true);
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
     * 施設（医療機関）情報を変更するクラス。
     */
    protected class FacilityInfoPanel extends JPanel {
        
        // View
        private final FacilityEditorView view;
        
        // 更新等のボタン
        private final JButton updateBtn;
        private final JButton clearBtn;
        private final JButton closeBtn;
        private boolean hasInitialized;
        
        public FacilityInfoPanel() {
            
            // GUI生成
            view = new FacilityEditorView();
            
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
            
            // 医療機関ID
            view.getFacilityId().setEnabled(false);
            
            // 名称
            view.getFacilityName().getDocument().addDocumentListener(dl);
            view.getFacilityName().addFocusListener(AutoKanjiListener.getInstance());
            
            // 郵便番号
            view.getZipField1().getDocument().addDocumentListener(dl);
            view.getZipField1().addFocusListener(AutoRomanListener.getInstance());
            view.getZipField2().getDocument().addDocumentListener(dl);
            view.getZipField2().addFocusListener(AutoRomanListener.getInstance());
           
            // 住所
            view.getAddressField().getDocument().addDocumentListener(dl);
            view.getAddressField().addFocusListener(AutoKanjiListener.getInstance());
            
            // 電話番号
            view.getAreaField().getDocument().addDocumentListener(dl);
            view.getAreaField().addFocusListener(AutoRomanListener.getInstance());
            view.getCityField().getDocument().addDocumentListener(dl);
            view.getCityField().addFocusListener(AutoRomanListener.getInstance());
            view.getNumberField().getDocument().addDocumentListener(dl);
            view.getNumberField().addFocusListener(AutoRomanListener.getInstance());
            
            // FAX
            view.getAreaFieldFax().getDocument().addDocumentListener(dl);
            view.getAreaFieldFax().addFocusListener(AutoRomanListener.getInstance());
            view.getCityFieldFax().getDocument().addDocumentListener(dl);
            view.getCityFieldFax().addFocusListener(AutoRomanListener.getInstance());
            view.getNumberFieldFax().getDocument().addDocumentListener(dl);
            view.getNumberFieldFax().addFocusListener(AutoRomanListener.getInstance());
            
            // URL
            view.getUrlField().getDocument().addDocumentListener(dl);
            view.getUrlField().addFocusListener(AutoRomanListener.getInstance());
            
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            
            updateBtn = new JButton(bundle.getString("actionText.update"));
            updateBtn.setEnabled(false);
            updateBtn.addActionListener((ActionEvent e) -> {
                update();
            });
            
            clearBtn = new JButton(bundle.getString("actionText.revert"));
            clearBtn.setEnabled(false);
            clearBtn.addActionListener((ActionEvent e) -> {
                get();
            });
            
            closeBtn = new JButton(bundle.getString("actionText.close"));
            closeBtn.addActionListener((ActionEvent e) -> {
                stop();
            });
            
            JPanel btnPanel;
            if (ClientContext.isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{clearBtn, closeBtn, updateBtn});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{updateBtn, clearBtn, closeBtn});
            }
            
            this.setLayout(new BorderLayout(0, 11));
            this.add(view, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        
        public final void get() {
            
            UserModel user = Project.getUserModel();
            FacilityModel facility = user.getFacilityModel();
            
            if (facility.getFacilityId() != null) {
                view.getFacilityId().setText(facility.getFacilityId());
            }
            
            if (facility.getFacilityName() != null) {
                view.getFacilityName().setText(facility.getFacilityName());
            }
            
            if (facility.getZipCode() != null) {
                String val = facility.getZipCode();
                try {
                    StringTokenizer st = new StringTokenizer(val, "-");
                    if (st.hasMoreTokens()) {
                        view.getZipField1().setText(st.nextToken());
                        view.getZipField2().setText(st.nextToken());
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            
            if (facility.getAddress() != null) {
                view.getAddressField().setText(facility.getAddress());
            }
            
            if (facility.getTelephone() != null) {
                String val = facility.getTelephone();
                try {
                    String[] cmp = val.split("-");
                    view.getAreaField().setText(cmp[0]);
                    view.getCityField().setText(cmp[1]);
                    view.getNumberField().setText(cmp[2]);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

            if (facility.getFacsimile() != null) {
                String val = facility.getFacsimile();
                try {
                    String[] cmp = val.split("-");
                    view.getAreaFieldFax().setText(cmp[0]);
                    view.getCityFieldFax().setText(cmp[1]);
                    view.getNumberFieldFax().setText(cmp[2]);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            
            if (facility.getUrl() != null) {
                view.getUrlField().setText(facility.getUrl());
            }
            
            hasInitialized = true;
        }
        
        private void checkButton() {
            
            if (!hasInitialized) {
                return;
            }
            
            boolean nameEmpty = view.getFacilityName().getText().trim().equals("");
            boolean fidEmpty = view.getFacilityId().getText().trim().equals("");
            boolean zip1Empty = view.getZipField1().getText().trim().equals("");
            boolean zip2Empty = view.getZipField2().getText().trim().equals("");
            boolean addressEmpty = view.getAddressField().getText().trim().equals("");
            boolean areaEmpty = view.getAreaField().getText().trim().equals("");
            boolean cityEmpty = view.getCityField().getText().trim().equals("");
            boolean numberEmpty = view.getNumberField().getText().trim().equals("");
            
            if (nameEmpty && fidEmpty && zip1Empty && zip2Empty && addressEmpty
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
            String val = view.getFacilityName().getText().trim();
            if (!val.equals("")) {
                facility.setFacilityName(val);
            }
            
            // 郵便番号
            val = view.getZipField1().getText().trim();
            String val2 = view.getZipField2().getText().trim();
            if ((!val.equals("")) && (!val2.equals(""))) {
                facility.setZipCode(val + "-" + val2);
            }
            
            // 住所
            val = view.getAddressField().getText().trim();
            if (!val.equals("")) {
                facility.setAddress(val);
            }
            
            // 電話番号
            val = view.getAreaField().getText().trim();
            val2 = view.getCityField().getText().trim();
            String val3 = view.getNumberField().getText().trim();
            if ((!val.equals("")) && (!val2.equals("")) && (!val3.equals(""))) {
                facility.setTelephone(val + "-" + val2 + "-" + val3);
            }

            // Fax番号
            val = view.getAreaFieldFax().getText().trim();
            val2 = view.getCityFieldFax().getText().trim();
            val3 = view.getNumberFieldFax().getText().trim();
            if ((!val.equals("")) && (!val2.equals("")) && (!val3.equals(""))) {
                facility.setFacsimile(val + "-" + val2 + "-" + val3);
            }
            
            // URL
            val = view.getUrlField().getText().trim();
            if (!val.equals("")) {
                facility.setUrl(val);
            }
            
            // 登録日
            // 変更しない
            
            // タスクを実行する
            final UserDelegater udl = new UserDelegater();        
           
            worker = new SimpleWorker<Boolean, Void>() {
        
                @Override
                protected Boolean doInBackground() throws Exception {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("updateUser doInBackground");
                    int cnt = udl.updateFacility(user);
                    return cnt > 0;
                }
                
                @Override
                protected void succeeded(Boolean result) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("updateUser succeeded");
                    java.util.ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
                    String msg = bundle.getString("message.updatedFacilityInfo");
                    JOptionPane.showMessageDialog(getFrame(),
                            msg,
                            ClientContext.getFrameTitle(getName()),
                            JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                protected void cancelled() {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("updateUser cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    JOptionPane.showMessageDialog(getFrame(),
                                cause.getMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("updateUser failed");
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getCause().getMessage());
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
                }

                @Override
                protected void startProgress() {
                    delayCount = 0;
                    taskTimer.start();
                }

                @Override
                protected void stopProgress() {
                    taskTimer.stop();
                    monitor.close();
                    taskTimer = null;
                    monitor = null;
                }
            };

            Component c = getFrame();
            String message = null;
            ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            String note = bundle.getString("task.default.updateMessage");
            maxEstimation = Integer.parseInt(bundle.getString("task.default.maxEstimation"));
            delay = Integer.parseInt(bundle.getString("task.default.delay"));

            monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

            taskTimer = new Timer(delay, (ActionEvent e) -> {
                delayCount++;
                
                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);
                    
                } else {
                    monitor.setProgress(delayCount);
                }
            });

            worker.execute();
        }
    }
    
    /**
     * ユーザリストを取得するクラス。名前がいけない。
     */
    protected class UserListPanel extends JPanel {
        
        private ListTableModel<UserModel> tableModel;
        private JTable table;
        private final JButton getButton;
        private final JButton deleteButton;
        private final JButton cancelButton;
        
        public UserListPanel() {
            
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            String line = bundle.getString("columnNames.userListTable");
            String[] columns = line.split(",");
            
            // ユーザテーブル
            tableModel = new ListTableModel<UserModel>(columns, 0) {
                
                // 編集不可
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
                
                // オブジェクトをテーブルに表示する
                @Override
                public Object getValueAt(int row, int col) {
                    
                    UserModel entry = getObject(row);
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
            table.setToolTipText(bundle.getString("toolTipText.deleteSelectedUser"));
            
            // renderer
            StripeTableCellRenderer rederer = new StripeTableCellRenderer();
            rederer.setTable(table);
            rederer.setDefaultRenderer();
            
            ListSelectionModel m = table.getSelectionModel();
            m.addListSelectionListener((ListSelectionEvent e) -> {
                if (e.getValueIsAdjusting() == false) {
                    // 削除ボタンをコントロールする
                    // 医療資格が other 以外は削除できない
                    int index1 = table.getSelectedRow();
                    UserModel entry = tableModel.getObject(index1);
                    if (entry!=null) {
                        controleDelete(entry);
                    }
                }
            });
            
            // Layout
            JScrollPane scroller = new JScrollPane(table,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroller.getViewport().setPreferredSize(new Dimension(480,200));
            
            getButton = new JButton(bundle.getString("actionText.listUsers"));
            getButton.setEnabled(true);
            //getButton.setMnemonic('L');
            getButton.addActionListener((ActionEvent e) -> {
                getUsers();
            });
            
            deleteButton = new JButton(bundle.getString("actionText.delete"));
            deleteButton.setEnabled(false);
            //deleteButton.setMnemonic('D');
            deleteButton.addActionListener((ActionEvent e) -> {
                deleteUser();
            });
            deleteButton.setToolTipText(bundle.getString("toolTipText.deleteBtn"));
            
            cancelButton = new JButton(bundle.getString("actionText.close"));
            cancelButton.addActionListener((ActionEvent e) -> {
                stop();
            });
            //cancelButton.setMnemonic('C');
            
            JPanel btnPanel;
            if (ClientContext.isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{deleteButton, cancelButton, getButton});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{getButton, deleteButton, cancelButton});
            }
            this.setLayout(new BorderLayout(0, 17));
            this.add(scroller, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
            
//s.oh^ 2013/01/22 表示時にリストを取得するように変更
            getUsers();
//s.oh$
        }
        
        /**
         * 医療資格が other 以外は削除できない。
         * @param user
         */
        private void controleDelete(UserModel user) {          
            boolean isMe = user.getId() == Project.getUserModel().getId();
            deleteButton.setEnabled(!isMe);
        }
        
        /**
         * 施設内の全ユーザを取得する。
         */
        private void getUsers() {
            
            final UserDelegater udl = new UserDelegater();
            
            worker = new SimpleWorker<List<UserModel>, Void>() {
        
                @Override
                protected List<UserModel> doInBackground() throws Exception {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("getUsers doInBackground");
                    ArrayList<UserModel> result = udl.getAllUser();
                    return result;
                }
                
                @Override
                protected void succeeded(List<UserModel> results) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("getUsers succeeded");
//s.oh^ 2013/01/21 メンテユーザの非表示
                    //tableModel.setDataProvider(results);
                    ArrayList<UserModel> list = new ArrayList<>();
                    for(UserModel model : results) {
                        if(model.getUserId().endsWith("lscmainte")) {
                        }else{
                            list.add(model);
                        }
                    }
                    tableModel.setDataProvider(list);
//s.oh$
                }

                @Override
                protected void cancelled() {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("getUsers cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    JOptionPane.showMessageDialog(getFrame(),
                                cause.getMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("getUsers failed");
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getCause().getMessage());
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
                }

                @Override
                protected void startProgress() {
                    delayCount = 0;
                    taskTimer.start();
                }

                @Override
                protected void stopProgress() {
                    taskTimer.stop();
                    monitor.close();
                    taskTimer = null;
                    monitor = null;
                }
            };
            
            Component c = getFrame();
            String message = null;
            ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            String note = bundle.getString("task.default.searchMessage");
            maxEstimation = Integer.parseInt(bundle.getString("task.default.maxEstimation"));
            delay = Integer.parseInt(bundle.getString("task.default.delay"));

            monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

            taskTimer = new Timer(delay, (ActionEvent e) -> {
                delayCount++;
                
                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);
                    
                } else {
                    monitor.setProgress(delayCount);
                }
            });

            worker.execute();
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
            final String deleteId = entry.getUserId();
            
            worker = new SimpleWorker<List<UserModel>, Void>() {
        
                @Override
                protected List<UserModel> doInBackground() throws Exception {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("deleteUser doInBackground");
                    ArrayList<UserModel> result = null;
                    if (udl.deleteUser(deleteId) > 0) {
                        result = udl.getAllUser();
                    } 
                    return result;
                }
                
                @Override
                protected void succeeded(List<UserModel> results) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("deleteUser succeeded");
//s.oh^ 2013/01/21 メンテユーザの非表示
                    //tableModel.setDataProvider(results);
                    ArrayList<UserModel> list = new ArrayList<>();
                    for(UserModel model : results) {
                        if(model.getUserId().endsWith("lscmainte")) {
                        }else{
                            list.add(model);
                        }
                    }
                    tableModel.setDataProvider(list);
//s.oh$
                    String msg = ClientContext.getMyBundle(AddUserImpl.class).getString("message.deletedUser");
                    JOptionPane.showMessageDialog(getFrame(),
                            msg,
                            ClientContext.getFrameTitle(getName()),
                            JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                protected void cancelled() {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("deleteUser cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    JOptionPane.showMessageDialog(getFrame(),
                                cause.getMessage(),
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("deleteUser failed");
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getCause().getMessage());
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
                }

                @Override
                protected void startProgress() {
                    delayCount = 0;
                    taskTimer.start();
                }

                @Override
                protected void stopProgress() {
                    taskTimer.stop();
                    monitor.close();
                    taskTimer = null;
                    monitor = null;
                }
            };

            Component c = getFrame();
            String message = null;
            ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            String note = bundle.getString("task.default.deleteMessage");
            maxEstimation = Integer.parseInt(bundle.getString("task.default.maxEstimation"));
            delay = Integer.parseInt(bundle.getString("task.default.delay"));
            
            monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

            taskTimer = new Timer(delay, (ActionEvent e) -> {
                delayCount++;
                
                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);
                    
                } else {
                    monitor.setProgress(delayCount);
                }
            });

            worker.execute();
        }
    }
    
    /**
     * 施設内ユーザ登録クラス。
     */
    protected class AddUserPanel extends JPanel {
        
        // cn 氏名(sn & ' ' & givenName)
        private final LicenseModel[] licenses; // 職種(MML0026)
        private final DepartmentModel[] depts; // 診療科(MML0028)
        
        // JTextField description;
        private final JButton okButton;
        private final JButton cancelButton;
        
        private boolean ok;
        
        // UserId と Password の長さ
        private final int[] userIdLength; // min,max
        private final int[] passwordLength; // min,max
        private final String idPassPattern;
        private final String usersRole; // user に与える role 名
        
        private AddUserView view;
        
        public AddUserPanel() {
            
            view = new AddUserView();
            
            userIdLength = ClientContext.getIntArray("addUser.userId.length");
            passwordLength = ClientContext.getIntArray("addUser.password.length");
            usersRole = ClientContext.getString("addUser.user.roleName");
            idPassPattern = ClientContext.getString("addUser.pattern.idPass");
            
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
            
            JTextField uid = view.getUid();
            uid.addFocusListener(AutoRomanListener.getInstance());
            uid.getDocument().addDocumentListener(dl);
            uid.setDocument(new RegexConstrainedDocument(idPassPattern));
//s.oh^ 不具合修正
            uid.enableInputMethods(false);
//s.oh$
            uid.addActionListener((ActionEvent e) -> {
                view.getUserPassword1().requestFocus();
            });
            
            JTextField userPassword1 = view.getUserPassword1();
            userPassword1.addFocusListener(AutoRomanListener.getInstance());
            userPassword1.getDocument().addDocumentListener(dl);
            userPassword1.setDocument(new RegexConstrainedDocument(idPassPattern));
            userPassword1.addActionListener((ActionEvent e) -> {
                view.getUserPassword2().requestFocus();
            });
            
            JTextField userPassword2 = view.getUserPassword2();
            userPassword2.addFocusListener(AutoRomanListener.getInstance());
            userPassword2.getDocument().addDocumentListener(dl);
            userPassword2.setDocument(new RegexConstrainedDocument(idPassPattern));
            userPassword2.addActionListener((ActionEvent e) -> {
                view.getSn().requestFocus();
            });
            
            JTextField sn = view.getSn();
            sn.addFocusListener(AutoKanjiListener.getInstance());
            sn.getDocument().addDocumentListener(dl);
            sn.addActionListener((ActionEvent e) -> {
                view.getGivenName().requestFocus();
            });
            
            JTextField givenName = view.getGivenName();
            givenName.addFocusListener(AutoKanjiListener.getInstance());
            givenName.getDocument().addDocumentListener(dl);
            givenName.addActionListener((ActionEvent e) -> {
                view.getEmailField().requestFocus();
            });
            
            JTextField emailField = view.getEmailField();
            emailField.addFocusListener(AutoRomanListener.getInstance());
            emailField.getDocument().addDocumentListener(dl);
            emailField.addActionListener((ActionEvent e) -> {
                view.getMayakuField().requestFocus();
            });
            
            // 麻薬
            JTextField mayaku = view.getMayakuField();
            mayaku.addFocusListener(AutoRomanListener.getInstance());
            mayaku.getDocument().addDocumentListener(dl);
            mayaku.addActionListener((ActionEvent e) -> {
                view.getUid().requestFocus();
            });
            
            // 医療資格
            licenses = ClientContext.getLicenseModel();
            view.getLicenseCombo().setModel(new DefaultComboBoxModel(licenses));
            
            // 診察科
            depts = ClientContext.getDepartmentModel();
            view.getDeptCombo().setModel(new DefaultComboBoxModel(depts));
            
            ActionListener al = (ActionEvent e) -> {
                addUserEntry();
            };
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            okButton = new JButton(bundle.getString("actionText.add"));
            okButton.addActionListener(al);
            okButton.setEnabled(false);
            cancelButton = new JButton(bundle.getString("actionText.close"));
            cancelButton.addActionListener((ActionEvent e) -> {
                stop();
            });
            
            JPanel btnPanel;
            if (ClientContext.isMac()) {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{cancelButton, okButton});
            } else {
                btnPanel = GUIFactory.createCommandButtonPanel(new JButton[]{okButton, cancelButton});
            }
            
            this.setLayout(new BorderLayout(0, 17));
            this.add(view, BorderLayout.CENTER);
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
            
            String userId = view.getUid().getText().trim();
            String pass = new String(view.getUserPassword1().getPassword());
            UserModel loginUser = Project.getUserModel();
            String facilityId = loginUser.getFacilityModel().getFacilityId();
            String hashPass = HashUtil.MD5(pass);
            
            final UserModel user = new UserModel();
            StringBuilder sb = new StringBuilder(facilityId);
            sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
            sb.append(userId);
            user.setUserId(sb.toString());
            user.setPassword(hashPass);
            user.setSirName(view.getSn().getText().trim());
            user.setGivenName(view.getGivenName().getText().trim());
            user.setCommonName(user.getSirName() + " " + user.getGivenName());
            
            // 施設情報
            // 管理者のものを使用する
            user.setFacilityModel(Project.getUserModel().getFacilityModel());
            
            // 医療資格
            int index = view.getLicenseCombo().getSelectedIndex();
            user.setLicenseModel(licenses[index]);
            
            // 診療科
            index = view.getDeptCombo().getSelectedIndex();
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
            user.setEmail(view.getEmailField().getText().trim());
            
            // 麻薬
            String value = (!view.getMayakuField().getText().trim().equals(""))
                         ? (view.getMayakuField().getText().trim())
                    : null;
            user.setUseDrugId(value);
            
            // Role = user
            RoleModel rm = new RoleModel();
            rm.setRole(usersRole);
            user.addRole(rm);
            rm.setUserModel(user);
            rm.setUserId(user.getUserId()); // 必要
            
            // タスクを実行する
            final UserDelegater udl = new UserDelegater();
            
            worker = new SimpleWorker<Boolean, Void>() {
        
                @Override
                protected Boolean doInBackground() throws Exception {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("addUserEntry doInBackground");
                    int cnt = udl.addUser(user);
                    return true;
                }
                
                @Override
                protected void succeeded(Boolean results) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("addUserEntry succeeded");
                    String msg = ClientContext.getMyBundle(AddUserImpl.class).getString("message.addedUser");
                    JOptionPane.showMessageDialog(getFrame(),
                            msg,
                            ClientContext.getFrameTitle(getName()),
                            JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                protected void cancelled() {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).fine("addUserEntry cancelled");
                }

                @Override
                protected void failed(java.lang.Throwable cause) {
                    String msg = ClientContext.getMyBundle(AddUserImpl.class).getString("warning.dupuricatedID");
                    JOptionPane.showMessageDialog(getFrame(),
                                msg,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.WARNING_MESSAGE);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("addUserEntry failed");
                }

                @Override
                protected void startProgress() {
                    delayCount = 0;
                    taskTimer.start();
                }

                @Override
                protected void stopProgress() {
                    taskTimer.stop();
                    monitor.close();
                    taskTimer = null;
                    monitor = null;
                }
            };

            Component c = getFrame();
            String message = null;
            ResourceBundle bundle = ClientContext.getMyBundle(AddUserImpl.class);
            String note = bundle.getString("task.default.addMessage");
            maxEstimation = Integer.parseInt(bundle.getString("task.default.maxEstimation"));
            delay = Integer.parseInt(bundle.getString("task.default.delay"));
            monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

            taskTimer = new Timer(delay, (ActionEvent e) -> {
                delayCount++;
                
                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);
                    
                } else {
                    monitor.setProgress(delayCount);
                }
            });

            worker.execute();
        }
        
        private boolean userIdOk() {
            
            String userId = view.getUid().getText().trim();
            if (userId.equals("")) {
                return false;
            }
            
            int len = userId.length();
            
            return (len >= userIdLength[0]);           
        }
        
        private boolean passwordOk() {
            
            String passwd1 = new String(view.getUserPassword1().getPassword());
            String passwd2 = new String(view.getUserPassword2().getPassword());
            
            if (passwd1.equals("") || passwd2.equals("")) {
                return false;
            }
           
            if ((passwd1.length() < passwordLength[0]) || (passwd2.length() < passwordLength[0])) {
                return false;
            }
           
            return passwd1.equals(passwd2);
        }
        
        private void checkButton() {
            
            boolean userOk = userIdOk();
            boolean passwordOk = passwordOk();
            boolean snOk = !view.getSn().getText().trim().equals("");
            boolean givenOk = !view.getGivenName().getText().trim().equals("");
            boolean emailOk = !view.getEmailField().getText().trim().equals("");
            
            boolean newOk = (userOk && passwordOk && snOk && givenOk && emailOk);
            
            if (ok != newOk) {
                ok = newOk;
                okButton.setEnabled(ok);
            }
        }
    }
}
