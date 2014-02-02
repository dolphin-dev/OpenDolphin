/*
 * PVTPostgresConnection.java
 *
 * Created on 2002/12/26
 *
 * Last updated on 2002/12/31
 *
 */
package mirrorI.dolphin.server;

import java.util.*;
import java.sql.*;
import java.util.logging.*;

import open.dolphin.dao.*;

/**
 *
 * This object gets the request from PVTServer for postgres connection<br>
 * Based on the request and connection parameter, it provides postgres connection<br>
 * on successful, it calls PVTPostgres.save function for add/update patient info<br>
 * Then, finally it removes the postgres connection<br>
 *
 * @author  Prashanth Kumar, Mirror-I corp.
 *
 */
public final class PVTPostgresConnection extends SqlDaoBean{

    private static Logger logger = Logger.getLogger(PVTServer.loggerLocation);

    private static Hashtable postgresConParameter;

	public PVTPostgresConnection(Hashtable h) {
		postgresConParameter = h;
        logger.finer("New PVTPostgresConnection created");
    }

    /**
	 *
	 * acquirePostgresConnection(), returns the postgres database connection<br>
	 * <br>
	 * This method is called from addWork()<br>
	 *
 	 */
    public Connection acquirePostgresConnection() {

		//logger.finer ("Method Entry");

		setDriver((String)postgresConParameter.get("driver"));
		setHost((String)postgresConParameter.get("host"));
		setPort(((Integer)postgresConParameter.get("port")).intValue());
		setDatabase((String)postgresConParameter.get("database"));
		setUser((String)postgresConParameter.get("user"));
		setPasswd((String)postgresConParameter.get("passwd"));

                Connection con = null;
        try {        
            con = getConnection();
        } catch (Exception e) {
        }

        if (con == null) {

            //logger.finer("Method abnormal Exit");
            return null;
        }
        else {
			//logger.finer("Method Exit");
			return con;
		}
    }

    /**
	 *
	 * releasePostgresConnection(), removes the  postgres database connection<br>
	 * <br>
	 * This method is called from addWork()<br>
	 *
 	 */
   	public void releasePostgresConnection(Connection con) {

		//logger.finer ("Method Entry");
		closeConnection(con);
		//logger.finer("Method Exit");
    }

    /**
	 *
	 * addWork(), gets the PVTPostgres object from PVTServer<br>
	 * <br>
	 * Gets Postgres Conenction, calls PVTPostgres.save() and disconnect Postgres Connection<br>
	 * <br>
	 * This method is called from PVTServer<br>
	 * <br>
	 * Returns true on getting successful db conneciton and on successful <br>
	 * database add/update operation, else returns false<br>
	 *
 	 */
    public boolean addWork(Object pvtPostgresObj) {

		//logger.finer ("Method Entry");

		boolean addWorkReturn=false;

		PVTPostgres pvtPostgres = (PVTPostgres)pvtPostgresObj;

		Connection con = acquirePostgresConnection();
		if (con == null) {
			logger.warning("Could not connect to Postgres database");
		}
		else{
			try {
				addWorkReturn=pvtPostgres.save(con);
				if (!addWorkReturn) {
					con.rollback();
				}
			}
			catch (Exception e) {
				logger.warning("Exception while calling pvtPostgres.save method");
				logger.warning("Exception details:"  + e );
			}
		}
		if (con != null) {
			releasePostgresConnection(con);
		}
        //logger.finer("Method Exit");
        return addWorkReturn;
    }

}