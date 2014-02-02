package open.dolphin.infomodel;

/**
 * Appoint
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Appoint extends InfoModel {

    // 予約名
    private String appName;
    
    // メモ
    private String memo;

    /** Creates new Appoint */
    public Appoint() {
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setAppName(String appName) {
            this.appName = appName;
    }

    public String getAppName() {
            return appName;
    }
}