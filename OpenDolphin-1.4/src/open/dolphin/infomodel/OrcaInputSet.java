/*
 * OrcaInputSet.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * ORCA の tbl_inputset エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
public class OrcaInputSet extends InfoModel {
    
    private String hospId;
    
    private String setCd;
    
    private String yukostYmd;
    
    private String yukoedYmd;
    
    private int setSeq;
    
    private String inputCd;
    
    private float suryo1;
    
    private float suryo2;
    
    private int kaisu;
    
    private String comment;
    
    private String atai1;
    
    private String atai2;
    
    private String atai3;
    
    private String atai4;
    
    private String termId;
    
    private String opId;
    
    private String creYmd;
    
    private String upYmd;
    
    private String upHms;
    
    
    /** 
     * Creates a new instance of OrcaInputSet 
     */
    public OrcaInputSet() {
    }

    public String getHospId() {
        return hospId;
    }

    public void setHospId(String hospId) {
        this.hospId = hospId;
    }

    public String getSetCd() {
        return setCd;
    }

    public void setSetCd(String setCd) {
        this.setCd = setCd;
    }

    public String getYukostYmd() {
        return yukostYmd;
    }

    public void setYukostYmd(String yukostYmd) {
        this.yukostYmd = yukostYmd;
    }

    public String getYukoedYmd() {
        return yukoedYmd;
    }

    public void setYukoedYmd(String yukoedYmd) {
        this.yukoedYmd = yukoedYmd;
    }

    public int getSetSeq() {
        return setSeq;
    }

    public void setSetSeq(int setSeq) {
        this.setSeq = setSeq;
    }

    public String getInputCd() {
        return inputCd;
    }

    public void setInputCd(String inputCd) {
        this.inputCd = inputCd;
    }

    public float getSuryo1() {
        return suryo1;
    }

    public void setSuryo1(float suryo1) {
        this.suryo1 = suryo1;
    }

    public float getSuryo2() {
        return suryo2;
    }

    public void setSuryo2(float suryo2) {
        this.suryo2 = suryo2;
    }

    public int getKaisu() {
        return kaisu;
    }

    public void setKaisu(int kaisu) {
        this.kaisu = kaisu;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAtai1() {
        return atai1;
    }

    public void setAtai1(String atai1) {
        this.atai1 = atai1;
    }

    public String getAtai2() {
        return atai2;
    }

    public void setAtai2(String atai2) {
        this.atai2 = atai2;
    }

    public String getAtai3() {
        return atai3;
    }

    public void setAtai3(String atai3) {
        this.atai3 = atai3;
    }

    public String getAtai4() {
        return atai4;
    }

    public void setAtai4(String atai4) {
        this.atai4 = atai4;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getCreYmd() {
        return creYmd;
    }

    public void setCreYmd(String creYmd) {
        this.creYmd = creYmd;
    }

    public String getUpYmd() {
        return upYmd;
    }

    public void setUpYmd(String upYmd) {
        this.upYmd = upYmd;
    }

    public String getUpHms() {
        return upHms;
    }

    public void setUpHms(String upHms) {
        this.upHms = upHms;
    }
    
    public ClaimItem getClaimItem() {
        ClaimItem ret = new ClaimItem();
        ret.setCode(getInputCd());
        ret.setNumber(String.valueOf(getSuryo1()));
        return ret;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
