/*
 * AdminInfo.java
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
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class AdminInfo extends Object {
    
    protected static final int TT_CLASSCODE = 0;
    protected static final int TT_ADMIN     = 1;
    protected static final int TT_MEMO      = 2;
    protected int eventType;
    
    public String class1Code;     // 大分類コード
    public String admin;          // 1 + 2;
    public String admin1;         // 大分類名(内服3回、頓用等)
    public String admin2;         // 小分類名(毎食後　等)
    public String adminCode;      // レセ用コード
    public String classCode;      // 診療行為区分（点数集計先）
    public String numberCode;     // 数量コード
    public String adminMemo;      // メモ
    public int showState = 2;

    /** 
     * Creates new SimpleMedItem 
     */
    public AdminInfo() {
    }
    
    public String toString() {
        if (showState == 2) {
            return admin2;
        }
        else {
            return admin;
        }
    }
}