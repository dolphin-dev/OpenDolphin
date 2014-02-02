/*
 * PatientRegister.java
 *
 * Created on 2001/10/05, 13:26
 *
 * Last updated on 2002/12/31
 * Revised on 2003/01/08 removed extra space from 'mmlHi:ratioType'
 *
 */

package mirrorI.dolphin.server;

import java.util.logging.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;

/**
 * Adds PatientVist record into Postgres DB<br>
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-i corp for reading xpath-add.txt,
 * to read 'department name' from xml and setting into pvtClaim ogject
 *
 */
public class PatientRegister extends DefaultHandler {

    public static final int TT_NONE                     				= -1;
    public static final int TT_PATIENT                  				= 1;
    public static final int TT_HEALTH                   				= 2;
    public static final int TT_CLAIM                    				= 3;

    public static final int TT_VERSION                  				= 0;
    public static final int TT_MASTER_ID                				= 1;
    public static final int TT_UUID                     					= 2;

    public static final int TT_PATIENT_ID               				= 3;
    public static final int TT_PATIENT_NAME             			= 4;
    public static final int TT_PATIENT_FULL_NAME       	 	= 5;
    public static final int TT_PATIENT_FAMILY_NAME      		= 6;
    public static final int TT_PATIENT_GIVEN_NAME       		= 7;
    public static final int TT_PATIENT_MIDDLE_NAME      		= 8;
    public static final int TT_PATIENT_PREFIX           			= 9;
    public static final int TT_PATIENT_DEGREE           			= 10;
    public static final int TT_PATIENT_BIRTHDAY         		= 11;
    public static final int TT_PATIENT_SEX              			= 12;
    public static final int TT_PATIENT_NATIONALITY     	 	= 13;
    public static final int TT_PATIENT_MARITAL         			= 14;

    public static final int TT_PATIENT_ADDRESS          		= 15;
    public static final int TT_PATIENT_AD_FULL          			= 16;
    public static final int TT_PATIENT_AD_PREFECTURE    	= 17;
    public static final int TT_PATIENT_AD_CITY          			= 18;
    public static final int TT_PATIENT_AD_TOWN          		= 19;
    public static final int TT_PATIENT_AD_HOME_NUMBER   	= 20;
    public static final int TT_PATIENT_AD_ZIP           			= 21;
    public static final int TT_PATIENT_AD_COUNTRY_CODE  	= 22;

    public static final int TT_PATIENT_PHONE            			= 23;
    public static final int TT_PATIENT_PH_AREA          			= 24;
    public static final int TT_PATIENT_PH_CITY          			= 25;
    public static final int TT_PATIENT_PH_NUMBER        		= 26;
    public static final int TT_PATIENT_PH_EXT           			= 27;
    public static final int TT_PATIENT_PH_COUNTRY       		= 28;
    public static final int TT_PATIENT_PH_MEMO          		= 29;

    public static final int TT_INSURANCE                				= 30;
    public static final int TT_INSURANCE_CLASS          		= 31;
    public static final int TT_INSURANCE_NUMBER         		= 32;
    public static final int TT_INSURANCE_CLIENT_GROUP   	= 33;
    public static final int TT_INSURANCE_CLIENT_NUMBER  	= 34;
    public static final int TT_INSURANCE_FAMILY_CLASS   	= 35;
    public static final int TT_INSURANCE_START_DATE     	= 36;
    public static final int TT_INSURANCE_EXPIRED_DATE   	= 37;
    public static final int TT_INSURANCE_DISEASE       		= 38;
    public static final int TT_INSURANCE_PAYIN_RATIO    		= 39;
    public static final int TT_INSURANCE_PAYOUT_RATIO  	= 40;
    public static final int TT_INSURANCE_ITEM           			= 41;
    public static final int TT_INSURANCE_PROVIDER_NAME  	= 42;
    public static final int TT_INSURANCE_PROVIDER       		= 43;
    public static final int TT_INSURANCE_RECIPIENT      		= 44;
    public static final int TT_INSURANCE_PUB_START_DATE 	= 45;
    public static final int TT_INSURANCE_PUB_EXPIRED_DATE = 46;
    public static final int TT_INSURANCE_PAYMEMNT_RATIO = 47;

    public static final int TT_CLAIM_INFO               				= 48;

   	public static final int TT_CLAIM_DEPARTMENT_NAME		= 49; // Added to get Depatrtment name
   	public static final int TT_CLAIM_APPOINT_NAME       		= 50; //49;
    public static final int TT_CLAIM_APPOINT_MEMO      		= 51; //50;
    public static final int TT_CLAIM_ITEM                      		= 52; //51;
    public static final int TT_CLAIM_ITEM_NAME            		= 53; //52;

    public static final String MML_VERSION              			= "2.3";

    // logger object creation
    private static Logger logger = Logger.getLogger(PVTServer.loggerLocation);

    private static Hashtable moduleDic;
    static {
        moduleDic = new Hashtable(5, 0.75f);
        moduleDic.put("patientInfo", new Integer(TT_PATIENT));
        moduleDic.put("healthInsurance", new Integer(TT_HEALTH));
        moduleDic.put("claim", new Integer(TT_CLAIM));
    }

    //Parse pvt-xpath.txt to get the MML path and stores in pathDic table.
    private static Hashtable pathDic;
    static {
        pathDic = new Hashtable(54, 0.75f);
        try {
            // pvt-xpath.txt file is read for getting department name
            String path = "/open/dolphin/resources/pvt-xpath.txt";
            InputStream in = mirrorI.dolphin.server.PatientRegister.class.getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringTokenizer st;
            String line;
            int index = 0;
            while((line = reader.readLine()) != null) {

                if (line.startsWith("#")) {
                    continue;
                }
                st = new StringTokenizer(line, ",");
                pathDic.put(st.nextToken(), new Integer(index));
                index++;
            }
        }
        catch (Exception e) {
            logger.warning("Exception while reading pvt-xpath.txt file");
            logger.warning( "Exception details:"  + e );
        }
    }

    private LinkedList linkList;

    private int moduleState;
    private int pathState;

    private PVTPostgres pvtPostgres;

    private PVTPatient pvtPatient;
    private mirrorI.dolphin.server.PVTHealthInsurance pvtHealth;
    private mirrorI.dolphin.server.PVTClaim pvtClaim;

    /** Creates new PatientRegister */
    public PatientRegister() {
        super();
    }

    /**
	 *
	 * regist(), gets MML information from PVTServer.Connection()<br>
	 * <br>
	 * Prase the MML file using SAX Parser and stores the info in corresponding objects<br>
	 * <br>
	 * This method is called from PVTServer<br>
	 * <br>
	 * Returns true on successful parsing, else returns false<br>
	 *
 	 */
    public boolean regist(String claim) {

        logger.finer("Method Entry");
        boolean result = false;

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(false);
            spf.setValidating(false);

            SAXParser saxParser = spf.newSAXParser();

            // Get the encapsulated SAX XMLReader
            XMLReader xmlReader = saxParser.getXMLReader();

            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(new MyErrorHandler());

            BufferedReader br = new BufferedReader(new StringReader(claim));
            InputSource source = new InputSource(br);
            xmlReader.parse(source);

            br.close();

            result = true;
        }
        catch (Exception e) {
            logger.warning("Exception while receiving data");
            logger.warning( "Exception details:"  + e );
        }
        logger.finer("Method Exit");
        return result;
    }

	/**
	 *
	 * getPVT(), returns filled PVTPostgres object<br>
	 * <br>
	 * This method is called from PVTServer<br>
	 *
	*/
	public PVTPostgres getPVT() {
		logger.finer("Method Entry");
		logger.finer("Method Exit");
		return pvtPostgres;
	}

	/**
	 *
	 * startDocument(), Automatically called from SAXParser<br>
	 *
	*/
    public void startDocument() {
		logger.finer("Method Entry");

        pvtPostgres = new PVTPostgres();
        linkList = new LinkedList();
        moduleState = TT_NONE;

        logger.finer("Method Exit");
    }

	/**
	 *
	 * endDocument(), Automatically called from SAXParser<br>
	 *
	*/
    public void endDocument () {
        logger.finer("Method Entry");
        logger.finer("Method Exit");
    }

	/**
	 *
	 * startElement(), Automatically called from SAXParser<br>
	 * <br>
	 * Creats pvtPatient, pvtHealth and pvtClaim object<br>
	 * <br>
	 * Parses and set the required data from MML into object's members<br>
	 *
	*/
    public void startElement(String uri, String name, String qName, Attributes attrs)  throws SAXException {
		logger.finer("Method Entry");

        linkList.addLast(qName);
        String path = getCurrentPath();

        logger.finer(path);

        if (qName.equals("docInfo")) {
            String val = attrs.getValue("contentModuleType");
            moduleState = getState(moduleDic, val);

            if (moduleState == TT_PATIENT) {
                pvtPatient = new PVTPatient();
                pvtPostgres.setPVTPatient(pvtPatient);
                logger.finer("Created new Patient Object");
            }
            else if (moduleState == TT_HEALTH) {
                pvtHealth = new mirrorI.dolphin.server.PVTHealthInsurance();
                pvtPostgres.addHealthInsurance(pvtHealth);
                logger.finer("Created new Health Insurance Object");
            }
            else if (moduleState == TT_CLAIM) {
                pvtClaim = new mirrorI.dolphin.server.PVTClaim();
                pvtPostgres.setPVTClaim(pvtClaim);
                logger.finer("Created new PVT Claim Object");
            }
        }

        pathState = getState(pathDic, path);

        // Get attribute value
        switch(pathState) {

            case TT_VERSION:
                String val = attrs.getValue("version");
                if (! val.equals(MML_VERSION)) {
					logger.warning("Invalid MML version:" + val);
                    throw new SAXException("Invalid MML version: " + val);
                }
                break;

            /**
             * PatientModule
             */
            case TT_PATIENT_ID:
                val = attrs.getValue("mmlCm:type");
                if (val != null) {
                    pvtPatient.setIdType(val);
                    logger.finer("pvtPatient.setIdType: " + val);
                }
                val = attrs.getValue("mmlCm:tableId");
                if (val != null) {
                    pvtPatient.setTableId(val);
                    logger.finer("pvtPatient.setTableId: " + val);
                }
                break;

            case TT_PATIENT_NAME:
                val = attrs.getValue("mmlNm:repCode");
                if (val != null) {
                    pvtPatient.addRepCode(val);
                    logger.finer("pvtPatient.addRepCode: " + val);
                }
                 break;

            case TT_PATIENT_ADDRESS:
                val = attrs.getValue("mmlAd:repCode");
                if (val != null) {
                    pvtPatient.addAddressRepCode(val);
                    logger.finer("pvtPatient.addAddressRepCode: " + val);
                }
                val = attrs.getValue("mmlAd:addressClass");
                if (val != null) {
                    pvtPatient.addAddressClass(val);
                    logger.finer("pvtPatient.addAddressClass: " + val);
                }
                break;

            /**
             * HealthInsuranceModule
             */
            case TT_INSURANCE_CLASS:
                val = attrs.getValue("mmlHi:ClassCode");
                if (val != null) {
                    pvtHealth.setInsuranceClassCode(val);
                    logger.finer("pvtHealth.setInsuranceClassCode: " + val);
                }
                val = attrs.getValue("mmlHi:tableId");
                if (val != null) {
                    pvtHealth.setInsuranceClassCodeTableId(val);
                    logger.finer("pvtHealth.setInsuranceClassCodeTableId: " + val);
                }
                break;

            case TT_INSURANCE_ITEM:
                val = attrs.getValue("mmlHi:priority");
                if (val != null) {
                    pvtHealth.addPublicInsurancePriority(val);
                    logger.finer("pvtHealth.addPublicInsurancePriority: " + val);
                }
                break;

            case TT_INSURANCE_PAYMEMNT_RATIO:
                val = attrs.getValue("mmlHi:ratioType");
                if (val != null) {
                    pvtHealth.addPublicInsurancePaymentRatioType(val);
                    logger.finer("pvtHealth.addPublicInsurancePaymentRatioType: " + val);
                }
                break;

            /**
             *  CalimModule
             */
            case TT_CLAIM_INFO:
                val = attrs.getValue("claim:status");
                if (val != null) {
                    pvtClaim.setClaimStatus(val);
                    logger.finer("pvtClaim.setClaimStatus: " + val);
                }
                val = attrs.getValue("claim:registTime");
                if (val != null) {
                    pvtClaim.setClaimRegistTime(val);
                    logger.finer("pvtClaim.setClaimRegistTime: " + val);
                }
                val = attrs.getValue("claim:admitFlag");
                if (val != null) {
                    pvtClaim.setClaimAdmitFlag(val);
                    logger.finer("pvtClaim.setClaimAdmitFlag: " + val);
                }
                break;

            case TT_CLAIM_APPOINT_NAME:
                val = attrs.getValue("claim:appCode");
                if (val != null ) {
                    logger.finer("AppCode: " + val);
                }
                val = attrs.getValue("claim:appCodeId");
                if (val != null ) {
                    logger.finer("AppCodeID: " + val);
                }
                break;
        }
        logger.finer("Method Exit");
    }

	/**
	 *
	 * endElement(), Automatically called from SAXParser<br>
	 *
	*/
    public void endElement(String uri, String name, String qName) throws SAXException {

		logger.finer("Method Entry");

        if (linkList.size() == 0) {
            pathState = TT_NONE;
            return;
        }

        logger.finer(getCurrentPath());

        linkList.removeLast();
        pathState = TT_NONE;

        logger.finer("Method Exit");
    }

	/**
	 *
	 * characters(), Automatically called from SAXParser<br>
	 * <br>
	 * Gets the characters, start address and length then same is set into object<br>
	 *
	*/
    public void characters(char ch[], int start, int length) {

    	logger.finer("Method Entry");

       	String text = new String(ch, start, length);

       	int st = 0;
       	int len = text.length();

       	while( st < len) {
            if (text.charAt(st) > 32) {
                break;
            }
            st++;
        }
        int ed = len - 1;
        while (ed > st) {
            if (text.charAt(ed) > 32) {
                break;
            }
            ed--;
        }

        if (ed != 0) {
            text = text.substring(st, ed + 1);
            if ( (text != null) && (!text.equals("")) ) {

                // Get element value;
                switch(pathState) {

                    case TT_MASTER_ID:
                        pvtPostgres.setMasterId(text);
                        break;

                    /**
                     * PatientModule
                     */
                    case TT_PATIENT_ID:
                        pvtPatient.setPatientId(text);
                        logger.finer("pvtPatient.setPatientId: " + text);
                        break;

                    case TT_PATIENT_FULL_NAME:
                        pvtPatient.addFullName(text);
                        logger.finer("pvtPatient.addFullName: " + text);
                        break;

                    case TT_PATIENT_FAMILY_NAME:
                        pvtPatient.addFamilyName(text);
                        logger.finer("pvtPatient.addFamilyName: " + text);
                        break;

                    case TT_PATIENT_GIVEN_NAME:
                        pvtPatient.addGivenName(text);
                        logger.finer("pvtPatient.addGivenName: " + text);
                        break;

                    case TT_PATIENT_MIDDLE_NAME:
                        pvtPatient.addMiddleName(text);
                        logger.finer("pvtPatient.addMiddleName: " + text);
                        break;

                    case TT_PATIENT_PREFIX:
                        pvtPatient.setPrefix(text);
                        logger.finer("pvtPatient.setPrefix: " + text);
                        break;

                    case TT_PATIENT_DEGREE:
                        pvtPatient.setDegree(text);
                        logger.finer("pvtPatient.setDegree: " + text);
                        break;

                    case TT_PATIENT_NATIONALITY:
                        pvtPatient.setNationality(text);
                        logger.finer("pvtPatient.setNationality: " + text);
                        break;

                    case TT_PATIENT_MARITAL:
                        pvtPatient.setMarital(text);
                        logger.finer("pvtPatient.setMarital: " + text);
                        break;

                    case TT_PATIENT_BIRTHDAY:
                        pvtPatient.setBirthday(text);
                        logger.finer("pvtPatient.setBirthday: " + text);
                        break;

                    case TT_PATIENT_SEX:
                        pvtPatient.setSex(text);
                        logger.finer("pvtPatient.setSex: " + text);
                        break;

                    case TT_PATIENT_AD_FULL:
                        pvtPatient.addAddressFull(text);
                        logger.finer("pvtPatient.addAddressFull: " + text);
                        break;

                    case TT_PATIENT_AD_PREFECTURE:
                        pvtPatient.addAddressPrefecture(text);
                        logger.finer("pvtPatient.addAddressPrefecture: " + text);
                        break;

                    case TT_PATIENT_AD_CITY:
                        pvtPatient.addAddressCity(text);
                        logger.finer("pvtPatient.addAddressCity: " + text);
                        break;

                    case TT_PATIENT_AD_TOWN:
                        pvtPatient.addAddressTown(text);
                        logger.finer("pvtPatient.addAddressTown: " + text);
                        break;

                    case TT_PATIENT_AD_HOME_NUMBER:
                        pvtPatient.addAddressHomeNumber(text);
                        logger.finer("pvtPatient.addAddressHomeNumber: " + text);
                        break;

                     case TT_PATIENT_AD_ZIP:
                        pvtPatient.addAddressZipCode(text);
                        logger.finer("pvtPatient.addAddressZipCode: " + text);
                        break;

                     case TT_PATIENT_AD_COUNTRY_CODE:
                        pvtPatient.addAddressCountryCode(text);
                        logger.finer("pvtPatient.addAddressCountryCode: " + text);
                        break;

                     case TT_PATIENT_PH_AREA:
                        pvtPatient.addPhoneAreaNumber(text);
                        logger.finer("pvtPatient.addPhoneAreaNumber: " + text);
                        break;

                     case TT_PATIENT_PH_CITY:
                        pvtPatient.addPhoneCityNumber(text);
                        logger.finer("pvtPatient.addPhoneCityNumber: " + text);
                        break;

                     case TT_PATIENT_PH_NUMBER:
                        pvtPatient.addPhoneNumber(text);
                        logger.finer("pvtPatient.addPhoneNumber: " + text);
                        break;


                    /**
                     * HealthInsuranceModule
                     */
                    case TT_INSURANCE_CLASS:
                        pvtHealth.setInsuranceClass(text);
                        logger.finer("pvtHealth.setInsuranceClass: " + text);
                        break;

                    case TT_INSURANCE_NUMBER:
                        pvtHealth.setInsuranceNumber(text);
                        logger.finer("pvtHealth.setInsuranceNumber: " + text);
                        break;

                    case TT_INSURANCE_CLIENT_GROUP:
                        pvtHealth.setInsuranceClientGroup(text);
                        logger.finer("pvtHealth.setInsuranceClientGroup: " + text);
                        break;

                    case TT_INSURANCE_CLIENT_NUMBER:
                        pvtHealth.setInsuranceClientNumber(text);
                        logger.finer("pvtHealth.setInsuranceClientNumber: " + text);
                        break;

                    case TT_INSURANCE_FAMILY_CLASS:
                        pvtHealth.setInsuranceFamilyClass(text);
                        logger.finer("pvtHealth.setInsuranceFamilyClass: " + text);
                        break;

                    case TT_INSURANCE_START_DATE:
                        pvtHealth.setInsuranceStartDate(text);
                        logger.finer("pvtHealth.setInsuranceStartDate: " + text);
                        break;

                    case TT_INSURANCE_EXPIRED_DATE:
                        pvtHealth.setInsuranceExpiredDate(text);
                        logger.finer("pvtHealth.setInsuranceExpiredDate: " + text);
                        break;

                    case TT_INSURANCE_DISEASE:
                        pvtHealth.addInsuranceDisease(text);
                        logger.finer("pvtHealth.addInsuranceDisease: " + text);
                        break;

                    case TT_INSURANCE_PAYIN_RATIO:
                        pvtHealth.setInsurancePayInRatio(text);
                        logger.finer("pvtHealth.setInsurancePayInRatio: " + text);
                        break;

                    case TT_INSURANCE_PAYOUT_RATIO:
                        pvtHealth.setInsurancePayOutRatio(text);
                        logger.finer("pvtHealth.setInsurancePayOutRatio: " + text);
                        break;

                    case TT_INSURANCE_PROVIDER_NAME:
                        pvtHealth.addPublicInsuranceProviderName(text);
                        logger.finer("pvtHealth.addPublicInsuranceProviderName: " + text);
                        break;

                    case TT_INSURANCE_PROVIDER:
                        pvtHealth.addPublicInsuranceProvider(text);
                        logger.finer("pvtHealth.addPublicInsuranceProvider: " + text);
                        break;

                    case TT_INSURANCE_RECIPIENT:
                        pvtHealth.addPublicInsuranceRecipient(text);
                        logger.finer("pvtHealth.addPublicInsuranceRecipient: " + text);
                        break;

                    case TT_INSURANCE_PUB_START_DATE:
                        pvtHealth.addPublicInsuranceStartDate(text);
                        logger.finer("pvtHealth.addPublicInsuranceStartDate: " + text);
                        break;

                    case TT_INSURANCE_PUB_EXPIRED_DATE:
                        pvtHealth.addPublicInsuranceExpiredDate(text);
                        logger.finer("pvtHealth.addPublicInsuranceExpiredDate: " + text);
                        break;

                     case TT_INSURANCE_PAYMEMNT_RATIO:
                        pvtHealth.addPublicInsurancePaymentRatio(text);
                        logger.finer("pvtHealth.addPublicInsurancePaymentRatio: " + text);
                        break;

                    /**
                     * ClaimModule
                     */

                    case TT_CLAIM_DEPARTMENT_NAME:
                    	pvtClaim.setClaimDeptName(text);
                    	logger.finer("pvtClaim.setClaimDeptName: " + text);
                    	break;

                    case TT_CLAIM_APPOINT_NAME:
                        pvtClaim.addClaimAppName(text);
                        logger.finer("pvtClaim.addClaimAppName: " + text);
                        break;

                    case TT_CLAIM_APPOINT_MEMO:
                        pvtClaim.setClaimAppMemo(text);
                        logger.finer("pvtClaim.setClaimAppMemo: " + text);
                        break;

                    case TT_CLAIM_ITEM_NAME:
                        // None sense code
                        break;

                    /**
                     *  UUID
                     */
                    case TT_UUID:
                        if (moduleState == TT_HEALTH) {
                            pvtHealth.setModuleUid(text);
                            logger.finer(" pvtHealth.setModuleUid: " + text);
                        }
                        break;

               		default:
               			break;
                }
            }
        }
    	logger.finer("Method Exit");
    }

	/**
	 *
	 * getCurrentPath(), returns the current path of MML file while parsing
	 *
	 */
    private String getCurrentPath() {

       logger.finer("Method Entry");

       if (linkList.size() == 0) {
            logger.finer("Method Exit");
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = linkList.size();
        for (int i = 0; i < len -1; i++) {
            buf.append((String)linkList.get(i));
            buf.append("/");
        }
        buf.append((String)linkList.get(len -1));

        logger.finer("Method Exit");
        return buf.toString();
    }

    private int getState(Hashtable h, String val) {
        int ret = TT_NONE;
        Integer i = (Integer)h.get(val);
        return i != null ? i.intValue() : ret;
    }

	/**
	 *
	 * MyErrorHandler(), to report errors and warnings while parsing<br>
	 * <br>
	 * This method is called from regist(). (XMLReader's ErrorHandler)<br>
	 *
	 */
    private static class MyErrorHandler implements ErrorHandler {

   		MyErrorHandler() {
		}
        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.
        public void warning (SAXParseException spe) throws SAXException {
            logger.warning("SAXParseException Warning: " + getParseExceptionInfo(spe));
        }

        public void error (SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            logger.warning("SAXParseException " + getParseExceptionInfo(spe));
            throw new SAXException (message);
        }

        public void fatalError (SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            logger.warning("SAXParseException " + getParseExceptionInfo(spe));
            throw new SAXException(message);
        }
    }
}