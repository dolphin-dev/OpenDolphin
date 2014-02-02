package open.dolphin.impl.login;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ILoginDialog;
import open.dolphin.client.ProjectSettingDialog;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;

/**
 * ログインダイアログ　クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractLoginDialog implements ILoginDialog {
    
    protected JDialog dialog;
    protected BlockGlass blockGlass;
    
    // 認証制御用
    protected UserDelegater userDlg;
    protected int tryCount;
    protected int maxTryCount;
    protected SimpleWorker worker;
    
    // 認証結果のプロパティ
    protected LoginStatus result;
    protected PropertyChangeSupport boundSupport;

    protected Action loginAction;
    protected Action cancelAction;
    protected Action settingAction;

    /**
     * Creates new LoginService
     */
    public AbstractLoginDialog() {
        boundSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * 認証結果プロパティリスナを登録する。
     * @param listener 登録する認証結果リスナ
     */
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
     * 認証結果プロパティリスナを登録する。
     * @param listener 削除する認証結果リスナ
     */
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.removePropertyChangeListener(prop, listener);
    }

    /**
     * 認証が成功したかどうかを返す。
     * @return true 認証が成功した場合
     */
    public LoginStatus getResult() {
        return result;
    }

    public void setResult(LoginStatus value) {
        this.result = value;
        boundSupport.firePropertyChange("LOGIN_PROP", -100, this.result);
    }

    /**
     * 警告メッセージを表示する。
     * @param msg 表示するメッセージ
     */
    protected void showMessageDialog(String msg) {
        String title = dialog.getTitle();
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * ログイン画面を開始する。
     */
    @Override
    public void start() {
        
        //-------------------------
        // GUI を構築しモデルを表示する
        //-------------------------
        JPanel content = createComponents();

        loginAction = new AbstractAction("ログイン") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tryLogin();
            }
        };
        loginAction.setEnabled(false);
        getLoginButton().setAction(loginAction);

        cancelAction = new AbstractAction("キャンセル") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doCancel();
            }
        };
        getCancelButton().setAction(cancelAction);

        settingAction = new AbstractAction("設 定") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doSetting();
            }
        };
        getSettingButton().setAction(settingAction);
        
        String title = ClientContext.getString("loginDialog.title");
        String windowTitle = ClientContext.getFrameTitle(title);
        dialog = new JDialog((Frame)null, windowTitle, true);
        dialog.setTitle(windowTitle);
        dialog.getRootPane().setDefaultButton(getLoginButton());
        blockGlass = new BlockGlass();
        dialog.setGlassPane(blockGlass);
        dialog.getContentPane().add(content);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
            @Override
            public void windowOpened(WindowEvent e) {
                doWindowOpened();
            }
        });
        
        //-------------------------------------
        // 中央へ表示する。（EDT からコールされている）
        //-------------------------------------
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

    /**
     * ログインダイアログをクローズする。
     */
    @Override
    public void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    /**
     * ログインをキャンセルする。
     */
    public void doCancel() {
        setResult(LoginStatus.CANCELD);
    }

    protected void setBusy(boolean busy) {
        if (busy) {
            blockGlass.block();
            getProgressBar().setIndeterminate(true);
        } else {
            blockGlass.unblock();
            getProgressBar().setIndeterminate(false);
            getProgressBar().setValue(0);
        }
        loginAction.setEnabled(!busy);
        cancelAction.setEnabled(!busy);
        settingAction.setEnabled(!busy);
    }

    protected void showUserIdPasswordError() {
        StringBuilder sb = new StringBuilder();
        sb.append("認証に失敗しました。");
        sb.append("\n");
        sb.append("ユーザーIDまたはパスワードが違います。");
        String msg = sb.toString();
        showMessageDialog(msg);
        ClientContext.getPart11Logger().warn(msg);
    }

    protected void showTryOutError() {
        StringBuilder sb = new StringBuilder();
        sb.append("認証に規定の回数失敗しました。");
        sb.append("\n");
        sb.append("アプリケーションを終了します。");
        String msg = sb.toString();
        showMessageDialog(msg);
        ClientContext.getPart11Logger().warn(msg);
    }
    
    protected abstract void tryLogin();

    protected abstract JPanel createComponents();

    protected abstract void doWindowOpened();

    protected abstract JButton getLoginButton();

    protected abstract JButton getCancelButton();

    protected abstract JButton getSettingButton();

    protected abstract JProgressBar getProgressBar();

    public abstract void setNewParams(Boolean newValue);
    
    
    /**
     * 設定ボタンがおされた時、設定画面を開始する。
     */
    @Override
    public void doSetting() {
        
        blockGlass.block();
        
        ProjectSettingDialog sd = new ProjectSettingDialog();
        PropertyChangeListener pl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                blockGlass.unblock();
                setNewParams((Boolean)evt.getNewValue());
            }
        };
        sd.addPropertyChangeListener("SETTING_PROP", pl);
        sd.setLoginState(false);
        sd.start();
    }
}