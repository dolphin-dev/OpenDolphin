/*
 * EntryListTablePostgres.java
 *
 * Created on 2003/03/10
 *
 * Last updated on 2003/03/18
 */
package mirrorI.dolphin.client;

import javax.swing.*;
import javax.swing.table.*;

import open.dolphin.client.*;

import java.util.*;
import java.beans.*;
import java.sql.*;
import java.util.logging.*;

/**
 * EntryListTablePostgres, gets the labo test list based on the pid or sample collection<br>
 * time entered in 'LaboTest受信一覧' screen.
 *
 * @author  Prashanth Kumar, Mirror-i Corp.
 */
public class EntryListTablePostgres extends JTable {

    public static final String COUNT_PROP = "countProp";

    String tableName;
    String filter;
    String[] columnName;
    String sortOrder;
    EntryListTableModel model;

    PropertyChangeSupport boundSupport;

    // To store labo test parameter
	private Properties laboRecdListParameter=null;

	//Postgres database connection
	private Connection conPostgres = null;
	//Postgres database conenction object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;

	//Logger for logging messages
	//private static Logger logger;

	private boolean isLocalId = true;
	private boolean[] searchResult = {false,false};
	private boolean[] test = {false,false};

	private static final int  LOCAL_PID				=7;
	private static final int  FACILITY_PID				=10;
	private static final int  PATIENT_PID				=0;
	private static final int  PATIENT_CN				=1;
	private static final int  PATIENT_KANA			=2;
	private static final int  PATIENT_SEX				=3;
	private static final int  PATIENT_BIRTHDAY	=4;
	private static final int  MML_ID_COLUMN		=8;
	private static final int  RECORD_EXIST			=0;
	private static final int  DB_ACCESS_ERROR	=1;
	private static final int  DATA_FROM_PATIENT	=4;

    public EntryListTablePostgres(EntryListTableModel model) {
        super(model);
        this.model = model;
        boundSupport = new PropertyChangeSupport(this);
    }

    /**
     * カラム名と最初の行数からこのクラスを生成する
     */
    public EntryListTablePostgres(String[] columnNames, int startNumRows) {
        this(new EntryListTableModel(columnNames,startNumRows));
    }

    /**
     * カラム幅を設定し合計の幅を返す
     */
    public int setColumnWidths( int[] widths ) {
		//logger.finer("Method Entry");
        Enumeration en = getColumnModel().getColumns();
        int i = 0;
        int width = 0;
        while( en.hasMoreElements() ) {
            TableColumn col = (TableColumn)en.nextElement();
            col.setMinWidth( widths[i] );
            if (i==MML_ID_COLUMN) {
				//set as 0 (not to display)
				col.setMaxWidth( widths[i] );
			}
            col.setPreferredWidth( widths[i] );
            col.setResizable( true );
            width += col.getPreferredWidth();
            i++;
        }
        sizeColumnsToFit( 0 );
        //logger.finer("Method Exit");
        return width;
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }

    public String getBase() {
        return tableName;
    }

    public void setBase(String value) {
        tableName = value;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String value) {
        filter = value;
    }

    public String[] getAttributesToFetch() {
        return columnName;
    }

    public void setAttributesToFetch(String[] value) {
        columnName = value;
    }

    public void setAttributesToSort(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setProprties(Properties laboRecdListParameter) {
		this.laboRecdListParameter=laboRecdListParameter;
	}

	public void setLogger(Logger logger) {
		//this.logger=logger;
	}

	public void setIsLocalId (boolean isLocalId){
		this.isLocalId=isLocalId;
	}

	public boolean[] getSearchResult(){
		return searchResult;
	}

    /**
     * エントリ情報を行単位に表示する
     */
    public void fetchEntries() {
		//logger.finer("Method Entry");
        postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(laboRecdListParameter);
        model.clear();
        ArrayList entries = fetchEntries(columnName, tableName, filter, sortOrder);
        if (entries != null) {
            model.addRows(entries);
            boundSupport.firePropertyChange(COUNT_PROP, new Integer(-1), new Integer(getDataSize()));
            searchResult[RECORD_EXIST] = true;
        }
        else{
			searchResult[RECORD_EXIST] = false;
			boundSupport.firePropertyChange(COUNT_PROP, new Integer(-1), new Integer(getDataSize()));
		}
		//logger.finer("Method Exit");
    }

    /**
     * データをクリアする
     */
    public void clear() {
        model.clear();
    }

    /**
     * 実際の行数を返す
     */
    public int getDataSize() {
        return model.getSize();
    }

	/**
	 *
	 * fetchEntries(), gets labo test records from Labo_module<br>
	 * calls getPatientInfo() for 'Cn, Kana, Sex, Birthda' from Patient table for<br>
	 * each labo record<br>
	 * This method is called from fetchEntries()<br>
	 *
	 */
    protected ArrayList fetchEntries(String[] columnName, String tableName, String filter, String sortOrder){
		//logger.finer("Method Entry");
		ArrayList ret = null;
		java.sql.Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		String[] data = null;
		int len=columnName.length;
		int i;
		Vector laboInfo=null;

		boolean duplicateRecord = false;
		String [] oldRecordSet=null;
		searchResult[DB_ACCESS_ERROR] = false;

		//Get Postgres connection
		conPostgres=postgresConnection.acquirePostgresConnection();

		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("select ");
			for (i=0; i<(columnName.length-1); i++){
				buf.append(columnName[i] + ", ");
			}
			buf.append(columnName[i]);

			buf.append(" from "+ tableName);
			buf.append(" where "+ filter + " "+sortOrder);

			sql = buf.toString();
			System.out.println(sql);
			//logger.finer("SQL Statement: " + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);

				//If  records available
				while( rs!=null && rs.next()) {
					data = new String[len+DATA_FROM_PATIENT];

					//check whether this record is already fetched as lobo_module might have multiple record for same patient;
					if( (laboInfo != null) && (laboInfo.size() > 0) && (rs.getString("PatientId")!=null)  && (rs.getString("mmlId")!=null)  ){
						duplicateRecord=false;
						for (int k=0; k<laboInfo.size(); k++){
							oldRecordSet = (String[])laboInfo.elementAt(k);
							if(oldRecordSet != null){
								String oldPid;
								if ( isLocalId ) {
									oldPid = "local" +  "__"  + oldRecordSet[PATIENT_PID];
								}
								else{
									oldPid = "facility" +  "__"  + oldRecordSet[PATIENT_PID];
								}
								//check old pid and mmlId matches with current pid/mmlId
								if ( (oldPid.equalsIgnoreCase(rs.getString("PatientId")) ) &&
								     (oldRecordSet[MML_ID_COLUMN].equalsIgnoreCase(rs.getString("mmlId"))) ){
									duplicateRecord=true;
									break;
								}
							}
						}
					}
					if(!duplicateRecord){
						for(i=0; i<len; i++){
							if(rs.getString(columnName[i]) != null) {
								//For PID, remove prefix local/facility
								if(i==PATIENT_PID){
									String pid=rs.getString(columnName[i]);
									if (isLocalId && pid.startsWith("local__") && pid.length()>LOCAL_PID ) {
										pid = pid.substring(LOCAL_PID,pid.length());
									}
									else if( (!isLocalId) && pid.startsWith("facility__") && pid.length()>FACILITY_PID ) {
										pid = pid.substring(FACILITY_PID,pid.length());
									}
									data[i]=pid;
								}
								//For other fileds, leave 4 index for cn,kana,sex and birtday (which will be fetched later)
								else if(i>0){
									data[i+DATA_FROM_PATIENT]=rs.getString(columnName[i]);
								}
							}
						}
						//Store this array into vector
						if (laboInfo == null){
							laboInfo=new Vector();
						}
						if (data !=null){
							//add this String [] into vector
							laboInfo.add(data);
						}
					}
				}
				if(laboInfo != null && laboInfo.size() > 0 ){
					ret=getPatientInfo(laboInfo);
				}
				searchResult[DB_ACCESS_ERROR] = true;
			}
			catch (SQLException sqle) {
				//logger.warning("SQL Exception while calling fetchEntries()");
				//logger.warning("Exception details:"  + sqle );
				ret = null;
				searchResult[DB_ACCESS_ERROR] = false;
			}
			catch (Exception e) {
				//logger.warning("Exception while calling fetchEntries()");
				//logger.warning("Exception details:"  + e );
				ret = null;
				searchResult[DB_ACCESS_ERROR] = false;
			}
			finally {
				try{
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
				catch (SQLException sqle) {
					//logger.warning("SQL Exception while calling fetchEntries()");
					//logger.warning("Exception details:"  + sqle );
					ret = null;
					searchResult[DB_ACCESS_ERROR] = false;
				}
				catch (Exception e) {
					//logger.warning("Exception while calling fetchEntries()");
					//logger.warning("Exception details:"  + e );
					ret = null;
					searchResult[DB_ACCESS_ERROR] = false;
				}
			}
		}
		else{
			//logger.warning("Could not get Postgrers Connection");
			searchResult[DB_ACCESS_ERROR] = false;
		}
        //logger.finer("Method Exit");
        return ret;
    }


	/**
	 *
	 * getPatientInfo(), gets patient's 'Cn, Kana, Sex, Birthda from Patient table<br>
	 * for each labo record passed in laboInfo vector <br>
	 *
	 * This method is called from fetchEntries()<br>
	 *
	 */
    protected ArrayList getPatientInfo(Vector laboInfo){
		//logger.finer("Method Entry");
		ArrayList ret = null;
		java.sql.Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		String[] data = null;

		//Get Postgres Connection
		conPostgres=postgresConnection.acquirePostgresConnection();

		if(conPostgres != null){
			for (int i=0; i<laboInfo.size(); i++){
				data = (String[])laboInfo.elementAt(i);

				if(data != null) {

					buf = new StringBuffer();
					buf.append("select Name, Kana, Gender, Birthday from tbl_patient where pid='");
					buf.append(data[PATIENT_PID]+"'");

					sql = buf.toString();
					//logger.finer("SQL Statement: " + sql);

					try {
						st = conPostgres.createStatement();
						rs = st.executeQuery(sql);
						//get patient info and store it in data array
						if( rs != null && rs.next() ) {
							if( rs.getString("Name") != null){
								data[PATIENT_CN] =  rs.getString("Name");
							}
							else {
								data[PATIENT_CN] = null;
							}
							if( rs.getString("Kana") != null){
								data[PATIENT_KANA] =  rs.getString("Kana");
							}
							else {
								data[PATIENT_KANA] = null;
							}
							if( rs.getString("Gender") != null){
								data[PATIENT_SEX] =  rs.getString("Gender");
							}
							else {
								data[PATIENT_SEX] = null;
							}
							if( rs.getString("Birthday") != null){
								data[PATIENT_BIRTHDAY] =  rs.getString("Birthday");
							}
							else {
								data[PATIENT_BIRTHDAY] = null;
							}
						}
						//add data into Arraylist
						if (ret== null) {
							ret = new ArrayList();
						}
						ret.add(data);
						searchResult[DB_ACCESS_ERROR] = true;
					}
					catch (SQLException sqle) {
						//logger.warning("SQL Exception while calling getPatientInfo()");
						//logger.warning("Exception details:"  + sqle );
						ret = null;
						searchResult[DB_ACCESS_ERROR] = false;
					}
					catch (Exception e) {
						//logger.warning("Exception while calling getPatientInfo()");
						//logger.warning("Exception details:"  + e );
						ret = null;
						searchResult[DB_ACCESS_ERROR] = false;
					}
				}
			}
		}
		else{
			//logger.warning("Could not get Postgrers Connection");
			searchResult[DB_ACCESS_ERROR] = false;
		}
		try{
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
		catch (SQLException sqle) {
			//logger.warning("SQL Exception while calling getPatientInfo()");
			//logger.warning("Exception details:"  + sqle );
			ret = null;
			searchResult[DB_ACCESS_ERROR] = false;
		}
		catch (Exception e) {
			//logger.warning("Exception while calling getPatientInfo()");
			//logger.warning("Exception details:"  + e );
			ret = null;
			searchResult[DB_ACCESS_ERROR] = false;
		}
		//logger.finer("Method Exit");
		return ret;
	}
}

