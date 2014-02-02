package open.dolphin.infomodel;

import java.util.List;

/**
 * Health-Insurance class to be parsed.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-I corp for adding 'PvtPublicInsuranceItem' and related function to store/get PvtPublicInsuranceItem name
 */
public class PVTHealthInsuranceModel extends InfoModel  {
    
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
    private String[] continuedDisease;
    
    // 入院時負担率
    private String payInRatio;
    
    // 外来時負担率
    private String payOutRatio;
    
    // 公費負担リスト
    private PVTPublicInsuranceItemModel[] pvtPublicInsuranceItem;

    // 公費負担リスト
    private List<PVTPublicInsuranceItemModel> publicItems;
    
    /** Creates new PVTHealthInsurance */
    public PVTHealthInsuranceModel() {
        super();
    }
    
    public String getGUID() {
        return uuid;
    }
    
    public void setGUID(String val) {
        uuid = val;
    }
    
    public String getInsuranceClass() {
        return insuranceClass;
    }
    
    public void setInsuranceClass(String val) {
        insuranceClass = val;
    }
    
    public String getInsuranceClassCode() {
        return insuranceClassCode;
    }
    
    public void setInsuranceClassCode(String val) {
        insuranceClassCode = val;
    }
    
    public String getInsuranceCodeAndClass() {
        StringBuilder sb = new StringBuilder();
        if (insuranceClassCode!=null) {
            sb.append(insuranceClassCode).append(" ");
        }
        if (insuranceClass!=null) {
            sb.append(insuranceClass);
        } else {
            sb.append("記載無");
        }
        return sb.toString();
    }
    
    public String getInsuranceClassCodeSys() {
        return insuranceClassCodeSys;
    }
    
    public void setInsuranceClassCodeSys(String val) {
        insuranceClassCodeSys = val;
    }
    
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    
    public void setInsuranceNumber(String val) {
        insuranceNumber = val;
    }
    
    public String getClientGroup() {
        return clientGroup;
    }
    
    public void setClientGroup(String val) {
        clientGroup = val;
    }
    
    public String getClientNumber() {
        return clientNumber;
    }
    
    public void setClientNumber(String val) {
        clientNumber = val;
    }
    
    public String getFamilyClass() {
        return familyClass;
    }
    
    public void setFamilyClass(String val) {
        familyClass = val;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    }
    
    public String getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(String val) {
        expiredDate = val;
    }
    
    public String[] getContinuedDisease() {
        return continuedDisease;
    }
    
    public void setContinuedDisease(String[] val) {
        continuedDisease = val;
    }
    
    public void addContinuedDisease(String val) {
        
        int len = 0;
        
        if (continuedDisease == null) {
            continuedDisease = new String[1];
        } else {
            len = continuedDisease.length;
            String[] dest = new String[len + 1];
            System.arraycopy(continuedDisease, 0, dest, 0, len);
            continuedDisease = dest;
        }
        continuedDisease[len] = val;
    }
    
    public String getPayInRatio() {
        return payInRatio;
    }
    
    public void setPayInRatio(String val) {
        payInRatio = val;
    }
    
    public String getPayOutRatio() {
        return payOutRatio;
    }
    
    public void setPayOutRatio(String val) {
        payOutRatio = val;
    }
    
    public PVTPublicInsuranceItemModel[] getPVTPublicInsuranceItem() {
        return pvtPublicInsuranceItem;
    }
    
    public void setPVTPublicInsuranceItem(PVTPublicInsuranceItemModel[] val) {
        pvtPublicInsuranceItem = val;
    }
    
    public void addPvtPublicInsuranceItem(PVTPublicInsuranceItemModel value) {
        if (pvtPublicInsuranceItem == null) {
            pvtPublicInsuranceItem = new PVTPublicInsuranceItemModel[1];
            pvtPublicInsuranceItem[0] = value;
            return;
        }
        int len = pvtPublicInsuranceItem.length;
        PVTPublicInsuranceItemModel[] dest = new PVTPublicInsuranceItemModel[len + 1];
        System.arraycopy(pvtPublicInsuranceItem, 0, dest, 0, len);
        pvtPublicInsuranceItem = dest;
        pvtPublicInsuranceItem[len] = value;
    }

    //----------------------------------------------------------
    public List<PVTPublicInsuranceItemModel> getPublicItems() {
        return publicItems;
    }

    public void setPublicItems(List<PVTPublicInsuranceItemModel> publicItems) {
        this.publicItems = publicItems;
    }
    //----------------------------------------------------------
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();  
        if (insuranceClassCode!=null) {
            sb.append(insuranceClassCode).append(" ");
        }
        if (insuranceClass!=null) {
            sb.append(insuranceClass);
        } else {
            sb.append("記載無");
        }
        if (pvtPublicInsuranceItem != null) {
            for (PVTPublicInsuranceItemModel pm : pvtPublicInsuranceItem) {
                sb.append(":");
                sb.append(pm.toString());
            }
        }
        return sb.toString();
               
//        StringBuilder buf = new StringBuilder();
//        
//        if (insuranceNumber != null && insuranceClass != null ) {
//            buf.append(insuranceNumber);
//            buf.append("  ");
//            buf.append(insuranceClass);
//        }
//        
//        else if (insuranceNumber != null) {
//            buf.append(insuranceNumber);
//        }
//        
//        else if (insuranceClass != null) {
//            buf.append(insuranceClass);
//        }
//        
//        else {
//            buf.append("自費");
//        }
//        
//        if (pvtPublicInsuranceItem != null) {
//            int len = pvtPublicInsuranceItem.length;
//            buf.append(" ");
//            for (int i = 0; i < len; i++) {
//                PVTPublicInsuranceItemModel item = pvtPublicInsuranceItem[i];
//                if (item != null) {
//                    if (i != 0) {
//                        buf.append("・");
//                    }
//                    buf.append(item.toString());
//                }
//            }
//            buf.append(" ");
//        }
//        
//        return buf.toString();
    }
    
    public String toClaim() {
        
        // 必須要素のみ出力        
//        <mmlHi:HealthInsuranceModule mmlHi:countryType="JPN">
//		<mmlHi:insuranceClass mmlHi:ClassCode="00" mmlHi:tableId="MML0031">国保</mmlHi:insuranceClass>
//		<mmlHi:insuranceNumber>138156</mmlHi:insuranceNumber>
//		<mmlHi:clientId>
//			<mmlHi:group>１１１</mmlHi:group>
//			<mmlHi:number>１１１</mmlHi:number>
//		</mmlHi:clientId>
//		<mmlHi:familyClass>true</mmlHi:familyClass>
//		<mmlHi:startDate>2010-04-01</mmlHi:startDate>
//		<mmlHi:expiredDate>9999-12-31</mmlHi:expiredDate>
//		<mmlHi:paymentOutRatio>0.30</mmlHi:paymentOutRatio>
//	</mmlHi:HealthInsuranceModule>
        
        StringBuilder sb = new StringBuilder();
        sb.append("<mmlHi:HealthInsuranceModule mmlHi:countryType=\"JPN\">");

        if (getInsuranceClass()!=null && getInsuranceClassCode()!=null) {
            sb.append("<mmlHi:insuranceClass mmlHi:ClassCode=");
            sb.append(addQuote(getInsuranceClassCode()));
            sb.append(" mmlHi:tableId=\"MML0031\">");
            sb.append(getInsuranceClass());
            sb.append("</mmlHi:insuranceClass>");
        }
        
        sb.append("<mmlHi:insuranceNumber>");
        sb.append(getInsuranceNumber());
        sb.append("</mmlHi:insuranceNumber>");
        
        sb.append("<mmlHi:clientId>");
        sb.append("<mmlHi:group>");
        sb.append(getClientGroup());
        sb.append("</mmlHi:group>");
        sb.append("<mmlHi:number>");
        sb.append(getClientNumber());
        sb.append("</mmlHi:number>");
        sb.append("</mmlHi:clientId>");
        
        sb.append("<mmlHi:familyClass>");
        sb.append(getFamilyClass());
        sb.append("</mmlHi:familyClass>");
        
        sb.append("<mmlHi:startDate>");
        sb.append(getStartDate());
        sb.append("</mmlHi:startDate>");
        
        sb.append("<mmlHi:expiredDate>");
        sb.append(getExpiredDate());
        sb.append("</mmlHi:expiredDate>");

        if (getPayInRatio()!=null) {
            sb.append("<mmlHi:paymentInRatio>");
            sb.append(getPayInRatio());
            sb.append("</mmlHi:paymentInRatio>");
        }

        if (getPayOutRatio()!=null) {
            sb.append("<mmlHi:paymentOutRatio>");
            sb.append(getPayOutRatio());
            sb.append("</mmlHi:paymentOutRatio>");
        }

        if (pvtPublicInsuranceItem!=null && pvtPublicInsuranceItem.length>0) {

            sb.append("<mmlHi:publicInsurance>");

            for (PVTPublicInsuranceItemModel pi : pvtPublicInsuranceItem) {
                sb.append(pi.toClaim());
            }

            sb.append("</mmlHi:publicInsurance>");
        }

        sb.append("</mmlHi:HealthInsuranceModule>");
        
        return sb.toString();
    }
    
    private String addQuote(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(str);
        sb.append("\"");
        return sb.toString();
    }
}
