/*
 * Kumamoto.java
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
import open.dolphin.dao.*;
import open.dolphin.infomodel.ID;

/**
 * ひごメドに固有のオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public final class KumamotoFactory extends DolphinFactory {

    private KumamotoDao dao;
    
    /** Creates new Project */
    public KumamotoFactory() {
        super();
    }
  
    // Since 2003/08/01 HOT 02b
    public ID createMasterId(String pid, String facilityId) {
       
        ID id = null;
        
        if (dao == null) {
            dao = (KumamotoDao)SqlDaoFactory.create(this, "dao.kumamoto");
        }
        String localId = dao.fetchLocalId(pid);
        
        if (localId == null) {

            /*JOptionPane.showMessageDialog(null,
                             (String)"患者の地域IDが登録されていません。\n登録後にもう一度保存を実行してください。",
                             "Dolphin: 地域サーバ送信",
                             JOptionPane.INFORMATION_MESSAGE);*/
        }
        else {
            id = new ID(localId, "local", "MML0024");
        }
        return id;        
    }
    
    public Object createSaveDialog(Frame parent, SaveParams params) {
        
        SaveDialog sd = new SaveDialog(parent);
        //params.setPrintCount(1);              // 印刷部数
        params.setAllowPatientRef(true);        // 患者の参照を許可
        params.setAllowClinicRef(true);         // 診療履歴のある医療機関へ参照を許可
        sd.setValue(params);
        return sd;
    }
    
	public Object createAboutDialog() {
		String title = "アバウト-" + ClientContext.getString("application.title");
		return new AboutDialog(null, title, "splash.jpg");
	}    
}