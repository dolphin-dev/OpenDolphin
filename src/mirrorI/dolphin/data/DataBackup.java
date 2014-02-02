/*
 * DataBackup.java
 *
 * Last updated on 2003/02/28
 *
 * Last updated on 2003/03/11 to change tar file structure
 *
 * Last updated on 2003/03/25 to allow PostgreSQL to be installed in any directory (earlier only in ..../pgsql)
 *
 */

package mirrorI.dolphin.data;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import sun.net.TelnetOutputStream;

/**
 *
 * DataBackup, takes backup of PostgreSQL backup and Labotest files<br>
 * This class can be called from command prompt, schedualr (CRON) and client system<br>
 *
 * @author  Aniruddha, Mirror - I
 */
public class DataBackup  extends sun.net.ftp.FtpClient {

    //Selected Media
    private static final int MEDIA_HDD     				 = 1;
    private static final int MEDIA_TAPE    				 = 2;
    //Selected Hard Disc
    private static final int HDD_DOLPHIN     			 = 1;
    private static final int HDD_CLAIM      				 = 2;

	private static final int RET_FAIL                         = 1;
	private static final int OPERATION_SUCCESS      = 0;
	private static final int OPERATION_FAIL              = 1;
	private static final int PATH_ERROR                   = 2;
	private static final int TAPE_ERROR                   = 3;

    public static final String DEFAULT_FILE_NAME 	= "dolphin";
    public static final String POSTGRES_LOCATION = "/usr/local/pgsql/bin";      // revised 2003/03/25
    public static final String LABO_FILES_LOCATION= "/dolphin/mmlLb/";
    public static final String TAPE_DEV_LOCATION  ="/dev/st0";

	public static String defaultLocation 				 = "/usr/local/dolphin/latest/";
	public static String lastFileLocation 				 = "/usr/local/dolphin/last/";

	//Property Table to store Postgres various paramater
	private static Properties dataBackupParameter;
	//Logger for logging messages
	private static Logger logger;
	//To get INI file parameter
	private static mirrorI.dolphin.server.IniFileRead iniFileRead;

	//creating dateString for using with backup file name
	java.util.Date date = new java.util.Date();
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
	String dateString = formatter.format(date);


    /** Creates new DataBackup */
    public DataBackup(Properties p, Logger l) {
		dataBackupParameter = p;
		logger = l;
    }

	/**
	 *
	 * startDatabackup(), Gets required deufault from properties(INI) and calls<br>
	 * startDatabackup()<br>
	 *
	 * This method is called from main()<br>
	 */
	public int startDatabackup() {
		logger.finer("Method Entry");
		//Check and read INI file to get backup parameters
		int startDataBackupRet=RET_FAIL;
		int selectedMedia=MEDIA_HDD;
		int selectedHdd=HDD_DOLPHIN;

		if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
			dataBackupParameter.containsKey("DefaultBackupPath") &&
			dataBackupParameter.containsKey("DefaultBackupLastPath") &&
			dataBackupParameter.containsKey("BackupMediaSelection") &&
			dataBackupParameter.containsKey("BackupSystemSelection") ) {

			//get requred field and store in local variable
			if(dataBackupParameter.getProperty("DefaultBackupPath") != null){
				defaultLocation = dataBackupParameter.getProperty("DefaultBackupPath");
			}
			else{
				logger.warning("Could not get 'DefaultBackupPath' for data backup from INI file, taking default: "+defaultLocation);
			}

			if (dataBackupParameter.getProperty("DefaultBackupLastPath") != null){
				lastFileLocation = dataBackupParameter.getProperty("DefaultBackupLastPath");
			}
			else{
				logger.warning("Could not get 'DefaultBackupLastPath' for data backup from INI file, taking default: "+lastFileLocation);
			}

			if( (new Integer(dataBackupParameter.getProperty("BackupMediaSelection")).intValue()) >0){
				selectedMedia = (new Integer(dataBackupParameter.getProperty("BackupMediaSelection")).intValue());
				if( ! (selectedMedia==MEDIA_HDD || selectedMedia==MEDIA_TAPE ) ){
					//set default value
					selectedMedia=MEDIA_HDD;
				}
			}
			else{
				logger.warning("Could not get 'BackupMediaSelection' for data backup from INI file, taking default: "+selectedMedia);
			}

			if( (new Integer(dataBackupParameter.getProperty("BackupSystemSelection")).intValue()) >0){
				selectedHdd = (new Integer(dataBackupParameter.getProperty("BackupSystemSelection")).intValue());
				if(! (selectedHdd==HDD_DOLPHIN || selectedHdd==HDD_CLAIM ) ){
					//default value
					selectedHdd=HDD_DOLPHIN;
				}
			}
			else{
				logger.warning("Could not get 'BackupSystemSelection' for data backup from INI file, taking default: "+selectedHdd);
			}
		}
		else {
			logger.warning("Could not get required parameter for data backup from INI file, taking default");
		}
		//call actual data bacup method
		startDataBackupRet= startDatabackup(selectedMedia,selectedHdd,defaultLocation);
		logger.finer("Method Exit");
		return startDataBackupRet;
	}

	/**
	*
	* startDatabackup(), takes backup of postgreSQL and Labo test files based on <br>
	* selected parameter<br>
	*
	* This method is called from this object's startDatabackup()  and 'client request handler'<br>
	*
	* selectedMedia: 1=HDD, 2=TAPE, selectedHdd: 1=Dolphin Server Hdd, 2=Claim server Hdd
	*
	*/
	public int startDatabackup(int selectedMedia, int selectedHdd, String location) {

		logger.finer("Method Entry");

		//back up taken in default location before transfering to tape or ORCA
		if((selectedMedia ==MEDIA_TAPE)|(selectedHdd ==HDD_CLAIM)) {
			location = defaultLocation;
		}
		//Set default values (Same will be used if the corresponding items are not found in INI file)
		String postgresLocation = POSTGRES_LOCATION;
		String laboFileLocation = LABO_FILES_LOCATION;

		if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
			dataBackupParameter.containsKey("PostgresLocation") &&
			dataBackupParameter.containsKey("LaboFileLocation") ) {

			//get requred field and store in local variable
			if(dataBackupParameter.getProperty("PostgresLocation") != null){
				postgresLocation = dataBackupParameter.getProperty("PostgresLocation");
			}
			else{
				logger.warning("Could not get 'PostgresLocation' for data backup from INI file, taking default: "+postgresLocation);
			}

			if (dataBackupParameter.getProperty("LaboFileLocation") != null){
				laboFileLocation = dataBackupParameter.getProperty("LaboFileLocation");
			}
			else{
				logger.warning("Could not get 'LaboFileLocation' for data backup from INI file, taking default: "+laboFileLocation);
			}
		}
		else {
			logger.warning("Could not get required parameter for data backup from INI file, taking defualt");
		}

		//in case user has not used  '/' at start and at end, add it
		if(!postgresLocation.endsWith(File.separator)){
			postgresLocation = postgresLocation + File.separator;
		}
		if(!postgresLocation.startsWith(File.separator)){
			postgresLocation = File.separator + postgresLocation ;
		}

		//in case user has not used  '/' at start and at end, add it
		if(!laboFileLocation.endsWith(File.separator)){
			laboFileLocation = laboFileLocation + File.separator;
		}
		if(!laboFileLocation.startsWith(File.separator)){
			laboFileLocation = File.separator + laboFileLocation;
		}

        int result =RET_FAIL;

        location = location.trim();

        if(location.indexOf(' ')>=(0)){
			// there should not be any blank in file location
			return PATH_ERROR ;
		}
		//in case user has not used  '/' at start and at end, add it
        if(!location.endsWith(File.separator)){
			location = location + File.separator;
		}
		if(!location.startsWith(File.separator)){
			location = File.separator + location ;
		}

        //check whether location specified exists - if it does not exist, try to make it
		File fileLocation = new  File(location);
		boolean directoryExist = fileLocation.isDirectory();

		if(!directoryExist) {
		    try{
				boolean makeDir = fileLocation.mkdirs();
				if(!makeDir){
					logger.warning( "Could not create directory");
					return PATH_ERROR ;
				}
			}
			catch(SecurityException se){
				logger.warning("Security Exception error while creating:  "+fileLocation.getAbsolutePath());
				logger.warning( "Exception details:"  + se );
				return PATH_ERROR ;
			}
			catch(Exception e){
				logger.warning("Exception error while creating:  "+fileLocation.getAbsolutePath());
				logger.warning( "Exception details:"  + e );
				return PATH_ERROR ;
			}
		}


		try{          //check whether write access to specified directory is available
			boolean canWrite = fileLocation.canWrite();
			if(!canWrite){
				logger.warning( "No write access in directory");
				return PATH_ERROR ;
			}
		}
		catch(SecurityException se){
				logger.warning("Security Exception error while creating:  "+fileLocation.getAbsolutePath());
				logger.warning( "Exception details:"  + se );
				return PATH_ERROR ;
		}


       //check that postgres location exists
		File databaseLocation = new  File(postgresLocation);
		if(!databaseLocation.isDirectory()){
			logger.warning( "Postgres location specified could not be found");
			return OPERATION_FAIL;
		}

        //Starting actual backup operation

//		String command = postgresLocation+"pgsql/bin/pg_dump -f "+ location+DEFAULT_FILE_NAME+dateString+"data.dmp  -F c dolphin";
		String command = postgresLocation+"pg_dump -f "+ location+DEFAULT_FILE_NAME+dateString+"data.dmp  -F c dolphin";    // revised 2003/03/25

		int dataBackup = executeSystemCommand(command);

		command = "tar -cf  "+ location+DEFAULT_FILE_NAME+dateString+"Labo.tar "+laboFileLocation;
    	int laboBackup = executeSystemCommand(command);

        command = "tar -cf  "+ DEFAULT_FILE_NAME+dateString+".tar "+
                                           DEFAULT_FILE_NAME+dateString+"data.dmp "+
                                           DEFAULT_FILE_NAME+dateString+"Labo.tar ";
        int makeTar = executeSystemCommand(command, location);

		command = "rm -f  "+   location+DEFAULT_FILE_NAME+dateString+"data.dmp "+
										   location+DEFAULT_FILE_NAME+dateString+"Labo.tar ";
		int deleteFile = executeSystemCommand(command);

	//	command = "gzip "+ location+DEFAULT_FILE_NAME+dateString+".tar ";
	//	int zipFile = executeSystemCommand(command);

	   command = "gzip "+ DEFAULT_FILE_NAME+dateString+".tar ";
	   int zipFile = executeSystemCommand(command, location);

		if (dataBackup ==0 && zipFile ==0){
			int fileMove = moveFiles(DEFAULT_FILE_NAME, location);

			if(selectedMedia==MEDIA_TAPE){
				return  moveToTape();
			};

			if(selectedHdd ==HDD_CLAIM){
				return  moveToORCA();
			};
			return OPERATION_SUCCESS;
		}
		logger.finer("Method Exit");
		return OPERATION_FAIL;
	}

	/**
	* executeSystemCommand(), Executes command at System level<br>
	* This method is called from this startDatabackup() <br>
	*/
	private int executeSystemCommand(String command){
		logger.finer("Method Entry");
		int exeSysComRet=OPERATION_FAIL;
		try{
			Runtime runTime = Runtime.getRuntime();
			Process process    = runTime.exec(command);
			int completed        = process.waitFor();
			int result =1;
			if(completed ==0){
				result = process.exitValue();
			}
			if (result ==0){
				logger.info("Successfully executed command: " +command);
				exeSysComRet= OPERATION_SUCCESS;
			}

			if (result !=0){
				logger.warning("Unsuccessfull while executing command: " +command);
				exeSysComRet= OPERATION_FAIL;
			}
		}
		catch ( IOException ioe){
			logger.warning("Unsuccessfull while executing: "+ command);
			logger.warning( "Exception details:"  +ioe);
			exeSysComRet= OPERATION_FAIL;
		}
		catch (InterruptedException ie){
			logger.warning("Unsuccessfull while executing: "+ command);
			logger.warning( "Exception details:"  +ie);
			exeSysComRet= OPERATION_FAIL;
		}
		catch(Exception e){
			logger.warning("Exception while executing system level command");
			logger.warning( "Exception details:"  +e );
			exeSysComRet= OPERATION_FAIL;
		}
		logger.finer("Method Exit");
		return exeSysComRet;
	}

//

private int executeSystemCommand(String command, String directory){
		logger.finer("Method Entry");
		int exeSysComRet=OPERATION_FAIL;
		try{

			File dir = new File(directory);
			boolean directoryExist = dir.isDirectory();

			if(!directoryExist){
				logger.warning("Directory specified for running command does not exist: " +directory);
			}

			Runtime runTime = Runtime.getRuntime();
			Process process    = runTime.exec(command, null, dir);
			int completed        = process.waitFor();
			int result =1;
			if(completed ==0){
				result = process.exitValue();
			}
			if (result ==0){
				logger.info("Successfully executed command: " +command + " in directory " + directory);
				exeSysComRet= OPERATION_SUCCESS;
			}

			if (result !=0){
				logger.warning("Unsuccessfull while executing command: " +command);
				exeSysComRet= OPERATION_FAIL;
			}
		}
		catch ( IOException ioe){
			logger.warning("Unsuccessfull while executing: "+ command);
			logger.warning( "Exception details:"  +ioe);
			exeSysComRet= OPERATION_FAIL;
		}
		catch (InterruptedException ie){
			logger.warning("Unsuccessfull while executing: "+ command);
			logger.warning( "Exception details:"  +ie);
			exeSysComRet= OPERATION_FAIL;
		}
		catch(Exception e){
			logger.warning("Exception while executing system level command");
			logger.warning( "Exception details:"  +e );
			exeSysComRet= OPERATION_FAIL;
		}
		logger.finer("Method Exit");
		return exeSysComRet;
	}


//


	/**
	* moveFiles(), Moves file from latest to last directory<br>
	* This method is called from this startDatabackup() <br>
	*/
	private int moveFiles(String filename, String location){
		logger.finer("Method Entry");
		//First check whether directories exist, if not then create them
		//location of latest backup
		File latestLocation = new File(location);
		//location of last backup
		File lastLocation    = new File(lastFileLocation);

		if(!latestLocation.isDirectory()) {
			try{
				latestLocation.mkdirs();
			}
			catch(SecurityException se){
				logger.warning("No access right for creating directory specified " +se);
				return PATH_ERROR ;
			}
		}

		if(!lastLocation.isDirectory()) {
			try{
				lastLocation.mkdirs();
			}
			catch(SecurityException se){
				logger.warning("No access right for creating directory specified "+se);
				return PATH_ERROR ;
			}
		}

		String lastFiles[] = latestLocation.list();
		String filesToDelete[] = lastLocation.list();

		//moving files from latest to last directory
		for(int i=0;i<lastFiles.length;i++){
			File file = new File(latestLocation.toString()+File.separator+lastFiles[i]);

			logger.finer("File to move exists " + file +" "+ file.isFile());

			if(!(file.toString()).equals(location+filename+dateString+".tar.gz")){         //file except latest backup are moved to last directory
                                                                                                            //file name extention is gz for gzipped file in Linux
				logger.finer("file to move  is " + file.toString());
				boolean moved = false;
				try {
					moved = file.renameTo(new File(lastLocation+File.separator+lastFiles[i]));
                    logger.finer("file to move  is " + file.toString());
				}
				catch(Exception e){
					logger.warning("Error while moving file "+lastFiles[i]);
				}

				if(!moved){
					logger.warning("File moving unsuccessfull for file "+location+lastFiles[i]);
				}
			}
        }

		//deleting files from last directory
		for(int i=0;i<filesToDelete.length;i++){

			File file = new File(lastFileLocation.toString()+filesToDelete[i]);

			try {
				boolean deleted= false;
				if(!file.equals(filename)){             //file except latest backup are moved to last directory
					deleted = file.delete();
				}

				if(!deleted){
					logger.warning("File deleting unsuccessfull for file "+lastFileLocation+filesToDelete[i]);
				}
			}
		    catch(Exception e){
				logger.warning("Error while deleting file "+lastFileLocation+filesToDelete[i]+" "+ e);
			}
		}
       logger.finer("Method Exit");
       return OPERATION_SUCCESS;
	}

	/**
	* moveFiles(), Moves backup file to tape drive<br>
	* This method is called from this startDatabackup() <br>
	*/
	private int moveToTape(){

		logger.finer("Method Entry");
		//default location
        String tapeLocation = TAPE_DEV_LOCATION;

		if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
			dataBackupParameter.containsKey("TapeLocation") ) {
			//get requred field and store in local variable
			if(dataBackupParameter.getProperty("TapeLocation") != null){
				tapeLocation = dataBackupParameter.getProperty("TapeLocation");
			}
			else{
				logger.warning("Could not get 'TapeLocation' for data backup from INI file, taking default: "+tapeLocation);
			}
		}
		else {
			logger.warning("Could not get 'TapeLocation' for data backup from INI file, taking default: "+tapeLocation);
		}
		//in case user has not used  '/' at start then add it
		if(!tapeLocation.startsWith(File.separator)){
			tapeLocation = File.separator + tapeLocation ;
		}

        String command = "tar -cvf "+tapeLocation +" " +(defaultLocation+DEFAULT_FILE_NAME+dateString+".tar.gz");
		int result  = executeSystemCommand(command);

		if(result==OPERATION_SUCCESS) {
			logger.info("Successfuly backup files are stored in Tape");
			logger.finer("Method Exit");
			return OPERATION_SUCCESS;
		}
		else{
			logger.warning("Error in storing backup files in Tape");
			logger.finer("Method Exit");
			return TAPE_ERROR;
		}

        /* This  block of code works for taking backup to floppy - but suitable only if data volume is less than 1.4MB

		String command = "cp "+(defaultLocation+DEFAULT_FILE_NAME+dateString+".tar.gz")+ "  /mnt/floppy ";
		int result  = executeSystemCommand(command);

		if(result==OPERATION_SUCCESS) {
			return OPERATION_SUCCESS;
		}
		else{
			return TAPE_ERROR;
		}
		*/
	}

	/**
	* moveFiles(), Moves backup file toORCA system hard disc<br>
	* This method is called from this startDatabackup() <br>
	*/
	private int moveToORCA(){

		logger.finer("Method Entry");
		String server = null;
		String user = null;
		String passwd = null;
		String directory =null;

		if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
			dataBackupParameter.containsKey("ORCAIPAddress") &&
			dataBackupParameter.containsKey("ORCAUser") &&
			dataBackupParameter.containsKey("ORCAPassword") &&
			dataBackupParameter.containsKey("ORCADirectory") ) {

			//get requred field and store in local variable
			if(dataBackupParameter.getProperty("ORCAIPAddress") != null){
				server = dataBackupParameter.getProperty("ORCAIPAddress");
			}

			if (dataBackupParameter.getProperty("ORCAUser") != null){
				user = dataBackupParameter.getProperty("ORCAUser");
			}

			if (dataBackupParameter.getProperty("ORCAPassword") != null){
				passwd = dataBackupParameter.getProperty("ORCAPassword");
			}

			if (dataBackupParameter.getProperty("ORCADirectory") != null){
				directory = dataBackupParameter.getProperty("ORCADirectory");
			}
		}
		else {
			logger.warning("Could not get required parameter for data backup to ORCA system from INI file");
			return OPERATION_FAIL;
		}
		//open FTP connection with ORCA system and move backup files
		try{
			openServer(server);
			login(user, passwd);
			binary();
			cd(directory);
			byte[] buffer = new byte[4096];
            FileInputStream fis = new FileInputStream(defaultLocation+DEFAULT_FILE_NAME+dateString+".tar.gz");
            TelnetOutputStream ts = put(DEFAULT_FILE_NAME+dateString+".tar.gz");
            while( (fis.read(buffer)) != -1 ) {
            	ts.write(buffer);
            }
            ts.close();
			fis.close();
			closeServer();
			logger.info("Successfuly backup files are moved into ORCA system");
			return OPERATION_SUCCESS;
		}
		catch(IOException ioe) {
			logger.warning("IO exception while trasferring backup files to ORCA");
			logger.warning( "Exception details:"  + ioe );
		}
		catch(Exception e) {
			logger.warning("Exception while trasferring backup files to ORCA");
			logger.warning("Exception details:"  + e );
		}
		logger.finer("Method Exit");
		return OPERATION_FAIL;
	}

	/**
	 *
	 * main(), DataBackup entry point<br>
	 *   	Reads INI file for path, media, system selection etc <br>
	 *		Opens log handler for logging messages<br>
	 * 	Calls DataBackup.startDatabackup()<br>
	 *
	 */
    public static void main(String args[]) {

		//To store Remote Server information like host ID, postgres connection etc etc
		int mainReturn=0;
		dataBackupParameter=null;
		//Read INI file and store info in 'masterUpdateParameter'
		iniFileRead = new mirrorI.dolphin.server.IniFileRead();
		dataBackupParameter = iniFileRead.readIniFile();
		if ( dataBackupParameter == null){
			System.out.println("Could not get INI file " );
			System.exit(1);
		}
		//set IniFileRead object as null
		iniFileRead = null;

		// Opening log handler
		if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
		    											  dataBackupParameter.containsKey("DataBackupLoggerLocation") &&
		    											  dataBackupParameter.getProperty("DataBackupLoggerLocation") !=null ) {

			logger = Logger.getLogger(dataBackupParameter.getProperty("DataBackupLoggerLocation"));
		}
		//To aviod run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
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
			System.out.println("Exception details:"  + e );
		}

		logger.finer("Method Entry");

        //Create Master Update Local
        DataBackup dataBackup = new DataBackup(dataBackupParameter,logger);

        //Call masterUpdateLocal getUpdate() method to get latest data/file from remote server
		mainReturn=dataBackup.startDatabackup();

		if( (mainReturn == RET_FAIL) ){
			logger.warning("Error in startDatabackup() of DataBackup");
			logger.warning("Abnormal Exit");
			System.exit(1);
		}
		logger.finer("Method Exit");
	}//Main end
}

