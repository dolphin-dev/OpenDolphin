/*
 * ClaimInfo.java
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
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class ClaimInfo extends InfoModel {
	
	private String uid;
	
	private String status = "perform";
	
	private String admitFlag = "false";
	
	private String department;
	
	private String departmentId;
	
	private String departmentIdSystem ="MML0028";
	
	private String insuranceClass;
	
	private String insuranceClassCode;
	
	private String insuranceClassCodeSystem ="MML0031";
	
	private String insuranceUid;

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setAdmitFlag(String admitFlag) {
		this.admitFlag = admitFlag;
	}

	public String getAdmitFlag() {
		return admitFlag;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentIdSystem(String departmentIdSystem) {
		this.departmentIdSystem = departmentIdSystem;
	}

	public String getDepartmentIdSystem() {
		return departmentIdSystem;
	}

	public void setInsuranceClass(String insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public String getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClassCode(String insuranceClassCode) {
		this.insuranceClassCode = insuranceClassCode;
	}

	public String getInsuranceClassCode() {
		return insuranceClassCode;
	}

	public void setInsuranceClassCodeSystem(String insuranceClassCodeSystem) {
		this.insuranceClassCodeSystem = insuranceClassCodeSystem;
	}

	public String getInsuranceClassCodeSystem() {
		return insuranceClassCodeSystem;
	}

	public void setInsuranceUid(String insuranceUid) {
		this.insuranceUid = insuranceUid;
	}

	public String getInsuranceUid() {
		return insuranceUid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}
}
