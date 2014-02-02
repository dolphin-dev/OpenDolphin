package open.dolphin.converter;

import open.dolphin.infomodel.CodeNamePack;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class CodeNamePackConverter implements IInfoModelConverter {
    
    private CodeNamePack model;
    
    public String getCode() {
        return model.getCode();
    }

    public String getName() {
        return model.getName();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (CodeNamePack)model;
    }
}
