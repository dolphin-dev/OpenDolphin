/*
 * Project.java
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
package open.dolphin.project;

import open.dolphin.infomodel.*;
import open.dolphin.util.Enviroment;

/**
 * プロジェクト情報管理クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ProjectStub extends Enviroment implements java.io.Serializable {
    
	//public class ProjectStub extends Enviroment implements java.io.Serializable {
    
		private int mode = AbstractSettingPanel.TT_SET;
    
		private boolean valid;
            
		/** LDAP から取得したユーザ情報*/
		private UserProfileEntry userProfile;
    
		/** CreatorInfo */
		private Creator creatorInfo;
    
		/** ログイン情報 */
		private String authenticationDN;
		private transient String passwd;
        
        
		public boolean isValid() {
			return valid;
		}
    
		public void setValid(boolean b) {
			valid = b;
		}
        
		public void accept(SettingVisitor v) {
			v.visit(this);
		}
        
		public int getMode() {
			return mode;
		}
    
		public void setMode(int val) {
			mode = val;
		}
        
		public String getName() {
			return getString("name");
		}
    
		public void setName(String val) {
			putString("name",val);
		}
        
		public UserProfileEntry getUserProfileEntry() {
			return userProfile;
		}
    
		public void setUserProfileEntry(UserProfileEntry val) {
			userProfile = val;
		}
    
		public Creator getCreatorInfo() {
			return creatorInfo;
		}
    
		public void setCreatorInfo(Creator val) {
			creatorInfo = val;
		}
    
		public String getAuthenticationDN() {
			return authenticationDN;
		}
    
		public void setAuthenticationDN(String dn) {
			authenticationDN = dn;
		}
    
		public String getUserId() {
			return getString("userId");
		}
    
		public void setUserId(String val) {
			putString("userId", val);
		}
    
		public String getPasswd() {
			return passwd;
		}
    
		public void setPasswd(String val) {
			passwd = val;
		}
    
		public String getHostAddress() {
			return getString("hostAddress");
		}
    
		public void setHostAddress(String val) {
			putString("hostAddress", val);
		}
    
		public int getHostPort() {
			return getInt("hostPort", 389);
		}
    
		public void setHostPort(int val) {
			putInt("hostPort", val);
		}
    
		// HOT
		public boolean getSendClaim() {
			return getBoolean("sendClaim", false);
		}
    
		public void setSendClaim(boolean b) {
			putBoolean("sendClaim", b);
		}
    
		public boolean getSendDiagnosis() {
			return getBoolean("sendDiagnosis", false);
		}
    
		public void setSendDiagnosis(boolean b) {
			putBoolean("sendDiagnosis", b);
		}    
    
		public String getClaimHostName() {
			return getString("claimHostName");
		}
    
		public void setClaimHostName(String b) {
			putString("claimHostName", b);
		}
    
		public String getClaimEncoding() {
			return getString("claimEncoding");
		}
    
		public void setClaimEncoding(String val) {
			putString("claimEncoding", val);
		}
		// HOT
    
		public String getClaimAddress() {
			return getString("claimAddress");
		}
    
		public void setClaimAddress(String val) {
			putString("claimAddress", val);
		}
    
		public int getClaimPort() {
			return getInt("claimPort", 5001);
		}
    
		public void setClaimPort(int val) {
			putInt("claimPort", val);
		}
    
		// HOT
		public boolean getSendMML() {
			return getBoolean("sendMML", false);
		}
    
		public void setSendMML(boolean b) {
			putBoolean("sendMML", b);
		}
    
		public String getMMLVersion() {
			return getString("mmlVersion");
		}
    
		public void setMMLVersion(String b) {
			putString("mmlVersion", b);
		}
    
		public String getMMLEncoding() {
			return getString("mmlEncoding");
		}
    
		public void setMMLEncoding(String val) {
			putString("mmlEncoding", val);
		}
    
		public boolean getMIMEEncoding() {
			return getBoolean("mimeEncoding", false);
		}
    
		public void setMIMEEncoding(boolean val) {
			putBoolean("mimeEncoding", val);
		}
        
		public String getUploaderIPAddress() {
			return getString("uploaderIPAddress");
		}
    
		public void setUploaderIPAddress(String val) {
			putString("uploaderIPAddress" ,val);
		}
    
		public String getUploadShareDirectory() {
			return getString("uploadShareDirectory");
		}
    
		public void setUploadShareDirectory(String val) {
			putString("uploadShareDirectory", val);
		}
    
		// end HOT

		public boolean getUseProxy() {
			return getBoolean("useProxy", false);
		}
    
		public void setUseProxy(boolean b) {
			putBoolean("useProxy", b);
		}
    
		public String getProxyHost() {
			return getString("proxyHost");
		}
    
		public void setProxyHost(String val) {
			putString("proxyHost", val);
		}
    
		public int getProxyPort() {
			return getInt("proxyPort", 8080);
		}
    
		public void setProxyPort(int val) {
			putInt("proxyPort", val);
		}
    
		public long getLastModify() {
			return getLong("lastModify", 0L);
		}
    
		public void setLastModify(long val) {
			putLong("lastModify", val);
		}
}