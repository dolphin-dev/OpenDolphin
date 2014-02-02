/*
 * ClaimItem.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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


/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc. 
 */
public class ClaimItem extends InfoModel {
    
    private static final long serialVersionUID = 3256217487799388468L;
	
    String name;
    String code;
    String codeSystem;
    String classCode;
    String classCodeSystem;
    String number;
    String unit;
    String numberCode;
    String numberCodeSystem;
    String memo;
    
    //TableValue subclassCode;      // IMP Claim003
    //MasterValue claimItem;
    //ClaimNumber[] numbers;    // *
    //String duration;          // ?
    //String[] locations;       // *
    //ClaimFilm[] films;        // *
    //String event;             // ?
    //String eventStart;        // IMP
    //String eventEnd;          // IMP
    //String memo;              // ?
    
    /** Creates new ClaimItem */
    public ClaimItem() {
    }
        
    public String getName() {
        return name;
    }
    
    public void setName(String val) {
        name = val;
    } 
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String val) {
        code = val;
    }  
    
    public String getCodeSystem() {
        return codeSystem;
    }
    
    public void setCodeSystem(String val) {
        codeSystem = val;
    }
    
    public String getClassCode() {
        return classCode;
    }
    
    public void setClassCode(String val) {
        classCode = val;
    } 
    
    public String getClassCodeSystem() {
        return classCodeSystem;
    }
    
    public void setClassCodeSystem(String val) {
        classCodeSystem = val;
    }
    
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String val) {
        number = val;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String val) {
        unit = val;
    }
    
    public String getNumberCode() {
        return numberCode;
    }
    
    public void setNumberCode(String val) {
        numberCode = val;
    } 
    
    public String getNumberCodeSystem() {
        return numberCodeSystem;
    }
    
    public void setNumberCodeSystem(String val) {
        numberCodeSystem = val;
    }     
        
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String val) {
        memo = val;
    }
    
    
    /*public TableValue getSubclassCode() {
        return subclassCode;
    }
    
    public void setSubclassCode(TableValue val) {
        subclassCode = val;
    }
    
    public MasterValue getClaimItem() {
        return claimItem;
    }
    
    public void setClaimItem(MasterValue val) {
        claimItem = val;
    }
    
    public ClaimNumber[] getNumber() {
        return numbers;
    }
        
    public void setNumber(ClaimNumber[] val) {
        numbers = val;
    }    
    
    public void addNumber(ClaimNumber val) {
        int size = 0;
        if (numbers == null) {
            numbers = new ClaimNumber[1];
        }
        else {
            size = numbers.length;
            ClaimNumber[] dest = new ClaimNumber[size + 1];
            System.arraycopy(numbers, 0, dest, 0, size);
            numbers = dest;
        }
        numbers[size] = val;
    }  
       
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String val) {
        duration = val;
    }  
    
    public String[] getLocation() {
        return locations;
    }
    
    public void setLocation(String[] val) {
        locations = val;
    }
    
    public void addLocation(String val) {
        int size = 0;
        if (locations == null) {
            locations = new String[1];
        }
        else {
            size = locations.length;
            String[] dest = new String[size + 1];
            System.arraycopy(locations, 0, dest, 0, size);
            locations = dest;
        }
        locations[size] = val;
    }
    
    public ClaimFilm[] getFilm() {
        return films;
    }
    
    public void setFilm(ClaimFilm[] val) {
        films = val;
    }
    
    public void addFilm(ClaimFilm val) {
        int size = 0;
        if (films == null) {
            films = new ClaimFilm[1];
        }
        else {
            size = films.length;
            ClaimFilm[] dest = new ClaimFilm[size + 1];
            System.arraycopy(films, 0, dest, 0, size);
            films = dest;
        }
        films[size] = val;
    } 
    
    public String getEvent() {
        return event;
    }
    
    public void setEvent(String val) {
        event = val;
    }
    
    public String getEventStart() {
        return eventStart;
    }
    
    public void setEventStart(String val) {
        eventStart = val;
    }
    
    public String getEventEnd() {
        return eventEnd;
    }
    
    public void setEventEnd(String val) {
        eventEnd = val;
    } 
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String val) {
        memo = val;
    }
    
    public boolean isValidModel() {
        return ( claimItem != null ) ? true : false;
    }*/
}