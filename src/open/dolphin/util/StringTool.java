/*
 * StringTool.java
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
package open.dolphin.util;

import java.util.*;
import java.io.*;

/**
 * Utilities to handel String.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StringTool {

    private static final char[] komoji = {
        'ぁ', 'ぃ','ぅ','ぇ','ぉ','っ','ゃ','ゅ','ょ','ゎ','ァ','ィ','ゥ','ェ','ォ','ッ','ャ','ュ','ョ','ヮ'
    };
    private static final char[] ohomoji = {
        'あ', 'い','う','え','お','つ','や','ゆ','よ','わ','ア','イ','ウ','エ','オ','ツ','ヤ','ユ','ヨ','ワ'
    };

    /** Creates new StringTool */
    public StringTool() {
    }

    public static Object[] tokenToArray(String line, String delim) {

        StringTokenizer st = new StringTokenizer(line, delim, true);
        ArrayList list = new ArrayList(10);
        int state = 0;
        String token;

        while (st.hasMoreTokens()) {

            token = st.nextToken();
            switch (state) {
                case 0:
                    // VALUE_STATE
                    if (token.equals(",")) {
                        token = null;
                    }
                    else {
                        state = 1;
                    }
                    list.add(token);
                    break;

                case 1:
                    // DELIM_STATE
                    state = 0;
                    break;
            }
        }

        return list.toArray();
    }

    public static String trimSpace(String text) {

        int start = 0;
        int len = text.length();

        while( start < len) {
            if (text.charAt(start) > 32) {
                break;
            }
            start++;
        }
        int end = len - 1;
        while (end > start) {
            if (text.charAt(end) > 32) {
                break;
            }
            end--;
        }

        return end != 0 ? text.substring(start, end + 1) : null;
    }
    

    public static boolean startsWithKatakana(String s) {
        return isKatakana(s.charAt(0));
    }

    public static boolean startsWithHiragana(String s) {
        return isHiragana(s.charAt(0));
    }

    public static boolean isKatakana(char c) {
        // ア  12449  12353 半角
        // ン　12531
        return ((int)c >= 12449) && ((int)c <= 12531) ? true : false;
    }

    public static boolean isHiragana(char c) {
        // あ  12354
        // ん　12435
        return ((int)c >= 12354) && ((int)c <= 12435) ? true : false;
    }

    private static char toKatakana(char c) {
        return isHiragana(c) ? (char)((int)c + 96) : c;
    }

    public static String hiraganaToKatakana(String s) {

        int len = s.length();
        char[] src = new char[len];
        s.getChars(0, s.length(), src, 0);

        char[] dst = new char[len];
        for (int i = 0; i < len; i++) {
            dst[i] = toKatakana(src[i]);
        }
        return new String(dst);
    }
    
    public static boolean isAllDigit(String str) {
        
        boolean ret = true;
        int len = str.length();
        
        for (int i = 0; i < len; i++) {
            
            char c = str.charAt(i);
            
            if (! Character.isDigit(c)) {
                ret = false;
                break;
            }
        }
        return ret;
    }
    
    public static boolean isAllKana(String str) {
        
        boolean ret = true;
        int len = str.length();
        
        for (int i = 0; i < len; i++) {
            
            char c = str.charAt(i);
            
            if ( isKatakana(c) || isHiragana(c) ) {
                continue;
            } else {
                ret = false;
                break;
            }
        }
        return ret;
    }    
    
    /**
     * Convert to Zenkaku
     */
    public static String toZenkaku(String s) {
        int len = s.length();
        char[] src = new char[len];
        s.getChars(0, s.length(), src, 0);

        char[] dst = new char[len];
        String st;
        for (int i = 0; i < len; i++) {
            for (int k = 0; k < komoji.length; k++) {
                if (komoji[k] == src[i]) {
                    src[i] = ohomoji[k];
                }
            }
            dst[i] = toKatakana(src[i]);
        }
        return new String(dst);
    }

    public static String toKatakana(String text, boolean b) {

        if (b) {
            text = toZenkaku(text);
        }
        return hiraganaToKatakana(text);
    }

    public static String unicodeToEuc(String s) {
        String ret = null;
        try {
            ret = new String(s.getBytes("8859_1"), "EUC_JP");
        }
        catch (UnsupportedEncodingException e) {
			System.out.println(e);
			e.printStackTrace();
        }
        return ret;
    }

    public static String eucToUnicode(String s) {
        String ret = null;
        try {
            ret = new String(s.getBytes("EUC_JP"), "8859_1");
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return ret;
    }
}