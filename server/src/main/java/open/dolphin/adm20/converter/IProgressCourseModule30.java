package open.dolphin.adm20.converter;

import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ProgressCourse;

/**
 *
 * @author kazushi
 */
public class IProgressCourseModule30 extends IAbstractModule30 {
    
    private IProgressCourse model;

    public IProgressCourse getModel() {
        return model;
    }

    public void setModel(IProgressCourse model) {
        this.model = model;
    }
    
    public void fromModel(ModuleModel model) {
        
    //minagawa^ 所見リストからカルテを参照するための拡張
        this.setDocPK(model.getDocumentModel().getId());
    //minagawa$    
        
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
        
        IModuleInfo info = new IModuleInfo();
        info.fromModel(model.getModuleInfoBean());
        this.setModuleInfo(info);
        
        // FreeText
        //System.err.println("freeText processing......");
        byte[] bytes = model.getBeanBytes();
        ProgressCourse pc = (ProgressCourse)(IOSHelper.xmlDecode(bytes));
        String text = pc.getFreeText();
        //System.err.println(text);
        String noHTMLString = text.replaceAll("\\<.*?>","");
        //System.err.println("removed......");
        //System.err.println(noHTMLString);
        pc.setFreeText(noHTMLString);
        IProgressCourse ipc = new IProgressCourse();
        ipc.fromModel(pc);
        this.setModel(ipc);
    }
    
    public ModuleModel toModel() {
        
        ModuleModel ret = new ModuleModel();
        
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
        
        ret.setModuleInfoBean(this.getModuleInfo().toModel());
        
        ret.setBeanBytes(IOSHelper.toXMLBytes(model.toModel()));
        
        return ret;
    }
}
