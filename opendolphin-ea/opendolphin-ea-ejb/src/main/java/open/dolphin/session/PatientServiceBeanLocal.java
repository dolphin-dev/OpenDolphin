/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author kazushi
 */
@Local
public interface PatientServiceBeanLocal {

    //public java.util.Collection getPatients(open.dolphin.dto.PatientSearchSpec spec);

    public PatientModel getPatientById(String fid, String patientId);

    public long addPatient(PatientModel patient);

    public int update(PatientModel patient);

    public List<PatientModel> getPatientsByName(String facilityId, String name);

    public List<PatientModel> getPatientsByKana(String facilityId, String name);

    public List<PatientModel> getPatientsByDigit(String fid, String digit);

    public java.util.List<open.dolphin.infomodel.PatientModel> getPatientsByPvtDate(java.lang.String fid, java.lang.String pvtDate);
    
}
