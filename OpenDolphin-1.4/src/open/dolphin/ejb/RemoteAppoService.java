package open.dolphin.ejb;

import java.util.Collection;

import open.dolphin.dto.AppointSpec;
import open.dolphin.dto.ModuleSearchSpec;

/**
 * RemoteAppoService
 *
 * @author Minagawa,Kazushi
 *
 */
public interface RemoteAppoService {
    
    /**
     * 予約を保存、更新、削除する。
     * @param spec 予約情報の DTO
     */
    public void putAppointments(AppointSpec spec);
    
    /**
     * 予約を検索する。
     * @param spec 検索仕様
     * @return 予約の Collection
     */
    public Collection getAppointmentList(ModuleSearchSpec spec);
    
}
