/*
 * Project.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.project;

import java.awt.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.*;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Project  {
    
    public static int KUMAMOTO = 0;
    public static int MIYAZAKI = 1;
    
    private static ProjectStub stub;

    /** Creates new Project */
    public Project() {
    }
        
    public static void setProjectStub(ProjectStub p) {
        stub = p;
    }
    
    public static ProjectStub getProjectStub() {
        return stub;
    }
    
    public static String getName() {
        return stub.getName();
    }
    
    public static int getLocalCode() {
        
        String name = stub.getName();
        if (name.equals("kumamoto")) {
            return KUMAMOTO;
            
        } else if (name.equals("miyazaki")) {
            return MIYAZAKI;
            
        } else {
            return -1;
        }
    }
        
    public static UserProfileEntry getUserProfileEntry() {
        return stub.getUserProfileEntry();
    }
    
    public static void setUserProfileEntry(UserProfileEntry profile) {
        stub.setUserProfileEntry(profile);
    }
    
    public static String getCreatorId() {
        return stub.getUserProfileEntry().getUserId();
    }
    
    public static Creator getCreatorInfo() {
        return stub.getCreatorInfo();
    }
    
    // 2003-10-30 licenseCode による制御
    // user の医療資格と lasmanager で判断する
    public static boolean isReadOnly() {
        
        String licenseCode = stub.getUserProfileEntry().getLicenseCode();
        String userId = stub.getUserProfileEntry().getUserId();
        
        return ( licenseCode.equals("doctor") || userId.equals("lasmanager") ) ? false : true;
    }
    
    public static String getAuthenticationDN() {
        return stub.getAuthenticationDN();
    }
    
    public static String getUserId() {
        return stub.getUserId();
    }
    
    public static String getPasswd() {
        return stub.getPasswd();
    }
    
    public static String getHostAddress() {
        return stub.getHostAddress();
    }
    
    public static int getHostPort() {
        return stub.getHostPort();
    }
    
    // HOT
    public static boolean getSendClaim() {
        return stub.getSendClaim();
    }
    
    public static boolean getSendDiagnosis() {
        return stub.getSendDiagnosis();
    }
    
    public static String getClaimHostName() {
        return stub.getClaimHostName();
    }
    // HOT
    
    public static String getClaimAddress() {
        return stub.getClaimAddress();
    }
    
    public static int getClaimPort() {
        return stub.getClaimPort();
    }
    
    public static String getClaimEncoding() {
        return stub.getClaimEncoding();
    }

    public static String getProxyHost() {
        return stub.getProxyHost();
    }
    
    public static int getProxyPort() {
        return stub.getProxyPort();
    }
    
    public static long getLastModify() {
        return stub.getLastModify();
    }
    
    public static void setLastModify(long val) {
        stub.setLastModify(val);
    }
    
    public static AbstractProjectFactory getProjectFactory() {
        return AbstractProjectFactory.getProjectFactory(stub.getName());
    }
    
    public static String createUUID() {
        return getProjectFactory().createUUID();
    }    
    
    public static ID getMasterId(String pid) {
        String fid = stub.getUserProfileEntry().getFacilityId();
        return getProjectFactory().createMasterId(pid, fid);
    }
    
    public static ID getClaimMasterId(String pid) {
        return new ID(pid, "facility", "MML0024");
    }
    
    public static DocInfo getDocInfo(SaveParams params, String gId) {
        return getProjectFactory().createDocInfo(params, gId);
    }
    
    public static Object createSaveDialog(Frame parent, SaveParams params) {
        return getProjectFactory().createSaveDialog(parent, params);
    }
    
    public static String getCSGWPath() {
        //String host = getHostAddress();
        // 2003-08-18
        String uploader = getUploaderIPAddress();
        String share = getUploadShareDirectory();
        String id = stub.getUserProfileEntry().getFacilityId();
        return getProjectFactory().createCSGWPath(uploader, share, id);
    }
    
    // HOT
    /**
     * センター送信をする場合は true
     */
    public static boolean getSendMML() {
        return stub.getSendMML();
    }
    
    public static String getMMLVersion() {
        return stub.getMMLVersion();
    }
    
    public static String getMMLEncoding() {
        return stub.getMMLEncoding();
    }
    
    /*public static boolean getUseLocalPatientId() {
        return stub.getUseLocalPatientId();
    }*/
    
    public static String getUploaderIPAddress() {
        return stub.getUploaderIPAddress();
    }
    
    public static String getUploadShareDirectory() {
        return stub.getUploadShareDirectory();
    }
    
        
    // MMLV3 OID
    public static String getFacilityOID() {
        String facilityId = getUserProfileEntry().getFacilityId();
        return getProjectFactory().getFacilityOID(facilityId);
    }
    
    public static DocInfo getClaimDocInfo(SaveParams params, String gId) {
        return getProjectFactory().createClaimDocInfo(params, gId);
    }
    
    // HOT
}