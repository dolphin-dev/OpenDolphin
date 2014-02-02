package open.dolphin.dto;

import java.io.Serializable;


/**
 * LaboSearchSpec
 * 
 * @author Minagawa,Kazushi
 *
 */
public class LaboSearchSpec implements Serializable {
		
	private static final long serialVersionUID = 2201738793947138141L;
	
	private long karteId;
	private String fromDate;
	private String toDate;
	
	/**
	 * @param patientId The patientId to set.
	 */
	public void setKarteId(long patientId) {
		this.karteId = patientId;
	}
	/**
	 * @return Returns the patientId.
	 */
	public long getKarteId() {
		return karteId;
	}
	/**
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * @return Returns the fromDate.
	 */
	public String getFromDate() {
		return fromDate;
	}
	/**
	 * @param toDate The toDate to set.
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	/**
	 * @return Returns the toDate.
	 */
	public String getToDate() {
		return toDate;
	}
}
