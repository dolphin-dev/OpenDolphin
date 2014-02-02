/*
 * Created on 2004/10/06
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.dto;

import java.util.Date;


/**
 * DiagnosisSearchSpec
 * 
 * @author Minagawa,Kazushi
 */
public class DiagnosisSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 3687480184889000203L;
	
	public static final int PATIENT_SEARCH 		= 0;
	public static final int CODE_SEARCH 			= 1;
	public static final int DIAGNOSIS_SEARCH 		= 2;
	public static final int CREATOR_SEARCH 		= 3;
	
	private int code;
	
	private long karteId;
	
	private String patientId;
	
	private String diagnosisCode;
	
	private String diagnosis;
	
	private String creatorId;
	
	private Date fromDate;
	
	private Date toDate;
	
	private char status;

	/**
	 * @param code The code to set.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}
	

	public long getKarteId() {
		return karteId;
	}

	public void setKarteId(long karteId) {
		this.karteId = karteId;
	}

	/**
	 * @param patientId The patientId to set.
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
	 * @param diagnosisCode The diagnosisCode to set.
	 */
	public void setDiagnosisCode(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}

	/**
	 * @return Returns the diagnosisCode.
	 */
	public String getDiagnosisCode() {
		return diagnosisCode;
	}

	/**
	 * @param diagnosis The diagnosis to set.
	 */
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	/**
	 * @return Returns the diagnosis.
	 */
	public String getDiagnosis() {
		return diagnosis;
	}

	/**
	 * @param creatorId The creatorId to set.
	 */
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return Returns the creatorId.
	 */
	public String getCreatorId() {
		return creatorId;
	}

	/**
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return Returns the fromDate.
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param toDate The toDate to set.
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return Returns the toDate.
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(char status) {
		this.status = status;
	}

	/**
	 * @return Returns the status.
	 */
	public char getStatus() {
		return status;
	}
}
