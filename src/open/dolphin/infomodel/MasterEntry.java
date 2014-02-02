/*
 * MasterEntry.java
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
package open.dolphin.infomodel;

import java.util.*;
import java.text.*;

/**
 *
 * @author  kazm
 */
public class MasterEntry extends InfoModel implements java.lang.Comparable {
    
    protected static String refDate;
    static {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        refDate = f.format(gc.getTime()).toString();
    }

    protected String code;

    protected String name;
    
    protected String kana;
    
    protected String startDate;
    
    protected String endDate;

    protected String disUseDate;

    protected String freqFlag;
    
    protected int dirty;

    /** Creates a new instance of DeseaseEntry */
    public MasterEntry() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String val) {
        code = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String val) {
        name = val;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String val) {
        kana = val;
    }
    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String val) {
        startDate = val;
    } 
    
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String val) {
        endDate = val;
    }     

    public String getDisUseDate() {
        return disUseDate;
    }

    public void setDisUseDate(String val) {
        disUseDate = val;
    }

    public String getFreqFlag() {
        return freqFlag;
    }

    public void setFreqFlag(String val) {
        freqFlag = val;
    }
    
    public void freqChanged() {
        dirty++;
        dirty = dirty % 2;
    }
    
    public boolean isDirty() {
        return dirty != 0 ? true : false;
    }
    
    public void resetDirty() {
        dirty = 0;
    }
    
    public int compareTo(Object obj) {
        
        MasterEntry other = (MasterEntry)obj;
        
        int myUse = getUseState(startDate, endDate);
        int otherUse = getUseState(other.getStartDate(), other.getEndDate());
        
        int ret = 0;
        
        switch (myUse) {
            
            case 0:
                if (otherUse == 0) {
                    ret = code.compareTo(other.getCode());
                } else if (otherUse == 1) {
                    ret = 1;
                } else if (otherUse == 2) {
                    ret = -1;
                }
                break;
            
            case 1:
                if (otherUse == 1) {
                    ret = code.compareTo(other.getCode());
                } else {
                    ret = -1;
                }
                break;
                
                
            case 2:
                if (otherUse == 0) {
                    ret = 1;
                } else if (otherUse == 1) {
                    ret = 1;
                } else if (otherUse == 2) {
                    ret = code.compareTo(other.getCode());;
                }
                break;
        }
        
        return ret;
    }
    
    public boolean isInUse() {
        
        return getUseState(startDate, endDate) == 1 ? true : false;
        
    }
    
    protected int getUseState(String startDate, String endDate) {
    
        // 有効期限前
        if (startDate != null && refDate.compareTo(startDate) < 0) {
            return 0;
            
        // 有効期限後    
        } else if (endDate != null && refDate.compareTo(endDate) > 0) {
            return 2;
        }
        
        // 有効期限内
        return 1;
    }
   
    
    public String toString() {
        return name;
    }
}