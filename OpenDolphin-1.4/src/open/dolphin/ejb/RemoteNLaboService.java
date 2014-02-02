/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.ejb;

import open.dolphin.infomodel.PatientModel;


/**
 *
 * @author kazushi
 */
public interface RemoteNLaboService {

    public PatientModel create(open.dolphin.infomodel.NLaboModule module);

    public java.util.List<open.dolphin.infomodel.NLaboModule> getLaboTest(java.lang.String patientId, int firstResult, int maxResult);

    public java.util.List<open.dolphin.infomodel.NLaboItem> getLaboTestItem(java.lang.String patientId, int firstResult, int maxResult, java.lang.String itemCode);
    
}
