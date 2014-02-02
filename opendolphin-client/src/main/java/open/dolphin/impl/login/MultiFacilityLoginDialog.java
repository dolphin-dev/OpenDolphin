package open.dolphin.impl.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * ログインダイアログ　クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MultiFacilityLoginDialog extends AbstractLoginDialog {
    
    private LoginPanelFacility view;
    private StateMgr stateMgr;

    
    /** Creates new LoginService */
    public MultiFacilityLoginDialog() {
    }
    
    @Override
    protected void tryLogin() {
        
        // User 情報を取得するためのデリゲータを得る
        if (userDlg == null) {
            userDlg = new UserDelegater();
        }
        
        // トライ出来る最大回数を得る
        if (maxTryCount == 0) {
            maxTryCount = ClientContext.getInt("loginDialog.maxTryCount");
        }
        
        ClientContext.getPart11Logger().info("認証を開始します");
        
        // 試行回数 += 1
        tryCount++;

        //Task
        worker = new SimpleWorker<UserModel, Void>() {

            @Override
            protected UserModel doInBackground() throws Exception {
                LoginSet set = (LoginSet)view.getFacilityCmb().getSelectedItem();
                Project.setString(Project.FACILITY_NAME, set.getName());
                Project.setString(Project.FACILITY_ID, set.getFacilityId());
                Project.setString(Project.SERVER_URI, set.getBaseURI());
                Project.setString(Project.JMARI_CODE, "JPN"+set.getJmariCode());
                Project.setString(Project.CLAIM_ADDRESS, set.getClaimAddress());
                Project.setInt(Project.CLAIM_PORT, set.getClaimPort());
                String fid = Project.getFacilityId();
                String uid = view.getUserIdField().getText().trim();
                String password = new String(view.getPasswordField().getPassword());
                UserModel userModel = userDlg.login(fid, uid, password);
                return userModel;
            }
            
            @Override
            protected void succeeded(UserModel userModel) {
                if (userModel != null) {   
                    // 認証成功
                    String time = ModelUtils.getDateTimeAsString(new Date());
                    StringBuilder sb = new StringBuilder();
                    sb.append(time).append(":");
                    sb.append(userModel.getUserId()).append(" がログインしました");
                    ClientContext.getPart11Logger().info(sb.toString());

                    //----------------------------------
                    // ユーザモデルを ProjectStub へ保存する
                    //----------------------------------
                    Project.getProjectStub().setUserModel(userModel);
                    Project.getProjectStub().setFacilityId(userModel.getFacilityModel().getFacilityId());
                    Project.getProjectStub().setUserId(userModel.idAsLocal());  // facilityId無し

                    setResult(LoginStatus.AUTHENTICATED);
                     
                } else {
                    if (tryCount <= maxTryCount) {
                        showUserIdPasswordError();

                    } else {
                        showTryOutError();
                        setResult(LoginStatus.NOT_AUTHENTICATED);
                    }
                }
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                ClientContext.getPart11Logger().warn("Task failed");
                ClientContext.getPart11Logger().warn(cause.getCause());
                ClientContext.getPart11Logger().warn(cause.getMessage());

                if (tryCount <= maxTryCount) {
                    showMessageDialog(cause.getMessage());

                } else {
                    showTryOutError();
                    setResult(LoginStatus.NOT_AUTHENTICATED);
                }
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    setBusy(false);
                    worker.removePropertyChangeListener(this);
                } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                    setBusy(true);
                }
            }  
        });
        
        worker.execute();
    }
    
    /**
     * GUI を構築する。
     */
    @Override
    protected JPanel createComponents() {

        String names = Project.getString("login.set.facility.name");
        String fids = Project.getString("login.set.facility.id");
        String uris = Project.getString("login.set.base.uri");
        String jmaris = Project.getString("login.set.jmari.code");
        String orcas = Project.getString("login.set.orca.address");
        String ports = Project.getString("login.set.orca.port");
        DefaultComboBoxModel cmbModel;
        
        String[] name = names.split("\\s*,\\s*");
        String[] fid = fids.split("\\s*,\\s*");
        String[] uri = uris.split("\\s*,\\s*");
        String[] jmari = jmaris.split("\\s*,\\s*");
        String[] orca = orcas.split("\\s*,\\s*");
        String[] port = ports.split("\\s*,\\s*");
        int cnt = name.length;
        LoginSet[] data = new LoginSet[cnt];

        for (int i = 0; i< cnt; i++) {
            LoginSet ls = new LoginSet();
            ls.setName(name[i]);
            ls.setFacilityId(fid[i]);
            ls.setBaseURI(uri[i]);
            ls.setJmariCode(jmari[i]);
            ls.setClaimAddress(orca[i]);
            ls.setClaimPort(Integer.parseInt(port[i]));
            data[i] =ls;
        }

        cmbModel = new DefaultComboBoxModel(data);

        view = new LoginPanelFacility();
        view.getFacilityCmb().setModel(cmbModel);

        // イベント接続を行う
        connect();

        return view;
    }
    
    /**
     * イベント接続を行う。
     */
    private void connect() {
        
        // Mediator ライクな StateMgr
        stateMgr = new StateMgr();
        
        // フィールドにリスナを登録する
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }
        };

        view.getFacilityCmb().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    stateMgr.onFacilityAction();
                }
            }
        });

        JTextField userIdField = view.getUserIdField();
        userIdField.getDocument().addDocumentListener(dl);
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.onUserIdAction();
            }
        });
        
        JPasswordField passwdField = view.getPasswordField();
        passwdField.getDocument().addDocumentListener(dl);
        passwdField.addFocusListener(AutoRomanListener.getInstance());
        passwdField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.onPasswordAction();
            }
        });
    }

    @Override
    protected JButton getLoginButton() {
        return view.getLoginBtn();
    }

    @Override
    protected JButton getCancelButton() {
        return view.getCancelBtn();
    }

    @Override
    protected JButton getSettingButton() {
        return view.getSettingBtn();
    }

    @Override
    protected JProgressBar getProgressBar() {
        return view.getProgressBar();
    }
    
    /**
     * モデルを表示する。
     */
    @Override
    protected void doWindowOpened() {
        String name = Project.getString(Project.FACILITY_NAME);
        String uid = Project.getUserId();

        if (name!=null) {
            int cnt = view.getFacilityCmb().getItemCount();
            for (int i=0; i <cnt; i++) {
                LoginSet set = (LoginSet)view.getFacilityCmb().getItemAt(i);
                if (set.getName().equals(name)) {
                    view.getFacilityCmb().setSelectedIndex(i);
                    if (uid==null || uid.equals("")) {
                        view.getUserIdField().requestFocus();
                    } else {
                        view.getUserIdField().setText(uid);
                        view.getPasswordField().requestFocus();
                    }
                }
            }
        } else if (uid != null && (!uid.equals(""))) {
            view.getUserIdField().setText(uid);
            view.getFacilityCmb().requestFocus();
        }
    }
    
    /**
     * 設定ダイアログから通知を受ける。
     * 有効なプロジェクトでればユーザIDをフィールドに設定しパスワードフィールドにフォーカスする。
     **/
    @Override
    public void setNewParams(Boolean newValue) {
        boolean valid = newValue.booleanValue();
        if (valid) {
            doWindowOpened();
        }
    }
    
    /**
     * ログインボタンを制御する簡易 StateMgr クラス。
     */
    class StateMgr  {
        
        private boolean okState;
        
        public StateMgr() {
        }
        
        /**
         * ログインボタンの enable/disable を制御する。
         */
        public void checkButtons() {
            boolean newOKState = true;
            newOKState = newOKState &&  (!view.getUserIdField().getText().equals(""));
            newOKState = newOKState && (view.getPasswordField().getPassword().length >0);
            
            if (newOKState != okState) {
                view.getLoginBtn().setEnabled(newOKState);
                okState = newOKState;
            }
        }

        public void onFacilityAction() {
            if (view.getUserIdField().getText().equals("")) {
                view.getUserIdField().requestFocus();
            } else {
                view.getPasswordField().requestFocus();
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
                view.getLoginBtn().doClick();
            }
        }
    }
}