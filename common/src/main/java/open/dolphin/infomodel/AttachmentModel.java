package open.dolphin.infomodel;

import java.io.Serializable;
import javax.persistence.*;
import javax.swing.ImageIcon;

/**
 * カルテのアタッチメント（文書や画像）クラス。
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
@Entity
@Table(name = "d_attachment")
public class AttachmentModel extends KarteEntryBean implements Serializable,java.lang.Cloneable {
    
    private String fileName;
    private String contentType;
    private long contentSize;
    private long lastModified;
    private String digest;
    
    private String title;
    private String uri;
    private String extension;
    private String memo; 
    
    @Lob
    @Column(nullable=false)
    private byte[] bytes;   // data
    
    @ManyToOne
    @JoinColumn(name="doc_id", nullable=false)
    private DocumentModel document;
    
    @Transient
    private ImageIcon icon; // icon
    
    @Transient
    private int attachmentNumber;
    
    @Transient
    private String location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public DocumentModel getDocumentModel() {
        return document;
    }

    public void setDocumentModel(DocumentModel document) {
        this.document = document;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public int getAttachmentNumber() {
        return attachmentNumber;
    }

    public void setAttachmentNumber(int attachmentNumber) {
        this.attachmentNumber = attachmentNumber;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        AttachmentModel ret = new AttachmentModel();
        ret.setConfirmed(this.getConfirmed());
        ret.setEnded(this.getEnded());
        ret.setFirstConfirmed(this.getConfirmed());
        ret.setLinkId(this.getLinkId());
        ret.setLinkRelation(this.getLinkRelation());
        ret.setRecorded(this.getRecorded());
        ret.setStarted(this.getStarted());
        ret.setStatus(this.getStatus());
        ret.setFileName(this.getFileName());
        ret.setContentType(this.getContentType());
        ret.setContentSize(this.getContentSize());
        ret.setLastModified(this.getLastModified());
        ret.setDigest(this.getDigest());
        ret.setTitle(this.getTitle());
        ret.setUri(this.getUri());
        ret.setExtension(this.getExtension());
        ret.setMemo(this.getMemo()); 
        ret.setAttachmentNumber(this.getAttachmentNumber());
        ret.setLocation(this.getLocation());

        if (this.getIcon()!=null) {
            ret.setIcon(new ImageIcon(this.getIcon().getImage()));
        }

        if (this.getBytes()!=null) {
            byte[] dest = new byte[this.getBytes().length];
            System.arraycopy(this.getBytes(), 0, dest, 0, this.getBytes().length);
            ret.setBytes(dest);
        }

        return ret;
    }
}
