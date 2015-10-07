package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.LicenseModel;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClientContext {
    
    private static ClientContextStub stub;
    
    public static void setClientContextStub(ClientContextStub s) {
        stub = s;
    }
    
    public static ClientContextStub getClientContextStub() {
        return stub;
    }
    
    public static LinkedHashMap<String, String> getToolProviders() {
        return stub.getToolProviders();
    }
    
    public static VelocityContext getVelocityContext() {
        return stub.getVelocityContext();
    }
    
    public static boolean isMac() {
        return stub.isMac();
    }
    
    public static boolean isWin() {
        return stub.isWin();
    }
    
    public static boolean isLinux() {
        return stub.isLinux();
    }
    
    public static boolean isJaJp() {
        return stub.isJaJp();
    }

    public static boolean isOpenDolphin() {
        return stub.isOpenDolphin();
    }
    
    public static boolean isAsp() {
        return stub.isAsp();
    }
    
    public static boolean isI18N() {
        return stub.isI18N();
    }
    
    public static ResourceBundle getBundle() {
        return stub.getBundle();
    }
    
    public static ResourceBundle getClaimBundle() {
        return stub.getClaimBundle();
    }
    
    public static ResourceBundle getMyBundle(Class cls) {
        return stub.getMyBundle(cls);
    }
    
    public static String getVersion() {
        return stub.getVersion();
    }
    
    public static String getBaseDirectory() {
        return stub.getBaseDirectory();
    }
    
    public static String getSettingDirectory() {
        return stub.getSettingDirectory();
    }
    
    public static String getLogDirectory() {
        return stub.getLogDirectory();
    }
    
    public static String getPDFDirectory() {
        return stub.getPDFDirectory();
    }

    public static String getSchemaDirectory() {
        return stub.getSchemaDirectory();
    }
    
    public static String getOdtTemplateDirectory() {
        return stub.getOdtTemplateDirectory();
    }
    
    public static String getTempDirectory() {
        return stub.getTempDirectory();
    }
    
    public static URL getResource(String name) {
        return stub.getResource(name);
    }

    public static URL getImageResource(String name) {
        return stub.getImageResource(name);
    }

    public static InputStream getResourceAsStream(String name) {
        return stub.getResourceAsStream(name);
    }
    
    public static InputStream getPluginResourceAsStream(String name) {
        return stub.getPluginResourceAsStream(name);
    }

    public static InputStream getTemplateAsStream(String name) {
        return stub.getTemplateAsStream(name);
    }
    
    public static String getString(String name) {
        return stub.getString(name);
    }

    public static String[] getStringArray(String name) {
        return stub.getStringArray(name);
    }

    public static boolean getBoolean(String name) {
        return stub.getBoolean(name);
    }

    public static boolean[] getBooleanArray(String name) {
        return stub.getBooleanArray(name);
    }

    public static int getInt(String name) {
        return stub.getInt(name);
    }

    public static int[] getIntArray(String name) {
        return stub.getIntArray(name);
    }

    public static long getLong(String name) {
        return stub.getLong(name);
    }

    public static long[] getLongArray(String name) {
        return stub.getLongArray(name);
    }

    public static Color getColor(String name){
        return stub.getColor(name);
    }

    public static Color[] getColorArray(String name) {
        return stub.getColorArray(name);
    }

    public static ImageIcon getImageIcon(String name) {
        return stub.getImageIcon(name);
    }

    public static String getFrameTitle(String name) {
        return stub.getFrameTitle(name);
    }

    public static Dimension getDimension(String name) {
        return stub.getDimension(name);
    }

    public static Class[] getClassArray(String name) {
        return stub.getClassArray(name);
    }
    
    public static HashMap<String, Color> getEventColorTable() {
        return stub.getEventColorTable();
    }

    public static NameValuePair[] getNameValuePair(String key) {
        return stub.getNameValuePair(key);
    }
    
    public static LicenseModel[] getLicenseModel() {
        return stub.getLicenseModel();
    }
    
    public static DepartmentModel[] getDepartmentModel() {
        return stub.getDepartmentModel();
    }
    
    public static int getHigherRowHeight() {
        return stub.getHigherRowHeight();
    }

    public static int getMoreHigherRowHeight() {
        return stub.getMoreHigherRowHeight();
    }
    
    public static ImageIcon getImageIconArias(String name) {
        return stub.getImageIconArias(name);
    }   
}