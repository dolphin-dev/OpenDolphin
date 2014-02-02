package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.OrcaInputSet;

/**
 * ORCA の tbl_inputset エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
public final class OrcaInputSetConverter implements IInfoModelConverter {
    
    private OrcaInputSet model;

    public String getHospId() {
        return model.getHospId();
    }

    public String getSetCd() {
        return model.getSetCd();
    }

    public String getYukostYmd() {
        return model.getYukostYmd();
    }

    public String getYukoedYmd() {
        return model.getYukoedYmd();
    }

    public int getSetSeq() {
        return model.getSetSeq();
    }

    public String getInputCd() {
        return model.getInputCd();
    }

    public float getSuryo1() {
        return model.getSuryo1();
    }

    public float getSuryo2() {
        return model.getSuryo2();
    }

    public int getKaisu() {
        return model.getKaisu();
    }

    public String getComment() {
        return model.getComment();
    }

    public String getAtai1() {
        return model.getAtai1();
    }

    public String getAtai2() {
        return model.getAtai2();
    }

    public String getAtai3() {
        return model.getAtai3();
    }

    public String getAtai4() {
        return model.getAtai4();
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
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (OrcaInputSet)model;
    }
}
