/*
 * MasterItem.java
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
package open.dolphin.order;

/**
 * Class to hold selected master item information.
 *
 * @author  Kazuhi Minagawa, Digital Globe, Inc.
 */
public class MasterItem implements java.io.Serializable {
    
    // Claim subclass code
    public int classCode;           // 0: 手技  1: 材料  2: 薬剤
        
    public String name;             // 名前
    
    public String code;             // コード
    
    public String masterTableId;    // コード体系名   
    
    public String number;           // 数量
    
    public String unit;             // 単位
    
    public String claimDiseaseCode;  // 医事用病名コード
    
    public String claimClassCode;    // 診療行為区分(007)・点数集計先
    
    /** 
     * Creates new MasterItem 
     */
    public MasterItem() {
    }
    
    public String toString() {
        return name;
    }
}