package open.dolphin.infomodel;

/**
 * OtherIdModel
 *
 * @author Minagawa,Kazushi
 *
 */
public class OtherIdModel extends InfoModel {
    
    private long id;
    
    private String otherId;
    
    private String idType;
    
    private String idTypeDesc;
    
    private String idTypeCodeSys;
    
    private PatientModel patient;
    
    /**
     * Idを返す。
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Idを設定する。
     * @param id Id
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @param id
     *            The id to set.
     */
    public void setOtherId(String id) {
        this.otherId = id;
    }
    
    /**
     * @return Returns the id.
     */
    public String getOtherId() {
        return otherId;
    }
    
    /**
     * @param otherIdType
     *            The otherIdType to set.
     */
    public void setIdType(String otherIdType) {
        this.idType = otherIdType;
    }
    
    /**
     * @return Returns the otherIdType.
     */
    public String getIdType() {
        return idType;
    }
    
    /**
     * @param otherIdTypeDesc
     *            The otherIdTypeDesc to set.
     */
    public void setIdTypeDesc(String otherIdTypeDesc) {
        this.idTypeDesc = otherIdTypeDesc;
    }
    
    /**
     * @return Returns the otherIdTypeDesc.
     */
    public String getIdTypeDesc() {
        return idTypeDesc;
    }
    
    /**
     * @param otherIdCodeSys
     *            The otherIdCodeSys to set.
     */
    public void setIdTypeCodeSys(String otherIdCodeSys) {
        this.idTypeCodeSys = otherIdCodeSys;
    }
    
    /**
     * @return Returns the otherIdCodeSys.
     */
    public String getIdTypeCodeSys() {
        return idTypeCodeSys;
    }
    
    /**
     * 患者を返す。
     * @return 患者
     */
    public PatientModel getPatient() {
        return patient;
    }
    
    /**
     * 患者を設定する。
     * @param patient 患者
     */
    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OtherIdModel other = (OtherIdModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
