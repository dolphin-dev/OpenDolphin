/*
 * FacilityProfileEntry.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class FacilityProfileEntry {
    
    private String facilityId;
    private String facilityName;
    private String departmentId;
    private String postalCode;
    private String state;
    private String registeredAddress;
    private String telephoneNumber;
    private boolean accessRight;
    
    /** Creates a new instance of FacilityProfileEntry */
    public FacilityProfileEntry() {
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String val) {
        facilityId = val;
    }
    
    public String getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(String val) {
        departmentId = val;
    }    
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String val) {
        facilityName = val;
    } 
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String val) {
        postalCode = val;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String val) {
        state = val;
    } 
    
    public String getRegisteredAddress() {
        return registeredAddress;
    }
    
    public void setRegisteredAddress(String val) {
        registeredAddress = val;
    }
    
    public String getTelephoneNumber() {
        return telephoneNumber;
    }
    
    public void setTelephoneNumber(String val) {
        telephoneNumber = val;
    }  
    
    public boolean getAccessRight() {
        return accessRight;
    }
    
    public void setAccessRight(boolean b) {
        accessRight = b;
    }
}