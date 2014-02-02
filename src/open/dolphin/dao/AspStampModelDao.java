/*
 * AspStampModelDao.java
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

import java.io.*;
import java.util.Enumeration;
import java.beans.*;

import open.dolphin.infomodel.IInfoModel;

import netscape.ldap.*;


/**
 * スタンプの IInfoModel を XML 形式で永続化する Dao クラス。
 *
 * @author  Kazushi Minagawa Digital Globe, Inc.
 */
public class AspStampModelDao extends LDAPDaoBean {
        
    /** Creates new StampModelDao */
    public AspStampModelDao() {
    }
        
    /**
     * Stamp　の Model(IInfoModel) を保存する
     * @param id  このスタンプの RDN
     * @param model スタンプのモデル
     */
    public boolean save(String id, IInfoModel model) {
        
        boolean result = false;
        LDAPConnection ld = null;
        
        try {
            ld = getConnection();
            LDAPAttributeSet attrs = new LDAPAttributeSet();
            attrs.add(new LDAPAttribute("objectclass", new String[]{"mmlSerializedStamp"}));
            attrs.add(new LDAPAttribute("mmlStampId", id));
            byte[] bytes = getXMLBytes(model);
            attrs.add(new LDAPAttribute("mmlBinary", bytes));

            // DN
            StringBuffer buf = new StringBuffer();
            buf.append("mmlStampId=");
            buf.append(id);
            buf.append(",");
            buf.append(getDN("aspStampRepository"));
            LDAPEntry entry = new LDAPEntry(buf.toString(), attrs);
            
            ld.add(entry);
            result = true;
        }
        //catch (NullPointerException ne) {
            //System.out.println("NullPointerException,maybe LDAPConnection is null");
        //}
        catch (Exception le) {
            processError(ld, null, le.toString());
        }
        
        disconnect(ld);
        
        return result;
    }
    
    /**
     * Stamp Model Bean の XML バイト配列を返す
     * @param model スタンプのモデル
     * @return XMLエンコードされたバイト配列
     */
    protected byte[] getXMLBytes(IInfoModel model)  {
        
        byte[] ret = null;        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bos));            
        e.writeObject(model);
        e.close();
        ret = bos.toByteArray();
        return ret;
    }    
    
    /**
     * スタンプをフェッチする
     */
    public IInfoModel get(String id) {
        
        IInfoModel o = null;
        
        LDAPConnection ld = null;
        
        try {  
            ld = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("mmlStampId=");
            buf.append(id);
            buf.append(",");
            buf.append(getDN("aspStampRepository"));
            String dn = buf.toString();
            String attrName = "mmlBinary";
                  
            byte[] bytes = null;

            LDAPEntry entry = ld.read(dn, new String[] {attrName});
            
            LDAPAttribute attr = entry.getAttribute(attrName);
            Enumeration enumVals = attr.getByteValues();
            if ( enumVals != null && enumVals.hasMoreElements() ) {
                bytes = (byte[])enumVals.nextElement();
            }

            XMLDecoder d = new XMLDecoder(
                                      new BufferedInputStream(
                                          new ByteArrayInputStream(bytes)));
            o = (IInfoModel)d.readObject();
            d.close();
        }
        //catch (NullPointerException ne) {
            //System.out.println("NullPointerException,maybe LDAPConnection is null");
        //}
        catch (Exception le) {
            processError(ld, o,le.toString());
            //System.out.println("LDAPException while reading the stamp model: " + le.toString());
        }  
        
        disconnect(ld);
        
        return o!= null ? (IInfoModel)o : null;        
    } 
    
    /**
     * スタンプを削除する
     * @param id スタンプエントリの RDN
     */
    public void remove(String id) {
       
        LDAPConnection ld = null;

        try {
            ld = getConnection();
            StringBuffer buf = new StringBuffer();
            buf.append("mmlStampId=");
            buf.append(id);
            buf.append(",");
            buf.append(getDN("aspStampRepository"));
            String dn = buf.toString();
            
            ld.delete(dn);
        }
        catch (Exception e) {
            processError(ld, null, e.toString());
        }
        disconnect(ld);
    }    
}