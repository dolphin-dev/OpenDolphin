package open.dolphin.converter14;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.infomodel.*;

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

    public List<AllergyModelConverter> getAllergies() {
        List<AllergyModel> list = model.getAllergies();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<AllergyModelConverter> ret = new ArrayList<AllergyModelConverter>();
        for (AllergyModel m : list) {
            AllergyModelConverter con = new AllergyModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<PhysicalModelConverter> getHeights() {
        List<PhysicalModel> list = model.getHeights();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<PhysicalModelConverter> ret = new ArrayList<PhysicalModelConverter>();
        for (PhysicalModel m : list) {
            PhysicalModelConverter con = new PhysicalModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<PhysicalModelConverter> getWeights() {
        List<PhysicalModel> list = model.getWeights();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<PhysicalModelConverter> ret = new ArrayList<PhysicalModelConverter>();
        for (PhysicalModel m : list) {
            PhysicalModelConverter con = new PhysicalModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<String> getPatientVisits() {
        return model.getPatientVisits();
    }

    public List<DocInfoModelConverter> getDocInfoList() {
        List<DocInfoModel> list = model.getDocInfoList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<DocInfoModelConverter> ret = new ArrayList<DocInfoModelConverter>();
        for (DocInfoModel m : list) {
            DocInfoModelConverter con = new DocInfoModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }

    public List<PatientMemoModelConverter> getMemoList() {
        List<PatientMemoModel> list = model.getMemoList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        List<PatientMemoModelConverter> ret = new ArrayList<PatientMemoModelConverter>();
        for (PatientMemoModel m : list) {
            PatientMemoModelConverter con = new PatientMemoModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        return ret;
    }
    
//masuda^
    public Date getLastDocDate() {
        return model.getLastDocDate();
    }
    
//masua$    

    @Override
    public void setModel(IInfoModel m) {
        this.model = (KarteBean)m;
    }
}
