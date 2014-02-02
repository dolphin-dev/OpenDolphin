/*
 * OrganizationalPerson.java
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
public class OrganizationalPerson extends Person {
    
    String id;
    Organization organization;
    OrganizationalUnit organizationalUnit;
    Address address;
    Phone[] phone;
    String[] emailAddress;
    
    /** Creates a new instance of OrganizationalPerson */
    public OrganizationalPerson() {
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String val) {
        id = val;
    }
    
    public Organization getOrganization() {
        return organization;
    }
    
    public void setOrganization(Organization val) {
        organization = val;
    }
    
    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }
    
    public void setOrganizationalUnit(OrganizationalUnit val) {
        organizationalUnit = val;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address val) {
        address = val;
    }
    
    public Phone[] getPhone() {
        return phone;
    }
    
    public void setPhone(Phone[] val) {
        phone = val;
    }
    
    public void addPhone(Phone value) {
        if (phone == null) {
            phone = new Phone[1];
            phone[0] = value;
            return;
        }
        int len = phone.length;
        Phone[] dest = new Phone[len + 1];
        System.arraycopy(phone, 0, dest, 0, len);
        phone = dest;
        phone[len] = value;
    }
    
    public String[] getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String[] val) {
        emailAddress = val;
    }   
    
    public void addEmailAddress(String value) {
        if (emailAddress == null) {
            emailAddress = new String[1];
            emailAddress[0] = value;
            return;
        }
        int len = emailAddress.length;
        String[] dest = new String[len + 1];
        System.arraycopy(emailAddress, 0, dest, 0, len);
        emailAddress = dest;
        emailAddress[len] = value;
    }    
}