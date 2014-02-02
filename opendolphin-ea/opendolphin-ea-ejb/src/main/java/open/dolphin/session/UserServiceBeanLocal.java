/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author kazushi
 */
@Local
public interface UserServiceBeanLocal {

    public UserModel getUser(String userId);

    public List<UserModel> getAllUser(String fid);

    public int updateUser(UserModel update);

    public int removeUser(String removeId);

    public int updateFacility(UserModel update);

    public int addUser(UserModel add);

    public boolean authenticate(java.lang.String userName, java.lang.String password);
    
}
