/*
 * Patient.java
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
package open.dolphin.infomodel;

/**
 *
 * @author  kazm
 */
public class Patient extends OrganizationalPerson {
    
    String localId;
    
    ID[] otherId;
    
    /** Creates a new instance of Patient */
    public Patient() {
    }
    
    public ID[] getOtherIdId() {
        return otherId;
    }
    
    public void setOtherIdId(ID[] val) {
        otherId = val;
    }    
    
    public void addId(ID value) {
        if (otherId == null) {
            otherId = new ID[1];
            otherId[0] = value;
            return;
        }
        int len = otherId.length;
        ID[] dest = new ID[len + 1];
        System.arraycopy(otherId, 0, dest, 0, len);
        otherId = dest;
        otherId[len] = value;
    }

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getLocalId() {
		return localId;
	}
}