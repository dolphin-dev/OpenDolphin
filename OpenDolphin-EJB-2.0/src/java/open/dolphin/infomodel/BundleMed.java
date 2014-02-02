/*
 * BundleMed.java
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
 * BundleMed
 *
 * @author Minagawa,Kazushi
 */
public class BundleMed extends BundleDolphin {
    
    private static final long serialVersionUID = -3898329425428401649L;
    
    /** Creates a new instance of BundleMed */
    public BundleMed() {
    }
    
    public String getAdminDisplayString() {
        
        //
        // 用法が null の場合あり
        //
        StringBuilder buf = new StringBuilder();
        
        if (admin != null && (!admin.equals(""))) {
        
            if (admin.startsWith("内服")) {
                buf.append(admin.substring(0,2));
                buf.append(" ");
                buf.append(admin.substring(4));

            } else {
                buf.append(admin);
            }
        }
        
        buf.append(" x ");
        buf.append(bundleNumber);
        
        if (admin != null && (!admin.equals(""))) {
            if (admin.startsWith("内服")) {
                if (admin.charAt(3) == '回') {
                    buf.append(" 日分");
                }
            }
        }
        
        return buf.toString();
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("RP").append("\n");
        
        ClaimItem[] items = getClaimItem();
        int len = items.length;
        ClaimItem item;
        String number;
        
        for (int i = 0; i < len; i++) {
            item = items[i];
            sb.append("・").append(item.getName());
            number = item.getNumber();
            if (number != null) {
                sb.append("　").append(number);
                if (item.getUnit() != null) {
                    sb.append(item.getUnit());
                }
            }
            sb.append("\n");
        }
        
        if (admin != null && (!admin.equals(""))) {
            sb.append(admin);
        }
        
        sb.append(" x ").append(bundleNumber).append("\n");
        
        // admMemo
        if (adminMemo != null) {
            sb.append(adminMemo).append("\n");
        }
        
        // Memo
        if (memo != null) {
            sb.append(memo).append("\n");
        }
        
        return sb.toString();
    }
}