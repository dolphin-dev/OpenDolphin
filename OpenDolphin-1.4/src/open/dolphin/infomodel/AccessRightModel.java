/*
 * AccessRight.java
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
 * AccessRightModel
 *
 * @author  Kazushi Minagawa
 */
public class AccessRightModel extends InfoModel {
    
    private static final long serialVersionUID = -90888255738195101L;
    
    private String permission;
    private String startDate;
    private String endDate;
    //private AccessLicenseeModel licensee;
    private String licenseeCode;
    private String licenseeName;
    private String licenseeCodeType;
    
    /** Creates a new instance of AccessRight */
    public AccessRightModel() {
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String val) {
        permission = val;
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
    
//    public AccessLicenseeModel getLicensee() {
//        return licensee;
//    }
//    
//    public void getLicensee(AccessLicenseeModel val) {
//        licensee = val;
//    }
    
    public void setLicenseeCode(String licenseeCode) {
        this.licenseeCode = licenseeCode;
    }
    
    public String getLicenseeCode() {
        return licenseeCode;
    }
    
    public void setLicenseeName(String licenseeName) {
        this.licenseeName = licenseeName;
    }
    
    public String getLicenseeName() {
        return licenseeName;
    }
    
    public void setLicenseeCodeType(String licenseeCodeType) {
        this.licenseeCodeType = licenseeCodeType;
    }
    
    public String getLicenseeCodeType() {
        return licenseeCodeType;
    }
}
