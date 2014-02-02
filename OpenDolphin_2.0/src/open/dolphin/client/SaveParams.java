package open.dolphin.client;

/**
 * Parametrs to save document.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SaveParams {
    
    // MML送信するかどうかのフラグ 送信する時 true
    private boolean sendMML;
    
    // 文書タイトル
    private String title;
    
    // 診療科情報
    private String department;
    
    // 印刷部数
    private int printCount = -1;
    
    // 患者への参照を許可するかどうかのフラグ 許可するとき true
    private boolean allowPatientRef;
    
    // 診療歴のある施設への参照許可フラグ 許可する時 true
    private boolean allowClinicRef;
    
    // 仮保存の時 true
    private boolean tmpSave;
    
    // CLAIM 送信フラグ
    private boolean sendClaim;
    
    // CLAIM 送信を disable にする
    private boolean disableSendClaim;

    
    /** 
     * Creates new SaveParams 
     */
    public SaveParams() {
        super();
    }
    
    public SaveParams(boolean sendMML) {
        this();
        this.sendMML = sendMML;
    }
    
    public boolean getSendMML() {
        return sendMML;
    }
    
    public void setSendMML(boolean b) {
        sendMML = b;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String val) {
        department = val;
    }
    
    public int getPrintCount() {
        return printCount;
    }
    
    public void setPrintCount(int val) {
        printCount = val;
    }
    
    public boolean isAllowPatientRef() {
        return allowPatientRef;
    }
    
    public void setAllowPatientRef(boolean b) {
        allowPatientRef = b;
    }
    
    public boolean isAllowClinicRef() {
        return allowClinicRef;
    }
    
    public void setAllowClinicRef(boolean b) {
        allowClinicRef = b;
    }

    public boolean isTmpSave() {
        return tmpSave;
    }

    public void setTmpSave(boolean tmpSave) {
        this.tmpSave = tmpSave;
    }
    
    public boolean isSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(boolean sendClaim) {
        this.sendClaim = sendClaim;
    }

    public boolean isDisableSendClaim() {
        return disableSendClaim;
    }

    public void setDisableSendClaim(boolean disableSendClaim) {
        this.disableSendClaim = disableSendClaim;
    }
}