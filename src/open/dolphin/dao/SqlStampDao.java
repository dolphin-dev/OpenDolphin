/*
 * StampDao.java
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
import java.util.ArrayList;
import java.io.*;
import java.beans.*;

import open.dolphin.client.StampTreeEntry;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlStampDao extends SqlDaoBean {
	
	private String whose;
    
	/** Creates a new instance of StampDao */
	public SqlStampDao() {
	}
    
	public String getWhoseTree() {
		return whose;
	}
    
	public ArrayList getTrees(String userId) {
        
		ArrayList ret = null;
		Connection conn = null;
        
		try {
			conn = getConnection();
            
			PreparedStatement ps = conn.prepareStatement("select * from tbl_stampTree where userId = ? order by number");
            
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			StampTreeEntry entry = null;
			
			while(rs.next()) {
				
				entry = new StampTreeEntry();
				
				entry.setId(rs.getString(2));
				entry.setUse(rs.getBoolean(3));
				entry.setNumber(rs.getInt(4));
				entry.setTreeXml(rs.getString(5));
				
				if (ret == null) {
					ret = new ArrayList();
					whose = userId;
				}
				ret.add(entry);
			}
			rs.close();
			
			// userId の Tree がないので、lasmanager tree があればそれををコピーする
			if (ret == null) {
				
				ps.setString(1, "lasmanager");
				rs = ps.executeQuery();

				while(rs.next()) {
					entry = new StampTreeEntry();

					entry.setId(rs.getString(2));
					entry.setUse(rs.getBoolean(3));
					entry.setNumber(rs.getInt(4));
					entry.setTreeXml(rs.getString(5));
					
					if (ret == null) {
						ret = new ArrayList();
						whose = "lasmanager";
					}
					ret.add(entry);
				}
				rs.close();
			}
			
			ps.close();
			
		} catch (SQLException e) {
			processError(conn, ret,"SQLException while reading the stamp tree: " + e.toString());
        
		} catch (NullPointerException  ne) {
			processError(conn, ret,"NUllPointerException while reading the stamp tree: " + ne.toString());
            
		} catch (RuntimeException re) {
			processError(conn, ret,"NUllPointerException while reading the stamp tree: " + re.toString());
        
		} catch (Exception  ee) {
			processError(conn, ret,"Exception while reading the stamp tree: " + ee.toString());
		}
        
		closeConnection(conn);
                
		return ret;
	}
	
	public String getTree(String userId) {
        
		Connection conn = null;
		String xml = null;
        
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("select tree from tbl_stamptree where userId = ?");
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				xml = new String(bytes);
			}
            
			ps.close();
			rs.close();
        
		} catch (SQLException e) {
			processError(conn, xml,"SQLException while reading the stamp tree: " + e.toString());
        
		} catch (NullPointerException  ne) {
			processError(conn, xml,"NUllPointerException while reading the stamp tree: " + ne.toString());
            
		} catch (RuntimeException re) {
			processError(conn, xml,"NUllPointerException while reading the stamp tree: " + re.toString());
        
		} catch (Exception  ee) {
			processError(conn, xml,"Exception while reading the stamp tree: " + ee.toString());
		}
        
		closeConnection(conn);
        
		return xml;
	}
	
	public String getGcpTree() {
        
		Connection conn = null;
		String xml = null;
        
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("select tree from tbl_gcptree where id = ?");
			ps.setString(1, "gcp");
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				xml = new String(bytes);
			}
            
			ps.close();
			rs.close();
        
		} catch (SQLException e) {
			processError(conn, xml,"SQLException while reading the stamp tree: " + e.toString());
        
		} catch (NullPointerException  ne) {
			processError(conn, xml,"NUllPointerException while reading the stamp tree: " + ne.toString());
            
		} catch (RuntimeException re) {
			processError(conn, xml,"NUllPointerException while reading the stamp tree: " + re.toString());
        
		} catch (Exception  ee) {
			processError(conn, xml,"Exception while reading the stamp tree: " + ee.toString());
		}
        
		closeConnection(conn);
        
		return xml;
	}	
	
	public boolean putGcpTree(String gcpTree) {
		
		boolean ret = false;
		Connection conn = null;
		byte[] data = gcpTree.getBytes();
		
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("select id from tbl_gcptree where id = ?");
			ps.setString(1, "gcp");
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				// 既存の Tree
				System.out.println("update the GCPTree");
				ps.close();
				rs.close();
				ps = conn.prepareStatement("update tbl_gcptree set tree = ? where id = ?");
				ps.setBytes(1, data);
				ps.setString(2, "gcp");
				
				int result = ps.executeUpdate();
				ps.close();
				ret = true;
				
			} else {
				// 	新規 Tree
				System.out.println("new GCPTree");
				ps.close();
				rs.close();
				ps = conn.prepareStatement("insert into tbl_gcptree values(?,?)");
				ps.setString(1, "gcp");
				ps.setBytes(2, data);
				
				int result = ps.executeUpdate();
				ps.close();
				ret = true;
			}
			
		} catch (Exception e) {
			processError(conn, "dummy", "Exception while saving the GcpTreeXml: " + e.toString());
			ret = false;
		}
		
		closeConnection(conn);
		
		return ret;    
	}
	
	public boolean deleteGcpTree() {
		
		boolean ret = false;
		Connection conn = null;
		
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("delete from tbl_gcptree where id=?");
			ps.setString(1, "gcp");
			int n = ps.executeUpdate();
			ps.close();
			ret = true;
			
		} catch (Exception e) {
			processError(conn, "dummy", "Exception while deleting the GcpTreeXml: " + e.toString());
			ret = false;
		}
		
		closeConnection(conn);
		
		return ret;    
	}	
	
	public boolean putTree(String userId, String treeXml) {
        
		boolean ret = false;
		Connection conn = null;
		byte[] data = treeXml.getBytes();
		
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("select userId from tbl_stamptree where userId = ?");
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				// 既存の Tree
				System.out.println("update the stampTree");
				ps.close();
				rs.close();
				ps = conn.prepareStatement("update tbl_stamptree set tree = ? where userId = ?");
				ps.setBytes(1, data);
				ps.setString(2, userId);
				
				int result = ps.executeUpdate();
				ps.close();
				ret = true;
				
			} else {
				// 	新規 Tree
				System.out.println("new stampTree");
				ps.close();
				rs.close();
				ps = conn.prepareStatement("insert into tbl_stamptree values(?,?)");
				ps.setString(1, userId);
				ps.setBytes(2, data);
				
				int result = ps.executeUpdate();
				ps.close();
				ret = true;
			}
			
		} catch (Exception e) {
			processError(conn, "dummy", "Exception while saving the GcpTreeXml: " + e.toString());
			ret = false;
		}
		
		closeConnection(conn);
		
		return ret; 
	}
        
	/*public boolean saveTrees(String userId, ArrayList entries, boolean newTree) {
        
		debug("dao: entering save tree");
        
		boolean ret = false;
		
		if (newTree) {
			 ret = addTree2(userId, entries);

		} else {
			ret = updateTree2(userId, entries);
		}
		
		return ret;
	}
	
	private boolean updateTree2(String userId, ArrayList entries) {
        
		debug("dao: entering update tree");
        
		boolean ret = false;
        
		Connection conn = null;
        
		try {
			conn = getConnection();
            
			System.out.println("dao: getConnection");
			PreparedStatement ps = conn.prepareStatement("update tbl_stampTree set use=?, number=?,tree=? where userId=? and id=?");
                
			int cnt = entries.size();
			StampTreeEntry entry = null;
			for (int i = 0; i < cnt; i++) {
				
				entry = (StampTreeEntry)entries.get(i);
				
				ps.setBoolean(1, entry.isUse());
				ps.setInt(2, entry.getNumber());
				ps.setString(3, entry.getTreeXml());
				ps.setString(4, userId);
				ps.setString(5, entry.getId());
				
				int result = ps.executeUpdate();
				debug("dao: executeUpdate");
				debug("dao:" + result);  
			}
            
			ps.close();
			ret = true;
            
			System.out.println("dao: update tree succeded");
            
		} catch (SQLException e) { 
			processError(conn, "dummy", "SQLException while saving the StampTreeXml: " + e.toString());
			ret = false;
			System.out.println("dao: update tree failed");
		}
        
		closeConnection(conn);
        
		return ret;
	}
	
	private boolean addTree2(String userId, ArrayList entries) {
        
		boolean ret = false;
        
		Connection conn = null;
        
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("insert into tbl_stampTree values(?,?,?,?,?)");
            
			int cnt = entries.size();
			StampTreeEntry entry = null;
			
			for (int i = 0; i < cnt; i++) {
				entry = (StampTreeEntry)entries.get(i);
				
				ps.setString(1, userId);
				ps.setString(2, entry.getId());
				ps.setBoolean(3, entry.isUse());
				ps.setInt(4, entry.getNumber());
				ps.setString(5, entry.getTreeXml());
				
				int result = ps.executeUpdate();
				debug("dao: executeUpdate");
				debug("dao:" + result);  
			}
			            
			ps.close();
            
			ret = true;
            
		} catch (SQLException e) {            
			processError(conn, "dummy", "SQLException while saving the StampTreeXml: " + e.toString());
			ret = false;
		}
        
		closeConnection(conn);
        
		return ret;
	} */ 
    
	public boolean addStamp(String userId, String category, String stampId, IInfoModel model) {
        
		Connection conn = null;

		boolean result = false;
        
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement("insert into tbl_stamp values(?,?,?,?)");
            
			ps.setString(1, userId);
			ps.setString(2, category);
			ps.setString(3, stampId);
			//byte[] bytes = getXMLBytes(model);
			ps.setString(4, getBeanXml(model));
            
			int count = ps.executeUpdate();
			ps.close();
            
			result = count == 1 ? true : false;
            
		} catch (NullPointerException ne) {
			processError(conn, "dummy", "NullPointerException,maybe LDAPConnection is null");
			result = false;
            
		} catch (SQLException le) {
			processError(conn, "dummy", "SQLException while writing the stamp model: " + le.toString());
			result = false;
            
		} catch (Exception e) {
			processError(conn, "dummy", "Problem while adding the stamp model: " + e.toString());
		}
        
		closeConnection(conn);
        
		return result;        
        
	}
    
	public IInfoModel getStamp(String userId, String category, String stampId) {
        
		Connection conn = null;
        
		IInfoModel ret = null;
        
		try {    
			conn = getConnection();
			//PreparedStatement ps = conn.prepareStatement("select stamp from stamp where userid=? and category=? and stampid=?");
			//PreparedStatement ps = conn.prepareStatement("select stamp from stamp where userid=? and stampid=?");
			PreparedStatement ps = conn.prepareStatement("select stamp from tbl_stamp where stampid=?");
            
			//ps.setString(1, userId);
			ps.setString(1, stampId);
			//ps.setString(2, category);
			//ps.setString(3, stampId);
            
			ResultSet rs = ps.executeQuery();
            
			if (rs.next()) {
            
				byte[] bytes = rs.getString(1).getBytes("UTF-8");
				XMLDecoder d = new XMLDecoder(
									  new BufferedInputStream(
										  new ByteArrayInputStream(bytes)));
                
				ret = (IInfoModel)d.readObject();
				d.close();
			}
			ps.close();
			rs.close();
            
		} catch (NullPointerException ne) {
			processError(conn, ret, "NullPointerException,maybe LDAPConnection is null");
            
		} catch (SQLException le) {
			processError(conn, ret, "SQLException while reading the stamp model: " + le.toString());
        
		} catch (Exception e) {
			processError(conn, ret, "Problem while reading the stamp model: " + e.toString());
		}
        
		closeConnection(conn);
        
		return ret != null ? ret : null;        
	} 
    
	public boolean removeStamp(String userId, String category, String stampId) {
        
		Connection conn = null;

		boolean result = false;
        
		try {
			conn = getConnection();
			//PreparedStatement ps = conn.prepareStatement("delete from stamp where userId=? and category=? and stampId=?");
			PreparedStatement ps = conn.prepareStatement("delete from tbl_stamp where stampId=?");
            
			//ps.setString(1, userId);
			//ps.setString(2, category);
			ps.setString(1, stampId);
           
			int count = ps.executeUpdate();
			ps.close();
            
			result = count == 1 ? true : false;
            
		} catch (NullPointerException ne) {
			processError(conn, "dummy", "NullPointerException,maybe LDAPConnection is null");
			result = false;
            
		} catch (SQLException le) {
			processError(conn, "dummy", "SQLException while writing the stamp model: " + le.toString());
			result = false;
		}
        
		closeConnection(conn);
        
		return result;        
        
	}    
    
	/**
	 * Stamp Model Bean の XML バイト配列を返す
	 * @param model スタンプのモデル
	 * @return XMLエンコードされたバイト配列
	 */
	private byte[] getXMLBytes(IInfoModel model)  {
        
		byte[] ret = null;        
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bos));            
		e.writeObject(model);
		e.close();
		ret = bos.toByteArray();    
		return ret;
	}
    
	private String getBeanXml(IInfoModel model) {
    	
		String ret = null;
    	
		try {
			ret = new String(getXMLBytes(model), "UTF-8");       
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
    	
		return ret;
	}
}
