/*
 * Person.java
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
 * Person ÉÇÉfÉãÅB
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class Person extends InfoModel {
    
    private String commonName;
    private String firstName;
	private String givenName;
	private String middleName;
    private String kanjiName;
	private String kanaName;
	private String romanName;
	private String gender;
	private String genderCodeSystem;
	private String birthday;
	private String age;
	private String maritalStatus;
	private String maritalStatusCodeSystem;
	private String nationality;
	private String nationalityCodeSystem;
	private String postalCode;
	private String homeAddress;
	private String homePhone;
	
	/** Creates a new instance of Person */
	public Person() {
	}
    
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	private void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	private String getGivenName() {
		return givenName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getMiddleName() {
		return middleName;
	}
    
    public String getName() {
        return kanjiName;
    }
    
    public void setName(String val) {
        kanjiName = val;
    }
    
    public String getKanaName() {
        return kanaName;
    }
    
    public void setKanaName(String val) {
        kanaName = val;
    }
    
    public String getRomanName() {
        return romanName;
    }
    
    public void setRomanName(String val) {
        romanName = val;
    }  
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String val) {
        gender = val;
    }

	public void setGenderCodeSystem(String genderCodeSystem) {
		this.genderCodeSystem = genderCodeSystem;
	}

	public String getGenderCodeSystem() {
		return genderCodeSystem;
	}      
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String val) {
		birthday = val;
    }
    
    public String getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(String val) {
        maritalStatus = val;
    }

	public void setMaritalStatusCodeSystem(String maritalStatusCodeSystem) {
		this.maritalStatusCodeSystem = maritalStatusCodeSystem;
	}

	public String getMaritalStatusCodeSystem() {
		return maritalStatusCodeSystem;
	} 
    
    public String getNationality() {
    	return nationality;    
    }
    
	public void setNationality(String val) {
		nationality = val;    
	}

	public void setNationalityCodeSystem(String nationalityCodeSystem) {
		this.nationalityCodeSystem = nationalityCodeSystem;
	}

	public String getNationalityCodeSystem() {
		return nationalityCodeSystem;
	}

	public void setHomePostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getHomePostalCode() {
		return postalCode;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return age;
	}   
}
