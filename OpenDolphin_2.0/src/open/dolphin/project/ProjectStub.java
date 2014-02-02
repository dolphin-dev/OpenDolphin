package open.dolphin.project;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.UserModel;
import org.apache.log4j.Level;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ProjectStub implements java.io.Serializable {

    //-------------------------------------------
    // デフォルトのプロジェクト名
    //-------------------------------------------
    private final String DEFAULT_PROJECT_NAME = "OpenDolphin";

    private final String DEFAULT_FACILITY_ID = "1.3.6.1.4.1.9414.10.1";
    private final String REST_BASE_RESOURCE = "/dolphin/openSource";

    //-------------------------------------------
    // Claim
    //-------------------------------------------
    private final boolean DEFAULT_SEND_CLAIM        = false;
    private final boolean DEFAULT_SEND_CLAIM_SAVE   = true;
    private final boolean DEFAULT_SEND_CLAIM_TMP    = false;
    private final boolean DEFAULT_SEND_CLAIM_MODIFY = false;
    private final boolean DEFAULT_SEND_DIAGNOSIS    = true;
    private final String DEFAULT_CLAIM_HOST_NAME    = "日医標準レセプト(ORCA)";
    private final String DEFAULT_CLAIM_ADDRESS      = null;
    private final int DEFAULT_CLAIM_PORT            = 8210;
    private final String DEFAULT_CLAIM_ENCODING     = "UTF-8";
    private final boolean DEFAULT_USE_AS_PVTSERVER  = true;
    
    //-------------------------------------------
    // MML
    //-------------------------------------------
    private final boolean DEFAULT_SEND_MML          = false;
    private final String DEFAULT_MML_VERSION        = "2.3";
    private final String DEFAULT_MML_ENCODING       = "UTF-8";
    private final String DEFAULT_SEND_MML_ADDRESS   = null;
    private final String DEFAULT_SEND_MML_DIRECTORY = null;
    
    //-------------------------------------------
    // Update
    //-------------------------------------------
    private final boolean DEFAULT_USE_PROXY     = false;
    private final String DEFAULT_PROXY_HOST     = null;
    private final int DEFAULT_PROXY_PORT        = 8080;

    private boolean valid;

    // ログインユーザー
    private UserModel userModel;

    // ユーザー設定
    private Properties userDefaults;

    // ユーザー設定を上書きするプロパティ
    private Properties customDefaults;

    private String baseURI;

    /**
     * ProjectStub を生成する。
     */
    public ProjectStub() {

        try {
            Properties test = (Properties)ClientContext.getLocalStorage().load("user-defaults.xml");
            if (test == null) {
                test = new Properties();
                ClientContext.getLocalStorage().save(test, "user-defaults.xml");
            }
            userDefaults = test;

            //-----------------------------------------
            // 上書き設定を読み込む
            //-----------------------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getSettingDirectory());
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            if (f.exists()) {
                FileInputStream fin = new FileInputStream(f);
                customDefaults = new Properties();
                customDefaults.load(fin);
                fin.close();

                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    String val = customDefaults.getProperty(key);
                    userDefaults.setProperty(key, val);
                }
            }
                
            //--------------------------------------------------
            // 1.4.7 以前の設定を userDefaults へコンバージョンする
            //--------------------------------------------------
            boolean debug = ClientContext.getBootLogger().getLevel() == Level.DEBUG ? true : false;

            if (Preferences.userRoot().nodeExists("/open/dolphin")) {
                if (debug) {
                    ClientContext.getBootLogger().debug("/open/dolphin exists");
                }
                Preferences top = Preferences.userRoot().node("/open/dolphin");
                String[] children = top.childrenNames();
                for (String str : children) {
                    String path = "/open/dolphin/" + str;
                    Preferences c = Preferences.userRoot().node(path);
                    String[] keys = c.keys();
                    for (String key : keys) {
                        String value = c.get(key, "");
                        userDefaults.setProperty(key, value);
                        if (debug) {
                            ClientContext.getBootLogger().debug(key + " = " + value);
                        }
                    }
                }

                top.removeNode();

            } else {
                if (debug) {
                    ClientContext.getBootLogger().debug("/open/dolphin not exists");
                }
            }
            
        } catch (BackingStoreException ex) {
            ex.printStackTrace(System.err);
            ClientContext.getBootLogger().warn(ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace(System.err);
            ClientContext.getBootLogger().warn(e.getMessage());
        }
    }

    /**
     * 設定ファイルが有効かどうかを返す。
     * @return 有効な時 true
     */
    public boolean isValid() {
        boolean ok = true;
        ok = ok && (getFacilityId()!=null);
        ok = ok && (getUserId()!=null);
        ok = ok && (getBaseURI()!=null);
        valid = ok;
        return valid;
    }

    /**
     * プロジェクト名を返す。
     * @return プロジェクト名 (Dolphin ASP, HOT, MAIKO, HANIWA ... etc)
     */
    public String getName() {
        return getString(Project.PROJECT_NAME, DEFAULT_PROJECT_NAME);
    }

    /**
     * プロジェクト名を返す。
     * @return プロジェクト名 (Dolphin ASP, HOT, MAIKO, HANIWA ... etc)
     */
    public void setName(String projectName) {
        setString(Project.PROJECT_NAME, projectName);
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
     * ログイン画面用のFacilityIDを返す。
     * @return ログイン画面に表示するFacilityID
     */
    public String getFacilityId() {
        return getString(Project.FACILITY_ID, DEFAULT_FACILITY_ID);
    }

    /**
     * ログイン画面用のFacilityIDを設定する。
     * @param ログイン画面に表示するFacilityID
     */
    public void setFacilityId(String val) {
        setString(Project.FACILITY_ID, val);
    }

    /**
     * ログイン画面用のUserIDを返す。
     * @return ログイン画面に表示するUserId
     */
    public String getUserId() {
        return getString(Project.USER_ID, null);
    }

    /**
     * ログイン画面用のUserIDを設定する。
     * @param ログイン画面に表示するUserId
     */
    public void setUserId(String val) {
        setString(Project.USER_ID, val);
    }

    public String getServerURI() {
        return getString(Project.SERVER_URI, null);
    }

    public void setServerURI(String val) {
        setString(Project.SERVER_URI, val);
    }

    public String getBaseURI() {
        if (baseURI==null) {
            StringBuilder sb = new StringBuilder();
            String test = getString(Project.SERVER_URI, null);
            if (test != null && test.endsWith("/")) {
                int len = test.length();
                test = test.substring(0, len-1);
            }
            sb.append(test);
            sb.append(REST_BASE_RESOURCE);
            baseURI = sb.toString();
        }
        return baseURI;
    }

    //-------------------------------------------
    // インスペクタ画面のレイアウト情報
    //-------------------------------------------
    public String getTopInspector() {
        return getString("topInspector", "メモ");
    }

    public void setTopInspector(String topInspector) {
        setString("topInspector", topInspector);
    }

    public String getSecondInspector() {
        return getString("secondInspector", "カレンダ");
    }

    public void setSecondInspector(String secondInspector) {
        setString("secondInspector", secondInspector);
    }

    public String getThirdInspector() {
        return getString("thirdInspector", "文書履歴");
    }

    public void setThirdInspector(String thirdInspector) {
        setString("thirdInspector", thirdInspector);
    }

    public String getForthInspector() {
        return getString("forthInspector", "アレルギ");
    }

    public void setForthInspector(String forthInspector) {
        setString("forthInspector", forthInspector);
    }

    public boolean getLocateByPlatform() {
        return getBoolean(Project.LOCATION_BY_PLATFORM, false);
    }

    public void setLocateByPlatform(boolean b) {
        setBoolean(Project.LOCATION_BY_PLATFORM, b);
    }
    
    public String getPDFStore() {
        String defaultStore = ClientContext.getPDFDirectory();
        return getString("pdfStore", defaultStore);
    }
    
    public void setPDFStore(String pdfStore) {
        setString("pdfStore", pdfStore);
    }

    public int getFetchKarteCount() {
        return getInt(Project.DOC_HISTORY_FETCHCOUNT, 1);
    }

    public void setFetchKarteCount(int cnt) {
        setInt(Project.DOC_HISTORY_FETCHCOUNT, cnt);
    }

    public boolean getScrollKarteV() {
        return getBoolean(Project.KARTE_SCROLL_DIRECTION, true);
    }

    public void setScrollKarteV(boolean b) {
        setBoolean(Project.KARTE_SCROLL_DIRECTION, b);
    }

    public boolean getAscendingKarte() {
        return getBoolean(Project.DOC_HISTORY_ASCENDING, false);
    }

    public void setAscendingKarte(boolean b) {
        setBoolean(Project.DOC_HISTORY_ASCENDING, b);
    }

    public int getKarteExtractionPeriod() {
        return getInt(Project.DOC_HISTORY_PERIOD, -12);
    }

    public void setKarteExtractionPeriod(int period) {
        setInt(Project.DOC_HISTORY_PERIOD, period);
    }

    public boolean getShowModifiedKarte() {
        return getBoolean(Project.DOC_HISTORY_SHOWMODIFIED, false);
    }

    public void setShowModifiedKarte(boolean b) {
        setBoolean(Project.DOC_HISTORY_SHOWMODIFIED, b);
    }

    public boolean getAscendingDiagnosis() {
        return getBoolean(Project.DIAGNOSIS_ASCENDING, false);
    }

    public void setAscendingDiagnosis(boolean b) {
        setBoolean(Project.DIAGNOSIS_ASCENDING, b);
    }

    public int getDiagnosisExtractionPeriod() {
        return getInt(Project.DIAGNOSIS_PERIOD, 0);
    }

    public void setDiagnosisExtractionPeriod(int period) {
        setInt(Project.DIAGNOSIS_PERIOD, period);
    }

    public boolean isAutoOutcomeInput() {
        return getBoolean("autoOutcomeInput", false);
    }

    public void setAutoOutcomeInput(boolean b) {
        setBoolean("autoOutcomeInput", b);
    }

    public boolean isReplaceStamp() {
        return getBoolean("replaceStamp", false);
    }

    public void setReplaceStamp(boolean b) {
        setBoolean("replaceStamp", b);
    }

    public boolean isStampSpace() {
        return getBoolean("stampSpace", true);
    }

    public void setStampSpace(boolean b) {
        setBoolean("stampSpace", b);
    }

    public boolean isLaboFold() {
        return getBoolean("laboFold", true);
    }

    public void setLaboFold(boolean b) {
        setBoolean("laboFold", b);
    }

    public String getDefaultZyozaiNum() {
        return getString("defaultZyozaiNum", "3");
    }

    public void setDefaultZyozaiNum(String defaultZyozaiNum) {
        setString("defaultZyozaiNum", defaultZyozaiNum);
    }

    public String getDefaultMizuyakuNum() {
        return getString("defaultMizuyakuNum", "1");
    }

    public void setDefaultMizuyakuNum(String defaultMizuyakuNum) {
        setString("defaultMizuyakuNum", defaultMizuyakuNum);
    }

    public String getDefaultSanyakuNum() {
        return getString("defaultSanyakuNum", "1.0");
    }

    public void setDefaultSanyakuNum(String defaultSanyakuNum) {
        setString("defaultSanyakuNum", defaultSanyakuNum);
    }

    public String getDefaultRpNum() {
        return getString("defaultRpNum", "3");
    }

    public void setDefaultRpNum(String defaultRpNum) {
        setString("defaultRpNum", defaultRpNum);
    }

    public boolean getMasterItemColoring() {
        return getBoolean("masterItemColoring", true);
    }

    public void setMasterItemColoring(boolean b) {
        setBoolean("masterItemColoring", b);
    }

    public int getLabotestExtractionPeriod() {
        return getInt(Project.LABOTEST_PERIOD, -6);
    }

    public void setLabotestExtractionPeriod(int period) {
        setInt(Project.LABOTEST_PERIOD, period);
    }

    public boolean getConfirmAtNew() {
        return getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, true);
    }

    public void setConfirmAtNew(boolean b) {
        setBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, b);
    }

    public int getCreateKarteMode() {
        return getInt(Project.KARTE_CREATE_MODE, 0); // 0=emptyNew, 1=applyRp, 2=copyNew
    }

    public void setCreateKarteMode(int mode) {
        setInt(Project.KARTE_CREATE_MODE, mode);
    }

    public boolean getPlaceKarteMode() {
        return getBoolean(Project.KARTE_PLACE_MODE, true);
    }

    public void setPlaceKarteMode(boolean mode) {
        setBoolean(Project.KARTE_PLACE_MODE, mode);
    }

    public boolean getConfirmAtSave() {
        return getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, true);
    }

    public void setConfirmAtSave(boolean b) {
        setBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, b);
    }

    public int getPrintKarteCount() {
        return getInt(Project.KARTE_PRINT_COUNT, 0);
    }

    public void setPrintKarteCount(int cnt) {
        setInt(Project.KARTE_PRINT_COUNT, cnt);
    }

    public int getSaveKarteMode() {
        return getInt(Project.KARTE_SAVE_ACTION, 0); // 0=save 1=saveTmp
    }

    public void setSaveKarteMode(int mode) {
        setInt(Project.KARTE_SAVE_ACTION, mode); // 0=save 1=saveTmp
    }

    public String getDefaultKarteTitle() {
        return getString("defaultKarteTitle", "経過記録");
    }

    public void setDefaultKarteTitle(String defaultKarteTitle) {
        setString("defaultKarteTitle", defaultKarteTitle);
    }

    public boolean isUseTop15AsTitle() {
        return getBoolean("useTop15AsTitle", true);
    }

    public void setUseTop15AsTitle(boolean useTop15AsTitle) {
        setBoolean("useTop15AsTitle", useTop15AsTitle);
    }

    public boolean isAutoCloseAfterSaving() {
        return getBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, false);
    }

    public void setAutoCloseAfterSaving(boolean b) {
        setBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, b);
    }

    public int getAgeToNeedMonth() {
        return getInt("ageToNeedMonth", 6);
    }

    public void setAgeToNeedMonth(int age) {
        setInt("ageToNeedMonth", age);
    }

    //-------------------------------------------
    // CLAIM関連情報
    //-------------------------------------------
    
    /**
     * ORCA バージョンを返す。
     * @return ORCA バージョン
     */
    public String getOrcaVersion() {
        return getString("orcaVersion", "40");
    }

    /**
     * ORCA バージョンを設定する。
     * @param ORCA バージョン
     */
    public void setOrcaVersion(String version) {
        setString("orcaVersion", version);
    }

    /**
     * JMARICode を返す。
     * @return JMARI Code
     */
    public String getJMARICode() {
        return getString("jmariCode", "JPN000000000000");
    }

    /**
     * JMARICode を返す。
     * @return JMARI Code
     */
    public void setJMARICode(String jamriCode) {
        setString("jmariCode", jamriCode);
    }

    /**
     * CLAIM 送信全体への設定を返す。
     * デフォルトが false になっているのは新規インストールの場合で ORCA 接続なしで
     * 使えるようにするため。
     * @param 送信する時 true
     */
    public boolean getSendClaim() {
        return getBoolean(Project.SEND_CLAIM, DEFAULT_SEND_CLAIM);
    }

    public void setSendClaim(boolean b) {
        setBoolean(Project.SEND_CLAIM, b);
    }

    /**
     * 保存時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true
     */
    public boolean getSendClaimSave() {
        return getBoolean(Project.SEND_CLAIM_SAVE, DEFAULT_SEND_CLAIM_SAVE);
    }

    public void setSendClaimSave(boolean b) {
        setBoolean(Project.SEND_CLAIM_SAVE, b);
    }

    /**
     * 仮保存時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendClaimTmp() {
        return getBoolean(Project.SEND_CLAIM_TMP, DEFAULT_SEND_CLAIM_TMP);
    }

    public void setSendClaimTmp(boolean b) {
        setBoolean(Project.SEND_CLAIM_TMP, b);
    }

    /**
     * 修正時に CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendClaimModify() {
        return getBoolean(Project.SEND_CLAIM_MODIFY, DEFAULT_SEND_CLAIM_MODIFY);
    }

    public void setSendClaimModify(boolean b) {
        setBoolean(Project.SEND_CLAIM_MODIFY, b);
    }

    /**
     * 病名 CLAIM 送信を行うかどうかを返す。
     * @param 行う時 true 
     */
    public boolean getSendDiagnosis() {
        return getBoolean(Project.SEND_DIAGNOSIS, DEFAULT_SEND_DIAGNOSIS);
    }

    public void setSendDiagnosis(boolean b) {
        setBoolean(Project.SEND_DIAGNOSIS, b);
    }

    public String getClaimHostName() {
        return getString(Project.CLAIM_HOST_NAME, DEFAULT_CLAIM_HOST_NAME);
    }

    public void setClaimHostName(String b) {
        setString(Project.CLAIM_HOST_NAME, b);
    }

    public String getClaimEncoding() {
        return getString(Project.CLAIM_ENCODING, DEFAULT_CLAIM_ENCODING);
    }

    public void setClaimEncoding(String val) {
        setString(Project.CLAIM_ENCODING, val);
    }

    public String getClaimAddress() {
        return getString(Project.CLAIM_ADDRESS, DEFAULT_CLAIM_ADDRESS);
    }

    public void setClaimAddress(String val) {
        setString(Project.CLAIM_ADDRESS, val);
    }

    public int getClaimPort() {
        return getInt(Project.CLAIM_PORT, DEFAULT_CLAIM_PORT);
    }

    public void setClaimPort(int val) {
        setInt(Project.CLAIM_PORT, val);
    }

    public boolean getUseAsPVTServer() {
        return getBoolean(Project.USE_AS_PVT_SERVER, DEFAULT_USE_AS_PVTSERVER);
    }

    public void setUseAsPVTServer(boolean b) {
        setBoolean(Project.USE_AS_PVT_SERVER, b);
    }
    
    public String getBindAddress() {
        return getString("BIND_ADDRESS", null);
    }

    public void setBindAddress(String val) {
        setString("BIND_ADDRESS", val);
    }

    public boolean isClaim01() {
        return getBoolean("CLAIM01", false);
    }

    public void setClaim01(boolean b) {
        setBoolean("CLAIM01", b);
    }

    //-------------------------------------------
    // AreaNetwork関連情報
    //-------------------------------------------
    public boolean getJoinAreaNetwork() {
        return getBoolean(Project.JOIN_AREA_NETWORK, false);		// 地域連携参加
    }

    public void setJoinAreaNetwork(boolean b) {
        setBoolean(Project.JOIN_AREA_NETWORK, b);				// 地域連携参加
    }

    public String getAreaNetworkName() {
        return getString(Project.AREA_NETWORK_NAME, null);			// 地域連携名
    }

    public void setAreaNetworkName(String name) {
        setString(Project.AREA_NETWORK_NAME, name);				// 地域連携名
    }

    public String getAreaNetworkFacilityId() {
        return getString(Project.AREA_NETWORK_FACILITY_ID, null);		// 地域連携施設ID
    }

    public void setAreaNetworkFacilityId(String id) {
        setString(Project.AREA_NETWORK_FACILITY_ID, id);			// 地域連携施設ID
    }

    public String getAreaNetworkCreatorId() {
        return getString(Project.AREA_NETWORK_CREATOR_ID, null);		// 地域連携CreatorID
    }

    public void setAreaNetworkCreatorId(String id) {
        setString(Project.AREA_NETWORK_CREATOR_ID, id);                         // 地域連携CreatorID
    }

    //-------------------------------------------
    // MML送信関連の情報
    //-------------------------------------------
    public boolean getSendMML() {
        return getBoolean(Project.SEND_MML, DEFAULT_SEND_MML);
    }

    public void setSendMML(boolean b) {
        setBoolean(Project.SEND_MML, b);
    }

    public String getMMLVersion() {
        return getString(Project.MML_VERSION, DEFAULT_MML_VERSION);
    }

    public void setMMLVersion(String b) {
        setString(Project.MML_VERSION, b);
    }

    public String getMMLEncoding() {
        return getString(Project.MML_ENCODING, DEFAULT_MML_ENCODING);
    }

    public void setMMLEncoding(String val) {
        setString(Project.MML_ENCODING, val);
    }

    public boolean getMIMEEncoding() {
        return getBoolean("mimeEncoding", false);
    }

    public void setMIMEEncoding(boolean val) {
        setBoolean("mimeEncoding", val);
    }

    public String getUploaderIPAddress() {
        return getString(Project.SEND_MML_ADDRESS, DEFAULT_SEND_MML_ADDRESS);
    }

    public void setUploaderIPAddress(String val) {
        setString(Project.SEND_MML_ADDRESS, val);
    }

    public String getUploadShareDirectory() {
        return getString(Project.SEND_MML_DIRECTORY, DEFAULT_SEND_MML_DIRECTORY);
    }

    public void setUploadShareDirectory(String val) {
        setString(Project.SEND_MML_DIRECTORY, val);
    }

    //-------------------------------------------
    // Send Amazon
    //-------------------------------------------
    public boolean isSendAmazon() {
        return getBoolean("sendAmazon", false);
    }

    public void setSendAmazon(boolean b) {
        setBoolean("sendAmazon", b);
    }

    //-------------------------------------------
    // Software Update 関連
    //-------------------------------------------
    public boolean getUseProxy() {
        return getBoolean(Project.USE_PROXY, DEFAULT_USE_PROXY);
    }

    public void setUseProxy(boolean b) {
        setBoolean(Project.USE_PROXY, b);
    }

    public String getProxyHost() {
        return getString(Project.PROXY_HOST, DEFAULT_PROXY_HOST);
    }

    public void setProxyHost(String val) {
        setString(Project.PROXY_HOST, val);
    }

    public int getProxyPort() {
        return getInt(Project.PROXY_PORT, DEFAULT_PROXY_PORT);
    }

    public void setProxyPort(int val) {
        setInt(Project.PROXY_PORT, val);
    }

    //------------------------------------------
    // UserDefaults
    //------------------------------------------
    public Properties getUserDefaults() {
        return userDefaults;
    }

    /**
     * Propertyを保存する。
     */
    public void saveUserDefaults() {
        try {
            //-------------------------------
            // 個別設定をremoveしてから保存する
            //-------------------------------
            if (customDefaults != null) {
                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    userDefaults.remove(key);
                }
            }
            
            ClientContext.getLocalStorage().save(userDefaults, "user-defaults.xml");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    
    private String arrayToLine(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String val : arr) {
            sb.append(val);
            sb.append(",");
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
    
    private String rectToLine(Rectangle rect) {
        StringBuilder sb = new StringBuilder();
        sb.append(rect.x).append(",");
        sb.append(rect.y).append(",");
        sb.append(rect.width).append(",");
        sb.append(rect.height);
        return sb.toString();
    }

    private String colorToLine(Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append(color.getRed()).append(",");
        sb.append(color.getGreen()).append(",");
        sb.append(color.getBlue());
        return sb.toString();
    }

    public String getString(String key, String defStr) {
        return getUserDefaults().getProperty(key, defStr);
    }

    public void setString(String key, String value) {
        getUserDefaults().setProperty(key, value);
    }

    public String[] getStringArray(String key, String[] defStr) {
        String line = getUserDefaults().getProperty(key, arrayToLine(defStr));
        return line.split("\\s*,\\s*");
    }

    public void setStringArray(String key, String[] value) {
        getUserDefaults().setProperty(key, arrayToLine(value));
    }

    public int getInt(String key, int defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Integer.parseInt(val);
    }

    public void setInt(String key, int value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public float getFloat(String key, float defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Float.parseFloat(val);
    }

    public void setFloat(String key, float value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public double getDouble(String key, double defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Double.parseDouble(val);
    }

    public void setDouble(String key, double value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public boolean getBoolean(String key, boolean defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Boolean.parseBoolean(val);
    }

    public void setBoolean(String key, boolean value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public Rectangle getRectangle(String key, Rectangle defRect) {
        String line = getString(key, rectToLine(defRect));
        String[] cmp = line.split("\\s*,\\s*");
        int x = Integer.parseInt(cmp[0]);
        int y = Integer.parseInt(cmp[1]);
        int width = Integer.parseInt(cmp[2]);
        int height = Integer.parseInt(cmp[3]);
        return new Rectangle(x, y, width, height);
    }

    public void setRectangle(String key, Rectangle value) {
        getUserDefaults().setProperty(key, rectToLine(value));
    }

    public Color getColor(String key, Color defVal) {
        String line = getString(key, colorToLine(defVal));
        String[] cmp = line.split("\\s*,\\s*");
        int r = Integer.parseInt(cmp[0]);
        int g = Integer.parseInt(cmp[1]);
        int b = Integer.parseInt(cmp[2]);
        return new Color(r, g, b);
    }

    public void setColor(String key, Color value) {
        getUserDefaults().setProperty(key, colorToLine(value));
    }
}
