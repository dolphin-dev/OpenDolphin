package open.dolphin.dto;

import java.util.Date;

/**
 * DocumentSearchSpec
 * 
 * 
 * @author Minagawa,Kazushi
 */
public class DocumentSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 8297575731862377052L;
	
	public static final int DOCTYPE_SEARCH = 0;
	public static final int PURPOSE_SEARCH = 1;
	public static final int CREATOR_SEARCH = 2;
	public static final int LICENSE_SEARCH = 3;
	
	private int code;
	
	private long karteId;
		
	private int docId;
	
	private String facilityId;
	
	private String patientId;
	
	private String docType;
	
	private String purpose;
	
	private String creator;
	
	private String license;
	
	private Date fromDate;
	
	private Date toDate;
	
	private String status;
	
	private boolean includeModifid;
	
	private boolean ascending;

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public long getKarteId() {
		return karteId;
	}

	public void setKarteId(long karteId) {
		this.karteId = karteId;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isIncludeModifid() {
		return includeModifid;
	}

	public void setIncludeModifid(boolean includeModifid) {
		this.includeModifid = includeModifid;
	}
}
