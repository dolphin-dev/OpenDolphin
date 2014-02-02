/*
 * UserProfileDao.java
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
package open.dolphin.dao;

import java.util.*;

import open.dolphin.infomodel.*;
import netscape.ldap.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class UserProfileDao extends LDAPDaoBean {
    /*
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
    */
    
    /** Creates new UserProfileDao */
	public UserProfileDao() {
	}
    
	public UserProfileEntry getFacilityInfo(String dn) {
        
		LDAPConnection ld = null;
        
		String[] attrs = new String[]{"cn", "facilityId", "facilityOid", "postalCode", "postalAddress", "telephoneNumber", "facsimileTelephoneNumber", "description"};        
               
		UserProfileEntry profile = null;
        
		try {
			ld = getConnection();
			LDAPEntry entry = ld.read(dn, attrs);
            
			profile = new UserProfileEntry();
			Enumeration enumVals;
            
			// 施設名
			LDAPAttribute attr = entry.getAttribute("cn");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setCommonName((String)enumVals.nextElement());
				}
			}
            
			// 医療機関コード
			attr = entry.getAttribute("facilityId");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setFacilityId((String)enumVals.nextElement());
				}
			}
            
			// 医療機関 OID
			attr = entry.getAttribute("facilityOid");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setFacilityOid((String)enumVals.nextElement());
				}
			}  
			
			// 郵便番号
			attr = entry.getAttribute("postalCode");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setPostalCode((String)enumVals.nextElement());
				}
			}  
			
			// 住所
			attr = entry.getAttribute("postalAddress");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setPostalAddress((String)enumVals.nextElement());
				}
			}
			
			// 電話
			attr = entry.getAttribute("telephoneNumber");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setTelephoneNumber((String)enumVals.nextElement());
				}
			}
			
			// FAX
			attr = entry.getAttribute("facsimileTelephoneNumber");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setFacsimileTelephoneNumber((String)enumVals.nextElement());
				}
			}			 			  						          
            
			// 備考
			attr = entry.getAttribute("description");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setDescription((String)enumVals.nextElement());
				}
			}
            
		} catch (Exception e) {
			processError(ld, profile, e.toString());
		} 
        
		disconnect(ld);
        
		return  profile;
	}   
	
	public boolean addFacilityInfo(LDAPConnection ld, UserProfileEntry profile) {
        
		boolean ret = false;
        
		LDAPAttributeSet attrs = new LDAPAttributeSet();

		// Object class
		attrs.add(new LDAPAttribute("objectclass", new String[]{"DolphinUser"}));

		// RD
		attrs.add(new LDAPAttribute("uid", profile.getUserId()));
		attrs.add(new LDAPAttribute("sn", profile.getSirName()));  // sn=cn
		attrs.add(new LDAPAttribute("cn", profile.getCommonName()));
        
		if (profile.getFacilityId() != null) {
			attrs.add(new LDAPAttribute("facilityId", profile.getFacilityId()));
		}
				
		if (profile.getFacilityOid() != null) {
			attrs.add(new LDAPAttribute("facilityOid", profile.getFacilityOid()));
		}
		
		if (profile.getPostalCode() != null) {
			attrs.add(new LDAPAttribute("postalCode", profile.getPostalCode()));
		}
		
		if (profile.getPostalAddress() != null) {
			attrs.add(new LDAPAttribute("postalAddress", profile.getPostalAddress()));
		}
		
		if (profile.getTelephoneNumber() != null) {
			attrs.add(new LDAPAttribute("telephoneNumber", profile.getTelephoneNumber()));
		}
		
		if (profile.getFacsimileTelephoneNumber() != null) {
			attrs.add(new LDAPAttribute("facsimileTelephoneNumber", profile.getFacsimileTelephoneNumber()));
		}								
		
		String val = profile.getDescription();
		if (val != null) {
			attrs.add(new LDAPAttribute("description", val));
		}

		// DN
		StringBuffer buf = new StringBuffer();
		buf.append("uid=");
		buf.append(profile.getUserId());
		buf.append(",");
		buf.append("ou=DolphinUsers,o=Dolphin");
        
		try {
			//ld = getConnection();
			LDAPEntry entry = new LDAPEntry(buf.toString(), attrs);
			ld.add(entry);
            
			ret = true;
        
		} catch (Exception e) {
			processError(ld, null, e.toString());
		}
        
		disconnect(ld);
        
		return ret;
        
	}
	
	public void updateFacilityInfo(UserProfileEntry profile) {
		
		LDAPConnection ld = null;

		try {
			ld = getConnection();
			StringBuffer buf = new StringBuffer();
			buf.append("uid=");
			buf.append(profile.getUserId());
			buf.append(",");
			buf.append(getDN("dolphinUsers"));
			String dn = buf.toString();
            
			// 削除
			ld.delete(dn);
			
		} catch (Exception e) {
			//processError(ld, null, e.toString());
		}
		
		// 追加
		addFacilityInfo(ld, profile);
		
		disconnect(ld);
	}
        
	public void fetch(UserProfileEntry profile) {	
        
		LDAPConnection ld = null;
		String dn = null;
		String[] attrs = null;
        
		try {           
			// 1. Organization Info
			dn = "uid=DolphinFacilityInfo,ou=DolphinUsers,o=Dolphin";
			attrs = new String[]{"cn", "facilityId", "facilityOid", "postalCode", "postalAddress", "telephoneNumber", "facsimileTelephoneNumber", "description"};
			
			ld = getConnection();
			LDAPEntry entry = ld.read(dn, attrs);
			Enumeration enumVals = null;
			
			// 施設名
			LDAPAttribute attr = entry.getAttribute("cn");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 // cn to facilityName
					 profile.setFacilityName((String)enumVals.nextElement());
				 }
			 }
    
			 // 医療機関コード
			 attr = entry.getAttribute("facilityId");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setFacilityId((String)enumVals.nextElement());
				 }
			 }
    
			 // 医療機関 OID
			 attr = entry.getAttribute("facilityOid");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setFacilityOid((String)enumVals.nextElement());
				 }
			 }  
	
			 // 郵便番号
			 attr = entry.getAttribute("postalCode");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setPostalCode((String)enumVals.nextElement());
				 }
			 }  
	
			 // 住所
			 attr = entry.getAttribute("postalAddress");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setPostalAddress((String)enumVals.nextElement());
				 }
			 }
	
			 // 電話
			 attr = entry.getAttribute("telephoneNumber");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setTelephoneNumber((String)enumVals.nextElement());
				 }
			 }
	
			 // FAX
			 attr = entry.getAttribute("facsimileTelephoneNumber");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setFacsimileTelephoneNumber((String)enumVals.nextElement());
				 }
			 }			 			  						          
    
			 // 備考
			 attr = entry.getAttribute("description");
			 if (attr != null) {
				 enumVals = attr.getStringValues();
				 if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					 profile.setDescription((String)enumVals.nextElement());
				 }
			 }        	
        	
			// 2. User Info
			// facilityId,facilityOid,description はもしあればオーバーライドされる
			dn = "uid=" + profile.getUserId() +",ou=DolphinUsers,o=Dolphin";
			attrs = new String[]{"uid", "userPassword", "sn", "givenName", "cn", "licenseCode", "facilityId", "facilityOid", "departmentId", "authority", "authorityToKarte", "mail", "description"};
			entry = ld.read(dn, attrs);
            
			attr = entry.getAttribute("uid");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setUserId((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("userPassword");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setPasswd((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("sn");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setSirName((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("givenName");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setGivenName((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("cn");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setCommonName((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("licenseCode");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setLicenseCode((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("facilityId");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setFacilityId((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("facilityOid");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setFacilityOid((String)enumVals.nextElement());
				}
			}            
            
			attr = entry.getAttribute("departmentId");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setDepartmentId((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("authority");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setAuthority((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("authorityToKarte");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setAuthorityToKarte((String)enumVals.nextElement());
				}
			}            
            
			attr = entry.getAttribute("mail");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setMail((String)enumVals.nextElement());
				}
			}
            
			attr = entry.getAttribute("description");
			if (attr != null) {
				enumVals = attr.getStringValues();
				if ( (enumVals != null) && enumVals.hasMoreElements() ) {
					profile.setDescription((String)enumVals.nextElement());
				}
			}
            
		} catch (Exception e) {
			processError(ld, profile, e.toString());
		} 
        
		disconnect(ld);
        
	}   
    
	public ArrayList getUsers() {
        
		LDAPConnection ld = null;   
		UserProfileEntry profile = null;        
		ArrayList results = null;
		String baseDN = getDN("dolphinUsers");
		String filter = "(&(objectClass=DolphinUser)(uid=*))";
		String[] attrs = new String[]{"uid", "userPassword", "sn", "givenName", "cn", "licenseCode", "facilityId", "facilityOid", "departmentId", "authority", "authorityToKarte", "mail", "description"};
        
		try {
			ld = getConnection();
			LDAPSearchResults res = ld.search(baseDN,
											  LDAPConnection.SCOPE_ONE, 
											  filter, 
											  attrs, 
											  false);
                        
			while(res.hasMoreElements()) {
            
				LDAPEntry entry = res.next();
				profile = new UserProfileEntry();
				Enumeration enumVals;

				LDAPAttribute attr = entry.getAttribute("uid");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setUserId((String)enumVals.nextElement());
						if (profile.getUserId().equals("lasmanager") || profile.getUserId().equals("DolphinFacilityInfo")) {
							continue;
						}
					}
				}

				attr = entry.getAttribute("userPassword");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setPasswd((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("sn");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setSirName((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("givenName");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setGivenName((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("cn");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setCommonName((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("licenseCode");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setLicenseCode((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("facilityId");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setFacilityId((String)enumVals.nextElement());
					}
				}
                
				attr = entry.getAttribute("facilityOid");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setFacilityOid((String)enumVals.nextElement());
					}
				}                

				attr = entry.getAttribute("departmentId");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setDepartmentId((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("authority");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setAuthority((String)enumVals.nextElement());
					}
				}
                
				attr = entry.getAttribute("authorityToKarte");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setAuthorityToKarte((String)enumVals.nextElement());
					}
				}                

				attr = entry.getAttribute("mail");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setMail((String)enumVals.nextElement());
					}
				}

				attr = entry.getAttribute("description");
				if (attr != null) {
					enumVals = attr.getStringValues();
					if ( (enumVals != null) && enumVals.hasMoreElements() ) {
						profile.setDescription((String)enumVals.nextElement());
					}
				}

				if (results == null) {
					results = new ArrayList();
				}
                
				results.add(profile);
			}

		} catch (Exception e) {
			processError(ld, results, e.toString());
		}
        
		disconnect(ld);
        
		return results;
	}    
    
	public void deleteUser(String uid) {
        
		LDAPConnection ld = null;

		try {
			ld = getConnection();
			StringBuffer buf = new StringBuffer();
			buf.append("uid=");
			buf.append(uid);
			buf.append(",");
			buf.append(getDN("dolphinUsers"));
			String dn = buf.toString();
            
			ld.delete(dn);
		}
		catch (Exception e) {
			processError(ld, null, e.toString());
		}
		disconnect(ld);
	}
    
	public boolean addUser(UserProfileEntry profile) {
        
		boolean ret = false;
        
		LDAPConnection ld = null;

		LDAPAttributeSet attrs = new LDAPAttributeSet();

		// Object class
		attrs.add(new LDAPAttribute("objectclass", new String[]{"DolphinUser"}));

		// RD
		attrs.add(new LDAPAttribute("uid", profile.getUserId()));
		attrs.add(new LDAPAttribute("userPassword", profile.getPasswd()));
		attrs.add(new LDAPAttribute("sn", profile.getSirName()));
		attrs.add(new LDAPAttribute("givenName", profile.getGivenName()));
		attrs.add(new LDAPAttribute("cn", profile.getCommonName()));
		attrs.add(new LDAPAttribute("licenseCode", profile.getLicenseCode()));
        
		if (profile.getFacilityId() != null) {
			attrs.add(new LDAPAttribute("facilityId", profile.getFacilityId()));
		}
		        
		if (profile.getDepartmentId() != null) {
			attrs.add(new LDAPAttribute("departmentId", profile.getDepartmentId()));        
		}
        
		if (profile.getAuthority() != null) {
			attrs.add(new LDAPAttribute("authority", profile.getAuthority()));
		}
        
		if (profile.getFacilityOid() != null) {
			attrs.add(new LDAPAttribute("facilityOid", profile.getFacilityOid()));
		}
		
		if (profile.getAuthorityToKarte() != null) {
			attrs.add(new LDAPAttribute("authorityToKarte", profile.getAuthorityToKarte()));
		}
		
		String val = profile.getMail();
		if (val != null) {
			attrs.add(new LDAPAttribute("mail", val));
		}

		val = profile.getDescription();
		if (val != null) {
			attrs.add(new LDAPAttribute("description", val));
		}

		// DN
		StringBuffer buf = new StringBuffer();
		buf.append("uid=");
		buf.append(profile.getUserId());
		buf.append(",");
		buf.append("ou=DolphinUsers,o=Dolphin");
        
		try {
			ld = getConnection();
			LDAPEntry entry = new LDAPEntry(buf.toString(), attrs);
			ld.add(entry);
            
			ret = true;
        
		} catch (Exception e) {
			processError(ld, null, e.toString());
		}
        
		disconnect(ld);
        
		return ret;
        
	}
    
	public boolean changePassword(UserProfileEntry profile) {
        
		boolean ret = false;
        
		LDAPConnection ld = null;
        
		LDAPModificationSet mods = new LDAPModificationSet();

		String newPass = profile.getPasswd();
		if (newPass != null && ! newPass.equals("")) {
			mods.add(LDAPModification.REPLACE, new LDAPAttribute("userPassword", newPass));
		}
        
		StringBuffer buf = new StringBuffer();
		buf.append("uid=");
		buf.append(profile.getUserId());
		buf.append(",");
		buf.append("ou=DolphinUsers,o=Dolphin");
        
		String theDN = buf.toString();
        
		try {
			ld = getConnection();
			ld.modify(theDN, mods);
			ret = true;
        
		} catch (Exception e) {
			processError(ld, null, e.toString());
		}
        
		disconnect(ld);
        
		return ret;
        
	}    
}