package open.dolphin.client;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.project.Project;
import open.dolphin.table.ObjectReflectTableModel;
import open.dolphin.table.OddEvenRowRenderer;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AllergyInspector {

    // TableModel
    private ObjectReflectTableModel tableModel;

    // コンテナパネル
    private AllergyView view;

    // Chart
    private ChartImpl context;

    /**
     * AllergyInspectorオブジェクトを生成する。
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

        // アレルギーテーブルを設定する
        String[] columnNames = ClientContext.getStringArray("patientInspector.allergyInspector.columnNames");
        int startNumRows = ClientContext.getInt("patientInspector.allergyInspector.startNumRows");
        String[] methodNames = ClientContext.getStringArray("patientInspector.allergyInspector.methodNames");
        tableModel = new ObjectReflectTableModel(columnNames, startNumRows, methodNames, null);
        view.getTable().setModel(tableModel);
        view.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       
        // レンダラを設定する
        view.getTable().setDefaultRenderer(Object.class, new OddEvenRowRenderer());

        // 右クリックによる追加削除のメニューを登録する
        view.getTable().addMouseListener(new MouseAdapter() {

            private void mabeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem item = new JMenuItem("追加");
                    pop.add(item);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            new AllergyEditor(AllergyInspector.this);
                        }
                    });
                    final int row = view.getTable().rowAtPoint(e.getPoint());
                    if (tableModel.getObject(row) != null) {
                        pop.add(new JSeparator());
                        JMenuItem item2 = new JMenuItem("削除");
                        pop.add(item2);
                        item2.addActionListener(new ActionListener() {
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
     * アレルギー情報を表示する。
     */
    public void update() {
        List list = context.getKarte().getEntryCollection("allergy");
        if (list != null && list.size() >0) {
            boolean asc = Project.getPreferences().getBoolean(Project.DOC_HISTORY_ASCENDING, false);
            if (asc) {
                Collections.sort(list);
            } else {
                Collections.sort(list, Collections.reverseOrder());
            }
            tableModel.setObjectList(list);
            scroll(asc);
        }
    }

    /**
     * アレルギーデータを追加する。
     */
    public void add(final AllergyModel model) {

        // GUI の同定日をTimeStampに変更する
        Date date = ModelUtils.getDateTimeAsObject(model.getIdentifiedDate()+"T00:00:00");

        final List<ObservationModel> addList = new ArrayList<ObservationModel>(1);

        ObservationModel observation = new ObservationModel();
        observation.setKarte(context.getKarte());
        observation.setCreator(Project.getUserModel());
        observation.setObservation(IInfoModel.OBSERVATION_ALLERGY);
        observation.setPhenomenon(model.getFactor());
        observation.setCategoryValue(model.getSeverity());
        observation.setConfirmed(date);
        observation.setRecorded(new Date());
        observation.setStarted(date);
        observation.setStatus(IInfoModel.STATUS_FINAL);
        observation.setMemo(model.getMemo());
        addList.add(observation);
        
        DBTask task = new DBTask<List<Long>>(context) {

            @Override
            protected List<Long> doInBackground() throws Exception {
                logger.debug("allergy add doInBackground");
                DocumentDelegater ddl = new DocumentDelegater();
                List<Long> ids = ddl.addObservations(addList);
                return ids;
            }
            
            @Override
            protected void succeeded(List<Long> result) {
                logger.debug("allergy add succeeded");
                model.setObservationId(result.get(0));
                boolean asc = Project.getPreferences().getBoolean(Project.DOC_HISTORY_ASCENDING, false);
                if (asc) {
                    tableModel.addRow(model);
                } else {
                    tableModel.addRow(0, model);
                }
                scroll(asc);
            }
        };
        
        task.execute();
    }

    /**
     * テーブルで選択したアレルギーを削除する。
     */
    public void delete(final int row) {

        AllergyModel model = (AllergyModel) tableModel.getObject(row);

        if (model == null) {
            return;
        }

        final List<Long> list = new ArrayList<Long>(1);
        list.add(new Long(model.getObservationId()));

        DBTask task = new DBTask<Void>(this.context) {

            @Override
            protected Void doInBackground() throws Exception {
                logger.debug("allergy delete doInBackground");
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                logger.debug("allergy delete succeeded");
                tableModel.deleteRow(row);
            }
        };

        task.execute();
    }
}
