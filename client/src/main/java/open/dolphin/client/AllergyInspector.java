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
import javax.swing.event.ListSelectionEvent;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.project.Project;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.StripeTableCellRenderer;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AllergyInspector {

    // TableModel
    private ListTableModel<AllergyModel> tableModel;

    // コンテナパネル
    private AllergyView view;

    // Chart
    private final ChartImpl context;

    /**
     * AllergyInspectorオブジェクトを生成する。
     * @param context
     */
    public AllergyInspector(ChartImpl context) {
        this.context = context;
        initComponents();
        update();
    }
    
    public Chart getContext() {
        return context;
    }

    /**
     * レイアウトパネルを返す。
     * @return
     */
    public JPanel getPanel() {
        return (JPanel) view;
    }
    
    public void clear() {
        tableModel.clear();
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private void initComponents() {

        view = new AllergyView();

//        // アレルギーテーブルを設定する
//        String[] columnNames = ClientContext.getStringArray("patientInspector.allergyInspector.columnNames");
//        String[] methodNames = ClientContext.getStringArray("patientInspector.allergyInspector.methodNames");
//        tableModel = new ListTableModel<AllergyModel>(columnNames, 0, methodNames, null);
//        view.getTable().setModel(tableModel);
//        view.getTable().setFillsViewportHeight(true);
//        //view.getTable().setRowHeight(ClientContext.getHigherRowHeight());
//        view.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
////s.oh^ 2014/04/02 閲覧権限の制御
//        //view.getTable().setToolTipText("追加・削除は右クリックで行います。");
//        if(!context.isReadOnly()) {
//            view.getTable().setToolTipText("追加・削除は右クリックで行います。");
//        }
////s.oh$
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AllergyInspector.class);
        String[] columnNames = bundle.getString("columnsLine.table").split(",");
        String[] methodNames = bundle.getString("methodsLine.table").split(",");
        
        tableModel = new ListTableModel<>(columnNames, 0, methodNames, null);
        view.getTable().setModel(tableModel);
        view.getTable().setFillsViewportHeight(true);
        view.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        if(!context.isReadOnly()) {
            String toolTipText = bundle.getString("toolTipText.table");
            view.getTable().setToolTipText(toolTipText);
        }
        
        // レンダラを設定する
        StripeTableCellRenderer rederer = new StripeTableCellRenderer();
        rederer.setTable(view.getTable());
        rederer.setDefaultRenderer();
        
        // 選択したアレルギーのメモを表示する
        ListSelectionModel slm = view.getTable().getSelectionModel();
        slm.addListSelectionListener((ListSelectionEvent lse) -> {
            if (!lse.getValueIsAdjusting()) {
                int index = view.getTable().getSelectedRow();
                AllergyModel allergy = tableModel.getObject(index);
                if (allergy!=null && allergy.getMemo()!=null) {
                    view.getMemoFld().setText(allergy.getMemo());
                } else {
                    view.getMemoFld().setText("");
                }
            }
        });

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

//minagawa^ 排他制御
        // 右クリックによる追加削除のメニューを登録する
        if (!context.isReadOnly()) {
            
            view.getTable().addMouseListener(new MouseAdapter() {

                private void mabeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        JPopupMenu pop = new JPopupMenu();
                        String addText = ClientContext.getMyBundle(AllergyInspector.class).getString("menuText.add");
                        JMenuItem item = new JMenuItem(addText);
                        pop.add(item);
                        item.addActionListener((ActionEvent e1) -> {
                            AllergyEditor ae = new AllergyEditor(AllergyInspector.this);
                        });
                        final int row = view.getTable().rowAtPoint(e.getPoint());
                        if (tableModel.getObject(row) != null) {
                            pop.add(new JSeparator());
                            JMenuItem item2 = new JMenuItem(copyAction);
                            pop.add(item2);
                            String deleteText = ClientContext.getMyBundle(AllergyInspector.class).getString("menuText.delete");
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
     * アレルギー情報を表示する。
     */
    public void update() {
        List<AllergyModel> list = context.getKarte().getAllergies();
        if (list != null && list.size() >0) {
            boolean asc = Project.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
            if (asc) {
                Collections.sort(list);
            } else {
                Collections.sort(list, Collections.reverseOrder());
            }
            tableModel.setDataProvider(list);
            scroll(asc);
        }
    }

    /**
     * アレルギーデータを追加する。
     * @param model
     */
    public void add(final AllergyModel model) {

        // GUI の同定日をTimeStampに変更する
        Date date = ModelUtils.getDateTimeAsObject(model.getIdentifiedDate()+"T00:00:00");

        final List<ObservationModel> addList = new ArrayList<>(1);

        ObservationModel observation = new ObservationModel();
        observation.setKarteBean(context.getKarte());
        observation.setUserModel(Project.getUserModel());
        observation.setObservation(IInfoModel.OBSERVATION_ALLERGY);
        observation.setPhenomenon(model.getFactor());
        observation.setCategoryValue(model.getSeverity());
        observation.setConfirmed(date);
        observation.setRecorded(new Date());
        observation.setStarted(date);
        observation.setStatus(IInfoModel.STATUS_FINAL);
        observation.setMemo(model.getMemo());
        addList.add(observation);
        
        DBTask task = new DBTask<List<Long>, Void>(context) {

            @Override
            protected List<Long> doInBackground() throws Exception {
                DocumentDelegater ddl = new DocumentDelegater();
                List<Long> ids = ddl.addObservations(addList);
                return ids;
            }
            
            @Override
            protected void succeeded(List<Long> result) {
                model.setObservationId(result.get(0));
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
            
            AllergyModel am = tableModel.getObject(rowsSelected[i]);
            if (am!=null) {
                sb.append(am.toString()).append("\n");
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
     * テーブルで選択したアレルギーを削除する。
     * @param row
     */
    public void delete(final int row) {

        AllergyModel model = (AllergyModel) tableModel.getObject(row);

        if (model == null) {
            return;
        }

        final List<Long> list = new ArrayList<>(1);
        list.add(model.getObservationId());

        DBTask task = new DBTask<Void, Void>(this.context) {

            @Override
            protected Void doInBackground() throws Exception {
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                tableModel.deleteAt(row);
            }
        };

        task.execute();
    }
}
