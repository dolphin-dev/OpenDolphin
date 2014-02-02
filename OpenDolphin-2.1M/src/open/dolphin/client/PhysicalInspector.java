package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.OddEvenRowRenderer;
import org.apache.log4j.Logger;

/**
 * 身長体重インスペクタクラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PhysicalInspector {
    
    private ListTableModel<PhysicalModel> tableModel;
    
    private PhysicalView view;
    
    private ChartImpl context;
    
    private Logger logger;
    
    /**
     * PhysicalInspectorオブジェクトを生成する。
     */
    public PhysicalInspector(ChartImpl context) {
        this.context = context;
        logger = ClientContext.getBootLogger();
        initComponents();
        update();
    }
    
    public Chart getContext() {
        return context;
    }

    public void clear() {
        tableModel.clear();
    }

    /**
     * レイアウトパネルを返す。
     * @return レイアウトパネル
     */
    public JPanel getPanel() {
        return (JPanel) view;
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private void initComponents() {
        
        view = new PhysicalView();  
        
         // カラム名
        String[] columnNames = ClientContext.getStringArray("patientInspector.physicalInspector.columnNames"); // {"身長","体重","BMI","測定日"};

        // テーブルの初期行数
        //int startNumRows = ClientContext.getInt("patientInspector.physicalInspector.startNumRows");

        // 属性値を取得するためのメソッド名
        String[] methodNames = ClientContext.getStringArray("patientInspector.physicalInspector.methodNames"); // {"getHeight","getWeight","getBMI","getConfirmDate"};

        // 身長体重テーブルを生成する
        tableModel = new ListTableModel<PhysicalModel>(columnNames, 0, methodNames, null);
        view.getTable().setModel(tableModel);
        view.getTable().setFillsViewportHeight(true);
        view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());
        view.getTable().getColumnModel().getColumn(2).setCellRenderer(new BMIRenderer());
        view.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTable().setToolTipText("追加・削除は右クリックで行います。");
        
        // 列幅を調整する カット&トライ
        int[] cellWidth = new int[]{50,50,50,110};
        for (int i = 0; i < cellWidth.length; i++) {
            TableColumn column = view.getTable().getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth[i]);
        }

        //-----------------------------------------------
        // Copy 機能を実装する
        //-----------------------------------------------
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        final AbstractAction copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        view.getTable().getInputMap().put(copy, "Copy");
        view.getTable().getActionMap().put("Copy", copyAction);
        
        // 右クリックによる追加削除のメニューを登録する
        view.getTable().addMouseListener(new MouseAdapter() {

            private void mabeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem item = new JMenuItem("追加");
                    pop.add(item);
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            PhysicalEditor npe = new PhysicalEditor(PhysicalInspector.this);
                        }
                    });
                    final int row = view.getTable().rowAtPoint(e.getPoint());
                    if (tableModel.getObject(row) != null) {
                        pop.add(new JSeparator());
                        JMenuItem item2 = new JMenuItem(copyAction);
                        pop.add(item2);
                        pop.add(new JSeparator());
                        JMenuItem item3 = new JMenuItem("削除");
                        pop.add(item3);
                        item3.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                delete(row);
                            }
                        });
                    }
                    pop.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mabeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mabeShowPopup(e);
            }
        });
    }
    
    private void scroll(boolean ascending) {
        
        int cnt = tableModel.getObjectCount();
        if (cnt > 0) {
            int row = 0;
            if (ascending) {
                row = cnt - 1;
            }
            Rectangle r = view.getTable().getCellRect(row, row, true);
            view.getTable().scrollRectToVisible(r);
        }
    }

    /**
     * 身長体重データを表示する。
     */
    public void update() {
        
        //List<PhysicalModel> listH = (List<PhysicalModel>)context.getKarte().getEntryCollection("height");
        //List<PhysicalModel> listW = (List<PhysicalModel>)context.getKarte().getEntryCollection("weight");
        List<PhysicalModel> listH = context.getKarte().getHeights();
        List<PhysicalModel> listW = context.getKarte().getWeights();
        
        List<PhysicalModel> list = new ArrayList<PhysicalModel>();
        
        // 身長体重ともある場合
        if (listH != null && listW != null) {
            
            for (int i = 0; i < listH.size(); i++) {
                
                PhysicalModel h = (PhysicalModel) listH.get(i);
                String memo = h.getMemo();
                if (memo == null) {
                    memo = h.getIdentifiedDate();
                }
                
                // 
                // 体重のメモが一致するものを見つける
                //
                PhysicalModel found = null;
                for (int j = 0; j < listW.size(); j++) {
                    PhysicalModel w = (PhysicalModel) listW.get(j);
                    String memo2 = w.getMemo();
                    if (memo2 == null) {
                        memo2 = w.getIdentifiedDate();
                    }
                    if (memo2.equals(memo)) {
                        found = w;
                        PhysicalModel m = new PhysicalModel();
                        m.setHeightId(h.getHeightId());
                        m.setHeight(h.getHeight());
                        m.setWeightId(w.getWeightId());
                        m.setWeight(w.getWeight());
                        m.setIdentifiedDate(h.getIdentifiedDate());
                        m.setMemo(memo);
                        list.add(m);
                        break;
                    }
                }
                
                if (found != null) {
                    // 一致する体重はリストから除く
                    listW.remove(found);
                } else {
                    // なければ身長のみを加える
                    list.add(h);
                }
            }
            
            // 体重のリストが残っていればループする
            if (listW.size() > 0) {
                for (int i = 0; i < listW.size(); i++) {
                    list.add(listW.get(i));
                }
            }
            
        } else if (listH != null) {
            // 身長だけの場合
            for (int i = 0; i < listH.size(); i++) {
                list.add(listH.get(i));
            }
            
        } else if (listW != null) {
            // 体重だけの場合
            for (int i = 0; i < listW.size(); i++) {
                list.add(listW.get(i));
            }
        }
        
        if (list.isEmpty()) {
            return;
        }
        
        boolean asc = Project.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
        if (asc) {
            Collections.sort(list);
        } else {
            Collections.sort(list, Collections.reverseOrder());
        }
        
        tableModel.setDataProvider(list);
        scroll(asc);
    }

    /**
     * 身長体重データを追加する。
     */
    public void add(final PhysicalModel model) {

        // 同定日
        String confirmedStr = model.getIdentifiedDate();
        Date confirmed = ModelUtils.getDateTimeAsObject(confirmedStr + "T00:00:00");
        
        // 記録日
        Date recorded = new Date();

        final List<ObservationModel> addList = new ArrayList<ObservationModel>(2);

        if (model.getHeight() != null) {
            ObservationModel observation = new ObservationModel();
            observation.setKarteBean(context.getKarte());
            observation.setUserModel(Project.getUserModel());
            observation.setObservation(IInfoModel.OBSERVATION_PHYSICAL_EXAM);
            observation.setPhenomenon(IInfoModel.PHENOMENON_BODY_HEIGHT);
            observation.setValue(model.getHeight());
            observation.setUnit(IInfoModel.UNIT_BODY_HEIGHT);
            observation.setConfirmed(confirmed);        // 確定（同定日）
            observation.setStarted(confirmed);          // 適合開始日
            observation.setRecorded(recorded);          // 記録日
            observation.setStatus(IInfoModel.STATUS_FINAL);
            //observation.setMemo(model.getMemo());
            addList.add(observation);
        }

        if (model.getWeight() != null) {

            ObservationModel observation = new ObservationModel();
            observation.setKarteBean(context.getKarte());
            observation.setUserModel(Project.getUserModel());
            observation.setObservation(IInfoModel.OBSERVATION_PHYSICAL_EXAM);
            observation.setPhenomenon(IInfoModel.PHENOMENON_BODY_WEIGHT);
            observation.setValue(model.getWeight());
            observation.setUnit(IInfoModel.UNIT_BODY_WEIGHT);
            observation.setConfirmed(confirmed);        // 確定（同定日）
            observation.setStarted(confirmed);          // 適合開始日
            observation.setRecorded(recorded);          // 記録日
            observation.setStatus(IInfoModel.STATUS_FINAL);
            //observation.setMemo(model.getMemo());
            addList.add(observation);
        }

        if (addList.isEmpty()) {
            return;
        }

        DBTask task = new DBTask<List<Long>, Void>(context) {

            @Override
            protected List<Long> doInBackground() throws Exception {
                logger.debug("physical add doInBackground");
                DocumentDelegater pdl = new DocumentDelegater();
                List<Long> ids = pdl.addObservations(addList);
                return ids;
            }

            @Override
            protected void succeeded(List<Long> result) {
                logger.debug("physical add succeeded");
                if (model.getHeight() != null && model.getWeight() != null) {
                    model.setHeightId(result.get(0));
                    model.setWeightId(result.get(1));
                } else if (model.getHeight() != null) {
                    model.setHeightId(result.get(0));
                } else {
                    model.setWeightId(result.get(0));
                }
                boolean asc = Project.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
                if (asc) {
                    tableModel.addObject(model);
                } else {
                    tableModel.addObject(0, model);
                }
                scroll(asc);
            }
        };

        task.execute();
    }
    
    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {
        StringBuilder sb = new StringBuilder();
        int numRows = view.getTable().getSelectedRowCount();
        int[] rowsSelected = view.getTable().getSelectedRows();
        int numColumns =   view.getTable().getColumnCount();

        for (int i = 0; i < numRows; i++) {

            StringBuilder s = new StringBuilder();
            for (int col = 0; col < numColumns; col++) {
                Object o = view.getTable().getValueAt(rowsSelected[i], col);
                if (o!=null) {
                    s.append(o.toString());
                }
                s.append(",");
            }
            if (s.length()>0) {
                s.setLength(s.length()-1);
            }
            sb.append(s.toString()).append("\n");

        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }


    /**
     * テーブルで選択した身長体重データを削除する。
     */
    public void delete(final int row) {

        PhysicalModel model = (PhysicalModel)tableModel.getObject(row);
        if (model == null) {
            return;
        }
        
        final List<Long> list = new ArrayList<Long>(2);
        
        if (model.getHeight() != null) {
            list.add(new Long(model.getHeightId()));
        }
        
        if (model.getWeight() != null) {
            list.add(new Long(model.getWeightId()));
        }
        
        DBTask task = new DBTask<Void, Void>(context) {

            @Override
            protected Void doInBackground() throws Exception {
                logger.debug("physical delete doInBackground");
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                logger.debug("physical delete succeeded");
                tableModel.deleteAt(row);
            }
        };
        
        task.execute();
    }
    
    /**
     * BMI値 を表示するレンダラクラス。
     */
    protected class BMIRenderer extends DefaultTableCellRenderer {
        
        /** 
         * Creates new IconRenderer 
         */
        public BMIRenderer() {
            super();
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused, row, col);  
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setForeground(table.getForeground());
                if (row % 2 == 0) {
                    setBackground(ClientContext.getColor("color.even"));
                } else {
                    setBackground(ClientContext.getColor("color.odd"));
                }
            }
            
            PhysicalModel h = (PhysicalModel)tableModel.getObject(row);
            
            Color fore = (h != null && h.getBmi() != null && h.getBmi().compareTo("25") > 0)  ? Color.RED : Color.BLACK;
            this.setForeground(fore);
            
            ((JLabel) c).setText(value == null ? "" : (String) value);
            
            if (h != null && h.getStandardWeight() != null) {
                this.setToolTipText("標準体重 = " + h.getStandardWeight());
            }
            
            return c;
        }
    }
}
