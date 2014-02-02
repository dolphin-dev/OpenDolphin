package open.dolphin.client;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTextPaneUI;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.project.Project;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

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
    private final String RESNAME_VERSION = "version";
    private final String RESNAME_APP_TITLE = "application.title";
    private final String TITLE_CONCAT = "-";
    //--------------------------------------------------------------------------

    private HashMap<String, Color> eventColorTable;
    private LinkedHashMap<String, String> toolProviders;

    private String pathToDolphin;

    private boolean dolphinPro;
    private boolean dolphin5mTest;
    private boolean dolphin;

    //private URLClassLoader pluginClassLoader;

    /**
     * ClientContextStub オブジェクトを生成する。
     */
    public ClientContextStub(String mode) {
//minagawa^ jdk7 packaging
        if (mode==null) {
            dolphin = true;
            dolphinPro = false;
            dolphin5mTest = false;
        } else {
            dolphinPro = (mode.equals("pro"));
            dolphin5mTest = (mode.equals("5m"));
            dolphin = !dolphinPro;
        }
//minagawa$
        try {
            //----------------------------------------
            // user.home に Dolphin directoryを生成する
            //----------------------------------------
            if (dolphinPro || dolphin5mTest) {
                pathToDolphin = createDirectory(System.getProperty("user.home"), "OpenDolphinPro");
            } else {
                pathToDolphin = createDirectory(System.getProperty("user.home"), "OpenDolphin");
            }
            String pathToSetting = createDirectory(pathToDolphin, "setting");
            createDirectory(pathToDolphin, "log");
            createDirectory(pathToDolphin, "pdf");
            createDirectory(pathToDolphin, "schema");
            createDirectory(pathToDolphin, "odt_template");
            createDirectory(pathToDolphin, "temp");

            //------------------------------
            // Log4J のコンフィグレーションを行う
            //------------------------------
            File log4jProp = new File(pathToSetting, "log4j.properties");

            if (log4jProp.exists()) {
                PropertyConfigurator.configure(log4jProp.getPath());

            } else {
                Properties prop = new Properties();
                BufferedInputStream in = new BufferedInputStream(getResourceAsStream("log4j.properties"));
                prop.load(in);
                in.close();
                prop.setProperty("log4j.appender.bootAppender.File", pathToLogFile("boot.log"));
                prop.setProperty("log4j.appender.part11Appender.File", pathToLogFile("part11.log"));
                prop.setProperty("log4j.appender.delegaterAppender.File", pathToLogFile("delegater.log"));
                prop.setProperty("log4j.appender.pvtAppender.File", pathToLogFile("pvt.log"));
                prop.setProperty("log4j.appender.labTestAppender.File", pathToLogFile("labTest.log"));
                prop.setProperty("log4j.appender.claimAppender.File", pathToLogFile("claim.log"));
                prop.setProperty("log4j.appender.mmlAppender.File", pathToLogFile("mml.log"));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(log4jProp));
                prop.store(out, getVersion());
                out.close();
                PropertyConfigurator.configure(prop);
            }

            //------------------------------
            // 基本情報を出力する
            //------------------------------
            if (getBootLogger().getLevel()==Level.DEBUG) {
                getBootLogger().debug("boot.time = " + DateFormat.getDateTimeInstance().format(new Date()));
                getBootLogger().debug("os.name = " + System.getProperty("os.name"));
                getBootLogger().debug("java.version = " + System.getProperty("java.version"));
                getBootLogger().debug("dolphin.version = " + getVersion());
                getBootLogger().debug("base.directory = " + getBaseDirectory());
                getBootLogger().debug("setting.directory = " + getSettingDirectory());
                getBootLogger().debug("log.directory = " + getLogDirectory());
                getBootLogger().debug("pdf.directory = " + getPDFDirectory());
                getBootLogger().debug("schema.directory = " + getSchemaDirectory());
                getBootLogger().debug("temp.directory = " + getTempDirectory());
                getBootLogger().debug("log.config.file = " + getString("log.config.file"));
                getBootLogger().debug("veleocity.log.file = " + getString("application.velocity.log.file"));
            }

//            //------------------------------
//            // Plugin Class Loader を生成する
//            //------------------------------
//            List<String> test = new ArrayList<String>();
//            File pluginDir = new File(getLocation("plugins"));
//            listJars(test, pluginDir);
//            List<URL> list = new ArrayList<URL>();
//            StringBuilder sb;
//            for (String path : test) {
//                sb = new StringBuilder();
//                if (isWin()) {
//                    sb.append("jar:file:/");
//                } else {
//                    sb.append("jar:file://");
//                }
//                sb.append(path);
//                sb.append("!/");
//                URL url = new URL(sb.toString());
//                list.add(url);
//            }
//            URL[] urls = list.toArray(new URL[list.size()]);
//            pluginClassLoader = new URLClassLoader(urls);

            //------------------------------
            // Velocityを初期化する
            //------------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(getLogDirectory());
            sb.append(File.separator);
            sb.append(getString("application.velocity.log.file"));
            Velocity.setProperty("runtime.log", sb.toString());
            Velocity.init();
            getBootLogger().debug("Velocity did initialize");

            //------------------------------
            // LookANdFeel、フォント、mac Menubarを変更する
            //------------------------------
            //setUI();

        } catch (DolphinException | IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private String createDirectory(String parent, String child) throws DolphinException {

        File dir = new File(parent, child);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ディレクトリ");
                sb.append(parent).append(File.separator).append(child);
                sb.append("を作成できません。");
                throw new DolphinException(sb.toString());
            }
        }
        return dir.getPath();
    }

    private String pathToLogFile(String logFile) {
        StringBuilder sb = new StringBuilder();
        sb.append(getLogDirectory()).append("/").append(logFile);
        return sb.toString();
    }
    
    public LinkedHashMap<String, String> getToolProviders() {
        return toolProviders;
    }

//    public URLClassLoader getPluginClassLoader() {
//        return pluginClassLoader;
//    }

    public VelocityContext getVelocityContext() {
        return new VelocityContext();
    }

    public ResourceBundle getBundle(Class cls) {

        String path = getClassResource(cls);

        // No cache
        return ResourceBundle.getBundle(path, Locale.getDefault(), cls.getClassLoader(), new ResourceBundle.Control() {

            @Override
            public long getTimeToLive(String baseName, Locale locale) {
                return ResourceBundle.Control.TTL_DONT_CACHE;
            }
        });
    }

    private static String getClassResource(Class cls) {
        StringBuilder sb = new StringBuilder();
        String clsName = cls.getName();
        int index = clsName.lastIndexOf(".");
        sb.append(clsName.subSequence(0, index));
        sb.append(".resources.");
        sb.append(clsName.substring(index+1));
        return sb.toString();
    }

    //---------------------------------------------------------

    public Logger getBootLogger() {
        return Logger.getLogger("boot.logger");
    }

    public Logger getPart11Logger() {
        return Logger.getLogger("part11.logger");
    }

    public Logger getClaimLogger() {
        return Logger.getLogger("claim.logger");
    }

    public Logger getMmlLogger() {
        return Logger.getLogger("mml.logger");
    }

    public Logger getPvtLogger() {
        return Logger.getLogger("pvt.logger");
    }

    public Logger getDelegaterLogger() {
        return Logger.getLogger("delegater.logger");
    }

    public Logger getLaboTestLogger() {
        return Logger.getLogger("labTest.logger");
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

    public boolean isOpenDolphin() {
        return dolphin;
    }

    public boolean isDolphinPro() {
        return dolphinPro;
    }
    
    public boolean is5mTest() {
        return dolphin5mTest;
    }

    //-----------------------------------------------------------

    private String getLocation(String dirName) {
        StringBuilder sb = new StringBuilder();
        sb.append(getBaseDirectory()).append(File.separator).append(dirName);
        return sb.toString();
    }

    public String getBaseDirectory() {
        return pathToDolphin;
    }

//    public String getPluginsDirectory() {
//        return getLocation("plugins");
//    }

    public String getSettingDirectory() {
        return getLocation("setting");
    }

    public String getLogDirectory() {
        return getLocation("log");
    }
    
    public String getPDFDirectory() {
        return getLocation("pdf");
    }

    public String getSchemaDirectory() {
        return getLocation("schema");
    }
    
    public String getOdtTemplateDirectory() {
        return getLocation("odt_template");
    }
    
    public String getTempDirectory() {
        return getLocation("temp");
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
        if (name!=null) {
            return new ImageIcon(getImageResource(name));
        }
        return null;
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
        LicenseModel model;
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
        DepartmentModel model;
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
        DiagnosisOutcomeModel model;
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
        DiagnosisCategoryModel model;
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
        NameValuePair[] ret;
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
        return ResourceBundle.getBundle(RESOURCE).getString(key);
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
        if (isMac()) {
            return 20;
        }
        String nimbus = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        String laf = UIManager.getLookAndFeel().getClass().getName();
        if (laf.equals(nimbus)) {
            return 25;
        }
        
        return 20;
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
    public void setupUI() {
        
        try {
            Font font = null;
            int size = 13;
            if (isWin() || isLinux()) {
                size = isLinux() ? 13: 12;
            }
        
            if (isMac()) {
                 System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(true));
                 UIManager.put("OptionPane.cancelButtonText", "キャンセル");
                 UIManager.put("OptionPane.okButtonText", "OK");
                 font = new Font("Hiragino Kaku Gothic", Font.PLAIN, size);
                 
            } else if (isWin()) {
                font = new Font("MSGothic", Font.PLAIN, size);
                
            } else {
                font = new Font("Lucida Grande", Font.PLAIN, size);
            }
            
            if (isWin() || isLinux()) {
                if (Project.getString("lookAndFeel")!=null) {
                    UIManager.setLookAndFeel(Project.getString("lookAndFeel"));
                }  else {
                    // Default=NimbusLookAndFeel
                    String nimbusCls = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
                    UIManager.setLookAndFeel(nimbusCls);
                }
            }
            
            // ToolBarの Dropdown menu制御
            UIManager.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
            
            // Font 設定
            FontUIResource fontUIResource = new FontUIResource(font);
            UIManager.put("Label.font", fontUIResource);
            UIManager.put("Button.font", fontUIResource);
            UIManager.put("ToggleButton.font", fontUIResource);
            UIManager.put("Menu.font", fontUIResource);
            UIManager.put("MenuItem.font", fontUIResource);
            UIManager.put("CheckBox.font", fontUIResource);
            UIManager.put("CheckBoxMenuItem.font", fontUIResource);
            UIManager.put("RadioButton.font", fontUIResource);
            UIManager.put("RadioButtonMenuItem.font", fontUIResource);
            UIManager.put("ToolBar.font", fontUIResource);
            UIManager.put("ComboBox.font", fontUIResource);
            UIManager.put("TabbedPane.font", fontUIResource);
            UIManager.put("TitledBorder.font", fontUIResource);
            UIManager.put("List.font", fontUIResource);
//minagawa^ mak jdk7 で : が表示されない            
//UIManager.put("TextField.font", fontUIResource);
//UIManager.put("TextArea.font", fontUIResource);
//minagawa$            
            UIManager.put("TextPane.font", fontUIResource);
//masuda先生^ tweet            
            if (UIManager.getLookAndFeel().getName().toLowerCase().startsWith("nimbus")) {
                UIManager.put("TextPaneUI", BasicTextPaneUI.class.getName());
                UIManager.put("TextPane.selectionBackground", new Color(57, 105, 138));
                UIManager.put("TextPane.selectionForeground", Color.WHITE);
                UIManager.put("TextPane.border", new EmptyBorder(4,6,4,6));
            }
//masuda$                 
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(System.err);
            getBootLogger().warn(e.getMessage());
        }        
//        // List up fonts
//        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
//        for (int i = 0; i < fonts.length; i++) {
//            System.out.println(fonts[i]);
//        }
        
//        for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults().entrySet()) {
//            System.err.println(UIManager.get(entry.getKey()));
//        }
    
//        font = new Font("SansSerif", Font.PLAIN, size);
//        } else {
//            //font = new Font("Hiragino Kaku Gothic", Font.PLAIN, size);
//            font = new Font("Lucida Grande", Font.PLAIN, size);
//        }
//        font = new Font("Lucida Grande", Font.PLAIN, size);
//        for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults().entrySet()) {
//            if (entry.getKey().toString().toLowerCase().endsWith("font")) {
//                System.err.println(entry.getKey());
//                UIManager.put(entry.getKey(), fontUIResource);
//            }
//        }
        getBootLogger().debug("デフォルトのフォントを変更しました。");
    }
    
//minagawa^ Icon Server
    public ImageIcon getImageIconArias(String name) {
        return this.getImageIcon(getString(name));
    }
//minagawa$    
}