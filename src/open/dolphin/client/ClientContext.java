/*
 * DolphinContext.java
 *
 * Copyright 2002 Dolphin Project. All rights resrerved.
 * Copyright 2004 Digital Globe, Inc. All rights resrerved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.io.*;
import java.net.*;
import java.awt.*;

import javax.swing.*;

import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.plugin.IPluginContext;

import org.apache.log4j.Logger;
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
    
    ///////////////////////////////////////////////////////////////////////////
    
    public static VelocityContext getVelocityContext() {
        return stub.getVelocityContext();
    }
    
    public static Logger getLogger(String category) {
        return stub.getLogger(category);
    }
    
    public static boolean isDebug() {
        return stub.isDebug();
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
    
    public static IPluginContext getPluginContext() {
        return stub.getPluginContext();
    }
    
    public static Object lookup(String name) {
        return stub.lookup(name);
    }
    
    public static String lookupString(String name) {
        return stub.getString(name);
    }
    
    public static String getVersion() {
        return stub.getVersion();
    }
    
    public static String getUserDirectory() {
        return stub.getLocation("base");
    }
    
    public static String getLocation(String loc) {
        return stub.getLocation(loc);
    }
    
    public static String getUpdateURL() {
        return stub.getUpdateURL();
    }
    
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
}