package open.dolphin.delegater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * PVT 関連の Business Delegater　クラス。
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public class ScheduleDelegater extends BusinessDelegater {

    private static final String RES_SCHEDULE = "/schedule";
    
    private static final ScheduleDelegater instance;

    static {
        instance = new ScheduleDelegater();
    }

    public static ScheduleDelegater getInstance() {
        return instance;
    }

    private ScheduleDelegater() {
    }

    public int removePvt(long pvtPK, long ptPK, String startDate) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/");
        sb.append(pvtPK).append(",").append(ptPK).append(",").append(startDate);
        String path = sb.toString();
        
        // DELETE
        deleteEasy(path);
        
        // Count
        return 1;
    }

    public List<PatientVisitModel> getPvtList(String pvtDate) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/").append(pvtDate);
        String path = sb.toString();
        
        // GET
        PatientVisitList result = getEasyJson(path, PatientVisitList.class);
        
        // Decode
        List<PatientVisitModel> list = result.getList();
        if (list != null && list.size() > 0) {
            for (PatientVisitModel pm : list) {
                decodeHealthInsurance(pm.getPatientModel());
                if (pm.getFirstInsurance()==null && pm.getPatientModel().getPvtHealthInsurances()!=null) {
                    PVTHealthInsuranceModel h = pm.getPatientModel().getPvtHealthInsurances().get(0);
                    pm.setFirstInsurance(h.toString());
                }
            }
        }
        return list;
    }
    
    public List<PatientVisitModel> getAssingedPvtList(String pvtDate, String orcaId, String unassignedId) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/");
        sb.append(orcaId).append(",");
        sb.append(unassignedId).append(",");
        sb.append(pvtDate);
        String path = sb.toString();
        
        // GET
        PatientVisitList result = getEasyJson(path, PatientVisitList.class);
        
        // Decode
        List<PatientVisitModel> list = result.getList();
        if (list != null && list.size() > 0) {
            for (PatientVisitModel pm : list) {
                decodeHealthInsurance(pm.getPatientModel());
                if (pm.getFirstInsurance()==null && pm.getPatientModel().getPvtHealthInsurances()!=null) {
                    PVTHealthInsuranceModel h = pm.getPatientModel().getPvtHealthInsurances().get(0);
                    pm.setFirstInsurance(h.toString());
                }
            }
        }
        return list;
    }
    
    public int postSchedule(PostSchedule ps) throws Exception {
        
        // PATH
        String path = "/schedule/document";
        
        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(ps);
        
        // POST
        String entityStr = postEasyJson(path, data, String.class);
        
        // Count
        return Integer.parseInt(entityStr);
    }

    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     *
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {

        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && c.size() > 0) {

            List<PVTHealthInsuranceModel> list = new ArrayList(c.size());

            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel) BeanUtils.xmlDecode(model.getBeanBytes());
                    list.add(hModel);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

            patient.setPvtHealthInsurances(list);
            patient.getHealthInsurances().clear();
            patient.setHealthInsurances(null);
        }
    }
}
