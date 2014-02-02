/*
 * DebugFactory.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
import java.rmi.server.*;

import open.dolphin.client.*;
import open.dolphin.dao.*;
import open.dolphin.infomodel.ID;

/**
 * 2001-2002 経済産業省・MEDIS　補正予算プロジェクト
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public class DebugFactory extends DolphinFactory {
    
    /** Creates new Project */
    public DebugFactory() {
    }
    
    public AuthenticationDao createAuthentication(String host, int port, String userId, String passwd) {
        return new AuthenticationDao(host, port, userId, "uid=" + userId + ",ou=DolphinUsers,o=Dolphin", passwd);
    }
    
    public String createUUID() {
        UID uuid = new UID();
        String uid = uuid.toString();          
        //System.out.println(uid);
        return uid.replace(':','_');
        //System.out.println(uid);
    }
    
    public String createCSGWPath(String uploaderAddress, String share, String facilityId) {
        if (csgwPath == null) {
            StringBuffer buf = new StringBuffer();
            buf.append(ClientContext.getUserDirectory());
            buf.append("/");
            buf.append("MML-Instance");
            buf.append(File.separator);
            buf.append(facilityId);
            csgwPath = buf.toString();
        }
        return csgwPath;
    }
    
    public ID createMasterId(String pid, String facilityId) {
        return new ID(pid, "facility", facilityId);
    }
    
    public Object createSaveDialog(Frame parent, SaveParams params) {
        SaveDialog sd = new SaveDialog(parent);
        //params.setPrintCount(1);          // 印刷部数
        params.setAllowPatientRef(true);    // 患者の参照
        params.setAllowClinicRef(true);     // 診療履歴のある医療機関
        sd.setValue(params);
        return sd;
    }
    
    public Object createAboutDialog() {
        return new AboutDialog(null, "アバウト-Dolphin", "splash.jpg");
    }
}