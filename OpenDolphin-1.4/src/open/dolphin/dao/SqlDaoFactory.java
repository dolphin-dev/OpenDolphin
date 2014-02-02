package open.dolphin.dao;

import open.dolphin.project.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SqlDaoFactory {
    
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
                dao.setDriver("org.postgresql.Driver");
                dao.setHost(Project.getClaimAddress());
                dao.setPort(5432);
                dao.setDatabase("orca");
                dao.setUser("orca");
                dao.setPasswd("");         
            }
            
        } catch (Exception e) {
            dao = null;
            e.printStackTrace();
        }

        return dao;
    }
}
