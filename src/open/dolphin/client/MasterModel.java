/*
 * MasterModel.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.client;

import java.beans.*;
import java.util.*;
import java.util.prefs.*;

import open.dolphin.dao.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MasterModel {
    
    public static final String TT_BUSY_PROP = "busyProp";
    public static final String TT_COLLECTION_PROP = "collectionProp";
    
    String master;
    
    boolean bStartsWith;
    
    String searchCode;
    
    String sortBy;
    
    String order;
    
    String keyword;
    
    int pageLimit;
    
    int pageCount;
    
    int curPage;
    
    int totalCount;
    
    int curCount;
    
    boolean busy;
    
    String message;
    
    ArrayList collection;
    
    PropertyChangeSupport boundSupport;
    
    /** Creates a new instance of MasterModel */
    public MasterModel() {
    }
    
    public MasterModel(String master) {
        this();
        this.master = master;
        
        Preferences prefs = ClientContext.getPreferences();
        bStartsWith = prefs.getBoolean("masterSearch." + master + "startsWith", false);
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }    
    
    public String getMaster() {
        return master;
    }
    
    public void setMaster(String master) {
        this.master = master;
    }
    
    protected void setBusy(boolean b) {
        busy = b;
        boundSupport.firePropertyChange(TT_BUSY_PROP, !busy, busy);
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String msg) {
        message = msg;
    }
    
    protected void start(String msg) {
        setMessage(msg);
        setBusy(true);
    }
    
    protected void stop(String msg) {
        message = msg;
        setBusy(false);
    }
    
    public void setStartsWith(boolean b) {
        if (bStartsWith != b) {
            bStartsWith = b;
            Preferences prefs = ClientContext.getPreferences();
            prefs.getBoolean("masterSearch." + master + "startsWith", bStartsWith);
        }
    }
    
    public boolean isStartsWith() {
        return bStartsWith;
    }
    
    public void setSearchCode(String code) {
        searchCode = code;
    }
    
    public void setSortBy(String code) {
        sortBy = code;
    } 
    
    public void setOrder(String code) {
        order = code;
    }         
    
    public void setKeyword(String key) {
        
        keyword = key;
        
        final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
        if (dao == null) {
            return;
        }

        Runnable r = new Runnable() {
            
            public void run() {
                
                start("検索しています...");
                clearPageParams();
                
                ArrayList list = dao.getByName(master, keyword, bStartsWith, searchCode, sortBy, order);
                
                int retCode = dao.getResultCode();
                
                if (retCode == SqlDaoBean.TT_NO_ERROR) {
                    setPageParams(dao.getTotalCount(), list.size());
                    setCollection(list);
                    stop("検索が終わりました");
                
                } else if (retCode == SqlDaoBean.TT_CONNECTION_ERROR) {
                    setCollection(null);
                    stop("データベースに接続できません");
                
                } else if (retCode == SqlDaoBean.TT_DATABASE_ERROR) {
                    setCollection(null);
                    stop("エラーが生じています");
                }
            }
        };
        
        Thread t = new Thread(r);
        t.start();
    }
    
    public void setCollection(ArrayList list) {
        collection = list;
        boundSupport.firePropertyChange(TT_COLLECTION_PROP, null, collection);
    }
    
    protected void clearPageParams() {
        curPage = 0;
        totalCount = 0;
        pageCount = 0;
    }
    
    protected void setPageParams(int tc, int cc) {
        totalCount = tc;
        curCount = cc;
        pageCount = totalCount / pageLimit;
        pageCount = ( (totalCount % pageLimit) != 0 ) ? pageCount++ : pageCount;
        curPage = 1;
    }
    
    public boolean hasNextPage() {
        if (pageCount == 1) {
            return false;
        }
        
        return ( (curPage + 1) <= pageCount ) ? true : false;
    }
    
    public boolean hasPreviousPage() {
        if (pageCount == 1) {
            return false;
        }
        
        return ( (curPage - 1) >= 1 ) ? true : false;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public int getCurrentCount() {
        return curCount;
    }
    
    public int getPageCount() {
        return pageCount;
    }
    
    public int getCurrentPage() {
        return curPage;
    }
        
    public void setPageLimit(int limit) {
        this.pageLimit = limit;
    }
}