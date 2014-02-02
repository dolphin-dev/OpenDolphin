/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.common;

/**
 *
 * @author Life Sciences Computing Corporation.
 */
public class OrcaConnect extends OrcaApi {
    /**
     * コンストラクタ
     */
    public OrcaConnect(String host, String port, String user, String pass, String ver) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        if(ver != null) {
            this.ver = ver;
        }else{
            this.ver = ORCAAPI_VER_47;
        }
        setVerInfo();
    }
    
    /**
     * ORCAの受付リストの取得
     * @param date 日付
     * @return 
     */
    public String getOrcaAcceptListAll(String date) {
        //OrcaConnect orca = new OrcaConnect("172.31.210.193", "8000", "ormaster", "ormaster123", "47");
        //String str = orca.getOrcaAcceptListAll("2013-06-06");
        //String str = diseaseget("10001", "2013-06");
        //1 Acceptance_Date 受付日 2010-12-20
        //2 Department_Code 診療科コード ※１(01:内科) 01 ※２
        //3 Physician_Code ドクタコード 10001 ※２
        //4 Medical_Information 診療内容区分 ※３(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01 ※２
        StringBuilder data = new StringBuilder();
        data.append(TAG_DATA_START);
        data.append(TAG_ACCEPTLST_START);
        data.append(TAG_ACCEPTDATE_START);
        data.append(date);
        data.append(TAG_ACCEPTDATE_END);
        data.append(TAG_DEPTCODE_START);
        data.append("");
        data.append(TAG_DEPTCODE_END);
        data.append(TAG_PHYSICODE_START);
        data.append("");
        data.append(TAG_PHYSICODE_END);
        data.append(TAG_MEDICALINFO_START);
        data.append("");
        data.append(TAG_MEDICALINFO_END);
        data.append(TAG_ACCEPTLST_END);
        data.append(TAG_DATA_END);
        
        return acceptlst(CLASS_03, data.toString());
    }
    
    /**
     * ORCAの受付情報の削除
     * @param patientID 患者ID
     * @param acceptID 受付ID
     * @param departCode 診療科コード
     * @param physicianCode ドクターコード
     * @return 
     */
    public String deleteOrcaAccept(String patientID, String acceptID, String departCode, String physicianCode) {
        //1 Patient_ID 患者番号 00012 必須
        //2 Acceptance_Id 受付ID   必須(受付取消のみ)
        //3 Department_Code 診療科コード ※１(01:内科) 01 必須(受付登録のみ)
        //4 Physician_Code ドクターコード 10001 必須(受付登録のみ)
        StringBuilder data = new StringBuilder();
        data.append(TAG_DATA_START);
        data.append(TAG_ACCEPTREQ_START);
        data.append(TAG_PATID_START);
        data.append(patientID);
        data.append(TAG_PATID_END);
        data.append(TAG_ACCEPTID_START);
        data.append(acceptID);
        data.append(TAG_ACCEPTID_END);
        data.append(TAG_DEPTCODE_START);
        data.append(departCode);
        data.append(TAG_DEPTCODE_END);
        data.append(TAG_PHYSICODE_START);
        data.append(physicianCode);
        data.append(TAG_PHYSICODE_END);
        data.append(TAG_ACCEPTREQ_END);
        data.append(TAG_DATA_END);
        
        return acceptmod(CLASS_02, data.toString());
    }
}
