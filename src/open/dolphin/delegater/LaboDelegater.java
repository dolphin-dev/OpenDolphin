package open.dolphin.delegater;

import java.util.Collection;

import javax.naming.NamingException;

import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.ejb.RemoteLaboService;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.PatientModel;

/**
 * Labo 関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LaboDelegater extends BusinessDelegater {
    
    /**
     * LaboModule を保存する。
     * @param laboModuleValue
     * @return LaboImportReply
     */
    public PatientModel putLaboModule(LaboModuleValue value) {
        
        try {
            return getService().putLaboModule(value);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    public Collection getLaboModules(LaboSearchSpec spec) {
        
        Collection c = null;
        
        try {
            c = getService().getLaboModuless(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return c;
    }
    
    private RemoteLaboService getService() throws NamingException {
        return (RemoteLaboService) getService("RemoteLaboService");
    }
}
