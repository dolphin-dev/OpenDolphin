/*
 * Created on 2004/02/03
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


import mirrorI.dolphin.server.PVTHealthInsurance;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class DInsuranceInfo extends InfoModel {
	
	boolean selected;
    
	PVTHealthInsurance pvtHealthInsurance;

	/** Creates new InsuranceClass */
	public DInsuranceInfo() {
	}
    
	public InsuranceClass getInsuranceClass() {
		InsuranceClass ic = new InsuranceClass();
		if (pvtHealthInsurance.getInsuranceClass() != null) {
			ic.setInsuranceClass(pvtHealthInsurance.getInsuranceClass());
			ic.setClassCode(pvtHealthInsurance.getInsuranceClassCode());
			ic.setTableId(pvtHealthInsurance.getInsuranceClassCodeTableId());
		} else {
			ic.setInsuranceClass("Ž©”ï");
			ic.setClassCode("Z1");
			ic.setTableId("MML0031");
		}
		return ic;
	}
    
	public boolean isSelected() {
		return selected;
	}
    
	public void setSelected(boolean b) {
		selected = b;
	}
    
	public String getUid() {
		return pvtHealthInsurance.getModuleUid();
	}
        
	public PVTHealthInsurance getPVTHealthInsurance() {
		return pvtHealthInsurance;
	}
    
	public void setPVTHealthInsurance(PVTHealthInsurance val) {
		pvtHealthInsurance = val;
	}
        
	public String toString() {
        
		if (pvtHealthInsurance != null) {
			return pvtHealthInsurance.toString();
		}
		return null;
	}	

}
