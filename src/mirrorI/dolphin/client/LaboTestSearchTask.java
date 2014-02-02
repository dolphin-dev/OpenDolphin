/*
 * LaboTestSearchTask.java
 *
 * Created on 2003/01/27
 *
 * Last updated on 2003/02/28
 *
 */

package mirrorI.dolphin.client;

import swingworker.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author  Prasahnth Kumar, Mirror-I Corp.
 *
 * This class get the corresponding test reults from Labo_Module, Labo_Specimen and Labo_Item and returns to LaboTestBean
 *
 */
public class LaboTestSearchTask{
    
    private int lengthOfTask;
    private int current;
    private String statMessage;

    private String patientId;
    private boolean isLocalId;
    private String fromDate;
    private String toDate;
    //private Vector headerV;
    //private Vector columns = new Vector();
    //private Vector row;
    
    private AllLaboTest allLaboTests;
    private Vector laboModules;

    //private static Logger logger ;
    private Properties laboTestParameter;

    //Postgres database conenction object
    mirrorI.dolphin.dao.PostgresConnection postgresConnection;

    /** Creates new LaboTestSearchTask */
    public LaboTestSearchTask(String patientId, boolean isLocalId, String fromDate, String toDate, Properties laboTestParameter) {

        this.laboTestParameter = laboTestParameter;
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
     * go(), cretes new object of  'ActualTask' class to start the search task<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
	public void go() {
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
     * getHeaderV(), returns header information for displaying test report<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
    //Vector getHeaderV() {
        //return headerV;
    //}

    /**
     *
     * getColumns(), returns column information for displaying test report<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
    //Vector getColumns() {
        //return columns;
    //}
    
	public AllLaboTest getAllLaboTest() {
        return allLaboTests;
    }
    
	public Vector getLaboModuleColumns() {
        return laboModules;
    }

    /**
     *
     * getLengthOfTask(), returns information about how much task needs to be done<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
	public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     *
     * getCurrent(), returns hou much task has been completed<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
	public int getCurrent() {
        return current;
    }

    /**
     *
     * stop(), makes lengthOfTaks to current<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
	public void stop() {
        current = lengthOfTask;
    }

	/**
     *
     * done(), returns true when search task is completed and returns else when<br>
     * search task is not completed.
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
	public boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

    /**
     *
     * getMessage(), returns the present message to LaboTestBean to display<br>
     * <br>
     * This method is called from LaboTestBean.btnSearchActionPerformed()<br>
     *
     */
   public String getMessage() {
        return statMessage;
    }

    /**
     *
     * ActualTask(), gets corresponding test result from Labo_Module, Labo_Specimen and Labo_Item<br>
     * Stores the retrived information in vectors.
     * <br>
     * This class is initiiated from go()<br>
     *
     */
    private class ActualTask {

        private Connection conPostgres = null;
        private boolean actualTaskReturn = false;
        private boolean databaseError = false;
        private boolean dataAvilable = false;
        //Vector row = null;
        Vector collectionOfExtRefs = null;

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

            // search laboModules which have laboSampleTime between fromDate and toDate.
            try{
                actualTaskReturn = searchLaboModules(conPostgres, patientId, isLocalId, fromDate, toDate);

                if (!actualTaskReturn) {
                    //Disconenct DB
                    if(conPostgres != null){
                        postgresConnection.releasePostgresConnection(conPostgres);
                        conPostgres = null;
                    }
                    if(databaseError){
                        statMessage = "該当するデータの受取りに失敗しました。";
                        //logger.warning("Error in getting corresponding data from DB");
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
                        statMessage = "データ取得が終了しました。";
                        //logger.finer("Successfuly received corresponding data");
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
                statMessage = "該当するデータの受取りに失敗しました。";
                //logger.warning("Exception while calling searchLaboModules()");
                //logger.warning( "Exception details:"  + e );
                stop();
                return;
            }
        }

        /**
         *
         * searchLaboModules(), gets corresponding test result from Labo_Module<br>
         * Stores the retrived information in vectors<br>
         * Calls searchLaboTests() to get Labo_Specimen data for each record in Labo_Module<br>
         * <br>
         * This method is called  from 'ActualTask' class<br>
         *
         */
        public boolean searchLaboModules(Connection conPostgres, String patientId, boolean isLocalId, String fromDate,
        												 String toDate) throws SQLException{
            //logger.finer ("Method Entry");
            boolean searchLaboModulesReturn = false;
            String searchPatientID = null;
            Statement st = null;
            StringBuffer buf = null;
            String sql = null;
            ResultSet rs = null;
            databaseError = false;
            //headerV = new Vector();
            
            if (patientId == null) {
                return searchLaboModulesReturn;
            }

            //Check whether DB connection is still avialble, if not get connection once again
            if(conPostgres == null){
                conPostgres = postgresConnection.acquirePostgresConnection();
            }
            //Check whether any of the received parameter is null, if so return false
            if (conPostgres == null || patientId == null  || fromDate == null || toDate == null){
                searchLaboModulesReturn = false;
                    //logger.warning("Either Postgres connection or Patien ID or Search period is null");
            }
            else{
                if (isLocalId ) {
                    searchPatientID = "local" +  "__"  + patientId.replaceAll(",","");
                
                } else{
                    searchPatientID = "facility" +  "__"  + patientId.replaceAll(",","");
                }
                
                String[] dbColumns = new String[]{
                    "UID", "SampleTime", "RegistTime", "ReportTime", "MmlConfirmDate", "ReportStatus", 
                    "RepMemo", "RepMemoCodeName", "RepMemoCode","RepMemoCodeId", "RepFreeMemo",
                    "TestCenterName", "Set"
                };
                buf = new StringBuffer();
                buf.append("Select ");
                
                for (int i = 0; i < dbColumns.length; i++) {
                    if (i != 0) {
                        buf.append(",");
                    }
                    buf.append(dbColumns[i]);
                }
                
                buf.append(" from Tbl_Labo_Module where patientID=");
                buf.append(postgresConnection.addSingleQuote(searchPatientID));
                buf.append(" and  SampleTime >=");
                buf.append(postgresConnection.addSingleQuote(fromDate + "T00:00:00"));
                buf.append(" and SampleTime <=");
                buf.append(postgresConnection.addSingleQuote(toDate + "T23:59:59"));
                buf.append(" order by SampleTime,RegistId asc");

                //Convert into string
                sql = buf.toString();
                System.out.println(sql);
                //logger.finer("Sql Statement: " + sql);
                
                try {
                    statMessage = "データ検索中...";
                    st = conPostgres.createStatement();
                    rs = st.executeQuery(sql);
                    
                    // This patient's labo test records exist
                    while (rs.next()) {
                        //To send proper message to LaboTestBean
                        dataAvilable=true;
                        
                        //row = new Vector();
                        SimpleLaboModule simpleLaboModule = new SimpleLaboModule();
                        
                        String uid = rs.getString(1);
                        simpleLaboModule.setSampleTime(rs.getString(2));
                        simpleLaboModule.setRegistTime(rs.getString(3));
                        simpleLaboModule.setReportTime(rs.getString(4));
                        simpleLaboModule.setMmlConfirmDate(rs.getString(5));            
                        simpleLaboModule.setReportStatus(rs.getString(6));
                        simpleLaboModule.setRepMemo(rs.getString(7));            
                        simpleLaboModule.setRepMemoCodeName(rs.getString(8));
                        simpleLaboModule.setRepMemo(rs.getString(9));            
                        simpleLaboModule.setRepMemoCodeId(rs.getString(10));
                        simpleLaboModule.setRepFreeMemo(rs.getString(11));
                        simpleLaboModule.setTestCenterName(rs.getString(12));            
                        simpleLaboModule.setSet(rs.getString(13)); 
                                                
                        if (uid != null) {
                            
                            searchLaboModulesReturn = searchExtRef(conPostgres, uid);

                            //Get ExtRef info, if exist then add it
                            if( (searchLaboModulesReturn) && (collectionOfExtRefs != null) && (collectionOfExtRefs.size() > 0) ) {
                                //Dummy row need to add to separate labo module and Extref
                                //row.add("");
                                for(int k=0; k<collectionOfExtRefs.size(); k++) {
                                    if(collectionOfExtRefs.elementAt(k) != null) {
                                        //row.add( (String)collectionOfExtRefs.elementAt(k));
                                    }
                                }
                                if(collectionOfExtRefs !=null){
                                    collectionOfExtRefs.clear();
                                    collectionOfExtRefs=null;
                                }
                            }
                            
                            //If Searching extref function returns flase, exit while loop and return false to called method
                            if(!searchLaboModulesReturn){
                                //logger.warning("searchExtRef() returns false, searching for labo modules will be aborted");
                                break;
                            }
                            
                            //Get Labo Test results
                            searchLaboModulesReturn = searchLaboTests(conPostgres, uid, simpleLaboModule);

                            //If Searching Labo Test/ Items function returns flase, exit while loop and return false to called method
                            if(!searchLaboModulesReturn){
                                //logger.warning("searchLaboModules() returns false, searching for labo modules will be aborted");
                                break;
                            }
                        }
                        
                        if(!searchLaboModulesReturn){
                            //logger.warning("searchExtRef() or searchLaboModules() returns false, searching for labo modules will be aborted");
                            break;
                        }
                                    
                        //Adds the information available in row into Columns
                        //columns.add(row);
                        
                        if (laboModules == null) {
                            laboModules = new Vector();
                        }
                        laboModules.add(simpleLaboModule);
                        //System.out.println(simpleLaboModule.toString());
                    }
                }
                catch (SQLException sqle) {
					System.out.println("SQL Exception while getting date from labo_modue");
					System.out.println( "Exception details:"  + sqle );
                    //System.out.println(sqle);
                    searchLaboModulesReturn = false;
                    databaseError = true;
                }
                catch (Exception e) {
					System.out.println("Exception while getting date from labo_modue");
					System.out.println( "Exception details:"  + e );
					//System.out.println(e);
                    searchLaboModulesReturn = false;
                    databaseError = true;
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
            //logger.finer ("Method Exit");
            return searchLaboModulesReturn;
        }

		/**
		 *
		 * searchExtRef(), gets extRef info from Labo_Ext_Ref<br>
		 * Stores the retrived information in vector<br>
		 * <br>
		 * This method is called from searchLaboModules()<br>
		 *
		 */
		private boolean searchExtRef(Connection conPostgres, String laboModuleUID) throws SQLException {
			//logger.finer ("Method Entry");
			boolean searchExtRefReturn = false;
			Statement st = null;
			StringBuffer buf = null;
			String sql=null;
			ResultSet rs = null;
			databaseError = false;
			//To store extRef status if avialble (Received, receiving error, yet to receive)
			int extRefRecd = 0;
			int extRefRecErr = 0;
			int extRefWaiting = 0;
			String extRef ="外部参考イメージ受付";
			String extRefTextRecd =   "完了　： ";
			String extRefTextErr =      "エラー： ";
			String extRefTextWating = "中　　： ";
			String extRefEndText=" 枚";

			//Check whether DB connection is still avialble, if not get connection once again
			if(conPostgres == null){
				conPostgres = postgresConnection.acquirePostgresConnection();
			}
			//Check whether any of the received parameter is null, if so return false
			if (conPostgres == null || laboModuleUID == null){
				searchExtRefReturn = false;
				//logger.warning("Either Postgres connection or laboModuleUID is null");
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
					if( (extRefRecd>0) || (extRefRecErr>0) || (extRefWaiting>0) ){
						//Initialize Vector
						if(collectionOfExtRefs == null) {
							collectionOfExtRefs = new Vector();
						}
						if(extRefRecd>0) {
							//Set into vector for received extref
							collectionOfExtRefs.addElement(extRef +extRefTextRecd +extRefRecd+extRefEndText);
						}
						if(extRefRecErr>0) {
							//Set into vector for error extref
							collectionOfExtRefs.addElement(extRef +extRefTextErr +extRefRecErr+extRefEndText);
						}
						if(extRefWaiting>0) {
							//Set into vector for waiting extref
							collectionOfExtRefs.addElement(extRef +extRefTextWating +extRefWaiting+extRefEndText);
						}
					}
					searchExtRefReturn = true;
				}
				catch (SQLException sqle) {
					System.out.println("SQL Exception while getting date from tbl_labo_ext_ref");
					System.out.println( "Exception details:"  + sqle );
					searchExtRefReturn = false;
					databaseError = true;
				}
				catch (Exception e) {
					System.out.println("Exception while getting date from tbl_labo_ext_ref");
					System.out.println( "Exception details:"  + e );
					searchExtRefReturn = false;
					databaseError = true;
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
			//logger.finer ("Method Exit");
			return searchExtRefReturn;
        }

        /**
         *
         * searchLaboTests(), gets corresponding test result from Labo_Specimen<br>
         * Stores the retrived information in vectors<br>
         * Calls searchLaboItems() to get Labo_Item data for each record in Labo_Specimen<br>
         * <br>
         * This method is called from searchLaboModules()<br>
         *
         */
        private boolean searchLaboTests(Connection conPostgres, String laboModuleUID, SimpleLaboModule simpleLaboModule) throws SQLException {
            //logger.finer ("Method Entry");
            boolean searchLaboTestsReturn = false;
            Statement st = null;
            StringBuffer buf = null;
            String sql=null;
            ResultSet rs = null;
            databaseError = false;

            //Check whether DB connection is still avialble, if not get connection once again
            if(conPostgres == null){
                conPostgres = postgresConnection.acquirePostgresConnection();
            }
            //Check whether any of the received parameter is null, if so return false
            if (conPostgres == null || laboModuleUID == null){
                searchLaboTestsReturn = false;
                //logger.warning("Either Postgres connection or laboModuleUID is null");
            }
            else{
                
                String[] dbColumns = new String[]{
                    "UID", "SpecimenCodeId", "SpecimenCode", "SpecimenName",
                    "SpcMemo", "SpcMemoCodeName", "SpcMemoCode", "SpcMemoCodeId","SpecimenFreeMemo"
                };
                
                buf = new StringBuffer();
                buf.append("Select ");
                for (int i = 0; i < dbColumns.length; i++) {
                    if (i != 0) {
                        buf.append(",");
                    }
                    buf.append(dbColumns[i]);
                }
                
                buf.append(" from Tbl_Labo_Specimen where LaboModuleUID= ");
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
                        
                        SimpleLaboTest laboTest = new SimpleLaboTest();
                        simpleLaboModule.addSimpleLaboTest(laboTest);
                        SimpleLaboSpecimen specimen = new SimpleLaboSpecimen();
                        laboTest.setSimpleSpecimen(specimen);
                        
                        String uid = rs.getString(1);
                        specimen.setSpecimenCodeID(rs.getString(2));
                        specimen.setSpecimenCode(rs.getString(3));
                        specimen.setSpecimenName(rs.getString(4));
                        specimen.setSpecimenMemo(rs.getString(5));
                        specimen.setSpecimenMemoCodeName(rs.getString(6));
                        specimen.setSpecimenMemoCode(rs.getString(7));
                        specimen.setSpecimenMemoCodeId(rs.getString(8));
                        specimen.setSpecimenFreeMemo(rs.getString(9));
                        
                        if (allLaboTests == null) {
                            allLaboTests = new AllLaboTest();
                        }
                        allLaboTests.addSpecimen(specimen);

                        //Get Labo Items for this specimen
                        if (uid != null) {
                            searchLaboTestsReturn =searchLaboItems(conPostgres, uid, specimen, laboTest);
                        }
                        //If Searching Labo Items function returns flase, exit while loop and return false to called method
                        if(!searchLaboTestsReturn){
                            //logger.warning("searchLaboItems() returns false, searching for labo specimens will be aborted");
                            break;
                        }
                    }
                }
                catch (SQLException sqle) {
					System.out.println("SQL Exception while getting date from labo_speimen");
					System.out.println( "Exception details:"  + sqle );
                    searchLaboTestsReturn = false;
                    databaseError = true;
                }
                catch (Exception e) {
					System.out.println("Exception while getting date from labo_speimen");
					System.out.println( "Exception details:"  + e );
                    searchLaboTestsReturn = false;
                    databaseError = true;
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
            //logger.finer ("Method Exit");
            return searchLaboTestsReturn;
        }

        /**
         *
         * searchLaboItems(), gets corresponding test result from Labo_Item<br>
         * Stores the retrived information in vectors.
         * <br>
         * This method is called from searchLaboTests()<br>
         *
         */
        private boolean searchLaboItems(Connection conPostgres, String laboSpecimenUID, SimpleLaboSpecimen specimen, SimpleLaboTest laboTest) throws SQLException {
            //logger.finer ("Method Entry");
            boolean  searchLaboItemsReturn = false;
            Statement st = null;
            StringBuffer buf = null;
            String sql=null;
            ResultSet rs = null;
            databaseError = false;
            Vector memos = new Vector();

            //Check whether DB connection is still avialble, if not get connection once again
            if(conPostgres == null){
                conPostgres = postgresConnection.acquirePostgresConnection();
            }
            //Check whether any of the received parameter is null, if so return false
            if (conPostgres == null || laboSpecimenUID == null){
                searchLaboItemsReturn = false;
                //logger.warning("Either Postgres connection or laboSpecimenUID is null");
            }
            else{
                
                String[] dbColumns = new String[]{
                    "ItemCodeID", "ItemCode", "ItemName", "Value", "Unit","Low", "Up", "Normal", "Nout",
                    "ItemMemo","ItemMemoCodeName", "ItemMemoCode","ItemMemoCodeId", "ItemFreeMemo", "ExtRef"
                };
                
                buf = new StringBuffer();
                buf.append("Select ");
                for (int i = 0; i < dbColumns.length; i++) {
                    if (i != 0) {
                        buf.append(",");
                    }
                    buf.append(dbColumns[i]);
                }
                
                buf.append(" from Tbl_Labo_Item ");
                buf.append("where LaboSpecimenUID= ");
                buf.append(postgresConnection.addSingleQuote(laboSpecimenUID));
                buf.append(" order by ItemCode, ItemCodeID asc");

                //Convert into string
                sql = buf.toString();
                System.out.println("Sql Statement: " + sql);
                try {
                    statMessage = "データ検索中...";
                    st = conPostgres.createStatement();
                    rs = st.executeQuery(sql);
                    // This patient's labo items records exist
                    
                    while (rs.next()) {
                        
                        //To send proper message to LaboTestBean
                        dataAvilable=true;

                        SimpleLaboTestItem testItem = new SimpleLaboTestItem();
                        LaboTestItemID testItemID = new LaboTestItemID();
                        
                        testItem.setItemCodeID(rs.getString(1));
                        testItemID.setItemCodeID(rs.getString(1));
                        
                        testItem.setItemCode(rs.getString(2));
                        testItemID.setItemCode(rs.getString(2));
                        
                        testItem.setItemName(trimJSpace(rs.getString(3)));
                        testItemID.setItemName(trimJSpace(rs.getString(3)));
                        
                        allLaboTests.addTestItem(specimen, testItemID);
                        
                        testItem.setItemValue(rs.getString(4));
                        testItem.setItemUnit(rs.getString(5));
                        testItem.setLow(rs.getString(6));
                        testItem.setUp(rs.getString(7));
                        testItem.setNormal(rs.getString(8));
                        testItem.setOut(rs.getString(9));
                        testItem.setItemMemo(rs.getString(10));
                        testItem.setItemMemoCodeName(rs.getString(11));
                        testItem.setItemMemoCode(rs.getString(12));
                        testItem.setItemMemoCodeId(rs.getString(13));
                        testItem.setItemFreeMemo(rs.getString(14));

                        /* Not required display
                        //Get extRef and store add into Vector
                        if(rs.getString("ExtRef") != null) {
                                memos.add("外部参照ファイル" +  ": " + rs.getString("ExtRef"));
                        }
                        */
                        //String ss = strItemName + ": " + strValue + " " + strUnit;
                        //MyTableCellData d = new MyTableCellData(ss,strLow, strUp, strNormal, strOut, memos);
                        //row.add(d);
                        
                        laboTest.addSimpleLaboTestItem(testItem);
                        
                    }
                        
                    searchLaboItemsReturn = true;
                    databaseError = false;
                }
                catch (SQLException sqle) {
					System.out.println("SQL Exception while getting date from tbl_labo_items");
					System.out.println( "Exception details:"  + sqle );
                    searchLaboItemsReturn = false;
                    databaseError = true;
                }
                catch (Exception e) {
					System.out.println("Exception while getting date from tbl_labo_items");
					System.out.println( "Exception details:"  + e );
                    searchLaboItemsReturn = false;
                    databaseError = true;
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
            //logger.finer ("Method Exit");
            return searchLaboItemsReturn;
        }
    }//End of classs ActualTask
    
    private String trimJSpace(String str) {
        String ret = null;
        if (str != null) {
            int index = str.indexOf("　");
            ret = index > 0 ? str.substring(0, index) : str;
        }
        return ret;
    }
}//End of class LaboTestSearchTask
