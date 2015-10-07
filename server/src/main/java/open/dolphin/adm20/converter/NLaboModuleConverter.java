package open.dolphin.adm20.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;

/**
 *
 * @author kazushi
 */
public final class NLaboModuleConverter {   //implements IInfoModelConverter {

    private NLaboModule model;

    public NLaboModuleConverter() {
    }

    public Long getId() {
        return model.getId();
    }
    
    public String getFacilityId() {
        return model.getFacilityId();
    }
    
    public String getFacilityName() {
        return model.getFacilityName();
    }
    
    public String getJmariCode() {
        return model.getJmariCode();
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getPatientName() {
        return model.getPatientName();
    }

    public String getPatientSex() {
        return model.getPatientSex();
    }

    public String getSampleDate() {
        return model.getSampleDate();
    }

    public String getNumOfItems() {
        return model.getNumOfItems();
    }

    public List<NLaboItemConverter> getItems() {
        List<NLaboItem> list = model.getItems();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<NLaboItemConverter> ret = new ArrayList<>();
        for (NLaboItem m : list) {
            NLaboItemConverter con = new NLaboItemConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public Boolean getProgressState() {
        return model.getProgressState();
    }

    public void setProgressState(Boolean progressState) {
        model.setProgressState(progressState);
    }

    public String getLaboCenterCode() {
        return model.getLaboCenterCode();
    }

    public String getModuleKey() {
        
        String key = model.getModuleKey();
        
        if (key!=null) {
            return key;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(model.getPatientId()).append(".");
            String test = model.getSampleDate();
            sb.append(test);
            if (test.length()=="yyyy-MM-dd".length()) {
                sb.append(" 00:00");
            }
            sb.append(model.getLaboCenterCode());
            return sb.toString();
        }
    }

    public String getReportFormat() {
        return model.getReportFormat();
    }

    public void setModel(NLaboModule model) {
        this.model = model;
    }
}
