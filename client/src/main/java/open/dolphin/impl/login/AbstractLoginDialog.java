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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.swing.*;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ILoginDialog;
import open.dolphin.delegater.ServerInfoDelegater;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectSettingDialog;
import open.dolphin.utilities.control.RssReaderPane;

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
     * @param prop
     * @param listener 登録する認証結果リスナ
     */
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
     * 認証結果プロパティリスナを登録する。
     * @param prop
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

        //java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractLoginDialog.class);
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractLoginDialog.class);
        loginAction = new AbstractAction(bundle.getString("action.login")) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tryLogin();
            }
        };
        loginAction.setEnabled(false);
        getLoginButton().setAction(loginAction);

        cancelAction = new AbstractAction(bundle.getString("action.cancel")) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doCancel();
            }
        };
        getCancelButton().setAction(cancelAction);
        getCancelButton().setToolTipText(bundle.getString("toolTipText.cancelBtn"));

        settingAction = new AbstractAction(bundle.getString("action.setting")) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doSetting();
            }
        };
        getSettingButton().setAction(settingAction);
        getSettingButton().setToolTipText(bundle.getString("toolTipText.settingBtn"));
        
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
        String msg = ClientContext.getMyBundle(AbstractLoginDialog.class).getString("error.failedToLogin");
        showMessageDialog(msg);
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(msg);
    }

    protected void showTryOutError() {
        String msg = ClientContext.getMyBundle(AbstractLoginDialog.class).getString("error.tryout");
        showMessageDialog(msg);
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(msg);
    }
    
    protected void showTestExpiredError() {
        String msg = ClientContext.getMyBundle(AbstractLoginDialog.class).getString("error.endEvalPeriod");
        showMessageDialog(msg);
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(msg);
    }
    
    protected boolean isTestUser(UserModel user) {
        //boolean test = ClientContext.is5mTest();
        boolean test = Project.isTester();
        test = test && user.getMemberType().equals("ASP_TESTER");
        return test;
    }
    
    protected boolean isExpired(UserModel user) {
        
        // 登録日を取得する
        Date registered = user.getRegisteredDate();
        
        // テスト期間を取得する 単位は月数
        int testPeriod = ClientContext.getInt("loginDialog.asp.testPeriod");
        
        // 登録日にテスト期間を加える
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(registered);
        gc.add(Calendar.MONTH, testPeriod);
        
        // 今日のを取得する
        GregorianCalendar today = new GregorianCalendar();
        
        // gc が今日以前?
        return gc.before(today);
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
        PropertyChangeListener pl = (PropertyChangeEvent evt) -> {
            blockGlass.unblock();
            setNewParams((Boolean)evt.getNewValue());
        };
        sd.addPropertyChangeListener("SETTING_PROP", pl);
        sd.setLoginState(false);
        sd.start();
    }
    
//s.oh^ RSS対応
    protected void showRSSInfo() {
        String rss;
        //"http://www.lscc.co.jp/rss/rss_dolphin.xml";
        rss = Project.getString("dolphin.rss");
        if(rss == null || rss.length() <= 0) {
            return;
        }
        
        RssReaderPane rssPane = new RssReaderPane();
        
        JDialog dlg = new JDialog(new JFrame(), "Dolphin RSS", true);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //dialog.getRootPane().setDefaultButton(done);
        //dialog.setPreferredSize(new Dimension(500, 500));
        
        dlg.setContentPane(rssPane.createRssPane(rss));
        dlg.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int x = (screen.width - dlg.getPreferredSize().width) / 2;
        int y = (screen.height - dlg.getPreferredSize().height) / n;
        dlg.setLocation(x, y);
        dlg.setVisible(true);
    }
//s.oh$
    
//s.oh^ 2014/03/13 傷病名削除診療科対応
    protected void getOrcaDeptInfo() {
//        //OrcaDelegater sdl = OrcaDelegaterFactory.create();
//        OrcaSqlDelegater sdl = new OrcaSqlDelegater();
//        ArrayList<String> list = new ArrayList<>();
//        try {
//            ArrayList<String> tmps = sdl.getDeptInfo();
//            boolean check = false;
//            for(String tmp : tmps) {
//                if(tmp.equals("00")) {
//                    check = true;
//                    break;
//                }
//            }
//            if(check) {
//                for(int i = 1; i <= 50; i++) {
//                    String code = String.format("%1$02d", i);
//                    for(int j = 0; j < tmps.size(); j++) {
//                        String name = tmps.get(j);
//                        if(name.equals(code) && (j + 1) < tmps.size()) {
//                            j = j + 1;
//                            list.add(code + ":" + tmps.get(j));
//                            Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "ORCA診療科情報：", code + ":" + tmps.get(j));
//                            break;
//                        }
//                    }
//                }
//                Project.setDeptInfo(list);
//                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "ORCAの診療科情報取得成功");
//            }else{
//                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "ORCAの診療科情報取得失敗");
//                for(DepartmentModel dm : ClientContext.getDepartmentModel()) {
//                    list.add(dm.getDepartment() + ":" + dm.getDepartmentDesc());
//                    Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "ORCA診療科情報：", dm.getDepartment() + ":" + dm.getDepartmentDesc());
//                }
//                Project.setDeptInfo(list);
//                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "デフォルト値から診療科を取得");
//            }
//        } catch (Exception ex) {
//            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "ORCAの診療科情報取得失敗", ex.getMessage());
//            for(DepartmentModel dm : ClientContext.getDepartmentModel()) {
//                list.add(dm.getDepartment() + ":" + dm.getDepartmentDesc());
//                Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "ORCA診療科情報：", dm.getDepartment() + ":" + dm.getDepartmentDesc());
//            }
//            Project.setDeptInfo(list);
//            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "デフォルト値から診療科を取得");
//        }
    }
//s.oh$
    
//s.oh^ 2014/07/08 クラウド0対応
    protected boolean checkCloudZero() {
        if(isCloudZero()) {
            int ret;
            UserDelegater ud = new UserDelegater();
            String uuid = Project.getString("dolphin.license");
            if(uuid == null) {
                uuid = UUID.randomUUID().toString();
                uuid = uuid.replaceAll("-", "");
                SimpleDateFormat effectiveFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                uuid = effectiveFormat.format(new Date()) + uuid;
            }
            try {
                ret = ud.checkLicense(uuid);
            } catch (Exception ex) {
                ret = 1;
            }
            if(ret == 0) {
                Project.setString("dolphin.license", uuid);
                Project.setCloudZero(true);
            }else{
                String msg;
                java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractLoginDialog.class);
                if(ret == 1) {
                    msg = bundle.getString("error.communication");
                }else if(ret == 2) {
                    msg = bundle.getString("error.licenceFile");
                }else if(ret == 3) {
                    msg = bundle.getString("error.licenceFile");
                }else if(ret == 4) {
                    msg = bundle.getString("error.overLicensedNumber");
                }else{
                    msg = "";
                }
                JOptionPane.showMessageDialog(null, msg, bundle.getString("error.licenseAuthentication"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isCloudZero() {
        boolean ret;
        
        ServerInfoDelegater sid = new ServerInfoDelegater();
        try {
            ret = sid.isCloudZero();
        } catch (Exception ex) {
            ret = false;
        }
        
        return ret;
    }
//s.oh$
}