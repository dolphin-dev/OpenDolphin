/*
 * Created on 2004/11/13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.project;

import java.io.Serializable;

/**
 * DolphinPrincipal
 * 
 * @author Minagawa, Kazushi
 */
public class DolphinPrincipal implements Serializable {
	
	private static final long serialVersionUID = -2401769499519969282L;
	
	private String userId;
	private String facilityId;
	
	/**
	 * ユーザIDを返す。
	 * @return ユーザID
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * ユーザIDを設定する。
	 * @param uid ユーザID
	 */
	public void setUserId(String uid) {
		this.userId = uid;
	}
	
	/**
	 * 施設IDを設定する。
	 * @param facilityId 施設ID
	 */
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	/**
	 * 施設IDを返す。
	 * @return 施設ID
	 */
	public String getFacilityId() {
		return facilityId;
	}

}