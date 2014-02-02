package cancer.document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.FirstEncounter1Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;

/**
 * 瀬田クリニック初診時情報1フォームクラス。
 */
public class FirstEncounter1 extends AbstractChartDocument {

    private static final String TITLE = "初診時記録1";
    private static final String DOC_TYPE = "SETA_1";
    private FirstEncounter1Model model;
    private FirstEncounter1View view;
    private StateMgr stateMgr;
    private boolean empty;
    private Date firstEncountered;

    /** Creates a new instance of FirstEncounter1 */
    public FirstEncounter1() {
        setTitle(TITLE);
    }

    @Override
    public void start() {

        // View を生成する
        view = new FirstEncounter1View();
        PopupListener pl = new PopupListener(view.getRecorded());
        setUI(view);

        // StateContext を nodata で初期化する
        super.enter();
        stateMgr = new StateMgr();

        long pk = getContext().getKarte().getId();
        DBTask task = new GetTask(getContext(), pk);

        task.execute();
    }

    class GetTask extends DBTask<Boolean, Void> {

        private long pk;

        public GetTask(Chart ctx, long pk) {
            super(ctx);
            this.pk = pk;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            logger.debug("FirstEncounter1 GetTask doInBackground");
            SetaDelegater ddl = new SetaDelegater();
            // First0
            List<FirstEncounterModel> list0 = ddl.getFirstEncounter(pk, "SETA_0");
            if (list0 != null && list0.size() > 0) {
                FirstEncounter0Model first1 = (FirstEncounter0Model) list0.get(0);
                firstEncountered = first1.getConfirmed();
            }
            // First1
            List<FirstEncounterModel> list1 = ddl.getFirstEncounter(pk, DOC_TYPE);
            //if (ddl.isNoError()) {
                if (list1 != null && list1.size() > 0) {
                    model = (FirstEncounter1Model) list1.get(0);
                    //firstEncountered = model.getConfirmed();
                }
            //} else {
                //logger.debug(ddl.getErrorMessage());
            //}

            return true;
        }

        @Override
        protected void succeeded(Boolean result) {
            logger.debug("FirstEncounter1 GetTask succeeded");
            if (result && model != null) {
                empty = false;
                display(model);
            } else {
                empty = true;
            }
            stateMgr.start();
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        long pk = 0L;
        if (model != null && model.getId() != 0L) {
            pk = model.getId();
        }
        model = new FirstEncounter1Model();
        model.setId(pk);
        restore(model);

        DBTask task = new SaveTask(getContext(), model);
        task.execute();
    }

    class SaveTask extends DBTask<Long, Void> {

        private FirstEncounter1Model fm;

        public SaveTask(Chart ctx, FirstEncounter1Model fm) {
            super(ctx);
            this.fm = fm;
        }

        @Override
        protected Long doInBackground() throws Exception {
            logger.debug("FirstEncounter1 SaveTask doInBackground");
            SetaDelegater ddl = new SetaDelegater();
            long result = ddl.saveOrUpdateFirstEncounter(fm);
            //if (!ddl.isNoError()) {
                //System.err.println(ddl.getErrorMessage());
            //}
            return new Long(result);
        }

        @Override
        protected void succeeded(Long result) {
            logger.debug("FirstEncounter1 SaveTask succeeded");
            if (result.longValue() != 0L) {
                model.setId(result);
                stateMgr.processSavedEvent();
            }
        }
    }

    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
    }

    public void modifyKarte() {
        stateMgr.processModifyEvent();
    }

    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }

    private void addListeners() {

        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }
        };

        view.getRecorded().getDocument().addDocumentListener(dl);
        //view.getDisease().getDocument().addDocumentListener(dl);
        view.getMetastatic1().getDocument().addDocumentListener(dl);
        view.getMetastatic2().getDocument().addDocumentListener(dl);
        view.getMetastatic3().getDocument().addDocumentListener(dl);
        view.getMetastatic4().getDocument().addDocumentListener(dl);
        view.getTissueType().getDocument().addDocumentListener(dl);
        view.getUnderstanding().getDocument().addDocumentListener(dl);
        //view.getT().getDocument().addDocumentListener(dl);
        //view.getN().getDocument().addDocumentListener(dl);
        //view.getM().getDocument().addDocumentListener(dl);
        //view.getStage().getDocument().addDocumentListener(dl);
        view.getTotsuRank().getDocument().addDocumentListener(dl);
        view.getSleepRank().getDocument().addDocumentListener(dl);
        view.getMindRank().getDocument().addDocumentListener(dl);
        view.getMealRank().getDocument().addDocumentListener(dl);
        view.getSubjectiveSymptom().getDocument().addDocumentListener(dl);
        view.getPastHistory().getDocument().addDocumentListener(dl);

        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.processDirtyEvent();
            }
        };

        view.getARadio().addActionListener(al);
        view.getBRadio().addActionListener(al);
        view.getCRadio().addActionListener(al);

        view.getSyohatuRadio().addActionListener(al);
        view.getSaihatuRadio().addActionListener(al);
        view.getKankaikiRadio().addActionListener(al);
        view.getZoakukiRadio().addActionListener(al);
        view.getTmkiRadio().addActionListener(al);

        view.getZeroRadio().addActionListener(al);
        view.getOneRadio().addActionListener(al);
        view.getTwoRadio().addActionListener(al);
        view.getThreeRadio().addActionListener(al);
        view.getFourRadio().addActionListener(al);

        // FocusListener
        view.getRecorded().addFocusListener(AutoRomanListener.getInstance());
        //view.getDisease().getDocument().addDocumentListener(dl);
        view.getMetastatic1().addFocusListener(AutoKanjiListener.getInstance());
        view.getMetastatic2().addFocusListener(AutoKanjiListener.getInstance());
        view.getMetastatic3().addFocusListener(AutoKanjiListener.getInstance());
        view.getMetastatic4().addFocusListener(AutoKanjiListener.getInstance());
        view.getTissueType().addFocusListener(AutoKanjiListener.getInstance());
        view.getUnderstanding().addFocusListener(AutoKanjiListener.getInstance());
        //view.getT().getDocument().addDocumentListener(dl);
        //view.getN().getDocument().addDocumentListener(dl);
        //view.getM().getDocument().addDocumentListener(dl);
        //view.getStage().getDocument().addDocumentListener(dl);
        view.getTotsuRank().addFocusListener(AutoRomanListener.getInstance());
        view.getSleepRank().addFocusListener(AutoRomanListener.getInstance());
        view.getMindRank().addFocusListener(AutoRomanListener.getInstance());
        view.getMealRank().addFocusListener(AutoRomanListener.getInstance());

        view.getSubjectiveSymptom().addFocusListener(AutoKanjiListener.getInstance());
        view.getPastHistory().addFocusListener(AutoKanjiListener.getInstance());

        // Combo
        view.getDiseaseCombo().addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getTCombo().addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getNCombo().addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getMCombo().addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getStageCombo().addItemListener((ItemListener) EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
    }

    public void comboChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            stateMgr.processDirtyEvent();
        }
    }

    private void setComboValue(JComboBox cmb, String value) {
        if (value != null) {
            cmb.setSelectedItem(value);
        }
    }

    private void setFieldValue(JTextField tf, String value) {
        if (value != null) {
            tf.setText(value);
        }
    }

    private void setAreaValue(JTextArea ta, String value) {
        if (value != null) {
            ta.setText(value);
        }
    }

    private void selectRadio(JRadioButton[] btns, String value) {
        for (JRadioButton btn : btns) {
            if (btn.getText().trim().equals(value)) {
                btn.setSelected(true);
                break;
            }
        }
    }

    private String getComboValue(JComboBox cmb) {
        String test = (String) cmb.getSelectedItem();
        if (test != null) {
            test.trim();
        }
        return (test == null || test.equals("")) ? null : test;
    }

    private String getFieldValue(JTextField tf) {
        String text = tf.getText().trim();
        if (!text.equals("")) {
            return text;
        }
        return null;
    }

    private String getAreaValue(JTextArea ta) {
        String text = ta.getText().trim();
        if (!text.equals("")) {
            return text;
        }
        return null;
    }

    private String getRadioValue(JRadioButton[] btns) {
        String ret = null;
        for (JRadioButton btn : btns) {
            if (btn.isSelected()) {
                ret = btn.getText().trim();
                break;
            }
        }
        return ret;
    }

    private void setEditables(boolean b) {

        view.getRecorded().setEditable(b);
        view.getDiseaseCombo().setEnabled(b);
        view.getMetastatic1().setEditable(b);
        view.getMetastatic2().setEditable(b);
        view.getMetastatic3().setEditable(b);
        view.getMetastatic4().setEditable(b);
        view.getTissueType().setEditable(b);
        view.getUnderstanding().setEditable(b);
        view.getTCombo().setEnabled(b);
        view.getNCombo().setEnabled(b);
        view.getMCombo().setEnabled(b);
        view.getStageCombo().setEnabled(b);
        view.getTotsuRank().setEditable(b);
        view.getSleepRank().setEditable(b);
        view.getMindRank().setEditable(b);
        view.getMealRank().setEditable(b);
        view.getSubjectiveSymptom().setEditable(b);
        view.getPastHistory().setEditable(b);

        view.getARadio().setEnabled(b);
        view.getBRadio().setEnabled(b);
        view.getCRadio().setEnabled(b);

        view.getSyohatuRadio().setEnabled(b);
        view.getSaihatuRadio().setEnabled(b);
        view.getKankaikiRadio().setEnabled(b);
        view.getZoakukiRadio().setEnabled(b);
        view.getTmkiRadio().setEnabled(b);

        view.getZeroRadio().setEnabled(b);
        view.getOneRadio().setEnabled(b);
        view.getTwoRadio().setEnabled(b);
        view.getThreeRadio().setEnabled(b);
        view.getFourRadio().setEnabled(b);
    }

    /**
     * 初診時1情報を表示する。
     * @param model
     */
    private void display(FirstEncounter1Model model) {

        if (firstEncountered != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
                String dStr = sdf.format(firstEncountered);
                setFieldValue(view.getRecorded(), dStr);
            } catch (Exception e) {
                logger.warn(e);
            }
        }
        
        if (model == null || model.getId() == 0L) {
            return;
        }

//        // 記載日
//        Date d = model.getRecorded();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
//        String dStr = sdf.format(d);
//        setFieldValue(view.getRecorded(), dStr);

        // 病名
        setComboValue(view.getDiseaseCombo(), model.getDisease());
        setFieldValue(view.getMetastatic1(), model.getMetastatic1());
        setFieldValue(view.getMetastatic2(), model.getMetastatic2());
        setFieldValue(view.getMetastatic3(), model.getMetastatic3());
        setFieldValue(view.getMetastatic4(), model.getMetastatic4());

        // 組織型
        setFieldValue(view.getTissueType(), model.getTissueType());

        // 理解度
        selectRadio(new JRadioButton[]{view.getARadio(),
            view.getBRadio(),
            view.getCRadio()
        },
                model.getUnderstandingRank());
        setFieldValue(view.getUnderstanding(), model.getUnderstanding());

        // TNM 分類
        setComboValue(view.getTCombo(), model.getT());
        setComboValue(view.getNCombo(), model.getN());
        setComboValue(view.getMCombo(), model.getM());
        setComboValue(view.getStageCombo(), model.getStage());

        // 初診時状況
        selectRadio(new JRadioButton[]{view.getSyohatuRadio(),
            view.getSaihatuRadio(),
            view.getKankaikiRadio(),
            view.getZoakukiRadio(),
            view.getTmkiRadio()
        },
                model.getFirstState());

        // 初診時PS
        selectRadio(new JRadioButton[]{view.getZeroRadio(),
            view.getOneRadio(),
            view.getTwoRadio(),
            view.getThreeRadio(),
            view.getFourRadio()
        },
                model.getFirstPs());

        // 初診時QOL
        setFieldValue(view.getTotsuRank(), model.getTotsuRank());
        setFieldValue(view.getSleepRank(), model.getSleepRank());
        setFieldValue(view.getMindRank(), model.getMindRank());
        setFieldValue(view.getMealRank(), model.getMealRank());

        // 自覚症状
        setAreaValue(view.getSubjectiveSymptom(), model.getSubjectiveSymptom());

        // 既往歴
        setAreaValue(view.getPastHistory(), model.getPastHistory());
    }

    private void restore(FirstEncounter1Model model) {

        // 記載日
        Date d = new Date();
        if (firstEncountered == null) {
            firstEncountered = d;
        }
        model.setConfirmed(firstEncountered);
        model.setRecorded(d);
        model.setStarted(firstEncountered);
        model.setKarteBean(getContext().getKarte());
        model.setUserModel(Project.getUserModel());
        model.setStatus(IInfoModel.STATUS_FINAL);

        // 病名
        model.setDisease(getComboValue(view.getDiseaseCombo()));

        // 転移
        model.setMetastatic1(getFieldValue(view.getMetastatic1()));
        model.setMetastatic2(getFieldValue(view.getMetastatic2()));
        model.setMetastatic3(getFieldValue(view.getMetastatic3()));
        model.setMetastatic4(getFieldValue(view.getMetastatic4()));

        // 組織型
        model.setTissueType(getFieldValue(view.getTissueType()));

        // 理解度
        String value = getRadioValue(new JRadioButton[]{view.getARadio(),
            view.getBRadio(),
            view.getCRadio()
        });
        model.setUnderstandingRank(value);
        model.setUnderstanding(getFieldValue(view.getUnderstanding()));

        // TNM 分類
        model.setT(getComboValue(view.getTCombo()));
        model.setN(getComboValue(view.getNCombo()));
        model.setM(getComboValue(view.getMCombo()));
        model.setStage(getComboValue(view.getStageCombo()));

        // 初診時状況
        value = getRadioValue(new JRadioButton[]{view.getSyohatuRadio(),
            view.getSaihatuRadio(),
            view.getKankaikiRadio(),
            view.getZoakukiRadio(),
            view.getTmkiRadio()
        });
        model.setFirstState(value);

        // 初診時PS
        value = getRadioValue(new JRadioButton[]{view.getZeroRadio(),
            view.getOneRadio(),
            view.getTwoRadio(),
            view.getThreeRadio(),
            view.getFourRadio()
        });
        model.setFirstPs(value);

        // 初診時QOL
        model.setTotsuRank(getFieldValue(view.getTotsuRank()));
        model.setSleepRank(getFieldValue(view.getSleepRank()));
        model.setMindRank(getFieldValue(view.getMindRank()));
        model.setMealRank(getFieldValue(view.getMealRank()));

        // 自覚症状
        model.setSubjectiveSymptom(getAreaValue(view.getSubjectiveSymptom()));

        // 既往歴
        model.setPastHistory(getAreaValue(view.getPastHistory()));
    }

    protected String displayDate(String dStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");    // yyyy-MM-dd
            Date d = input.parse(dStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");     // yyyy年M月d日
            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    protected abstract class State {

        public abstract void enter();
    }

    class EmptyState extends State {

        @Override
        public void enter() {
            setEditables(true);
            if (firstEncountered != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dStr = sdf.format(firstEncountered);
                    setFieldValue(view.getRecorded(), dStr);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    class CleanState extends State {

        @Override
        public void enter() {
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true); // OK
        }
    }

    class DirtyState extends State {

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    class StartEditing extends State {

        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    /**
     * StateContext クラス。
     */
    class StateMgr {

        private EmptyState emptyState = new EmptyState();
        private CleanState cleanState = new CleanState();
        private DirtyState dirtyState = new DirtyState();
        private StartEditing startEditing = new StartEditing();
        private State curState;

        public StateMgr() {
        }

        public void start() {
            if (empty) {
                processEmptyEvent();
            } else {
                processCleanEvent();
            }
            addListeners();
        }

        public void enter() {
            curState.enter();
        }

        public void processEmptyEvent() {
            curState = emptyState;
            enter();
        }

        public void processCleanEvent() {
            curState = cleanState;
            enter();
        }

        public void processDirtyEvent() {

            boolean index = view.getDiseaseCombo().getSelectedIndex() != 0 ? true : false;
            boolean nonNull = getComboValue(view.getDiseaseCombo()) != null ? true : false;

            boolean newDirty = index && nonNull ? true : false;
            if (isDirtyState() != newDirty) {
                curState = newDirty ? dirtyState : curState;
                enter();
            }
        }

        public void processSavedEvent() {
            curState = cleanState;
            enter();
        }

        public void processModifyEvent() {
            curState = startEditing;
            enter();
        }

        public boolean isDirtyState() {
            return curState == dirtyState ? true : false;
        }
    }

    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;
        private JTextField tf;

        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(PopupListener.this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {
                popup = new JPopupMenu();
                CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
                cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
                cc.setCalendarRange(new int[]{-12, 0});
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                String mmldate = SimpleDate.simpleDateToMmldate(sd);
                tf.setText(displayDate(mmldate));
                popup.setVisible(false);
                popup = null;
            }
        }
    }
}
