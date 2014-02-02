package open.dolphin.infomodel;

import java.util.Date;

/**
 *
 * @author Kazushi Minagawa.
 */
public class KarteNumber extends InfoModel implements java.io.Serializable {
    
    // KarteBeanのPK
    private long karteNumber;
    
    // システム登録日
    private Date created;
    
    // 保健医療機関コードとJMARIコードの連結
    private String number;

    public long getKarteNumber() {
        return karteNumber;
    }

    public void setKarteNumber(long karteNumber) {
        this.karteNumber = karteNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
