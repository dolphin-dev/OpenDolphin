package open.dolphin.adm10.converter;

import javax.persistence.Embeddable;
import open.dolphin.infomodel.ExtRefModel;

/**
 * 外部参照要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
@Embeddable
public class IExtRefModel implements java.io.Serializable {
    
    // コンテントタイプ
    private String contentType;
    
    // 外部参照の医学的役割
    private String medicalRole;
    
    // タイトル
    private String title;
    
    // href
    private String href;

    //-----------------------------------
    private String bucket;
    private String sop;
    private String url;
    private String facilityId;
    //-----------------------------------
    
    //-----------------------------------
    private String imageTime;
    private String bodyPart;
    private String shutterNum;
    private String seqNum;
    private String extension;
    //-----------------------------------
    
    /** デフォルトコンストラクタ */
    public IExtRefModel() {
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMedicalRole() {
        return medicalRole;
    }

    public void setMedicalRole(String medicalRole) {
        this.medicalRole = medicalRole;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getSop() {
        return sop;
    }

    public void setSop(String sop) {
        this.sop = sop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getImageTime() {
        return imageTime;
    }

    public void setImageTime(String imageTime) {
        this.imageTime = imageTime;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getShutterNum() {
        return shutterNum;
    }

    public void setShutterNum(String shutterNum) {
        this.shutterNum = shutterNum;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public void fromModel(ExtRefModel model) {
        this.setContentType(model.getContentType());
        this.setMedicalRole(model.getMedicalRole());
        this.setTitle(model.getTitle());
        this.setHref(model.getHref());
        this.setSop(model.getSop());
    }
    
    public ExtRefModel toModel() {
        ExtRefModel ret = new ExtRefModel();
        ret.setContentType(this.getContentType());
        ret.setMedicalRole(this.getMedicalRole());
        ret.setTitle(this.getTitle());
        ret.setHref(this.getHref());
        ret.setSop(this.getSop());
        return ret;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("contentType=").append(this.getContentType()).append("\n");
        sb.append("medicalRole=").append(this.getMedicalRole()).append("\n");
        sb.append("title=").append(this.getTitle()).append("\n");
        sb.append("href=").append(this.getHref()).append("\n");
        return sb.toString();
    }
}