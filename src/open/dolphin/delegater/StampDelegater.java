/*
 * StampDelegater.java
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
import java.util.List;

import javax.naming.NamingException;

import open.dolphin.ejb.RemoteStampService;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 * Stamp関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampDelegater extends BusinessDelegater {
    
    /**
     * StampTree を保存/更新する。
     * @param model 保存する StampTree
     * @return 保存個数
     */
    public long putTree(IStampTreeModel model) {
        
        try {
            // Tree の XML を byte[] へ変換する
            model.setTreeBytes(model.getTreeXml().getBytes("UTF-8"));  // UTF-8 bytes
            return getService().putTree((StampTreeModel) model);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    /**
     * User のスタンプツリーを読み込む。
     * @param userPk ログインユーザの db キー
     */
    public List<IStampTreeModel> getTrees(long userPk) {
        
        List<IStampTreeModel> treeList = null;
        
        try {
            treeList = getService().getTrees(userPk);
            if (treeList != null && treeList.size() > 0) {
                for (IStampTreeModel model : treeList) {
                    
                    //
                    // StampTree の xml 定義を復元する
                    //
                    String treeDef = new String(model.getTreeBytes(), "UTF-8");
                    
                    // 
                    // 修復用のコード 一回のみ
                    //
                    //treeDef = treeDef.replaceAll("&", "&amp;");
                    
                    model.setTreeXml(treeDef);
                    
                    model.setTreeBytes(null);
                }
            }
            return treeList;
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return null;
    }
    
    
    /**
     * 個人用のStampTreeを保存し公開する。
     * @param model 個人用のStampTreeで公開するもの
     * @return id
     */
    public long saveAndPublishTree(StampTreeModel model, byte[] publishBytes) {
        
        try {
            model.setTreeBytes(model.getTreeXml().getBytes("UTF-8"));  // UTF-8 bytes
            return getService().saveAndPublishTree(model, publishBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0L;
    }
    
    /**
     * 既に保存されている個人用のTreeを公開する。
     * @param model 既に保存されている個人用のTreeで公開するもの
     * @return 公開数
     */
    public int publishTree(StampTreeModel model, byte[] publishBytes) {
        
        try {
            model.setTreeBytes(model.getTreeXml().getBytes("UTF-8"));  // UTF-8 bytes
            return getService().publishTree(model, publishBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    /**
     * 公開されているTreeを更新する。
     * @param model 更新するTree
     * @return 更新数
     */
    public int updatePublishedTree(StampTreeModel model, byte[] publishBytes) {
        
        try {
            model.setTreeBytes(model.getTreeXml().getBytes("UTF-8"));  // UTF-8 bytes
            return getService().updatePublishedTree(model, publishBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    /**
     * 公開されているTreeを削除する。
     * @param id 削除するTreeのID
     * @return 削除数
     */
    public int cancelPublishedTree(StampTreeModel model) {
        
        try {
            model.setTreeBytes(model.getTreeXml().getBytes("UTF-8"));  // UTF-8 bytes
            return getService().cancelPublishedTree(model);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    public List<PublishedTreeModel> getPublishedTrees() {
        
        try {
            return getService().getPublishedTrees();
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return null;
    }
    
    public List<Long> subscribeTrees(List<SubscribedTreeModel> subscribeList) {
        
        try {
            return getService().subscribeTrees(subscribeList);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return null;
    }
    
    
    public int unsubscribeTrees(List<SubscribedTreeModel> removeList) {
        
        try {
            return getService().unsubscribeTrees(removeList);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        return 0;
    }
    
    
    
    /**
     * ASP StampTreeを取得する。
     * @return ユーザの StampTree
     */
    public StampTreeModel getAspTree(String treemanager) {
        
        StampTreeModel tree = null;
        
        try {
            tree = getService().getAspTree(treemanager);
            if (tree != null) {
                tree.setTreeXml(new String(tree.getTreeBytes(), "UTF-8"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return tree;
    }
    
    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    public List<String> putStamp(List<StampModel> list) {
        
        try {
            return getService().putStamp(list);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    /**
     * Stampを保存する。
     * @param model StampModel
     * @return 保存件数
     */
    public String putStamp(StampModel model) {
        
        try {
            return getService().putStamp(model);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public StampModel getStamp(String stampId) {
        
        try {
            return getService().getStamp(stampId);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    /**
     * Stampを取得する。
     * @param stampId 取得する StampModel の id
     * @return StampModel
     */
    public List<StampModel> getStamp(List<ModuleInfoBean> list) {
        
        List<StampModel> retList = null;
        
        List<String> ids = new ArrayList<String>(list.size());
        for (ModuleInfoBean info : list) {
            ids.add(info.getStampId());
        }
        
        try {
            retList = getService().getStamp(ids);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return retList;
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(String stampId) {
        
        try {
            return getService().removeStamp(stampId);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    /**
     * Stampを削除する。
     * @param stampId 削除する StampModel の id
     * @return 削除件数
     */
    public int removeStamp(List<String> ids) {
        
        try {
            return getService().removeStamp(ids);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return 0;
    }
    
    private RemoteStampService getService() throws NamingException {
        return (RemoteStampService) getService("RemoteStampService");
    }
}
