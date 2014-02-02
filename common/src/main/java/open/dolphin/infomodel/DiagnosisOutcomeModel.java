/*
 * DiagnosisOutcomeModel.java
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
package open.dolphin.infomodel;

import javax.persistence.Embeddable;

/**
 * Diagnosis のカテゴリーモデル。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Embeddable
public class DiagnosisOutcomeModel extends InfoModel implements java.io.Serializable {
    
    // 転帰（コード値）
    private String outcome;
    
    // 転帰説明
    private String outcomeDesc;
    
    // 転帰体系
    private String outcomeCodeSys;
    
    @Override
    public String toString() {
        return getOutcomeDesc();
    }
    
    /**
     * @param outcome The outcome to set.
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    /**
     * @return Returns the outcome.
     */
    public String getOutcome() {
        return outcome;
    }
    
    /**
     * @param outcomeDesc The outcomeDesc to set.
     */
    public void setOutcomeDesc(String outcomeDesc) {
        this.outcomeDesc = outcomeDesc;
    }
    
    /**
     * @return Returns the outcomeDesc.
     */
    public String getOutcomeDesc() {
        return outcomeDesc;
    }
    
    /**
     * @param outcomeCodeSys The outcomeCodeSys to set.
     */
    public void setOutcomeCodeSys(String outcomeCodeSys) {
        this.outcomeCodeSys = outcomeCodeSys;
    }
    
    /**
     * @return Returns the outcomeCodeSys.
     */
    public String getOutcomeCodeSys() {
        return outcomeCodeSys;
    }
}
