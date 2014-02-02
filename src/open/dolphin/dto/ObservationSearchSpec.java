package open.dolphin.dto;

import java.util.Date;

/**
 * ObservationSearchSpec
 * 
 * @author Minagawa,kazushi
 *
 */
public class ObservationSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 1297578145028629411L;

	private long karteId;
	
	private String observation;
	
	private String phenomenon;
	
	private Date firstConfirmed;

	public Date getFirstConfirmed() {
		return firstConfirmed;
	}

	public void setFirstConfirmed(Date firstConfirmed) {
		this.firstConfirmed = firstConfirmed;
	}

	public long getKarteId() {
		return karteId;
	}

	public void setKarteId(long karteId) {
		this.karteId = karteId;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}
}
