/*
 * MasterUpdateLocal.java
 *
 * Created on 2003/01/29
 *
 * Last updated on 2003/03/06
 *
 * Revised on 2003/03/12 method 'storeUpdatedTime' renamed from 'stoteUpdatedTime'.
 * Revised on 2003/03/24 Getting 'localServerIdentification' parameter from LDAP (Previously: INI file).
 *
 */
package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;

/**
 *
 * This class takes care of getting latest information about master table and files from<br>
 * remote system<br>
 *
 * @author  Prashanth Kumar Mirror-I Corp
 *
 */
public class MasterUpdateLocal {

    private static final int AUTH_PASS                  = 0x21;
    private static final int AUTH_FAIL    		= 0x22;
    private static final int SVR_DB_ACCESS_FAIL         = 0x23;
    private static final int SVR_FILE_ACCESS_FAIL       = 0x24;
    private static final int DB_UPDATE_SUCCESS		= 0x25;
    private static final int DB_UPDATE_FAIL		= 0x26;
    private static final int FILE_UPDATE_SUCCESS	= 0x27;
    private static final int FILE_UPDATE_FAIL		= 0x28;
    private static final int UPDATE_REC_EXIST		= 0x31;
    private static final int UPDATE_REC_NOT_EXIST	= 0x32;
    private static final int UPDATE_FILE_EXIST	= 0x41;
    private static final int UPDATE_FILE_NOT_EXIST	= 0x42;
    private static final String DOLPHIN_USER		= "lasmanager";

    //To send appropriate message
    //error
    private static final int RET_FAIL								= -1;
    //No error and data updates available
    private static final int UPDATE_DATA_EXST_SUC		= 1;
    //No error and data updates not available
    private static final int UPDATE_DATA_NOT_EXST_SUC	= 3;
    //No error and file updates available
    private static final int UPDATE_FILE_EXST_SUC			= 5;
    //No error and file updates not available
    private static final int UPDATE_FILE_NOT_EXST_SUC	= 6;

    //Property Table to store Postgres various parameters
    private static Properties masterUpdateParameter;

    //Logger for logging messages
    private static Logger logger;

    private static IniFileRead iniFileRead;

    private MasterUpdateLocalChangedData changedData;
    private FacilityID facilityId;

    //Vector to store changeMaster objects
    private Vector collectionOfChangedRecords=null;

    //Vector to store changeMaster objects
    private Vector collectionOfUpdatedFiles=null;

    /** Creates new MasterUpdateLocal */
    public MasterUpdateLocal(Properties p, Logger l) {

        masterUpdateParameter = p;
        logger = l;
    }

    /**
     *
     * getUpdate(), sends request to get last updated time and sends request for latest<br>
     * data/files to remote server<br>
     *
     * Based on the response from remote server, local data/files are updated<br>
     *
     * This method is called from nain() (in case of direct execution) and <br>
     * ClientRequestHandler.Connection.run() (when this program is executed from client)<br>
     *
     */
	public int getUpdate() {
		logger.finer("Method Entry");
		boolean getUpdateReturn = false;
		int methodRet = 0;
		String localServerIdentification = null;
		String remoteServerHostId = null;
		int remoteServerPort = 0;
		Socket localSocket = null;
		ObjectOutputStream writer=null;
		ObjectInputStream reader = null;
		boolean storeUpdatedTimeRet = false;
		String updateReqTime=null;

		//Last Updated Time in local server
		java.sql.Timestamp lastUpdateTimeStamp= new java.sql.Timestamp(1);
		//Set defuault timestamp in case this program is running for the first time
		String defaultTime = "1900-01-01 00:00:00.00";
		lastUpdateTimeStamp = lastUpdateTimeStamp.valueOf(defaultTime);
		//Initialize 'MasterUpdateLocalChangedData' object
		changedData = new MasterUpdateLocalChangedData(masterUpdateParameter,logger);
		facilityId = new FacilityID(logger);

		//Get Local server Identification from LDAP, DolphinUser-FacilityId
		localServerIdentification=facilityId.getFacilityID(DOLPHIN_USER);
                //localServerIdentification = "JPN433010100001";
                //System.out.println("Facility ID = " + localServerIdentification);

		//Get Remote host IP Address
		if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
															 masterUpdateParameter.containsKey("RemoteHostIP") &&
															 masterUpdateParameter.getProperty("RemoteHostIP") !=null ) {

			remoteServerHostId = masterUpdateParameter.getProperty("RemoteHostIP");
		}
		//Get Remote host port for master update
		if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
															 masterUpdateParameter.containsKey("RemoteHostPort") &&
															 masterUpdateParameter.getProperty("RemoteHostPort") !=null ) {

			remoteServerPort = new Integer(masterUpdateParameter.getProperty("RemoteHostPort")).intValue();
		}

		//Check for any null value in above parameters
		if ( (localServerIdentification != null ) && (remoteServerHostId != null) && (remoteServerPort > 0)){
			try {
				//Get last updated time in local server
				getUpdateReturn=changedData.getlastUpdateTimeStamp(lastUpdateTimeStamp);
				if (getUpdateReturn){
					//Conenct to Server
					localSocket = new Socket(remoteServerHostId, remoteServerPort );

					if(localSocket.isConnected()){
						logger.info("connected to remote server");
						//Initialize output stream writer
						writer = new ObjectOutputStream(new BufferedOutputStream (localSocket.getOutputStream()));

						//Send local server Identification
						writer.writeObject(localServerIdentification);
						writer.flush();

						//Initialize input stream reader
						reader = new ObjectInputStream(new BufferedInputStream(localSocket.getInputStream()));
						// Read result (local server identification)
						int localServerCom = reader.read();
						if (localServerCom == AUTH_PASS) {
							logger.info("This local server is identified by remote server");

							//Send Lastupdated time to Remote server
							writer.writeObject(lastUpdateTimeStamp.toString());
							writer.flush();

							//Check whether updated record exist or not
							localServerCom = reader.read();

							if (localServerCom ==UPDATE_REC_EXIST) {
								logger.info("Updated Records exist for this local server");
								collectionOfChangedRecords = new Vector();
								collectionOfChangedRecords = (Vector)reader.readObject();
								if( (collectionOfChangedRecords != null) && (collectionOfChangedRecords.size()> 0)) {
									getUpdateReturn=changedData.handleUpdatedRecords(collectionOfChangedRecords);
									//Send DB update status to Remote system
									if(getUpdateReturn) {
										writer.write(DB_UPDATE_SUCCESS);
										logger.info("Updated local server DB successfuly");
										methodRet =UPDATE_DATA_EXST_SUC;
									}
									else{
										writer.write(DB_UPDATE_FAIL);
										logger.warning("Fail to update local server DB");
										methodRet =RET_FAIL;
									}
									writer.flush();
								}
							}
							else if (localServerCom ==UPDATE_REC_NOT_EXIST){
								logger.info("Updated Records does not exist for this local server");
								methodRet = UPDATE_DATA_NOT_EXST_SUC;
							}
							else if (localServerCom ==SVR_DB_ACCESS_FAIL){
								logger.warning("Error in accessing Server database");
								methodRet =RET_FAIL;
							}

							//Check whether updated files exist or not
							localServerCom = reader.read();
							if (localServerCom ==UPDATE_FILE_EXIST) {
								logger.info("Updated files exist for this local server");
								collectionOfUpdatedFiles = new Vector();
								collectionOfUpdatedFiles = (Vector)reader.readObject();
								if( (collectionOfUpdatedFiles != null) && (collectionOfUpdatedFiles.size()> 0)){
									getUpdateReturn=changedData.handleUpdatedFiles(collectionOfUpdatedFiles);
									//Send File update status to Remote system
									if(getUpdateReturn) {
										writer.write(FILE_UPDATE_SUCCESS);
										logger.info("Updated local server files successfuly");
										if( !(methodRet ==RET_FAIL) ) {
											methodRet += UPDATE_FILE_EXST_SUC;
										}
									}
									else{
										writer.write(FILE_UPDATE_FAIL);
										logger.warning("Fail to update local server Files");
										methodRet =RET_FAIL;
									}
									writer.flush();
								}
							}
							else if(localServerCom ==UPDATE_FILE_NOT_EXIST){
								logger.info("Updated files does not exist for this local server");
								if( !(methodRet ==RET_FAIL) ) {
									methodRet += UPDATE_FILE_NOT_EXST_SUC;
								}
							}
							else if(localServerCom ==SVR_FILE_ACCESS_FAIL){
								logger.warning("Error in accessing required Server Files");
								methodRet =RET_FAIL;
							}
							//Read Update requested time from server
							updateReqTime =  (String)reader.readObject();
						}
						//When Authentication fails
						else if (localServerCom == AUTH_FAIL){
							logger.warning("AUTH_FAIL, Check local server identification");
							methodRet=RET_FAIL;
						}
						else if(localServerCom == SVR_DB_ACCESS_FAIL){
							logger.warning("Error in accessing Server database");
							methodRet=RET_FAIL;
						}
					}//isConnected()!=true
					else {
						logger.warning("Could not establish conenction with remote server");
						methodRet=RET_FAIL;
					}
				}
				else {
					logger.warning("Error in getting local server last updated time");
					methodRet=RET_FAIL;
				}
				//Store Local updated time (This time is stored only on success of DB and File update)
				if( (methodRet == (UPDATE_DATA_EXST_SUC + UPDATE_FILE_EXST_SUC)) ||
				    (methodRet == (UPDATE_DATA_EXST_SUC + UPDATE_FILE_NOT_EXST_SUC)) ||
				    (methodRet == (UPDATE_DATA_NOT_EXST_SUC + UPDATE_FILE_EXST_SUC)) ||
				    (methodRet == (UPDATE_DATA_NOT_EXST_SUC + UPDATE_FILE_NOT_EXST_SUC)) ){

					storeUpdatedTimeRet=changedData.storeUpdatedTime(updateReqTime);

					if(!storeUpdatedTimeRet){
						logger.warning("Error in storing updated time");
					}
					else{
						logger.info("Local server database and/or files updated successfully");
					}

				}
			}
			catch (IOException ioe) {
				logger.warning("IOException while communicating  with remote server");
                logger.warning( "Exception details:"  + ioe );
                methodRet=RET_FAIL;
			}
			catch (Exception e) {
				logger.warning("Exception while communicating  with remote server");
                logger.warning( "Exception details:"  + e );
                methodRet=RET_FAIL;
			}
			finally {
                try {
                    if (reader != null) {
						reader.close();
					}
                    if (writer != null) {
						writer.close();
					}
                    if (localSocket != null) {
						localSocket.close();
					}
                }
                catch (IOException e2) {
					logger.warning("Exception while closing socket or writer or reader");
                    logger.warning( "Exception details:"  + e2 );
                }
			}
		}
		else{
			logger.warning("Got null value, Please check Local Server Identification, Remote Server Host Id and Remote Server Port");
			methodRet=RET_FAIL;
		}
        logger.finer("Method exit");
		return methodRet;
	}

	/**
	 *
	 * main(), MasterUpdateLocal entry point<br>
	 *   	Reads INI file for DB connection parameter, remote host ID etc <br>
	 *		Opens log handler for logging messages<br>
	 * 	Calls MasterUpdateLocal<br>
	 *
	 */
    public static void main(String args[]) {

		//To store Remote Server information like host ID, postgres connection etc etc
		int mainReturn=0;
		masterUpdateParameter=null;
		//Read INI file and store info in 'masterUpdateParameter'
		iniFileRead = new IniFileRead();
		masterUpdateParameter = iniFileRead.readIniFile();
		if ( masterUpdateParameter == null){
			  System.out.println("Could not get INI file " );
			  System.exit(1);
		}
		//set IniFileRead object as null
		iniFileRead = null;

		// Opening log handler
		if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
		    												 masterUpdateParameter.containsKey("MstUpdLoggerLocation") &&
		    												 masterUpdateParameter.getProperty("MstUpdLoggerLocation") !=null ) {

			logger = Logger.getLogger(masterUpdateParameter.getProperty("MstUpdLoggerLocation"));
			//Copy to LoggerLocation for PostgresConnection Object
			masterUpdateParameter.put("LoggerLocation",masterUpdateParameter.getProperty("MstUpdLoggerLocation"));
		}
		//To avoid run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
			masterUpdateParameter.put("LoggerLocation","Dummy");
		}

		try {
			logger.addHandler(new FileHandler());
		}
		catch(IOException ioe) {
			System.out.println("File handler could not be found");
			System.out.println( "Exception details:"  + ioe );
		}
		catch(Exception e) {
			System.out.println("Exception while opening logger handler");
			System.out.println( "Exception details:"  + e );
		}

		logger.finer("Method Entry");

        //Create Master Update Local
        MasterUpdateLocal masterUpdateLocal = new MasterUpdateLocal(masterUpdateParameter,logger);

        //Call masterUpdateLocal getUpdate() method to get latest data/file from remote server
		mainReturn=masterUpdateLocal.getUpdate();

		if( (mainReturn == RET_FAIL) ){
			logger.warning("Error in getUpdate() of MasterUpdateLocal");
			logger.warning("Abnormal Exit");
			System.exit(1);
		}
		logger.finer("Method Exit");
	}//Main end

} //Class End
