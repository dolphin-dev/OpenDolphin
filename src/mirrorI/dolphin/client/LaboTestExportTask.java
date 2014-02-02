/*
 * LaboTestExportTask.java
 *
 * Created on 2003/01/27
 *
 * Last updated on 2003/02/28
 *
 */

package mirrorI.dolphin.client;

import swingworker.*;
import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author  Prasahnth Kumar, Mirror-I Corp.
 *
 * This class gets the corresponding test reults from Labo_Module, Labo_Specimen and Labo_Item and writes into selected file
 *
 */
public class LaboTestExportTask {
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;

    private FileWriter fw = null;

    private String patientId = null;
    private boolean isLocalId = false;
    private String fromDate = null;
    private String toDate = null;

    //private static Logger logger ;
    private Properties laboTestParameter;

    //Postgres database conenction object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection;

    /** Creates new LaboTestExportTask */
    public LaboTestExportTask(FileWriter fw,String patientId, boolean isLocalId,String fromDate, String toDate,
    									   Properties laboTestParameter) {

    	this.laboTestParameter = laboTestParameter;
       	this.fw = fw;
		this.patientId = patientId;
		this.isLocalId = isLocalId;
		this.fromDate = fromDate;
        this.toDate = toDate;
        //this.logger = logger;

		//Compute length of task...
        lengthOfTask = 1000;

		postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(laboTestParameter);
    }

	/**
	 *
	 * go(), cretes new object of  'ActualTask' class to start the export task<br>
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    void go() {
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

	/**
	 *
	 * getLengthOfTask(), returns information about how much task needs to be done<br>
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    int getLengthOfTask() {
        return lengthOfTask;
    }

	/**
	 *
	 * getCurrent(), returns hou much task has been completed<br>
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    int getCurrent() {
        return current;
    }

	/**
	 *
	 * stop(), makes lengthOfTaks to current<br>
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    void stop() {
        current = lengthOfTask;
    }

	/**
	 *
	 * done(), returns true when search task is completed and returns else when<br>
	 * search task is not completed.
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

 	/**
	 *
	 * getMessage(), returns the present message to LaboTestBean to display<br>
	 * <br>
	 * This method is called from LaboTestBean.btnExportActionPerformed()<br>
	 *
 	 */
    String getMessage() {
        return statMessage;
    }

 	/**
	 *
	 * ActualTask(), gets corresponding test result from Labo_Module, Labo_Specimen and Labo_Item<br>
	 * Writes the retrived information into file.
	 * <br>
	 * This class is initiiated from go()<br>
	 *
 	 */
    class ActualTask {

		private Connection conPostgres=null;
		private boolean actualTaskReturn=false;
		private boolean databaseError = false;
		private boolean ioError = false;
		private boolean dataAvilable = false;

        ActualTask() {

            //Check any of the parameter related to DB connection is null
            if ( (laboTestParameter == null) || (laboTestParameter.size() <= 0) || (laboTestParameter.getProperty("Driver") == null) ||
                 (laboTestParameter.getProperty("Host") == null) || (new Integer(laboTestParameter.getProperty("Port")).intValue() <= 0) ||
                 (laboTestParameter.getProperty("DBName") == null) ||  (laboTestParameter.getProperty("DBUser") == null)) {

                statMessage = "サーバー設定の取得に失敗しました。";
                //logger.warning("Conenction to Postgres DB is failed, connection parameter is null, please check INI file");
                stop();
                return;
            }

            statMessage = "サーバーへ接続中...";

			//Get Postgres Conenction
            conPostgres =postgresConnection.acquirePostgresConnection();
            //If Getting conenction is unsuccessful
            if (conPostgres == null) {
                statMessage = "サーバー接続に失敗しました。";
                //logger.warning("Couldn't get Postgres DB conenction, please check DB status and INI file");
                stop();
                return;
            }

			// output Labo Modules which have laboSampleTime between fromDate and toDate.
			try{
				actualTaskReturn = outputLaboModules(conPostgres, fw, patientId, isLocalId, fromDate, toDate);

				if (!actualTaskReturn) {
					//Disconenct DB
					if(conPostgres != null){
						postgresConnection.releasePostgresConnection(conPostgres);
						conPostgres = null;
					}
					if(databaseError){
						statMessage = "該当するデータを受取るに失敗しました。";
						//logger.warning("Error in getting corresponding data from DB");
					}
					else if(ioError){
						statMessage = "ファイル書き出しに失敗しました。";
						//logger.warning("Error in outputing into file");
					}
					else{
						statMessage = "該当するデータはありません。";
						//logger.finer("Corresponding Data is not avilable");
					}
					stop();
					return;
				}
				//On Successfuly getting required data, diconnect DB and return message
				else{
				   //Disconenct DB
					if(conPostgres != null){
						postgresConnection.releasePostgresConnection(conPostgres);
						conPostgres = null;
					}
					// notify the successful message
					// NOTE: it is very important that this message is received by the
					// searchTimer in LaboTestBean to check the result of this ActualTask
					if(dataAvilable){
						statMessage = "ファイル書き出しが終了しました。";
						//logger.finer("Successfuly written into file");
					}
					else{
						statMessage = "該当するデータはありません。";
						//logger.finer("Corresponding Data is not avilable");
					}
					stop();
					return;
				}
			}
			catch (Exception e) {
				statMessage = "ファイル書き出しに失敗しました。";
				//logger.warning("Exception while calling outputLaboModules()");
				//logger.warning( "Exception details:"  + e );
				stop();
				return;
			}
        }

		/**
		 *
		 * outputLaboModules(), gets corresponding test result from Labo_Module<br>
		 * writes the retrived information in selected file<br>
		 * Calls outputLaboTests() to get Labo_Specimen data for each record in Labo_Module<br>
		 * <br>
		 * This method is called  from 'ActualTask' class<br>
		 *
		 */
        public boolean outputLaboModules(Connection conPostgres, FileWriter fw, String patientId, boolean isLocalId,
        												 String fromDate, String toDate) throws SQLException, IOException {
			//logger.finer ("Method Entry");
			boolean outputLaboModulesReturn = false;
			String searchPatientID = null;
			Statement st = null;
			StringBuffer buf = null;
			String sql=null;
			ResultSet rs = null;
			databaseError = false;
			ioError=false;

			String dbFieldName[] = {"SampleTime","MmlConfirmDate","TestReporterName","CreatorLicense","TestCenterID","TestCenterName",
									         "TestDeptId","TestDeptName","Address","Email","Phone","RegistId","RegistTime","ReportTime",
									         "ReportStatus","RepMemo","RepMemoCodeName","RepMemoCode","RepMemoCodeId","RepFreeMemo",
									         "SetCode","SetCodeId","Set"};

			String[] outputFieldName = { "検体採取日時","確定日","報告者","報告者資格","検査実施施設ＩＤ","検査実施施設",
                                                    "検査実施施設部署ＩＤ","検査実施施設部署","住所","電子メール","電話番号",
                                                    "登録ＩＤ","登録日時","報告日時","報告状態","報告メモ","報告メモコード名","報告メモコード",
                                                    "報告メモコードＩＤ","報告自由メモ","セットコード","セットテーブル","セット名"};

			//Check whether DB connection is still avialble, if not get connection once again
			if(conPostgres == null){
				conPostgres = postgresConnection.acquirePostgresConnection();
			}
			//Check whether any of the received parameter is null, if so return false
			if (conPostgres == null || patientId == null  || fromDate == null || toDate == null){
				outputLaboModulesReturn = false;
				//logger.warning("Either Postgres connection or Patien ID or Search period is null");
			}
			else{
				if (isLocalId ) {
					searchPatientID = "local" +  "__"  + patientId.replaceAll(",","");
				}
				else{
					searchPatientID = "facility" +  "__"  + patientId.replaceAll(",","");
				}
				buf = new StringBuffer();
				buf.append("Select UID, SampleTime, MmlConfirmDate, TestReporterName, CreatorLicense,TestCenterID,TestCenterName, ");
				buf.append("TestDeptId, TestDeptName, Address, Email, Phone, RegistId, RegistTime, ReportTime, ReportStatus, RepMemo, ");
				buf.append("RepMemoCodeName, RepMemoCode,RepMemoCodeId, RepFreeMemo, ");
				buf.append("SetCode, SetCodeId, Set from Tbl_Labo_Module where patientID=");
				buf.append(postgresConnection.addSingleQuote(searchPatientID));
				buf.append(" and  SampleTime >=");
				buf.append(postgresConnection.addSingleQuote(fromDate + "T00:00:00"));
				buf.append(" and SampleTime <=");
				buf.append(postgresConnection.addSingleQuote(toDate + "T23:59:59"));
				buf.append(" order by SampleTime,RegistId asc");

				//Convert into string
				sql = buf.toString();
				//logger.finer("Sql Statement: " + sql);
				try {
					statMessage = "データ検索中...";
					st = conPostgres.createStatement();
					rs = st.executeQuery(sql);
					// This patient's labo test records exist
					while (rs.next()) {
						//To send proper message to LaboTestBean
						dataAvilable=true;

						//Get related value from DB and output into file
						for (int i = 0; i < dbFieldName.length; i++) {
							if(rs.getString(dbFieldName[i]) != null) {
								fw.write( outputFieldName[i] + "," + rs.getString(dbFieldName[i]) + "\n");
							}
							else{
								fw.write( outputFieldName[i] + "," + "\"\"" + "\n");
							}
						}

						fw.flush();
						//Output ExtRef and LaboTest
						if (rs.getString("UID") != null) {
							outputLaboModulesReturn = outputExtRef(conPostgres, rs.getString("UID"), fw);

							//If outputExtRef() returns flase, exit while loop and return false to called method
							if(!outputLaboModulesReturn){
								//logger.warning("outputExtRef() returns false, outputing labo modules will be aborted");
								break;
							}
							//output Labo Test results
							outputLaboModulesReturn = outputLaboTests(conPostgres, rs.getString("UID"), fw);

							//If outputing LaboTests/ Items function returns flase, exit while loop and return false to called method
							if(!outputLaboModulesReturn){
								//logger.warning("outputLaboTests() returns false, outputing labo modules will be aborted");
								break;
							}
						}
						if(!outputLaboModulesReturn){
							//logger.warning("outputExtRef() or outputLaboTests() returns false, outputing labo modules will be aborted");
							break;
						}
					}
				}
				catch (SQLException sqle) {
					//logger.warning("SQL Exception while getting date from labo_modue");
					//logger.warning( "Exception details:"  + sqle );
					outputLaboModulesReturn = false;
					databaseError = true;
				}
				catch (IOException io) {
					//logger.warning("IO Exception while exporting labo_module");
					//logger.warning( "Exception details:"  + io );
					outputLaboModulesReturn = false;
					ioError = true;
				}
				catch (Exception e) {
					//logger.warning("Exception while exporting labo_module");
					//logger.warning( "Exception details:"  + e );
					outputLaboModulesReturn = false;
					ioError = true;
				}
				finally{
					if(rs !=null){
						rs.close();
					}
					if(st != null) {
						st.close();
					}
				}
			}
			//logger.finer ("Method Exit");
			return outputLaboModulesReturn;
        }

		/**
		 *
		 * outputExtRef(), gets corresponding test result from Labo_Ext_Ref<br>
		 * writes the retrived information in selected file<br>
		 * <br>
		 * This method is called from outputLaboModules()<br>
		 *
		 */
		private  boolean outputExtRef(Connection conPostgres, String laboModuleUID, FileWriter fw)  throws SQLException, IOException {
			//logger.finer ("Method Entry");
			boolean outputExtRefReturn = false;
			Statement st = null;
			StringBuffer buf = null;
			String sql=null;
			ResultSet rs = null;
			databaseError = false;
			ioError=false;
			//To store extRef status if avialble (Received, receiving error, yet to receive)
			int extRefRecd = 0;
			int extRefRecErr = 0;
			int extRefWaiting = 0;
			String extRef ="外部参考イメージ受付";
			String extRefTextRecd =   "完了";
			String extRefTextErr =      "エラー";
			String extRefTextWating = "中";
			String extRefEndText="枚";

			//Check whether DB connection is still avialble, if not get connection once again
			if(conPostgres == null){
				conPostgres = postgresConnection.acquirePostgresConnection();
			}
			//Check whether any of the received parameter is null, if so return false
			if (conPostgres == null || laboModuleUID == null || fw == null){
				outputExtRefReturn = false;
				//logger.warning("Either Postgres connection or laboModuleUID or output file handler is null");
			}
			else{
				buf = new StringBuffer();
				buf.append("Select ImageStatus from Tbl_Labo_Ext_Ref where LaboModuleUID= ");
				buf.append(postgresConnection.addSingleQuote(laboModuleUID));
				buf.append(" order by UID asc");

				//Convert into string
				sql = buf.toString();
				//logger.finer("Sql Statement: " + sql);
				try {
					statMessage = "データ検索中...";
					st = conPostgres.createStatement();
					rs = st.executeQuery(sql);
					// ExtRef data Available
					while (rs.next()) {
						if (rs.getString("ImageStatus") != null) {
							//Received
							if(rs.getString("ImageStatus").equalsIgnoreCase("y") ){
								extRefRecd++;
							}
							//Receiving Error
							else if(rs.getString("ImageStatus").equalsIgnoreCase("e") ){
								extRefRecErr++;
							}
							//Waiting
							else if(rs.getString("ImageStatus").equalsIgnoreCase("a") ){
								extRefWaiting++;
							}
							//Invalid
							else{
								//logger.warning("Invalid ExtRef's ImageStatus" +rs.getString("ImageStatus") );
							}
						}
					}
					//Set if extRef data available
					if(extRefRecd>0) {
						//Write into file for received extref
						fw.write(extRef +extRefTextRecd + ","+extRefRecd+extRefEndText + "\n");
					}
					else{
						fw.write(extRef +extRefTextRecd + "," + "\"\"" + "\n");
					}
					if(extRefRecErr>0) {
						//Write into file for error extref
						fw.write(extRef +extRefTextErr+ ","+extRefRecErr+extRefEndText + "\n");
					}
					else{
						fw.write(extRef +extRefTextErr + "," + "\"\"" + "\n");
					}
					if(extRefWaiting>0) {
						//Write into file for waiting extref
						fw.write(extRef +extRefTextWating + ","+extRefWaiting+extRefEndText + "\n");
					}
					else{
						fw.write(extRef +extRefTextWating + "," + "\"\"" + "\n");
					}
					fw.flush();
					outputExtRefReturn = true;
				}
				catch (SQLException sqle) {
					//logger.warning("SQL Exception while getting date from labo_ext_ref");
					//logger.warning( "Exception details:"  + sqle );
					outputExtRefReturn = false;
					databaseError = true;
				}
				catch (IOException io) {
					//logger.warning("IO Exception while exporting labo_ext_ref");
					//logger.warning( "Exception details:"  + io );
					outputExtRefReturn = false;
					ioError = true;
				}
				catch (Exception e) {
					//logger.warning("Exception while exporting labo_ext_ref");
					//logger.warning( "Exception details:"  + e );
					outputExtRefReturn = false;
					ioError = true;
				}
				finally{
					if(rs !=null){
						rs.close();
					}
					if(st != null) {
						st.close();
					}
				}
			}
			return outputExtRefReturn;
        }

		/**
		 *
		 * outputLaboTests(), gets corresponding test result from Labo_Specimen<br>
		 * writes the retrived information in selected file<br>
		 * Calls outputLaboItems() to get Labo_Item data for each record in Labo_Specimen<br>
		 * <br>
		 * This method is called from outputLaboModules()<br>
		 *
		 */
		private  boolean outputLaboTests(Connection conPostgres, String laboModuleUID, FileWriter fw)  throws SQLException, IOException {
			//logger.finer ("Method Entry");
			boolean outputLaboTestsReturn = false;
			Statement st = null;
			StringBuffer buf = null;
			String sql=null;
			ResultSet rs = null;
			databaseError = false;
			ioError=false;
			String dbFieldName[] = {"SpecimenName","SpecimenCode","SpecimenCodeId","SpcMemo","SpcMemoCodeName",
									         "SpcMemoCode", "SpcMemoCodeId","SpecimenFreeMemo"};

			String outputFieldName[] = {"検体","検体コード","検体テーブル","検体メモ","検体メモコード名","検体メモコード",
									               "検体メモコード名ＩＤ","検体自由メモ"};


			//Check whether DB connection is still avialble, if not get connection once again
			if(conPostgres == null){
				conPostgres = postgresConnection.acquirePostgresConnection();
			}
			//Check whether any of the received parameter is null, if so return false
			if (conPostgres == null || laboModuleUID == null || fw == null){
				outputLaboTestsReturn = false;
				//logger.warning("Either Postgres connection or laboModuleUID or output file handler is null");
			}
			else{
				buf = new StringBuffer();
				buf.append("Select UID, SpecimenName, SpecimenCode, SpecimenCodeId, SpcMemo, SpcMemoCodeName, ");
				buf.append("SpcMemoCode, SpcMemoCodeId, SpecimenFreeMemo from Tbl_Labo_Specimen where LaboModuleUID= ");
				buf.append(postgresConnection.addSingleQuote(laboModuleUID));
				buf.append(" order by SpecimenCodeId, SpecimenCode asc");

				//Convert into string
				sql = buf.toString();
				//logger.finer("Sql Statement: " + sql);
				try {
					statMessage = "データ検索中...";
					st = conPostgres.createStatement();
					rs = st.executeQuery(sql);
					// This patient's labo test records exist
					while (rs.next()) {
						//To send proper message to LaboTestBean
						dataAvilable=true;
						//Get related value from DB and output into file
						for (int i = 0; i < dbFieldName.length; i++) {
							if(rs.getString(dbFieldName[i]) != null) {
								fw.write( outputFieldName[i] + "," + rs.getString(dbFieldName[i]) + "\n");
							}
							else{
								fw.write( outputFieldName[i] + "," + "\"\"" + "\n");
							}
						}
						fw.flush();

						//Get Labo Items for this specimen
						if (rs.getString("UID") != null) {
							outputLaboTestsReturn =outputLaboItems(conPostgres, rs.getString("UID"), fw);
						}
						//If Outputing labo  Items function returns flase, exit while loop and return false to called method
						if(!outputLaboTestsReturn){
							break;
						}
					}
				}
				catch (SQLException sqle) {
					//logger.warning("SQL Exception while getting date from Labo_Specimen");
					//logger.warning( "Exception details:"  + sqle );
					outputLaboTestsReturn = false;
					databaseError = true;
				}
				catch (IOException io) {
					//logger.warning("IO Exception while exporting labo_specimen");
					//logger.warning( "Exception details:"  + io );
					outputLaboTestsReturn = false;
					ioError = true;
				}
				catch (Exception e) {
					//logger.warning("Exception while exporting labo_specimen");
					//logger.warning( "Exception details:"  + e );
					outputLaboTestsReturn = false;
					ioError = true;
				}
				finally{
					if(rs !=null){
						rs.close();
					}
					if(st != null) {
						st.close();
					}
				}
			}
			return outputLaboTestsReturn;
        }

		/**
		 *
		 * outputLaboItems(), gets corresponding test result from Labo_Item<br>
		 * writes the retrived information in selected file<br>
		 * <br>
		 * This method is called from outputLaboTests()<br>
		 *
		 */
		private boolean outputLaboItems(Connection conPostgres, String laboSpecimenUID, FileWriter fw)  throws SQLException, IOException {
			//logger.finer ("Method Entry");
			boolean  outputLaboItemsReturn = false;
			Statement st = null;
			StringBuffer buf = null;
			String sql=null;
			ResultSet rs = null;
			databaseError = false;
			ioError=false;

			String dbFieldName[] = {"ItemName", "ItemCode", "ItemCodeId", "Acode","Icode","Scode","Mcode","Rcode","Value", "Unit",
									         "UnitCode", "UnitCodeId","Up", "Low", "Normal", "Out", "ItemMemo", "ItemMemoCodeName",
									         "ItemMemoCode", "ItemMemoCodeId","ItemFreeMemo" };

			String outputFieldName[] = {"項目名","項目コード","項目テーブル", "JLAC10-A","JLAC10-I","JLAC10-S",
									               "JLAC10-M","JLAC10-R","値","単位","単位コード","単位テーブル","上限値",
									               "下限値","基準値","異常値","項目メモ","項目メモコード名","項目メモコード",
									               "項目メモコード名ＩＤ","項目自由メモ" };


			//Check whether DB connection is still avialble, if not get connection once again
			if(conPostgres == null){
				conPostgres = postgresConnection.acquirePostgresConnection();
			}
			//Check whether any of the received parameter is null, if so return false
			if (conPostgres == null || laboSpecimenUID == null || fw == null){
				outputLaboItemsReturn = false;
				//logger.warning("Either Postgres connection or laboSpecimenUID or output file handler is null");
			}
			else{
				buf = new StringBuffer();
				buf.append("Select ItemName, ItemCode, ItemCodeId, Acode,Icode,Scode,Mcode,Rcode,Value, Unit, UnitCode, UnitCodeId,");
				buf.append(" Up, Low, Normal, Out, ItemMemo, ItemMemoCodeName, ItemMemoCode, ItemMemoCodeId, ItemFreeMemo");
				buf.append(" from Tbl_Labo_Item where LaboSpecimenUID= ");
				buf.append(postgresConnection.addSingleQuote(laboSpecimenUID));
				buf.append(" order by ItemCode, ItemCodeID asc");

				//Convert into string
				sql = buf.toString();
				//logger.finer("Sql Statement: " + sql);
				try {
					statMessage = "データ検索中...";
					st = conPostgres.createStatement();
					rs = st.executeQuery(sql);
					// This patient's labo items records exist
					while (rs.next()) {
						//To send proper message to LaboTestBean
						dataAvilable=true;
						//Get related value from DB and output into file
						for (int i = 0; i < dbFieldName.length; i++) {
							if(rs.getString(dbFieldName[i]) != null) {
								fw.write( outputFieldName[i] + "," + rs.getString(dbFieldName[i]) + "\n");
							}
							else{
								fw.write( outputFieldName[i] + "," + "\"\"" + "\n");
							}
						}
						fw.flush();
					}
					outputLaboItemsReturn = true;
					databaseError = false;
					ioError = false;
				}
				catch (SQLException sqle) {
					//logger.warning("SQL Exception while getting date from labo_item");
					//logger.warning( "Exception details:"  + sqle );
					outputLaboItemsReturn = false;
					databaseError = true;
				}
				catch (IOException io) {
					//logger.warning("IO Exception while exporting labo_item");
					//logger.warning( "Exception details:"  + io );
					outputLaboItemsReturn = false;
					ioError = true;
				}
				catch (Exception e) {
					//logger.warning("Exception while exporting labo_item");
					//logger.warning( "Exception details:"  + e );
					outputLaboItemsReturn = false;
					ioError = true;
				}
				finally{
					if(rs !=null){
						rs.close();
					}
					if(st != null) {
						st.close();
					}
				}
			}
			//logger.finer ("Method Exit");
			return outputLaboItemsReturn;
        }
	}//End of classs ActualTask
}

