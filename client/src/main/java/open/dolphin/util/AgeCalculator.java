package open.dolphin.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AgeCalculator {
    
    private static final String SAI = "歳";

    public static String getAgeAndBirthday(String mmlBirthday, int monthAge) {

        int[] spec = getAgeSpec(mmlBirthday);

        if (spec[0] != -1 && spec[1] != -1) {

            StringBuilder sb = new StringBuilder();
            sb.append(spec[0]);

            if (spec[0] < monthAge && spec[1] != 0) {
                sb.append(".").append(spec[1]);
            }

            sb.append(" ").append(SAI);
            sb.append(" (").append(mmlBirthday).append(")");
            return sb.toString();
        }
        return null;
    }

    public static String getAge(String mmlBirthday, int monthAge) {

        int[] spec = getAgeSpec(mmlBirthday);

        if (spec[0] != -1 && spec[1] != -1) {

            StringBuilder sb = new StringBuilder();
            sb.append(spec[0]);

            if (spec[0] < monthAge && spec[1] != 0) {
                sb.append(".").append(spec[1]);
            }
            return sb.toString();
        }
        return null;
    }

    public static int[] getAgeSpec(String mmlBirthday) {

        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = new GregorianCalendar(); // Today
            int years = 0;
            int month = 0;
            int days = 0;

            gc1.clear(Calendar.MILLISECOND);
            gc1.clear(Calendar.SECOND);
            gc1.clear(Calendar.MINUTE);
            gc1.clear(Calendar.HOUR_OF_DAY);

            gc2.clear(Calendar.MILLISECOND);
            gc2.clear(Calendar.SECOND);
            gc2.clear(Calendar.MINUTE);
            gc2.clear(Calendar.HOUR_OF_DAY);

            while (gc1.before(gc2)) {
                gc1.add(Calendar.YEAR, 1);
                years++;
            }

            gc1.add(Calendar.YEAR, -1);
            years--;

            while (gc1.before(gc2)) {
                gc1.add(Calendar.MONTH, 1);
                month++;
            }
            gc1.add(Calendar.MONTH, -1);
            month--;

            while (gc1.before(gc2)) {
                gc1.add(Calendar.DAY_OF_MONTH, 1);
                days++;
            }
            days--;

            return new int[]{years, month, days};

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new int[]{-1, -1, -1};
        }
    }

    public static GregorianCalendar getCalendar(String mmlDate) {

        try {
            // Trim time if contains
            mmlDate = trimTime(mmlDate);
            String[] cmp = mmlDate.split("-");
            String yearSt = cmp[0];
            String monthSt = cmp[1];
            if (monthSt.startsWith("0")) {
                monthSt = monthSt.substring(1);
            }
            String daySt = cmp[2];
            if (daySt.startsWith("0")) {
                daySt = daySt.substring(1);
            }
            int year = Integer.parseInt(yearSt);
            int month = Integer.parseInt(monthSt);
            month--;
            int day = Integer.parseInt(daySt);

            return new GregorianCalendar(year, month, day);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static String trimTime(String mmlDate) {

        if (mmlDate != null) {
            int index = mmlDate.indexOf('T');
            if (index > -1) {
                return mmlDate.substring(0, index);
            } else {
                return mmlDate;
            }
        }
        return null;
    }
    
//masuda^   和暦を含めた生年月日
    public static String getAgeAndBirthday2(String mmlBirthday, int monthAge){

        String age = getAge(mmlBirthday, monthAge);
        if (age != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(age);
            sb.append(" ");
            sb.append(SAI);
            sb.append(" ([");
            sb.append(toNengo(mmlBirthday).substring(0, 3));
            sb.append("]");
            sb.append(mmlBirthday);
            sb.append(")");
            return sb.toString();
        }
        return null;
    }

    // 西暦=>年号変換 Dr pnus?
    public static String toNengo(String mmlBirthday) {
        int year;
        int month;
        int day;
        String nengo;

        year = Integer.valueOf(mmlBirthday.substring(0,4));
        month = Integer.valueOf(mmlBirthday.substring(5,7));
        day = Integer.valueOf(mmlBirthday.substring(8,10));

        // 1990年より先は平成
        if (year >= 1990) {
            nengo = "H"; year = year - 1988;
        }
        // 1989年だったら，1月7日以前は昭和
        else if (year == 1989) {
            if (month == 1 && day <= 7) {
                nengo = "S"; year = 64;
            }
            else {
                nengo = "H"; year = 1;
            }
        }
        // 1927年から1988年は昭和
        else if (year >= 1927 && year <= 1988) {
            nengo = "S"; year = year - 1925;
        }
        // 1926年だったら，12月25日以降は昭和
        else if (year == 1926) {
            if (month == 12 && day >= 25) {
                nengo = "S"; year = 1;
            }
            else {
                nengo = "T"; year = 15;
            }
        }
        // 1913年から1925年は大正
        else if (year >= 1913 && year <= 1925) {
            nengo = "T"; year = year - 1911;
        }
        // 1912 年だったら，7/30 以降は大正
        else if (year == 1912) {
            if (month >= 8) {
                nengo = "T"; year = 1;
            }
            else if (month <= 6) {
                nengo = "M"; year = 45;
            }
            else if (day >= 30) {
                nengo = "T"; year = 1;
            }
            else {
                nengo = "M"; year = 45;
            }
        }
        // 1911年以前は明治
        else {
            nengo = "M"; year = year - 1867;
        }

        StringBuilder buf = new StringBuilder();
        buf.append(nengo);
        buf.append(String.format("%02d", year));
        buf.append("-");
        buf.append(String.format("%02d", month));
        buf.append("-");
        buf.append(String.format("%02d", day));
        return buf.toString();
    }

    // test(mmlDate形式)当時の年齢を計算する
    public static String getAge2(String mmlBirthday, String test) {

        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = getCalendar(test);
            int years = 0;

            gc1.clear(GregorianCalendar.MILLISECOND);
            gc1.clear(GregorianCalendar.SECOND);
            gc1.clear(GregorianCalendar.MINUTE);
            gc1.clear(GregorianCalendar.HOUR_OF_DAY);

            gc2.clear(GregorianCalendar.MILLISECOND);
            gc2.clear(GregorianCalendar.SECOND);
            gc2.clear(GregorianCalendar.MINUTE);
            gc2.clear(GregorianCalendar.HOUR_OF_DAY);

            while (gc1.before(gc2)) {
                gc1.add(GregorianCalendar.YEAR, 1);
                years++;
            }
            years--;

            int month = 12;

            while (gc1.after(gc2)) {
                gc1.add(GregorianCalendar.MONTH, -1);
                month--;
            }

            StringBuilder buf = new StringBuilder();
            buf.append(years);
            if (month != 0) {
                buf.append(".");
                buf.append(month);
            }
            return buf.toString();

        } catch (Exception e) {
            return null;
        }
    }
//masuda$
}
