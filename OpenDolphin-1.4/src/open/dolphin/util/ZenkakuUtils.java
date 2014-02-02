package open.dolphin.util;

/**
 *
 * @author kazm
 */
public class ZenkakuUtils {

    private static final char[] MATCHIES = {'ÇO', 'ÇP', 'ÇQ', 'ÇR', 'ÇS', 'ÇT', 'ÇU', 'ÇV', 'ÇW', 'ÇX', 'Å@', 'Çç', 'Çá', 'ÅD', 'Å{', 'Å['};
    private static final char[] REPLACES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', 'm', 'g', '.', '+', '-'};

    public static String toHalfNumber(String test) {
        for (int i = 0; i < MATCHIES.length; i++) {
            test = test.replace(MATCHIES[i], REPLACES[i]);
        }
        return test;
    }
}
