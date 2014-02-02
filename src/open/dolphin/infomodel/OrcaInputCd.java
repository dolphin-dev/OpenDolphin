/*
 * OrcaInputCd.java
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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * ORCA の tbl_inputcd エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
public class OrcaInputCd extends InfoModel {
    
    private String hospId;
    
    private String cdsyu;
    
    private String inputCd;
    
    private String sryKbn;
    
    private String sryCd;
    
    private int dspSeq;
    
    private String dspName;
    
    private String termId;
    
    private String opId;
    
    private String creYmd;
    
    private String upYmd;
    
    private String upHms;
    
    private ArrayList<OrcaInputSet> inputSet;
    
    
    /** 
     * Creates a new instance of OrcaInputCd 
     */
    public OrcaInputCd() {
    }

    public String getHospId() {
        return hospId;
    }

    public void setHospId(String hospId) {
        this.hospId = hospId;
    }

    public String getCdsyu() {
        return cdsyu;
    }

    public void setCdsyu(String cdsyu) {
        this.cdsyu = cdsyu;
    }

    public String getInputCd() {
        return inputCd;
    }

    public void setInputCd(String inputCd) {
        this.inputCd = inputCd;
    }

    public String getSryKbn() {
        return sryKbn;
    }

    public void setSryKbn(String sryKbn) {
        this.sryKbn = sryKbn;
    }

    public String getSryCd() {
        return sryCd;
    }

    public void setSryCd(String sryCd) {
        this.sryCd = sryCd;
    }

    public int getDspSeq() {
        return dspSeq;
    }

    public void setDspSeq(int dspSeq) {
        this.dspSeq = dspSeq;
    }

    public String getDspName() {
        return dspName;
    }

    public void setDspName(String dspName) {
        this.dspName = dspName;
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
    
    public ArrayList getInputSet() {
        return inputSet;
    }
    
    public void setInputSet(ArrayList list) {
        inputSet = list;
    }
    
    public void addInputSet(OrcaInputSet set) {
        if (inputSet == null) {
            inputSet = new ArrayList<OrcaInputSet>();
        }
        inputSet.add(set);
    }
    
    public ModuleInfoBean getStampInfo() {
        
        ModuleInfoBean ret = new ModuleInfoBean();
        ret.setStampName(getDspName());
        ret.setStampRole(ROLE_ORCA_SET);
        ret.setEntity(ENTITY_MED_ORDER);
        ret.setStampId(getInputCd());
        return ret;
    }
    
    public BundleMed getBundleMed() {
        
        BundleMed ret = new BundleMed();
        
        for (OrcaInputSet set : inputSet) {
            
            ret.addClaimItem(set.getClaimItem());
        }
        
        return ret;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
