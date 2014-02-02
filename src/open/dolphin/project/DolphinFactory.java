/*
 * DolphinFactory.java
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
import java.io.*;
import java.net.*;

import open.dolphin.client.*;
import open.dolphin.dao.*;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ID;

/**
 * プロジェクトに依存するオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public abstract class DolphinFactory extends AbstractProjectFactory {

    protected String csgwPath;
    
    /** Creates new Project */
    public DolphinFactory() {
    }
    
    public AuthenticationDao createAuthentication(String host, int port, String userId, String passwd) {
        StringBuffer buf = new StringBuffer();
        buf.append("uid=");
        buf.append(userId);
        buf.append(",");
        buf.append("ou=DolphinUsers,o=Dolphin");
        String bindDN = buf.toString();
        return new AuthenticationDao(host, port, userId, bindDN, passwd);
    }
    
    public ID createMasterId(String pid, String facilityId) {
        return new ID(pid, "facility", facilityId);
    }
    
    public String createUUID() {
                
        String uid = null;
        
        try {
            String host = Project.getHostAddress();
            Socket s = new Socket(host, 5105);
            
            BufferedInputStream reader = new BufferedInputStream(s.getInputStream());
            StringBuffer buf = new StringBuffer();
            int c;
            while((c = reader.read()) != -1) {
                buf.append((char)c);
            }
            uid = buf.toString();
            s.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }        
        return uid;
    }
    
    public String createCSGWPath(String uploaderAddress, String share, String facilityId) {
        if (csgwPath == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("\\\\");
            buf.append(uploaderAddress);
            //buf.append("\\public");
            buf.append("\\");
            buf.append(share);
            buf.append("\\");
            buf.append(facilityId);
            csgwPath = buf.toString();
        }
        return csgwPath;
    }
    
    public DocInfo createDocInfo(SaveParams params, String gId) {
        
        //String mmlVersion = Project.getMMLVersion();
        
        DocInfo docInfo = new DocInfo();
        /*docInfo.setVersion(mmlVersion);
        
        // Set group-id
        if (gId != null) {
            GroupId gid = new GroupId(gId, "record");
            gid.setVersion(mmlVersion);
            docInfo.addGroupId(gid);
        }
        
        // ToDo 文書タイトル設定
        docInfo.setTitle(params.getTitle());
        docInfo.setPurpose("record");    // デフォルト
        
        // 診療科に応じて動的に変更
        String dept = params.getDepartment();
        Department department = new Department();
        department.addDepartmentName(new DepartmentName(dept));
        ID did = new ID(MMLTable.getDepartmentCode(dept),"medical","MML0028");
        department.setId(did);
        CreatorInfo creator = Project.getCreatorInfo();
        creator.getPersonalizedInfo().setDepartment(department);
        docInfo.setCreatorInfo(creator);
                
        // Creates SecurityLevel
        SecurityLevel sc = new SecurityLevel();
        sc.setVersion(mmlVersion);
        
        // Default access rigth
        AccessRight ar = new AccessRight();
        ar.setVersion(mmlVersion);
        ar.setPermit("all");
        ar.addScFacilityName(new ScFacilityName("記載者施設", "creator", null, null, null));
        sc.addAccessRight(ar);
        
        // For 診療歴のある施設
        if (params.isAllowClinicRef()) {
            AccessRight ar2 = new AccessRight();
            ar2.setVersion(mmlVersion);
            ar2.setPermit("read");
            ar2.addScFacilityName(new ScFacilityName("診療歴のある施設", "experience", null, null, null)); 
            sc.addAccessRight(ar2);
        }
        
        // For 患者
        if (params.isAllowPatientRef()) {
            AccessRight ar3 = new AccessRight();
            ar3.setVersion(mmlVersion);
            ar3.setPermit("read");                   
            ar3.addScPersonName(new ScPersonName("被記載者(患者)", "patient", null, null, null));
            sc.addAccessRight(ar3); 
        }
        
        // For facilities
        ArrayList list = params.getFacilityAccessList();
        if (list != null && list.size() >0) {
            
            for (int i = 0; i < list.size(); i++) {
                
                AccessRight ar4 = new AccessRight();
                ar4.setVersion(mmlVersion);
                ar4.setPermit("read");
                ar4.addScFacilityName((ScFacilityName)list.get(i));
                sc.addAccessRight(ar4); 
            }
        }
        
        docInfo.setSecurityLevel(sc);*/
        
        return docInfo;    
    }
    
    public DocInfo createClaimDocInfo(SaveParams params, String gId) {
        
        DocInfo docInfo = new DocInfo();
        /*docInfo.setVersion("230");
        
        // Set group-id
        if (gId != null) {
            GroupId gid = new GroupId(gId, "record");
            docInfo.addGroupId(gid);
        }
        
        // ToDo 文書タイトル設定
        docInfo.setTitle(params.getTitle());
        docInfo.setGenerationPurpose("record");    // デフォルト
        
        // 診療科に応じて動的に変更
        String dept = params.getDepartment();
        Department department = new Department();
        department.addDepartmentName(new DepartmentName(dept));
        ID did = new ID(MMLTable.getDepartmentCode(dept),"medical","MML0028");
        department.setId(did);
        CreatorInfo creator = Project.getCreatorInfo();
        creator.getPersonalizedInfo().setDepartment(department);
        docInfo.setCreatorInfo(creator);
                
        // Creates SecurityLevel
        SecurityLevel sc = new SecurityLevel();
        
        // Default access rigth
        AccessRight ar = new AccessRight();
        ar.setPermit("all");
        ar.addScFacilityName(new ScFacilityName("記載者施設", "creator", null, null, null));
        sc.addAccessRight(ar);
        
        if (params.isAllowClinicRef()) {
            AccessRight ar2 = new AccessRight();            
            ar2.setPermit("read");
            ar2.addScFacilityName(new ScFacilityName("診療歴のある施設", "experience", null, null, null)); 
            sc.addAccessRight(ar2);
        }
        
        if (params.isAllowPatientRef()) {
            AccessRight ar3 = new AccessRight();
            ar3.setPermit("read");                   
            ar3.addScPersonName(new ScPersonName("被記載者(患者)", "patient", null, null, null));
            sc.addAccessRight(ar3); 
        }
        docInfo.setSecurityLevel(sc);*/
        
        return docInfo;    
    }    
    
    public String getFacilityOID(String facilityId) {
        
        //String mmlOID = "1.2.392.114319.1.1";  //1.2.392.114319.5.1
        String mmlOID = "1.2.392.114319.5.1";  
        //String dolphin = "10";
        
        if (facilityId.startsWith("JPN")) {
            facilityId = facilityId.substring(3);
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append(mmlOID);
        buf.append(".");
        //buf.append(dolphin);
        //buf.append(".");
        
        try {
            Long.parseLong(facilityId);
            buf.append(facilityId);
        
        } catch (NumberFormatException ne) {
            buf.append("999999999999");
        }
        
        return buf.toString();
    }
    
	public Object createAboutDialog() {
		String title = "アバウト-" + ClientContext.getString("application.title");
		return new AboutDialog(null, title, "splash.jpg");
	}
    
    public abstract Object createSaveDialog(Frame parent, SaveParams params);
}