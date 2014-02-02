package open.dolphin.project;

import java.awt.Color;
import java.awt.Rectangle;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.UserModel;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ProjectStub implements java.io.Serializable {

    // デフォルトのプロジェクト名
    private final String DEFAULT_PROJECT_NAME = "OpenDolphin";
    
    // OpenDolphin のデフォルト施設ID
    private final String DEFAULT_FACILITY_ID = "1.3.6.1.4.1.9414.10.1";
    
    // OpenDolphinPRO のデフォルト施設ID
    private final String DEFAULT_FACILITY_ID_PRO = "1.3.6.1.4.1.9414.10.1";
    
    // REST context
    private final String REST_BASE_RESOURCE = "/dolphin/openSource/14";

    // 有効な設定ファイルかどうか
    private boolean valid;

    // ログインユーザー
    private UserModel userModel;

    // ユーザー設定
    private Properties userDefaults;

    // デフォルトプロパティ値
    private Properties applicationDefaults;

    // ユーザー設定を上書きするプロパティ
    private Properties customDefaults;
    
    // REST baseURI = ServerURI + REST_BASE_RESOURCE
    private String baseURI;
    
    /** ヒロクリニック 医療機関基本情報(ORCA登録) */
    private String basicInfo;

    /**
     * ProjectStub を生成する。
     */
    public ProjectStub() {

        try {
            // デフォルトプロパティを読み込む
            InputStream in = ClientContext.getResourceAsStream("Defaults.properties");
            BufferedInputStream bin = new BufferedInputStream(in);
            applicationDefaults = new Properties();
            applicationDefaults.load(bin);
            bin.close();
            
            // このバージョンからServer-ORCA接続をデフォルトにする
            applicationDefaults.put("claim.sender", "server");

            // User Default を生成する
            userDefaults = new Properties(applicationDefaults);

            // 設定ファイルを読み込む
            File parent = getSettingDirectory();
            File target = new File(parent, "user-defaults.properties");
            if (target.exists()) {
                loadProperties(userDefaults, "user-defaults.properties");
            }
            
            //-----------------------------------------
            // 上書き設定を読み込む: custom.properties
            //-----------------------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getSettingDirectory());
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            if (f.exists()) {
                FileInputStream fin = new FileInputStream(f);
                InputStreamReader inr = new InputStreamReader(fin, "JISAutoDetect");
                BufferedReader br = new BufferedReader(inr);
                customDefaults = new Properties();
                customDefaults.load(br);
                br.close();

                // UserDefaultへ設定する
                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    String val = customDefaults.getProperty(key);
                    userDefaults.setProperty(key, val);
//s.oh^ 文字コード対応
                    String keyTmp = key;
                    byte[] bt = keyTmp.getBytes();
                    int idx = -1;
                    for(int i = 0; i < bt.length; i++) {
                        if(bt[i] < 0) {
                            idx = i;
                        }else{
                            break;
                        }
                    }
                    if(idx >= 0) {
                        key = new String(bt, idx + 1, bt.length - (idx + 1));
                        customDefaults.put(key, val);
                        userDefaults.setProperty(key, val);
                    }
//s.oh$
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
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
        return ClientContext.isOpenDolphin() 
               ? getString(Project.FACILITY_ID, DEFAULT_FACILITY_ID)
               : getString(Project.FACILITY_ID, DEFAULT_FACILITY_ID_PRO);
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
        baseURI = null;
    }

    /**
     * REST の base URI を返す。
     * @return ServerURI + resource context
     */
    public String getBaseURI() {

        if (baseURI==null) {
            StringBuilder sb = new StringBuilder();
            String test = getServerURI();
            if (test != null) {
                if (test.endsWith("/")) {
                    int len = test.length();
                    test = test.substring(0, len-1);
                }
                sb.append(test);
                sb.append(REST_BASE_RESOURCE);
                baseURI = sb.toString();
            }
        }
        return baseURI;
    }

    //------------------------------------------
    // UserDefaults
    //------------------------------------------
    public Properties getUserDefaults() {
        return userDefaults;
    }

    //------------------------------------------
    // (Application) Defaults
    //------------------------------------------
    public Properties getApplicationDefaults() {
        return applicationDefaults;
    }

    public void loadProperties(Properties prop, String name) {

        FileInputStream fin;
        BufferedInputStream bin=null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);
            fin = new FileInputStream(target);
            bin = new BufferedInputStream(fin);
            prop.load(bin);

        } catch (FileNotFoundException fe) {

        } catch (IOException ie) {
            ie.printStackTrace(System.err);

        } finally {
            closeStream(bin);
        }
    }

    public void storeProperties(Properties prop, String name) {

        BufferedOutputStream fout = null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);

            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new DolphinException("ディレクトリを作成できません。 " + parent);
                }
            }

            fout = new BufferedOutputStream(new FileOutputStream(target));
            prop.store(fout, "1.0");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            closeStream(fout);
        }
    }

    public Properties loadPropertiesAsObject(String name) {

        XMLDecoder d;
        Properties ret = null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);
            d = new XMLDecoder(
                          new BufferedInputStream(
                              new FileInputStream(target)));
            ret = (Properties)d.readObject();
            d.close();

        } catch (FileNotFoundException fe) {
        }

        return ret;
    }

    public void storePropertiesAsObject(Properties prop, String name) {

        XMLEncoder e;

        File parent = getSettingDirectory();
        File target = new File(parent, name);
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new DolphinException("ディレクトリを作成できません。 " + parent);
            }
        }

        try {
            e = new XMLEncoder(
                          new BufferedOutputStream(
                              new FileOutputStream(target)));
            e.writeObject(prop);
            e.close();

        } catch (FileNotFoundException fe) {

        } 
    }

    public boolean deleteSettingFile(String fileName) {
        File delete = new File(getSettingDirectory(), fileName);
        return delete.delete();
    }

    private void closeStream(Closeable st) {

        if (st!=null) {
            try {
                st.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * Propertyを保存する。
     */
    public void saveUserDefaults() {

        BufferedOutputStream fout = null;
        
        try {
            //-------------------------------
            // 個別設定をremoveしてから保存する
            //-------------------------------
            Properties toSave;
            synchronized (userDefaults) {
                toSave = (Properties)userDefaults.clone();
            }
            if (customDefaults != null) {
                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    toSave.remove(key);
                }
            }
            //--------------------------------
            // 稼働状況も除く
            //--------------------------------
            toSave.remove(GUIConst.PVT_SERVER_IS_RUNNING);      // PVTServer is running
            //toSave.remove(GUIConst.BIND_ADDRESS_PVT_SERVER);    // Bind address for PVTServer
            toSave.remove(GUIConst.SEND_CLAIM_IS_RUNNING);      // Send claim is running
            //toSave.remove(GUIConst.ADDRESS_CLAIM);              // Claim server address
            toSave.remove(GUIConst.SEND_MML_IS_RUNNING);        // Send MML
            //toSave.remove(GUIConst.CSGW_PATH);                  // Path for MML data
            toSave.remove(GUIConst.PVT_RELAY_IS_RUNNING);
            //--------------------------------
            File parent = getSettingDirectory();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new DolphinException("ディレクトリを作成できません。 " + parent);
                }
            }
            File target = new File(parent, "user-defaults.properties");
            fout = new BufferedOutputStream(new FileOutputStream(target));
            toSave.store(fout, "1.0");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            closeStream(fout);
        }
    }

    private File getSettingDirectory() {

        String userHome = System.getProperty("user.home");
        String appId = ClientContext.getString("application.id");
        String vendorId = ClientContext.getString("application.vender.id");
        File ret;

        StringBuilder sb = new StringBuilder();
        if (ClientContext.isWin()) {
            File appDataDir = null;
            try {
                String appDataEnv = System.getenv("APPDATA");
                if ((appDataEnv != null) && (appDataEnv.length() > 0)) {
                    appDataDir = new File(appDataEnv);
                }
            }
            catch(SecurityException ignore) {
            }
            if ((appDataDir != null) && appDataDir.isDirectory()) {
                // APPDATA|vendorId\appId\
                sb.append(vendorId).append(File.separator).append(appId).append(File.separator);
                ret = new File(appDataDir, sb.toString());
            }
            else {
                // userHome\Application Data\vendorId\appId\
                sb.append(userHome).append(File.separator);
                sb.append("Application Data").append(File.separator);
                sb.append(vendorId).append(File.separator);
                sb.append(appId).append(File.separator);
                ret = new File(sb.toString());
            }

        } else if (ClientContext.isMac()) {
            sb.append(userHome).append(File.separator);
            sb.append("Library/Application Support/");
            sb.append(appId).append(File.separator);
            ret = new File(sb.toString());
        } else {
            sb.append(userHome).append(File.separator).append(".").append(appId).append(File.separator);
            ret = new File(sb.toString());
        }

        return ret;
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

    public String getString(String key) {
        return getUserDefaults().getProperty(key);
    }

    public String getString(String key, String defStr) {
        return getUserDefaults().getProperty(key, defStr);
    }

    public void setString(String key, String value) {
        getUserDefaults().setProperty(key, value);
    }

    public String[] getStringArray(String key) {
        String line = getUserDefaults().getProperty(key);
        return line!=null ? line.split("\\s*,\\s*") : null;
    }

    public String[] getStringArray(String key, String[] defStr) {
        String line = getUserDefaults().getProperty(key, arrayToLine(defStr));
        return line.split("\\s*,\\s*");
    }

    public void setStringArray(String key, String[] value) {
        getUserDefaults().setProperty(key, arrayToLine(value));
    }

    public int getInt(String key) {
        String val = getString(key);
        return val !=null ? Integer.parseInt(val) : 0;
    }

    public int getInt(String key, int defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Integer.parseInt(val);
    }

    public void setInt(String key, int value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public float getFloat(String key) {
        String val = getString(key);
        return val!=null ? Float.parseFloat(val) : 0L;
    }

    public float getFloat(String key, float defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Float.parseFloat(val);
    }

    public void setFloat(String key, float value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public double getDouble(String key) {
        String val = getString(key);
        return val!=null ? Double.parseDouble(val) : 0D;
    }

    public double getDouble(String key, double defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Double.parseDouble(val);
    }

    public void setDouble(String key, double value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public boolean getBoolean(String key) {
        String val = getString(key);
        return val!=null ? Boolean.parseBoolean(val) : false;
    }

    public boolean getBoolean(String key, boolean defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Boolean.parseBoolean(val);
    }

    public void setBoolean(String key, boolean value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public Rectangle getRectangle(String key) {
        String line = getString(key);
        if (line!=null) {
            String[] cmp = line.split("\\s*,\\s*");
            int x = Integer.parseInt(cmp[0]);
            int y = Integer.parseInt(cmp[1]);
            int width = Integer.parseInt(cmp[2]);
            int height = Integer.parseInt(cmp[3]);
            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
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

    public Color getColor(String key) {
        String line = getString(key);
        if (line!=null) {
            String[] cmp = line.split("\\s*,\\s*");
            int r = Integer.parseInt(cmp[0]);
            int g = Integer.parseInt(cmp[1]);
            int b = Integer.parseInt(cmp[2]);
            return new Color(r, g, b);
        } else {
            return null;
        }
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


    //---------------------------------------------------

    public String getDefaultString(String key) {
        return getApplicationDefaults().getProperty(key);
    }

    public String[] getDefaultStringArray(String key) {
        String line = getApplicationDefaults().getProperty(key);
        return line!=null ? line.split("\\s*,\\s*") : null;
    }

    public int getDefaultInt(String key) {
        String val = getDefaultString(key);
        return val !=null ? Integer.parseInt(val) : 0;
    }

    public float getDefaultFloat(String key) {
        String val = getDefaultString(key);
        return val!=null ? Float.parseFloat(val) : 0L;
    }

    public double getDefaultDouble(String key) {
        String val = getDefaultString(key);
        return val!=null ? Double.parseDouble(val) : 0D;
    }

    public boolean getDefaultBoolean(String key) {
        String val = getDefaultString(key);
        return val!=null ? Boolean.parseBoolean(val) : false;
    }

    public Rectangle getDefaultRectangle(String key) {
        String line = getDefaultString(key);
        if (line!=null) {
            String[] cmp = line.split("\\s*,\\s*");
            int x = Integer.parseInt(cmp[0]);
            int y = Integer.parseInt(cmp[1]);
            int width = Integer.parseInt(cmp[2]);
            int height = Integer.parseInt(cmp[3]);
            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
    }

    public Color getDefaultColor(String key) {
        String line = getDefaultString(key);
        if (line!=null) {
            String[] cmp = line.split("\\s*,\\s*");
            int r = Integer.parseInt(cmp[0]);
            int g = Integer.parseInt(cmp[1]);
            int b = Integer.parseInt(cmp[2]);
            return new Color(r, g, b);
        } else {
            return null;
        }
    }
    
 //新宿ヒロクリニック 処方箋印刷^      
    /**
     * 医療機関基本情報を返す。
     * @return 医療機関基本情報 String
     */
    // @002 2010/07/16
    public String getBasicInfo() {
        return basicInfo;
    }

    /**
     * 医療機関基本情報を設定する。
     * @param basicInfo 医療機関基本情報
     */
    // @002 2010/07/16
    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

}
