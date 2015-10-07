package open.dolphin.adm10.converter;

import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ISchemaModel implements java.io.Serializable {

    private long id;
    
    // Date
    private String confirmed;
    
    // Date
    private String started;
    
    // Date
    private String ended;
    
    // Date
    private String recorded;
    
    private long linkId;
    
    private String linkRelation;
    
    private String status;
    
    private UserModel userModel;
    
    private KarteBean karteBean;
    
    private IExtRefModel extRef;
    
    private byte[] jpegByte;
    
    
    public ISchemaModel() {
        extRef = new IExtRefModel();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public String getLinkRelation() {
        return linkRelation;
    }

    public void setLinkRelation(String linkRelation) {
        this.linkRelation = linkRelation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public KarteBean getKarteBean() {
        return karteBean;
    }

    public void setKarteBean(KarteBean karteBean) {
        this.karteBean = karteBean;
    }

    public IExtRefModel getExtRefModel() {
        return extRef;
    }

    public void setExtRefModel(IExtRefModel extRef) {
        this.extRef = extRef;
    }
    
    public byte[] getJpegByte() {
        return jpegByte;
    }

    public void setJpegByte(byte[] jpegByte) {
        this.jpegByte = jpegByte;
    }
    
    public void fromModel(SchemaModel model) {
        
        this.setId(model.getId());
        
        // Date
        this.setConfirmed(IOSHelper.toDateStr(model.getConfirmed()));
        
        // Date
        this.setStarted(IOSHelper.toDateStr(model.getStarted()));
        
        // Date
        this.setEnded(IOSHelper.toDateStr(model.getEnded()));
        
        // Date
        this.setRecorded(IOSHelper.toDateStr(model.getRecorded()));
        
        this.setLinkId(model.getLinkId());
        this.setLinkRelation(model.getLinkRelation());
        this.setStatus(model.getStatus());
        //this.setUserModel(model.getUserModel());
        //this.setKarteBean(model.getKarteBean());
        
        IExtRefModel ext = new IExtRefModel();
        extRef.fromModel(model.getExtRefModel());
        this.setExtRefModel(extRef);
        
        // base64
        this.setJpegByte(model.getJpegByte());
    }
    
    public SchemaModel toModel() {
        
        SchemaModel ret = new SchemaModel();
        
        ret.setId(this.getId());
        
        // Date
        ret.setConfirmed(IOSHelper.toDate(this.getConfirmed()));
        
        // Date
        ret.setStarted(IOSHelper.toDate(this.getStarted()));
        
        // Date
        ret.setEnded(IOSHelper.toDate(this.getEnded()));
        
        // Date
        ret.setRecorded(IOSHelper.toDate(this.getRecorded()));
        
        ret.setLinkId(this.getLinkId());
        ret.setLinkRelation(this.getLinkRelation());
        ret.setStatus(this.getStatus());
        ret.setUserModel(this.getUserModel());
        ret.setKarteBean(this.getKarteBean());
        
        ret.setExtRefModel(this.getExtRefModel().toModel());
        
        // base64
        ret.setJpegByte(this.getJpegByte());
        
        return ret;
    }
}
