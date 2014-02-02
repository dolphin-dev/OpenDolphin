/*
 * PostgresConnection.java
 *
 * Created on 2003/01/29
 *
 * Last updated on 2003/02/28
 *
 */
package mirrorI.dolphin.dao;

import java.util.*;
import java.sql.*;
import java.util.logging.*;

import open.dolphin.dao.*;

/**
 *
 * This is a general class for Postgres database connection<br>
 * Gets connection parameter<br>
 *
 * Main methods are 'acquirePostgresConnection()' and 'releasePostgresConnection()'
 *
 * @author  Prashanth Kumar, Mirror-I corp.
 *
 */
public final class PostgresConnection extends SqlDaoBean {

	//Default Driver (This value is considered only when this item does not exist in INI file)
	private static final String DEFAULT_DRIVER="org.postgresql.Driver";
	//Default Host
	private static final String DEFAULT_HOST="localhost";
	//Default port
	private static final int DEFAULT_PORT=5432;
	//Default DB name
	private static final String DEFAULT_DB_NAME="dolphin";
	//Default DB User Name
	private static final String DEFAULT_DB_USER_NAME="dolphin";
	//Default DB pwd
	private static final String DEFAULT_DB_PWD="";

    private Logger logger;

    private Properties postgresConParameter;

	public PostgresConnection(Properties h) {
		this.postgresConParameter = h;
		if (postgresConParameter != null && postgresConParameter.size() > 0 &&
		    												postgresConParameter.containsKey("LoggerLocation") &&
		    												postgresConParameter.getProperty("LoggerLocation") !=null  ) {
			logger = Logger.getLogger(postgresConParameter.getProperty("LoggerLocation"));
		}
		//To aviod run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
		}
    }

    /**
	 *
	 * acquirePostgresConnection(), returns the postgres database connection<br>
	 *
 	 */
    public Connection acquirePostgresConnection() {
		logger.finer ("Method Entry");
		Connection conPostgres = null;
		//Set connection parameter
		if (postgresConParameter != null && postgresConParameter.size() > 0) {
			try{

				if(postgresConParameter != null && postgresConParameter.size() > 0 ){

					if(postgresConParameter.containsKey("Driver") && postgresConParameter.getProperty("Driver") != null ){
						setDriver(postgresConParameter.getProperty("Driver"));
					}
					else{
						setDriver(DEFAULT_DRIVER);
						logger.warning("Error in getting driver for postgres conenction, taking default value: " + DEFAULT_DRIVER );
					}

					if(postgresConParameter.containsKey("Host") && postgresConParameter.getProperty("Host") != null ){
						setHost(postgresConParameter.getProperty("Host"));
					}
					else{
						setHost(DEFAULT_HOST);
						logger.warning("Error in getting host for postgres conenction, taking default value: " + DEFAULT_HOST );
					}

					if(postgresConParameter.containsKey("Port") && new Integer(postgresConParameter.getProperty("Port")).intValue() >0) {
						setPort(new Integer(postgresConParameter.getProperty("Port")).intValue());
					}
					else{
						setPort(DEFAULT_PORT);
						logger.warning("Error in getting port for postgres conenction, taking default value: " + DEFAULT_PORT );
					}

					if(postgresConParameter.containsKey("DBName") && postgresConParameter.getProperty("DBName") != null ){
						setDatabase(postgresConParameter.getProperty("DBName"));
					}
					else{
						setDatabase(DEFAULT_DB_NAME);
						logger.warning("Error in getting DB name for postgres conenction, taking default value: " + DEFAULT_DB_NAME );
					}

					if(postgresConParameter.containsKey("DBUser") && postgresConParameter.getProperty("DBUser") != null ){
						setUser(postgresConParameter.getProperty("DBUser"));
					}
					else{
						setUser(DEFAULT_DB_USER_NAME);
						logger.warning("Error in getting DB user name for postgres conenction, taking default value: " + DEFAULT_DB_USER_NAME );
					}

					if(postgresConParameter.containsKey("DBPwd") && postgresConParameter.getProperty("DBPwd") != null ){
						setPasswd(postgresConParameter.getProperty("DBPwd"));
					}
					else{
						setPasswd(DEFAULT_DB_PWD);
						logger.warning("Error in getting DB password name for postgres conenction, taking default value: " + DEFAULT_DB_PWD );
					}
				}
				//Setting default values
				else{
					logger.warning("Error in getting required paramter for postgres conenction, taking default values");
					setDriver(DEFAULT_DRIVER);
					setHost(DEFAULT_HOST);
					setPort(DEFAULT_PORT);
					setDatabase(DEFAULT_DB_NAME);
					setUser(DEFAULT_DB_USER_NAME);
					setPasswd(DEFAULT_DB_PWD);
				}

				//Get connection
				conPostgres = getConnection();
				if (conPostgres != null) {
					logger.finer("Got postgres connection Successfuly ");
				}
				else {
					logger.warning("Could not get Postgres connection, check parameter file and database status");
					conPostgres= null;
				}
			}
			catch(Exception e) {
				logger.warning("Exception while getting postgres connection");
				logger.warning( "Exception details:"  + e );
				conPostgres= null;
			}
		}
		else{
			logger.warning("Could not get Postgres connection parameters, please check INI file");
			conPostgres= null;
		}

		logger.finer ("Method Exit");
		return conPostgres;
	}

    /**
	 *
	 * releasePostgresConnection(), removes the  postgres database connection<br>
	 *
 	 */
	public void releasePostgresConnection(Connection conPostgres) {
		logger.finer ("Method Entry");
		try{
			if (conPostgres != null) {
				closeConnection(conPostgres);
				conPostgres = null;
			}
		}
		catch(Exception e) {
			logger.warning("Exception while releasing postgres connection");
			logger.warning( "Exception details:"  + e );
		}
		logger.finer ("Method Exit");
	}

	public void closeStatement(Statement st) {
		logger.finer ("Method Entry");
		super.closeStatement(st);
		logger.finer ("Method Exit");
	}

    /**
	 *
	 * To make sql statement ('xxxx',)<br>
	 *
 	 */
    public String addSingleQuoteComa(String s) {
        StringBuffer buf = new StringBuffer();
        buf.append("'");
        buf.append(s);
        buf.append("',");
        return buf.toString();
    }

	/**
	 *
	 * To make sql statement ('xxxx')<br>
	 *
	 */
	public String addSingleQuote(String s) {
		StringBuffer buf = new StringBuffer();
		buf.append("'");
		buf.append(s);
		buf.append("'");
		return buf.toString();
    }
}