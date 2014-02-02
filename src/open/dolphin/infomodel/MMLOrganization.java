/*
 * MMLOrganization.java
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
package open.dolphin.infomodel;

/**
 *
 * @author  kazm
 */
public class MMLOrganization extends InfoModel {
    
    Name[] name;
    ID[] id;
    Address[] address;
    Phone[] phone;
    
    /** Creates a new instance of MMLOrganization */
    public MMLOrganization() {
    }
    
    public Name[] getMMLName() {
        return name;
    }
    
    public void setMMLName(Name[] val) {
        name = val;
    }
    
    public ID[] getId() {
        return id;
    }
    
    public void setId(ID[] val) {
        id = val;
    }    
    
    public void addId(ID val) {
        int len = 0;
        if (id == null) {
            id = new ID[1];
        }
        else {
            len = id.length;
            ID[] dest = new ID[len + 1];
            System.arraycopy(id, 0, dest, 0, len);
            id = dest;
        }
        id[len] = val;
    }    
    
    public Address[] getAddress() {
        return address;
    }
    
    public void setAddress(Address[] val) {
        address = val;
    }    
    
    public void addAddress(Address val) {
        int len = 0;
        if (address == null) {
            address = new Address[1];
        }
        else {
            len = address.length;
            Address[] dest = new Address[len + 1];
            System.arraycopy(address, 0, dest, 0, len);
            address = dest;
        }
        address[len] = val;
    }
    
    public Phone[] getPhone() {
        return phone;
    }
    
    public void setPhone(Phone[] val) {
        phone = val;
    }    
    
    public void addPhone(Phone val) {
        int len = 0;
        if (phone == null) {
            phone = new Phone[1];
        }
        else {
            len = phone.length;
            Phone[] dest = new Phone[len + 1];
            System.arraycopy(phone, 0, dest, 0, len);
            phone = dest;
        }
        phone[len] = val;        
    }    
}