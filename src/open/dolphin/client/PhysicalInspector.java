/*
 * PhysicalInspector.java
 *
 * Created on 2007/01/18, 18:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.im.InputSubset;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 *
 * @author kazm
 */
public class PhysicalInspector {
    
    private ObjectListTable physicaltable;

    // 削除ボタン
    private JButton deleteBtn;

    // 追加ボタン
    private JButton addBtn;

    
    // 身長テキストフィールド
    private JTextField heightField;

    // 体重テキストフィールド
    private JTextField weightField;

    // 測定日テキストフィールド
    private JTextField confirmDateField;

    // レイアウト用パネル
    private JPanel physicalPanel;

    // ボタンコントロールフラグ
    private boolean ok;
    
    private ChartPlugin context;

    /**
     * PhysicalInspectorオブジェクトを生成する。
     */
    public PhysicalInspector(ChartPlugin context) {
        this.context = context;
        initComponents();
        update();
    }

    public void clear() {
        if (physicaltable != null) {
            physicaltable.clear();
        }
    }

    /**
     * レイアウトパネルを返す。
     * @return レイアウトパネル
     */
    public JPanel getPanel() {
        return physicalPanel;
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private void initComponents() {
        
         // カラム名
        String[] columnNames = ClientContext.getStringArray("patientInspector.physicalInspector.columnNames"); // {"身長","体重","BMI","測定日"};

        // テーブルの初期行数
        int startNumRows = ClientContext.getInt("patientInspector.physicalInspector.startNumRows");

        // 属性値を取得するためのメソッド名
        String[] methodNames = ClientContext.getStringArray("patientInspector.physicalInspector.methodNames"); // {"getHeight","getWeight","getBMI","getConfirmDate"};

        // 追加ボタン用のアイコン
        ImageIcon addIcon = ClientContext.getImageIcon("add_16.gif");

        // 削除ボタン用のアイコン
        ImageIcon deleteIcon = ClientContext.getImageIcon("del_16.gif");
        
        // テキストフィールドの長さ
        int[] fieldLength = ClientContext.getIntArray("patientInspector.physicalInspector.fieldLength"); // 5,5,5?
        
        // ラベル配列
        String[] labelTexts = ClientContext.getStringArray("patientInspector.physicalInspector.labelTexts");// 身長,cm,体重,Kg,BMI値,%,測定日

        // 身長ラベル
        JLabel heightLabel = new JLabel(labelTexts[0], SwingConstants.RIGHT);

        // 身長単位ラベル
        JLabel heightUnitLabel = new JLabel(labelTexts[1], SwingConstants.RIGHT);

        // 体重ラベル
        JLabel weightLabel = new JLabel(labelTexts[2], SwingConstants.RIGHT);

        // 体重単位ラベル
        JLabel weightUnitLabel = new JLabel(labelTexts[3], SwingConstants.RIGHT);

        // 測定日ラベル
        JLabel confirmDateLabel = new JLabel(labelTexts[6], SwingConstants.RIGHT);
        
        
        // 身長体重テーブルを生成する
        physicaltable = new ObjectListTable(columnNames, startNumRows, methodNames, null);
        physicaltable.getTable().getColumnModel().getColumn(2).setCellRenderer(new BMIRenderer());
        
        // GUI コンポーネントを生成する
        physicalPanel = new JPanel();
        addBtn = new JButton(addIcon);
        deleteBtn = new JButton(deleteIcon);
        heightField = new JTextField(fieldLength[0]);
        weightField = new JTextField(fieldLength[1]);
        confirmDateField = new JTextField(10);
    
        addBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        addBtn.setMargin(new Insets(2, 2, 2, 2));
        deleteBtn.setMargin(new Insets(2, 2, 2, 2));
        
        String datePattern = ClientContext.getString("common.pattern.mmlDate");
        confirmDateField.setDocument(new RegexConstrainedDocument(datePattern));

        // 選択された physical データを削除する
        physicaltable.addPropertyChangeListener(
                ObjectListTable.SELECTED_OBJECT, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "rowSelectionChanged", ""));

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                addCheck();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                addCheck();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        };

        heightField.getDocument().addDocumentListener(dl);
        weightField.getDocument().addDocumentListener(dl);
        confirmDateField.getDocument().addDocumentListener(dl);

        addBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "add"));
        addBtn.setToolTipText("身長体重データを追加します");

        deleteBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "delete"));
        deleteBtn.setToolTipText("選択した身長体重データを削除します");

        // 同定日にポップアップカレンダを設定する
        new PopupListener(confirmDateField);
        confirmDateField.setToolTipText("右クリックでカレンダーがポップアップします");
        
        //
        // 身長、体重、同定日フィールドで自動的に IME OFF にする
        //
        heightField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(null);
            }
        });
        
        weightField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(null);
            }
        });
        
        confirmDateField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                JTextField tf = (JTextField) event.getSource();
                tf.getInputContext().setCharacterSubsets(null);
            }
        });
        
        JPanel cmdPanel = new JPanel();
        cmdPanel.add(deleteBtn);
        cmdPanel.add(addBtn);

        GridBagBuilder gb = new GridBagBuilder();
        gb.add(heightLabel, 0, 0, GridBagConstraints.EAST);
        gb.add(createUnitField(heightField, heightUnitLabel), 1, 0,
                GridBagConstraints.WEST);

        gb.add(weightLabel, 0, 1, GridBagConstraints.EAST);
        gb.add(createUnitField(weightField, weightUnitLabel), 1, 1,
                GridBagConstraints.WEST);

        // gb.add(bmiLabel, 0, 2, GridBagConstraints.EAST);
        // gb.add(createUnitField(bmiField,bmiUnitLabel), 1, 2,
        // GridBagConstraints.WEST);

        gb.add(confirmDateLabel, 0, 2, GridBagConstraints.EAST);
        gb.add(confirmDateField, 1, 2, GridBagConstraints.WEST);

        JPanel sip = gb.getProduct();

        physicalPanel.setLayout(new BoxLayout(physicalPanel,
                BoxLayout.Y_AXIS));
        physicalPanel.add(physicaltable.getScroller());
        physicalPanel.add(cmdPanel);
        physicalPanel.add(sip);

        //本日オブジェクト
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String todayString = sdf.format(date);
        confirmDateField.setText(todayString);
    }

    /**
     * 身長体重データを表示する。
     */
    @SuppressWarnings("unchecked")
    public void update() {
        
        List listH = context.getKarte().getEntryCollection("height");
        List listW = context.getKarte().getEntryCollection("weight");
        
//        if (listH != null && listW != null && listH.size() == listW.size()) {
//            List list = new ArrayList(listH.size());
//            for (int i = 0; i < listH.size(); i++) {
//                PhysicalModel h = (PhysicalModel) listH.get(i);
//                PhysicalModel w = (PhysicalModel) listW.get(i);
//                PhysicalModel m = new PhysicalModel();
//                m.setHeightId(h.getHeightId());
//                m.setHeight(h.getHeight());
//                m.setWeightId(w.getWeightId());
//                m.setWeight(w.getWeight());
//                m.setIdentifiedDate(h.getIdentifiedDate());
//                list.add(m);
//            }
//            physicaltable.setObjectList(list);
//        }
        
        List list = new ArrayList();
        
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
                Object found = null;
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
        
        physicaltable.setObjectList(list);
    }

    /**
     * 追加ボタンを制御する。
     */
    private void addCheck() {

//        boolean newOk
//                = (heightField.getText().trim().equals("") == false
//                && weightField.getText().trim().equals("") == false
//                && confirmDateField.getText().trim().equals("") == false)
//                ? true
//                : false;
        
        //
        // 身長と体重を独立にする 2007-04-12
        //
        boolean newOk = heightField.getText().trim().equals("") 
                     && weightField.getText().trim().equals("")
                     ? false
                     : true;
        
        newOk = newOk && (confirmDateField.getText().trim().equals("") == false) ? true : false;

        if (newOk != ok) {
            addBtn.setEnabled(newOk);
            ok = newOk;
        }
    }
    
    public void rowSelectionChanged(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(
                ObjectListTable.SELECTED_OBJECT)) {
            Object[] selected = (Object[]) e.getNewValue();
            boolean canDelete = (selected != null && selected.length > 0) ? true
                    : false;
            if (canDelete != deleteBtn.isEnabled()) {
                deleteBtn.setEnabled(canDelete);
            }
        }
    }

    /**
     * 身長体重データを追加する。
     */
    public void add() {

        try {
            String h = heightField.getText().trim();
            String w = weightField.getText().trim();
            final PhysicalModel model = new PhysicalModel();
            
            if (!h.equals("")) {
                model.setHeight(h);
            }
            if (!w.equals("")) {
                model.setWeight(w);
            }

            // 同定日
            String confirmedStr = confirmDateField.getText().trim();
            model.setIdentifiedDate(confirmedStr);
            Date confirmed = ModelUtils.getDateTimeAsObject(confirmedStr + "T00:00:00");

            // 記録日
            Date recorded = new Date();
            //model.setMemo(ModelUtils.getDateAsString(recorded));

            final List<ObservationModel> addList = new ArrayList<ObservationModel>(2);

            if (model.getHeight() != null) {
                ObservationModel observation = new ObservationModel();
                observation.setKarte(context.getKarte());
                observation.setCreator(Project.getUserModel());
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
                observation.setKarte(context.getKarte());
                observation.setCreator(Project.getUserModel());
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
            
            if (addList.size() == 0) {
                return;
            }

            // Worker thread
            Runnable r = new Runnable() {
                public void run() {
                    fireStart();
                    DocumentDelegater pdl = new DocumentDelegater();
                    List<Long> ids = pdl.addObservations(addList);
                    
                    if (model.getHeight() != null && model.getWeight() != null) {
                        model.setHeightId(ids.get(0));
                        model.setWeightId(ids.get(1));
                    } else if (model.getHeight() != null) {
                        model.setHeightId(ids.get(0));
                    } else {
                        model.setWeightId(ids.get(0));
                    }
                    
                    physicaltable.addRow(model);
                    fireStop();
                }
            };
            addBtn.setEnabled(false);
            Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        } catch (Exception e) {
            // input error
        }
    }

    /**
     * 追加ボタンを disabled にし、progressbar を開始する。
     */
    private void fireStart() {
        Runnable awt = new Runnable() {
            public void run() {
                context.getStatusPanel().start();
            }
        };
        SwingUtilities.invokeLater(awt);
    }

    /**
     * ProgressBar を停止する。
     */
    private void fireStop() {
        Runnable awt = new Runnable() {
            public void run() {
                context.getStatusPanel().stop();
            }
        };
        SwingUtilities.invokeLater(awt);
    }

    /**
     * テーブルで選択した身長体重データを削除する。
     */
    public void delete() {

        Object[] selected = physicaltable.getSelectedObject();
        PhysicalModel model = (PhysicalModel) selected[0];
        final List<Long> list = new ArrayList<Long>(2);
        
        if (model.getHeight() != null) {
            list.add(new Long(model.getHeightId()));
        }
        
        if (model.getWeight() != null) {
            list.add(new Long(model.getWeightId()));
        }

        Runnable r = new Runnable() {
            public void run() {
                fireStart();
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                physicaltable.deleteSelectedRows();
                fireStop();
            }
        };
        deleteBtn.setEnabled(false);
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    private JPanel createUnitField(JTextField tf, JLabel unit) {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.add(tf);
        p.add(unit);
        return p;
    }

    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;

        private JTextField tf;

        // private LiteCalendarPanel calendar;

        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(this);
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(context.getContext().getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[] { -12, 0 });
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                tf.setText(SimpleDate.simpleDateToMmldate(sd));
                popup.setVisible(false);
                // calendar = null;
                popup = null;
            }
        }
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
                setBackground(ClientContext.getColor("color.even"));
            } else {
                setBackground(ClientContext.getColor("color.odd"));
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            
            PhysicalModel h = (PhysicalModel) physicaltable.getTableModel().getObject(row);
            
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
