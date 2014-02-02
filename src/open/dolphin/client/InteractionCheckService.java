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
import open.dolphin.plugin.*;

import swingworker.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class InteractionCheckService extends DefaultPlugin {
    
    public static final int TT_NONE             = 0;
    public static final int TT_NO_INTERACTION   = 1;
    public static final int TT_INTERACTION      = -1;
    public static final int TT_DATABASE_ERROR   = -2;
    public static final int TT_NO_CONNECTION    = -3;
    public static final int TT_ERROR            = -4;
    
    private int resultCode;
    private Object[] input;
    private Connection conn;
    private PreparedStatement ps;
    private boolean checkOver;
    private ArrayList result;
    private String statMessage;

    private String underCheckMessage = ClientContext.getString("interactionCheck.message.underCheck");
    private String endCheckMessage = ClientContext.getString("interactionCheck.message.endCheck");
    private String noCodeMessage = ClientContext.getString("interactionCheck.message.noCode");
    private String noConnectionMessage = ClientContext.getString("interactionCheck.message.noConnection");
    private String dbErrorMessage = ClientContext.getString("interactionCheck.message.dbError");
    private String interactionMessage = ClientContext.getString("interactionCheck.message.interaction");
    
    /** Creates a new instance of MemberSearchService */
    public InteractionCheckService() {
    }
    
    public void setCode(Object[] input) {
        this.input = input;
    }
    
    public void go() {
        
        statMessage = underCheckMessage;
        
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
            checkInteraction();
        }
        
        private void checkInteraction() {
            
            if (input == null || input.length == 0) {
                statMessage = noCodeMessage;
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
                
                /**
                 * 相互作用リストを生成する
                 */
                String sql = "select drugcd2,syojyoucd from tbl_interact where drugcd = ?";
                ps = conn.prepareStatement(sql);
                
                InteractionEntry entry = null;
                String code1 = null;
                String code2 = null;
                //Logger logger = ClientContext.getLogger();

                for (int i = 0; i < input.length; i++) {

                    code1 = (String)input[i];
                    debug("checking: " + code1);
                    ps.setString(1, code1);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {

                        code2 = rs.getString(1);
						debug("found interaction: " + code2);

                        if (hasCode2(code2,i)) {

							debug("input contains: " + code2);
                            entry = new InteractionEntry();
                            entry.setCode1(code1);
                            entry.setCode2(code2);
                            entry.setInteractionCode(rs.getString(2));

                            if (result == null) {
                                result = new ArrayList();
                            }
                            result.add(entry);

                        } else {
							debug("input dose not contain " + code2);
                        }   
                    }
                }

                ps.close();

                if (result != null) {
                    /**
                     * 相互作用がある場合
                     */
                    
                    // 1. 薬品名称を設定する
                    sql = "select name from tbl_tensu where srycd = ?";
                    ps = conn.prepareStatement(sql);

                    int size = result.size();

                    for (int i = 0; i < size; i++) {

                        entry = (InteractionEntry)result.get(i);

                        for (int k = 0; k < 2; k++) {
                            String code = k== 0 ? entry.getCode1() : entry.getCode2();
                            ps.setString(1, code);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                String name = rs.getString(1);
                                if (k == 0) {
                                    entry.setName1(name);
                                } else {
                                    entry.setName2(name);
                                }
                            }
                        }
                    }

                    ps.close();

                    // 2. 相互作用情報を設定する
                    sql = "select syojyou from tbl_sskijyo where syojyoucd = ?";
                    ps = conn.prepareStatement(sql);

                    for (int i = 0; i < size; i++) {

                        entry = (InteractionEntry)result.get(i);

                        String code = entry.getInteractionCode();
                        ps.setString(1, code);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            entry.setInteraction(rs.getString(1));
                        }
                    }
                }

                closePs(ps);
                closeConn(conn);
                
                boolean error = (result != null && result.size() > 0) ? true : false;
                
                statMessage = error ? interactionMessage : endCheckMessage;
                resultCode = error? TT_INTERACTION : TT_NO_INTERACTION;
                
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
        
        private boolean hasCode2(String code2, int index) {
        
            boolean has = false;

            for (int i = 0; i < input.length; i++) {

                if (i != index) {

                    String code1 = (String)input[i];
                    if (code1.equals(code2)) {
                        has = true;
                        break;
                    }
                }
            }

            return has;
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
    
    private void debug(String msg) {
    	if (ClientContext.isDebug()) {
    		System.out.println(msg);
    	}
    }
}
