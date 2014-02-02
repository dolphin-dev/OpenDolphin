/*
 * DataConverison.java
 *
 * Delivered on 2003/01/31
 *
 * Last updated on 2003/02/28
 */
package mirrorI.dolphin.data;

import java.util.*;
import java.sql.*;
import java.io.*;
import netscape.ldap.*;
import java.util.logging.*;

/**
 *
 * This class converts data from LDAP to Postgres
 *
 * @author  Aniruddha, Mirror - I
 */
public class DataConversion {

    public static final String createrSearchKey      ="cn=Document,cn=Karte,o=Dolphin";
    public static final String departmentSearchKey   ="ou=DolphinUsers,o=Dolphin";

    private static Properties postgresConenctionParameter=null;

    private static Connection conPostgre = null;

    private static mirrorI.dolphin.dao.PostgresConnection postgresConnection;

    private static mirrorI.dolphin.server.IniFileRead iniFileRead;

    private static Logger logger;

    private String encoding = "UTF8";

    /** Creates new DataConversion */
    public DataConversion() {
        postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(postgresConenctionParameter);
    }
    
    private boolean doMasterFlag(DataConversion dataConv, ConnectingToLDAP instance,  LDAPConnection conLDAP){
        
        PreparedStatement psmt=null;
        boolean result = false;
        
        int count = DataMapping.masterBase.length;
        String[] attrs = new String[]{"mmlMasterItemCode"};
        
        for (int i = 0; i < count; i++) {
            
            String base = DataMapping.masterBase[i];
            System.out.println(base);
            String objectclass = DataMapping.masterObjClass[i];
            String table = DataMapping.masterTable[i];
            
            StringBuffer buf = new StringBuffer();
            //buf.append("(&(objectclass=");
            //buf.append(objectclass);
            //buf.append(")(mmlFreqFlag=*))");
            //String filter = buf.toString();
            buf.append("(&(mmlMasterItemCode=*)(mmlFreqFlag=*))");
            String filter = buf.toString();
            System.out.println(filter);
            
            buf = new StringBuffer();
            buf.append("update ");
            buf.append(table);
            buf.append(" set freqflag='1' where code=?");
            String sql = buf.toString();
            System.out.println(sql);
            
            try {
                //conPostgre.setAutoCommit(false);
                psmt = conPostgre.prepareStatement(sql);
                
            } catch (SQLException e) {
                debug(e.toString());
            }
            
            try {
                LDAPSearchResults allResults = conLDAP.search(base, LDAPConnection.SCOPE_ONE, filter,attrs, false);
                
                while(allResults.hasMoreElements()) {
            
                    //System.out.println("Got " + allResults.getCount() + " flag");
                    LDAPEntry entry = allResults.next();
                    Enumeration enumVals;

                    LDAPAttribute attr = entry.getAttribute("mmlMasterItemCode");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            
                            String code = (String)enumVals.nextElement();
                            System.out.println(code);
                            //psmt.setString(1, code);
                            //psmt.executeUpdate();
                        }
                    }
                }                
            } catch (Exception e) {
                debug(e.toString());
            }
            
            try {
                conPostgre.commit();
                psmt.close();
            } catch (Exception e2) {
                debug(e2.toString());
            }
            result = true;
        }
        
        return result;
    }
    

    /**
     *
     * doDataTransfer(), doDataTransfer converts LDAP data into postgres<br>
     *
     * This method is called from main()<br>
     *
     */
    private boolean doDataTransfer(DataConversion dataConv, ConnectingToLDAP instance,  LDAPConnection conLDAP){

        //logger.finer("Method Entry");
        PreparedStatement psmt=null;
        boolean result = false;

        //Getting LDAP data sources (CN) and PostgreSQL table names

        String [] ldapCN      = dataConv.getLdapCN();
        String [] tableName = dataConv.getTableName();

        //Loop  for each LDAP data source
        for(int i=0;i<ldapCN.length;i++) {
        //for(int i=1;i<2;i++) {
        //for(int i=9;i<ldapCN.length;i++) {
        //for(int i=11;i<ldapCN.length;i++) {

            //logger.info("Starting to handle "+ldapCN[i]);
            debug("Starting to handle "+ldapCN[i]);

            //Getting LDAP attribute names and corresponding table field names for LDAP data source
            String LdapAttribute[]     = DataMapping.getLDAPAttribute(i);
            String tableField []       = DataMapping.getTableField(i);
            String dataType  []        = DataMapping.getDataType(i);
            String condition []        = DataMapping.getCondition(i);
            String LDAPValue[]         =  new String[LdapAttribute.length];

            // Creating PreparedStatement for inserting values in PostgreSQL
            StringBuffer sql = new StringBuffer("insert into " + tableName[i] + " (");

            int k=0;
            for(k=0;k<(LdapAttribute.length-1);k++){
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
                psmt = conPostgre.prepareStatement(sql.toString());
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
		LDAPSearchResults allResults = conLDAP.search(ldapCN[i], LDAPv2.SCOPE_SUB, (LdapAttribute[0]+"=*"),LdapAttribute, false);

		//logger.warning("Inside search results for " +ldapCN[i]+ " number of result is "+ allResults.getCount());
                debug("Inside search results for " +ldapCN[i]+ " number of result is "+ allResults.getCount());
		// Start of Handling each record
		while(allResults.hasMoreElements()) {

                    LDAPEntry entry = allResults.next();
		    LDAPAttribute attribute = entry.getAttribute(LdapAttribute[0]);

                    Enumeration enumEachVal;
                    LDAPAttribute eachVal = entry.getAttribute(LdapAttribute[0]);

                    //logger.info("LDAPAttribute is "+eachVal + "in " + ldapCN[i]);
                    debug("LDAPAttribute is "+eachVal + "in " + ldapCN[i]);
                    
                    if(eachVal != null) {
                        enumEachVal = eachVal.getStringValues();
                        LDAPEntry eachEntry;

			if ( (enumEachVal != null) && enumEachVal.hasMoreElements() ) {
                            LDAPValue[0] = new String((String)enumEachVal .nextElement());

                            //logger.info("LDAPValue[0] is " +LDAPValue[0]);
                            debug("LDAPValue[0] is " +LDAPValue[0]);
                            //logger.info("LdapAttribute[0] is " +LdapAttribute[0]);
                            debug("LdapAttribute[0] is " +LdapAttribute[0]);
                            eachEntry = conLDAP.read((LdapAttribute[0]+"="+LDAPValue[0]+","+ldapCN[i]), LdapAttribute);    // Getting entry corresponding to unique ID

                            for(int j=0;j<LdapAttribute.length;j++){

                                Enumeration enumVals;
                                LDAPAttribute attr = eachEntry.getAttribute(LdapAttribute[j]);
                                //if there is no value for attribute, then set it null
                                if(attr==null) {
                                    if( condition[j].equals("visibleFlag") ) {
                                        psmt.setString((j+1), "1");
                                    }
                                    else{
                                        psmt.setString((j+1), null);
                                        //logger.info("Setting blank value for  "+LdapAttribute[j]);
                                        debug("Setting blank value for  "+LdapAttribute[j]);
                                    }
                                }
				else if (attr != null) {
                                    byte [][] enumValsByte = attr.getByteValueArray();
                                    if (enumValsByte.length != 0) {
                                        LDAPValue[j] = new String(enumValsByte[0],encoding);

					//in case the value from LDAP needs to be changed
                                        if(!(condition[j].equals("none"))){

                                            if(condition[j].equals("visibleFlag")){
                                                if( (LDAPValue[j] != null) && (LDAPValue[j].equals("true")) ){
                                                    LDAPValue[j] = "0";
						}
						else{
                                                    LDAPValue[j] = "1";
						}
                                            }

                                            if(condition[j].equals("creatorID")){
                                                    LDAPValue[j] = dataConv.getCreatorID(createrSearchKey, conLDAP);
                                            }

                                            if(condition[j].equals("department")){
                                                    LDAPValue[j] = DataMapping.getDepartmentName(dataConv.getDepartment(departmentSearchKey, conLDAP));
                                            }
                                            
                                            // 2003-04-01 for appointment
                                            if(condition[j].equals("null")){
                                                    LDAPValue[j] = null;
                                            }
                                        }
					if(dataType[j].equals("text")) {
                                            //check LDAPValue[j] for any unsupported char(like |)
                                            psmt.setString((j+1), mirrorI.dolphin.util.CharConversion.convert(LDAPValue[j]));

					}
                                        else if(dataType[j].equals("int")) {
                                                psmt.setInt((j+1), Integer.valueOf(LDAPValue[j]).intValue());
                                        }

                                        else if(dataType[j].equals("char")) {
                                                psmt.setInt((j+1), Integer.valueOf(LDAPValue[j]).intValue());
                                        }

                                        else if(dataType[j].equals("bytea")) {
                                                psmt.setBytes((j+1), enumValsByte[0]);
                                        }

                                        else{
                                                //logger.warning("Undefined datatype for  "+ LdapAttribute[j]+ " in " + ldapCN[i]);
                                            debug("Undefined datatype for  "+ LdapAttribute[j]+ " in " + ldapCN[i]);
                                        }
                                    }
                                }
                            }
                            psmt.executeUpdate();
                            //logger.info("psmt is  "+psmt);
                            debug("psmt is  "+psmt);
						}
                            }  // End of Handling each record
                            
                            try{
                               psmt.close();	//closing prepared statement for the data source
                               //logger.info("Closing prepared statement for data source "+ldapCN[i] );
                               debug("Closing prepared statement for data source "+ldapCN[i] );
                            }
                            catch (SQLException sqle){
                                    //logger.warning("SQL Error in closing prepared statement for data source "+ldapCN[i] );
                                    //logger.warning( "Exception details:"  + sqle );
                                    System.out.println("Exception details:"  + sqle);
                            }
                            catch (Exception e){
                                    //logger.warning("Error in closing prepared statement for data source "+ldapCN[i] );
                                    //logger.warning( "Exception details:"  + e );
                                    System.out.println("Exception details:"  + e);
                            }

                        }   // End of handling each table / distinguished name (DN)
			
                    }
                    catch (SQLException sqle){
                            //logger.warning("SQL Exception while executing sql statements");
                            //logger.warning( "Exception details:"  + sqle );
                            //logger.warning("There is no entry for "+ ldapCN[i]);
                            System.out.println("Exception details:"  + sqle);
                    }
                    catch(Exception e) {
                            //logger.warning("Exception while executing sql statements");
                            //logger.warning( "Exception details:"  + e );
                            System.out.println("Exception details:"  + e);
                    }
                    finally{
                                    //to put code for handling errors here
                    }
                    result = true;
		}	   //--------------------------- End of Loop for each data source

		try{
                    conPostgre.commit();
                    psmt.close();
                    //postgresConnection.releasePostgresConnection(conPostgre);
                    //logger.info("Closing PostgreSQL Connection");
                    debug("Closing PostgreSQL Connection");
                    //instance.releaseLDAPConnection(conLDAP);
                    //logger.info("Closing LDAP Connection");
                    debug("Closing LDAP Connection");
		}
		catch (SQLException sqle){
                    //logger.warning("SQL exception while closing database");
                    System.out.println("Exception details:"  + sqle);
                    //logger.warning( "Exception details:"  + sqle );
                    result = false;
		}
		catch(Exception e) {
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

	private String getCreatorID (String searchKey, LDAPConnection conLDAP ) throws netscape.ldap.LDAPException{
		LDAPSearchResults result = conLDAP.search(searchKey, LDAPv2.SCOPE_SUB, "mmlCid=*" ,(new String[]{"mmlCid"}), false);

        LDAPEntry entry =null;

		if(result.hasMoreElements()){
			entry =	result.next();
		}

		Enumeration enumVals;
		String creatorID = null;

		LDAPAttribute attr = entry.getAttribute("mmlCid");
		if (attr != null) {
			enumVals = attr.getStringValues();
			if ( (enumVals != null) && enumVals.hasMoreElements() ) {
				creatorID = (String)enumVals.nextElement();
			}
		}
		return  creatorID;
	}

	String getDepartment(String searchKey, LDAPConnection conLDAP ) throws netscape.ldap.LDAPException{

	  LDAPSearchResults result = conLDAP.search(searchKey, LDAPv2.SCOPE_SUB,"uid=*" ,(new String[]{"uid","departmentId"}), false);

	   LDAPEntry entry =null;
	   String department = null;

	   	while(result.hasMoreElements()){
			entry = result.next();
	    	Enumeration enumVals;
	    	LDAPAttribute attr = entry.getAttribute("uid");
	     	if (attr != null) {
	     		enumVals = attr.getStringValues();
	      		if ( (enumVals != null) && enumVals.hasMoreElements() ) {
	       			//if(!((String)enumVals.nextElement()).equals("lasmanager")){
                                if(((String)enumVals.nextElement()).equals("lasmanager")){
	        			Enumeration enumVals1;
	        			LDAPAttribute attr1 = entry.getAttribute("departmentId");
		         		if (attr1 != null) {
	         				enumVals1 = attr1.getStringValues();
	          				if ( (enumVals1 != null) && enumVals1.hasMoreElements() ) {
	           					department = (String)enumVals1.nextElement();
	           					return  department;
	          				}
	         			}
	       			}
	     		}
	   		}
	  	}
	    return  department;
	}
        
        private void debug(String msg) {
            System.out.println(msg);
        }

	/**
	 *
	 * main(), DataConversion entry point<br>
	 *   	Reads INI file for DB conenction parameter, etc <br>
	 *     Opens log handler for logging messages<br>*
	 *     Gets LDAP and postgres conenction<br>
	 * 	Calls DataConversion.doDataTransfer()<br>
	 *
	 */
	public static void main(String args[]) {
		//Property Table to store Postgres various paramater
		postgresConenctionParameter=null;

		//Getting PostgreSQL Connection parameter
		postgresConenctionParameter=null;

		iniFileRead = new mirrorI.dolphin.server.IniFileRead();

		postgresConenctionParameter = iniFileRead.readIniFile();
		if ( postgresConenctionParameter == null){
			  //logger.warning("Could not get INI file" );
                          System.out.println("Could not get INI file" );
			  System.exit(1);
		}
		//set IniFileRead object as null
		iniFileRead = null;

		// Opening log handler
		if (postgresConenctionParameter != null && postgresConenctionParameter.size() > 0 &&
		    														  postgresConenctionParameter.containsKey("DataConversion") &&
		    														  postgresConenctionParameter.getProperty("DataConversion") !=null ) {

			logger = Logger.getLogger(postgresConenctionParameter.getProperty("DataConversion"));
			//Copy to LoggerLocation for PostgresConnection Object
			postgresConenctionParameter.put("LoggerLocation",postgresConenctionParameter.getProperty("DataConversion"));
		}
		//To aviod run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
			postgresConenctionParameter.put("LoggerLocation","Dummy");
		}
		try {
			logger.addHandler(new FileHandler());
		}
		catch(IOException ioe) {
			logger.warning("File handler could not be found");
			logger.warning( "Exception details:"  + ioe );
		}
		catch(Exception e) {
			System.out.println("Exception while opening logger handler");
			System.out.println( "Exception details:"  + e );
		}

		//logger.finer("Method Entry");

		//Getting LDAP Connection
        ConnectingToLDAP instance  = ConnectingToLDAP.getInstance();

        LDAPConnection conLDAP    = instance.acquireLDAPConnection();

        if(conLDAP == null) {
			//logger.warning("Could not connect to LDAP");
			System.out.println("Could not connect to LDAP");
			System.exit(1);
		}

		DataConversion dataConv = new DataConversion();

		//Getting PostgreSQL Connection
		conPostgre  =  postgresConnection.acquirePostgresConnection();

		if (conPostgre == null) {
			//logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			System.out.println("Error in connecting to DB, please check INI file paramater and DB status");
			System.exit(1);
		}

		//boolean result = dataConv.doDataTransfer(dataConv, instance,  conLDAP);
                boolean result = true;

                if(result){
			//logger.info("Data Transfer Completed");
			System.out.println("Data Transfer Completed");
		}
		else{
			//logger.warning("Error in Transfer of Data");
			System.out.println("Error in Transfer of Data");
		}
		//logger.finer("Method Exit");
                
                // Master Flag
                result = dataConv.doMasterFlag(dataConv, instance,  conLDAP);

                if(result){
			//logger.info("Data Transfer Completed");
			System.out.println("Master flag Completed");
		}
		else{
			//logger.warning("Error in Transfer of Data");
			System.out.println("Error in Flag of Master");
		}
                
                try {
                    postgresConnection.releasePostgresConnection(conPostgre);
                    instance.releaseLDAPConnection(conLDAP);
                }
                catch (Exception le) {
                    
                }
	}   // end of main method
}