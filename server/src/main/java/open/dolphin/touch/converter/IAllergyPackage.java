/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.touch.converter;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author kazushi
 */
public class IAllergyPackage implements Serializable {
    
    private long ptPK;
    
    private List<IAllergyModel> added;
    
    private List<IAllergyModel> modified;
    
    private List<IAllergyModel> deleted;

    public List<IAllergyModel> getAdded() {
        return added;
    }

    public void setAdded(List<IAllergyModel> added) {
        this.added = added;
    }

    public List<IAllergyModel> getModified() {
        return modified;
    }

    public void setModified(List<IAllergyModel> modified) {
        this.modified = modified;
    }

    public List<IAllergyModel> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<IAllergyModel> deleted) {
        this.deleted = deleted;
    }

    public long getPtPK() {
        return ptPK;
    }

    public void setPtPK(long ptPK) {
        this.ptPK = ptPK;
    }
}
