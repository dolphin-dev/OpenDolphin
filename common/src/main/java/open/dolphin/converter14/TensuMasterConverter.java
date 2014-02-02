package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.TensuMaster;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class TensuMasterConverter implements IInfoModelConverter {
    
    private TensuMaster model;
    
    public Integer getHospnum() {
        return model.getHospnum();
    }

    public String getSrycd() {
        return model.getSrycd();
    }

    public String getYukostymd() {
        return model.getYukoedymd();
    }

    public String getYukoedymd() {
        return model.getYukoedymd();
    }

    public String getName() {
        return model.getName();
    }

    public String getKananame() {
        return model.getKananame();
    }

    public String getTaniname() {
        return model.getTaniname();
    }

    public String getTensikibetu() {
        return model.getTensikibetu();
    }

    public String getTen() {
        return model.getTen();
    }

    public String getYkzkbn() {
        return model.getYkzkbn();
    }

    public String getYakkakjncd() {
        return model.getYakkakjncd();
    }

    public String getNyugaitekkbn() {
        return model.getNyugaitekkbn();
    }

    public String getRoutekkbn() {
        return model.getRoutekkbn();
    }

    public String getSrysyukbn() {
        return model.getSrysyukbn();
    }

    public String getHospsrykbn() {
        return model.getHospsrykbn();
    }   
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (TensuMaster)model;
    }
}
