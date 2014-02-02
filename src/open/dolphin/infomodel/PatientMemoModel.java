package open.dolphin.infomodel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * MemoModel
 *
 * @author Minagawa, Kazushi
 *
 */
@Entity
@Table(name = "d_patient_memo")
public class PatientMemoModel extends KarteEntryBean {
    
    private static final long serialVersionUID = 5125449675384830669L;
    
    private String memo;
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
}
