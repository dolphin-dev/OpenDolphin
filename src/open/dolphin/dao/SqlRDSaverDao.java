/*
 * SqlRDSaverDao.java
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
package open.dolphin.dao;

import java.sql.*;
import java.util.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.RegisteredDiagnosisModule;

/**
 * Class to add Registered Diagnosis Entry.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SqlRDSaverDao extends SqlDaoBean {
        
    //RegisteredDiagnosisModule[] rd;
    private Object[] modules;
    
    private DocInfo[] docInfo;
    
    private String pid;

    /** Creates new RDEntry */
    public SqlRDSaverDao() {
    }
    
    public void setPid(String val) {
        pid = val;
    }
        
    public void setDocInfo(DocInfo[] info) {
        docInfo = info;
    }
    
    public void setRegisteredDiagnosis(Object[] modules) {
        this.modules = modules;
    }    
    
    public boolean save() {
        
        boolean ret = false;
        
        Connection conn = null;
       
        try {
            conn = getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. モジュール数分のエントリを追加する
            int len = modules.length;
            
            PreparedStatement ps = conn.prepareStatement("insert into tbl_diagnosis values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for (int i = 0; i < len; i ++) {
                   
                RegisteredDiagnosisModule rd = (RegisteredDiagnosisModule)modules[i];
                DocInfo info = docInfo[i];
                
				ps.setString(1, pid);                               // pid
                ps.setString(2, info.getDocId());                   // docId
				ps.setString(3, info.getFirstConfirmDate());        // firstConfirmdate
                ps.setString(4, info.getConfirmDate());             // confirmdate
                
                ps.setString(5, rd.getDiagnosis());                 // diagnosis
                ps.setString(6, rd.getDiagnosisCode());             // diagnosisCode
                ps.setString(7, rd.getDiagnosisCodeSystem());       // codeSystem
                
                String category = rd.getCategory();
                if (category != null) {
                    ps.setString(8, category);        				// category
                    ps.setString(9, rd.getCategoryTable());         // categoryTable
                }
                else {
                    ps.setString(8, null);
                    ps.setString(9, null);
                }
                
                ps.setString(10, rd.getOutcome());                  // outcome
                ps.setString(11, rd.getFirstEncounterDate());       // firstEncounterDate
                ps.setString(12, rd.getStartDate());                // startDate
                ps.setString(13, rd.getEndDate());                  // endDate
                
                if (info.getParentId() != null) {
					ps.setString(14, info.getParentId().getId());   // parentId
                } else {
					ps.setString(14, null);
                }
                
                ps.setString(15, info.getCreator().getId());              // creator-id
                ps.setString(16, "0");                              // status
               
                debug(ps.toString());
                ps.executeUpdate();
                debug("executeUpdate diagnosis");
            }
            ps.close();
            
            conn.commit();
            ret = true;
            
        } catch (SQLException e) {
            rollback(conn);
            processError(conn, "dummy", "SQLException: " + e.toString());
        }
        
        closeConnection(conn);
        
        return ret;
    }
    
    public boolean update(ArrayList list, String newConfirmDate) {
    //public boolean update(Object[] o, String newConfirmDate) {
        
        boolean ret = false;
        
        Connection conn = null;
       
        try {
            conn = getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. モジュール数分のエントリを追加する
            int len = list.size();
            //int len = o.length;
            
            PreparedStatement ps = conn.prepareStatement("update tbl_diagnosis set outcome = ? ,endDate = ? ,confirmDate = ? where docId = ?");

            for (int i = 0; i < len; i ++) {
                      
                DiagnosisEntry entry = (DiagnosisEntry)list.get(i);
                ps.setString(1, entry.getOutcome());        // outcome
                ps.setString(2, entry.getEndDate());        // enddate
                ps.setString(3, newConfirmDate);            // new ConfirmDate
                ps.setString(4, entry.getUID());            // uid
                
                //Object[] data = (Object[])o[i];
                //ps.setString(1, (String)data[2]);       // outcome
                //ps.setString(2, (String)data[5]);       // enddate
                //ps.setString(3, newConfirmDate);        // new ConfirmDate
                //ps.setString(4, (String)data[7]);       // uid
               
                ps.executeUpdate();
            }
            ps.close();
            
            conn.commit();
            ret = true;
            
        } catch (SQLException e) {
            rollback(conn);
            processError(conn, "dummy","SQLException: " + e.toString());
        }
        
        closeConnection(conn);
        
        return ret;
    }
    
    public boolean delete(DiagnosisEntry entry) {
        
        boolean ret = false;
        
        Connection conn = null;
        
        try {
            conn = getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
                       
            PreparedStatement ps = conn.prepareStatement("delete from tbl_diagnosis where uid = ?");
            
            ps.setString(1, entry.getUID());            // uid
            ps.executeUpdate();

            ps.close();
            conn.commit();
            ret = true;
            
        } catch (SQLException e) {
            rollback(conn);
            processError(conn, "dummy", "SQLException: " + e.toString());
        }
        
        closeConnection(conn);
        
        return ret;
    }    
}