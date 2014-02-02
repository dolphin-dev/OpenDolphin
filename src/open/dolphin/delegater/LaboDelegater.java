/*
 * LaboDelegater.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.delegater;

import java.util.Collection;

import javax.naming.NamingException;

import open.dolphin.dto.LaboSearchSpec;
import open.dolphin.ejb.RemoteLaboService;
import open.dolphin.infomodel.LaboModuleValue;
import open.dolphin.infomodel.PatientModel;

/**
 * Labo 関連の Delegater クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LaboDelegater extends BusinessDelegater {
    
    /**
     * LaboModule を保存する。
     * @param laboModuleValue
     * @return LaboImportReply
     */
    public PatientModel putLaboModule(LaboModuleValue value) {
        
        try {
            return getService().putLaboModule(value);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return null;
    }
    
    public Collection getLaboModules(LaboSearchSpec spec) {
        
        Collection c = null;
        
        try {
            c = getService().getLaboModuless(spec);
            
        } catch (Exception e) {
            e.printStackTrace();
            processError(e);
        }
        
        return c;
    }
    
    private RemoteLaboService getService() throws NamingException {
        return (RemoteLaboService) getService("RemoteLaboService");
    }
}
