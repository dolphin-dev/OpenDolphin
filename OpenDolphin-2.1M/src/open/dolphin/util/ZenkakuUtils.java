package open.dolphin.util;

/**
 *
 * @author Kazushi Minagawa.
 */
public class ZenkakuUtils {

    private static final char FULL_MINUS = (char)65293;
    private static final char HALF_MINUS = '-';

    private static final char[] MATCHIES = {'０', '１', '２', '３', '４', '５', '６', '７', '８', '９', '　', 'ｍ', 'ｇ', 'Ｌ', '．', '＋', FULL_MINUS};
    private static final char[] REPLACES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', 'm', 'g', 'L', '.', '+', HALF_MINUS};

    public static String toHalfNumber(String test) {
        if (test != null) {
            for (int i = 0; i < MATCHIES.length; i++) {
                test = test.replace(MATCHIES[i], REPLACES[i]);
            }
        }
        return test;
    }

    //---------------------------------------
    // UTF-8
    // コメント等の全角のマイナスを半角へ変換する
    //---------------------------------------
    public static String utf8Replace(String str) {
        if (str != null) {
            return str.replace(FULL_MINUS, HALF_MINUS);
        }
        return null;
    }
}
