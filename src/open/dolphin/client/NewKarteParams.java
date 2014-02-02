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

import open.dolphin.infomodel.PVTHealthInsuranceModel;


/**
 * NewKarteParams
 *
 * @author  Kazushi Minagawa
 */
public class NewKarteParams {
    
    private IChart.NewKarteOption option;
    private IChart.NewKarteMode createMode;
    private String groupId;
    private String department;
    private String departmentCode;
    private Object[] insurances;
    private int initialSelectedInsurance;
    private PVTHealthInsuranceModel insurance;
    private boolean openFrame;
    
    
    /** Creates a new instance of NewKarteParams */
    public NewKarteParams(IChart.NewKarteOption option) {
        this.option = option;
    }
    
    public IChart.NewKarteOption getOption() {
        return option;
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
    
    public String getDepartmentCode() {
        return departmentCode;
    }
    
    public void setDepartmentCode(String departmentCode) {
        departmentCode = departmentCode;
    }
    
    public Object[] getInsurances() {
        return insurances;
    }
    
    public void setInsurances(Object[] ins) {
        insurances = ins;
    }
    
    public PVTHealthInsuranceModel getPVTHealthInsurance() {
        return insurance;
    }
    
    public void setPVTHealthInsurance(PVTHealthInsuranceModel val) {
        insurance = val;
    }
    
    public void setOpenFrame(boolean openFrame) {
        this.openFrame = openFrame;
    }
    
    public boolean isOpenFrame() {
        return openFrame;
    }
    
    public IChart.NewKarteMode getCreateMode() {
        return createMode;
    }
    
    public void setCreateMode(IChart.NewKarteMode createMode) {
        this.createMode = createMode;
    }
    
    public int getInitialSelectedInsurance() {
        return initialSelectedInsurance;
    }
    
    public void setInitialSelectedInsurance(int index) {
        initialSelectedInsurance = index;
    }
}