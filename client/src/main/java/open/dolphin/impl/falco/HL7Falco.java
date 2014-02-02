package open.dolphin.impl.falco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.client.LabResultParser;
import open.dolphin.impl.labrcv.NLaboImportSummary;
import open.dolphin.infomodel.*;
import open.dolphin.util.AgeCalculater;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * FALCO オーダリングシステム及び検査結果取り込みクラス。
 *
 * @author Tanigawa. Lifec Siences Computing Corp.
 */
public class HL7Falco implements LabResultParser {

    private static final String FALCO = "FALCO";
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    //private static final String DATE_FORMAT_8 = "yyyyMMdd";
    private Boolean DEBUG;
    private Logger logger;

    public HL7Falco() {
        logger = ClientContext.getLaboTestLogger();
        DEBUG = (logger.getLevel() == Level.DEBUG);
    }

    /**
     * 検査結果ファイルをパースし、NLaboImportSummaryのリストを返す。
     * @param file HL7 結果ファイル
     * @return  NLaboImportSummaryのリスト
     */
    @Override
    public List<NLaboImportSummary> parse(Path path) {

        ArrayList<HL7ResultSet> list;
        HL7ResultSet ret;
        HL7GetParam hl = new HL7GetParam();
        list = hl.HL7GetLine(path);

        if (DEBUG) {
            logger.debug(list.size());
            logger.debug("結果");
        }

        String currentKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<NLaboModule>();
        List<NLaboImportSummary> retList = new ArrayList<NLaboImportSummary>();

        SimpleDateFormat defaultDF = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        SimpleDateFormat df8 = new SimpleDateFormat(DATE_FORMAT_8);

        for (int i = 0; i < list.size(); i++) {

            ret = (HL7ResultSet) list.get(i);

            if (false) {
                System.err.println("-----------------------");
                System.err.println(ret.No);
                System.err.println("オーダー番号" + ret.EnforcerOrderNo);
                System.err.println("検査会社名:" + ret.studyCo);
                System.err.println("受信施設名:" + ret.receptionIns);
                System.err.println("透析前後:" + ret.dialysisBA);
                System.err.println("手術後:" + ret.OperationBA);
                //System.out.println("検査結果:"+ret.studyResult);patientDiv
                System.err.println("カルテ番号:" + ret.karteNo);
                System.err.println("カナ患者名:" + ret.patientNameKANA);
                System.err.println("漢字患者名:" + ret.patientNameKANJI);
                System.err.println("患者生年月日:" + ret.patientBirthdate);
                System.err.println("患者年齢:" + ret.patientAge);
                System.err.println("性別:" + ret.sex);
                System.err.println("患者区分:" + ret.patientDiv);
                System.err.println("担当医:" + ret.medicalAtt);
                System.err.println("採取日:" + ret.pecimenRecvDT);
                System.err.println("検査項目コード:" + ret.studyCode);
                System.err.println("検査項目名略称:" + ret.studyNickname);
                System.err.println("検査項目名:" + ret.studyName);
                System.err.println("MEDISコード:" + ret.medisCode);
                System.err.println("基準値:" + ret.standardval);
                //tanigawa^
                //2012121127@Add
                 System.err.println("異常区分:" + ret.abnormaldiv);
                //tanigawa$
                System.err.println("単位名称:" + ret.unit);
                System.err.println("検査結果タイプ:" + ret.Rtype);
                System.err.println("検査結果:" + ret.studyResult);
                System.err.println("異常値区分:" + ret.abnormaldiv);
                System.err.println("副成分(坑酸菌結果区分):" + ret.fktest);
            }

            // 検体採取日
            String sampleDate = null;
            try {
                Date test = df8.parse(ret.pecimenRecvDT);
                sampleDate = defaultDF.format(test);

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

            // LabModule の Key を生成する
//            StringBuilder sb = new StringBuilder();
//            sb.append(ret.karteNo).append(".");
//            sb.append(ret.pecimenRecvDT).append(".");
//            sb.append(ret.studyCo);
//            String testKey = sb.toString();

//            if (!testKey.equals(currentKey)) {
//                // 検査項目数を設定する
//                if (curModule != null && curModule.getItems() != null) {
//                    curModule.setNumOfItems(String.valueOf(curModule.getItems().size()));
//                }
//                // 新規LabModuleを生成しリストに加える
//                curModule = new NLaboModule();
//                curModule.setLaboCenterCode(ret.studyCo);
//                curModule.setPatientId(ret.karteNo);
//                curModule.setPatientName(ret.patientNameKANA);
//                curModule.setPatientSex(ret.sex);
//                curModule.setSampleDate(sampleDate);
//                allModules.add(curModule);
//
//                currentKey = testKey;
//            }
            
            // HL7 オーダー番号に変更
            String testKey = ret.EnforcerOrderNo;
            
            if (!testKey.equals(currentKey)) {
                // 新規LabModuleを生成しリストに加える
                currentKey = testKey;
                curModule = new NLaboModule();
                curModule.setLaboCenterCode(ret.studyCo);
                curModule.setPatientId(ret.karteNo);
                curModule.setPatientName(ret.patientNameKANA);
                curModule.setPatientSex(ret.sex);
                curModule.setSampleDate(sampleDate);
                curModule.setModuleKey(currentKey);         // Key=Hl7 order number
                allModules.add(curModule);
            }

            // NLaboItemを生成し関係を構築する
            NLaboItem item = new NLaboItem();
            curModule.addItem(item);
            item.setLaboModule(curModule);

            item.setPatientId(ret.karteNo);             // カルテ番号
            item.setSampleDate(sampleDate);             // 検体採取日

            item.setLaboCode(ret.studyCo);              //検査会社名
            //ret.receptionIns;                         //受信施設名
            //ret.karteNo;                              //カルテ番号
            //item.setSampleDate(sampleDate);           //検体採取日
            //ret.patientNameKANA;                      //カナ患者名
            //ret.patientNameKANJI;                     //漢字患者名
            //ret.patientBirthdate;                     //生年月日
            //ret.patientAge;                           //年齢
            //ret.sex;                                  //性別
            //ret.patientDiv;                           //患者区分
            //ret.medicalAtt;                           //担当医
            item.setLipemia(ret.nyubi);                 //乳ビ
            item.setHemolysis(ret.Hemolyze);            //溶血
            item.setDialysis(ret.dialysisBA);           //透析前後
            //ret.OperationBA;                          //手術前後　項目にはない
            item.setReportStatus(ret.reportCode);       //報告状況コード
            if (ret.groupCode != null) {
                item.setGroupCode(ret.groupCode);       //グループコード
            } else {
                item.setGroupCode(FALCO);               //グループコード
            }
            if (ret.groupName != null) {
                item.setGroupName(ret.groupName);       //グループ名称
            } else {
                item.setGroupName(FALCO);               //グループコード
            }
            if (ret.studyCodeP != null) {
                item.setParentCode(ret.studyCodeP);     //検査項目コード・親
            } else {
                item.setParentCode(ret.studyCode);
            }
            item.setItemCode(ret.studyCode);            //検査項目コード
            item.setMedisCode(ret.medisCode);           //MEDISコード
            item.setItemName(ret.studyNickname);        //検査項目略称
            item.setItemName(ret.studyName);            //検査項目名
            item.setAbnormalFlg(ret.abnormaldiv);       //異常区分
            item.setNormalValue(ret.standardval);       //基準値
            item.setValue(ret.studyResult);             //検査結果
            item.setUnit(ret.unit);                     //単位
            item.setSpecimenCode(ret.InspectionMate);   //検査材料コード
            item.setSpecimenName(ret.InspectionName);   //検査材料名称
            item.setCommentCode1(ret.reportCode1);      //報告コメントコード1
            item.setComment1(ret.reportName1);          //報告コメント名称1
            item.setCommentCode2(ret.reportCode2);      //報告コメントコード2
            item.setComment2(ret.reportName2);          //報告コメント名称2
            //ret.No;
            //ret.Rtype;                                //検査結果タイプ
            //ret.fktest;                               //副成分取り出しテスト
        }

        // サマリを生成する
        for (NLaboModule module : allModules) {
            NLaboImportSummary summary = new NLaboImportSummary();
            summary.setLaboCode(module.getLaboCenterCode());
            summary.setPatientId(module.getPatientId());
            summary.setPatientName(module.getPatientName());
            summary.setPatientSex(module.getPatientSex());
            summary.setSampleDate(module.getSampleDate());
            summary.setNumOfTestItems(String.valueOf(module.getItems().size()));
            summary.setModule(module);
            retList.add(summary);
        }

        return retList;
    }

    /**
     * 検査オーダをFALCOシステムへ送信（書き込む）。
     * @param list  検査オーダのClaimBundle List
     * @param path  書き込み先のパス
     * @return      結果値
     */
    public int order(PatientModel patient, UserModel creater, List<BundleDolphin> list, String facilityId, String orderNumber, String path) {

        HL7OrderSet set = new HL7OrderSet();
        HL7GetParam hl = new HL7GetParam();

//        //未設定の場合は、"DolphinPro"
//        //set.messageprofileId ="XXXXXXXXXXXXX"; //システム名(任意）
//        //医療機関コード10桁+"00";
//        set.SreceptionIns = "131000000100";    //施設名(ORCAと同じ）
//        //患者情報
//        set.karteNo = "000001";                     //カルテ番号（患者ID）
//        set.patientNameKANA ="カンジャ　サンプル";   //患者氏名（全角カナ）
//        set.patientNameKANJI ="患者　さんぷる";     //患者氏名（全角漢字）:任意
//        set.patientBirthdate ="20110420";          //"X"ライブラリで無条件でつける。（西暦固定）
//        set.patientAge = "10";                     //年齢(任意）
//        set.sex ="M";                              //性別
//        set.plocationCdep = "内科";                 //診療科
//        set.plocationWard = "北病棟";               //任意
//        set.mediattName ="ｱﾕﾐ ﾊﾏｻﾞｷ";               //任意
//        //自動的に作る
//        //set.clientOrderNo = "201101010001";  //依頼者オーダー番号
//        //報告書に記述される（任意）
//        set.EnforcerField1 = "0001";        //実施者フィールド1
//        set.EnforcerField2 = "コメント";        //実施者フィールド2

//        //複数登録可能
//       // "検査項目コード","検査項目名称（略称）：任意","検査項目正式名称"
//        set.obx.add(new HL7OBXOdrerSet("160008010","末梢血液","末梢血液一般"));
//        set.obx.add(new HL7OBXOdrerSet("160017410","ＴＰ","ＴＰ"));
//        set.obx.add(new HL7OBXOdrerSet("160022510","","ＧＯＴ"));
//        set.obx.add(new HL7OBXOdrerSet("160020410",null,"γーＧＴＰ"));
//        for (ClaimBundle bundle : list) {
//            ClaimItem[] items = bundle.getClaimItem();
//            if (items!=null) {
//                for (ClaimItem item : items) {
//                    HL7OBXOdrerSet bbb = new HL7OBXOdrerSet();
//                    bbb.studyCode = item.getCode();         //"160008010";
//                    bbb.studyNickname = null;               //"末梢血液";
//                    bbb.studyName = item.getName();         //"末梢血液一般";
//                    set.obx.add(bbb);
//                }
//            }
//        }
//        System.err.println("--------------------------");
//        System.err.println(patient.getPatientId());
//        System.err.println(patient.getBirthday().replaceAll("-", ""));
//        System.err.println(String.valueOf(AgeCalculater.getAge(patient.getBirthday(), 6)));
//        System.err.println(creater.getDepartmentModel().getDepartmentDesc());
//        System.err.println("--------------------------");

        set.SreceptionIns = facilityId;           //施設名(ORCAと同じ）
        //set.SreceptionIns = "999999999900";           //施設名(ORCAと同じ）

        // 患者情報
        set.karteNo = patient.getPatientId();           //カルテ番号（患者ID）
        set.patientNameKANA = patient.getKanaName();    //患者氏名（全角カナ）
        set.patientNameKANJI = patient.getFullName();   //患者氏名（全角漢字）:任意
        set.patientBirthdate = patient.getBirthday().replaceAll("-", "");   //"X"ライブラリで無条件でつける。（西暦固定）
        set.patientAge = String.valueOf(AgeCalculater.getAge(patient.getBirthday(), 6));    //年齢(任意）
        set.sex = ModelUtils.getGenderMFDesc(patient.getGender());                  //性別

        set.plocationCdep = creater.getDepartmentModel().getDepartmentDesc();//診療科
        //set.plocationWard = "北病棟";                //任意
        //set.mediattName = creater.getCommonName();  //"ｱﾕﾐ ﾊﾏｻﾞｷ"; //任意 
        //自動的に作る
        set.clientOrderNo = orderNumber;            //依頼者オーダー番号
        //報告書に記述される（任意）
        //set.EnforcerField1 = "00001";        //実施者フィールド1
        //set.EnforcerField2 = "User Name of Dolphin";     //実施者フィールド2

        for (BundleDolphin bundle : list) {
            ClaimItem[] items = bundle.getClaimItem();
            if (items != null) {
                for (ClaimItem item : items) {
                    HL7OBXOdrerSet bbb = new HL7OBXOdrerSet();
                    bbb.studyCode = item.getCode();     //"160008010";
                    bbb.studyNickname = null;           //"末梢血液";
                    bbb.studyName = item.getName();     //"末梢血液一般";
                    set.obx.add(bbb);
                }
            }
        }

        hl.HL7Createdata(set);
        int nret = hl.HL7WriteFile(path);//HL7ファイル書き込みパス名

        return nret;
    }
}

class HL7ResultSet{
    //20120322
    String clientOrderNo;       //依頼者オーダー番号
    String EnforcerOrderNo;     //実施者オーダー番号
    String studyCo;             //検査会社名
    String receptionIns;        //受信施設名
    String karteNo;             //カルテ番号
    String studyDate;           //検査日
    String patientNameKANA;     //カナ患者名
    String patientNameKANJI;    //漢字患者名
    String patientBirthdate;    //生年月日
    String patientAge;          //年齢
    String sex;                 //性別
    String patientDiv;          //患者区分
    String medicalAtt;          //担当医
    String nyubi;               //乳ビ
    String Hemolyze;            //溶血
    String dialysisBA;          //透析前後
    String OperationBA;         //手術前後　項目にはない
    String reportCode;          //報告状況コード
    String groupCode;           //グループコード
    String groupName;           //グループ名称
    String studyCodeP;          //検査項目コード・親
    String studyCode;           //検査項目コード
    String medisCode;           //MEDISコード
    String pecimenRecvDT;       //検体採取日　
    String studyNickname;       //検査項目略称
    String studyName;           //検査項目名
    String abnormaldiv;         //異常区分
    String standardval;         //基準値
    String studyResult;         //検査結果
    byte[] Studyindata;         //検査データ
    String unit;                //単位
    String InspectionMate;      //検査材料コード
    String InspectionName;      //検査材料名称
    String reportCode1;         //報告コメントコード1
    String reportName1;         //報告コメント名称1
    String reportCode2;         //報告コメントコード2
    String reportName2;         //報告コメント名称2
    String No;
    String Rtype;               //検査結果タイプ
    String fktest;              //副成分取り出しテスト
 }
class HL7OBXOdrerSet{
    String studyCode;       //検査項目コード
    String studyNickname;   //検査項目名称（電子カルテ項目名称）
    String studyName;       //検査項目正式名称（電子カルテ項目名称）
    /**
     *
     * @param stCode 検査項目コード
     * @param stNicknme 検査項目名称（電子カルテ項目名称）
     * @param stName 検査項目正式名称（電子カルテ項目名称）
     */
    HL7OBXOdrerSet( String stCode, String stNicknme, String stName){
        studyCode = stCode;
        studyNickname = stNicknme;
        studyName = stName;
    }
    HL7OBXOdrerSet(){
        studyCode = null;
        studyNickname = null;
        studyName = null;
    }

}
class HL7OrderSet{
    //MSH
    //10桁の医療機関コード＋2桁の医療機関サブコード
    //東京都＋医科＋医療機関コード＋2桁の医療機関サブコード
    //13+1+1234567
    String SreceptionIns;        //送信施設名
    String reciveIns;           //検査センターコード
    String messageprofileId;    //システム名
    //PID
    String karteNo;             //カルテ番号
    String patientNameKANA;     //カナ患者名
    String patientNameKANJI;    //漢字患者名
    String patientBirthdate;    //生年月日
    String patientAge;          //年齢
    String sex;                 //性別
    //PV1
    String plocationCdep;       //診療科
    String plocationWard;       //病棟
    String mediattName;         //担当医（半角カナ）="ｱﾕﾐ ﾊﾏｻﾞｷ";
    //SPM
    String spcollectionDT;      //検体採取日時
    //ORC
    String orderC;              //オーダー制御
    String clientOrderNo;       //依頼者オーダー番号
    //TQ1
    //OBR
    String EnforcerField1;      //実施者フィールド1
    String EnforcerField2;      //実施者フィールド2
    //OBX
    ArrayList <HL7OBXOdrerSet> obx;
    HL7OrderSet(){
        obx = new ArrayList();
    }
 }
//
//
//
//
class HL7FieldStatus{
      int       pos;                    //位置
      String    fname;                   //フィールド名
      String    type;                   //データ型
      int       maxlen;                 //最大長
      String    OP;                    //OP指定
      String    rep;                   //繰り返し
      int       repNo;                 //反復
      int       ing;                   //成分
      int       sing;                  //副成分
      String    fmt;                   //書式
      int       blen;                   //有効桁数
      int       mlen;                   //最大桁数
      int       prop;                   //属性
      int       fixed;                 // 固定:1/可変:0
      String    abb;                    //"x","-","○"
      String    buf;                   //データ
      public HL7FieldStatus(){}
      public HL7FieldStatus(int pos,
                              String fname,
                              String type,
                              int maxlen,
                              String OP,
                              String rep,
                              int repNo,
                              int ing,
                              int sing,
                              String fmt,
                              int blen,
                              int mlen,
                              int prop,
                              int fixed,
                              String abb,
                              String buf){
          this.pos = pos;
          this.fname = fname;
          this.type = type;
          this.maxlen = maxlen;
          this.OP =OP;
          this.rep = rep;
          this.repNo = repNo;
          this.ing = ing;
          this.sing = sing;
          this.fmt = fmt;
          this.blen = blen;
          this.mlen = mlen;
          this.prop = prop;
          this.fixed = fixed;
          this.abb = abb;
          this.buf = buf;
    }

}
//MSH
class HL7MSH_SetParam{
    String message;                //"MSH"
    String dmt;                    //区切り文字
    String encodeStr;              //符号化文字
    String transApp;               //送信アプリケーション
    String receptionIns;           //送信施設名
    String reciveApp;              //受信アプリケーション
    String reciveIns;              //受信施設
    String messageDT;              //メッセージ日時
    String security;               //セキュリティ
    String messageType;            //メッセージ型
    String messageCid;             //メッセージ制御ID
    String processId;               //処理ID
    String versionId;               //バージョンID
    String seqNo;                   //シーケンス番号
    String contPnt;                 //継続ポインタ
    String acceptreplyType;         //受諾肯定応答型
    String appaffirmatType;         //アプリケーション肯定応答型
    String countryCode;             //国コード
    String charSet;                 //文字セット
    String messageMainLang;         //メッセージの主な言語
    String subsetcharSetman;        //代替文字セット操作法
    String messageprofileId;        //メッセージプロファイル識別子
    //
    String studyCo;                 //検査会社名
    //String receptionIns;            //受信施設名
 }

class HL7MSH_PutParam{
    HL7MSH_SetParam g = new HL7MSH_SetParam();
    String NotUSE   = "Notuse";
     public  HL7MSH_PutParam(){
        //デフォルト
        //String NotUSE   = "Notuse";
        g.message                      ="MSH";
        g.dmt                          ="|";
        g.encodeStr                    ="^~\\&";
        g.transApp                     ="H100";
        g.reciveApp                    ="L100";
        g.reciveIns                    = "1"; //??
        g.messageType                  ="OML^033^OML_ZA1";
        g.processId                    ="P";
        g.versionId                    ="2.5";
        g.acceptreplyType              ="AL";
        g.countryCode                  ="JPN";
        g.charSet                      ="UNICODE UTF-8";
        g.messageMainLang              ="jpn^^ISO 639-2";
        g.messageprofileId             ="DolphinPro";

     }
}
class HL7MSH_GetParam{
    HL7MSH_SetParam g = new HL7MSH_SetParam();
     public  HL7MSH_GetParam(){
        g.studyCo                      = "3.0";
        g.receptionIns                 = "5.0";
     }
}
//PID
class HL7PID_SetParam{
    String message;              //"PID"
    String seqNo;                //シーケンス番号
    //String karteNo;             //カルテ番号（患者ID)
    String patientID;           //患者ID

    String pIDlist_patientCode; //患者IDリスト:患者コード
    String pIDlist_data1;       //患者IDリスト:未使用

    String karteNo;             //患者IDリスト:カルテID
    String pIDlist2_data1;       //患者IDリスト:未使用

    String subPatientID;        //代替患者ID

    String patientNameKANA;     //患者名:カナ患者名
    String patientNameKANA1;    //患者名:未使用

    String patientNameKANJI;     //患者名:漢字患者名
    String patientNameKANJI1;    //患者名:未使用

    String mOldname;              //母親の旧姓
    String patientBirthdate;      //生年月日
    String patientAge;            //年齢
    String sex;                   //性別
    String patientOtherName;      //患者別
    String race;                  //人種
    String address;               //住所
    String countyCode;            //郡コード
    String telnoHo;               //電話番号（自宅）
    String telnoCo;               //電話番号（会社）
    String language;              //使用言語
    String marriageStat;          //結婚状態

 }
class HL7PID_PutParam{
    HL7PID_SetParam g = new HL7PID_SetParam();
    String NotUSE   = "Notuse";
     public  HL7PID_PutParam(){
         //デフォルト
         g.message                     ="PID";
         g.pIDlist_data1              ="^^^^PI";
         g.pIDlist2_data1             ="^^^^MR";
         g.patientNameKANA1           ="^^^^^^L^P";
         g.patientNameKANJI1          ="^^^^^^L^I";

     }
}
class HL7PID_GetParam{
    HL7PID_SetParam g = new HL7PID_SetParam();
    public  HL7PID_GetParam(){
        g.karteNo                      = "3.0";
        g.patientNameKANA              = "5.0.1.~.0";
        g.patientNameKANJI             = "5.0.1.~.1";
        g.patientBirthdate             = "7.0";
        g.patientAge                   = "7.2";
        g.sex                          = "8.0";

    }
}
//PV1
class HL7PV1_SetParam{
    String message;             //"PV1"
    String seqNo;               //シーケンス番号
    String patientDiv;          //患者区分
    String medicalAtt;          //担当医
    String plocationCdep;       //患者所在（診療科）
    String plocationSroom;      //患者所在(病室)
    String plocationSbed;       //患者所在(病床）
    String plocationDummy;       // 未使用
    String plocationWard;       //患者所在(病棟）
    String plocationFloor;       //患者所在(階）
    String plocationRemarks;      //患者所在(備考）
    String plocationCode;      //患者所在(コード）
    String hospDiv;              //入院区分
    String pregistnumber;       //事前登録番号
    String pastplocation;       //過去患者所在
    String mediattendant;       //担当医
    String mediattName;         //担当医名（半角カナ)
    String mediattDummy;        //担当医名（未使用)
    String mediattNameP;        //カナ

 }
class HL7PV1_PutParam{
    HL7PV1_SetParam g = new HL7PV1_SetParam();
    String NotUSE   = "Notuse";
     public  HL7PV1_PutParam(){
         //デフォルト
         g.message                     ="PV1";
         g.patientDiv                  ="O";
         g.plocationDummy              ="^^";
         g.mediattDummy                ="^^^^^^^^^^^";


     }
}
class HL7PV1_GetParam{
    HL7PV1_SetParam g = new HL7PV1_SetParam();
    public  HL7PV1_GetParam(){
        g.patientDiv              = "2.0";
        g.medicalAtt              = "7.0";


    }
}
//SPM
class HL7SPM_SetParam{
    String message;             //"SPM"
    String seqNo;               //シーケンス番号
    String spcollectionDT ;     //検体採取日時

 }
class HL7SPM_PutParam{
    HL7SPM_SetParam g = new HL7SPM_SetParam();
    String NotUSE   = "Notuse";
     public  HL7SPM_PutParam(){
         //デフォルト
         g.message                     ="SPM";
      }
}
//SAC
class HL7SAC_SetParam{
    String message;             //"SAC"
    String seqNo;               //シーケンス番号

 }
class HL7SAC_PutParam{
    HL7SAC_SetParam g = new HL7SAC_SetParam();
    String NotUSE   = "Notuse";
     public  HL7SAC_PutParam(){
         //デフォルト
         g.message                     ="SAC";

      }
}
//ORC
class HL7ORC_SetParam{
    String message;             //"ORC"
    String orderC;              //オーダー制御
    String clientOrderNo;       //依頼者オーダー番号
    //20120322
    String EnforcerOrderNo;     //実施者オーダー番号
 }
class HL7ORC_PutParam{
    HL7ORC_SetParam g = new HL7ORC_SetParam();
    String NotUSE   = "Notuse";
     public  HL7ORC_PutParam(){
         //デフォルト
         g.message                     ="ORC";
      }
}
//20120322-S
class HL7ORC_GetParam{
    HL7ORC_SetParam g = new HL7ORC_SetParam();
    public  HL7ORC_GetParam(){
        g.EnforcerOrderNo              = "3.0";


    }
}
//20120322-E
//TQ1
class HL7TQ1_SetParam{
    String message;             //"TQ1"
    String seqNo;               //シーケンス番号
    String amount;              //数量
    String repPattern;         //繰り返しパターン
    String implisitTime;       //明示的な時間
    String timeunit;           //関連時間/単位
    String servPeriod;         //サービス期間
    String startingDate;       //開始日時
    String endingDate;         //終了日時
    String priority;           //優先度
    String priorityDmy;        //優先度(未使用）
    String priorityChar;       //L
 }
class HL7TQ1_PutParam{
    HL7TQ1_SetParam g = new HL7TQ1_SetParam();
    String NotUSE   = "Notuse";
     public  HL7TQ1_PutParam(){
         //デフォルト
         g.message                     ="TQ1";
         g.amount                      =NotUSE;
         g.repPattern                  =NotUSE;
         g.implisitTime                =NotUSE;
         g.timeunit                    =NotUSE;
         g.servPeriod                  =NotUSE;
         g.startingDate                =NotUSE;
         g.endingDate                  =NotUSE;
         g.priorityDmy                 =NotUSE;

         //優先度
         //緊急：S,至急：A,通常：S
         //g.priority                    ="R";
         g.priorityChar                ="L";

      }
}

//OBR
class HL7OBR_SetParam{
    String message;             //"OBR"
    String seqNo;               //シーケンス番号
    String clientOrderNo;       //依頼者オーダー番号
    String EnforcerOrderNo;     //実施者オーダー番号
    String generalService;      //汎用サービスID
    String priority;            //優先度
    String demandDT;            //要求日時
    String studyDT;             //検査日時
    String studyEndDT;          //検査終了日時
    String quCollection;        //採取量
    String collecterID;         //採取者ID
    String pecimenMCord;        //検体処置コード
    String dangerousCord;       //危険コード
    String relatedCinfo;        //関連臨床情報
    String pecimenRecvDT;       //検体受付日時
    String pecimenOrCollect;    //検体採取元（検体材料）
    String orderpublisher;       //オーダー発行者
    String ordercallTelNo;       //オーダーコールバック用電話番号
    String clientField1;        //依頼者フィールド1
    String clientField2;        //依頼者フィールド2
    String EnforcerField1;      //実施者フィールド1
    String EnforcerField2;      //実施者フィールド2
    String resultRepDT;        //結果報告/状態変更日時
    //result
    String dialysisBA;          //透析前後
    String OperationBA;         //手術前後　項目にはない
 }
class HL7OBR_PutParam{
    HL7OBR_SetParam g = new HL7OBR_SetParam();
    String NotUSE   = "Notuse";
     public  HL7OBR_PutParam(){
         //デフォルト
         g.message                     ="OBR";
         g.EnforcerOrderNo             =NotUSE;
         g.generalService              =NotUSE;
         g.priority                    =NotUSE;
         g.demandDT                    =NotUSE;
         g.studyDT                     =NotUSE;
         g.studyEndDT                  =NotUSE;
         g.quCollection                =NotUSE;
         g.collecterID                 =NotUSE;
         g.pecimenMCord                =NotUSE;
         g.dangerousCord               =NotUSE;
         g.relatedCinfo                =NotUSE;
         g.pecimenRecvDT               =NotUSE;
         g.pecimenOrCollect            =NotUSE;
         g.orderpublisher              =NotUSE;
         g.ordercallTelNo              =NotUSE;
         g.clientField1                =NotUSE;
         g.clientField2                =NotUSE;
         g.resultRepDT                 =NotUSE;

       }
}
class HL7OBR_GetParam{
    HL7OBR_SetParam g = new HL7OBR_SetParam();
    public  HL7OBR_GetParam(){
        g.dialysisBA              = "27.4";
        g.OperationBA             = "27.5";
        g.pecimenRecvDT           = "14.0";//20110427

    }
}
//OBX
class HL7OBX_SetParam{
    String message;             //OBX
    String studyDate;           //検査日
    String nyubi;               //乳ビ
    String Hemolyze;            //溶血
    //String dialysisBA;          //透析前後
    //String OperationBA;         //手術前後　項目にはない
    String reportCode;          //報告状況コード
    String groupCode;           //グループコード
    String groupName;           //グループ名称
    String studyCodeP;          //検査項目コード・親
    String studyCode;           //検査項目コード
    String medisCode;           //MEDISコード
    String studyNickname;       //検査項目略称
    String studyName;           //検査項目名
    String abnormaldiv;         //異常区分
    String standardval;         //基準値
    String studyResult;         //検査結果（NM,SN,RP,CWE)
    byte[] studyindata;         //検査データ
    String unit;                //単位
    String InspectionMate;      //検査材料コード
    String InspectionName;      //検査材料名称
    String reportCode1;         //報告コメントコード1
    String reportName1;         //報告コメント名称1
    String reportCode2;         //報告コメントコード2
    String reportName2;         //報告コメント名称2
//
    String No;                  //シーケンスNo
    String Rtype;               //結果値タイプ
    String CWEstudyResult;
    String SNstudyResult;
    String STstudyResult;
    String NMstudyResult;
    String RPstudyResult;
    String EDstudyResult;       //20110428(base64)
    String ISstudyResult;       //20120322
    String datafmt;             //20110428
    String encode;              //20110428
    String fname;               //20110428
    //
    String fktest;              //副成分取り出しテスト

}
class HL7OBX_PutParam{
    HL7OBX_SetParam g = new HL7OBX_SetParam();
    String NotUSE   = "Notuse";
     public  HL7OBX_PutParam(){
         //デフォルト
         g.message                     ="OBX";
         //結果タイプ
            //ST:文字
            //NM:数値
            //SN:構造数値
            //CWE:コード
            //RP:参照ポインタ
         g.Rtype                       ="ST";
         g.medisCode                   ="REZE";

       }
}
class HL7OBX_GetParam{
    HL7OBX_SetParam g = new HL7OBX_SetParam();
    public  HL7OBX_GetParam(){
        g.No                           = "1.0";
        g.Rtype                        = "2.0";
        g.studyNickname                = "3.1";
        g.studyCode                    = "3.3";
        g.medisCode                    = "3.3";
        g.studyName                    = "3.4";
        ////副成分取り出し用
        g.fktest                       = "3.6.&.2";
        g.nyubi                        = null;
        g.Hemolyze                     = null;
        //dialysisBA                   = "27.2";
        //OperationBA                  = "24.3";
        g.reportCode                   = null;
        g.groupCode                    = null;
        g.groupName                    = null;
        g.studyCodeP                   = null;
        //g.studyCode                    = "3.3";
        //g.medisCode                    = "3.3";
        //g.studyNickname                = "3.1";
        //g.studyName                    = "3.4";
        //g.abnormaldiv                  = null;
        //tanigawa^
        //20121127@Add
        g.abnormaldiv                  = "8.0";
        //tanigawa$
        g.standardval                  = "7.0";
        // NM,SN,RP,CWE
        g.ISstudyResult                = "5.0"; //20120322
        g.NMstudyResult                = "5.0";
        g.SNstudyResult                = "5.0";
        g.STstudyResult                = "5.1";
        g.RPstudyResult                = "5.0"; //?????
        g.CWEstudyResult               = "5.1"; //20110427
        g.EDstudyResult                = "5.4"; //20110428
        g.datafmt                      = "5.2"; //20110428
        g.encode                       = "5.3"; //20110428
        g.fname                        = null;
        //
        g.unit                         = "6.1";
        g.InspectionMate               = null;
        g.InspectionName               = null;
        g.reportCode1                  = null;
        g.reportName1                  = null;
        g.reportCode2                  = null;
        g.reportName2                  = null;
        g.studyDate                    = "14.0";
    }
}
class HL7SetParam{
    String dialysisBA;          //透析前後
    String OperationBA;         //手術前後　項目にはない

    public  HL7SetParam(){
        dialysisBA                   = "27.2";
        OperationBA                  = "24.3";

    }
}
 class HL7GetParam {
    //Defaut
    char m_fSep = '|';  //フィールド
    char m_cSep = '^';  //成分
    char n_rSep = '~';  //反復
    char m_scSep ='&';  //副成分

    String HL7Str ="";
    String HL7FilePath = "";
    String clientODERNo ="";
    //String indataFilepath =""; //20110428
    String patientID ="";       //20110428
    String pecimenRecvDT="";       //20110428
    ArrayList indata;           //20110428
    public HL7GetParam(){
        indata = new  ArrayList();           //20110428

    }
    //MSHセグメント
    HL7MSH_SetParam HL7GetMSHSegment(String line){
        String str;
        HL7MSH_GetParam Get = new HL7MSH_GetParam();
        HL7MSH_SetParam Set = new HL7MSH_SetParam();

        try{
            m_fSep = line.charAt(3);
            //検査会社名
            Set.studyCo = HL7GetStringItem(line,Get.g.studyCo);
            //受信施設名
            Set.receptionIns = HL7GetStringItem(line,Get.g.receptionIns);
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    //PIDセグメント
    HL7PID_SetParam HL7GetPIDSegment(String line){
        String str;
        HL7PID_GetParam Get = new HL7PID_GetParam();
        HL7PID_SetParam Set = new HL7PID_SetParam();
        try{
            Set.karteNo = HL7GetStringItem(line,Get.g.karteNo);
            patientID = Set.karteNo; //20110428
            Set.patientNameKANA = HL7GetStringItem(line,Get.g.patientNameKANA);
            Set.patientNameKANJI = HL7GetStringItem(line,Get.g.patientNameKANJI);
            Set.patientBirthdate = HL7GetStringItem(line,Get.g.patientBirthdate);
            Set.patientAge = HL7GetStringItem(line,Get.g.patientAge);
            Set.sex = HL7GetStringItem(line,Get.g.sex);
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    //PV1セグメント
    HL7PV1_SetParam HL7GetPV1Segment(String line){
        String str;
        HL7PV1_GetParam Get = new HL7PV1_GetParam();
        HL7PV1_SetParam Set = new HL7PV1_SetParam();
        try{
            Set.patientDiv = HL7GetStringItem(line,Get.g.patientDiv);
            Set.medicalAtt = HL7GetStringItem(line,Get.g.medicalAtt);
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    //20120322-S
    //ORCセグメント
    HL7ORC_SetParam HL7GetORCSegment(String line){
        String str;
        HL7ORC_GetParam Get = new HL7ORC_GetParam();
        HL7ORC_SetParam Set = new HL7ORC_SetParam();
        try{
            Set.EnforcerOrderNo = HL7GetStringItem(line,Get.g.EnforcerOrderNo);
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    //20120322-E
    //PV1セグメント
    HL7OBR_SetParam HL7GetOBRSegment(String line){
        String str;
        HL7OBR_GetParam Get = new HL7OBR_GetParam();
        HL7OBR_SetParam Set = new HL7OBR_SetParam();
        try{
            Set.dialysisBA = HL7GetStringItem(line,Get.g.dialysisBA);
            Set.OperationBA = HL7GetStringItem(line,Get.g.OperationBA);
            Set.pecimenRecvDT = HL7GetStringItem(line,Get.g.pecimenRecvDT);
            pecimenRecvDT = Set.pecimenRecvDT; //20110428
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    //OBXセグメント
    HL7OBX_SetParam HL7GetOBXSegment(String line){
        String str;
        HL7OBX_GetParam Get = new HL7OBX_GetParam();
        HL7OBX_SetParam Set = new HL7OBX_SetParam();
        //indata = new ArrayList();   //20110428
        try{
            Set.fktest = HL7GetStringItem(line,Get.g.fktest);
            Set.No = HL7GetStringItem(line,Get.g.No);
            //検査日
            Set.studyDate = HL7GetStringItem(line,Get.g.studyDate);
             //検査コード
            Set.studyCode = HL7GetStringItem(line,Get.g.studyCode);
             //検査項目略称
            Set.studyNickname = HL7GetStringItem(line,Get.g.studyNickname);
             //検査項目名称
             Set.studyName = HL7GetStringItem(line,Get.g.studyName);
             // ＭＥＤＩＳコード
             Set.medisCode = HL7GetStringItem(line,Get.g.medisCode);
             //基準値
             Set.standardval = HL7GetStringItem(line,Get.g.standardval);
             //tanigawa^
             //20121127@Add
             //異常区分
             Set.abnormaldiv = HL7GetStringItem(line,Get.g.abnormaldiv);
             //tanigawa$
             //単位名称
             Set.unit = HL7GetStringItem(line,Get.g.unit);
             //結果タイプ
             Set.Rtype = HL7GetStringItem(line,Get.g.Rtype);
             if(Set.Rtype.equals("SN")){
                 String buf;
                buf = HL7GetStringItem(line,Get.g.SNstudyResult);
                 //20130201@Add
                if(buf.equals("<")){
                    buf="未満";
                }else if(buf.equals(">")){
                    buf = "超過";
                }else if(buf.equals("<=")){
                    buf = "以下";
                }else if(buf.equals(">=")){
                    buf = "以上";
                }
                 //20130202Up
                Set.studyResult = HL7GetStringItem(line,"5.1");
                //20130202@Add
                Set.studyResult = Set.studyResult+buf;   
                //Set.studyResult = HL7GetStringItem(line,Get.g.STstudyResult);
              }else if(Set.Rtype.equals("NM")){
                Set.studyResult = HL7GetStringItem(line,Get.g.NMstudyResult);
              }else if(Set.Rtype.equals("ST")){
                //Set.studyResult = HL7GetStringItem(line,Get.g.SNstudyResult);
                Set.studyResult = HL7GetStringItem(line, Get.g.STstudyResult);    // 20111213
              }else if(Set.Rtype.equals("CWE")){
                Set.studyResult = HL7GetStringItem(line,Get.g.CWEstudyResult);
              }else if(Set.Rtype.equals("RP")){
                Set.studyResult = "---";
              }else if(Set.Rtype.equals("IS")){ //20120322
                Set.studyResult = HL7GetStringItem(line,Get.g.ISstudyResult);               
              }else if(Set.Rtype.equals("ED")){ //20110428(base64)
                String buf;
                buf = HL7GetStringItem(line,Get.g.datafmt);
                if(buf.equals("BMP")){
                     buf = HL7GetStringItem(line,Get.g.encode);
                     if(buf.equals("Base64")){
                        buf = HL7GetStringItem(line,Get.g.EDstudyResult);
                        Set.studyindata = new byte[(int) buf.length()];
                        Set.studyindata = buf.getBytes();
                        indata.add(Set);//20110428
                     }
                }
              }
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    ArrayList HL7GetLine(Path path){
        int i,PIDCount=0;
        String line;
        String ADT;
        ArrayList list = new ArrayList();
        //indata = new ArrayList(); //20110428
        HL7SetParam Get = new HL7SetParam();
        HL7ResultSet Set;
        BufferedReader br = null;
        HL7MSH_SetParam MSHlist = null;
        HL7PID_SetParam PIDlist = null;
        HL7PV1_SetParam PV1list = null;
        //20120322-S
        HL7ORC_SetParam ORClist = null;
        //20120322-E
        HL7OBR_SetParam OBRlist = null;
        HL7OBX_SetParam OBXlist = null;
        //Get.fktest = HL7GetFacterString("&&11", 2,'&');
        try{
            //File f = new File(fname);
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(path), "UTF-8"));
            /* // 20111213 del
            while((line = br.readLine()) != null){
                ADT = line.substring(0, 3);
                if(ADT.equals("MSH")) {
                    MSHlist = HL7GetMSHSegment(line);
                }
                break;
            }
            */
            while((line = br.readLine()) != null){
                ADT = line.substring(0, 3);
                if(ADT.equals("MSH")) {
                    MSHlist = HL7GetMSHSegment(line);
                    continue;   // 20111213
                }
                if(ADT.equals("PV1")) {
                    PV1list = HL7GetPV1Segment(line);
                    continue;
                }
                if(ADT.equals("IN1")) {
                       continue;
                }
                if(ADT.equals("ORC")) {
                    ORClist = HL7GetORCSegment(line);
                       continue;
                }
                //20110511
                if(ADT.equals("SPM")) {
                    continue;
                }
                if(ADT.equals("TQ1")) {
                    continue;
                }
                if(ADT.equals("OBR")) {
                    //透析前後
                    OBRlist = HL7GetOBRSegment(line);
                    continue;
                }
                if(ADT.equals("PID")) {
                    PIDlist = HL7GetPIDSegment(line);
                    PIDCount++;
                     continue;
                }
                Set = new HL7ResultSet();
                Set.studyCo = MSHlist.studyCo;                      //検査会社名
                Set.receptionIns = MSHlist.receptionIns;            //受信施設
                Set.dialysisBA = OBRlist.dialysisBA;                //透析前後
                Set.OperationBA = OBRlist.OperationBA;
                Set.karteNo = PIDlist.karteNo;                      //患者ＩＤ（カルテＮｏ）
                Set.patientNameKANA = PIDlist.patientNameKANA;      //患者氏名カナ
                Set.patientNameKANJI = PIDlist.patientNameKANJI;    //患者氏名漢字
                Set.patientBirthdate = PIDlist.patientBirthdate;    //生年月日
                Set.patientAge = PIDlist.patientAge;                //年齢
                Set.sex =PIDlist.sex;                               //性別
                Set.patientDiv = PV1list.patientDiv;               //患者区分
                Set.medicalAtt = PV1list.medicalAtt;                //担当医
                Set.pecimenRecvDT =  OBRlist.pecimenRecvDT;        //検体採取日
                //20120322
                Set.EnforcerOrderNo = ORClist.EnforcerOrderNo;      //実施者オーダー番号
                if(ADT.equals("OBX")){
                     OBXlist = HL7GetOBXSegment(line);
                     //20120322-S
                     if(OBXlist.Rtype.equals("ED")){
                         continue;
                     }
                     //20120322-E
                     Set.fktest = OBXlist.fktest;
                     Set.No = OBXlist.No;
                     //採取日
                     Set.studyDate = OBXlist.studyDate;
                     //検査コード
                     Set.studyCode = OBXlist.studyCode;
                      //検査項目略称
                     Set.studyNickname = OBXlist.studyNickname;
                      //検査項目名称
                     Set.studyName = OBXlist.studyName;
                     // ＭＥＤＩＳコード
                     Set.medisCode = OBXlist.medisCode;
                     Set.unit = OBXlist.unit;
                     Set.standardval =OBXlist.standardval;
                     //tanigawa^
                     //20121127@Add
                     Set.abnormaldiv =OBXlist.abnormaldiv;
                      //tanigawa$
                     //結果タイプ
                     Set.Rtype = OBXlist.Rtype;
                     //検査結果
                     Set.studyResult = OBXlist.studyResult;
                }
                //20120322-S
                if(Set.studyName.length() == 0){
                    if(Set.studyNickname.length() == 0){
                        Set.studyName = "項目名称なし";
                        Set.studyNickname = "項目名称なし";
                    }else{
                        Set.studyName = Set.studyNickname;
                    }
                }else{
                    if(Set.studyNickname.length() == 0){
                        Set.studyNickname = Set.studyName;
                    }
                }
                if(Set.studyResult.length() == 0){
                    Set.studyResult = "";
                }
                //20120322-E
                list.add(Set);
                //indata.add(Set); //20110428
            }
            //System.out.println(PIDCount);
        }
        catch(IOException ex){
            System.out.println("ファイル例外"+ex+"が発生しました");
        }
        catch(Exception ex){
            System.out.println("例外aaaaa"+ex+"が発生しました");
            System.out.println(ex.getStackTrace());
        }
        finally{
            try{
                br.close();
            }catch(Exception ex){}
        }

        return(list);
    }

       String HL7GetStringItem(String line,String str){
        int i,p,pos;
        int cnt=0;
        int n,no;
        String hn ="";
        int    hno=-1;
        String fk ="";
        int    fkno=-1;
        String Item,witem,wno;
        String rItem="";
        StringBuilder sb = new StringBuilder("");
        //line =line +"|";
        line =line + String.valueOf(m_fSep);
        //反復チェック
        pos = p = str.indexOf('~');
        if(p != -1){
            hn = str.substring(pos+2);
            p = hn.indexOf('.');
            if(p == -1){
                hno = Integer.valueOf(hn).intValue();
            }else{
                hn = str.substring(pos+2,pos+2+p);
                hno = Integer.valueOf(hn).intValue();
            }
        }
        //副成分
        pos = p = str.indexOf('&');
        if(p != -1){
            fk = str.substring(pos+2);
            p = fk.indexOf('.');
            if(p == -1){
                fkno = Integer.valueOf(fk).intValue();
            }
        }
        p = str.indexOf('.');
        wno = str.substring(0,p);
        no = Integer.valueOf(wno).intValue();
        str = str.substring(p+1);
        //パラメタ正規化
        if(hno != -1){
            p = str.indexOf('~');
            str = str.substring(0,p-1);
        }
        if(fkno != -1){
            p = str.indexOf('&');
            if(p != -1){
                str = str.substring(0,p-1);
            }
        }
        try{
            //System.out.println("["+line+"]");
            p = line.indexOf('|');
            while((pos = line.indexOf(m_fSep,p+1)) != -1){
                Item = line.substring(p+1,pos);
                p = pos;
                if(++cnt == no){
                    //反復チェック
                    for(i=0;i<hno;i++){
                       p = Item.indexOf('~');
                       Item = Item.substring(p+1);
                    }
                    //^
                    if(str == null){
                        sb.append(Item);
                        break;
                    }else{
                        p=0;
                        String wsstr;
                        String wstr;
                        wstr = str;
                        while((pos= wstr.indexOf('.',p)) != -1){
                            pos= wstr.indexOf('.',p);
                            witem = wstr.substring(p,p+1);
                            n= Integer.valueOf(witem).intValue();
                            //wsstr = HL7GetFacterString(Item,n+1,m_cSep);
                            wsstr = HL7GetFacterString(Item,n,m_cSep);
                            if(wsstr != null){
                             //副成分
                                if(fkno != -1){
                                    wsstr = HL7GetFacterString(wsstr,fkno,m_scSep);
                                }
                            }
                            sb.append(wsstr);
                            wstr = wstr.substring(pos+1);
                            sb.append(" ");
                        }
                         n= Integer.valueOf(wstr).intValue();
                         wsstr = HL7GetFacterString(Item,n,m_cSep);
                         if(wsstr != null){
                          //副成分
                            if(fkno != -1){
                                wsstr = HL7GetFacterString(wsstr,fkno,m_scSep);
                            }
                            sb.append(wsstr);
                        }
                         break;
                    }
                }
            }
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        rItem = sb.toString();
        return(rItem);
    }


       String HL7GetFacterString(String line,int no,char sep){
        int p,pos;
        int cnt=0;
        String Item=null;
        String rItem=null;
         //System.out.println("[["+line+"]]");
        //line =line + String.valueOf(sep);
        try{
            //p=0;
            while(true){
                p=0;
                pos = line.indexOf(sep,p);
                if(pos == 0){
                    p++;cnt++;
                    line = line.substring(p);
                    continue;
                }
                if(pos != -1){
                    Item = line.substring(p,pos);
                    p = pos;
                } else if(pos == -1){
                    Item = line.substring(p);

                }
                if(cnt++ == no){
                    rItem = Item;
                    break;
                }
                if(pos != -1) p++;
                line = line.substring(p);
                if(pos == -1) break;
            }
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました ");
            System.out.println(ex.getStackTrace());
        }
        return(rItem);
    }
//
//
//
//
    void HL7Sleep(int t){
        try {
            Thread.sleep(t);
        } catch(InterruptedException e){

        }
    }

    int  HL7SetIndataWrite(String path){
        int ret = 0;
        int i;
        String indataFileName ="";// "d:\\hl7\\indata\\123.bmp";

        String a;
        HL7OBX_SetParam nret;

        for(i=0;i<indata.size();i++){
            nret= (HL7OBX_SetParam)indata.get(i);
            a = String.format("%1$04d", i+1);
            indataFileName = path+"\\"+patientID+"-"+pecimenRecvDT+"-"+a+".bmp";
            DecodeBase64DataOut(indataFileName,nret.studyindata);
        }
        ret = i;
        return(ret);
    }
    int HL7WriteFile(String path){
        int ret =0;
        String FLGFileName = "";
        HL7FilePath = path+File.separator+CurrentDateTime("YYYYMMDDHHMMSS")+".HL7";
        try{
            //.HL7
            File outFile = new File(HL7FilePath);
            if(outFile.exists() && outFile.isFile()){
                HL7Sleep(1000);
                HL7FilePath = path+File.separator+CurrentDateTime("YYYYMMDDHHMMSS")+".HL7";
            }
            OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
            BufferedWriter bf = new BufferedWriter(os);
            bf.write(HL7Str);
            bf.flush();
            bf.close();
            //FileWriter out = new FileWriter(outFile);
            //out.write(HL7Str);
            //out.close( );
            //.FLG
            FLGFileName = HL7FilePath.replace(".HL7", ".FLG");
            outFile = new File(FLGFileName);
            FileWriter out = new FileWriter(outFile);
            out.close( );
        } catch(IOException e) {
                ret = -1;
        }
        return(ret);
    }
    void HL7Createdata(HL7OrderSet set){
        int i,ret =0;
        String MSH;
        String PID;
        String PV1;
        //未使用
        //String IN1;
        String SPM;
        String SAC;
        String ORC;
        String TQ1;
        String OBR;
        String OBX;
        char cr = 0x0d;
        StringBuilder ms = new StringBuilder();
        // MSH
        MSH = HL7MSGCreate(set);
        ms.append(MSH).append(cr);
        //HL7Str += MSH + cr;
        System.out.println(MSH);
        //PID
        PID = HL7PIDCreate(set);
        ms.append(PID).append(cr);
        System.out.println(PID);
        //PV1
        PV1 = HL7PV1Create(set);
        ms.append(PV1).append(cr);
        System.out.println(PV1);
        //IN1
        //SPM
        SPM = HL7SPMCreate();
        ms.append(SPM).append(cr);
        System.out.println(SPM);
        //SAC
        SAC = HL7SACCreate();
        ms.append(SAC).append(cr);
        System.out.println(SAC);
        //ORC
        // minagawa
        //ORC = HL7ORCCreate();
        ORC = HL7ORCCreate(set);
        ms.append(ORC).append(cr);
        System.out.println(ORC);
        //TQ1
        TQ1 = HL7TQ1Create();
        ms.append(TQ1).append(cr);
        System.out.println(TQ1);
        //OBR
        OBR = HL7OBRCreate(set);
        ms.append(OBR).append(cr);
        System.out.println(OBR);
          //OBX
        HL7OBXOdrerSet obx;
        for(i=0;i<set.obx.size();i++){

            obx = set.obx.get(i);
            OBX = HL7OBXCreate(i+1,obx);
            ms.append(OBX).append(cr);
            System.out.println(OBX);
        }
        HL7Str = ms.toString();
        //System.out.println(HL7Str);
    }
    String HL7MSGCreate(HL7OrderSet set){
        String MSH = "";
        HL7MSH_PutParam msg = new HL7MSH_PutParam();
        //上位から設定するもの
        msg.g.receptionIns = set.SreceptionIns;//送信施設名


        //ここで設定するもの
        if(set.messageprofileId != null){
            msg.g.messageprofileId = set.messageprofileId;
        }
        msg.g.messageDT =CurrentDateTime("YYYYMMDDHHMMSS");                 //メッセージ日時
        msg.g.messageCid = "H"+msg.g.messageDT+GetSeqNo();  //メッセージ制御ID

        MSH = HL7MSGCreateSet(msg);


        return(MSH);
    }
    String HL7MSGCreateSet(HL7MSH_PutParam msg){
        String MSH = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(2,"符号化文字",                 "ST",4,"R","N",0,0,0,"X",4,4,0,1,"×",msg.g.encodeStr),
            new HL7FieldStatus(3,"送信アプリケーション",        "ST",227,"O","N",0,0,0,"X",4,4,0,1,"×",msg.g.transApp),
            new HL7FieldStatus(4,"送信施設名",                 "HD",227,"O","N",0,0,0,"X",12,12,0,1,"×",msg.g.receptionIns),
            new HL7FieldStatus(5,"受信アプリケーション",        "HD",227,"O","N",0,0,0,"X",4,4,0,1,"×",msg.g.reciveApp),
            new HL7FieldStatus(6,"受信施設",                   "HD",227,"O","N",0,0,0,"X",3,3,0,0,"×",msg.g.reciveIns),
            new HL7FieldStatus(7,"メッセージ日時",              "TS",26,"R","N",0,0,0,"9",17,17,1,1,"×",msg.g.messageDT),
            new HL7FieldStatus(8,"セキュリティ",                "ST",40,"O","N",0,0,0,"-",0,0,0,0,"",msg.NotUSE),
            new HL7FieldStatus(9,"メッセージ型",                "MSG",15,"R","N",0,0,0,"X",3,15,1,1,"×",msg.g.messageType),
            new HL7FieldStatus(10,"メッセージ制御ID",           "ST",20,"R","N",0,0,0,"X",22,22,1,1,"×",msg.g.messageCid),
            new HL7FieldStatus(11,"処理ID",                     "PT",3,"R","N",0,0,0,"X",1,1,1,1,"×",msg.g.processId),
            new HL7FieldStatus(12,"バージョンID",               "VID",60,"R","N",0,0,0,"X",6,6,1,0,"×",msg.g.versionId),
            new HL7FieldStatus(13,"シーケンス番号",              "NM",15,"O","N",0,0,0,"-",6,6,1,0,"",msg.NotUSE),
            new HL7FieldStatus(14,"継続ポインタ",               "ST",180,"O","N",0,0,0,"-",6,6,1,0,"",msg.NotUSE),
            new HL7FieldStatus(15,"受諾肯定応答型",              "ID",2,"O","N",0,0,0,"X",2,2,1,1,"×",msg.g.acceptreplyType),
            new HL7FieldStatus(16,"アプリケーション肯定応答型",   "ID",2,"O","N",0,0,0,"-",2,2,1,1,"",msg.NotUSE),
            new HL7FieldStatus(17,"国コード",                    "ID",3,"O","N",0,0,0,"X",3,3,1,1,"×",msg.g.countryCode),
            new HL7FieldStatus(18,"文字セット",                  "ID",16,"O","N",0,0,0,"X",16,16,1,0,"×",msg.g.charSet),
            new HL7FieldStatus(19,"メッセージの主な言語",         "CE",250,"O","N",0,0,0,"X",3,14,1,1,"×",msg.g.messageMainLang),
            new HL7FieldStatus(20,"代替文字セット操作法",         "ID",20,"O","N",0,0,0,"X",3,14,1,1,"",msg.NotUSE),
            new HL7FieldStatus(21,"メッセージプロファイル識別子",  "EI",427,"O","N",0,0,0,"X",60,60,1,0,"×",msg.g.messageprofileId),
        };
        MSH = HL7SetSetMessage(st,msg.g.message);
        return(MSH);

    }
    String HL7PIDCreate(HL7OrderSet set){
        String PID = "";
        HL7PID_PutParam pid = new HL7PID_PutParam();
        //上位から設定するもの
        pid.g.sex               = set.sex;
        pid.g.karteNo           = set.karteNo;
        pid.g.patientNameKANA   = set.patientNameKANA;
        pid.g.patientNameKANJI  = set.patientNameKANJI;
        pid.g.patientBirthdate  = "X"+set.patientBirthdate;
        pid.g.patientAge        = set.patientAge;
        //tanigawa^
        //20121127@Add(0.1対応）
        //"0.1->"0"
        float age = Float.valueOf(pid.g.patientAge).floatValue();
        int intage = (int)age;
        pid.g.patientAge = Integer.toString(intage);
        //tanigawa$
        //ここで設定するもの
        pid.g.seqNo = "0001";

        PID = HL7PIDCreateSet(pid);


        return(PID);
    }
    String HL7PIDCreateSet(HL7PID_PutParam pid){
        String PID = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",pid.g.seqNo),
            new HL7FieldStatus(2,"患者ID",                     "CX",20,"B","N",0,0,0,"-",4,4,0,1,"",pid.NotUSE),
            new HL7FieldStatus(3,"患者IDリスト:患者コード",     "CX",22,"R","Y",1,1,0,"X",4,4,0,1,"○",pid.g.pIDlist_patientCode),
            new HL7FieldStatus(3,"患者IDリスト",                "CX",22,"R","Y",1,2,0,"X",16,45,0,0,"○",pid.g.pIDlist_data1),
            new HL7FieldStatus(3,"患者IDリスト:カルテID",       "CX",22,"R","Y",2,1,0,"X",4,4,0,1,"○",pid.g.karteNo),
            new HL7FieldStatus(3,"患者IDリスト",                "CX",22,"R","Y",2,2,0,"X",3,3,0,0,"○",pid.g.pIDlist2_data1),
            new HL7FieldStatus(4,"代替患者ID",                  "CX",20,"B","N",0,0,0,"9",17,17,1,1,"",pid.NotUSE),
            new HL7FieldStatus(5,"患者名:カナ患者名",            "XPN",72,"R","Y",1,1,0,"-",20,29,0,0,"×",pid.g.patientNameKANA),
            new HL7FieldStatus(5,"患者名",                      "XPN",72,"R","Y",1,2,0,"X",3,15,1,1,"x",pid.g.patientNameKANA1),
            new HL7FieldStatus(5,"患者名:漢字患者名",           "XPN",72,"R","Y",2,1,0,"X",20,29,1,1,"x",pid.g.patientNameKANJI),
            new HL7FieldStatus(5,"患者名",                     "XPN",72,"R","Y",2,2,0,"X",1,1,1,1,"x",pid.g.patientNameKANJI1),
            new HL7FieldStatus(6,"母親の旧姓",                  "XPN",250,"R","Y",0,0,0,"X",6,6,1,0,"",pid.NotUSE),
            new HL7FieldStatus(7,"生年月日",                    "TS",13,"O","N",1,1,0,"X",9,13,1,0,"○",pid.g.patientBirthdate),
            new HL7FieldStatus(7,"年齢",                       "TS",13,"O","N",1,2,0,"-",3,13,1,0,"○",pid.g.patientAge),
            new HL7FieldStatus(8,"性別",                       "IS",1,"O","N",0,0,0,"X",1,1,1,1,"×",pid.g.sex),
            new HL7FieldStatus(9,"患者別名",                    "XPN",2,"O","N",0,0,0,"-",2,2,1,1,"",pid.NotUSE),
            new HL7FieldStatus(10,"人種",                       "CE",3,"O","N",0,0,0,"-",3,3,1,1,"",pid.NotUSE),
            new HL7FieldStatus(11,"住所",                       "XAD",16,"O","Y",0,0,0,"-",16,16,1,0,"",pid.NotUSE),
            new HL7FieldStatus(12,"郡コード",                   "IS",250,"O","N",0,0,0,"-",3,14,1,1,"",pid.NotUSE),
            new HL7FieldStatus(13,"電話番号（自宅）",            "XTN",20,"O","N",0,0,0,"-",3,14,1,1,"",pid.NotUSE),
            new HL7FieldStatus(14,"電話番号（会社）",            "XTN",427,"O","Y",0,0,0,"-",60,60,1,0,"",pid.NotUSE),
            new HL7FieldStatus(15,"使用言語",                    "CE",427,"O","Y",0,0,0,"-",60,60,1,0,"",pid.NotUSE),
            new HL7FieldStatus(16,"結婚状態",                    "CE",427,"O","Y",0,0,0,"-",60,60,1,0,"",pid.NotUSE),
        };
        PID = HL7SetSetMessage(st,pid.g.message);
        return(PID);
    }
    //PV1
    String HL7PV1Create(HL7OrderSet set){
        String PV1 = "";
        HL7PV1_PutParam pv1 = new HL7PV1_PutParam();
        //上位から設定するもの
        pv1.g.plocationCdep = set.plocationCdep;//"内科";
        pv1.g.plocationWard = set.plocationWard;//"北病棟";
        pv1.g.mediattName   = set.mediattName;//"ｱﾕﾐ ﾊﾏｻﾞｷ";

        //ここで設定するもの
        pv1.g.seqNo = "0001";

        if(pv1.g.mediattName != null && !(pv1.g.mediattName.isEmpty())){
            pv1.g.mediattNameP = "P";
        }

        PV1 = HL7PV1CreateSet(pv1);

        return(PV1);
    }
    String HL7PV1CreateSet(HL7PV1_PutParam pv1){
        String PV1 = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",pv1.g.seqNo),
            new HL7FieldStatus(2,"患者区分",                    "IS",1,"R","N",0,0,0,"X",4,4,0,1,"○",pv1.g.patientDiv),
            new HL7FieldStatus(3,"患者所在（診療科）",           "PL",1,"O","N",1,1,0,"X",4,4,0,1,"○",pv1.g.plocationCdep),
            new HL7FieldStatus(3,"患者所在(病室)",               "PL",1,"O","N",1,2,0,"X",4,4,0,1,"○",pv1.g.plocationSroom),
            new HL7FieldStatus(3,"患者所在(病床)",               "PL",1,"O","N",1,3,0,"X",4,4,0,1,"○",pv1.g.plocationSbed),
            new HL7FieldStatus(3,"患者所在(未使用)",              "PL",1,"O","N",1,4,0,"X",4,4,0,1,"○",pv1.g.plocationDummy),
            new HL7FieldStatus(3,"患者所在(病棟)",               "PL",1,"O","N",1,7,0,"X",4,4,0,1,"○",pv1.g.plocationWard),
            new HL7FieldStatus(3,"患者所在(階)",               "PL",1,"O","N",1,8,0,"X",4,4,0,1,"○",pv1.g.plocationFloor),
            new HL7FieldStatus(3,"患者所在(備考)",               "PL",1,"O","N",1,9,0,"X",4,4,0,1,"○",pv1.g.plocationRemarks),
            new HL7FieldStatus(3,"患者所在(コード)",               "PL",1,"O","N",1,10,0,"X",4,4,0,1,"○",pv1.g.plocationCode),
            new HL7FieldStatus(4,"入院区分",                     "IS",1,"O","N",0,0,0,"X",4,4,0,1,"○",pv1.NotUSE),
            new HL7FieldStatus(5,"事前登録番号",                  "CX",250,"O","N",0,0,0,"X",4,4,0,1,"○",pv1.NotUSE),
            new HL7FieldStatus(6,"過去患者所在",                  "PL",80,"O","N",0,0,0,"X",4,4,0,1,"○",pv1.NotUSE),
            new HL7FieldStatus(7,"担当医",                       "XCN",250,"O","N",1,1,0,"X",4,4,0,1,"○",pv1.g.mediattendant),
            new HL7FieldStatus(7,"担当医(半角カナ)",              "XCN",250,"O","N",1,2,0,"X",4,4,0,1,"○",pv1.g.mediattName),
            new HL7FieldStatus(7,"担当医(半角カナ)",              "XCN",250,"O","N",1,3,0,"X",4,4,0,1,"○",pv1.g.mediattDummy),
            new HL7FieldStatus(7,"担当医(カナ)",                  "XCN",250,"O","N",1,15,0,"X",4,4,0,1,"○",pv1.g.mediattNameP),
            new HL7FieldStatus(8,"紹介医",                        "XCN",250,"O","N",0,0,0,"X",4,4,0,1,"○",pv1.NotUSE),
        };
        PV1 = HL7SetSetMessage(st,pv1.g.message);
        return(PV1);
    }
    //SPM
    String HL7SPMCreate(){
        String SPM = "";
        HL7SPM_PutParam spm = new HL7SPM_PutParam();
        //上位から設定するもの
        spm.g.spcollectionDT = CurrentDateTime("YYYYMMDDHHMM"); //とりあえず

        //ここで設定するもの
        spm.g.seqNo = "0001";


        SPM = HL7SPMCreateSet(spm);


        return(SPM);
    }
    String HL7SPMCreateSet(HL7SPM_PutParam spm){
        String SPM = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",spm.g.seqNo),
            new HL7FieldStatus(2,"検体ID",                     "EIP",1,"R","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(3,"被検体ID",                   "EIP",1,"O","N",0,1,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(4,"検体タイプ",                 "CWE",1,"O","N",0,2,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(5,"検体タイプ修飾子",            "CWE",1,"O","N",0,3,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(6,"検体添加物",                 "CWE",1,"O","N",0,4,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(7,"検体採取法",                 "CWE",1,"O","N",0,7,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(8,"検体部位",                   "CWE",1,"O","N",0,8,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(9,"検体部位修飾子",              "CWE",1,"O","N",0,9,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(10,"検体採取部位",               "CWE",1,"O","N",0,10,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(11,"検体役割",                   "CWE",1,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(12,"検体採取量",                 "CQ",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(13,"検体総数",                   "NM",80,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(14,"検体記述",                   "ST",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(15,"検体取り扱いコード",          "CWE",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(16,"検体リスクコード",            "CWE",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
            new HL7FieldStatus(7,"検体採取日",                   "DR",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.g.spcollectionDT),
            new HL7FieldStatus(8,"検体受領日時",                 "TS",250,"O","N",0,0,0,"X",4,4,0,1,"○",spm.NotUSE),
        };
        SPM = HL7SetSetMessage(st,spm.g.message);
        return(SPM);
    }
    //SAC
    String HL7SACCreate(){
        String SAC = "";
        HL7SAC_PutParam sac = new HL7SAC_PutParam();
        //上位から設定するもの


        //ここで設定するもの
        sac.g.seqNo = "0001";

        SAC = HL7SACCreateSet(sac);

        return(SAC);
    }
    String HL7SACCreateSet(HL7SAC_PutParam sac){
        String SAC = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",sac.g.seqNo),
            new HL7FieldStatus(2,"ダミー",                     "EIP",1,"R","N",0,0,0,"X",4,4,0,1,"○",sac.NotUSE),

        };
        SAC = HL7SetSetMessage(st,sac.g.message);
        return(SAC);
    }
    //ORC
    // minagawa
    //String HL7ORCCreate(){
    String HL7ORCCreate(HL7OrderSet set){
        String ORC = "";
        HL7ORC_PutParam orc = new HL7ORC_PutParam();
        //上位から設定するもの
        orc.g.orderC = "NM";              //オーダー制御
        // minagawa
        //orc.g.clientOrderNo = "DL"+CurrentDateTime("YYYYMMDDHHMMSS");         //依頼者オーダー番号
        orc.g.clientOrderNo = set.clientOrderNo;
        clientODERNo = orc.g.clientOrderNo;
        //ここで設定するもの


        ORC = HL7ORCCreateSet(orc);

        return(ORC);
    }
    String HL7ORCCreateSet(HL7ORC_PutParam orc){
        String ORC = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "ID",4,"O","N",0,0,0,"9",4,4,1,0,"×",orc.g.orderC),
            new HL7FieldStatus(2,"依頼者オーダー番号",           "EI",1,"R","N",0,0,0,"X",4,4,0,1,"×",orc.g.clientOrderNo),
            new HL7FieldStatus(3,"ダミー",                     "EIP",1,"R","N",0,0,0,"X",4,4,0,1,"○",orc.NotUSE),

        };
        ORC = HL7SetSetMessage(st,orc.g.message);
        return(ORC);
    }
    //ORC
    String HL7TQ1Create(){
        String TQ1 = "";
        HL7TQ1_PutParam tq1 = new HL7TQ1_PutParam();
        //上位から設定するもの
        //tq1.g.priority          ="A";              //優先度

        //ここで設定するもの
        tq1.g.seqNo = "0001";
        if(tq1.g.priority == null){
            tq1.g.priority          ="R";
            //tq1.g.priorityChar =     "L";
        }

        TQ1 = HL7TQ1CreateSet(tq1);

        return(TQ1);
    }
    String HL7TQ1CreateSet(HL7TQ1_PutParam tq1){
        String TQ1 = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",tq1.g.seqNo),
            new HL7FieldStatus(2,"数量",                        "CQ",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.amount),
            new HL7FieldStatus(3,"繰り返しパターン",             "RPT",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.repPattern),
            new HL7FieldStatus(4,"明示的な時間",                 "TM",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.implisitTime),
            new HL7FieldStatus(5,"関連時間/単位",                "CQ",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.timeunit),
            new HL7FieldStatus(6,"サービス期間",                 "CQ",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.servPeriod),
            new HL7FieldStatus(7,"開始日時",                     "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.startingDate),
            new HL7FieldStatus(8,"終了日時",                     "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.g.endingDate),
            new HL7FieldStatus(9,"優先度",                       "CWE",1,"R","N",1,1,0,"X",4,4,0,1,"○",tq1.g.priority),
            new HL7FieldStatus(9,"優先度(未使用）",               "CWE",1,"R","N",1,2,0,"X",4,4,0,1,"○",tq1.g.priorityDmy),
            new HL7FieldStatus(9,"L",                            "CWE",1,"R","N",1,3,0,"X",4,4,0,1,"○",tq1.g.priorityChar),
            new HL7FieldStatus(10,"ダミー",                       "CWE",1,"R","N",0,0,0,"X",4,4,0,1,"○",tq1.NotUSE),

        };
        TQ1 = HL7SetSetMessage(st,tq1.g.message);
        return(TQ1);
    }
    //OBR
    String HL7OBRCreate(HL7OrderSet set){
        String OBR = "";
        HL7OBR_PutParam obr = new HL7OBR_PutParam();
        //上位から設定するもの
       obr.g.clientOrderNo      = clientODERNo;
       //obr.g.clientOrderNo      = set.clientOrderNo;//"201101010001";
       obr.g.EnforcerField1     = set.EnforcerField1;//"0001";
       obr.g.EnforcerField2     = set.EnforcerField2;//"0001";

        //ここで設定するもの
        obr.g.seqNo = "0001";

        OBR = HL7OBRCreateSet(obr);

        return(OBR);
    }
    String HL7OBRCreateSet(HL7OBR_PutParam obr){
        String OBR = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",obr.g.seqNo),
            new HL7FieldStatus(2,"依頼者オーダー番号",          "EI",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.clientOrderNo),
            new HL7FieldStatus(3,"実施者オーダー番号",          "EI",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.EnforcerOrderNo),
            new HL7FieldStatus(4,"汎用サービスID",              "CE",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.generalService),
            new HL7FieldStatus(5,"優先度",                      "ID",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.priority),
            new HL7FieldStatus(6,"要求日時",                    "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.demandDT),
            new HL7FieldStatus(7,"検査日時",                     "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.studyDT),
            new HL7FieldStatus(8,"検査終了日時",                 "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.studyEndDT),
            new HL7FieldStatus(9,"採取量",                       "CQ",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.quCollection),
            new HL7FieldStatus(10,"採取者ID",                    "XCN",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.collecterID),
            new HL7FieldStatus(11,"検体処置コード",               "ID",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.pecimenMCord),
            new HL7FieldStatus(12,"危険コード",                   "CE",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.dangerousCord),
            new HL7FieldStatus(13,"関連臨床情報",                  "ST",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.relatedCinfo),
            new HL7FieldStatus(14,"検体受付日時",                  "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.pecimenRecvDT),
            new HL7FieldStatus(15,"検体採取元（検体材料）",         "SPS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.pecimenOrCollect),
            new HL7FieldStatus(16,"オーダー発行者",                 "XCN",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.orderpublisher),
            new HL7FieldStatus(17,"オーダーコールバック用電話番号", "XTN",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.ordercallTelNo),
            new HL7FieldStatus(18,"依頼者フィールド1",             "ST",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.clientField1),
            new HL7FieldStatus(19,"依頼者フィールド2",             "ST",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.clientField2),
            new HL7FieldStatus(20,"実施者フィールド1",             "ST",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.EnforcerField1),
            new HL7FieldStatus(21,"実施者フィールド2",             "ST",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.g.EnforcerField2),
            new HL7FieldStatus(22,"結果報告/状態変更日時",          "TS",1,"R","N",0,0,0,"X",4,4,0,1,"○",obr.NotUSE),

        };
        OBR = HL7SetSetMessage(st,obr.g.message);
        return(OBR);
    }
//OBX
    String HL7OBXCreate(int no,HL7OBXOdrerSet set){
        String OBX = "";
        HL7OBX_PutParam obx = new HL7OBX_PutParam();
        //上位から設定するもの
       obx.g.studyCode = set.studyCode;//"160008010";
       obx.g.studyNickname = set.studyNickname;//"末梢血液一般";
       obx.g.studyName = set.studyName;//"末梢血液一般";

        //ここで設定するもの
       obx.g.No = String.format("%1$04d", no);
       if(obx.g.Rtype  == null){
           obx.g.Rtype = "ST";
       }
       if(obx.g.studyNickname == null || obx.g.studyNickname.isEmpty()){
           obx.g.studyNickname = obx.g.studyName;
       }
       OBX = HL7OBXCreateSet(obx);

        return(OBX);
    }
    String HL7OBXCreateSet(HL7OBX_PutParam obx){
        String OBX = "";
        HL7FieldStatus[] st = {
            new HL7FieldStatus(1,"シーケンス番号",              "SI",4,"O","N",0,0,0,"9",4,4,1,0,"×",obx.g.No),
            new HL7FieldStatus(2,"結果値タイプ",                "ID",4,"O","N",0,0,0,"9",4,4,1,0,"×",obx.g.Rtype),
            new HL7FieldStatus(3,"検査項目コード",              "CWE",4,"O","N",1,1,0,"9",4,4,1,0,"×",obx.g.studyCode),
            new HL7FieldStatus(3,"検査項目略称",                "CWE",4,"O","N",1,2,0,"9",4,4,1,0,"×",obx.g.studyNickname),
            new HL7FieldStatus(3,"検査項目区分（REZE)",          "CWE",4,"O","N",1,3,0,"9",4,4,1,0,"×",obx.g.medisCode),
            new HL7FieldStatus(3,"検査項目（将来用)",            "CWE",4,"O","N",1,4,0,"9",4,4,1,0,"×",obx.NotUSE),
            new HL7FieldStatus(3,"検査項目（将来用)",            "CWE",4,"O","N",1,5,0,"9",4,4,1,0,"×",obx.NotUSE),
            new HL7FieldStatus(3,"検査項目（将来用)",            "CWE",4,"O","N",1,6,0,"9",4,4,1,0,"×",obx.NotUSE),
            new HL7FieldStatus(3,"検査項目（未使用)",            "CWE",4,"O","N",1,7,0,"9",4,4,1,0,"×",obx.NotUSE),
            new HL7FieldStatus(3,"検査項目（未使用)",            "CWE",4,"O","N",1,8,0,"9",4,4,1,0,"×",obx.NotUSE),
            new HL7FieldStatus(3,"検査項目名",                  "CWE",4,"O","N",1,9,0,"9",4,4,1,0,"×",obx.g.studyName),
            new HL7FieldStatus(4,"検査サブID",                  "ST",4,"O","N",0,0,0,"9",4,4,1,0,"×",obx.NotUSE),

        };
        OBX = HL7SetSetMessage(st,obx.g.message);
        return(OBX);
    }
    //
    //
    //
    //
    //
    String HL7SetSetMessage(HL7FieldStatus[] st,String MSG){
        int i;
        //int repNo =0;
        int posNo = -1;
        int oposNo = -1;
        int repNo = -1;
        int orepNo = -1;
        int len = 0;

        char Sep ='|';
        String Message ="";
        StringBuilder ms = new StringBuilder(MSG);
        for(i=0;i<st.length;i++){
            oposNo = st[i].pos;
            //繰り返し
            if(st[i].rep.equals("Y")){
                ms.append('|');
                orepNo = st[i].repNo;
                oposNo = st[i].pos;
                for(;i<st.length;i++){
                    posNo = st[i].pos;
                    repNo = st[i].repNo;
                    if(oposNo != posNo) {
                        i--;
                        break;
                    }
                    if(orepNo != repNo){
                        orepNo = st[i].repNo;
                        ms.append('~');
                    }
                    if(st[i].buf == null) {
                        continue;
                    }
                    if(st[i].buf.equals("Notuse")){
                        continue;
                    }
                    ms.append(st[i].buf);
                    continue;
                }
            } else if(st[i].rep.equals("N")){
                orepNo = st[i].ing;
                for(;i<st.length;i++){
                    posNo = st[i].pos;
                    repNo = st[i].ing;
                    if(oposNo != posNo) {
                        len = ms.length();
                        while(ms.charAt(len-1) == '^'){
                            len--;ms.setLength(len);
                        }
                        i--;
                        break;
                    } else {
                        if(orepNo == repNo){
                            ms.append('|');
                        } else {
                            ms.append('^');
                        }
                    }
                    if(st[i].buf == null) {
                        continue;
                    }
                    if(st[i].buf.equals("Notuse")){
                        continue;
                    }
                    ms.append(st[i].buf);
                }
            }
        }
        len = ms.length();
        while(ms.charAt(len-1) == m_fSep){
            len--;ms.setLength(len);
        }
        Message  = ms.toString();
        return(Message);

    }
    //
    String CurrentDateTime(String fmt){
        String a="";
        Calendar cal1 = Calendar.getInstance();  //(1)オブジェクトの生成

        int year = cal1.get(Calendar.YEAR);        //(2)現在の年を取得
        int month = cal1.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
        int day = cal1.get(Calendar.DATE);         //(4)現在の日を取得
        int hour = cal1.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
        int minute = cal1.get(Calendar.MINUTE);    //(6)現在の分を取得
        int second = cal1.get(Calendar.SECOND);    //(7)現在の秒を取得

        a += Integer.toString(year);
        if(month < 10) a += "0";
        a += Integer.toString(month);
        if(day < 10) a += "0";
        a += Integer.toString(day);
        if(hour < 10) a += "0";
        a += Integer.toString(hour);
        if(minute < 10) a += "0";
        a += Integer.toString(minute);
        if(fmt.equals("YYYYMMDDHHMMSS")){
            if(second < 10) a += "0";
            a += Integer.toString(second);
        }

        return(a);
    }
    String GetSeqNo(){
        //int a = 1;

        String a = "0001";
        return(a);
    }
    int DecodeBase64DataOut(String fname,byte[] indata){
        int ret = -1;
    try{
	   //	Base64デコード	         //
	   byte[] outdata = Base64.decodeBase64(indata);

	   File outf = new File(fname);
	   FileOutputStream fo = new FileOutputStream(outf);
	   fo.write(outdata);
	   fo.close();
           ret =0;

        }
        catch (Exception e)
	{
            e.printStackTrace(System.err);
            ret = -1;
	}
        return(ret);
    }

}
