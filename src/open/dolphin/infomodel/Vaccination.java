/*
 * Vaccination.java
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
 * VaccinationItem 要素クラス
 *
 * @author  Kazushi Minagawa, Digital Globe,Inc.
 */
public class Vaccination extends InfoModel {
   
    // 接種ワクチン名
    String vaccine;

    // 実施状態
    String injected;

    // 接種年齢
    String age;

    // 実施時メモ
    String memo;

    /**
     * デフォルトコンストラクタ
     */
    public Vaccination() {
    }

    // vaccine
    public String getVaccine() {
        return vaccine;
    }
    
    public void setVaccine(String value) {
        vaccine = value;
    }

    // injected
    public String getInjected() {
        return injected;
    }
    
    public void setInjected(String value) {
        injected = value;
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
     
        boolean vaccineOk = ((vaccine != null) && (! vaccine.equals(""))) ? true : false;
        boolean injectedOk = ((injected != null) && (! injected.equals(""))) ? true : false;
        
        return (vaccineOk && injectedOk) ? true : false;
    }
}