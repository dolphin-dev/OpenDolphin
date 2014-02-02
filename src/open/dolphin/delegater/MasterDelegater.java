/*
 * SqlMasterDao.java
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

import java.util.*;

import javax.naming.NamingException;

import open.dolphin.dto.MasterSearchSpec;
import open.dolphin.ejb.RemoteMasterService;
import open.dolphin.infomodel.AdminComentValue;
import open.dolphin.infomodel.AdminValue;
import open.dolphin.infomodel.RadiologyMethodValue;


/**
 * MasterDelegater
 *
 * @author Kazushi Minagawa
 */
public final class MasterDelegater extends BusinessDelegater {

    /** Creates a new instance of SqlMasterDao */
    public MasterDelegater() {
    }
    
    /**
     * select ... from administration where hiearchyCode1 is not null order by hierarchyCode1
     * valueObject: AdministrationEntry
     */
    public Object[] getAdminClass() {
    	
    	Collection ret = null;

        try {
            MasterSearchSpec spec = new MasterSearchSpec();
            spec.setCode(MasterSearchSpec.ADMIN_CLASS);
            spec.setFrom("0");
            ret = getService().getMaster(spec);

        } catch (Exception e) {
        	e.printStackTrace();
        	processError(e);
        }

        return (ret != null && ret.size() >0) ?  ret.toArray() : null;
    }

    /**
     * select ... from administration where hierarchyCode2 like %h1 order by hierarchyCode2
     * valuObject: AdministrationEntry
     */
    public Vector<AdminValue> getAdministration(String h1) {
    	
    	Vector<AdminValue> collection = new Vector<AdminValue>();
        
        try {
        	MasterSearchSpec spec = new MasterSearchSpec();
            spec.setCode(MasterSearchSpec.ADMINISTRATION);
            spec.setHierarchyCode1(h1 + "%");
            Collection result = getService().getMaster(spec);
            
            for (Iterator iter = result.iterator(); iter.hasNext(); ) {
            	collection.add((AdminValue)iter.next());
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        	processError(e);
        }

        return collection;
    }

    /**
     * select ... from admin_comment
     * valuObject: String
     */
    public Object[] getAdminComment() {

    	ArrayList<String> list = null;
    	
        try {
        	MasterSearchSpec spec = new MasterSearchSpec();
            spec.setCode(MasterSearchSpec.ADMIN_COMENT);
            Collection result = getService().getMaster(spec);
            Iterator iter = result.iterator();
            AdminComentValue value = null;
            list = new ArrayList<String>();
            
            while (iter.hasNext()) {
            	value = (AdminComentValue)iter.next();
                list.add(value.getAdminComent());
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        	processError(e);
        }

        return list != null ? list.toArray() : null;
    }


    /**
     * select ... from radiology_method where hierarchyCode1 is not null order by hierarchyCode1
     * valuObject: RadiologyMethodEntry
     */
    public Object[] getRadiologyMethod() {     						//This was orginal

        Collection collection = null;

        try {
        	MasterSearchSpec spec = new MasterSearchSpec();
            spec.setCode(MasterSearchSpec.RADIOLOGY_METHOD);
            spec.setFrom("0");
            collection  = getService().getMaster(spec);
            
        } catch (Exception e) {
        	e.printStackTrace();
        	processError(e);
        }

        return collection != null ? collection.toArray() : null;
    }

    /**
     * select ... from radiology_method where hierarchyCode2 like h1% order by hierarchyCode2
     * valuObject: RadiologyMethodEntry
     */
    public Vector<RadiologyMethodValue> getRadiologyComments(String h1) {

        Vector<RadiologyMethodValue> collection = null;

        try {
        	MasterSearchSpec spec = new MasterSearchSpec();
            spec.setCode(MasterSearchSpec.RADIOLOGY_COMENT);
            spec.setHierarchyCode1(h1+ "%");
            Collection result = getService().getMaster(spec);
            collection = new Vector<RadiologyMethodValue>();

            for (Iterator iter = result.iterator(); iter.hasNext(); ) {
            	RadiologyMethodValue value = (RadiologyMethodValue)iter.next();
                collection.add(value);
            }

        } catch (Exception e) {
        	e.printStackTrace();
        	processError(e);
        }
        
        return collection;
    }
    
	private RemoteMasterService getService() throws NamingException {
		return (RemoteMasterService) getService("RemoteMasterService");
	}

}