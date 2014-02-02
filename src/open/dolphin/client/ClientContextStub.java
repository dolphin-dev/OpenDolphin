/*
 */
package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.io.InputStream;
import java.net.URL;
import java.security.Security;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.plugin.IPluginContext;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

/**
 * Dolphin Client のコンテキストクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClientContextStub {
    
    private final String RESOURCE_LOCATION  = "/open/dolphin/resources/";
    private final String TEMPLATE_LOCATION  = "/open/dolphin/resources/templates/";
    private final String IMAGE_LOCATION     = "/open/dolphin/resources/images/";
    private final String SCHEMA_LOCATION    = "/open/dolphin/resources/schema/";
    
    private boolean DEBUG = false;
    
    private IPluginContext context;
    
    
    /**
     * ClientContextStub オブジェクトを生成する。
     */
    public ClientContextStub(IPluginContext ctx) {
        
        this.context = ctx;
        
        try {
            // 基本情報を出力する
            Logger bootLogger = (Logger) context.lookup("boot.logger");
            bootLogger.info("起動時刻 = " + DateFormat.getDateTimeInstance().format(new Date()));
            bootLogger.info("os.name = " + System.getProperty("os.name"));
            bootLogger.info("java.version = " + System.getProperty("java.version"));
            bootLogger.info("dolphin.version = " + (String)context.lookup("version"));
            bootLogger.info("base.directory = " + (String)context.lookup("base.dir"));
            bootLogger.info("lib.directory = " + (String)context.lookup("lib.dir"));
            bootLogger.info("log.directory = " + (String)context.lookup("log.dir"));
            bootLogger.info("setting.directory = " + (String)context.lookup("setting.dir"));
            bootLogger.info("security.directory = " + (String)context.lookup("security.dir"));
            bootLogger.info("schema.directory = " + (String)context.lookup("schema.dir"));
            bootLogger.info("project.file = " + (String)context.lookup("application.project.file"));
            bootLogger.info("log.config.file = " + (String)context.lookup("log.config.file"));
            bootLogger.info("veleocity.log.file = " + (String)context.lookup("application.velocity.log.file"));
            bootLogger.info("login.config.file = " + (String)context.lookup("application.security.login.config"));
            bootLogger.info("ssl.trsutStore = " + (String)context.lookup("application.security.ssl.trustStore"));
            
            // Velocity を初期化する
            Velocity.setProperty("runtime.log", (String)context.lookup("application.velocity.log.file"));
            Velocity.init();
            bootLogger.info("Velocity を初期化しました");
            
            // デフォルトの UI フォントを変更する
            setUIFonts();
            
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.startsWith("mac")) {
                System.setProperty("apple.laf.useScreenMenuBar",String.valueOf(true));
            }
            
            if (! DEBUG) {
                // login configuration file
                String loginConfig = (String)context.lookup("application.security.login.config");
                System.setProperty("java.security.auth.login.config", loginConfig);
                bootLogger.info("ログイン構成ファイルを設定しました: " + loginConfig);
                
//                // System Properties を設定する
//                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//                bootLogger.info("Security Provider を追加しました: com.sun.net.ssl.internal.ssl.Provider");
//                
//                // SSL trust store
//                String trustStore = (String)context.lookup("application.security.ssl.trustStore");
//                System.setProperty("javax.net.ssl.trustStore", trustStore);
//                bootLogger.info("trustStoreを設定しました: " + trustStore);
                
            } else {
                String loginConfig = (String)context.lookup("application.security.login.config");
                System.setProperty("java.security.auth.login.config", loginConfig);
                bootLogger.info("ログイン構成ファイルを設定しました: " + loginConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public VelocityContext getVelocityContext() {
        return new VelocityContext();
    }
    
    public Logger getLogger(String category) {
        return (Logger)lookup(category + ".logger");
    }
    
    public boolean isDebug() {
        return DEBUG;
    }
    
    public boolean isMac() {
        return System.getProperty("os.name").toLowerCase().startsWith("mac") ? true : false;
    }
    
    public boolean isWin() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows") ? true : false;
    }
    
    public boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().startsWith("linux") ? true : false;
    }
    
    public String getLocation(String dir) {
        
        String ret = null;
        
        if (dir.equals("base")) {
            ret = (String)lookup("base.dir");
            
        } else if (dir.equals("lib")) {
            ret = isMac() ? (String)lookup("lib.mac.dir") : (String)lookup("lib.dir");
            
        } else if (dir.equals("dolphin.jar")) {
            ret = isMac() ? (String)lookup("dolphin.jar.mac.dir") : (String)lookup("dolphin.jar.dir");
            
        } else if (dir.equals("security")) {
            ret = (String)lookup("security.dir");
            
        } else if (dir.equals("log")) {
            ret = (String)lookup("log.dir");
            
        } else if (dir.equals("setting")) {
            ret = (String)lookup("setting.dir");
            
        } else if (dir.equals("schema")) {
            ret = (String)lookup("schema.dir");
        }
        
        return ret;
    }
    
    public String getVersion() {
        return getString("version");
    }
    
    public String getUpdateURL() {
        //return isMac() ? (String)lookup("updater.url.mac") : (String)lookup("updater.url.win");
        if (isMac()) {
            return (String) lookup("updater.url.mac");
        } else if (isWin()) {
            return (String) lookup("updater.url.win");
        } else if (isLinux()) {
            return (String) lookup("updater.url.linux");
        } else {
            return (String) lookup("updater.url.linux");
        }
    }
    
    public String getFrameTitle(String title) {
        try {
            String resTitle = getString(title);
            if (resTitle != null) {
                title = resTitle;
            }
            StringBuilder buf = new StringBuilder();
            buf.append(title);
            buf.append(" - ");
            buf.append(getString("application.title"));
            buf.append(" ");
            buf.append(getString("version"));
            return buf.toString();
            
        } catch (Exception e) {
            return title;
        }
    }
    
    public URL getResource(String name) {
        if (! name.startsWith("/")) {
            name = RESOURCE_LOCATION + name;
        }
        return this.getClass().getResource(name);
    }
    
    public URL getMenuBarResource() {
        return isMac() ? getResource("MacMainMenuBar.xml") : getResource("WindowsMainMenuBar.xml");
    }
    
    public URL getImageResource(String name) {
        if (! name.startsWith("/")) {
            name = IMAGE_LOCATION + name;
        }
        return this.getClass().getResource(name);
    }
    
    public InputStream getResourceAsStream(String name) {
        if (! name.startsWith("/")) {
            name = RESOURCE_LOCATION + name;
        }
        return this.getClass().getResourceAsStream(name);
    }
    
    public InputStream getTemplateAsStream(String name) {
        if (! name.startsWith("/")) {
            name = TEMPLATE_LOCATION + name;
        }
        return this.getClass().getResourceAsStream(name);
    }
    
    public ImageIcon getImageIcon(String name) {
        return new ImageIcon(getImageResource(name));
    }
    
    public ImageIcon getSchemaIcon(String name) {
        if (! name.startsWith("/")) {
            name = SCHEMA_LOCATION + name;
        }
        return new ImageIcon(this.getClass().getResource(name));
    }
    
    public LicenseModel[] getLicenseModel() {
        String[] desc = getStringArray("licenseDesc");
        String[] code = getStringArray("license");
        String codeSys = getString("licenseCodeSys");
        LicenseModel[] ret = new LicenseModel[desc.length];
        LicenseModel model = null;
        for (int i = 0; i < desc.length; i++) {
            model = new LicenseModel();
            model.setLicense(code[i]);
            model.setLicenseDesc(desc[i]);
            model.setLicenseCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }
    
    public DepartmentModel[] getDepartmentModel() {
        String[] desc = getStringArray("departmentDesc");
        String[] code = getStringArray("department");
        String codeSys = getString("departmentCodeSys");
        DepartmentModel[] ret = new DepartmentModel[desc.length];
        DepartmentModel model = null;
        for (int i = 0; i < desc.length; i++) {
            model = new DepartmentModel();
            model.setDepartment(code[i]);
            model.setDepartmentDesc(desc[i]);
            model.setDepartmentCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }
    
    public DiagnosisOutcomeModel[] getDiagnosisOutcomeModel() {
        String[] desc = getStringArray("diagnosis.outcomeDesc");
        String[] code = getStringArray("diagnosis.outcome");
        String codeSys = getString("diagnosis.outcomeCodeSys");
        DiagnosisOutcomeModel[] ret = new DiagnosisOutcomeModel[desc.length];
        DiagnosisOutcomeModel model = null;
        for (int i = 0; i < desc.length; i++) {
            model = new DiagnosisOutcomeModel();
            model.setOutcome(code[i]);
            model.setOutcomeDesc(desc[i]);
            model.setOutcomeCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }
    
    public DiagnosisCategoryModel[] getDiagnosisCategoryModel() {
        String[] desc = getStringArray("diagnosis.outcomeDesc");
        String[] code = getStringArray("diagnosis.outcome");
        String[] codeSys = getStringArray("diagnosis.outcomeCodeSys");
        DiagnosisCategoryModel[] ret = new DiagnosisCategoryModel[desc.length];
        DiagnosisCategoryModel model = null;
        for (int i = 0; i < desc.length; i++) {
            model = new DiagnosisCategoryModel();
            model.setDiagnosisCategory(code[i]);
            model.setDiagnosisCategoryDesc(desc[i]);
            model.setDiagnosisCategoryCodeSys(codeSys[i]);
            ret[i] = model;
        }
        return ret;
    }
    
    public NameValuePair[] getNameValuePair(String key) {
        NameValuePair[] ret = null;
        String[] code = getStringArray(key + ".value");
        String[] name = getStringArray(key + ".name");
        int len = code.length;
        ret = new NameValuePair[len];
        
        for (int i = 0; i < len; i++) {
            ret[i] = new NameValuePair(name[i], code[i]);
        }
        return ret;
    }
    
    //////////////////////////////////////////////////
    
    public IPluginContext getPluginContext() {
        return context;
    }
    
    public Object lookup(String name) {
        try {
            return context.lookup(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getString(String key) {
        try {
            return (String)context.lookup(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    public String[] getStringArray(String key) {
        try {
            Object o = context.lookup(key);
            if (o instanceof Collection) {
                Collection c = (Collection)o;
                String[] ret = new String[c.size()];
                int index = 0;
                for (Iterator iter = c.iterator(); iter.hasNext();)  {
                    ret[index++] = (String)iter.next();
                }
                return ret;
            } else if (o instanceof String) {
                return new String[]{(String)o};
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    public int getInt(String key) {
        return Integer.parseInt((String)lookup(key));
    }
    
    public int[] getIntArray(String key) {
        String[] obj = getStringArray(key);
        int[] ret = new int[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Integer.parseInt(obj[i]);
        }
        return ret;
    }
    
    public long getLong(String key) {
        return Long.parseLong((String)lookup(key));
    }
    
    public long[] getLongArray(String key) {
        String[] obj = getStringArray(key);
        long[] ret = new long[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Long.parseLong(obj[i]);
        }
        return ret;
    }
    
    public float getFloat(String key) {
        return Float.parseFloat((String)lookup(key));
    }
    
    public float[] getFloatArray(String key) {
        String[] obj = getStringArray(key);
        float[] ret = new float[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Float.parseFloat(obj[i]);
        }
        return ret;
    }
    
    public double getDouble(String key) {
        return Double.parseDouble((String)lookup(key));
    }
    
    public double[] getDoubleArray(String key) {
        String[] obj = getStringArray(key);
        double[] ret = new double[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Double.parseDouble(obj[i]);
        }
        return ret;
    }
    
    public boolean getBoolean(String key) {
        return Boolean.valueOf((String)lookup(key)).booleanValue();
    }
    
    public boolean[] getBooleanArray(String key) {
        String[] obj = getStringArray(key);
        boolean[] ret = new boolean[obj.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Boolean.valueOf(obj[i]).booleanValue();
        }
        return ret;
    }
    
    public Point lgetPoint(String name) {
        int[] data = getIntArray(name);
        return new Point(data[0], data[1]);
    }
    
    public Dimension getDimension(String name) {
        int[] data = getIntArray(name);
        return new Dimension(data[0], data[1]);
    }
    
    public Insets getInsets(String name) {
        int[] data = getIntArray(name);
        return new Insets(data[0], data[1], data[2], data[3]);
    }
    
    public Color getColor(String key) {
        int[] data = getIntArray(key);
        return new Color(data[0], data[1], data[2]);
    }
    
    public Color[] getColorArray(String key) {
        int[] data = getIntArray(key);
        int cnt = data.length / 3;
        Color[] ret = new Color[cnt];
        for (int i = 0; i < cnt; i++) {
            int bias = i*3;
            ret[i] = new Color(data[bias],data[bias+1], data[bias+2]);
        }
        return ret;
    }
    
    public Class[] getClassArray(String name) {
        String[] clsStr = getStringArray(name);
        Class[] ret = new Class[clsStr.length];
        try {
            for (int i = 0; i < clsStr.length; i++) {
                ret[i] = Class.forName(clsStr[i]);
            }
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /////////////////////////////////////////////////////////
    
    /**
     * Windows のデフォルトフォントを設定する。
     */
    private void setUIFonts() {
        
        String osName = System.getProperty("os.name");
        
        if (osName.startsWith("Windows")) {
            Font font = new Font("Dialog", Font.PLAIN, 12);
            UIManager.put("Label.font", font);
            UIManager.put("Button.font", font);
            UIManager.put("ToggleButton.font", font);
            UIManager.put("Menu.font", font);
            UIManager.put("MenuItem.font", font);
            UIManager.put("CheckBox.font", font);
            UIManager.put("CheckBoxMenuItem.font", font);
            UIManager.put("RadioButton.font", font);
            UIManager.put("RadioButtonMenuItem.font", font);
            UIManager.put("ToolBar.font", font);
            UIManager.put("ComboBox.font", font);
            UIManager.put("TabbedPane.font", font);
            UIManager.put("TitledBorder.font", font);
            UIManager.put("List.font", font);
            
            getLogger("boot").info("デフォルトのフォントを変更しました");
        }
    }
}