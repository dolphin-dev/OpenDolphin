package open.dolphin.client;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import open.dolphin.infomodel.ModelUtils;

import org.apache.log4j.Logger;

import open.dolphin.delegater.UserDelegater;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * ログインダイアログ　クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LoginDialog {
    
    /** Login Status */
    public enum LoginStatus {AUTHENTICATED, NOT_AUTHENTICATED, CANCELD};
    
    private LoginView view;
    private BlockGlass blockGlass;
    
    // 認証制御用
    private UserDelegater userDlg;
    private Logger part11Logger;
    private int tryCount;
    private int maxTryCount;
    
    // 認証結果のプロパティ
    private LoginStatus result;
    private PropertyChangeSupport boundSupport;
    
    // モデル
    private DolphinPrincipal principal;
    
    // StateMgr
    private StateMgr stateMgr;
    
    
    /**
     * Creates new LoginService
     */
    public LoginDialog() {
    }
    
    /**
     * 認証結果プロパティリスナを登録する。
     * @param listener 登録する認証結果リスナ
     */
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
     * 認証結果プロパティリスナを登録する。
     * @param listener 削除する認証結果リスナ
     */
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
     * ログイン画面を開始する。
     */
    public void start() {
        
        //
        // ダイアログモデルを生成し値を初期化する
        //
        principal = new DolphinPrincipal();
        if (Project.isValid()) {
            principal.setFacilityId(Project.getFacilityId());
            principal.setUserId(Project.getUserId());
        }
        
        //
        // GUI を構築しモデルを表示する
        //
        initComponents();
        bindModelToView();
        
        //
        // EDT からコールされている
        //
        int width = view.getWidth();
        int height = view.getHeight();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int left = (screen.width - width) / 2;
        int top = (screen.height - height) / n;
        view.setLocation(left, top);
        view.setVisible(true);
    }
    
    /**
     * 認証が成功したかどうかを返す。
     * @return true 認証が成功した場合
     */
    public LoginStatus getResult() {
        return result;
    }
    
    /**
     * PropertyChange で結果を受け取るアプリに通知する。
     * @param result true 認証が成功した場合
     */
    private void notifyResult(final LoginStatus ret) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               boundSupport.firePropertyChange("LOGIN_PROP", -100, ret);
            }
        });
    }
    
    /**
     * 認証を試みる。
     * JBoss の DatabaseLoginModule を使用しているため、UserValueが取得できた場合に認証が成功したとみなす。
     * 詳細はBusiness Delegater へ委譲。
     */
    public void tryLogin() {
        
        // User 情報を取得するためのデリゲータを得る
        if (userDlg == null) {
            userDlg = new UserDelegater();
        }
        
        // Part11 ロガーを取得する
        if (part11Logger == null) {
            part11Logger = ClientContext.getLogger("part11");
        }
        
        // トライ出来る最大回数を得る
        if (maxTryCount == 0) {
            maxTryCount = ClientContext.getInt("loginDialog.maxTryCount");
        }
        
        part11Logger.info("認証を開始します");
        
        // 試行回数 += 1
        tryCount++;
        
        // userIdとpasswordを取得する
        bindViewToModel();
        final String password = new String(view.getPasswordField().getPassword());
        
        // LoginTask を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int lengthOfTask = maxEstimation / delay;	// タスクの長さ = 最大予想時間 / 割り込み間隔
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        
        Task task = new Task<UserModel, Void>(app) {

            @Override
            protected UserModel doInBackground() throws Exception {
                UserModel userModel = userDlg.login(principal, password);
                return userModel;
            }
            
            @Override
            protected void succeeded(UserModel userModel) {
                part11Logger.debug("Task succeeded");
                if (userModel != null) {
                    //
                    // Member の有効期間をチェックする
                    //
                    Project.UserType userType = Project.UserType.valueOf(userModel.getMemberType());
                    part11Logger.info("User Type = " + userType.toString());

                    if (userType.equals(Project.UserType.ASP_TESTER)) {

                        // 登録日を取得する
                        Date registered = userModel.getRegisteredDate();

                        // テスト期間を取得する 単位は月数
                        int testPeriod = ClientContext.getInt("loginDialog.asp.testPeriod");

                        // 登録日にテスト期間を加える
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(registered);
                        gc.add(Calendar.MONTH, testPeriod);

                        // ログ
                        part11Logger.info("登録日: " + ModelUtils.getDateAsString(registered));
                        part11Logger.info("有効期限: " + ModelUtils.getDateAsString(gc.getTime()));

                        // 今日のを取得する
                        GregorianCalendar today = new GregorianCalendar();

                        // gcが今日以前の時は有効期限切れ
                        if (gc.before(today)) {
                            String evalOut = ClientContext.getString("loginDialog.asp.evalout.msg");
                            part11Logger.warn(evalOut);
                            showMessageDialog(evalOut);
                            result = LoginStatus.NOT_AUTHENTICATED;
                            notifyClose(result);
                            return;

                        } else {
                            // 残りの日数を計算する
                            // 7日以内の時メッセージを表示する
                            int days = 0;
                            int warningDays = ClientContext.getInt("loginDialog.asp.warning.days");
                            while (today.before(gc)) {
                                days++;
                                if (days > warningDays) {
                                    break;
                                }
                                today.add(Calendar.DAY_OF_MONTH, 1);
                            }

                            //if (days <= warningDays)  {
                            if (days <= warningDays) {
                                //if (days <= warningDays && ((days % 2) != 0) )  {
                                part11Logger.info("残り " + days + " 日");
                                String title = view.getTitle();
                                String msg1 = "評価期間は残り " + days + " 日です。";
                                String msg2 = "継続してご使用の場合はサポートライセンスのご購入をお願いします。";
                                Object obj = new String[]{msg1, msg2};
                                JOptionPane.showMessageDialog(null, obj, title, JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }

                    // 認証成功
                    String time = ModelUtils.getDateTimeAsString(new Date());
                    part11Logger.info(time + ": " + userModel.getUserId() + " がログインしました");

                    // ユーザID、施設ID、ユーザモデルを rojectStub へ保存する
                    Project.getProjectStub().setUserId(principal.getUserId());
                    Project.getProjectStub().setUserModel(userModel);
                    Project.getProjectStub().setDolphinPrincipal(principal);

                    result = LoginStatus.AUTHENTICATED;
                    notifyClose(result);
                     
                } else {
                    part11Logger.warn("User == null, this never ocuured");
                }
            }
            
            @Override
            protected void cancelled() {
                part11Logger.debug("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                part11Logger.warn("Task failed");
                part11Logger.warn(cause.getCause());
                part11Logger.warn(cause.getMessage());
                if (tryCount <= maxTryCount && cause instanceof Exception) {
                    userDlg.processError((Exception) cause);
                    String errMsg = userDlg.getErrorMessage();
                    showMessageDialog(errMsg);
                } else {
                    StringBuilder sb = new StringBuilder();
                    userDlg.processError((Exception) cause);
                    sb.append(userDlg.getErrorMessage());
                    sb.append("\n");
                    sb.append(ClientContext.getString("loginDialog.forceClose"));
                    String msg = sb.toString();
                    showMessageDialog(msg);
                    result = LoginStatus.NOT_AUTHENTICATED;
                    notifyClose(result);
                }
            }
            
            @Override
            protected void interrupted(java.lang.InterruptedException e) {
                part11Logger.warn("Task interrupted");
                part11Logger.warn(e.getMessage());
            }
        };
        
        final TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        taskMonitor.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {
                
                String propertyName = e.getPropertyName();
                part11Logger.info("propertyName = " + propertyName);

                if ("started".equals(propertyName)) {
                    //part11Logger.info("login task started");
                    setBusy(true);

                } else if ("done".equals(propertyName)) {
                    //part11Logger.info("login task done");
                    setBusy(false);
                    taskMonitor.removePropertyChangeListener(this);
                }
            }  
        });
        
        appCtx.getTaskService().execute(task);
        
    }
    
    /**
     * データベースアクセス中の処理を行う。
     */
    private void setBusy(boolean busy) {
        
        if (busy) {
            blockGlass.block();
            view.getUserIdField().setEnabled(false);
            view.getPasswordField().setEnabled(false);
            view.getSettingBtn().setEnabled(false);
            view.getLoginBtn().setEnabled(false);
            view.getCancelBtn().setEnabled(false);
            view.getProgressBar().setIndeterminate(true);
            
        } else {
            view.getProgressBar().setIndeterminate(true);
            view.getProgressBar().setValue(0);
            view.getUserIdField().setEnabled(true);
            view.getPasswordField().setEnabled(true);
            view.getSettingBtn().setEnabled(true);
            view.getLoginBtn().setEnabled(true);
            view.getCancelBtn().setEnabled(true);
            blockGlass.unblock();
        }
    }
    
    /**
     * 警告メッセージを表示する。
     * @param msg 表示するメッセージ
     */
    private void showMessageDialog(String msg) {
        String title = view.getTitle();
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * ログインダイアログを終了する。
     * @param result
     */
    private void notifyClose(LoginStatus result) {
        view.setVisible(false);
        view.dispose();
        notifyResult(result);
    }
    
    /**
     * GUI を構築する。
     */
    private void initComponents() {
        
        String title = ClientContext.getString("loginDialog.title");
        String windowTitle = ClientContext.getFrameTitle(title);
        
        view = new LoginView((Frame) null, false);
        view.setTitle(windowTitle);
        view.getRootPane().setDefaultButton(view.getLoginBtn());
        blockGlass = new BlockGlass();
        view.setGlassPane(blockGlass);
        //
        // イベント接続を行う
        //
        connect();
    }
    
    /**
     * イベント接続を行う。
     */
    private void connect() {
        
        //
        // Mediator ライクな StateMgr
        //
        stateMgr = new StateMgr();
        
        //
        // フィールドにリスナを登録する
        //
        JTextField userIdField = view.getUserIdField();
        userIdField.getDocument().addDocumentListener(ProxyDocumentListener.create(stateMgr, "checkButtons"));
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addActionListener(ProxyActionListener.create(stateMgr, "onUserIdAction"));
        
        JPasswordField passwdField = view.getPasswordField();
        passwdField.getDocument().addDocumentListener(ProxyDocumentListener.create(stateMgr, "checkButtons"));
        passwdField.addFocusListener(AutoRomanListener.getInstance());
        passwdField.addActionListener(ProxyActionListener.create(stateMgr, "onPasswordAction"));
        
        //
        // ボタンに ActionListener を登録する
        //
        view.getSettingBtn().addActionListener(ProxyActionListener.create(this, "doSettingDialog"));
        view.getCancelBtn().addActionListener(ProxyActionListener.create(this, "doCancel"));
        view.getLoginBtn().addActionListener(ProxyActionListener.create(this, "tryLogin"));
        view.getLoginBtn().setEnabled(false);
        
        //
        // ダイアログに WindowAdapter を設定する
        //
        view.addWindowListener(stateMgr);
    }
    
    /**
     * モデルを表示する。
     */
    private void bindModelToView() {
        
        if (principal.getUserId() != null && (!principal.getUserId().equals(""))) {
            view.getUserIdField().setText(principal.getUserId());
        }
    }
    
    /**
     * モデル値を取得する。
     */
    private void bindViewToModel() {
        
        String id = view.getUserIdField().getText().trim();
        
        if (!id.equals("")) {
            principal.setUserId(id);
        }
    }
    
    
    /**
     * 設定ボタンがおされた時、設定画面を開始する。
     */
    public void doSettingDialog() {
        
        blockGlass.block();
        
        ProjectSettingDialog sd = new ProjectSettingDialog();
        PropertyChangeListener pl = ProxyPropertyChangeListener.create(this, "setNewParams", new Class[]{Boolean.class});
        sd.addPropertyChangeListener("SETTING_PROP", pl);
        sd.setLoginState(false);
        sd.start();
    }
    
    /**
     * 設定ダイアログから通知を受ける。
     * 有効なプロジェクトでればユーザIDをフィールドに設定しパスワードフィールドにフォーカスする。
     **/
    public void setNewParams(Boolean newValue) {
        
        blockGlass.unblock();
        
        boolean valid = newValue.booleanValue();
        if (valid) {
            principal.setUserId(Project.getUserId());
            principal.setFacilityId(Project.getFacilityId());
            bindModelToView();
            view.getPasswordField().requestFocus();
        }
    }
    
    /**
     * ログインをキャンセルする。
     */
    public void doCancel() {
        view.setVisible(false);
        view.dispose();
        result = LoginStatus.CANCELD;
        notifyResult(result);
    }
    
    /**
     * ログインボタンを制御する簡易 StateMgr クラス。
     */
    class StateMgr extends WindowAdapter {
        
        private boolean okState;
        
        public StateMgr() {
        }
        
        /**
         * ログインボタンの enable/disable を制御する。
         */
        public void checkButtons() {
            
            boolean userEmpty = view.getUserIdField().getText().equals("") ? true : false;
            boolean passwdEmpty = view.getPasswordField().getPassword().length == 0 ? true : false;
            
            boolean newOKState = ( (userEmpty == false) && (passwdEmpty == false) ) ? true : false;
            
            if (newOKState != okState) {
                view.getLoginBtn().setEnabled(newOKState);
                okState = newOKState;
            }
        }
        
        /**
         * UserId フィールドでリターンきーが押された時の処理を行う。
         */
        public void onUserIdAction() {
            view.getPasswordField().requestFocus();
        }
        
        /**
         * Password フィールドでリターンきーが押された時の処理を行う。
         */
        public void onPasswordAction() {
            
            if (view.getUserIdField().getText().equals("")) {
                
                view.getUserIdField().requestFocus();
                
            } else if (view.getPasswordField().getPassword().length != 0 && okState) {
                //
                // ログインボタンをクリックする
                //
                view.getLoginBtn().doClick();
            }
        }
        
        @Override
        public void windowClosing(WindowEvent e) {
            doCancel();
        }
        
        @Override
        public void windowOpened(WindowEvent e) {
            
            if (!view.getUserIdField().getText().trim().equals("")) {
                //
                // UserId に有効な値が設定されていれば
                // パスワードフィールドにフォーカスする
                //
                view.getPasswordField().requestFocus();
            }
        }
    }
}













