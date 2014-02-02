package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author kazushi Minagawa @digital-globe.co.jp
 */
@Entity
@Table(name = "d_nlabo_module")
public class NLaboModule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 患者ID fid:Pid
    @Column(nullable = false)
    private String patientId;

    // ラボコード
    private String laboCenterCode;

    // 患者氏名
    private String patientName;

    // 患者性別
    private String patientSex;

    // 検体採取日または検査受付日時
    private String sampleDate;

    // この検査モジュールに含まれている検査項目の数
    private String numOfItems;

    @OneToMany(mappedBy = "laboModule", cascade={CascadeType.ALL})
    private Collection<NLaboItem> items;

    @Transient
    private Boolean progressState;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NLaboModule)) {
            return false;
        }
        NLaboModule other = (NLaboModule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "oms.ehr.entity.LaboModule[id=" + id + "]";
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the patientSex
     */
    public String getPatientSex() {
        return patientSex;
    }

    /**
     * @param patientSex the patientSex to set
     */
    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    /**
     * @return the sampleDate
     */
    public String getSampleDate() {
        return sampleDate;
    }

    /**
     * @param sampleDate the sampleDate to set
     */
    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    /**
     * @return the numOfItems
     */
    public String getNumOfItems() {
        return numOfItems;
    }

    /**
     * @param numOfItems the numOfItems to set
     */
    public void setNumOfItems(String numOfItems) {
        this.numOfItems = numOfItems;
    }

    /**
     * @return the items
     */
    public Collection<NLaboItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(Collection<NLaboItem> items) {
        this.items = items;
    }

    public void addItem(NLaboItem item) {

        if (this.items == null) {
            this.items = new ArrayList<NLaboItem>();
        }

        this.items.add(item);
    }

    /**
     * @return the progressState
     */
    public Boolean getProgressState() {
        return progressState;
    }

    /**
     * @param progressState the progressState to set
     */
    public void setProgressState(Boolean progressState) {
        this.progressState = progressState;
    }

    /**
     * @return the laboCenterCode
     */
    public String getLaboCenterCode() {
        return laboCenterCode;
    }

    /**
     * @param laboCenterCode the laboCenterCode to set
     */
    public void setLaboCenterCode(String laboCenterCode) {
        this.laboCenterCode = laboCenterCode;
    }


    /**
     * 引数で指定された検査コードを持つ NLaboItemを返す。
     * @param testCode 検査コード
     * @return 該当するNLaboItem
     */
    public NLaboItem getTestItem(String testCode) {

        if (items == null || items.size() == 0 ) {
            return null;
        }

        NLaboItem ret = null;

        for (NLaboItem item : items) {
            if (item.getItemCode().equals(testCode)) {
                ret = item;
                break;
            }
        }

        return ret;
    }
}
