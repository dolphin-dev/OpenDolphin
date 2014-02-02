/*
 * HierarchyEntry.java
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
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class HierarchyEntry extends InfoModel{
	
	private static final long serialVersionUID = -5434694699379636176L;
	private String hierarchyCode1;
	private String hierarchyCode2;
	private String hierarchyCode3;

	/** Creates a new instance of HierarchyEntry */
	public HierarchyEntry() {
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

	public String getHierarchyCode3() {
		return hierarchyCode3;
	}

	public void setHierarchyCode3(String val) {
		hierarchyCode3 = val;
	}	

}
