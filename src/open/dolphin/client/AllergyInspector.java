/*
 * AllergyInspector.java
 *
 * Created on 2007/01/18, 18:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 *
 * @author kazm
 */
public class AllergyInspector {
    
    // アレルギーテーブル
    private ObjectListTable allergyTable;

    // 削除ボタン
    private JButton deleteBtn;

    // 追加ボタン
    private JButton addBtn;

    // 要因入力用テキストフィールド
    private JTextField factorField;

    // 反応程度を選択するためのコンボボックス
    private JComboBox severityCombo;

    // 同定日を入力するためのテキストフィールド
    private JTextField confirmDateField;

    // メモを入力するためのテキストフィールド
    private JTextField memoField;

    // メモラベル
    private JLabel memoLabel;

    // コンテナパネル
    private JPanel allergyPanel;

    // ボタンコントロールフラグ
    private boolean ok;
    
    private ChartPlugin context;
    

    /**
     * AllergyInspectorオブジェクトを生成する。
     */
    public AllergyInspector(ChartPlugin context) {
        this.context = context;
        initComponents();
        update();
    }

    public void clear() {
        if (allergyTable != null) {
            allergyTable.clear();
        }
    }

    /**
     * レイアウトパネルを返す。
     * @return
     */
    public JPanel getPanel() {
        return allergyPanel;
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private void initComponents() {
        
        // アレルギテーブルの仕様
        String[] columnNames = ClientContext.getStringArray("patientInspector.allergyInspector.columnNames");
        int startNumRows = ClientContext.getInt("patientInspector.allergyInspector.startNumRows");
        String[] methodNames = ClientContext.getStringArray("patientInspector.allergyInspector.methodNames");
        allergyTable = new ObjectListTable(columnNames, startNumRows, methodNames, null);
        String[] severityValue = ClientContext.getStringArray("patientInspector.allergyInspector.severity.values"); // {"severe","moderate","mild","noReaction"};
        int[] fieldLength = ClientContext.getIntArray("patientInspector.allergyInspector.fieldLength"); // 10,15

        // 追加、削除ボタンのアイコン
        ImageIcon addIcon = ClientContext.getImageIcon("add_16.gif");
        ImageIcon deleteIcon = ClientContext.getImageIcon("del_16.gif");

        // 追加ボタン 削除ボタン
        deleteBtn = new JButton(deleteIcon);
        deleteBtn.setEnabled(false);
        deleteBtn.setMargin(new Insets(2, 2, 2, 2));
        
        addBtn = new JButton(addIcon);
        addBtn.setEnabled(false);
        addBtn.setMargin(new Insets(2, 2, 2, 2));
        
        confirmDateField = new JTextField(fieldLength[0]);
        String datePattern = ClientContext.getString("common.pattern.mmlDate");
        confirmDateField.setDocument(new RegexConstrainedDocument(datePattern));

        // 選択されたアレルギーデータを削除する
        allergyTable.addPropertyChangeListener(ObjectListTable.SELECTED_OBJECT, 
                (PropertyChangeListener) EventHandler.create(PropertyChangeListener.class, this, "rowSelectionChanged", ""));

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                addCheck();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                addCheck();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                addCheck();
            }
        };
        
        // 要因入力用テキストフィールド
        factorField = new JTextField(fieldLength[0]);
        factorField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                factorField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
        });

        // 反応程度を選択するためのコンボボックス
        severityCombo = new JComboBox(severityValue);

        // メモを入力するためのテキストフィールド
        memoField = new JTextField(fieldLength[1]);
        memoField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                memoField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
        });

        factorField.getDocument().addDocumentListener(dl);
        confirmDateField.getDocument().addDocumentListener(dl);
        severityCombo.addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "severityChanged", ""));
        
        addBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "add"));
        addBtn.setToolTipText("アレルギーを追加します");

        deleteBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "delete"));
        deleteBtn.setToolTipText("選択したアレルギーを削除します");

        // 同定日にポップアップカレンダを設定する
        new PopupListener(confirmDateField);
        confirmDateField.setToolTipText("右クリックでカレンダーがポップアップします");
        confirmDateField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                confirmDateField.getInputContext().setCharacterSubsets(null);
            }
        });

        JPanel cmdPanel = new JPanel();
        cmdPanel.add(deleteBtn);
        cmdPanel.add(addBtn);
        
        // リソースからラベル文字列を取得する
        String[] labelTexts = ClientContext.getStringArray("patientInspector.allergyInspector.labelTexts");// 要因,反応程度,同定日,メモ

        // 要因ラベル
        JLabel factorLabel = new JLabel(labelTexts[0], SwingConstants.RIGHT);

        // 反応程度ラベル
        JLabel severityLabel = new JLabel(labelTexts[1], SwingConstants.RIGHT);

        // 同定日ラベル
        JLabel confirmDateLabel = new JLabel(labelTexts[2], SwingConstants.RIGHT);
        
        memoLabel = new JLabel(labelTexts[3], SwingConstants.RIGHT);

        GridBagBuilder gb = new GridBagBuilder();
        gb.add(factorLabel, 0, 0, GridBagConstraints.EAST);
        gb.add(factorField, 1, 0, GridBagConstraints.WEST);
        gb.add(severityLabel, 0, 1, GridBagConstraints.EAST);
        gb.add(severityCombo, 1, 1, GridBagConstraints.WEST);
        gb.add(confirmDateLabel, 0, 2, GridBagConstraints.EAST);
        gb.add(confirmDateField, 1, 2, GridBagConstraints.WEST);
        gb.add(memoLabel, 0, 3, GridBagConstraints.EAST);
        gb.add(memoField, 1, 3, GridBagConstraints.WEST);
        JPanel sip = gb.getProduct();

        allergyPanel = new JPanel();
        allergyPanel.setLayout(new BoxLayout(allergyPanel, BoxLayout.Y_AXIS));
        allergyPanel.add(allergyTable.getPanel());
        allergyPanel.add(cmdPanel);
        allergyPanel.add(sip);

        //本日オブジェクト
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String todayString = sdf.format(date);
        confirmDateField.setText(todayString);
    }

    /**
     * アレルギー情報を表示する。
     */
    @SuppressWarnings("unchecked")
    public void update() {
        List list = context.getKarte().getEntryCollection("allergy");
        allergyTable.setObjectList(list);
    }

    /**
     * 追加ボタンのコントロールを行う／
     */
    private void addCheck() {
        boolean newOk
                = (factorField.getText().trim().equals("") == false && confirmDateField.getText().trim().equals("") == false)
                ? true
                : false;

        if (newOk != ok) {
            addBtn.setEnabled(newOk);
            ok = newOk;
        }
    }
    
    public void rowSelectionChanged(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(
                ObjectListTable.SELECTED_OBJECT)) {
            Object[] selected = (Object[]) e.getNewValue();
            boolean canDelete
                    = (selected != null && selected.length > 0)
                    ? true
                    : false;
            if (canDelete != deleteBtn.isEnabled()) {
                deleteBtn.setEnabled(canDelete);
            }
        }
    }
    
    public void severityChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            addCheck();
        }
    }

    /**
     * アレルギーデータを追加する。
     */
    public void add() {

        final AllergyModel model = new AllergyModel();
        model.setFactor(factorField.getText().trim());
        model.setSeverity((String)severityCombo.getSelectedItem());
        model.setIdentifiedDate(confirmDateField.getText().trim());
        model.setMemo(memoField.getText().trim());

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

        //worker thread
        Runnable r = new Runnable() {
            public void run() {
                fireStart();
                DocumentDelegater ddl = new DocumentDelegater();
                // 登録時にレコードIDを返す
                List<Long> ids = ddl.addObservations(addList);
                model.setObservationId(ids.get(0));
                allergyTable.addRow(model);
                fireStop();
            }
        };
        addBtn.setEnabled(false);
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * 追加ボタンを非活性化し progressbar を開始する。
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
     * ProgressBar をストップし追加ボタンの enable 属性を制御する。
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
     * テーブルで選択したアレルギーを削除する。
     */
    public void delete() {
        Object[] selected = allergyTable.getSelectedObject();
        AllergyModel model = (AllergyModel) selected[0];

        final List<Long> list = new ArrayList<Long>(1);
        list.add(new Long(model.getObservationId()));

        Runnable r = new Runnable() {
            public void run() {
                fireStart();
                DocumentDelegater ddl = new DocumentDelegater();
                ddl.removeObservations(list);
                allergyTable.deleteSelectedRows();
                fireStop();
            }
        };
        deleteBtn.setEnabled(false);
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
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
                popup = null;
            }
        }
    }
}
