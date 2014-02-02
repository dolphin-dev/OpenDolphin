package open.dolphin.infomodel;


import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * LaboSpecimenValue
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_labo_specimen")
public class LaboSpecimenValue extends InfoModel {
    
    private static final long serialVersionUID = -7010436187772562514L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name="module_id", nullable=false)
    private LaboModuleValue laboModule;
    
    private String specimenName;
    
    private String specimenCode;
    
    private String specimenCodeId;
    
    @OneToMany(mappedBy="laboSpecimen", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<LaboItemValue> laboItems;
    
    
    public LaboSpecimenValue() {
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public LaboModuleValue getLaboModule() {
        return laboModule;
    }
    
    public void setLaboModule(LaboModuleValue laboModule) {
        this.laboModule = laboModule;
    }
    
    public Collection<LaboItemValue> getLaboItems() {
        return laboItems;
    }
    
    public void setLaboItems(Collection<LaboItemValue> laboItems) {
        this.laboItems = laboItems;
    }
    
    public void addLaboItem(LaboItemValue item) {
        if (laboItems == null) {
            laboItems = new ArrayList<LaboItemValue>();
        }
        laboItems.add(item);
    }
    
    public String getSpecimenCode() {
        return specimenCode;
    }
    
    public void setSpecimenCode(String specimenCode) {
        this.specimenCode = specimenCode;
    }
    
    public String getSpecimenCodeId() {
        return specimenCodeId;
    }
    
    public void setSpecimenCodeId(String specimenCodeId) {
        this.specimenCodeId = specimenCodeId;
    }
    
    public String getSpecimenName() {
        return specimenName;
    }
    
    public void setSpecimenName(String specimenName) {
        this.specimenName = specimenName;
    }
}
