package open.dolphin.converter;

import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisOutcomeModelConverter implements IInfoModelConverter {

    private DiagnosisOutcomeModel model;

    public DiagnosisOutcomeModelConverter() {
    }

    public String getOutcome() {
        return model.getOutcome();
    }

    public String getOutcomeDesc() {
        return model.getOutcomeDesc();
    }

    public String getOutcomeCodeSys() {
        return model.getOutcomeCodeSys();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiagnosisOutcomeModel)model;
    }
}
