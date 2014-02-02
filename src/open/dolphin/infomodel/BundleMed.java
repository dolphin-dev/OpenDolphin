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
 *
 * @author  kazm
 */
public class BundleMed extends BundleDolphin {
    
    /** Creates a new instance of BundleMed */
    public BundleMed() {
    }
    
    public String getAdminDisplayString() {
        
        StringBuffer buf = new StringBuffer();
        
        if (admin.startsWith("内服")) {
            buf.append(admin.substring(0,2));
            buf.append(" ");
            buf.append(admin.substring(4));
            
        } else {
           buf.append(admin);
        }
        
        buf.append(" x ");
        buf.append(bundleNumber);
        if (admin.startsWith("内服")) {
            if (admin.charAt(3) == '回') {
                buf.append (" 日分");
            }
        }
        
        return buf.toString();
    } 
    
	public String toString() {
     
		StringBuffer buf = new StringBuffer();
        
		buf.append("RP\n");
        
		ClaimItem[] items = getClaimItem();
		int len = items.length;
		ClaimItem item;
		String number;
        
		for (int i = 0; i < len; i++) {
			item = items[i];
			buf.append("・");
			buf.append(item.getName());
			buf.append("　");
			number = item.getNumber(); 
            
			if (number != null) {
				buf.append(number);
				if (item.getUnit() != null) {
					buf.append(item.getUnit());
				}
			}
			buf.append("\n");
		}
        
		if (admin.startsWith("内服")) {
			buf.append(admin.substring(0,2));
			buf.append(" ");
			buf.append(admin.substring(4));
		} else {
		   buf.append(admin);
		}
		buf.append(" x ");
		buf.append(bundleNumber);
		// FIXME
		if (admin.startsWith("内服")) {
			if (admin.charAt(3) == '回') {
				buf.append (" 日分");
			}
			// 回がない場合がいいケースもある
			//else {
				//buf.append(" 回分");
			//}
		}
		//else {
			//buf.append(" 回分");
		//}
		buf.append("\n");
        
		// Print admMemo
		if (adminMemo != null) {
			buf.append(adminMemo);
			buf.append("\n");
		}
        
		// Print admMemo
		if (memo != null) {
			buf.append(memo);
			buf.append("\n");
		}        
        
		//buf.append("\n");
		return buf.toString();
	}        
}