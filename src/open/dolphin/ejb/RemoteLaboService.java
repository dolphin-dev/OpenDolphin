package open.dolphin.ejb;

import java.util.Collection;

import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.PatientModel;

/**
 * RemoteLaboService
 *
 * @author Minagawa,Kazushi
 */
public interface RemoteLaboService {
    
    /**
     * LaboModuleを保存する。
     * @param laboModuleValue LaboModuleValue
     */
    public PatientModel putLaboModule(LaboModuleValue laboModuleValue);
    
    /**
     * 患者の検体検査モジュールを取得する。
     * @param spec LaboSearchSpec 検索仕様
     * @return laboModule の Collection
     */
    public Collection getLaboModuless(LaboSearchSpec spec);
    
}
