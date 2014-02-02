/*
 * BaseClinicModule.java
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
 * 基礎的診療情報クラス
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class BaseClinicModule extends InfoModel {
   
    // アレルギー
    Allergy[] allergy;

    // 血液型
    BloodType bloodtype;

    // 感染症
    Infection[] infection;

    /**
     * デフォルトコンストラクタ
     */
    public BaseClinicModule() {
    }

    // allergy
    public Allergy[] getAllergy() {
        return allergy;
    }
    
    public void setAllergy(Allergy[] value) {
        allergy = value;
    }
    
    public void addAllergy(Allergy value) {
        if (allergy == null) {
            allergy = new Allergy[1];
            allergy[0] = value;
            return;
        }
        int len = allergy.length;
        Allergy[] dest = new Allergy[len + 1];
        System.arraycopy(allergy, 0, dest, 0, len);
        allergy = dest;
        allergy[len] = value;
    }

    // bloodtype
    public BloodType getBloodType() {
        return bloodtype;
    }
    
    public void setBloodType(BloodType value) {
        bloodtype = value;
    }

    // infection
    public Infection[] getInfection() {
        return infection;
    }  
    
    public void setInfection(Infection[] value) {
        infection = value;
    }
    
    public void addInfection(Infection value) {
        if (infection == null) {
            infection = new Infection[1];
            infection[0] = value;
            return;
        }
        int len = infection.length;
        Infection[] dest = new Infection[len + 1];
        System.arraycopy(infection, 0, dest, 0, len);
        infection = dest;
        infection[len] = value;
    }    

    public boolean isValidModel() {

        if ( allergy != null ) {
            return allergy[0].isValidModel();
        }
        
        if ( (bloodtype != null) && (bloodtype.isValidModel())) {
            return true;
        }
        
        if ( infection != null ) {
            return infection[0].isValidModel();
        }
        
        return false;
    }
}