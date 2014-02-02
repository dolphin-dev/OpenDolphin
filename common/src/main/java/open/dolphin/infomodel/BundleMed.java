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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        BundleMed ret = new BundleMed();
        ret.setAdmin(this.getAdmin());
        ret.setAdminCode(this.getAdminCode());
        ret.setAdminCodeSystem(this.getAdminCodeSystem());
        ret.setAdminMemo(this.getAdminMemo());
        ret.setBundleNumber(this.getBundleNumber());
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setClassName(this.getClassName());
        ret.setInsurance(this.getInsurance());
        ret.setMemo(this.getMemo());
        ClaimItem[] items = this.getClaimItem();
        if (items!=null) {
            for (ClaimItem item : items) {
                ret.addClaimItem((ClaimItem)item.clone());
            }
        }
        ret.setOrderName(this.getOrderName());
        return ret;
    }
    
    //---------------------------------------------
    // 用法用量でまとめる事が可能か
    //---------------------------------------------
    public boolean canMerge(BundleMed other) {
        
        if (other==null) {
            return false;
        }
        
        // 内用のみ
        ClaimItem[] items = this.getClaimItem();
        boolean hasNaiyo = (items!=null && items.length>0);
        if (hasNaiyo) {
            for (ClaimItem item : items) {
                if (item.getYkzKbn()==null || (!item.getYkzKbn().equals(ClaimConst.YKZ_KBN_NAIYO))) {
                    hasNaiyo = false;
                    break;
                }
            }
        }
        
        if (!hasNaiyo) {
            return false;
        }
        
        // Other 内用のみ
        hasNaiyo = (other.getClaimItem()!=null && other.getClaimItem().length>0);
        items = other.getClaimItem();
        if (hasNaiyo) {
            for (ClaimItem item : items) {
                if (item.getYkzKbn()==null || (!item.getYkzKbn().equals(ClaimConst.YKZ_KBN_NAIYO))) {
                    hasNaiyo = false;
                    break;
                }
            }
        }
        
        if (!hasNaiyo) {
            return false;
        }
        
        // 仮定
        boolean canMerge = true;
        
        canMerge = canMerge && (getAdminCode()!=null && getBundleNumber()!=null);
        canMerge = canMerge && (other!=null && other.getAdminCode()!=null && other.getBundleNumber()!=null);
        if (canMerge) {
            canMerge = (getAdminCode().equals(other.getAdminCode()));
            canMerge = canMerge && (getBundleNumber().equals(other.getBundleNumber()));
        }
        return canMerge;
    }
    
    //--------------------------------------------
    // この処方に他の処方の項目を加える
    //--------------------------------------------
    public void merge(BundleMed other) {
        ClaimItem[] items = other.getClaimItem();
        if (items!=null) {
            for (ClaimItem ci : items) {
                if (ci.getClassCode().equals(String.valueOf(ClaimConst.YAKUZAI))) {
                    addClaimItem(ci);
                }
            }
        }
    }
}