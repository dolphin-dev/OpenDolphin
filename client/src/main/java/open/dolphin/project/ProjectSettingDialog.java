package open.dolphin.project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.plugin.PluginLoader;

/**
 * 環境設定ダイアログ。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ProjectSettingDialog implements PropertyChangeListener {

    // GUI
    private JDialog dialog;
    private JPanel itemPanel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton okButton;
    private JButton cancelButton;
    //
    // 全体のモデル
    //
    private HashMap<String, AbstractPropertySheet> settingMap;
    private ArrayList<AbstractPropertySheet> allSettings;
    private ArrayList<JToggleButton> allBtns;
    private String startSettingName;
    private boolean loginState;
    private PropertyChangeSupport boundSupport;
    private static final String SETTING_PROP = "SETTING_PROP";
    private boolean okState;

    /**
     * Creates new ProjectSettingDialog
     */
    public ProjectSettingDialog() {
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public boolean getLoginState() {
        return loginState;
    }

    public void setLoginState(boolean b) {
        loginState = b;
    }

    public boolean getValue() {
        return Project.getProjectStub().isValid();
    }

    public void notifyResult() {
        if (boundSupport!=null) {
            boolean valid = Project.getProjectStub().isValid();
            boundSupport.firePropertyChange(SETTING_PROP, !valid, valid);
        }
    }

    /**
     * オープン時に表示する設定画面をセットする。
     * @param startSettingName
     */
    public void setProject(String startSettingName) {
        this.startSettingName = startSettingName;
    }

    /**
     * 設定画面を開始する。
     */
    public void start() {

        Runnable r = () -> {
            // モデルを得る
            // 全ての設定プラグイン(Reference)を得、リストに格納する
            allSettings = new ArrayList<>();
            PluginLoader<AbstractPropertySheet> loader = PluginLoader.load(AbstractPropertySheet.class);
            Iterator<AbstractPropertySheet> iter = loader.iterator();
            while (iter.hasNext()) {
                AbstractPropertySheet setting = iter.next();
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine(setting.getClass().getName());
                allSettings.add(setting);
            }
            
            // 設定パネル(AbstractSettingPanel)を格納する Hashtableを生成する
            // key=設定プラグインの名前 value=設定プラグイン
            settingMap = new HashMap<>();
            
            // GUI を構築しモデルをバインドする
            initComponents();
            
            // オープン時に表示する設定画面を決定する
            int index = 0;
            
            if (startSettingName != null) {
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("startSettingName = " + startSettingName);
                for (AbstractPropertySheet setting : allSettings) {
                    if (startSettingName.equals(setting.getId())) {
                        java.util.logging.Logger.getLogger(this.getClass().getName()).fine("found index " + index);
                        break;
                    }
                    index++;
                }
            }
            
            index = (index >= 0 && index < allSettings.size()) ? index : 0;
            
            // Click the toggle button
            allBtns.get(index).doClick();
        };

        SwingUtilities.invokeLater(r);
    }

    /**
     * Setup the user interface
     */
    private void initComponents() {

        // Panel contains all toggle buttons
        itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 設定プラグインを起動するためのトグルボタンを生成し
        // パネルへ加える
        allBtns = new ArrayList<>();
        ButtonGroup bg = new ButtonGroup();
        for (AbstractPropertySheet setting : allSettings) {
            String id = setting.getId();
            String text = setting.getTitle();
            String iconStr = setting.getIcon();
            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.FINE, "id = {0}", id);
            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.FINE, "text = {0}", text);
            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.FINE, "icon = {0}", iconStr);           
            ImageIcon icon = ClientContext.getImageIconArias(iconStr);            
            JToggleButton tb = new JToggleButton(text, icon);
            tb.setHorizontalTextPosition(SwingConstants.CENTER);
            tb.setVerticalTextPosition(SwingConstants.BOTTOM);
            itemPanel.add(tb);
            bg.add(tb);
            tb.setActionCommand(id);    // button の actionCommand=id
            allBtns.add(tb);
        }

        // 設定パネルのコンテナとなるカードパネル
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(ProjectSettingDialog.class);

        // コマンドボタン
        String text = bundle.getString("optionText.save");
        okButton = GUIFactory.createButton(text, null, null);
        okButton.setEnabled(false);

        // Cancel     
        text = GUIFactory.getCancelButtonText();       
        cancelButton = GUIFactory.createButton(text, "C", null);

        // 全体ダイアログのコンテントパネル
        JPanel panel = new JPanel(new BorderLayout(11, 0));
        panel.add(itemPanel, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        // ダイアログを生成する
        String title = bundle.getString("title.settingWindow");
        Object[] options = new Object[]{okButton, cancelButton};

        JOptionPane jop = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okButton);

        dialog = jop.createDialog((Frame) null, ClientContext.getFrameTitle(title));
        dialog.setResizable(true);
        java.util.logging.Logger.getLogger(this.getClass().getName()).fine("dialog created");

        // イベント接続を行う
        connect();
    }

    /**
     * GUI コンポーネントのイベント接続を行う。
     */
    private void connect() {

        // 設定項目ボタンに追加するアクションリスナを生成する
        ActionListener al = (ActionEvent event) -> {
            AbstractPropertySheet theSetting = null;
            
            // Action Command に設定パネルのIDが設定してある
            String name = event.getActionCommand();
            
            for (AbstractPropertySheet setting : allSettings) {
                String id = setting.getId();
                if (id.equals(name)) {
                    theSetting = setting;
                    break;
                }
            }
            
            // ボタンに対応する設定パネルにスタートをかける
            if (theSetting != null) {
                startSetting(theSetting);
            }
        };

        // 全てのボタンにリスナを追加する
        for (JToggleButton btn : allBtns) {
            btn.addActionListener(al);
        }
        
        //Server-ORCA連携^
        // ログインした状態では baseURI とレセコンの設定は不可とする
        allBtns.get(0).setEnabled(!getLoginState());
        allBtns.get(1).setEnabled(!getLoginState());
        //Server-ORCA連携$

        // Save
        okButton.addActionListener((ActionEvent e) -> {
            doOk();
        });
        okButton.setEnabled(false);

        // Cancel
        cancelButton.addActionListener((ActionEvent e) -> {
            doCancel();
        });

        // Dialog
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        });
    }

    /**
     * 選択された項目(SettingPanel)の編集を開始する.
     */
    private void startSetting(final AbstractPropertySheet sp) {

        // 既に生成されている場合はそれを表示する
        if (sp.getContext() != null) {
            cardLayout.show(cardPanel, sp.getTitle());
            return;
        }

        Runnable r = () -> {
            // まだ生成されていない場合は
            // 選択された設定パネルを生成しカードに追加する
            try {
                settingMap.put(sp.getId(), sp);
                sp.setContext(ProjectSettingDialog.this);
                sp.setProjectStub(Project.getProjectStub());
                sp.start();
                
                SwingUtilities.invokeLater(() -> {
                    cardPanel.add(sp.getUI(), sp.getTitle());
                    cardLayout.show(cardPanel, sp.getTitle());
                    dialog.validate();
                    dialog.pack();
                    
                    if (!dialog.isVisible()) {
                        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
                        int x = (size.width - dialog.getPreferredSize().width) / 2;
                        int y = (size.height - dialog.getPreferredSize().height) / 3;
                        dialog.setLocation(x, y);
                        dialog.setVisible(true);
                    } else {
                        dialog.repaint();
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        };

        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * SettingPanel の state が変化した場合に通知を受け、
     * 全てのカードをスキャンして OK ボタンをコントロールする。
     * @param e
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();
        if (!prop.equals(AbstractPropertySheet.STATE_PROP)) {
            return;
        }

        // 全てのカードをスキャンして OK ボタンをコントロールする
        boolean newOk = true;
        Iterator<AbstractPropertySheet> iter = settingMap.values().iterator();
        int cnt = 0;
        while (iter.hasNext()) {
            cnt++;
            AbstractPropertySheet p = iter.next();
            if (p.getState().equals(AbstractPropertySheet.State.INVALID_STATE)) {
                newOk = false;
                break;
            }
        }

        if (okState != newOk) {
            okState = newOk;
            okButton.setEnabled(okState);
        }
    }

    public void doOk() {

        Iterator<AbstractPropertySheet> iter = settingMap.values().iterator();
        while (iter.hasNext()) {
            AbstractPropertySheet p = iter.next();
            java.util.logging.Logger.getLogger(this.getClass().getName()).fine(p.getTitle());
            p.save();
        }
        //----------------------------------------
        // UserDefaults 保存 たしかに保存だから
        //----------------------------------------
        Project.saveUserDefaults();
        dialog.setVisible(false);
        dialog.dispose();
        notifyResult();
    }

    public void doCancel() {
        dialog.setVisible(false);
        dialog.dispose();
        notifyResult();
    }
}
