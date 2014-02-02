/*
 * AppointEntry.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.client;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 **/
public final class AppointEntry implements java.io.Serializable, Comparable {
    
    public static final int TT_NONE       = 0;
    public static final int TT_NEW        = 1;
    public static final int TT_HAS        = 2;
    public static final int TT_REPLACE    = 3;
    
    private String dn;
    private int state;
    private String appointName;
    private String memo;
    private String date;
    
    public AppointEntry() {
    }
    
    public String getDN() {
        return dn;
    }
    
    public void setDN(String val) {
        dn = val;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int val) {
        state = val;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String val) {
        date = val;
    }
    
    public String getAppointName() {
        return appointName;
    }
    
    public void setAppointName(String val) {
        appointName = val;
    }
        
    public String getAppointMemo() {
        return memo;
    }
    
    public void setAppointMemo(String val) {
        memo = val;
    }
    
    public int compareTo(Object o) {
        String s1 = this.date;
        String s2 = ((AppointEntry)o).getDate();
        return s1.compareTo(s2);
    }
    
    public String toString() {    
        return date;
    }
}