
package cancer.document;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractMainTool;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.client.NewKarte;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.SimpleAddressModel;
import open.dolphin.infomodel.SimpleDate;
import open.dolphin.project.Project;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * 瀬田クリニック新規診療録（新患）登録。
 */
public class NewKarteImpl extends AbstractMainTool implements NewKarte {
    
    private String TITLE = "新患登録(新規診療録)";
    
    // 患者情報モデル
    private PatientModel patient;
    
    // 瀬田クリニック固有の情報
    private FirstEncounter0Model model;
    
    private StateMgr stateMgr;
    
    private ResourceMap resMap;
    
    private SetaPatientView view;
    private NewKarteFrame frame;
    private Action newAction;
    private Action addAndPutPvtAction;
    private Action addNewKarteAction;
    private Action modifyAction;
    private Action clearAction;
    
    // pvt を制御するフラグ
    private boolean onModify;
    
    /** Creates a new instance of NewKarte */
    public NewKarteImpl() {
        setName(TITLE);
    }
    
    @Override
    public void start() {
        
        resMap = ClientContext.getResourceMap(NewKarteImpl.class);
        
        // フォームビューを生成する
        view = new SetaPatientView();
        frame = new NewKarteFrame();
        frame.getScrollPane().setViewportView(view);
        
        // カレンダを設定する
        PopupListener pl = new PopupListener(view.getConfirmed());
        
        // Action を注入する
        ApplicationContext ctx = ClientContext.getApplicationContext();
        ActionMap map = ctx.getActionMap(NewKarteImpl.this);

        newAction = map.get("newPatient");
        modifyAction = map.get("modify");
        clearAction = map.get("clear");
        addNewKarteAction = map.get("addNewKarte");
        addAndPutPvtAction = map.get("addAndPutPvt");

        frame.getNewBtn().setAction(newAction);
        frame.getModifyBtn().setAction(modifyAction);
        frame.getAddBtn().setAction(addNewKarteAction);
        frame.getAddPvtBtn().setAction(addAndPutPvtAction);
        
        frame.getNewBtn().setToolTipText("新たに患者さんを登録する場合にクリックします。");
        frame.getModifyBtn().setToolTipText("登録した（表示されている）データを修正します。");
        frame.getAddBtn().setToolTipText("フォームに入力されたデータを登録をします。");
        frame.getAddPvtBtn().setToolTipText("登録と受付の処理を同時に行います。");
 
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });

        // SessionStrage からリストアする
        Rectangle bounds = null;
        try {
            bounds = (Rectangle) ctx.getLocalStorage().load("newPatientBounds.xml");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        if (bounds == null) {
            frame.pack();
            int n = ClientContext.isMac() ? 3 : 2;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - frame.getWidth()) / 2;
            int y = (screenSize.height - frame.getHeight()) / n;
            bounds = new Rectangle(x, y, frame.getWidth(), frame.getHeight());
        }
        frame.setBounds(bounds);
        frame.setVisible(true);
        
        stateMgr = new StateMgr();
        addListeners();
        frame.getNewBtn().doClick();
    }
    
    private JFrame getFrame() {
        return this.frame;
    }
    
    @Override
    public void stop() {
        
        if (stateMgr.isDirtyState()) {
            
            String message = resMap.getString("unsavedMessage");
            String title = resMap.getString("unsavedTitle");
            String save = resMap.getString("saveText");
            String discard = resMap.getString("discardText");
            String cancel = resMap.getString("cancelText");
            
            int option = JOptionPane.showOptionDialog(
                    getFrame(),
                    message,
                    ClientContext.getFrameTitle(title),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] { save, discard, cancel },
                    save);
            
            switch (option) {
                
            case 0:
                frame.getAddBtn().doClick();
                break;
                
            case 1:
                close();
                break;
                
            case 2:
                break;
            }
            
        } else {
            close();
        }
    }
    
    private void close() {
        
        try {
            
            ClientContext.getLocalStorage().save(frame.getBounds(), "newPatientBounds.xml");

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        frame.setVisible(false);
        frame.dispose();
    }
    
    @org.jdesktop.application.Action
    public void newPatient() {
        
        view.getNumField().setEditable(false);
        
        stateMgr.processNewEvent();
        patient = new PatientModel();
        model = new FirstEncounter0Model();
        model.setConfirmed(new Date());
        clearForm();
        display(patient, model);
    }
    
    @org.jdesktop.application.Action
    public void modify() {
        stateMgr.processModifyEvent();
    }
    
    @org.jdesktop.application.Action
    public void addAndPutPvt() {
        
        boolean ok = restore(patient, model);
        
        if (!ok) {
            return;
        }
        
        final PatientVisitModel pvt = new PatientVisitModel();
        pvt.setPatientModel(patient);
        pvt.setPvtDate(ModelUtils.getDateTimeAsString(new Date()));
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getUserModel().getDepartmentModel().getDepartmentDesc());
        sb.append(",");
        sb.append(Project.getUserModel().getDepartmentModel().getDepartment());
        pvt.setDepartment(sb.toString());
        
        Application app = ClientContext.getApplicationContext().getApplication();
        TaskMonitor taskMonitor = ClientContext.getApplicationContext().getTaskMonitor();
        
        Task task = new Task<Boolean, Void>(app) {
            
            @Override
            protected Boolean doInBackground() throws Exception {
                
                SetaDelegater ddl = new SetaDelegater();
                Object[] ids = ddl.saveOrUpdateAsPvt(pvt, model);
                //boolean result = ddl.isNoError();
                //if (result) {
                    Long pid = (Long) ids[0];
                    patient.setId(pid.longValue());
                    Long mid = (Long) ids[1];
                    model.setId(mid.longValue());
                    patient.setPatientId((String) ids[2]);
                    view.getNumField().setText(patient.getPatientId());
                //} else {
                    //System.err.println(ddl.getErrorMessage());
                //}
                return true;
            }
            
            @Override
            protected void succeeded(Boolean result) {
                
                String msg = result ? resMap.getString("successMsg") : resMap.getString("errMsg");
                JOptionPane.showMessageDialog(getFrame(), msg);
                stateMgr.processSavedEvent();
            }
        };
        
        StatusBar bar = new StatusBar(task, taskMonitor, frame.getProgressBar());
        ClientContext.getApplicationContext().getTaskService().execute(task);
    }
    
    @org.jdesktop.application.Action
    @Override
    public void addNewKarte() {
        
        boolean ok = restore(patient, model);
        
        if (!ok) {
            return;
        }
        
        Application app = ClientContext.getApplicationContext().getApplication();
        TaskMonitor taskMonitor = ClientContext.getApplicationContext().getTaskMonitor();
        
        Task task = new Task<Boolean, Void>(app) {
            

            @Override
            protected Boolean doInBackground() throws Exception {
                
                SetaDelegater ddl = new SetaDelegater();
                Object[] ids = ddl.saveOrUpdatePatient(patient, model);
                //boolean result = ddl.isNoError();
                //if (result) {
                    Long pid = (Long) ids[0];
                    patient.setId(pid.longValue());
                    Long mid = (Long) ids[1];
                    model.setId(mid.longValue());
                    patient.setPatientId((String) ids[2]);
                    view.getNumField().setText(patient.getPatientId());
                //} else {
                    //System.err.println(ddl.getErrorMessage());
                //}
                return true;
            }
            
            @Override
            protected void succeeded(Boolean result) {
                
                String msg = result ? resMap.getString("successMsg") : resMap.getString("errMsg");
                JOptionPane.showMessageDialog(getFrame(), msg);
                stateMgr.processSavedEvent();
            }
        };
        
        StatusBar bar = new StatusBar(task, taskMonitor, frame.getProgressBar());
        ClientContext.getApplicationContext().getTaskService().execute(task);
    }
    
    class StatusBar implements PropertyChangeListener {
        
        private Task task;
        private TaskMonitor taskMonitor;
        private JProgressBar bar;
        
        public StatusBar(Task task, TaskMonitor taskMonitor, JProgressBar bar) {
            this.task = task;
            this.taskMonitor = taskMonitor;
            this.taskMonitor.addPropertyChangeListener(StatusBar.this);
            this.bar = bar;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
        
            String propertyName = e.getPropertyName();

            if ("started".equals(propertyName)) {
                bar.setIndeterminate(true);

            } else if ("done".equals(propertyName)) {
                bar.setIndeterminate(false);
                bar.setValue(0);
                taskMonitor.removePropertyChangeListener(this); // 重要
            }
        }
    }
    
    @org.jdesktop.application.Action
    public void clear() {
        clearForm();
    }
   
    // 患者情報と初診時情報を表示する
    private void display(PatientModel patient, FirstEncounter0Model model) {
        
        // 番号確定日
        setFieldValue(view.getNumField(), patient.getPatientId());
        setFieldValue(view.getConfirmed(), objectToMmlDate(model.getConfirmed()));
        
        // 氏名
        setFieldValue(view.getKanaField(), patient.getKanaName());
        setFieldValue(view.getNameField(), patient.getFullName());
        
        // 性別
        selectRadio(new JRadioButton[]{view.getMaleBtn(), view.getFemaleBtn()},
                    patient.getGenderDesc());
        
        // 生年月日
        if (patient.getBirthday() != null) {
            setFieldValue(view.getBirthdayField(), patient.getBirthday());
        }
        
        // 年齢
        if (patient.getBirthday() != null && (!patient.getBirthday().equals(""))) {
            setFieldValue(view.getAgeField(), ModelUtils.getAge(patient.getBirthday()));
        }
        
        // 住所
        SimpleAddressModel adrm = patient.getSimpleAddressModel();
        if (adrm != null) {
            setFieldValue(view.getZipCodeField(), adrm.getZipCode());
            setFieldValue(view.getAddressField(), adrm.getAddress());
        }
        
        // 電話
        setFieldValue(view.getTelephoneField(), patient.getTelephone());
        setFieldValue(view.getMobileField(), patient.getMobilePhone());
        
        // 職業 勤務状況
        setComboValue(view.getOccupationCombo(), model.getOccupation());
        selectRadio(new JRadioButton[]{view.getOnBusinessBtn(), view.getOffBusinessBtn(), view.getOtherBusinessBtn()},
                    model.getOccupationStatus());
        setFieldValue(view.getOtherOccupationStatus(), model.getOtherOccupationStatus());
        
        // 上記以外の連絡先
        setFieldValue(view.getOtherContactPerson(), model.getOtherContactPerson());
        setComboValue(view.getOtherRelationCombo(), model.getOtherContactRelation());
        setFieldValue(view.getOtherContactPhone(), model.getOtherContactPhone());
        
        // 現在の病院
        setFieldValue(view.getCurrentHospital(), model.getCurrentHospital());
        setComboValue(view.getDeptCombo(), model.getCurrentDept());
        setFieldValue(view.getCurrentDoctor(), model.getCurrentDoctor());
        
        // 本日来院
        view.getThisPerosnVisit().setSelected(model.isThisPerosnVisit());
        setFieldValue(view.getOtherVisitorsName1(), model.getOtherVisitorsName1());
        setFieldValue(view.getOtherVisitorsName2(), model.getOtherVisitorsName2());
        setFieldValue(view.getOtherVisitorsName3(), model.getOtherVisitorsName3());
        setComboValue(view.getJComboBox3(), model.getOtherVisitorsRelation1());
        setComboValue(view.getJComboBox4(), model.getOtherVisitorsRelation2());
        setComboValue(view.getJComboBox5(), model.getOtherVisitorsRelation3());
        
        // 血液型感染症
        setComboValue(view.getAboCombo(), model.getABOBloodType());
        setComboValue(view.getRhdCombo(), model.getRHDBloodType());
        setFieldValue(view.getInfection(), model.getInfection());
        
        // 培養形態投与量
        setFieldValue(view.getCulture1(), model.getCulture1());
        setFieldValue(view.getCulture2(), model.getCulture2());
        setFieldValue(view.getCulture3(), model.getCulture3());
        setFieldValue(view.getToyoryou1(), model.getToyoryou1());
        setFieldValue(view.getToyoryou2(), model.getToyoryou2());
        setFieldValue(view.getToyoryou3(), model.getToyoryou3());
    }
    
    /**
     * View の値をモデルへ設定する。
     * @param patient PatientModel
     * @param model 固有情報
     * @return 有効なモデルの時 true
     */
    private boolean restore(PatientModel patient, FirstEncounter0Model model) {
        
        if (patient == null || model == null) {
            return false;
        }
        
        // 初診年月日 = confirmed （確定日）としている
        boolean canContinue = true;
        String testDate = getFieldValue(view.getConfirmed());
        try {
            String format = resMap.getString("mmlDate");
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date d = sdf.parse(testDate);
            model.setConfirmed(d);
            model.setRecorded(new Date());
            model.setStarted(d);
            model.setStatus(IInfoModel.STATUS_FINAL);
                //model.setKarte(getContext().getKarte()); EJB で設定する
            model.setUserModel(Project.getUserModel());
            
        } catch (Exception e) {
            // 日付のフォーマットが正しくない場合は警告しリターンする
            String msg = resMap.getString("illegalDate");
            JOptionPane.showMessageDialog(getFrame(), msg);
            canContinue = false;
        }
        
        if (!canContinue) {
            return false;
        }
        
        // カルテ番号 = 患者ID
        patient.setPatientId(getFieldValue(view.getNumField()));
        
        // 氏名
        patient.setKanaName(getFieldValue(view.getKanaField()));
        patient.setFullName(getFieldValue(view.getNameField()));
        
        // 性別
        if (view.getMaleBtn().isSelected()) {
            patient.setGender(IInfoModel.MALE);
            patient.setGenderDesc(IInfoModel.MALE_DISP);
        } else if (view.getFemaleBtn().isSelected()) {
            patient.setGender(IInfoModel.FEMALE);
            patient.setGenderDesc(IInfoModel.FEMALE_DISP);
        }
        
        // 生年月日
        patient.setBirthday(getFieldValue(view.getBirthdayField()));
        
        // 住所
        SimpleAddressModel adrm = new SimpleAddressModel();
        adrm.setZipCode(getFieldValue(view.getZipCodeField()));
        adrm.setAddress(getFieldValue(view.getAddressField()));
        patient.setSimpleAddressModel(adrm);
        
        // 電話
        patient.setTelephone(getFieldValue(view.getTelephoneField()));
        patient.setMobilePhone(getFieldValue(view.getMobileField()));
        
        // 職業
        model.setOccupation(getComboValue(view.getOccupationCombo()));
        model.setOccupationStatus(getRadioValue(new JRadioButton[]{view.getOnBusinessBtn(),
                                                                   view.getOffBusinessBtn(),
                                                                   view.getOtherBusinessBtn()}));
        // 上記以外の連絡先
        model.setOtherContactPerson(getFieldValue(view.getOtherContactPerson()));
        model.setOtherContactRelation(getComboValue(view.getOtherRelationCombo()));
        model.setOtherContactPhone(getFieldValue(view.getOtherContactPhone()));
        
        // 現在の病院
        model.setCurrentHospital(getFieldValue(view.getCurrentHospital()));
        model.setCurrentDept(getComboValue(view.getDeptCombo()));
        model.setCurrentDoctor(getFieldValue(view.getCurrentDoctor()));
        
        // 本日来院者
        model.setThisPerosnVisit(view.getThisPerosnVisit().isSelected());
        model.setOtherVisitorsName1(getFieldValue(view.getOtherVisitorsName1()));
        model.setOtherVisitorsName2(getFieldValue(view.getOtherVisitorsName2()));
        model.setOtherVisitorsName3(getFieldValue(view.getOtherVisitorsName3()));
        model.setOtherVisitorsRelation1(getComboValue(view.getJComboBox3()));
        model.setOtherVisitorsRelation2(getComboValue(view.getJComboBox4()));
        model.setOtherVisitorsRelation3(getComboValue(view.getJComboBox5()));
        
        // 血液型感染症
        model.setABOBloodType(getComboValue(view.getAboCombo()));
        model.setRHDBloodType(getComboValue(view.getRhdCombo()));
        model.setInfection(getFieldValue(view.getInfection()));
        
        // 培養形態投与量
        model.setCulture1(getFieldValue(view.getCulture1()));
        model.setCulture2(getFieldValue(view.getCulture2()));
        model.setCulture3(getFieldValue(view.getCulture3()));
        model.setToyoryou1(getFieldValue(view.getToyoryou1()));
        model.setToyoryou2(getFieldValue(view.getToyoryou2()));
        model.setToyoryou3(getFieldValue(view.getToyoryou3()));
        
        return true;
    }
    
    private void setEditables(boolean b) {
        
        // 番号
        view.getNumField().setEditable(false); // 自動採番
        //view.getNumField().setEditable(b);
        view.getConfirmed().setEditable(b);
        
        // 氏名
        view.getKanaField().setEditable(b);
        view.getNameField().setEditable(b);
        
        // 性別
        view.getMaleBtn().setEnabled(b);
        view.getFemaleBtn().setEnabled(b);
        
        // 生年月日
        view.getBirthdayField().setEditable(b);
        
        // 住所
        view.getZipCodeField().setEditable(b);
        view.getAddressField().setEditable(b);
       
        // 電話
        view.getTelephoneField().setEditable(b);
        view.getMobileField().setEditable(b);
        
        // 職業
        view.getOccupationCombo().setEnabled(b);
        
        // 現在の勤務状況
        view.getOnBusinessBtn().setEnabled(b);
        view.getOffBusinessBtn().setEnabled(b);
        view.getOtherBusinessBtn().setEnabled(b);
        view.getOtherOccupationStatus().setEditable(b);
        
        // 上記以外の連絡先
        view.getOtherContactPerson().setEditable(b);
        view.getOtherRelationCombo().setEnabled(b);
        view.getOtherContactPhone().setEditable(b);
        
        // 現在の病院
        view.getCurrentHospital().setEditable(b);
        view.getDeptCombo().setEnabled(b);
        view.getCurrentDoctor().setEditable(b);
        
        // 本日来院
        view.getThisPerosnVisit().setEnabled(b);
        view.getOtherVisitorsName1().setEditable(b);
        view.getOtherVisitorsName2().setEditable(b);
        view.getOtherVisitorsName3().setEditable(b);
        view.getJComboBox3().setEnabled(b);
        view.getJComboBox4().setEnabled(b);
        view.getJComboBox5().setEnabled(b);
        
        // 血液型感染症
        view.getAboCombo().setEnabled(b);
        view.getRhdCombo().setEnabled(b);
        view.getInfection().setEditable(b);
        
        // 培養形態投与量
        view.getCulture1().setEditable(b);
        view.getCulture2().setEditable(b);
        view.getCulture3().setEditable(b);
        view.getToyoryou1().setEditable(b);
        view.getToyoryou2().setEditable(b);
        view.getToyoryou3().setEditable(b);
    }
    
    private void clearForm() {
        
        // 番号
        view.getNumField().setText("");
        view.getConfirmed().setText("");
        
        // 氏名
        view.getKanaField().setText("");
        view.getNameField().setText("");
        
        // 性別
        view.getMaleBtn().setSelected(false);
        view.getFemaleBtn().setSelected(false);
        
        // 生年月日
        view.getBirthdayField().setText("");
        
        view.getAgeField().setText("");
        
        // 住所
        view.getZipCodeField().setText("");
        view.getAddressField().setText("");
       
        // 電話
        view.getTelephoneField().setText("");
        view.getMobileField().setText("");
        
        // 職業
        view.getOccupationCombo().setSelectedIndex(0);
        
        // 現在の勤務状況
        view.getOnBusinessBtn().setSelected(false);
        view.getOffBusinessBtn().setSelected(false);
        view.getOtherBusinessBtn().setSelected(false);
        view.getOtherOccupationStatus().setText("");
        
        // 上記以外の連絡先
        view.getOtherContactPerson().setText("");
        view.getOtherRelationCombo().setSelectedIndex(0);
        view.getOtherContactPhone().setText("");
        
        // 現在の病院
        view.getCurrentHospital().setText("");
        view.getDeptCombo().setSelectedIndex(0);
        view.getCurrentDoctor().setText("");
        
        // 本日来院
        view.getThisPerosnVisit().setText("");
        view.getOtherVisitorsName1().setText("");
        view.getOtherVisitorsName2().setText("");
        view.getOtherVisitorsName3().setText("");
        view.getJComboBox3().setSelectedIndex(0);
        view.getJComboBox4().setSelectedIndex(0);
        view.getJComboBox5().setSelectedIndex(0);
        
        // 血液型感染症
        view.getAboCombo().setSelectedIndex(0);
        view.getRhdCombo().setSelectedIndex(0);
        view.getInfection().setText("");
        
        // 培養形態投与量
        view.getCulture1().setText("");
        view.getCulture2().setText("");
        view.getCulture3().setText("");
        view.getToyoryou1().setText("");
        view.getToyoryou2().setText("");
        view.getToyoryou3().setText("");
    }
    
    private void addListeners() {
        
        //System.out.println("addListeners called");
        
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
        
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.processDirtyEvent();
            }
        };
        
        // 番号
        //view.getNumField().getDocument().addDocumentListener(dl);
        view.getConfirmed().getDocument().addDocumentListener(dl);
        
        // 氏名
        view.getKanaField().getDocument().addDocumentListener(dl);
        view.getNameField().getDocument().addDocumentListener(dl);
        
        // 性別
        view.getMaleBtn().addActionListener(al);
        view.getFemaleBtn().addActionListener(al);
        
        // 生年月日
        view.getBirthdayField().getDocument().addDocumentListener(dl);
        
        // 住所
        view.getZipCodeField().getDocument().addDocumentListener(dl);
        view.getAddressField().getDocument().addDocumentListener(dl);
       
        // 電話
        view.getTelephoneField().getDocument().addDocumentListener(dl);
        view.getMobileField().getDocument().addDocumentListener(dl);
        
        // 職業
        //view.getOccupation().getDocument().addDocumentListener(dl);
        
        // 現在の勤務状況
        view.getOnBusinessBtn().addActionListener(al);
        view.getOffBusinessBtn().addActionListener(al);
        view.getOtherBusinessBtn().addActionListener(al);
        view.getOtherOccupationStatus().getDocument().addDocumentListener(dl);
        
        // 上記以外の連絡先
        view.getOtherContactPerson().getDocument().addDocumentListener(dl);
        //view.getOtherContactRelation().getDocument().addDocumentListener(dl);
        view.getOtherContactPhone().getDocument().addDocumentListener(dl);
        
        // 現在の病院
        view.getCurrentHospital().getDocument().addDocumentListener(dl);
        //view.getCurrentDept().getDocument().addDocumentListener(dl);
        view.getCurrentDoctor().getDocument().addDocumentListener(dl);
        
        // 本日来院
        view.getThisPerosnVisit().addActionListener(al);
        view.getOtherVisitorsName1().getDocument().addDocumentListener(dl);
        view.getOtherVisitorsName2().getDocument().addDocumentListener(dl);
        view.getOtherVisitorsName3().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation1().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation2().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation3().getDocument().addDocumentListener(dl);
        
        // 血液型感染症
        //view.getBloodType().getDocument().addDocumentListener(dl);
        view.getInfection().getDocument().addDocumentListener(dl);
        
        // 培養形態投与量
        view.getCulture1().getDocument().addDocumentListener(dl);
        view.getCulture2().getDocument().addDocumentListener(dl);
        view.getCulture3().getDocument().addDocumentListener(dl);
        view.getToyoryou1().getDocument().addDocumentListener(dl);
        view.getToyoryou2().getDocument().addDocumentListener(dl);
        view.getToyoryou3().getDocument().addDocumentListener(dl);
        
        view.getAgeField().addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                String val = getFieldValue(view.getBirthdayField()); 
                if (val != null && (!val.equals(""))) {
                    String age = ModelUtils.getAge(val);
                    view.getAgeField().setText(age);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        
        // Focus Listeners
        //view.getNumField().addFocusListener(AutoRomanListener.getInstance());
        view.getConfirmed().addFocusListener(AutoRomanListener.getInstance());
        
        view.getKanaField().addFocusListener(AutoKanjiListener.getInstance());
        view.getNameField().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getBirthdayField().addFocusListener(AutoRomanListener.getInstance());
        
        view.getZipCodeField().addFocusListener(AutoRomanListener.getInstance());
        
        view.getAddressField().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getTelephoneField().addFocusListener(AutoRomanListener.getInstance());
        view.getMobileField().addFocusListener(AutoRomanListener.getInstance());
        
        view.getOtherOccupationStatus().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getOtherContactPerson().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherContactPhone().addFocusListener(AutoRomanListener.getInstance());
        
        view.getCurrentHospital().addFocusListener(AutoKanjiListener.getInstance());
        view.getCurrentDoctor().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getOtherVisitorsName1().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherVisitorsName2().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherVisitorsName3().addFocusListener(AutoKanjiListener.getInstance());
        
        view.getInfection().addFocusListener(AutoKanjiListener.getInstance());
        view.getCulture1().addFocusListener(AutoKanjiListener.getInstance());
        view.getCulture2().addFocusListener(AutoKanjiListener.getInstance());
        view.getCulture3().addFocusListener(AutoKanjiListener.getInstance());
        view.getToyoryou1().addFocusListener(AutoRomanListener.getInstance());
        view.getToyoryou2().addFocusListener(AutoRomanListener.getInstance());
        view.getToyoryou3().addFocusListener(AutoRomanListener.getInstance());
        
        
        // Combo
        view.getOccupationCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getOtherRelationCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getOtherRelationCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getDeptCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getJComboBox3().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getJComboBox4().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getJComboBox5().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getAboCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
        view.getRhdCombo().addItemListener((ItemListener)
            EventHandler.create(ItemListener.class, this, "comboChanged", "stateChange"));
    }
    
    private String getComboValue(JComboBox cmb) {
        String test = (String) cmb.getSelectedItem();
        if (test != null) {
            test.trim();
        }
        return (test == null || test.equals("")) ? null : test;
    }
    
    private String getFieldValue(JTextField tf) {
        String test = tf.getText().trim();
        return test.equals("") ? null : test;
    }
    
    private String getRadioValue(JRadioButton[] btns) {
        String ret = null;
        for (JRadioButton btn : btns) {
            if (btn.isSelected()) {
                ret = btn.getText();
                break;
            }
        }
        return ret;
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
    
    private void selectRadio(JRadioButton[] btns, String value) {
        for (JRadioButton b : btns) {
            if (b.getText().equals(value)) {
                b.setSelected(true);
                break;
            }
        }
    }     
    
    private String objectToMmlDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        return sdf.format(d);
    }
    
    public void comboChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            stateMgr.processDirtyEvent();
        }
    }
        
    private boolean isValidData() {

        boolean formIsValid = true;

        formIsValid = formIsValid && (getFieldValue(view.getConfirmed()) != null);
        formIsValid = formIsValid && (getFieldValue(view.getKanaField()) != null);
        formIsValid = formIsValid && (getFieldValue(view.getNameField()) != null);
        formIsValid = formIsValid && (view.getMaleBtn().isSelected() || view.getFemaleBtn().isSelected());
        formIsValid = formIsValid && (getFieldValue(view.getBirthdayField()) != null);
        formIsValid = formIsValid && (getFieldValue(view.getAddressField()) != null);

        return formIsValid;
    }
        
    protected abstract class State {
        
        public abstract void enter();
    }
    
    class EmptyState extends State {
        
        @Override
        public void enter() {
            newAction.setEnabled(true);
            modifyAction.setEnabled(false);
            clearAction.setEnabled(false);
            addNewKarteAction.setEnabled(false);
            addAndPutPvtAction.setEnabled(false);
        }
    }
    
    class NewState extends State {
        
        @Override
        public void enter() {
            setEditables(true);
            newAction.setEnabled(false);
            modifyAction.setEnabled(false);
            clearAction.setEnabled(false);
            addNewKarteAction.setEnabled(false);
            addAndPutPvtAction.setEnabled(false);
        }
    }
    
    class CleanState extends State {
        
        @Override
        public void enter() {
            setEditables(false);
            newAction.setEnabled(true);
            modifyAction.setEnabled(true);
            clearAction.setEnabled(false);
            addNewKarteAction.setEnabled(false);
            addAndPutPvtAction.setEnabled(false);
        }
    }
    
    class DirtyState extends State {
        
        @Override
        public void enter() {
            newAction.setEnabled(false);
            modifyAction.setEnabled(false);
            clearAction.setEnabled(true);
            addNewKarteAction.setEnabled(true);
            // 更新の場合は一度保存されているので
            // pvt としての登録はさせない
            addAndPutPvtAction.setEnabled(!onModify);
        }
    }
    
    // 修正ボタンが押された時の状態
    class StartEditing extends State {
        
        @Override
        public void enter() {
            setEditables(true);
            newAction.setEnabled(false);
            modifyAction.setEnabled(false);
            clearAction.setEnabled(true);
            addNewKarteAction.setEnabled(false);
            addAndPutPvtAction.setEnabled(false);
        }
    }
    
    /**
     * StateContext クラス。
     */
    class StateMgr {
        
        private EmptyState emptyState = new EmptyState();
        private NewState newState = new NewState();
        private CleanState cleanState = new CleanState();
        private DirtyState dirtyState = new DirtyState();
        private StartEditing startEditing = new StartEditing();
        private State curState;
        
        public StateMgr() {
            curState = emptyState;
            curState.enter();
        }
        
        public void enter() {
            curState.enter();
        }
        
        public void processNewEvent() {
            curState = newState;
            onModify = false;
            enter();
        }
        
        public void processDirtyEvent() {

            boolean newDirty = isValidData();
            
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
            onModify = true;
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