/*
 * ImageEntry.java
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

import javax.swing.*;

/**
 *
 * @author  Kazushi Minagawa, Digital globe, Inc.
 */
public class ImageEntry {
    
    private String confirmDate;
    
    private String title;
    
    private String medicalRole;
    
    private String contentType;
    
    private ImageIcon imageIcon;
    
    private String dn;
    
    /** Creates a new instance of ImageEntry */
    public ImageEntry() {
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }
    
    public String getMedicalRole() {
        return medicalRole;
    }
    
    public void setMedicalRole(String val) {
        medicalRole = val;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String val) {
        contentType = val;
    }    
    
    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public void setImageIcon(ImageIcon val) {
        imageIcon = val;
    }   
    
    public String getDN() {
        return dn;
    }
    
    public void setDN(String val) {
        dn = val;
    }    
}
