package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.util.BeanUtils;

/**
 * 患者関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class  PatientDelegater extends BusinessDelegater {

    private static final String BASE_RESOURCE = "patient/";
    private static final String NAME_RESOURCE = "patient/name/";
    private static final String KANA_RESOURCE = "patient/kana/";
    private static final String ID_RESOURCE = "patient/id/";
    private static final String DIGIT_RESOURCE = "patient/digit/";
    private static final String PVT_DATE_RESOURCE = "patient/pvt/";

    
    /**
     * 患者を追加する。
     * @param patient 追加する患者
     * @return PK
     */
    public long addPatient(PatientModel patient) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(patient);

        String path = BASE_RESOURCE;

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .post(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Long.parseLong(entityStr);
    }
    
    /**
     * 患者を検索する。
     * @param pid 患者ID
     * @return PatientModel
     */
    public PatientModel getPatientById(String pid) {
        
        String path = ID_RESOURCE;

        ClientResponse response = getResource(path)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

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
     * 患者を検索する。
     * @param spec PatientSearchSpec 検索仕様
     * @return PatientModel の Collection
     */
    public Collection getPatients(PatientSearchSpec spec) {

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

        ClientResponse response = getResource(path)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        List<PatientModel> list = (List<PatientModel>) con.parse(entityStr);
        if (list != null && list.size() > 0) {
            for (PatientModel pm : list) {
                decodeHealthInsurance(pm);
            }
        }
        return list;
    }

    /**
     * 患者を更新する。
     * @param patient 更新する患者
     * @return 更新数
     */
    public int updatePatient(PatientModel patient) {

        PlistConverter con = new PlistConverter();
        String repXml = con.convert(patient);

        String path = BASE_RESOURCE;

        ClientResponse response = getResource(path)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return Integer.parseInt(entityStr);
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
}
