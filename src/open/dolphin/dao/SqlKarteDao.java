/*
 * SqlKarteDao.java
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
package open.dolphin.dao;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.Allergy;
import open.dolphin.infomodel.BaseClinicModule;
import open.dolphin.infomodel.BloodType;
import open.dolphin.infomodel.ClaimInfo;
import open.dolphin.infomodel.Creator;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ExtRef;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Infection;
import open.dolphin.infomodel.Karte;
import open.dolphin.infomodel.LifestyleModule;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.ParentId;
import open.dolphin.infomodel.Schema;
import open.dolphin.infomodel.Version;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlKarteDao extends SqlDaoBean {
    
    /** Creates a new instance of SqlKarteDao */
    public SqlKarteDao() {
    }
        
    /**
     * Returns Document History
     * @param pid 患者ID
     * @param docType ドキュメントタイプ
     * @param past 最初の確定日
     * @param includeModify 修正履歴を含むかどうか
     * @return ArrayList DocInfoを格納したリスト
     */
    public ArrayList getDocumentHistory(String pid, String docType, String past, boolean includeModify) {
        
        Connection conn = null;        
        java.sql.Statement st = null;
        ArrayList results = null;
       
        StringBuffer buf = new StringBuffer();
        buf.append("select * from tbl_docInfo where pid = ");
        buf.append(addSingleQuote(pid));
        if (!docType.equals("*")) {
            buf.append(" and docType = ");
            buf.append(addSingleQuote(docType));
        }
        buf.append(" and firstConfirmDate >= ");
        buf.append(addSingleQuote(past));
        
        // 修正履歴を含まない時
        if (! includeModify) {
			buf.append(" and status != ");
			buf.append("'");
			buf.append(DocInfo.TT_MODIFIED);   // 修正フラグがたっていないもののみ
			buf.append("'");
        }
		
		//buf.append(" order by firstConfirmDate desc");
		//buf.append(" order by confirmDate desc");
		buf.append(" order by firstConfirmDate desc,version desc");
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet  rs = st.executeQuery(sql);
            DocInfo info = null;
            ClaimInfo claimInfo = null;
            Creator creator = null;
            
            while (rs.next()) {
            	
				info = new DocInfo();
				
				info.setDocId(rs.getString(2));
				info.setFirstConfirmDate(rs.getString(3));
				info.setConfirmDate(rs.getString(4));
				info.setDocType(rs.getString(5));
				info.setSeries(rs.getString(6));
				info.setSeriesNumber(rs.getString(7));
				info.setTitle(rs.getString(8));
				info.setPurpose(rs.getString(9));
				
				claimInfo = new ClaimInfo();
				claimInfo.setDepartment(rs.getString(10));
				claimInfo.setInsuranceClass(rs.getString(11));
				info.setClaimInfo(claimInfo);
				
				info.setHasMark(rs.getBoolean(12));
				info.setHasImage(rs.getBoolean(13));
				info.setHasRp(rs.getBoolean(14));
				info.setHasTreatment(rs.getBoolean(15));
				info.setHasLaboTest(rs.getBoolean(16));
				
				Version version = new Version();
				version.setVersionNumber(rs.getString(17));
				if (rs.getString(18) != null) {
					version.setReleaseNote(rs.getString(18));
				}
				info.setVersion(version);
				
				if (rs.getString(19) != null) {
					ParentId parent = new ParentId();
					parent.setId(rs.getString(19));
					parent.setRelation(rs.getString(20));
					info.setParentId(parent);
				}
				
				creator = new Creator();
				creator.setId(rs.getString(21));
				creator.setName(rs.getString(22));
				creator.setLicense(rs.getString(23));
				info.setCreator(creator);
				
				info.setStatus(rs.getString(24).charAt(0));
				
				if (results == null) {
					results = new ArrayList();
				}
				
                results.add(info);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting the document history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    }
    
    /**
     * Returns Diagnosis History.
     *
     * @param pid 患者ID
     * @param past 最初の確定日
     * @param ArrayList DiagnosisEntryを格納したリスト
     */
    public ArrayList getDiagnosisHistory(String pid, String past) {

        Connection conn = null;
        java.sql.Statement st = null;
        ArrayList results = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select diagnosis,category,outcome,firstEncounterDate,startDate,endDate,firstConfirmDate,docId,confirmDate from tbl_diagnosis where pid = ");
        buf.append(addSingleQuote(pid));
        buf.append(" and firstConfirmDate>=");
        buf.append(addSingleQuote(past));
        buf.append(" order by firstConfirmDate desc");
        String sql = buf.toString();
        printTrace(sql);
                                         
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
			DiagnosisEntry entry = null;

            // Loop on results until complete
            while (rs.next()) {
                
                entry = new DiagnosisEntry();
                
                entry.setDiagnosis(rs.getString(1));
                entry.setCategory(rs.getString(2));
                entry.setOutcome(rs.getString(3));
                entry.setFirstEncounterDate(rs.getString(4));
                entry.setStartDate(rs.getString(5));
                entry.setEndDate(rs.getString(6));
                entry.setFirstConfirmDate(rs.getString(7));
                entry.setUID(rs.getString(8));
                entry.setConfirmDate(rs.getString(9));
                
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(entry);          
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting diagnosis history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    }        
    
    /**
     * Returns Order History.
     * 
     * @param pid 患者ID
     * @param order 検索するオーダ名
     * @param startDate 検索期間の開始日
     * @param endDate 検索期間の終了日
     * @return ArrayList オーダモジュールを格納した配列
     */
    public ArrayList getOrderHistory(String pid, String order, String startDate, String endDate) {
     
        Connection conn = null;
        java.sql.Statement st = null;
        ArrayList results = null;  
        
        StringBuffer buf = new StringBuffer();
        buf.append("select firstConfirmDate, stampName, beanXml from tbl_docContents where pid = ");
        buf.append(addSingleQuote(pid));
        buf.append(" and entityName=");
        buf.append(addSingleQuote(order));
        buf.append(" and firstConfirmDate>=");
        buf.append(addSingleQuote(startDate));
        buf.append(" and firstConfirmDate<=");
        buf.append(addSingleQuote(endDate));
        buf.append(" and status !=");
		buf.append("'");
        buf.append(DocInfo.TT_MODIFIED);
        buf.append("'");
        buf.append(" order by firstConfirmDate");
        String sql = buf.toString();
        printTrace(sql);
        
        try {                                          
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            Object[] data = null;
            XMLDecoder d = null;
			byte[] bytes = null;
            ModuleInfo stampInfo = null;
            Module stamp = null;
            
            while (rs.next()) {
            	
                data = new Object[2];
                
                data[0] = rs.getString(1);
                
                stampInfo = new ModuleInfo();
                stampInfo.setName(rs.getString(2));
                stamp = new Module();
                stamp.setModuleInfo(stampInfo);
                bytes = rs.getString(3).getBytes("UTF-8");

                // XMLDecode
                d = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(bytes)));
                stamp.setModel((IInfoModel)d.readObject());
				d.close();
				
                data[1] = stamp;
                
                if (results == null) {
					results = new ArrayList();
                }
				
                results.add(data);
            }
            
            rs.close();
            
        } catch (Exception e) {
            processError(conn, results, "SQLException while getting order history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return  results;
    } 
    

    public ArrayList getImageHistory(String pid, String startDate, String endDate, Dimension iconSize) {        
        
        Connection conn = null;
        java.sql.Statement st = null;
        ArrayList results = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select oid,firstConfirmDate,contenttype,title,medicalrole,jpegphoto from tbl_image where pid = ");
        buf.append(addSingleQuote(pid));
        buf.append(" and firstConfirmDate>=");
        buf.append(addSingleQuote(startDate));
        buf.append(" and firstConfirmDate<=");
        buf.append(addSingleQuote(endDate));
		buf.append(" and status !=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");        
        buf.append(" order by firstConfirmDate");
        String sql = buf.toString();
        printTrace(sql);
    
        try {                                          
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                
                ImageEntry ie = new ImageEntry();
                ie.setDN(String.valueOf(rs.getInt(1)));
                ie.setConfirmDate(rs.getString(2));
                ie.setContentType(rs.getString(3));
                ie.setTitle(rs.getString(4));
                ie.setMedicalRole(rs.getString(5));
                
                byte[] bytes = rs.getBytes(6);
                        
                // Create ImageIcon
                ImageIcon icon = new ImageIcon(bytes);
                if (icon == null) {
                    System.out.println("Icon is null");
                }
                else {
                    ie.setImageIcon(adjustImageSize(icon, iconSize));
                }
                
				if (results == null) {
					results = new ArrayList();
				}
                results.add(ie);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting image history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return  results;
    }
    
    public Schema getSchema(String oid) {
        
        Connection conn = null;
        java.sql.Statement st = null;
        Schema schema = null;

        StringBuffer buf = new StringBuffer();
        buf.append("select contenttype,title,medicalrole,jpegphoto from tbl_image where oid = ");
        buf.append(addSingleQuote(oid));
        String sql = buf.toString();
        printTrace(sql);
    
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                
                schema = new Schema();
                ExtRef extRef = new ExtRef();
                schema.setModel(extRef);
                
                extRef.setContentType(rs.getString(1));
                extRef.setTitle(rs.getString(2));
                extRef.setMedicalRole(rs.getString(3));
                byte[] bytes = rs.getBytes(4);

                // Create ImageIcon
                ImageIcon icon = new ImageIcon(bytes);
                if (icon == null) {
                    System.out.println("Icon is null");
                }
                else {
                    schema.setIcon(icon);
                }
            }
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, schema, "SQLException while getting image: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return schema;
    }
    
    
    public ArrayList getPvtHistory(String pid, String startDate, String endDate) {
        
        Connection conn = null;
        ArrayList results = null;
        java.sql.Statement st = null;
            
        StringBuffer buf = new StringBuffer();
        buf.append("select registTime from tbl_patientVisit where pid = ");
        buf.append(addSingleQuote(pid));        
        buf.append(" and registTime>=");
        buf.append(addSingleQuote(startDate + "T00:00:00"));
        buf.append(" and registTime<=");
        buf.append(addSingleQuote(endDate + "T23:59:59"));
        buf.append(" order by registTime");              
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	if (results == null) {
					results = new ArrayList();
            	}
                results.add(rs.getString(1));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting patient-visit history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    }    


    public ArrayList getOrderDateHistory(String pid, String order, String startDate, String endDate) {
                
        Connection conn = null;
        ArrayList results = null;
        java.sql.Statement st = null;
                
        StringBuffer buf = new StringBuffer();
        buf.append("select firstConfirmdate from tbl_docContents where pid =");
        buf.append(addSingleQuote(pid));
        buf.append(" and entityName = ");
        buf.append(addSingleQuote(order));
        buf.append(" and firstConfirmdate>=");
        buf.append(addSingleQuote(startDate + "T00:00:00"));
        buf.append(" and firstConfirmdate<=");
        buf.append(addSingleQuote(endDate + "T23:59:59"));
		buf.append(" and status !=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");        
        buf.append(" order by firstConfirmdate");              
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	if (results == null) {
					results = new ArrayList();
            	}
                results.add(rs.getString(1));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting order-date history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;   
    }
    

    public ArrayList getImageDateHistory(String pid, String startDate, String endDate) {
        
        Connection conn = null;
        ArrayList results = null;
        java.sql.Statement st = null;

        StringBuffer buf = new StringBuffer();
        buf.append("select firstConfirmdate from tbl_image where pid =");
        buf.append(addSingleQuote(pid));
        buf.append(" and firstConfirmdate>=");
        buf.append(addSingleQuote(startDate + "T00:00:00"));
        buf.append(" and firstConfirmdate<=");
        buf.append(addSingleQuote(endDate + "T23:59:59"));
		buf.append(" and status !=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");        
        buf.append(" order by firstConfirmdate");              
        String sql = buf.toString();
        printTrace(sql);
        
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
            	if (results == null) {
					results = new ArrayList();
            	}
                results.add(rs.getString(1));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting image-date history: " + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;   
    }
    
    public Karte getKarte(String docId) {
    	
		Connection conn = null;
		Karte karte = null;
		java.sql.Statement st = null;
		StringBuffer buf = null;

		try {
			conn = getConnection();
			st = conn.createStatement();
			
			// 1. Stamp
			buf = new StringBuffer();
			buf.append("select stampName, stampRole, entityName, stampMemo, moduleId, parentId, firstConfirmDate, confirmDate, beanXml from tbl_docContents where docId = ");
			buf.append(addSingleQuote(docId));
			buf.append(" order by stampNumber");              
			String sql = buf.toString();
			printTrace(sql);
			ResultSet rs = st.executeQuery(sql);
			
			Module module = null;
			ModuleInfo moduleInfo = null;
			String role = null;
			byte[] bytes = null;
			String beanXml = null;
			XMLDecoder d = null;
			karte = new Karte();
			
			while(rs.next()) {
				
				module = new Module();
				moduleInfo = new ModuleInfo();
				module.setModuleInfo(moduleInfo);

				moduleInfo.setName(rs.getString(1));
				moduleInfo.setRole(rs.getString(2));
				moduleInfo.setEntity(rs.getString(3));
				moduleInfo.setMemo(rs.getString(4));
				moduleInfo.setModuleId(rs.getString(5));
				moduleInfo.setParentId(rs.getString(6));
				moduleInfo.setFirstConfirmDate(rs.getString(7));
				moduleInfo.setConfirmDate(rs.getString(8));
				
				beanXml = rs.getString(9);
				bytes = beanXml.getBytes("UTF-8");
				// XMLDecode
				d = new XMLDecoder(
					new BufferedInputStream(
						new ByteArrayInputStream(bytes)));

				module.setModel((IInfoModel)d.readObject());

				karte.addModule(module);
			}
			
			// 2. image
			getImage(conn, docId, karte);

					
		} catch (Exception e) {
			processError(conn, karte, "Exception while getting the Karte: " + e.toString());
		}
		
		closeStatement(st);
		closeConnection(conn);
		
    	return karte;
    }
        
    private void getImage(Connection con, String docId, Karte karte) throws SQLException {
                
        StringBuffer buf = new StringBuffer();
        buf.append("select contentType,title,hRef,medicalRole,jpegPhoto from tbl_image where docId = ");
        buf.append(addSingleQuote(docId));
        buf.append(" order by imageNo");
        String sql = buf.toString();
        printTrace(sql);
        
        java.sql.Statement st = con.createStatement();
        
        ResultSet rs = st.executeQuery(sql);

        // Loop on result until complete
        while (rs.next()) {

            Schema schema = new Schema();
            ExtRef extRef = new ExtRef();
            schema.setModel(extRef);

            // contentType
            extRef.setContentType(rs.getString(1));

            // title
            extRef.setTitle(rs.getString(2));

            // HRef
            String val = rs.getString(3);
            //System.out.println("href=" + val);
            extRef.setHref(val);
            schema.setFileName(val);

            // MedicalRole
            extRef.setMedicalRole(rs.getString(4));

            // JPEG binary
            byte[] bytes = rs.getBytes(5);

            // Create ImageIcon
            ImageIcon icon = new ImageIcon(bytes);
            if (icon != null) {
                schema.setIcon(icon);
                debug("Got image");
            } else {
				debug("Image is null");
            }

            if (karte == null) {
                karte = new Karte();
            }
            
            karte.addSchema(schema);
        }
         
        rs.close();
        closeStatement(st);
        
    }
       
    public ArrayList getAppointments(String pid, String startDate, String endDate) {
        
        Connection conn = null;
        java.sql.Statement st = null;
        ArrayList results = null;
                
        // select pvtDate,appointName,appointMemo from appoint where 
        // pid = pi and pvtDate >=  and pvtDate <= 
        // order by pvtDate
        StringBuffer buf = new StringBuffer();
        buf.append("select oid,appointDate,appointName,appointMemo from tbl_appointment where pid = ");
        buf.append(addSingleQuote(pid));
        buf.append(" and appointDate>=");
        buf.append(addSingleQuote(startDate));
        buf.append(" and appointDate<=");
        buf.append(addSingleQuote(endDate));
        buf.append(" order by appointDate");
        String sql = buf.toString();
        printTrace(sql);
        
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                
                AppointEntry app = new AppointEntry();
                app.setDN(String.valueOf(rs.getInt(1))); // oid to modify
                app.setDate(rs.getString(2));
                app.setAppointName(rs.getString(3));
                app.setAppointMemo(rs.getString(4));
 
 				if (results == null) {
					results = new ArrayList();
 				}
                results.add(app);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, results, "SQLException while getting appointments:" + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return results;
    }
    
    public BaseClinicModule getBaseClinicModule(String pid) {
        
        Connection conn = null;
        java.sql.Statement st = null;
        BaseClinicModule ret = null;
        
        try {
                
            // Bloodtype(uid,pid,confirmdate,firstConfirmDate,abo,rho,memo,creatorId,status)
            // Allergy(uid,pid,confirmDate,firstConfirmDate,factor,severity,identifieddate,memo,creatorId,status)
            // Infection(uid,pid,confirmDate,firstConfirmDate,factor,examvalue,dentifieddate,memo,creatorId,status)
            // 1. Bloodtype
            StringBuffer buf = new StringBuffer();
            buf.append("select abo,rho,memo from tbl_bloodType where pid=");
            buf.append(addSingleQuote(pid));
			buf.append(" and status !=");
			buf.append("'");
			buf.append(DocInfo.TT_MODIFIED);
			buf.append("'");            
            buf.append(" order by firstConfirmDate desc");
            String sql = buf.toString();
            printTrace(sql);

            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
			BloodType blood = null;

            // Only latest TODO
            if (rs.next()) {
                blood = new BloodType();
                blood.setAbo(rs.getString(1));
                blood.setRhod(rs.getString(2));              
                blood.setMemo(rs.getString(3));
                
                if (ret == null) {
                	ret = new BaseClinicModule();
                }
                ret.setBloodType(blood);
            }
            rs.close();
            closeStatement(st);

            // 2. Allergy
            buf = new StringBuffer();
            buf.append("select factor,severity,severityTableId,identifieddate,memo from tbl_allergy where pid=");
            buf.append(addSingleQuote(pid));
			buf.append(" and status !=");
			buf.append("'");
			buf.append(DocInfo.TT_MODIFIED);
			buf.append("'");            
            buf.append(" order by firstConfirmDate desc");
            sql = buf.toString();
            printTrace(sql);

            st = conn.createStatement();
            rs = st.executeQuery(sql);
            Allergy allergy = null;

            // No limit TODO
            while (rs.next()) {
                allergy = new Allergy();
				allergy.setFactor(rs.getString(1));
				allergy.setSeverity(rs.getString(2));
				allergy.setSeverityTableId(rs.getString(3));
				allergy.setIdentifiedDate(rs.getString(4));
				allergy.setMemo(rs.getString(5));
				
				if (ret == null) {
					ret = new BaseClinicModule();
				}
				ret.addAllergy(allergy);
            }
            rs.close();
            closeStatement(st);

            // 3. Infection
            buf = new StringBuffer();
            buf.append("select factor,examvalue,identifieddate,memo from tbl_infection where pid=");
            buf.append(addSingleQuote(pid));
			buf.append(" and status !=");
			buf.append("'");
			buf.append(DocInfo.TT_MODIFIED);
			buf.append("'");            
            buf.append(" order by firstConfirmDate desc");
            sql = buf.toString();
            printTrace(sql);

            st = conn.createStatement();
            rs = st.executeQuery(sql);
            
			Infection infection = null;

            // No limit TODO
            while (rs.next()) {
				infection = new Infection();
				infection.setFactor(rs.getString(1));
				infection.setExamValue(rs.getString(2));
				infection.setIdentifiedDate(rs.getString(3));
				infection.setMemo(rs.getString(4));

				if (ret == null) {
					ret = new BaseClinicModule();
				}
				ret.addInfection(infection);
            }
            closeStatement(st);
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, ret, "SQLException while getting baseClinic info:" + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return ret;
    }
    
    public LifestyleModule getLifestyleModule(String pid) {
        
        Connection conn = null;
        java.sql.Statement st = null;
        LifestyleModule ret = null;
                
        StringBuffer buf = new StringBuffer();
        buf.append("select tobacco,alcohol,occupation,otherlifestyle from tbl_lifestyle where pid=");
        buf.append(addSingleQuote(pid));
		buf.append(" and status !=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");        
        buf.append(" order by firstConfirmDate desc");
        String sql = buf.toString();
        printTrace(sql);
           
        try {
            conn = getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            // Only latest TODO
            if (rs.next()) {
                ret = new LifestyleModule();
                ret.setTobacco(rs.getString(1));
                ret.setAlcohol(rs.getString(2));              
                ret.setOccupation(rs.getString(3));
                ret.setOther(rs.getString(4));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            processError(conn, ret, "SQLException while getting bloodtype:" + e.toString());
        }
        
        closeStatement(st);
        closeConnection(conn);
        
        return ret;
    }
        
    
    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {

        if ( (icon.getIconHeight() > dim.height) ||
             (icon.getIconWidth() > dim.width) ) {
            Image img = icon.getImage();
            float hRatio = (float)icon.getIconHeight() / dim.height;
            float wRatio = (float)icon.getIconWidth() / dim.width;
            int h, w;
            
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int)(icon.getIconWidth() / hRatio);
                
            } else {
                w = dim.width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
            
        } else {
            return icon;
        }
    }
}
