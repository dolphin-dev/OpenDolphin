/*
 * FamilyHistory.java
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
 * 家族歴モジュールクラス
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class FamilyHistoryModel extends InfoModel {

    private static final long serialVersionUID = 195829004269159780L;

	// 続柄
    String relation;
   
    // RegisteredDiagnosisModule の diagnosis のみを使用
    String diagnosis;
   
    // 年齢
    String age;
   
    // メモ
    String memo;
   
    /** Creates new FamilyHistoryItem */
    public FamilyHistoryModel() {
    }
   
    // relation
    public String getRelation() {      
        return relation;
    }
    
    public void setRelation(String value) {
        relation = value;
    }  

    // diagnosis
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String value) {
        diagnosis = value;
    }
   
    // age
    public String getAge() {
        return age;
    }
    
    public void setAge(String value) {      
        age = value;
    }  
   
    // memo
    public String getMemo() {      
        return memo;
    }
    
    public void setMemo(String value) {      
        memo = value;
    }
    
    public boolean isValidModel() {
     
        boolean relationOk = ((relation != null) && (! relation.equals("")))
                                ? true
                                : false;
                                
        boolean diagnosisOk = ((diagnosis != null) && (! diagnosis.equals("")))
                                ? true
                                : false;
        
        return (relationOk && diagnosisOk) ? true : false;
    }
}