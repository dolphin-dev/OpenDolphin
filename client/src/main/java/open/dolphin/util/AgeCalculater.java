package open.dolphin.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AgeCalculater {
    
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
            
//s.oh^ 2013/06/10 月齢計算
            String[] birth = mmlBirthday.split("-");
            if(birth != null && birth.length == 3) {
                years = gc2.get(Calendar.YEAR) - Integer.parseInt(birth[0]);
                month = (gc2.get(Calendar.MONTH) + 1) - Integer.parseInt(birth[1]);
                days = gc2.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(birth[2]);
                if(days < 0) {
                    month = month - 1;
                }
                if(month < 0) {
                    years = years - 1;
                    month = month + 12;
                }
                int nowMonth = gc2.get(Calendar.MONTH) + 1;
                if(gc2.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(birth[2])) {
                    days = gc2.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(birth[2]);
                }else if(gc2.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(birth[2])) {
                    days = 0;
                }else{
                    if(nowMonth == 2) {
                        if(gc2.get(Calendar.YEAR) % 4 == 0) {
                            if(gc2.get(Calendar.YEAR) % 400 == 0) {
                                days = 29 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            }else if(gc2.get(Calendar.YEAR) % 100 == 0) {
                                days = 28 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            }else{
                                days = 29 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            }
                        }else{
                            days = 28 - Integer.parseInt(birth[2]);
                            days = days + gc2.get(Calendar.DAY_OF_MONTH);
                        }
                    }else if(nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
                        days = 31 - Integer.parseInt(birth[2]);
                        days = days + gc2.get(Calendar.DAY_OF_MONTH);
                    }else{
                        days = 30 - Integer.parseInt(birth[2]);
                        days = days + gc2.get(Calendar.DAY_OF_MONTH);
                    }
                }
            }
//s.oh$

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
}
