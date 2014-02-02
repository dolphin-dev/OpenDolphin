package open.dolphin.infomodel;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Kazushi Minagawa.
 */
//@Entity
//@Table(name="d_patient_file")
public class PatientFileModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String docType;
    private String contentType;
    private long contentSize;
    private long lastModified;
    private String digest;
    private String memo;
    private String fileName; // original file name
    
    //@Transient
    private String location;
    
    private String extension;
    
    //@Lob
    private byte[] fileData;
    
    // @ManyToOne は止め
    private long patient_id;
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PatientFileModel)) {
            return false;
        }
        PatientFileModel other = (PatientFileModel) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.PatientFile[ id=" + id + " ]";
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
}
