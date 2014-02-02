package open.dolphin.delegater;

import java.io.BufferedReader;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.NLaboModuleConverter;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * Labo 関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LaboDelegater extends BusinessDelegater {

    //=========================================================
    // 新 LabMozule
    //=========================================================

    public List<PatientLiteModel> getConstrainedPatients(List<String> idList) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/patient/");
        for (String pid : idList) {
            sb.append(pid);
            sb.append(CAMMA);
        }
        int len = sb.length();
        sb.setLength(len-1);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        PatientLiteList result = mapper.readValue(br, PatientLiteList.class);
        br.close();

        // List
        return result.getList();
    }
    
    /**
     * 検査結果を追加する。
     * @param value 追加する検査モジュール
     * @return      患者オブジェクト
     */
    public PatientModel putNLaboModule(NLaboModule value) throws Exception {

//        System.err.println(value.getPatientId());
//        System.err.println(value.getPatientName());
//        System.err.println(value.getPatientSex());
//        List<NLaboItem> items = value.getItems();
//        for (NLaboItem item : items) {
//            System.err.println(item.getItemCode());
//            System.err.println(item.getItemName());
//        }

        // PATH
        String path = "/lab/module";
        
        // Converter
        NLaboModuleConverter conv = new NLaboModuleConverter();
        conv.setModel(value);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.post(String.class);

        // PatientModel
        BufferedReader br = getReader(response);
        ObjectMapper mapper2 = new ObjectMapper();
        PatientModel patient = mapper2.readValue(br, PatientModel.class);
        br.close();
        
        return patient;
    }

    /**
     * ラボモジュールを検索する。
     * @param patientId     対象患者のID
     * @param firstResult   取得結果リストの最初の番号
     * @param maxResult     取得する件数の最大値
     * @return              ラボモジュールを採取日で降順に格納したリスト
     */
    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/module/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        NLaboModuleList result = mapper.readValue(br, NLaboModuleList.class);
        br.close();
        
        // List
        return result.getList();
    }

    /**
     * 指定された検査コードの結果を取得する。
     * @param patientId     対象患者
     * @param firstResult   全件数のなかで最初に返す番号
     * @param maxResult     戻す最大件数
     * @param itemCode      検索する検査コード
     * @return              検査結果項目を採取日で降順に格納したリスト
     */
    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, String itemCode) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/item/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        sb.append(CAMMA);
        sb.append(itemCode);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        NLaboItemList result = mapper.readValue(br, NLaboItemList.class);
        br.close();
        
        // List
        return result.getList();
    }
}
