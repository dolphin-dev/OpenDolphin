/*
 * ClaimFilm.java
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
 * Claim Film　要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimFilm extends InfoModel {
    
    String filmSize;
    
    String sizeCode;        // IMP Claim005
    
    String sizeCodeTableId;
    
    //String sizeCodeId = "Claim005";
    
    String filmDivision;    // IMP
    
    String filmNumber;      // integer

    /** 
     * Creates new ClaimFilm 
     */
    public ClaimFilm() {
    }
    
    public String getFilmSize() {
        return filmSize;
    }
    
    public void setFilmSize(String val) {
        filmSize = val;
    }
        
    /*public String getSizeCodeId() {
        return sizeCodeId;
    }
    
    public void setSizeCodeId(String val) {
        sizeCodeId = val;
    }*/   
    
    public String getFilmDivision() {
        return filmDivision;
    }
    
    public void setFilmDivision(String val) {
        filmDivision = val;
    }      

    public String getFilmNumber() {
        return filmNumber;
    }
    
    public void setFilmNumber(String val) {
        filmNumber = val;
    }    
    
    public boolean isValidModel() {
        return ( (filmSize != null) && (filmNumber != null) ) ? true : false;
    }

	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}

	public String getSizeCode() {
		return sizeCode;
	}

	public void setSizeCodeTableId(String sizeCodeTableId) {
		this.sizeCodeTableId = sizeCodeTableId;
	}

	public String getSizeCodeTableId() {
		return sizeCodeTableId;
	}
}