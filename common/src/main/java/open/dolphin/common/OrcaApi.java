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
public class OrcaApi {
    private static final String URL_HTTP = "http://";
    public static final String REQUESTMETHOD_POST = "POST";
    
    // ORCA
    public static final String ORCAAPI_VER_47 = "47";
    
    public static final String KIND_01 = "?class=01";
    public static final String KIND_02 = "?class=02";
    public static final String KIND_03 = "?class=03";
    
    // 受付一覧
    private static final String ORCAAPI47_ACCEPTLIST = "/api01rv2/acceptlstv2";
    private String ACCEPTLIST;
    
    // 予約一覧
    private static final String ORCAAPI47_APPOINTLIST = "/api01rv2/appointlstv2";
    private String APPOINTLIST;
    
    // 患者病名返却
    private static final String ORCAAPI47_GETDISEASE = "/api01rv2/diseasegetv2";
    private String GETDISEASE;
    
    // 患者予約情報
    private static final String ORCAAPI47_APPOINTLIST2 = "/api01rv2/appointlst2v2";
    private String APPOINTLIST2;
    
    // 受付登録
    private static final String ORCAAPI47_ACCEPTMOD = "/orca11/acceptmodv2";
    private String ACCEPTMOD;
    
    // システム管理情報
    private static final String ORCAAPI47_SYSTEM01LST = "/api01rv2/system01lstv2";
    private String SYSTEM01LST;
    
    protected String host;
    protected String port;
    protected String user;
    protected String pass;
    protected String ver;
    
    /**
     * コンストラクタ
     */
    protected OrcaApi() {
        super();
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
            SYSTEM01LST = ORCAAPI47_SYSTEM01LST;
        }
    }
    
    /**
     * 受付一覧
     * @param date 受付日(YYYY-MM-DD)
     * @param dcode 診療科コード
     * @param pcode ドクターコード
     * @param medical 診療内容区分
     * @param kind class=01:受付中取得,class=02:受付済み取得,class=03:全受付取得
     * @return 
     */
    protected String acceptlst(String date, String dcode, String pcode, String medical, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<acceptlstreq type=\"record\">");
        sbParam.append("<Acceptance_Date type=\"string\">");
        sbParam.append(date);
        sbParam.append("</Acceptance_Date>");
        sbParam.append("<Department_Code type=\"string\">");
        sbParam.append(dcode);
        sbParam.append("</Department_Code>");
        sbParam.append("<Physician_Code type=\"string\">");
        sbParam.append(pcode);
        sbParam.append("</Physician_Code>");
        sbParam.append("<Medical_Information type=\"string\">");
        sbParam.append(medical);
        sbParam.append("</Medical_Information>");
        sbParam.append("</acceptlstreq>");
        sbParam.append("</data>");

        return orcaSendRecv(ACCEPTLIST + kind, sbParam.toString());
/*
1 Information_Date 実施日 2011-03-13
2 Information_Time 実施時間 10:50:00
3 Api_Result 結果コード（ゼロ以外エラー） 00
4 Api_Result_Message エラーメッセージ 処理終了
5 Reskey   Patient Info
6 Acceptance_Date 受付日 2011-03-15
7 Acceptlst_Infomation 受付一覧情報 (繰り返し 500)  
7-1 Acceptance_Time 受付時間 15:30:00
7-2 Department_Code 診療科コード(01:内科) 01
7-3 Department_WholeName 診療科名称 内科
7-4 Physician_Code ドクターコード 10001
7-5 Physician_WholeName ドクター名 日本　一
7-6 Medical_Information 診療内容区分(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01
7-7 Claim_Infometion claim情報 0
7-8 Account_Time 会計時間 15:50:00
7-9 Patient_Information 患者基本情報
7-9-1 Patient_ID 患者番号 00012
7-9-2 WholeName 患者氏名 日医　太郎
7-9-3 WholeName_inKana 患者カナ氏名 ニチイ　タロウ
7-9-4 BirthDate 生年月日 1975-01-01
7-9-5 Sex 性別(1:男性、2:女性) 1
7-10 HealthInsurance_Information 保険組合せ情報
7-10-1 InsuranceProvider_Class 保険の種類 060
7-10-2 InsuranceProvider_Number 保険者番号 138057
7-10-3 InsuranceProvider_WholeName 保険の制度名称 国保
7-10-4 HealthInsuredPerson_Symbol 記号 ０１
7-10-5 HealthInsuredPerson_Number 番号 １２３４５６７
7-10-6 HealthInsuredPerson_Continuation 継続区分(1:継続療養、 2:任意継続)
7-10-7 HealthInsuredPerson_Assistance 補助区分(詳細については、「日医標準レセプトソフトデータベーステーブル定義書」を参照して下さい。) 3
7-10-8 RelationToInsuredPerson 本人家族区分(1:本人、 2:家族) 1
7-10-9 HealthInsuredPerson_WholeName 被保険者名 日医　太郎
7-10-10 Certificate_StartDate 適用開始日 2010-05-01
7-10-11 Certificate_ExpiredDate 適用終了日 9999-12-31
7-10-12 PublicInsurance_Information 公費情報（繰り返し 4）
7-10-12-1 PublicInsurance_Class 公費の種類 010
7-10-12-2 PublicInsurance_Name 公費の制度名称 感37の2
7-10-12-3 PublicInsurer_Number 負担者番号 10131142
7-10-12-4 PublicInsuredPerson_Number 受給者番号 1234566
7-10-12-5 Rate_Admission  入院ー負担率（割） 0.05
7-10-12-6 Money_Admission 入院ー固定額 0
7-10-12-7 Rate_Outpatient 外来ー負担率（割） 0.05
7-10-12-8 Money_Outpatient 外来ー固定額 0
7-10-12-9 Certificate_IssuedDate 適用開始日 2010-05-01
7-10-12-10 Certificate_ExpiredDate 適用終了日 9999-12-31
*/
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
/*
1 Information_Date 実施日 2011-03-13
2 Information_Time 実施時間 10:50:00
3 Api_Result 結果コード（ゼロ以外エラー） 00
4 Api_Result_Message エラーメッセージ 処理終了
5 Reskey   Patient Info
6 Appointment_Date 予約日 2011-03-15
7 Appointlst_Infomation 予約情報 (繰り返し500)
7-1 Appointment_Time 予約時間 15:30:00
7-2 Medical_Information 診療内容区分(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01
7-3 Department_Code 予約診療科コード(01:内科) 01
7-4 Department_WholeName 予約診療科名称 内科
7-5 Physician_Code 予約ドクタコード 10001
7-6 Physician_WholeName 予約ドクター名 日本　一
7-7 Visit_Information 来院情報 (1:来院済)
8 Patient_Information 患者情報
8-1 Patient_ID 患者番号 00012
8-2 WholeName 患者氏名 日医　太郎
8-3 WholeName_inKana 患者カナ氏名 ニチイ　タロウ
8-4 BirthDate 生年月日 1975-01-01
8-5 Sex 性別(1:男性、2:女性) 1
8-6 Home_Address_Information 自宅住所情報
8-6-1 PhoneNumber1 自宅電話番号 03-8888-9999
*/
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
/*
1 Information_Date 実施日 2011-09-07
2 Information_Time 実施時間 17:30:30
3 Api_Result 結果コード(ゼロ以外エラー) 00
4 Api_Result_Message エラーメッセージ 処理終了
5 Reskey   Medical Info
6 Disease_Infores 患者病名情報
6-1 Patient_ID 患者番号 00012
6-2 WholeName 患者氏名 日医　太郎
6-3 WholeName_inKana 患者カナ氏名 ニチイ　タロウ
6-4 BirthDate 生年月日 1975-01-01
6-5 Sex 性別(1:男性、2:女性) 1
7 Base_Date 基準月 2011-09
8 Disease_Information 病名情報（繰り返し　２００）
8-1 InOut 入外区分(1:入院、2:入院外) 2
8-2 Department_Code 診療科コード(01:内科) 01
8-3 Insurance_Combination_Number 保険組み合わせ番号 0002
8-4 Disease_Name 病名 ACバイパス術後機械的合併症
8-5 Disease_Single 単独病名情報（繰り返し　２１）
8-5-1 Disease_Single_Code 病名コード 8830052
8-5-2 Disease_Single_Name 単独病名 ACバイパス術後機械的合併症
8-5-3 Disease_Single_Condition 単独病名状態(空白:通常、2:削除、3:廃止(実施日時点での))
8-6 Disease_Category 主病フラグ(PD:主病名) PD
8-7 Disease_SuspectedFlag 疑い、急性フラグ(S:疑い、A:急性、SA:急性かつ疑い) S
8-8 Disease_StartDate 病名開始日 2011-09-08
8-9 Disease_EndDate 転帰日 2011-09-08
8-10 Disease_OutCome 転帰フラグ(F:治癒、D:死亡、C:中止、S:移行) F
8-11 Disease_Supplement_Name 補足コメント
8-12 Disease_Karte_Name カルテ病名
8-13 Disease_Class 疾患区分(03:皮膚科特定疾患指導管理料(１)、04:皮膚科特定疾患指導管理料(２)、05:特定疾患療養管理料、07:てんかん指導料、08:特定疾患療養管理料又はてんかん指導料、09:難病外来指導管理料) 03
4-14 Disease_Receipt_Print レセプト表示有無(1:表示しない、空白:表示する) 1
4-15 Disease_Receipt_Print_Period レセプト表示期間
*/
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
/*
1 Information_Date 実施日 2012-12-17
2 Information_Time 実施時間 14:09:44
3 Api_Result 結果コード（ゼロ以外エラー） 00
4 Api_Result_Message エラーメッセージ 処理終了
5 Reskey レスポンスキー情報  Patient Info
6 Base_Date 基準日 2012-12-18
7 Patient_Information 患者基本情報
7-1 Patient_ID 患者番号 00001
7-2 WholeName 患者氏名 テスト　患者
7-3 WholeName_inKana 患者カナ氏名 テスト　カンジャ
7-4 BirthDate 生年月日 1970-01-01
7-5 Sex 性別（1:男性、2:女性） 1
8 Appointlst_Information  予約情報（繰り返し ５０）
8-1 Appointment_Date 予約日 2012-12-22
8-2 Appointment_Time 予約時間 11:00:00
8-3 Medical_Information 診療内容区分（01:診察１、02:薬のみ、03:注射のみ、04:検査のみ、05:リハビリテーション、06:健康診断、07:予防注射、99:該当なし） 01
8-4 Medical_Information_WholeName 診療内容名称 診察１
8-5 Department_Code 予約診療科コード（01:内科） 01
8-6 Department_WholeName 予約診療科名称 内科
8-7 Physician_Code 予約ドクターコード 10001
8-8 Physician_WholeName 予約ドクター名 おるか
8-9  Visit_Information 来院情報（１：来院済） 1
8-10  Appointment_Note  予約メモ 予約メモテスト
*/
    }
    
    /**
     * 受付登録
     * @param pid 患者ID
     * @param accept 受付ID
     * @param depart 診療科コード
     * @param physician ドクターコード
     * @param kind 種類
     * @return 
     */
    protected String acceptmod(String pid, String accept, String depart, String physician, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<acceptreq type=\"record\">");
        sbParam.append("<Patient_ID type=\"string\">");
        sbParam.append(pid);
        sbParam.append("</Patient_ID>");
        sbParam.append("<Acceptance_Id type=\"string\">");
        sbParam.append(accept);
        sbParam.append("</Acceptance_Id>");
        sbParam.append("<Department_Code type=\"string\">");
        sbParam.append(depart);
        sbParam.append("</Department_Code>");
        sbParam.append("<Physician_Code type=\"string\">");
        sbParam.append(physician);
        sbParam.append("</Physician_Code>");
        sbParam.append("</acceptrea>");
        sbParam.append("</data>");

        return orcaSendRecv(ACCEPTMOD + kind, sbParam.toString());
/*
1Patient_ID 患者番号 00012 必須
2 Acceptance_Date 受付日 2011-03-15
3 Acceptance_Time 受付時間 15:30:00
4 Acceptance_Id 受付ID 必須(受付取消のみ)
5 Department_Code 診療科コード ※１(01:内科)	01 必須(受付登録のみ)
6 Physician_Code ドクターコード 10001 必須(受付登録のみ)
7 Medical_Information 診療内容区分 ※２(01:診察１、 02:薬のみ、 03:注射のみ、 04:検査のみ、 05:リハビリテーション、 06:健康診断、 07:予防注射、 99:該当なし) 01 ※３
8 HealthInsurance_Information 保険組合せ情報
8-1 InsuranceProvider_Class 保険の種類(060:国保) 060 ※４
8-2 InsuranceProvider_Number 保険者番号 138057 ※４
8-3 InsuranceProvider_WholeName 保険の制度名称 国保 ※４
8-4 HealthInsuredPerson_Symbol 記号 ０１
8-5 HealthInsuredPerson_Number 番号 １２３４５６７
8-6 HealthInsuredPerson_Continuation 継続区分(1:継続療養、 2:任意継続)
8-7 HealthInsuredPerson_Assistance 補助区分(詳細については、「日医標準レセプトソフトデータベーステーブル定義書」を参照して下さい。) 3
8-8 RelationToInsuredPerson 本人家族区分(1:本人、 2:家族) 1
8-9 HealthInsuredPerson_WholeName 被保険者名 日医　太郎
8-10 Certificate_StartDate 適用開始日 2010-05-01
8-11 Certificate_ExpiredDate 適用終了日 9999-12-31
8-12 PublicInsurance_Information 公費情報（繰り返し4）
8-12-1 PublicInsurance_Class 公費の種類 010 ※４
8-12-2 PublicInsurance_Name 公費の制度名称 感37の2 ※４
8-12-3 PublicInsurer_Number 負担者番号 10131142 ※４
8-12-4 PublicInsuredPerson_Number 受給者番号 1234566 ※４
8-12-5 Certificate_IssuedDate 適用開始日 2010-05-01
8-12-6 Certificate_ExpiredDate 適用終了日 9999-12-31
*/
    }
    
    /**
     * システム管理情報
     * @param date 受付日(YYYY-MM-DD)
     * @param kind class=01:診療科対象,class=02:ドクター対象,class=03:ドクター以外の職員対象
     * @return 
     */
    protected String system01lst(String date, String kind) {
        StringBuilder sbParam = new StringBuilder();
        sbParam.append("<data>");
        sbParam.append("<system01_managereq type=\"record\">");
        sbParam.append("<Base_Date type=\"string\">");
        sbParam.append(date);
        sbParam.append("</Base_Date>");
        sbParam.append("</system01_managereq>");
        sbParam.append("</data>");

        return orcaSendRecv(SYSTEM01LST + kind, sbParam.toString());
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while((line = reader.readLine()) != null) {
                ret.append(line);//.append("\n");
            }
            reader.close();
            
            connection.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(OrcaApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OrcaApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret.toString();
    }
}
