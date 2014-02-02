/*
 * MedicineEntry.java
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
 *
 * @author  aniruddha
 */
public final class MedicineEntry extends MasterEntry {

    private String unit;
    private String costFlag;
    private String cost;
    private String jncd;

    /** Creates a new instance of MedicineEntry */
    public MedicineEntry() {
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
    
    public String getJNCD() {
	return jncd;
    }

    public void setJNCD(String val) {
	jncd = val;
    }    

}
