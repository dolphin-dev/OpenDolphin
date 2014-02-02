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
    public static SqlDaoBean create(Object o, String keyString) {
        
        SqlDaoBean dao = null;
        
        try {            
            dao = new open.dolphin.dao.SqlMasterDao();
            
            dao.setDriver("org.postgresql.Driver");
            
            if (keyString.equals("dao.master")) {
                dao.setHost(Project.getClaimAddress());
                dao.setPort(5432) ;
                dao.setDatabase("orca");
                dao.setUser("orca");
                dao.setPasswd("");
                      
            } else {
                dao.setHost(Project.getHostAddress());
                dao.setPort(5432) ;
                dao.setDatabase("dolphin");
                dao.setUser("dolphin");
                dao.setPasswd("");
            }
            
        } catch (Exception e) {
            //assert false : e;
            System.out.println(e);
            e.printStackTrace();
        }
        return dao;
    }
}
