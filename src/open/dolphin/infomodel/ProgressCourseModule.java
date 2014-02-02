/*
 * ProgressCourseModule.java
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
 * ProgressCourseModule クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe,Inc.
 */
public class ProgressCourseModule extends InfoModel {
        
    String freeExpression;
        
    // XHTML format
    String soaFreeText;
    
    // XHTML format
    String pFreeText;
        
    /**
     * デフォルトコンストラクタ
     */
    public ProgressCourseModule() {
    }
    
    public String getFreeExpression() {
        return freeExpression;
    }
    
    public void setFreeExpression(String value) {
        freeExpression = value;
    }
    
    public String getSOAFreeText() {
        return soaFreeText;
    }
    
    public void setSOAFreeText(String val) {
        soaFreeText = val;
    }
        
    public String getPFreeText() {
        return pFreeText;
    }
    
    public void setPFreeText(String freeText) {
        pFreeText = freeText;
    }
}