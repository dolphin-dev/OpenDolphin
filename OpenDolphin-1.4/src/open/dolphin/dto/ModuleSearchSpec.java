package open.dolphin.dto;

import java.util.Date;


/**
 * ModuleSearchSpec
 * 
 * @author Minagawa,Kazushi
 * 
 */
public class ModuleSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 4550131751936543011L;

	public static final int ENTITY_SEARCH 		= 0;
	
	private int code;
	
	private long karteId;
	
	private String patientId;
	
	private String entity;
	
	private Date[] fromDate;
	
	private Date[] toDate;
	
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
	 * @param entity The entity to set.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return Returns the entity.
	 */
	public String getEntity() {
		return entity;
	}
}
