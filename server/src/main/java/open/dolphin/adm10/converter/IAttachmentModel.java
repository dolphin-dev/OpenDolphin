/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm10.converter;

import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.UserModel;

/**
 * iPadのFreeText対応
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class IAttachmentModel {

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
    
    private String fileName;
    private String contentType;
    private long contentSize;
    private long lastModified;
    private String digest;
    private String title;
    private String uri;
    private String extension;
    private String memo; 
    
    private byte[] bytes;
    
    public IAttachmentModel() {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public void fromModel(AttachmentModel model) {
        
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
        
        this.setFileName(model.getFileName());
        this.setContentType(model.getContentType());
        this.setContentSize(model.getContentSize());
        this.setLastModified(model.getLastModified());
        this.setDigest(model.getDigest());
        this.setTitle(model.getTitle());
        this.setUri(model.getUri());
        this.setExtension(model.getExtension());
        this.setMemo(model.getMemo());
        
        // base64
        this.setBytes(model.getBytes());
    }
    
    public AttachmentModel toModel() {
        
        AttachmentModel ret = new AttachmentModel();
        
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
        
        ret.setFileName(this.getFileName());
        ret.setContentType(this.getContentType());
        ret.setContentSize(this.getContentSize());
        ret.setLastModified(this.getLastModified());
        ret.setDigest(this.getDigest());
        ret.setTitle(this.getTitle());
        ret.setUri(this.getUri());
        ret.setExtension(this.getExtension());
        ret.setMemo(this.getMemo());
        
        // base64
        ret.setBytes(this.getBytes());
        
        return ret;
    }
}
