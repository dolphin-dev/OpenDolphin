package open.dolphin.delegater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import open.dolphin.converter.PatientVisitModelConverter;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * PVT 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public class PVTDelegater extends BusinessDelegater {

    private static final String RES_PVT = "/pvt2";
    
    private static final PVTDelegater instance;

    static {
        instance = new PVTDelegater();
    }

    public static PVTDelegater getInstance() {
        return instance;
    }

    private PVTDelegater() {
    }

    /**
     * 受付情報 PatientVisitModel をデータベースに登録する。
     *
     * @param pvtModel 受付情報 PatientVisitModel
     * @return 保存に成功した個数
     */
    public int addPvt(PatientVisitModel pvtModel) {
        
        try {
            // Converter
            PatientVisitModelConverter conv = new PatientVisitModelConverter();
            conv.setModel(pvtModel);
            
            // JSON
            ObjectMapper mapper = this.getSerializeMapper();
            byte[] data = mapper.writeValueAsBytes(conv);
            
            // POST
            String entityStr = postEasyJson(RES_PVT, data, String.class);
            
            return Integer.parseInt(entityStr);
            
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
        return 0;
    }

    public int removePvt(long id) {
        
        try {
            // PATH
            StringBuilder sb = new StringBuilder();
            sb.append(RES_PVT);
            sb.append(id);
            String path = sb.toString();
            
            // DELETE
            deleteEasy(path);
            
            // Count
            return 1;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return 0;
    }

    public List<PatientVisitModel> getPvtList() {

        StringBuilder sb = new StringBuilder();
        sb.append(RES_PVT);
        sb.append("/pvtList");
        String path = sb.toString();
        
        try {

            // GET
            PatientVisitList result = getEasyJson(path, PatientVisitList.class);
            
            // Decode
            List<PatientVisitModel> list = result.getList();
            if (list != null && list.size() > 0) {
                for (PatientVisitModel pm : list) {
                    decodeHealthInsurance(pm.getPatientModel());
                }
            }
            //return list;
            return (list != null) ? list : new ArrayList<>(1);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return new ArrayList<>(1);
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

            List<PVTHealthInsuranceModel> list = new ArrayList<>(c.size());

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
