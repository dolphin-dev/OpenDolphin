/*
 * MasterUpdateRemoteChangedData.java
 *
 * Created on 2003/02/05
 *
 * Last updated on 2003/03/06
 *
 */

package mirrorI.dolphin.remote;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.sql.*;

/**
 *
 * This class takes care of the information to be sent to requested local server<br>
 * and keep a history of local server accessed. Information can be data or files<br>
 *
 * @author Prashanth Kumar, Mirror-i Corp.
 *
 */
public class MasterUpdateRemoteChangedData{

    private static final int DB_ACCESS_FAIL 			= 0x51;
    private static final int DB_ACCESS_OK	 			= 0x52;
    private static final int VALID_LOCAL_SERVER		= 0x53;

    //Used for sending data to local server
	private static final int TABLE_NAME    				= 0;
	private static final int REC_REF_NEW					= 1;
	private static final int CHG_TYPE				 	    = 2;
	private static final int REC_REF_OLD			 	    = 3;
	private static final int FILE_PATH	    				= 0;
	private static final int FILE_NAME    					= 1;
	private static final int FILE_LENGTH			 	    = 3;
	private static final int TABLE_HEADER		 	    = 4;

	//To store local server update history
   	private static final int DB_UPDATE_SUCCESS		= 0x25;
    private static final int DB_UPDATE_FAIL				= 0x26;
	private static final int FILE_UPDATE_SUCCESS	= 0x27;
    private static final int FILE_UPDATE_FAIL			= 0x28;

	// To store labo test parameter
	private Properties masterUpdateParameter=null;

	//To store logging messages
	private Logger logger=null;

	//Postgres database connection
	private Connection conPostgres = null;

	//Postgres database connection object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;
	//mirrorI.dolphin.dao.PostgresConnectionDummy postgresConnection=null;

	//to get data mapping details
	mirrorI.dolphin.server.MasterUpdateDataMapping dataMapping = null;

    /** Creates new MasterUpdateLocalChnagedData */
    public MasterUpdateRemoteChangedData(Properties masterUpdateParameter, Logger logger) {
		//copy master Update Parameter to local property variable
		this.masterUpdateParameter=masterUpdateParameter;
		//copy main logger to local logger
		this.logger = logger;
		//Create new postgresConneciton object for database object
		postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(masterUpdateParameter);
		//postgresConnection = new mirrorI.dolphin.dao.PostgresConnectionDummy(masterUpdateParameter);
    }

	/**
	 *
	 * checkLocalServerIdentity(), determines whether requested local server is<br>
	 * registered server or not by checking local server ID in  'LocalServerMaster'<br>
	 *
	 * This method is called from MasterUpdateRemote.Connection.run()<br>
	 *
 	 */
	public int checkLocalServerIdentity(String localServerIdentification)  throws SQLException {

		int checkLocalServerIdentityReturn=0;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;

		//Get Postgres Connection
		conPostgres = postgresConnection.acquirePostgresConnection();

		if (conPostgres == null) {
			logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			checkLocalServerIdentityReturn = DB_ACCESS_FAIL;
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null && localServerIdentification != null){
			buf = new StringBuffer();
			buf.append("Select LocalServerID from LocalServerMaster where LocalServerID =");
			buf.append(postgresConnection.addSingleQuote(localServerIdentification));

			//Convert into string
			sql = buf.toString();
			logger.finer("Sql Statement: " + sql);

			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);
				// If updated record exist
				if (rs.next()) {
					logger.info("Local Server ID exist in remote server: " + localServerIdentification);
					checkLocalServerIdentityReturn= DB_ACCESS_OK +VALID_LOCAL_SERVER;
				}
				//to send only DB access is OK, but not a valid local server
				if(checkLocalServerIdentityReturn != (DB_ACCESS_OK +VALID_LOCAL_SERVER) ){
					//checkLocalServerIdentityReturn=DB_ACCESS_OK;
                                    // No local server identification 2003-06-02
                                    checkLocalServerIdentityReturn= DB_ACCESS_OK +VALID_LOCAL_SERVER;
				}
			}
			catch(SQLException sqle) {
				logger.warning("SQL Exception while getting data from LocalServerMaster");
				logger.warning( "Exception details:"  + sqle );
				checkLocalServerIdentityReturn=DB_ACCESS_FAIL;
			}
			catch(Exception e) {
				logger.warning("Exception while getting data from LocalServerMaster");
				logger.warning( "Exception details:"  + e );
				checkLocalServerIdentityReturn=DB_ACCESS_FAIL;
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
		return checkLocalServerIdentityReturn;
	}

	/**
	 *
	 * getUpdateRec(), fetch the changed data from 'ChangeMaster' table and <br>
	 * stores in 'changeMaster' object <br>
	 *
	 * This method is called from MasterUpdateRemote.Connection.run()<br>
	 *
 	 */
	public boolean getUpdateRec(Vector collectionOfChangeMaster, String localServerLastUpdateTime) throws SQLException {
		boolean getUpdateRecReturn = false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;

		//Get Postgres Connection
		conPostgres = postgresConnection.acquirePostgresConnection();

		if (conPostgres == null) {
			logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			getUpdateRecReturn = false;
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("Select RecordLocation, RecordRefNew, RecordRefOld, ChangeType, type from ChangeMaster where ");
			buf.append("UpdatedTime >");
			buf.append(postgresConnection.addSingleQuote(localServerLastUpdateTime));

			//Convert into string
			sql = buf.toString();
			logger.finer("Sql Statement: " + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);
				// If updated record exist
				while (rs.next()) {
					//Create new collectionOfChangeMaster if it is not created
					MasterUpdateChangeMaster changeMaster = new MasterUpdateChangeMaster();

					if (rs.getString("RecordLocation") != null)
						changeMaster.setRecordLocation( rs.getString("RecordLocation"));

					if (rs.getString("RecordRefNew") != null)
						changeMaster.setRecordRefNew( rs.getString("RecordRefNew"));

					if (rs.getString("RecordRefOld") != null)
						changeMaster.setRecordRefOld( rs.getString("RecordRefOld"));

					if (rs.getString("ChangeType") != null)
						changeMaster.setChangetype( rs.getString("ChangeType"));

					if (rs.getString("type") != null)
						changeMaster.setType( rs.getString("type"));

					//If change master is not null then add into collectionOfChangeMaster
					if(changeMaster != null) {
						collectionOfChangeMaster.addElement(changeMaster);
					}
				}
				getUpdateRecReturn = true;
			}
			catch(SQLException sqle) {
				logger.warning("SQL Exception while getting data from ChangeMaster");
				logger.warning( "Exception details:"  + sqle );
				getUpdateRecReturn = false;
			}
			catch(Exception e) {
				logger.warning("Exception while getting data from ChangeMaster");
				logger.warning( "Exception details:"  + e );
				getUpdateRecReturn = false;
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
		return getUpdateRecReturn;
	}

	/**
	 *
	 * getChangedData(), fetch the required data for requested local server and <br>
	 * stores in 'collectionOfChangedRecords' object <br>
	 *
	 * This method is called from MasterUpdateRemote.Connection.run()<br>
	 *
 	 */
	public boolean getChangedData(Vector collectionOfChangeMaster, Vector collectionOfChangedRecords) throws SQLException{
		logger.finer("Method Entry");

		boolean getChangedDataReturn = false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;
		String[] databaseField = null;
		String[] updatedRecordSet= null;
		String[] chgDataHeader=null;
		boolean masterTableFound = false;
		boolean sqlConstructError = false;
		String primaryKey = null;
		int i=0;
		int j=0;
		int k=0;
		int masterTableIndex=0;
		String primaryKeyConditionStmt=null;
		boolean primaryKeyExist=false;

		//Get 'changeMaster' object from 'collectionOfChangeMaster'
		MasterUpdateChangeMaster changeMaster = new MasterUpdateChangeMaster();
		for(k=0; k<collectionOfChangeMaster.size(); k++) {
			changeMaster = (MasterUpdateChangeMaster)collectionOfChangeMaster.elementAt(k);
			//Get updated record from Server DB only when getType is 'D' (Database)and changeType is 'DI'/'DU' (Insert/Update)
			if( (changeMaster != null) && (changeMaster.getType() != null) && (changeMaster.getChangetype() !=null) &&
				(changeMaster.getRecordRefNew() !=null) && (changeMaster.getType().equalsIgnoreCase("D")) ) {

				//Initialize header array
				chgDataHeader = new String[TABLE_HEADER];
				chgDataHeader[TABLE_NAME] = changeMaster.getRecordLocation();
				chgDataHeader[REC_REF_NEW] = changeMaster.getRecordRefNew();
				chgDataHeader[CHG_TYPE] = changeMaster.getChangetype();
				//If change type is Update then, get old vlaue also
				if( changeMaster.getChangetype().equalsIgnoreCase("DU") ) {
					chgDataHeader[REC_REF_OLD] = changeMaster.getRecordRefOld();
				}
				else{
					chgDataHeader[REC_REF_OLD] = null;
				}

				if( (changeMaster.getChangetype().equalsIgnoreCase("DI")) || (changeMaster.getChangetype().equalsIgnoreCase("DU")) ) {

					//Get Postgres Connection
					if (conPostgres == null) {
						conPostgres = postgresConnection.acquirePostgresConnection();
					}
					if (conPostgres == null) {
						logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
						getChangedDataReturn = false;
					}
					//On getting postgres connection successfully, get values from server db
					if(conPostgres != null){
						if (changeMaster.getRecordLocation() != null) {
							masterTableFound = false;
							primaryKey = null;
							//Get Master table and match with 'changeMaster.getRecordLocation'
							for (masterTableIndex = 0; masterTableIndex<dataMapping.masterTable.length; masterTableIndex++){
								if(changeMaster.getRecordLocation().equalsIgnoreCase(dataMapping.masterTable[masterTableIndex])){
									//Get primary key for corresponding table
									primaryKey = dataMapping.primaryKey[masterTableIndex];
									masterTableFound = true;
									break;
								}
							}
							//get corresponding 'databaseField' info from 'dataMapping' object
							if(masterTableFound) {
								databaseField = dataMapping.getDbStructure(masterTableIndex);
								if(databaseField == null){
									masterTableFound = false;
									logger.warning("Error in getting master table DB structure");
								}
							}
						}

						//If changeMaster has valid master db name
						if(masterTableFound) {
							//Copy chgDataHeader info to updatedRecordSet
							updatedRecordSet = new String[databaseField.length + TABLE_HEADER];
							updatedRecordSet[TABLE_NAME] = chgDataHeader[TABLE_NAME];
							updatedRecordSet[REC_REF_NEW] = chgDataHeader[REC_REF_NEW];
							updatedRecordSet[CHG_TYPE] = chgDataHeader[CHG_TYPE];
							//If change type is Update then, copy old values from chgDataHeader to updatedRecordSet
							if( changeMaster.getChangetype().equalsIgnoreCase("DU") ) {
								updatedRecordSet[REC_REF_OLD] = chgDataHeader[REC_REF_OLD];
								//Check whether this table has primary key
								primaryKeyExist=dataMapping.primarKeyExist(updatedRecordSet[TABLE_NAME]);
							}
							else{
								updatedRecordSet[REC_REF_OLD] = null;
							}

							//For DI and DU with primary key table
							if ( (changeMaster.getChangetype().equalsIgnoreCase("DI")) ||
							   ( (changeMaster.getChangetype().equalsIgnoreCase("DU")) && (primaryKeyExist) )) {
								buf = new StringBuffer();
								buf.append("Select ");
								for (i = 0; i<(databaseField.length-1); i++) {
									buf.append(databaseField[i] + ", ");
								}
								buf.append(databaseField[i] );
								buf.append(" from " + updatedRecordSet[0] + " where ");

								//check primary key for any multiple condition
								primaryKeyConditionStmt=dataMapping.getPrimaryKeyConditionStmt(primaryKey,changeMaster.getRecordRefNew());

								//On successfully getting condition statement, add into sql buffer
								if(primaryKeyConditionStmt != null) {
									buf.append(primaryKeyConditionStmt);
									getChangedDataReturn=true;
								}
								else{
									logger.warning("Error in constructiong SQL statement, Primary key filed value does not match");
									getChangedDataReturn=false;
								}

								//if Sql Construction is successful
								if(getChangedDataReturn) {
									//Convert into string
									sql = buf.toString();
									logger.finer("Sql Statement: " + sql);

									try {
										st = conPostgres.createStatement();
										rs = st.executeQuery(sql);

										// If records exist
										if (rs.next()) {
											for(i=0,j=TABLE_HEADER; j<updatedRecordSet.length; i++,j++){
												if(rs.getString(databaseField[i]) != null){
													updatedRecordSet[j] = rs.getString(databaseField[i]);
												}
												else{
													updatedRecordSet[j] = null;
												}
											}
											//insert into vector if object is not null
											if(updatedRecordSet != null) {
												collectionOfChangedRecords.addElement(updatedRecordSet);
											}
										}
										//When Corresponding record doesn't exist
										else{
											logger.warning("Corresponding record doesn't exist for: " + sql);
										}
										getChangedDataReturn = true;
									}
									catch(SQLException sqle) {
										logger.warning("SQL Exception while getting data from: " + updatedRecordSet[0] );
										logger.warning( "Exception details:"  + sqle );
										getChangedDataReturn = false;
									}
									catch(Exception e) {
										logger.warning("Exception while getting data from: "+ updatedRecordSet[0] );
										logger.warning( "Exception details:"  + e );
										getChangedDataReturn = false;
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
								}//Sql Construction error checking
							}
							// DU without primary key
							else if ( (changeMaster.getChangetype().equalsIgnoreCase("DU") ) && (!primaryKeyExist)  ){
								//store new value into array (coma separated)
								int m=TABLE_HEADER;
								int hasmoreField=0;
								String newValue=chgDataHeader[REC_REF_NEW];

								hasmoreField=newValue.indexOf(",");
								if( hasmoreField < 1){
									updatedRecordSet[m]=newValue;
								}
								else if (hasmoreField > 0){
									while ( (hasmoreField >=0) ){
										if(newValue.substring(0,hasmoreField).equals("NULL") ){
											updatedRecordSet[m]=null;
										}
										else{
											updatedRecordSet[m]=newValue.substring(0,hasmoreField);
										}
										newValue=newValue.substring((hasmoreField+1),newValue.length());
										hasmoreField=newValue.indexOf(",");
										m++;
									}
									//get last filed
									if(newValue.equals("NULL") ){
										updatedRecordSet[m]=null;
									}
									else{
										updatedRecordSet[m]=newValue;
									}
								}
								//insert into vector if object is not null
								if(updatedRecordSet != null) {
									collectionOfChangedRecords.addElement(updatedRecordSet);
								}
								getChangedDataReturn = true;
							}
						}
						//If master database is unknown
						else{
							logger.warning("Not a valid Master Database: " +changeMaster.getRecordLocation());
						}
					}
				}
				//changeType is 'DD (Delete)
				else {
					logger.finer("This record need to be deleted in local server table: " + changeMaster.getRecordLocation()
																												  +" Ref: "+changeMaster.getRecordRefNew() );
					//Copy change Header Info into collectionOfChangedRecords vector
					//This header info should goto local server for deleting this particular record in local server
					if(chgDataHeader != null) {
						collectionOfChangedRecords.addElement(chgDataHeader);
					}
					getChangedDataReturn = true;
					continue;
				}
			}
			//If changeMaster Doesn't contain any DB updated records
			else {
				getChangedDataReturn = true;
			}
		}
		logger.finer("Method Exit");
		return getChangedDataReturn;
	}

	/**
	 *
	 * getChangedFiles(), get the required files for requested local server and <br>
	 * stores in 'collectionOfUpdatedFiles' object <br>
	 *
	 * This method is called from MasterUpdateRemote.Connection.run()<br>
	 *
 	 */
	public boolean getChangedFiles(Vector collectionOfChangeMaster,Vector collectionOfUpdatedFiles) throws IOException {

		logger.finer("Method Entry");
		boolean getChangedFilesReturn = false;
		MasterUpdateChangeMaster changeMaster = new MasterUpdateChangeMaster();

		//Get 'changeMaster' object from 'collectionOfChangeMaster'
		for(int k=0; k<collectionOfChangeMaster.size(); k++) {

			File fileToSend=null;
			String[] fileInfo = null;
			long lenFileToSend=0;
			BufferedInputStream bin=null;
			byte[] fileToSendBytes = null;

			//Get 'changeMaster' from 'collectionOfChangeMaster' vector
			changeMaster = (MasterUpdateChangeMaster)collectionOfChangeMaster.elementAt(k);
			//check for updated files
			if( (changeMaster != null) && (changeMaster.getType() != null) && (changeMaster.getChangetype() != null) &&
				(changeMaster.getType().equalsIgnoreCase("F")) ) {
				//handle files here
				try {
					fileInfo = new String[dataMapping.fileTransInfo.length];
					//File Path Name
					fileInfo[FILE_PATH]=changeMaster.getRecordLocation();
					//Check whether path name ends with file separator, if not add it
					if(!fileInfo[FILE_PATH].endsWith(File.separator)) {
						fileInfo[FILE_PATH] = fileInfo[FILE_PATH] + File.separator;
					}
					//File name
					fileInfo[FILE_NAME]=changeMaster.getRecordRefNew();
					//Change Type
					fileInfo[CHG_TYPE]=changeMaster.getChangetype();

					//If ChangeType is  'FI' or 'FU' i.e. file to be copied(newly) or updated, then only get updated file
					if( (changeMaster.getChangetype().equalsIgnoreCase("FI")) || (changeMaster.getChangetype().equalsIgnoreCase("FU")) ){
						fileToSend = new File(fileInfo[FILE_PATH] + fileInfo[FILE_NAME] );

						//check whether this file is exist and readable
						if (fileToSend.isFile() && fileToSend.canRead() ){
							lenFileToSend = fileToSend.length();
							fileInfo[FILE_LENGTH]=Long.toString(lenFileToSend);

							//get  file to be sent as byte[]
							logger.finer("File to send is: "+ fileToSend.getAbsolutePath() + " length: " +lenFileToSend);
							bin = new BufferedInputStream(new FileInputStream(fileToSend));
							fileToSendBytes =new byte[(int)lenFileToSend];
							bin.read(fileToSendBytes, 0, (int)lenFileToSend);

							//insert file info into vector (insert only after successfully getting sending file as byte[])
							collectionOfUpdatedFiles.addElement(fileInfo);
							//Insert File to be sent as bytes into vector
							collectionOfUpdatedFiles.addElement(fileToSendBytes);
							getChangedFilesReturn=true;
							logger.finer("File to send  is: "+ fileToSend.getAbsolutePath() + " length: " +lenFileToSend);
						}
						else{
							logger.warning("File to be sent to local server is either not exist or not a valid file or can not read: "+ fileToSend.getAbsolutePath());
							getChangedFilesReturn=false;
						}
					}
					//If ChangeType is  'FD' i.e. file to be deleted
					else if(changeMaster.getChangetype().equalsIgnoreCase("FD") ){
						//insert file info into vector
						collectionOfUpdatedFiles.addElement(fileInfo);
						getChangedFilesReturn=true;
						logger.finer("File to be deleted  is: "+fileInfo[FILE_PATH]+fileInfo[FILE_NAME]);
					}
				}
				catch (IOException ioe) {
					logger.warning("IO Exception while getting updated files from Remote server");
					logger.warning( "Exception details:"  + ioe );
					getChangedFilesReturn=false;
				}
				catch (Exception e) {
					logger.warning("Exception while getting updated files from Remote server");
					logger.warning( "Exception details:"  + e );
					getChangedFilesReturn=false;
				}
				finally {
					if (fileToSendBytes != null) {
						fileToSendBytes=null;
					}
					if (bin != null) {
						bin.close();
						bin= null;
					}
				}
			}
			//If changeMaster Doesn't contain any file updated records
			else{
				getChangedFilesReturn=true;
			}
		}
		logger.finer("Method Exit");
		return getChangedFilesReturn;
	}

	/**
	 *
	 * storeLocalServerUpdateHistory(), stores requested local server history in 'LocalServerHistory' table<br>
	 *
	 * This method is called from MasterUpdateRemote.Connection.run()<br>
	 *
	 * Appending pattern : Success/Fail/NO_UPDATE(0==No Update)
	 *
 	 */
	public boolean storeLocalServerUpdateHistory(int dbUpdStatus, int fileUpdStatus, String localServerIdentification) throws SQLException{
		logger.finer("Method Entry");
		boolean  storeLocSvrUpdHistRet=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;

		//Get Postgres Connection
		conPostgres = postgresConnection.acquirePostgresConnection();

		if (conPostgres == null) {
			logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
			storeLocSvrUpdHistRet = false;;
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null && localServerIdentification != null){
			try {
				buf = new StringBuffer();
				buf.append("insert into LocalServerHistory (LocalServerId,DbUpdateStatus,FileUpdateStatus,UpdateReqestTime) values (" );
				//Local Server Identification
				buf.append(postgresConnection.addSingleQuoteComa(localServerIdentification));
				//DB Update Status-Success
				if(dbUpdStatus == DB_UPDATE_SUCCESS){
					buf.append(postgresConnection.addSingleQuoteComa("SUCCESS"));
				}
				//DB Update Status-Fail
				else if(dbUpdStatus == DB_UPDATE_FAIL){
					buf.append(postgresConnection.addSingleQuoteComa("FAIL"));
				}
				//DB Update Status-No Updates
				else{
					buf.append(postgresConnection.addSingleQuoteComa("NO_UPDATE"));
				}
				//File Update Status-Success
				if(fileUpdStatus == FILE_UPDATE_SUCCESS){
					buf.append(postgresConnection.addSingleQuoteComa("SUCCESS"));
				}
				//File Update Status-Fail
				else if(fileUpdStatus == FILE_UPDATE_FAIL){
					buf.append(postgresConnection.addSingleQuoteComa("FAIL"));
				}
				//File Update Status-No Updates
				else{
					buf.append(postgresConnection.addSingleQuoteComa("NO_UPDATE"));
				}
				//Update Request Time
				buf.append("now() )");
				//Convert to string
				sql = buf.toString();
				logger.finer("SQL Statement: " + sql);
				//Create statement
				st = conPostgres.createStatement();
				st.executeUpdate(sql);
				storeLocSvrUpdHistRet = true;
				logger.info("Updated local server history updated successfully, Local server ID: "  + localServerIdentification);
				logger.info("Updated local server history updated successfully, DB update status: "  + dbUpdStatus
				                                                                                        + ", File update status: "+fileUpdStatus);
				logger.info("Update status code: 0x25:DB Update Success, 0x26:DB Update Fail, 0x27:File Update Success, 0x28:File Update fail, else no updates");
			}
			catch (SQLException sqle) {
				logger.warning("SQL Exception while calling storeLocalServerUpdateHistory()");
				logger.warning("Exception details:"  + sqle );
				storeLocSvrUpdHistRet = false;
			}
			catch (Exception e) {
				logger.warning("Exception while calling storeLocalServerUpdateHistory()");
				logger.warning("Exception details:"  + e );
				storeLocSvrUpdHistRet = false;
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
			logger.warning("Either DB Conenction and/or Local Server Identification is null");
			storeLocSvrUpdHistRet=false;
		}
		logger.finer("Method Exit");
		return storeLocSvrUpdHistRet;
	}

}//end of class

