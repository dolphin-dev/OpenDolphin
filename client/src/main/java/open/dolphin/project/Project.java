package open.dolphin.project;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.Properties;
import open.dolphin.client.ClientContext;
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
    //public static final String BASE_URI 		= "baseURI";
    
    // CLAIM
    // 2012-07  claim.sender=(client | server) client=client送信, server=server送信
    public static final String CLAIM_SENDER 		= "claim.sender";
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
    public static final String INTERACTION_CHECK        = "interaction.check";
//minagawa^ 定期チェック    
    public static final String PVT_TIMER_CHECK          = "pvt.timer.check";
//minagawa$    
//minagawa^ 予定カルテ    (予定カルテ対応)
    public static final String SEND_CLAIM_EDIT_FROM_SCHEDULE = "send.claim.edit.from.schedule";
    public static final String SEND_CLAIM_WHEN_SCHEDULE = "send.claim.when.schedule";
    public static final String SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP = "send.claim.depends.on.check.at.tmp";
    public static final String USE_SCHEDULE_KARTE = "use.schedule.karte";
//minagawa$    
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
    public static final String DIAGNOSIS_ACTIVE_ONLY = "diagnosis.activeOnly";
    
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
    public static final String KARTE_MERGE_RP_WITH_SAME_ADMIN = "merge.rp.with.sameAdmin";
    
    public static final String KARTE_PDF_SEND_AT_SAVE = "karte.pdf.send.at.save";
    public static final String KARTE_PDF_SEND_DIRECTORY = "karte.pdf.send.directory";
    
//s.oh^ 2013/02/07 印刷対応
    public static final String KARTE_PRINT_DIRECT = "karte.print.direct";
    public static final String KARTE_PRINT_PDF = "karte.print.pdf";
//s.oh$

    // Stamp
    public static final String STAMP_REPLACE = "replaceStamp";
    public static final String STAMP_SPACE = "stampSpace";
    
    // StampEditor
    public static final String STAMP_EDITOR_BUTTON_TYPE = "stamp.editor.buttonType";
    public static final String DEFAULT_ZYOZAI_NUM = "defaultZyozaiNum";
    public static final String DEFAULT_MIZUYAKU_NUM = "defaultMizuyakuNum";
    public static final String DEFAULT_SANYAKU_NUM = "defaultSanyakuNum";
    public static final String DEFAULT_CAPSULE_NUM = "defaultCapsuleNum";
    public static final String DEFAULT_RP_NUM = "defaultRpNum";
    public static final String DEFAULT_RP_OUT = "rp.out";
    public static final String ORDER_TABLE_CLICK_COUNT_TO_START = "order.table.clickCountToStart";
    public static final String MASTER_SEARCH_REALTIME = "masterSearch.realTime";
    public static final String MASTER_SEARCH_PARTIAL_MATCH = "masterSearch.partialMatch";
    public static final String MASTER_SEARCH_ITEM_COLORING = "masterItemColoring";

    // 紹介状等
    public static final String LETTER_ATESAKI_TITLE = "letter.atesaki.title";
    public static final String LETTER_INCLUDE_GREETINGS = "letter.greetings.include";
    public static final String PLAIN_PRINT_PATIENT_NAME = "plain.print.patinet.name";
    public static final String LOCATION_PDF = "pdfStore"; // PDF 出力ディレクトリー
    public static final String SHINDANSYO_FONT_SIZE = "sindansyo.font.size";

    // 医療資格
    private static final String LICENSE_DOCTOR = "doctor";
    
    // 受付 Relay
    public static final String PVT_RELAY = "pvt.relay";
    public static final String PVT_RELAY_DIRECTORY = "pvt.relay.directory";
    public static final String PVT_RELAY_ENCODING = "pvt.relay.encoding";
    public static final String PVT_RELAY_NAME = "pvt.relay.name";   // lsc,fev....
    
//Hiro Clinic 処方せん出力のための保険医療機関コード
    public static final String FACILITY_CODE_OF_INSURNCE_SYSTEM = "facility.code.insurance.system";

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
    
    // 2012-07 ORCAとの通信をClientで行うかどうかの便利メソッド
    public static boolean claimSenderIsClient() {
        // ASPの場合は Client-ORCA
        if (ClientContext.isOpenDolphin()) {
            return true;
        }
        String test = stub.getString(CLAIM_SENDER);
        return (test!=null && test.equals("client"));
    }
    
    // 2012-07 ORCAとの通信をServerで行うかどうかの便利メソッド
    public static boolean claimSenderIsServer() {
        // ASPの場合は Client-ORCA
        if (ClientContext.isOpenDolphin()) {
            return false;
        }
        String test = stub.getString(CLAIM_SENDER);
        return (test!=null && test.equals("server"));
    }
    
    // ORCAへ送信、病名取り込み、可能かどうか
    // 使用場所
    //  --KarteEditor sendClaim  -> ChartImplの isSendClaim()
    //  --保存ダイアログのClaim送信CheckBox enabled -> 
    //  --病名取り込み -> button を show|hide
    //  --病名送信  -> ChartImplの isSendClaim()
    //  --StampBoxのORCAタブ -> OrcaTreeのenter()
    public static boolean canAccessToOrca() {
        
        // DolphinPRO
        if (ClientContext.isDolphinPro()) {
            return claimSenderIsClient() ? claimAddressIsValid() : true;
        }
        
        // 評価
        if (ClientContext.is5mTest()) {
            // Server-ORCAでは送信不可
            return claimSenderIsClient() ? claimAddressIsValid() : false;
        }
        
        // ASP user
        if (ClientContext.isOpenDolphin()) {
            return claimAddressIsValid();
        }
        
        return false;
    }
    
    // Master 検索が可能かどうか
    // 使用場所
    //  マスター検索フィールドの enabled
    public static boolean canSearchMaster() {
        
        // DolphinPRO
        if (ClientContext.isDolphinPro()) {
            return claimSenderIsClient() ? claimAddressIsValid() : true;
        }
        
        // 評価
        if (ClientContext.is5mTest()) {
            // Server-ORCAでマスター検索可
            return claimSenderIsClient() ? claimAddressIsValid() : true;
        }
        
        // ASP
        if (ClientContext.isOpenDolphin()) {
            return claimAddressIsValid();
        }
        
        return false;
    }
    
    // 有効なアドレスかどうか
    private static boolean claimAddressIsValid() {
        // null empty のみチェック ToDo
        String host = stub.getString(CLAIM_ADDRESS);
        return (host!=null && (!host.equals("")));
    }
    
    // StampがGlobalに公開できるか?
    public static boolean canGlobalPublish() {
        // DolphinPRO
        if (ClientContext.isDolphinPro()) {
            return false;
        }
        
        // 評価
        if (ClientContext.is5mTest()) {
            // LSCのみ
            return (getUserModel().getFacilityModel().getFacilityId().endsWith("70.1"));
        }
        
        // ASP
        if (ClientContext.isOpenDolphin()) {
            return true;
        }
        
        return false;
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
    
//新宿ヒロクリニック 処方せん印刷^ Propertiesへ保存するように変更
    /**
     * スタブに設定されている医療機関基本情報を返す。
     * @return 医療機関基本情報 String
     */
    // @002 2010/07/16
    public static String getBasicInfo() {
        return stub.getString(FACILITY_CODE_OF_INSURNCE_SYSTEM);
    }

    /**
     * スタブに医療機関基本情報を設定する。
     * @param basicInfo 医療機関基本情報
     */
    // @002 2010/07/16
    public static void setBasicInfo(String basicInfo) {
        stub.setString(FACILITY_CODE_OF_INSURNCE_SYSTEM, basicInfo);
    }
//新宿ヒロクリニック$    
}