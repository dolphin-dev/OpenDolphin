/*
 * LaboTestImageHandler.java
 *
 * Created on 2003/01/19
 *
 * Last updated on 2003/02/28
 *
 */

package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.sql.*;

/**
 *
 * This class handles extrernal reference files which are received after parsing corresponding mml file<br>
 *
 * @author Prashanth Kumar, Mirror-i Corp.
 *
 */
public class LaboTestImageHandler{

	//default mml receiving dir
	private static final String MML_REC_DEFAULT_DIR="/dolphin/mmlLb/incoming/";
	//default extRef receiving directory
	private static final String EXTREF_REC_DEFAULT_DIR="/dolphin/mmlLb/extRefs";
	//default un-referenced files storing directory
	private static final String ERROR_FILES_STORING_PATH="/dolphin/mmlLb/ErrorFiles";
	//defualt  extRef retry times
	private static final int EXT_REF_TIMEOUT_RETRY		= 4;

	//Vector to store ExtRefs objects
	private Vector  collectionOfExtRefs=null;

	// To store labo test parameter
	private Properties laboTestParameter=null;

	//To store logging messages
	private Logger logger=null;

	//Postgres database connection
	private Connection conPostgres = null;

	//Postgres database conenction object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;

	//To check availabilty of extRef info in DB
	private boolean extRefExistInDB = false;

    /** Creates new LaboTestImageHandler */
    public LaboTestImageHandler(Properties laboTestParameter, Logger loggerLabo) {
		//copy labo test parameter to local property variable
		this.laboTestParameter=laboTestParameter;
		//copy main logger to local logger
		this.logger = loggerLabo;
		//Create new postgresConneciton object for database object
		postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(laboTestParameter);
    }

	/**
	 *
	 * constructExtRef(), Search for previous session's leftout files based on 'ImageStatus'<br>
	 * flag from 'Labo_Ext_Ref' table.(When extRef files were not received while<br>
	 * closing LaboTestReceiver)<br>
	 * if  'ImageStatus' flag is 'a' then constructs ExtRef object and stores in 'collectionOfExtRefs'<br>
	 * <br>
	 * This method is called from LaboTestReceiver.startReceiver()<br>
	 *
 	 */
	public boolean constructExtRef() throws SQLException{

		logger.finer("Method Entry");
		boolean constructExtRefReturn=false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		ExtRefs extRefs=null;

		//Get Postgres connection
		conPostgres=postgresConnection.acquirePostgresConnection();
		if (conPostgres == null) {
			logger.warning("Could not connect to Postgres database");
			constructExtRefReturn = false;
		}
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("select Href from Tbl_Labo_Ext_Ref where ImageStatus='a'");
			sql = buf.toString();
			logger.finer("SQL Statement: " + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);

				//If  previous session's leftout files avialble
				while( rs!=null && rs.next()) {

					if(rs.getString("Href") != null) {
						addExtRef(rs.getString("Href"));
						logger.finer("extRef files expected, extRef: "+rs.getString("Href"));
					}
				}
				constructExtRefReturn = true;
			}
			catch (Exception e) {
				logger.warning("Exception while calling constructExtRef()");
				logger.warning("Exception details:"  + e );
				constructExtRefReturn = false;
			}
			finally {
				if(rs!=null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
				if (conPostgres != null){
					postgresConnection.releasePostgresConnection(conPostgres);
					conPostgres = null;
				}
			}
		}
		logger.finer("Method Exit");
		return constructExtRefReturn;
	}

	/**
	 *
	 * handleUnreferencedExtRefFile(), Search for unreferenced files in 'Incoming' dir.<br>
	 *
	 * Files are having extension 'xml' or 'tmp' and expected extRef are skipped in this search<br>
	 * <br>
	 * This method is called from LaboTestReceiver.startReceiver()<br>
	 *
 	 */
	public boolean handleUnreferencedExtRefFile(){

		logger.finer("Method Entry");
		boolean unreferencedExtFileReturn = false;
		boolean foundReference = false;
		boolean foundXmlFile = false;
		String filesList[] = null;
		Vector unreferencedExtRefFile=null;
		File unreferencedExtFileRecDir;
		String laboTestIncomingDir=MML_REC_DEFAULT_DIR;
		String laboTestErrorFilesDir=ERROR_FILES_STORING_PATH;

		//check for above directories in INI file
		if (laboTestParameter != null && laboTestParameter.size() > 0 &&
													  laboTestParameter.containsKey("SourceDir") &&
													  laboTestParameter.getProperty("SourceDir") !=null ) {
			//Get mml incoming dir and store it in laboTestMmlRecDir
			laboTestIncomingDir=(String)laboTestParameter.getProperty("SourceDir");
		}
		else{
			logger.warning("Error in getting 'mml incoming dir' value from INI, taking default as: "+laboTestIncomingDir);
		}

		//check for above directories in INI file
		if (laboTestParameter != null && laboTestParameter.size() > 0 &&
													  laboTestParameter.containsKey("ErrorFilesDir") &&
													  laboTestParameter.getProperty("ErrorFilesDir") !=null ) {
			//Get mml incoming dir and store it in laboTestMmlRecDir
			laboTestErrorFilesDir=(String)laboTestParameter.getProperty("ErrorFilesDir");
		}
		else{
			logger.warning("Error in getting 'error files storing dir' value from INI, taking default as: "+laboTestErrorFilesDir);
		}

		//Checking file separator at the end of the below paths
		if(!laboTestIncomingDir.endsWith(File.separator)) {
			laboTestIncomingDir = laboTestIncomingDir + File.separator;
		}

		if(!laboTestErrorFilesDir.endsWith(File.separator)) {
			laboTestErrorFilesDir = laboTestErrorFilesDir + File.separator;
		}

		//Checking for file separator in the below paths
		laboTestIncomingDir = laboTestIncomingDir.replace('/',File.separatorChar);
		laboTestIncomingDir = laboTestIncomingDir.replace('\\', File.separatorChar);
		laboTestErrorFilesDir = laboTestErrorFilesDir.replace('/',File.separatorChar);
		laboTestErrorFilesDir = laboTestErrorFilesDir.replace('\\', File.separatorChar);

		//check whether laboTestMmlRecDir is exist
		unreferencedExtFileRecDir = new File(laboTestIncomingDir);
		if (unreferencedExtFileRecDir.exists() == false ||  unreferencedExtFileRecDir.isDirectory() == false ||
		                                                                     unreferencedExtFileRecDir.canRead() == false) {
		   logger.finer("Error in reading target directory, dir may not be exist,,"+ unreferencedExtFileRecDir.getAbsolutePath() );
		   unreferencedExtFileReturn = false;
		   return unreferencedExtFileReturn;
		}

		filesList = unreferencedExtFileRecDir.list();
		if((filesList == null) || ((filesList != null) && (filesList.length<1))){
			unreferencedExtFileReturn = true;
			return unreferencedExtFileReturn;
		}

		//Check whether any XML files available, if exist then skip this function
		//If XML file exist, it is assumed that these unreferenced files might be having reference in existing XML file
		for (int i=0; i < filesList.length; i++) {
			foundXmlFile=false;
			if (filesList[i].toLowerCase().endsWith("xml") == true){
				unreferencedExtFileReturn = false;
				logger.finer("Found XML file, hence abort checking for unreferenced files");
				foundXmlFile=true;
				break;
			}
		}
		if(foundXmlFile){
			unreferencedExtFileReturn = true;
			return unreferencedExtFileReturn;
		}

		//Get files list wherefiles extension is not 'tmp' file
		//It is assumed that files extension will be 'tmp' while receiving xml/image from Lab.
		for (int i=0; i < filesList.length; i++) {
			foundReference=false;
			// case insensitive
			if (filesList[i].toLowerCase().endsWith("tmp")) {
				continue;
			}

			File file = new File(unreferencedExtFileRecDir.getPath(),filesList[i]);
			//File file = new File(filesList[i]);
			if (file.isFile() == false) {
				// target object is not a file,  skip it
				continue;
			}

			//Check whether found file has reference in collectionOfExtRefs

			if(collectionOfExtRefs != null && collectionOfExtRefs.size()>0){
				ExtRefs extRefsOut = new ExtRefs();
				for(int k=0; k<collectionOfExtRefs.size(); k++) {
					extRefsOut = (ExtRefs)collectionOfExtRefs.elementAt(k);
					if (extRefsOut !=null && extRefsOut.getExtRefName() != null){
						if(extRefsOut.getExtRefName().equalsIgnoreCase(file.getName())){
							foundReference = true;
							break;
						}
					}
				}
			}
			//If this unreferenced file has any reference then do not include in unreferencedExtRefFile vector for moving
			if(foundReference){
				continue;
			}

			logger.finer("Found unreferenced file: " + file.getName());
			if(unreferencedExtRefFile == null) {
				unreferencedExtRefFile = new Vector();
			}
			unreferencedExtRefFile.addElement(file);
		}

		//Get one by one files from vector unreferencedExtRefFile and move to laboTestErrorFilesDir

		if(unreferencedExtRefFile!=null ){
			while (unreferencedExtRefFile.size() > 0) {
				File unreferencedFile = (File)unreferencedExtRefFile.elementAt(0);
				if(unreferencedFile.isFile()){
					unreferencedExtFileReturn=moveUnreferencedFile(unreferencedFile,laboTestErrorFilesDir);
					if(!unreferencedExtFileReturn){
						logger.warning("Couldn't move unreferenced file: " + unreferencedFile.getAbsolutePath());
					}
				}
				//Remove the reference from Vector
				unreferencedExtRefFile.removeElementAt(0);
			}
		}
		logger.finer("Method Exit");
		return unreferencedExtFileReturn;
	}

	/**
	 *
	 * moveUnreferencedFile(), moves unreferenced files from incoming dir to<br>
	 * Error Files dir. Error Dir is created based on the date if doesn't exist<br>
	 * <br>
	 * This method is called from handleUnreferencedExtRefFile()<br>
	 *
 	 */
	private boolean moveUnreferencedFile(File unreferencedSrcFile, String laboTestErrorFilesDir){
		logger.finer("Method Entry");
		boolean moveUnreferencedFileReturn=false;
		String laboTestErrorFilesDirPath;

		//Creating dir based on date under unreferenced folder(laboTestErrorFilesDir).
		java.util.Date dateMills = new java.util.Date(System.currentTimeMillis());
		java.sql.Date  date = new java.sql.Date(dateMills.getTime());
		String todayDate = date.toString();

		//Concate with today's date
		laboTestErrorFilesDir = laboTestErrorFilesDir + todayDate+ File.separator;
		//Check whether the above dir is available for moving files, if no then create
		try{
			File newDestDir = new File(laboTestErrorFilesDir);
			if (newDestDir.exists() == false) {
				logger.finer("unreferenced files dir doesn't exist, creating: "+ laboTestErrorFilesDir);
				if (newDestDir.mkdirs() == true) {
					if (newDestDir.isDirectory() == false || newDestDir.canRead() == false) {
						logger.warning("Error in reading unreferenced files dir "+ laboTestErrorFilesDir);
						return moveUnreferencedFileReturn;
					}
				}
				else{
					logger.warning("Error in creating unreferenced files dir "+ laboTestErrorFilesDir);
					return moveUnreferencedFileReturn;
				}
			}
		}
		catch (Exception e) {
			logger.warning("Exception while calling moveUnreferencedFile()");
			logger.warning("Exception details:"  + e.getMessage());
		}

		//Concate unreferenced file name with newly created dir
		laboTestErrorFilesDirPath = laboTestErrorFilesDir + unreferencedSrcFile.getName();

		File unreferencedDestFile = new File(laboTestErrorFilesDirPath);

		if(unreferencedSrcFile.isFile()){
			try{
				moveUnreferencedFileReturn=unreferencedSrcFile.renameTo(unreferencedDestFile);
				if (!moveUnreferencedFileReturn) {
						logger.warning("Couldn't move unreferenced file: " + unreferencedSrcFile.getAbsolutePath());
						moveUnreferencedFileReturn=false;
					}
				else{
					logger.finer("Successfuly moved " + unreferencedDestFile.getAbsolutePath());
					moveUnreferencedFileReturn=true;
				}
			}
			catch (Exception e) {
				logger.warning("Exception while moving unreferenced file "  +  unreferencedSrcFile.getAbsolutePath());
				logger.warning( "Exception details:"  + e.getMessage());
				moveUnreferencedFileReturn=false;
			}
		}
		else{
			logger.warning("Not a valid file for moving into unreferenced dir "+ unreferencedSrcFile.getAbsolutePath() );
			moveUnreferencedFileReturn=false;
		}
		logger.finer("Method Exit");
		return moveUnreferencedFileReturn;
	}

	/**
	 *
	 * addExtRef(), adds extRef to ExtRef object and stores in 'collectionOfExtRefs''<br>
	 *
	 * This method is called from LaboTestProcessor.moveFile() and constructExtRef()<br>
	 *
 	 */
    public boolean addExtRef(String extRef){
		logger.finer("Method Entry");

		//If it is not yet created
		if(collectionOfExtRefs == null){
			collectionOfExtRefs =  new Vector();
		}
		ExtRefs extRefs = new ExtRefs();
		extRefs.setExtRefName(extRef);
		collectionOfExtRefs.addElement(extRefs);
		logger.finer("extRef is added in collectionOfExtRefs , extRef: "+ extRefs.getExtRefName() );

		logger.finer("Method Exit");
		return true;
	}

	/**
	 *
	 * checkExtRef(), checks for external reference file stored in collectionOfExtRefs peiodically<br>
	 * if extref file avilabe then moves to external reference folder and updates 'ImageStatus'<br>
	 * flag of  'Labo_Ext_Ref' table<br>
	 *
	 * If extref file is not yet received and value of timeout counter is less than pre-defined<br>
	 * then advances the timeout counter in the object. else(timeout), update 'ImageStatus'<br>
	 * as 'e' and delete corresponding information in collectionOfExtRefs.
	 *
	 * This method is called from LaboTestProcessor.WatchingThread(run)()<br>
	 *
 	 */
	public boolean checkExtRef() throws SQLException{
		logger.finer("Method Entry");
		boolean checkExtRefReturn=false;
		final  String EXT_REF_FILE_RECD ="y";
		final  String EXT_REF_FILE_REC_ERROR ="e";

		try {
			if(collectionOfExtRefs!=null && collectionOfExtRefs.size()> 0 ){
				ExtRefs extRefsOut = new ExtRefs();
				for(int i=0; i<collectionOfExtRefs.size();) {
					//Get Postgres connection first time
					if (conPostgres == null) {
						conPostgres=postgresConnection.acquirePostgresConnection();
					}
					extRefsOut = (ExtRefs)collectionOfExtRefs.elementAt(i);
					if (extRefsOut !=null && extRefsOut.getExtRefName() != null){
						//Though the extref is available in collectionOfExtRefs, we need to check whether it is available in DB
						//This check is required during db error (after inserting into labo_ext_ref, db would have rolled back)
						checkExtRefReturn = isExtRefExistInDB(extRefsOut.getExtRefName());
						if( !extRefExistInDB){
							//Delete this extRefsOut from collectionOfExtRefs
							logger.finer("extRef data not avilable in DB for: " + extRefsOut.getExtRefName()
							                                                                     + ", Hence This ExtRefs shall be deleted from collectionOfExtRefs");
							collectionOfExtRefs.remove(i);
						}
						else{
							checkExtRefReturn=moveExtRef(extRefsOut.getExtRefName());

							//Moving extRef is successful
							if(checkExtRefReturn){
								checkExtRefReturn=updateExtRefResult(extRefsOut.getExtRefName(),EXT_REF_FILE_RECD);
								if(checkExtRefReturn){
									logger.finer("Successfuly updated extRef file received result for:  " + extRefsOut.getExtRefName());
									//delete this object reference from collectionOfExtRefs
									collectionOfExtRefs.remove(i);
								}
								else{
									logger.warning("Could not update extRef file received result for: "+ extRefsOut.getExtRefName() );
									//Advance the index of collectionOfExtRefs
									i++;
								}
							}
							// Moving extRef is error (extRef file might not have come or error in moving).
							else{
								//Check for timeout, if true then update database and delete this node from collectionOfExtRefs
								int extRefCounter = extRefsOut.getExtRefCounter();

								int extRefTimout  =EXT_REF_TIMEOUT_RETRY;
								//check for this item in INI file
								if (laboTestParameter != null && laboTestParameter.size() > 0 &&
																				 laboTestParameter.containsKey("ExtRefTimeout") &&
																				 new Integer(laboTestParameter.getProperty("ExtRefTimeout")).intValue() >0) {
									//Get watch frequence and store it in laboTestFrequency
									extRefTimout =(new Integer(laboTestParameter.getProperty("ExtRefTimeout")).intValue());
								}
								else{
									logger.warning("Error in getting 'extRef receiving retry time' value from INI, taking default as: "+extRefTimout+" MiliSeconds");
								}
								if(extRefCounter >= extRefTimout){
									checkExtRefReturn=updateExtRefResult(extRefsOut.getExtRefName(),EXT_REF_FILE_REC_ERROR);
									if(checkExtRefReturn){
										logger.warning("ExtRef Image did not come with in pre-defined time: " + extRefsOut.getExtRefName());
										logger.finer("Successfuly updated extRef file not received result for:  " + extRefsOut.getExtRefName());
										//delete this object reference from collectionOfExtRefs
										collectionOfExtRefs.remove(i);
									}
									else{
										logger.warning("Could not update extRef file not received result for: "+ extRefsOut.getExtRefName());
										//Advance the index of collectionOfExtRefs
										i++;
									}
								}
								//advance the timeout checking counter in object and update collectionOfExtRefs
								else{
									extRefsOut.setExtRefCounter((extRefsOut.getExtRefCounter() + 1));
									collectionOfExtRefs.setElementAt(extRefsOut,i);
									logger.finer("No time out error for:  " + extRefsOut.getExtRefName()
																						  + " timout check counter advanced to " + extRefsOut.getExtRefCounter());
									//Advance the index of collectionOfExtRefs
									i++;
								}
							}
						}
					}
					else{
						// If extRefsOut object is null or extRefsOut.getExtRefName() returns null then remove
						// that object in collectionOfExtRefs
						collectionOfExtRefs.remove(i);
					}
				}
				//Disconnect postgres conneciton
				if (conPostgres != null){
					postgresConnection.releasePostgresConnection(conPostgres);
					conPostgres = null;
				}
			}
		}
		catch(Exception e){
			logger.warning("Exception in  checkExtRef()");
			logger.warning( "Exception details:"  + e);
			checkExtRefReturn=false;
		}
		logger.finer("Method Exit");
		return checkExtRefReturn;
	}

	/**
	 *
	 * isExtRefExistInDB(), searches whether extref is available in DB<br>
	 * Gets 'href' from called method<br>
	 *
	 * This method is called from checkExtRef()<br>
	 *
 	 */
	private boolean isExtRefExistInDB(String href) throws SQLException{
		logger.finer ("Method Entry");
		boolean isExtRefExistInDBReturn = false;
		Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;
		extRefExistInDB = false;

		//Get Postgres connection (if is disconnected)
		if (conPostgres == null) {
			conPostgres=postgresConnection.acquirePostgresConnection();
		}
		if (conPostgres == null) {
			logger.warning("Could not connect to Postgres database");
			isExtRefExistInDBReturn = false;
		}
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("select count(*) as NumberOfRows from Tbl_Labo_Ext_Ref where Href=");
			buf.append(postgresConnection.addSingleQuote(href));
			buf.append(" and ImageStatus ='a'");
			sql = buf.toString();
			logger.finer("SQL Statement: "+ sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);
				if (rs.next()) {
					if ( (rs.getInt("NumberOfRows") > 0) ) {
						extRefExistInDB=true;
						isExtRefExistInDBReturn = true;
						logger.finer("extRef data avilable in DB for: " + href);
					}
				}
				else{
					isExtRefExistInDBReturn = true;
				}
			}
			catch (SQLException e) {
				logger.warning("Exception while getting extRef infro from Tbl_Labo_Ext_Ref");
				logger.warning( "Exception details:"  + e );
				isExtRefExistInDBReturn = false;
			}
			finally {
				if(rs !=null){
					rs.close();
				}
				if(st != null) {
					st.close();
				}
			}
		}
		logger.finer ("Method Exit");
		return isExtRefExistInDBReturn;
	}

	/**
	 *
	 * moveExtRef(), gets extRef file to be moved <br>
	 * Gets the source/destination path from 'laboTestParameter'<br>
	 *
	 * Based on the above path info, this function checks for the existance of source file<br>
	 * If source file exist then, same is moved into destination directory<br>
	 *
	 * This method is called from checkExtRef()<br>
	 *
 	 */
	private boolean moveExtRef(String extRef){
		logger.finer("Method Entry");
		boolean moveExtRefReturn=false;

		String extRefSrcDir =MML_REC_DEFAULT_DIR;
		String extRefDestDir =EXTREF_REC_DEFAULT_DIR;

		//check for above directories in INI file
		if (laboTestParameter != null && laboTestParameter.size() > 0 &&
													  laboTestParameter.containsKey("SourceDir") &&
													  laboTestParameter.getProperty("SourceDir") !=null ) {
			//Get extRef incoming dir and store it in extRefSrcDir
			extRefSrcDir=(String)laboTestParameter.getProperty("SourceDir");
		}
		else{
			logger.warning("Error in getting 'mml incoming dir' value from INI, taking default as: "+extRefSrcDir);
		}
		if (laboTestParameter != null && laboTestParameter.size() > 0 &&
													  laboTestParameter.containsKey("ExtRefDir") &&
													  laboTestParameter.getProperty("ExtRefDir") !=null ) {
			//Get extRef moving directory and store it in extRefDestDir
			extRefDestDir=(String)laboTestParameter.getProperty("ExtRefDir");
		}
		else{
			logger.warning("Error in getting 'extRef dir' value from INI, taking default as: "+extRefDestDir);
		}
		//Checking file separator at the end of the below paths
		if(!extRefSrcDir.endsWith(File.separator)) {
			extRefSrcDir = extRefSrcDir + File.separator;
		}

		if(!extRefDestDir.endsWith(File.separator)) {
			extRefDestDir = extRefDestDir + File.separator;
		}

		//Checking for file separator in the below paths
		extRefSrcDir = extRefSrcDir.replace('/',File.separatorChar);
		extRefSrcDir = extRefSrcDir.replace('\\', File.separatorChar);
		extRefDestDir = extRefDestDir.replace('/',File.separatorChar);
		extRefDestDir = extRefDestDir.replace('\\', File.separatorChar);

		File extRefSrcPath  = new File (extRefSrcDir   + extRef );
		File extRefDestPath = new File (extRefDestDir + extRef );

		logger.finer("extRef file source path: " + extRefSrcPath.getAbsolutePath() );
		logger.finer("extRef file destination path: " + extRefDestPath.getAbsolutePath() );

		if(!extRefSrcPath.isFile()){
			logger.warning("extRef file not yet received:  " + extRef );
			moveExtRefReturn=false;
		}
		else{

			// move extRef file from source dir to extRefs rir
			try {
				moveExtRefReturn = extRefSrcPath.renameTo(extRefDestPath);
				if (!moveExtRefReturn) {
					logger.warning("Couldn't move extRef file: " + extRef);
					moveExtRefReturn=false;
				}
				else{
					moveExtRefReturn=true;
				}
			}
			catch (Exception e) {
				logger.warning("Exception while moving extRef file"  + extRef);
				logger.warning( "Exception details:"  + e);
				moveExtRefReturn=false;
			}
		}
		logger.finer("Method Exit");
		return moveExtRefReturn;
	}

	/**
	 *
	 * updateExtRefResult(), gets DB connection, extref file name and flag value <br>
	 *
	 * Based on the above information this method updats corresponding database record's 'ImageStatus' flag<br>
	 *
	 * This method is called from checkExtRef()<br>
	 *
 	 */
	private boolean updateExtRefResult(String href, String flagImageStatus) throws SQLException{
		logger.finer ("Method Entry");
		boolean updateExtRefResultReturn=false;
		Statement st = null;
		// Constructing sql
		StringBuffer buf = null;;
		//Get Postgres connection (if is disconnected)
		if (conPostgres == null) {
			conPostgres=postgresConnection.acquirePostgresConnection();
		}
		if (conPostgres == null) {
			logger.warning("Could not connect to Postgres database");
			updateExtRefResultReturn = false;
		}
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("update Tbl_Labo_Ext_Ref set ImageStatus=");
			buf.append(postgresConnection.addSingleQuote(flagImageStatus));

			buf.append(" where Href=");
			buf.append(postgresConnection.addSingleQuote(href));

			String sql = buf.toString();
			logger.finer("SQL Statement: "+ sql);
			try {
				st = conPostgres.createStatement();
				st.executeUpdate(sql);
				updateExtRefResultReturn=true;
			}
			catch (SQLException e) {
				logger.warning("Exception while updating ExtRef result in Tbl_Labo_Ext_Ref");
				logger.warning( "Exception details:"  + e );
				updateExtRefResultReturn = false;
			}
			finally {
				if(st != null) {
					st.close();
				}
			}
		}
		logger.finer("Method Exit");
		return updateExtRefResultReturn;
	}

	/**
	 *
	 * This class stores ExtRefs name and counter(timeout) <br>
	 *
 	 */
	private class ExtRefs {

      	private String 	extRefName;
		private int			extRefCounter=0;

		//Creating new ExtRefs;
      	public ExtRefs() {

        }

		public void setExtRefName(String href){
			this.extRefName=href;
		}

		public String getExtRefName(){
			return this.extRefName;
		}

		public void setExtRefCounter(int extRefCtr){
			this.extRefCounter=extRefCtr;
		}

		public int getExtRefCounter(){
			return this.extRefCounter;
		}
	}
}