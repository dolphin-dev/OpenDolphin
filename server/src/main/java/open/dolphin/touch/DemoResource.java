package open.dolphin.touch;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import open.dolphin.infomodel.*;
import open.dolphin.touch.session.IPhoneServiceBean;

/**
 * REST Web Service
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */

@Path("/demo")
public class DemoResource extends AbstractResource {

    private static final String ELEMENT_PATIENT_VISIT_START = "<patientVisit>";
    private static final String ELEMENT_PATIENT_VISIT_END = "</patientVisit>";
    private static final String ELEMENT_PVT_DATE = "pvtDate";
    private static final String ELEMENT_PVT_STATUS = "pvtStatus";

    private static final String ELEMENT_ADDRESS_START = "<address>";
    private static final String ELEMENT_ADDRESS_END = "</address>";
    private static final String ELEMENT_ZIP_CODE = "zipCode";
    private static final String ELEMENT_FULL_ADDRESS = "fullAddress";
    private static final String ELEMENT_TELEPHONE = "telephone";
    private static final String ELEMENT_MOBILE_PHONE = "mobilePhone";
    private static final String ELEMENT_E_MAIL = "email";

    private static final String ELEMENT_BUNDLE_MED_START = "<bundleMed>";
    private static final String ELEMENT_BUNDLE_MED_END = "</bundleMed>";

    //private static final String ELEMENT_BUNDLE_START = "<bundle>";
    //private static final String ELEMENT_BUNDLE_END = "</bundle>";
    //private static final String ELEMENT_BUNDLE_NAME = "name";
    //private static final String ELEMENT_BUNDLE_ENTITY = "entity";
    //private static final String ELEMENT_BUNDLE_NUMBER = "bundleNumber";

    //private static final String ELEMENT_CLAIM_ITEM_START = "<claimItem>";
    //private static final String ELEMENT_CLAIM_ITEM_END = "</claimItem>";
    private static final String ELEMENT_RP_DATE = "rpDate";
    private static final String ELEMENT_CLAIM_ITEM_NAME = "name";
    private static final String ELEMENT_CLAIM_ITEM_QUANTITY = "quantity";
    private static final String ELEMENT_CLAIM_ITEM_UNIT = "unit";
    private static final String ELEMENT_CLAIM_ITEM_NUM_DAYS = "numDays";
    private static final String ELEMENT_CLAIM_ITEM_ADMINI = "administration";

    //private static final String ELEMENT_MODULE_START = "<module>";
   // private static final String ELEMENT_MODULE_END = "</module>";

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
    private static final String ELEMENT_END_DATE = "endDate";

    private static final String ELEMENT_SCHEMA_START = "<schema>";
    private static final String ELEMENT_BASE64 = "base64";
    private static final String ELEMENT_SCHEMA_END = "</schema>";

    private static final String MML_DATE_TIME_SEPARATOR = "T";

    /*private static final String TEST_FACILITY_ID = "2.100";
    private static final String TEST_FACILITY_NAME = "EHR クリニック";
    private static final String TEST_USER_ID = "ehrTouch";
    private static final String TEST_USER_NAME = "EHR";
    private static final String TEST_PASSWORD = "098f6bcd4621d373cade4e832627b4f6";
    private static final String TEST_MEMBER_TYPE = "touchTester";
    private static final String ELEMENT_MEMBER_TYPE = "memberType";
    private static final String SYLK_FACILITY_ID = "1.3.6.1.4.1.9414.2.100";
    private static final String TEST_PATIENT_PK1 = "33809";
    private static final String TEST_PATIENT_PK2 = "33813";
    private static final String TEST_PATIENT_PK3 = "33817";
    private static final String TEST_PATIENT_PK4 = "33821";
    private static final String TEST_PATIENT_PK5 = "33826";
    private static final String TEST_DEMO_FACILITY_ID = "1.3.6.1.4.1.9414.2.1";
    private static final String TEST_DEMO_PATIENT_ID = "00001";*/

    private static final String TEST_FACILITY_ID = "2.100";
    private static final String TEST_FACILITY_NAME = "DolphinProクリニック";
    private static final String TEST_USER_ID = "dolphin";
    private static final String TEST_USER_NAME = "EHR";
    private static final String TEST_PASSWORD = "098f6bcd4621d373cade4e832627b4f6";
    private static final String TEST_MEMBER_TYPE = "touchTester";
    private static final String ELEMENT_MEMBER_TYPE = "memberType";
    private static final String SYLK_FACILITY_ID = "1.3.6.1.4.1.9414.2.100";
    private static final String TEST_PATIENT_PK1 = "26";
    private static final String TEST_PATIENT_PK2 = "29";
    private static final String TEST_PATIENT_PK3 = "18";
    private static final String TEST_PATIENT_PK4 = "312";
    private static final String TEST_PATIENT_PK5 = "71";
    private static final String TEST_DEMO_FACILITY_ID = "1.3.6.1.4.1.9414.70.2";
    private static final String TEST_DEMO_PATIENT_ID = "D_00002";

    //private static int FIRST_RESULT = 0;
    
    @Inject
    private IPhoneServiceBean iPhoneServiceBean;


    /** Creates a new instance of DolphinResource */
    public DemoResource() {
    }

    @GET
    @Path("/user/{param}")
    @Produces("application/xml")
    public String getUser(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length < 3) {
            debug("params < 3, return");
            return null;
        }

        String userId = params[0];
        String facilityId = params[1];
        String password = params[2];
        System.err.println(userId);
        System.err.println(facilityId);
        System.err.println(password);
        boolean pad = params.length == 4 && params[3].equals("pad") ? true : false;

        String retXML;
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        // 2.1 ehrTouch test の組
        if ( (!facilityId.equals(TEST_FACILITY_ID)) ||
                (!userId.equals(TEST_USER_ID)) ||
                (!password.equals(TEST_PASSWORD)) ) {

            sb.append(RESOURCE_END);
            retXML = sb.toString();
            System.err.println(retXML);
            return retXML;
        }

        sb.append(USER_START);
        propertyString(ELEMENT_USER_ID, userId, sb);
        propertyString(ELEMENT_COMMON_NAME, TEST_USER_NAME, sb);
        propertyString(ELEMENT_MEMBER_TYPE, TEST_MEMBER_TYPE, sb);

        sb.append(FACILITY_START);

        if (pad) {
            propertyString(ELEMENT_FACILITY_ID, SYLK_FACILITY_ID, sb);      // 2.1 をデモに使用する
        } else {
            propertyString(ELEMENT_FACILITY_ID, TEST_FACILITY_ID, sb);
        }

        propertyString(ELEMENT_FACILITY_NAME, TEST_FACILITY_NAME, sb);
        sb.append(FACILITY_END);

        sb.append(USER_END);
        sb.append(RESOURCE_END);

        retXML = sb.toString();
        System.err.println(retXML);

        return retXML;
    }

    @GET
    @Path("/patient/firstVisitors/{param}")
    @Produces("application/xml")
    public String getFirstVisitors(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length < 3) {
            debug("params!=3, return");
            return null;
        }

        String facilityId = params[0];
        //int firstResult = Integer.parseInt(params[1]);
        //int maxResult = Integer.parseInt(params[2]);
        boolean pad = params.length == 4 && params[3].equals("pad") ? true : false;

        // 新患リストは 150 が firstResult
        int firstResult = 150;
        int maxResult = 50;

        List<DemoPatient> list = iPhoneServiceBean.getFirstVisitorsDemo(firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        GregorianCalendar gc = new GregorianCalendar();

        int cnt = 0;
        int cicle = 5;
        String pk = null;
        String patientId = null;

        for (DemoPatient patient : list) {

            sb.append(PATIENT_START);

            if (pad) {

                int mod = cnt % cicle;
                cnt++;

                switch (mod) {

                    case 0:
                        pk = TEST_PATIENT_PK1;
                        patientId = "00001";
                        break;

                    case 1:
                        pk = TEST_PATIENT_PK2;
                        patientId = "00002";
                        break;

                    case 2:
                        pk = TEST_PATIENT_PK3;
                        patientId = "00003";
                        break;

                    case 3:
                        pk = TEST_PATIENT_PK4;
                        patientId = "00004";
                        break;

                    case 4:
                        pk = TEST_PATIENT_PK5;
                        patientId = "00005";
                        break;
                }

            } else {

                pk = String.valueOf(patient.getId());
                switch (pk.length()) {
                    case 1:
                        patientId = "00000" + pk;
                        break;

                    case 2:
                        patientId = "0000" + pk;
                        break;

                    case 3:
                        patientId = "000" + pk;
                        break;
                }
            }

            propertyString(ELEMENT_PK, pk, sb);
            propertyString(ELEMENT_PATIENT_ID, patientId, sb);
            propertyString(ELEMENT_NAME, patient.getName(), sb);
            propertyString(ELEMENT_KANA, patient.getKana(), sb);

            String sex = sexValueToDesc(patient.getSex());
            propertyString(ELEMENT_SEX, sex, sb);
            propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()), sb);

            gc.add(Calendar.DAY_OF_MONTH, -1);
            propertyString(ELEMENT_FIRST_VISIT, simpleFormat(gc.getTime()), sb);

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
        //int firstResult = Integer.parseInt(params[1]);
        //int maxResult = Integer.parseInt(params[2]);

        // 直近の来院
        int firstResult = 0;
        int maxResult = 30;

        List<DemoPatient> list = iPhoneServiceBean.getPatientVisitDemo(firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        Date now = new Date();
        String nowStr = simpleFormat(now);

        for (DemoPatient patient : list) {

            sb.append(ELEMENT_PATIENT_VISIT_START);
            propertyString(ELEMENT_PVT_DATE, nowStr, sb);

            sb.append(PATIENT_START);

            String id = String.valueOf(patient.getId());
            String patientId = null;

            switch (id.length()) {
                case 1:
                    patientId = "00000" + id;
                    break;

                case 2:
                    patientId = "0000" + id;
                    break;

                case 3:
                    patientId = "000" + id;
                    break;
            }
            propertyString(ELEMENT_PK, id, sb);
            propertyString(ELEMENT_PATIENT_ID, patientId, sb);

            propertyString(ELEMENT_NAME, patient.getName(), sb);
            propertyString(ELEMENT_KANA, patient.getKana(), sb);

            String sex = sexValueToDesc(patient.getSex());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()), sb);

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
            debug("params!=3, return");
            return null;
        }

        String facilityId = params[0];
        String start = params[1];
        String end = params[2];

        // 無条件に設定
        int firstResult = 0;
        int maxResult = 60;
        boolean pad = false;

        if (params.length==6) {
            firstResult = Integer.parseInt(params[3]);
            //maxResult = Integer.parseInt(params[4]);
            maxResult = firstResult == 0 ? maxResult : 1;   // refresh
            pad = params[5].equals("pad") ? true : false;
        }

        start = start.replaceAll(" ", MML_DATE_TIME_SEPARATOR);
        //end = end.replaceAll(" ", MML_DATE_TIME_SEPARATOR);

        // getPatientVisitRangeDemo
        List<DemoPatient> list = iPhoneServiceBean.getPatientVisitRangeDemo(firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        int index = start.indexOf(MML_DATE_TIME_SEPARATOR);
        String dummyDate = start.substring(0, index);
        int dummyH = 9;
        int dummyM = 0;
        String hStr;
        String mStr;
        StringBuilder timeSb;
        String done;

        int cnt = 0;
        int cicle = 5;
        String pk = null;
        String patientId = null;

        for (DemoPatient patient : list) {

            timeSb = new StringBuilder();
            timeSb.append(dummyDate);
            timeSb.append(MML_DATE_TIME_SEPARATOR);

            hStr = String.valueOf(dummyH);
            if (hStr.length()==1) {
                timeSb.append("0");
            }
            timeSb.append(hStr);

            timeSb.append(":");

            mStr = String.valueOf(dummyM);
            if (mStr.length()==1) {
                timeSb.append("0");
            }
            timeSb.append(mStr);

            timeSb.append(":00");


            sb.append(ELEMENT_PATIENT_VISIT_START);
            propertyString(ELEMENT_PVT_DATE, timeSb.toString(), sb);

            // 始めの30人に診察終了フラグをセットする
            done = index++ < 30 ? "1" : "0";
            propertyString(ELEMENT_PVT_STATUS, done, sb);

            sb.append(PATIENT_START);

            if (pad) {
                int mod = cnt % cicle;
                cnt++;

                switch (mod) {

                    case 0:
                        pk = TEST_PATIENT_PK1;
                        patientId = "00001";
                        break;

                    case 1:
                        pk = TEST_PATIENT_PK2;
                        patientId = "00002";
                        break;

                    case 2:
                        pk = TEST_PATIENT_PK3;
                        patientId = "00003";
                        break;

                    case 3:
                        pk = TEST_PATIENT_PK4;
                        patientId = "00004";
                        break;

                    case 4:
                        pk = TEST_PATIENT_PK5;
                        patientId = "00005";
                        break;
                }

            } else {

                pk = String.valueOf(patient.getId());
                patientId = null;

                switch (pk.length()) {
                    case 1:
                        patientId = "00000" + pk;
                        break;

                    case 2:
                        patientId = "0000" + pk;
                        break;

                    case 3:
                        patientId = "000" + pk;
                        break;
                }
            }

            propertyString(ELEMENT_PK, pk, sb);
            propertyString(ELEMENT_PATIENT_ID, patientId, sb);
            propertyString(ELEMENT_NAME, patient.getName(), sb);
            propertyString(ELEMENT_KANA, patient.getKana(), sb);

            String sex = sexValueToDesc(patient.getSex());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()),sb);

            sb.append(PATIENT_END);

            sb.append(ELEMENT_PATIENT_VISIT_END);

            // 5分 進める
            dummyM+=5;
            if (dummyM == 60) {
                dummyH++;
                dummyM = 0;
            }
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

        if (params.length < 3) {
            debug("params!=3, return");
            return null;
        }

        String facilityId = params[0];
        String start = params[1];
        String end = params[2];
        boolean pad = (params.length== 4 && params[3].equals("pad")) ? true : false;

        // 無条件に設定
        int firstResult = 60;
        int maxResult = 70;
        int examDone = 0; // to 30

        start = start.replaceAll(" ", MML_DATE_TIME_SEPARATOR);
        //end = end.replaceAll(" ", MML_DATE_TIME_SEPARATOR);

        // getPatientVisitRangeDemo
        List<DemoPatient> list = iPhoneServiceBean.getPatientVisitRangeDemo(firstResult, maxResult);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        int index = start.indexOf(MML_DATE_TIME_SEPARATOR);
        String dummyDate = start.substring(0, index);
        int dummyH = 9;
        int dummyM = 0;
        String hStr;
        String mStr;
        StringBuilder timeSb;
        String done;

        int cnt = 0;
        int cicle = 5;
        String pk = null;
        String patientId = null;

        for (DemoPatient patient : list) {

            timeSb = new StringBuilder();
            timeSb.append(dummyDate);
            timeSb.append(MML_DATE_TIME_SEPARATOR);

            hStr = String.valueOf(dummyH);
            if (hStr.length()==1) {
                timeSb.append("0");
            }
            timeSb.append(hStr);

            timeSb.append(":");

            mStr = String.valueOf(dummyM);
            if (mStr.length()==1) {
                timeSb.append("0");
            }
            timeSb.append(mStr);

            timeSb.append(":00");


            sb.append(ELEMENT_PATIENT_VISIT_START);
            propertyString(ELEMENT_PVT_DATE, timeSb.toString(), sb);

            // 始めの30人に診察終了フラグをセットする
            done = index++ < 30 ? "1" : "0";
            propertyString(ELEMENT_PVT_STATUS, done, sb);

            sb.append(PATIENT_START);

            if (pad) {
                int mod = cnt % cicle;
                cnt++;

                switch (mod) {

                    case 0:
                        pk = TEST_PATIENT_PK1;
                        patientId = "00001";
                        break;

                    case 1:
                        pk = TEST_PATIENT_PK2;
                        patientId = "00002";
                        break;

                    case 2:
                        pk = TEST_PATIENT_PK3;
                        patientId = "00003";
                        break;

                    case 3:
                        pk = TEST_PATIENT_PK4;
                        patientId = "00004";
                        break;

                    case 4:
                        pk = TEST_PATIENT_PK5;
                        patientId = "00005";
                        break;
                }

            } else {

                pk = String.valueOf(patient.getId());
                patientId = null;

                switch (pk.length()) {
                    case 1:
                        patientId = "00000" + pk;
                        break;

                    case 2:
                        patientId = "0000" + pk;
                        break;

                    case 3:
                        patientId = "000" + pk;
                        break;
                }
            }

            propertyString(ELEMENT_PK, pk, sb);
            propertyString(ELEMENT_PATIENT_ID, patientId, sb);
            propertyString(ELEMENT_NAME, patient.getName(), sb);
            propertyString(ELEMENT_KANA, patient.getKana(), sb);

            String sex = sexValueToDesc(patient.getSex());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()),sb);

            sb.append(PATIENT_END);

            sb.append(ELEMENT_PATIENT_VISIT_END);

            // 5分 進める
            dummyM+=5;
            if (dummyM == 60) {
                dummyH++;
                dummyM = 0;
            }
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

        Long id = Long.parseLong(pk);

        DemoPatient patient = iPhoneServiceBean.getPatientDemo(id);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (patient == null) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        sb.append(PATIENT_START);

        String ppk = String.valueOf(patient.getId());
        String patientId = null;

        switch (ppk.length()) {
            case 1:
                patientId = "00000" + ppk;
                break;

            case 2:
                patientId = "0000" + ppk;
                break;

            case 3:
                patientId = "000" + ppk;
                break;
        }

        propertyString(ELEMENT_PK, ppk, sb);
        propertyString(ELEMENT_PATIENT_ID, patientId, sb);
        propertyString(ELEMENT_NAME, patient.getName(), sb);
        propertyString(ELEMENT_KANA, patient.getKana(), sb);

        String sex = sexValueToDesc(patient.getSex());
        propertyString(ELEMENT_SEX, sex, sb);

        propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()), sb);

        if (patient.getAddress()!=null) {
            sb.append(ELEMENT_ADDRESS_START);
            propertyString(ELEMENT_FULL_ADDRESS, patient.getAddress(), sb);
            sb.append(ELEMENT_ADDRESS_END);
        }

        propertyString(ELEMENT_TELEPHONE, patient.getTelephone(), sb);
        propertyString(ELEMENT_MOBILE_PHONE, patient.getMobile(), sb);
        propertyString(ELEMENT_E_MAIL, patient.getEmail(), sb);

        sb.append(PATIENT_END);

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
        if (params.length < 4) {
            debug("params!=4, return");
            return null;
        }

        String facilityId = params[0];
        String name = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);
        boolean pad = params.length == 5 && params[4].equals("pad") ? true : false;

        List<DemoPatient> list;

        // ひらがなで始まっている場合
        if (KanjiHelper.isHiragana(name.charAt(0))) {
            list = iPhoneServiceBean.getPatientsByKanaDemo(name, firstResult, maxResult);
        }
        else {
            // 漢字で検索
            list = iPhoneServiceBean.getPatientsByNameDemo(name, firstResult, maxResult);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        int cnt = 0;
        int cicle = 5;
        String pk = null;
        String patientId = null;

        for (DemoPatient patient : list) {

            sb.append(PATIENT_START);

            if (pad) {

                int mod = cnt % cicle;
                cnt++;

                switch (mod) {

                    case 0:
                        pk = TEST_PATIENT_PK1;
                        patientId = "00001";
                        break;

                    case 1:
                        pk = TEST_PATIENT_PK2;
                        patientId = "00002";
                        break;

                    case 2:
                        pk = TEST_PATIENT_PK3;
                        patientId = "00003";
                        break;

                    case 3:
                        pk = TEST_PATIENT_PK4;
                        patientId = "00004";
                        break;

                    case 4:
                        pk = TEST_PATIENT_PK5;
                        patientId = "00005";
                        break;
                }

            } else {

                pk = String.valueOf(patient.getId());
                patientId = null;

                switch (pk.length()) {
                    case 1:
                        patientId = "00000" + pk;
                        break;

                    case 2:
                        patientId = "0000" + pk;
                        break;

                    case 3:
                        patientId = "000" + pk;
                        break;
                }
            }

            propertyString(ELEMENT_PK, pk, sb);
            propertyString(ELEMENT_PATIENT_ID, patientId, sb);
            propertyString(ELEMENT_NAME, patient.getName(), sb);
            propertyString(ELEMENT_KANA, patient.getKana(), sb);

            String sex = sexValueToDesc(patient.getSex());
            propertyString(ELEMENT_SEX, sex, sb);

            propertyString(ELEMENT_BIRTHDAY, toIsoBirtday(patient.getBirthday()), sb);

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

        Long pk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);
        String entity = ENTITY_MED_ORDER;

        List<DemoRp> retList = iPhoneServiceBean.getRpDemo();
        Collections.shuffle(retList);

        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        if (retList==null || retList.isEmpty()) {
            sb.append(RESOURCE_END);
            return sb.toString();
        }

        int index = 0;
        GregorianCalendar gc = new GregorianCalendar();

        for (int i = 0; i < 5; i++) {

            gc.add(Calendar.DAY_OF_MONTH, -14);
            Date date = gc.getTime();
            sb.append(ELEMENT_BUNDLE_MED_START);
            propertyString(ELEMENT_RP_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date), sb);
            String bundleNumber = String.valueOf(i+3);
            String admin = null;

            switch (i) {
                case 0:
                    admin = "医師の指示通りに";
                    break;

                case 1:
                    admin = "1日3回毎食後に";
                    break;

                case 2:
                    admin = "就寝前に";
                    break;

                case 3:
                    admin = "1日3回食間に";
                    break;

                case 4:
                    admin = "1日1回朝食後に";
                    break;
            }

            for (int j = 0; j < 3; j++) {

                DemoRp rp = retList.get(index++);
                sb.append(ELEMENT_CLAIM_ITEM_START);
                propertyString(ELEMENT_CLAIM_ITEM_NAME, rp.getName(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_QUANTITY, rp.getQuantity(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_UNIT, rp.getUnit(), sb);
                propertyString(ELEMENT_CLAIM_ITEM_NUM_DAYS, bundleNumber, sb);
                propertyString(ELEMENT_CLAIM_ITEM_ADMINI, admin, sb);
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
    @Path("/module/laboTest/{param}")
    @Produces("application/xml")
    public String getLaboTest(@PathParam("param") String param) {

        String [] params = param.split(",");

        if (params.length != 4) {
            debug("params!=4, return");
            return null;
        }
        String facilityId;  // = params[0];
        String patientId;   // = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);
        facilityId = TEST_DEMO_FACILITY_ID;
        patientId = TEST_DEMO_PATIENT_ID;

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

        GregorianCalendar gc = new GregorianCalendar();

        for (NLaboModule module : list) {

            sb.append(ELEMENT_MODULE_START);
            propertyString(ELEMENT_LABO_CODE, module.getLaboCenterCode(), sb);

            gc.add(Calendar.DAY_OF_MONTH, -14);
            propertyString(ELEMENT_SAMPLE_DATE, simpleFormat(gc.getTime()), sb);

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
        String facilityId;  // = params[0];
        String patientId;   // = params[1];
        int firstResult = Integer.parseInt(params[2]);
        int maxResult = Integer.parseInt(params[3]);
        String itemCode = params[4];
        facilityId = TEST_DEMO_FACILITY_ID;
        patientId = TEST_DEMO_PATIENT_ID;

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
        if (item.getNormalValue()!=null) {
            propertyString(ELEMENT_NORMAL_VALUE, item.getNormalValue(), sb);
        }

        // 単位
        if (item.getUnit()!=null) {
            propertyString(ELEMENT_UNIT, item.getUnit(), sb);
        }

        GregorianCalendar gc = new GregorianCalendar();

        // sampleDate の逆順で結果データを出力する
        for (int k = 0; k < cnt; k++) {

            item = list.get(k);

            sb.append(ELEMENT_RESULT_START);

            // sampleDate
            gc.add(Calendar.DAY_OF_MONTH, -14);
            propertyString(ELEMENT_SAMPLE_DATE, simpleFormat(gc.getTime()), sb);

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

        Long pk = Long.parseLong(params[0]);
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        // 結果を XML にエンコードする
        StringBuilder sb = new StringBuilder();
        sb.append(XML);
        sb.append(RESOURCE_START);

        // 件数を取得する
        if (firstResult == 0) {

        }

        List<DemoDisease> list = iPhoneServiceBean.getDiagnosisDemo();
        Collections.shuffle(list);

        int cnt = list.size();

        if (cnt == 0) {
            sb.append(RESOURCE_END);
            String ret = sb.toString();
            debug(ret);
            return ret;
        }

        cnt = 0;
        int index = firstResult;
        GregorianCalendar gc = new GregorianCalendar();
        GregorianCalendar gc2 = new GregorianCalendar();

        while (cnt < maxResult) {

            DemoDisease model = list.get(index++);

            sb.append(ELEMENT_DIAGNOSIS_START);

            if (model.getDisease()!=null) {
                propertyString(ELEMENT_DIAGNOSIS, model.getDisease(), sb);
            }

            propertyString(ELEMENT_CATEGORY, "主病名", sb);

            // 最初の二つはアクティブとする
            if (cnt < 2) {
                gc.add(Calendar.DAY_OF_MONTH, -14);
                gc2.add(Calendar.DAY_OF_MONTH, -7);
                // 開始日のみ
                propertyString(ELEMENT_START_DATE, new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()), sb);

            } else {

                if (cnt % 3 != 0) {
                    propertyString(ELEMENT_OUTCOME, "治癒", sb);
                } else {
                    propertyString(ELEMENT_OUTCOME, "中止", sb);
                }

                // 開始日
                gc.add(Calendar.DAY_OF_MONTH, -14);
                propertyString(ELEMENT_START_DATE, new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()), sb);

                // 終了日
                gc2.add(Calendar.DAY_OF_MONTH, -7);
                propertyString(ELEMENT_END_DATE, new SimpleDateFormat("yyyy-MM-dd").format(gc2.getTime()), sb);
            }

            sb.append(ELEMENT_DIGNOSIS_END);

            cnt++;
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

        Long pk = Long.parseLong(params[0]);
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
            propertyString(ELEMENT_ZIP_CODE, patient.getSimpleAddressModel().getZipCode(), sb);
            propertyString(ELEMENT_FULL_ADDRESS, patient.getSimpleAddressModel().getAddress(), sb);
            sb.append(ELEMENT_ADDRESS_END);
        }

        propertyString(ELEMENT_TELEPHONE, patient.getTelephone(), sb);
        propertyString(ELEMENT_MOBILE_PHONE, patient.getMobilePhone(), sb);
        propertyString(ELEMENT_E_MAIL, patient.getEmail(), sb);
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
//            Long count = ServiceLocator.getWebService().getDocumentCount(patientPk);
//            sb.append(ELEMENT_PAGE_INFO_START);
//            propertyString(ELEMENT_NUM_RECORDS, count.toString(), sb);
//            sb.append(ELEMENT_PAGE_INFO_END);

            int count = 0;

            if (patientPk == Integer.parseInt(TEST_PATIENT_PK1)) {
                // patientId = "00001";
                count = 9;

            } else if (patientPk == Integer.parseInt(TEST_PATIENT_PK2)) {
                // patientId = "00002";
                count = 8;

            } else if (patientPk == Integer.parseInt(TEST_PATIENT_PK3)) {
                // patientId = "00003";
                count = 7;

            } else if (patientPk == Integer.parseInt(TEST_PATIENT_PK4)) {
                // patientId = "00004";
                count = 6;

            } else if (patientPk == Integer.parseInt(TEST_PATIENT_PK5)) {
                // patientId = "00005";
                count = 5;
            }

            sb.append(ELEMENT_PAGE_INFO_START);
            propertyString(ELEMENT_NUM_RECORDS, String.valueOf(count), sb);
            sb.append(ELEMENT_PAGE_INFO_END);
        }

        // 検索する
        List<DocumentModel> list = iPhoneServiceBean.getDocuments(patientPk, firstResult, maxResult);

        for (DocumentModel doc : list) {

            sb.append(ELEMENT_DOCUMENT_START);

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
            }

            if (soaSpec != null && pSpec != null) {
                int index = soaSpec.indexOf(NAME_STAMP_HOLDER);
                if (index > 0) {
                    String sTmp = soaSpec;
                    String pTmp = pSpec;
                    soaSpec = pTmp;
                    //pSpec = sTmp;
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
}