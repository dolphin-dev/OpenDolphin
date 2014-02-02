/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 *
 * @author kazushi
 */
@Local
public interface StampServiceBeanLocal {

    public long putTree(StampTreeModel model);

    public List<IStampTreeModel> getTrees(long userPk);

    public long saveAndPublishTree(StampTreeModel model, byte[] publishBytes);

    public int publishTree(StampTreeModel model, byte[] publishBytes);

    public int updatePublishedTree(StampTreeModel model, byte[] publishBytes);

    public int cancelPublishedTree(StampTreeModel model);

    public List<PublishedTreeModel> getPublishedTrees(String fid);

    public List<Long> subscribeTrees(List<SubscribedTreeModel> addList);

    public int unsubscribeTrees(List<Long> list);

    //public StampTreeModel getAspTree(String managerId);

    //--------------------------------------------------//

    public List<String> putStamp(List<StampModel> list);

    public String putStamp(StampModel model);

    public StampModel getStamp(String stampId);

    public List<StampModel> getStamp(List<String> ids);

    public int removeStamp(String stampId);

    public int removeStamp(List<String> ids);

    public long saveAndPublishTree(java.util.List<open.dolphin.infomodel.IStampTreeModel> list);

    public int updatePublishedTree(java.util.List<open.dolphin.infomodel.IStampTreeModel> list);
    
}
