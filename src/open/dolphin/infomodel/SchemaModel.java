/*
 * Schema.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

/**
 * SchemaModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
@Entity
@Table(name = "d_image")
public class SchemaModel extends KarteEntryBean {	//implements Comparable {
    
    private static final long serialVersionUID = -2500342431785313368L;
    
    @Embedded
    private ExtRefModel extRef;
    
    @Lob
    @Column(nullable=false)
    private byte[] jpegByte;
    
    @ManyToOne
    @JoinColumn(name="doc_id", nullable=false)
    private DocumentModel document;
    
    // Comaptible props
    @Transient
    private String fileName;
    
    @Transient
    private ImageIcon icon;
    
    @Transient
    private int imageNumber;
    
    
    /** Creates new Schema */
    public SchemaModel() {
    }
    
    public ExtRefModel getExtRef() {
        return extRef;
    }
    
    public void setExtRef(ExtRefModel val) {
        extRef = val;
    }
    
    public DocumentModel getDocument() {
        return document;
    }
    
    public void setDocument(DocumentModel document) {
        this.document = document;
    }
    
    public byte[] getJpegByte() {
        return jpegByte;
    }
    
    public void setJpegByte(byte[] jpegByte) {
        this.jpegByte = jpegByte;
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public void setIcon(ImageIcon val) {
        icon = val;
    }
    
    public int getImageNumber() {
        return imageNumber;
    }
    
    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String val) {
        fileName = val;
    }
    
    public IInfoModel getModel() {
        return (IInfoModel)getExtRef();
    }
    
    public void setModel(IInfoModel val) {
        setExtRef((ExtRefModel)val);
    }
    
    /**
     * ämíËì˙ãyÇ—ÉCÉÅÅ[ÉWî‘çÜÇ≈î‰ärÇ∑ÇÈÅB
     * @param other
     * @return
     */
    public int compareTo(Object other) {
        int result = super.compareTo(other);
        if (result == 0) {
            // primittive Ç»ÇÃÇ≈î‰ärÇÕOK
            int no1 = getImageNumber();
            int no2 = ((SchemaModel) other).getImageNumber();
            result = no1 - no2;
        }
        return result;
    }
}
