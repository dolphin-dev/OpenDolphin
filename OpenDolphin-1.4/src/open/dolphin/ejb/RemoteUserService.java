package open.dolphin.ejb;

import java.util.Collection;

import open.dolphin.infomodel.UserModel;

/**
 * RemoteUserService
 *
 * @author Minagawa, Kazushi
 */
public interface RemoteUserService {
    
    /**
     * 施設管理者が院内Userを登録する。
     * @param add 登録するUser
     * @return 追加件数
     */
    public int addUser(UserModel add);
    
    /**
     * Userを検索する。
     * @param userId 検索するユーザの複合キー
     * @return 該当するUser
     */
    public UserModel getUser(String userId);
    
    /**
     * 施設内の全Userを取得する。
     * @return 施設内ユーザリスト
     */
    public Collection<UserModel> getAllUser();
    
    /**
     * User情報(パスワード等)を更新する。
     * @param update 更新するUser detuched
     * @return 更新件数
     */
    public int updateUser(UserModel update);
    
    /**
     * Userを削除する。
     * @param removeId 削除するユーザのId
     * @return 削除件数
     */
    public int removeUser(String removeId);
    
    /**
     * 施設情報を更新する。
     * @param update 更新するUser detuched
     */
    public int updateFacility(UserModel update);
    
    /**
     * サポートライセンスを購入する。
     * @param purchase detuched User
     */
    public void purchase(UserModel purchase);
    
}
