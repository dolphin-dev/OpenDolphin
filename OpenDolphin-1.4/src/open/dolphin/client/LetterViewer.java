package open.dolphin.client;

import java.awt.print.PageFormat;
import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.project.Project;

/**
 * 文書履歴で選択された紹介状を表示するクラス。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterViewer extends AbstractChartDocument implements DocumentViewer {

    private StateMgr stateMgr;
    private LetterView2 view;
    private TouTouLetter model;
    private boolean documentListenerAdded;
    
        
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
        
        if (b &&(!documentListenerAdded)) {
        
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
            
            documentListenerAdded = true;
        }
    }
    
    private void restore(boolean save) {
        
        if (model == null) {
            return;
        }

        if (save) {
            Date d = new Date();
            model.setConfirmed(d);
            model.setRecorded(d);
            model.setStarted(d);
        }
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
        stateMgr = new StateMgr();
        this.enter();
    }

    @Override
    public void stop() {
    }

    @Override
    public void enter() {
        super.enter();
        stateMgr.enter();
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
    public void historyPeriodChanged() {
        stateMgr.processEmptyEvent();
    }

    @Override
    public void showDocuments(DocInfoModel[] docs, JScrollPane scroller) {

        if (docs == null || docs.length == 0) {
            stateMgr.processEmptyEvent();
            return;
        }

        DocInfoModel docInfo = docs[0];
        long pk = docInfo.getDocPk();

        if (pk == 0L) {
            return;
        }

        LetterGetTask task = new LetterGetTask(getContext(), pk, scroller);

        task.execute();
    }

    class LetterGetTask extends DBTask<LetterModel, Void> {

        private long letterPk;
        private JScrollPane scroller;

        public LetterGetTask(Chart app, long letterPk, JScrollPane scroller) {
            super(app);
            this.letterPk = letterPk;
            this.scroller = scroller;
        }

        @Override
        protected LetterModel doInBackground() throws Exception {

            DocumentDelegater ddl = new DocumentDelegater();
            LetterModel letter = ddl.getLetter(letterPk);

            if (ddl.isNoError()) {
                return letter;
            } else {
                return null;
            }
        }

        @Override
        protected void succeeded(LetterModel letter) {
            
            logger.debug("LetterGetTask succeeded");
            
            if (letter != null) {

                model = (TouTouLetter) letter;

                view = new LetterView2();
                view.getConfirmed().setText(ModelUtils.getDateAsString(model.getConfirmed()));
                view.getCHospital().setText(model.getConsultantHospital());
                view.getCDept().setText(model.getConsultantDept());
                view.getCDoctor().setText(model.getConsultantDoctor());
                view.getPatientName().setText(model.getPatientName());

//                view.getMyHospital().setText(model.getClientHospital());
//                view.getMyName().setText(model.getClientName());
//                view.getAddress().setText(model.getClientAddress());
//                view.getTelephone().setText(model.getClientTelephone());

                view.getPatientName().setText(model.getPatientName());
                view.getPatientGender().setText(model.getPatientGender());
                view.getPatientBirthday().setText(model.getPatientBirthday());
                view.getPatientAge().setText(model.getPatientAge());

                view.getDisease().setText(model.getDisease());
                view.getPurpose().setText(model.getPurpose());
                view.getClinicalCourse().setText(model.getClinicalCourse());
                view.getPastFamily().setText(model.getPastFamily());
                view.getMedication().setText(model.getMedication());
                view.getRemarks().setText(model.getRemarks());

                scroller.setViewportView(view);

                stateMgr.processCleanEvent();
            }
        }
    }
    
    /**
     * 紹介状を変更する。
     */
    public void modifyKarte() {
        stateMgr.processModifyKarteEvent();
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
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }

    /**
     * 抽象状態クラス。
     */
    protected abstract class LetterState {

        public LetterState() {
        }

        public abstract void enter();
    }

    /**
     * 表示するカルテがない状態を表す。
     */
    protected final class EmptyState extends LetterState {

        public EmptyState() {
        }

        @Override
        public void enter() {
            boolean canEdit = isReadOnly() ? false : true;
            getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);     // 新規カルテ
            getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);  // 新規文書
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
            getContext().enabledAction(GUIConst.ACTION_DELETE, false);          // 削除
            getContext().enabledAction(GUIConst.ACTION_PRINT, false);           // 印刷   
            getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);       // 昇順
            getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);      // 降順
            getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);   // 修正履歴表示
        }
    }

    /**
     * カルテが表示されている状態を表す。
     */
    protected final class CleanState extends LetterState {

        public CleanState() {
        }

        @Override
        public void enter() {
            
            // 編集を不可にし、保存を不可にする
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            
            boolean canEdit = isReadOnly() ? false : true;
            getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);      // 新規カルテ
            getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);   // 新規文書
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, canEdit);   // 修正
            getContext().enabledAction(GUIConst.ACTION_DELETE, false);           // 削除
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);             // 印刷
            getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);        // 昇順
            getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);       // 降順
            getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);    // 修正履歴表示
        }
    }
    
    protected final class StartEditingState extends LetterState {

        @Override
        public void enter() {
            // 編集可能にする
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);            // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
        }
    }
    
    protected final class DirtyState extends LetterState {

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);             // 保存
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);            // 印刷
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);    // 修正
        }
    }

    /**
     * StateContext クラス。
     */
    protected final class StateMgr {

        private LetterState emptyState = new EmptyState();
        private LetterState cleanState = new CleanState();
        private StartEditingState startEditingState = new StartEditingState();
        private DirtyState dirtyState = new DirtyState();
        private LetterState currentState;

        public StateMgr() {
            currentState = emptyState;
        }

        public void processEmptyEvent() {
            currentState = emptyState;
            this.enter();
        }

        public void processCleanEvent() {
            currentState = cleanState;
            this.enter();
        }
        
        public void processModifyKarteEvent() {
            currentState = startEditingState;
            this.enter();
        }
        
        public void processSavedEvent() {
            currentState = cleanState;
            this.enter();
        }
        
        public void processDirtyEvent() {

            boolean newDirty = (
                    getFieldValue(view.getDisease()) != null &&
                    getFieldValue(view.getPurpose()) != null &&
                    getAreaValue(view.getClinicalCourse()) != null)
                    ? true
                    : false;
            
            // 必須入力が残っている状態は empty とする
            currentState = newDirty ? dirtyState : emptyState;
            this.enter();
        }
        
        public void enter() {
            currentState.enter();
        }
        
        public boolean isDirtyState() {
            return currentState == dirtyState ? true : false;
        }
    }
}
