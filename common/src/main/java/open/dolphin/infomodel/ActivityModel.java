package open.dolphin.infomodel;

import java.util.Date;

/**
 *
 * @author kazushi Minagawa. LSC
 */
public class ActivityModel implements java.io.Serializable {
    
    // M=Month Y=Year T=Total
    private String flag;
    private int year;
    private int month;
    private Date fromDate;
    private Date toDate;
    
    private String facilityId;
    private String facilityName;
    private String facilityZip;
    private String facilityAddress;
    private String facilityTelephone;
    private String facilityFacimile;
    
    private long numOfUsers;
    
    private long numOfPatients;
    
    private long numOfPatientVisits;
    
    private long numOfKarte;
    
    private long numOfImages;
    
    private long numOfAttachments;
    
    private long numOfDiagnosis;
   
    private long numOfLetters;
    
    private long numOfLabTests;
    
    private String dbSize;
    
    private String bindAddress;

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return the facilityId
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return the facilityName
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * @param facilityName the facilityName to set
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * @return the facilityZip
     */
    public String getFacilityZip() {
        return facilityZip;
    }

    /**
     * @param facilityZip the facilityZip to set
     */
    public void setFacilityZip(String facilityZip) {
        this.facilityZip = facilityZip;
    }

    /**
     * @return the facilityAddress
     */
    public String getFacilityAddress() {
        return facilityAddress;
    }

    /**
     * @param facilityAddress the facilityAddress to set
     */
    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    /**
     * @return the facilityTelephone
     */
    public String getFacilityTelephone() {
        return facilityTelephone;
    }

    /**
     * @param facilityTelephone the facilityTelephone to set
     */
    public void setFacilityTelephone(String facilityTelephone) {
        this.facilityTelephone = facilityTelephone;
    }

    /**
     * @return the facilityFacimile
     */
    public String getFacilityFacimile() {
        return facilityFacimile;
    }

    /**
     * @param facilityFacimile the facilityFacimile to set
     */
    public void setFacilityFacimile(String facilityFacimile) {
        this.facilityFacimile = facilityFacimile;
    }

    /**
     * @return the numOfUsers
     */
    public long getNumOfUsers() {
        return numOfUsers;
    }

    /**
     * @param numOfUsers the numOfUsers to set
     */
    public void setNumOfUsers(long numOfUsers) {
        this.numOfUsers = numOfUsers;
    }

    /**
     * @return the numOfPatients
     */
    public long getNumOfPatients() {
        return numOfPatients;
    }

    /**
     * @param numOfPatients the numOfPatients to set
     */
    public void setNumOfPatients(long numOfPatients) {
        this.numOfPatients = numOfPatients;
    }

    /**
     * @return the numOfPatientVisits
     */
    public long getNumOfPatientVisits() {
        return numOfPatientVisits;
    }

    /**
     * @param numOfPatientVisits the numOfPatientVisits to set
     */
    public void setNumOfPatientVisits(long numOfPatientVisits) {
        this.numOfPatientVisits = numOfPatientVisits;
    }

    /**
     * @return the numOfKarte
     */
    public long getNumOfKarte() {
        return numOfKarte;
    }

    /**
     * @param numOfKarte the numOfKarte to set
     */
    public void setNumOfKarte(long numOfKarte) {
        this.numOfKarte = numOfKarte;
    }

    /**
     * @return the numOfImages
     */
    public long getNumOfImages() {
        return numOfImages;
    }

    /**
     * @param numOfImages the numOfImages to set
     */
    public void setNumOfImages(long numOfImages) {
        this.numOfImages = numOfImages;
    }

    /**
     * @return the numOfAttachments
     */
    public long getNumOfAttachments() {
        return numOfAttachments;
    }

    /**
     * @param numOfAttachments the numOfAttachments to set
     */
    public void setNumOfAttachments(long numOfAttachments) {
        this.numOfAttachments = numOfAttachments;
    }

    /**
     * @return the numOfDiagnosis
     */
    public long getNumOfDiagnosis() {
        return numOfDiagnosis;
    }

    /**
     * @param numOfDiagnosis the numOfDiagnosis to set
     */
    public void setNumOfDiagnosis(long numOfDiagnosis) {
        this.numOfDiagnosis = numOfDiagnosis;
    }

    /**
     * @return the numOfLetters
     */
    public long getNumOfLetters() {
        return numOfLetters;
    }

    /**
     * @param numOfLetters the numOfLetters to set
     */
    public void setNumOfLetters(long numOfLetters) {
        this.numOfLetters = numOfLetters;
    }

    /**
     * @return the numOfLabTests
     */
    public long getNumOfLabTests() {
        return numOfLabTests;
    }

    /**
     * @param numOfLabTests the numOfLabTests to set
     */
    public void setNumOfLabTests(long numOfLabTests) {
        this.numOfLabTests = numOfLabTests;
    }

    /**
     * @return the dbSize
     */
    public String getDbSize() {
        return dbSize;
    }

    /**
     * @param dbSize the dbSize to set
     */
    public void setDbSize(String dbSize) {
        this.dbSize = dbSize;
    }

    /**
     * @return the bindAddress
     */
    public String getBindAddress() {
        return bindAddress;
    }

    /**
     * @param bindAddress the bindAddress to set
     */
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    
}
