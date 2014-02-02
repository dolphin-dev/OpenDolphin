
package cancer.document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import open.dolphin.client.*;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.delegater.SetaDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.FirstEncounter0Model;
import open.dolphin.infomodel.FirstEncounterModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.SimpleAddressModel;
import open.dolphin.project.Project;


/**
 * Documet to show Patient and Health Insurance info.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class SetaPatientInfoDocument extends AbstractChartDocument {
    
    // Title
    private static final String TITLE = "ä≥é“èÓïÒ";
    
    // èâêféûèÓïÒï∂èëÉ^ÉCÉv
    private static final String DOC_TYPE = "SETA_0";
    
    // èâêféûèÓïÒÉÇÉfÉã
    private FirstEncounter0Model model;
    
    private SetaPatientView view;
    
    // State Context
    private StateContext stateMgr;
    private boolean empty;
    
    /** 
     * Creates new PatientInfoDocument 
     */
    public SetaPatientInfoDocument() {
        setTitle(TITLE);   
    }
    
    @Override
    public void start() {
        
        // view Çê∂ê¨Ç∑ÇÈ
        this.view = new SetaPatientView();
        JScrollPane scroller = new JScrollPane(this.view);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
        
        // StateContext Çê∂ê¨Ç∑ÇÈ
        super.enter();
        stateMgr = new StateContext();
        
        // èâêféûèÓïÒÇéÊìæÇ∑ÇÈ
        final long pk = getContext().getKarte().getId();
        
        DBTask task = new DBTask<FirstEncounter0Model, Void>(getContext()) {
            
            @Override
            protected FirstEncounter0Model doInBackground() throws Exception {
                SetaDelegater ddl = new SetaDelegater();
                List<FirstEncounterModel> list =  ddl.getFirstEncounter(pk, DOC_TYPE);
                if (list != null && list.size() > 0) {
                    FirstEncounter0Model result = (FirstEncounter0Model) list.get(0);
                    return result;
                } else {
                    return null;
                }
            }
            
            @Override
            protected void succeeded(FirstEncounter0Model result) {
                if (result != null) {
                    model = result;
                    display(model);
                    empty = false;
                } else {
                    empty = true;
                }
                stateMgr.start();
            }
        };
        
        task.execute();
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
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
     * ä≥é“èÓïÒÇçXêVÇ∑ÇÈÅB
     */
    @Override
    public void save() {
        
        PatientModel patient = getContext().getPatient();
        restore(patient, model);
        
        DBTask task = new UpdateTask(getContext(), patient, model);
        
        task.execute();
    }
    
    // çXêVÉ^ÉXÉN
    class UpdateTask extends DBTask<Void, Void> {
        
        private PatientModel patient;
        private FirstEncounter0Model fem;
        private SetaDelegater ddl;
        
        public UpdateTask(Chart ctx, PatientModel patient, FirstEncounter0Model fem) {
            super(ctx);
            this.patient = patient;
            this.fem = fem;
        }
        
        @Override
        protected Void doInBackground() {
            ddl = new SetaDelegater();
            ddl.saveOrUpdatePatient(patient, fem);
            
            return null;
        }
                
        @Override
        protected void succeeded(Void result) {
            stateMgr.processSavedEvent();
        }
    }
    
    public void modifyKarte() {
        stateMgr.processModifyEvent();
    }
    
    // ä≥é“èÓïÒÇ∆èâêféûèÓïÒÇï\é¶Ç∑ÇÈ
    private void display(FirstEncounter0Model model) {
        
        // ä≥é“èÓïÒ
        PatientModel patient = getContext().getPatient();
        
        // î‘çÜ
        setFieldValue(view.getNumField(), patient.getPatientId());
        
        // èâêfì˙
        setFieldValue(view.getConfirmed(), dateToString2(model.getConfirmed()));
        
        // éÅñº
        setFieldValue(view.getKanaField(), patient.getKanaName());
        setFieldValue(view.getNameField(), patient.getFullName());
        
        // ê´ï 
        selectRadio(new JRadioButton[]{view.getMaleBtn(), view.getFemaleBtn()},
                    patient.getGenderDesc());
        
        // ê∂îNåéì˙
        setFieldValue(view.getBirthdayField(), patient.getBirthday());
        
        // îNóÓ
        setFieldValue(view.getAgeField(), ModelUtils.getAge(patient.getBirthday()));
        
        // èZèä
        SimpleAddressModel adrm = patient.getSimpleAddressModel();
        if (adrm != null) {
            setFieldValue(view.getZipCodeField(), adrm.getZipCode());
            setFieldValue(view.getAddressField(), adrm.getAddress());
        }
        
        // ìdòb
        setFieldValue(view.getTelephoneField(), patient.getTelephone());
        setFieldValue(view.getMobileField(), patient.getMobilePhone());
        
        // êEã∆ ãŒñ±èÛãµ
        setComboValue(view.getOccupationCombo(), model.getOccupation());
        selectRadio(new JRadioButton[]{view.getOnBusinessBtn(), view.getOffBusinessBtn(), view.getOtherBusinessBtn()},
                    model.getOccupationStatus());
        setFieldValue(view.getOtherOccupationStatus(), model.getOtherOccupationStatus());
        
        // è„ãLà»äOÇÃòAóçêÊ
        setFieldValue(view.getOtherContactPerson(), model.getOtherContactPerson());
        setComboValue(view.getOtherRelationCombo(), model.getOtherContactRelation());
        setFieldValue(view.getOtherContactPhone(), model.getOtherContactPhone());
        
        // åªç›ÇÃïaâ@
        setFieldValue(view.getCurrentHospital(), model.getCurrentHospital());
        setComboValue(view.getDeptCombo(), model.getCurrentDept());
        setFieldValue(view.getCurrentDoctor(), model.getCurrentDoctor());
        
        // ñ{ì˙óàâ@
        view.getThisPerosnVisit().setSelected(model.isThisPerosnVisit());
        setFieldValue(view.getOtherVisitorsName1(), model.getOtherVisitorsName1());
        setFieldValue(view.getOtherVisitorsName2(), model.getOtherVisitorsName2());
        setFieldValue(view.getOtherVisitorsName3(), model.getOtherVisitorsName3());
        setComboValue(view.getJComboBox3(), model.getOtherVisitorsRelation1());
        setComboValue(view.getJComboBox4(), model.getOtherVisitorsRelation2());
        setComboValue(view.getJComboBox5(), model.getOtherVisitorsRelation3());
        
        // ååâtå^ä¥êıè«
        setComboValue(view.getAboCombo(), model.getABOBloodType());
        setComboValue(view.getRhdCombo(), model.getRHDBloodType());
        
        // rhd
        setFieldValue(view.getInfection(), model.getInfection());
        
        // î|ó{å`ë‘ìäó^ó 
        setFieldValue(view.getCulture1(), model.getCulture1());
        setFieldValue(view.getCulture2(), model.getCulture2());
        setFieldValue(view.getCulture3(), model.getCulture3());
        setFieldValue(view.getToyoryou1(), model.getToyoryou1());
        setFieldValue(view.getToyoryou2(), model.getToyoryou2());
        setFieldValue(view.getToyoryou3(), model.getToyoryou3());
    }
    
    private void restore(PatientModel patient, FirstEncounter0Model model) {
        
        // èâêfì˙
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf1.parse(getFieldValue(view.getConfirmed()));
            model.setConfirmed(d);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        // ãLò^ì˙ìô
        Date d = new Date();
        model.setRecorded(d);
        model.setStatus(IInfoModel.STATUS_FINAL);
        model.setKarteBean(getContext().getKarte());
        model.setUserModel(Project.getUserModel());
        
        // éÅñº
        patient.setKanaName(getFieldValue(view.getKanaField()));
        patient.setFullName(getFieldValue(view.getNameField()));
        
        // ê´ï 
        if (view.getMaleBtn().isSelected()) {
            patient.setGender(IInfoModel.MALE);
            patient.setGenderDesc(IInfoModel.MALE_DISP);
        } else if (view.getFemaleBtn().isSelected()) {
            patient.setGender(IInfoModel.FEMALE);
            patient.setGenderDesc(IInfoModel.FEMALE_DISP);
        }
        
        // ê∂îNåéì˙
        patient.setBirthday(getFieldValue(view.getBirthdayField()));
        
        // èZèä
        SimpleAddressModel adrm = new SimpleAddressModel();
        adrm.setZipCode(getFieldValue(view.getZipCodeField()));
        adrm.setAddress(getFieldValue(view.getAddressField()));
        patient.setSimpleAddressModel(adrm);
        
        // ìdòb
        patient.setTelephone(getFieldValue(view.getTelephoneField()));
        patient.setMobilePhone(getFieldValue(view.getMobileField()));
        
        // êEã∆
        model.setOccupation(getComboValue(view.getOccupationCombo()));
        model.setOccupationStatus(getRadioValue(new JRadioButton[]{view.getOnBusinessBtn(),
                                                                   view.getOffBusinessBtn(),
                                                                   view.getOtherBusinessBtn()}));
        // è„ãLà»äOÇÃòAóçêÊ
        model.setOtherContactPerson(getFieldValue(view.getOtherContactPerson()));
        model.setOtherContactRelation(getComboValue(view.getOtherRelationCombo()));
        model.setOtherContactPhone(getFieldValue(view.getOtherContactPhone()));
        
        // åªç›ÇÃïaâ@
        model.setCurrentHospital(getFieldValue(view.getCurrentHospital()));
        model.setCurrentDept(getComboValue(view.getDeptCombo()));
        model.setCurrentDoctor(getFieldValue(view.getCurrentDoctor()));
        
        // ñ{ì˙óàâ@é“
        model.setThisPerosnVisit(view.getThisPerosnVisit().isSelected());
        model.setOtherVisitorsName1(getFieldValue(view.getOtherVisitorsName1()));
        model.setOtherVisitorsName2(getFieldValue(view.getOtherVisitorsName2()));
        model.setOtherVisitorsName3(getFieldValue(view.getOtherVisitorsName3()));
        model.setOtherVisitorsRelation1(getComboValue(view.getJComboBox3()));
        model.setOtherVisitorsRelation2(getComboValue(view.getJComboBox4()));
        model.setOtherVisitorsRelation3(getComboValue(view.getJComboBox5()));
        
        // ååâtå^ä¥êıè«
        model.setABOBloodType(getComboValue(view.getAboCombo()));
        model.setRHDBloodType(getComboValue(view.getRhdCombo()));
        
        //ä¥êıè«
        model.setInfection(getFieldValue(view.getInfection()));
        
        // î|ó{å`ë‘ìäó^ó 
        model.setCulture1(getFieldValue(view.getCulture1()));
        model.setCulture2(getFieldValue(view.getCulture2()));
        model.setCulture3(getFieldValue(view.getCulture3()));
        model.setToyoryou1(getFieldValue(view.getToyoryou1()));
        model.setToyoryou2(getFieldValue(view.getToyoryou2()));
        model.setToyoryou3(getFieldValue(view.getToyoryou3()));
    }
    
    private void setEditables(boolean b) {
        
        // ä≥é“ID
        view.getNumField().setEditable(false);
        
        // èâêfì˙
        view.getConfirmed().setEditable(b);
        
        // éÅñº
        view.getKanaField().setEditable(b);
        view.getNameField().setEditable(b);
        
        // ê´ï 
        view.getMaleBtn().setEnabled(b);
        view.getFemaleBtn().setEnabled(b);
        
        // ê∂îNåéì˙
        view.getBirthdayField().setEditable(b);
        
        // èZèä
        view.getZipCodeField().setEditable(b);
        view.getAddressField().setEditable(b);
       
        // ìdòb
        view.getTelephoneField().setEditable(b);
        view.getMobileField().setEditable(b);
        
        // êEã∆
        view.getOccupationCombo().setEnabled(b);
        
        // åªç›ÇÃãŒñ±èÛãµ
        view.getOnBusinessBtn().setEnabled(b);
        view.getOffBusinessBtn().setEnabled(b);
        view.getOtherBusinessBtn().setEnabled(b);
        view.getOtherOccupationStatus().setEditable(b);
        
        // è„ãLà»äOÇÃòAóçêÊ
        view.getOtherContactPerson().setEditable(b);
        view.getOtherRelationCombo().setEnabled(b);
        view.getOtherContactPhone().setEditable(b);
        
        // åªç›ÇÃïaâ@
        view.getCurrentHospital().setEditable(b);
        view.getDeptCombo().setEnabled(b);
        view.getCurrentDoctor().setEditable(b);
        
        // ñ{ì˙óàâ@
        view.getThisPerosnVisit().setEnabled(b);
        view.getOtherVisitorsName1().setEditable(b);
        view.getOtherVisitorsName2().setEditable(b);
        view.getOtherVisitorsName3().setEditable(b);
        view.getJComboBox3().setEnabled(b);
        view.getJComboBox4().setEnabled(b);
        view.getJComboBox5().setEnabled(b);
        
        // ååâtå^ä¥êıè«
        view.getAboCombo().setEnabled(b);
        view.getRhdCombo().setEnabled(b);
        view.getInfection().setEditable(b);
        
        // î|ó{å`ë‘ìäó^ó 
        view.getCulture1().setEditable(b);
        view.getCulture2().setEditable(b);
        view.getCulture3().setEditable(b);
        view.getToyoryou1().setEditable(b);
        view.getToyoryou2().setEditable(b);
        view.getToyoryou3().setEditable(b);
    }
    
    private void addListeners() {
        
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
        
        
        // î‘çÜ
        view.getNumField().getDocument().addDocumentListener(dl);
        view.getConfirmed().getDocument().addDocumentListener(dl);
        view.getNumField().addFocusListener(AutoRomanListener.getInstance());
        
        // èâêfîNåéì˙
        view.getConfirmed().addFocusListener(AutoRomanListener.getInstance());
        
        // éÅñº
        view.getKanaField().getDocument().addDocumentListener(dl);
        view.getKanaField().addFocusListener(AutoKanjiListener.getInstance());
        view.getNameField().getDocument().addDocumentListener(dl);
        view.getNameField().addFocusListener(AutoKanjiListener.getInstance());
        
        // ê´ï 
        view.getMaleBtn().addActionListener(al);
        view.getFemaleBtn().addActionListener(al);
        
        // ê∂îNåéì˙
        view.getBirthdayField().getDocument().addDocumentListener(dl);
        view.getBirthdayField().addFocusListener(AutoRomanListener.getInstance());
        
        // èZèä
        view.getZipCodeField().getDocument().addDocumentListener(dl);
        view.getZipCodeField().addFocusListener(AutoRomanListener.getInstance());
        view.getAddressField().getDocument().addDocumentListener(dl);
        view.getAddressField().addFocusListener(AutoKanjiListener.getInstance());
       
        // ìdòb
        view.getTelephoneField().getDocument().addDocumentListener(dl);
        view.getTelephoneField().addFocusListener(AutoRomanListener.getInstance());
        view.getMobileField().getDocument().addDocumentListener(dl);
        view.getMobileField().addFocusListener(AutoRomanListener.getInstance());
        
        // êEã∆
        //view.getOccupationCombo().addItemListener(il);
        
        // åªç›ÇÃãŒñ±èÛãµ
        view.getOnBusinessBtn().addActionListener(al);
        view.getOffBusinessBtn().addActionListener(al);
        view.getOtherBusinessBtn().addActionListener(al);
        view.getOtherOccupationStatus().getDocument().addDocumentListener(dl);
        view.getOtherOccupationStatus().addFocusListener(AutoKanjiListener.getInstance());
        
        // è„ãLà»äOÇÃòAóçêÊ
        view.getOtherContactPerson().getDocument().addDocumentListener(dl);
        view.getOtherContactPerson().addFocusListener(AutoKanjiListener.getInstance());
        //view.getOtherContactRelation().getDocument().addDocumentListener(dl);
        //view.getOtherContactRelation().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherContactPhone().getDocument().addDocumentListener(dl);
        view.getOtherContactPhone().addFocusListener(AutoRomanListener.getInstance());
        
        // åªç›ÇÃïaâ@
        view.getCurrentHospital().getDocument().addDocumentListener(dl);
        //view.getCurrentDept().getDocument().addDocumentListener(dl);
        view.getCurrentDoctor().getDocument().addDocumentListener(dl);
        view.getCurrentHospital().addFocusListener(AutoKanjiListener.getInstance());
        //view.getCurrentDept().addFocusListener(AutoKanjiListener.getInstance());
        view.getCurrentDoctor().addFocusListener(AutoKanjiListener.getInstance());
                
        // ñ{ì˙óàâ@
        view.getThisPerosnVisit().addActionListener(al);
        view.getOtherVisitorsName1().getDocument().addDocumentListener(dl);
        view.getOtherVisitorsName2().getDocument().addDocumentListener(dl);
        view.getOtherVisitorsName3().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation1().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation2().getDocument().addDocumentListener(dl);
        //view.getOtherVisitorsRelation3().getDocument().addDocumentListener(dl);
        view.getOtherVisitorsName1().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherVisitorsName2().addFocusListener(AutoKanjiListener.getInstance());
        view.getOtherVisitorsName3().addFocusListener(AutoKanjiListener.getInstance());
        //view.getOtherVisitorsRelation1().addFocusListener(AutoKanjiListener.getInstance());
        //view.getOtherVisitorsRelation2().addFocusListener(AutoKanjiListener.getInstance());
        //view.getOtherVisitorsRelation3().addFocusListener(AutoKanjiListener.getInstance());
        
        
        // ååâtå^ä¥êıè«
        //view.getBloodType().getDocument().addDocumentListener(dl);
        view.getInfection().getDocument().addDocumentListener(dl);
        //view.getBloodType().addFocusListener(AutoRomanListener.getInstance());
        view.getInfection().addFocusListener(AutoKanjiListener.getInstance());
        
        // î|ó{å`ë‘ìäó^ó 
        view.getCulture1().getDocument().addDocumentListener(dl);
        view.getCulture2().getDocument().addDocumentListener(dl);
        view.getCulture3().getDocument().addDocumentListener(dl);
        view.getToyoryou1().getDocument().addDocumentListener(dl);
        view.getToyoryou2().getDocument().addDocumentListener(dl);
        view.getToyoryou3().getDocument().addDocumentListener(dl);
        
        view.getCulture1().addFocusListener(AutoKanjiListener.getInstance());
        view.getCulture2().addFocusListener(AutoKanjiListener.getInstance());
        view.getCulture3().addFocusListener(AutoKanjiListener.getInstance());
        view.getToyoryou1().addFocusListener(AutoRomanListener.getInstance());
        view.getCulture2().addFocusListener(AutoRomanListener.getInstance());
        view.getCulture3().addFocusListener(AutoRomanListener.getInstance());
        
        
        view.getAgeField().addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                String val = getFieldValue(view.getBirthdayField()); 
                if (val != null) {
                    String age = ModelUtils.getAge(val);
                    view.getAgeField().setText(age);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        
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
    
    public void comboChanged(int state) {
        if (state == ItemEvent.SELECTED) {
            stateMgr.processDirtyEvent();
        }
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
    
    private String dateToString(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyîNMåédì˙");
        return sdf.format(d);
    }
    
    private String dateToString2(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
    
    private String mmlDateToString(String mmlDate) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf1.parse(mmlDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyîNMåédì˙");
            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
    
    private boolean isValidData() {
        boolean newValid = ( (getFieldValue(view.getConfirmed()) != null) &&
                              (getFieldValue(view.getKanaField()) != null) &&
                              (getFieldValue(view.getNameField()) != null) &&
                              (getFieldValue(view.getBirthdayField()) != null) &&
                              (getFieldValue(view.getAddressField()) != null)) ? true : false;
        return newValid;
    }
    
    abstract class State {
        
        public abstract void enter();
        
    }
    
    class EmptyState extends State {
        
        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class CleanState extends State {
        
        @Override
        public void enter() {
            setEditables(false);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);
        }
    }
    
    class DirtyState extends State {
        
        @Override
        public void enter() {
            getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class StartEditing extends State {
        
        @Override
        public void enter() {
            setEditables(true);
            getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
    
    class StateContext {
        
        private EmptyState emptyState = new EmptyState();
        private CleanState cleanState = new CleanState();
        private DirtyState dirtyState = new DirtyState();
        private StartEditing startEditing = new StartEditing();
        private State curState;
        
        public StateContext() {
        }
        
        public void start() {
            if (!empty) {
                processCleanEvent();
            } else {
                processEmptyEvent();
            }
            addListeners();
        }
        
        public void enter() {
            curState.enter();
        }
        
        public void processEmptyEvent() {
            curState = emptyState;
            enter();
        }
        
        public void processCleanEvent() {
            curState = cleanState;
            enter();
        }
        
        public void processSavedEvent() {
            curState = cleanState;
            this.enter();
        }
        
        public void processDirtyEvent() {
            
            boolean newDirty = isValidData();
            
            if (isDirtyState() != newDirty) {
                curState = newDirty ? dirtyState : curState;
                enter();
            }
        }
        
        public void processModifyEvent() {
            curState = startEditing;
            enter();
        }
        
        public boolean isDirtyState() {
            return curState == dirtyState ? true : false;
        }
    }
}