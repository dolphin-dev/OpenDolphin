package open.dolphin.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.project.Project;

/**
 * 文書履歴で選択された紹介状を表示するクラス。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterReplyViewer2 extends AbstractChartDocument implements DocumentViewer {

    private StateMgr stateMgr;
    private LetterReplyView2 view;
    private TouTouReply model;
    private boolean documentListenerAdded;
    private boolean popAdded;

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
                ClientContext.getFrameTitle("紹介患者経過報告印刷"),
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
            LetterModel letter = ddl.getLetterReply(letterPk);

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

                // モデルを得る
                model = (TouTouReply) letter;

                // View を生成する
                view = new LetterReplyView2();
                
                // View へモデルの値を設定する
                view.getConfirmed().setText(ModelUtils.getDateAsString(model.getConfirmed()));
                view.getClientHospital().setText(model.getClientHospital());
                view.getClientDept().setText(model.getClientDept());
                view.getClientDoctor().setText(model.getClientDoctor());
                view.getVisited().setText(model.getVisited());

                view.getPatientName().setText(model.getPatientName());
                view.getPatientBirthday().setText(model.getPatientBirthday());

                view.getInformedContent().setText(model.getInformedContent());
                
                // 病院の住所、電話、名称、担当医
                FacilityModel fm = Project.getUserModel().getFacilityModel();
                view.getConsultantAddress().setText(fm.getAddress());
                view.getConsultantTelephone().setText(fm.getTelephone());
                view.getConsultantHospital().setText(model.getConsultantHospital());
                view.getConsultantDoctor().setText(model.getConsultantDoctor());
                
                boolean b = false;
                view.getClientHospital().setEditable(b);
                view.getClientDept().setEditable(b);
                view.getClientDoctor().setEditable(b);
                view.getVisited().setEditable(b);
                view.getInformedContent().setEditable(b);

                scroller.setViewportView(view);

                stateMgr.processCleanEvent();
            }
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

        model.setClientHospital(getFieldValue(view.getClientHospital()));
        model.setClientDept(getFieldValue(view.getClientDept()));
        model.setClientDoctor(getFieldValue(view.getClientDoctor()));
        String visited = getFieldValue(view.getVisited());
        model.setVisited(visited);
        model.setInformedContent(getAreaValue(view.getInformedContent()));
    }
    
    private void setEditables(boolean b) {
        
        view.getClientHospital().setEditable(b);
        view.getClientDept().setEditable(b);
        view.getClientDoctor().setEditable(b);
        view.getVisited().setEditable(b);
        view.getInformedContent().setEditable(b);
        
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
            view.getClientHospital().getDocument().addDocumentListener(dl);
            view.getClientDept().getDocument().addDocumentListener(dl);
            view.getClientDoctor().getDocument().addDocumentListener(dl);
            view.getVisited().getDocument().addDocumentListener(dl);
            view.getInformedContent().getDocument().addDocumentListener(dl);

            view.getClientHospital().addFocusListener(AutoKanjiListener.getInstance());
            view.getClientDept().addFocusListener(AutoKanjiListener.getInstance());
            view.getClientDoctor().addFocusListener(AutoKanjiListener.getInstance());
            view.getVisited().addFocusListener(AutoRomanListener.getInstance());
            view.getInformedContent().addFocusListener(AutoKanjiListener.getInstance());
            
            documentListenerAdded = true;
        }
        
        if (b &&(!popAdded)) {
            new PopupListener(view.getVisited());
            popAdded = true;
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
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            boolean canEdit = isReadOnly() ? false : true;
            getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);      // 新規カルテ
            getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);   // 新規文書
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);      // 修正
            getContext().enabledAction(GUIConst.ACTION_DELETE, false);           // 削除
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);             // 印刷
            getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);        // 昇順
            getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);       // 降順
            getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);    // 修正履歴表示
        }
    }
    
    class StartEditingState extends LetterState {

        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class DirtyState extends LetterState {

        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
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
            currentState.enter();
        }
        
        public void processSavedEvent() {
            currentState = cleanState;
            currentState.enter();
        }
        
        public void processDirtyEvent() {

            boolean newDirty = (getFieldValue(view.getClientHospital()) != null &&
                    getAreaValue(view.getInformedContent()) != null)
                    ? true
                    : false;
            
            // 必須入力が残っている状態は empty とする
            currentState = newDirty ? dirtyState : emptyState;
            currentState.enter();
        }
        
        public boolean isDirtyState() {
            return currentState == dirtyState ? true : false;
        }

        public void enter() {
            currentState.enter();
        }
    }
}
