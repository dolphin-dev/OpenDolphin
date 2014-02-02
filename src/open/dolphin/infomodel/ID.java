/*
 * ID.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ID extends InfoModel {
    
    String id;
    String idType;
    String idTypeTableId;
    
    /** Creates a new instance of ID */
    public ID() {
    }
    
    public ID(String id, String idType, String idTypeTableId) {
    	this();
    	this.id = id;
    	this.idType = idType;
    	this.idTypeTableId = idTypeTableId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String val) {
        id = val;
    }

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdTypeTableId(String idTypeTableId) {
		this.idTypeTableId = idTypeTableId;
	}

	public String getIdTypeTableId() {
		return idTypeTableId;
	}
}