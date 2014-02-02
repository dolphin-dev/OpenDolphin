/*
 * MasterUpdateLocalChnagedData.java
 *
 * Created on 2003/02/05
 *
 * Last updated on 2003/03/06
 *
 * Revised on 2003/03/12 to skip updating 'FreqFlag' while updating master table.
 * Revised on 2003/03/25 'localDir' existence check and create code is added in handleUpdatedFiles().
 *
 */

package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.sql.*;

/**
 *
 * This class takes care of the information to be sent to remote system <br>
 * and information received from remote system. Information can be data or files<br>
 *
 * @author Prashanth Kumar, Mirror-i Corp.
 *
 */
public class MasterUpdateLocalChangedData{

    private static final int TABLE_NAME    				= 0;
    private static final int REC_REF_NEW					= 1;
   	private static final int CHG_TYPE				 	    = 2;
   	private static final int REC_REF_OLD			 	    = 3;
   	private static final int TABLE_HEADER		 	    = 4;

	private static final int FILE_PATH	    				= 0;
 	private static final int FILE_NAME    					= 1;
   	private static final int FILE_LENGTH			 	    = 3;
   	//For update/insert record
   	private static final int DB_OPE_FIRST_TIME 	    = 0;
   	private static final int DB_OPE_NOT_FIRST_TIME = 1;

	// To store labo test parameter
	private Properties masterUpdateParameter=null;

	//To store logging messages
	private Logger logger=null;

	//Postgres database connection
	private Connection conPostgres = null;

	//Postgres database connection object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;

	//To get Master table's information
	MasterUpdateDataMapping dataMapping = null;
	String primaryKey = null;

    /** Creates new MasterUpdateLocalChnagedData */
    public MasterUpdateLocalChangedData(Properties masterUpdateParameter, Logger logger) {
		//copy master Update Parameter to local property variable
		this.masterUpdateParameter=masterUpdateParameter;
		//copy main logger to local logger
		this.logger = logger;
		//Create new postgresConneciton object for database object
		postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(masterUpdateParameter);
    }

	/**
	 *
	 * getlastUpdateTimeStamp(), retrieves and sends the local server last update time<br>
	 * from 'Local_Master_Update' table.
	 *
	 * This method is called from MasterUpdateLocal.getUpdate()<br>
	 *
 	 */
	public boolean getlastUpdateTimeStamp(java.sql.Timestamp lastUpdateTimeStamp) throws SQLException {
		logger.finer("Method Entry");
		boolean getlastUpdateTimeStampReturn = false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;

		//Get Postgres Connection
		conPostgres = postgresConnection.acquirePostgresConnection();

		if (conPostgres == null) {
			logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			getlastUpdateTimeStampReturn = false;
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("Select UpdatedTime from Tbl_Local_Master_Update order by UpdatedTime desc");

			//Convert into string
			sql = buf.toString();
			logger.finer("Sql Statement: " + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);
				// If last updated time's record is available
				if (rs.next()) {
					if (rs.getTimestamp("UpdatedTime") != null) {
						lastUpdateTimeStamp.setTime( rs.getTimestamp("UpdatedTime").getTime());
						getlastUpdateTimeStampReturn = true;
					}
				}
				//If no records also we need to set as true (local server might be requesting update for the first time)
				else{
					getlastUpdateTimeStampReturn = true;
				}
			}
			catch(SQLException sqle) {
				logger.warning("SQL Exception while getting date from Tbl_Local_Master_Update");
				logger.warning( "Exception details:"  + sqle );
				getlastUpdateTimeStampReturn = false;
			}
			catch(Exception e) {
				logger.warning("Exception while getting date from Tbl_Local_Master_Update");
				logger.warning( "Exception details:"  + e );
				getlastUpdateTimeStampReturn = false;
			}
			finally {
				if(rs !=null){
					rs.close();
				}
				if(st !=null){
					st.close();
				}
				if(conPostgres !=null){
					postgresConnection.releasePostgresConnection(conPostgres);
					conPostgres =null;
				}
			}
		}
		logger.finer("Method Exit");
		return getlastUpdateTimeStampReturn;
	}

	/**
	 *
	 * handleUpdatedRecords(), takes care of data received from remote server<br>
	 *
	 * This method is called from MasterUpdateLocal.getUpdate()<br>
	 *
 	 */
	public boolean handleUpdatedRecords(Vector collectionOfChangedRecords) {
		logger.finer("Method Entry");
		boolean handleUpdRecRet = false;
		//If received object is not null and  has some data
		if( (collectionOfChangedRecords != null) && (collectionOfChangedRecords.size()> 0)){
			try {
				//Get Postgres connection
				conPostgres=postgresConnection.acquirePostgresConnection();
				if (conPostgres == null) {
					logger.warning("Could not connect to Postgres database");
					handleUpdRecRet = false;
				}
				if(conPostgres != null){
					//To manuplate DB in one transaction
					conPostgres.setAutoCommit(false);
					//Synchronize DB
					handleUpdRecRet=synchronizeLocalDB(conPostgres,collectionOfChangedRecords);

					//Commit transaction
					if(handleUpdRecRet){
						conPostgres.commit();
						handleUpdRecRet =true;
					}
					//Rollback transaction
					else{
						conPostgres.rollback();
						logger.warning("Error In DB operation, rollback transaction");
						handleUpdRecRet =false;
					}
				}
			}
			catch (SQLException sqle) {
				logger.warning("SQL Exception while calling handleUpdatedRecords()");
				logger.warning("Exception details:"  + sqle );
				handleUpdRecRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling handleUpdatedRecords()");
				logger.warning("Exception details:"  + e );
				handleUpdRecRet = false;
			}
			finally {
				//Disconnect DB
				if(conPostgres != null){
					postgresConnection.releasePostgresConnection(conPostgres);
					conPostgres = null;
				}
				//Clear Buffer
				if(collectionOfChangedRecords !=null){
					collectionOfChangedRecords.clear();
					collectionOfChangedRecords=null;
				}
			}
		}
		logger.finer("Method Exit");
		return handleUpdRecRet;
	}

	/**
	 *
	 * synchronizeLocalDB(), analyaze the received data from remote system<br>
	 * and calls appropriate method to insert/update/delete local server database<br>
	 *
	 * This method is called from handleUpdatedRecords()<br>
	 *
 	 */
	private boolean synchronizeLocalDB(Connection conPostgres, Vector collectionOfChangedRecords)  throws SQLException {
		logger.finer("Method Entry");

		boolean synchLocDBRet = false;
		String[] updatedRecSet = null;
		String[] dbStructure=null;
		int dbOperRetry=0;

		if( conPostgres != null){
			try {
				for(int i=0;  i<collectionOfChangedRecords.size(); i++){
					updatedRecSet = null;
					updatedRecSet =  (String[])collectionOfChangedRecords.elementAt(i);
					//Check for minimum header length
					if( (updatedRecSet != null) && (updatedRecSet.length>2) ){
						//Get Corresponding DB table structure based on the recived table name
						dbStructure = getDbStructure(updatedRecSet[TABLE_NAME]);
						if(dbStructure != null) {
							//To call update method on while inserting and visa versa
							dbOperRetry=DB_OPE_FIRST_TIME;
							//call Insert Operation
							if( updatedRecSet[CHG_TYPE].equalsIgnoreCase("DI") ){
								//Though it is insert statement, pass primary key info also, as this metod would call updateRecord in case of
								//error while inserting.
								synchLocDBRet = insertRecord(conPostgres,updatedRecSet,dbStructure,primaryKey,dbOperRetry);
								if(!synchLocDBRet){
									synchLocDBRet=false;
									logger.warning("Error In inserting record, rollback transaction");
									break;
								}
							}
							//call Update Operation
							else if(updatedRecSet[CHG_TYPE].equalsIgnoreCase("DU") ){
								synchLocDBRet = updateRecord(conPostgres,updatedRecSet,dbStructure,primaryKey,dbOperRetry);
								if(!synchLocDBRet){
									synchLocDBRet=false;
									logger.warning("Error In updating record, rollback transaction");
									break;
								}
							}
							//call Delete Operation
							else if(updatedRecSet[CHG_TYPE].equalsIgnoreCase("DD") ){
								synchLocDBRet = deleteRecord(conPostgres,updatedRecSet,primaryKey);
								if(!synchLocDBRet){
									synchLocDBRet=false;
									logger.warning("Error In deleting record, rollback transaction");
									break;
								}
							}
							else{
								logger.warning("Not a valid Database operation type(Not update,insert,delete): " +updatedRecSet[CHG_TYPE]);
								continue;
							}
						}
						else{
							logger.warning("Not a valid Master Database: " +updatedRecSet[TABLE_NAME]);
							continue;
						}
					}//array not null
					else{
						logger.warning("Received updated info string array is null or not having minimum length");
						continue;
					}
				}//for looop
			}//try
			catch (SQLException sqle) {
				logger.warning("SQL Exception while calling handleUpdatedRecords()");
				logger.warning("Exception details:"  + sqle );
				synchLocDBRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling handleUpdatedRecords()");
				logger.warning("Exception details:"  + e );
				synchLocDBRet = false;
			}
			finally{
			}
		}
		else {
			logger.warning("Either DB Connection and/or collectionOfChangedRecords is null");
			synchLocDBRet=false;
		}
		logger.finer("Method Exit");
		return synchLocDBRet;
	}

	/**
	 *
	 * getDbStructure(), returns the required master table db structure array<br>
	 *
	 * This method is called from synchronizeLocalDB()<br>
	 *
 	 */
	private String[] getDbStructure(String requiredMasterTableName){
		logger.finer("Method Entry");

		String [] dbStructure =null;
		int masterTableIndex=0;
		boolean masterTableFound = false;
		primaryKey = null;

		try {
			for (masterTableIndex =0; masterTableIndex < dataMapping.masterTable.length; masterTableIndex++){
				if(requiredMasterTableName.equalsIgnoreCase(dataMapping.masterTable[masterTableIndex])) {
					masterTableFound = true;
					//Get Primary key for this table
					primaryKey = dataMapping.primaryKey[masterTableIndex];
					break;
				}
			}

			if(masterTableFound){
				dbStructure = dataMapping.getDbStructure(masterTableIndex);
				if(dbStructure == null){
					masterTableFound = false;
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException aiobe) {
			logger.warning("Array Index out of bounds exception while calling getDbStructure()");
			logger.warning("Exception details:"  + aiobe );
			masterTableFound = false;
			dbStructure = null;
		}
		catch(Exception e) {
			logger.warning("Exception while calling getDbStructure()");
			logger.warning("Exception details:"  + e );
			masterTableFound = false;
			dbStructure = null;
		}
		logger.finer("Method Exit");
		return dbStructure;
	}

	/**
	 *
	 * insertRecord(), takes care of inserting data received from remote server<br>
	 *
	 * This method is called from synchronizeLocalDB() and updateRecord()<br>
	 *
	 * Note: On error while inserting (if already this record exist), then this method would call updateRecord()<br>
	 *
 	 */
	private boolean insertRecord(Connection conPostgres,String[] updatedRecSet, String[] dbStructure,String primaryKey,
											  int dbOperRetry) throws SQLException {
		logger.finer("Method Entry");
		boolean insertRecordRet=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		int i=0;
		boolean recordAlreadyExist = false;
		int dbOperUpdRetry	=0;
		String primaryKeyConditionStmt=null;

		if( conPostgres != null && primaryKey !=null){
			try {
				buf = new StringBuffer();
				//Before inserting, just check whether this record already exist. If so then try to update, else insert
				//This checking is done only first time ( i.e. when this method not called from updateRecord() )
				if(dbOperRetry == DB_OPE_FIRST_TIME) {
					buf.append("select count(*) as numberOfRecord from "+ updatedRecSet[TABLE_NAME] + " where ");
					//check primary key for any multiple condition
					logger.finer("Get where condition Column Name: "+primaryKey +" Value: "+updatedRecSet[REC_REF_NEW]);
					primaryKeyConditionStmt=dataMapping.getPrimaryKeyConditionStmt(primaryKey,updatedRecSet[REC_REF_NEW]);
					//On successfully getting condition statement, add into sql buffer
					if(primaryKeyConditionStmt != null) {
						buf.append(primaryKeyConditionStmt);
						insertRecordRet=true;
					}
					else{
						logger.warning("Error in constructing SQL statement, Primary key filed value does not match");
						insertRecordRet=false;
					}

					//if Sql Construction is successful
					if(insertRecordRet) {
						//Convert to string
						sql = buf.toString();
						logger.finer("SQL Statement: " + sql);
						//Create statement
						st = conPostgres.createStatement();
						rs = st.executeQuery(sql);
						//If already this record exist
						if (rs.next()) {
							//check for number of records
							if( (rs.getInt("numberOfRecord") > 0) ) {
								recordAlreadyExist=true;
								logger.warning("This record can not be inserted as this reocord already exist, shall try to update");
								//Need to update this record
								dbOperUpdRetry=DB_OPE_NOT_FIRST_TIME;
								insertRecordRet =updateRecord(conPostgres,updatedRecSet,dbStructure,primaryKey,dbOperUpdRetry);
							}
						}
					}
				}
				//If record does not exist or this metod is called from updateRecord()
				if( (dbOperRetry == DB_OPE_FIRST_TIME && recordAlreadyExist==false)  || (dbOperRetry ==DB_OPE_NOT_FIRST_TIME) ) {

					buf = new StringBuffer();
					st = null;
					buf.append("insert into "+ updatedRecSet[TABLE_NAME] + "( " );
					//Insert db field name
					for(i=0; i<(dbStructure.length-1); i++) {
						buf.append(dbStructure[i] + ", ");
					}
					buf.append(dbStructure[i]);
					buf.append(" ) values ( ");

					//Insert values
					for(i=TABLE_HEADER; i<(updatedRecSet.length-1); i++) {
						if ( (updatedRecSet[i] !=null)  ){
							buf.append(postgresConnection.addSingleQuote(updatedRecSet[i]) + ", ");
						}
						else{
							buf.append("NULL, ");
						}
					}
					if ( (updatedRecSet[i] !=null) ){
						buf.append(postgresConnection.addSingleQuote(updatedRecSet[i]));
					}
					else{
						buf.append("NULL");
					}
					buf.append(" )");

					//Convert to string
					sql = buf.toString();
					logger.finer("SQL Statement: " + sql);
					//Create statement
					st = conPostgres.createStatement();
					st.executeUpdate(sql);
					insertRecordRet = true;
				}
			}
			catch (SQLException sqle) {
				logger.warning("SQL Exception while calling insertRecord()");
				logger.warning("Exception details:"  + sqle );
				insertRecordRet = false;
			}
			catch(ArrayIndexOutOfBoundsException aiobe) {
				logger.warning("Array Index out of bounds exception while calling insertRecord()");
				logger.warning("Exception details:"  + aiobe );
				insertRecordRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling insertRecord()");
				logger.warning("Exception details:"  + e );
				insertRecordRet = false;
			}
			finally{
				if(rs!=null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
			}
		}
		else {
			logger.warning("Either DB Connection and/or primary key is null");
			insertRecordRet=false;
		}
		logger.finer("Method Exit");
		return insertRecordRet;
	}

	/**
	 *
	 * updateRecord(), takes care of updating data received from remote server<br>
	 *
	 * This method is called from synchronizeLocalDB() and insertRecord()<br>
	 *
	 * Note: On error while updating (if no record exist to update), then this method would call insertRecord()<br>
	 *
 	 */
	private boolean updateRecord(Connection conPostgres,String[] updatedRecSet, String[] dbStructure,String primaryKey,
	                                          int dbOperRetry) throws SQLException {
		logger.finer("Method Entry");
		boolean  updateRecordRet=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		int i=0;
		int j=0;
		int updatedRecCount=0;
		int dbOperInsertRetry	=0;
		String primaryKeyConditionStmt=null;

		if( conPostgres != null && primaryKey !=null){
			try {
				buf = new StringBuffer();
				buf.append("update "+ updatedRecSet[TABLE_NAME] +  " set" );
				//Insert db field name
				for(i=0,j=TABLE_HEADER; i<(dbStructure.length-1); i++,j++) {
					//Not to update 'FreqFlag', check for freq flag and skip setting if it is trueand
					if( !(dbStructure[i].equalsIgnoreCase("freqFlag")) ){
						buf.append(" "+dbStructure[i] + "=");
						if(updatedRecSet[j] != null) {
							buf.append("'" +updatedRecSet[j] + "'," );
						}
						else{
							buf.append("NULL," );
						}
					}
					//skip this index for both recordset array and dbStructure array
					else{
						continue;
					}
				}

				//Not to update 'FreqFlag', check for freq flag and skip setting if it is trueand
				if( !(dbStructure[i].equalsIgnoreCase("freqFlag")) ){
					if(updatedRecSet[j] != null) {
						buf.append(" "+dbStructure[i] + "='" + updatedRecSet[j] + "' where ");
					}
					else {
						buf.append(" "+dbStructure[i] + "=NULL  where ");
					}
				}
				else{
					String temp =buf.toString();
					//check for extra coma
					if(temp.endsWith(",") ){
						temp=temp.substring(0,(temp.length()-1) );
						buf=null;
						buf=new StringBuffer();
						buf.append(temp);
					}
					buf.append(" where ");
				}

				//check primary key for any multiple condition
				//if it is first time i.e. when this method is not called from insertRecord(), then make where condition
				//based on old reference
				if(dbOperRetry==DB_OPE_FIRST_TIME){
					logger.finer("Get where condition Column Name(First Time): "+primaryKey +" Value: "+updatedRecSet[REC_REF_OLD]);
					primaryKeyConditionStmt=dataMapping.getPrimaryKeyConditionStmt(primaryKey,updatedRecSet[REC_REF_OLD]);
				}
				//When this method is called from insertRecord(), then make where condition
				//based on new reference
				else if ( dbOperRetry==DB_OPE_NOT_FIRST_TIME){
					logger.finer("Get where condition Column Name(Not First Time): "+primaryKey +" Value: "+updatedRecSet[REC_REF_NEW]);
					primaryKeyConditionStmt=dataMapping.getPrimaryKeyConditionStmt(primaryKey,updatedRecSet[REC_REF_NEW]);
				}

				//On successfully getting condition statement, add into sql buffer
				if(primaryKeyConditionStmt != null) {
					buf.append(primaryKeyConditionStmt);
					updateRecordRet=true;
				}
				else{
					logger.warning("Error in constructing SQL statement, Primary key filed value does not match");
					updateRecordRet=false;
				}

				//if Sql Construction is successful
				if(updateRecordRet) {
					//Convert to string
					sql = buf.toString();
					logger.finer("SQL Statement: " + sql);
					//Create statement
					st = conPostgres.createStatement();
					updatedRecCount=st.executeUpdate(sql);
					//If update is successful
					if(updatedRecCount > 0){
						updateRecordRet = true;
					}
					//when no record to update (due to mis match in the local and remote server DB)
					//try to insert this record, if it is first time (not to allow insert/update looping)
					else if( (updatedRecCount == 0) && (dbOperRetry==DB_OPE_FIRST_TIME) ) {
						logger.warning("Could not update as there is no such records, shall try to insert this record");
						dbOperInsertRetry = DB_OPE_NOT_FIRST_TIME;
						//try to insert
						updateRecordRet = insertRecord(conPostgres,updatedRecSet,dbStructure,primaryKey,dbOperInsertRetry);
					}
				}
			}
			catch (SQLException e) {
				logger.warning("Exception while calling updateRecord()");
				logger.warning("Exception details:"  + e );
				updateRecordRet = false;
			}
			catch(ArrayIndexOutOfBoundsException aiobe) {
				logger.warning("Array Index out of bounds exception while calling updateRecord()");
				logger.warning("Exception details:"  + aiobe );
				updateRecordRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling updateRecord()");
				logger.warning("Exception details:"  + e );
				updateRecordRet = false;
			}
			finally{
				if(rs!=null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
			}
		}
		else {
			logger.warning("Either DB Connection and/or primary key is null");
			updateRecordRet=false;
		}
		logger.finer("Method Exit");
		return updateRecordRet;
	}

	/**
	 *
	 * deleteRecord(), takes care of deleting local server database records <br>
	 * based on the data received from remote server<br>
	 *
	 * This method is called from synchronizeLocalDB() and insertRecord()<br>
	 *
 	 */
	private boolean deleteRecord(Connection conPostgres,String[] updatedRecSet, String primaryKey)	throws SQLException {
		logger.finer("Method Entry");

		boolean deleteRecordRet=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		int i=0;
		int deleteRecCount=0;
		String primaryKeyConditionStmt=null;

		if( conPostgres != null && primaryKey !=null){
			try {
				buf = new StringBuffer();
				buf.append("delete from "+ updatedRecSet[TABLE_NAME] + " where ");
				//check primary key for any multiple condition
				logger.finer("Get where condition Column Name: "+primaryKey +" Value: "+updatedRecSet[REC_REF_NEW]);
				primaryKeyConditionStmt=dataMapping.getPrimaryKeyConditionStmt(primaryKey,updatedRecSet[REC_REF_NEW]);
				//On successfully getting condition statement, add into sql buffer
				if(primaryKeyConditionStmt != null) {
					buf.append(primaryKeyConditionStmt);
					deleteRecordRet=true;
				}
				else{
					logger.warning("Error in constructing SQL statement, Primary key filed value does not match");
					deleteRecordRet=false;
				}

				//if Sql Construction is successful
				if(deleteRecordRet) {
					//Convert to string
					sql = buf.toString();
					logger.finer("SQL Statement: " + sql);
					//Create statement
					st = conPostgres.createStatement();
					deleteRecCount=st.executeUpdate(sql);
					//Check whether such records were exist and deleted, else just log the error message
					if(deleteRecCount < 1){
						logger.warning("No such record exist for delete");
					}
					deleteRecordRet = true;
				}
			}
			catch (SQLException e) {
				logger.warning("Exception while calling deleteRecord()");
				logger.warning("Exception details:"  + e );
				deleteRecordRet = false;
			}
			catch(ArrayIndexOutOfBoundsException aiobe) {
				logger.warning("Array Index out of bounds exception while calling deleteRecord()");
				logger.warning("Exception details:"  + aiobe );
				deleteRecordRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling deleteRecord()");
				logger.warning("Exception details:"  + e );
				deleteRecordRet = false;
			}
			finally{
				if(rs!=null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
			}
		}
		else {
			logger.warning("Either DB Connection and/or primary key is null");
			deleteRecordRet=false;
		}
		logger.finer("Method Exit");
		return deleteRecordRet;
	}

	/**
	 *
	 * handleUpdatedFiles(), takes care of files received from remote server<br>
	 *
	 * This method is called from MasterUpdateLocal.getUpdate()<br>
	 *
 	 */
	public boolean handleUpdatedFiles(Vector collectionOfUpdatedFiles) throws IOException,SecurityException{
		logger.finer("Method Entry");

		boolean handleUpdFilesRet = false;
		String[] fileInfo = null;
		byte [] recdfileInbyte=null;
		File recdFile=null;
		File fileToDelete=null;
		FileOutputStream fos=null;
		int fileLength=0;
		String localErrorFileDir = null;
		String todayDate=null;
		String localErrDestDir = null;
		boolean fileNeedToDelete = false;
		boolean fileNeedToUpd = false;

		//process collectionOfUpdatedFiles till items available in vector
		for(int j=0;  j<collectionOfUpdatedFiles.size(); j++){
			//reset file to update/delete flags
			fileNeedToDelete=false;
			fileNeedToUpd = false;

			//Get file file info header array
			fileInfo = (String[])collectionOfUpdatedFiles.elementAt(j);
			//If the file need to be overwrite/copy, then get file also in the form of bytes
			if( (fileInfo != null)  && ( (fileInfo[CHG_TYPE].equalsIgnoreCase("FI")) || (fileInfo[CHG_TYPE].equalsIgnoreCase("FU")) ) ) {
				j++;
				recdfileInbyte = (byte[])collectionOfUpdatedFiles.elementAt(j);
				//set this file need to updated flag on
				fileNeedToUpd=true;
			}
			else if( (fileInfo != null)  && (fileInfo[CHG_TYPE].equalsIgnoreCase("FD")) ){
				//set this file should be deleted flag on
				fileNeedToDelete=true;
			}


			//Over writing or coping received files
			if( (fileNeedToUpd==true) && (fileInfo != null) && (recdfileInbyte != null) ){
				//Converts the byte[] into file and store it
				try {
					//check file separator for below path
					if(!fileInfo[FILE_PATH].endsWith(File.separator)) {
						fileInfo[FILE_PATH] = fileInfo[FILE_PATH] + File.separator;
					}
					//Checking for file separator in the below paths
					fileInfo[FILE_PATH] = fileInfo[FILE_PATH].replace('/',File.separatorChar);
					fileInfo[FILE_PATH] = fileInfo[FILE_PATH].replace('\\', File.separatorChar);

					//Check whether 'fileInfo[FILE_PATH] ' exist, if not then create
					File localDir = new File(fileInfo[FILE_PATH]);
					if (localDir.exists() == false || localDir.isDirectory() == false) {
						logger.warning("Target directory doesn't exist: " +fileInfo[FILE_PATH]);

						// Create new directory with specified name.
						if (localDir.mkdirs() == true) {
							logger.finer("Local directory was created successfuly: "+ fileInfo[FILE_PATH]);
						}
						else{
							logger.warning("Local directory could not be created: "+ fileInfo[FILE_PATH]);
						}
					}
					if (localDir.exists() == true) {
						//Create file with received path name and file name
						recdFile = new File(fileInfo[FILE_PATH] + fileInfo[FILE_NAME]);
						//Get received file length
						fileLength = new Integer(fileInfo[FILE_LENGTH]).intValue();
						//Open file output stream and write
						fos = new FileOutputStream(recdFile);

						logger.finer("File to recd is: "+ recdFile.getAbsolutePath() + " length: " +fileLength);
						logger.finer("Change type: "+fileInfo[CHG_TYPE] );

						fos.write(recdfileInbyte,0, fileLength);
						fos.close();
						handleUpdFilesRet=true;
					}
				}
				catch(IOException ioe) {
					//Error while writing into required path, write to 'LocalErrorFilesDir'
					logger.warning("Exception while calling handleUpdatedFiles()");
					logger.warning("Exception details:"  + ioe );
					logger.info("Program shall try to copy this file to  'LocalErrorFilesDir'");
					try {
						//get 'LocalErrorFilesDir' from INI file
						if( masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
							masterUpdateParameter.containsKey("LocalErrorFilesDir")) {
							localErrorFileDir=masterUpdateParameter.getProperty("LocalErrorFilesDir");
							if(localErrorFileDir !=null ){
								//check file separator for below path
								if(!localErrorFileDir.endsWith(File.separator)) {
									localErrorFileDir = localErrorFileDir + File.separator;
								}
								//Checking for file separator in the below paths
								localErrorFileDir = localErrorFileDir.replace('/',File.separatorChar);
								localErrorFileDir = localErrorFileDir.replace('\\', File.separatorChar);

								//Creating dir based on date under 'LocalErrorFilesDir' folde.
								java.util.Date dateMills = new java.util.Date(System.currentTimeMillis());
								java.sql.Date  date = new java.sql.Date(dateMills.getTime());
								todayDate = date.toString();

								//check destination directory exist and readable
								localErrDestDir = localErrorFileDir + todayDate+fileInfo[FILE_PATH];
								File destDir = new File(localErrDestDir);
								//If it is not exist, then create it
								if (destDir.exists() == false) {
									logger.finer("local Err files Dir doesn't exist, creating: "+ localErrDestDir);
									if (destDir.mkdirs() == true) {
										if (destDir.isDirectory() != true || destDir.canRead() != true) {
											logger.warning("Error in reading local Err files Dir: "+ localErrDestDir);
										}
									}
									else{
										logger.warning("could not create local Err files Dir: " +localErrDestDir );
										handleUpdFilesRet=false;
									}
								}
								if( (destDir.exists() == true) && (destDir.isDirectory() == true) && (destDir.canRead() == true) ){
									//recdFile = new File(localErrorFileDir + fileInfo[FILE_PATH] +fileInfo[FILE_NAME]);
									recdFile = new File(localErrDestDir+File.separatorChar+fileInfo[FILE_NAME]);
									fos = new FileOutputStream(recdFile);
									fos.write(recdfileInbyte,0, fileLength);
									fos.close();
									handleUpdFilesRet=true;
								}
							}
							else{
								logger.warning("Error in getting value for 'LocalErrorFilesDir' from INI file");
								handleUpdFilesRet=false;
							}
						}
						else{
							logger.warning("Error in reading master update property (INI file) or property might not have key 'LocalErrorFilesDir' ");
							handleUpdFilesRet=false;
						}
					}
					catch(IOException ioe2) {
						logger.warning("Exception while calling handleUpdatedFiles(), Error in coping received file into error directory.");
						logger.warning("Exception details:"  + ioe2 );
						handleUpdFilesRet=false;
					}
				}
				finally {
					if( recdFile!=null) {
						recdFile=null;
					}
					if(fos != null) {
						fos.close();
						fos=null;
					}
				}
			}


			//Deleting file in local server
			if( (fileInfo != null) && (fileNeedToDelete == true) ) {
				try {
					//check file separator for below path
					if(!fileInfo[FILE_PATH].endsWith(File.separator)) {
						fileInfo[FILE_PATH] = fileInfo[FILE_PATH] + File.separator;
					}
					fileToDelete = new File(fileInfo[FILE_PATH] + fileInfo[FILE_NAME]);
					//check whether this file exist in local server and can be deleted, if so then try to delete
					if( (fileToDelete.exists() ==true) && (fileToDelete.isFile() == true) && (fileToDelete.canWrite() == true) ){
						handleUpdFilesRet = fileToDelete.delete();
						if(handleUpdFilesRet) {
							logger.finer("File deleted successfully : " + fileToDelete.getAbsolutePath() );
							handleUpdFilesRet=true;
						}
						else{
							logger.warning("Error in deleting File : " + fileToDelete.getAbsolutePath() );
							handleUpdFilesRet=false;
						}
					}
					else if( (fileToDelete.exists() ==true) && (fileToDelete.isFile() == true) &&(fileToDelete.canWrite() == false) ){
						logger.warning("Error in deleting File, no permisssion to delete : " + fileToDelete.getAbsolutePath() );
						handleUpdFilesRet=false;
					}
					else {
						logger.warning("File does not exist or not a valid file to delete: " + fileToDelete.getAbsolutePath() );
						handleUpdFilesRet=true;
					}
				}
				catch(Exception ioe3) {
					logger.warning("Exception while calling handleUpdatedFiles(), Error in deleting files");
					logger.warning("Exception details:"  + ioe3 );
					handleUpdFilesRet=false;
				}
				finally {
					if (fileToDelete!=null) {
						fileToDelete=null;
					}
				}
			}

			//Reset buffer
			try {
				if ( fileInfo != null ) {
					fileInfo = null;
				}
				if( recdfileInbyte != null ){
					recdfileInbyte = null;
				}
			}
			catch (Exception e) {
				logger.warning("Exception while calling handleUpdatedFiles(), Error in closing buffer");
				logger.warning("Exception details:"  + e );
			}
			//check for any error in above (update/copy/delete) operation, if so then get out of for loop
			if(!handleUpdFilesRet) {
				logger.warning("Error occurred  while processing received files, processing further will be aborted");
				break;
			}

		}//end of for loop
		//clear vector
		if(collectionOfUpdatedFiles !=null){
			collectionOfUpdatedFiles.clear();
			collectionOfUpdatedFiles=null;
		}
		logger.finer("Method Exit");
		return handleUpdFilesRet;
	}

	/**
	 *
	 * storeUpdatedTime(), receives local server update requested time and append in 'local_master_update' <br>
	 *
	 * This method is called from MasterUpdateLocal.getUpdate()<br>
	 *
 	 */
	public boolean storeUpdatedTime(String updateReqTime) throws SQLException{
		logger.finer("Method Entry");

		boolean  stoteUpdatedTimeRet=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;

		//Get Postgres Connection
		conPostgres = postgresConnection.acquirePostgresConnection();

		if (conPostgres == null) {
			logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			stoteUpdatedTimeRet = false;;
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null ){
			try {
				buf = new StringBuffer();
				buf.append("insert into tbl_local_master_update (UpdatedTime) values " );
				//Updated Time
				buf.append("(" + postgresConnection.addSingleQuote(updateReqTime) + ")");
				//Convert to string
				sql = buf.toString();
				logger.finer("SQL Statement: " + sql);
				//Create statement
				st = conPostgres.createStatement();
				st.executeUpdate(sql);
				stoteUpdatedTimeRet = true;
			}
			catch (SQLException sqle) {
				logger.warning("SQL Exception while calling stoteUpdatedTime()");
				logger.warning("Exception details:"  + sqle );
				stoteUpdatedTimeRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling stoteUpdatedTime()");
				logger.warning("Exception details:"  + e );
				stoteUpdatedTimeRet = false;
			}
			finally{
				if(st != null){
					st.close();
				}
				if(conPostgres !=null){
					postgresConnection.releasePostgresConnection(conPostgres);
					conPostgres =null;
				}
			}
		}
		else {
			logger.warning("DB Connection is null");
			stoteUpdatedTimeRet=false;
		}
		logger.finer("Method Exit");
		return stoteUpdatedTimeRet;
	}

}//end of class