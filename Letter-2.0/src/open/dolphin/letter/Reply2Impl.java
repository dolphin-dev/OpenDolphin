package open.dolphin.letter;

import java.awt.BorderLayout;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.BundleTransferHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.CutCopyPasteAdapter;
import open.dolphin.client.Letter;
import open.dolphin.client.Panel2;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterItem;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterText;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.util.AgeCalculater;
import org.apache.log4j.Level;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Reply2Impl extends AbstractChartDocument implements Letter {

    protected static final String TITLE = "ご　報　告";
    protected static final String ITEM_VISITED_DATE = "visited";
    protected static final String TEXT_INFORMED_CONTENT = "informedContent";

    private static final String TITLE__PREFIX = "ご報告:";

    protected LetterModule model;
    protected Reply2View view;
    private boolean listenerIsAdded;

    protected LetterStateMgr stateMgr;
    protected boolean DEBUG;

    /** Creates a new instance of LetterDocument */
    public Reply2Impl() {
        setTitle(TITLE);
        DEBUG = ClientContext.getBootLogger().getLevel() == Level.DEBUG ? true : false;
    }

    @Override
    public void modelToView(LetterModule m) {

        if (view == null) {
            view = new Reply2View();
        }

        // 日付
        String dateStr = LetterHelper.getDateAsString(m.getConfirmed());
        LetterHelper.setModelValue(view.getConfirmed(), dateStr);

        // 紹介元（宛先）医療機関名
        LetterHelper.setModelValue(view.getClientHospital(), m.getClientHospital());

        // 紹介元（宛先）診療科
        LetterHelper.setModelValue(view.getClientDept(), m.getClientDept());

        // 紹介元紹介元（宛先）担当医
        LetterHelper.setModelValue(view.getClientDoctor(), m.getClientDoctor());

        // 患者氏名
        LetterHelper.setModelValue(view.getPatientName(), m.getPatientName());

        // 患者生年月日
        String val = LetterHelper.getBirdayWithAge(m.getPatientBirthday(), m.getPatientAge());
        LetterHelper.setModelValue(view.getPatientBirthday(), val);

        // 紹介先（差出人）住所
        if (m.getConsultantAddress()==null) {
            m.setConsultantZipCode(Project.getUserModel().getFacilityModel().getZipCode());
            m.setConsultantAddress(Project.getUserModel().getFacilityModel().getAddress());
        }
        val = LetterHelper.getAddressWithZipCode(m.getConsultantAddress(), m.getConsultantZipCode());
        LetterHelper.setModelValue(view.getConsultantAddress(), val);

        // 紹介先（差出人）電話
        if (m.getConsultantTelephone()==null) {
            m.setConsultantTelephone(Project.getUserModel().getFacilityModel().getTelephone());
        }
        LetterHelper.setModelValue(view.getConsultantTelephone(), m.getConsultantTelephone());

        // 紹介先（差出人）病院名
        LetterHelper.setModelValue(view.getConsultantHospital(), m.getConsultantHospital());

        // 紹介（差出人）先医師
        LetterHelper.setModelValue(view.getConsultantDoctor(), m.getConsultantDoctor());

        //----------------------------------------------------------------------

        // 来院日
        String value = model.getItemValue(ITEM_VISITED_DATE);
        if (value != null) {
            LetterHelper.setModelValue(view.getVisited(), value);
        }

        // Informed
        String text = model.getTextValue(TEXT_INFORMED_CONTENT);
        if (text!=null) {
            LetterHelper.setModelValue(view.getInformedContent(), text);
        }
    }

    @Override
    public void viewToModel() {

        long savedId = model.getId();
        model.setId(0L);
        model.setLinkId(savedId);

        Date d = new Date();
        model.setConfirmed(d);
        model.setRecorded(d);
        model.setKarteBean(getContext().getKarte());
        model.setUserModel(Project.getUserModel());
        model.setStatus(IInfoModel.STATUS_FINAL);

        // 紹介元（宛先）
        model.setClientHospital(LetterHelper.getFieldValue(view.getClientHospital()));
        model.setClientDept(LetterHelper.getFieldValue(view.getClientDept()));
        model.setClientDoctor(LetterHelper.getFieldValue(view.getClientDoctor()));

        // 患者情報、差し出し人側はtartでmodelに設定済

        // 来院日
        String value = LetterHelper.getFieldValue(view.getVisited());
        if (value != null) {
            LetterItem item = new LetterItem(ITEM_VISITED_DATE, value);
            model.addLetterItem(item);
        }

        // Informed
        String informed = LetterHelper.getAreaValue(view.getInformedContent());
        if (informed!=null) {
            LetterText text = new LetterText();
            text.setName(TEXT_INFORMED_CONTENT);
            text.setTextValue(informed);
            model.addLetterText(text);
        }

        // Title
        StringBuilder sb = new StringBuilder();
        sb.append(TITLE__PREFIX).append(model.getClientHospital());
        model.setTitle(sb.toString());
    }

    @Override
    public void start() {

        this.model = new LetterModule();

        // Handle Class
        this.model.setHandleClass(Reply2Viewer.class.getName());
        this.model.setLetterType(IInfoModel.CONSULTANT);

        // 確定日等
        Date d = new Date();
        this.model.setConfirmed(d);
        this.model.setRecorded(d);
        this.model.setStarted(d);
        this.model.setStatus(IInfoModel.STATUS_FINAL);
        this.model.setKarteBean(getContext().getKarte());
        this.model.setUserModel(Project.getUserModel());

        // 患者情報
        PatientModel patient = getContext().getPatient();
        this.model.setPatientId(patient.getPatientId());
        this.model.setPatientName(patient.getFullName());
        this.model.setPatientKana(patient.getKanaName());
        this.model.setPatientGender(patient.getGenderDesc());
        this.model.setPatientBirthday(patient.getBirthday());

        int showMonth = Project.getInt("ageToNeedMonth", 6);
        String age = AgeCalculater.getAge(patient.getBirthday(), showMonth);
        this.model.setPatientAge(age);
        
        if (patient.getSimpleAddressModel()!=null) {
            this.model.setPatientAddress(patient.getSimpleAddressModel().getAddress());
        }
        this.model.setPatientTelephone(patient.getTelephone());

        // 紹介元
        UserModel user = Project.getUserModel();
        this.model.setConsultantHospital(user.getFacilityModel().getFacilityName());
        this.model.setConsultantDoctor(user.getCommonName());
        this.model.setConsultantDept(user.getDepartmentModel().getDepartmentDesc());
        this.model.setConsultantTelephone(user.getFacilityModel().getTelephone());
        this.model.setConsultantFax(user.getFacilityModel().getFacsimile());
        this.model.setConsultantZipCode(user.getFacilityModel().getZipCode());
        this.model.setConsultantAddress(user.getFacilityModel().getAddress());

        // view を生成
        this.view = new Reply2View();
        JScrollPane scroller = new JScrollPane(this.view);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller);

        modelToView(this.model);
        setEditables(true);
        setListeners();
        
        stateMgr = new LetterStateMgr(this);
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        viewToModel();

        DBTask task = new DBTask<Boolean, Void>(getContext()) {

            @Override
            protected Boolean doInBackground() throws Exception {

                LetterDelegater ddl = new LetterDelegater();
                long result = ddl.saveOrUpdateLetter(model);
                model.setId(result);
                return true;
            }

            @Override
            protected void succeeded(Boolean result) {
                getContext().getDocumentHistory().getDocumentHistory();
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
        
        viewToModel();

        StringBuilder sb = new StringBuilder();
        sb.append("PDFファイルを作成しますか?");

        int option = JOptionPane.showOptionDialog(
                getContext().getFrame(),
                sb.toString(),
                ClientContext.getFrameTitle("御報告書印刷"),
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

    @Override
    public void makePDF() {

        if (this.model == null) {
            return;
        }

        Runnable r = new Runnable() {

            @Override
            public void run() {

                Reply2PDFMaker pdf = new Reply2PDFMaker();
                String pdfDir = Project.getString("pdfStore", System.getProperty("user.dir"));
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
                                
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace(System.err);
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

    public void modifyKarte() {
        stateMgr.processModifyKarteEvent();
    }

    @Override
    public void setEditables(boolean b) {
        view.getClientHospital().setEditable(b);
        view.getClientDept().setEditable(b);
        view.getClientDoctor().setEditable(b);
        view.getInformedContent().setEditable(b);
    }

    @Override
    public void setListeners() {

        if (listenerIsAdded) {
            return;
        }

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

        // 紹介元（宛先）病院
        view.getClientHospital().getDocument().addDocumentListener(dl);
        view.getClientHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getClientHospital().setTransferHandler(new BundleTransferHandler());
        view.getClientHospital().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介元（宛先）診療化
        view.getClientDept().getDocument().addDocumentListener(dl);
        view.getClientDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getClientDept().setTransferHandler(new BundleTransferHandler());
        view.getClientDept().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介元（宛先）医師
        view.getClientDoctor().getDocument().addDocumentListener(dl);
        view.getClientDoctor().addFocusListener(AutoKanjiListener.getInstance());
        view.getClientDoctor().setTransferHandler(new BundleTransferHandler());
        view.getClientDoctor().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 来院日
        PopupCalendarListener pl = new PopupCalendarListener(view.getVisited());
        view.getVisited().getDocument().addDocumentListener(dl);

        // Informed
        view.getInformedContent().getDocument().addDocumentListener(dl);
        view.getInformedContent().addFocusListener(AutoKanjiListener.getInstance());
        view.getInformedContent().setTransferHandler(new BundleTransferHandler());
        view.getInformedContent().addMouseListener(CutCopyPasteAdapter.getInstance());

        listenerIsAdded = true;
    }

    @Override
    public boolean letterIsDirty() {
        boolean dirty =  true;
        dirty = dirty && (LetterHelper.getFieldValue(view.getClientHospital()) != null);
        dirty = dirty && (LetterHelper.getFieldValue(view.getVisited()) != null);
        dirty = dirty && (LetterHelper.getAreaValue(view.getInformedContent()) != null);
        return dirty;
    }
}
