package open.dolphin.infomodel;

/**
 * PatientLiteModel
 *
 * @author Minagawa, kazushi
 */
public class PatientLiteModel extends InfoModel {
    
    private static final long serialVersionUID = 2257606235838636648L;
    
    private String patientId;
    private String name;
    private String gender;
    private String genderDesc;
    private String genderCodeSys;
    private String birthday;
    
    /**
     * 簡易患者情報オブジェクトを生成する。
     */
    public PatientLiteModel() {
    }
    
    /**
     * 患者IDを設定する。
     * @param patientId 患者ID
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    /**
     * 患者IDを返す。
     * @return 患者ID
     */
    public String getPatientId() {
        return patientId;
    }
    
    /**
     * フルネームを設定する。
     * @param name フルネーム
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * フルネームを返す。
     * @return フルネーム
     */
    public String getName() {
        return name;
    }
    
    /**
     * 性別を設定する。
     * @param gender 性別
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * 性別を返す。
     * @return 性別
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * 性別説明を設定する。
     * @param genderDesc 性別説明
     */
    public void setGenderDesc(String genderDesc) {
        this.genderDesc = genderDesc;
    }
    
    /**
     * 性別説明を返す。
     * @return 性別説明
     */
    public String getGenderDesc() {
        return genderDesc;
    }
    
    /**
     * 性別説明体系を設定する。
     * @param genderCodeSys 性別説明体系
     */
    public void setGenderCodeSys(String genderCodeSys) {
        this.genderCodeSys = genderCodeSys;
    }
    
    /**
     * 性別説明体系を返す。
     * @return 性別説明体系
     */
    public String getGenderCodeSys() {
        return genderCodeSys;
    }
    
    /**
     * 生年月日を設定する。
     * @param birthday 生年月日 yyyy-MM-dd
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    /**
     * 生年月日を返す。
     * @return 生年月日 yyyy-MM-dd
     */
    public String getBirthday() {
        return birthday;
    }
}
