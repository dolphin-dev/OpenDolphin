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
public class ExtRefModel extends InfoModel implements java.io.Serializable {
    
    // MIME ContentType
    @Column(nullable=false)
    private String contentType;
    
    // Medical Role
    @Column(nullable=false)
    private String medicalRole;
    
    // Medical Role コード体系
    @Transient
    private String medicalRoleTableId;
    
    // タイトル
    @Column(nullable=false)
    private String title;
    
    // href 
    @Column(nullable=false)
    private String href;

    // S3
    private String bucket;

    // S3
    private String sop;

    // S3
    private String url;
    
    // S3
    private String facilityId;
    
    //-----------------------------------
    // Unitea
    //-----------------------------------
    private String imageTime;
    private String bodyPart;
    private String shutterNum;
    private String seqNum;
    private String extension;
    //-----------------------------------
    
    /** デフォルトコンストラクタ */
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

    public String getSop() {
        return sop;
    }

    public void setSop(String sop) {
        this.sop = sop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ExtRefModel ret = new ExtRefModel();
        ret.setBucket(this.getBucket());
        ret.setContentType(this.getContentType());
        ret.setHref(this.getHref());
        ret.setMedicalRole(this.getMedicalRole());
        ret.setMedicalRoleTableId(this.getMedicalRoleTableId());
        ret.setSop(this.getSop());
        ret.setTitle(this.getTitle());
        ret.setUrl(this.getUrl());
        return ret;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getImageTime() {
        return imageTime;
    }

    public void setImageTime(String imageTime) {
        this.imageTime = imageTime;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }
    public String getShutterNum() {
        return shutterNum;
    }

    public void setShutterNum(String shutterNum) {
        this.shutterNum = shutterNum;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}