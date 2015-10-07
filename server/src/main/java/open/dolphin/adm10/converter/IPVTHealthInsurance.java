package open.dolphin.adm10.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;

/**
 * Health-Insurance class to be parsed.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 * 
 */
public final class IPVTHealthInsurance implements java.io.Serializable {
    
    // ORCA 受付時に送信される UUID
    private String uuid;
    
    // 保険種別
    private String insuranceClass;
    
    // 保険種別コード
    private String insuranceClassCode;
    
    // 保険種別コード体系
    private String insuranceClassCodeSys;
    
    // 保険者番号
    private String insuranceNumber;
    
    // 被保険者記号
    private String clientGroup;
    
    // 被保険者番号
    private String clientNumber;
    
    // 本人家族区分
    private String familyClass;
    
    // 開始日(交付年月日）
    private String startDate;
    
    // 有効期限
    private String expiredDate;
    
    // 継続疾患名
    private List<String> continuedDisease;
    
    // 入院時負担率
    private String payInRatio;
    
    // 外来時負担率
    private String payOutRatio;

    // 公費負担リスト
    private List<IPVTPublicInsuranceItem> publicItems;
    
    /** Creates new PVTHealthInsurance */
    public IPVTHealthInsurance() {
        super();
    }

    public String getGUID() {
        return uuid;
    }

    public void setGUID(String uuid) {
        this.uuid = uuid;
    }

    public String getInsuranceClass() {
        return insuranceClass;
    }

    public void setInsuranceClass(String insuranceClass) {
        this.insuranceClass = insuranceClass;
    }

    public String getInsuranceClassCode() {
        return insuranceClassCode;
    }

    public void setInsuranceClassCode(String insuranceClassCode) {
        this.insuranceClassCode = insuranceClassCode;
    }

    public String getInsuranceClassCodeSys() {
        return insuranceClassCodeSys;
    }

    public void setInsuranceClassCodeSys(String insuranceClassCodeSys) {
        this.insuranceClassCodeSys = insuranceClassCodeSys;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getFamilyClass() {
        return familyClass;
    }

    public void setFamilyClass(String familyClass) {
        this.familyClass = familyClass;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public List<String> getContinuedDisease() {
        return continuedDisease;
    }

    public void setContinuedDisease(List<String> continuedDisease) {
        this.continuedDisease = continuedDisease;
    }

    public String getPayInRatio() {
        return payInRatio;
    }

    public void setPayInRatio(String payInRatio) {
        this.payInRatio = payInRatio;
    }

    public String getPayOutRatio() {
        return payOutRatio;
    }

    public void setPayOutRatio(String payOutRatio) {
        this.payOutRatio = payOutRatio;
    }

    public List<IPVTPublicInsuranceItem> getPublicItems() {
        return publicItems;
    }

    public void setPublicItems(List<IPVTPublicInsuranceItem> publicItems) {
        this.publicItems = publicItems;
    }
        
    public void fromModel(PVTHealthInsuranceModel model) {
        this.setGUID(model.getGUID());
        this.setInsuranceClass(model.getInsuranceClass());
        this.setInsuranceClassCode(model.getInsuranceClassCode());
        this.setInsuranceClassCodeSys(model.getInsuranceClassCodeSys());
        this.setInsuranceNumber(model.getInsuranceNumber());
        this.setClientGroup(model.getClientGroup());
        this.setClientNumber(model.getClientNumber());
        this.setFamilyClass(model.getFamilyClass());
        this.setStartDate(model.getStartDate());
        this.setExpiredDate(model.getExpiredDate());
        this.setPayInRatio(model.getPayInRatio());
        this.setPayOutRatio(model.getPayOutRatio());
        
        // 配列をListへ変換
        if (model.getContinuedDisease()!=null && this.getContinuedDisease().size()>0) {
            String[] arr = model.getContinuedDisease();
            this.setContinuedDisease(Arrays.asList(arr));
        }
        
        // 配列をListへ変換
        if (model.getPublicItems()!=null && model.getPublicItems().size()>0) {
            List<IPVTPublicInsuranceItem> list = new ArrayList();
            for (PVTPublicInsuranceItemModel item : model.getPublicItems()) {
                IPVTPublicInsuranceItem p = new IPVTPublicInsuranceItem();
                p.fromModel(item);
                list.add(p);
            }
            this.setPublicItems(list);
        }
    }
    
    public PVTHealthInsuranceModel toModel() {
        PVTHealthInsuranceModel ret = new PVTHealthInsuranceModel();
        ret.setGUID(this.getGUID());
        ret.setInsuranceClass(this.getInsuranceClass());
        ret.setInsuranceClassCode(this.getInsuranceClassCode());
        ret.setInsuranceClassCodeSys(this.getInsuranceClassCodeSys());
        ret.setInsuranceNumber(this.getInsuranceNumber());
        ret.setClientGroup(this.getClientGroup());
        ret.setClientNumber(this.getClientNumber());
        ret.setFamilyClass(this.getFamilyClass());
        ret.setStartDate(this.getStartDate());
        ret.setExpiredDate(this.getExpiredDate());
        ret.setPayInRatio(this.getPayInRatio());
        ret.setPayOutRatio(this.getPayOutRatio());
        
        // 配列へ変換
        if (this.getContinuedDisease()!=null && this.getContinuedDisease().size()>0) {
            String[] arr = this.getContinuedDisease().toArray(new String[this.getContinuedDisease().size()]);
            ret.setContinuedDisease(arr);
        }
        
        // 配列へ変換
        if (this.getPublicItems()!=null && this.getPublicItems().size()>0) {
            List<PVTPublicInsuranceItemModel> list = new ArrayList<PVTPublicInsuranceItemModel>();
            for (IPVTPublicInsuranceItem item : this.getPublicItems()) {
                PVTPublicInsuranceItemModel model = item.toModel();
                list.add(model);
            }
            PVTPublicInsuranceItemModel[] arr = list.toArray(new PVTPublicInsuranceItemModel[list.size()]);
            ret.setPVTPublicInsuranceItem(arr);
        }
        return ret;
    }
}
