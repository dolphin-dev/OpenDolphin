/*
 * Created on 2005/02/23
 *
 */
package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * SimpleDate
 *
 * @author Kazushi Minagawa
 *
 */
public class SimpleDate extends InfoModel implements Comparable {
    
    private static final long serialVersionUID = 2137848922359964710L;
    
    private int year;
    
    private int month;
    
    private int day;
    
    private ArrayList timeFrames;
    
    private String eventCode;
    
    public SimpleDate() {
    }
    
    public static SimpleDate mmlDateToSimpleDate(String mmlDate) {
        // mmlDate = YYYY-MM-DDThh:mm:ss
        int year = Integer.parseInt(mmlDate.substring(0, 4));
        int month = Integer.parseInt(mmlDate.substring(5, 7)) - 1; // for
        // Calendar
        int date = Integer.parseInt(mmlDate.substring(8, 10));
        return new SimpleDate(year, month, date);
    }
    
    public static String simpleDateToMmldate(SimpleDate sd) {
        
        StringBuffer buf = new StringBuffer();
        buf.append(sd.getYear());
        buf.append("-");
        int month = sd.getMonth() + 1;
        if (month < 10) {
            buf.append("0");
        }
        buf.append(month);
        buf.append("-");
        int day = sd.getDay();
        if (day < 10) {
            buf.append("0");
        }
        buf.append(day);
        return buf.toString();
    }
    
    public SimpleDate(int year, int month, int day) {
        this();
        setYear(year);
        setMonth(month);
        setDay(day);
    }
    
    public SimpleDate(int[] spec) {
        this();
        setYear(spec[0]);
        setMonth(spec[1]);
        setDay(spec[2]);
    }
    
    public SimpleDate(GregorianCalendar gc) {
        this();
        setYear(gc.get(Calendar.YEAR));
        setMonth(gc.get(Calendar.MONTH));
        setDay(gc.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * @param year
     *            The year to set.
     */
    public void setYear(int year) {
        this.year = year;
    }
    
    /**
     * @return Returns the year.
     */
    public int getYear() {
        return year;
    }
    
    /**
     * @param month
     *            The month to set.
     */
    public void setMonth(int month) {
        this.month = month;
    }
    
    /**
     * @return Returns the month.
     */
    public int getMonth() {
        return month;
    }
    
    /**
     * @param day
     *            The day to set.
     */
    public void setDay(int day) {
        this.day = day;
    }
    
    /**
     * @return Returns the day.
     */
    public int getDay() {
        return day;
    }
    
    public boolean equalDate(int year, int month, int day) {
        return (year == this.year && month == this.month && day == this.day) ? true
                : false;
    }
    
    @Override
    public String toString() {
        return String.valueOf(day);
    }
    
    /**
     * @param markColor
     *            The markColor to set.
     */
    public void setEventCode(String c) {
        this.eventCode = c;
    }
    
    /**
     * @return Returns the markColor.
     */
    public String getEventCode() {
        return eventCode;
    }
    
    @Override
    public int compareTo(Object o) {
        
        if (o != null && o.getClass() == this.getClass()) {
            SimpleDate other = (SimpleDate) o;
            int oYear = other.getYear();
            int oMonth = other.getMonth();
            int oDay = other.getDay();
            
            if (year != oYear) {
                return year < oYear ? -1 : 1;
                
            } else if (month != oMonth) {
                return month < oMonth ? -1 : 1;
                
            } else if (day != oDay) {
                return day < oDay ? -1 : 1;
                
            } else {
                return 0;
            }
        }
        return -1;
    }
    
    public int compareMonthDayTo(Object o) {
        
        if (o != null && o.getClass() == this.getClass()) {
            SimpleDate other = (SimpleDate) o;
            int oMonth = other.getMonth();
            int oDay = other.getDay();
            
            if (month != oMonth) {
                return month < oMonth ? -1 : 1;
                
            } else if (day != oDay) {
                return day < oDay ? -1 : 1;
                
            } else {
                return 0;
            }
        }
        return -1;
    }
    
    public void setTimeFrames(ArrayList timeFrames) {
        this.timeFrames = timeFrames;
    }
    
    public ArrayList getTimeFrames() {
        return timeFrames;
    }
}
