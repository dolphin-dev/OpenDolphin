/*
 * DocInfo.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
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
 * DocInfo ÉNÉâÉXÅB
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DocInfo extends InfoModel {
	
	public static final char TT_TEMPORARY = 'T';
	public static final char TT_FINAL     = 'F';
	public static final char TT_MODIFIED  = 'M';
    
	private String docId;
	private String docType;
	private String firstConfirmDate;
	private String confirmDate;
	private String series;
	private String seriesNumber;
	private String title;
	private String purpose;
	private ClaimInfo  claimInfo;
	private boolean hasMark;
	private boolean hasImage;
	private boolean hasRp;
	private boolean hasTreatment;
	private boolean hasLaboTest;
	private AccessRight[] accessRight;
    private Creator creator;
    private Version version;
	private ParentId parentId;
	private char status;
    
    /** Creates new DocInfo */
    public DocInfo() {
    } 
    
    public String getDocId() {
        return docId;
    }
    
    public void setDocId(String id) {
        docId = id;
    }
    
    public String getDocType() {
        return docType;
    }
    
    public void setDocType(String value) {
        docType = value;
    }
    
    public String getFirstConfirmDate() {
        return firstConfirmDate;
    }
    
    public void setFirstConfirmDate(String value) {
        firstConfirmDate = value;
    }  
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String value) {
        confirmDate = value;
    }

	public void setSeries(String series) {
		this.series = series;
	}

	public String getSeries() {
		return series;
	}  
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String value) {
        title = value;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String value) {
        purpose = value;
    }     
    
    public AccessRight[] getAccessRight() {
        return accessRight;
    }
    
    public void setAccessRight(AccessRight[] s) {
        accessRight = s;
    }
    
    public void addAccessRight(AccessRight value) {
        if (accessRight == null) {
            accessRight = new AccessRight[1];
            accessRight[0] = value;
            return;
        }
        int len = accessRight.length;
        AccessRight[] dest = new AccessRight[len + 1];
        System.arraycopy(accessRight, 0, dest, 0, len);
        accessRight = dest;
        accessRight[len] = value;
    }   
        
    public ParentId getParentId() {
        return parentId;
    }
    
    public void setParentId(ParentId val) {
        parentId = val;
    }

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public boolean getHasImage() {
		return hasImage;
	}

	public void setHasRp(boolean hasRp) {
		this.hasRp = hasRp;
	}

	public boolean getHasRp() {
		return hasRp;
	}

	public void setHasTreatment(boolean hasTreatment) {
		this.hasTreatment = hasTreatment;
	}

	public boolean getHasTreatment() {
		return hasTreatment;
	}

	public void setHasLaboTest(boolean hasLaboTest) {
		this.hasLaboTest = hasLaboTest;
	}

	public boolean getHasLaboTest() {
		return hasLaboTest;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public Creator getCreator() {
		return creator;
	}

	public void setClaimInfo(ClaimInfo claimInfo) {
		this.claimInfo = claimInfo;
	}

	public ClaimInfo getClaimInfo() {
		return claimInfo;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setHasMark(boolean hasMark) {
		this.hasMark = hasMark;
	}

	public boolean getHasMark() {
		return hasMark;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Version getVersion() {
		return version;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public char getStatus() {
		return status;
	}
}