/*
 * BundleDolphin.java
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
 * BundleDolphin
 *
 * @author  Minagawa,Kazushi
 */
public class BundleDolphin extends ClaimBundle {
    
    private static final long serialVersionUID = -8747202550129389855L;
    
    private String orderName;
    
    /** Creates a new instance of BundleDolphin */
    public BundleDolphin() {
    }
    
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }
    
    public String getOrderName() {
        return orderName;
    }
    
    public String toString() {
        
        StringBuilder buf = new StringBuilder();
        
        // Print order name
        buf.append(orderName);
        buf.append("\n");
        ClaimItem[] items = getClaimItem();
        int len = items.length;
        ClaimItem item;
        String number;
        
        for (int i = 0; i < len; i++) {
            item = items[i];
            
            // Print item name
            buf.append("E");
            buf.append(item.getName());
            
            // Print item number
            number = item.getNumber();
            if (number != null) {
                buf.append("@");
                buf.append(number);
                if (item.getUnit() != null) {
                    buf.append(item.getUnit());
                }
            }
            buf.append("\n");
        }
        
        // Print bundleNumber
        if (! bundleNumber.equals("1")) {
            buf.append("X@");
            buf.append(bundleNumber);
            buf.append("\n");
        }
        
        // Print admMemo
        if (adminMemo != null) {
            buf.append(adminMemo);
            buf.append("\n");
        }
        
        // Print bundleMemo
        if (memo != null) {
            buf.append(memo);
            buf.append("\n");
        }
        
        return buf.toString();
    }
    
}