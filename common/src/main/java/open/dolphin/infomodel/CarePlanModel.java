package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author kazushi Minagawa
 */
@Entity
@Table(name = "d_care_plan")
public class CarePlanModel extends InfoModel implements Serializable {
    
    // PK
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;
    
    private long karteId;
    
    private String userId;
    
    private String commonName;
    
    private String status;
    
    // 頻度
    private Integer frequency;
    
    // Entity
    private String entity;

    // 診療行為セットにユーザーがつける名前　(ex. 風邪セット etc.) == stampName
    private String stampName;

    //--------------------------------------------------------
    // 以下 Bundle 情報
    //--------------------------------------------------------

    // 診療行為名 CLAIM規格
    private String className;

    // 診療行為コード CLAIM規格
    private String classCode;

    // 診療行為コード体系 CLAIM規格
    private String classCodeSystem;

    // 用法
    private String administration;

    // 用法コード
    private String adminCode;

    // 用法コード体系
    private String adminCodeSystem;

    // 用法メモ
    private String adminMemo;

    // バンドル数
    private String bundleNumber;

    // メモ
    private String memo;

    // 保険種別
    private String insurance;

    // order name（日本語）
    private String orderName;
    
    @OneToMany(mappedBy="carePlan", cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
    private Set<CarePlanItem> carePlanItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public long getKarteId() {
        return karteId;
    }

    public void setKarteId(long karteId) {
        this.karteId = karteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getStampName() {
        return stampName;
    }

    public void setStampName(String stampName) {
        this.stampName = stampName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCodeSystem() {
        return classCodeSystem;
    }

    public void setClassCodeSystem(String classCodeSystem) {
        this.classCodeSystem = classCodeSystem;
    }

    public String getAdministration() {
        return administration;
    }

    public void setAdministration(String administration) {
        this.administration = administration;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getAdminCodeSystem() {
        return adminCodeSystem;
    }

    public void setAdminCodeSystem(String adminCodeSystem) {
        this.adminCodeSystem = adminCodeSystem;
    }

    public String getAdminMemo() {
        return adminMemo;
    }

    public void setAdminMemo(String adminMemo) {
        this.adminMemo = adminMemo;
    }

    public String getBundleNumber() {
        return bundleNumber;
    }

    public void setBundleNumber(String bundleNumber) {
        this.bundleNumber = bundleNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Set<CarePlanItem> getCarePlanItems() {
        return carePlanItems;
    }

    public void setCarePlanItems(Set<CarePlanItem> carePlanItems) {
        this.carePlanItems = carePlanItems;
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
        final CarePlanModel other = (CarePlanModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    public ModuleModel toModleModel() {
        
        ModuleModel result = new ModuleModel();
        ModuleInfoBean info = result.getModuleInfoBean();
        
        info.setEntity(this.getEntity());
        info.setStampName(this.getStampName());
        info.setStampRole(IInfoModel.ROLE_P);
        
        ClaimBundle bundle = (this.getEntity().equals(IInfoModel.ENTITY_MED_ORDER))
                ? new BundleMed()
                : new BundleDolphin();
        result.setModel(bundle);
        
        bundle.setAdmin(this.getAdministration());
        bundle.setAdminCode(this.getAdminCode());
        bundle.setAdminCodeSystem(this.getAdminCodeSystem());
        bundle.setAdminMemo(this.getMemo());
        bundle.setBundleNumber(this.getBundleNumber());
        bundle.setClassCode(this.getClassCode());
        bundle.setClassCodeSystem(this.getClassCodeSystem());
        bundle.setClassName(this.getClassName());
        bundle.setInsurance(this.getInsurance());
        bundle.setMemo(this.getMemo());
        
        Iterator<CarePlanItem> iter = this.getCarePlanItems().iterator();
        List<ClaimItem> list = new ArrayList();
        while (iter.hasNext()) {
            CarePlanItem item = iter.next();
            list.add(item.toClaimItem());
        }
        
        ClaimItem[] items = list.toArray(new ClaimItem[0]);
        bundle.setClaimItem(items);
        
        return result;
    }
}
