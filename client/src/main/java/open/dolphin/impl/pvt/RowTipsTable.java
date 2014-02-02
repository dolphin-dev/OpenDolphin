
package open.dolphin.impl.pvt;

import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JTable;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.table.ListTableSorter;
import org.apache.commons.lang.time.DurationFormatUtils;

/**
 *
 * @author Kazushi Minagawa.
 * @author modified by masuda, Masuda Naika
 */
public class RowTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {

        //ListTableModel<PatientVisitModel> model = (ListTableModel<PatientVisitModel>) getModel();
        ListTableSorter sorter = (ListTableSorter) getModel();
        int row = rowAtPoint(e.getPoint());
        PatientVisitModel pvt = (PatientVisitModel) sorter.getObject(row);
//pns^  待ち時間表示 modified by masuda
        //return pvt != null ? pvt.getPatient().getKanaName() : null;
        if (pvt != null) {
            Date pvtDate = ModelUtils.getDateTimeAsObject(pvt.getPvtDate());
            String waitingTime = "";
            if (!pvt.getStateBit(PatientVisitModel.BIT_SAVE_CLAIM) && !pvt.getStateBit(PatientVisitModel.BIT_MODIFY_CLAIM)) {
                Date now = new Date();
                waitingTime = " - 待ち時間 ";
                if (now.after(pvtDate)) {
                    waitingTime = waitingTime + DurationFormatUtils.formatPeriod(pvtDate.getTime(), now.getTime(), "HH:mm");
                } else {
                    waitingTime = "00:00";
                }
            }
            return pvt.getPatientModel().getKanaName() + waitingTime;
        }
        return null;
//pns$
    }
}
