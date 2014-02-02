package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table (name = "d_letter_module")
public class LetterModule extends KarteEntryBean implements Serializable {

    // Title
    private String title;

    // è–âÓèÛéÌï 
    private String letterType;

    // Handle Class
    private String handleClass;

    // è–âÓå≥ïaâ@
    private String clientHospital;

    // è–âÓå≥êfó√â»
    private String clientDept;

    // è–âÓå≥à„ét
    private String clientDoctor;

    // è–âÓå≥óXï÷î‘çÜ
    private String clientZipCode;

    // è–âÓå≥èZèä
    private String clientAddress;

    // è–âÓå≥ìdòbî‘çÜ
    private String clientTelephone;

    // è–âÓå≥FAXî‘çÜ
    private String clientFax;

    // è–âÓêÊïaâ@
    private String consultantHospital;

    // è–âÓêÊêfó√â»
    private String consultantDept;

    // è–âÓêÊà„ét
    private String consultantDoctor;

    // è–âÓêÊóXï÷î‘çÜ
    private String consultantZipCode;

    // è–âÓêÊèZèä
    private String consultantAddress;

    // è–âÓêÊìdòbî‘çÜ
    private String consultantTelephone;

    // è–âÓêÊFAXî‘çÜ
    private String consultantFax;

    // ä≥é“ID
    private String patientId;

    // ä≥é“éÅñº
    private String patientName;

    // ä≥é“ÉJÉi
    private String patientKana;

    // ä≥é“ê´ï 
    private String patientGender;

    // ä≥é“ê∂îNåéì˙
    private String patientBirthday;

    // ä≥é“îNóÓ
    private String patientAge;

    // êEã∆
    private String patientOccupation;

    // ä≥é“óXï÷î‘çÜ
    private String patientZipCode;

    // ä≥é“èZèä
    private String patientAddress;
    
    // ä≥é“ìdòbî‘çÜ
    private String patientTelephone;
    
    // ä≥é“ågë—î‘çÜ
    private String patientMobilePhone;
    
    // ä≥é“Faxî‘çÜ
    private String patientFaxNumber;

    //@OneToMany(mappedBy="module", cascade={CascadeType.ALL})
    @Transient
    private List<LetterItem> letterItems;

    //@OneToMany(mappedBy="module", cascade={CascadeType.ALL})
    @Transient
    private List<LetterText> letterTexts;

    @Transient
    private List<LetterDate> letterDates;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getLetterType() {
        return letterType;
    }

    public void setLetterType(String letterType) {
        this.letterType = letterType;
    }

    public String getHandleClass() {
        return handleClass;
    }

    public void setHandleClass(String handleClass) {
        this.handleClass = handleClass;
    }

    public String getClientHospital() {
        return clientHospital;
    }

    public void setClientHospital(String clientHospital) {
        this.clientHospital = clientHospital;
    }

    public String getClientDept() {
        return clientDept;
    }

    public void setClientDept(String clientDept) {
        this.clientDept = clientDept;
    }

    public String getClientDoctor() {
        return clientDoctor;
    }

    public void setClientDoctor(String clientDoctor) {
        this.clientDoctor = clientDoctor;
    }

    public String getClientZipCode() {
        return clientZipCode;
    }

    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }

    public String getClientFax() {
        return clientFax;
    }

    public void setClientFax(String clientFax) {
        this.clientFax = clientFax;
    }

    public String getConsultantHospital() {
        return consultantHospital;
    }

    public void setConsultantHospital(String consultantHospital) {
        this.consultantHospital = consultantHospital;
    }

    public String getConsultantDept() {
        return consultantDept;
    }

    public void setConsultantDept(String consultantDept) {
        this.consultantDept = consultantDept;
    }

    public String getConsultantDoctor() {
        return consultantDoctor;
    }

    public void setConsultantDoctor(String consultantDoctor) {
        this.consultantDoctor = consultantDoctor;
    }

    public String getConsultantZipCode() {
        return consultantZipCode;
    }

    public void setConsultantZipCode(String consultantZipCode) {
        this.consultantZipCode = consultantZipCode;
    }

    public String getConsultantAddress() {
        return consultantAddress;
    }

    public void setConsultantAddress(String consultantAddress) {
        this.consultantAddress = consultantAddress;
    }

    public String getConsultantTelephone() {
        return consultantTelephone;
    }

    public void setConsultantTelephone(String consultantTelephone) {
        this.consultantTelephone = consultantTelephone;
    }

    public String getConsultantFax() {
        return consultantFax;
    }

    public void setConsultantFax(String consultantFax) {
        this.consultantFax = consultantFax;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientKana() {
        return patientKana;
    }

    public void setPatientKana(String patientKana) {
        this.patientKana = patientKana;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientOccupation() {
        return patientOccupation;
    }

    public void setPatientOccupation(String patientOccupation) {
        this.patientOccupation = patientOccupation;
    }

    public String getPatientZipCode() {
        return patientZipCode;
    }

    public void setPatientZipCode(String patientZipCode) {
        this.patientZipCode = patientZipCode;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientTelephone() {
        return patientTelephone;
    }

    public void setPatientTelephone(String patientTelephone) {
        this.patientTelephone = patientTelephone;
    }

    public String getPatientMobilePhone() {
        return patientMobilePhone;
    }

    public void setPatientMobilePhone(String patientMobilePhone) {
        this.patientMobilePhone = patientMobilePhone;
    }

    public String getPatientFaxNumber() {
        return patientFaxNumber;
    }

    public void setPatientFaxNumber(String patientFaxNumber) {
        this.patientFaxNumber = patientFaxNumber;
    }

    public List<LetterItem> getLetterItems() {
        return letterItems;
    }

    public void setLetterItems(List<LetterItem> letterItems) {
        this.letterItems = letterItems;
    }

    public List<LetterText> getLetterTexts() {
        return letterTexts;
    }

    public void setLetterTexts(List<LetterText> letterTexts) {
        this.letterTexts = letterTexts;
    }

    public List<LetterDate> getLetterDates() {
        return letterDates;
    }

    public void setLetterDates(List<LetterDate> letterDates) {
        this.letterDates = letterDates;
    }
    
    public void addLetterItem(LetterItem item) {
        if (letterItems==null) {
            letterItems = new ArrayList<LetterItem>();
            letterItems.add(item);
        } else {
            LetterItem exist = getLetterItem(item.getName());
            if (exist == null) {
                letterItems.add(item);
            } else {
                exist.setValue(item.getValue());
            }
        }
    }

    public void addLetterText(LetterText text) {
        if (letterTexts==null) {
            letterTexts = new ArrayList<LetterText>();
            letterTexts.add(text);
        } else {
            LetterText exist = getLetterText(text.getName());
            if (exist == null) {
                letterTexts.add(text);
            } else {
                exist.setTextValue(text.getTextValue());
            }
        }
    }

    public void addLetterDate(LetterDate date) {
        if (letterDates==null) {
            letterDates = new ArrayList<LetterDate>();
            letterDates.add(date);
        } else {
            LetterDate exist = getLetterDate(date.getName());
            if (exist == null) {
                letterDates.add(date);
            } else {
                exist.setValue(date.getValue());
            }
        }
    }

    public LetterItem getLetterItem(String name) {

        LetterItem ret = null;

        if (letterItems!=null) {
            for (LetterItem item : letterItems) {
                if (item.getName().equals(name)) {
                    ret = item;
                    break;
                }
            }
        }

        return ret;
    }

    public LetterText getLetterText(String name) {

        LetterText ret = null;

        if (letterTexts!=null) {
            for (LetterText txt : letterTexts) {
                if (txt.getName().equals(name)) {
                    ret = txt;
                    break;
                }
            }
        }

        return ret;
    }

    public LetterDate getLetterDate(String name) {

        LetterDate ret = null;

        if (letterDates!=null) {
            for (LetterDate date : letterDates) {
                if (date.getName().equals(name)) {
                    ret = date;
                    break;
                }
            }
        }

        return ret;
    }

    public String getItemValue(String name) {

        String value = null;

        if (letterItems!=null) {
            for (LetterItem item : letterItems) {
                if (item.getName().equals(name)) {
                    value = item.getValue();
                    break;
                }
            }
        }

        return value;
    }

    public String getTextValue(String name) {

        String value = null;

        if (letterTexts!=null) {
            for (LetterText txt : letterTexts) {
                if (txt.getName().equals(name)) {
                    value = txt.getTextValue();
                    break;
                }
            }
        }

        return value;
    }

    public Date getDateValue(String name) {

        Date value = null;

        if (letterDates!=null) {
            for (LetterDate date : letterDates) {
                if (date.getName().equals(name)) {
                    value = date.getValue();
                    break;
                }
            }
        }

        return value;
    }
}
