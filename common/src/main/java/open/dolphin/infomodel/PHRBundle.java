package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.List;


/**
 * ClaimBundle 要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PHRBundle extends PHRModel implements java.io.Serializable {
    
    // Document
    private String catchId;
    
    // Module Entry情報
    private String bundleId;
    private String started;
    private String confirmed;
    private String status;
    
    // ModuleInfo
    private String ent;
    private String role;
    private int numberAsStamp;
	
    // 診療行為名
    private String clsName;
    
    // 診療行為コード
    private String clsCode;
    
    // コード体系
    private String clsCodeSystem;
    
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
    private List<PHRClaimItem> claimItems;
    
    // メモ
    private String memo;
    
    // 保険種別
    private String insurance;
    
    // = Entity
    private String orderName;
    
    /** Creates new ClaimBundle*/
    public PHRBundle() {
        claimItems = new ArrayList();
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

    public List<PHRClaimItem> getClaimItems() {
        return claimItems;
    }

    public void setClaimItems(List<PHRClaimItem> claimItems) {
        this.claimItems = claimItems;
    }
    
    public void addPHRClaimItem(PHRClaimItem phrClaimItem) {
        this.claimItems.add(phrClaimItem);
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

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getClsCode() {
        return clsCode;
    }

    public void setClsCode(String clsCode) {
        this.clsCode = clsCode;
    }

    public String getClsCodeSystem() {
        return clsCodeSystem;
    }

    public void setClsCodeSystem(String clsCodeSystem) {
        this.clsCodeSystem = clsCodeSystem;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String moduleId) {
        this.bundleId = moduleId;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnt() {
        return ent;
    }

    public void setEnt(String ent) {
        this.ent = ent;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNumber() {
        return numberAsStamp;
    }

    public void setNumber(int numberAsStamp) {
        this.numberAsStamp = numberAsStamp;
    }

    public String getCatchId() {
        return catchId;
    }

    public void setCatchId(String docId) {
        this.catchId = docId;
    }
}