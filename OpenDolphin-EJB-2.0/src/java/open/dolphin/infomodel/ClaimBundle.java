/*
 * ClaimBundle.java
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
 * ClaimBundle 要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimBundle extends InfoModel {
	
    String className;           // 診療行為名
    String classCode;           // 診療行為コード
    String classCodeSystem;     // コード体系
    String admin;               // 用法
    String adminCode;           // 用法コード
    String adminCodeSystem;     // 用法コード体系
    String adminMemo;           // 用法メモ
    String bundleNumber;        // バンドル数
    ClaimItem[] claimItem;      // バンドル構成品目
    String memo;                // メモ
    private String insurance;           // 保険種別
    
    /** Creates new ClaimBundle*/
    public ClaimBundle() {        
    }
        
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String val) {
        className = val;
    } 
    
    public String getClassCode() {
        return classCode;
    }
    
    public void setClassCode(String val) {
        classCode = val;
    }
    
    public String getClassCodeSystem() {
        return classCodeSystem;
    }
    
    public void setClassCodeSystem(String val) {
        classCodeSystem = val;
    }    
    
    public String getAdmin() {
        return admin;
    }
    
    public void setAdmin(String val) {
        admin = val;
    }
    
    public String getAdminCode() {
        return adminCode;
    }
    
    public void setAdminCode(String val) {
        adminCode = val;
    } 
    
    public String getAdminCodeSystem() {
        return adminCodeSystem;
    }
    
    public void setAdminCodeSystem(String val) {
        adminCodeSystem = val;
    } 
    
    public String getAdminMemo() {
        return adminMemo;
    }
    
    public void setAdminMemo(String val) {
        adminMemo = val;
    }  
    
    public String getBundleNumber() {
        return bundleNumber;
    }
    
    public void setBundleNumber(String val) {
        bundleNumber = val;
    } 
    
    public ClaimItem[] getClaimItem() {
        return claimItem;
    }
    
    public void setClaimItem(ClaimItem[] val) {
        claimItem = val;
    }
    
    public void addClaimItem(ClaimItem val) {
        if (claimItem == null) {
            claimItem = new ClaimItem[1];
            claimItem[0] = val;
            return;
        }
        int len = claimItem.length;
        ClaimItem[] dest = new ClaimItem[len + 1];
        System.arraycopy(claimItem, 0, dest, 0, len);
        claimItem = dest;
        claimItem[len] = val;
    }  
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String val) {
        memo = val;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClaimBundle ret = new ClaimBundle();
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
        return ret;
    }
}