/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import java.util.List;
import javax.ejb.Local;
import open.dolphin.infomodel.AppointmentModel;

/**
 *
 * @author kazushi
 */
@Local
public interface AppoServiceBeanLocal {

    public List<List> getAppointmentList(long karteId, java.util.List fromDate, java.util.List toDate);

    public int putAppointments(List<AppointmentModel> list);
    
}
