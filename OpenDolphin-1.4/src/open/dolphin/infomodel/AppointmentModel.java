/*
 * AppointEntry.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.infomodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_appo")
public class AppointmentModel extends KarteEntryBean {
    
    private static final long serialVersionUID = 6166365309219504946L;
    
    public static final int TT_NONE = 0;
    
    public static final int TT_NEW = 1;
    
    public static final int TT_HAS = 2;
    
    public static final int TT_REPLACE = 3;
    
    private String patientId;
    
    @Transient
    private int state;
    
    @Column(name="c_name", nullable = false)
    private String name;
    
    private String memo;
    
    @Column(name="c_date", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date date;
    
    public AppointmentModel() {
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int val) {
        state = val;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date val) {
        date = val;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String val) {
        name = val;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String val) {
        memo = val;
    }
    
    /**
     * @param patientId
     *            The patientId to set.
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    /**
     * @return Returns the patientId.
     */
    public String getPatientId() {
        return patientId;
    }
    
    /**
     * ó\ñÒì˙Ç≈î‰ärÇ∑ÇÈÅB
     */
    public int compareTo(Object o) {
        Date s1 = this.date;
        Date s2 = ((AppointmentModel) o).getDate();
        return s1.compareTo(s2);
    }
    
    public String toString() {
        return ModelUtils.getDateAsString(getDate());
    }
}