package open.dolphin.infomodel;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * InfoModel
 *
 * @author Minagawa,Kazushi
 */
public class ModelUtils implements IInfoModel {
    
    public static final Date AD1800 = new Date(-5362016400000L);
    
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

    public static String getGenderMFDesc(String gender) {

        if (gender != null) {
            String test = gender.toLowerCase();
            if (test.startsWith("m") || test.startsWith("男") ) {
                return "M";
            } else if (test.startsWith("f") || test.startsWith("女") ) {
                return "F";
            } else {
                return "U";
            }
        }
        return "U";
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

    public static ModuleModel cloneModule(ModuleModel module) {
        try {
            return (ModuleModel)module.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static SchemaModel cloneSchema(SchemaModel model) {
        try {
            return (SchemaModel)model.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static AttachmentModel cloneAttachment(AttachmentModel model) {
        try {
            return (AttachmentModel)model.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static BundleDolphin cloneBundleDolphin(BundleDolphin model) {
        try {
            return (BundleDolphin)model.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static BundleMed cloneBundleMed(BundleMed model) {
        try {
            return (BundleMed)model.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
//masuda^
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
    // 日付の比較のためにNullなら大昔にする
    public static GregorianCalendar getStartDate(Date start) {
        GregorianCalendar ret;
        if (start != null) {
            ret = getMidnightGc(start);
        } else {
            ret = new GregorianCalendar();
            ret.setTime(AD1800);
        }
        return ret;
    }
    // 日付の比較のためにNullなら遠い未来にする
    public static GregorianCalendar getEndedDate(Date ended) {
        GregorianCalendar ret;
        if (ended != null) {
            ret = getMidnightGc(ended);
        } else {
            ret = new GregorianCalendar();
            ret.setTime(new Date(Long.MAX_VALUE));
        }
        return ret;
    }
    // 指定日の０時０分０秒のGregorianCalendarを取得する
    public static GregorianCalendar getMidnightGc(Date d) {
        GregorianCalendar ret = new GregorianCalendar();
        ret.setTime(d);
        int year = ret.get(GregorianCalendar.YEAR);
        int month = ret.get(GregorianCalendar.MONTH);
        int date = ret.get(GregorianCalendar.DAY_OF_MONTH);
        ret.clear();
        ret.set(year, month, date);
        return ret;
    }
    //
    public static boolean isDateBetween(Date start, Date end, Date test) {
        //boolean ret = (test.after(start) || test.getTime() == start.getTime())
        //           && (test.before(end) || test.getTime() == end.getTime());
        boolean ret = !test.before(start) && !test.after(end);
        return ret;
    }
    
    public static String getAgeBirthday2(String mmlBirthday){

        String age = getAge(mmlBirthday);
        if (age != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(age);
            sb.append(" ");
            sb.append(AGE);
            sb.append(" ([");
            sb.append(toNengo(mmlBirthday).substring(0, 3));
            sb.append("]");
            sb.append(mmlBirthday);
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
    
    //// 西暦=>年号変換
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
        if (year <= 9) {
            buf.append("0");
        }
        buf.append(Integer.toString(year));
        buf.append("-");
        if (month <= 9) {
            buf.append("0");
        }
        buf.append(Integer.toString(month));
        buf.append("-");
        if (day <= 9) {
            buf.append("0");
        }
        buf.append(Integer.toString(day));
        return buf.toString();
    }
    
    public static String getByoKanrenKbnStr(int byoKanrenKbn) {
        String ret = "";
        switch (byoKanrenKbn) {
            case 3:
                ret = "皮特疾Ⅰ";
                break;
            case 4:
                ret = "皮特疾Ⅱ";
                break;
            case 5:
                ret = "特定疾患";
                break;
        }
        return ret;
    }
    
    public static String extractText(String xml) {
        StringBuilder sb = new StringBuilder();
        String head[] = xml.split("<text>");
        for (String str : head) {
            String tail[] = str.split("</text>");
            if (tail.length == 2) {
                sb.append(tail[0].trim());
            }
        }
        return sb.toString();
    }

    public static Object xmlDecode(byte[] bytes) {
        
        // target should not be null でヌルポとか何とか…
        // org.hibernate.collection.PersistentBag でヌルポとか何とか…
        // なんでやねん
        ExceptionListener el = new ExceptionListener() {

            public void exceptionThrown(Exception e) {
            }
        };

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));
        
        d.setExceptionListener(el);
        
        return d.readObject();
    }
    
    public static String convertListLongToStr(List<Long> list){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Long value : list) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(String.valueOf(value));
        }
        return sb.toString();
    }
    
    public static List<Long> convertStrToListLong(String str) {
        String[] values = str.split(",");
        List<Long> list = new ArrayList<Long>();
        for (String value : values) {
            list.add(Long.valueOf(value));
        }
        return list;
    }
//masuda$
    
    
//新宿ヒロクリニック 処方箋印刷^       
    public static String convertToGengo(String dateStr) {
        int year;
        int month;
        int day;
        String nengo;

        year = Integer.valueOf(dateStr.substring(0, 4));
        month = Integer.valueOf(dateStr.substring(5, 7));
        day = Integer.valueOf(dateStr.substring(8, 10));

        if (year >= 1990) {
            // 1990年より先は平成
            nengo = "平成";
            year = year - 1988;
        } else if (year == 1989) {
            // 1989年だったら，1月7日以前は昭和
            if (month == 1 & day <= 7) {
                nengo = "昭和";
                year = 64;
            } else {
                nengo = "平成";
                year = 1;
            }
        } else if (year >= 1927 & year <= 1988) {
            // 1927年から1988年は昭和
            nengo = "昭和";
            year = year - 1925;
        } else if (year == 1926) {
            // 1926年だったら，12月25日以降は昭和
            if (month == 12 & day >= 25) {
                nengo = "昭和";
                year = 1;
            } else {
                nengo = "大正";
                year = 15;
            }
        } else if (year >= 1913 & year <= 1925) {
            // 1913年から1925年は大正
            nengo = "大正";
            year = year - 1911;
        } else if (year == 1912) {
            // 1912 年だったら，7/30 以降は大正
            if (month >= 8) {
                nengo = "大正";
                year = 1;
            } else if (month <= 6) {
                nengo = "明治";
                year = 45;
            } else if (day >= 30) {
                nengo = "大正";
                year = 1;
            } else {
                nengo = "明治";
                year = 45;
            }
        } else {
            // 1911年以前は明治
            nengo = "明治";
            year = year - 1867;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(nengo);
        if (year <= 9) {
            sb.append(" ");
        }
        sb.append(Integer.toString(year));
        sb.append("年");
        if (month <= 9) {
            sb.append(" ");
        }
        sb.append(Integer.toString(month));
        sb.append("月");
        if (day <= 9) {
            sb.append(" ");
        }
        sb.append(Integer.toString(day));
        sb.append("日");
        return sb.toString();
    }
//新宿ヒロクリニック 処方箋印刷$    
}
