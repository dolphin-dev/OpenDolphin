package open.dolphin.converter14;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.LetterItem;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LetterItemConverter implements IInfoModelConverter {

    private LetterItem model;

    public LetterItemConverter() {
    }

    public String getName() {
        return model.getName();
    }

    public String getValue() {
        return model.getValue();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (LetterItem)model;
    }
}
