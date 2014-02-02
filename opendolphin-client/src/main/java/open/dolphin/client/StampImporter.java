package open.dolphin.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;

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
        "name", "category", "partyName", "description", "publishType", "isImported"
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
    
    private String title = "スタンプインポート";
    private JFrame frame;
    private BlockGlass blockGlass;
    private JTable browseTable;
    private ListTableModel<PublishedTreeModel> tableModel;
    private JButton importBtn;
    private JButton deleteBtn;
    private JButton cancelBtn;
    private JLabel publicLabel;
    private JLabel localLabel;
    private JLabel importedLabel;
    
    private StampBoxPlugin stampBox;
    private List<Long> importedTreeList;

    // timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private int maxEstimation = 90*1000;    // 90 秒
    private int delay = 300;                // 300 mmsec
    
    public StampImporter(StampBoxPlugin stampBox) {
        this.stampBox = stampBox;
        importedTreeList = stampBox.getImportedTreeList();
    }

    /**
     * 公開されているTreeのリストを取得しテーブルへ表示する。
     */
    public void start() {

        final SimpleWorker worker = new SimpleWorker<List<PublishedTreeModel>, Void>() {

            @Override
            protected List<PublishedTreeModel> doInBackground() throws Exception {
                StampDelegater sdl = new StampDelegater();
                List<PublishedTreeModel> result = sdl.getPublishedTrees();
                return result;
            }

            @Override
            protected void succeeded(List<PublishedTreeModel> result) {
                // DBから取得が成功したらGUIコンポーネントを生成する
                initComponent();
                if (importedTreeList != null && importedTreeList.size() > 0) {
                    for (PublishedTreeModel model : result) {
                        for (Long id : importedTreeList) {
                            if (id.longValue() == model.getId()) {
                                model.setImported(true);
                                break;
                            }
                        }
                    }
                }
                tableModel.setDataProvider(result);
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(frame,
                            cause.getMessage(),
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
                ClientContext.getBootLogger().warn(cause.getMessage());
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

        String message = "スタンプインポート";
        String note = "公開スタンプを取得しています...";
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                   // no cancel
                } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        worker.execute();
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    public void initComponent() {
        
        frame = new JFrame(ClientContext.getFrameTitle(title));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
        
        JPanel contentPane = createBrowsePane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
        frame.pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int x = (screen.width - frame.getPreferredSize().width) / 2;
        int y = (screen.height - frame.getPreferredSize().height) / n;
        frame.setLocation(x, y);

        blockGlass = new BlockGlass();
        frame.setGlassPane(blockGlass);

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

        tableModel = new ListTableModel<PublishedTreeModel>(COLUMN_NAMES, 10, METHOD_NAMES, CLASSES);
        browseTable = new JTable(tableModel);
        for (int i = 0; i < COLUMN_WIDTH.length; i++) {
            browseTable.getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTH[i]);
        }
        browseTable.setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        
        importBtn = new JButton("インポート");
        importBtn.setEnabled(false);
        cancelBtn = new JButton("閉じる");
        deleteBtn = new JButton("削除");
        deleteBtn.setEnabled(false);
        publicLabel = new JLabel("グローバル", WEB_ICON, SwingConstants.CENTER);
        localLabel = new JLabel("院内", HOME_ICON, SwingConstants.CENTER);
        importedLabel = new JLabel("インポート済", FLAG_ICON, SwingConstants.CENTER);

        JScrollPane tableScroller = new JScrollPane(browseTable);
        tableScroller.getViewport().setPreferredSize(new Dimension(730, 380));
        
        // レイアウトする
        browsePane.setLayout(new BorderLayout(0, 17));
        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 5));
        flagPanel.add(localLabel);
        flagPanel.add(publicLabel);
        flagPanel.add(importedLabel);
        JPanel cmdPanel = GUIFactory.createCommandButtonPanel(new JButton[]{cancelBtn, deleteBtn, importBtn});
        browsePane.add(flagPanel, BorderLayout.NORTH);
        browsePane.add(tableScroller, BorderLayout.CENTER);
        browsePane.add(cmdPanel, BorderLayout.SOUTH);
        
        // レンダラを設定する
        PublishTypeRenderer pubTypeRenderer = new PublishTypeRenderer();
        browseTable.getColumnModel().getColumn(4).setCellRenderer(pubTypeRenderer);
        ImportedRenderer importedRenderer = new ImportedRenderer();
        browseTable.getColumnModel().getColumn(5).setCellRenderer(importedRenderer);
        
        // BrowseTableをシングルセレクションにする
        browseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel sleModel = browseTable.getSelectionModel();
        sleModel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int row = browseTable.getSelectedRow();
                    PublishedTreeModel model = tableModel.getObject(row);
                    if (model != null) {
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
            }
        });

        // import
        importBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                importPublishedTree();
            }
        });

        // remove
        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeImportedTree();
            }
        });

        // キャンセル
        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        
        return browsePane;
    }
    
    /**
     * ブラウザテーブルで選択した公開Treeをインポートする。
     */
    public void importPublishedTree() {

        // テーブルはシングルセレクションである
        int row = browseTable.getSelectedRow();
        final PublishedTreeModel importTree = tableModel.getObject(row);

        if (importTree == null) {
            return;
        }

        // Import 済みの場合
        if (importTree.isImported()) {
            return;
        }

        try {
            importTree.setTreeXml(new String(importTree.getTreeBytes(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        // サブスクライブリストに追加する
        SubscribedTreeModel sm = new SubscribedTreeModel();
        sm.setUserModel(Project.getUserModel());
        sm.setTreeId(importTree.getId());
        final List<SubscribedTreeModel> subscribeList = new ArrayList<SubscribedTreeModel>(1);
        subscribeList.add(sm);

        final SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                StampDelegater sdl = new StampDelegater();
                sdl.subscribeTrees(subscribeList);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                // スタンプボックスへインポートする
                stampBox.importPublishedTree(importTree);
                // Browser表示をインポート済みにする
                importTree.setImported(true);
                tableModel.fireTableDataChanged();
            }

            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("Task cancelled");
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(frame,
                        cause.getMessage(),
                        ClientContext.getFrameTitle(title),
                        JOptionPane.WARNING_MESSAGE);
                ClientContext.getBootLogger().warn(cause.getMessage());
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                blockGlass.block();
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                blockGlass.unblock();
                taskTimer = null;
                monitor = null;
            }
        };

        String message = "スタンプインポート";
        String note = "インポートしています...";
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    //worker.cancel(true);
                    // No cancel

                } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        worker.execute();
    }
    
    /**
     * インポートしているスタンプを削除する。
     */
    public void removeImportedTree() {

        // 削除するTreeを取得する
        int row = browseTable.getSelectedRow();
        final PublishedTreeModel removeTree = tableModel.getObject(row);
        
        if (removeTree == null) {
            return;
        }

        SubscribedTreeModel sm = new SubscribedTreeModel();
        sm.setTreeId(removeTree.getId());
        sm.setUserModel(Project.getUserModel());
        final List<SubscribedTreeModel> list = new ArrayList<SubscribedTreeModel>(1);
        list.add(sm);
        
        // Unsubscribeタスクを実行する
        
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
  
            @Override
            protected Void doInBackground() throws Exception {
                StampDelegater sdl = new StampDelegater();
                sdl.unsubscribeTrees(list);
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                // スタンプボックスから削除する
                stampBox.removeImportedTree(removeTree.getId());
                // ブラウザ表示を変更する
                removeTree.setImported(false);
                tableModel.fireTableDataChanged();
            }
            
            @Override
            protected void cancelled() {
                ClientContext.getBootLogger().debug("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(frame,
                            cause.getMessage(),
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
                ClientContext.getBootLogger().warn(cause.getMessage());
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                blockGlass.block();
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                blockGlass.unblock();
                taskTimer = null;
                monitor = null;
            }
        };

        String message = "スタンプインポート";
        String note = "インポート済みスタンプを削除しています...";
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    //worker.cancel(true);
                    // No cancel

                } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        worker.execute();
    }
        
    class PublishTypeRenderer extends DefaultTableCellRenderer {
        
        /** Creates new IconRenderer */
        public PublishTypeRenderer() {
            super();
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setForeground(table.getForeground());
                if (row % 2 == 0) {
                    setBackground(EVEN_COLOR);
                } else {
                    setBackground(ODD_COLOR);
                }
            }
            
            if (value != null && value instanceof String) {
                
                String pubType = (String) value;
                
                if (pubType.equals(IInfoModel.PUBLISHED_TYPE_GLOBAL)) {
                    setIcon(WEB_ICON);
                } else {
                    setIcon(HOME_ICON);
                } 
                this.setText("");
                
            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }
    
    class ImportedRenderer extends DefaultTableCellRenderer {
        
        /** Creates new IconRenderer */
        public ImportedRenderer() {
            super();
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {           
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setForeground(table.getForeground());
                if (row % 2 == 0) {
                    setBackground(EVEN_COLOR);
                } else {
                    setBackground(ODD_COLOR);
                }
            }
            
            if (value != null && value instanceof Boolean) {
                
                Boolean imported = (Boolean) value;
                
                if (imported.booleanValue()) {
                    this.setIcon(FLAG_ICON);
                } else {
                    this.setIcon(null);
                }
                this.setText("");
                
            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }
}