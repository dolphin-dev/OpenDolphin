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
public class LetterImpl extends AbstractChartDocument implements Letter {

    protected static final String TITLE = "診療情報提供書";
    
    protected static final String ITEM_DISEASE = "disease";
    protected static final String ITEM_PURPOSE = "purpose";
    protected static final String TEXT_PAST_FAMILY = "pastFamily";
    protected static final String TEXT_CLINICAL_COURSE = "clinicalCourse";
    protected static final String TEXT_MEDICATION = "medication";
    protected static final String ITEM_REMARKS = "remarks";

    private static final String TITLE__PREFIX = "紹介状:";

    protected LetterModule model;
    protected LetterView view;
    private boolean listenerIsAdded;

    protected LetterStateMgr stateMgr;
    protected boolean DEBUG;

    /** Creates a new instance of LetterDocument */
    public LetterImpl() {
        setTitle(TITLE);
        DEBUG = ClientContext.getBootLogger().getLevel() == Level.DEBUG ? true : false;
    }

    @Override
    public void modelToView(LetterModule m) {

        if (view == null) {
            view = new LetterView();
        }

        // 日付
        String dateStr = LetterHelper.getDateAsString(m.getConfirmed());
        LetterHelper.setModelValue(view.getConfirmed(), dateStr);

        // 紹介先（宛先）医療機関名
        LetterHelper.setModelValue(view.getConsultantHospital(), m.getConsultantHospital());

        // 紹介先（宛先）診療科
        LetterHelper.setModelValue(view.getConsultantDept(), m.getConsultantDept());

        // 紹介先（宛先）担当医
        LetterHelper.setModelValue(view.getConsultantDoctor(), m.getConsultantDoctor());

        // 患者氏名
        LetterHelper.setModelValue(view.getPatientName(), m.getPatientName());

        // 患者生年月日
        LetterHelper.setModelValue(view.getPatientBirthday(), m.getPatientBirthday());

        // 年齢
        LetterHelper.setModelValue(view.getPatientAge(), m.getPatientAge());

        // 性別
        LetterHelper.setModelValue(view.getPatientGender(), m.getPatientGender());

        // 紹介先（差出人）住所
        //String val = LetterHelper.getAddressWithZipCode(m.getConsultantAddress(), m.getConsultantZipCode());
        //LetterHelper.setModelValue(view.getConsultantAddress(), val);

        // 紹介先（差出人）電話
        //LetterHelper.setModelValue(view.getConsultantTelephone(), m.getConsultantTelephone());

        // 紹介先（差出人）病院名
        //LetterHelper.setModelValue(view.getConsultantHospital(), m.getConsultantHospital());

        // 紹介（差出人）先医師
        //LetterHelper.setModelValue(view.getConsultantDoctor(), m.getConsultantDoctor());

        //----------------------------------------------------------------------

        // 病名
        String value = model.getItemValue(ITEM_DISEASE);
        if (value != null) {
            LetterHelper.setModelValue(view.getDisease(), value);
        }

        // 紹介目的
        value = model.getItemValue(ITEM_PURPOSE);
        if (value != null) {
            LetterHelper.setModelValue(view.getPurpose(), value);
        }

        // 既往歴、家族歴
        String text = model.getTextValue(TEXT_PAST_FAMILY);
        if (text!=null) {
            LetterHelper.setModelValue(view.getPastFamily(), text);
        }

        // 症状経過
        text = model.getTextValue(TEXT_CLINICAL_COURSE);
        if (text!=null) {
            LetterHelper.setModelValue(view.getClinicalCourse(), text);
        }

        // 現在の処方
        text = model.getTextValue(TEXT_MEDICATION);
        if (text!=null) {
            LetterHelper.setModelValue(view.getMedication(), text);
        }

        // 備考
        value = model.getItemValue(ITEM_REMARKS);
        if (value != null) {
            LetterHelper.setModelValue(view.getRemarks(), value);
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

        // 紹介先（宛先）
        model.setConsultantHospital(LetterHelper.getFieldValue(view.getConsultantHospital()));
        model.setConsultantDept(LetterHelper.getFieldValue(view.getConsultantDept()));
        model.setConsultantDoctor(LetterHelper.getFieldValue(view.getConsultantDoctor()));

        // 患者情報、差し出し人側はtartでmodelに設定済

        // 傷病名
        String value = LetterHelper.getFieldValue(view.getDisease());
        if (value != null) {
            LetterItem item = new LetterItem(ITEM_DISEASE, value);
            model.addLetterItem(item);
        }

        // 紹介目的
        value = LetterHelper.getFieldValue(view.getPurpose());
        if (value != null) {
            LetterItem item = new LetterItem(ITEM_PURPOSE, value);
            model.addLetterItem(item);
        }

        // 既往歴、家族歴
        String text = LetterHelper.getAreaValue(view.getPastFamily());
        if (text != null) {
            LetterText lt = new LetterText(TEXT_PAST_FAMILY, text);
            model.addLetterText(lt);
        }

        // 症状経過
        text = LetterHelper.getAreaValue(view.getClinicalCourse());
        if (text != null) {
            LetterText lt = new LetterText(TEXT_CLINICAL_COURSE, text);
            model.addLetterText(lt);
        }

        // 現在の処方
        text = LetterHelper.getAreaValue(view.getMedication());
        if (text != null) {
            LetterText lt = new LetterText(TEXT_MEDICATION, text);
            model.addLetterText(lt);
        }

        // 備考
        value = LetterHelper.getFieldValue(view.getRemarks());
        if (value != null) {
            LetterItem item = new LetterItem(ITEM_REMARKS, value);
            model.addLetterItem(item);
        }

        // Title
        StringBuilder sb = new StringBuilder();
        sb.append(TITLE__PREFIX).append(model.getConsultantHospital());
        model.setTitle(sb.toString());
    }

    @Override
    public void start() {

        this.model = new LetterModule();

        // Handle Class
        this.model.setHandleClass(LetterViewer.class.getName());
        this.model.setLetterType(IInfoModel.CLIENT);

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
        this.model.setClientHospital(user.getFacilityModel().getFacilityName());
        this.model.setClientDoctor(user.getCommonName());
        this.model.setClientDept(user.getDepartmentModel().getDepartmentDesc());
        this.model.setClientTelephone(user.getFacilityModel().getTelephone());
        this.model.setClientFax(user.getFacilityModel().getFacsimile());
        this.model.setClientZipCode(user.getFacilityModel().getZipCode());
        this.model.setClientAddress(user.getFacilityModel().getAddress());

        // view を生成
        this.view = new LetterView();
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

                LetterPDFMaker pdf = new LetterPDFMaker();
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
        view.getConsultantHospital().setEditable(b);
        view.getConsultantDept().setEditable(b);
        view.getConsultantDoctor().setEditable(b);
        view.getDisease().setEditable(b);
        view.getPurpose().setEditable(b);
        view.getPastFamily().setEditable(b);
        view.getClinicalCourse().setEditable(b);
        view.getMedication().setEditable(b);
        view.getRemarks().setEditable(b);
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

        // 紹介先（宛先）病院
        view.getConsultantHospital().getDocument().addDocumentListener(dl);
        view.getConsultantHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantHospital().setTransferHandler(new BundleTransferHandler());
        view.getConsultantHospital().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介先（宛先）診療化
        view.getConsultantDept().getDocument().addDocumentListener(dl);
        view.getConsultantDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantDept().setTransferHandler(new BundleTransferHandler());
        view.getConsultantDept().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介先（宛先）医師
        view.getConsultantDoctor().getDocument().addDocumentListener(dl);
        view.getConsultantDoctor().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantDoctor().setTransferHandler(new BundleTransferHandler());
        view.getConsultantDoctor().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 傷病名
        view.getDisease().getDocument().addDocumentListener(dl);
        view.getDisease().addFocusListener(AutoKanjiListener.getInstance());
        view.getDisease().setTransferHandler(new BundleTransferHandler());
        view.getDisease().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介目的
        view.getPurpose().getDocument().addDocumentListener(dl);
        view.getPurpose().addFocusListener(AutoKanjiListener.getInstance());
        view.getPurpose().setTransferHandler(new BundleTransferHandler());
        view.getPurpose().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 既往歴、家族歴
        view.getPastFamily().getDocument().addDocumentListener(dl);
        view.getPastFamily().addFocusListener(AutoKanjiListener.getInstance());
        view.getPastFamily().setTransferHandler(new BundleTransferHandler());
        view.getPastFamily().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 症状経過
        view.getClinicalCourse().getDocument().addDocumentListener(dl);
        view.getClinicalCourse().addFocusListener(AutoKanjiListener.getInstance());
        view.getClinicalCourse().setTransferHandler(new BundleTransferHandler());
        view.getClinicalCourse().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 現在の処方
        view.getMedication().getDocument().addDocumentListener(dl);
        view.getMedication().addFocusListener(AutoKanjiListener.getInstance());
        view.getMedication().setTransferHandler(new BundleTransferHandler());
        view.getMedication().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 備考
        view.getRemarks().getDocument().addDocumentListener(dl);
        view.getRemarks().addFocusListener(AutoKanjiListener.getInstance());
        view.getRemarks().setTransferHandler(new BundleTransferHandler());
        view.getRemarks().addMouseListener(CutCopyPasteAdapter.getInstance());

        listenerIsAdded = true;
    }

    @Override
    public boolean letterIsDirty() {
        boolean dirty =  true;
        dirty = dirty && (LetterHelper.getFieldValue(view.getConsultantHospital()) != null);
        dirty = dirty && (LetterHelper.getFieldValue(view.getDisease()) != null);
        dirty = dirty && (LetterHelper.getFieldValue(view.getPurpose()) != null);
        dirty = dirty && (LetterHelper.getAreaValue(view.getClinicalCourse()) != null);
        return dirty;
    }
}
