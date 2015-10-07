package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa @digital-globe.co.jp
 */
public class PHRLabModule implements Serializable {

    // Module ID
    private String catchId;

    // 患者氏名
    private String patientName;

    // 患者性別
    private String patientSex;
    
    // 患者生年月日
    private String patientBirthday;
    
    // 医療機関ID
    private String facilityId;
    
    // 医療機関名
    private String facilityName;
    
    // JMARI等
    private String facilityNumber; 
    
    // ラボコード
    private String labCenterCode;

    // 検体採取日または検査受付日時
    private String sampleDate;

    // この検査モジュールに含まれている検査項目の数
    private String numOfItems;

    // Report format
    private String reportFormat;

    // ManyToOne
    private List<PHRLabItem> testItems;
    
    public PHRLabModule() {
        testItems = new ArrayList();
    }

    public String getCatchId() {
        return catchId;
    }

    public void setCatchId(String moduleId) {
        this.catchId = moduleId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityNumber() {
        return facilityNumber;
    }

    public void setFacilityNumber(String facilityNumber) {
        this.facilityNumber = facilityNumber;
    }

    public String getLabCenterCode() {
        return labCenterCode;
    }

    public void setLabCenterCode(String labCenterCode) {
        this.labCenterCode = labCenterCode;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getNumOfItems() {
        return numOfItems;
    }

    public void setNumOfItems(String numOfItems) {
        this.numOfItems = numOfItems;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public List<PHRLabItem> getTestItems() {
        return testItems;
    }

    public void setTestItems(List<PHRLabItem> phrItems) {
        this.testItems = phrItems;
    }
    
    public void addTestItem(PHRLabItem phrLabItem) {
        this.testItems.add(phrLabItem);
    }
}
