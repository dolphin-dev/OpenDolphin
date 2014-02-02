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
public class StampTreeModel extends InfoModel implements IStampTreeModel,java.io.Serializable {
    
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
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public UserModel getUserModel() {
        return user;
    }
    
    @Override
    public void setUserModel(UserModel user) {
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
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    @Override
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getPublished() {
        return published;
    }
    
    public void setPublished(String published) {
        this.published = published;
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
