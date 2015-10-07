package open.dolphin.touch.converter;

import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ModuleModel;

/**
 *
 * @author kazushi minagawa
 */
public class IBundleModule extends IAbstractModule {
    
    private IClaimBundle model;

    public IClaimBundle getModel() {
        return model;
    }

    public void setModel(IClaimBundle model) {
        this.model = model;
    }
    
    public void fromModel(ModuleModel m) {
        
        this.setId(m.getId());
        
        // Date
        this.setConfirmed(IOSHelper.toDateStr(m.getConfirmed()));
        this.setStarted(IOSHelper.toDateStr(m.getStarted()));
        this.setEnded(IOSHelper.toDateStr(m.getEnded()));
        this.setRecorded(IOSHelper.toDateStr(m.getRecorded()));
        
        this.setLinkId(m.getLinkId());
        this.setLinkRelation(m.getLinkRelation());
        this.setStatus(m.getStatus());
        //this.setUserModel(m.getUserModel());
        //this.setKarteBean(m.getKarteBean());
        
        // constractで生成済
        this.getModuleInfo().fromModel(m.getModuleInfoBean());
        
        // decord
        ClaimBundle bundle = (ClaimBundle)IOSHelper.xmlDecode(m.getBeanBytes());
        IClaimBundle ib = new IClaimBundle();
        ib.fromModel(bundle);
        this.setModel(ib);
    }
    
    public ModuleModel toModel() {
        
        ModuleModel ret = new ModuleModel();
        
        ret.setId(this.getId());
        
        // Date
        ret.setConfirmed(IOSHelper.toDate(this.getConfirmed()));
        ret.setStarted(IOSHelper.toDate(this.getStarted()));
        ret.setEnded(IOSHelper.toDate(this.getEnded()));
        ret.setRecorded(IOSHelper.toDate(this.getRecorded()));
        
        ret.setLinkId(this.getLinkId());
        ret.setLinkRelation(this.getLinkRelation());
        ret.setStatus(this.getStatus());
        ret.setUserModel(this.getUserModel());
        ret.setKarteBean(this.getKarteBean());
        
        ret.setModuleInfoBean(this.getModuleInfo().toModel());
        
        ClaimBundle bundle = model.toModel();
        ret.setBeanBytes(IOSHelper.toXMLBytes(bundle));
        
        return ret;
    }
}