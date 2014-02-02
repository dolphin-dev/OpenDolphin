package open.dolphin.labrcv;

import java.io.*;
import java.util.*;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class Hl7Parser implements LabResultParser {
    
    private Boolean DEBUG;
    private Logger logger;

    public Hl7Parser() {
        logger = ClientContext.getLaboTestLogger();
        DEBUG = (logger.getLevel() == Level.DEBUG) ? true : false;
    }

    /**
     * @param args the command line arguments
     */
    @Override
    public List<NLaboImportSummary> parse(File file) {
        
        ArrayList<HL7ResultSet> list;
        HL7ResultSet ret;
        HL7GetParam hl = new HL7GetParam();
        list = hl.HL7GetLine(file.getPath());

        if (DEBUG) {
            logger.debug(list.size());
            logger.debug("結果");
        }

        String currentKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<NLaboModule>();
        List<NLaboImportSummary> retList = new ArrayList<NLaboImportSummary>();

        for(int i=0;i<list.size();i++){

            ret= (HL7ResultSet)list.get(i);
            
            if (DEBUG) {
                logger.debug("-----------------------");
                logger.debug(ret.No);
                logger.debug("検査会社名:"+ret.studyCo);
                logger.debug("受信施設名:"+ret.receptionIns);
                logger.debug("透析前後:"+ret.dialysisBA);
                logger.debug("手術後:"+ret.OperationBA);
                //System.out.println("検査結果:"+ret.studyResult);patientDiv
                logger.debug("カルテ番号:"+ret.karteNo);
                logger.debug("カナ患者名:"+ret.patientNameKANA);
                logger.debug("漢字患者名:"+ret.patientNameKANJI);
                logger.debug("患者生年月日:"+ret.patientBirthdate);
                logger.debug("患者年齢:"+ret.patientAge);
                logger.debug("性別:"+ret.sex);
                logger.debug("患者区分:"+ret.patientDiv);
                logger.debug("担当医:"+ret.medicalAtt);
                logger.debug("採取日:"+ret.studyDate);
                logger.debug("検査項目コード:"+ret.studyCode);
                logger.debug("検査項目名略称:"+ret.studyNickname);
                logger.debug("検査項目名:"+ret.studyName);
                logger.debug("MEDISコード:"+ret.medisCode);
                logger.debug("基準値:"+ret.standardval);
                logger.debug("単位名称:"+ret.unit);
                logger.debug("検査結果タイプ:"+ret.Rtype);
                logger.debug("検査結果:"+ret.studyResult);
                logger.debug("副成分(坑酸菌結果区分):"+ret.fktest);
            }

            // LabModule の Key を生成する
            StringBuilder sb = new StringBuilder();
            sb.append(ret.karteNo).append(".");
            sb.append(ret.studyDate).append(".");
            sb.append(ret.studyCo);
            String testKey = sb.toString();

            if (!testKey.equals(currentKey)) {
                // 新規LabModuleを生成しリストに加える
                curModule = new NLaboModule();
                curModule.setLaboCenterCode(ret.studyCo);
                curModule.setPatientId(ret.karteNo);
                curModule.setPatientName(ret.patientNameKANJI);
                curModule.setPatientSex(ret.sex);
                curModule.setSampleDate(ret.studyDate);
                allModules.add(curModule);

                currentKey = testKey;
            }

            // NLaboItemを生成し関係を構築する
            NLaboItem item = new NLaboItem();
            curModule.addItem(item);
            item.setLaboModule(curModule);

            item.setPatientId(ret.karteNo);             // カルテ番号
            item.setSampleDate(ret.studyDate);          // 検体採取日

            item.setLaboCode(ret.studyCo);              //検査会社名
            //ret.receptionIns;                         //受信施設名
            //ret.karteNo;                              //カルテ番号
            item.setSampleDate(ret.studyDate);          //検体採取日
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
            item.setGroupCode(ret.groupCode);           //グループコード
            item.setGroupName(ret.groupName);           //グループ名称
            item.setParentCode(ret.studyCodeP);         //検査項目コード・親
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
}
class HL7ResultSet{
    String studyCo;             //検査会社名
    String receptionIns;        //受信施設名
    String karteNo;             //カルテ番号
    String studyDate;           //採取日
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
    String studyNickname;       //検査項目略称
    String studyName;           //検査項目名
    String abnormaldiv;         //異常区分
    String standardval;         //基準値
    String studyResult;         //検査結果
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
//MSH
class HL7MSH_SetParam{
    String studyCo;             //検査会社名
    String receptionIns;        //受信施設名
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
    String karteNo;             //カルテ番号
    String patientNameKANA;     //カナ患者名
    String patientNameKANJI;    //漢字患者名
    String patientBirthdate;    //生年月日
    String patientAge;          //年齢
    String sex;                 //性別

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
    String patientDiv;          //患者区分
    String medicalAtt;          //担当医

 }
class HL7PV1_GetParam{
    HL7PV1_SetParam g = new HL7PV1_SetParam();
    public  HL7PV1_GetParam(){
        g.patientDiv              = "2.0";
        g.medicalAtt              = "7.0";
 

    }
}
//OBR
class HL7OBR_SetParam{
    String dialysisBA;          //透析前後
    String OperationBA;         //手術前後　項目にはない
 }
class HL7OBR_GetParam{
    HL7OBR_SetParam g = new HL7OBR_SetParam();
    public  HL7OBR_GetParam(){
        g.dialysisBA              = "27.4";
        g.OperationBA             = "27.5";


    }
}
//OBX
class HL7OBX_SetParam{
    String studyDate;           //採取日
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
    String unit;                //単位
    String InspectionMate;      //検査材料コード
    String InspectionName;      //検査材料名称
    String reportCode1;         //報告コメントコード1
    String reportName1;         //報告コメント名称1
    String reportCode2;         //報告コメントコード2
    String reportName2;         //報告コメント名称2
//
    String No;
    String Rtype;
    String CWEstudyResult;
    String SNstudyResult;
    String STstudyResult;
    String NMstudyResult;
    String RPstudyResult;
    //
    String fktest;              //副成分取り出しテスト
 
}
class HL7OBX_GetParam{
    HL7OBX_SetParam g = new HL7OBX_SetParam();
    public  HL7OBX_GetParam(){
        g.studyDate                    = "14.0";
        g.nyubi                        = null;
        g.Hemolyze                     = null;
        //dialysisBA                   = "27.2";
        //OperationBA                  = "24.3";
        g.reportCode                   = null;
        g.groupCode                    = null;
        g.groupName                    = null;
        g.studyCodeP                   = null;
        g.studyCode                    = "3.3";
        g.medisCode                    = "3.3";
        g.studyNickname                = "3.1";
        g.studyName                    = "3.4";
        g.abnormaldiv                  = null;
        g.standardval                  = "7.0";
        // NM,SN,RP,CWE
        g.NMstudyResult                = "5.0";
        g.SNstudyResult                = "5.0";
        g.STstudyResult                = "5.0";
        g.RPstudyResult                = "5.0"; //?????
        g.CWEstudyResult               = "5.0";
        //
        g.unit                         = "6.1";
        g.InspectionMate               = null;
        g.InspectionName               = null;
        g.reportCode1                  = null;
        g.reportName1                  = null;
        g.reportCode2                  = null;
        g.reportName2                  = null;
        g.No                           = "1.0";
        g.Rtype                        = "2.0";
        g.fktest                       = "3.6.&.2";
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
    //PV1セグメント
    HL7OBR_SetParam HL7GetOBRSegment(String line){
        String str;
        HL7OBR_GetParam Get = new HL7OBR_GetParam();
        HL7OBR_SetParam Set = new HL7OBR_SetParam();
        try{
            Set.dialysisBA = HL7GetStringItem(line,Get.g.dialysisBA);
            Set.OperationBA = HL7GetStringItem(line,Get.g.OperationBA);
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
        try{
            Set.fktest = HL7GetStringItem(line,Get.g.fktest);
            Set.No = HL7GetStringItem(line,Get.g.No);
            //採取日
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
             //単位名称
             Set.unit = HL7GetStringItem(line,Get.g.unit);
             //結果タイプ
             Set.Rtype = HL7GetStringItem(line,Get.g.Rtype);
             if(Set.Rtype.equals("ST")){
                Set.studyResult = HL7GetStringItem(line,Get.g.STstudyResult);
              }else if(Set.Rtype.equals("NM")){
                Set.studyResult = HL7GetStringItem(line,Get.g.NMstudyResult);
              }else if(Set.Rtype.equals("SN")){
                Set.studyResult = HL7GetStringItem(line,Get.g.SNstudyResult);
              }else if(Set.Rtype.equals("CWE")){
                Set.studyResult = HL7GetStringItem(line,Get.g.CWEstudyResult);
              }else if(Set.Rtype.equals("RP")){
                Set.studyResult = "????????";
            }
        }
        catch(Exception ex){
            System.out.println("例外"+ex+"が発生しました");
        }
        return(Set);
    }
    ArrayList HL7GetLine(String fname){
        int i,PIDCount=0;
        String line;
        String ADT;
        ArrayList list = new ArrayList();
        HL7SetParam Get = new HL7SetParam();
        HL7ResultSet Set;
        BufferedReader br = null;
        HL7MSH_SetParam MSHlist = null;
        HL7PID_SetParam PIDlist = null;
        HL7PV1_SetParam PV1list = null;
        HL7OBR_SetParam OBRlist = null;
        HL7OBX_SetParam OBXlist = null;
        //Get.fktest = HL7GetFacterString("&&11", 2,'&');
        try{
            br = new BufferedReader(new FileReader(fname));
            while((line = br.readLine()) != null){
                ADT = line.substring(0, 3);
                if(ADT.equals("MSH")) {
                    MSHlist = HL7GetMSHSegment(line);
                }
                break;
            }
            while((line = br.readLine()) != null){
                ADT = line.substring(0, 3);
                if(ADT.equals("PV1")) {
                    PV1list = HL7GetPV1Segment(line);
                    continue;
                }
                if(ADT.equals("IN1")) {
                       continue;
                }
                if(ADT.equals("ORC")) {
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

                if(ADT.equals("OBX")){
                     OBXlist = HL7GetOBXSegment(line);
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
                     //結果タイプ
                     Set.Rtype = OBXlist.Rtype;
                     //検査結果
                     Set.studyResult = OBXlist.studyResult;
                }
                list.add(Set);
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
 
}

