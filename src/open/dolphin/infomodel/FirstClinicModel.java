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
public class FirstClinicModel extends InfoModel {
   
    private static final long serialVersionUID = -5844350884747510997L;

	// 家族歴
    FamilyHistoryModel[] familyHistory;
   
    // 小児期情報
    ChildhoodModel childhood;
   
    // 既往歴　（FreeNote）
    String pastHistory;
   
    // 主訴
    String chiefComplaints;
   
    // 現病歴
    String presentIllnessNotes;
   
    /** Creates new FirstClinicModule */
    public FirstClinicModel() {
    }
   
    // familyHistory
    public FamilyHistoryModel[] getFamilyHistory() {
        return familyHistory;
    }
    
    public void setFamilyHistory(FamilyHistoryModel[] value) {
        familyHistory = value;
    }
    
	public void addFamilyHistory(FamilyHistoryModel value) {
		if (familyHistory == null) {
			familyHistory = new FamilyHistoryModel[1];
			familyHistory[0] = value;
			return;
		}
		int len = familyHistory.length;
		FamilyHistoryModel[] dest = new FamilyHistoryModel[len + 1];
		System.arraycopy(familyHistory, 0, dest, 0, len);
		familyHistory = dest;
		familyHistory[len] = value;
	}    
   
    // childhood
    public ChildhoodModel getChildhood() {
        return childhood;
    }
    
    public void setChildhood(ChildhoodModel value) {
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