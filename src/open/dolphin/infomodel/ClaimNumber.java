/*
 * ClaimNumber.java
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
 * Claim Number 要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimNumber extends InfoModel {
    
    String number;          // decimal
    
    String numberCode;      // REQ Claim004
    
    String numberCodeTableId;
    
    //String numberCodeId = "Claim004";
    
    String unit;            // IMP

    /**
     * Creates new ClaimNumber 
     */
    public ClaimNumber() {
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
    
    public boolean isValidModel() {
        return ( (number != null) && (getNumberCode() != null) ) ? true : false;
    }

	public void setNumberCode(String numberCode) {
		this.numberCode = numberCode;
	}

	public String getNumberCode() {
		return numberCode;
	}

	public void setNumberCodeTableId(String numberCodeTableId) {
		this.numberCodeTableId = numberCodeTableId;
	}

	public String getNumberCodeTableId() {
		return numberCodeTableId;
	}
}