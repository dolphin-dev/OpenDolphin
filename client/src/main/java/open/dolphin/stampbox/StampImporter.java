package open.dolphin.stampbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;

/**
 * StampImporter
 *
 * @author Minagawa,Kazushi
 */
public class StampImporter {
    
    private final String[] COLUMN_NAMES;
    private final String[] METHOD_NAMES;
    private final Class[] CLASSES;
    private final int[] COLUMN_WIDTH;
    
    private final String title;
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
    
    private final StampBoxPlugin stampBox;
    private final List<Long> importedTreeList;

    // timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private final int maxEstimation = 90*1000;    // 90 秒
    private final int delay = 300;                // 300 mmsec
    
    public StampImporter(StampBoxPlugin stampBox) {
        
        // Resource Injection
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampImporter.class);
        title = bundle.getString("title.window");
        
        String line = bundle.getString("columnNames.table");
        COLUMN_NAMES = line.split(",");
        
        line = "name,category,partyName,description,publishType,isImported";
        METHOD_NAMES = line.split(",");
        
        CLASSES = new Class[]{
            String.class, String.class, String.class, String.class, String.class, Boolean.class
        };
        
        COLUMN_WIDTH = new int[] {
            120, 90, 170, 270, 40, 40
        };
        
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
                if (result==null) {
                    result = new ArrayList(1);
                }
                return result;
            }

            @Override
            protected void succeeded(List<PublishedTreeModel> result) {
                // DBから取得が成功したらGUIコンポーネントを生成する
                initComponent();
                if (importedTreeList != null && importedTreeList.size() > 0) {
                    for (PublishedTreeModel model : result) {
                        for (Long id : importedTreeList) {
                            if (id == model.getId()) {
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
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

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampImporter.class);
        String message = bundle.getString("message.progress.import");
        String note = bundle.getString("note.getting.publishedStamp");
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                // no cancel
            } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }
    
    /**
     * GUIコンポーネントを初期化する。
     */
    public void initComponent() {
        
        stampBox.getBlockGlass().block();
        
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
        stampBox.getBlockGlass().unblock();
    }
    
    /**
     * 公開スタンプブラウズペインを生成する。
     */
    private JPanel createBrowsePane() {
        
        JPanel browsePane = new JPanel();

        tableModel = new ListTableModel<>(COLUMN_NAMES, 0, METHOD_NAMES, CLASSES);
        browseTable = new JTable(tableModel);
        for (int i = 0; i < COLUMN_WIDTH.length; i++) {
            browseTable.getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTH[i]);
        }
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampImporter.class);
        String buttonTextImport = bundle.getString("buttonText.import");
        String buttonTextClose = bundle.getString("buttonText.close");
        String buttonTextDelete = bundle.getString("buttonText.delete");
        importBtn = new JButton(buttonTextImport);
        importBtn.setEnabled(false);
        cancelBtn = new JButton(buttonTextClose);
        deleteBtn = new JButton(buttonTextDelete);
        deleteBtn.setEnabled(false);

        String labelTextGlobal = bundle.getString("labelText.global");
        String labeltextInternal = bundle.getString("labelText.internal");
        String labelTextImportDone = bundle.getString("lablelText.importDone");
        publicLabel = new JLabel(labelTextGlobal, ClientContext.getImageIconArias("icon_world_small"), SwingConstants.CENTER);
        localLabel = new JLabel(labeltextInternal, ClientContext.getImageIconArias("icon_hospital_small"), SwingConstants.CENTER);
        importedLabel = new JLabel(labelTextImportDone, ClientContext.getImageIconArias("icon_flag_blue_small"), SwingConstants.CENTER);
        
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
        StripeTableCellRenderer rederer = new StripeTableCellRenderer();
        rederer.setTable(browseTable);
        browseTable.getColumnModel().getColumn(0).setCellRenderer(rederer);
        browseTable.getColumnModel().getColumn(1).setCellRenderer(rederer);
        browseTable.getColumnModel().getColumn(2).setCellRenderer(rederer);
        browseTable.getColumnModel().getColumn(3).setCellRenderer(rederer);
        
        PublishTypeRenderer pubTypeRenderer = new PublishTypeRenderer();
        pubTypeRenderer.setTable(browseTable);
        browseTable.getColumnModel().getColumn(4).setCellRenderer(pubTypeRenderer);
        
        ImportedRenderer importedRenderer = new ImportedRenderer();
        importedRenderer.setTable(browseTable);
        browseTable.getColumnModel().getColumn(5).setCellRenderer(importedRenderer);
        
        // BrowseTableをシングルセレクションにする
        browseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel sleModel = browseTable.getSelectionModel();
        sleModel.addListSelectionListener((ListSelectionEvent e) -> {
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
        });

        // import
        importBtn.addActionListener((ActionEvent e) -> {
            importPublishedTree();
        });

        // remove
        deleteBtn.addActionListener((ActionEvent e) -> {
            removeImportedTree();
        });

        // キャンセル
        cancelBtn.addActionListener((ActionEvent e) -> {
            stop();
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
        final List<SubscribedTreeModel> subscribeList = new ArrayList<>(1);
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("Task cancelled");
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(frame,
                        cause.getMessage(),
                        ClientContext.getFrameTitle(title),
                        JOptionPane.WARNING_MESSAGE);
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
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

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampImporter.class);
        String message = bundle.getString("message.progress.import");
        String note = bundle.getString("note.importing");
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                //worker.cancel(true);
                // No cancel
                
            } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
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
        final List<SubscribedTreeModel> list = new ArrayList<>(1);
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
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(frame,
                            cause.getMessage(),
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(cause.getMessage());
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

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampImporter.class);
        String message = bundle.getString("message.progress.import");
        String note = bundle.getString("note.deletingImportedStamp");
        Component c = frame;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                //worker.cancel(true);
                // No cancel
                
            } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }
        
    class PublishTypeRenderer extends StripeTableCellRenderer {
        
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
            
            super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, col);
            
            if (value != null && value instanceof String) {
                
                String pubType = (String) value;
                
                if (pubType.equals(IInfoModel.PUBLISHED_TYPE_GLOBAL)) {
                    setIcon(ClientContext.getImageIconArias("icon_world_small"));
                } else {
                    setIcon(ClientContext.getImageIconArias("icon_hospital_small"));                
                } 
                this.setText("");
                
            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }
    
    class ImportedRenderer extends StripeTableCellRenderer {
        
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
            
            if (value != null && value instanceof Boolean) {
                
                Boolean imported = (Boolean) value;
                
                if (imported) {
                    this.setIcon(ClientContext.getImageIconArias("icon_flag_blue_small"));                   
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