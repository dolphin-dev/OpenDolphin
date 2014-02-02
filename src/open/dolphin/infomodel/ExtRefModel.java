/*
 * ExtRef.java
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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * 外部参照要素クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
@Embeddable
public class ExtRefModel extends InfoModel {
    
    private static final long serialVersionUID = -3408876454565957708L;
    
    @Column(nullable=false)
    private String contentType;
    
    @Column(nullable=false)
    private String medicalRole;
    
    @Transient
    private String medicalRoleTableId;
    
    @Column(nullable=false)
    private String title;
    
    @Column(nullable=false)
    private String href;
    
    /**
     * デフォルトコンストラクタ
     */
    public ExtRefModel() {
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String value) {
        contentType = value;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String value) {
        title = value;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String value) {
        href = value;
    }
    
    public void setMedicalRole(String medicalRole) {
        this.medicalRole = medicalRole;
    }
    
    public String getMedicalRole() {
        return medicalRole;
    }
    
    public void setMedicalRoleTableId(String medicalRoleTableId) {
        this.medicalRoleTableId = medicalRoleTableId;
    }
    
    public String getMedicalRoleTableId() {
        return medicalRoleTableId;
    }
}