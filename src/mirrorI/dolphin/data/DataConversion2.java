/*
 * DataConversion2.java
 *
 * Created on 2003/04/01, 21:19
 */

package mirrorI.dolphin.data;

import java.util.*;
import java.sql.*;

import open.dolphin.infomodel.*;
import netscape.ldap.*;

/**
 *
 * @author  kazm
 */
public class DataConversion2 {
    
    //private String creatorID;
    //private String department;
    private ArrayList users;
    private String encoding = "UTF8";
    
    /** Creates a new instance of DataConversion2 */
    public DataConversion2() {
    }
    
    public void setUsers(ArrayList list) {
        users = list;
    }
    
    /*public void setCreatorId(String val) {
        creatorID = val;
    }
    
    public void setDepartment(String val) {
        department = val;
    }*/
    
    public boolean doDataTransfer(LDAPConnection oldLd, Connection conn){

        PreparedStatement psmt = null;
        boolean result = false;

        //Getting LDAP data sources (CN) and PostgreSQL table names

        String [] ldapCN      = getLdapCN();
        String [] tableName = getTableName();

        //Loop  for each LDAP data source
        for(int i=0;i<ldapCN.length;i++) {

            //logger.info("Starting to handle "+ldapCN[i]);
            debug("Starting to handle "+ldapCN[i]);

            //Getting LDAP attribute names and corresponding table field names for LDAP data source
            String[] LdapAttribute      = DataMapping.getLDAPAttribute(i);
            String[] tableField         = DataMapping.getTableField(i);
            String[] dataType           = DataMapping.getDataType(i);
            String[] condition          = DataMapping.getCondition(i);
            String[] LDAPValue          =  new String[LdapAttribute.length];

            // Creating PreparedStatement for inserting values in PostgreSQL
            StringBuffer sql = new StringBuffer("insert into " + tableName[i] + " (");

            int k=0;
            for(k=0; k < (LdapAttribute.length-1); k++){
                sql.append(tableField[k] + ",");
            }

            sql.append(tableField[k]  + ") values (");

            int m=0;
            for(m=0;m<(LdapAttribute.length-1);m++){
                sql.append("?,");
            }
            sql.append("?)");

            //logger.info(sql.toString());
            debug(sql.toString());

            try {
                psmt = conn.prepareStatement(sql.toString());
                //logger.info("Prepared statement created for  " + ldapCN[i]);
                debug("Prepared statement created for  " + ldapCN[i]);
            }
            catch (SQLException sqle){
                //logger.warning("SQL Exception while creating Prepared Statement");
                //logger.warning( "Exception details:"  + sqle );
                System.out.println("Exception details:"  + sqle);
            }
            catch(Exception e) {
                //logger.warning("Exception while creating Prepared Statement");
                //logger.warning( "Exception details:"  + e );
                System.out.println("Exception details:"  + e);
            }

	    //Getting LDAP records
	    // Start of Handling each Table / distinguished name (DN)
	    try {
                // Getting all entries
		LDAPSearchResults res = oldLd.search(ldapCN[i], LDAPv2.SCOPE_ONE, "(objectclass=*)",LdapAttribute, false);

		//logger.warning("Inside search results for " +ldapCN[i]+ " number of result is "+ allResults.getCount());
                //debug("Inside search results for " +ldapCN[i]+ " number of result is "+ allResults.getCount());
		// Start of Handling each record
		while(res != null && res.hasMoreElements()) {
                    
                    try {

                        LDAPEntry entry = res.next();
                        debug("DN = " + entry.getDN());
                        Enumeration enumVals;
                        String cid = null;
                        
                        for (int j = 0; j < LdapAttribute.length; j++) {
                        
                            LDAPAttribute attr = entry.getAttribute(LdapAttribute[j]);
                            
                            if(attr == null) {
                                
                                if( condition[j].equals("visibleFlag") ) {
                                    psmt.setString((j+1), "1");
                                
                                } else{
                                    psmt.setString((j+1), null);
                                    //logger.info("Setting blank value for  "+LdapAttribute[j]);
                                    //debug("Setting blank value for  "+ LdapAttribute[j]);
                                }
                                
                            } else if (attr != null) {
                                
                                byte [][] enumValsByte = attr.getByteValueArray();
                                    
                                if (enumValsByte.length != 0) {
                                    
                                    LDAPValue[j] = new String(enumValsByte[0],encoding);
                                    
                                    //if (!dataType[j].equals("bytea")) {
                                        //debug(LDAPValue[j]);
                                    //}

                                    //in case the value from LDAP needs to be changed
                                    if(!(condition[j].equals("none"))){

                                        if(condition[j].equals("visibleFlag")){
                                            
                                            if( (LDAPValue[j] != null) && (LDAPValue[j].equals("true")) ){
                                                LDAPValue[j] = "0";
                                                
                                            } else{
                                                    LDAPValue[j] = "1";
                                            }
                                        }
                                        
                                        if (condition[j].equals("keepCid")){
                                            cid = LDAPValue[j];
                                        }

                                        if(condition[j].equals("creatorID")){
                                            //LDAPValue[j] = creatorID;  2003-04-14
                                            LDAPValue[j] = null;
                                        }

                                        if(condition[j].equals("department")){
                                            //LDAPValue[j] = department;
                                            if (cid != null) {
                                                 LDAPValue[j] = getDepartment(cid);
                                                 cid = null;
                                                 
                                            } else {
                                                LDAPValue[j] = null;
                                            }
                                        }

                                        // 2003-04-01 for appointment
                                        if(condition[j].equals("null")){
                                            LDAPValue[j] = null;
                                        }
                                    }
                                    
                                    if(dataType[j].equals("text")) {
                                        //check LDAPValue[j] for any unsupported char(like |)
                                        psmt.setString((j+1), mirrorI.dolphin.util.CharConversion.convert(LDAPValue[j]));

                                    } else if(dataType[j].equals("int")) {
                                        psmt.setInt((j+1), Integer.valueOf(LDAPValue[j]).intValue());
                                    
                                    } else if(dataType[j].equals("char")) {
                                        psmt.setInt((j+1), Integer.valueOf(LDAPValue[j]).intValue());
                                    
                                    } else if(dataType[j].equals("bytea")) {
                                        psmt.setBytes((j+1), enumValsByte[0]);
                                    
                                    } else{
                                            //logger.warning("Undefined datatype for  "+ LdapAttribute[j]+ " in " + ldapCN[i]);
                                        debug("Undefined datatype for  "+ LdapAttribute[j]+ " in " + ldapCN[i]);
                                    }
                                }
                            }
                        }
                        
                        psmt.executeUpdate();
                        //logger.info("psmt is  "+psmt);
                        //debug("psmt is  "+ psmt);
                        psmt.close();	//closing prepared statement for the data source
                           //logger.info("Closing prepared statement for data source "+ldapCN[i] );
                        debug("Closing prepared statement for data source "+ldapCN[i] );
                        
                    } catch (SQLException sqle){
                            //logger.warning("SQL Error in closing prepared statement for data source "+ldapCN[i] );
                            //logger.warning( "Exception details:"  + sqle );
                            System.out.println("Exception details:"  + sqle);                                    
                        
                    } catch (Exception e){
                            //logger.warning("Error in closing prepared statement for data source "+ldapCN[i] );
                            //logger.warning( "Exception details:"  + e );
                            System.out.println("Exception details:"  + e);                        
                    }

                } // ends for each entry
            
            } catch (LDAPException sqle){
                //logger.warning("SQL Exception while executing sql statements");
                //logger.warning( "Exception details:"  + sqle );
                //logger.warning("There is no entry for "+ ldapCN[i]);
                System.out.println("Exception details:"  + sqle);
            } catch(Exception e) {
                //logger.warning("Exception while executing sql statements");
                //logger.warning( "Exception details:"  + e );
                System.out.println("Exception details:"  + e);
            }
        
        } // End for each base / table
        
        try{
            conn.commit();
            debug("Commited PostgreSQL databse");
            //psmt.close();
            //postgresConnection.releasePostgresConnection(conPostgre);
            //logger.info("Closing PostgreSQL Connection");
            //debug("Closing PostgreSQL Connection");
            //instance.releaseLDAPConnection(conLDAP);
            //logger.info("Closing LDAP Connection");
            //debug("Closing LDAP Connection");
        } catch (SQLException sqle){
            //logger.warning("SQL exception while closing database");
            System.out.println("Exception details:"  + sqle);
            //logger.warning( "Exception details:"  + sqle );
            result = false;
            
        } catch(Exception e) {
            //logger.warning("Exception while closing database");
            //logger.warning( "Exception details:"  + e );
            System.out.println("Exception details:"  + e);
            result = false;
        }
        //logger.finer("Method Exit");
        return result;
    }

    private String[] getLdapCN(){
        return DataMapping.getLdapCN();
    }

    private String[] getTableName(){
        return DataMapping.getDatabaseTable();
    }
    
    private void debug(String msg) {
        System.out.println(msg);
    }
    
    private String getDepartment(String id) {
        
        if (users == null || users.size() == 0) {
            return null;
        }
        
        String dept = null;
        
        int userCount = users.size();
        
        for (int i = 0; i < userCount; i++) {
            
            UserProfileEntry profile = (UserProfileEntry)users.get(i);
            if (profile.getUserId().equals(id)) {
                dept = DataMapping.getDepartmentName(profile.getDepartmentId());
                break;
            }
        }
        
        return dept;
    }
}
