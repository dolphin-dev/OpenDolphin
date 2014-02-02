package open.dolphin.delegater;

import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 *
 * @author Kazushi Minagawa
 */
public class OrcaDelegaterFactory {
    
    public static OrcaDelegater create() {
        
        // マスター検索が可能かどうか
        if (Project.canSearchMaster()) {

            if (Project.claimSenderIsClient()) {
                // CLIENT-ORCA OrcaSqlDelegater
                return (OrcaDelegater)create("open.dolphin.dao.OrcaSqlDelegater");

            } else if (Project.claimSenderIsServer()) {
                // SERVER-ORCA OrcaRestDelegater
                return (OrcaDelegater)create("open.dolphin.delegater.OrcaRestDelegater");
            }
        }
        
        return null;
    }
    
    private static Object create(String clsName) {
        try {
            return Class.forName(clsName).newInstance();
        } catch (InstantiationException ex) {
            Log.outputFuncLog(Log.LOG_LEVEL_0,"I",System.err.toString());
            ex.printStackTrace(System.err);
        } catch (IllegalAccessException ex) {
            Log.outputFuncLog(Log.LOG_LEVEL_0,"I",System.err.toString());
            ex.printStackTrace(System.err);
        } catch (ClassNotFoundException ex) {
            Log.outputFuncLog(Log.LOG_LEVEL_0,"I",System.err.toString());
            ex.printStackTrace(System.err);
        }
        return null;
    }
}
