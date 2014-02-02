package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.print.PageFormat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * 海老原先生フォームの紹介状。がん相談蕩々
 */
public class LetterImpl extends AbstractChartDocument implements Letter {

    private static final String TITLE = "診療情報提供書";
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
    
    private void addListeners() {
        // 
        // DirtyListener を登録する
        //
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
    }

    // Form に入力されたデータをモデルへ格納する
    private void restore(boolean save) {

        if (save) {
            // 保存タスクからコールされた場合は
            // 日時を設定する
            Date d = new Date();
            model.setConfirmed(d);
            model.setRecorded(d);
            model.setStarted(d);
        }
        model.setStatus(IInfoModel.STATUS_FINAL);
        model.setKarte(getContext().getKarte());
        model.setCreator(Project.getUserModel());

        // 紹介先情報を設定する
        model.setConsultantHospital(getFieldValue(view.getCHospital()));
        model.setConsultantDept(getFieldValue(view.getCDept()));
        model.setConsultantDoctor(getFieldValue(view.getCDoctor()));

        // 紹介内容を設定する
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
        this.model.setRecorded(d);
        this.model.setStarted(d);

        // 患者情報を設定する
        PatientModel patient = getContext().getPatient();
        this.model.setPatientName(patient.getFullName());
        this.model.setPatientGender(patient.getGenderDesc());
        this.model.setPatientBirthday(patient.getBirthday());
        this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));

        // 紹介元医師情報（ログインユーザ）を設定する
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

        // Listners を追加する
        addListeners();

        // 状態制御を開始する
        stateMgr = new StateMgr();
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        restore(true);

        DBTask task = new DBTask<Boolean, Void>(getContext()) {

            @Override
            protected Boolean doInBackground() throws Exception {

                SetaDelegater ddl = new SetaDelegater();
                long result = ddl.saveOrUpdateLetter(model);
                if (ddl.isNoError()) {
                    model.setId(result);
                    return new Boolean(true);
                } else {
                    throw new Exception(ddl.getErrorMessage());
                }
            }

            @Override
            protected void succeeded(Boolean result) {
                stateMgr.processSavedEvent();
            }
        };

        task.execute();
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
        
        restore(false);

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

    /**
     * 紹介状の PDF を作成する。
     */
    public void makePDF() {

        if (this.model == null) {
            return;
        }

        Runnable r = new Runnable() {

            @Override
            public void run() {

                PDFLetterMaker pdf = new PDFLetterMaker();
                String pdfDir = Project.getPreferences().get("pdfStore", System.getProperty("user.dir"));
                pdf.setDocumentDir(pdfDir);
                pdf.setModel(model);
                boolean result = pdf.create();
                
                if (result) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(pdf.getDocumentDir());
                    sb.append(File.separator);
                    sb.append(pdf.getFileName());
                    String path = sb.toString();

                    try {
                        File target = new File(path);
                        if (target.exists()) {
                            if (ClientContext.isMac()) {
                                new ProcessBuilder("open", path).start();
                            } else if (ClientContext.isWin()) {
                                new ProcessBuilder("cmd.exe", "/c", path).start();
                            } else {
                                // 
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    /**
     * 抽象 State クラス。
     */
    protected abstract class State {

        public abstract void enter();
    }

    /**
     * Empty State クラス。
     * 表示するモデルがない状態
     */
    class EmptyState extends State {

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, false);           // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
        }
    }
    
    /**
     * 編集を開始する状態。
     */
    class StartEditingState extends State {

        /**
         * フィールドの編集を可にする。
         */
        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
        }
    }

    /**
     * Dirty State クラス。
     */
    class DirtyState extends State {

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);             // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
        }
    }

    /**
     * Saved State クラス。
     */
    class CleanState extends State {

        /**
         * 保存された状態 - フィールドの編集を不可にする。
         */
        @Override
        public void enter() {
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);     // 修正
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

            boolean newDirty = (
                    getFieldValue(view.getDisease()) != null &&
                    getFieldValue(view.getPurpose()) != null &&
                    getAreaValue(view.getClinicalCourse()) != null)
                    ? true
                    : false;
            
            // 必須入力が残っている状態は empty とする
            curState = newDirty ? dirtyState : emptyState;
            curState.enter();
        }

        // 保存イベントを受け cleanState へ遷移する
        public void processSavedEvent() {
            curState = cleanState;
            curState.enter();
        }

        // 修正イベントを受け startEditingState へ遷移する
        public void processModifyKarteEvent() {
            curState = startEditingState;
            curState.enter();
        }
        
        public boolean isDirtyState() {
            return curState == dirtyState ? true : false;
        }
    }
}














