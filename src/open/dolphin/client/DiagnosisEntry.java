/*
 * DiagnosisEntry.java
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
package open.dolphin.client;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DiagnosisEntry {
    
    private String uid;
    private String diagnosis;
    private String category;
    private String outcome;
    private String firstEncounterDate;
    private String startDate;
    private String endDate;
    private String confirmDate;
    private String firstConfirmDate;
    private boolean modified;
    
    /** Creates a new instance of DiagnosisEntry */
    public DiagnosisEntry() {
    }
    
    public String getUID() {
        return uid;
    }
    
    public void setUID(String val) {
        uid = val;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String val) {
        diagnosis = val;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String val) {
        category = val;
    }   
    
    public String getOutcome() {
        return outcome;
    }
    
    public void setOutcome(String val) {
        outcome = val;
    }  
    
    public String getFirstEncounterDate() {
        return firstEncounterDate;
    }
    
    public void setFirstEncounterDate(String val) {
        firstEncounterDate = val;
    } 
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String val) {
        startDate = val;
    } 
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String val) {
        endDate = val;
    } 
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean val) {
        modified = val;
    }  
    
    public String getFirstConfirmDate() {
        return firstConfirmDate;
    }
    
    public void setFirstConfirmDate(String val) {
        firstConfirmDate = val;
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }    
}