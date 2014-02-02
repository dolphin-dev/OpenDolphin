/*
 * MMLDate.java
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
import java.text.*;

/**
 * Utility class to handle MML Date format.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class MMLDate extends Object {
    
    private static final String MML_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String MML_DATE_PATTERN = "yyyy-MM-dd";
    private static final String MML_TIME_PATTERN = "HH:mm:ss";

    /** Creates new MMLDate */
    public MMLDate() {        
    }
    
    public static String getDateTime(String pattern) {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        return f.format(gc.getTime()).toString();
    }
    
    public static String getDateTime() {
        return getDateTime(new GregorianCalendar());
    }
    
    public static String getDate() {
        return getDate(new GregorianCalendar());
    }
    
    public static String getTime() {
        return getTime(new  GregorianCalendar());
    }
    
    public static String getDateTime(GregorianCalendar gc) {
        
        SimpleDateFormat f = new SimpleDateFormat(MML_DATETIME_PATTERN);
        return f.format(gc.getTime()).toString();
    }

    public static String getDate(GregorianCalendar gc) {
        
        SimpleDateFormat f = new SimpleDateFormat(MML_DATE_PATTERN);
        return f.format(gc.getTime()).toString();
    }

    public static String getTime(GregorianCalendar gc) {

        SimpleDateFormat f = new SimpleDateFormat(MML_TIME_PATTERN);
        return f.format(gc.getTime()).toString();
    }
    
    public static String getDayFromToday(int n) {       
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DAY_OF_MONTH, n);
        return getDate(gc);   
    }
    
    public static String getMonthFromToday(int n) {       
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.MONTH, n);
        return getDate(gc);   
    }
        
    public static String getYearFromToday(int n) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.YEAR, n);
        return getDate(gc); 
    }
    
    public static int[] getCalendarYMD(String mmlDate) {
     
        int[] ret = new int[3];
        try {
            StringTokenizer st = new StringTokenizer(mmlDate, "-");
            
            // Year
            String val = st.nextToken();
            ret[0] = Integer.parseInt(val);
            
            // Month
            val = st.nextToken();
            if (val.charAt(0) == '0') {
                val = val.substring(1);
            }
            ret[1] = Integer.parseInt(val) -1;
            
            // day
            val = st.nextToken();
            if (val.charAt(0) == '0') {
                val = val.substring(1);
            }
            ret[2] = Integer.parseInt(val);
        }
        catch (Exception e) {
            ret = null;
        }
        
        return ret;
    }
}