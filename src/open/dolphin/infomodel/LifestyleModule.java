/*
 * LifestyleModule.java
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
 * 生活習慣モジュールクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class LifestyleModule extends InfoModel {
    
    // タバコ
    String tobacco;

    // アルコール
    String alcohol;

    // 仕事
    String occupation;

    // 他の生活習慣
    String other;

    /**
     * デフォルトコンストラクタ
     */
    public LifestyleModule() {
    }

    // tobacco
    public String getTobacco() {
        return tobacco;
    }
    
    public void setTobacco(String value) {
        tobacco = value;
    }

    // alcohol
    public String getAlcohol() {
        return alcohol;
    }
    
    public void setAlcohol(String value) {
        alcohol = value;
    }  

    // occupation
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String value) {
        occupation = value;
    }

    // other
    public String getOther() {
        return other;
    }
    
    public void setOther(String value) {
        other = value;
    }
    
    // isValid?
    public boolean isValidModel() {

        boolean tobaccoClear = (tobacco == null || tobacco.equals("")) ? true : false;
        boolean alcoholClear = (alcohol == null || alcohol.equals("")) ? true : false;
        boolean occupationClear = (occupation == null || occupation.equals("")) ? true : false;

        return (tobaccoClear || alcoholClear || occupationClear) ? false : true;
    }
}
   