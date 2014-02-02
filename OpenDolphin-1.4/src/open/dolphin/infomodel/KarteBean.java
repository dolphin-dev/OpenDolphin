package open.dolphin.infomodel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * KarteBean
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_karte")
public class KarteBean extends InfoModel {
    
    private static final long serialVersionUID = 4658519288418950016L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
    private PatientModel patient;
    
    @Transient
    private Map<String, List> entries;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date created;
    
    /**
     * Idを返す。
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * Idを設定する。
     * @param id カルテのId
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * 患者を返す。
     * @return このカルテのオーナー
     */
    public PatientModel getPatient() {
        return patient;
    }
    
    /**
     * 患者を設定する。
     * @param patient このカルテのオーナー
     */
    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }
    
    /**
     * このカルテの作成日を返す。
     * @return カルテの作成日
     */
    public Date getCreated() {
        return created;
    }
    
    /**
     * このカルテの作成日を設定する。
     * @param created カルテの作成日
     */
    public void setCreated(Date created) {
        this.created = created;
    }
    
    /**
     * カルテのエントリを返す。
     * @return カテゴリをKey、エントリのコレクションをValueにしたHashMap
     */
    public Map<String, List> getEntries() {
        return entries;
    }
    
    /**
     * カルテのエントリを設定する。
     * param entries カテゴリをKey、エントリのコレクションをValueにしたHashMap
     */
    public void setEntries(Map<String, List> entries) {
        this.entries = entries;
    }
    
    /**
     * 指定したカテゴリのエントリコレクションを返す。
     * @param category カテゴリ
     * @return　エントリのコレクション
     */
    public List getEntryCollection(String category) {
        return entries != null ? entries.get(category) : null;
    }
    
    /**
     * カテゴリとそのエントリのコレクションを追加する。
     * @param category カテゴリ
     * @param entries カテゴリのエントリーのコレクション
     */
    public void addEntryCollection(String category, List entrs) {
        
        if (entries == null) {
            entries = new HashMap<String, List>();
        }
        entries.put(category, entrs);
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
        final KarteBean other = (KarteBean) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
