package open.dolphin.touch;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.*;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.*;
import open.dolphin.touch.converter.IDocument;
import open.dolphin.touch.converter.IPriscription;
import open.dolphin.touch.session.IPhoneServiceBean;
//import open.dolphin.msg.ServerPrescriptionPDFMaker;
import open.dolphin.session.KarteServiceBean;
import open.dolphin.touch.converter.IDocument2;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */

@Path("/touch")
public class DolphinResource extends AbstractResource {

    private static final String ELEMENT_PATIENT_VISIT_START = "<patientVisit>";
    private static final String ELEMENT_PATIENT_VISIT_END = "</patientVisit>";
    private static final String ELEMENT_PVT_DATE = "pvtDate";
    private static final String ELEMENT_PVT_STATUS = "pvtStatus";
//s.oh^ 2013/11/05 iPhone/iPadの受付リストに保険を追加
    private static final String ELEMENT_PVT_F_INS = "pvtFirstInsurance";    // add funabashi 20131103
//s.oh$

    private static final String ELEMENT_ADDRESS_START = "<address>";
    private static final String ELEMENT_ADDRESS_END = "</address>";
    private static final String ELEMENT_ZIP_CODE = "zipCode";
    private static final String ELEMENT_FULL_ADDRESS = "fullAddress";
    private static final String ELEMENT_TELEPHONE = "telephone";
    private static final String ELEMENT_MOBILE_PHONE = "mobilePhone";
    private static final String ELEMENT_E_MAIL = "email";

    //private static final String ENTITY_MED_ORDER = "medOrder";
    private static final String ELEMENT_BUNDLE_MED_START = "<bundleMed>";
    private static final String ELEMENT_BUNDLE_MED_END = "</bundleMed>";
    //private static final String ELEMENT_CLAIM_ITEM_START = "<claimItem>";
    //private static final String ELEMENT_CLAIM_ITEM_END = "</claimItem>";
    private static final String ELEMENT_RP_DATE = "rpDate";
    private static final String ELEMENT_CLAIM_ITEM_NAME = "name";
    private static final String ELEMENT_CLAIM_ITEM_QUANTITY = "quantity";
    private static final String ELEMENT_CLAIM_ITEM_UNIT = "unit";
    private static final String ELEMENT_CLAIM_ITEM_NUM_DAYS = "numDays";
    private static final String ELEMENT_CLAIM_ITEM_ADMINI = "administration";

    //private static final String ELEMENT_MODULE_START = "<module>";
    //private static final String ELEMENT_MODULE_END = "</module>";

    private static final String ELEMENT_LAB_ITEM_START = "<laboItem>";
    private static final String ELEMENT_LAB_ITEM_END = "</laboItem>";
    private static final String ELEMENT_LABO_CODE = "laboCode";
    private static final String ELEMENT_SAMPLE_DATE = "sampleDate";
    private static final String ELEMENT_GROUP_CODE = "groupCode";
    private static final String ELEMENT_GROUP_NAME = "groupName";
    private static final String ELEMENT_PARENT_CODE = "parentCode";
    private static final String ELEMENT_ITEM_CODE = "itemCode";
    private static final String ELEMENT_ITEM_MEDIS_CODE = "medisCode";
    private static final String ELEMENT_ITEM_NAME = "itemName";
    private static final String ELEMENT_NORMAL_VALUE = "normalValue";
    //private static final String ELEMENT_UNIT = "unit";
    //private static final String ELEMENT_VALUE = "value";
    private static final String ELEMENT_OUT_FLAG = "outFlag";
    private static final String ELEMENT_COMMENT_1 = "comment1";
    private static final String ELEMENT_COMMENT_2 = "comment2";
    private static final String ELEMENT_TEST_ITEM_START = "<testItem>";
    private static final String ELEMENT_TEST_ITEM_END = "</testItem>";
    private static final String ELEMENT_RESULT_START = "<result>";
    private static final String ELEMENT_RESULT_END = "</result>";

    private static final String ELEMENT_DIAGNOSIS_START = "<registeredDiagnosis>";
    private static final String ELEMENT_DIGNOSIS_END = "</registeredDiagnosis>";
    private static final String ELEMENT_DIAGNOSIS = "diagnosis";
    private static final String ELEMENT_CATEGORY = "category";
    private static final String ELEMENT_OUTCOME = "outcome";
    //private static final String ELEMENT_START_DATE = "startDate";
    private static final String ELEMENT_END_DATE = "endDate";

    private static final String ELEMENT_SCHEMA_START = "<schema>";
    private static final String ELEMENT_BUCKET = "bucket";
    private static final String ELEMENT_SOP = "sop";
    private static final String ELEMENT_BASE64 = "base64";
    private static final String ELEMENT_SCHEMA_END = "</schema>";

    private static final String MML_DATE_TIME_SEPARATOR = "T";
    private static final String USER_ALLOWED_TYPE = "ASP_MEMBER";
    private static final String ASP_TEST_USER = "ASP_TESTER";

    // S3 parameters
    private static final String ELEMENT_S3_URL = "s3URL";
    private static final String ELEMENT_S3_ACCESS_KEY = "s3AccessKey";
    private static final String ELEMENT_S3_SECRET_KEY = "s3SecretKey";
    // S3 parameters
    
    @Inject
    private IPhoneServiceBean iPhoneServiceBean;
    
    @Inject
    private KarteServiceBean karteService;

    /** Creates a new instance of DolphinResource */
    public DolphinResource() {
    }

    @GET
    @Path("/user/{param}")
    @Produces("application/xml")
    public String getUser(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(servletReq.getHeader("userName"));
        debug(servletReq.getHeader("password"));

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        // userId, 医療機関ID, パスワード
        String userId = params[0];
        String facilityId = params[1];
        String password = params[2];

        // OID 1.3.6.1.4.1.9414.2.xxx:userId を構築する
        StringBuilder sb = new StringBuilder();
        sb.append(DOLPHIN_ASP_OID);
        sb.append(facilityId);
        sb.append(":");
        sb.append(userId);
        String qid = sb.toString();
        debug(qid);

        // 戻り値のXMLを構築する
        String retXML = null;
        sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        UserModel user = null;

        try {
            // User を検索する
            user = iPhoneServiceBean.getUser(qid, password);
            debug("got user");

            // ASP Member 以外の時は評価期間中かどうかを判定する
            if (user.getMemberType().equals(ASP_TEST_USER)) {

                Date registered = user.getRegisteredDate();
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(registered);
                gc.add(Calendar.MONTH, 5);

                GregorianCalendar now = new GregorianCalendar();

                if (gc.before(now)) {
                    user = null;
                }
            }

        } catch (Exception e) {
            debug("Exception at get user");
            sb.append(RESOURCE_END);
            retXML = sb.toString();
        }

        // 登録なし、認証不可、MEMBER 以外
        if (user == null) {
            debug("user == null, return");
            return retXML;
        }

        // ローカルuserId
        int index = user.getUserId().indexOf(":");
        userId = user.getUserId().substring(index+1);

        sb.append(USER_START);
//minagawa^ VisitTouch
        propertyString(ELEMENT_USER_PK, String.valueOf(user.getId()), sb);                          // PK
//minagawa$        
        propertyString(ELEMENT_USER_ID, userId, sb);                                                // userId
        propertyString(ELEMENT_COMMON_NAME, user.getCommonName(), sb);                              // commonName
//minagawa^ VisitTouch
        propertyString(ELEMENT_LICENSE, user.getLicenseModel().getLicense(), sb);                   // 医療資格
        propertyString(ELEMENT_LICENSE_DESC, user.getLicenseModel().getLicenseDesc(), sb);          // 医療資格名称（説明）
        propertyString(ELEMENT_DEPARTMENT, user.getDepartmentModel().getDepartment(), sb);          // 診療科   01
        propertyString(ELEMENT_DEPARTMENT_DESC, user.getDepartmentModel().getDepartmentDesc(), sb); // 診療科名称   内科
        propertyString(ELEMENT_ORCA_ID, user.getOrcaId(), sb);                                      // ORCA ID
//minagawa$
        sb.append(FACILITY_START);
        propertyString(ELEMENT_FACILITY_ID, user.getFacilityModel().getFacilityId(), sb);           // 施設ID
        propertyString(ELEMENT_FACILITY_NAME, user.getFacilityModel().getFacilityName(), sb);       // 施設名称
//minagawa^ VisitTouch
        propertyString(ELEMENT_FACILITY_ZIP, user.getFacilityModel().getZipCode(), sb);              // 郵便番号
        propertyString(ELEMENT_FACILITY_ADDRESS, user.getFacilityModel().getAddress(), sb);              // 住所
        propertyString(ELEMENT_FACILITY_TELEPHONE, user.getFacilityModel().getTelephone(), sb);            // 電話
        propertyString(ELEMENT_FACILITY_FAX, user.getFacilityModel().getFacsimile(), sb);            // FAC
//minagawa$        

        //-------- s3 params -----------//
        propertyString(ELEMENT_S3_URL, user.getFacilityModel().getS3URL(), sb);
        propertyString(ELEMENT_S3_ACCESS_KEY, user.getFacilityModel().getS3AccessKey(), sb);
        propertyString(ELEMENT_S3_SECRET_KEY, user.getFacilityModel().getS3SecretKey(), sb);
        //-------- s3 params -----------//

        sb.append(FACILITY_END);

        sb.append(USER_END);
        sb.append(RESOURCE_END);

        retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/patient/firstVisitors/{param}")
    @Produces("application/xml")
    public String getFirstVisitors(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        // 医療機関ID、最初の結果、最大件数
        String facilityId = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        // 新患リストを取得する
        List<KarteBean> list = iPhoneServiceBean.getFirstVisitors(facilityId, firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        for (KarteBean karte : list) {

            // ManyToOne で取得されている
            PatientModel patient = karte.getPatientModel();

            sb.append(PATIENT_START);

            propertyString(ELEMENT_PK, String.valueOf(patient.getId()), sb);
            propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(), sb);
            propertyString(ELEMENT_NAME, patient.getFullName(), sb);
            propertyString(ELEMENT_KANA, patient.getKanaName(), sb);

            String sex = sexValueToDesc(patient.getGender());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(), sb);
            propertyString(ELEMENT_FIRST_VISIT, simpleFormat(karte.getCreated()), sb);

            sb.append(PATIENT_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/patient/visit/{param}")
    @Produces("application/xml")
    public String getPatientVisit(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        String facilityId = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        List<PatientVisitModel> list = iPhoneServiceBean.getPatientVisit(facilityId, firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        for (PatientVisitModel visit : list) {

            sb.append(ELEMENT_PATIENT_VISIT_START);
//minagawa^ VisitTouch
            propertyString(ELEMENT_PK, String.valueOf(visit.getId()), sb);          // 受付のPK
//minagawa$            
            propertyString(ELEMENT_PVT_DATE, visit.getPvtDate(), sb);

            PatientModel patient = visit.getPatientModel();

            sb.append(PATIENT_START);

            propertyString(ELEMENT_PK, String.valueOf(patient.getId()), sb);
            propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(), sb);
            propertyString(ELEMENT_NAME, patient.getFullName(), sb);
            propertyString(ELEMENT_KANA, patient.getKanaName(), sb);

            String sex = sexValueToDesc(patient.getGender());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(), sb);
            
            sb.append(PATIENT_END);
            
            sb.append(ELEMENT_PATIENT_VISIT_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/patient/visitRange/{param}")
    @Produces("application/xml")
    public String getPatientVisitRange(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length < 3) {
            debug("params!>=3, return");
            return null;
        }

        // 医療機関ID、検索の開始日と終了日
        String facilityId = params[0];
        String start = params[1];
        String end = params[2];
        start = start.replaceAll(" ", MML_DATE_TIME_SEPARATOR);
        end = end.replaceAll(" ", MML_DATE_TIME_SEPARATOR);

        int firstResult = 0;
        int maxResult = 1000;
        
        if (params.length==5) {
            firstResult = Integer.parseInt(params[3]);
            maxResult = Integer.parseInt(params[4]);
        }

        // start ~ end 間のPVTを検索する
        List<PatientVisitModel> list = iPhoneServiceBean.getPatientVisitRange(facilityId, start, end, firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        for (PatientVisitModel visit : list) {

            sb.append(ELEMENT_PATIENT_VISIT_START);
//minagawa^ VisitTouch
            propertyString(ELEMENT_PK, String.valueOf(visit.getId()), sb);          // 受付のPK
//minagawa$            
            // 来院日
            propertyString(ELEMENT_PVT_DATE, visit.getPvtDate(),sb);
            
            // 診察終了 or Not
            propertyString(ELEMENT_PVT_STATUS, String.valueOf(visit.getState()), sb);
            
//s.oh^ 2013/11/05 iPhone/iPadの受付リストに保険を追加
            propertyString(ELEMENT_PVT_F_INS, String.valueOf(visit.getFirstInsurance()), sb);   // add funabashi 20131103
//s.oh$

            PatientModel patient = visit.getPatientModel();

            sb.append(PATIENT_START);

            propertyString(ELEMENT_PK, String.valueOf(patient.getId()),sb);
            propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(),sb);
            propertyString(ELEMENT_NAME, patient.getFullName(),sb);
            propertyString(ELEMENT_KANA, patient.getKanaName(),sb);

            String sex = sexValueToDesc(patient.getGender());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(),sb);

            sb.append(PATIENT_END);

            sb.append(ELEMENT_PATIENT_VISIT_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    @GET
    @Path("/patient/visitLast/{param}")
    @Produces("application/xml")
    public String getPatientVisitLast(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length != 3) {
            debug("params!=3, return");
            return null;
        }

        // 医療機関ID、検索範囲の開始日、終了日
        String facilityId = params[0];
        String start = params[1];
        String end = params[2];
        start = start.replaceAll(" ", MML_DATE_TIME_SEPARATOR);
        end = end.replaceAll(" ", MML_DATE_TIME_SEPARATOR);

        List<PatientVisitModel> list = iPhoneServiceBean.getPatientVisitRange(facilityId, start, end, 0, 1000);

        // 検索結果が空で前日検索の場合はその日以降で最初に結果のあるものを返す
        int tryCnt = 0;

        while (list.isEmpty() && tryCnt++ < 6) {

            // startの日付を取り出す
            int index = start.indexOf(MML_DATE_TIME_SEPARATOR);
            start = start.substring(0, index);
            Date last = simpleParse(start);

            // それを1日前に戻す
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(last);
            gc.add(Calendar.DAY_OF_MONTH, -1);
            String lastStr = simpleFormat(gc.getTime());

            // 日の始めと終わりにする
            start =  lastStr + "T00:00:00";
            end = lastStr + "T23:59:59";

            // 検索する
            list = iPhoneServiceBean.getPatientVisitRange(facilityId, start, end, 0, 1000);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        for (PatientVisitModel visit : list) {

            sb.append(ELEMENT_PATIENT_VISIT_START);
//minagawa^ VisitTouch
            propertyString(ELEMENT_PK, String.valueOf(visit.getId()), sb);          // 受付のPK
//minagawa$            
            propertyString(ELEMENT_PVT_DATE, visit.getPvtDate(),sb);

            PatientModel patient = visit.getPatientModel();

            sb.append(PATIENT_START);

            propertyString(ELEMENT_PK, String.valueOf(patient.getId()),sb);
            propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(),sb);
            propertyString(ELEMENT_NAME, patient.getFullName(),sb);
            propertyString(ELEMENT_KANA, patient.getKanaName(),sb);

            String sex = sexValueToDesc(patient.getGender());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(),sb);

            sb.append(PATIENT_END);

            sb.append(ELEMENT_PATIENT_VISIT_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/patient/{pk}")
    @Produces("application/xml")
    public String getPatientById(@PathParam("pk") String pk) {

        // PK
        Long id = Long.parseLong(pk);

        PatientModel patient = iPhoneServiceBean.getPatient(id);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (patient == null) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        sb.append(PATIENT_START);

        propertyString(ELEMENT_PK, String.valueOf(patient.getId()),sb);
        propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(),sb);
        propertyString(ELEMENT_NAME, patient.getFullName(),sb);
        propertyString(ELEMENT_KANA, patient.getKanaName(),sb);

        String sex = sexValueToDesc(patient.getGender());
        propertyString(ELEMENT_SEX, sex, sb);

        propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(), sb);

        if (patient.getSimpleAddressModel()!=null) {
            sb.append(ELEMENT_ADDRESS_START);
            propertyString(ELEMENT_ZIP_CODE, patient.getSimpleAddressModel().getZipCode(), sb);
            propertyString(ELEMENT_FULL_ADDRESS, patient.getSimpleAddressModel().getAddress(), sb);
            sb.append(ELEMENT_ADDRESS_END);
        }

        propertyString(ELEMENT_TELEPHONE, patient.getTelephone(), sb);
        propertyString(ELEMENT_MOBILE_PHONE, patient.getMobilePhone(), sb);
        propertyString(ELEMENT_E_MAIL, patient.getEmail(), sb);

        sb.append(PATIENT_END);
        
        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/patientPackage/{pk}")
    @Produces("application/xml")
    public String getPatientPackage(@PathParam("pk") String pk) {

        // PK
        Long id = Long.parseLong(pk);

        PatientPackage pack = iPhoneServiceBean.getPatientPackage(id);

        PatientModel patient = pack.getPatient();

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (patient == null) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        sb.append("<patientPackage>");

        // 患者情報
        sb.append(PATIENT_START);

        propertyString(ELEMENT_PK, String.valueOf(patient.getId()),sb);
        propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(),sb);
        propertyString(ELEMENT_NAME, patient.getFullName(),sb);
        propertyString(ELEMENT_KANA, patient.getKanaName(),sb);

        String sex = sexValueToDesc(patient.getGender());
        propertyString(ELEMENT_SEX, sex, sb);

        propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(), sb);

        if (patient.getSimpleAddressModel()!=null) {
            sb.append(ELEMENT_ADDRESS_START);
            propertyString(ELEMENT_ZIP_CODE,patient.getSimpleAddressModel().getZipCode(), sb);
            propertyString(ELEMENT_FULL_ADDRESS, patient.getSimpleAddressModel().getAddress(), sb);
            sb.append(ELEMENT_ADDRESS_END);
        }

        propertyString(ELEMENT_TELEPHONE, patient.getTelephone(), sb);
        propertyString(ELEMENT_MOBILE_PHONE, patient.getMobilePhone(), sb);
        propertyString(ELEMENT_E_MAIL, patient.getEmail(), sb);
//s.oh^ 2014/08/29 患者情報の追加
        propertyString("reserve1", patient.getReserve1(), sb);
        propertyString("reserve2", patient.getReserve2(), sb);
        propertyString("reserve3", patient.getReserve3(), sb);
        propertyString("reserve4", patient.getReserve4(), sb);
        propertyString("reserve5", patient.getReserve5(), sb);
        propertyString("reserve6", patient.getReserve6(), sb);
//s.oh$
        sb.append(PATIENT_END);


        // 健康保険
        // Health Insurance を変換をする beanXML2PVT
        List<HealthInsuranceModel> c = pack.getInsurances();

        for (HealthInsuranceModel model : c) {

            try {
                // byte[] を XMLDecord
                PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel)xmlDecode(model.getBeanBytes());
                PVTPublicInsuranceItemModel[] publicItems = hModel.getPVTPublicInsuranceItem();

                sb.append("<healthInsurance>");
                propertyString("insuranceClass", hModel.getInsuranceClass(),sb);
                propertyString("insuranceClassCode", hModel.getInsuranceClassCode(),sb);
                propertyString("insuranceClassCodeSys", hModel.getInsuranceClassCodeSys(),sb);
                propertyString("insuranceNumber", hModel.getInsuranceNumber(),sb);
                propertyString("clientGroup", hModel.getClientGroup(),sb);
                propertyString("clientNumber", hModel.getClientNumber(),sb);
                propertyString("familyClass", hModel.getFamilyClass(),sb);
                propertyString("startDate", hModel.getStartDate(),sb);
                propertyString("expiredDate", hModel.getExpiredDate(),sb);
                propertyString("payInRatio", hModel.getPayInRatio(),sb);
                propertyString("payOutRatio", hModel.getPayOutRatio(),sb);

                if (publicItems != null && publicItems.length > 0) {
                    for (PVTPublicInsuranceItemModel pb : publicItems) {
                        sb.append("<publicInsurance>");
                        propertyString("priority", pb.getPriority(),sb);
                        propertyString("providerName", pb.getProviderName(),sb);
                        propertyString("provider", pb.getProvider(),sb);
                        propertyString("recipient", pb.getRecipient(),sb);
                        propertyString("startDate", pb.getStartDate(),sb);
                        propertyString("expiredDate", pb.getExpiredDate(),sb);
                        propertyString("paymentRatio", pb.getPaymentRatio(),sb);
                        propertyString("paymentRatioType", pb.getPaymentRatioType(),sb);
                        sb.append("</publicInsurance>");
                    }
                }
                sb.append("</healthInsurance>");

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }


        // Allergy
        List<AllergyModel> list = pack.getAllergies();

        for (AllergyModel allergy : list) {

            sb.append("<allergy>");
            propertyString("factor", allergy.getFactor(), sb);
            propertyString("severity", allergy.getSeverity(), sb);
            propertyString("identifiedDate", allergy.getIdentifiedDate(), sb);
            sb.append("</allergy>");
        }

        sb.append("</patientPackage>");

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    
    @GET
    @Path("/patients/name/{param}")
    @Produces("application/xml")
    public String getPatientsByName(@PathParam("param") String param) {

        String [] params = param.split(",");
        if (params.length !=4) {
            debug("params!=4, return");
            return null;
        }

        String facilityId = params[0];
        String name = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);

        List<PatientModel> list;

        // ひらがなで始まっている場合はカナに変換する
        if (KanjiHelper.isHiragana(name.charAt(0))) {
            name = KanjiHelper.hiraganaToKatakana(name);
        }

        if (KanjiHelper.isKatakana(name.charAt(0))) {
            list = iPhoneServiceBean.getPatientsByKana(facilityId, name, firstResult, maxResult);

        } else {
            // 漢字で検索
            list = iPhoneServiceBean.getPatientsByName(facilityId, name, firstResult, maxResult);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        for (PatientModel patient : list) {

            sb.append(PATIENT_START);

            propertyString(ELEMENT_PK, String.valueOf(patient.getId()), sb);
            propertyString(ELEMENT_PATIENT_ID, patient.getPatientId(), sb);
            propertyString(ELEMENT_NAME, patient.getFullName(), sb);
            propertyString(ELEMENT_KANA, patient.getKanaName(), sb);

            String sex = sexValueToDesc(patient.getGender());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, patient.getBirthday(), sb);

            sb.append(PATIENT_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    @GET
    @Path("/module/rp/{param}")
    @Produces("application/xml")
    public String getRp(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        long pk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);
        String entity = ENTITY_MED_ORDER;

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (firstResult == 0) {
            Long count = iPhoneServiceBean.getModuleCount(pk, entity);
            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        List<ModuleModel> retList = iPhoneServiceBean.getModules(pk, entity, firstResult, maxResult);

        if (retList==null || retList.isEmpty()) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        for (ModuleModel module : retList) {

            module.setModel((InfoModel)xmlDecode(module.getBeanBytes()));

            BundleMed med = (BundleMed)module.getModel();
            ClaimItem[] items = med.getClaimItem();
            
            sb.append(ELEMENT_BUNDLE_MED_START);
            propertyString(ELEMENT_RP_DATE, simpleFormat(module.getStarted()), sb);

            for (ClaimItem item : items) {
                sb.append(ELEMENT_CLAIM_ITEM_START);
                propertyString(ELEMENT_CLAIM_ITEM_NAME, item.getName(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_QUANTITY, item.getNumber(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_UNIT, item.getUnit(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_NUM_DAYS, med.getBundleNumber(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_ADMINI, med.getAdmin(), sb);
                sb.append(ELEMENT_CLAIM_ITEM_END);
            }

            sb.append(ELEMENT_BUNDLE_MED_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/module/{param}")
    @Produces("application/xml")
    public String getModule(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=4) {
            debug("params!=4, return");
            return null;
        }

        long pk = Long.parseLong(params[0]);
        String entity = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);
        
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (firstResult == 0) {
            Long count = iPhoneServiceBean.getModuleCount(pk, entity);
            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        List<ModuleModel> retList = iPhoneServiceBean.getModules(pk, entity, firstResult, maxResult);

        if (retList==null || retList.isEmpty()) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        for (ModuleModel module : retList) {

            module.setModel((InfoModel)xmlDecode(module.getBeanBytes()));

            BundleDolphin bundle = (BundleDolphin) module.getModel();
            bundle.setOrderName(module.getModuleInfoBean().getEntity());
            ClaimItem[] items = bundle.getClaimItem();

            entity = bundle.getOrderName();
            boolean orderIsRp = (entity != null && entity.equals(ENTITY_MED_ORDER)) ? true : false;

            String bundleNumber = bundle.getBundleNumber();
            String admin = orderIsRp ? bundle.getAdmin() : null;

            sb.append(ELEMENT_MODULE_START);

            // order|entity Name
            propertyString(ELEMENT_ENTITY, entity, sb);
            propertyString(ELEMENT_NAME, entityToName(entity), sb);
            propertyString(ELEMENT_START_DATE, simpleFormat(module.getStarted()), sb);

            // ClaimItem
            for (ClaimItem cl : items) {

                sb.append(ELEMENT_CLAIM_ITEM_START);

                propertyString(ELEMENT_NAME, cl.getName(), sb);         // 名称
                propertyString(ELEMENT_QUANTITY, cl.getNumber(), sb);   // 数量
                propertyString(ELEMENT_UNIT, cl.getUnit(), sb);         // 単位

                if (orderIsRp) {
                    propertyString(ELEMENT_CLAIM_ITEM_NUM_DAYS, bundleNumber, sb);  // 日数／回数
                    propertyString(ELEMENT_CLAIM_ITEM_ADMINI, admin, sb);           // 用法
                }

                sb.append(ELEMENT_CLAIM_ITEM_END);
            }

            sb.append(ELEMENT_MODULE_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    @GET
    @Path("/module/laboTest/{param}")
    @Produces("application/xml")
    public String getLaboTest(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length != 4) {
            debug("params!=4, return");
            return null;
        }
        String facilityId = params[0];
        String patientId = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (firstResult == 0) {
            Long count = iPhoneServiceBean.getLabTestCount(facilityId, patientId);
            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        List<NLaboModule> list = iPhoneServiceBean.getLaboTest(facilityId, patientId, firstResult, maxResult);

        for (NLaboModule module : list) {

            sb.append(ELEMENT_MODULE_START);
            propertyString(ELEMENT_LABO_CODE, module.getLaboCenterCode(), sb);
            propertyString(ELEMENT_SAMPLE_DATE, module.getSampleDate(), sb);
            propertyString(ELEMENT_PATIENT_ID, module.getPatientId(), sb);

            Collection<NLaboItem> items = module.getItems();

            // LaboItemをイテレートする
            for (NLaboItem item : items) {

                // <laboItem groupCode=...... />
                sb.append(ELEMENT_LAB_ITEM_START);

                // グループコード
                propertyString(ELEMENT_GROUP_CODE, item.getGroupCode(), sb);

                // グループ名称
                propertyString(ELEMENT_GROUP_NAME, item.getGroupName(), sb);

                // 親コード
                propertyString(ELEMENT_PARENT_CODE, item.getParentCode(), sb);

                // 検査項目コード
                propertyString(ELEMENT_ITEM_CODE, item.getItemCode(), sb);

                // MEDIS
                propertyString(ELEMENT_ITEM_MEDIS_CODE, item.getMedisCode(), sb);

                // 検査項目名
                propertyString(ELEMENT_ITEM_NAME, item.getItemName(), sb);

                // 基準値
                propertyString(ELEMENT_NORMAL_VALUE, item.getNormalValue(), sb);

                // 単位
                propertyString(ELEMENT_UNIT, item.getUnit(), sb);

                // 検査結果
                propertyString(ELEMENT_VALUE, item.getValue(), sb);

                // 異常値フラグ
                propertyString(ELEMENT_OUT_FLAG, item.getAbnormalFlg(), sb);

                // コメント1
                propertyString(ELEMENT_COMMENT_1, item.getComment1(), sb);

                // コメント2
                propertyString(ELEMENT_COMMENT_2, item.getComment2(), sb);

                sb.append(ELEMENT_LAB_ITEM_END);

            }

            sb.append(ELEMENT_MODULE_END);
        }

        sb.append(RESOURCE_END);

        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }


    @GET
    @Path("/item/laboItem/{param}")
    @Produces("application/xml")
    public String getLaboGraph(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length != 5) {
            debug("params!=5, return");
            return null;
        }
        String facilityId = params[0];
        String patientId = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);
        String itemCode = params[4];

        // 結果を XML にエンコードする
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        List<NLaboItem> list  = iPhoneServiceBean.getLaboTestItem(facilityId, patientId, firstResult, maxResult, itemCode);

        int cnt = list.size();

        if (cnt == 0) {
            sb.append(RESOURCE_END);
            String ret = sb.toString();
            debug(ret);
            return ret;
        }

        // この検査項目の共通情報を出力する
        NLaboItem item = list.get(cnt-1);

        // TestItem
        sb.append(ELEMENT_TEST_ITEM_START);

        // 検査項目コード
        propertyString(ELEMENT_ITEM_CODE, item.getItemCode(), sb);

        // 検査項目名
        propertyString(ELEMENT_ITEM_NAME, item.getItemName(), sb);

        // 基準値
        propertyString(ELEMENT_NORMAL_VALUE, item.getNormalValue(), sb);

        // 単位
        propertyString(ELEMENT_UNIT, item.getUnit(), sb);

        // sampleDate の逆順で結果データを出力する
        for (int k = 0; k < cnt; k++) {

            item = list.get(k);

            sb.append(ELEMENT_RESULT_START);

            // sampleDate
            propertyString(ELEMENT_SAMPLE_DATE, item.getSampleDate(), sb);

            // value
            propertyString(ELEMENT_VALUE, item.getValue(), sb);

            // comment1
            propertyString(ELEMENT_COMMENT_1, item.getComment1(), sb);

            // comment2
            propertyString(ELEMENT_COMMENT_2, item.getComment1(), sb);

            sb.append(ELEMENT_RESULT_END);
        }

        sb.append(ELEMENT_TEST_ITEM_END);

        sb.append(RESOURCE_END);
        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    @GET
    @Path("/module/diagnosis/{param}")
    @Produces("application/xml")
    public String getDiagnosis(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        long pk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        // 結果を XML にエンコードする
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (firstResult == 0) {
            Long count = iPhoneServiceBean.getDiagnosisCount(pk);
            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        List<RegisteredDiagnosisModel> list = iPhoneServiceBean.getDiagnosis(pk, firstResult, maxResult);

        int cnt = list.size();

        if (cnt == 0) {
            sb.append(RESOURCE_END);
            String ret = sb.toString();
            debug(ret);
            return ret;
        }

        for (RegisteredDiagnosisModel model : list) {

            sb.append(ELEMENT_DIAGNOSIS_START);

            propertyString(ELEMENT_DIAGNOSIS, model.getAliasOrName(), sb);

            propertyString(ELEMENT_CATEGORY, model.getCategoryDesc(), sb);

            propertyString(ELEMENT_OUTCOME, model.getOutcomeDesc(), sb);

            propertyString(ELEMENT_START_DATE, model.getStartDate(), sb);

            propertyString(ELEMENT_END_DATE, model.getEndDate(), sb);

            sb.append(ELEMENT_DIGNOSIS_END);
        }

        sb.append(RESOURCE_END);
        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }

    @GET
    @Path("/module/schema/{param}")
    @Produces("application/xml")
    public String getSchema(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length !=3) {
            debug("params!=3, return");
            return null;
        }

        long pk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        // 結果を XML にエンコードする
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        List<SchemaModel> list = iPhoneServiceBean.getSchema(pk, firstResult, maxResult);

        int cnt = list.size();

        if (cnt == 0) {
            sb.append(RESOURCE_END);
            String ret = sb.toString();
            debug(ret);
            return ret;
        }
        
        String retXML = null;

        try {

            for (SchemaModel schema : list) {

                sb.append(ELEMENT_SCHEMA_START);

                // bucket
                if (schema.getExtRefModel().getBucket()!=null) {
                    propertyString(ELEMENT_BUCKET, schema.getExtRefModel().getBucket(), sb);
                }

                // sop
                if (schema.getExtRefModel().getSop()!=null) {
                    propertyString(ELEMENT_SOP, schema.getExtRefModel().getSop(), sb);
                }

                byte[] bytes = schema.getJpegByte();
                String base64Str = new String(Base64Utils.encode(bytes));
                propertyString(ELEMENT_BASE64, base64Str, sb);

                sb.append(ELEMENT_SCHEMA_END);
            }

            sb.append(RESOURCE_END);
            retXML = sb.toString();
            debug(retXML);

        } catch (Exception e) {

        }

        return retXML;
    }

    @GET
    @Path("/document/progressCourse/{param}")
    @Produces("application/xml")
    public String getProgressCource(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length != 3) {
            debug("params!=3, return");
            return null;
        }

        long patientPk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        // 全件数を取得する
        if (firstResult == 0) {
            Long count = iPhoneServiceBean.getDocumentCount(patientPk);
            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        // 検索する
        List<DocumentModel> list = iPhoneServiceBean.getDocuments(patientPk, firstResult, maxResult);

        for (DocumentModel doc : list) {

            sb.append(ELEMENT_DOCUMENT_START);
            
//minagawa^ VisitTouch
            propertyString(ELEMENT_PK, String.valueOf(doc.getId()), sb);          // DocumentのPK
//minagawa$            
            // 確定日、記載者
            Date started = doc.getStarted();
            UserModel u = doc.getUserModel();
            propertyString("started", simpleFormat(started), sb);
            propertyString("responsibility", u.getCommonName(), sb);

            List<ModuleModel> soaModules = new ArrayList<ModuleModel>();
            List<BundleDolphin> bundles = new ArrayList<BundleDolphin>();
            String soaSpec = null;
            String pSpec = null;

            Collection<ModuleModel> modules = doc.getModules();

            for (ModuleModel bean : modules) {

                bean.setModel((InfoModel) xmlDecode(bean.getBeanBytes()));

                String role = bean.getModuleInfoBean().getStampRole();

                if (role.equals(IInfoModel.ROLE_SOA)) {
                    soaModules.add(bean);

                } else if (role.equals(IInfoModel.ROLE_SOA_SPEC)) {
                    soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();

                } else if (role.equals(IInfoModel.ROLE_P)) {
                    BundleDolphin b = (BundleDolphin) bean.getModel();
                    b.setOrderName(bean.getModuleInfoBean().getEntity());
                    bundles.add(b);

                } else if (role.equals(IInfoModel.ROLE_P_SPEC)) {
                    pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                } 
                //-------------------------------------------------------
                // goody
                else if (bean.getModel() instanceof ProgressCourse) {
                    if (soaSpec==null) {
                        soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    } else if (pSpec==null) {
                        pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    }

                } else {
                    BundleDolphin b = (BundleDolphin) bean.getModel();
                    b.setOrderName(bean.getModuleInfoBean().getEntity());
                    bundles.add(b);
                }
                //--------------------------------------------------------
            }

            if (soaSpec != null && pSpec != null) {
                int index = soaSpec.indexOf(NAME_STAMP_HOLDER);
                if (index > 0) {
                    String sTmp = soaSpec;
                    String pTmp = pSpec;
                    soaSpec = pTmp;
                    pSpec = sTmp;
                }
            }

            // soa text
            if (soaSpec != null && (!soaSpec.equals(""))) {
                sb.append(ELEMENT_MODULE_START);
                propertyString(ELEMENT_ENTITY, "progressCourse", sb);
                sb.append(ELEMENT_TEXT_ITEM_START);
                propertyString(ELEMENT_NAME, "soaText", sb);
                StringBuilder text = new StringBuilder();
                renderPane(text, soaSpec);
                propertyString(ELEMENT_VALUE, text.toString(), sb);
                sb.append(ELEMENT_TEXT_ITEM_END);
                sb.append(ELEMENT_MODULE_END);
            }

            // schema
            Collection<SchemaModel> images = doc.getSchema();

            try {

                for (SchemaModel schema : images) {

                    sb.append(ELEMENT_SCHEMA_START);

                    // bucket
                    if (schema.getExtRefModel().getBucket()!=null) {
                        propertyString(ELEMENT_BUCKET, schema.getExtRefModel().getBucket(), sb);
                    }

                    // sop
                    if (schema.getExtRefModel().getSop()!=null) {
                        propertyString(ELEMENT_SOP, schema.getExtRefModel().getSop(), sb);
                    }

                    // image bytes
                    byte[] bytes = schema.getJpegByte();
                    String base64Str = new String(Base64Utils.encode(bytes));
                    propertyString(ELEMENT_BASE64, base64Str, sb);

                    sb.append(ELEMENT_SCHEMA_END);
                }

            } catch (Exception e) {

            }

//            if (pSpec != null) {
//                sb.append("<module>");
//                propertyString("entity", "progressCourse"));
//                sb.append("<textItem>");
//                propertyString("name", "pText"));
//                StringBuilder text = new StringBuilder();
//                renderPane(text, pSpec);
//                propertyString("value", text.toString()));
//                sb.append("</textItem>");
//                sb.append("</module>");
//            }

            // p modules
            for (BundleDolphin pmodule : bundles) {

                String entity = pmodule.getOrderName();
                boolean orderIsRp = (entity != null && entity.equals(ENTITY_MED_ORDER)) ? true : false;

                String bundleNumber = pmodule.getBundleNumber();
                String admin = orderIsRp ? pmodule.getAdmin() : null;

                sb.append(ELEMENT_MODULE_START);

                // order|entity Name
                propertyString(ELEMENT_ENTITY, entity, sb);
                propertyString(ELEMENT_NAME, entityToName(entity), sb);

                // ClaimItem
                ClaimItem[] items = pmodule.getClaimItem();

                for (ClaimItem cl : items) {

                    sb.append(ELEMENT_CLAIM_ITEM_START);

                    propertyString(ELEMENT_NAME, cl.getName(), sb);         // 名称
                    propertyString(ELEMENT_QUANTITY, cl.getNumber(), sb);   // 数量
                    propertyString(ELEMENT_UNIT, cl.getUnit(), sb);         // 単位

                    if (orderIsRp) {
                        propertyString(ELEMENT_CLAIM_ITEM_NUM_DAYS, bundleNumber, sb);  // 日数／回数
                        propertyString(ELEMENT_CLAIM_ITEM_ADMINI, admin, sb);           // 用法
                    }

                    sb.append(ELEMENT_CLAIM_ITEM_END);
                }

                sb.append(ELEMENT_MODULE_END);
            }

            sb.append(ELEMENT_DOCUMENT_END);
        }

        sb.append(RESOURCE_END);
        String retXML = sb.toString();
        debug(retXML);

        return retXML;
    }
    
    @GET
    @Path("/stampTree/{param}")
    @Produces("application/json")
    public String getStampTree(@PathParam("param") String param) {
        
        long pk = Long.parseLong(param);
        IStampTreeModel treeModel = iPhoneServiceBean.getTrees(pk);
        
        try {
            String treeXml = new String(treeModel.getTreeBytes(), "UTF-8");
            BufferedReader reader = new BufferedReader(new StringReader(treeXml));
            JSONStampTreeBuilder builder = new JSONStampTreeBuilder();
            StampTreeDirector director = new StampTreeDirector(builder);
            String json = director.build(reader);
            reader.close();
            //System.err.println(json);
            return json;
            
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        }
        
        return null;
    }
    
    @GET
    @Path("/stamp/{param}")
    @Produces("application/json")
    public String getStamp(@PathParam("param") String param) {
        
        StampModel stampModel = iPhoneServiceBean.getStamp(param);
        
        if (stampModel!=null) {
            XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(stampModel.getStampBytes())));
            InfoModel model = (InfoModel)d.readObject();
            JSONStampBuilder builder = new JSONStampBuilder();
            String json = builder.build(model);
            return json;
        }
        
        return null;
    }
    
    //--------------------------------------------------------------------------
    
    @POST
    @Path("/idocument")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(String json) throws IOException {
        
        // JSON to IDocument
        ObjectMapper mapper = new ObjectMapper();
        IDocument document = mapper.readValue(json, IDocument.class);
        
        // IDocument to DocumentModel
        DocumentModel model = document.toModel();
        
        // 追加
        long pk = karteService.addDocument(model);
        
        // pkを返却
        return String.valueOf(pk);
    }
    
    // S.Oh 2014/02/06 iPadのFreeText対応 Add Start
    @POST
    @Path("/idocument2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument2(String json) throws IOException {
        
        // JSON to IDocument2
        ObjectMapper mapper = new ObjectMapper();
        IDocument2 document = mapper.readValue(json, IDocument2.class);
        
        // IDocument to DocumentModel
        DocumentModel model = document.toModel();
        
        // 追加
        long pk = karteService.addDocument(model);
        
        // pkを返却
        return String.valueOf(pk);
    }
    // S.Oh 2014/02/06 Add End
    
//    @POST
//    @Path("/priscription")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public String postPriscription(String json) throws IOException {
//        
//        // JSON to IPriscription
//        ObjectMapper mapper = new ObjectMapper();
//        IPriscription document = mapper.readValue(json, IPriscription.class);
//        
//        // IPriscriptionModel to PriscriptionModel
//        PriscriptionModel model = document.toModel();
//        
//        // create PDF
//        ServerPrescriptionPDFMaker maker = new ServerPrescriptionPDFMaker(model);
//        String filename = maker.output();
//        
//        return filename;
//    }
}