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
import open.dolphin.utilities.utility.OtherProcessLink;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;

/**
 * 受付連携
 * @author oh
 */
public class PVTReceptionLink {
    private static final String KEY_PATIENT_ID = "%PATIENTID%";                     // 患者ID
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
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_TODAY, sdf1.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(format);
                bw.newLine();
                bw.close();
                File csv = new File(path + ".csv");
                tmp.renameTo(csv);
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
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_TODAY, sdf1.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(format);
                bw.newLine();
                bw.close();
                File csv = new File(path + ".csv");
                tmp.renameTo(csv);
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
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_TODAY, sdf1.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".csv";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(format);
                bw.newLine();
                bw.close();
                File csv = new File(path + ".csv");
                tmp.renameTo(csv);
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
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_TODAY, sdf1.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ".xml";
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(sb.toString(), data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(sb.toString());
                bw.newLine();
                bw.close();
                File xml = new File(path + ".xml");
                tmp.renameTo(xml);
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
        Date today = new Date();
        file = file.replaceAll(KEY_PATIENT_ID, patientModel.getPatientId());
        file = file.replaceAll(KEY_TODAY, sdf1.format(today));
        String path = dir + File.separator + file;
        if(path.startsWith("[TCP]")) {
            path = path.substring(path.indexOf("]") + 1) + ext;
            String[] data = path.split(",");
            if(data.length < 3) return;
            OtherProcessLink opl = new OtherProcessLink();
            opl.linkTCPToFile(format, data[0], Integer.valueOf(data[1]), data[2]);
        }else{
            try {
                File tmp = new File(path);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), encoding));
                bw.write(format);
                bw.newLine();
                bw.close();
                File csv = new File(path + ext);
                tmp.renameTo(csv);
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
}
