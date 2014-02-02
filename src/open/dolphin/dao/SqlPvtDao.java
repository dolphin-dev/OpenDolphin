/*
 * PVTDao.java
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

import java.util.*;
import java.sql.*;
import java.beans.*;
import java.io.*;

import open.dolphin.infomodel.DInsuranceInfo;
import open.dolphin.infomodel.Patient;
import open.dolphin.infomodel.PatientVisit;
import open.dolphin.util.*;

import mirrorI.dolphin.server.*;


/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlPvtDao extends SqlDaoBean {
    
    /** Creates new PVTDao */
    public SqlPvtDao() {
    }
    
    public ArrayList getPatientVisit(String date, int skipCount) {
            
        Connection conn = null;
        ArrayList results = null;

        try {
            conn = getConnection();
            results = getVisit(conn, date, skipCount);

            if (results != null) {
                getInsurance(conn, results);
                getAppointment(conn, results);
            }
        
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting PVT: " + e.toString());
        }
        
        closeConnection(conn);
        
        return results;
    }
    
    public ArrayList getHealthInsurance(String pid) {
        
        Connection conn = null;      
        ArrayList results = null;
        
        try {
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("select insuranceBytes from tbl_healthinsurance where pid = ?");
            XMLDecoder d = null;
            PVTHealthInsurance ins = null;
            byte[] bytes = null;
            
            ps.setString(1, pid);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                
                bytes = rs.getBytes(1);
                d = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(bytes)));
                ins = (PVTHealthInsurance)d.readObject();
                d.close();

                DInsuranceInfo di = new DInsuranceInfo();
                di.setPVTHealthInsurance(ins);
              
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(di); 
            }

            ps.close();
            rs.close();
        
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting Health Insurance: " + e.toString());
        }
        
        closeConnection(conn);
        return results;    
    }
    
    private ArrayList getVisit(Connection conn, String date, int skipCount) throws SQLException {
    
        ArrayList results = null;
        java.sql.Statement st= null;
        
        // patient_visit
        StringBuffer buf = new StringBuffer();
        buf.append("select oid, * from tbl_patientVisit where registTime like ");
        buf.append(addSingleQuote(date + "%"));
        buf.append(" order by registTime");
        String sql = buf.toString();
        printTrace(sql);
                        
        st= conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        for (int i = 0; i < skipCount; i++) {
            rs.next();
        }

        PatientVisit pvt = null;
        Patient patient = null;
        int index;
        String val;

        while (rs.next()) {

            pvt = new PatientVisit();
            pvt.setNumber(rs.getInt(1));
            patient = new Patient();
			pvt.setPatient(patient);
			patient.setId(rs.getString(2));
			patient.setName(rs.getString(3));
			patient.setGender(rs.getString(4));
			patient.setBirthday(rs.getString(5));
            val = rs.getString(6);
            index = val.indexOf("T");
            val = index > -1 ? val.substring(index + 1) : val;
            pvt.setTime(val);
            pvt.setDepartment(rs.getString(7));
            pvt.setState(Integer.parseInt(rs.getString(8)));

            // Calculate age
			patient.setAge(AgeCalculator.getAge(patient.getBirthday()));

            if (results == null) {
                results = new ArrayList();
            }
            results.add(pvt);
        }

        rs.close();
            
        closeStatement(st);

        return results;
    }
    
    
    private void getInsurance(Connection conn, ArrayList results) throws SQLException {
            
        PreparedStatement ps = conn.prepareStatement("select insuranceBytes from tbl_healthinsurance where pid = ?");
        PatientVisit pvt = null;
        XMLDecoder d = null;
        PVTHealthInsurance ins = null;
        byte[] bytes = null;

        int size = results.size();
        for (int i = 0; i < size; i++) {

            pvt = (PatientVisit)results.get(i);
            ps.setString(1, pvt.getPatient().getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                bytes = rs.getBytes(1);
                if (bytes == null) {
                    continue;
                }

                d = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(bytes)));
                ins = (PVTHealthInsurance)d.readObject();
                d.close();

                DInsuranceInfo di = new DInsuranceInfo();
                di.setPVTHealthInsurance(ins);

                pvt.addInsuranceInfo(di); 
            }
        }
        ps.close();
    }
    
    
    private void getAppointment(Connection conn, ArrayList results) throws SQLException {
        
        // appointment
        PreparedStatement ps = conn.prepareStatement("select appointDate from tbl_appointment where pid = ? and appointDate >= ? and appointDate <= ? ");
        String from = MMLDate.getDayFromToday(-3);
        String until = MMLDate.getDayFromToday(3);

        PatientVisit pvt = null;
        int size = results.size();

        for (int i = 0; i < size; i++) {
            pvt = (PatientVisit)results.get(i);
            ps.setString(1, pvt.getPatient().getId());
            ps.setString(2, from);
            ps.setString(3, until);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pvt.setAppointment(true);
            }
            rs.close();
        }
        ps.close();
    }
    
    public String getStatus(int oid) {
        
        String ret = null;
        Connection conn = null;
        
        java.sql.Statement st= null;
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            StringBuffer buf = new StringBuffer();
            buf.append("select status from tbl_patientVisit where oid = ");
            buf.append(addSingleQuote(String.valueOf(oid)));
            ResultSet rs = st.executeQuery(buf.toString());
            if (rs.next()) {
                ret = rs.getString(1);
            }
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, ret, "SQLException while checking PvtState: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return ret;
    }
}