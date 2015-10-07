package open.dolphin.adm10.converter;

import open.dolphin.infomodel.*;

/**
 * FacilityModel
 *
 * @author Minagawa,Kazushi
 *
 */
public class IFacilityModel extends InfoModel implements java.io.Serializable {
    
    private long id;
    
    /** Business Key */
    private String facilityId;
    
    // 医療機関名
    private String facilityName;
    
    // 郵便番号
    private String zipCode;
    
    // 住所
    private String address;
    
    // 電話番号
    private String telephone;

    // FAX
    private String facsimile;
    
    // URL
    private String url;
    
    // システム登録日
    private String registeredDate;
    
    // メンバータイプ
    private String memberType;

    // S3 アカウント
    private String s3URL;
    private String s3AccessKey;
    private String s3SecretKey;
    
//    // 保健医療機関コード 7桁
//    private String insuraceFacilityId;
//    
//    // JMARIコード 12桁
//    private String jmariCode;

    /**
     * FacilityModelオブジェクトをせいせいする。
     */
    public IFacilityModel() {
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String name) {
        this.facilityName = name;
    }

    public String getZipCode() {
        return zipCode;
    }
   
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }
   
    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFacsimile() {
        return facsimile;
    }

    public void setFacsimile(String facsimile) {
        this.facsimile = facsimile;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    public String getMemberType() {
        return memberType;
    }
        
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getS3URL() {
        return s3URL;
    }

    public void setS3URL(String s3URL) {
        this.s3URL = s3URL;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
    }

//    public String getInsuraceFacilityId() {
//        return insuraceFacilityId;
//    }
//
//    public void setInsuraceFacilityId(String insuraceFacilityId) {
//        this.insuraceFacilityId = insuraceFacilityId;
//    }
//
//    public String getJmariCode() {
//        return jmariCode;
//    }
//
//    public void setJmariCode(String jmariCode) {
//        this.jmariCode = jmariCode;
//    }
    
    public void fromModel(FacilityModel model) {
        this.setAddress(model.getAddress());
        this.setFacilityId(model.getFacilityId());
        this.setFacilityName(model.getFacilityName());
        this.setFacsimile(model.getFacsimile());
        this.setId(model.getId());
        this.setMemberType(model.getMemberType());
        this.setRegisteredDate(IOSHelper.toDateStr(model.getRegisteredDate()));
        this.setS3AccessKey(model.getS3AccessKey());
        this.setS3SecretKey(model.getS3SecretKey());
        this.setS3URL(model.getS3URL());
        this.setTelephone(model.getTelephone());
        this.setZipCode(model.getZipCode());
    }

    public FacilityModel toModel() {
        FacilityModel ret = new FacilityModel();
        ret.setAddress(this.getAddress());
        ret.setFacilityId(this.getFacilityId());
        ret.setFacilityName(this.getFacilityName());
        ret.setFacsimile(this.getFacsimile());
        ret.setId(this.getId());
        ret.setMemberType(this.getMemberType());
        ret.setRegisteredDate(IOSHelper.toDate(this.getRegisteredDate()));
        ret.setS3AccessKey(this.getS3AccessKey());
        ret.setS3SecretKey(this.getS3SecretKey());
        ret.setS3URL(this.getS3URL());
        ret.setTelephone(this.getTelephone());
        ret.setZipCode(this.getZipCode());
        return ret;
    }
}