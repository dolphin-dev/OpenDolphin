/*
 * SqlKarteSaverDao.java
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

import java.io.*;
import java.beans.*;
import java.sql.*;

import open.dolphin.infomodel.Allergy;
import open.dolphin.infomodel.BaseClinicModule;
import open.dolphin.infomodel.BloodType;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ExtRef;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Infection;
import open.dolphin.infomodel.Karte;
import open.dolphin.infomodel.LifestyleModule;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.Schema;

/**
 * Karte データを保存する DAO クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlKarteSaverDao extends SqlDaoBean {
	
	private Karte model;
	private boolean modify;
	private int pvtOid;
            
    /** Creates new DataSaver */
    public SqlKarteSaverDao() {        
    }
    
    public void setKarte(Karte model) {
    	this.model = model;
    }
    
    public void setPvtOid(int oid) {
		pvtOid = oid;
    }
    
    public void setModify(boolean b) {
    	this.modify = b;
    }
                
    public void doWork() {
    	    	
    	String pid = model.getPatient().getId();
    	DocInfo docInfo = model.getDocInfo();
    	String docId = docInfo.getDocId();
		Module[] modules = null;
		Module module = null;
		ModuleInfo moduleInfo = null;
		byte[] bytes = null;
		String beanXml = null;
		
		// ドキュメントの内容属性を検出する
		boolean hasMark = false;
		
		// image があるかどうか
		boolean hasImage = model.getSchema() != null ? true : false;
		
		// RP があるかどうか
		boolean hasRp = model.getModule("medOrder") != null ? true : false;
		
		// 処置があるかどうか		
		boolean hasTreatment = model.getModule("treatmentOrder") != null ? true : false;
		
		// LaboTest があるかどうか				
		boolean hasLaboTest = model.getModule("testOrder") != null ? true : false;		
           
        Connection conn = null;
        
        try {
            conn = getConnection();
			printTrace("got connection");

            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. DocInfo を追加する            
            PreparedStatement ps = conn.prepareStatement(
				"insert into tbl_docinfo values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			ps.setString( 1, pid);               					// pid                   
            ps.setString( 2, docId);                    			// docId
			ps.setString( 3, docInfo.getFirstConfirmDate());        // firstConfirmdate
            ps.setString( 4, docInfo.getConfirmDate());             // confirmdate
            ps.setString( 5, docInfo.getDocType());   				// docType
			ps.setString( 6, docInfo.getSeries());                  // series
			ps.setString( 7, docInfo.getSeriesNumber());            // number
            ps.setString( 8, docInfo.getTitle());                   // title
            ps.setString( 9, docInfo.getPurpose());                 // purpose            
            ps.setString(10, docInfo.getClaimInfo().getDepartment());  // department
            ps.setString(11, docInfo.getClaimInfo().getInsuranceClass());  // insuranceClass
            
			ps.setBoolean(12, hasMark);								// hasImage
			ps.setBoolean(13, hasImage);							// hasImage
			ps.setBoolean(14, hasRp);								// hasRp
			ps.setBoolean(15, hasTreatment);						// hasTreatment
			ps.setBoolean(16, hasLaboTest);							// hasLaboTest
			
			ps.setString(17, docInfo.getVersion().getVersionNumber()); // version number
			ps.setString(18, docInfo.getVersion().getReleaseNote());            // relaese note
			
            if (docInfo.getParentId() != null) {
				ps.setString(19, docInfo.getParentId().getId());       	// parentId
				ps.setString(20, docInfo.getParentId().getRelation()); 	// parentIdRelation
            } else {
				ps.setString(19, null);       						// parentId
				ps.setString(20, null); 							// parentIdRelation
            }
            
            ps.setString(21, docInfo.getCreator().getId());         // creatorId
            ps.setString(22, docInfo.getCreator().getName());  // creatorName
            ps.setString(23, docInfo.getCreator().getLicense());    // creatorLicense
            
            ps.setString(24, String.valueOf(docInfo.getStatus()));	// status
           
           	printTrace(ps.toString());
            ps.executeUpdate();
			printTrace("executeUpdated docInfo");
            ps.close();
            
            // 2. Stamp を追加する
            modules = model.getModule();			
			
			if (modules != null) {
				
				ps = conn.prepareStatement("insert into tbl_docContents values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								
				for (int i = 0; i < modules.length; i++) {
					
					module = modules[i];
					moduleInfo = module.getModuleInfo();
					beanXml = getBeanXml(module.getModel());
					
					ps.setString(1, pid);               			// pid                   
					ps.setString(2, docId);							// docId
					
					ps.setString(3, moduleInfo.getName());			// stampName
					ps.setString(4, moduleInfo.getRole());    		// stampRole
					ps.setString(5, moduleInfo.getEntity());        // entityName
					ps.setString(6, moduleInfo.getGcpVisit());      // visit number	
					ps.setString(7, moduleInfo.getMemo());    		// stampMemo
					
					ps.setString(8, String.valueOf(i));             // stampNumber
					ps.setString(9, moduleInfo.getModuleId());		// moduleId;
					ps.setString(10, moduleInfo.getParentId());		// parentId;
					ps.setString(11, moduleInfo.getParentIdRelation());	// parentIdRelation;
					ps.setString(12, moduleInfo.getFirstConfirmDate()); // firstConfirmdate
					ps.setString(13, moduleInfo.getConfirmDate());      // confirmdate
					
					ps.setString(14, beanXml);                      // beanXml 
					ps.setString(15, "0");							// status
					
					ps.executeUpdate();
					printTrace("executeUpdated docContents");
				}
				
				ps.close();
			}
			
            // 3. 画像登録
            Schema[] schemas = model.getSchema();
            
            if (schemas != null) {
                
                ps = conn.prepareStatement("insert into tbl_image values(?,?,?,?,?,?,?,?,?,?,?,?)");

                for (int i = 0; i < schemas.length; i++) {  
                    String imageNo = String.valueOf(i);
                    Schema schema = schemas[i];
                    ExtRef extRef = (ExtRef)schema.getModel();
                    
					ps.setString( 1, pid);           					// pid
                    ps.setString( 2, docId);                           	// docId
                    ps.setString( 3, imageNo);                          // imageNo.
					ps.setString( 4, docInfo.getFirstConfirmDate());    // firstConfirmDate
                    ps.setString( 5, docInfo.getConfirmDate());         // confirmDate
                    ps.setString( 6, extRef.getHref());                 // href
                    ps.setString( 7, extRef.getContentType());          // contentType
                    ps.setString( 8, extRef.getTitle());                // title
                    ps.setString( 9, extRef.getMedicalRole());          // medicalRole
                    ps.setBytes (10, schema.getJPEGByte());             // jpegPhoto
                    ps.setString(11, docInfo.getCreator().getId());           // creatorId
                    ps.setString(12, "0");                              // status
                    
                    ps.executeUpdate();
                }
                
                ps.close();
            }
            
            // 4. Alletgy, Bloodtype, Infection
            IInfoModel iModel = null;
            Module m = model.getModule("baseClinic");
            
            if (m != null) {
            	
            	// 4-1. Allergy
            	iModel = m.getModel();
            	Allergy[] allergies = ((BaseClinicModule)iModel).getAllergy();
            	
            	if (allergies != null) {
            		
					ps = conn.prepareStatement("insert into tbl_allergy values(?,?,?,?,?,?,?,?,?,?,?)");
					
					for (int i = 0; i < allergies.length; i++) {				
						ps.setString(1, pid);								// pid
						ps.setString(2, docId);								// docId
						ps.setString(3, docInfo.getFirstConfirmDate());		// firstConfirmDate
						ps.setString(4, docInfo.getConfirmDate());			// confirmDate
						
						ps.setString(5, allergies[i].getFactor());			// factor
						ps.setString(6, allergies[i].getSeverity());		// severity
						ps.setString(7, allergies[i].getSeverityTableId());	// severity tableId
						ps.setString(8, allergies[i].getIdentifiedDate());	// identifiedDate
						ps.setString(9, allergies[i].getMemo());			// memo
						
						ps.setString(10, docInfo.getCreator().getId());			// creatorId
						ps.setString(11, "0");
						ps.executeUpdate();
					}
					ps.close();
            	}
            	
            	// 4-2.
            	BloodType bloodtype = ((BaseClinicModule)iModel).getBloodType();
            	
            	if (bloodtype != null) {
            		
					ps = conn.prepareStatement("insert into tbl_bloodType values(?,?,?,?,?,?,?,?,?)");
					ps.setString(1, pid);								// pid
					ps.setString(2, docId);								// docId
					ps.setString(3, docInfo.getFirstConfirmDate());		// firstConfirmDate
					ps.setString(4, docInfo.getConfirmDate());			// confirmDate
					
					ps.setString(5, bloodtype.getAbo());				// abo
					ps.setString(6, bloodtype.getRhod());				// rho
					ps.setString(7, bloodtype.getMemo());				// memo
					
					ps.setString(8, docInfo.getCreator().getId());			// creatorId
					ps.setString(9, "0");								// status
					ps.executeUpdate();
					ps.close();
            	}
            	
            	// 4-3.
            	Infection[] infections = ((BaseClinicModule)iModel).getInfection();
            	
            	if (infections != null) {
            		
					ps = conn.prepareStatement("insert into tbl_infection values(?,?,?,?,?,?,?,?,?,?)");
            		
            		for (int i = 0; i < infections.length; i++) {
						ps.setString(1, pid);								// pid
						ps.setString(2, docId);								// docId
						ps.setString(3, docInfo.getFirstConfirmDate());		// firstConfirmDate
						ps.setString(4, docInfo.getConfirmDate());			// confirmDate
						
						ps.setString(5, infections[i].getFactor());			// factor
						ps.setString(6, infections[i].getExamValue());		// examValue
						ps.setString(7, infections[i].getIdentifiedDate());	// identifiedDate
						ps.setString(8, infections[i].getMemo());			// memo
						
						ps.setString(9, docInfo.getCreator().getId());			// creatorId
						ps.setString(10, "0");
						ps.executeUpdate();
            		}
            		ps.close();
            	}
            }
            
            // 5. Lifestyle
            m = model.getModule("lifestyle");
			
            if (m != null) {
            	
				iModel = m.getModel();
            	
				ps = conn.prepareStatement("insert into tbl_lifestyle values(?,?,?,?,?,?,?,?,?)");
				ps.setString(1, pid);								// pid
				ps.setString(2, docId);								// docId
				ps.setString(3, docInfo.getFirstConfirmDate());		// firstConfirmDate
				ps.setString(4, docInfo.getConfirmDate());			// confirmDate
				
				ps.setString(5, ((LifestyleModule)iModel).getTobacco());
				ps.setString(6, ((LifestyleModule)iModel).getAlcohol());
				ps.setString(7, ((LifestyleModule)iModel).getOccupation());
				ps.setString(8, ((LifestyleModule)iModel).getOther());
				
				ps.setString(9, "0");
				ps.executeUpdate();
				ps.close();
            }
            
			// 6. write PVT status
			if (pvtOid != 0) {
				ps = conn.prepareStatement("update tbl_patientVisit set status = '3' where oid = ?");
				ps.setInt(1, pvtOid);
				ps.executeUpdate();
				ps.close();
			}
			
			// 7. modify
			if (modify) {
				doModify(conn, docInfo.getParentId().getId());
			}
            
			// 8. Commit
			conn.commit();
			debug("committed save the karte");
			
        } catch (Exception e) {
			rollback(conn);
			processError(conn, "dummy", "Exception while saving the karte: " + e);
			debug("Exception while saving the karte: " + e);
        }
        
        closeConnection(conn);
    }
    
    private void doModify(Connection conn, String docId) throws SQLException {
        
		java.sql.Statement st= null;
		String theDocId = addSingleQuote(docId);
        
        //conn.setAutoCommit(false);
        
        // 1. docInfo
        StringBuffer buf = new StringBuffer();
        buf.append("update tbl_docinfo set status=");
        buf.append("'");
        buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
        buf.append("where docId=");
        buf.append(theDocId);
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
        
        // 2. contents
		buf = new StringBuffer();
		buf.append("update tbl_docContents set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();            

        // 3. Image
		buf = new StringBuffer();
		buf.append("update tbl_image set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
        
        // 4. allergy
		buf = new StringBuffer();
		buf.append("update tbl_allergy set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);        
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
        
        // 5. blood_type
		buf = new StringBuffer();
		buf.append("update tbl_bloodType set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);         
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
        
        // 6. infection
		buf = new StringBuffer();
		buf.append("update tbl_infection set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);         
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
        
        // 7. lifestyle
		buf = new StringBuffer();
		buf.append("update tbl_lifestyle set status=");
		buf.append("'");
		buf.append(DocInfo.TT_MODIFIED);
		buf.append("'");
		buf.append("where docId=");
		buf.append(theDocId);         
        st= conn.createStatement();
        st.executeUpdate(buf.toString());
        st.close();
    }
    
    private String getModuleId(String docId, int moduleNo) {
    	StringBuffer buf = new StringBuffer();
    	buf.append(docId);
    	buf.append("-");
    	buf.append(String.valueOf(moduleNo));
    	return buf.toString();    
    }
    
    private byte[] getXMLBytes(Object bean)  {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    } 
    
	private String getBeanXml(Object bean)  {
		String ret = null;
		try {
			ret = new String(getXMLBytes(bean), "UTF-8");
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return ret;
	} 
}