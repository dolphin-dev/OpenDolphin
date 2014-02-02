package open.dolphin.client;

import java.util.Date;

/**
 * Parametrs to save document.
 * (予定カルテ対応)
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SaveParamsM {
    
    public static final int NEW_KARTE       = 0;
    public static final int FINAL_MODIFY    = 1;
    public static final int TMP_MODIFY      = 2;
    public static final int SCHEDULE_MODIFY = 3;
    public static final int SCHEDULE_SCHEDULE = 4;
    public static final int SAVE_AS_FINAL   = 0;
    public static final int SAVE_AS_TMP     = 1;
    //public static final int SAVE_AS_SCHEDULE = 2;
    
    // 文書タイトル
    private String title;
    
    // 編集元のタイトル
    private String oldTitle;
    
    // 診療科情報
    private String department;
    
    // 印刷部数
    private int printCount = -1;

    // MML送信するかどうかのフラグ 送信する時 true
    private boolean sendMML;
    
    // 患者への参照を許可するかどうかのフラグ 許可するとき true
    private boolean allowPatientRef;
    
    // 診療歴のある施設への参照許可フラグ 許可する時 true
    private boolean allowClinicRef;
    
    // 仮保存の時 true
    private boolean tmpSave;
    
    // CLAIM 送信フラグ
    private boolean sendClaim;
    
    // CLAIM 送信を disable にする
    private boolean sendEnabled;

    // 検体検査オーダー送信フラグ
    private boolean sendLabtest;

    // 保存しようとするカルテに検体検査があるかどうか
    private boolean hasLabtest;
    
//minagawa^ CLAIM送信日
    private Date claimDate;
//minagawa$    
    
    // 保存ダイアログ開始時のカルテ属性
    private int enterOption;
    
    // 保存ダイアログ終了後のカルテ保存オプション
    private int returnOption;
   
    public String getOldTitle() {
        return oldTitle;
    }
    public void setOldTitle(String title) {
        oldTitle = title;
    }
    
    /** 
     * Creates new SaveParams 
     */
    public SaveParamsM() {
    }
    
    public SaveParamsM(boolean sendMML) {
        this();
        this.sendMML = sendMML;
    }
    
    public boolean getSendMML() {
        return isSendMML();
    }
    
    public boolean isSendMML() {
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

    public boolean isSendEnabled() {
        return sendEnabled;
    }

    public void setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
    }

    public boolean isSendLabtest() {
        return sendLabtest;
    }

    public void setSendLabtest(boolean sendLabtestOrder) {
        this.sendLabtest = sendLabtestOrder;
    }

    public boolean isHasLabtest() {
        return hasLabtest;
    }

    public void setHasLabtest(boolean sendLabtestEnabled) {
        this.hasLabtest = sendLabtestEnabled;
    }

//minagawa^ CLAIM送信日    
    public Date getClaimDate() {
        return claimDate;
    }
    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }   
//minagawa$ 
    
    public int getEnterOption() {
        return enterOption;
    }

    public void setEnterOption(int enterOption) {
        this.enterOption = enterOption;
    }

    public int getReturnOption() {
        return returnOption;
    }

    public void setReturnOption(int returnOption) {
        this.returnOption = returnOption;
    }
}