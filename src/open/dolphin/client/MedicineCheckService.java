/*
 * MedicineCheckService.java
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

import java.sql.*;
import java.util.*;

import open.dolphin.dao.*;
import open.dolphin.plugin.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicineCheckService extends DefaultPlugin {
    
    public static final int TT_NO_INTERRACTION  = 0;
    public static final int TT_INTERRACTION     = -1;
    public static final int TT_ERROR            = -2;
    
    private Object[] input;
    private java.sql.Connection conn;
    private PreparedStatement ps;
    private ArrayList errorList;
    private int resultCode;
    
    /** Creates a new instance of MedicineCheckService */
    public MedicineCheckService() {
    }
    
    public ArrayList getErrorList() {
        return errorList;
    }
    
    public int medicine(final Object[] input) {
        
        this.input = input;
        
        try {
            if (conn == null) {
                SqlDaoBean dao = (SqlDaoBean)SqlDaoFactory.create(this, "dao.master");
                conn = dao.getConnection();
            }
            
            check();
            
            conn.close();
        
        } catch (Exception e) {
            System.out.println(e);
            resultCode = TT_ERROR;
            
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e2) {
                }
            }
            
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                }
            }
        }
                        
        return resultCode;
    }
    
    private void check() throws SQLException {
        
        if (ps == null) {
            String sql = "select drugcd2,syojyoucd from tbl_interact where drugcd = ?";
            ps = conn.prepareStatement(sql);
        }
        
        /**
         * 相互作用リストを生成する
         */
        InteractionEntry entry = null;
        String code1 = null;
        String code2 = null;
        //Logger logger = ClientContext.getLogger();
        
        for (int i = 0; i < input.length; i++) {
        
            code1 = (String)input[i];
            debug("checking " + code1);
            ps.setString(1,code1);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                
                code2 = rs.getString(1);
				debug("found " + code2);

                if (hasCode2(code2,i)) {

					debug("input contains " + code2);
                    entry = new InteractionEntry();
                    entry.setCode1(code1);
                    entry.setCode2(code2);
                    entry.setInteractionCode(rs.getString(2));

                    if (errorList == null) {
                        errorList = new ArrayList();
                    }
                    errorList.add(entry);
                
                } else {
					debug("input dose not contain " + code2);
                }   
            }
        }
        
        ps.close();
        
        if (errorList == null) {
            resultCode = TT_NO_INTERRACTION;
            return;
        }

        /**
         * 相互作用がある場合
         */
        // 1. 薬品名称を設定する
        
        resultCode = TT_INTERRACTION;
        
        String sql = "select name from tbl_tensu where srycd = ?";
        ps = conn.prepareStatement(sql);

        int size = errorList.size();

        for (int i = 0; i < size; i++) {

            entry = (InteractionEntry)errorList.get(i);

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

            entry = (InteractionEntry)errorList.get(i);
           
            String code = entry.getInteractionCode();
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                entry.setInteraction(rs.getString(1));
            }
        }
        
        ps.close();
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
    
	private void debug(String msg) {
		if (ClientContext.isDebug()) {
			System.out.println(msg);
		}
	}     
}
