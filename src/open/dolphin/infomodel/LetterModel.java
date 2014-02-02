package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 紹介状モデル。
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="docType",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("Letter")
@Table(name = "d_letter")
public class LetterModel extends KarteEntryBean {
    
    // ASP サーバへ配備する時、コメントアウトしてはいけない
    @Lob
    @Column(nullable=false, length=1048576)
    private byte[] beanBytes;
    
    /** Creates a new instance of LetterModel */
    public LetterModel() {
    }
    
    public byte[] getBeanBytes() {
        return beanBytes;
    }

    public void setBeanBytes(byte[] beanBytes) {
        this.beanBytes = beanBytes;
    }
}
