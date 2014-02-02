/*
 * MemberSearchService.java
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
package open.dolphin.client;

import java.util.*;

import open.dolphin.dao.*;
import open.dolphin.plugin.*;
import swingworker.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MemberSearchService extends DefaultPlugin {
    
    private boolean searchOver;
    private ArrayList results;
    private String statMessage;
    
    /** Creates a new instance of MemberSearchService */
    public MemberSearchService() {
    }
    
    public void go() {
        statMessage = "センターへ接続しています...";
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    public boolean done() {
        return searchOver;
    }

    public String getMessage() {
        return statMessage;
    }

    public  ArrayList getMemberList() {
        return results;
    }
    
    private class ActualTask {

         /** Creates new ActualTask */
        ActualTask() {
            //send request to execute
            getMember();
        }
        
        private void getMember() {
            statMessage = "検索しています...";
            MemberDao dao = (MemberDao)DaoFactory.createMemberDao(this);
            results = dao.getFacilityList();
            
            if (results != null) {
                int count = results.size();
                statMessage = count + " 件のデータを取得しました";
            
            } else {
                statMessage = "検索を終了しました";
            }
            
            searchOver = true;
        }
    }
}