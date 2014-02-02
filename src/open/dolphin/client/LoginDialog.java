/*
 * LoginDialog.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
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
import open.dolphin.infomodel.ModelUtils;

import org.apache.log4j.Logger;

import open.dolphin.delegater.UserDelegater;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ログインダイアログ　クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LoginDialog {
    
    /** Login Status */
    public enum LoginStatus {AUTHENTICATED, NOT_AUTHENTICATED, CANCELD};
    
    //
    // GUI Components
    //
    private JDialog dialog;
    private JTextField userIdField;
    private JPasswordField passwdField;
    private JButton settingButton;
    private JButton loginButton;
    private JButton cancelButton;
    private UltraSonicProgressLabel glassPane;
    
    //
    // 認証制御用
    //
    private BlockGlass glass;
    private UserDelegater userDlg;
    private Logger part11Logger;
    private int tryCount;
    private int maxTryCount;
    private javax.swing.Timer taskTimer;
    private LoginTask task;
    
    //
    // 認証結果のプロパティ
    //
    private LoginStatus result;
    private PropertyChangeSupport boundSupport;
    
    //
    // ダイアログモデル
    //
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
        // EDT から表示する
        //
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.pack();
                int width = dialog.getWidth();
                int height = dialog.getHeight();
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int n = ClientContext.isMac() ? 3 : 2;
                int left = (screen.width - width) / 2;
                int top = (screen.height - height) / n;
                dialog.setLocation(left, top);
                dialog.setVisible(true);
            }
        });
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
    private void notifyResult(LoginStatus ret) {
        boundSupport.firePropertyChange("LOGIN_PROP", -100, ret);
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
        String passwd = new String(passwdField.getPassword());
        
        // LoginTask を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int lengthOfTask = maxEstimation / delay;	// タスクの長さ = 最大予想時間 / 割り込み間隔
        
        task = new LoginTask(principal, passwd, userDlg, lengthOfTask);
        
        // TaskTimer を生成する
        taskTimer = new javax.swing.Timer(delay, ProxyActionListener.create(this, "onTimerAction"));
        
        // スタ－トをかける
        setBusy(true);
        task.start();
    }
    
    /**
     * タスクタイマーの割り込みで認証結果を処理する。
     */
    public void onTimerAction() {
        
        if (task.isDone()) {
            
            setBusy(false);
            
            //
            // 認証結果である userModel を取得する
            //
            UserModel userModel = task.getUserModel();
            
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
                            String title = dialog.getTitle();
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
                //
                // 認証が失敗した場合
                //
                if (tryCount <= maxTryCount) {
                    String msg = userDlg.getErrorMessage();
                    part11Logger.warn(msg);
                    showMessageDialog(msg);
                    
                } else {
                    //
                    // 規定回数以上失敗
                    //
                    StringBuilder sb = new StringBuilder();
                    sb.append(userDlg.getErrorMessage());
                    sb.append("\n");
                    sb.append(ClientContext.getString("loginDialog.forceClose"));
                    String msg = sb.toString();
                    part11Logger.warn(msg);
                    showMessageDialog(msg);
                    result = LoginStatus.NOT_AUTHENTICATED;
                    notifyClose(result);
                }
            }
            
        } else if (task.isTimeOver()) {
            //
            // タイムオーバー処理を行う
            //
            setBusy(false);
            new TimeoutWarning(dialog, dialog.getTitle(), null).start();
        }
    }
    
    /**
     * データベースアクセス中の処理を行う。
     */
    private void setBusy(boolean busy) {
        
        if (busy) {
            userIdField.setEnabled(false);
            passwdField.setEnabled(false);
            settingButton.setEnabled(false);
            loginButton.setEnabled(false);
            cancelButton.setEnabled(false);
            glassPane.start();
            taskTimer.start();
            
        } else {
            glassPane.stop();
            taskTimer.stop();
            userIdField.setEnabled(true);
            passwdField.setEnabled(true);
            settingButton.setEnabled(true);
            loginButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }
    
    /**
     * 警告メッセージを表示する。
     * @param msg 表示するメッセージ
     */
    private void showMessageDialog(String msg) {
        String title = dialog.getTitle();
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * ログインダイアログを終了する。
     * @param result
     */
    private void notifyClose(LoginStatus result) {
        dialog.setVisible(false);
        dialog.dispose();
        notifyResult(result);
    }
    
    /**
     * GUI を構築する。
     */
    private void initComponents() {
        
        // Image ラベルを生成する
        JLabel imageLabel = new JLabel(ClientContext.getImageIcon("splash.jpg"));
        
        // ユーザIDフィールドを生成する
        userIdField = GUIFactory.createTextField(10, null, null, null);
        
        // パスワードフィールドを生成する
        passwdField = GUIFactory.createPassField(10, null, null, null);
        
        // 設定ボタンを生成する
        String text = ClientContext.getString("loginDialog.settingButtonText");
        settingButton = new JButton(text);
        
        // Cancelボタンを生成する
        text =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(text);
        
        // Loginボタンを生成する
        text = ClientContext.getString("loginDialog.loginButtonText");
        loginButton = new JButton(text);
        
        //
        // レイアウトをする
        //
        String loginInfoText = ClientContext.getString("loginDialog.loginBorderTitle");
        String userIdText = ClientContext.getString("loginDialog.userIdLabel");
        String passwrdText = ClientContext.getString("loginDialog.passwdLabel");
        GridBagBuilder gbl = new GridBagBuilder(loginInfoText);
        
        int row = 0;
        JLabel label = new JLabel(userIdText, SwingConstants.RIGHT);
        gbl.add(label,          0, row, GridBagConstraints.EAST);
        gbl.add(userIdField,    1, row, GridBagConstraints.WEST);
        
        row++;
        label = new JLabel(passwrdText, SwingConstants.RIGHT);
        gbl.add(label, 		0, row, GridBagConstraints.EAST);
        gbl.add(passwdField,    1, row, GridBagConstraints.WEST);
        
        row++;
        glassPane = new UltraSonicProgressLabel();
        int width = userIdField.getPreferredSize().width;
        int height = glassPane.getPreferredSize().height;
        glassPane.setPreferredSize(new Dimension(width, height));
        gbl.add(new JLabel(""), 0, row, 1, 1, GridBagConstraints.CENTER);
        gbl.add(glassPane, 	1, row, 1, 1, GridBagConstraints.CENTER);
        
        // ID Panelを得る
        JPanel idPanel = gbl.getProduct();
        
        // ボタンパネルを生成する
        JPanel buttonPanel = ClientContext.isMac()
        ? GUIFactory.createCommandButtonPanel(new JButton[]{settingButton,cancelButton,loginButton})
        : GUIFactory.createCommandButtonPanel(new JButton[]{settingButton,loginButton,cancelButton});
        
        // 右側パネルを生成する
        JPanel rightPanel = new JPanel(new BorderLayout(0, 17));
        rightPanel.add(idPanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        //
        // コンテントパネルを生成する
        //
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.add(imageLabel);
        content.add(Box.createHorizontalStrut(11));
        content.add(rightPanel);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        //
        // ダイアログを生成する
        //
        // リソースからWindowタイトルを取得する
        String title = ClientContext.getString("loginDialog.title");
        String windowTitle = ClientContext.getFrameTitle(title);
        
        // Login Dialog を生成し GlassPaneをセットする
        dialog = new JDialog((Frame) null, windowTitle, false);
        glass = new BlockGlass();
        dialog.setGlassPane(glass);
        
        //
        // コンテンツを加える
        //
        content.setOpaque(true);
        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(loginButton);
        
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
        userIdField.getDocument().addDocumentListener(ProxyDocumentListener.create(stateMgr, "checkButtons"));
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addActionListener(ProxyActionListener.create(stateMgr, "onUserIdAction"));
        
        passwdField.getDocument().addDocumentListener(ProxyDocumentListener.create(stateMgr, "checkButtons"));
        passwdField.addFocusListener(AutoRomanListener.getInstance());
        passwdField.addActionListener(ProxyActionListener.create(stateMgr, "onPasswordAction"));
        
        //
        // ボタンに ActionListener を登録する
        //
        settingButton.addActionListener(ProxyActionListener.create(this, "doSettingDialog"));
        cancelButton.addActionListener(ProxyActionListener.create(this, "doCancel"));
        loginButton.addActionListener(ProxyActionListener.create(this, "tryLogin"));
        loginButton.setEnabled(false);
        
        //
        // ダイアログに WindowAdapter を設定する
        //
        dialog.addWindowListener(stateMgr);
    }
    
    /**
     * モデルを表示する。
     */
    private void bindModelToView() {
        
        if (principal.getUserId() != null && (!principal.getUserId().equals(""))) {
            userIdField.setText(principal.getUserId());
        }
    }
    
    /**
     * モデル値を取得する。
     */
    private void bindViewToModel() {
        
        if (!userIdField.getText().trim().equals("")) {
            principal.setUserId(userIdField.getText().trim());
        }
    }
    
    
    /**
     * 設定ボタンがおされた時、設定画面を開始する。
     */
    public void doSettingDialog() {
        
        glass.block();
        
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
        
        glass.unblock();
        
        boolean valid = newValue.booleanValue();
        if (valid) {
            principal.setUserId(Project.getUserId());
            principal.setFacilityId(Project.getFacilityId());
            bindModelToView();
            passwdField.requestFocus();
        }
    }
    
    /**
     * ログインをキャンセルする。
     */
    public void doCancel() {
        dialog.setVisible(false);
        dialog.dispose();
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
            
            boolean userEmpty = userIdField.getText().equals("") ? true : false;
            boolean passwdEmpty = passwdField.getPassword().length == 0 ? true : false;
            
            boolean newOKState = ( (userEmpty == false) && (passwdEmpty == false) ) ? true : false;
            
            if (newOKState != okState) {
                loginButton.setEnabled(newOKState);
                okState = newOKState;
            }
        }
        
        /**
         * UserId フィールドでリターンきーが押された時の処理を行う。
         */
        public void onUserIdAction() {
            passwdField.requestFocus();
        }
        
        /**
         * Password フィールドでリターンきーが押された時の処理を行う。
         */
        public void onPasswordAction() {
            
            if (userIdField.getText().equals("")) {
                
                userIdField.requestFocus();
                
            } else if (passwdField.getPassword().length != 0 && okState) {
                //
                // ログインボタンをクリックする
                //
                loginButton.doClick();
            }
        }
        
        @Override
        public void windowClosing(WindowEvent e) {
            doCancel();
        }
        
        @Override
        public void windowOpened(WindowEvent e) {
            
            if (!userIdField.getText().trim().equals("")) {
                //
                // UserId に有効な値が設定されていれば
                // パスワードフィールドにフォーカスする
                //
                passwdField.requestFocus();
            }
        }
    }
}













