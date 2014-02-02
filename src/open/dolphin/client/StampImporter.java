package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;
import open.dolphin.plugin.helper.ComponentMemory;
import open.dolphin.project.Project;

/**
 * StampImporter
 *
 * @author Minagawa,Kazushi
 */
public class StampImporter {
    
    private static final String[] COLUMN_NAMES = {
        "名  称", "カテゴリ", "公開者", "説  明", "公開先", "インポート"
    };
    private static final String[] METHOD_NAMES = {
        "getName", "getCategory", "getPartyName", "getDescription", "getPublishType", "isImported"
    };
    private static final Class[] CLASSES = {
        String.class, String.class, String.class, String.class, String.class, Boolean.class
    };
    private static final int[] COLUMN_WIDTH = {
        120, 90, 170, 270, 40, 40
    };
    private static final Color ODD_COLOR = ClientContext.getColor("color.odd");
    private static final Color EVEN_COLOR = ClientContext.getColor("color.even");
    private static final ImageIcon WEB_ICON = ClientContext.getImageIcon("web_16.gif");
    private static final ImageIcon HOME_ICON = ClientContext.getImageIcon("home_16.gif");
    private static final ImageIcon FLAG_ICON = ClientContext.getImageIcon("flag_16.gif");
    
    private static final int WIDTH = 780;
    private static final int HEIGHT = 380;
    
    private String title = "スタンプインポート";
    private JFrame frame;
    private ObjectListTable browseTable;
    private JButton importBtn;
    private JButton deleteBtn;
    private JButton cancelBtn;
    private JLabel publicLabel;
    private JLabel localLabel;
    private JLabel importedLabel;
    
    private StampBoxPlugin stampBox;
    private List<Long> importedTreeList;
    private StampDelegater sdl;
    private Timer timer;
    private BrowseTask browseWorker;
    private  SubscribeTask subscribeWorker;
    private UnsubscribeTask unsubscribeTask;
    private ProgressMonitor progressMonitor;
    
    public StampImporter(StampBoxPlugin stampBox) {
        this.stampBox = stampBox;
        importedTreeList = stampBox.getImportedTreeList();
    }
    
    /**
     * 公開されているTreeのリストを取得しテーブルへ表示する。
     */
    public void start() {
        
        sdl = new StampDelegater();
        
        int delay = 200;
        int maxEstimation = 60*1000;
        String mmsg = "公開スタンプを取得しています...";
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        progressMonitor = new ProgressMonitor(frame, null, mmsg, 0, maxEstimation/delay);
        
        browseWorker = new BrowseTask(sdl, maxEstimation/delay);
        
        timer = new javax.swing.Timer(delay, new ActionListener() {
            
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                
                progressMonitor.setProgress(browseWorker.getCurrent());
                
                if (browseWorker.isDone()) {
                    
                    timer.stop();
                    progressMonitor.close();
                    
                    if (sdl.isNoError()) {
                        // DBから取得が成功したらGUIコンポーネントを生成する
                        initComponent();
                        List list = browseWorker.getResult();
                        if (importedTreeList != null && importedTreeList.size() > 0) {
                            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                                PublishedTreeModel model = (PublishedTreeModel) iter.next();
                                for (Long id : importedTreeList) {
                                    if (id.longValue() == model.getId()) {
                                        model.setImported(true);
                                        break;
                                    }
                                }
                            }
                        }
                        browseTable.setObjectList(list);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                sdl.getErrorMessage(),
                                ClientContext.getFrameTitle(title),
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                } else if (browseWorker.isTimeOver()) {
                    timer.stop();
                    progressMonitor.close();
                    new TimeoutWarning(frame, title, null).start();
                }
            }
        });
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(milisToPopup);
        progressMonitor.setMillisToPopup(decideToPopup);
        browseWorker.start();
        timer.start();
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    public void initComponent() {
        frame = new JFrame(ClientContext.getFrameTitle(title));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int x = (screen.width - WIDTH) / 2;
        int y = (screen.height - HEIGHT) / n;
        ComponentMemory cm = new ComponentMemory(frame, new Point(x, y), new Dimension(new Dimension(WIDTH, HEIGHT)), this);
        cm.setToPreferenceBounds();
        
        JPanel contentPane = createBrowsePane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }
    
    /**
     * 終了する。
     */
    public void stop() {
        frame.setVisible(false);
        frame.dispose();
    }
    
    /**
     * 公開スタンプブラウズペインを生成する。
     */
    private JPanel createBrowsePane() {
        
        JPanel browsePane = new JPanel();
        
        browseTable = new ObjectListTable(COLUMN_NAMES, 10, METHOD_NAMES, CLASSES);
        browseTable.setColumnWidth(COLUMN_WIDTH);
        importBtn = new JButton("インポート");
        importBtn.setEnabled(false);
        cancelBtn = new JButton("閉じる");
        deleteBtn = new JButton("削除");
        deleteBtn.setEnabled(false);
        publicLabel = new JLabel("グローバル", WEB_ICON, SwingConstants.CENTER);
        localLabel = new JLabel("院内", HOME_ICON, SwingConstants.CENTER);
        importedLabel = new JLabel("インポート済", FLAG_ICON, SwingConstants.CENTER);
        
        // レイアウトする
        browsePane.setLayout(new BorderLayout(0, 17));
        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 5));
        flagPanel.add(localLabel);
        flagPanel.add(publicLabel);
        flagPanel.add(importedLabel);
        JPanel cmdPanel = GUIFactory.createCommandButtonPanel(new JButton[]{cancelBtn, deleteBtn, importBtn});
        browsePane.add(flagPanel, BorderLayout.NORTH);
        browsePane.add(browseTable.getScroller(), BorderLayout.CENTER);
        browsePane.add(cmdPanel, BorderLayout.SOUTH);
        
        // レンダラを設定する
        PublishTypeRenderer pubTypeRenderer = new PublishTypeRenderer();
        browseTable.getTable().getColumnModel().getColumn(4).setCellRenderer(pubTypeRenderer);
        ImportedRenderer importedRenderer = new ImportedRenderer();
        browseTable.getTable().getColumnModel().getColumn(5).setCellRenderer(importedRenderer);
        
        // BrowseTableをシングルセレクションにする
        browseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // コンポーネント間のイベント接続を行う
        PropertyChangeListener pl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                Object[] selected = (Object[]) e.getNewValue();
                if (selected != null && selected.length > 0) {
                    PublishedTreeModel model = (PublishedTreeModel) selected[0];
                    if (model.isImported()) {
                        importBtn.setEnabled(false);
                        deleteBtn.setEnabled(true);
                    } else {
                        importBtn.setEnabled(true);
                        deleteBtn.setEnabled(false);
                    }
                    
                } else {
                    importBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                }
            }
        };
        browseTable.addPropertyChangeListener(ObjectListTable.SELECTED_OBJECT, pl);
        
        // import
        importBtn.addActionListener(new ReflectActionListener(this, "importPublishedTree"));
        // remove
        deleteBtn.addActionListener(new ReflectActionListener(this, "removeImportedTree"));
        // キャンセル
        cancelBtn.addActionListener(new ReflectActionListener(this, "stop"));
        
        return browsePane;
    }
    
    /**
     * ブラウザテーブルで選択した公開Treeをインポートする。
     */
    public  void importPublishedTree() {
        
        Object[] objects = (Object[]) browseTable.getSelectedObject();
        if (objects == null || objects.length == 0) {
            return;
        }
        // テーブルはシングルセレクションである
        // TODO ブラウズ時にbyte[]を取得している...
        final PublishedTreeModel importTree = (PublishedTreeModel) objects[0];
        try {
            importTree.setTreeXml(new String(importTree.getTreeBytes(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //importTree.setTreeBytes(null);
        // サブスクライブリストに追加する
        SubscribedTreeModel sm = new SubscribedTreeModel();
        sm.setUser(Project.getUserModel());
        sm.setTreeId(importTree.getId());
        List<SubscribedTreeModel> subscribeList = new ArrayList<SubscribedTreeModel>(1);
        subscribeList.add(sm);
        
        // デリゲータを生成する
        sdl = new StampDelegater();
        
        // Worker, Timer を実行する
        int delay = 200;
        int maxEstimation = 60*1000;
        String mmsg = "公開スタンプをインポートしています...";
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        progressMonitor = new ProgressMonitor(frame, null, mmsg, 0, maxEstimation/delay);
        
        subscribeWorker = new SubscribeTask(subscribeList, sdl, maxEstimation/delay);
        
        timer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                progressMonitor.setProgress(subscribeWorker.getCurrent());
                
                if (subscribeWorker.isDone()) {
                    
                    timer.stop();
                    progressMonitor.close();
                    
                    if (sdl.isNoError()) {
                        // スタンプボックスへインポートする
                        stampBox.importPublishedTree(importTree);
                        // Browser表示をインポート済みにする
                        importTree.setImported(true);
                        browseTable.getTableModel().fireTableDataChanged();
                        
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                sdl.getErrorMessage(),
                                ClientContext.getFrameTitle(title),
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                } else if (subscribeWorker.isTimeOver()) {
                    timer.stop();
                    progressMonitor.close();
                    new TimeoutWarning(frame, title, null).start();
                }
            }
        });
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(milisToPopup);
        progressMonitor.setMillisToPopup(decideToPopup);
        subscribeWorker.start();
        timer.start();
    }
    
    /**
     * インポートしているスタンプを削除する。
     */
    public void removeImportedTree() {
        
        Object[] objects = (Object[]) browseTable.getSelectedObject();
        if (objects == null || objects.length == 0) {
            return;
        }
        
        // 削除するTreeを取得する
        final PublishedTreeModel removeTree = (PublishedTreeModel) objects[0];
        SubscribedTreeModel sm = new SubscribedTreeModel();
        sm.setTreeId(removeTree.getId());
        sm.setUser(Project.getUserModel());
        List<SubscribedTreeModel> list = new ArrayList<SubscribedTreeModel>(1);
        list.add(sm);
        
        // DeleteTaskを実行する
        sdl = new StampDelegater();
        
        // Unsubscribeタスクを実行する
        int delay = 200;
        int maxEstimation = 60*1000;
        String mmsg = "インポート済みスタンプを削除しています...";
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        progressMonitor = new ProgressMonitor(frame, null, mmsg, 0, maxEstimation/delay);
        
        unsubscribeTask = new UnsubscribeTask(list, sdl, maxEstimation/delay);
        
        timer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                progressMonitor.setProgress(unsubscribeTask.getCurrent());
                
                if (unsubscribeTask.isDone()) {
                    
                    timer.stop();
                    progressMonitor.close();
                    
                    if (sdl.isNoError()) {
                        // スタンプボックスから削除する
                        stampBox.removeImportedTree(removeTree.getId());
                        // ブラウザ表示を変更する
                        removeTree.setImported(false);
                        browseTable.getTableModel().fireTableDataChanged();
                        
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                sdl.getErrorMessage(),
                                ClientContext.getFrameTitle(title),
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                } else if (subscribeWorker.isTimeOver()) {
                    timer.stop();
                    progressMonitor.close();
                    new TimeoutWarning(frame, title, null).start();
                }
            }
        });
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(milisToPopup);
        progressMonitor.setMillisToPopup(decideToPopup);
        unsubscribeTask.start();
        timer.start();
    }
    
    /**
     * 公開されているTreeのリストを取得するタスク。
     */
    class BrowseTask extends AbstractInfiniteTask {
        
        private StampDelegater mySdl;
        private List<PublishedTreeModel> result;
        
        public BrowseTask(StampDelegater mySdl, int taskLength) {
            this.mySdl = mySdl;
            setTaskLength(taskLength);
        }
        
        public List<PublishedTreeModel> getResult() {
            return result;
        }
        
        protected void doTask() {
            result = mySdl.getPublishedTrees();
            setDone(true);
        }
    }
    
    /**
     * サブスクライブタスク。
     */
    class SubscribeTask extends AbstractInfiniteTask {
        
        private StampDelegater mySdl;
        private List<SubscribedTreeModel> subscribeList;
        
        public SubscribeTask(List<SubscribedTreeModel> subscribeList, StampDelegater mySdl, int taskLength) {
            this.subscribeList = subscribeList;
            this.mySdl = mySdl;
            setTaskLength(taskLength);
        }
        
        protected void doTask() {
            mySdl.subscribeTrees(subscribeList);
            setDone(true);
        }
    }
    
    /**
     * アンサブスクライブタスク。
     */
    class UnsubscribeTask extends AbstractInfiniteTask {
        
        private StampDelegater mySdl;
        private List<SubscribedTreeModel> subscribeList;
        
        public UnsubscribeTask(List<SubscribedTreeModel> subscribeList, StampDelegater mySdl, int taskLength) {
            this.subscribeList = subscribeList;
            this.mySdl = mySdl;
            setTaskLength(taskLength);
        }
        
        protected void doTask() {
            mySdl.unsubscribeTrees(subscribeList);
            setDone(true);
        }
    }
    
    protected class PublishTypeRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 7134379493874260895L;
        
        /** Creates new IconRenderer */
        public PublishTypeRenderer() {
            super();
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);
            
            if (row % 2 == 0) {
                setBackground(EVEN_COLOR);
            } else {
                setBackground(ODD_COLOR);
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            if (value != null && value instanceof String) {
                
                String pubType = (String) value;
                
                if (pubType.equals(IInfoModel.PUBLISHED_TYPE_GLOBAL)) {
                    setIcon(WEB_ICON);
                } else {
                    setIcon(HOME_ICON);
                } 
                ((JLabel) c).setText("");
            } else {
                setIcon(null);
                ((JLabel) c).setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
    
    protected class ImportedRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 7134379493874260895L;
        
        /** Creates new IconRenderer */
        public ImportedRenderer() {
            super();
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);
            
            if (row % 2 == 0) {
                setBackground(EVEN_COLOR);
            } else {
                setBackground(ODD_COLOR);
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            if (value != null && value instanceof Boolean) {
                
                Boolean imported = (Boolean) value;
                
                if (imported.booleanValue()) {
                    setIcon(FLAG_ICON);
                } else {
                    setIcon(null);
                }
                ((JLabel) c).setText("");
            } else {
                setIcon(null);
                ((JLabel) c).setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
}