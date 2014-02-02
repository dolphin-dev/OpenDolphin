package open.dolphin.infomodel;

import javax.persistence.*;

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
public class FirstEncounterModel extends KarteEntryBean implements java.io.Serializable {
        
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
