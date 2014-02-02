/*
 * MMLTable.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.order;

import java.util.*;

/**
 * MML Table Dictionary class.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MMLTable {

    /** Creates new MMLTable */
    public MMLTable() {
    }

    private static Hashtable diagnosisCategoryDesc;
    static {
        diagnosisCategoryDesc = new Hashtable(20, 0.75f);
        diagnosisCategoryDesc.put("mainDiagnosis", "å•a–¼");
        diagnosisCategoryDesc.put("complication", "‡•¹(•¹‘¶)Ç");
        diagnosisCategoryDesc.put("drg", "f’fŒQ–¼(DRG)");
        diagnosisCategoryDesc.put("academicDiagnosis", "Šwpf’f–¼");
        diagnosisCategoryDesc.put("claimingDiagnosis", "ˆã–•a–¼");
        diagnosisCategoryDesc.put("clinicalDiagnosis", "—Õ°f’f–¼");
        diagnosisCategoryDesc.put("pathologicalDiagnosis", "•a—f’f–¼");
        diagnosisCategoryDesc.put("laboratoryDiagnosis", "ŒŸ¸f’f–¼");
        diagnosisCategoryDesc.put("operativeDiagnosis", "èpf’f–¼");
        diagnosisCategoryDesc.put("confirmedDiagnosis", "Šm’èf’f");
        diagnosisCategoryDesc.put("suspectedDiagnosis", "‹^‚¢•a–¼");        
    }
    public static String getDiagnosisCategoryDesc(String key) {
        return (String)diagnosisCategoryDesc.get(key);
    }
    
    private static Hashtable diagnosisCategoryValue;
    static {
        diagnosisCategoryValue = new Hashtable(20, 0.75f);
        diagnosisCategoryValue.put("å•a–¼", "mainDiagnosis");
        diagnosisCategoryValue.put("‡•¹(•¹‘¶)Ç", "complication");
        diagnosisCategoryValue.put("f’fŒQ–¼(DRG)", "drg");
        diagnosisCategoryValue.put("Šwpf’f–¼", "academicDiagnosis");
        diagnosisCategoryValue.put("ˆã–•a–¼", "claimingDiagnosis");
        diagnosisCategoryValue.put("—Õ°f’f–¼", "clinicalDiagnosis");
        diagnosisCategoryValue.put("•a—f’f–¼", "pathologicalDiagnosis");
        diagnosisCategoryValue.put("ŒŸ¸f’f–¼", "laboratoryDiagnosis");
        diagnosisCategoryValue.put("èpf’f–¼", "operativeDiagnosis");
        diagnosisCategoryValue.put("Šm’èf’f", "confirmedDiagnosis");
        diagnosisCategoryValue.put("‹^‚¢•a–¼", "suspectedDiagnosis");        
    }
    public static String getDiagnosisCategoryValue(String key) {
        return (String)diagnosisCategoryValue.get(key);
    }    
    
        
    private static Hashtable diagnosisCategoryTable;
    static {
        diagnosisCategoryTable = new Hashtable(20, 0.75f);
        diagnosisCategoryTable.put("mainDiagnosis", "MML0012");
        diagnosisCategoryTable.put("complication", "MML0012");
        diagnosisCategoryTable.put("drg", "MML0012");
        diagnosisCategoryTable.put("academicDiagnosis", "MML0013");
        diagnosisCategoryTable.put("claimingDiagnosis", "MML0013");
        diagnosisCategoryTable.put("clinicalDiagnosis", "MML0014");
        diagnosisCategoryTable.put("pathologicalDiagnosis", "MML0014");
        diagnosisCategoryTable.put("laboratoryDiagnosis", "MML0014");
        diagnosisCategoryTable.put("operativeDiagnosis", "MML0014");
        diagnosisCategoryTable.put("confirmedDiagnosis", "MML0015");
        diagnosisCategoryTable.put("suspectedDiagnosis", "MML0015");        
    }
    public static String getDiagnosisCategoryTable(String key) {
        return (String)diagnosisCategoryTable.get(key);
    }    

    
    private static Hashtable diagnosisOutcomeDesc;
    static {
        diagnosisOutcomeDesc = new Hashtable(20, 0.75f);
        diagnosisOutcomeDesc.put("died", "€–S");
        diagnosisOutcomeDesc.put("worsening", "ˆ«‰»");
        diagnosisOutcomeDesc.put("unchanged", "•s•Ï");
        diagnosisOutcomeDesc.put("recovering", "‰ñ•œ");
        diagnosisOutcomeDesc.put("fullyRecovered", "‘S¡");
        diagnosisOutcomeDesc.put("sequelae", "‘±”­Ç(‚Ì”­¶)");
        diagnosisOutcomeDesc.put("end", "I—¹");
        diagnosisOutcomeDesc.put("pause", "’†~");
        diagnosisOutcomeDesc.put("continued", "Œp‘±");
        diagnosisOutcomeDesc.put("transfer", "“]ˆã");
        diagnosisOutcomeDesc.put("transferAcute", "“]ˆã(‹}«•a‰@‚Ö)");
        diagnosisOutcomeDesc.put("transferChronic", "“]ˆã(–«•a‰@‚Ö)"); 
        diagnosisOutcomeDesc.put("home", "©‘î‚Ö‘Ş‰@"); 
        diagnosisOutcomeDesc.put("unknown", "•s–¾"); 
    }
    public static String getDiagnosisOutcomeDesc(String key) {
        return (String)diagnosisOutcomeDesc.get(key);
    }    
    
    private static Hashtable diagnosisOutcomeValue;
    static {
        diagnosisOutcomeValue = new Hashtable(20, 0.75f);
        diagnosisOutcomeValue.put("€–S", "died");
        diagnosisOutcomeValue.put("ˆ«‰»", "worsening");
        diagnosisOutcomeValue.put("•s•Ï", "unchanged");
        diagnosisOutcomeValue.put("‰ñ•œ", "recovering");
        diagnosisOutcomeValue.put("‘S¡", "fullyRecovered");
        diagnosisOutcomeValue.put("‘±”­Ç(‚Ì”­¶)", "sequelae");
        diagnosisOutcomeValue.put("I—¹", "end");
        diagnosisOutcomeValue.put("’†~", "pause");
        diagnosisOutcomeValue.put("Œp‘±", "continued");
        diagnosisOutcomeValue.put("“]ˆã", "transfer");
        diagnosisOutcomeValue.put("“]ˆã(‹}«•a‰@‚Ö)", "transferAcute");
        diagnosisOutcomeValue.put("“]ˆã(–«•a‰@‚Ö)", "transferChronic"); 
        diagnosisOutcomeValue.put("©‘î‚Ö‘Ş‰@", "home"); 
        diagnosisOutcomeValue.put("•s–¾", "unknown"); 
    }
    public static String getDiagnosisOutcomeValue(String key) {
        return (String)diagnosisOutcomeValue.get(key);
    }    
    
    
    private static Hashtable claimClassCode;
    static {
     
        claimClassCode = new Hashtable(45, 0.75f);
        claimClassCode.put("110", "‰f");
        claimClassCode.put("120", "Äf(Äf)");
        claimClassCode.put("122", "Äf(ŠO—ˆŠÇ—‰ÁZ)");
        claimClassCode.put("123", "Äf(ŠÔŠO)");
        claimClassCode.put("124", "Äf(‹x“ú)");
        claimClassCode.put("125", "Äf([–é)");
        claimClassCode.put("130", "w“±");
        claimClassCode.put("140", "İ‘î");
        claimClassCode.put("210", "“Š–ò(“à•E“Ú•E’²Ü)(“ü‰@ŠO)");
        claimClassCode.put("230", "“Š–ò(ŠO—pE’²Ü)(“ü‰@ŠO)");
        claimClassCode.put("240", "“Š–ò(’²Ü)(“ü‰@)");
        claimClassCode.put("250", "“Š–ò(ˆ•û)");
        claimClassCode.put("260", "“Š–ò(–ƒ“Å)");
        claimClassCode.put("270", "“Š–ò(’²Šî)");
        claimClassCode.put("300", "’Ë(¶•¨Šw“I»ÜE¸–§‘±“_“HE–ƒ–ò)");
        claimClassCode.put("311", "’Ë(”ç‰º‹Ø“÷“à)");
        claimClassCode.put("321", "’Ë(Ã–¬“à)");
        claimClassCode.put("331", "’Ë(‚»‚Ì‘¼)");
        claimClassCode.put("400", "ˆ’u");
        claimClassCode.put("500", "èp(èp)");
        claimClassCode.put("502", "èp(—AŒŒ)");
        claimClassCode.put("503", "èp(ƒMƒvƒX)");
        claimClassCode.put("540", "–ƒŒ");
        claimClassCode.put("600", "ŒŸ¸");
        claimClassCode.put("700", "‰æ‘œf’f");
        claimClassCode.put("800", "‚»‚Ì‘¼");
        claimClassCode.put("903", "“ü‰@(“ü‰@—¿)");
        claimClassCode.put("906", "“ü‰@(ŠO”‘)");
        claimClassCode.put("910", "“ü‰@(“ü‰@ˆãŠwŠÇ——¿)");
        claimClassCode.put("920", "“ü‰@(“Á’è“ü‰@—¿E‚»‚Ì‘¼)");
        claimClassCode.put("970", "“ü‰@(H–—Ã—{)");
        claimClassCode.put("971", "“ü‰@(•W€•‰’SŠz)");
    }    
    public static String getClaimClassCodeName(String key) {
        return (String)claimClassCode.get(key);
    }
    
    private static Hashtable departmentCode;
    static {
     
        departmentCode = new Hashtable(40, 1.0f);
        departmentCode.put("“à‰È", "01");
        departmentCode.put("¸_‰È", "02");
        departmentCode.put("_Œo‰È", "03");
        departmentCode.put("_Œo“à‰È", "04");
        departmentCode.put("ŒÄ‹zŠí‰È", "05");
        departmentCode.put("Á‰»Ší‰È", "06");
        departmentCode.put("ˆİ’°‰È", "07");
        departmentCode.put("zŠÂŠí‰È", "08");
        departmentCode.put("¬™‰È", "09");
        departmentCode.put("ŠO‰È", "10");
        departmentCode.put("®Œ`ŠO‰È", "11");
        departmentCode.put("Œ`¬ŠO‰È", "12");
        departmentCode.put("”ü—eŠO‰È", "13");
        departmentCode.put("”]_ŒoŠO‰È", "14");
        departmentCode.put("ŒÄ‹zŠíŠO‰È", "15");
        departmentCode.put("S‘ŸŒŒŠÇŠO‰È", "16");
        departmentCode.put("¬™ŠO‰È", "17");
        departmentCode.put("”ç•†‚Ğ”AŠí‰È", "18");
        departmentCode.put("”ç•†‰È", "19");
        departmentCode.put("‚Ğ”AŠí‰È", "20");
        departmentCode.put("«•a‰È", "21");
        departmentCode.put("‚±‚¤–å‰È", "22");
        departmentCode.put("Y•wl‰È", "23");
        departmentCode.put("Y‰È", "24");
        departmentCode.put("•wl‰È", "25");
        departmentCode.put("Šá‰È", "26");
        departmentCode.put("¨•@‚¢‚ñ‚±‚¤‰È", "27");
        departmentCode.put("‹CŠÇH“¹‰È", "28");
        departmentCode.put("—Šwf—Ã‰È", "29");
        departmentCode.put("•úËü‰È", "30");
        departmentCode.put("–ƒŒ‰È", "31");
        departmentCode.put("lH“§Í‰È", "32");
        departmentCode.put("S—Ã“à‰È", "33");
        departmentCode.put("ƒAƒŒƒ‹ƒM[", "34");
        departmentCode.put("ƒŠƒEƒ}ƒ` ", "35");
        departmentCode.put("ƒŠƒnƒrƒŠ", "36");
        departmentCode.put("èI‹„", "A1");
    }    
    public static String getDepartmentCode(String key) {
        return (String)departmentCode.get(key);
    }   
    
    private static Hashtable departmentName;
    static {
     
        departmentName = new Hashtable(40, 1.0f);
        departmentName.put("01", "“à‰È");
        departmentName.put("02", "¸_‰È");
        departmentName.put("03", "_Œo‰È");
        departmentName.put("04", "_Œo“à‰È");
        departmentName.put("05", "ŒÄ‹zŠí‰È");
        departmentName.put("06", "Á‰»Ší‰È");
        departmentName.put("07", "ˆİ’°‰È");
        departmentName.put("08", "zŠÂŠí‰È");
        departmentName.put("09", "¬™‰È");
        departmentName.put("10", "ŠO‰È");
        departmentName.put("11", "®Œ`ŠO‰È");
        departmentName.put("12", "Œ`¬ŠO‰È");
        departmentName.put("13", "”ü—eŠO‰È");
        departmentName.put("14", "”]_ŒoŠO‰È");
        departmentName.put("15", "ŒÄ‹zŠíŠO‰È");
        departmentName.put("16", "S‘ŸŒŒŠÇŠO‰È");
        departmentName.put("17", "¬™ŠO‰È");
        departmentName.put("18", "”ç•†‚Ğ”AŠí‰È");
        departmentName.put("19", "”ç•†‰È");
        departmentName.put("20", "‚Ğ”AŠí‰È");
        departmentName.put("21", "«•a‰È");
        departmentName.put("22", "‚±‚¤–å‰È");
        departmentName.put("23", "Y•wl‰È");
        departmentName.put("24", "Y‰È");
        departmentName.put("25", "•wl‰È");
        departmentName.put("26", "Šá‰È");
        departmentName.put("27", "¨•@‚¢‚ñ‚±‚¤‰È");
        departmentName.put("28", "‹CŠÇH“¹‰È");
        departmentName.put("29", "—Šwf—Ã‰È");
        departmentName.put("30", "•úËü‰È");
        departmentName.put("31", "–ƒŒ‰È");
        departmentName.put("32", "lH“§Í‰È");
        departmentName.put("33", "S—Ã“à‰È");
        departmentName.put("34", "ƒAƒŒƒ‹ƒM[");
        departmentName.put("35", "ƒŠƒEƒ}ƒ` ");
        departmentName.put("36", "ƒŠƒnƒrƒŠ");
        departmentName.put("A1", "èI‹„");
    }    
    public static String getDepartmentName(String key) {
        return (String)departmentName.get(key);
    }    
}