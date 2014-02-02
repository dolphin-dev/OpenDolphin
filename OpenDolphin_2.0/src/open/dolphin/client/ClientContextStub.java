package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ActionMap;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;

import open.dolphin.plugin.PluginLister;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SessionStorage;

/**
 * Dolphin Client のコンテキストクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ClientContextStub {

    //--------------------------------------------------------------------------
    private final String RESOURCE_LOCATION = "/open/dolphin/resources/";
    private final String TEMPLATE_LOCATION = "/open/dolphin/resources/templates/";
    private final String IMAGE_LOCATION = "/open/dolphin/resources/images/";
    private final String SCHEMA_LOCATION = "/open/dolphin/resources/schema/";
    private final String RESOURCE = "open.dolphin.resources.Dolphin_ja";
    private final String PROPERTY_OS = "os.name";
    private final String OS_WIN = "windows";
    private final String OS_MAC = "mac";
    private final String OS_LINUX = "linux";
    private final String CATEGORY_BOOT = "boot";
    private final String CATEGORY_PART11 = "part11";
    private final String CATEGORY_CLAIM = "claim";
    private final String CATEGORY_MML = "mml";
    private final String CATEGORY_PVT = "pvt";
    private final String CATEGORY_DELEGATER = "delegater";
    private final String CATEGORY_LABTEST = "laboTest";
    private final String CATEGORY_CLOUD = "cloud";
    private final String RESNAME_VERSION = "version";
    private final String RESNAME_APP_TITLE = "application.title";
    private final String TITLE_CONCAT = "-";
    //--------------------------------------------------------------------------

    private ResourceBundle resBundle;
    private URLClassLoader pluginClassLoader;
    private Logger bootLogger;
    private Logger part11Logger;
    private Logger delegaterLogger;
    private Logger pvtLogger;
    private Logger laboTestLogger;
    private Logger claimLogger;
    private Logger mmlLogger;
    private Logger cloudLogger;
    private HashMap<String, Color> eventColorTable;
    private ApplicationContext applicationContext;
    private LinkedHashMap<String, String> toolProviders;
    

    /**
     * ClientContextStub オブジェクトを生成する。
     */
    public ClientContextStub() {

        try {
            //------------------------------
            // ResourceBundle を得る
            //------------------------------
            resBundle = ResourceBundle.getBundle(RESOURCE);

            //------------------------------
            // Logger を生成する
            //------------------------------
            bootLogger = Logger.getLogger("boot.logger");
            part11Logger = Logger.getLogger("part11.logger");
            delegaterLogger = Logger.getLogger("delegater.logger");
            pvtLogger = Logger.getLogger("pvt.logger");
            laboTestLogger = Logger.getLogger("laboTest.logger");
            claimLogger = Logger.getLogger("claim.logger");
            mmlLogger = Logger.getLogger("mml.logger");
            cloudLogger = Logger.getLogger("cloud.logger");

            //------------------------------
            // Log4J のコンフィグレーションを行う
            //------------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(getLocation("setting"));
            sb.append(File.separator);
            sb.append(getString("log.config.file"));
            String logConfigFile = sb.toString();
            PropertyConfigurator.configure(logConfigFile);

            //------------------------------
            // 基本情報を出力する
            //------------------------------
            bootLogger.info("起動時刻 = " + DateFormat.getDateTimeInstance().format(new Date()));
            bootLogger.info("os.name = " + System.getProperty("os.name"));
            bootLogger.info("java.version = " + System.getProperty("java.version"));
            bootLogger.info("dolphin.version = " + getString("version"));
            bootLogger.info("base.directory = " + getString("base.dir"));
            bootLogger.info("lib.directory = " + getString("lib.dir"));
            bootLogger.info("plugins.directory = " + getString("plugins.dir"));
            bootLogger.info("log.directory = " + getString("log.dir"));
            bootLogger.info("setting.directory = " + getString("setting.dir"));
            bootLogger.info("schema.directory = " + getString("schema.dir"));
            bootLogger.info("log.config.file = " + getString("log.config.file"));
            bootLogger.info("veleocity.log.file = " + getString("application.velocity.log.file"));

            //------------------------------
            // Plugin Class Loader を生成する
            //------------------------------
            List<String> test = new ArrayList<String>();
            File pluginDir = new File(getLocation("plugins"));
            listJars(test, pluginDir);
            List<URL> list = new ArrayList<URL>();
            for (String path : test) {
                sb = new StringBuilder();
                if (isWin()) {
                    sb.append("jar:file:/");
                } else {
                    sb.append("jar:file://");
                }
                sb.append(path);
                sb.append("!/");
                URL url = new URL(sb.toString());
                list.add(url);
                bootLogger.debug(url);
            }
            URL[] urls = list.toArray(new URL[list.size()]);
            pluginClassLoader = new URLClassLoader(urls);
            
            //------------------------------
            // Plugin をリストアップする
            //------------------------------
            PluginLister<MainTool> lister = PluginLister.list(MainTool.class, pluginClassLoader);
            toolProviders = lister.getProviders();
            if (toolProviders != null) {
                Iterator<String> iter = toolProviders.keySet().iterator();
                while (iter.hasNext()) {
                    String cmd = iter.next();
                    String clsName = toolProviders.get(cmd);
                    bootLogger.debug(cmd + " = " + clsName);
                }
            }

            //------------------------------
            // Velocityを初期化する
            //------------------------------
            sb = new StringBuilder();
            sb.append(getLocation("log"));
            sb.append(File.separator);
            sb.append(getString("application.velocity.log.file"));
            Velocity.setProperty("runtime.log", sb.toString());
            Velocity.init();
            bootLogger.info("Velocity を初期化しました。");

            //------------------------------
            // LookANdFeel、フォント、mac Menubarを変更する
            //------------------------------
            setUI();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ResourceMap getResourceMap(Class clazz) {
        return applicationContext.getResourceMap(clazz);
    }

    public ActionMap getActionMap(Object obj) {
        return applicationContext.getActionMap(obj);
    }

    public SessionStorage getSessionStorage() {
        return applicationContext.getSessionStorage();
    }

    public LocalStorage getLocalStorage() {
        return applicationContext.getLocalStorage();
    }

    public URLClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }
    
    public LinkedHashMap<String, String> getToolProviders() {
        return toolProviders;
    }

    public VelocityContext getVelocityContext() {
        return new VelocityContext();
    }
    
    //---------------------------------------------------------

    public Logger getLogger(String category) {
        if (category.equals(CATEGORY_BOOT)) {
            return bootLogger;
        } else if (category.equals(CATEGORY_PART11)) {
            return part11Logger;
        } else if (category.equals(CATEGORY_CLAIM)) {
            return claimLogger;
        } else if (category.equals(CATEGORY_MML)) {
            return mmlLogger;
        } else if (category.equals(CATEGORY_PVT)) {
            return pvtLogger;
        } else if (category.equals(CATEGORY_DELEGATER)) {
            return delegaterLogger;
        } else if (category.equals(CATEGORY_LABTEST)) {
            return laboTestLogger;
        } else if (category.equals(CATEGORY_CLOUD)) {
            return laboTestLogger;
        }
        return bootLogger;
    }

    public Logger getBootLogger() {
        return bootLogger;
    }

    public Logger getPart11Logger() {
        return part11Logger;
    }

    public Logger getClaimLogger() {
        return claimLogger;
    }

    public Logger getMmlLogger() {
        return mmlLogger;
    }

    public Logger getCloudLogger() {
        return cloudLogger;
    }

    public Logger getPvtLogger() {
        return pvtLogger;
    }

    public Logger getDelegaterLogger() {
        return delegaterLogger;
    }

    public Logger getLaboTestLogger() {
        return laboTestLogger;
    }

    //-----------------------------------------------------------

    public boolean isMac() {
        return System.getProperty(PROPERTY_OS).toLowerCase().startsWith(OS_MAC) ? true : false;
    }

    public boolean isWin() {
        return System.getProperty(PROPERTY_OS).toLowerCase().startsWith(OS_WIN) ? true : false;
    }

    public boolean isLinux() {
        return System.getProperty(PROPERTY_OS).toLowerCase().startsWith(OS_LINUX) ? true : false;
    }

    //-----------------------------------------------------------

    public String getLocation(String dir) {

        String ret = null;
        StringBuilder sb = new StringBuilder();

        sb.append(System.getProperty(getString("base.dir")));

        if (dir.equals("base")) {
            ret = sb.toString();

        } else if (dir.equals("lib")) {
            sb.append(File.separator);
            if (isMac()) {
                sb.append(getString("lib.mac.dir"));
            } else {
                sb.append(getString("lib.dir"));
            }
            ret = sb.toString();

        } else if (dir.equals("dolphin.jar")) {
            if (isMac()) {
                sb.append(File.separator);
                sb.append(getString("dolphin.jar.mac.dir"));
            }
            ret = sb.toString();

        } else if (dir.equals("log")) {
            sb.append(File.separator);
            sb.append(getString("log.dir"));
            ret = sb.toString();

        } else if (dir.equals("setting")) {
            sb.append(File.separator);
            sb.append(getString("setting.dir"));
            ret = sb.toString();

        } else if (dir.equals("schema")) {
            sb.append(File.separator);
            sb.append(getString("schema.dir"));
            ret = sb.toString();

        } else if (dir.equals("plugins")) {
            sb.append(File.separator);
            sb.append(getString("plugins.dir"));
            ret = sb.toString();
        } else if (dir.equals("pdf")) {
            sb.append(File.separator);
            sb.append(getString("pdf.dir"));
            ret = sb.toString();
        }

        return ret;
    }

    public String getBaseDirectory() {
        return getLocation("base");
    }

    public String getPluginsDirectory() {
        return getLocation("plugins");
    }

    public String getSettingDirectory() {
        return getLocation("setting");
    }

    public String getSecurityDirectory() {
        return getLocation("security");
    }

    public String getLogDirectory() {
        return getLocation("log");
    }

    public String getLibDirectory() {
        return getLocation("lib");
    }
    
    public String getPDFDirectory() {
        return getLocation("pdf");
    }

    public String getDolphinJarDirectory() {
        return getLocation("dolphin.jar");
    }

    //-----------------------------------------------------------

    public String getVersion() {
        return getString(RESNAME_VERSION);
    }

    public String getFrameTitle(String title) {
        try {
            String resTitle = getString(title);
            if (resTitle != null) {
                title = resTitle;
            }
        } catch (Exception e) {
        }
        StringBuilder buf = new StringBuilder();
        buf.append(title);
        buf.append(TITLE_CONCAT);
        buf.append(getString(RESNAME_APP_TITLE));
        buf.append(TITLE_CONCAT);
        buf.append(getString(RESNAME_VERSION));
        return buf.toString();
    }

    public URL getResource(String name) {
        if (!name.startsWith("/")) {
            name = RESOURCE_LOCATION + name;
        }
        return this.getClass().getResource(name);
    }

    public URL getImageResource(String name) {
        if (!name.startsWith("/")) {
            name = IMAGE_LOCATION + name;
        }
        return this.getClass().getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        if (!name.startsWith("/")) {
            name = RESOURCE_LOCATION + name;
        }
        return this.getClass().getResourceAsStream(name);
    }

    public InputStream getTemplateAsStream(String name) {
        if (!name.startsWith("/")) {
            name = TEMPLATE_LOCATION + name;
        }
        return this.getClass().getResourceAsStream(name);
    }

    public ImageIcon getImageIcon(String name) {
        return new ImageIcon(getImageResource(name));
    }

    public ImageIcon getSchemaIcon(String name) {
        if (!name.startsWith("/")) {
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

    public HashMap<String, Color> getEventColorTable() {
        if (eventColorTable == null) {
            setupEventColorTable();
        }
        return eventColorTable;
    }

    private void setupEventColorTable() {
        // イベントカラーを定義する
        eventColorTable = new HashMap<String, Color>(10, 0.75f);
        eventColorTable.put("TODAY", getColor("color.TODAY_BACK"));
        eventColorTable.put("BIRTHDAY", getColor("color.BIRTHDAY_BACK"));
        eventColorTable.put("PVT", getColor("color.PVT"));
        eventColorTable.put("DOC_HISTORY", getColor("color.PVT"));
    }

    public String getString(String key) {
        return resBundle.getString(key);
    }

    public String[] getStringArray(String key) {
        String line = getString(key);
        return line.split(",");
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
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
        return Long.parseLong(getString(key));
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
        return Float.parseFloat(getString(key));
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
        return Double.parseDouble(getString(key));
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
        return Boolean.valueOf(getString(key)).booleanValue();
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
            int bias = i * 3;
            ret[i] = new Color(data[bias], data[bias + 1], data[bias + 2]);
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
            e.printStackTrace(System.err);
        }
        return null;
    }

    public int getHigherRowHeight() {
        return 20;
    }

    public int getMoreHigherRowHeight() {
        return isMac() ? 20 : 25;
    }
    
    private void listJars(List list, File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                listJars(list, file);
            } else if (file.isFile()) {
                String path = file.getPath();
                if (path.toLowerCase().endsWith(".jar")) {
                    list.add(path);
                }
            }
        }
    }

    /**
     * LookAndFeel、フォント、Mac メニューバー使用を設定する。
     */
    private void setUI() {

        try {
            String defaultLaf = UIManager.getSystemLookAndFeelClassName();
            String nimbusCls = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            //String userLaf = Project.getString("lookAndFeel2", nimbusCls);
            String userLaf = nimbusCls;
            
            // Nimbus へ切り替える
            if ((!isMac()) && userLaf.equals(nimbusCls)) {
                UIManager.setLookAndFeel(nimbusCls);
                
            } else {
                UIManager.setLookAndFeel(defaultLaf);
            }

            if (isMac()) {
                // MenuBar
                System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(true));
                System.setProperty("com.apple.macos.smallTabs", String.valueOf(true));
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            bootLogger.warn(e.getMessage());
        }

        if (isWin() || isLinux()) {
            int size = isLinux() ? 13: 12;
            Font font = new Font("SansSerif", Font.PLAIN, size);
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

            bootLogger.info("デフォルトのフォントを変更しました。");
        }
    }
}