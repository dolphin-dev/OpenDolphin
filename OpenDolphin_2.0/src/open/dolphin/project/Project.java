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
    
    // インスペクタの locationByPlatform 属性
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
    
    // 検体検査
    public static final String LABOTEST_PERIOD 		= "laboTest.period";
    
    // 処方
    public static final String RP_OUT			= "rp.out";
        
    // 確認ダイアログ
    public static final String KARTE_SHOW_CONFIRM_AT_NEW = "karte.showConfirmAtNew";
    public static final String KARTE_CREATE_MODE = "karte.createMode";
    public static final String KARTE_PLACE_MODE = "karte.placeMode";
    public static final String KARTE_SHOW_CONFIRM_AT_SAVE = "karte.showConfirmAtSave";
    public static final String KARTE_PRINT_COUNT = "karte.printCount";
    public static final String KARTE_SAVE_ACTION = "karte.saveAction";
    public static final String KARTE_AUTO_CLOSE_AFTER_SAVE = "karte.auto.close";

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
    
    //-----------------------------------------------------
    // CLAIM
    //-----------------------------------------------------
    
    /**
     * 診療行為の送信を行うかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getSendClaim() {
        return stub.getSendClaim();
    }
    
    /**
     * 保存時に送信を行うかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getSendClaimSave() {
        return stub.getSendClaimSave();
    }
    
    /**
     * 仮保存時に診療行為の送信を行うかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getSendClaimTmp() {
        return stub.getSendClaimTmp();
    }
    
    /**
     * 修正時に診療行為の送信を行うかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getSendClaimModify() {
        return stub.getSendClaimModify();
    }
    
    /**
     * 病名の送信を行うかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getSendDiagnosis() {
        return stub.getSendDiagnosis();
    }
    
    /**
     * CLAIM のホスト名を返す。
     * @return return CLAIM のホスト名
     */
    public static String getClaimHostName() {
        return stub.getClaimHostName();
    }
    
    /**
     * 受付情報を受信するかどうかを返す。
     * @return 行うとき true
     */
    public static boolean getUseAsPVTServer() {
        return stub.getUseAsPVTServer();
    }
    
    public static String getBindAddress() {
        return stub.getBindAddress();
    }
    
    public static boolean isClaim01() {
        return stub.isClaim01();
    }
    
    /**
     * CLAIM ホストの IP アドレスを返す。
     * @return CLAIM ホストの IP アドレス
     */
    public static String getClaimAddress() {
        return stub.getClaimAddress();
    }
    
    /**
     * CLAIM ホストの診療行為送信先ポート番号を返す。
     * @return CLAIM ホスト名の診療行為送信先ポート番号
     */
    public static int getClaimPort() {
        return stub.getClaimPort();
    }
    
    /**
     * CLAIM 送信時のXMLエンコーディングを返す。
     * @return CLAIM エンコーディング
     */
    public static String getClaimEncoding() {
        return stub.getClaimEncoding();
    }

    public static String getOrcaVersion() {
        return stub.getOrcaVersion();
    }

    public static String getJMARICode() {
        return stub.getJMARICode();
    }

    //------------------------------------------------
    // Proxy Support
    //------------------------------------------------
    public static String getProxyHost() {
        return stub.getProxyHost();
    }
    
    public static int getProxyPort() {
        return stub.getProxyPort();
    }
    
    /**
     * ProjectFactoryを返す。
     * @return Project毎に異なる部分の情報を生成するためのFactory
     */
    public static AbstractProjectFactory getProjectFactory() {
        return AbstractProjectFactory.getProjectFactory(stub.getName());
    }

    //------------------------------------------------
    // 地域連携
    //------------------------------------------------
    
    /**
     * 地域連携に参加するかどうかを返す。
     * @return 参加する時 true
     */
    public static boolean getJoinAreaNetwork() {
        return stub.getJoinAreaNetwork();
    }
    
    /**
     * 地域連携用の施設IDを返す。
     * @return 地域連携で使用する施設ID
     */
    public static String getAreaNetworkFacilityId() {
        return stub.getAreaNetworkFacilityId();
    }
    
    /**
     * 地域連携用のCreatorIDを返す。
     * @return 地域連携で使用するCreatorId
     */
    public static String getAreaNetworkCreatorId() {
        return stub.getAreaNetworkCreatorId();
    }
        
    /**
     * 地域連携用の患者MasterIdを返す。
     * @return 地域連携で使用する患者MasterId
     */
    public static ID getMasterId(String pid) {
        String fid = stub.getAreaNetworkFacilityId();
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
        sb.append(getUploadShareDirectory());
        sb.append(File.separator);
        sb.append(getJMARICode());
        return sb.toString();
    }
    
    // HOT
    public static boolean getSendMML() {
        return stub.getSendMML();
    }
    
    public static String getMMLVersion() {
        return stub.getMMLVersion();
    }
    
    public static String getMMLEncoding() {
        return stub.getMMLEncoding();
    }
    
    public static String getUploaderIPAddress() {
        return stub.getUploaderIPAddress();
    }
    
    public static String getUploadShareDirectory() {
        return stub.getUploadShareDirectory();
    }

    public static boolean isSendAmazon() {
        return stub.isSendAmazon();
    }

    //---------------------------------------------------
    public static Properties getUserDefaults() {
        return stub.getUserDefaults();
    }

    public static void saveUserDefaults() {
        stub.saveUserDefaults();
    }

    public static String getString(String key, String defStr) {
        return stub.getString(key, defStr);
    }

    public static void setString(String key, String value) {
        stub.setString(key, value);
    }

    public static String[] getStringArray(String key, String[] defStr) {
        return stub.getStringArray(key, defStr);
    }

    public static void setStringArray(String key, String[] value) {
        stub.setStringArray(key, value);
    }

    public static Rectangle getRectangle(String key, Rectangle defRect) {
        return stub.getRectangle(key, defRect);
    }

    public static void setRectangle(String key, Rectangle value) {
        stub.setRectangle(key, value);
    }

    public static Color getColor(String key, Color defVal) {
        return stub.getColor(key, defVal);
    }

    public static void setColor(String key, Color value) {
        stub.setColor(key, value);
    }

    public static int getInt(String key, int defVal) {
        return stub.getInt(key, defVal);
    }

    public static void setInt(String key, int value) {
        stub.setInt(key, value);
    }

    public static float getFloat(String key, float defVal) {
        return stub.getFloat(key, defVal);
    }

    public static void setFloat(String key, float value) {
        stub.setFloat(key, value);
    }

    public static double getDouble(String key, double defVal) {
        return stub.getDouble(key, defVal);
    }

    public static void setDouble(String key, double value) {
        stub.setDouble(key, value);
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return stub.getBoolean(key, defVal);
    }

    public static void setBoolean(String key, boolean value) {
        stub.setBoolean(key, value);
    }
}