/*
 * ClaimItem.java
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
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc. 
 */
public class ClaimItem extends InfoModel {

    private static final String DISPOSE_UNIT = "管";
	
    private String name;
    private String code;
    private String codeSystem;
    private String classCode;
    private String classCodeSystem;
    private String number;
    private String unit;
    private String numberCode;
    private String numberCodeSystem;
    private String memo;
    
    // 薬剤区分 2011-02-10 追加
    private String ykzKbn;

    
    /** Creates new ClaimItem */
    public ClaimItem() {
    }
        
    public String getName() {
        return name;
    }
    
    public void setName(String val) {
        name = val;
    } 
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String val) {
        code = val;
    }  
    
    public String getCodeSystem() {
        return codeSystem;
    }
    
    public void setCodeSystem(String val) {
        codeSystem = val;
    }
    
    public String getClassCode() {
        return classCode;
    }
    
    public void setClassCode(String val) {
        classCode = val;
    } 
    
    public String getClassCodeSystem() {
        return classCodeSystem;
    }
    
    public void setClassCodeSystem(String val) {
        classCodeSystem = val;
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
    
    public String getNumberCode() {
        return numberCode;
    }
    
    public void setNumberCode(String val) {
        numberCode = val;
    } 
    
    public String getNumberCodeSystem() {
        return numberCodeSystem;
    }
    
    public void setNumberCodeSystem(String val) {
        numberCodeSystem = val;
    }     
        
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String val) {
        memo = val;
    }

    public String getYkzKbn() {
        return ykzKbn;
    }

    public void setYkzKbn(String ykzKbn) {
        this.ykzKbn = ykzKbn;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClaimItem ret = new ClaimItem();
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setCode(this.getCode());
        ret.setCodeSystem(this.getCodeSystem());
        ret.setMemo(this.getMemo());
        ret.setName(this.getName());
        ret.setNumber(this.getNumber());
        ret.setNumberCode(this.getNumberCode());
        ret.setNumberCodeSystem(this.getNumberCodeSystem());
        ret.setUnit(this.getUnit());
        ret.setYkzKbn(this.getYkzKbn());
        return ret;
    }

    //---------------------------------
    // 残量廃棄
    //---------------------------------
    public boolean getCanDispose() {

        if (this.getUnit()!=null &&
            this.getUnit().equals(DISPOSE_UNIT) &&
            this.getNumber()!=null) {
            Float qt = Float.parseFloat(this.getNumber());
            return (qt < 1.0) ? true : false;
        }

        return false;
    }
}