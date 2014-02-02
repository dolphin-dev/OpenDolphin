/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 * ORCA APIクラス
 * @author Life Sciences Computing Corporation.
 */
public class OrcaApi extends OrcaCommonDefImpl {
    
    // 受付一覧
    private String ACCEPTLIST;
    
    // 予約一覧
    private String APPOINTLIST;
    
    // 患者病名返却
    private String GETDISEASE;
    
    // 患者予約情報
    private String APPOINTLIST2;
    
    // 受付登録
    private String ACCEPTMOD;
    
    protected String host;
    protected String port;
    protected String user;
    protected String pass;
    protected String ver;
    
    protected String apiResult;
    protected String apiResultMessage;
    
    /**
     * コンストラクタ
     */
    protected OrcaApi() {
        Init();
    }
    
    /**
     * 初期化
     */
    void Init() {
        host = null;
        port = null;
        user = null;
        pass = null;
        ver = null;
    }
    
    protected void setVerInfo() {
        if(ver.equals(ORCAAPI_VER_47)) {
            ACCEPTLIST = ORCAAPI47_ACCEPTLIST;
            APPOINTLIST = ORCAAPI47_APPOINTLIST;
            GETDISEASE = ORCAAPI47_GETDISEASE;
            APPOINTLIST2 = ORCAAPI47_APPOINTLIST2;
            ACCEPTMOD = ORCAAPI47_ACCEPTMOD;
        }
    }
    
    /**
     * 受付一覧
     * @param param class=01:受付中取得,class=02:受付済み取得,class=03:全受付取得
     * @param data データ
     * @return 
     */
    protected String acceptlst(String param, String data) {
        //1 Information_Date 実施日 2011-03-13
        //2 Information_Time 実施時間 10:50:00
        //3 Api_Result 結果コード（ゼロ以外エラー） 00
        //4 Api_Result_Message エラーメッセージ 処理終了
        //5 Reskey   Patient Info
        //6 Acceptance_Date 受付日 2011-03-15
        //7 Acceptlst_Infomation 受付一覧情報 (繰り返し 500) 
        //7-1 Acceptance_Time 受付時間 15:30:00
        //7-2 Department_Code 診療科コード ※４(01:内科) 01
        //7-3 Department_WholeName 診療科名称 内科
        //7-4 Physician_Code ドクターコード 10001
        //7-5 Physician_WholeName ドクター名 日本　一
        //7-6 Medical_Information 診療内容区分 ※５(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01
        //7-7 Claim_Infometion claim情報 0
        //7-8 Account_Time 会計時間 15:50:00
        //7-9 Patient_Information 患者基本情報
        //7-9-1 Patient_ID 患者番号 00012
        //7-9-2 WholeName 患者氏名 日医　太郎
        //7-9-3 WholeName_inKana 患者カナ氏名 ニチイ　タロウ
        //7-9-4 BirthDate 生年月日 1975-01-01
        //7-9-5 Sex 性別(1:男性、2:女性) 1
        //7-10 HealthInsurance_Information 保険組合せ情報
        //7-10-1 InsuranceProvider_Class 保険の種類 060
        //7-10-2 InsuranceProvider_Number 保険者番号 138057
        //7-10-3 InsuranceProvider_WholeName 保険の制度名称 国保
        //7-10-4 HealthInsuredPerson_Symbol 記号 ０１
        //7-10-5 HealthInsuredPerson_Number 番号 １２３４５６７
        //7-10-6 HealthInsuredPerson_Continuation 継続区分(1:継続療養、 2:任意継続) 
        //7-10-7 HealthInsuredPerson_Assistance 補助区分(詳細については、「日医標準レセプトソフトデータベーステーブル定義書」を参照して下さい。) 3
        //7-10-8 RelationToInsuredPerson 本人家族区分(1:本人、 2:家族) 1
        //7-10-9 HealthInsuredPerson_WholeName 被保険者名 日医　太郎
        //7-10-10 Certificate_StartDate 適用開始日 2010-05-01
        //7-10-11 Certificate_ExpiredDate 適用終了日 9999-12-31
        //7-10-12 PublicInsurance_Information 公費情報（繰り返し 4）
        //7-10-12-1 PublicInsurance_Class 公費の種類 010
        //7-10-12-2 PublicInsurance_Name 公費の制度名称 感37の2
        //7-10-12-3 PublicInsurer_Number 負担者番号 10131142
        //7-10-12-4 PublicInsuredPerson_Number 受給者番号 1234566
        //7-10-12-5 Rate_Admission  入院ー負担率（割） 0.05
        //7-10-12-6 Money_Admission 入院ー固定額 0
        //7-10-12-7 Rate_Outpatient 外来ー負担率（割） 0.05
        //7-10-12-8 Money_Outpatient 外来ー固定額 0
        //7-10-12-9 Certificate_IssuedDate 適用開始日 2010-05-01
        //7-10-12-10 Certificate_ExpiredDate 適用終了日 9999-12-31
        return orcaSendRecv(ACCEPTLIST + param, data);
    }
    
    /**
     * 予約一覧
     * @param date 予約日
     * @param medical 診療内容区分
     * @param pcode ドクターコード
     * @param kind class=01:予約一覧取得
     * @return 
     */
    protected String appointlst(String date, String medical, String pcode, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<appointlstreq type=\"record\">");
        sbParam.append("<Appointment_Date type=\"string\">");
        sbParam.append(date);
        sbParam.append("</Appointment_Date>");
        sbParam.append("<Medical_Information type=\"string\">");
        sbParam.append(medical);
        sbParam.append("</Medical_Information>");
        sbParam.append("<Physician_Code type=\"string\">");
        sbParam.append(pcode);
        sbParam.append("</Physician_Code>");
        sbParam.append("</appointlstreq>");
        sbParam.append("</data>");

        return orcaSendRecv(APPOINTLIST + kind, sbParam.toString());
    }
    
    /**
     * 患者病名情報の返却
     * @param pid 患者ID(※必須)
     * @param date 基準月(YYYY-MM)(※省略の場合はシステム時間)
     * @param kind class=01:患者病名情報の取得
     * @return 
     */
    protected String diseaseget(String pid, String date, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<disease_inforeq type=\"record\">");
        sbParam.append("<Patient_ID type=\"string\">");
        sbParam.append(pid);
        sbParam.append("</Patient_ID>");
        sbParam.append("<Base_Date type=\"string\">");
        sbParam.append(date);
        sbParam.append("</Base_Date>");
        sbParam.append("</disease_inforeq>");
        sbParam.append("</data>");

        return orcaSendRecv(GETDISEASE + kind, sbParam.toString());
    }
    
    /**
     * 患者予約情報
     * @param pid 患者ID(※必須)
     * @param date 基準日(YYYY-MM-DD)(※省略の場合はシステム時間)
     * @param kind class=01:患者予約情報取得
     * @return 
     */
    protected String appointlst2(String pid, String date, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<appointlstreq2 type=\"record\">");
        sbParam.append("<Patient_ID type=\"string\">");
        sbParam.append(pid);
        sbParam.append("</Patient_ID>");
        sbParam.append("<Base_Date type=\"string\">");
        sbParam.append(date);
        sbParam.append("</Base_Date>");
        sbParam.append("</appointlstreq2>");
        sbParam.append("</data>");

        return orcaSendRecv(APPOINTLIST + kind, sbParam.toString());
    }
    
    /**
     * 受付登録
     * @param param class=01:受付登録,class=02:受付取消
     * @param data データ
     * @return 
     */
    protected String acceptmod(String param, String data) {
        //1 Information_Date 実施日 2011-03-13
        //2 Information_Time 実施時間 10:50:00
        //3 Api_Result 結果コード（ゼロ以外エラー） 00
        //4 Api_Result_Message エラーメッセージ 受付登録終了
        //5 Reskey   Acceptance_Info
        //6 Acceptance_Date 受付日 2011-03-15
        //7 Acceptance_Time 受付時間 15:30:00
        //8 Acceptance_Id 受付ID
        //9 Department_Code 診療科コード ※５(01:内科) 01
        //10 Department_WholeName 診療科名称 内科
        //11 Physician_Code ドクターコード 10001
        //12 Physician_WholeName ドクター名 日本　一
        //13 Medical_Information 診療内容区分 ※６(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01
        //14 Patient_Information 患者基本情報
        //14-1 Patient_ID 患者番号 00012
        //14-2 WholeName 患者氏名 日医　太郎
        //14-3 WholeName_inKana 患者カナ氏名 ニチイ　タロウ
        //14-4 BirthDate 生年月日 1975-01-01
        //14-5 Sex 性別(1:男性、2:女性) 1
        //14-6 Home_Address_Information 自宅住所情報
        //14-6-1 Address_ZipCode 郵便番号 1130021
        //14-6-2 WholeAddress 住所 東京都文京区本駒込６?１６?３
        //15 HealthInsurance_Information 保険組合せ情報 (繰り返し 20）
        //15-1 InsuranceProvider_Class 保険の種類(060:国保) 060
        //15-2 InsuranceProvider_Number 保険者番号 138057
        //15-3 InsuranceProvider_WholeName 保険の制度名称 国保
        //15-4 HealthInsuredPerson_Symbol 記号 ０１
        //15-5 HealthInsuredPerson_Number 番号 １２３４５６７
        //15-6 HealthInsuredPerson_Continuation 継続区分(1:継続療養、 2:任意継続) 
        //15-7 HealthInsuredPerson_Assistance 補助区分(詳細については、「日医標準レセプトソフトデータベーステーブル定義書」を参照して下さい。) 3
        //15-8 RelationToInsuredPerson 本人家族区分(1:本人、 2:家族) 1
        //15-9 HealthInsuredPerson_WholeName 被保険者名 日医　太郎
        //15-10 Certificate_StartDate 適用開始日 2010-05-01
        //15-11 Certificate_ExpiredDate 適用終了日 9999-12-31
        //15-12 PublicInsurance_Information 公費情報（繰り返し 4）
        //15-12-1 PublicInsurance_Class 公費の種類 010
        //15-12-2 PublicInsurance_Name 公費の制度名称 感37の2
        //15-12-3 PublicInsurer_Number 負担者番号 10131142
        //15-12-4 PublicInsuredPerson_Number 受給者番号 1234566
        //15-12-5 Rate_Admission 入院?負担率（割） 0.05
        //15-12-6 Money_Admission 入院?固定額 0
        //15-12-7 Rate_Outpatient 外来?負担率（割） 0.05
        //15-12-8 Money_Outpatient 外来?固定額 0
        //15-12-9 Certificate_IssuedDate 適用開始日 2011-03-14
        //15-12-10 Certificate_ExpiredDate 適用終了日 9999-12-31
        return orcaSendRecv(ACCEPTMOD + param, data);
    }
    
    /**
     * 送受信
     * @param urlInfo URL情報
     * @param data 送信データ
     * @return 
     */
    protected String orcaSendRecv(String urlInfo, String data) {
        StringBuilder ret = new StringBuilder();
        
        try {
            StringBuilder urlStr = new StringBuilder();
            urlStr.append(URL_HTTP);
            urlStr.append(host);
            urlStr.append(":");
            urlStr.append(port);
            urlStr.append(urlInfo);
            URL url = new URL(urlStr.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(REQUESTMETHOD_POST);
            connection.setRequestProperty("Content-Type", "application/xml");
            
            byte[] encoded = Base64.encodeBase64((user + ":" + pass).getBytes());
            connection.setRequestProperty("Authorization", "Basic " + new String(encoded));
            
            connection.setRequestProperty("Content-Length", "" + data.length());
            
            PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            printWriter.printf(data);
            printWriter.close();
            
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARSET_DEFAULT));
            String line = null;
            while((line = reader.readLine()) != null) {
                ret.append(line);//.append("\n");
            }
            reader.close();
            
            connection.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(OrcaApi.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(OrcaApi.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        
        if(ret != null && ret.toString() != null) {
            apiResult = ret.substring(ret.indexOf(TAG_APIRESULT_START) + TAG_APIRESULT_START.length(), ret.indexOf(TAG_APIRESULT_END));
            apiResultMessage = ret.substring(ret.indexOf(TAG_APIRESULTMSG_START) + TAG_APIRESULTMSG_START.length(), ret.indexOf(TAG_APIRESULTMSG_END));
        }
        
        return ret.toString();
    }
    
    /**
     * 結果コードの取得
     * @return 
     */
    public String getApiResult() {
        return apiResult;
    }
    
    /**
     * エラーメッセージの取得
     * @return 
     */
    public String getApiResultMessage() {
        return apiResultMessage;
    }
}
