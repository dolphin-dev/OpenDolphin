package open.dolphin.order;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.TaskTimerMonitor;
import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlMasterDao;
import open.dolphin.project.Project;
import open.dolphin.table.ObjectTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * マスタ検索パネルのルート抽象クラス。
 *
 * @author Kazushi Minagawa
 */
public abstract class MasterPanel extends JPanel {

    /** マスタ項目選択プロパティ名 */
    public static final String SELECTED_ITEM_PROP = "selectedItemProp";
    /** 検索中プロパティ名 */
    public static final String BUSY_PROP = "busyProp";
    /** 件数プロパティ名 */
    public static final String ITEM_COUNT_PROP = "itemCount";
    /** キーワードフィールド用の tooltip text */
    protected static final String TOOLTIP_KEYWORD = "漢字が使用できます";
    /** キーワードフィールドの長さ */
    protected static final int KEYWORD_FIELD_LENGTH = 12;
    /** 検索アイコン */
    protected static final String FIND_ICON = "/open/dolphin/resources/images/srch_16.gif";
    /** キーワード部分のボーダタイトル */
    protected static final String keywordBorderTitle = ClientContext.getString("masterSearch.text.keywordBorderTitle");
    protected static final Color[] masterColors = ClientContext.getColorArray("masterSearch.masterColors");
    protected static final String[] masterNames = ClientContext.getStringArray("masterSearch.masterNames");
    protected static final String[] masterTabNames = ClientContext.getStringArray("masterSearch.masterTabNames");
    /** 検索結果テーブルの開始行数 */
    protected final int START_NUM_ROWS = 20;
    /** キーワードフィールド */
    protected JTextField keywordField;
    /** 検索アイコン */
    protected ImageIcon findIcon = new ImageIcon(this.getClass().getResource(FIND_ICON));
    /** 検索アイコンを表示するラベル */
    protected JLabel findLabel = new JLabel(findIcon);
    /** ソートボタン配列 */
    protected JRadioButton[] sortButtons;
    /** 検索結果テーブル */
    protected JTable table;
    /** 検索結果テーブルの table model */
    protected ObjectTableModel tableModel;
    /** 検索するマスタ名 */
    protected String master;
    /** 検索するクラス */
    protected String searchClass;
    /** ソート節 */
    protected String sortBy;
    /** order by 節 */
    protected String order;
    /** 選択されたマスタ項目 */
    protected MasterItem selectedItem;
    /** 検索中のフラグ */
    protected boolean busy;
    /** 検索結果件数 */
    protected int itemCount;
    /** タスク用のタイマ */
    protected javax.swing.Timer taskTimer;
    /** 割り込み時間 */
    protected static final int TIMER_DELAY = 200;
    /** 束縛サポート */
    protected PropertyChangeSupport boundSupport;
    /** プレファレンス */
    protected Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    protected Logger logger;
    
    /**
     * MasterPanelオブジェクトを生成する。
     */
    public MasterPanel() {
        logger = ClientContext.getBootLogger();
    }

    /**
     * MasterPanelオブジェクトを生成する。
     * @param master マスタ名
     * @param pulse 進捗バー  // 何故?
     */
    public MasterPanel(final String master) {
        //public MasterPanel(final String master, UltraSonicProgressLabel pulse) {

        this();
        setMaster(master);
        //this.pulse = pulse;

        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

        //
        // キーワードフィールドを生成する
        //
        keywordField = new JTextField(KEYWORD_FIELD_LENGTH);
        keywordField.setToolTipText(TOOLTIP_KEYWORD);
        keywordField.setMaximumSize(keywordField.getPreferredSize());
        keywordField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String key = keywordField.getText().trim();
                if (!key.equals("")) {
                    search(key);
                }
            }
        });

        // フォーカスがあたった時 IME をオンにする
        keywordField.addFocusListener(AutoKanjiListener.getInstance());

        // 初期化する
        initialize();
    }

    /**
     * サブクラスが実装する初期化メソッド。
     */
    protected abstract void initialize();

    /**
     * 束縛リスナを登録する。
     * @param prop プロパティ名
     * @param l リスナ
     */
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }

    /**
     * 束縛リスナを削除する。
     * @param prop プロパティ名
     * @param l リスナ
     */
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }

    /**
     * 選択されたマスタ項目を返す。
     * @return 選択されたマスタ項目
     */
    public MasterItem getSelectedItem() {
        return selectedItem;
    }

    /**
     * 選択されたマスタ項目プロパティをセットしリスナへ通知する。
     * @param 選択されたマスタ項目
     */
    public void setSelectedItem(MasterItem item) {
        MasterItem oldItem = selectedItem;
        selectedItem = item;
        boundSupport.firePropertyChange(SELECTED_ITEM_PROP, oldItem, selectedItem);
    }

    /**
     * 検索中プロパティを返す。
     * @return 検索中の時 true
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * 検索中プロパティを設定しリスナへ通知する。
     * @param newBusy 検索中の時 true
     */
    public void setBusy(boolean newBusy) {
        boolean oldBusy = busy;
        busy = newBusy;
        boundSupport.firePropertyChange(BUSY_PROP, oldBusy, busy);
    }

    /**
     * 件数を返す。
     * @return マスタ検索の結果件数
     */
    public int getCount() {
        return itemCount;
    }

    /**
     * 検索結果件数をセットしリスナへ通知する。
     * @param count マスタ検索の結果件数
     */
    public void setItemCount(int count) {
        itemCount = count;
        boundSupport.firePropertyChange(ITEM_COUNT_PROP, -1, itemCount);
    }

    /**
     * 検索クラスを設定する。
     * @param searchClass 検索クラス
     */
    public void setSearchClass(String searchClass) {
        this.searchClass = searchClass;
    }

    /**
     * ソート項目を設定する。
     * @param sortBy ソート項目
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * マスタ名を返す。
     * @return マスタ名
     */
    public String getMaster() {
        return master;
    }

    /**
     * マスタ名を設定する。
     * @param master マスタ名
     */
    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * order by 項目を設定する。
     * @param order order by 項目
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * このマスタパネルのタブが選択された時コールされる。
     * 検索結果件数を書き換える。
     */
    public void enter() {
        setItemCount(tableModel.getObjectCount());
    }

    /**
     * プログラムの終了処理を行う。
     */
    public void dispose() {
        if (tableModel != null) {
            tableModel.clear();
        }
    }

    /**
     * 引数のキーワードからマスタを検索する。
     * @param text キーワード
     */
    protected void search(final String text) {
        
        logger.debug("master = " + master);
        logger.debug("text = " + text);
        logger.debug("searchClass = " + searchClass);
        logger.debug("sortBy = " + sortBy);
        logger.debug("order = " + order);

        // CLAIM(Master) Address が設定されていない場合に警告する
        String address = Project.getClaimAddress();
        if (address == null || address.equals("")) {
            String msg0 = "レセコンのIPアドレスが設定されていないため、マスターを検索できません。";
            String msg1 = "環境設定メニューからレセコンのIPアドレスを設定してください。";
            Object message = new String[]{msg0, msg1};
            Window parent = SwingUtilities.getWindowAncestor(MasterPanel.this);
            String title = ClientContext.getFrameTitle(getMaster());
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // DAOを生成する
        final SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create(this, "dao.master");
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        
        Task task = new Task<Object, Void>(app) {

            @Override
            protected Object doInBackground() throws Exception {
                Object result = dao.getByName(master, text, false, searchClass, sortBy, order);
                return result;
            }
            
            @Override
            protected void succeeded(Object result) {
                logger.debug("Task succeeded");
                processResult(dao.isNoError(), result, dao.getErrorMessage());
            }
            
            @Override
            protected void cancelled() {
                logger.debug("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                logger.warn(cause.getMessage());
            }
            
            @Override
            protected void interrupted(java.lang.InterruptedException e) {
                logger.warn(e.getMessage());
            }
        };
        
        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = "マスタ検索";
        String note = text + "を検索しています...";
        Component c = SwingUtilities.getWindowAncestor(this);
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, note, 200, 60*1000);
        taskMonitor.addPropertyChangeListener(w);
        
        appCtx.getTaskService().execute(task);
    }

    /**
     * 検索結果をテーブルへ表示する。
     */
    protected void processResult(boolean noErr, Object result, String message) {

        if (noErr) {

            tableModel.setObjectList((List) result);
            setItemCount(tableModel.getObjectCount());

        } else {

            String title = ClientContext.getFrameTitle(getMaster());
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * ソートボタンへ登録するアクションリスナクラス。
     */
    protected class SortActionListener implements ActionListener {

        private MasterPanel target;
        private String sortBy;
        private int btnIndex;

        public SortActionListener(MasterPanel target, String sortBy, int btnIndex) {
            this.target = target;
            this.sortBy = sortBy;
            this.btnIndex = btnIndex;
        }

        public void actionPerformed(ActionEvent e) {
            prefs.putInt("masterSearch." + target.getMaster() + ".sort", btnIndex);
            target.setSortBy(sortBy);
        }
    }
}
