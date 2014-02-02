/*
 * StampCheckService.java
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
package open.dolphin.client;

import java.sql.*;
import java.util.*;

import open.dolphin.dao.*;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.plugin.*;
import swingworker.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampCheckService extends DefaultPlugin {
    
    public static final int TT_NONE             = 0;
    public static final int TT_NO_INVALID_STAMP = 1;
    public static final int TT_INVALID_STAMP    = -1;
    public static final int TT_DATABASE_ERROR   = -2;
    public static final int TT_NO_CONNECTION    = -3;
    public static final int TT_ERROR            = -4;
    
    private int resultCode;
    private Module[] orderStamps;
    private Connection conn;
    private PreparedStatement ps;
    private boolean checkOver;
    private ArrayList result;
    private String statMessage;
    private String disUseMessage = ClientContext.getString("stampCheck.message.disUse");
    private String deletedMessage = ClientContext.getString("stampCheck.message.deleted");
    private String underCheckMessage = ClientContext.getString("stampCheck.message.underCheck");
    private String endCheckMessage = ClientContext.getString("stampCheck.message.endCheck");
    private String noStampMessage = ClientContext.getString("stampCheck.message.noStamp");
    private String noConnectionMessage = ClientContext.getString("stampCheck.message.noConnection");
    private String dbErrorMessage = ClientContext.getString("stampCheck.message.dbError");
    private String invalidStampMessage = ClientContext.getString("stampCheck.message.invalidStamp");
    
    private String refDate;
    
    /** Creates a new instance of MemberSearchService */
    public StampCheckService() {
    }
    
    public void setRefDate(String date) {
        refDate = date;
    }
    
    public void setStamp(Module[] orderStamps) {
        this.orderStamps = orderStamps;
    }
    
    public void go() {
        
        statMessage = underCheckMessage; //"スタンプをチェックしています...";
        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    public boolean done() {
        return checkOver;
    }

    public String getMessage() {
        return statMessage;
    }

    public  ArrayList getErrorList() {
        return result;
    }
    
    public  int getResultCode() {
        return resultCode;
    }
    
    private class ActualTask {

         /** Creates new ActualTask */
        ActualTask() {
            //send request to execute
            checkStamp();
        }
        
        private void checkStamp() {
            
            if (orderStamps == null || orderStamps.length == 0) {
                statMessage = noStampMessage;   //"チェックするスタンプがありません";
                resultCode = TT_NONE;
                checkOver = true;
                return;
            }
            
            SqlDaoBean dao = (SqlDaoBean)SqlDaoFactory.create(this, "dao.master");
            if (dao == null) {
                statMessage = "Application error";
                resultCode = TT_ERROR;
                //assert false : statMessage;
                System.out.println(statMessage);
                checkOver = true;
                return;
            }
            
            try {
                conn = dao.getConnection();
                
                int stampCount = orderStamps.length;
                String reason = null;
            
                for (int i = 0; i < stampCount; i++) {

                    ClaimBundle b = (ClaimBundle)orderStamps[i].getModel();

                    // Defualt CODE SYSTEM に移行
                    b.setAdminCodeSystem(null);

                    ModuleInfo info = orderStamps[i].getModuleInfo();
                    ClaimItem[] items = b.getClaimItem();

                    if (items == null) {
                        continue;
                    }

                    for (int k = 0; k < items.length; k++) {

                        reason = isValid(items[k]);

                        if (reason != null ) {

                            StringBuffer buf = new StringBuffer();
                            buf.append(info.getName());
                            buf.append(" : ");
                            buf.append(items[k].getName());
                            buf.append(" (");
                            buf.append(reason);
                            buf.append(")");

                            if (result == null) {
                                result = new ArrayList();
                            }

                            result.add(buf.toString());
                        }
                    }
                }
                
                closePs(ps);
                closeConn(conn);
                
                boolean error = (result != null && result.size() > 0) ? true : false;
                
                statMessage =  error  ?
                              invalidStampMessage:  //"無効なスタンプがあります";
                              endCheckMessage;          //"スタンプチェックが終了しました";
               
                resultCode = error  ? TT_INVALID_STAMP : TT_NO_INVALID_STAMP;
                
            } catch (SQLException e) {
                if (conn == null) {
                    statMessage = noConnectionMessage;  //"データベースに接続できません";
                    resultCode = TT_NO_CONNECTION;
                } else {
                    statMessage = dbErrorMessage;       //"データベースにエラー";
                    resultCode = TT_DATABASE_ERROR;
                }
                
                closePs(ps);
                closeConn(conn);
            }
            
            checkOver = true;
        }
        
        private String isValid(ClaimItem item) throws SQLException {
            
            String code = item.getCode();
            String subclass = item.getClassCode();
            
            // Default CODE SYSTEM に移行, 既存スタンプにセットされている
            // コード体系名をクリア
            item.setClassCodeSystem(null);
            
            String reason = null;
            
            if (ps == null) {
                ps = conn.prepareStatement("select srycd, yukoedymd from tbl_tensu where srycd = ?");
            }
            
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            String disUse = null;
            boolean valid = false;

            while (rs.next()) {
                disUse = rs.getString(2);
                if ( disUse != null ) {
                    
                    // 一つでも有効期限内であれば OK
                    if (refDate.compareTo(disUse) <= 0) {
                        valid = true;
                        break;   
                    }
                }
            }
            
            if (! valid) {
                reason = disUseMessage; //"有効期限以降";
            }
                            
            return reason;   
        }
        
        private void closePs(PreparedStatement o) {
            if (o != null) {
                try {
                    o.close();
                } catch (Exception e) {
                }
            }
        }
        
        private void closeConn(Connection o) {
            if (o != null) {
                try {
                    o.close();
                } catch (Exception e) {
                }
            }
        }        
    }
}