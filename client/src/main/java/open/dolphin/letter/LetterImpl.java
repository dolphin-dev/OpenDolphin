package open.dolphin.letter;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.*;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
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
    
    private boolean modify;

    /** Creates a new instance of LetterDocument */
    public LetterImpl() {
        setTitle(TITLE);
        DEBUG = ClientContext.getBootLogger().getLevel() == Level.DEBUG ? true : false;
    }
    
//minagawa^ LSC Test    
    public LetterModule getModel() {
        return model;
    }
    
    public void setModel(LetterModule m) {
        this.model = m;
    }
    
    public boolean isModify() {
        return modify;
    }
    
    public void setModify(boolean b) {
        modify = b;
    }
//minagawa$
    
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

        // title
        StringBuilder sb = new StringBuilder();
        sb.append("先生　");
        String title = Project.getString("letter.atesaki.title");
        if (title!=null && (!title.equals("無し"))) {
            sb.append(title);
        }
        LetterHelper.setModelValue(view.getAtesakiLbl(), sb.toString());

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
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getDisease(), "");
        }
//s.oh$

        // 紹介目的
        value = model.getItemValue(ITEM_PURPOSE);
        if (value != null) {
            LetterHelper.setModelValue(view.getPurpose(), value);
        }
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getPurpose(), "");
        }
//s.oh$

        // 既往歴、家族歴
        String text = model.getTextValue(TEXT_PAST_FAMILY);
        if (text!=null) {
            LetterHelper.setModelValue(view.getPastFamily(), text);
        }
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getPastFamily(), "");
        }
//s.oh$

        // 症状経過
        text = model.getTextValue(TEXT_CLINICAL_COURSE);
        if (text!=null) {
            LetterHelper.setModelValue(view.getClinicalCourse(), text);
        }
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getClinicalCourse(), "");
        }
//s.oh$

        // 現在の処方
        text = model.getTextValue(TEXT_MEDICATION);
        if (text!=null) {
            LetterHelper.setModelValue(view.getMedication(), text);
        }
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getMedication(), "");
        }
//s.oh$

        // 備考
        value = model.getItemValue(ITEM_REMARKS);
        if (value != null) {
            LetterHelper.setModelValue(view.getRemarks(), value);
        }
//s.oh^ 不具合修正
        else{
            LetterHelper.setModelValue(view.getRemarks(), "");
        }
//s.oh$
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
//minagawa^ LSC Test        
        if (this.model==null) {

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

            int showMonth = Project.getInt(Project.KARTE_AGE_TO_NEED_MONTH);
            String age = AgeCalculater.getAge(patient.getBirthday(), showMonth);
            this.model.setPatientAge(age);
            // 患者住所
            if (patient.getSimpleAddressModel()!=null) {
                this.model.setPatientAddress(patient.getSimpleAddressModel().getAddress());
                this.model.setPatientZipCode(patient.getSimpleAddressModel().getZipCode());
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
        }
//minagawa$        

        // view を生成
        this.view = new LetterView();
// minagawa 中央へ位置するように変更 ^        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(this.view);
        JScrollPane scroller = new JScrollPane(p);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
        //JScrollPane scroller = new JScrollPane(this.view);
        //getUI().setLayout(new BorderLayout());
        //getUI().add(scroller, BorderLayout.NORTH);
        //getUI().setLayout(new FlowLayout(FlowLayout.CENTER));
        //getUI().add(scroller);
// minagawa $  
        
        modelToView(this.model);
        setEditables(true);
        setListeners();
        
        stateMgr = new LetterStateMgr(this);
//minagawa^ LSC Test        
        this.enter();
//minagawa$        

//s.oh^ 文書の必要事項対応
        //view.getConsultantHospital().setBackground(Color.YELLOW);
        //view.getDisease().setBackground(Color.YELLOW);
        //view.getPurpose().setBackground(Color.YELLOW);
        //view.getClinicalCourse().setBackground(Color.YELLOW);
//s.oh$
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
//minagawa^ Chartの close box 押下で保存する場合、保存終了を通知しておしまい。                    
            if (boundSupport!=null) {
                setChartDocDidSave(true);
                return;
            }
//minagawa$                
// minagawa 紹介状等の履歴に遷移 ^                
                getContext().getDocumentHistory().getLetterHistory();
//                getContext().getDocumentHistory().getDocumentHistory();
// minagawa $
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
                new String[]{"PDF作成", "フォーム印刷", GUIFactory.getCancelButtonText()},
                "PDF作成");

        if (option == 0) {
            makePDF();
        } else if (option == 1) {
            PageFormat pageFormat = getContext().getContext().getPageFormat();
            String name = getContext().getPatient().getFullName();
            Panel2 panel = (Panel2) this.view;
            panel.printPanel(pageFormat, 1, false, name, 0, true);
        }
    }

    @Override
    public void makePDF() {

        if (this.model == null) {
            return;
        }
        
        SwingWorker w = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                LetterPDFMaker pdf = new LetterPDFMaker();
                pdf.setDocumentDir(Project.getString(Project.LOCATION_PDF));
                pdf.setModel(model);
                return pdf.create();
            }
            
            @Override
            protected void done() {
                String err = null;
                try {
//minagawa^ jdk7                   
                    //String pathToPDF = get();
                    //Desktop.getDesktop().open(new File(pathToPDF));
                    URI uri = Paths.get(get()).toUri();
                    Desktop.getDesktop().browse(uri);
//minagawa$                    
                } catch (IOException ex) {
                    err = "PDFファイルに関連づけされたアプリケーションを起動できません。";
                } catch (InterruptedException ex) {
                } catch (Throwable ex) {
                    err = ex.getMessage();
                }
                
                if (err!=null) {
                    Window parent = SwingUtilities.getWindowAncestor(getContext().getFrame());
                    JOptionPane.showMessageDialog(parent, err, ClientContext.getFrameTitle("PDF作成"), JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        
        w.execute();
    }

    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }
//minagawa^ LSC Test
//    public void modifyKarte() {
//        stateMgr.processModifyKarteEvent();      
//    }
//minagawa$
    
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

        ChartMediator med = getContext().getChartMediator();

        // 紹介先（宛先）病院
        view.getConsultantHospital().getDocument().addDocumentListener(dl);
        view.getConsultantHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantHospital().setTransferHandler(new BundleTransferHandler(med, view.getConsultantHospital()));
        view.getConsultantHospital().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介先（宛先）診療化
        view.getConsultantDept().getDocument().addDocumentListener(dl);
        view.getConsultantDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantDept().setTransferHandler(new BundleTransferHandler(med, view.getConsultantDept()));
        view.getConsultantDept().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介先（宛先）医師
        view.getConsultantDoctor().getDocument().addDocumentListener(dl);
        view.getConsultantDoctor().addFocusListener(AutoKanjiListener.getInstance());
        view.getConsultantDoctor().setTransferHandler(new BundleTransferHandler(med, view.getConsultantDoctor()));
        view.getConsultantDoctor().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 傷病名
        view.getDisease().getDocument().addDocumentListener(dl);
        view.getDisease().addFocusListener(AutoKanjiListener.getInstance());
        view.getDisease().setTransferHandler(new BundleTransferHandler(med, view.getDisease()));
        view.getDisease().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 紹介目的
        view.getPurpose().getDocument().addDocumentListener(dl);
        view.getPurpose().addFocusListener(AutoKanjiListener.getInstance());
        view.getPurpose().setTransferHandler(new BundleTransferHandler(med, view.getPurpose()));
        view.getPurpose().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 既往歴、家族歴
        view.getPastFamily().getDocument().addDocumentListener(dl);
        view.getPastFamily().addFocusListener(AutoKanjiListener.getInstance());
        view.getPastFamily().setTransferHandler(new BundleTransferHandler(med, view.getPastFamily()));
        view.getPastFamily().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 症状経過
        view.getClinicalCourse().getDocument().addDocumentListener(dl);
        view.getClinicalCourse().addFocusListener(AutoKanjiListener.getInstance());
        view.getClinicalCourse().setTransferHandler(new BundleTransferHandler(med, view.getClinicalCourse()));
        view.getClinicalCourse().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 現在の処方
        view.getMedication().getDocument().addDocumentListener(dl);
        view.getMedication().addFocusListener(AutoKanjiListener.getInstance());
        view.getMedication().setTransferHandler(new BundleTransferHandler(med, view.getMedication()));
        view.getMedication().addMouseListener(CutCopyPasteAdapter.getInstance());

        // 備考
        view.getRemarks().getDocument().addDocumentListener(dl);
        view.getRemarks().addFocusListener(AutoKanjiListener.getInstance());
        view.getRemarks().setTransferHandler(new BundleTransferHandler(med, view.getRemarks()));
        view.getRemarks().addMouseListener(CutCopyPasteAdapter.getInstance());

        listenerIsAdded = true;
    }

    @Override
    public boolean letterIsDirty() {
//minagawa^ LSC Test         
        boolean dirty = (LetterHelper.getFieldValue(view.getConsultantHospital())!=null);     // 紹介先
        dirty = dirty || (LetterHelper.getFieldValue(view.getDisease())!=null);               // 病名
        dirty = dirty || (LetterHelper.getFieldValue(view.getPurpose())!=null);               // 紹介目的
        dirty = dirty || (LetterHelper.getAreaValue(view.getClinicalCourse())!=null);         // 症状経過
        dirty = dirty || (LetterHelper.getFieldValue(view.getConsultantDept())!=null);        // 紹介先診療科
        dirty = dirty || (LetterHelper.getFieldValue(view.getConsultantDoctor())!=null);      // 紹介先医師
        dirty = dirty || (LetterHelper.getAreaValue(view.getPastFamily())!=null);             // 家族歴
        dirty = dirty || (LetterHelper.getAreaValue(view.getMedication())!=null);             // 現在の処方
        dirty = dirty || (LetterHelper.getFieldValue(view.getRemarks())!=null);               // 備考
        return dirty;
//minagawa$        
    }
}
