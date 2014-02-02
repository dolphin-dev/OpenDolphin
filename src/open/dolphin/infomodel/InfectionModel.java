/*
 * InfectionItem.java
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
 * InfectionItem 要素クラス
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class InfectionModel extends InfoModel {
    
    private static final long serialVersionUID = 6810737611367863388L;

	// 要因
    String factor;              

    // 検査値
    String examValue;           

    // 同定日
    String identifiedDate;      

    // メモ
    String memo;                

    /**
     * デフォルトコンストラクタ
     */
    public InfectionModel() {
    }

    // factor
    public String getFactor() {
        return factor;
    }
    
    public void setFactor(String value) {
        factor = value;
    }

    // examValue
    public String getExamValue() {
        return examValue;
    }
    
    public void setExamValue(String value) {
        examValue = value;
    }

    // writer.write 
    public String getIdentifiedDate() {
        return identifiedDate;
    }
    
    public void setIdentifiedDate(String value) {
        identifiedDate = value;
    }

    // memo
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String value) {
        memo = value;
    }
    
    public boolean isValidModel() {
        
        return ( (factor == null) || (factor.equals("")) )
                                                         ? false
                                                         : true;
        
    }
}