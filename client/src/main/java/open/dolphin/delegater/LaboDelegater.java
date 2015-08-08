package open.dolphin.delegater;

import java.util.List;
import open.dolphin.converter.NLaboModuleConverter;
import open.dolphin.infomodel.*;
import org.codehaus.jackson.map.ObjectMapper;

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
        PatientLiteList result = null;
        try {
            result = getEasyJson(path, PatientLiteList.class);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        // List
        return result!=null ? result.getList() : null;
    }
    
    /**
     * 検査結果を追加する。
     * @param value 追加する検査モジュール
     * @return 
     * @throws java.lang.Exception
     */
    public PatientModel putNLaboModule(NLaboModule value) throws Exception {

        // PATH
        String path = "/lab/module";
        
        // Converter
        NLaboModuleConverter conv = new NLaboModuleConverter();
        conv.setModel(value);

        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // POST
        PatientModel patient = postEasyJson(path, data, PatientModel.class);
        
        return patient;
    }

    /**
     * ラボモジュールを検索する。
     * @param patientId     対象患者のID
     * @param firstResult   取得結果リストの最初の番号
     * @param maxResult     取得する件数の最大値
     * @return 
     * @throws java.lang.Exception 
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
        NLaboModuleList result = getEasyJson(path, NLaboModuleList.class);
        
        // List
        return result.getList();
    }
    
//s.oh^ 2013/09/18 ラボデータの高速化
    public String getLaboTestCount(String pid) {
        // PATH
        String path = "/lab/module/count/";
        path += pid;

        // GET
        String entityStr = getEasyText(path, String.class);
        
        return entityStr;
    }
//s.oh$

    /**
     * 指定された検査コードの結果を取得する。
     * @param patientId     対象患者
     * @param firstResult   全件数のなかで最初に返す番号
     * @param maxResult     戻す最大件数
     * @param itemCode      検索する検査コード
     * @return              検査結果項目を採取日で降順に格納したリスト
     * @throws java.lang.Exception
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
        NLaboItemList result = getEasyJson(path, NLaboItemList.class);
        
        // List
        return result.getList();
    }
    
    // ラボデータの削除 2013/06/24
    public int deleteLabTest(long moduleId) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/lab/module/");
        sb.append(moduleId);
        String path = sb.toString();

        // DELETE
        deleteEasy(path);
        
        // Count
        return 1;
    }
}
