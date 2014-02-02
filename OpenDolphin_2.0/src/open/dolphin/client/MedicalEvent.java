package open.dolphin.client;

import java.util.*;

import open.dolphin.infomodel.AppointmentModel;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicalEvent {
    
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    private String displayDate;
    private boolean today;
    private boolean outOfMonth;
    private String medicalCode;
    private AppointmentModel appo;
    
    /** Creates a new instance of MedicalEventEntry */
    public MedicalEvent() {
    }
    
    public MedicalEvent(int year, int month, int day, int dayOfWeek) {
        this();    
        this.year = year;
        this.month = month;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        setDisplayDate(); 
    }
    
    public int getYear() {
        return year;
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    
    public boolean isOutOfMonth() {
        return outOfMonth;
    }
    
    public void setOutOfMonth(boolean b) {
        outOfMonth = b;
    }
    
    public boolean isToday() {
        return today;
    }
    
    public void setToday(boolean b) {
        today = b;
    }
    
    public String getMedicalCode() {
        return medicalCode;
    }
    
    public void setMedicalCode(String val) {
        medicalCode = val;
    }
    
    public boolean before(GregorianCalendar gc) {
        GregorianCalendar me = new GregorianCalendar(year, month, day);
        return me.before(gc);
    }
    
    public AppointmentModel getAppointEntry() {
        return appo;
    }
    
    public void setAppointEntry(AppointmentModel val) {
        appo = val;
    }
    
    public String getAppointmentName() {
        return appo == null ? null : appo.getName();
    }
    
    @Override
    public String toString() {
        return String.valueOf(day);
    }
    
    public String getDisplayDate() {
        return displayDate;
    }
    
    private void setDisplayDate() {
        StringBuilder buf = new StringBuilder();
        String val = String.valueOf(year);
        buf.append(val);
        buf.append("-");
        val = String.valueOf(month+1);
        if (val.length() == 1) {
            buf.append("0");
        }
        buf.append(val);
        buf.append("-");
        val = String.valueOf(day);
        if (val.length() == 1) {
            buf.append("0");
        }
        buf.append(val);
        
        displayDate =  buf.toString();
    }    
}