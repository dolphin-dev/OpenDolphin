/*
 * Address.java
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
 * èZèäÉÇÉfÉãÅB 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Address extends InfoModel {
    
    private String addressClass;
	private String addressClassTableId;
	private String countryCode;
	private String zipCode;
	private String full;
    
    /** Creates a new instance of Address */
    public Address() {
    }
        
    public String getCountryCode() {
        return countryCode;
    }
   
    public void setCountryCode(String value) {
        countryCode = value;
    }
    
    public String getZipCode() {
        return zipCode;
    }
   
    public void setZipCode(String value) {
        zipCode = value;
    }
   
    public String getAddress() {
        return full;
    }
   
    public void setAddress(String value) {
        full = value;
    }

	public void setAddressClass(String addressClass) {
		this.addressClass = addressClass;
	}

	public String getAddressClass() {
		return addressClass;
	}

	public void setAddressClassTableId(String addressClassTableId) {
		this.addressClassTableId = addressClassTableId;
	}

	public String getAddressClassTableId() {
		return addressClassTableId;
	}
}