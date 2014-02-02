package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * 海老原先生フォームの紹介状。がん相談蕩々
 */
public class LetterReplyImpl2 extends AbstractChartDocument implements Letter {

    private static final String TITLE = "ご報告";
    private TouTouReply model;
    private LetterReplyView2 view;
    private StateMgr stateMgr;

    /** Creates a new instance of LetterDocument */
    public LetterReplyImpl2() {
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

    private void displayModel(TouTouReply m) {

        String dateStr = getDateAsString(m.getConfirmed());
        view.getConfirmed().setText(dateStr);
        view.getVisited().setText(m.getVisited());

        String birthdayStr = getDateString(m.getPatientBirthday());

        setModelValue(view.getPatientName(), m.getPatientName());
        setModelValue(view.getPatientBirthday(), birthdayStr);
        
        // 病院名、住所、電話番号
        UserModel user = Project.getUserModel();
        FacilityModel fm = user.getFacilityModel();
        setModelValue(view.getConsultantAddress(), fm.getAddress());
        setModelValue(view.getConsultantTelephone(), fm.getTelephone());
        setModelValue(view.getConsultantHospital(), m.getConsultantHospital());
        setModelValue(view.getConsultantDoctor(), m.getConsultantDoctor());
    }

    /**
     * 入力データをモデルの値に格納する。
     * @param save save() からコールされた場合 true
     */
    private void restore(boolean save) {

        // save からコールされた場合はその時の Date を設定する
        if (save) {
            Date d = new Date();
            model.setConfirmed(d);
            model.setRecorded(d);
            model.setStarted(d);
        }

        // 紹介元への返事なのでクライアントになる
        model.setClientHospital(getFieldValue(view.getClientHospital()));
        model.setClientDept(getFieldValue(view.getClientDept()));
        model.setClientDoctor(getFieldValue(view.getClientDoctor()));

        // 来院日
        String visited = getFieldValue(view.getVisited());
        model.setVisited(visited);

        // 返事の内容
        model.setInformedContent(getAreaValue(view.getInformedContent()));
    }

    @Override
    public void start() {

        // 紹介状モデルを生成する
        this.model = new TouTouReply();

        // 確定日として現在を設定する
        Date d = new Date();
        this.model.setConfirmed(d);
        this.model.setRecorded(d);
        this.model.setStarted(d);
        this.model.setStatus(IInfoModel.STATUS_FINAL);
        
        // 来院日を今日に設定する
        this.model.setVisited(ModelUtils.getDateAsFormatString(d, "yyyy-MM-dd"));
        
        // カルテ、記録者との関係を設定する
        this.model.setKarte(getContext().getKarte());
        this.model.setCreator(Project.getUserModel());
        

        // 患者情報を設定する
        PatientModel patient = getContext().getPatient();
        this.model.setPatientName(patient.getFullName());
        this.model.setPatientGender(patient.getGenderDesc());
        this.model.setPatientBirthday(patient.getBirthday());
        this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));

        // 医師情報（ログインユーザを使用する）を設定する
        // 報告（返書）なのでログインしているユーザがコンサルタントになる
        UserModel user = Project.getUserModel();
        this.model.setConsultantHospital(user.getFacilityModel().getFacilityName());
        this.model.setConsultantDoctor(user.getCommonName());

        // view を生成する
        this.view = new LetterReplyView2();
        JScrollPane scroller = new JScrollPane(this.view);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller);

        // モデルを表示する
        displayModel(this.model);

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
        view.getClientHospital().getDocument().addDocumentListener(dl);
        view.getClientDept().getDocument().addDocumentListener(dl);
        view.getClientDoctor().getDocument().addDocumentListener(dl);
        view.getVisited().getDocument().addDocumentListener(dl);
        view.getInformedContent().getDocument().addDocumentListener(dl);
        
        // Focus Listener を登録しIMEを自動で制御する
        view.getClientHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getClientDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getClientDoctor().addFocusListener(AutoKanjiListener.getInstance());
        view.getVisited().addFocusListener(AutoRomanListener.getInstance());
        view.getInformedContent().addFocusListener(AutoKanjiListener.getInstance());
        
        // 来院日にポップアップカレンダを登録する
        new PopupListener(view.getVisited());

        // 状態制御を開始する
        stateMgr = new StateMgr();
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        // 入力データをモデル値へストアする
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
        
        // 入力データをモデル値へストアする
        // 保存前にも印刷が出来るようにするため
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
                new String[]{"PDF作成", "フォーム印刷", "取消し"},
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
     * 紹介状返書のPDFを生成する。
     */
    public void makePDF() {

        if (this.model == null) {
            return;
        }

        Runnable r = new Runnable() {

            @Override
            public void run() {

                PDFReplyMaker2 pdf = new PDFReplyMaker2();
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
                                // UNIX の場合はどうする?
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

    private void setEditables(boolean b) {
        view.getClientHospital().setEditable(b);
        view.getClientDept().setEditable(b);
        view.getClientDoctor().setEditable(b);
        view.getVisited().setEditable(b);
        view.getInformedContent().setEditable(b);
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

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, false);           // 印刷
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

        @Override
        public void enter() {
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);     // 修正
        }
    }

    class StartEditingState extends State {

        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
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

            boolean newDirty = (getFieldValue(view.getClientHospital()) != null &&
                    getAreaValue(view.getInformedContent()) != null)
                    ? true
                    : false;
            
            // 必須入力が残っている状態は empty とする
            curState = newDirty ? dirtyState : emptyState;
            curState.enter();
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
    
    class PopupListener extends MouseAdapter implements PropertyChangeListener {
        
        private JPopupMenu popup;
        
        private JTextField tf;
        
        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(this);
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
                cc.setCalendarRange(new int[] { -12, 0 });
                popup.insert(cc, 0);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
                SimpleDate sd = (SimpleDate) e.getNewValue();
                String mmldate = SimpleDate.simpleDateToMmldate(sd);     
                tf.setText(mmldate);
                popup.setVisible(false);
                popup = null;
            }
        }
    }
}














