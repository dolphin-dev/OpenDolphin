/*
 * AspStampTreeDao.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.dao;

import java.util.*;
import netscape.ldap.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AspStampTreeDao extends LDAPDaoBean {
    
    /** Creates new StampTreeDao */
    public AspStampTreeDao() {
    }
   
    /**
     * StampTree の XML データをフェッチする
     * @param creatorId ユーザID
     * @return XML データ
     */
    public String get(String creatorId) {
                        
        byte[] bytes = null;
        
        LDAPConnection ld = null;
        
        try {
            ld = getConnection();
            String dn = getStampTreeDN(creatorId);
            String attrName = "mmlBinary";
            LDAPEntry entry = ld.read(dn, new String[] {attrName});            
            LDAPAttribute attr = entry.getAttribute(attrName);
            if (attr != null) {
                Enumeration enumVals = attr.getByteValues();
                if ( enumVals != null && enumVals.hasMoreElements() ) {
                    bytes = (byte[])enumVals.nextElement();
                }
            }
        }
        catch (LDAPException e) {
            processError(ld, null, "LDAPException while reading the tree:" + e.toString());
        } 
        
        disconnect(ld);
        
        return bytes != null ? new String(bytes) : null;
    }
    
    /**
     * StampTree の XML データを保存する
     * @param creatorId ユーザID
     * @param treeXml 保存する XML データ
     */
    public void save(String creatorId, String treeXml) {
     
        LDAPConnection ld = null;

        String dn = getStampTreeDN(creatorId);
        
        try {
            ld = getConnection();
            byte[] data = treeXml.getBytes();
            LDAPAttribute attr = new LDAPAttribute("mmlBinary", data);
            LDAPModification mod = new LDAPModification(LDAPModification.REPLACE, attr);
            ld.modify(dn, mod);
        }
        catch (Exception e) {
            processError(ld, null, "Exception while saving the StampTreeXml: " + e.toString());
        }
        disconnect(ld);
    } 
    
    /**
     * LDAP に CreatorID 用のスタンプツリーエントリをつくる
     */
    public void addEntry(String creatorId) {
     
        LDAPConnection ld = null;
        
        String dn = getStampTreeDN(creatorId);
        //System.out.println(dn);

        try {
            ld = getConnection();
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            String[] objectClass = new String[]{"mmlStampTree"};
            attrs.add(new LDAPAttribute("objectclass",objectClass));
            attrs.add(new LDAPAttribute("uid",creatorId));
            LDAPEntry entry = new LDAPEntry(dn, attrs);
            
            ld.add(entry);
        }
        catch (Exception e) {
            processError(ld, null, "Exception while adding the StampTree entry: " + e.toString());
        }
        disconnect(ld);
    }        
    
    private String getStampTreeDN(String creatorId) {
        StringBuffer buf = new StringBuffer();
        buf.append("uid=");
        buf.append(creatorId);
        buf.append(",");
        buf.append(getDN("aspStampTree"));
        return buf.toString();
    }    
}