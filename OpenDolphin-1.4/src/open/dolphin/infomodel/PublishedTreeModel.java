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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * PublishedTreeModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name="d_published_tree")
public class PublishedTreeModel extends InfoModel implements IStampTreeModel {
    
    private static final long serialVersionUID = -1402248987372246092L;
    
    @Id
    private long id;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserModel user;
    
    // TreeSet‚Ì–¼Ì
    @Column(nullable=false)
    private String name;
    
    // OID or Public
    // OID ‚ÌŽž‚ÍŽ{Ý—p
    @Column(nullable=false)
    private String publishType;
    
    // Tree‚ÌƒJƒeƒSƒŠ
    @Column(nullable=false)
    private String category;
    
    // ’c‘Ì–¼“™
    @Column(nullable=false)
    private String partyName;
    
    // URL
    @Column(nullable=false)
    private String url;
    
    // à–¾
    @Column(nullable=false)
    private String description;
    
    // ŒöŠJ‚µ‚½“ú
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date publishedDate;
    
    @Transient
    private String treeXml;
    
    @Column(nullable=false)
    @Lob
    private byte[] treeBytes;
    
    // XV‚µ‚½“ú
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date lastUpdated;
    
    @Transient
    private boolean imported;
    
//    @Version
//    private int version;
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public UserModel getUser() {
        return user;
    }
    
    @Override
    public void setUser(UserModel user) {
        this.user = user;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getPublishType() {
        return publishType;
    }
    

    @Override
    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }
    
    @Override
    public String getCategory() {
        return category;
    }
    
    @Override
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String getPartyName() {
        return partyName;
    }
    
    @Override
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public Date getPublishedDate() {
        return publishedDate;
    }
    
    @Override
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    @Override
    public byte[] getTreeBytes() {
        return treeBytes;
    }
    
    @Override
    public void setTreeBytes(byte[] treeBytes) {
        this.treeBytes = treeBytes;
    }
    
    @Override
    public String getTreeXml() {
        return treeXml;
    }
    
    @Override
    public void setTreeXml(String treeXml) {
        this.treeXml = treeXml;
    }
    
    @Override
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    @Override
    public void setLastUpdated(Date updatedDate) {
        this.lastUpdated = updatedDate;
    }
    
    public boolean isImported() {
        return imported;
    }
    
    public void setImported(boolean imported) {
        this.imported = imported;
    }
    
//    public int getVersion() {
//        return version;
//        return 0;
//    }
//    
//    public void setVersion(int version) {
//        //this.version = version;
//    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
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
        final PublishedTreeModel other = (PublishedTreeModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
