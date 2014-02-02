/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.common;

/**
 *
 * @author Life Sciences Computing Corporation.
 */
public class OrcaCommonDefImpl {
    protected static final String REQUESTMETHOD_GET = "GET";
    protected static final String REQUESTMETHOD_POST = "POST";
    protected static final String CHARSET_DEFAULT = "UTF-8";
    protected static final String CHARSET_SHIFTJIS = "Shift_JIS";
    
    protected static final String URL_HTTP = "http://";
    
    // APIの各パラメータ
    protected static final String ORCAAPI_VER_47 = "47";
    protected static final String CLASS_01 = "?class=01";
    protected static final String CLASS_02 = "?class=02";
    protected static final String CLASS_03 = "?class=03";
    protected static final String TAG_DATA_START = "<data>";
    protected static final String TAG_DATA_END = "</data>";
    protected static final String TAG_PATID_START = "<Patient_ID type=\"string\">";
    protected static final String TAG_PATID_END = "</Patient_ID>";
    protected static final String TAG_ACCEPTID_START = "<Acceptance_Id type=\"string\">";
    protected static final String TAG_ACCEPTID_END = "</Acceptance_Id>";
    protected static final String TAG_DEPTCODE_START = "<Department_Code type=\"string\">";
    protected static final String TAG_DEPTCODE_END = "</Department_Code>";
    protected static final String TAG_PHYSICODE_START = "<Physician_Code type=\"string\">";
    protected static final String TAG_PHYSICODE_END = "</Physician_Code>";
    protected static final String TAG_ACCEPTDATE_START = "<Acceptance_Date type=\"string\">";
    protected static final String TAG_ACCEPTDATE_END = "</Acceptance_Date>";
    protected static final String TAG_MEDICALINFO_START = "<Medical_Information type=\"string\">";
    protected static final String TAG_MEDICALINFO_END = "</Medical_Information>";
    
    protected static final String TAG_APIRESULT_START = "<Api_Result type=\"string\">";
    protected static final String TAG_APIRESULT_END = "</Api_Result>";
    protected static final String TAG_APIRESULTMSG_START = "<Api_Result_Message type=\"string\">";
    protected static final String TAG_APIRESULTMSG_END = "</Api_Result_Message>";
    
    // 受付一覧
    protected static final String ORCAAPI47_ACCEPTLIST = "/api01rv2/acceptlstv2";
    protected static final String TAG_ACCEPTLST_START = "<acceptlstreq type=\"record\">";
    protected static final String TAG_ACCEPTLST_END = "</acceptlstreq>";
    
    // 予約一覧
    protected static final String ORCAAPI47_APPOINTLIST = "/api01rv2/appointlstv2";
    
    // 患者病名返却
    protected static final String ORCAAPI47_GETDISEASE = "/api01rv2/diseasegetv2";
    
    // 患者予約情報
    protected static final String ORCAAPI47_APPOINTLIST2 = "/api01rv2/appointlst2v2";
    
    // 受付登録
    protected static final String ORCAAPI47_ACCEPTMOD = "/orca11/acceptmodv2";
    protected static final String TAG_ACCEPTREQ_START = "<acceptreq type=\"record\">";
    protected static final String TAG_ACCEPTREQ_END = "</acceptreq>";
}
