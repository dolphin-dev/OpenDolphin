package open.dolphin.delegater;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.sun.jersey.api.client.ClientResponse;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * PVT 関連の Business Delegater　クラス。
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public class ScheduleDelegater extends BusinessDelegater {

    private static final String RES_SCHEDULE = "/schedule";
    
    private static final boolean debug = false;
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
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);

        // Check
        checkStatus(response);

        // Count
        return 1;
    }

    public List<PatientVisitModel> getPvtList(String pvtDate) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/").append(pvtDate);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientVisitList result = mapper.readValue(br, PatientVisitList.class);
        
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
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientVisitList result = mapper.readValue(br, PatientVisitList.class);
        
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
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(ps);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        String entityStr = getString(response);
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

    @Override
    protected void debug(int status, String entity) {
        if (debug || DEBUG) {
            super.debug(status, entity);
        }
    }
}
