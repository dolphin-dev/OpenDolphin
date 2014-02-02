/*
 * Miyazaki.java
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


/**
 * はにわネットに固有のオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public final class MiyazakiFactory extends DolphinFactory {

    /** Creates new Project */
    public MiyazakiFactory() {
        super();
    }
        
    /*public ID createMasterId(String pid, String facilityId) {
        return new ID(pid, "facility", facilityId);
    }*/
    
    public Object createSaveDialog(Frame parent,SaveParams params) {
        SaveDialog sd = new SaveDialog(parent);
        //params.setPrintCount(1);            // 印刷部数
        params.setAllowPatientRef(false);    // 患者の参照
        params.setAllowClinicRef(false);     // 診療履歴のある医療機関
        sd.setValue(params);
        return sd;
    }
    
	public Object createAboutDialog() {
		String title = "アバウト-" + ClientContext.getString("application.title");
		return new AboutDialog(null, title, "splash.jpg");
	}        
}