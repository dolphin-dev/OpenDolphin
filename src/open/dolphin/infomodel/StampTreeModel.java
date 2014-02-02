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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * StampTreeModel
 * Userのパーソナルツリークラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name="d_stamp_tree")
public class StampTreeModel extends InfoModel implements IStampTreeModel {
    
    private static final long serialVersionUID = 4158667207942678250L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    // UserPK
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserModel user;
    
    // TreeSetの名称
    @Column(name="tree_name", nullable=false)
    private String name;
    
    // OID or Public
    // OID の時は施設用
    private String publishType;
    
    // Treeのカテゴリ
    private String category;
    
    // 団体名等
    private String partyName;
    
    // URL
    private String url;
    
    // 説明
    private String description;
    
    // 公開した日
    @Temporal(value = TemporalType.DATE)
    private Date publishedDate;
    
    // 最終更新日
    @Temporal(value = TemporalType.DATE)
    private Date lastUpdated;
    
    // 公開しているtreeのエンティティ
    private String published;
    
    @Transient
    private String treeXml;
    
    @Column(nullable=false)
    @Lob
    private byte[] treeBytes;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public UserModel getUser() {
        return user;
    }
    
    public void setUser(UserModel user) {
        this.user = user;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPublishType() {
        return publishType;
    }
    
    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPartyName() {
        return partyName;
    }
    
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getPublished() {
        return published;
    }
    
    public void setPublished(String published) {
        this.published = published;
    }
    
    public byte[] getTreeBytes() {
        return treeBytes;
    }
    
    public void setTreeBytes(byte[] treeBytes) {
        this.treeBytes = treeBytes;
    }
    
    public String getTreeXml() {
        return treeXml;
    }
    
    public void setTreeXml(String treeXml) {
        this.treeXml = treeXml;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final StampTreeModel other = (StampTreeModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
