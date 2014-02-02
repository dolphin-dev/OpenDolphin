/*
 * MasterDataUpdateService.java
 *
 * Created on 2003/02/13
 *
 * Last updated on 2003/03/06
 *
 * Revised on 2003/03/11 renamed button name from '取消し' to '閉じる' and disabled when '更新'button is disabled
 *
 */
package mirrorI.dolphin.client;

import javax.swing.*;

import open.dolphin.plugin.*;
import open.dolphin.project.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;

/**
 * マスタデータ更新サービス。
 *
 * This is one of the default service in Dolphin client<br>
 * This service meant for executing 'Master data update' program which exist in local server<br>
 * This service is executed on clicking 'マスタデータ更新' from dolphin client main window<br>
 *
 * @author  Prashanth Kumar, Mirror-I Corp.
 *
 */
public class MasterDataUpdateService extends AbstractFramePlugin {

    private static final String  MAIN_MESSAGE 		="ローカルサーバのマスタデータ更新";
    private static final String  LAST_UPD_MESSAGE	="前回の更新:";
    private static final String  STATUS_LABEL		="現在の状態:";
    private static final String  INIT_STATUS_MESG	="更新の準備が出来ています。";

    //To execute update task in separate thread
    private final int UPDATE_TIMER_INTERVAL		= 200;

    //Property Table to store Postgres,logger various parameter
    private static Properties masterUpdateParameter;

	//Postgres database connection
    private Connection conPostgres = null;

	//Postgres database connection object
    mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;

	//To execute actual work in separate thread
    private javax.swing.Timer updateTimer = null;
    private boolean updateEnd = false;

	// To display last updated time in GUI
    private String lastUpdatedTime=null;

	//To handle actual task (sending/getting reqest to/from 'ClientRequestHandler')
    private MasterDataUpdateTask updateTask;

    // GUI components
    private JButton updateBtn;
   	private JButton cancelBtn;
   	private JTextField lastUpdFeild;
   	private JTextField statusMsgFeild;
   	
   	private boolean busy;

    /** Creates new MasterDataUpdateService */
    public MasterDataUpdateService() {
    }
    
    private boolean isBusy() {
    	return busy;
    }
    
    private void setBusy(boolean b) {
    	busy = b;
    }

    public void initComponent() {

		boolean startReturn=false;
        //Initialize INI file parameter, logger etc
        startReturn = startInitialization();
		//logger.finer("Method Entry");
        //If true returns then get last updated time from local server
        if(startReturn){
			lastUpdatedTime = getLastUpdatedTime();
		}
		else{
			lastUpdatedTime = "データ受信エラー";
		}
		//Create Panel
        JPanel ui =createContent();
        ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
      	centerFrame(new Dimension(432,216), ui);
    }
    
    public void processWindowClosing() {
    	if (! isBusy()) {
    		stop();
    	}
    }
    
	public void windowOpened(WindowEvent windowEvent) {
		updateBtn.requestFocus();
	}    

	/**
	 *
	 * createContent(), creates various GUI components required for 'マスタデータ更新' GUI<br>
	 * This method is called from start()<br>
	 *
	 */
    private JPanel createContent() {
		//logger.finer("Method Entry");
        JPanel ui = new JPanel();

        // Set layout
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));

        // Message display panel
        JPanel message = createMessagePanel();
        ui.add(message);
        ui.add(createVSpace(20));

		//masted data last updated time panel
		JPanel updatedTime = createLastUpdatedPanel();
        ui.add(updatedTime);
        ui.add(createVSpace(20));

        // Button panel (Update and Cancel)
        JPanel action = createButtonPanel();
        ui.add(action);
        ui.add(createVSpace(20));

        // Status message panel
        JPanel status = createStatusPanel();
        ui.add(status);

		//logger.finer("Method Exit");
        return ui;
    }

	/**
	 *
	 * createMessagePanel(), creates top message panel required for 'マスタデータ更新' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createMessagePanel() {
		//logger.finer("Method Entry");
        // Main message panel
        JLabel mainMsgLabel;
        JPanel message = createBoxLayoutPanel(0);
		JPanel p = createBoxLayoutPanel(0);
        mainMsgLabel = new JLabel(MAIN_MESSAGE);
        p.add(mainMsgLabel);
		message.add(p);

		//logger.finer("Method Exit");
        return message;
    }

	/**
	 *
	 * createLastUpdatedPanel(), creates master data lasted updated time display panel<br>
	 * required for 'マスタデータ更新' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createLastUpdatedPanel() {
		//logger.finer("Method Entry");
        // Last Updated time display panel
        JPanel updateTime = createBoxLayoutPanel(0);
        JPanel p = createBoxLayoutPanel(0);

        //Adding lasted update label
        JLabel lastUpdLabel = new JLabel(LAST_UPD_MESSAGE);
        p.add(lastUpdLabel);
        p.add(createHSpace(11));

		//Adding lasted updated time field
		lastUpdFeild = new JTextField();
		lastUpdFeild.setText(lastUpdatedTime);
		lastUpdFeild.setEditable(false);
        Dimension dim = new Dimension(160, 20);
        lastUpdFeild.setPreferredSize(dim);
        lastUpdFeild.setMaximumSize(dim);
		p.add(lastUpdFeild);

		updateTime.add(p);
		updateTime.add(Box.createHorizontalGlue());

		//.finer("Method Exit");
		return updateTime;
    }

	/**
	 *
	 * createButtonPanel(), creates '更新' & '閉じる' buttons required for <br>
	 * 'マスタデータ更新' GUI and adds their listener<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createButtonPanel() {
		//logger.finer("Method Entry");

       	// Button panel
        JPanel action = createBoxLayoutPanel(0);
        JPanel p = createBoxLayoutPanel(0);

		//Adding Update button (更新)
   		updateBtn = new JButton("\u66F4\u65B0 ( U )");
    	updateBtn.setMnemonic('U');
        updateBtn.setEnabled(true);
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	exeUpdate(evt);
             }
        });
        p.add(updateBtn);
        p.add(createHSpace(200));

		//Adding Cancel button (閉じる)
   		cancelBtn = new JButton("\u9589\u3058\u308b ( C )");
    	cancelBtn.setMnemonic('C');
        cancelBtn.setEnabled(true);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	processWindowClosing();
             }
        });
        p.add(cancelBtn);
        action.add(p);
        action.add(Box.createHorizontalGlue());

        //logger.finer("Method Exit");
        return action;
    }

	/**
	 *
	 * createStatusPanel(), creates current status message panel required for <br>
	 * 'マスタデータ更新' GUI and adds their listener<br>
	 * This method is called from createContent()<br>
	 *
	 */
 	private JPanel  createStatusPanel() {
		//logger.finer("Method Entry");

 		// Status display panel
        JPanel status = createBoxLayoutPanel(0);
        JPanel p = createBoxLayoutPanel(0);

		//Adding status label
        JLabel statusLabel = new JLabel(STATUS_LABEL);
        p.add(statusLabel);
        p.add(createHSpace(11));

		//Adding status message filed
		statusMsgFeild = new JTextField();
		statusMsgFeild.setText(INIT_STATUS_MESG);
		statusMsgFeild.setEditable(false);
        Dimension dim = new Dimension(315, 20);
        statusMsgFeild.setPreferredSize(dim);
        statusMsgFeild.setMaximumSize(dim);
		p.add(statusMsgFeild);

		status.add(p);
		status.add(Box.createHorizontalGlue());

		//logger.finer("Method Exit");
        return status;
 	}

	/**
	 *
	 * exeUpdate(), send request to 'ClientRequestHandler' for executing<br>
	 * 'MasterUpdateLocal' program through object of 'MasterDataUpdateTask'<br>
	 *
	 * This method is called when '更新' button is clicked from 'マスタデータ更新' GUI<br>
	 *
	 */
	private void exeUpdate(java.awt.event.ActionEvent evt){
		//logger.finer("Method Entry");

		//Set message for status panel
		statusMsgFeild.setText("ローカルサーバーへ接続中 . . . . . .");

		//Send request to local server's 'ClientRequestHandler'
		updateTask = new MasterDataUpdateTask(masterUpdateParameter);
		statusMsgFeild.setText(updateTask.getMessage());
		//As update operation is going to take considerable amount of time, execute in a separate thread with timer
		updateTimer = new javax.swing.Timer(UPDATE_TIMER_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				statusMsgFeild.setText(updateTask.getMessage());
				//Check whether update operation is completed
				if (updateTask.done()) {
					Toolkit.getDefaultToolkit().beep();
					updateTimer.stop();
					//reset 'setBusy' and enable 'update' button (When setBusy() is true, this window can not be closed)
					setBusy(false);
					//disable update and  cancel button so that user can not click this button continuously
					updateBtn.setEnabled(true);
					cancelBtn.setEnabled(true);
					//Get the latest updated time and display
					lastUpdFeild.setText(getLastUpdatedTime());

					updateTask = null;
					//logger.finer("Method Exit");
				}
			}
		});
		//set as busy (When setBusy() is true, this window can not be closed)
		setBusy(true);
		//Disable 'Update' and 'Cancel' Button
		updateBtn.setEnabled(false);
		cancelBtn.setEnabled(false);
		//send request for executing  'MasterUpdateLocal' program
		updateTask.go();
		//start timer to get back the control over GUI
		updateTimer.start();
	}


    private JPanel createBoxLayoutPanel(int direction) {
        JPanel p = new JPanel();
        if (direction == 0) {
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        }
        else {
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        }
        return p;
    }

    private Component createHSpace(int space) {
        return Box.createRigidArea(new Dimension(space, 0));
    }

    private Component createVSpace(int space) {
        return Box.createRigidArea(new Dimension(0, space));
    }

	/**
	 *
	 * startInitialization(), reads INI file and starts logger<br>
	 *
	 * This method is called start()<br>
	 *
	 */
	private boolean startInitialization(){
		boolean initReturn=false;

                String ip = Project.getHostAddress();
                String port = String.valueOf(6002);
                masterUpdateParameter = new Properties();
                masterUpdateParameter.put("ClientReqHandlerIP",ip);
                masterUpdateParameter.put("ClientReqHandlerPort",port);
                String Driver 	= "org.postgresql.Driver";
                String Host 	= Project.getHostAddress(); //172.168.158.2
                String Port 	= String.valueOf(5432);
                String DBName	= "dolphin";
                String DBUser	= "dolphin";
                String DBPwd	= "";
                masterUpdateParameter.put("Driver", Driver);
                masterUpdateParameter.put("Host", Host);
                masterUpdateParameter.put("Port", Port);
                masterUpdateParameter.put("DBName", DBName);
                masterUpdateParameter.put("DBUser", DBUser);
                masterUpdateParameter.put("DBPwd", DBPwd);
                
		//Read INI file and store info in 'masterUpdateParameter'
		//iniFileRead = new mirrorI.dolphin.server.IniFileRead();
		//masterUpdateParameter = iniFileRead.readIniFile();
		//set IniFileRead object as null
		//iniFileRead = null;
		//if ( masterUpdateParameter == null){
			//System.out.println("Could not get INI file " );
			//initReturn=false;
		//}
		//else{
			// Opening log handler
			//if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
																 //masterUpdateParameter.containsKey("MstUpdLoggerLocation") &&
																 //masterUpdateParameter.getProperty("MstUpdLoggerLocation") != null) {

				//logger = Logger.getLogger(masterUpdateParameter.getProperty("MstUpdLoggerLocation"));
				//Copy to LoggerLocation for PostgresConnection Object
				//masterUpdateParameter.put("LoggerLocation",masterUpdateParameter.getProperty("MstUpdLoggerLocation"));
			//}
			//To avoid run time exception error (when logger info not found in INI file)
			//else{
				//logger = Logger.getLogger("Dummy");
				//masterUpdateParameter.put("LoggerLocation","Dummy");
			//}
			/*try {
				if(fileHandler != null){
					logger.addHandler(fileHandler);
				}
				else{
					fileHandler = new FileHandler();
					logger.addHandler(fileHandler);
				}
			}
			catch(IOException e) {
				System.out.println("File handler could not be found");
				System.out.println( "Exception details:"  + e );
				initReturn=false;
			}
			catch(Exception e) {
				System.out.println("Exception while opening log handler");
				System.out.println( "Exception details:"  + e );
				initReturn=false;
			}*/
			//Create new postgresConneciton object for database object
			postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(masterUpdateParameter);
			initReturn=true;
		//}
		//logger.finer("Method Exit");
		return initReturn;
	}

	/**
	 *
	 * getLastUpdatedTime(), gets last master updated time from local server<br>
	 *
	 * This method is called run()<br>
	 *
	 */
	private String getLastUpdatedTime() {
		//logger.finer("Method Entry");
		String lastUpdatedTime = "データ受信エラー";
		java.sql.Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs = null;
		Timestamp lastUpdateTimeStamp= new java.sql.Timestamp(1);

		//Get Postgres Connection
		if(postgresConnection != null){
			conPostgres = postgresConnection.acquirePostgresConnection();
		}

		if (conPostgres == null) {
			//logger.warning("Error in connecting to DB, please check INI file paramater and DB status");
		}
		//On getting postgres connection successfully, get the last updated time stamp
		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("Select UpdatedTime from Tbl_Local_Master_Update order by UpdatedTime desc");

			//Convert into string
			sql = buf.toString();
			//logger.finer("Sql Statement" + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);
				// If last updated time's record is available
				if (rs.next()) {
					if (rs.getTimestamp("UpdatedTime") != null) {
						lastUpdateTimeStamp.setTime( rs.getTimestamp("UpdatedTime").getTime());
						lastUpdatedTime = DateFormat.getDateTimeInstance().format(new java.util.Date(lastUpdateTimeStamp.getTime() ) );
					}
				}
				//If no records also we need to set as true (local server might be requesting update for the first time)
				else{
					lastUpdatedTime = "該当するデータはありません";
				}
			}
			catch(SQLException e) {
				//logger.warning("SQLException while getting date from Local_Master_Update");
				//logger.warning( "Exception details:"  + e );
				lastUpdatedTime = "データ受信エラー";
			}
			catch(Exception e) {
				//logger.warning("Exception while getting date from Local_Master_Update");
				//logger.warning( "Exception details:"  + e );
				lastUpdatedTime = "データ受信エラー";
			}
			finally {
				try{
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
				catch(Exception e) {
					//logger.warning("Exception while closing record set or statement or postgres connection");
					//logger.warning( "Exception details:"  + e );
				}
			}
		}
		//logger.finer("Method Exit");
		return lastUpdatedTime;
	}

}//class end