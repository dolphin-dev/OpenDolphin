package open.dolphin.converter;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterText;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LetterTextConverter implements IInfoModelConverter {

    private LetterText model;

    public LetterTextConverter() {
    }

    public String getName() {
        return model.getName();
    }

    public String getTextValue() {
        return model.getTextValue();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (LetterText)model;
    }
}
