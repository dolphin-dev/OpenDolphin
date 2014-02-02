/*
 * PatientDao.java
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
import java.io.*;
import java.beans.*;

import open.dolphin.infomodel.Patient;
import open.dolphin.util.*;
import mirrorI.dolphin.server.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlPatientDao extends SqlDaoBean {

    /** Creates new PatientDao */
    public SqlPatientDao() {
    }
    
    public Patient getById(String pid) {
        
        Connection conn = null;
        Patient entry = null;
        java.sql.Statement st= null;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("select * from tbl_patient where pid = ");
            buf.append(addSingleQuote(pid));
            String sql = buf.toString();
            printTrace(sql);
            
            st= conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                entry = new Patient();
                entry.setId(rs.getString(1));
                entry.setName(rs.getString(2));
                entry.setKanaName(rs.getString(3));
                entry.setRomanName(rs.getString(4));
                entry.setGender(rs.getString(5));
                entry.setBirthday(rs.getString(6));
                entry.setNationality(rs.getString(7));
                entry.setMaritalStatus(rs.getString(8));
                entry.setHomePostalCode(rs.getString(9));
                entry.setHomeAddress(rs.getString(10));
                entry.setHomePhone(rs.getString(11));
                entry.addEmailAddress(rs.getString(12));
                entry.setLocalId(rs.getString(14));
            }
            rs.close();
                
        } catch (SQLException e) {
            processError(conn, entry, "SQLException while getting patient-entry: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return entry;
    }
    
    
    public boolean modifyLocalId(String pid, String localId) {
        
        Connection conn = null;
        Patient entry = null;
        java.sql.Statement st= null;
        int n = 0;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("update tbl_patient set localId = ");
            buf.append(addSingleQuote(localId));
            buf.append(" where pid = ");
            buf.append(addSingleQuote(pid));            
            String sql = buf.toString();
            printTrace(sql);
            
            st= conn.createStatement();
            n = st.executeUpdate(sql);
                
        } catch (SQLException e) {
            processError(conn, "dummy", "SQLException while getting patient-entry: " + e.toString());
            n = -1;
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return n == 1 ? true : false;
    }    
    
    public ArrayList getHealthInsurance(String pid) {
        
        Connection conn = null;
        ArrayList results = null;
        PVTHealthInsurance entry = null;
        java.sql.Statement st= null;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("select insuranceBytes from tbl_healthinsurance where pid = ");
            buf.append(addSingleQuote(pid));
            String sql = buf.toString();
            printTrace(sql);
            
            st= conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                byte[] bytes = rs.getBytes(1);
                
                // XMLDecode
                XMLDecoder d = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(bytes)));
                entry = (PVTHealthInsurance)d.readObject();
                //System.out.println(entry.toString());
                d.close();
                
                if (results == null) {
                    results = new ArrayList();
                }
                
                results.add(entry);
            }
            rs.close();
                
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting patient-entry: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;        
        
    }
    
    public ArrayList searchById(String pid) {
        
        Connection conn = null;
        Patient entry = null;
        java.sql.Statement st= null;
        ArrayList results = null;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            if (pid.equals("*")) {
                buf.append("select * from tbl_patient order by pid");
                
            } else {
                buf.append("select * from tbl_patient where pid like ");
                buf.append(addSingleQuote(pid + "%"));
            }
            String sql = buf.toString();
            this.setTrace(true);
            printTrace(sql);
            
            st= conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                entry = new Patient();
                entry.setId(rs.getString(1));
                entry.setName(rs.getString(2));
                entry.setKanaName(rs.getString(3));
                entry.setRomanName(rs.getString(4));
                entry.setGender(rs.getString(5));
                entry.setBirthday(rs.getString(6));
                entry.setNationality(rs.getString(7));
                entry.setMaritalStatus(rs.getString(8));
                entry.setHomePostalCode(rs.getString(9));
                entry.setHomeAddress(rs.getString(10));
                entry.setHomePhone(rs.getString(11));
                entry.addEmailAddress(rs.getString(12));
                entry.setLocalId(rs.getString(14));
                
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(entry);
            }
            rs.close();
                
        } catch (SQLException e) {
            processError(conn, entry, "SQLException while getting patient-entry: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    } 
    
    public ArrayList searchByName(String name) {
        
        Connection conn = null;
        Patient entry = null;
        java.sql.Statement st= null;
        ArrayList results = null;
        
        try {
            conn = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("select * from tbl_patient where ");
            if (StringTool.startsWithHiragana(name)) {
                String val = StringTool.hiraganaToKatakana(name);
                buf.append(" kana like ");
                buf.append(addSingleQuote("%" + val + "%"));
            
            } else if (StringTool.startsWithKatakana(name)) {
                buf.append(" kana like ");
                buf.append(addSingleQuote("%" + name + "%"));
				//buf.append(addSingleQuote(name + "%"));
                
            } else {
                buf.append(" name like ");
                buf.append(addSingleQuote("%" + name + "%"));
            }
            String sql = buf.toString();
            this.setTrace(true);
            printTrace(sql);
            
            st= conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                entry = new Patient();
                entry.setId(rs.getString(1));
                entry.setName(rs.getString(2));
                entry.setKanaName(rs.getString(3));
                entry.setRomanName(rs.getString(4));
                entry.setGender(rs.getString(5));
                entry.setBirthday(rs.getString(6));
                entry.setNationality(rs.getString(7));
                entry.setMaritalStatus(rs.getString(8));
                entry.setHomePostalCode(rs.getString(9));
                entry.setHomeAddress(rs.getString(10));
                entry.setHomePhone(rs.getString(11));
                entry.addEmailAddress(rs.getString(12));
                entry.setLocalId(rs.getString(14));
                
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(entry);
            }
            
            rs.close();
                
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting patient-entry: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    }        
}