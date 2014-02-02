/*
 * FirstClinicModule.java
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
 * 初診時特有情報モジュールクラス
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class FirstClinicModule extends InfoModel {
   
    // 家族歴
    FamilyHistory[] familyHistory;
   
    // 小児期情報
    Childhood childhood;
   
    // 既往歴　（FreeNote）
    String pastHistory;
   
    // 主訴
    String chiefComplaints;
   
    // 現病歴
    String presentIllnessNotes;
   
    /** Creates new FirstClinicModule */
    public FirstClinicModule() {
    }
   
    // familyHistory
    public FamilyHistory[] getFamilyHistory() {
        return familyHistory;
    }
    
    public void setFamilyHistory(FamilyHistory[] value) {
        familyHistory = value;
    }
    
	public void addFamilyHistory(FamilyHistory value) {
		if (familyHistory == null) {
			familyHistory = new FamilyHistory[1];
			familyHistory[0] = value;
			return;
		}
		int len = familyHistory.length;
		FamilyHistory[] dest = new FamilyHistory[len + 1];
		System.arraycopy(familyHistory, 0, dest, 0, len);
		familyHistory = dest;
		familyHistory[len] = value;
	}    
   
    // childhood
    public Childhood getChildhood() {
        return childhood;
    }
    
    public void setChildhood(Childhood value) {
        childhood = value;
    }

    // pastHistory
    public String getPastHistory() {
        return pastHistory;
    }
    
    public void setPastHistory(String value) {
        pastHistory = value;
    }   

    // chiefComplaints
    public String getChiefComplaints() {
        return chiefComplaints;
    }
    
    public void setChiefComplaints(String value) {
        chiefComplaints = value;
    }    
   
    // presentIllnessNotes
    public String getPresentIllnessNotes() {
        return presentIllnessNotes;
    }
    
    public void setPresentIllnessNotes(String value) {
        presentIllnessNotes = value;
    }
   
    public boolean isValidMML() {
     
        if ( (familyHistory != null) && (familyHistory[0].isValidModel()) ) {
            return true;
        }
        
        if ( (childhood != null) && (childhood.isValidModel()) ) {
            return true;
        }
        
        if ( (pastHistory != null) && (! pastHistory.equals("")) ) {
            return true;
        }
        
        if ( (chiefComplaints != null) && (! chiefComplaints.equals("")) ) {
            return true;
        }
        
        if ( (presentIllnessNotes != null) && (! presentIllnessNotes.equals("")) ) {
            return true;
        }
        
        return false;
    }
}