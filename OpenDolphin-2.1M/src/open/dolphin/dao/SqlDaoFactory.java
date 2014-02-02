package open.dolphin.dao;

import open.dolphin.project.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlDaoFactory {

    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    private static final int POSTGRESQL_PORT = 5432;
    private static final String DB_ORAC = "orca";
    private static final String USER_ORCA = "orca";
    private static final String PASSWORD_ORCA = "";
    
    /** Creates a new instance of DaoFactory */
    public SqlDaoFactory() {
    }
    
    /**
     * Creates DataAccessObject
     */
    public static SqlDaoBean create(String keyString) {
        
        SqlDaoBean dao = null;
        
        try {
            if (keyString.equals("dao.master")) {

                dao = new open.dolphin.dao.SqlMasterDao();
                dao.setDriver(POSTGRESQL_DRIVER);
                dao.setHost(Project.getString(Project.CLAIM_ADDRESS));
                dao.setPort(POSTGRESQL_PORT);
                dao.setDatabase(DB_ORAC);
                dao.setUser(USER_ORCA);
                dao.setPasswd(PASSWORD_ORCA);
            }
            
        } catch (Exception e) {
            dao = null;
            e.printStackTrace(System.err);
        }

        return dao;
    }
}
