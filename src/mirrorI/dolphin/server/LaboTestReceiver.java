/*
 * LaboTestReceiver.java
 *
 * Created on 2003/01/25
 *
 * Last updated on 2003/02/28
 *
 */

package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.net.*;

/**
 *
 * @author Prashanth Kumar, Mirror-i Corp.
 *
 * This class receives mml file and extRef from laboratory. Parses mml file and stores<br>
 * information in postgres database and moves extRef file to predefined directory<br>
 * calls 'LaboTestProcessor' for parsing and 'LaboTestImageHandler' for handling extRef<br>
 *
 */
public class LaboTestReceiver  {

	//Set default frequencey time (This value is considered only when this item does not exist in INI file)
	private static final int LABO_TEST_DEFAULT_FREQ=1000*90;
	//default mml receiving dir
	private static final String MML_REC_DEFAULT_DIR="/dolphin/mmlLb/incoming/";
	//default extRef receiving directory
	private static final String EXTREF_REC_DEFAULT_DIR="/dolphin/mmlLb/extRefs";
	//default parsed mml storing directory
	private static final String MML_STORING_PATH="/dolphin/mmlLb/parsedMmlFiles";

	//Hash Table to store Labo test various paramater
	private Properties laboTestParameter;

	//Logger for logging messages
	public static Logger logger;

	WatchingThread watchingThread = null;
	Vector foundFiles = null;
	boolean isBusy = false;

	//LaboTest Process for parsing mml and storing into database.
	LaboTestProcessor fileProcessor;

	//To handle image files
	LaboTestImageHandler laboTestImageHandler;

	//INI file read object
	public static mirrorI.dolphin.server.IniFileRead iniFileRead;

	//Dummy Server socket to disable multiple instance of LaboTestReceiver
	private ServerSocket listenSocket;

	/** Create New LaboTestReceiver */
	public LaboTestReceiver(Properties laboTestParameter) {

		int port = 23456;
		  try{
			  listenSocket = new ServerSocket(port);
		  }
		  catch (IOException ioe){
			  System.out.println("Labo Test Receiver is already running in this system");
			  System.out.println("Only one Labo Test Receiver can run at a time.");
			  System.exit(1);
		  }

		//Copy labotest parameters to local property
		this.laboTestParameter = laboTestParameter;
	}

	//Starting receiver to watch for mml file
    public boolean startReceiver() {
		logger.finer("Method Entry");
		boolean startReceiverReturn=false;

		if (watchingThread == null) {
			logger.finer("Labo test receiver thread started");

			//Initialize Image handler and call constructExtRef()
			laboTestImageHandler = new LaboTestImageHandler(laboTestParameter,logger);
			try{
				startReceiverReturn = laboTestImageHandler.constructExtRef();
			}
			catch (Exception e) {
				logger.warning("Exception while calling constructExtRef() of LaboTestImageHandler");
				logger.warning("Exception details:"  + e);
				return false;
			}

		  	//To handle unreferenced image file which are present in xx/xx/incoming dir
		  	startReceiverReturn = laboTestImageHandler.handleUnreferencedExtRefFile();
		   	if(!startReceiverReturn){
				logger.warning("Error in calling handleUnreferencedExtRefFile() of LaboTestImageHandler");
		  	}

		  	fileProcessor = new LaboTestProcessor(laboTestParameter,logger);
		  	watchingThread = new WatchingThread();
		  	watchingThread.start();
		}
		else{
			logger.warning("Labo test receiver starting error");
			startReceiverReturn=false;
		}
		logger.finer("Method Exit");
		return startReceiverReturn;
	}

    //WatchingThread class
    private class WatchingThread extends Thread {

        public WatchingThread() {
            super();
        }

        public void run() {
            //Check for MML file and process the same if it exist
            //Set default frequencey time (This value is considered only same item does not exist in INI file)
            int laboTestFrequency=LABO_TEST_DEFAULT_FREQ;
            //check for this item in INI file
			if (laboTestParameter != null && laboTestParameter.size() > 0 &&
															 laboTestParameter.containsKey("WatchFrequency") &&
															 new Integer(laboTestParameter.getProperty("WatchFrequency")).intValue() >0) {
				//Get watch frequence and store it in laboTestFrequency
				laboTestFrequency =(new Integer(laboTestParameter.getProperty("WatchFrequency")).intValue());
			}
			else{
				logger.warning("Error in getting 'WatchFrequence' value from INI, taking default as: "+laboTestFrequency+" MiliSeconds");
			}

            doWork();

            while (true) {
                while (true) {
					try {
						Thread.sleep(laboTestFrequency);
					}
					catch(InterruptedException e){
						logger.warning("laboTest thread 'WatchingThread' Interrupted Exception error");
					}

					if (isBusy == false) {
						doWork();
						//Check for ExtRef files and update database
						try{
							laboTestImageHandler.checkExtRef();
						}
						catch (Exception e) {
							logger.warning("Exception while calling checkExtRef() of LaboTestImageHandler");
							logger.warning("Exception details:"  + e);
						}
					}
					else{
						logger.warning("Watching Thread is busy even after completing one cycle");
						logger.warning(", , ,parsing current file is skipped even if mml file is exist");
					}
					yield();
					break;
                }
	    	}
        }

		/**
		 *
		 * doWork(), calls watchDirectory() to check for mml file, if files exist<br>
		 * then it calls processFiles() for handling mml file <br>
		 * <br>
		 * This method is called from run()<br>
		 *
		 */
		private void doWork() {
			logger.finer("Method Entry");
			boolean doWorkReturn=false;
			foundFiles = new Vector();

			//Watch directory for mml files
			doWorkReturn = watchDirectory();

			if(doWorkReturn){
				processFiles();
			}
			logger.finer("Method Exit");
		}

		/**
		 *
		 * watchDirectory(), checks for mml file in 'laboTestMmlRecDir'<br>
		 * Returns true if it finds any mml file<br>
		 * <br>
		 * This method is called from doWork()<br>
		 *
		 */
		private boolean watchDirectory() {
			logger.finer("Method Entry");
			String laboTestMmlRecDir =MML_REC_DEFAULT_DIR;
			String laboTestExtRefRecDir =EXTREF_REC_DEFAULT_DIR;
			//check for above directories in INI file
			if (laboTestParameter != null && laboTestParameter.size() > 0 &&
														  laboTestParameter.containsKey("SourceDir") &&
														  laboTestParameter.getProperty("SourceDir") !=null ) {
				//Get mml incoming dir and store it in laboTestMmlRecDir
				laboTestMmlRecDir=(String)laboTestParameter.getProperty("SourceDir");
			}
			else{
				logger.warning("Error in getting 'mml incoming dir' value from INI, taking default as: "+laboTestMmlRecDir);
			}
			if (laboTestParameter != null && laboTestParameter.size() > 0 &&
															 laboTestParameter.containsKey("ExtRefDir") &&
															 laboTestParameter.getProperty("ExtRefDir") !=null ) {
				//Get extRef moving dir and store it in laboTestExtRefRecDir
				laboTestExtRefRecDir=(String)laboTestParameter.getProperty("ExtRefDir");
			}
			else{
				logger.warning("Error in getting 'extRef dir' value from INI, taking default as: "+laboTestExtRefRecDir);
			}

			//Checking for file separator in the below paths
			laboTestMmlRecDir = laboTestMmlRecDir.replace('/',File.separatorChar);
			laboTestMmlRecDir = laboTestMmlRecDir.replace('\\', File.separatorChar);
			laboTestExtRefRecDir = laboTestExtRefRecDir.replace('/',File.separatorChar);
			laboTestExtRefRecDir = laboTestExtRefRecDir.replace('\\', File.separatorChar);

			// Firstly, check the existence of the target directory to watch.
			File targetDir = new File(laboTestMmlRecDir);
			if (targetDir.exists() == false) {
				logger.warning("Target directory doesn't exist: " +laboTestMmlRecDir);

				// Create new directory with specified name.
				if (targetDir.mkdirs() == true) {
					logger.finer("Target directory was created successfuly: "+ laboTestMmlRecDir);
				}
				else{
					logger.warning("Target directory could not be created: "+ laboTestMmlRecDir);
					return  false;
				}
			}
			if (targetDir.exists() == true) {
				// Check the readability of the directory
				if (targetDir.isDirectory() == false || targetDir.canRead() == false) {
					logger.warning("Error in reading target directory."+ laboTestMmlRecDir);
					return false;
				 }
			}
			// Firstly, check the existence of the extRefs directory.
			File extDir = new File(laboTestExtRefRecDir);
			if (extDir.exists() == false) {
				 logger.warning("extRefs directory doesn't exist: " +laboTestExtRefRecDir);

				// Create new directory with specified name.
				if (extDir.mkdirs() == true) {
					 logger.finer("extRefs directory was created successfuly: "+ laboTestExtRefRecDir);
				}
				else{
					logger.warning("extRefs directory could not be created: "+ laboTestExtRefRecDir);
					return false;
				}
			}
			if (extDir.exists() == true) {
				// Check the readability of the directory
				if (extDir.isDirectory() == false || extDir.canRead() == false) {
					 logger.warning("Error in reading extRefs directory.");
					 return false;
				 }
			}
			// Traverse target directory to find files in it.
			String flist[] = targetDir.list();
			for (int i=0; i < flist.length; ++i) {
				// case insensitive
				if ( flist[i].toLowerCase().endsWith("xml") == false ) {
					continue;
				}
				File file = new File(targetDir.getPath(),flist[i]);
				if (file.isFile() == false) {
					// target object is not a file,  skip it
					continue;
				}
				logger.info("Found XML file: " + file.getName());
				foundFiles.addElement(file);
			}

			//Check whether was there any file in target directory
			if(foundFiles.size() > 0){
				logger.finer("Found mml files");
				return true;
			}
			else{
				logger.finer("Nothing was found so far... Maybe next time.");
				return false;
			}
		}

		/**
		 *
		 * processFiles(), calls processFile() of LaboTestProcessor to parse mml file<br>
		 * and stores parsed info in postgres DB<br>
		 * then it calls moveMmlFile() to move parsed mml file <br>
		 * <br>
		 * This method is called from doWork()<br>
		 *
		 */
		private void processFiles() {
			logger.finer("Method Exit");
			boolean processFilesReturn=false;

			//set as busy
			isBusy = true;

			// Process Files
			while (foundFiles.size() > 0) {
				//get the first file in vector
				File mmlFile = (File)foundFiles.elementAt(0);

				try{
					// process this file
					processFilesReturn = fileProcessor.processFile(mmlFile,laboTestImageHandler);
				}
				catch (Exception e) {
					logger.warning("Exception while calling processFile() of LaboTestProcessor");
					logger.warning("Exception details:"  + e);
				}

				// move the parsed file
				try {
					if(processFilesReturn){
						logger.finer("Parsed and stored information of file " + mmlFile.getName() +" Successfuly");
						logger.finer("Mml file shall be moved to 'Save' directory");
					}
					else{
						logger.warning("Error in processing or storing information of file " + mmlFile.getName());
						logger.finer("Mml file shall be moved to 'Error' directory");
					}
					processFilesReturn = moveMmlFile(mmlFile,processFilesReturn);

					if(!processFilesReturn){
						logger.warning ("Error in moving mml file: " + mmlFile.getName() + ", this file shall be deleted , , ");
						mmlFile.delete();
					}
				}
				catch (Exception e) {
					logger.warning("Exception while calling moving xml file into directory");
					logger.warning("Exception details:"  + e );
				}
				// remove this file from the list
				foundFiles.removeElementAt(0);
				logger.info("Processing current file done..." + mmlFile);
			}
			logger.finer("Method Exit");
			isBusy = false;
		}

		/**
		 *
		 * moveMmlFile(), moves Mml files from incoming dir to<br>
		 * Mml Storing Dir. Mml Storing Dir is created based on the date if doesn't exist<br>
		 * <br>
		 * This method is called from processFiles()<br>
		 *
		 */
		private boolean moveMmlFile(File mmlSrcFile, boolean saveOrErrorFlag){
			logger.finer("Method Entry");
			boolean moveMmlFile=false;
			String mmlStoringDirPath=null;
			String mmlStoringDir=MML_STORING_PATH;

            //check for Mml Storing Directory in INI file
			if (laboTestParameter != null && laboTestParameter.size() > 0 &&
														  laboTestParameter.containsKey("MmlStoringDir") &&
														  laboTestParameter.getProperty("MmlStoringDir") !=null ) {
				//Get mml incoming dir and store it in laboTestMmlRecDir
				mmlStoringDir =(String)laboTestParameter.getProperty("MmlStoringDir");
			}
			else{
				logger.warning("Error in getting 'parsed mml files storing ' value from INI, taking default as: "+mmlStoringDir);
			}

			//Checking file separator at the end of the below paths
			if(!mmlStoringDir.endsWith(File.separator)) {
				mmlStoringDir = mmlStoringDir + File.separatorChar;
			}

			//Checking for file separator in the below paths
			mmlStoringDir = mmlStoringDir.replace('/',File.separatorChar);
			mmlStoringDir = mmlStoringDir.replace('\\', File.separatorChar);

			//Concate 'Save' or 'Error' for destination dir
			if(saveOrErrorFlag){
				mmlStoringDir = mmlStoringDir + "Save" + File.separatorChar;
			}
			else{
				mmlStoringDir = mmlStoringDir + "Error" + File.separatorChar;
			}

			//Creating dir based on date under unreferenced folder(laboTestErrorFilesDir).
			java.util.Date dateMills = new java.util.Date(System.currentTimeMillis());
			java.sql.Date  date = new java.sql.Date(dateMills.getTime());
			String todayDate = date.toString();

			//Concate with today's date
			mmlStoringDir = mmlStoringDir + todayDate+ File.separatorChar;
			//Check whether the above dir is available for moving files, if no then create
			try{
				File newDestDir = new File(mmlStoringDir);
				if (newDestDir.exists() == false) {
					logger.finer("Mml files storing Dir doesn't exist, creating: "+ mmlStoringDir);
					if (newDestDir.mkdirs() == true) {
						if (newDestDir.isDirectory() == false || newDestDir.canRead() == false) {
							logger.warning("Error in reading mml files storing dir: "+ mmlStoringDir);
							return moveMmlFile;
						}
					}
					else{
						logger.warning("Error in creating mml files storing dir:  "+ mmlStoringDir);
						return moveMmlFile;
					}
				}
			}
			catch (Exception e) {
				logger.warning("Exception while calling moveMmlFile()");
				logger.warning("Exception details:"  + e);
			}

			//Concate unreferenced file name with newly created dir
			mmlStoringDirPath = mmlStoringDir + mmlSrcFile.getName();

			File mmlDestFile = new File(mmlStoringDirPath);

			if(mmlSrcFile.isFile()){
				try{
					moveMmlFile=mmlSrcFile.renameTo(mmlDestFile);
					if (!moveMmlFile) {
							logger.warning("Couldn't move Mml file: " + mmlSrcFile.getAbsolutePath());
							moveMmlFile=false;
						}
					else{
						logger.finer("Successfuly moved  xml file from: " + mmlSrcFile.getAbsolutePath() +
										 " to: " + mmlDestFile.getAbsolutePath());
						moveMmlFile=true;
					}
				}
				catch (Exception e) {
					logger.warning("Exception while moving Mml file "  +  mmlSrcFile.getAbsolutePath());
					logger.warning( "Exception details:"  + e);
					moveMmlFile=false;
				}
			}
			else{
				logger.warning("Not a valid file for moving into Mml  dir "+ mmlSrcFile.getAbsolutePath() );
				moveMmlFile=false;
			}
			 logger.finer("Method Exit");
			return moveMmlFile;
		}
    }

	/**
	 *
	 * main(), LaboTestReceiver entry point<br>
	 * If LaboTest is not running then<br>
	 *   	Reads INI file for LaboTest Parameter<br>
	 *		Opens log handler for logging messages<br>
	 * 	Calls LaboTestReceiver<br>
	 * Else<br>
	 *		OutPut error message and exit<br>
	 *
	 */
	public static void main(String args[]) {

		//To store labotest parameter like postgres connection, dir name etc
		boolean mainReturn=false;
		Properties laboTestParameter=null;

		//Reading INI File Parameter
		iniFileRead = new mirrorI.dolphin.server.IniFileRead();

		laboTestParameter = iniFileRead.readIniFile();
		if ( laboTestParameter == null){
			  System.out.println("Could not get INI file " );
			  System.exit(1);
		}
		//set IniFileRead object as null
		iniFileRead = null;

		// Opening log handler
		if (laboTestParameter != null && laboTestParameter.size() > 0 &&
		    										  laboTestParameter.containsKey("LaboLoggerLocation") &&
		    										  laboTestParameter.getProperty("LaboLoggerLocation") !=null ) {

			logger = Logger.getLogger(laboTestParameter.getProperty("LaboLoggerLocation"));
			//Copy to LoggerLocation for PostgresConnection Object
			laboTestParameter.put("LoggerLocation",laboTestParameter.getProperty("LaboLoggerLocation"));
		}
		//To aviod run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
			laboTestParameter.put("LoggerLocation","Dummy");
		}

		try {
			logger.addHandler(new FileHandler());
		}
		catch(IOException e) {
			System.out.println("File handler could not be found");
			System.out.println( "Exception details:"  + e );
		}
		catch(Exception e) {
			System.out.println("Exception while opening logger handler");
			System.out.println( "Exception details:"  + e );
		}

        logger.finer("Method Entry");
        //Create LaboTest
        LaboTestReceiver laboTestReceiver = new LaboTestReceiver(laboTestParameter);

        //Call Labotest startReceiver() method to start watching for MML file
        mainReturn=laboTestReceiver.startReceiver();

        if(!mainReturn){
			logger.warning("Error in startReceiver() of LaboTestReceiver");
			System.exit(1);
		}
        logger.finer("Method Exit");
	}
}
