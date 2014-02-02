/*
 * DiseaseHelper.java
 * Created on 2004/02/21
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.message;

import java.util.List;
import open.dolphin.infomodel.UserLiteModel;

/**
 * StringBuilder
 *
 * @author Kazushi Minagawa
 *
 */
public class DiseaseHelper {
    
    private static final long serialVersionUID = 1199682898434546678L;
    
    private String patientId;
    private String confirmDate;
    private UserLiteModel creator;
    private String groupId;
    private String department;
    private String departmentDesc;
    private List diagnosisModuleItems;
    
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setCreator(UserLiteModel creator) {
        this.creator = creator;
    }
    
    public UserLiteModel getCreator() {
        return creator;
    }
    
    public String getCreatorId() {
        return creator.getUserId();
    }
    
    public String getCreatorName() {
        return creator.getCommonName();
    }
    
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String dept) {
        department = dept;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }
    
    public void setDepartmentDesc(String desc) {
        departmentDesc = desc;
    }

    public String getCreatorLicense() {
        return creator.getLicenseModel().getLicense();
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }

    public List getDiagnosisModuleItems() {
        return diagnosisModuleItems;
    }

    public void setDiagnosisModuleItems(List diagnosisModuleItems) {
        this.diagnosisModuleItems = diagnosisModuleItems;
    }
}
