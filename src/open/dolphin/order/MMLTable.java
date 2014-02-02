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
    
    private static Hashtable<String, String> claimClassCode;
    static {
        claimClassCode = new Hashtable<String, String>(45, 0.75f);
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
    
    private static Hashtable<String, String> departmentCode;
    static {
     
        departmentCode = new Hashtable<String, String>(40, 1.0f);
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
        departmentCode.put("”å”AŠí", "20");
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
}