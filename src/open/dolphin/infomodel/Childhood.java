/*
 * Childhood.java
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


/**
 * 小児期情報クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Childhood extends InfoModel {
   
    // 出生時情報
    BirthInfo birthInfo;
   
    // 予防接種情報
    Vaccination[] vaccinations;
   
    /**
     * デフォルトコンストラクタ
     */
    public Childhood() {
    }
   
    // birthInfo
    public BirthInfo getBirthInfo() {      
        return birthInfo;
    }
   
    public void setBirthInfo(BirthInfo value) {      
        birthInfo = value;
    }
   
    // vaccination
    public Vaccination[] getVaccination() {      
        return vaccinations;
    }
    
    public void setVaccination(Vaccination[] value) {
        vaccinations = value;
    }
    
    public void addVaccination(Vaccination value) {
    	if (vaccinations == null) {
			vaccinations = new Vaccination[1];
			vaccinations[0] = value;
			return;
    	}
		int len = vaccinations.length;
		Vaccination[] dest = new Vaccination[len + 1];
		System.arraycopy(vaccinations, 0, dest, 0, len);
		vaccinations = dest;
		vaccinations[len] = value;
    }
    
    public boolean isValidModel() {
        
        if ( (birthInfo != null) && (birthInfo.isValidModel()) ) {
            return true;
        }
        
        if ( (vaccinations != null) && (vaccinations[0].isValidModel()) ) {
            return true;
        }
        
        return false;
    }
}