/*
 * LaboRecdListService.java
 *
 * Created on 2003/03/08
 *
 * Last updated on 2003/03/18
 */
package mirrorI.dolphin.client;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.client.*;
import open.dolphin.plugin.*;
import open.dolphin.project.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.logging.*;

/**
 * LaboTest受信一覧サービス。
 * This is one of the default service in Dolphin client<br>
 * This service meant for displaying Labo Test received list<br>
 * This service is executed on clicking 'ＬａｂｏＴｅｓｔ受付一覧' from dolphin client main window<br>
 *
 * @author  Prashanth Kumar, Mirror-i Corp.
 */
public class LaboRecdListService extends AbstractFramePlugin {

    // 患者情報及びLabo_Moduleテーブル関連
    //Note: mmlUID shall not be displayed (used for pid update)
    private final String[] COLUMN_NAMES 				= {"患者ID", "氏  名", "カ  ナ", "性別","生年月日","検体採取日時",
                                                             				"報告日時","確定日付","mmlID"};
	//Last column, i.e. mmlID is set as 0 (not to display)
   	private final int[] COLUMN_WIDTH 					= { 80, 120, 120, 30, 80, 130, 130,80,0 };

   	// フェッチ属性-Labo_Moduleテーブル
    private final String[] COLUMN_LABO_MODULE 	= { "PatientId","SampleTime", "ReportTime", "mmlConfirmDate","mmlId" };
    private final String SORT_COLUMN 					= "order by SampleTime asc";

   	private static final String  STATUS_LABEL			="現在の状態:";
   	private static final String  INIT_STATUS_MESG	="Labo Test 受信一覧の検索準備が出来ています。";
   	private static final String  DATE_ERR_MESG		="検体採取日は『CCYY-MM-DD』書式で入力してください。";
   	private static final String  SEARCHING_MESG		="検索中 . . . . . .";

   	private static final int DISPLY_OFFSET				= 50;
   	private static final int DATE_LENGTH				= 10;
   	private static final int YEAR_SEP_LENGTH			= 4;
   	private static final int MONTH_SEP_LENGTH		= 7;
   	private static final int PATIENT_ID						= 0;
   	private static final int PATIENT_MML_UID			= 8;
	private static final int LOCAL_PID						=7;
	private static final int FACILITY_PID					=10;
	private static final int PID_SEARCHED				=1;
	private static final int DATE_SEARCHED			=2;
	private static final int RECORD_EXIST				=0;
	private static final int DB_ACCESS_ERROR			=1;
	private int lastSearchedOption							=0;

   	//1000(MiliSecond)*60(Sec)*60(Min)*24(Hr)*7(Day) - To calculate 7 days in milisecond
   	private static final long ONE_WEEK_BACK_DATE	=604800000;

	//To get INI file parameter
        private static Properties laboRecdListParameter=null;
    //private mirrorI.dolphin.server.IniFileRead iniFileRead;

	//Logger for logging messages
	//private static Logger logger=null;
	private static FileHandler fileHandler;

	private boolean isLocalId = true;
	private boolean displayUpdateMesg=true;

	// Global GUI components
	private JTextField pidField;
	private JButton pidBtn;
	private JTextField sampleColStartField;
	private JTextField sampleColEndField;
	private JButton sampleColBtn;
	private EntryListTablePostgres entryListTable;
	private JTextField countField;
	private JProgressBar progressBar;
	private JTextField statusMsgFeild;

    private PropertyChangeSupport boundSupport;

    /** Creates new LaboRecdListService */
    public LaboRecdListService() {
        boundSupport = new PropertyChangeSupport(this);
    }

    public void initComponent() {
        startInitialization();
        //logger.finer("Method Entry");
        //setWindowClosingProp(DISPOSE_ON_CLOSE);
        JPanel ui = createContent();
        ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.getContentPane().add(ui);

		//Set this GUI size
		Dimension guiSize = new Dimension(820,300);
		//Get the total window size to set this GUI's starting location
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = ( (screenSize.width - guiSize.width) / 2) + (DISPLY_OFFSET/2);
		int y = ( (screenSize.height - guiSize.height) / 2 ) + DISPLY_OFFSET;
		setToPreferenceBounds(x, y, guiSize.width, guiSize.height);
		//logger.finer("Method Exit");
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(propName, l);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(propName, l);
    }


	/**
	 *
	 * createContent(), creates various GUI components required for 'ＬａｂｏＴｅｓｔ受付一覧' GUI<br>
	 * This method is called from start()<br>
	 *
	 */
    private JPanel createContent() {
		//logger.finer("Method Entry");
        JPanel ui = new JPanel();

        // Set layout
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));

        // Search panel
        JPanel search = createSearchPanel();
        ui.add(search);
        ui.add(createVSpace(11));

        // Serach Result Table
        entryListTable = createEntryListTable();
        JScrollPane scroller = new JScrollPane(entryListTable);
        ui.add(scroller);
        ui.add(createVSpace(11));

        // Status panel
        JPanel status = createStatusPanel();
        ui.add(status);

        // Coonect
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("countProp")) {
                    Integer i = (Integer)e.getNewValue();
                    countField.setText(String.valueOf(i.intValue()));
                }
            }
        };
        entryListTable.addPropertyChangeListener("countProp", pcl);
        entryListTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String existingPid= (String)entryListTable.getValueAt(entryListTable.getSelectedRow(), PATIENT_ID);
                    String mmlId= (String)entryListTable.getValueAt(entryListTable.getSelectedRow(), PATIENT_MML_UID);
                    if (existingPid != null && mmlId != null) {
                    	showUpdatePidDialog(existingPid,mmlId);
                    }
                }
            }
        });
		//logger.finer("Method Exit");
        return ui;
    }

	/**
	 *
	 * createSearchPanel(), creates top search panel required for 'ＬａｂｏＴｅｓｔ受付一覧' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createSearchPanel() {
		//logger.finer("Method Entry");
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkButtons();
            }

            public void removeUpdate(DocumentEvent e) {
                checkButtons();
            }
            public void changedUpdate(DocumentEvent e) {
            }
        };

        // Search panel
        JPanel search = createBoxLayoutPanel(0);

        // Search panel by 患者ID
        JPanel p = createBoxLayoutPanel(0);
        p.add(new JLabel("患者ID:"));
        p.add(createHSpace(11));
        pidField = new JTextField();
        pidField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                pidField.getInputContext().setCharacterSubsets(null);
            }
            public void focusLosted(FocusEvent event) {
               pidField.getInputContext().setCharacterSubsets(null);
            }
        });
        Dimension dim = new Dimension(80, 20);
        pidField.setPreferredSize(dim);
        pidField.setMaximumSize(dim);
        pidField.getDocument().addDocumentListener(dl);
        pidField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pidBtn.doClick();
            }
        });
        p.add(pidField);
        p.add(createHSpace(11));
        pidBtn = new JButton("(I)",ClientContext.getImageIcon("Zoom24.gif"));
        pidBtn.setMnemonic('I');
        pidBtn.setEnabled(false);
        pidBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pid = pidField.getText().trim();
                if (pid != null) {
                    doPidSearch(pid);
                }
            }
        });
        p.add(pidBtn);
        search.add(p);

		search.add(Box.createHorizontalGlue());

        //Search panel Sample collection date
        //start date
        //get default start date
		java.util.Date dateMills = new java.util.Date(System.currentTimeMillis()-ONE_WEEK_BACK_DATE);
		java.sql.Date  date = new java.sql.Date( (dateMills.getTime()) );

        p = createBoxLayoutPanel(0);
       	p.add(new JLabel("検体採取日(CCYY-MM-DD)で"));

        p.add(createHSpace(11));
        sampleColStartField = new JTextField(date.toString());
  		sampleColStartField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                //nameField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                sampleColStartField.getInputContext().setCharacterSubsets(null);
            }
            public void focusLosted(FocusEvent event) {
                sampleColStartField.getInputContext().setCharacterSubsets(null);
            }
        });
        dim = new Dimension(75, 20);
        sampleColStartField.setPreferredSize(dim);
        sampleColStartField.setMaximumSize(dim);
        sampleColStartField.getDocument().addDocumentListener(dl);
        sampleColStartField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = sampleColStartField.getText().trim();
                if (text != null){
                    sampleColBtn.doClick();
                }
            }
        });
        p.add(sampleColStartField);
        p.add(createHSpace(5));

        //end date
		//get default end date
		dateMills = new java.util.Date(System.currentTimeMillis());
		date = new java.sql.Date( (dateMills.getTime()) );

       	p.add(new JLabel("から"));

        p.add(createHSpace(5));
        sampleColEndField = new JTextField(date.toString());
  		sampleColEndField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                //nameField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                sampleColEndField.getInputContext().setCharacterSubsets(null);
            }
            public void focusLosted(FocusEvent event) {
                sampleColEndField.getInputContext().setCharacterSubsets(null);
            }
        });
        dim = new Dimension(75, 20);
        sampleColEndField.setPreferredSize(dim);
        sampleColEndField.setMaximumSize(dim);
        sampleColEndField.getDocument().addDocumentListener(dl);
        sampleColEndField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = sampleColEndField.getText().trim();
                if (text != null){
                    sampleColBtn.doClick();
                }
            }
        });
        p.add(sampleColEndField);

        p.add(createHSpace(11));
        p.add(new JLabel("まで"));

        p.add(createHSpace(11));

        sampleColBtn = new JButton("(D)",ClientContext.getImageIcon("Zoom24.gif"));
        sampleColBtn.setMnemonic('D');
        sampleColBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String startDate = sampleColStartField.getText().trim();
                String endDate = sampleColEndField.getText().trim();
                //if (displayName != null) {
                    doSampleColSearch(startDate,endDate);
                //}
            }
        });
        p.add(sampleColBtn);
        search.add(p);
		//logger.finer("Method Exit");
        return search;
    }

	/**
	 *
	 * createEntryListTable(), creates labo test list table required for<br>
	 * 'ＬａｂｏＴｅｓｔ受付一覧' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private EntryListTablePostgres createEntryListTable() {
		//logger.finer("Method Entry");
        LaboTestListTableModel model = new LaboTestListTableModel(COLUMN_NAMES, 3);
        EntryListTablePostgres table = new EntryListTablePostgres(model);
        table.setProprties(laboRecdListParameter);
        //table.setLogger(logger);
        table.setIsLocalId(isLocalId);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setBase("Tbl_Labo_Module");
        table.setAttributesToFetch(COLUMN_LABO_MODULE);
        table.setAttributesToSort(SORT_COLUMN);
        table.setColumnWidths(COLUMN_WIDTH);
        //logger.finer("Method Exit");
        return table;
    }

	/**
	 *
	 * createStatusPanel(), creates status panel required for<br>
	 * 'ＬａｂｏＴｅｓｔ受付一覧' GUI<br>
	 * This method is called from createContent()<br>
	 *
	 */
    private JPanel createStatusPanel() {
		//logger.finer("Method Entry");
        JPanel p = createBoxLayoutPanel(0);

		//Adding status label
      	JLabel statusLabel = new JLabel(STATUS_LABEL);
        p.add(statusLabel);
        p.add(createHSpace(11));

		//Adding status message filed
		statusMsgFeild = new JTextField();
		statusMsgFeild.setText(INIT_STATUS_MESG);
		statusMsgFeild.setEditable(false);
        Dimension dim = new Dimension(400, 20);
        statusMsgFeild.setPreferredSize(dim);
        statusMsgFeild.setMaximumSize(dim);
		p.add(statusMsgFeild);

        p.add(Box.createHorizontalGlue());
        p.add(new JLabel("該当件数:"));
        p.add(createHSpace(11));
        countField = new JTextField();
        countField.setEditable(false);
        Dimension dim1 = new Dimension(30, 20);
        countField.setPreferredSize(dim1);
        countField.setMaximumSize(dim1);
        p.add(countField);
        p.add(createHSpace(11));

        progressBar = new JProgressBar(0, 100);
        dim = new Dimension(100, 17);
        progressBar.setPreferredSize(dim);
        progressBar.setMaximumSize(dim);
        p.add(progressBar);
		//logger.finer("Method Exit");
        return p;
    }

	/**
	 *
	 * showUpdatePidDialog(), displays PID change dialog<br>
	 * This method is called from mouseClicked() (mouse double click on list)<br>
	 *
	 */
	private void showUpdatePidDialog(String existingPid, String mmlId){
		//logger.finer("Method Entry");
		String[] updateResult=null;
		//logger.finer("PID change dialog is called for Pid: "+ existingPid);
		LaboRecdPidChgDialog laboRecdPidChgDialog = new LaboRecdPidChgDialog(existingPid, mmlId, isLocalId, laboRecdListParameter);
        laboRecdPidChgDialog.start();

       	boolean result[] = laboRecdPidChgDialog.getResult();
       	if(result[RECORD_EXIST]){
       		updateResult =laboRecdPidChgDialog.getUpdateResult();
			if(lastSearchedOption != 0) {
				//if last searching was based on pid
				if (lastSearchedOption == PID_SEARCHED){
					doPidSearch(updateResult[1]);
					pidField.setText(updateResult[1]);
				}
				//if last searching was based on pid
				else if (lastSearchedOption == DATE_SEARCHED){
					String startDate = sampleColStartField.getText().trim();
					String endDate = sampleColEndField.getText().trim();
					doSampleColSearch(startDate,endDate);
				}
			}
       		//set displayUpdateMesg as false to show below message
       		displayUpdateMesg=false;
       		statusMsgFeild.setText("患者ＩＤ『"+ updateResult[0]+"』は『"+updateResult[1]+"』に変更されました。");
		}
		else if(!result[DB_ACCESS_ERROR]){
			doSearch();
		}
		//logger.finer("Method Exit");
	}

	/**
	 *
	 * doPidSearch(), calls doSearch() to get labo test list for entered ID<br>
	 * This method is called when pidBtnbuton is clicked<br>
	 *
	 */
    private void doPidSearch(String pid) {
        //logger.finer("Method Entry");
        StringBuffer buf = new StringBuffer();

        buf.append("PatientId='");

		if ( isLocalId ) {
			pid = "local" +  "__"  + pid.replaceAll(",","");
		}
		else{
			pid = "facility" +  "__"  + pid.replaceAll(",","");
		}
        buf.append(pid);
		buf.append("'");

        entryListTable.setFilter(buf.toString());

        //set lastSearchedOption to PID_SEARCHED
        lastSearchedOption=PID_SEARCHED;
        displayUpdateMesg=true;
        //logger.finer("doSearch() is called with, PID: "+pid);
        doSearch();
        //logger.finer("Method Exit");
    }

	/**
	 *
	 * doSampleColSearch(), calls doSearch() to get labo test list for entered <br>
	 * Sample collection date range<br>
	 * This method is called when sampleColBtn is clicked<br>
	 *
	 */
    private void doSampleColSearch(String startDate, String endDate) {
		//logger.finer("Method Entry");

		boolean checkDateFormat=false;
		String dateSeparator="-";

		//check start and end date format(yyyy-mm-dd)
		if( (startDate.length()==DATE_LENGTH) && (endDate.length()==DATE_LENGTH) ){
			if( dateSeparator.equalsIgnoreCase( startDate.substring(YEAR_SEP_LENGTH,(YEAR_SEP_LENGTH+1))) &&
				dateSeparator.equalsIgnoreCase( startDate.substring(MONTH_SEP_LENGTH,(MONTH_SEP_LENGTH+1))) &&
				dateSeparator.equalsIgnoreCase( endDate.substring(YEAR_SEP_LENGTH,(YEAR_SEP_LENGTH+1))) &&
				dateSeparator.equalsIgnoreCase( endDate.substring(MONTH_SEP_LENGTH,(MONTH_SEP_LENGTH+1))) ){
				//set checkDateFormat as true
				checkDateFormat=true;
			}
		}

		if(checkDateFormat){
			StringBuffer buf = new StringBuffer();

			buf.append(" SampleTime >=");
			buf.append("'"+startDate + "T00:00:00'");
			buf.append(" and SampleTime <=");
			buf.append("'"+endDate + "T23:59:59'");
			entryListTable.setFilter(buf.toString());

			//set lastSearchedOption to DATE_SEARCHED
        	lastSearchedOption=DATE_SEARCHED;
			displayUpdateMesg=true;
			//logger.finer("doSearch() is called with, Start Date: "+startDate+" End Date: "+endDate);
			doSearch();
		}
		//Display error message
		else{
			statusMsgFeild.setText(DATE_ERR_MESG);
		}
		//logger.finer("Method Exit");
    }

	/**
	 *
	 * doSearch(), calls entryListTable.fetchEntries() to get labo test list <br>
	 * This method is called doSampleColSearch() and doPidSearch <br>
	 *
	 */
    private void doSearch() {
		//logger.finer("Method Entry");
        statusMsgFeild.setText(SEARCHING_MESG);

        Runnable progressRunner = new Runnable() {
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            progressBar.setIndeterminate(true);
                        }
                    });

                    entryListTable.fetchEntries();
                    boolean[] searchResult=entryListTable.getSearchResult();
                    if(searchResult[RECORD_EXIST]){
						 if(displayUpdateMesg){
							 statusMsgFeild.setText("患者ＩＤを変更したい場合は該当ＩＤ上でダブルクリックして下さい。");
							 //logger.finer("Method exit");
						 }
					}
					else if(!searchResult[DB_ACCESS_ERROR]){
						statusMsgFeild.setText("データベースサーバー接続に失敗しました。");
						//logger.finer("Method exit");
					}
					else{
						statusMsgFeild.setText("該当するデータはありません。");
						//logger.finer("Method exit");
					}

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(0);
                        }
                    });
                }
                catch (Exception e) {
                    //logger.warning("Exception while calling doSearch()");
                    //logger.warning("Exception details: "+e);
                }
            }
        };
        Thread t = new Thread(progressRunner, "ProgressThread");
        t.start();
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("pvtNumber")) {
        }
        super.propertyChange(e);
    }


	/**
	 *
	 * checkButtons(), enable/disable pidBtn and sampleColBtn <br>
	 * This method is called createSearchPanel()<br>
	 *
	 */
    private void checkButtons() {
		//logger.finer("Method entry");
        boolean pidText = (pidField.getText().equals("")) ? false : true;
        pidBtn.setEnabled(pidText);

   		boolean startDateTxt = (sampleColStartField.getText().equals("")) ? false : true;
   		boolean endDateTxt = (sampleColEndField.getText().equals("")) ? false : true;

        boolean sampleCollection = (startDateTxt && endDateTxt )? true : false;
       	//Enable sampleColBtn only if both start and end date are entered
       	sampleColBtn.setEnabled(sampleCollection);
       	//logger.finer("Method exit");
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

    private void setTitledBorder(JPanel p, String title) {
        p.setBorder(BorderFactory.createTitledBorder(title));
    }

	/**
	 *
	 * startInitialization(), reads INI file, starts logger and check patient ID type<br>
	 *
	 * This method is called start()<br>
	 *
	 */
	private boolean startInitialization(){
		boolean initReturn=false;

		laboRecdListParameter = new Properties();
                String Driver 	= "org.postgresql.Driver";
                String Host 	= Project.getHostAddress(); //172.168.158.2
                String Port 	= String.valueOf(5432);
                String DBName	= "dolphin";
                String DBUser	= "dolphin";
                String DBPwd	= "";
                laboRecdListParameter.put("Driver", Driver);
                laboRecdListParameter.put("Host", Host);
                laboRecdListParameter.put("Port", Port);
                laboRecdListParameter.put("DBName", DBName);
                laboRecdListParameter.put("DBUser", DBUser);
                laboRecdListParameter.put("DBPwd", DBPwd);
		//Read INI file and store info in 'masterUpdateParameter'
		//iniFileRead = new mirrorI.dolphin.server.IniFileRead();
		//laboRecdListParameter = iniFileRead.readIniFile();
		//set IniFileRead object as null
		//iniFileRead = null;
		//if ( laboRecdListParameter == null){
			//System.out.println("Could not get INI file " );
			//initReturn=false;
		//}
		//else{
			// Opening log handler
			//if (laboRecdListParameter != null && laboRecdListParameter.size() > 0 &&
																 //laboRecdListParameter.containsKey("LaboRecdListLocation") &&
																 //laboRecdListParameter.getProperty("LaboRecdListLocation") != null) {

				//logger = Logger.getLogger(laboRecdListParameter.getProperty("LaboRecdListLocation"));
				//Copy to LoggerLocation for PostgresConnection Object
				//laboRecdListParameter.put("LoggerLocation",laboRecdListParameter.getProperty("LaboRecdListLocation"));
			//}
			//To avoid run time exception error (when logger info not found in INI file)
			//else{
				//logger = Logger.getLogger("Dummy");
				//laboRecdListParameter.put("LoggerLocation","Dummy");
			//}
			//try {
				//if(fileHandler != null){
					//logger.addHandler(fileHandler);
				//
				//else{
					//fileHandler = new FileHandler();
					//logger.addHandler(fileHandler);
				//}
				//logger.finer("Method entry");
			//}
			//catch(IOException e) {
				//System.out.println("File handler could not be found");
				//System.out.println( "Exception details:"  + e );
				//initReturn=false;
			//}
			//catch(Exception e) {
				//System.out.println("Exception while opening log handler");
				//System.out.println( "Exception details:"  + e );
				//initReturn=false;
			//}

			/////////////////////////////
			/////////////////////////////
			// check the prefecture...
			// Kumamoto uses patient Id of local type.
			// Miyazaki uses that of facility type.
			/////////////////////////////
			/////////////////////////////
			if ( Project.getLocalCode() == Project.KUMAMOTO ) {
				isLocalId = true;
			}
			else {
				isLocalId = false;
        	}
			initReturn=true;
		//}
		//logger.finer("Method exit");
		return initReturn;
	}

    protected class LaboTestListTableModel extends EntryListTableModel {

        public LaboTestListTableModel(String[] columnNames, int startNumRows) {
            super(columnNames, startNumRows);
        }

        public Object getValueAt(int row, int col) {
            Object[] data = getRowData(row);

            if (data == null) {
                return null;
            }

            if (col == 3) {
                String sex = (String)data[col];
                if(sex != null){
					if (sex.equals("male")) {
						return (Object)"M";
					}
					else if (sex.equals("female")) {
						return (Object)"F";
					}
					else {
						return data[col];
					}
				}
				else{
					data[col]=null;
					return data[col];
				}
            }
            else {
                return data[col];
            }
        }
    }
}
