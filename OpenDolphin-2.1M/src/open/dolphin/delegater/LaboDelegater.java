package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;

import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;

import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientLiteModel;
import open.dolphin.infomodel.PatientModel;

/**
 * Labo 関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LaboDelegater extends BusinessDelegater {

    //=========================================================
    // 新 LabMozule
    //=========================================================

    public List<PatientLiteModel> getConstrainedPatients(List<String> idList) {

        StringBuilder sb = new StringBuilder();
        sb.append("lab/patient/");
        for (String pid : idList) {
            sb.append(pid);
            sb.append(CAMMA);
        }
        int len = sb.length();
        sb.setLength(len-1);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);
        
        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<PatientLiteModel> list = (List<PatientLiteModel>) con.parse(entityStr);
        return list;
    }
    
    /**
     * 検査結果を追加する。
     * @param value 追加する検査モジュール
     * @return      患者オブジェクト
     */
    public PatientModel putNLaboModule(NLaboModule value) {

//        System.err.println(value.getPatientId());
//        System.err.println(value.getPatientName());
//        System.err.println(value.getPatientSex());
//        List<NLaboItem> items = value.getItems();
//        for (NLaboItem item : items) {
//            System.err.println(item.getItemCode());
//            System.err.println(item.getItemName());
//        }

        String path = "lab/module/";

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(value);

        //System.err.println(repXml);

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .type(MediaType.APPLICATION_XML_TYPE)
                .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser parser = new PlistParser();
        PatientModel patient = (PatientModel) parser.parse(entityStr);
        return patient;
    }

    /**
     * ラボモジュールを検索する。
     * @param patientId     対象患者のID
     * @param firstResult   取得結果リストの最初の番号
     * @param maxResult     取得する件数の最大値
     * @return              ラボモジュールを採取日で降順に格納したリスト
     */
    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult) {

        StringBuilder sb = new StringBuilder();
        sb.append("lab/module/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<NLaboModule> list = (List<NLaboModule>) con.parse(entityStr);
        return list;
    }

    /**
     * 指定された検査コードの結果を取得する。
     * @param patientId     対象患者
     * @param firstResult   全件数のなかで最初に返す番号
     * @param maxResult     戻す最大件数
     * @param itemCode      検索する検査コード
     * @return              検査結果項目を採取日で降順に格納したリスト
     */
    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, String itemCode) {

        StringBuilder sb = new StringBuilder();
        sb.append("lab/item/");
        sb.append(patientId);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstResult));
        sb.append(CAMMA);
        sb.append(String.valueOf(maxResult));
        sb.append(CAMMA);
        sb.append(itemCode);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<NLaboItem> list = (List<NLaboItem>) con.parse(entityStr);
        return list;
    }
}
