package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SessionStorage;

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
    
    public static ApplicationContext getApplicationContext() {
        return stub.getApplicationContext();
    }
    
    public static void setApplicationContext(ApplicationContext ctx) {
        stub.setApplicationContext(ctx);
    }
    
    public static ResourceMap getResourceMap(Class clazz) {
        return stub.getResourceMap(clazz);
    }
    
    public static ActionMap getActionMap(Object obj) {
        return stub.getActionMap(obj);
    }
    
    public static SessionStorage getSessionStorage() {
        return stub.getSessionStorage();
    }
    
    public static LocalStorage getLocalStorage() {
        return stub.getLocalStorage();
    }
    
    public static URLClassLoader getPluginClassLoader() {
        return stub.getPluginClassLoader();
    }
    
    public static LinkedHashMap<String, String> getToolProviders() {
        return stub.getToolProviders();
    }

    
    public static VelocityContext getVelocityContext() {
        return stub.getVelocityContext();
    }
    
    public static Logger getLogger(String category) {
        return stub.getLogger(category);
    }
        
    public static Logger getBootLogger() {
        return stub.getBootLogger();
    }
    
    public static Logger getPart11Logger() {
        return stub.getPart11Logger();
    }
    
    public static Logger getClaimLogger() {
        return stub.getClaimLogger();
    }
    
    public static Logger getMmlLogger() {
        return stub.getMmlLogger();
    }
    
    public static Logger getPvtLogger() {
        return stub.getPvtLogger();
    }
    
    public static Logger getDelegaterLogger() {
        return stub.getDelegaterLogger();
    }
    
    public static Logger getLaboTestLogger() {
        return stub.getLaboTestLogger();
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
    
    //////////////////////////////////////////////////////////
    
    public static String getVersion() {
        return stub.getVersion();
    }
    
    public static String getUserDirectory() {
        return stub.getLocation("base");
    }
    
    public static String getLocation(String loc) {
        return stub.getLocation(loc);
    }
    
    public static String getBaseDirectory() {
        return stub.getBaseDirectory();
    }
    
    public static String getPluginsDirectory() {
        return stub.getPluginsDirectory();
    }
    
    public static String getSettingDirectory() {
        return stub.getSettingDirectory();
    }
    
    public static String getSecurityDirectory() {
        return stub.getSecurityDirectory();
    }
    
    public static String getLogDirectory() {
        return stub.getLogDirectory();
    }
    
    public static String getLibDirectory() {
        return stub.getLibDirectory();
    }
    
    public static String getPDFDirectory() {
        return stub.getPDFDirectory();
    }
    
    public static String getDolphinJarDirectory() {
        return stub.getDolphinJarDirectory();
    }
    
//    public static String getUpdateURL() {
//        return stub.getUpdateURL();
//    }
    
    public static URL getMenuBarResource() {
        return stub.getMenuBarResource();
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
    
    ////////////////////////////////////////////////////
    
    public static NameValuePair[] getNameValuePair(String key) {
        return stub.getNameValuePair(key);
    }
    
    public static LicenseModel[] getLicenseModel() {
        return stub.getLicenseModel();
    }
    
    public static DepartmentModel[] getDepartmentModel() {
        return stub.getDepartmentModel();
    }
    
    public static DiagnosisOutcomeModel[] getDiagnosisOutcomeModel() {
        return stub.getDiagnosisOutcomeModel();
    }
    
    public static DiagnosisCategoryModel[] getDiagnosisCategoryModel() {
        return stub.getDiagnosisCategoryModel();
    }

    public static int getHigherRowHeight() {
        return stub.getHigherRowHeight();
    }

    public static int getMoreHigherRowHeight() {
        return stub.getMoreHigherRowHeight();
    }
}