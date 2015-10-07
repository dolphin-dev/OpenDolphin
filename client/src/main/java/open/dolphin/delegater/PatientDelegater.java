package open.dolphin.delegater;

import java.util.Collection;
import java.util.List;
import open.dolphin.converter.PatientModelConverter;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.util.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;

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
    private static final String TMP_KARTE_RESOURCE = "/patient/documents/status";
//s.oh^ 2014/07/22 一括カルテPDF出力
    private static final String ALL_PATIENTS_RESOURCE = "/patient/all";
//s.oh$
    
    /**
     * 患者を検索する。
     * @param pid 患者ID
     * @return PatientModel
     * @throws java.lang.Exception
     */
    public PatientModel getPatientById(String pid) throws Exception {
        
        // PATH
        String path = ID_RESOURCE;
        
        // GET
        PatientModel patient = getEasyJson(path, PatientModel.class);
        
        return patient;
    }
    
    /**
     * 患者を検索する。
     * @param spec PatientSearchSpec 検索仕様
     * @return PatientModel の Collection
     * @throws java.lang.Exception
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
        PatientList list = getEasyJson(path, PatientList.class);
        
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
     * @throws java.lang.Exception
     */
    public int updatePatient(PatientModel patient) throws Exception {
        
        // PATH
        String path = BASE_RESOURCE;
        
        // Converter
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);

        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String entityStr = putEasyJson(path, data, String.class);
        
        // Count
        return  Integer.parseInt(entityStr);
    }
    
    public List getTmpKarte() throws Exception {
     
        // PATH
        String path = TMP_KARTE_RESOURCE;

        // GET
        PatientList list = getEasyJson(path, PatientList.class);
        
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
    
//s.oh^ 2014/07/22 一括カルテPDF出力
    public List<PatientModel> getAllPatient() throws Exception {
     
        // PATH
        String path = ALL_PATIENTS_RESOURCE;

        // GET
        PatientList list = getEasyJson(path, PatientList.class);
        
        // List
        return list.getList();
    }
//s.oh$
    
//s.oh^ 2014/10/01 患者検索(傷病名)
    public List getCustom(String val) throws Exception {
     
        // PATH
        String path = BASE_RESOURCE + "custom/" + val;

        // GET
        PatientList list = getEasyJson(path, PatientList.class);
        
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
//s.oh$
}
