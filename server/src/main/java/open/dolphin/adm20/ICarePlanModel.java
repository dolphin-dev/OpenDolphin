package open.dolphin.adm20;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import open.dolphin.infomodel.CarePlanItem;
import open.dolphin.infomodel.CarePlanModel;
import open.dolphin.adm20.converter.IOSHelper;

/**
 *
 * @author kazushi Minagawa
 */
public class ICarePlanModel implements Serializable {
    
    // PK
    private long id;
    
    // 確定日
    private String startDate;
    
    // 開始日
    private String endDate;
    
    // 終了日
    private String created;
    
    // 記録日日
    private String updated;
    
    private long karteId;
    
    private String userId;
    
    private String commonName;
    
    // 実施
    private String status;
    
    // 記録責任者（システムの利用者）
    //private UserModel userModel;
    
    //private IUserModel iuser;
    
    // カルテへの外部参照
    //private KarteBean karteBean;
    
    // 頻度
    private int frequency;
    
    // Entity
    private String entity;

    // 診療行為セットにユーザーがつける名前　(ex. 風邪セット etc.) == stampName
    private String stampName;

//--------------------------------------------------------
// 以下 Bundle 情報
//--------------------------------------------------------

    // 診療行為名
    private String className;

    // 診療行為コード
    private String classCode;

    // 診療行為コード体系
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
    
    // claim item
    private Collection<ICarePlanItem> carePlanItems;

    // iOSへ返却
    public void fromModel(CarePlanModel model) {
        
        this.setId(model.getId());
        this.setStartDate(IOSHelper.toDateStr(model.getStartDate()));
        this.setEndDate(IOSHelper.toDateStr(model.getEndDate()));
        this.setCreated(IOSHelper.toDateStr(model.getCreated()));
        this.setUpdated(IOSHelper.toDateStr(model.getUpdated()));
        this.setStatus(model.getStatus());
        
        this.setFrequency(model.getFrequency());
        this.setEntity(model.getEntity());
        this.setStampName(model.getStampName());
        
        this.setClassName(model.getClassName());
        this.setClassCode(model.getClassCode());
        this.setClassCodeSystem(model.getClassCodeSystem());
        this.setAdministration(model.getAdministration());
        this.setAdminCode(model.getAdminCode());
        this.setAdminCodeSystem(model.getAdminCodeSystem());
        this.setAdminMemo(model.getAdminMemo());
        this.setBundleNumber(model.getBundleNumber());
        this.setMemo(model.getMemo());
        this.setInsurance(model.getInsurance());
        this.setOrderName(model.getOrderName());
        
        // CarePlanItemを変換する
        Set<CarePlanItem> set = model.getCarePlanItems();
        Iterator<CarePlanItem> iter = set.iterator();
        List<ICarePlanItem> list = new ArrayList(set.size());
        while (iter.hasNext()) {
            CarePlanItem item = (CarePlanItem)iter.next();
            ICarePlanItem it = new ICarePlanItem();
            it.fromModel(item);
            list.add(it);
        }
        
        this.setCarePlanItems(list);
    }
    
    // iOSからEntityModelへ
    public CarePlanModel toModel() {
        
        CarePlanModel ret = new CarePlanModel();
        
        ret.setId(this.getId());
        ret.setStartDate(IOSHelper.toDate(this.getStartDate()));
        ret.setEndDate(IOSHelper.toDate(this.getEndDate()));
        ret.setCreated(IOSHelper.toDate(this.getCreated()));
        ret.setUpdated(IOSHelper.toDate(this.getUpdated()));
        ret.setStatus(this.getStatus());
        
        ret.setKarteId(this.getKarteId());
        ret.setUserId(this.getUserId());
        ret.setCommonName(this.getCommonName());
        
        ret.setFrequency(this.getFrequency());
        ret.setEntity(this.getEntity());
        ret.setStampName(this.getStampName());
        
        ret.setClassName(this.getClassName());
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setAdministration(this.getAdministration());
        ret.setAdminCode(this.getAdminCode());
        ret.setAdminCodeSystem(this.getAdminCodeSystem());
        ret.setAdminMemo(this.getAdminMemo());
        ret.setBundleNumber(this.getBundleNumber());
        ret.setMemo(this.getMemo());
        ret.setInsurance(this.getInsurance());
        ret.setOrderName(this.getOrderName());
        
        // ICarePlanItemを変換する
        if (this.getCarePlanItems()!=null) {
            Set<CarePlanItem> set = new HashSet<>();
            for (ICarePlanItem item : this.getCarePlanItems()) {
                CarePlanItem it = item.toModel();
                // ManyToOneの関係を構築する
                it.setCarePlan(ret);
                set.add(it);
            }
            ret.setCarePlanItems(set);
        }
        
        return ret;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
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

    public Collection<ICarePlanItem> getCarePlanItems() {
        return carePlanItems;
    }

    public void setCarePlanItems(Collection<ICarePlanItem> carePlanItems) {
        this.carePlanItems = carePlanItems;
    }
}
