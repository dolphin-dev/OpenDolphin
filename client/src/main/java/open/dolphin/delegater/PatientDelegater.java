package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.PatientModelConverter;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * 患者関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class  PatientDelegater extends BusinessDelegater {

    private static final String BASE_RESOURCE = "/patient/";
    private static final String NAME_RESOURCE = "/patient/name/";
    private static final String KANA_RESOURCE = "/patient/kana/";
    private static final String ID_RESOURCE = "/patient/id/";
    private static final String DIGIT_RESOURCE = "/patient/digit/";
    private static final String PVT_DATE_RESOURCE = "/patient/pvt/";
//minagawa^ 仮保存カルテ取得対応
    private static final String TMP_KARTE_RESOURCE = "/patient/documents/status";
//minagawa$
    
    /**
     * 患者を追加する。
     * @param patient 追加する患者
     * @return PK
     */
    public long addPatient(PatientModel patient) throws Exception {
        
        // Converter
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(BASE_RESOURCE);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        
        // PK
        String entityStr = getString(response);
        long pk = Long.parseLong(entityStr);
        return pk;
    }
    
    /**
     * 患者を検索する。
     * @param pid 患者ID
     * @return PatientModel
     */
    public PatientModel getPatientById(String pid) throws Exception {
        
        // PATH
        String path = ID_RESOURCE;
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // PatientModel
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientModel patient = mapper.readValue(br, PatientModel.class);
        br.close();

        return patient;
    }
    
    /**
     * 患者を検索する。
     * @param spec PatientSearchSpec 検索仕様
     * @return PatientModel の Collection
     */
    public Collection getPatients(PatientSearchSpec spec) throws Exception {

        StringBuilder sb = new StringBuilder();

        switch (spec.getCode()) {

            case PatientSearchSpec.NAME_SEARCH:
                sb.append(NAME_RESOURCE);
                sb.append(spec.getName());
                break;

            case PatientSearchSpec.KANA_SEARCH:
                sb.append(KANA_RESOURCE);
                sb.append(spec.getName());
                break;

            case PatientSearchSpec.DIGIT_SEARCH:
                sb.append(DIGIT_RESOURCE);
                sb.append(spec.getDigit());
                break;

           case PatientSearchSpec.DATE_SEARCH:
                sb.append(PVT_DATE_RESOURCE);
                sb.append(spec.getDigit());
                break;
        }

        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientList list = mapper.readValue(br, PatientList.class);
        br.close();
        
        // Decode
        if (list != null && list.getList()!=null) {
            List<PatientModel> inList = list.getList();
            for (PatientModel pm : inList) {
                decodeHealthInsurance(pm);
            }
            return inList;
            
        } else {
            return null;
        }
    }

    /**
     * 患者を更新する。
     * @param patient 更新する患者
     * @return 更新数
     */
    public int updatePatient(PatientModel patient) throws Exception {
        
        // PATH
        String path = BASE_RESOURCE;
        
        // Converter
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        // Count
        String entityStr = getString(response);
        return  Integer.parseInt(entityStr);
    }
    
//minagawa^ 仮保存カルテ取得対応
    public List getTmpKarte() throws Exception {
     
        // PATH
        String path = TMP_KARTE_RESOURCE;
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientList list = mapper.readValue(br, PatientList.class);
        br.close();
        
        // Decode
        if (list != null && list.getList()!=null) {
            List<PatientModel> inList = list.getList();
            for (PatientModel pm : inList) {
                decodeHealthInsurance(pm);
            }
            return inList;
            
        } else {
            return null;
        }
    }
//minagawa$

    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     */
    private void decodeHealthInsurance(PatientModel patient) {

        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && c.size() > 0) {

            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)BeanUtils.xmlDecode(model.getBeanBytes());
                    patient.addPvtHealthInsurance(hModel);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

            c.clear();
            patient.setHealthInsurances(null);
        }
    }
}
