package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;



import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.util.BeanUtils;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PVTDelegater extends BusinessDelegater {

    private static final String RES_PVT = "pvt/";
    private static final String RES_PVT_MEMO = "pvt/memo/";
    
    /**
     * 受付情報 PatientVisitModel をデータベースに登録する。
     * @param pvtModel   受付情報 PatientVisitModel
     * @param principal  UserId と FacilityId
     * @return 保存に成功した個数
     */
    public int addPvt(PatientVisitModel pvtModel) {

        // convert
        PlistConverter con = new PlistConverter();
        String repXml = con.convert(pvtModel);

        // resource post
        String path = RES_PVT;
        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String enityStr = response.getEntity(String.class);
        
        if (DEBUG) {
            debug(status, enityStr);
        }
        // result = count
        int cnt = Integer.parseInt(enityStr);
        return cnt;
    }
    
    /**
     * 来院情報を取得する。
     * @param date     検索する来院日
     * @param firstRecord 何番目のレコードから取得するか
     * @return PatientVisitModel のコレクション
     */
    public Collection<PatientVisitModel> getPvt(String[] date, int firstRecord) {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(date[0]);
        sb.append(CAMMA);
        sb.append(String.valueOf(firstRecord));
        sb.append(CAMMA);
        sb.append(date[1]);
        sb.append(CAMMA);
        sb.append(date[2]);
        String path = sb.toString();

        //Long start = System.currentTimeMillis();
        //System.err.print("start rest");
        ClientResponse response = getResource(path)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        //Long time = System.currentTimeMillis() - start;
        //System.err.println(" time = " + time);
        String enityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, enityStr);
        }

        PlistParser parser = new PlistParser();
        List<PatientVisitModel> list = (List<PatientVisitModel>) parser.parse(enityStr);
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
    public Collection<PatientVisitModel> getPvtForAssigned(String did, String unassigened, String[] date, int firstRecord) {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(did).append(CAMMA);
        sb.append(unassigened).append(CAMMA);
        sb.append(date[0]).append(CAMMA);
        sb.append(String.valueOf(firstRecord)).append(CAMMA);
        sb.append(date[1]).append(CAMMA);
        sb.append(date[2]);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        String enityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, enityStr);
        }

        PlistParser parser = new PlistParser();
        List<PatientVisitModel> list = (List<PatientVisitModel>) parser.parse(enityStr);
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
    
    public int removePvt(long id) {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(id);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.TEXT_PLAIN)
                    .delete(ClientResponse.class);

        int status = response.getStatus();

        if (DEBUG) {
            debug(status, "delete response");
        }

        return 1;
    }
    
    public int updatePvtState(long pk, int state) {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append(pk);
        sb.append(CAMMA);
        sb.append(state);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.TEXT_PLAIN)
                    .put(ClientResponse.class);

        int status = response.getStatus();
        String enityStr = response.getEntity(String.class);
        //System.err.println("updatePvtState result="+enityStr);

        if (DEBUG) {
            debug(status, enityStr);
        }

        return Integer.parseInt(enityStr);
    }

    /**
     * メモを更新する。
     * @param pk
     * @param state
     * @return
     */
    public int updateMemo(long pk, String memo) {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT_MEMO);
        sb.append(pk);
        sb.append(CAMMA);
        sb.append(memo);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                    .accept(MediaType.TEXT_PLAIN)
                    .put(ClientResponse.class);

        int status = response.getStatus();

        if (DEBUG) {
            debug(status, "put response");
        }

        // result = count
        return 1;
    }
}
