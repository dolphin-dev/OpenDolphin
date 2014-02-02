package open.dolphin.converter14;

import open.dolphin.infomodel.*;

/**
 * 薬剤相互作用のモデル
 *
 * @author masuda, Masuda Naika
 */
public class DrugInteractionModelConverter implements IInfoModelConverter {

    private DrugInteractionModel model;
    
    public DrugInteractionModelConverter() {
    }

    public String getSrycd1(){
        return model.getSrycd1();
    }
    public String getSrycd2(){
        return model.getSrycd2();
    }
    public String getSskijo(){
        return model.getSskijo();
    }
    public String getSyojyoucd(){
        return model.getSyojyoucd();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DrugInteractionModel)model;
    }
}
