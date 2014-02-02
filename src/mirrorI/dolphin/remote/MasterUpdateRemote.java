/*
 * MasterUpdateRemote.java
 *
 * Created on 2003/01/15
 *
 * Last updated on 2003/03/06
 *
 */
package mirrorI.dolphin.remote;

import java.text.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import java.util.logging.*;

/**
 * MasterUpdateRemote() listen to Local Servers for Updation through socket<br>
 * <br>
 * Creates 'Connection' thread on getting  request from Local Servers<br>
 *
 * @author  Prashanth Kumar, Mirror-I
 *
 */
public class MasterUpdateRemote extends Thread {
    
    private static final int AUTH_PASS    	    = 0x21;
    private static final int AUTH_FAIL    	    = 0x22;
    private static final int SVR_DB_ACCESS_FAIL     = 0x23;
    private static final int SVR_FILE_ACCESS_FAIL   = 0x24;
    private static final int DB_UPDATE_SUCCESS	    = 0x25;
    private static final int DB_UPDATE_FAIL	    = 0x26;
    private static final int FILE_UPDATE_SUCCESS    = 0x27;
    private static final int FILE_UPDATE_FAIL	    = 0x28;

    private static final int UPDATE_REC_EXIST	    = 0x31;
    private static final int UPDATE_REC_NOT_EXIST   = 0x32;
    private static final int UPDATE_FILE_EXIST	    = 0x41;
    private static final int UPDATE_FILE_NOT_EXIST  = 0x42;
	//To identify local server
    private static final int DB_ACCESS_FAIL 	    = 0x51;
    private static final int DB_ACCESS_OK	    = 0x52;
    private static final int VALID_LOCAL_SERVER	    = 0x53;


    private static final int DEFAULT_PORT   = 6001;

    private int port = DEFAULT_PORT;

    private ServerSocket listenSocket;

    //Properties to store master update various parameters
    private static Properties masterUpdateParameter;

    //Logger for logging messages
    public static Logger logger;

    //INI file read object
    private  static mirrorI.dolphin.server.IniFileRead iniFileRead;

    /** Creates new MasterUpdateRemote */
    public MasterUpdateRemote() throws java.io.IOException {
        super();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * Creates new ServerSocket and calls run() of this thread<br>
     *
     * startServer(), This method is called from main()<br>
     *
     */
    public boolean startServer() throws IOException {
        listenSocket = new ServerSocket(port);
        this.start();
        logger.info("Remote Server is listening on port: " + port);
        return true;
    }

    /**
     *
     * Listens to Local Servers and creates new 'Connection' thread on request<br>
     *
     * run(), This method is called from startServer()<br>
     *
     */
    public void run() {
        
        try {
            while (true) {
               Socket clientSocket = listenSocket.accept();
                if(clientSocket.isConnected()){
                    Connection c = new Connection(clientSocket);		
                }
            }
        } catch (IOException e) {
            logger.warning("Exception while listening for connections");
            logger.warning( "Exception details:"  + e );
        }
    }

    /**
      * 'Connection' thread<br>
      *
      * 'Connection' thread created from 'MasterUpdateRemote' thread<br>
      * Fetches data and send to Local Server
      *<br>
      *
      */
    protected final class Connection extends Thread {
        
        private Socket client;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;

	//Vector to store changeMaster objects
	private Vector collectionOfChangeMaster;

	//Vector to store changeMaster objects
	private Vector collectionOfChangedRecords;

	//Vector to store file and file info
	private Vector collectionOfUpdatedFiles;

	//Local server Details
	private String localServerLastUpdateTime;
	private String localServerIdentification;

	private MasterUpdateRemoteChangedData changedData;


        public Connection(Socket clientSocket) {
            
            super();

            client = clientSocket;

            String addr = client.getInetAddress().getHostAddress();
            String time = DateFormat.getDateTimeInstance().format(new java.util.Date());
            logger.info("Connected from " + addr + " at " + time);

            try {
                reader = new ObjectInputStream(new BufferedInputStream (client.getInputStream()));
                writer = new ObjectOutputStream(new BufferedOutputStream (client.getOutputStream()));
                this.start();
            
            } catch (IOException e) {
                logger.warning("Exception while getting socket streams");
                logger.warning("Exception details:"  + e );
                try {
                    if ((reader != null) && (!client.isClosed()) ) {
                        reader.close();
                    }
                    if ( (writer != null) && (!client.isClosed())  ) {
			writer.close();
                    }
                    if (client != null) {
			client.close();
                    }
                
                } catch (IOException e2) {
                    logger.warning("Exception while closing socket or writer or reader on exception");
                    logger.warning( "Exception details:"  + e2 );
                }
            }
        }

        /**
         *
         * Accepts request from local server and fetch required data and sends to local server<br>
         *
         * This method is called from Connection thread<br>
         *
         */
        public void run() {
            boolean getUpdateReturn = false;
            boolean getUpdateDataReturn = false;
            boolean getUpdateFileReturn = false;
            int readDBUpdStatus = 0;
            int readFileUpdStatus = 0;
            String updateReqTime = null;
            int checkLocalServerIdentityReturn = 0;
            //Initialize 'MasterUpdateRemoteChangedData'
            changedData = new MasterUpdateRemoteChangedData(masterUpdateParameter,logger);
            try {
				
                //Get local server identification from local server.		
                localServerIdentification = (String) reader.readObject();
                logger.info("Local Server identification: "+ localServerIdentification);

                //Is it a valid local server(check this local server id with existing 'LocalServerMaster')
                checkLocalServerIdentityReturn = changedData.checkLocalServerIdentity(localServerIdentification);

                if(checkLocalServerIdentityReturn == (DB_ACCESS_OK +VALID_LOCAL_SERVER ) ){
                    logger.info("Local Server identified");
                    //Store update requested time, later on same shall be sent to local server for record purpose
                    updateReqTime = DateFormat.getDateTimeInstance().format(new java.util.Date());
                    updateReqTime = updateReqTime.replaceAll("/","-");

                    //Send as 'Local Server is registered server'
                    writer.write(AUTH_PASS);
                    writer.flush();

                    //Get local server last updated time stamp
                    localServerLastUpdateTime = (String)reader.readObject();
                    logger.info("Local Server last updated time stamp is: "+ localServerLastUpdateTime);

                    //Initialize vector to get the updated info for this local server
                    collectionOfChangeMaster =  new Vector();
                    getUpdateReturn=changedData.getUpdateRec(collectionOfChangeMaster, localServerLastUpdateTime);

                    //If updated data exist for this local server
                    if( (collectionOfChangeMaster.size() > 0) && (getUpdateReturn) ){

                        //Initialize vectors to get updated database and files
                        collectionOfChangedRecords = new Vector();
                        collectionOfUpdatedFiles =  new Vector();

                        //Get updated database
                        getUpdateDataReturn=changedData.getChangedData(collectionOfChangeMaster,collectionOfChangedRecords);

                        //Get updated files
                        getUpdateFileReturn=changedData.getChangedFiles(collectionOfChangeMaster,collectionOfUpdatedFiles);

                        //Send all updated db objects in vector to Local server if vector has some data
                        if( (collectionOfChangedRecords.size() > 0) && (getUpdateDataReturn)){
                            writer.write(UPDATE_REC_EXIST);
                            writer.flush();
                            writer.writeObject(collectionOfChangedRecords);
                            writer.flush();
                            //Read DB update status from local system
                            readDBUpdStatus=reader.read();
                        
                        }
			//If no updated records for this local server
			else if(getUpdateDataReturn){
                            writer.write(UPDATE_REC_NOT_EXIST);
                        }
                        //If getting updated records for this local server is error
                        else if (!getUpdateDataReturn){
                            writer.write(SVR_DB_ACCESS_FAIL);
                            logger.warning("Error in accessing server database");
                        }

                        //Send all upated files object in vector to Local server if vector has some data
                        if( (collectionOfUpdatedFiles.size() > 0) && (getUpdateFileReturn)){
                            writer.write(UPDATE_FILE_EXIST);
                            writer.flush();
                            writer.writeObject(collectionOfUpdatedFiles);
                            writer.flush();
                            //Read File update status from local system
                            readFileUpdStatus=reader.read();
                        }
			//If no updated files for this local server
			else if(getUpdateFileReturn){
                            writer.write(UPDATE_FILE_NOT_EXIST);
			}
			//If getting updated files for this local server is error
			else if (!getUpdateFileReturn){
                            writer.write(SVR_FILE_ACCESS_FAIL);
                            logger.warning("Error in accessing server files");
                        }
                    }
                    //If getting updated info for this local server is error
		    else if(!getUpdateReturn){
                        writer.write(SVR_DB_ACCESS_FAIL);
                    }
		    //To send message local server that there is no updates for
		    else if( (collectionOfChangeMaster.size() < 1) && (getUpdateReturn) ){
			writer.write(UPDATE_REC_NOT_EXIST);
			writer.flush();
			writer.write(UPDATE_FILE_NOT_EXIST);
                    }
					
                    //Update local server update history (if DB access is OK)
		    //DB: Success/Fail/NULL(0==No Update)
		    //File: Success/Fail/NULL(0==No Update)
		    getUpdateDataReturn=changedData.storeLocalServerUpdateHistory(readDBUpdStatus, readFileUpdStatus,localServerIdentification);
		    if(!getUpdateDataReturn){
                        logger.warning("Error in storing local server update history");
                    }

		    //Send update requested time
		    writer.writeObject(updateReqTime);
                }
                //If Local server ID does not exist in the LocalServerMaster''
                else if(checkLocalServerIdentityReturn ==  DB_ACCESS_OK){
                    writer.write(AUTH_FAIL);
                }
		//Accessing DB failed
		else if(checkLocalServerIdentityReturn ==  DB_ACCESS_FAIL){
                    writer.write(SVR_DB_ACCESS_FAIL);
		    logger.warning("Error in accessing server database");
                }
		writer.flush();
            }
            catch (ClassNotFoundException cnf) {
		logger.warning("Class Not Found Exception while getting objects through streams");
                logger.warning( "Exception details:"  + cnf);
            }
            catch (SQLException sqle) {
		logger.warning("SQL Exception while getting data from remote server");
                logger.warning( "Exception details:"  + sqle);
            }
            catch (IOException ioe) {
		logger.warning("IO Exception while getting files from remote server or reading streams");
                logger.warning( "Exception details:"  + ioe);
            }
            catch (Exception e) {
		logger.warning("Exception while getting files from remote server or reading streams");
                logger.warning( "Exception details:"  + e);
            }
            finally {
                try {
                    if ((reader != null) && (!client.isClosed()) ) {
                        reader.close();
                    }
                    if ( (writer != null) && (!client.isClosed())  ) {
			writer.close();
                    }
                    if (client != null) {
						
                        client.close();
                    }
                    if(collectionOfChangeMaster !=null){
                        collectionOfChangeMaster.clear();
                        collectionOfChangeMaster=null;
                    }
                    if(collectionOfChangedRecords != null){
                        collectionOfChangedRecords.clear();
                        collectionOfChangedRecords = null;
                    }
                    if(collectionOfUpdatedFiles != null) {
                        collectionOfUpdatedFiles.clear();
                        collectionOfUpdatedFiles = null;
                    }
                    if (changedData != null) {
                        changedData=null;
                    }
                }
                catch (IOException e2) {
		    logger.warning("Exception while closing socket or writer or reader after sending all the info to local server");
                    logger.warning( "Exception details:"  + e2 );
                }
            }
            logger.finer("Method Exit");
        }
        
    }//class 'Connection' end

    /**
     *
     * main(), MasterUpdateRemote entry point<br>
     * Creates MasterUpdateRemote() thread and calls its startServer() method<br>
     *
     */
    public static void main(String[] args) {

            //To store Remote Server information like postgres connection etc
            boolean mainReturn=false;
            masterUpdateParameter=null;

            int port 	= 6001;

            //Reading INI File Parameter
            iniFileRead = new mirrorI.dolphin.server.IniFileRead();

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

            try{
                //Create masterUpdateRemote
                MasterUpdateRemote masterUpdateRemote = new MasterUpdateRemote();

                masterUpdateRemote.setPort(port);

                //Call masterUpdateRemote startReceiver() method to start listening for local server request
                mainReturn=masterUpdateRemote.startServer();
            }
            catch (Exception e){
                    logger.warning("Exception while creating 'MasterUpdateRemote' object");
                    logger.warning( "Exception details:"  + e);
            }
            if(!mainReturn){
                    logger.warning("Error in startServer() of masterUpdateRemote");
                    logger.warning("Abnormal Exit");
                    System.exit(1);
            }
	}//Main() end

}//Class 'MasterUpdateRemote' end
