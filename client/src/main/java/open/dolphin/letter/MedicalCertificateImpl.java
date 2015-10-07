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
import java.util.concurrent.ExecutionException;
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

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicalCertificateImpl extends AbstractChartDocument implements Letter {

    protected static final String ITEM_DISEASE = "disease";
    protected static final String TEXT_INFORMED_CONTENT = "informedContent";

    protected LetterModule model;
    protected MedicalCertificateView view;
    private boolean listenerIsAdded;

    protected LetterStateMgr stateMgr;
    
    private boolean modify;

    /** Creates a new instance of LetterDocument */
    public MedicalCertificateImpl() {
        setTitle(ClientContext.getMyBundle(MedicalCertificateImpl.class).getString("title.medicalCertificate"));
    }
       
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

    @Override
    public void modelToView(LetterModule m) {

        if (view == null) {
            view = new MedicalCertificateView();
        }

        // 患者氏名
        LetterHelper.setModelValue(view.getPatientNameFld(), m.getPatientName());

        // 患者生年月日
        //String val = LetterHelper.getBirdayWithAge(m.getPatientBirthday(), m.getPatientAge());
        String val = LetterHelper.getDateString(m.getPatientBirthday());
        LetterHelper.setModelValue(view.getPatientBirthday(), val);

        // 患者性別
        LetterHelper.setModelValue(view.getSexFld(), m.getPatientGender());

        // 患者住所
        LetterHelper.setModelValue(view.getPatientAddress(), m.getPatientAddress());

        // 確定日
        String dateStr = LetterHelper.getDateAsString(m.getStarted());
        LetterHelper.setModelValue(view.getConfirmedFld(), dateStr);

        // 病院住所
        val = LetterHelper.getAddressWithZipCode(m.getConsultantAddress(), m.getConsultantZipCode());
        LetterHelper.setModelValue(view.getHospitalAddressFld(), val);

        // 病院名
        LetterHelper.setModelValue(view.getHospitalNameFld(), m.getConsultantHospital());

        // 病院電話
        LetterHelper.setModelValue(view.getHospitalTelephoneFld(), m.getConsultantTelephone());

        // 医師
        LetterHelper.setModelValue(view.getDoctorNameFld(), m.getConsultantDoctor());

        //----------------------------------------------------------------------

        // 病名
        String value = model.getItemValue(ITEM_DISEASE);
        if (value != null) {
            LetterHelper.setModelValue(view.getDiseaseFld(), value);
        }

        // Informed
        String text = model.getTextValue(TEXT_INFORMED_CONTENT);
        if (text!=null) {
            LetterHelper.setModelValue(view.getInformedContent(), text);
        }
    }

    @Override
    // 2013/06/24
    //public void viewToModel() {
    public void viewToModel(boolean save) {

        if (save) {
            if (model.getId()==0L) {
                // 新規作成で保存 日時を現時刻で再設定する
                Date d = new Date();
                this.model.setConfirmed(d);
                this.model.setRecorded(d);
                this.model.setStarted(d); 
            } else {
                // 修正で保存
                Date d = new Date();
                model.setConfirmed(d);              // 確定日
                model.setRecorded(d);               // 記録日
                model.setLinkId(model.getId());     // LinkId
                model.setId(0L);                    // id=0L -> 常に新規保存 persit される、元のモデルは削除される（要変更）
            }
        }

        // 患者情報、差し出し人側はtartでmodelに設定済

        // 傷病名
        String value = LetterHelper.getFieldValue(view.getDiseaseFld());
        model.addLetterItem(new LetterItem(ITEM_DISEASE, value));

        // Informed
        String informed = LetterHelper.getAreaValue(view.getInformedContent());
        if (informed!=null) {
            LetterText text = new LetterText();
            text.setName(TEXT_INFORMED_CONTENT);
            text.setTextValue(informed);
            model.addLetterText(text);
        }
//s.oh^ 2014/09/18 文書修正
        else{
            model.addLetterText(new LetterText(TEXT_INFORMED_CONTENT, ""));
        }
//s.oh$

        // Title
        StringBuilder sb = new StringBuilder();
        sb.append(ClientContext.getMyBundle(MedicalCertificateImpl.class).getString("title.medicalCertificate")).append(":").append(value);
        model.setTitle(sb.toString());
    }

    @Override
    public void start() {
        if (this.model==null) {
            this.model = new LetterModule();

            // Handle Class
            this.model.setHandleClass(MedicalCertificateViewer.class.getName());
            this.model.setLetterType(IInfoModel.MEDICAL_CERTIFICATE);

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
            this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));
            if (patient.getSimpleAddressModel()!=null) {
                this.model.setPatientAddress(patient.getSimpleAddressModel().getAddress());
            }
            this.model.setPatientTelephone(patient.getTelephone());

            // 病院
            UserModel user = Project.getUserModel();
            this.model.setConsultantHospital(user.getFacilityModel().getFacilityName());
            this.model.setConsultantDoctor(user.getCommonName());
            this.model.setConsultantDept(user.getDepartmentModel().getDepartmentDesc());
            this.model.setConsultantTelephone(user.getFacilityModel().getTelephone());
            this.model.setConsultantFax(user.getFacilityModel().getFacsimile());
            this.model.setConsultantZipCode(user.getFacilityModel().getZipCode());
            this.model.setConsultantAddress(user.getFacilityModel().getAddress());
        }
//s.oh^ 2014/04/03 文書の複製
        else if(this.modify && this.model.getId() == 0) {
            Date d = new Date();
            this.model.setConfirmed(d);
            this.model.setRecorded(d);
            this.model.setStarted(d);
            this.model.setEnded(null);
            
            // 患者情報
            PatientModel patient = getContext().getPatient();
            this.model.setPatientId(patient.getPatientId());
            this.model.setPatientName(patient.getFullName());
            this.model.setPatientKana(patient.getKanaName());
            this.model.setPatientGender(patient.getGenderDesc());
            this.model.setPatientBirthday(patient.getBirthday());
            this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));
            if (patient.getSimpleAddressModel()!=null) {
                this.model.setPatientAddress(patient.getSimpleAddressModel().getAddress());
            }
            this.model.setPatientTelephone(patient.getTelephone());

            // 病院
            UserModel user = Project.getUserModel();
            this.model.setConsultantHospital(user.getFacilityModel().getFacilityName());
            this.model.setConsultantDoctor(user.getCommonName());
            this.model.setConsultantDept(user.getDepartmentModel().getDepartmentDesc());
            this.model.setConsultantTelephone(user.getFacilityModel().getTelephone());
            this.model.setConsultantFax(user.getFacilityModel().getFacsimile());
            this.model.setConsultantZipCode(user.getFacilityModel().getZipCode());
            this.model.setConsultantAddress(user.getFacilityModel().getAddress());
        }
//s.oh$
        // view を生成
        this.view = new MedicalCertificateView();       
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(this.view);
        JScrollPane scroller = new JScrollPane(p);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);

        modelToView(this.model);
        setEditables(true);
        setListeners();      
        
        stateMgr = new LetterStateMgr(this);      
        this.enter();        
        
//s.oh^ 文書の必要事項対応
        //view.getDiseaseFld().setBackground(Color.YELLOW);
        //view.getInformedContent().setBackground(Color.YELLOW);
//s.oh$
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        viewToModel(true);

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
                // 2013/04/19
                if (boundSupport!=null) {
                    setChartDocDidSave(true);
                    return;
                }                             
                getContext().getDocumentHistory().getLetterHistory();
                
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
        
        viewToModel(false);

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(MedicalCertificateImpl.class);
        String msg = bundle.getString("question.createPDF");
        String optionCreatePDF = bundle.getString("option.createPDF");
        String optionPrintForm = bundle.getString("option.printForm");
        String title = bundle.getString("title.optionPane.printMedicalCertificate");

        int option = JOptionPane.showOptionDialog(
                getContext().getFrame(),
                msg,
                ClientContext.getFrameTitle(title),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{optionCreatePDF, optionPrintForm, GUIFactory.getCancelButtonText()},
                optionCreatePDF);
        
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
                MedicalCertificatePDFMaker pdf = new MedicalCertificatePDFMaker();
                pdf.setDocumentDir(Project.getString(Project.LOCATION_PDF));
                pdf.setModel(model);
                return pdf.create();
            }
            
            @Override
            protected void done() {
                String err = null;
                try {
                    URI uri = Paths.get(get()).toUri();
                    Desktop.getDesktop().browse(uri);       
                } catch (IOException ex) {
                    err = ClientContext.getMyBundle(MedicalCertificateImpl.class).getString("error.cannotLaunchApplication");
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    err = ex.getMessage();
                }
                
                if (err!=null) {
                    Window parent = SwingUtilities.getWindowAncestor(getContext().getFrame());
                    JOptionPane.showMessageDialog(parent, err, ClientContext.getFrameTitle(ClientContext.getMyBundle(MedicalCertificateImpl.class).getString("title.optionPane.PDFCreation")), JOptionPane.WARNING_MESSAGE);
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

//    public void modifyKarte() {
//        stateMgr.processModifyKarteEvent();
//    }

    @Override
    public void setEditables(boolean b) {
        view.getDiseaseFld().setEditable(b);
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

        ChartMediator med = getContext().getChartMediator();

        // 傷病名
        view.getDiseaseFld().getDocument().addDocumentListener(dl);
        view.getDiseaseFld().addFocusListener(AutoKanjiListener.getInstance());
        view.getDiseaseFld().setTransferHandler(new BundleTransferHandler(med, view.getDiseaseFld()));
        view.getDiseaseFld().addMouseListener(CutCopyPasteAdapter.getInstance());

        // Informed
        view.getInformedContent().getDocument().addDocumentListener(dl);
        view.getInformedContent().addFocusListener(AutoKanjiListener.getInstance());
        view.getInformedContent().setTransferHandler(new BundleTransferHandler(med, view.getInformedContent()));
        view.getInformedContent().addMouseListener(CutCopyPasteAdapter.getInstance());

//        // 診断日
//        PopupCalendarListener pl = new PopupCalendarListener(view.getConfirmedFld());
//        view.getConfirmedFld().getDocument().addDocumentListener(dl);

        listenerIsAdded = true;
    }

    @Override
    public boolean letterIsDirty() {       
        boolean dirty = (LetterHelper.getFieldValue(view.getDiseaseFld()) != null);
        dirty = dirty || (LetterHelper.getAreaValue(view.getInformedContent()) != null);
        return dirty;       
    }
}
