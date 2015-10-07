package open.dolphin.adm10.converter;

import open.dolphin.converter.IInfoModelConverter;
import open.dolphin.infomodel.KarteNumber;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa
 */
public class IKarteNumber implements IInfoModelConverter {
    
    private KarteNumber model;
    
    public long getKarteNumber() {
        return model.getKarteNumber();
    }
    
    public String getCreated() {
        return (model.getCreated()!=null) ? IOSHelper.toDateStr(model.getCreated()) : null;
    }
    
    public String getNumber() {
        return model.getNumber();
    }
    
    @Override
    public void setModel(IInfoModel m) {
        this.model = (KarteNumber)m;
    }
}
