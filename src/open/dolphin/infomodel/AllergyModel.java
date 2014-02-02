/*
 * AllergyItem.java
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

/**
 * AllergyModel
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AllergyModel extends InfoModel implements Comparable {
    
    private static final long serialVersionUID = -6327488237646390391L;
    
    private long observationId;
    
    // 要因
    private String factor;
    
    // 反応程度
    private String severity;
    
    // コード体系
    private String severityTableId;
    
    // 同定日
    private String identifiedDate;
    
    // メモ
    private String memo;
    
    public String getFactor() {
        return factor;
    }
    
    public void setFactor(String factor) {
        this.factor = factor;
    }
    
    public String getIdentifiedDate() {
        return identifiedDate;
    }
    
    public void setIdentifiedDate(String identifiedDate) {
        this.identifiedDate = identifiedDate;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getSeverityTableId() {
        return severityTableId;
    }
    
    public void setSeverityTableId(String severityTableId) {
        this.severityTableId = severityTableId;
    }
    
    /**
     * 同定日で比較する。
     * @param other 比較対象オブジェクト
     * @return 比較値
     */
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String val1 = getIdentifiedDate();
            String val2 = ((AllergyModel)other).getIdentifiedDate();
            return val1.compareTo(val2);
        }
        return 1;
    }
    
    public long getObservationId() {
        return observationId;
    }
    
    public void setObservationId(long observationId) {
        this.observationId = observationId;
    }
}