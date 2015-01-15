/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.impl.server;

import java.io.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import open.dolphin.utilities.utility.OtherProcessLink;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * 受付連携
 * @author oh
 */
public class PVTReceptionLink {
    private static final String KEY_PATIENT_ID = "%PATIENTID%";                     // 患者ID
    private static final String KEY_PATIENT_ID_0SUP = "%PATIENTID0SUP%";            // 患者ID(0サプレス)
    private static final String KEY_PATIENT_KANJI = "%PATIENTKANJI%";               // 患者氏名(漢字)
    private static final String KEY_PATIENT_KANA = "%PATIENTKANA%";                 // 患者氏名(半角ｶﾅ)
    private static final String KEY_PATIENT_ZENKAKUKANA = "%PATIENTZENKAKUKANA%";   // 患者氏名(全角カナ)
    private static final String KEY_PATIENT_ASCII = "%PATIENTASCII%";               // 患者氏名(ASCII)
    private static final String KEY_PATIENT_SEX = "%PATIENTSEX%";                   // 患者性別
    private static final String KEY_PATIENT_BIRTH = "%PATIENTBIRTH%";               // 患者誕生日
    private static final String KEY_TODAY = "%TODAY%";                              // 今日の日付
    private static final String KEY_PVTDATE = "%PVTDATE%";                          // 受付日時
    private static final String KEY_PATIENT_ZIPCODE = "%PATIENTZIPCODE%";           // 患者郵便番号
    private static final String KEY_PATIENT_ADDRESS = "%PATIENTADDRESS%";           // 患者住所
    private static final String KEY_PATIENT_PHONE = "%PATIENTPHONE%";               // 患者電話番号
    
    private static final String KEY_PATIENT_TELEPHONE = "%PATIENTTELEPHONE%";       // 患者電話
    private static final String KEY_PATIENT_MOBILEPHONE = "%PATIENTMOBILEPHONE%";   // 患者携帯電話
    //private static final String KEY_INSURANCECLASS = "%INSURANCECLASS%";            // 保険種別
    //private static final String KEY_INSURANCECLASSCODE = "%INSURANCECLASSCODE%";    // 保険種別コード
    //private static final String KEY_INSURANCENUMBER = "%INSURANCENUMBER%";          // 保険者番号
    //private static final String KEY_FAMILYCLASS = "%FAMILYCLASS%";                  // 本人家族区分
    
//s.oh^ 2014/08/01 受付連携
    private static final String KEY_DEPT_CODE = "%DEPTCODE%";                       // 診療科コード
    private static final String KEY_DEPT_NAME = "%DEPTNAME%";                       // 診療科
    private static final String KEY_ATTENDING_KANJI = "%ATTENDINGKANJI%";           // 担当医(漢字)
    private static final String KEY_ATTENDING_ID = "%ATTENDINGID%";                 // 担当医(ID)
    private static final String KEY_INSURANCE_FIRST = "%INSURANCEFIRST%";           // 保険
//s.oh$
    
    private static final String KEY_FILENAME_DATE = "%FILENAMEDATE%";               // ファイル名
    
    private static final String KEY_RETURN = "%RETURN%";                            // 改行コード
    
    private static final String[][] KANA = { {"ア", "ｱ"},  {"イ", "ｲ"},  {"ウ", "ｳ"},  {"エ", "ｴ"},  {"オ", "ｵ"},
                                             {"カ", "ｶ"},  {"キ", "ｷ"},  {"ク", "ｸ"},  {"ケ", "ｹ"},  {"コ", "ｺ"},
                                             {"サ", "ｻ"},  {"シ", "ｼ"},  {"ス", "ｽ"},  {"セ", "ｾ"},  {"ソ", "ｿ"},
                                             {"タ", "ﾀ"},  {"チ", "ﾁ"},  {"ツ", "ﾂ"},  {"テ", "ﾃ"},  {"ト", "ﾄ"},
                                             {"ナ", "ﾅ"},  {"ニ", "ﾆ"},  {"ヌ", "ﾇ"},  {"ネ", "ﾈ"},  {"ノ", "ﾉ"},
                                             {"ハ", "ﾊ"},  {"ヒ", "ﾋ"},  {"フ", "ﾌ"},  {"ヘ", "ﾍ"},  {"ホ", "ﾎ"},
                                             {"マ", "ﾏ"},  {"ミ", "ﾐ"},  {"ム", "ﾑ"},  {"メ", "ﾒ"},  {"モ", "ﾓ"},
                                             {"ヤ", "ﾔ"},  {"ユ", "ﾕ"},  {"ヨ", "ﾖ"},
                                             {"ラ", "ﾗ"},  {"リ", "ﾘ"},  {"ル", "ﾙ"},  {"レ", "ﾚ"},  {"ロ", "ﾛ"},
                                             {"ワ", "ﾜ"},  {"ヲ", "ｦ"},  {"ン", "ﾝ"},
                                             {"ァ", "ｧ"},  {"ィ", "ｨ"},  {"ゥ", "ｩ"},  {"ェ", "ｪ"},  {"ォ", "ｫ"},
                                             {"ッ", "ｯ"},
                                             {"ャ", "ｬ"},  {"ュ", "ｭ"},  {"ョ", "ｮ"},
                                             {"ガ", "ｶﾞ"}, {"ギ", "ｷﾞ"}, {"グ", "ｸﾞ"}, {"ゲ", "ｹﾞ"}, {"ゴ", "ｺﾞ"},
                                             {"ザ", "ｻﾞ"}, {"ジ", "ｼﾞ"}, {"ズ", "ｽﾞ"}, {"ゼ", "ｾﾞ"}, {"ゾ", "ｿﾞ"},
                                             {"ダ", "ﾀﾞ"}, {"ヂ", "ﾁﾞ"}, {"ヅ", "ﾂﾞ"}, {"デ", "ﾃﾞ"}, {"ド", "ﾄﾞ"},
                                             {"バ", "ﾊﾞ"}, {"ビ", "ﾋﾞ"}, {"ブ", "ﾌﾞ"}, {"ベ", "ﾍﾞ"}, {"ボ", "ﾎﾞ"},
                                             {"パ", "ﾊﾟ"}, {"ピ", "ﾋﾟ"}, {"プ", "ﾌﾟ"}, {"ペ", "ﾍﾟ"}, {"ポ", "ﾎﾟ"},
                                             {"　", " "},  {"ー", "-"},
                                             {"０", "0"},  {"１", "1"},  {"２", "2"},  {"３", "3"},  {"４", "4"},  {"５", "5"}, {"６", "6"}, {"７", "7"}, {"８", "8"}, {"９", "9"},
                                             {"Ａ", "A"},  {"Ｂ", "B"},  {"Ｃ", "C"},  {"Ｄ", "D"},  {"Ｅ", "E"},  {"Ｆ", "F"}, {"Ｇ", "G"}, {"Ｈ", "H"}, {"Ｉ", "I"}, {"Ｊ", "J"},
                                             {"Ｋ", "K"},  {"Ｌ", "L"},  {"Ｍ", "M"},  {"Ｎ", "N"},  {"Ｏ", "O"},  {"Ｐ", "P"}, {"Ｑ", "Q"}, {"Ｒ", "R"}, {"Ｓ", "S"}, {"Ｔ", "T"},
                                             {"Ｕ", "U"},  {"Ｖ", "V"},  {"Ｗ", "W"},  {"Ｘ", "X"},  {"Ｙ", "Y"},  {"Ｚ", "Z"},
                                             {"ａ", "a"},  {"ｂ", "b"},  {"ｃ", "c"},  {"ｄ", "d"},  {"ｅ", "e"},  {"ｆ", "f"}, {"ｇ", "g"}, {"ｈ", "h"}, {"ｉ", "i"}, {"ｊ", "j"},
                                             {"ｋ", "k"},  {"ｌ", "l"},  {"ｍ", "m"},  {"ｎ", "n"},  {"ｏ", "o"},  {"ｐ", "p"}, {"ｑ", "q"}, {"ｒ", "r"}, {"ｓ", "s"}, {"ｔ", "t"},
                                             {"ｕ", "u"},  {"ｖ", "v"},  {"ｗ", "w"},  {"ｘ", "x"},  {"ｙ", "y"},  {"ｚ", "z"} };
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    private static final String DEFAULT_CSV_ENCODING = "Shift_JIS";
    private static final String DEFAULT_XML_ENCODING = "Shift_JIS";
    
    public PVTReceptionLink() { 
        
    }

    /**
     * CSV連携
     * @param patientModel 
     */
    public void receptionCSVLink(PatientVisitModel pvtModel) {
        PatientModel patientModel = pvtModel.getPatientModel();
        if(patientModel == null) return;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        String format = Project.getString("reception.csvlink.format");
        if(format == null || format.length() <= 0) return;
        String dir = Project.getString("reception.csvlink.dir");
        if(dir == null || dir.length() <= 0) return;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString("reception.csvlink.birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString("reception.csvlink.malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        String encoding = Project.getString("reception.csvlink.encoding", DEFAULT_CSV_ENCODING);
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        String file = Project.getString("reception.csvlink.filename", "ID_%PATIENTID%");
        String fDateFormat = Project.getString("reception.csvlink.filename.dateformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        file = file.replaceAll(KEY_TODAY, sdf3.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                if(Project.getBoolean("reception.csvlink.rename", true)) {
                    File tmp = new File(path);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                    File csv = new File(path + ".csv");
                    tmp.renameTo(csv);
                }else{
                    File tmp = new File(path + ".csv");
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * CSV連携
     * @param patientModel 
     */
    public void receptionCSVLink2(PatientVisitModel pvtModel) {
        PatientModel patientModel = pvtModel.getPatientModel();
        if(patientModel == null) return;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        String format = Project.getString("reception.csvlink2.format");
        if(format == null || format.length() <= 0) return;
        String dir = Project.getString("reception.csvlink2.dir");
        if(dir == null || dir.length() <= 0) return;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString("reception.csvlink2.birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString("reception.csvlink2.malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        String encoding = Project.getString("reception.csvlink2.encoding", DEFAULT_CSV_ENCODING);
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        String file = Project.getString("reception.csvlink2.filename", "ID_%PATIENTID%");
        String fDateFormat = Project.getString("reception.csvlink2.filename.dateformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        file = file.replaceAll(KEY_TODAY, sdf3.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                if(Project.getBoolean("reception.csvlink2.rename", true)) {
                    File tmp = new File(path);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                    File csv = new File(path + ".csv");
                    tmp.renameTo(csv);
                }else{
                    File tmp = new File(path + ".csv");
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * CSV連携
     * @param patientModel 
     */
    public void receptionCSVLink3(PatientVisitModel pvtModel) {
        PatientModel patientModel = pvtModel.getPatientModel();
        if(patientModel == null) return;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        String format = Project.getString("reception.csvlink3.format");
        if(format == null || format.length() <= 0) return;
        String dir = Project.getString("reception.csvlink3.dir");
        if(dir == null || dir.length() <= 0) return;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString("reception.csvlink3.birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString("reception.csvlink3.malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        String encoding = Project.getString("reception.csvlink3.encoding", DEFAULT_CSV_ENCODING);
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        String file = Project.getString("reception.csvlink3.filename", "ID_%PATIENTID%");
        String fDateFormat = Project.getString("reception.csvlink3.filename.dateformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        file = file.replaceAll(KEY_TODAY, sdf3.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                if(Project.getBoolean("reception.csvlink3.rename", true)) {
                    File tmp = new File(path);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                    File csv = new File(path + ".csv");
                    tmp.renameTo(csv);
                }else{
                    File tmp = new File(path + ".csv");
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * XML連携
     * @param patientModel 
     */
    public void receptionXMLLink(PatientVisitModel pvtModel) {
        PatientModel patientModel = pvtModel.getPatientModel();
        if(patientModel == null) return;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        StringBuilder sb = new StringBuilder();
        String format = Project.getString("reception.xmllink.format");
        if(format == null || format.length() <= 0) return;
        String dir = Project.getString("reception.xmllink.dir");
        if(dir == null || dir.length() <= 0) return;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString("reception.xmllink.birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString("reception.xmllink.malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        String encoding = Project.getString("reception.xmllink.encoding", DEFAULT_XML_ENCODING);
        sb.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        sb.append(format);
        String file = Project.getString("reception.xmllink.filename", "ID_%PATIENTID%");
        String fDateFormat = Project.getString("reception.xmllink.filename.dateformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        file = file.replaceAll(KEY_TODAY, sdf3.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".xml";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(sb.toString(), data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                if(Project.getBoolean("reception.xmllink.rename", true)) {
                    File tmp = new File(path);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(sb.toString());
                    bw.newLine();
                    bw.close();
                    File xml = new File(path + ".xml");
                    tmp.renameTo(xml);
                }else{
                    File tmp = new File(path + ".xml");
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 連携
     * @param patientModel 
     */
    public void receptionLink(PatientVisitModel pvtModel) {
        PatientModel patientModel = pvtModel.getPatientModel();
        if(patientModel == null) return;
        PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
        String format = Project.getString("reception.link.format");
        if(format == null || format.length() <= 0) return;
        String dir = Project.getString("reception.link.dir");
        if(dir == null || dir.length() <= 0) return;
        String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        String birthFormat = Project.getString("reception.link.birthformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
        Date date = sdf1.parse(birth, new ParsePosition(0));
        String sexFormats = Project.getString("reception.link.malefemale", "m,f");
        String[] sexFormat = sexFormats.split(",");
        String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
        String encoding = Project.getString("reception.link.encoding", DEFAULT_CSV_ENCODING);
        format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
        format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
        format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
        format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
        format = format.replaceAll(KEY_PATIENT_SEX, sex);
        format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
        format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
        if(patientModel.getAddress() != null) {
            if(patientModel.getAddress().getZipCode() != null) {
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            }
            if(patientModel.getAddress().getAddress() != null) {
                format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
            }else{
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
        }else{
            format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
            format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
        }else if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_PHONE, "");
        }
        if(patientModel.getTelephone() != null) {
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
        }else{
            format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
        }
        if(patientModel.getMobilePhone() != null) {
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
        }else{
            format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
        }
        String file = Project.getString("reception.link.filename", "ID_%PATIENTID%");
        String ext = Project.getString("reception.link.ext", ".txt");
        String fDateFormat = Project.getString("reception.link.filename.dateformat", DEFAULT_DATE_FORMAT);
        SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
        file = file.replaceAll(KEY_TODAY, sdf3.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ext;
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                if(Project.getBoolean("reception.link.rename", true)) {
                    File tmp = new File(path);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                    File csv = new File(path + ext);
                    tmp.renameTo(csv);
                }else{
                    File tmp = new File(path + ext);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                    bw.write(format);
                    bw.newLine();
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 連携
     * @param patientModel 
     */
    public void receiptLink(PatientVisitModel pvtModel) {
        final String KEY_DEF = "receipt.link";
        int num = Project.getInt("receipt.link.num", 1);
        for(int i = 1; i < num+1; i++) {
            PatientModel patientModel = pvtModel.getPatientModel();
            if(patientModel == null) return;
            PVTKanaToAscii kana2Ascii = new PVTKanaToAscii();
            String format = Project.getString(KEY_DEF + String.valueOf(i) + ".format");
            if(format == null || format.length() <= 0) return;
            String dir = Project.getString(KEY_DEF + String.valueOf(i) + ".dir");
            if(dir == null || dir.length() <= 0) return;
            String birth = (patientModel.getBirthday() != null) ? patientModel.getBirthday().replaceAll("-", "").replaceAll("/", "") : "";
            SimpleDateFormat sdf1 = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            String birthFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".birthformat", DEFAULT_DATE_FORMAT);
            SimpleDateFormat sdf2 = new SimpleDateFormat(birthFormat);
            Date date = sdf1.parse(birth, new ParsePosition(0));
            String sexFormats = Project.getString(KEY_DEF + String.valueOf(i) + ".malefemale", "m,f");
            String[] sexFormat = sexFormats.split(",");
            String sex = (patientModel.getGender() != null) ? (patientModel.getGender().toLowerCase().startsWith("m") ? sexFormat[0] : sexFormat[1]) : "";
            String encoding = Project.getString(KEY_DEF + String.valueOf(i) + ".encoding", DEFAULT_CSV_ENCODING);
//s.oh^ 2014/08/01 受付連携
            format = format.replaceAll(KEY_DEPT_CODE, pvtModel.getDeptCode());
            format = format.replaceAll(KEY_DEPT_NAME, pvtModel.getDeptName());
            format = format.replaceAll(KEY_ATTENDING_KANJI, pvtModel.getDoctorName());
            format = format.replaceAll(KEY_ATTENDING_ID, pvtModel.getDoctorId());
            format = format.replaceAll(KEY_INSURANCE_FIRST, (pvtModel.getFirstInsurance() == null) ? "" : pvtModel.getFirstInsurance());
//s.oh$
            format = format.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
            format = format.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
            format = format.replaceAll(KEY_PATIENT_KANJI, patientModel.getFullName());
            format = format.replaceAll(KEY_PATIENT_KANA, fullKanaToHalfKana(patientModel.getKanaName()));
            format = format.replaceAll(KEY_PATIENT_ZENKAKUKANA, patientModel.getKanaName());
            format = format.replaceAll(KEY_PATIENT_ASCII, kana2Ascii.CHGKanatoASCII(patientModel.getKanaName(), ""));
            format = format.replaceAll(KEY_PATIENT_SEX, sex);
            format = format.replaceAll(KEY_PATIENT_BIRTH, sdf2.format(date));
            format = format.replaceAll(KEY_PVTDATE, pvtModel.getPvtDate());
            if(patientModel.getAddress() != null) {
                if(patientModel.getAddress().getZipCode() != null) {
                    format = format.replaceAll(KEY_PATIENT_ZIPCODE, patientModel.getAddress().getZipCode());
                }else{
                    format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
                }
                if(patientModel.getAddress().getAddress() != null) {
                    format = format.replaceAll(KEY_PATIENT_ADDRESS, patientModel.getAddress().getAddress());
                }else{
                    format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
                }
            }else{
                format = format.replaceAll(KEY_PATIENT_ZIPCODE, "");
                format = format.replaceAll(KEY_PATIENT_ADDRESS, "");
            }
            if(patientModel.getTelephone() != null) {
                format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getTelephone());
            }else if(patientModel.getMobilePhone() != null) {
                format = format.replaceAll(KEY_PATIENT_PHONE, patientModel.getMobilePhone());
            }else{
                format = format.replaceAll(KEY_PATIENT_PHONE, "");
            }
            if(patientModel.getTelephone() != null) {
                format = format.replaceAll(KEY_PATIENT_TELEPHONE, patientModel.getTelephone());
            }else{
                format = format.replaceAll(KEY_PATIENT_TELEPHONE, "");
            }
            if(patientModel.getMobilePhone() != null) {
                format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, patientModel.getMobilePhone());
            }else{
                format = format.replaceAll(KEY_PATIENT_MOBILEPHONE, "");
            }
            format = format.replaceAll(KEY_RETURN, "\n");
            String file = Project.getString(KEY_DEF + String.valueOf(i) + ".filename", "ID_%PATIENTID%");
            String ext = Project.getString(KEY_DEF + String.valueOf(i) + ".ext", ".txt");
            String fDateFormat = Project.getString(KEY_DEF + String.valueOf(i) + ".filename.dateformat", DEFAULT_DATE_FORMAT);
            SimpleDateFormat sdf3 = new SimpleDateFormat(fDateFormat);
            Date today = new Date();
            file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
            file = file.replaceAll(KEY_PATIENT_ID_0SUP, zeroSuppress(patientModel.getPatientId()));
            file = file.replaceAll(KEY_TODAY, sdf3.format(today));
            String path = dir + File.separator + file;
            if(path.startsWith("[TCP]")) {
                path = path.substring(path.indexOf("]") + 1) + ext;
                String[] data = path.split(",");
                if(data.length < 3) return;
                OtherProcessLink opl = new OtherProcessLink();
                opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "TCP", data[0], data[1], data[2]);
                Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, format);
            }else{
                try {
                    if(Project.getBoolean(KEY_DEF + String.valueOf(i) + ".rename", false)) {
                        String tmpExt = Project.getString(KEY_DEF + String.valueOf(i) + ".tmpext", "");
                        File tmp = new File(path + tmpExt);
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                        bw.write(format);
                        bw.newLine();
                        bw.close();
                        File csv = new File(path + ext);
                        tmp.renameTo(csv);
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "FILE", tmp.getPath(), "→", csv.getPath());
                        Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, format);
                    }else{
                        File tmp = new File(path + ext);
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                        bw.write(format);
                        bw.newLine();
                        bw.close();
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "FILE", tmp.getPath());
                        Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, format);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "ファイル出力失敗", ex.toString());
                }
            }
        }
    }
    
    /**
     * Claimの横流し
     * @param pvtXml 
     */
    public void claimWrite(String pvtXml) {
        final String KEY_DEF = "receipt.claim.write";
        int num = Project.getInt("receipt.claim.write.num", 1);
        for(int i = 1; i < num+1; i++) {
            String dir = Project.getString(KEY_DEF + String.valueOf(i) + ".dir");
            if(dir == null || dir.length() <= 0) return;
            String encoding = Project.getString(KEY_DEF + String.valueOf(i) + ".encoding", DEFAULT_CSV_ENCODING);
            String file = Project.getString(KEY_DEF + String.valueOf(i) + ".filename", "Claim.txt");
            String path = dir + File.separator + file;
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(pvtXml);
                bw.newLine();
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(PVTBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 全角カナから半角カナへの変換
     * @param kana
     * @return 
     */
    private String fullKanaToHalfKana(String kana) {
        StringBuffer sb = new StringBuffer();
        for(String [] pair : KANA) {
            //switch('') {
            //    case '':
            //        break;
            //}
            kana = kana.replaceAll(pair[0], pair[1]);
        }
        return kana;
    }
    
    /**
     * ゼロサプレス
     * @param data
     * @return 
     */
    private String zeroSuppress(String data) {
        String ret = null;
        Pattern ptn = java.util.regex.Pattern.compile("^0+([0-9]+.*)");
        Matcher m = ptn.matcher(data);
        if(m.matches()) {
            ret = m.group(1);
        }else{
            ret = data;
        }
        return ret;
    }
    
    /**
     * ゼロパディング
     * @param data
     * @param digit
     * @return 
     */
    private String zeroPadding(String data, int digit) {
        String ret = null;
        String format = "%0" + String.valueOf(digit) + "d";
        ret = String.format(format, data);
        return ret;
    }
}
