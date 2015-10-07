package open.dolphin.adm10.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ClaimItem;


/**
 * ClaimBundle 要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class IClaimBundle implements java.io.Serializable {
	
    // 診療行為名
    private String className;
    
    // 診療行為コード
    private String classCode;
    
    // コード体系
    private String classCodeSystem;
    
    // 用法
    private String admin;
    
    // 用法コード
    private String adminCode;
    
    // 用法コード体系
    private String adminCodeSystem;
    
    // 用法メモ
    private String adminMemo;
    
    // バンドル数
    private String bundleNumber;
    
    // バンドル構成品目
    private List<IClaimItem> claimItems;
    
    // メモ
    private String memo;
    
    // 保険種別
    private String insurance;
    
    // = Entity
    private String orderName;
    
    /** Creates new ClaimBundle*/
    public IClaimBundle() {        
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
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

    public List<IClaimItem> getClaimItems() {
        return claimItems;
    }

    public void setClaimItems(List<IClaimItem> claimItems) {
        this.claimItems = claimItems;
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
    
    public void fromModel(ClaimBundle model) {
        
//        if (this.orderName.equals("medOrder")) {
//            BundleMed med  = new BundleMed();
//            med.setOrderName(orderName);
//            ret = (ClaimBundle)med;
//            
//        } else {
//            BundleDolphin bd = new BundleDolphin();
//            bd.setOrderName(orderName);
//            ret = (ClaimBundle)bd;
//        }
//        
//        this.setOrderName(model.get);
        
        this.setClassName(model.getClassName());
        this.setClassCode(model.getClassCode());
        this.setClassCodeSystem(model.getClassCodeSystem());
        this.setAdmin(model.getAdmin());
        this.setAdminCode(model.getAdminCode());
        this.setAdminCodeSystem(model.getAdminCodeSystem());
        this.setAdminMemo(model.getAdminMemo());
        this.setBundleNumber(model.getBundleNumber());
        
        // ArrayToList
        if (model.getClaimItem()!=null && model.getClaimItem().length>0) {
            List<IClaimItem> list = new ArrayList(model.getClaimItem().length);
            for (ClaimItem ci : model.getClaimItem()) {
                IClaimItem conv = new IClaimItem();
                conv.fromModel(ci);
                list.add(conv);
            }
            this.setClaimItems(list);
        }
        
        this.setMemo(model.getMemo());
        this.setInsurance(model.getInsurance());
    }
    
    public ClaimBundle toModel() {
        
        ClaimBundle ret;
        
        //if (this.orderName.equals("medOrder")) {
        if (this.orderName!=null && this.orderName.equals("処 方")) {
            BundleMed med  = new BundleMed();
            med.setOrderName(orderName);
            ret = (ClaimBundle)med;
            
        } else {
            BundleDolphin bd = new BundleDolphin();
            bd.setOrderName(orderName);
            ret = (ClaimBundle)bd;
        }
        
        ret.setClassName(this.getClassName());
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setAdmin(this.getAdmin());
        ret.setAdminCode(this.getAdminCode());
        ret.setAdminCodeSystem(this.getAdminCodeSystem());
        ret.setAdminMemo(this.getAdminMemo());
        ret.setBundleNumber(this.getBundleNumber());
        
        // listToArray
        if (this.getClaimItems()!=null && this.getClaimItems().size()>0) {
            List<ClaimItem> list = new ArrayList(this.getClaimItems().size());
            for (IClaimItem ci : this.getClaimItems()) {
                list.add(ci.toModel());
            }
            ClaimItem[] items = list.toArray(new ClaimItem[list.size()]);
            ret.setClaimItem(items);
        }
        
        ret.setMemo(this.getMemo());
        ret.setInsurance(this.getInsurance());
        
        return ret;
    }
}