/*
 * ClientInfo.java
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
public class ClientInfo extends InfoModel {
    
    Name[] personName;
    Address[] address;
    Phone[] phone;

    /** Creates new Class */
    public ClientInfo() {
    }

    public Name[] getPersonName() {
        return personName;
    }
    
    public void setPersonName(Name[] val) {
        personName = val;
    }    
    
    public void addPersonName(Name val) {
        int len = 0;
        if (personName == null) {
            personName = new Name[1];
        }
        else {
            len = personName.length;
            Name[] dest = new Name[len + 1];
            System.arraycopy(personName, 0, dest, 0, len);
            personName = dest;
        }
        personName[len] = val;
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