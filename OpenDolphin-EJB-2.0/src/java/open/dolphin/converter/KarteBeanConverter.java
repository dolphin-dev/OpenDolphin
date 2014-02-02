package open.dolphin.converter;

import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.PhysicalModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class KarteBeanConverter implements IInfoModelConverter {

    private KarteBean model;

    public KarteBeanConverter() {
    }

    public long getId() {
        return model.getId();
    }

//    public PatientModel getPatientModel() {
//        return model.getPatientModel();
//    }

    public Date getCreated() {
        return model.getCreated();
    }

    public List<AllergyModel> getAllergies() {
        return model.getAllergies();
    }

    public List<PhysicalModel> getHeights() {
        return model.getHeights();
    }

    public List<PhysicalModel> getWeights() {
        return model.getWeights();
    }

    public List<String> getPatientVisits() {
        return model.getPatientVisits();
    }

    public List<DocInfoModel> getDocInfoList() {
        return model.getDocInfoList();
    }

    public List<PatientMemoModel> getMemoList() {
        return model.getMemoList();
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (KarteBean)m;
    }
}
