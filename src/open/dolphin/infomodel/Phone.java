/*
 * Phone.java
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

/**
 *
 * @author  kazm
 */
public class Phone extends InfoModel {
    
    String telEquipType;
    String country;
    String area;
    String city;
    String number;
    String extension;
    String memo;
    
    /** Creates a new instance of Phone */
    public Phone() {
    }
    
    public String getTelEquipType() {
        return telEquipType;
    }
   
    public void setTelEquipType(String value) {
        telEquipType = value;
    }     
    
    public String getCountry() { 
        return country;
    }
   
    public void setCountry(String value) {
        country = value;
    }
    
    public String getArea() {
        return area;
    }
   
    public void setArea(String value) {
        area = value;
    }
   
    public String getCity() {
        return city;
    }
   
    public void setCity(String value) {
        city = value;
    }
    
    public String getNumber() {
        return number;
    }
   
    public void setNumber(String value) {
        number = value;
    }
    
    public String getExtension() {
        return extension;
    }
   
    public void setExtension(String value) {
        extension = value;
    }
    
    public String getMemo() {
        return memo;
    }
   
    public void setMemo(String value) {
        memo = value;
    }
}
