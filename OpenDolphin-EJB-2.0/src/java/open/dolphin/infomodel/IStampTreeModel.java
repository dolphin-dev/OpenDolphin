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
package open.dolphin.infomodel;

import java.util.Date;

/**
 * StampTreeModel
 * Userのパーソナルツリークラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface IStampTreeModel {
    
    public long getId();
    
    public void setId(long id);
    
    public UserModel getUserModel();
    
    public void setUserModel(UserModel user);
    
    public String getName();
    
    public void setName(String name);
    
    public String getPublishType();
    
    public void setPublishType(String publishType);
    
    public String getCategory();
    
    public void setCategory(String category);
    
    public String getPartyName();
    
    public void setPartyName(String partyName);
    
    public String getUrl();
    
    public void setUrl(String url);
    
    public String getDescription();
    
    public void setDescription(String description);
    
    public Date getPublishedDate();
    
    public void setPublishedDate(Date lastUpdated);
    
    public Date getLastUpdated();
    
    public void setLastUpdated(Date lastUpdated);
    
    public byte[] getTreeBytes();
    
    public void setTreeBytes(byte[] treeBytes);
    
    public String getTreeXml();
    
    public void setTreeXml(String treeXml);
}
