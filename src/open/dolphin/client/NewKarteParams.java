/*
 * NewKarteParams.java
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
package open.dolphin.client;

import open.dolphin.infomodel.DInsuranceInfo;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class NewKarteParams {
    
    private String groupId;
    
    private String department;
    
    private DInsuranceInfo insurance;
    
    /** Creates a new instance of NewKarteParams */
    public NewKarteParams() {
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String val) {
        groupId = val;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String val) {
        department = val;
    }
    
    public DInsuranceInfo getDInsuranceInfo() {
        return insurance;
    }
    
    public void setDInsuranceInfo(DInsuranceInfo val) {
        insurance = val;
    }
}