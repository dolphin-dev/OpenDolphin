/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.touch.converter;

import java.util.Date;
import open.dolphin.infomodel.LastDateCount;

/**
 *
 * @author kazushi
 */
public class ILastDateCount implements java.io.Serializable {
    
    private String created;
    
    private long diagnosisCount;
    
    private long activeDiagnosisCount;
    
    private long docCount;
    
    private String lastDocDate;
    
    private long labCount;
    
    private String lastLabDate;
    
    private long imageCount;
    
    private String lastImageDate;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getDocCount() {
        return docCount;
    }

    public void setDocCount(long docCount) {
        this.docCount = docCount;
    }

    public String getLastDocDate() {
        return lastDocDate;
    }

    public void setLastDocDate(String lastDocDate) {
        this.lastDocDate = lastDocDate;
    }

    public long getLabCount() {
        return labCount;
    }

    public void setLabCount(long labCount) {
        this.labCount = labCount;
    }

    public String getLastLabDate() {
        return lastLabDate;
    }

    public void setLastLabDate(String lastLabDate) {
        this.lastLabDate = lastLabDate;
    }

    public long getImageCount() {
        return imageCount;
    }

    public void setImageCount(long imageCount) {
        this.imageCount = imageCount;
    }

    public String getLastImageDate() {
        return lastImageDate;
    }

    public void setLastImageDate(String lastImageDate) {
        this.lastImageDate = lastImageDate;
    }
    
    public void fromModel(LastDateCount model) {
        this.setCreated(IOSHelper.toDateStr(model.getCreated()));
        this.setDiagnosisCount(model.getDiagnosisCount());
        this.setActiveDiagnosisCount(model.getActiveDiagnosisCount());
        this.setDocCount(model.getDocCount());
        this.setLastDocDate(IOSHelper.toDateStr(model.getLastDocDate()));
        this.setLabCount(model.getLabCount());
        this.setLastLabDate(model.getLastLabDate());
        this.setImageCount(model.getImageCount());
        this.setLastImageDate(IOSHelper.toDateStr(model.getLastImageDate()));
    }

    public long getActiveDiagnosisCount() {
        return activeDiagnosisCount;
    }

    public void setActiveDiagnosisCount(long activeDiagnosisCount) {
        this.activeDiagnosisCount = activeDiagnosisCount;
    }

    public long getDiagnosisCount() {
        return diagnosisCount;
    }

    public void setDiagnosisCount(long diagnosisCount) {
        this.diagnosisCount = diagnosisCount;
    }
}
