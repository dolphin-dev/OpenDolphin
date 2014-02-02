package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 初診時情報クラス。
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="docType",
    discriminatorType=DiscriminatorType.STRING
)
@Table(name = "d_first_encounter")
public class FirstEncounterModel extends KarteEntryBean {
        
    //@Lob ASP サーバへ配備する時、コメントアウトしてはいけない
    @Column(nullable=false)
    private byte[] beanBytes;
    
    /** Creates a new instance of FirstEncounterModel */
    public FirstEncounterModel() {
    }

    public byte[] getBeanBytes() {
        return beanBytes;
    }

    public void setBeanBytes(byte[] beanBytes) {
        this.beanBytes = beanBytes;
    }
}
