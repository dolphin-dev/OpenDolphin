/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import open.dolphin.utilities.common.OrcaApi;

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
        return acceptlst(date, "", "", "", OrcaConnect.KIND_03);
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
        return acceptmod(patientID, acceptID, departCode, physicianCode, OrcaConnect.KIND_02);
    }
}
