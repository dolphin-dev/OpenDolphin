package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * HealthInsuranceModel
 *
 * @author Minagawa,kazushi
 *
 */
@Entity
@Table(name = "d_health_insurance")
public class HealthInsuranceModel extends InfoModel  {
    
    private static final long serialVersionUID = 3064687655700962022L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Lob
    @Column(nullable=false)
    private byte[] beanBytes;
    
    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
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
     * BeanXml を背低する。
     * @param BeanXmlバイト配列
     */
    public void setBeanBytes(byte[] beanBytes) {
        this.beanBytes = beanBytes;
    }
    
    /**
     * BeanXml を返す。
     * @return BeanXmlバイト配列
     */
    public byte[] getBeanBytes() {
        return beanBytes;
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
        final HealthInsuranceModel other = (HealthInsuranceModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
