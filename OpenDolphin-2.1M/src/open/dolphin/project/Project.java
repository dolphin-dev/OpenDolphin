package open.dolphin.project;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.Properties;
import open.dolphin.infomodel.ID;

import open.dolphin.infomodel.UserModel;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Project  {
    
    // Prpject Name
    public static final String PROJECT_NAME		= "name";
    
    // USER
    public static final String USER_TYPE		= "userType";
    public static final String FACILITY_NAME            = "facilityName";
    public static final String FACILITY_ID 		= "facilityId";
    public static final String USER_ID 			= "userId";
    public static final String SERVER_URI 		= "baseURI";
    public static final String BASE_URI 		= "baseURI";
    
    // CLAIM
    public static final String SEND_CLAIM 		= "sendClaim";
    public static final String SEND_CLAIM_SAVE          = "sendClaimSave";
    public static final String SEND_CLAIM_TMP 		= "sendClaimTmp";
    public static final String SEND_CLAIM_MODIFY 	= "sendClaimModify";
    public static final String SEND_DIAGNOSIS 		= "sendDiagnosis";
    public static final String CLAIM_HOST_NAME          = "claimHostName";
    public static final String CLAIM_VERSION 		= "claimVersion";
    public static final String CLAIM_ENCODING 		= "claimEncoding";
    public static final String CLAIM_ADDRESS 		= "claimAddress";
    public static final String CLAIM_PORT 		= "claimPort";
    public static final String USE_AS_PVT_SERVER 	= "useAsPVTServer";
    public static final String JMARI_CODE               = "jmariCode";
    public static final String CLAIM_BIND_ADDRESS       = "BIND_ADDRESS";
    public static final String CLAIM_01                 = "CLAIM01";

    // Labtest
    public static final String SEND_LABTEST             = "order.labtest.send";
    public static final String SEND_LABTEST_SYSTEM      = "order.labtest.system";
    public static final String SEND_LABTEST_PATH        = "order.labtest.path";
    public static final String SEND_LABTEST_FACILITY_ID = "order.labtest.facility.id";
    
    // Area Network
    public static final String JOIN_AREA_NETWORK 	= "joinAreaNetwork";
    public static final String AREA_NETWORK_NAME 	= "jareaNetworkName";
    public static final String AREA_NETWORK_FACILITY_ID = "jareaNetworkFacilityId";
    public static final String AREA_NETWORK_CREATOR_ID  = "jareaNetworkCreatorId";
    
    // MML
    public static final String SEND_MML			= "mml.send";
    public static final String MML_VERSION    		= "mml.version";
    public static final String MML_ENCODING		= "mml.encoding";
    public static final String SEND_MML_ADDRESS 	= "mml.address";
    public static final String SEND_MML_DIRECTORY 	= "mml.directory";
    public static final String SEND_MML_PROTOCOL 	= "mml.protocol";
   
    // ソフトウェア更新
    public static final String USE_PROXY		= "useProxy";
    public static final String PROXY_HOST		= "proxyHost";
    public static final String PROXY_PORT		= "proxyPort";
    public static final String LAST_MODIFIED  		= "lastModify";
    
    // インスペクタのメモ位置
    public static final String INSPECTOR_MEMO_LOCATION  = "inspectorMemoLocation";

    // インスペクタ配置
    public static final String TOP_INSPECTOR = "topInspector";
    public static final String SECOND_INSPECTOR = "secondInspector";
    public static final String THIRD_INSPECTOR = "thirdInspector";
    public static final String FORTH_INSPECTOR = "forthInspector";
    public static final String LOCATION_BY_PLATFORM     = "locationByPlatform";
    
    // 文書履歴
    public static final String DOC_HISTORY_ASCENDING 	= "docHistory.ascending";
    public static final String DOC_HISTORY_SHOWMODIFIED = "docHistory.showModified";
    public static final String DOC_HISTORY_FETCHCOUNT 	= "docHistory.fetchCount";
    public static final String DOC_HISTORY_PERIOD 	= "docHistory.period";
    public static final String KARTE_SCROLL_DIRECTION   = "karte.scroll.direction";
    public static final String DOUBLE_KARTE             = "karte.double";
    
    // 病名
    public static final String DIAGNOSIS_ASCENDING 	= "diagnosis.ascending";
    public static final String DIAGNOSIS_PERIOD 	= "diagnosis.period";
    public static final String OFFSET_OUTCOME_DATE 	= "diagnosis.offsetOutcomeDate";
    public static final String DIAGNOSIS_AUTO_OUTCOME_INPUT = "autoOutcomeInput";
    
    // 検体検査
    public static final String LABOTEST_PERIOD 		= "laboTest.period";
    public static final String LABTEST_FOLD = "laboFold";
    
    // 処方
    public static final String RP_OUT			= "rp.out";
        
    // カルテ
    public static final String KARTE_USE_TOP15_AS_TITLE = "useTop15AsTitle";
    public static final String KARTE_DEFAULT_TITLE = "defaultKarteTitle";
    public static final String KARTE_SHOW_CONFIRM_AT_NEW = "karte.showConfirmAtNew";
    public static final String KARTE_CREATE_MODE = "karte.createMode";
    public static final String KARTE_PLACE_MODE = "karte.placeMode";
    public static final String KARTE_SHOW_CONFIRM_AT_SAVE = "karte.showConfirmAtSave";
    public static final String KARTE_PRINT_COUNT = "karte.printCount";
    public static final String KARTE_SAVE_ACTION = "karte.saveAction";
    public static final String KARTE_AUTO_CLOSE_AFTER_SAVE = "karte.auto.close";
    public static final String KARTE_AGE_TO_NEED_MONTH = "ageToNeedMonth";

    // Stamp
    public static final String STAMP_REPLACE = "replaceStamp";
    public static final String STAMP_SPACE = "stampSpace";

    public static final String DEFAULT_ZYOZAI_NUM = "defaultZyozaiNum";
    public static final String DEFAULT_MIZUYAKU_NUM = "defaultMizuyakuNum";
    public static final String DEFAULT_SANYAKU_NUM = "defaultSanyakuNum";
    public static final String DEFAULT_RP_NUM = "defaultRpNum";
    public static final String DEFAULT_RP_OUT = "rp.out";
    public static final String ORDER_TABLE_CLICK_COUNT_TO_START = "order.table.clickCountToStart";
    public static final String MASTER_SEARCH_REALTIME = "masterSearch.realTime";
    public static final String MASTER_SEARCH_PARTIAL_MATCH = "masterSearch.partialMatch";
    public static final String MASTER_SEARCH_ITEM_COLORING = "masterItemColoring";

    public static final String LOCATION_PDF = "pdfStore";

    // 医療資格
    private static final String LICENSE_DOCTOR = "doctor";

    // 切り株
    private static ProjectStub stub;
    
    /** Creates new Project*/
    public Project() {
    }
    
    public static void setProjectStub(ProjectStub p) {
        stub = p;
    }
    
    public static ProjectStub getProjectStub() {
        return stub;
    }
    
    public static boolean isValid() {
        return stub.isValid();
    }
    
    public static UserModel getUserModel() {
        return stub.getUserModel();
    }
    
    public static void setUserModel(UserModel value) {
        stub.setUserModel(value);
    }
    
    public static String getFacilityId() {
        return stub.getFacilityId();
    }
    
    public static String getUserId() {
        return stub.getUserId();
    }

    public static boolean isReadOnly() {
        String licenseCode = stub.getUserModel().getLicenseModel().getLicense();
        return licenseCode.equals(LICENSE_DOCTOR) ? false : true;
    }

    public static String getBaseURI() {
        return stub.getBaseURI();
    }
    
    /**
     * ProjectFactoryを返す。
     * @return Project毎に異なる部分の情報を生成するためのFactory
     */
    public static AbstractProjectFactory getProjectFactory() {
        return AbstractProjectFactory.getProjectFactory(stub.getName());
    }

    /**
     * 地域連携用の患者MasterIdを返す。
     * @return 地域連携で使用する患者MasterId
     */
    public static ID getMasterId(String pid) {
        String fid = Project.getString(Project.AREA_NETWORK_FACILITY_ID);   //stub.getAreaNetworkFacilityId();
        return getProjectFactory().createMasterId(pid, fid);
    }

    /**
     * CLAIM送信に使用する患者MasterIdを返す。
     * 地域連携ルールと異なるため。
     */
    public static ID getClaimMasterId(String pid) {
        return new ID(pid, "facility", "MML0024");
    }

    /**
     * CSGW(Client Side Gate Way)へのパスを返す。
     */
    public static String getCSGWPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getString(Project.SEND_MML_DIRECTORY));
        sb.append(File.separator);
        sb.append(Project.getString(Project.JMARI_CODE));
        return sb.toString();
    }

    //---------------------------------------------------
    public static Properties getUserDefaults() {
        return stub.getUserDefaults();
    }

    public static void saveUserDefaults() {
        stub.saveUserDefaults();
    }

    public static void loadProperties(Properties prop, String name) {
        stub.loadProperties(prop, name);
    }

    public static void storeProperties(Properties prop, String name) {
        stub.storeProperties(prop, name);
    }

    public static Properties loadPropertiesAsObject(String name) {
        return stub.loadPropertiesAsObject(name);
    }

    public static void storePropertiesAsObject(Properties prop, String name) {
        stub.storePropertiesAsObject(prop, name);
    }

    public static boolean deleteSettingFile(String file) {
        return stub.deleteSettingFile(file);
    }

    public static String getString(String key) {
        return stub.getString(key);
    }

    public static String getString(String key, String defStr) {
        return stub.getString(key, defStr);
    }

    public static void setString(String key, String value) {
        stub.setString(key, value);
    }

    public static String[] getStringArray(String key) {
        return stub.getStringArray(key);
    }

    public static String[] getStringArray(String key, String[] defStr) {
        return stub.getStringArray(key, defStr);
    }

    public static void setStringArray(String key, String[] value) {
        stub.setStringArray(key, value);
    }

    public static Rectangle getRectangle(String key) {
        return stub.getRectangle(key);
    }

    public static Rectangle getRectangle(String key, Rectangle defRect) {
        return stub.getRectangle(key, defRect);
    }

    public static void setRectangle(String key, Rectangle value) {
        stub.setRectangle(key, value);
    }

    public static Color getColor(String key) {
        return stub.getColor(key);
    }

    public static Color getColor(String key, Color defVal) {
        return stub.getColor(key, defVal);
    }

    public static void setColor(String key, Color value) {
        stub.setColor(key, value);
    }

    public static int getInt(String key) {
        return stub.getInt(key);
    }

    public static int getInt(String key, int defVal) {
        return stub.getInt(key, defVal);
    }

    public static void setInt(String key, int value) {
        stub.setInt(key, value);
    }

    public static float getFloat(String key) {
        return stub.getFloat(key);
    }

    public static float getFloat(String key, float defVal) {
        return stub.getFloat(key, defVal);
    }

    public static void setFloat(String key, float value) {
        stub.setFloat(key, value);
    }

    public static double getDouble(String key) {
        return stub.getDouble(key);
    }

    public static double getDouble(String key, double defVal) {
        return stub.getDouble(key, defVal);
    }

    public static void setDouble(String key, double value) {
        stub.setDouble(key, value);
    }

    public static boolean getBoolean(String key) {
        return stub.getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return stub.getBoolean(key, defVal);
    }

    public static void setBoolean(String key, boolean value) {
        stub.setBoolean(key, value);
    }

    //--------------------------------------------------------------

    public static String getDefaultString(String key) {
        return stub.getDefaultString(key);
    }

    public static String[] getDefaultStringArray(String key) {
        return stub.getDefaultStringArray(key);
    }

    public static Rectangle getDefaultRectangle(String key) {
        return stub.getDefaultRectangle(key);
    }

    public static Color getDefaultColor(String key) {
        return stub.getDefaultColor(key);
    }

    public static int getDefaultInt(String key) {
        return stub.getDefaultInt(key);
    }

    public static float getDefaultFloat(String key) {
        return stub.getDefaultFloat(key);
    }

    public static double getDefaultDouble(String key) {
        return stub.getDefaultDouble(key);
    }

    public static boolean getDefaultBoolean(String key) {
        return stub.getDefaultBoolean(key);
    }
}