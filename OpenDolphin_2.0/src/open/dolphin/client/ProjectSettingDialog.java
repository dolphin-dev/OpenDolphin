package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.project.Project;
import org.apache.log4j.Logger;

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
    private HashMap<String, AbstractSettingPanel> settingMap;
    private ArrayList<AbstractSettingPanel> allSettings;
    private ArrayList<JToggleButton> allBtns;
    private String startSettingName;
    private boolean loginState;
    private PropertyChangeSupport boundSupport;
    private static final String SETTING_PROP = "SETTING_PROP";
    private boolean okState;

    private Logger logger;

    /**
     * Creates new ProjectSettingDialog
     */
    public ProjectSettingDialog() {
        logger = ClientContext.getBootLogger();
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
        boolean valid = Project.getProjectStub().isValid() ? true : false;
        boundSupport.firePropertyChange(SETTING_PROP, !valid, valid);
    }

    /**
     * オープン時に表示する設定画面をセットする。
     */
    public void setProject(String startSettingName) {
        this.startSettingName = startSettingName;
    }

    /**
     * 設定画面を開始する。
     */
    public void start() {

        Runnable r = new Runnable() {

            @Override
            public void run() {

                //
                // モデルを得る
                // 全ての設定プラグイン(Reference)を得、リストに格納する
                //
                try {
                    allSettings = new ArrayList<AbstractSettingPanel>();
                    PluginLoader<AbstractSettingPanel> loader = PluginLoader.load(AbstractSettingPanel.class);
                    Iterator<AbstractSettingPanel> iter = loader.iterator();
                    while (iter.hasNext()) {
                        AbstractSettingPanel setting = iter.next();
                        logger.debug(setting.getClass().getName());
                        allSettings.add(setting);
                    }

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    return;
                }

                // 設定パネル(AbstractSettingPanel)を格納する Hashtableを生成する
                // key=設定プラグインの名前 value=設定プラグイン
                settingMap = new HashMap<String, AbstractSettingPanel>();

                //
                // GUI を構築しモデルをバインドする
                //
                initComponents();

                //
                // オープン時に表示する設定画面を決定する
                //
                int index = 0;

                if (startSettingName != null) {
                    logger.debug("startSettingName = " + startSettingName);
                    for (AbstractSettingPanel setting : allSettings) {
                        if (startSettingName.equals(setting.getId())) {
                            logger.debug("found index " + index);
                            break;
                        }
                        index++;
                    }
                }
                
                index = (index >= 0 && index < allSettings.size()) ? index : 0;

                //
                // ボタンを押して表示する
                //
                allBtns.get(index).doClick();
            }
        };

        SwingUtilities.invokeLater(r);
    }

    /**
     * GUI を構築する。
     */
    private void initComponents() {

        itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //
        // 設定プラグインを起動するためのトグルボタンを生成し
        // パネルへ加える
        //
        allBtns = new ArrayList<JToggleButton>();
        ButtonGroup bg = new ButtonGroup();
        for (AbstractSettingPanel setting : allSettings) {
            String id = setting.getId();
            String text = setting.getTitle();
            String iconStr = setting.getIcon();
            logger.debug("id = " + id);
            logger.debug("text = " + text);
            logger.debug("icon = " + iconStr);
            ImageIcon icon = ClientContext.getImageIcon(iconStr);
            JToggleButton tb = new JToggleButton(text, icon);
            if (ClientContext.isWin()) {
                tb.setMargin(new Insets(0, 0, 0, 0));
            }
            tb.setHorizontalTextPosition(SwingConstants.CENTER);
            tb.setVerticalTextPosition(SwingConstants.BOTTOM);
            itemPanel.add(tb);
            bg.add(tb);
            tb.setActionCommand(id);    // button の actionCommand=id
            allBtns.add(tb);
        }

        //
        // 設定パネルのコンテナとなるカードパネル
        //
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // コマンドボタン
        String text = ClientContext.getString("settingDialog.saveButtonText");
        okButton = GUIFactory.createButton(text, null, null);
        okButton.setEnabled(false);

        // Cancel
        text = (String) UIManager.get("OptionPane.cancelButtonText");
        cancelButton = GUIFactory.createButton(text, "C", null);

        // 全体ダイアログのコンテントパネル
        JPanel panel = new JPanel(new BorderLayout(11, 0));
        panel.add(itemPanel, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        //
        // ダイアログを生成する
        //
        String title = ClientContext.getString("settingDialog.title");
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
        logger.debug("dialog created");

        //
        // イベント接続を行う
        //
        connect();

    }

    /**
     * GUI コンポーネントのイベント接続を行う。
     */
    private void connect() {

        //
        // 設定項目ボタンに追加するアクションリスナを生成する
        //
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                AbstractSettingPanel theSetting = null;

                // Action Command に設定パネルのIDが設定してある
                String name = event.getActionCommand();
                
                for (AbstractSettingPanel setting : allSettings) {
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
            }
        };

        //
        // 全てのボタンにリスナを追加する
        //
        for (JToggleButton btn : allBtns) {
            btn.addActionListener(al);
        }

        // Save
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doOk();
            }
        });
        okButton.setEnabled(false);

        // Cancel
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
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
    private void startSetting(final AbstractSettingPanel sp) {

        //
        // 既に生成されている場合はそれを表示する
        //
        if (sp.getContext() != null) {
            cardLayout.show(cardPanel, sp.getTitle());
            return;
        }

        Runnable r = new Runnable() {

            @Override
            public void run() {

                //
                // まだ生成されていない場合は
                // 選択された設定パネルを生成しカードに追加する
                try {
                    settingMap.put(sp.getId(), sp);
                    sp.setContext(ProjectSettingDialog.this);
                    sp.setProjectStub(Project.getProjectStub());
                    sp.start();

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
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
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        };

        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * SettingPanel の state が変化した場合に通知を受け、
     * 全てのカードをスキャンして OK ボタンをコントロールする。
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();
        if (!prop.equals(AbstractSettingPanel.STATE_PROP)) {
            return;
        }

        //
        // 全てのカードをスキャンして OK ボタンをコントロールする
        //
        boolean newOk = true;
        Iterator<AbstractSettingPanel> iter = settingMap.values().iterator();
        int cnt = 0;
        while (iter.hasNext()) {
            cnt++;
            AbstractSettingPanel p = iter.next();
            if (p.getState().equals(AbstractSettingPanel.State.INVALID_STATE)) {
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

        Iterator<AbstractSettingPanel> iter = settingMap.values().iterator();
        while (iter.hasNext()) {
            AbstractSettingPanel p = iter.next();
            logger.debug(p.getTitle());
            p.save();
        }

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
