/*
 * BirthInfo.java
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
 * BirthInfo 要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class BirthInfo extends InfoModel {
   
    // 出生施設名　簡略化
    String facilityName;
   
    // 分娩週
    String deliveryWeeks;
   
    // 分娩法
    String deliveryMethod;
   
    // 体重
    String bodyWeight;
   
    // 体重単位
    String bodyWeightUnit = "kg";
   
    // 体長
    String bodyHeight;
   
    // 体長単位
    String bodyHeightUnit = "cm";
   
    // 胸囲
    String chestCircumference;
   
    // 胸囲単位
    String chestCircumferenceUnit = "cm";
   
    // 頭囲
    String headCircumference;
   
    // 頭囲単位
    String headCircumferenceUnit = "cm";
   
    // メモ
    String memo;
   
    /**
     * デフォルトコンストラクタ
     */
    public BirthInfo() {
    }
  
    // facilityName
    public String getFacilityName() {
        return facilityName;
    }
   
    public void setFacilityName(String value) {
        facilityName = value;
    } 
   
    // deliveryWeeks
    public String getDeliveryWeeks() {
        return deliveryWeeks;
    }
    
    public void setDeliveryWeeks(String value) {
        deliveryWeeks = value;
    }   
   
    // deliveryMethod
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String value) {
        deliveryMethod = value;
    }
   
    // bodyWeight
    public String getBodyWeight() {
        return bodyWeight;
    }
   
    public void setBodyWeight(String value) {
        bodyWeight = value;
    }
   
    // bodyHeight
    public String getBodyHeight() {
        return bodyHeight;
    }
    
    public void setBodyHeight(String value) {
        bodyHeight = value;
    }
   
    // chestCircumference
    public String getChestCircumference() {
        return chestCircumference;
    }
   
    public void setChestCircumference(String value) {
        chestCircumference = value;
    }
   
    // headCircumference
    public String getHeadCircumference() {
        return headCircumference;
    }
   
    public void setHeadCircumference(String value) {
        headCircumference = value;
    }
   
    // memo
    public String getMemo() {
        return memo;
    }
   
    public void setMemo(String value) {
        memo = value;
    }
   
    public boolean isValidModel() {
        
        // At least one item is not null
        if ( (facilityName != null) && (! facilityName.equals("")) ) {
            return true;
        }
        
        if ( (deliveryWeeks != null) && (! deliveryWeeks.equals("")) ) {
            return true;
        }
        
        if ( (deliveryMethod != null) && (! deliveryMethod.equals("")) ) {
            return true;
        }
        
        if ( (bodyWeight != null) && (! bodyWeight.equals("")) ) {
            return true;
        }
        
        if ( (bodyHeight != null) && (! bodyHeight.equals("")) ) {
            return true;
        }
        
        if ( (chestCircumference != null) && (! chestCircumference.equals("")) ) {
            return true;
        }
        
        if ( (headCircumference != null) && (! headCircumference.equals("")) ) {
            return true;
        }
        
        if ( (memo != null) && (! memo.equals("")) ) {
            return true;
        }
        
        return false;
    }
}