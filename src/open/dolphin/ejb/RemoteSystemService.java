package open.dolphin.ejb;

import java.util.Collection;

import open.dolphin.infomodel.UserModel;

/**
 * RemoteSystemService
 *
 * @author Minagawa, Kazushi
 */
public interface RemoteSystemService {
    
    /**
     * 次のOIDを取得する。
     *
     * @return OID
     */
    public String helloDolphin();
    
    /**
     * 施設と管理者情報を登録する。
     * @param user 施設管理者
     */
    public void addFacilityAdmin(UserModel user);
    
    /**
     * 用法マスタを登録する。
     */
    public void putAdminMaster(Collection c);
    
    /**
     * 用法コメントマスタを登録する。
     */
    public void putAdminComentMaster(Collection c);
    
    /**
     * 放射線メソッドマスタを登録する。
     */
    public void putRadMethodMaster(Collection c);
    
    
}
