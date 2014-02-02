/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.PatientVisitModel;

/**
 *
 * @author kazushi
 */
@Local
public interface PVTServiceBeanLocal {

    public List<PatientVisitModel> getPvt(String fid, String date, int firstResult, String appoDateFrom, String appoDateTo);

    public int addPvt(open.dolphin.infomodel.PatientVisitModel pvt);

    public int removePvt(long id);

    public int updatePvtState(long pk, int state);

    public java.util.List<open.dolphin.infomodel.PatientVisitModel> getPvt(java.lang.String fid, java.lang.String did, java.lang.String unassigned, java.lang.String date, int firstResult, java.lang.String appoDateFrom, java.lang.String appoDateTo);

    public int updateMemo(long pk, java.lang.String memo);
    
}
