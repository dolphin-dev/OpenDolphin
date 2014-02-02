package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PVTHealthInsuranceModelConverter implements IInfoModelConverter {

    private PVTHealthInsuranceModel model;

    public PVTHealthInsuranceModelConverter() {
    }

    public String getGUID() {
        return model.getGUID();
    }

    public String getInsuranceClass() {
        return model.getInsuranceClass();
    }

    public String getInsuranceClassCode() {
        return model.getInsuranceClassCode();
    }

    public String getInsuranceClassCodeSys() {
        return model.getInsuranceClassCodeSys();
    }

    public String getInsuranceNumber() {
        return model.getInsuranceNumber();
    }

    public String getClientGroup() {
        return model.getClientGroup();
    }

    public String getClientNumber() {
        return model.getClientNumber();
    }

    public String getFamilyClass() {
        return model.getFamilyClass();
    }

    public String getStartDate() {
        return model.getStartDate();
    }

    public String getExpiredDate() {
        return model.getExpiredDate();
    }

    public String[] getContinuedDisease() {
        return model.getContinuedDisease();
    }

    public String getPayInRatio() {
        return model.getPayInRatio();
    }

    public String getPayOutRatio() {
        return model.getPayOutRatio();
    }

    public List<PVTPublicInsuranceItemModelConverter> getPublicItems() {
        List<PVTPublicInsuranceItemModel> list = model.getPublicItems();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<PVTPublicInsuranceItemModelConverter> ret = new ArrayList<PVTPublicInsuranceItemModelConverter>();
        for (PVTPublicInsuranceItemModel m : list) {
            PVTPublicInsuranceItemModelConverter con = new PVTPublicInsuranceItemModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    @Override
    public void setModel(IInfoModel m) {
        this.model = (PVTHealthInsuranceModel)m;
        PVTPublicInsuranceItemModel[] testArray = model.getPVTPublicInsuranceItem();
        if (testArray!=null && testArray.length>0) {
            List<PVTPublicInsuranceItemModel> list = new ArrayList<PVTPublicInsuranceItemModel>();
            list.addAll(Arrays.asList(testArray));
            model.setPublicItems(list);
        }
    }
}
