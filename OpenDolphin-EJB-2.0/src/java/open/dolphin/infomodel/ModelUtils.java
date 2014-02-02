package open.dolphin.infomodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * InfoModel
 *
 * @author Minagawa,Kazushi
 */
public class ModelUtils implements IInfoModel {
    
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
    
    public static String trimDate(String mmlDate) {
        
        if (mmlDate != null) {
            int index = mmlDate.indexOf('T');
            if (index > -1) {
                // THH:mm:ss -> HH:mm
                return mmlDate.substring(index + 1, index + 6);
            } else {
                return mmlDate;
            }
        }
        return null;
    }
    
    public static String getAgeBirthday(String mmlBirthday) {

        String age = getAge(mmlBirthday);

        if (age != null) {

            StringBuilder sb = new StringBuilder();
            sb.append(age);
            sb.append(" ");
            sb.append(AGE);
            sb.append(" (");
            sb.append(mmlBirthday);
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
    
    public static String getAge(String mmlBirthday) {
        
        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = new GregorianCalendar(); // Today
            int years = 0;
            int month = 0;
            
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
            month--;
            
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

    public static int[] getAgeSpec(String mmlBirthday) {

        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = new GregorianCalendar(); // Today
            int years = 0;
            int month = 0;

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
            month--;

            return new int[]{years, month};

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new int[]{-1, -1};
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
    
    
    public static Date getDateAsObject(String mmlDate) {
        if (mmlDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_WITHOUT_TIME);
                return sdf.parse(mmlDate);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
    
    public static Date getDateTimeAsObject(String mmlDate) {
        if (mmlDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
                return sdf.parse(mmlDate);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
    
    public static String getDateAsString(Date date) {
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_WITHOUT_TIME);
                return sdf.format(date);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
    
    public static String getDateTimeAsString(Date date) {
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
                return sdf.format(date);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
    
    public static String getDateAsFormatString(Date date, String format) {
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.format(date);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
    
    public static String getGenderDesc(String gender) {
        
        if (gender != null) {
            String test = gender.toLowerCase();
            if (test.equals(MALE)) {
                return MALE_DISP;
            } else if (test.equals(FEMALE)) {
                return FEMALE_DISP;
            } else {
                return UNKNOWN;
            }
        }
        return UNKNOWN;
    }
    
    public boolean isValidModel() {
        return true;
    }
    
    public static String[] splitDiagnosis(String diagnosis) {
        if (diagnosis == null) {
            return null;
        }
        String[] ret = null;
        try {
            ret = diagnosis.split("\\s*,\\s*");
        } catch (Exception e) {
        }
        return ret;
    }
    
    public static String getDiagnosisName(String hasAlias) {
        String[] splits = splitDiagnosis(hasAlias);
        return (splits != null && splits.length == 2 && splits[0] != null) ? splits[0] : hasAlias;
    }
    
    public static String getDiagnosisAlias(String hasAlias) {
        String[] splits = splitDiagnosis(hasAlias);
        return (splits != null && splits.length == 2 && splits[1] != null) ? splits[1] : null;
    }
}
