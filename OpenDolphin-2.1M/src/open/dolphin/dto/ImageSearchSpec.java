package open.dolphin.dto;

import java.awt.Dimension;
import java.util.Date;

/**
 * ImageSearchSpec
 * 
 * @author Minagawa,Kazushi
 *
 */
public class ImageSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 1306931621795428447L;
	
	public static final int ID_SEARCH 		= 0;
	public static final int PATIENT_SEARCH 	= 1;
	
	private int code;
	
	private long karteId;
	
	private long id;
	
	private String patientId;
	
	private String medicalRole;
	
	private Date[] fromDate;
	
	private Date[] toDate;
	
	private Dimension iconSize;
	
	private String status;

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
	 * @param medicalRole The medicalRole to set.
	 */
	public void setMedicalRole(String medicalRole) {
		this.medicalRole = medicalRole;
	}

	/**
	 * @return Returns the medicalRole.
	 */
	public String getMedicalRole() {
		return medicalRole;
	}

	/**
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(Date[] fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return Returns the fromDate.
	 */
	public Date[] getFromDate() {
		return fromDate;
	}

	/**
	 * @param toDate The toDate to set.
	 */
	public void setToDate(Date[] toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return Returns the toDate.
	 */
	public Date[] getToDate() {
		return toDate;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param iconSize The iconSize to set.
	 */
	public void setIconSize(Dimension iconSize) {
		this.iconSize = iconSize;
	}

	/**
	 * @return Returns the iconSize.
	 */
	public Dimension getIconSize() {
		return iconSize;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}

}
