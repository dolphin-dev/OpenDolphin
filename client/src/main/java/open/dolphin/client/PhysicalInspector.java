package open.dolphin.client;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableColumn;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;

/**
 * 身長体重インスペクタクラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PhysicalInspector {
    
    private ListTableModel<PhysicalModel> tableModel;
    
    private PhysicalView view;
    
    private final ChartImpl context;
    
    /**
     * PhysicalInspectorオブジェクトを生成する。
     * @param context
     */
    public PhysicalInspector(ChartImpl context) {
        this.context = context;
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
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(PhysicalInspector.class);
        
         // カラム名 & 属性値を取得するためのメソッド名
        String columsLine = bundle.getString("columnsLine.table");
        String methodsLine = bundle.getString("methodsLine.table");
        String[] columnNames = columsLine.split(",");
        String[] methodNames = methodsLine.split(",");

        // 身長体重テーブルを生成する
        tableModel = new ListTableModel<>(columnNames, 0, methodNames, null);
        view.getTable().setModel(tableModel);
        view.getTable().setFillsViewportHeight(true);
        view.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//s.oh^ 2014/04/02 閲覧権限の制御
        if(!context.isReadOnly()) {
            String toolTipText = bundle.getString("tooTipText.table");
            view.getTable().setToolTipText(toolTipText);
        }
//s.oh$        
        
        //OddEvenRowRenderer heightR = new OddEvenRowRenderer();
        StripeTableCellRenderer heightR = new StripeTableCellRenderer();
        heightR.setHorizontalAlignment(SwingConstants.LEFT);
        heightR.setTable(view.getTable());
        view.getTable().getColumnModel().getColumn(0).setCellRenderer(heightR);
        
        //OddEvenRowRenderer weightR = new OddEvenRowRenderer();
        StripeTableCellRenderer weightR = new StripeTableCellRenderer();
        weightR.setHorizontalAlignment(SwingConstants.LEFT);
        weightR.setTable(view.getTable());
        view.getTable().getColumnModel().getColumn(1).setCellRenderer(weightR);
        
        //BMIRenderer bmiR = new BMIRenderer();
        StripeTableCellRenderer bmiR = new StripeTableCellRenderer();
        bmiR.setHorizontalAlignment(SwingConstants.LEFT);
        bmiR.setTable(view.getTable());
        view.getTable().getColumnModel().getColumn(2).setCellRenderer(bmiR);
        
        StripeTableCellRenderer rendere = new StripeTableCellRenderer();
        rendere.setTable(view.getTable());
        view.getTable().getColumnModel().getColumn(3).setCellRenderer(rendere);
        
        view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
        
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
        String copyText = bundle.getString("actionText.copy");
        final AbstractAction copyAction = new AbstractAction(copyText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        view.getTable().getInputMap().put(copy, "Copy");
        view.getTable().getActionMap().put("Copy", copyAction);
        
        // 右クリックによる追加削除のメニューを登録する
        
        if (!context.isReadOnly()) {
            view.getTable().addMouseListener(new MouseAdapter() {

                private void mabeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        JPopupMenu pop = new JPopupMenu();
                        String addText = bundle.getString("menuText.add");
                        JMenuItem item = new JMenuItem(addText);
                        pop.add(item);
                        item.addActionListener((ActionEvent e1) -> {
                            PhysicalEditor npe = new PhysicalEditor(PhysicalInspector.this);
                        });
                        final int row = view.getTable().rowAtPoint(e.getPoint());
                        if (tableModel.getObject(row) != null) {
                            pop.add(new JSeparator());
                            JMenuItem item2 = new JMenuItem(copyAction);
                            pop.add(item2);
                            pop.add(new JSeparator());
                            String deleteText = bundle.getString("menuText.delete");
                            JMenuItem item3 = new JMenuItem(deleteText);
                            pop.add(item3);
                            item3.addActionListener((ActionEvent e1) -> {
                                delete(row);
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
        
        List<PhysicalModel> listH = context.getKarte().getHeights();
        List<PhysicalModel> listW = context.getKarte().getWeights();
        
        List<PhysicalModel> list = new ArrayList<>();
        
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
                listW.stream().forEach((listW1) -> {
                    list.add(listW1);
                });
            }
            
        } else if (listH != null) {
            // 身長だけの場合
            listH.stream().forEach((listH1) -> {
                list.add(listH1);
            });
            
        } else if (listW != null) {
            // 体重だけの場合
            listW.stream().forEach((listW1) -> {
                list.add(listW1);
            });
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
     * @param model
     */
    public void add(final PhysicalModel model) {

        // 同定日
        String confirmedStr = model.getIdentifiedDate();
        Date confirmed = ModelUtils.getDateTimeAsObject(confirmedStr + "T00:00:00");
        
        // 記録日
        Date recorded = new Date();

        final List<ObservationModel> addList = new ArrayList<>(2);

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
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("physical add doInBackground");
                DocumentDelegater pdl = new DocumentDelegater();
                List<Long> ids = pdl.addObservations(addList);
                return ids;
            }

            @Override
            protected void succeeded(List<Long> result) {
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("physical add succeeded");
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

        for (int i = 0; i < numRows; i++) {
            PhysicalModel pm = tableModel.getObject(rowsSelected[i]);
            if (pm!=null) {
                sb.append(pm.toString()).append("\n");
            }         
            // 最後の改行を除く
            if (sb.length()>0) {
                sb.setLength(sb.length()-1);
            }
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }


    /**
     * テーブルで選択した身長体重データを削除する。
     * @param row
     */
    public void delete(final int row) {

        PhysicalModel model = (PhysicalModel)tableModel.getObject(row);
        if (model == null) {
            return;
        }
        
        final List<Long> list = new ArrayList<>(2);
        
        if (model.getHeight() != null) {
            list.add(model.getHeightId());
        }
        
        if (model.getWeight() != null) {
            list.add(model.getWeightId());
        }
        
        DBTask task = new DBTask<Void, Void>(context) {

            @Override
            protected Void doInBackground() throws Exception {
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("physical delete doInBackground");
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                java.util.logging.Logger.getLogger(this.getClass().getName()).fine("physical delete succeeded");
                tableModel.deleteAt(row);
            }
        };
        
        task.execute();
    }
}
