/*
 * MasterDataUpdateService.java
 *
 * Created on 2003/02/13
 *
 * Last updated on 2003/03/06
 *
 * Revised on 2003/03/11 renamed button name from '取消し' to '閉じる' and disabled when '開始'button is disabled
 *
 */
package mirrorI.dolphin.client;

import javax.swing.*;

import open.dolphin.plugin.*;
import open.dolphin.project.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * データバックアップサービス。
 *
 * This is one of the default service in Dolphin client<br>
 * This service meant for executing 'Data Backup' program which exist in local server<br>
 * This service is executed on clicking ' データバックアップ' from dolphin client main window<br>
 *
 * @author  Prashanth Kumar, Mirror-I Corp.
 *
 */
public class DataBackupService extends AbstractFramePlugin {

    private static final String  MAIN_HEADING 		="Dolphinサーバのデータバックアップ";
    private static final String  FILE_LOCATION_DOL	="Dolphinサーバ";
    private static final String  FILE_LOCATION_CLAM     ="Claimサーバ";
    private static final String  PATH_LOCATION		="バックアップファイルのパス:";
    private static final String  DEFAULT_PATH		="/usr/local/dolphin/latest/";
    private static final String  STATUS_LABEL		="現在の状態:";
    private static final String  INIT_STATUS_MESG	="バックアップの準備が出来ています。";
    private static final String  MEDIA_LOC_SEL		="バックアップ先メディア／サーバの選択";
    private static final String  MEDIA_HDD_LAB		="ハードディスク";
    private static final String  MEDIA_TAPE_LAB		="テープ";

    private static final int DISPLY_OFFSET		= 50;
    //Selected Media
    private static final int MEDIA_HDD     		= 1;
    private static final int MEDIA_TAPE    		= 2;
    //Selected Hard Disc
    private static final int HDD_DOLPHIN     		= 1;
    private static final int HDD_CLAIM      		= 2;

    //To execute backup task in separate thread
    private final int BACKUP_TIMER_INTERVAL		= 200;

    //Property Table to store Postgres,logger various parameter
    private static Properties dataBackupParameter;

    //Logger for logging messages
    //private static Logger logger;
    //private static FileHandler fileHandler;

    //To read INI file parameter
    //private static mirrorI.dolphin.server.IniFileRead iniFileRead;

    //To execute actual work in separate thread
    private javax.swing.Timer backupTimer = null;
    private boolean backupEnd = false;

    //To handle actual task (sending/getting request to/from 'ClientRequestHandler')
    private DataBackupTask dataBackupTask;

    // GUI components
    private JButton backupBtn;
    private JButton cancelBtn;
    private JTextField statusMsgFeild;
    private JTextField pathEntryField;
    private JRadioButton dolphinHDD;
    private JRadioButton claimHDD;
    private JRadioButton mediaHDD;
    private JRadioButton mediaTape;
    
    private boolean busy;

    /** Creates new DataBackupService */
    public DataBackupService() {
    }
    
    private boolean isBusy() {
    	return busy;
    }
    
    private void setBusy(boolean b) {
    	busy = b;
    }

    public void initComponent() {

        boolean startReturn = false;
        //Initialize INI file parameter, logger etc
        startReturn = startInitialization();
	//logger.finer("Method Entry");

	//Create Panel
        JPanel ui =createContent();
        ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.getContentPane().add(ui);
        //Set this GUI size
        Dimension guiSize = new Dimension(439,298);
        //Get the total window size to set this GUI's starting location
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ( (screenSize.width - guiSize.width) / 2) - (DISPLY_OFFSET+10);
        int y = ( (screenSize.height - guiSize.height) / 2 ) + DISPLY_OFFSET;
        setToPreferenceBounds(x, y, guiSize.width, guiSize.height);
    }
    
    public void windowProcessWindowClosing() {
    	if (! isBusy()) {
    		stop();
    	}
    }
    
	public void windowOpened(WindowEvent windowEvent) {
		backupBtn.requestFocus();
	}
    
	/**
	 *
	 * createContent(), creates various GUI components required for 'データバックアップ' GUI<br>
	 * This method is called from start()<br>
	 *
	 */
    private JPanel createContent() {
		//logger.finer("Method Entry");
        JPanel ui = new JPanel();

        // Set layout
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));

        //Main heading display panel
        JPanel msgPnl = createMessagePanel();
        ui.add(msgPnl);
        ui.add(createVSpace(11));

        // Medai selection (HDD/Tape) display panel
        JPanel mediaLocSelection = createMediaLocSelection();
        ui.add(mediaLocSelection);
        ui.add(createVSpace(11));

        // Path entry  display panel
        JPanel pathEntry = createPathEntry();
        ui.add(pathEntry);
        ui.add(createVSpace(11));

        // Button panel (Backup and Cancel)
        JPanel action = createButtonPanel();
        ui.add(action);
        ui.add(createVSpace(11));

        // Status message panel
        JPanel status = createStatusPanel();
        ui.add(status);

		//logger.finer("Method Exit");
        return ui;
    }

	/**
	 *
	 * createMessagePanel(), creates top message panel required for 'データバックアップ' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createMessagePanel() {
		//logger.finer("Method Entry");
        // Main message panel
        JLabel mainMsgLabel;
        JPanel message = createBoxLayoutPanel(0);
		JPanel p = createBoxLayoutPanel(0);
        mainMsgLabel = new JLabel(MAIN_HEADING);
        p.add(mainMsgLabel);
		message.add(p);

		//logger.finer("Method Exit");
        return message;
    }

	/**
	 *
	 * createMediaLocSelection(), creates media and location selection panel<br>
	 * required for 'データバックアップ' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createMediaLocSelection() {
		//logger.finer("Method Entry");
		JPanel mediaSelection = new JPanel();
		mediaSelection.setLayout(new BoxLayout(mediaSelection, BoxLayout.Y_AXIS));

		// Media button - Hard Disk
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(createHSpace(5));
		mediaHDD = new JRadioButton(MEDIA_HDD_LAB,true);
		mediaHDD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mediaHDDSelected(evt);
			 }
		});
        p.add(mediaHDD);
		p.add(Box.createHorizontalGlue());
		mediaSelection.add(p);

			// Hard Disk Selection
			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			//Give tab space
			p.add(createHSpace(21));
			//Dolphin server HDD
			dolphinHDD = new JRadioButton(FILE_LOCATION_DOL,true);
			dolphinHDD.setFont(new java.awt.Font("DialogInput", 0, 12));
			dolphinHDD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dolphinHddSelected(evt);
				 }
        	});
			p.add(dolphinHDD);

			p.add(createHSpace(11));

			//Claim server HDD
			claimHDD = new JRadioButton(FILE_LOCATION_CLAM);
			claimHDD.setFont(new java.awt.Font("DialogInput", 0, 12));
			claimHDD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					claimHddSelected(evt);
				 }
        	});
			p.add(claimHDD);
			p.add(Box.createHorizontalGlue());
			mediaSelection.add(p);
			mediaSelection.add(createVSpace(5));

		//Media button - Tape
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(createHSpace(5));
		mediaTape = new JRadioButton(MEDIA_TAPE_LAB) ;
		mediaTape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mediaTapeSelected(evt);
			 }
		});
        p.add(mediaTape);
        p.add(Box.createHorizontalGlue());

  		//Making media radio buttons group
		ButtonGroup mediaBG = new ButtonGroup();
		mediaBG.add(dolphinHDD);
        mediaBG.add(claimHDD);

  		//Making HDD radio buttons group
		ButtonGroup diskBG = new ButtonGroup();
		diskBG.add(mediaHDD);
        diskBG.add(mediaTape);

		mediaSelection.add(p);
		mediaSelection.add(Box.createHorizontalGlue());
		mediaSelection.setBorder(BorderFactory.createTitledBorder(MEDIA_LOC_SEL));

		//logger.finer("Method Exit");
		return mediaSelection;
    }

	/**
	 *
	 * mediaHDDSelected(), disable/enable corresponding GUI components<br>
	 * This method is called when user clicks on 'ハードディスク' radio button <br>
	 *
	 */
	private void mediaHDDSelected(java.awt.event.ActionEvent evt){
		//If Dolphinサーバ is selected
		if(dolphinHDD.isSelected()){
			//enable path entry text field
			pathEntryField.setEditable(true);
		}
		else
		{
			//disable path entry text field
			pathEntryField.setEditable(false);
		}
		//enable hard disc selection radio button
		dolphinHDD.setEnabled(true);
		claimHDD.setEnabled(true);
	}

	/**
	 *
	 * dolphinHddSelected(), disable/enable corresponding GUI components<br>
	 * This method is called when user clicks on 'Dolphinサーバ ' radio button <br>
	 *
	 */
	private void dolphinHddSelected(java.awt.event.ActionEvent evt){
		//enable path entry text field
		pathEntryField.setEditable(true);
	}

	/**
	 *
	 * claimHddSelected(), disable/enable corresponding GUI components<br>
	 * This method is called when user clicks on 'Claimサーバ ' radio button <br>
	 *
	 */
	private void claimHddSelected(java.awt.event.ActionEvent evt){
		pathEntryField.setEditable(false);

	}

	/**
	 *
	 * mediaTapeSelected(), disable/enable corresponding GUI components<br>
	 * This method is called when user clicks on 'テープ ' radio button <br>
	 *
	 */
	private void mediaTapeSelected(java.awt.event.ActionEvent evt){
		//disable path entry field
		pathEntryField.setEditable(false);

		//disable hard disc selection radio button
		dolphinHDD.setEnabled(false);
		claimHDD.setEnabled(false);
	}

	/**
	 *
	 * createPathEntry(), creates backup path entry panel<br>
	 * required for 'データバックアップ' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createPathEntry() {
		//logger.finer("Method Entry");
        // Path entry display panel
        JPanel pathEntry = createBoxLayoutPanel(0);
        JPanel p = createBoxLayoutPanel(0);

        //Adding path location label
        JLabel pathLocation = new JLabel(PATH_LOCATION);
        p.add(pathLocation);
        p.add(createHSpace(11));

     	//Adding path location entry text field
		pathEntryField = new JTextField();
        pathEntryField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                pathEntryField.getInputContext().setCharacterSubsets(null);
            }
            public void focusLosted(FocusEvent event) {
               pathEntryField.getInputContext().setCharacterSubsets(null);
            }
        });
        Dimension dim = new Dimension(210, 20);
        pathEntryField.setPreferredSize(dim);
        pathEntryField.setMaximumSize(dim);
        pathEntryField.setText(DEFAULT_PATH);
        p.add(pathEntryField);

		pathEntry.add(p);
		pathEntry.add(Box.createHorizontalGlue());

		//logger.finer("Method Exit");
		return pathEntry;
    }

	/**
	 *
	 * createButtonPanel(), creates '開始' & '閉じる' buttons required for <br>
	 * 'データバックアップ' GUI and adds their listener<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createButtonPanel() {
		//logger.finer("Method Entry");

       	// Button panel
        JPanel action = createBoxLayoutPanel(0);
        JPanel p = createBoxLayoutPanel(0);

		//Adding Start button (開始)
   		backupBtn = new JButton("\u958B\u59CB ( S )");
    	backupBtn.setMnemonic('S');
        backupBtn.setEnabled(true);
        backupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	exeBackup(evt);
             }
        });
        p.add(backupBtn);
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
	 * 'データバックアップ' GUI <br>
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
        Dimension dim = new Dimension(320, 20);
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
	 * exeBackup(), send request to 'ClientRequestHandler' for executing<br>
	 * 'DataBackup' program through object of 'DataBackupTask'<br>
	 *
	 * This method is called when '開始' button is clicked from 'データバックアップ' GUI<br>
	 *
	 */
	private void exeBackup(java.awt.event.ActionEvent evt){
		//logger.finer("Method Entry");
		int selectedMedia = 0;
		int selectedHDD = 0;

		//Validation (check possible empty path when hard disc option is seleced for dolphin server )
		if( ( mediaHDD.isSelected() ) && ( dolphinHDD.isSelected() ) && (pathEntryField.getText().trim().length()<1) ){
			statusMsgFeild.setText("異常終了：バックアップファイルのパスが必要です。");
			//logger.warning("Backup file path is empty");
		}
		else{
			//get selected parameter to send a request for backup

			//get the media selected
			if(mediaHDD.isSelected()){
				selectedMedia=MEDIA_HDD;
				//logger.finer("HDD is selected");
			}
			else{
				selectedMedia=MEDIA_TAPE;
				//logger.finer("Tape is selected");
			}

			//get the selected hard disc
			if(dolphinHDD.isSelected()){
				selectedHDD=HDD_DOLPHIN;
				//logger.finer("Dolphin server is selected");
			}
			else{
				selectedHDD=HDD_CLAIM;
				//logger.finer("claim server is selected");
			}
			//logger.finer("path: "+ pathEntryField.getText().trim());

			//Set message for status panel
			statusMsgFeild.setText("ローカルサーバーへ接続中 . . . . . .");

			//Send request to local server's 'ClientRequestHandler'
			dataBackupTask = new DataBackupTask(selectedMedia,selectedHDD,pathEntryField.getText().trim(),dataBackupParameter);
			statusMsgFeild.setText(dataBackupTask.getMessage());
			//As backup operation is going to take considerable amount of time, execute in a separate thread with timer
			backupTimer = new javax.swing.Timer(BACKUP_TIMER_INTERVAL, new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					statusMsgFeild.setText(dataBackupTask.getMessage());
					//Check whether update operation is completed
					if (dataBackupTask.done()) {
						Toolkit.getDefaultToolkit().beep();
						backupTimer.stop();
						//reset 'setBusy' and enable '開始' button (When setBusy() is true, this window can not be closed)
						setBusy(false);
						//enable 開始 and 閉じる button so that user can not click this button continuously
						backupBtn.setEnabled(true);
						cancelBtn.setEnabled(true);
						dataBackupTask = null;
						//logger.finer("Method Exit");
					}
				}
			});
			//set as busy (When setBusy() is true, this window can not be closed)
			setBusy(true);
			//Disable '開始' and 閉じる　Button
			backupBtn.setEnabled(false);
			cancelBtn.setEnabled(false);
			//send request for executing  'DataBackup' program
			dataBackupTask.go();
			//start timer to get back the control over GUI
			backupTimer.start();
		}
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
    private boolean startInitialization() {
        
        boolean initReturn = true;
        
        String ip = Project.getHostAddress();
        String port = String.valueOf(6002);
        dataBackupParameter = new Properties();
        dataBackupParameter.put("ClientReqHandlerIP",ip);
        dataBackupParameter.put("ClientReqHandlerPort",port);
        
        //dataBackupParameter = null;
        //Read INI file and store info in 'dataBackupParameter'
        //iniFileRead = new mirrorI.dolphin.server.IniFileRead();
        //dataBackupParameter = iniFileRead.readIniFile();
        //set IniFileRead object as null
        //iniFileRead = null;
        //if ( dataBackupParameter == null) {
            //System.out.println("Could not get INI file " );
            //initReturn=false;
        
        //} else {
            // Opening log handler
            //if (dataBackupParameter != null && dataBackupParameter.size() > 0 &&
                //dataBackupParameter.containsKey("DataBackupLoggerLocation") &&
                //dataBackupParameter.getProperty("DataBackupLoggerLocation") != null ) {

                    //logger = Logger.getLogger(dataBackupParameter.getProperty("DataBackupLoggerLocation"));
            //}
            //To avoid run time exception error (when logger info not found in INI file)
            //else{
                    //logger = Logger.getLogger("Dummy");
            //}
            //try {
                    //if(fileHandler != null){
                            //logger.addHandler(fileHandler);
                    //}
                    //else{
                            //fileHandler = new FileHandler();
                            //logger.addHandler(fileHandler);
                    //}
                    //initReturn=true;
            //}
            //catch(IOException e) {
                    //System.out.println("File handler could not be found");
                    //System.out.println( "Exception details:"  + e );
                    ///initReturn=false;
            //}
            //catch(Exception e) {
                    //System.out.println("Exception while opening log handler");
                    //System.out.println( "Exception details:"  + e );
                    //initReturn=false;
            //}
        //}
        //logger.finer("Method Exit");
        return initReturn;
    }

}//class end