/*
 * LaboRecdPidChgDialog.java
 *
 * Created on 2003/03/08
 *
 * Last updated on 2003/03/18
 *
 */
package mirrorI.dolphin.client;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

/**
 * @author  Prashanth Kumar, Mirror-i Corp.
 *
 * This class, displays PID change dialog and update the PatientID of Labo_Module,<br>
 * if changed ID exist in 'Patient' table<br>.
 *
 */
public class LaboRecdPidChgDialog {

	private static final int  RECORD_EXIST			=0;
	private static final int  DB_ACCESS_ERROR	=1;

    private JTextField newPIDField;
    private JTextField existingPIDField;
    private JButton updateButton;
    private JButton cancelButton;
    private boolean okState;
    private boolean[] result={false,true};
    private JDialog dialog;

    private String existingPid=null;
    private String mmlId=null;
    private boolean isLocalId = true;
    private String[] updateResult=null;

    // To store labo test parameter
	private Properties laboRecdListParameter=null;

	//Postgres database connection
	private Connection conPostgres = null;
	//Postgres database conenction object
	mirrorI.dolphin.dao.PostgresConnection postgresConnection=null;

	//Logger for logging messages
	//private static Logger logger=null;

    /** Creates new LaboRecdPidChgDialog  */
    public LaboRecdPidChgDialog(String existingPid, String mmlId, boolean isLocalId, Properties laboRecdListParameter) {
		this.existingPid=existingPid;
		this.mmlId=mmlId;
		this.laboRecdListParameter=laboRecdListParameter;
		//this.logger=logger;
		this.isLocalId=isLocalId;
    }

    public boolean[] getResult() {
        return result;
    }
    public String[] getUpdateResult(){
		return updateResult;
	}

	/**
	 *
	 * start(), create GUI components to displays PID change dialog<br>
	 * This method is called from LaboRecdListService.showUpdatePidDialog()<br>
	 *
	 */
    public void start() {
		//logger.finer("Method Entry");
        String title = "\u60A3\u8005ÇhÇc\u5909\u66f4-Dolphin";
        dialog = new JDialog((Frame)null, title, true);

        Component c = createComponent();
        dialog.getContentPane().add(c, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(updateButton);
        dialog.pack();
        Point loc = DesignFactory.getCenterLoc(dialog.getWidth(), dialog.getHeight());
        dialog.setLocation(loc.x, loc.y);
        dialog.show();
        //logger.finer("Method exit");
    }

	/**
	 *
	 * createComponent(), create GUI components to displays PID change dialog<br>
	 * This method is called from start()<br>
	 *
	 */
    private Component createComponent() {
		//logger.finer("Method Entry");
        JPanel top = new JPanel(new BorderLayout(0,2));
        //Entry Panel
        top.add(createIdPanel(), BorderLayout.NORTH);

        //Bottom Panel
        JPanel bottom = createButtonPanel();

       	//add top and bottom panel into one panel and return
       	JPanel panel = new JPanel(new BorderLayout(0,7));
        panel.add(top, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
		//logger.finer("Method exit");
        return panel;
    }

	/**
	 *
	 * createIdPanel(), creates ID panel to display PID change dialog<br>
	 * This method is called from createComponent()<br>
	 *
	 */
    private JPanel createIdPanel() {
		//logger.finer("Method Entry");
        // DocumentListener
        DocumentListener dl = new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                checkButtons();
            }

            public void removeUpdate(DocumentEvent e) {
                checkButtons();
            }
        };

        JPanel panel = new JPanel(new BorderLayout(0, 7));

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

        // UserID label
        String text = "ïœçXå„ÇÃÇhÇc:";
        content.add(new JLabel(text));

        // Strut
        content.add(Box.createHorizontalStrut(7));

        // new PID Field
        newPIDField = new JTextField();
        newPIDField.setText(existingPid);
        Dimension dim = new Dimension(80,20);
        newPIDField.setPreferredSize(dim);
        newPIDField.setMaximumSize(dim);

        newPIDField.getDocument().addDocumentListener(dl);
        content.add(newPIDField);

        // Glue
        content.add(Box.createHorizontalGlue());

		// Strut
        content.add(Box.createHorizontalStrut(70));

        // Passwd lable
        text = "åªç›ÇÃID:";
        content.add(new JLabel(text));

        // Strut
        content.add(Box.createHorizontalStrut(7));

        // existing PID field
        existingPIDField = new JTextField();
        existingPIDField.setText(existingPid);
        existingPIDField.setEditable(false);
        existingPIDField.setPreferredSize(dim);
        existingPIDField.setMaximumSize(dim);

        existingPIDField.getDocument().addDocumentListener(dl);
        content.add(existingPIDField);

        content.setBorder(BorderFactory.createEmptyBorder(3, 12, 10, 11));

        panel.add(content, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createTitledBorder("ê≥ÇµÇ¢ÇhÇcÇ…ïœçXÇµÇƒÅwÇhÇcïœçXÅxÉ{É^ÉìÇÉNÉäÉbÉNÇµÇƒâ∫Ç≥Ç¢") );
		//logger.finer("Method exit");
        return panel;
    }

	/**
	 *
	 * createButtonPanel(), creates button panel to display PID change dialog<br>
	 * This method is called from createComponent()<br>
	 *
	 */
    private JPanel createButtonPanel() {
		//logger.finer("Method Entry");
        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

        // Strut
        content.add(Box.createHorizontalStrut(5));

        // Update Button
        String text = "ID\u5909\u66f4 ( U )";
        updateButton = new JButton(text);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateButton.setEnabled(false);
                updatePID();
                updateButton.setEnabled(true);
            }
        });
        updateButton.setEnabled(false);
        updateButton.setMnemonic('U');
        content.add(updateButton);

        // Glue
        content.add(Box.createHorizontalGlue());

        // Cancel Button
        cancelButton = new JButton(text);
        cancelButton = new JButton("\u9589\u3058\u308b ( C )");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doCancel();
            }
        });
        cancelButton.setMnemonic('C');
        content.add(cancelButton);
		//.finer("Method exit");
        return content;
    }

	/**
	 *
	 * checkButtons(), enable/disable updateButton<br>
	 * This method is called from createIdPanel()<br>
	 *
	 */
    private void checkButtons() {
        //logger.finer("Method Entry");
        boolean newIdEmpty = newPIDField.getText().equals("") ? true : false;
        boolean pidSame = existingPIDField.getText().equals( newPIDField.getText() ) == true ? true : false;

        boolean newOKState = ( newIdEmpty == false && pidSame == false ) ? true : false;

        if (newOKState != okState) {
            updateButton.setEnabled(newOKState);
            okState = newOKState;
        }
        //logger.finer("Method exit");
    }

	/**
	 *
	 * updatePID(), update existing PatientId of Labo_Module if the changed<br>
	 * PID exist in Patient table <br>
	 * This method is called when updateButton is clicked<br>
	 *
	 */
    private void updatePID() {
		//logger.finer("Method Entry");
		java.sql.Statement st = null;
		StringBuffer buf = null;
		String sql=null;
		ResultSet rs=null;
		String newPid=null;
		String existingPid=null;

		//Check whether updated PID(New) exist in patient table

		//Get Postgres connection
		if(postgresConnection == null) {
			postgresConnection = new mirrorI.dolphin.dao.PostgresConnection(laboRecdListParameter);
		}
		conPostgres=postgresConnection.acquirePostgresConnection();

		if(conPostgres != null){
			buf = new StringBuffer();
			buf.append("select pid from patient where pid='");
			buf.append(newPIDField.getText() +"'");
			sql = buf.toString();
			//logger.finer("SQL Statement: " + sql);
			try {
				st = conPostgres.createStatement();
				rs = st.executeQuery(sql);

				//If  records available
				if( rs!=null && rs.next()) {
					//add/check local/facility in patient ID
					updateResult=null;
					updateResult=new String[2];
					updateResult[0]=existingPIDField.getText();
					updateResult[1]=newPIDField.getText().replaceAll(",","");
					if ( isLocalId ) {
						newPid = "local" +  "__"  + newPIDField.getText().replaceAll(",","");
						existingPid=   "local" +  "__"  + existingPIDField.getText();
					}
					else{
						newPid = "facility" +  "__"  + newPIDField.getText().replaceAll(",","");
						existingPid=   "facility" +  "__"  + existingPIDField.getText();
					}

					//update new PID in Labo_module
					buf=null;
					buf = new StringBuffer();
					buf.append("update Tbl_Labo_Module set PatientId='"+newPid+"', ");
					buf.append("pidmatch='c' where mmlId='" + mmlId +"' and PatientID='"+existingPid+"'");
					sql = null;
					sql = buf.toString();
					//logger.finer("SQL Statement: " + sql);
					int updateRec=st.executeUpdate(sql);
					//logger.finer("Number of records updated: "+ updateRec);
					if (updateRec > 0) {
						result[RECORD_EXIST] = true;
					}
					dialog.setVisible(false);
        			dialog.dispose();
        			result[DB_ACCESS_ERROR] = true;
        			//logger.info("PatientId of Labo_Module: "+existingPid+" changed to: "+newPid);
				}
				//else display error message
				else{
					String errorMesg="ä≥é“ÇhÇc Åw"+newPIDField.getText() + "Åx ÇÕìoò^Ç≥ÇÍÇƒÇ®ÇËÇ‹ÇπÇÒÅB";
					JOptionPane.showMessageDialog(null,errorMesg,"Dolphin: ä≥é“îFèÿ", JOptionPane.WARNING_MESSAGE);
					//logger.warning("PID " +newPIDField.getText() +" Doesn't exist in 'Patient' table");
					newPIDField.setText(null);
					newPIDField.requestFocus();
		   		}
			}
			catch (SQLException sqle) {
				//logger.warning("SQL Exception while calling fetchEntries()");
				//logger.warning("Exception details:"  + sqle );
				result[DB_ACCESS_ERROR] = false;
				doCancel();
			}
			catch (Exception e) {
				//logger.warning("Exception while calling fetchEntries()");
				//logger.warning("Exception details:"  + e );
				result[DB_ACCESS_ERROR] = false;
				doCancel();
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
					result[DB_ACCESS_ERROR] = false;
					doCancel();
				}
				catch (Exception e) {
					//logger.warning("Exception while calling fetchEntries()");
					//logger.warning("Exception details:"  + e );
					result[DB_ACCESS_ERROR] = false;
					doCancel();
				}
			}
		}
		else{
			result[DB_ACCESS_ERROR] = false;
			doCancel();
		}
		//logger.finer("Method exit");
    }

    private void doCancel() {
        //logger.finer("Method Entry");
        result[RECORD_EXIST] = false;
        dialog.setVisible(false);
        dialog.dispose();
        //logger.finer("Method exit");
    }
}
