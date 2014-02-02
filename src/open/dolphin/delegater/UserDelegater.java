/*
 * StatusPanel.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
    public UserModel login(DolphinPrincipal principal, String password) {
        
        UserModel ret = null;
        
        try {
            String pk = principal.getFacilityId() + ":" + principal.getUserId();
            UsernamePasswordHandler h = new UsernamePasswordHandler(pk, password.toCharArray());
            LoginContext lc = new LoginContext(getSecurityDomain(), h);
            lc.login();
            
            ret = getUser(pk);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return ret;
    }
    
    /**
     * ユーザ認証を行う。
     * @return  UserModel
     */
    public void reLogin(DolphinPrincipal principal, String password) {
        
        UserModel ret = null;
        
        try {
            String pk = principal.getFacilityId() + ":" + principal.getUserId();
            UsernamePasswordHandler h = new UsernamePasswordHandler(pk, password.toCharArray());
            LoginContext lc = new LoginContext(getSecurityDomain(), h);
            lc.login();
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
    }
    
    /**
     * ユーザを検索して返す。
     * @param userId
     * @return UserModel
     */
    public UserModel getUser(String pk) {
        
        try {
            return getService().getUser(pk);
            
        }  catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    public ArrayList<UserModel> getAllUser() {
        
        try {
            Collection c = getService().getAllUser();
            ArrayList<UserModel> ret = new ArrayList<UserModel>();
            
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                UserModel user = (UserModel)iter.next();
                ret.add(user);
            }
            
            return ret;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    /**
     * ユーザを保存する。
     * @param userModel
     * @return
     */
    public int putUser(UserModel userModel) {
        
        int retCode = 0;
        
        try {
            retCode = getService().addUser(userModel);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    public int updateUser(UserModel userModel) {
        
        int retCode = 0;
        
        try {
            retCode = getService().updateUser(userModel);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    public int removeUser(String uid) {
        
        int retCode = 0;
        
        try {
            retCode = getService().removeUser(uid);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    public int updateFacility(UserModel user) {
        
        int retCode = 0;
        
        try {
            retCode = getService().updateFacility(user);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retCode;
    }
    
    private RemoteUserService getService() throws NamingException {
        return (RemoteUserService)getService("RemoteUserService");
    }
}
