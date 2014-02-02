package open.dolphin.project;

import java.io.OutputStream;
import java.util.prefs.Preferences;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.UserModel;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ProjectStub implements java.io.Serializable {

    private Preferences prefs;
    private boolean valid;
    private DolphinPrincipal principal;
    private String providerURL;
    private UserModel userModel;
    // Preferences のノード名
    private final String NODE_NAME = "/open/dolphin/project";
    // デフォルトのプロジェクト名
    private final String DEFAULT_PROJECT_NAME = "ASPOpenDolphin";
    // User ID
    private final String DEFAULT_USER_ID = null;
    private final String DEFAULT_FACILITY_ID = null;
    // Server
    private final String DEFAULT_HOST_ADDRESS = "localhost";
    private final int JBOSS_J2EE_PORT = 1099;
    private final String JBOSS_PROTOCOL = "jnp";
    // Claim
    private final boolean DEFAULT_SEND_CLAIM = false;
    private final boolean DEFAULT_SEND_CLAIM_SAVE = true;
    private final boolean DEFAULT_SEND_CLAIM_TMP = false;
    private final boolean DEFAULT_SEND_CLAIM_MODIFY = false;
    private final boolean DEFAULT_SEND_DIAGNOSIS = true;
    private final String DEFAULT_CLAIM_HOST_NAME = "日医標準レセプト(ORCA)";
    private final String DEFAULT_CLAIM_ADDRESS = null;
    private final int DEFAULT_CLAIM_PORT = 5001;
    private final String DEFAULT_CLAIM_ENCODING = "UTF-8";
    private final boolean DEFAULT_USE_AS_PVTSERVER = true;
    // MML
    private final boolean DEFAULT_SEND_MML = false;
    private final String DEFAULT_MML_VERSION = "2.3";
    private final String DEFAULT_MML_ENCODING = "UTF-8";
    private final String DEFAULT_SEND_MML_ADDRESS = null;
    private final String DEFAULT_SEND_MML_DIRECTORY = null;
    // Update
    private final boolean DEFAULT_USE_PROXY = false;
    private final String DEFAULT_PROXY_HOST = null;
    private final int DEFAULT_PROXY_PORT = 8080;
    private final long DEFAULT_LAST_MODIFIED = 0L;

    /** ProjectStub を生成する。 */
    public ProjectStub() {
        prefs = Preferences.userRoot().node(NODE_NAME);
    }

    /**
     * Preferencesを返す。
     */
    public Preferences getPreferences() {
        return prefs;
    }

    /**
     * 設定ファイルが有効かどうかを返す。
     * @return 有効な時 true
     */
    public boolean isValid() {

        // UserTypeを判定する
        if (getUserType().equals(Project.UserType.UNKNOWN)) {
            return false;
        }

        // UserIdとFacilityIdを確認する
        if (getUserId() == null || getFacilityId() == null) {
            return false;
        }

        // Master 検索のため
        // 2007-01-05 ASP StampBox のため上記条件は廃止
        //        if ( getClaimAddress() == null ) {
        //            return false;
        //        }

        // MML送信を行う場合の確認をする
        if (getSendMML() && (getUploaderIPAddress() == null || getUploadShareDirectory() == null)) {
            return false;
        }

        // ここまで来れば有効である
        valid = true;
        return valid;
    }

    public DolphinPrincipal getDolphinPrincipal() {
        return principal;
    }

    public void setDolphinPrincipal(DolphinPrincipal principal) {
        this.principal = principal;
    }

    /**
     * ProviderURLを返す。
     * @return JNDI に使用する ProviderURL
     */
    public String getProviderURL() {
        if (providerURL == null) {
            String host = "localhost";
            Project.UserType userType = getUserType();
            switch (userType) {
                case ASP_MEMBER:
                    host = ClientContext.getString("addUser.aspMember.server.address");
                    break;

                case ASP_TESTER:
                    host = ClientContext.getString("addUser.aspTester.server.address");
                    break;

                case ASP_DEV:
                    host = ClientContext.getString("addUser.aspTester.server.address");
                    break;

                case FACILITY_USER:
                    host = prefs.get(Project.HOST_ADDRESS, DEFAULT_HOST_ADDRESS);
                    break;

                case UNKNOWN:
                    host = prefs.get(Project.HOST_ADDRESS, DEFAULT_HOST_ADDRESS);
                    break;
            }
            StringBuilder buf = new StringBuilder();
            buf.append(JBOSS_PROTOCOL);
            buf.append("://");
            buf.append(host);
            buf.append(":");
            buf.append(prefs.getInt(Project.HOST_PORT, JBOSS_J2EE_PORT));
            String url = buf.toString();
            setProviderURL(url);
        }
        return providerURL;
    }

    /**
     * JNDIのProviderURLを設定する。
     * @param providerURL JNDIのProviderURL
     */
    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    /**
     * プロジェクト名を返す。
     * @return プロジェクト名 (Dolphin ASP, HOT, MAIKO, HANIWA ... etc)
     */
    public String getName() {
        return prefs.get(Project.PROJECT_NAME, DEFAULT_PROJECT_NAME);
    }

    /**
     * プロジェクト名を返す。
     * @return プロジェクト名 (Dolphin ASP, HOT, MAIKO, HANIWA ... etc)
     */
    public void setName(String projectName) {
        prefs.put(Project.PROJECT_NAME, projectName);
    }

    /**
     * ログインユーザ情報を返す。
     * @return Dolphinサーバに登録されているユーザ情報
     */
    public UserModel getUserModel() {
        return userModel;
    }

    /**
     * ログインユーザ情報を設定する。
     * @param userModel ログイン時にDolphinサーバから取得した情報
     */
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    /**
     * ログイン画面用のUserIDを返す。
     * @return ログイン画面に表示するUserId
     */
    public String getUserId() {
        return prefs.get(Project.USER_ID, DEFAULT_USER_ID);
    }

    /**
     * ログイン画面用のUserIDを設定する。
     * @param ログイン画面に表示するUserId
     */
    public void setUserId(String val) {
        prefs.put(Project.USER_ID, val);
    }

    /**
     * ログイン画面用のFacilityIDを返す。
     * @return ログイン画面に表示するFacilityID
     */
    public String getFacilityId() {
        return prefs.get(Project.FACILITY_ID, DEFAULT_FACILITY_ID);
    }

    /**
     * ログイン画面用のFacilityIDを設定する。
     * @param ログイン画面に表示するFacilityID
     */
    public void setFacilityId(String val) {
        prefs.put(Project.FACILITY_ID, val);
    }

    /**
     * ORCA バージョンを返す。
     * @return ORCA バージョン
     */
    public String getOrcaVersion() {
        return prefs.get("orcaVersion", "40");
    }

    /**
     * ORCA バージョンを設定する。
     * @param ORCA バージョン
     */
    public void setOrcaVersion(String version) {
        prefs.put("orcaVersion", version);
    }

    /**
     * JMARICode を返す。
     * @return JMARI Code
     */
    public String getJMARICode() {
        return prefs.get("jmariCode", "JPN000000000000");
    }

    /**
     * JMARICode を返す。
     * @return JMARI Code
     */
    public void setJMARICode(String jamriCode) {
        prefs.put("jmariCode", jamriCode);
    }

    //
    // UserType
    //
    public Project.UserType getUserType() {
        // Preference 情報がない場合は　UNKNOWN を返す
        // これは Project.isValid() で必ずテストされる
        String userType = prefs.get(Project.USER_TYPE, Project.UserType.UNKNOWN.toString());
        return Project.UserType.valueOf(userType);
    }

    public void setUserType(Project.UserType userType) {
        prefs.put(Project.USER_TYPE, userType.toString());
    }

    //
    // サーバ情報
    //
    public String getHostAddress() {
        return prefs.get(Project.HOST_ADDRESS, DEFAULT_HOST_ADDRESS);
    }

    public void setHostAddress(String val) {
        prefs.put(Project.HOST_ADDRESS, val);
    }

    public int getHostPort() {
        return prefs.getInt(Project.HOST_PORT, JBOSS_J2EE_PORT);
    }

    public void setHostPort(int val) {
        prefs.putInt(Project.HOST_PORT, val);
    }

    public String getTopInspector() {
        return prefs.get("topInspector", "メモ");
    }

    public void setTopInspector(String topInspector) {
        prefs.put("topInspector", topInspector);
    }

    public String getSecondInspector() {
        return prefs.get("secondInspector", "カレンダ");
    }

    public void setSecondInspector(String secondInspector) {
        prefs.put("secondInspector", secondInspector);
    }

    public String getThirdInspector() {
        return prefs.get("thirdInspector", "文書履歴");
    }

    public void setThirdInspector(String thirdInspector) {
        prefs.put("thirdInspector", thirdInspector);
    }

    public String getForthInspector() {
        return prefs.get("forthInspector", "アレルギ");
    }

    public void setForthInspector(String forthInspector) {
        prefs.put("forthInspector", forthInspector);
    }

    public boolean getLocateByPlatform() {
        return prefs.getBoolean(Project.LOCATION_BY_PLATFORM, false);
    }

    public void setLocateByPlatform(boolean b) {
        prefs.putBoolean(Project.LOCATION_BY_PLATFORM, b);
    }
    
    public String getPDFStore() {
        String defaultStore = ClientContext.getPDFDirectory();
        return prefs.get("pdfStore", defaultStore);
    }
    
    public void setPDFStore(String pdfStore) {
        prefs.put("pdfStore", pdfStore);
    }

    public int getFetchKarteCount() {
        return prefs.getInt(Project.DOC_HISTORY_FETCHCOUNT, 1);
    }

    public void setFetchKarteCount(int cnt) {
        prefs.putInt(Project.DOC_HISTORY_FETCHCOUNT, cnt);
    }

    public boolean getScrollKarteV() {
        return prefs.getBoolean(Project.KARTE_SCROLL_DIRECTION, true);
    }

    public void setScrollKarteV(boolean b) {
        prefs.putBoolean(Project.KARTE_SCROLL_DIRECTION, b);
    }

    public boolean getAscendingKarte() {
        return prefs.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
    }

    public void setAscendingKarte(boolean b) {
        prefs.putBoolean(Project.DOC_HISTORY_ASCENDING, b);
    }

    public int getKarteExtractionPeriod() {
        return prefs.getInt(Project.DOC_HISTORY_PERIOD, -12);
    }

    public void setKarteExtractionPeriod(int period) {
        prefs.putInt(Project.DOC_HISTORY_PERIOD, period);
    }

    public boolean getShowModifiedKarte() {
        return prefs.getBoolean(Project.DOC_HISTORY_SHOWMODIFIED, false);
    }

    public void setShowModifiedKarte(boolean b) {
        prefs.putBoolean(Project.DOC_HISTORY_SHOWMODIFIED, b);
    }

    public boolean getAscendingDiagnosis() {
        return prefs.getBoolean(Project.DIAGNOSIS_ASCENDING, false);
    }

    public void setAscendingDiagnosis(boolean b) {
        prefs.putBoolean(Project.DIAGNOSIS_ASCENDING, b);
    }

    public int getDiagnosisExtractionPeriod() {
        return prefs.getInt(Project.DIAGNOSIS_PERIOD, 0);
    }

    public void setDiagnosisExtractionPeriod(int period) {
        prefs.putInt(Project.DIAGNOSIS_PERIOD, period);
    }

    public boolean isAutoOutcomeInput() {
        return prefs.getBoolean("autoOutcomeInput", false);
    }

    public void setAutoOutcomeInput(boolean b) {
        prefs.putBoolean("autoOutcomeInput", b);
    }

    public boolean isReplaceStamp() {
        return prefs.getBoolean("replaceStamp", false);
    }

    public void setReplaceStamp(boolean b) {
        prefs.putBoolean("replaceStamp", b);
    }

    public boolean isStampSpace() {
        return prefs.getBoolean("stampSpace", true);
    }

    public void setStampSpace(boolean b) {
        prefs.putBoolean("stampSpace", b);
    }

    public boolean isLaboFold() {
        return prefs.getBoolean("laboFold", true);
    }

    public void setLaboFold(boolean b) {
        prefs.putBoolean("laboFold", b);
    }

    public String getDefaultZyozaiNum() {
        return prefs.get("defaultZyozaiNum", "3");
    }

    public void setDefaultZyozaiNum(String defaultZyozaiNum) {
        prefs.put("defaultZyozaiNum", defaultZyozaiNum);
    }

    public String getDefaultMizuyakuNum() {
        return prefs.get("defaultMizuyakuNum", "1");
    }

    public void setDefaultMizuyakuNum(String defaultMizuyakuNum) {
        prefs.put("defaultMizuyakuNum", defaultMizuyakuNum);
    }

    public String getDefaultSanyakuNum() {
        return prefs.get("defaultSanyakuNum", "1.0");
    }

    public void setDefaultSanyakuNum(String defaultSanyakuNum) {
        prefs.put("defaultSanyakuNum", defaultSanyakuNum);
    }

    public String getDefaultRpNum() {
        return prefs.get("defaultRpNum", "3");
    }

    public void setDefaultRpNum(String defaultRpNum) {
        prefs.put("defaultRpNum", defaultRpNum);
    }

    public int getLabotestExtractionPeriod() {
        return prefs.getInt(Project.LABOTEST_PERIOD, -6);
    }

    public void setLabotestExtractionPeriod(int period) {
        prefs.putInt(Project.LABOTEST_PERIOD, period);
    }

    public boolean getConfirmAtNew() {
        return prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true);
    }

    public void setConfirmAtNew(boolean b) {
        prefs.putBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, b);
    }

    public int getCreateKarteMode() {
        return prefs.getInt(Project.KARTE_CREATE_MODE, 0); // 0=emptyNew, 1=applyRp, 2=copyNew
    }

    public void setCreateKarteMode(int mode) {
        prefs.getInt(Project.KARTE_CREATE_MODE, mode);
    }

    public boolean getPlaceKarteMode() {
        return prefs.getBoolean(Project.KARTE_PLACE_MODE, true);
    }

    public void setPlaceKarteMode(boolean mode) {
        prefs.putBoolean(Project.KARTE_PLACE_MODE, mode);
    }

    public boolean getConfirmAtSave() {
        return prefs.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, true);
    }

    public void setConfirmAtSave(boolean b) {
        prefs.putBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, b);
    }

    public int getPrintKarteCount() {
        return prefs.getInt(Project.KARTE_PRINT_COUNT, 0);
    }

    public void setPrintKarteCount(int cnt) {
        prefs.putInt(Project.KARTE_PRINT_COUNT, cnt);
    }

    public int getSaveKarteMode() {
        return prefs.getInt(Project.KARTE_SAVE_ACTION, 0); // 0=save 1=saveTmp
    }

    public void setSaveKarteMode(int mode) {
        prefs.putInt(Project.KARTE_SAVE_ACTION, mode); // 0=save 1=saveTmp
    }

    //
    // CLAIM関連情報
    // 
    /**
     * CLAIM 送信全体への設定を返す。
     * デフォルトが false になっているのは新規インストールの場合で ORCA 接続なしで
     * 使えるようにするため。
     * @param 送信する時 true
     */
    public boolean getSendClaim() {
        return prefs.getBoolean(Project.SEND_CLAIM, DEFAULT_SEND_CLAIM);
    }

    public void setSendClaim(boolean b) {
        prefs.putBoolean(Project.SEND_CLAIM, b);
    }

    /**
     * 保存時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true
     */
    public boolean getSendClaimSave() {
        return prefs.getBoolean(Project.SEND_CLAIM_SAVE, DEFAULT_SEND_CLAIM_SAVE);
    }

    public void setSendClaimSave(boolean b) {
        prefs.putBoolean(Project.SEND_CLAIM_SAVE, b);
    }

    /**
     * 仮保存時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendClaimTmp() {
        return prefs.getBoolean(Project.SEND_CLAIM_TMP, DEFAULT_SEND_CLAIM_TMP);
    }

    public void setSendClaimTmp(boolean b) {
        prefs.putBoolean(Project.SEND_CLAIM_TMP, b);
    }

    /**
     * 修正時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendClaimModify() {
        return prefs.getBoolean(Project.SEND_CLAIM_MODIFY, DEFAULT_SEND_CLAIM_MODIFY);
    }

    public void setSendClaimModify(boolean b) {
        prefs.putBoolean(Project.SEND_CLAIM_MODIFY, b);
    }

    public String getDefaultKarteTitle() {
        return prefs.get("defaultKarteTitle", "経過記録");
    }

    public void setDefaultKarteTitle(String defaultKarteTitle) {
        prefs.put("defaultKarteTitle", defaultKarteTitle);
    }

    public boolean isUseTop15AsTitle() {
        return prefs.getBoolean("useTop15AsTitle", true);
    }

    public void setUseTop15AsTitle(boolean useTop15AsTitle) {
        prefs.putBoolean("useTop15AsTitle", useTop15AsTitle);
    }

    /**
     * 病名 CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendDiagnosis() {
        return prefs.getBoolean(Project.SEND_DIAGNOSIS, DEFAULT_SEND_DIAGNOSIS);
    }

    public void setSendDiagnosis(boolean b) {
        prefs.putBoolean(Project.SEND_DIAGNOSIS, b);
    }

    public String getClaimHostName() {
        return prefs.get(Project.CLAIM_HOST_NAME, DEFAULT_CLAIM_HOST_NAME);
    }

    public void setClaimHostName(String b) {
        prefs.put(Project.CLAIM_HOST_NAME, b);
    }

    public String getClaimEncoding() {
        return prefs.get(Project.CLAIM_ENCODING, DEFAULT_CLAIM_ENCODING);
    }

    public void setClaimEncoding(String val) {
        prefs.put(Project.CLAIM_ENCODING, val);
    }

    public String getClaimAddress() {
        return prefs.get(Project.CLAIM_ADDRESS, DEFAULT_CLAIM_ADDRESS);
    }

    public void setClaimAddress(String val) {
        prefs.put(Project.CLAIM_ADDRESS, val);
    }

    public int getClaimPort() {
        return prefs.getInt(Project.CLAIM_PORT, DEFAULT_CLAIM_PORT);
    }

    public void setClaimPort(int val) {
        prefs.putInt(Project.CLAIM_PORT, val);
    }

    public void setUseAsPVTServer(boolean b) {
        prefs.putBoolean(Project.USE_AS_PVT_SERVER, b);
    }

    public boolean getUseAsPVTServer() {
        return prefs.getBoolean(Project.USE_AS_PVT_SERVER, DEFAULT_USE_AS_PVTSERVER);
    }

    public boolean isClaim01() {
        return prefs.getBoolean("CLAIM01", false);
    }

    public void setClaim01(boolean b) {
        prefs.putBoolean("CLAIM01", b);
    }

    // 
    // AreaNetwork関連情報
    // 
    public boolean getJoinAreaNetwork() {
        return prefs.getBoolean(Project.JOIN_AREA_NETWORK, false);		// 地域連携参加
    }

    public void setJoinAreaNetwork(boolean b) {
        prefs.putBoolean(Project.JOIN_AREA_NETWORK, b);				// 地域連携参加
    }

    public String getAreaNetworkName() {
        return prefs.get(Project.AREA_NETWORK_NAME, null);			// 地域連携名
    }

    public void setAreaNetworkName(String name) {
        prefs.put(Project.AREA_NETWORK_NAME, name);				// 地域連携名
    }

    public String getAreaNetworkFacilityId() {
        return prefs.get(Project.AREA_NETWORK_FACILITY_ID, null);		// 地域連携施設ID
    }

    public void setAreaNetworkFacilityId(String id) {
        prefs.put(Project.AREA_NETWORK_FACILITY_ID, id);			// 地域連携施設ID
    }

    public String getAreaNetworkCreatorId() {
        return prefs.get(Project.AREA_NETWORK_CREATOR_ID, null);		// 地域連携CreatorID
    }

    public void setAreaNetworkCreatorId(String id) {
        prefs.put(Project.AREA_NETWORK_CREATOR_ID, id);                         // 地域連携CreatorID
    }

    // 
    // MML送信関連の情報
    // 
    public boolean getSendMML() {
        return prefs.getBoolean(Project.SEND_MML, DEFAULT_SEND_MML);
    }

    public void setSendMML(boolean b) {
        prefs.putBoolean(Project.SEND_MML, b);
    }

    public String getMMLVersion() {
        return prefs.get(Project.MML_VERSION, DEFAULT_MML_VERSION);
    }

    public void setMMLVersion(String b) {
        prefs.put(Project.MML_VERSION, b);
    }

    public String getMMLEncoding() {
        return prefs.get(Project.MML_ENCODING, DEFAULT_MML_ENCODING);
    }

    public void setMMLEncoding(String val) {
        prefs.put(Project.MML_ENCODING, val);
    }

    public boolean getMIMEEncoding() {
        return prefs.getBoolean("mimeEncoding", false);
    }

    public void setMIMEEncoding(boolean val) {
        prefs.putBoolean("mimeEncoding", val);
    }

    public String getUploaderIPAddress() {
        return prefs.get(Project.SEND_MML_ADDRESS, DEFAULT_SEND_MML_ADDRESS);
    }

    public void setUploaderIPAddress(String val) {
        prefs.put(Project.SEND_MML_ADDRESS, val);
    }

    public String getUploadShareDirectory() {
        return prefs.get(Project.SEND_MML_DIRECTORY, DEFAULT_SEND_MML_DIRECTORY);
    }

    public void setUploadShareDirectory(String val) {
        prefs.put(Project.SEND_MML_DIRECTORY, val);
    }

    //
    // Software Update 関連
    // 
    public boolean getUseProxy() {
        return prefs.getBoolean(Project.USE_PROXY, DEFAULT_USE_PROXY);
    }

    public void setUseProxy(boolean b) {
        prefs.putBoolean(Project.USE_PROXY, b);
    }

    public String getProxyHost() {
        return prefs.get(Project.PROXY_HOST, DEFAULT_PROXY_HOST);
    }

    public void setProxyHost(String val) {
        prefs.put(Project.PROXY_HOST, val);
    }

    public int getProxyPort() {
        return prefs.getInt(Project.PROXY_PORT, DEFAULT_PROXY_PORT);
    }

    public void setProxyPort(int val) {
        prefs.putInt(Project.PROXY_PORT, val);
    }

    public long getLastModify() {
        return prefs.getLong(Project.LAST_MODIFIED, DEFAULT_LAST_MODIFIED);
    }

    public void setLastModify(long val) {
        prefs.putLong(Project.LAST_MODIFIED, val);
    }

    ///////////////////////////////////////////////
    public void exportSubtree(OutputStream os) {
        try {
            prefs.exportSubtree(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            prefs.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
