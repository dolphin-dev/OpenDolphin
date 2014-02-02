/*
 * ClaimInformation.java
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
 * ClaimInformation 要素クラス.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimInformation extends InfoModel {

    String status;          	// REQ (受付・実施・予約）
    
    String statusTableId;
    
    String orderTime;           // IMP

    String appointTime;         // IMP

    String registTime;          // IMP

    String performTime;         // IMP

    String admitFlag;           // REQ（入院・外来）
    
    String timeClass;       	// IMP Claim001（時間外区分）
    
    String timeClassTableId;
    
    String insuranceUid;        // IMP
    
    String defaultTableId;      // IMP
    
    Appoint appoint;
    
    MMLOrganization patientDepartment;
    
    MMLOrganization patientWard;
    
    String insuranceClass;
    
    String insuranceClassTableId;
    
    /** 
     * Creates new ClaimInformation 
     */
    public ClaimInformation() {
    }
        
    public String getOrderTime() {
        return orderTime;
    }
    
    public void setOrderTime(String val) {
        orderTime = val;
    }  
    
    public String getAppointTime() {
        return appointTime;
    }
    
    public void setAppointTime(String val) {
        appointTime = val;
    }
    
    public String getRegistTime() {
        return registTime;
    }
    
    public void setRegistTime(String val) {
        registTime = val;
    }        
    
    public String getPerformTime() {
        return performTime;
    }
    
    public void setPerformTime(String val) {
        performTime = val;
    }    

    public String getAdmitFlag() {
        return admitFlag;
    }
    
    public void setAdmitFlag(String val) {
        admitFlag = val;
    }  
        
    public String getInsuranceUid() {
        return insuranceUid;
    }
    
    public void setInsuranceUid(String val) {
        insuranceUid = val;
    } 
    
    public String getDefaultTableId() {
        return defaultTableId;
    }
    
    public void setDefaultTableId(String val) {
        defaultTableId = val;
    }
    
    public Appoint getAppoint() {
        return appoint;
    }
    
    public void setAppoint(Appoint val) {
        appoint = val;
    }
    
    public MMLOrganization getPatientDepartment() {
        return patientDepartment;
    }
    
    public void setPatientDepartment(MMLOrganization val) {
        patientDepartment = val;
    } 
    
    public MMLOrganization getPatientWard() {
        return patientWard;
    }
    
    public void setPatientWard(MMLOrganization val) {
        patientWard = val;
    }

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatusTableId(String statusTableId) {
		this.statusTableId = statusTableId;
	}

	public String getStatusTableId() {
		return statusTableId;
	}

	public void setTimeClass(String timeClass) {
		this.timeClass = timeClass;
	}

	public String getTimeClass() {
		return timeClass;
	}

	public void setTimeClassTableId(String timeClassTableId) {
		this.timeClassTableId = timeClassTableId;
	}

	public String getTimeClassTableId() {
		return timeClassTableId;
	}

	public void setInsuranceClass(String insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public String getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClassTableId(String insuranceClassTableId) {
		this.insuranceClassTableId = insuranceClassTableId;
	}

	public String getInsuranceClassTableId() {
		return insuranceClassTableId;
	} 
}
