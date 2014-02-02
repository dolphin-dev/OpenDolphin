package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.print.PageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import org.jdesktop.application.Task;

/**
 * 海老原先生フォームの紹介状。がん相談蕩々
 */
public class LetterImpl extends AbstractChartDocument implements Letter {

    private static final String TITLE = "紹介状";
    private TouTouLetter model;
    private LetterView2 view;
    private StateMgr stateMgr;

    /** Creates a new instance of LetterDocument */
    public LetterImpl() {
        setTitle(TITLE);
    }

    private void setModelValue(JTextField tf, String value) {
        if (value != null) {
            tf.setText(value);
        }
    }

    private String getFieldValue(JTextField tf) {
        String ret = tf.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    private String getAreaValue(JTextArea ta) {
        String ret = ta.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    private String getDateAsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
        return sdf.format(date);
    }

    private String getDateString(String mmlDate) {
        Date d = ModelUtils.getDateAsObject(mmlDate);
        return getDateAsString(d);
    }

    private void displayModel(TouTouLetter model) {

        String dateStr = getDateAsString(model.getConfirmed());
        view.getConfirmed().setText(dateStr);

        String birthdayStr = getDateString(model.getPatientBirthday());

        setModelValue(view.getPatientName(), model.getPatientName());
        setModelValue(view.getPatientGender(), model.getPatientGender());
        setModelValue(view.getPatientBirthday(), birthdayStr);
        setModelValue(view.getPatientAge(), model.getPatientAge());
        setModelValue(view.getPatientName(), model.getPatientName());

//        setModelValue(view.getMyHospital(), model.getClientHospital());
//        setModelValue(view.getMyName(), model.getClientName());
//        setModelValue(view.getAddress(), model.getClientAddress());
//        setModelValue(view.getTelephone(), model.getClientTelephone());
    }

    private void restore(TouTouLetter model) {

        Date d = new Date();
        model.setConfirmed(d);
        model.setRecorded(d);
        model.setStarted(d);
        model.setStatus(IInfoModel.STATUS_FINAL);
        model.setKarte(getContext().getKarte());
        model.setCreator(Project.getUserModel());

        model.setConsultantHospital(getFieldValue(view.getCHospital()));
        model.setConsultantDept(getFieldValue(view.getCDept()));
        model.setConsultantDoctor(getFieldValue(view.getCDoctor()));

        model.setDisease(getFieldValue(view.getDisease()));
        model.setPurpose(getFieldValue(view.getPurpose()));
        model.setClinicalCourse(getAreaValue(view.getClinicalCourse()));
        model.setPastFamily(getAreaValue(view.getPastFamily()));
        model.setMedication(getAreaValue(view.getMedication()));
        model.setRemarks(getFieldValue(view.getRemarks()));
    }

    @Override
    public void start() {

        // 紹介状モデルを生成する
        this.model = new TouTouLetter();

        // 確定日として現在を表紙させる
        Date d = new Date();
        this.model.setConfirmed(d);

        PatientModel patient = getContext().getPatient();
        this.model.setPatientName(patient.getFullName());
        this.model.setPatientGender(patient.getGenderDesc());
        this.model.setPatientBirthday(patient.getBirthday());
        this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));

        UserModel user = Project.getUserModel();
        this.model.setClientHospital(user.getFacilityModel().getFacilityName());
        this.model.setClientName(user.getCommonName());
        this.model.setClientAddress(user.getFacilityModel().getAddress());
        this.model.setClientTelephone(user.getFacilityModel().getTelephone());
        this.model.setClientFax(null);

        // view を生成する
        this.view = new LetterView2();
        JScrollPane scroller = new JScrollPane(this.view);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller);

        // モデルを表示する
        displayModel(this.model);

        // 
        // DirtyListener を登録する
        //
        DocumentListener dl = new DocumentListener() {

            public void insertUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            public void changedUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }
        };
        view.getCHospital().getDocument().addDocumentListener(dl);
        view.getCDept().getDocument().addDocumentListener(dl);
        view.getCDoctor().getDocument().addDocumentListener(dl);
        view.getDisease().getDocument().addDocumentListener(dl);
        view.getPurpose().getDocument().addDocumentListener(dl);
        view.getPastFamily().getDocument().addDocumentListener(dl);
        view.getClinicalCourse().getDocument().addDocumentListener(dl);
        view.getMedication().getDocument().addDocumentListener(dl);
        view.getRemarks().getDocument().addDocumentListener(dl);
        
        view.getCHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getCDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getCDoctor().addFocusListener(AutoKanjiListener.getInstance());
        view.getDisease().addFocusListener(AutoKanjiListener.getInstance());
        view.getPurpose().addFocusListener(AutoKanjiListener.getInstance());
        view.getPastFamily().addFocusListener(AutoKanjiListener.getInstance());
        view.getClinicalCourse().addFocusListener(AutoKanjiListener.getInstance());
        view.getMedication().addFocusListener(AutoKanjiListener.getInstance());
        view.getRemarks().addFocusListener(AutoKanjiListener.getInstance());

        // 状態制御を開始する
        stateMgr = new StateMgr();
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        restore(this.model);

        Task task = new Task<Void, Void>(app) {

            protected Void doInBackground() {

                SetaDelegater ddl = new SetaDelegater();
                long result = ddl.saveOrUpdateLetter(model);
                if (ddl.isNoError()) {
                    model.setId(result);
                }
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                stateMgr.processSavedEvent();
            }
        };

        taskService.execute(task);
    }

    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
    }

    @Override
    public void print() {
        
        if (this.model == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PDFファイルを作成しますか?");

        int option = JOptionPane.showOptionDialog(
                getContext().getFrame(),
                sb.toString(),
                ClientContext.getFrameTitle("紹介状印刷"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"PDF作成", "画面印刷", "取消し"},
                "PDF作成");

        if (option == 0) {
            makePDF();
        } else if (option == 1) {
            PageFormat pageFormat = getContext().getContext().getPageFormat();
            String name = getContext().getPatient().getFullName();
            Panel2 panel = (Panel2) this.view;
            panel.printPanel(pageFormat, 1, false, name, 0);
        }
    }

    public void makePDF() {

        if (this.model == null) {
            return;
        }

        Runnable r = new Runnable() {

            public void run() {

                PDFLetterMaker pdf = new PDFLetterMaker();
                String pdfDir = Project.getPreferences().get("pdfStore", System.getProperty("user.dir"));
                pdf.setDocumentDir(pdfDir);
                pdf.setModel(model);
                final boolean result = pdf.create();
                final String fileName = pdf.getFileName();
                final String dir = pdf.getDocumentDir();

                Runnable awt = new Runnable() {

                    public void run() {
                        if (result) {
                            StringBuilder sb = new StringBuilder();
                            //String fileName = pdf.getFileName();
                            //String dir = pdf.getDocumentDir();
                            sb.append(fileName);
                            sb.append("を");
                            sb.append("\n");
                            sb.append(dir);
                            sb.append("に保存しました。");
                            sb.append("\n");
                            sb.append("PDF ビュワーを起動し印刷してください。");
                            JOptionPane.showMessageDialog(
                                    getContext().getFrame(),
                                    sb.toString(),
                                    ClientContext.getFrameTitle("紹介状作成"),
                                    JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            JOptionPane.showMessageDialog(
                                    getContext().getFrame(),
                                    "紹介状PDFファイルを生成することができません。",
                                    ClientContext.getFrameTitle("紹介状作成"),
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                };
                EventQueue.invokeLater(awt);
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }

    /**
     * 紹介状を変更する。
     */
    public void modifyKarte() {
        stateMgr.processModifyKarteEvent();
    }

    private void setEditables(boolean b) {

        view.getConfirmed().setEditable(b);
        view.getCHospital().setEditable(b);
        view.getCDept().setEditable(b);
        view.getCDoctor().setEditable(b);
        view.getDisease().setEditable(b);
        view.getPurpose().setEditable(b);
        view.getPastFamily().setEditable(b);
        view.getClinicalCourse().setEditable(b);
        view.getMedication().setEditable(b);
        view.getRemarks().setEditable(b);
    }

    /**
     * 抽象 State クラス。
     */
    protected abstract class State {

        public abstract void enter();
    }

    /**
     * Claen State クラス。
     */
    class EmptyState extends State {

        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_PRINT, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    /**
     * Dirty State クラス。
     */
    class DirtyState extends State {

        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    /**
     * Saved State クラス。
     */
    class CleanState extends State {

        public void enter() {
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);
        }
    }

    class StartEditingState extends State {

        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    /**
     * State の Context クラス。
     */
    class StateMgr {

        private EmptyState emptyState = new EmptyState();
        private DirtyState dirtyState = new DirtyState();
        private CleanState cleanState = new CleanState();
        private StartEditingState startEditingState = new StartEditingState();
        private State curState;

        public StateMgr() {
            curState = emptyState;
            enter();
        }

        public void enter() {
            curState.enter();
        }

        public void processDirtyEvent() {

            boolean newDirty = (getFieldValue(view.getCHospital()) != null &&
                    getFieldValue(view.getDisease()) != null &&
                    getFieldValue(view.getPurpose()) != null &&
                    getAreaValue(view.getClinicalCourse()) != null)
                    ? true
                    : false;
            if (isDirtyState() != newDirty) {
                curState = newDirty ? dirtyState : emptyState;
                curState.enter();
            }
        }

        public void processSavedEvent() {
            curState = cleanState;
            curState.enter();
        }

        public void processModifyKarteEvent() {
            curState = startEditingState;
            curState.enter();
        }

        public boolean isDirtyState() {
            return curState == dirtyState ? true : false;
        }
    }
}














