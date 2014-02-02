/*
 * AdministrationEntry.java
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
package open.dolphin.infomodel;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class AdministrationEntry extends MasterEntry {
	
	private String hierarchyCode1;
	private String hierarchyCode2;
	private String adminName;
	private String claimClassCode;
	private String numberCode;
	private String displayName;


	/** Creates a new instance of AdministrationEntry */
	public AdministrationEntry() {
	}

	public String getHierarchyCode1() {
		return hierarchyCode1;
	}

	public void setHierarchyCode1(String val) {
		hierarchyCode1 = val;
	}

	public String getHierarchyCode2() {
		return hierarchyCode2;
	}

	public void setHierarchyCode2(String val) {
		hierarchyCode2 = val;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String val) {
		adminName = val;
	}

	public String getClaimClassCode() {
		return claimClassCode;
	}

	public void setClaimClassCode(String val) {
		claimClassCode = val;
	}

	public String getNumberCode() {
		return numberCode;
	}

	public void setNumberCode(String val) {
		numberCode = val;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String val) {
		displayName = val;
	}
    
	public String toString() {     
		return adminName;
	}	

}
