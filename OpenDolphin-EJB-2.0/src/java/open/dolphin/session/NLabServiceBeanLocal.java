/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author kazushi
 */
@Local
public interface NLabServiceBeanLocal {

    public PatientModel create(String fid, NLaboModule module);

    public List<NLaboModule> getLaboTest(String patientId, int firstResult, int maxResult);

    public List<NLaboItem> getLaboTestItem(String patientId, int firstResult, int maxResult, java.lang.String itemCode);

    public java.util.List<open.dolphin.infomodel.PatientLiteModel> getConstrainedPatients(java.lang.String fid, java.util.List<java.lang.String> idList);
    
}
