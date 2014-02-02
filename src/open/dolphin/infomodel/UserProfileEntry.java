/*
 * UserProfileEntry.java
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
        uid           => 利用者ID
        userPassword  => パスワード
        sn            => 姓
        givenName     => 名
        cn            => 氏名(sn & ' ' & givenName),
        licenceCode   => 職種(MML0026)
        facilityId    => 医療機関コード(ORCA医療機関コード)
        departmentId  => 診療科(MML0028)
        authority     => LASに対する権限(admin:管理者,user:一般利用者)
        mail          => メールアドレス
        description   => その他情報
        objectClass   => 'DolphinUser'
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class UserProfileEntry {
    
	private String uid;
	private String passwd;
	private String sn;
	private String givenName;
	private String cn;
	private String licenceCode;
	private String facilityId;
	private String departmentId;
	private String authority;
	private String mail;
	private String description;
	private boolean accessRight;
    
	private String departmentName;   // 2004-06-03
	private String facilityName;     // 2004-06-03
	private String facilityOid;      // 2004-06-03
	private String authorityToKarte; // 2004-06-03
	private String postalCode;       // OrganizationalPerson
	private String postalAddress;	 // OrganizationalPerson
	private String telephoneNumber;  // Person
	private String facsimileTelephoneNumber; // OrganizationalPerson
	 
    
	/** Creates a new instance of UserProfileEntry */
	public UserProfileEntry() {
	}
    
	public String getUserId() {
		return uid;
	}
    
	public void setUserId(String val) {
		uid = val;
	}
    
	public String getPasswd() {
		return passwd;
	}
    
	public void setPasswd(String val) {
		passwd = val;
	}    
    
	public String getSirName() {
		return sn;
	}
    
	public void setSirName(String val) {
		sn = val;
	}
    
	public String getGivenName() {
		return givenName;
	}
    
	public void setGivenName(String val) {
		givenName = val;
	} 
    
	public String getCommonName() {
		return cn;
	}
    
	public void setCommonName(String val) {
		cn = val;
	}
    
	public String getLicenseCode() {
		return licenceCode;
	}
    
	public void setLicenseCode(String val) {
		licenceCode = val;
	} 
    
	public String getFacilityId() {
		return facilityId;
	}
    
	public void setFacilityId(String val) {
		facilityId = val;
	}
    
	public String getDepartmentId() {
		return departmentId;
	}
    
	public void setDepartmentId(String val) {
		departmentId = val;
	} 
    
	public String getAuthority() {
		return authority;
	}
    
	public void setAuthority(String val) {
		authority = val;
	}
    
	public String getMail() {
		return mail;
	}
    
	public void setMail(String val) {
		mail = val;
	} 
    
	public String getDescription() {
		return description;
	}
    
	public void setDescription(String val) {
		description = val;
	} 
    
	public boolean getAccessRight() {
		return accessRight;
	}
    
	public void setAccessRight(boolean val) {
		accessRight = val;
	}    
    
	public boolean isAdmin() {
		return authority.toLowerCase().endsWith("admin") ? true : false;
	}
    
	public String toString() {
        
		StringBuffer buf = new StringBuffer();
		buf.append("uid: ");
		buf.append(uid);
		buf.append("\n");
        
		buf.append("password: ");
		buf.append(passwd);
		buf.append("\n");
        
		buf.append("sn: ");
		buf.append(sn);
		buf.append("\n");
        
		buf.append("givenName: ");
		buf.append(givenName);
		buf.append("\n");
        
		buf.append("cn: ");
		buf.append(cn);
		buf.append("\n");
        
		buf.append("licenceCode: ");
		buf.append(licenceCode);
		buf.append("\n");
        
		buf.append("authorityToKarte: ");
		buf.append(authorityToKarte);
		buf.append("\n");       
		
		buf.append("departmentId: ");
		buf.append(departmentId);
		buf.append("\n");		 
        
		buf.append("facilityName: ");
		buf.append(facilityName);
		buf.append("\n");        
        
		buf.append("facilityId: ");
		buf.append(facilityId);
		buf.append("\n");
        
		buf.append("facilityOid: ");
		buf.append(facilityOid);
		buf.append("\n");        
        
		buf.append("postalCode: ");
		buf.append(postalCode);
		buf.append("\n");
		
		buf.append("postalAddress: ");
		buf.append(postalAddress);
		buf.append("\n");
		
		buf.append("telephoneNumber: ");
		buf.append(telephoneNumber);
		buf.append("\n");
		
		buf.append("facsimileTelephoneNumber: ");
		buf.append(facsimileTelephoneNumber);
		buf.append("\n");						
        
		buf.append("authority: ");
		buf.append(authority);
		buf.append("\n");
        
		buf.append("mail: ");
		buf.append(mail);
		buf.append("\n");
        
		buf.append("description: ");
		buf.append(description);
		buf.append("\n");
        
		return buf.toString();
	}

	public void setFacilityOid(String facilityOid) {
		this.facilityOid = facilityOid;
	}

	public String getFacilityOid() {
		return facilityOid;
	}

	public void setAuthorityToKarte(String authorityToKarte) {
		this.authorityToKarte = authorityToKarte;
	}

	public String getAuthorityToKarte() {
		return authorityToKarte;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber) {
		this.facsimileTelephoneNumber = facsimileTelephoneNumber;
	}

	public String getFacsimileTelephoneNumber() {
		return facsimileTelephoneNumber;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentName() {
		return departmentName;
	}
}