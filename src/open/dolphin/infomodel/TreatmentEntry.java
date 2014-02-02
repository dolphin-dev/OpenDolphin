/*
 * TreatmentEntry.java
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
 * TreatmentEntry
 *
 * @author  aniruddha
 */
public final class TreatmentEntry extends MasterEntry {
    
    private static final long serialVersionUID = 6147020634071271583L;
    
    private String unit;
    private String costFlag;
    private String cost;
    private String inOutFlag;
    private String oldFlag;
    private String claimClassCode;
    private String hospitalClinicFlag;
    private String claimClassCodeInHospital;
    
    
    /** Creates a new instance of TreatmentEntry */
    public TreatmentEntry() {
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String val) {
        unit = val;
    }
    
    public String getCostFlag() {
        return costFlag;
    }
    
    public void setCostFlag(String val) {
        costFlag = val;
    }
    
    public String getCost() {
        return cost;
    }
    
    public void setCost(String val) {
        cost = val;
    }
    
    public String getInOutFlag() {
        return inOutFlag;
    }
    
    public void setInOutFlag(String val) {
        inOutFlag = val;
    }
    
    public String getOldFlag() {
        return oldFlag;
    }
    
    public void setOldFlag(String val) {
        oldFlag = val;
    }
    
    public String getClaimClassCode() {
        return claimClassCode;
    }
    
    public void setClaimClassCode(String val) {
        claimClassCode = val;
    }
    
    public String getHospitalClinicFlag() {
        return hospitalClinicFlag;
    }
    
    public void setHospitalClinicFlag(String val) {
        hospitalClinicFlag = val;
    }
    
    public String getClaimClassCodeInHospital() {
        return claimClassCodeInHospital;
    }
    
    public void setClaimClassCodeInHospital(String val) {
        claimClassCodeInHospital = val;
    }
    
}