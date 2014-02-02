package open.dolphin.converter14;

import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterDate;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LetterDateConverter implements IInfoModelConverter {

    private LetterDate model;

    public LetterDateConverter() {
    }

    public String getName() {
        return model.getName();
    }

    public Date getValue() {
        return model.getValue();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (LetterDate)model;
    }
}
