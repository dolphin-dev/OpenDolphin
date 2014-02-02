package open.dolphin.delegater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;

import org.jboss.security.auth.callback.UsernamePasswordHandler;

import open.dolphin.ejb.RemoteUserService;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.DolphinPrincipal;

/**
 * User 関連の Business Delegater　クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class UserDelegater extends BusinessDelegater {
    
    /**
     * ユーザ認証を行う。
     * @return  UserModel
     */
    public UserModel login(DolphinPrincipal principal, String password) throws Exception {
        
        String pk = principal.getFacilityId() + ":" + principal.getUserId();
        UsernamePasswordHandler h = new UsernamePasswordHandler(pk, password.toCharArray());
        LoginContext lc = new LoginContext(getSecurityDomain(), h);
        lc.login();
        return getUser(pk);
        
//        UserModel ret = null;
//        
//        try {
//            String pk = principal.getFacilityId() + ":" + principal.getUserId();
//            UsernamePasswordHandler h = new UsernamePasswordHandler(pk, password.toCharArray());
//            LoginContext lc = new LoginContext(getSecurityDomain(), h);
//            lc.login();
//            
//            ret = getUser(pk);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//            throw new Exception(e);
//        }
//        
//        return ret;
    }
    
    /**
     * ユーザを検索して返す。
     * @param userId
     * @return UserModel
     */
    public UserModel getUser(String pk) throws Exception {
        
        return getService().getUser(pk);
        
//        try {
//            return getService().getUser(pk);
//            
//        }  catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return null;
    }
    
    public ArrayList<UserModel> getAllUser() throws Exception {
        
        Collection c = getService().getAllUser();
        ArrayList<UserModel> ret = new ArrayList<UserModel>();

        for (Iterator iter = c.iterator(); iter.hasNext(); ) {
            UserModel user = (UserModel) iter.next();
            ret.add(user);
        }

        return ret;
        
//        try {
//            Collection c = getService().getAllUser();
//            ArrayList<UserModel> ret = new ArrayList<UserModel>();
//            
//            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
//                UserModel user = (UserModel)iter.next();
//                ret.add(user);
//            }
//            
//            return ret;
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return null;
    }
    
    /**
     * ユーザを保存する。
     * @param userModel
     * @return
     */
    public int putUser(UserModel userModel) throws Exception {
        
        return getService().addUser(userModel);
        
//        int retCode = 0;
//        
//        try {
//            retCode = getService().addUser(userModel);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return retCode;
    }
    
    public int updateUser(UserModel userModel) throws Exception {
        
        return getService().updateUser(userModel);
        
//        int retCode = 0;
//        
//        try {
//            retCode = getService().updateUser(userModel);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return retCode;
    }
    
    public int removeUser(String uid) throws Exception {
        
        return getService().removeUser(uid);
        
//        int retCode = 0;
//        
//        try {
//            retCode = getService().removeUser(uid);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return retCode;
    }
    
    public int updateFacility(UserModel user) throws Exception {
        
        return getService().updateFacility(user);
        
//        int retCode = 0;
//        
//        try {
//            retCode = getService().updateFacility(user);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            processError(e);
//        }
//        
//        return retCode;
    }
    
    private RemoteUserService getService() throws NamingException {
        return (RemoteUserService)getService("RemoteUserService");
    }
}
