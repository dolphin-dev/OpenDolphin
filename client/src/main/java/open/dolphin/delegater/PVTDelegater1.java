package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.PatientVisitModelConverter;
import open.dolphin.infomodel.*;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PVTDelegater1 extends BusinessDelegater {

    private static final String RES_PVT = "/pvt";
    private static final String RES_PVT_MEMO = "/pvt/memo";
    
    /**
     * 受付情報 PatientVisitModel をデータベースに登録する。
     * @param pvtModel   受付情報 PatientVisitModel
     * @param principal  UserId と FacilityId
     * @return 保存に成功した個数
     */
    public int addPvt(PatientVisitModel pvtModel) throws Exception {
        
        // Converter
        PatientVisitModelConverter conv = new PatientVisitModelConverter();
        conv.setModel(pvtModel);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(RES_PVT);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
    
    /**
     * 来院情報を取得する。
     * @param date     検索する来院日
     * @param firstRecord 何番目のレコードから取得するか
     * @return PatientVisitModel のコレクション
     */
    public Collection<PatientVisitModel> getPvt(String[] date, int firstRecord) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append("/");
        sb.append(date[0]);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstRecord));
        sb.append(CAMMA);
        sb.append(date[1]);
        sb.append(CAMMA);
        sb.append(date[2]);
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
            }
        }
        return list;
    }

    /**
     * 担当分と未決定分の来院情報を取得する。
     * @param did    担当ID
     * @param unassigened   未決定ID 19999
     * @param date  来院日
     * @param firstRecord　最初のレコード
     * @return 来院情報リスト
     */
    public Collection<PatientVisitModel> getPvtForAssigned(String did, String unassigened, String[] date, int firstRecord) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(did).append(CAMMA);
        sb.append(unassigened).append(CAMMA);
        sb.append(date[0]).append(CAMMA);
        sb.append(String.valueOf(firstRecord)).append(CAMMA);
        sb.append(date[1]).append(CAMMA);
        sb.append(date[2]);
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
            }
        }
        return list;
    }
    
    
    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {
        
        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && c.size() > 0) {

            ArrayList<PVTHealthInsuranceModel> list = new ArrayList<PVTHealthInsuranceModel>(c.size());

            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)BeanUtils.xmlDecode(model.getBeanBytes());
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
    
    public int removePvt(long id) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(id);
        String path = sb.toString();
        
        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);

        // Check
        checkStatus(response);

        // Count
        return 1;
    }
    
    public int updatePvtState(long pk, int state) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(pk);
        sb.append(CAMMA);
        sb.append(state);
        String path = sb.toString();
        
        // PUT
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.put(String.class);
        
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }

    /**
     * メモを更新する。
     * @param pk
     * @param state
     * @return
     */
    public int updateMemo(long pk, String memo) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT_MEMO);
        sb.append(pk);
        sb.append(CAMMA);
        sb.append(memo);
        String path = sb.toString();
        
        // PUT
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.put(String.class);
        
        // Check
        checkStatus(response);
        
        // Count
        return 1;
    }
}
