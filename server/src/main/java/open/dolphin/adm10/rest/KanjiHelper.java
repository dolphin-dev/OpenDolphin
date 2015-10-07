/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.adm10.rest;

/**
 *
 * @author kazushi
 */
public class KanjiHelper {

    private static final char[] komoji = { 'ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ', 'っ', 'ゃ',
    'ゅ', 'ょ', 'ゎ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ッ', 'ャ', 'ュ', 'ョ', 'ヮ' };

    private static final char FIRST_HIRAGANA    = 'ぁ';
    private static final char LAST_HIRAGANA     = 'ん';
    private static final char FIRST_KATAKANA    = 'ァ';
    private static final char LAST_KATAKANA     = 'ヶ';

    private static final String[] HALF_KATAKANA = { "ｧ", "ｱ", "ｨ", "ｲ", "ｩ",
      "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ", "ｸﾞ", "ｹ",
      "ｹﾞ", "ｺ", "ｺﾞ", "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ",
      "ｿﾞ", "ﾀ", "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ",
      "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾊﾞ", "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ",
      "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ", "ﾏ", "ﾐ", "ﾑ", "ﾒ",
      "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ﾜ",
      "ﾜ", "ｲ", "ｴ", "ｦ", "ﾝ", "ｳﾞ", "ｶ", "ｹ" };

    public static boolean isKatakana(char c) {
        return (c >= FIRST_KATAKANA && c <= LAST_KATAKANA) ? true : false;
    }

    public static boolean isHiragana(char c) {
        return (c >= FIRST_HIRAGANA && c <= LAST_HIRAGANA) ? true : false;
    }

    private static char toKatakana(char c) {
        return  (char) ((int)FIRST_KATAKANA + (int) c - (int)FIRST_HIRAGANA);
    }

    public static String hiraganaToKatakana(String s) {

        int len = s.length();
        char[] src = new char[len];
        s.getChars(0, s.length(), src, 0);

        char[] dst = new char[len];
        for (int i = 0; i < len; i++) {
            if (isHiragana(src[i])) {
                dst[i] = toKatakana(src[i]);
            // あ！の検索不具合対応
            }else{
                dst[i] = src[i];
            }

        }
        return new String(dst);
    }

    public static String matomoTobaka(String s) {

        int len = s.length();
        char[] src = new char[len];
        s.getChars(0, s.length(), src, 0);

        char[] dst = new char[len];
        for (int i = 0; i < len; i++) {
            int index = (int)src[i] - (int)FIRST_KATAKANA;
            dst[i] = HALF_KATAKANA[index].charAt(0);

        }
        return new String(dst);
    }
}
