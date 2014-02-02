package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

/**
 * SchemaModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
@Entity
@Table(name = "d_image")
public class SchemaModel extends KarteEntryBean implements java.io.Serializable {
    
    @Embedded
    private ExtRefModel extRef;
    
    @Lob
    @Column(nullable=false)
    private byte[] jpegByte;
    
    @ManyToOne
    @JoinColumn(name="doc_id", nullable=false)
    private DocumentModel document;
    
    // Comaptible props
    @Transient
    private String fileName;
    
    @Transient
    private ImageIcon icon;
    
    @Transient
    private int imageNumber;

//    private String url;
//
//    private String sop;
    
    
    /** Creates new Schema */
    public SchemaModel() {
    }
    
    public ExtRefModel getExtRefModel() {
        return extRef;
    }
    
    public void setExtRefModel(ExtRefModel val) {
        extRef = val;
    }
    
    public DocumentModel getDocumentModel() {
        return document;
    }
    
    public void setDocumentModel(DocumentModel document) {
        this.document = document;
    }
    
    public byte[] getJpegByte() {
        return jpegByte;
    }
    
    public void setJpegByte(byte[] jpegByte) {
        this.jpegByte = jpegByte;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public void setIcon(ImageIcon val) {
        icon = val;
    }
    
    public int getImageNumber() {
        return imageNumber;
    }
    
    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String val) {
        fileName = val;
    }
    
    public IInfoModel getModel() {
        return (IInfoModel)getExtRefModel();
    }
    
    public void setModel(IInfoModel val) {
        setExtRefModel((ExtRefModel)val);
    }
    
    /**
     * 確定日及びイメージ番号で比較する。
     * @param other
     * @return
     */
    @Override
    public int compareTo(Object other) {
        int result = super.compareTo(other);
        if (result == 0) {
            // primittive なので比較はOK
            int no1 = getImageNumber();
            int no2 = ((SchemaModel) other).getImageNumber();
            result = no1 - no2;
        }
        return result;
    }

//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getSop() {
//        return sop;
//    }
//
//    public void setSop(String sop) {
//        this.sop = sop;
//    }
}
