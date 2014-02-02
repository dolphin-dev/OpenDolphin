/*
 * ProjectFactory.java
 *
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
import open.dolphin.dao.*;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ID;

/**
 * プロジェクトに依存するオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractProjectFactory {

    private static KumamotoFactory kumamoto;
    private static MiyazakiFactory miyazaki;
    
    private static DebugFactory debug;
    private static DemoFactory demo;
    
    /** Creates new ProjectFactory */
    public AbstractProjectFactory() {
    }
    
    public static AbstractProjectFactory getProjectFactory(String proj) {
        
        if (proj.equals("kumamoto")) {
            if (kumamoto == null) {
                kumamoto = new KumamotoFactory();
            }
            return kumamoto;
            
        } else if (proj.equals("miyazaki")) {
            if (miyazaki == null) {
                miyazaki = new MiyazakiFactory();
            }
            return miyazaki;
            
        } else if (proj.equals("debug")) {
            if (debug == null) {
                debug = new DebugFactory();
            }
            return debug;
            
        } else if (proj.equals("demo")) {
            if (demo == null) {
                demo = new DemoFactory();
            }
            return demo;
            
        } else {
            //assert false : proj;
        }
        
        return null;
    }
    
    public abstract AuthenticationDao createAuthentication(String host, int port, String userId, String passwd);
    
    public abstract String createUUID();
    
    public abstract String createCSGWPath(String uploaderAddress, String share, String facilityId);
    
    public abstract Object createAboutDialog();
    
    public abstract ID createMasterId(String pid, String facilityId);
    
    public abstract Object createSaveDialog(Frame parent, SaveParams params);
    
    public abstract DocInfo createDocInfo(SaveParams params, String gId);
    
    // MML V3    
    public abstract DocInfo createClaimDocInfo(SaveParams params, String gId);
    
    public abstract String getFacilityOID(String facilityId);
}