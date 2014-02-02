package open.dolphin.converter;

import java.util.ArrayList;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.OrcaInputCd;
import open.dolphin.infomodel.OrcaInputSet;

/**
 * ORCA の tbl_inputcd エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
public final class OrcaInputCdConverter implements IInfoModelConverter {
    
    private OrcaInputCd model;

    public String getHospId() {
        return model.getHospId();
    }

    public String getCdsyu() {
        return model.getCdsyu();
    }

    public String getInputCd() {
        return model.getInputCd();
    }

    public String getSryKbn() {
        return model.getSryKbn();
    }

    public String getSryCd() {
        return model.getSryCd();
    }

    public int getDspSeq() {
        return model.getDspSeq();
    }

    public String getDspName() {
        return model.getDspName();
    }

    public String getTermId() {
        return model.getTermId();
    }

    public String getOpId() {
        return model.getOpId();
    }

    public String getCreYmd() {
        return model.getCreYmd();
    }

    public String getUpYmd() {
        return model.getUpYmd();
    }

    public String getUpHms() {
        return model.getUpHms();
    }
    
    public ArrayList getInputSet() {
        ArrayList list = model.getInputSet();
        if (list==null || list.isEmpty()) {
            return null;
        }
        ArrayList ret = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            OrcaInputSet set = (OrcaInputSet)list.get(i);
            OrcaInputSetConverter conv = new OrcaInputSetConverter();
            conv.setModel(set);
            ret.add(conv);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (OrcaInputCd)model;
    }
}
