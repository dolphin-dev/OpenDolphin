package open.dolphin.infomodel;

import java.io.Serializable;

/**
 * LaboImportSummary
 * 
 * @author Minagawa,Kazushi
 */
public class LaboImportSummary implements Serializable {
	
	private static final long serialVersionUID = 8730078673332969884L;
	
	private String patientId;

	private PatientModel patient;
	
	private String setName;
	
	private String specimenName;
	
	private String sampleTime;
	
	private String reportTime;
	
	private String reportStatus;
	
	private String laboratoryCenter;
	
	private String result;
	

	public PatientModel getPatient() {
		return patient;
	}

	public void setPatient(PatientModel patient) {
		this.patient = patient;
	}

	public String getPatientBirthday() {
		return this.getPatient().getBirthday();
	}

	public String getPatientGender() {
		return this.getPatient().getGenderDesc();
	}

	public String getPatientName() {
		return this.getPatient().getFullName();
	}


	public String getSpecimenName() {
		return specimenName;
	}

	public void setSpecimenName(String specimenName) {
		this.specimenName = specimenName;
	}

	public String getLaboratoryCenter() {
		return laboratoryCenter;
	}

	public void setLaboratoryCenter(String laboratoryCenter) {
		this.laboratoryCenter = laboratoryCenter;
	}

	public String getPatientId() {
		return patientId;
	}
	
	public void setPatientId(String id) {
		patientId = id;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getReportTime() {
		int index = reportTime.indexOf('T');
		return index > 0 ? reportTime.substring(0, index) : reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public String getSampleTime() {
		int index = sampleTime.indexOf('T');
		return index > 0 ? sampleTime.substring(0, index) : sampleTime;
	}

	public void setSampleTime(String sampleTime) {
		this.sampleTime = sampleTime;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
