/*
 * ClaimEvent.java
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
package open.dolphin.plugin.event;

/**
 * CLAIM インスタンスを通知するイベント。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimMessageEvent extends java.util.EventObject {
    
    private String patientId;
	private String patientName;
	private String patientSex;
	private String title;
	private String instance;
	private int number;
	private String confirmDate;
    
    /** Creates new ClaimEvent */
    public ClaimMessageEvent(Object source) {
        super(source);
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String val) {
        patientId = val;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String val) {
        patientName = val;
    } 
    
    public String getPatientSex() {
        return patientSex;
    }
    
    public void setPatientSex(String val) {
        patientSex = val;
    }     
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }     
    
    public String getClaimInsutance() {
        return instance;
    }
    
    public void setClaimInstance(String val) {
        instance = val;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int val) {
        number = val;
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }    
}