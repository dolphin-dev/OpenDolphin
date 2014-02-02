/*
 * AllergyItem.java
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
 * PhysicalModel
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PhysicalModel extends InfoModel implements Comparable {
    
    private static final long serialVersionUID = 5923780180643179995L;
    
    private long heightId;
    private long weightId;
    
    // 身長
    private String height;
    
    // 体重
    private String weight;
    
    // BMI
    private int bmi;
    
    // 同定日
    private String identifiedDate;
    
    // メモ
    private String memo;
    
    /**
     * デフォルトコンストラクタ
     */
    public PhysicalModel() {
    }
    
    public long getHeightId() {
        return heightId;
    }
    
    public void setHeightId(long heightId) {
        this.heightId = heightId;
    }
    
    public long getWeightId() {
        return weightId;
    }
    
    public void setWeightId(long weightId) {
        this.weightId = weightId;
    }
    
    // factor
    public String getHeight() {
        return height;
    }
    public void setHeight(String value) {
        height = value;
    }
    
    // identifiedDate
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
    
    public void setWeight(String severity) {
        this.weight = severity;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public String getBmi() {
//        if (bmi == null) {
//            bmi = calcBmi();
//        }
//        return bmi;
        return calcBmi();
    }
    
    /**
     * @return Returns the bmi.
     */
    public String calcBmi() {
        if (height != null && weight != null) {
            float fw = new Float(weight).floatValue();
            float fh = new Float(height).floatValue();
            float bmif = (10000f*fw) / (fh*fh);
            String bmiS = String.valueOf(bmif);
            int index = bmiS.indexOf('.');
            int len = bmiS.length();
            if (index >0 && (index + 2 < len)) {
                bmiS = bmiS.substring(0,index+2);
            }
            return bmiS;
        }
        return null;
    }
    
    public String getStandardWeight() {
        if (getHeight() == null) {
            return  null;
        }
        try {
            float h = Float.parseFloat(getHeight());
            h /= 100.0f;
            float stW = 22.0f * (h * h);
            String stWS = String.valueOf(stW);
            int index = stWS.indexOf('.');
            if (index > 0) {
                stWS = stWS.substring(0, index +2);
            }
            return stWS;

        } catch (Exception e) {
        }
        return null;
    }
    
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String val1 = getIdentifiedDate();
            String val2 = ((PhysicalModel)other).getIdentifiedDate();
            return val1.compareTo(val2);
        }
        return 1;
    }
}