
package open.dolphin.client;

import java.awt.event.MouseEvent;
import javax.swing.JTable;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.table.ObjectReflectTableModel;

/**
 *
 * @author kazm
 */
public class RowTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {
        
        ObjectReflectTableModel model = (ObjectReflectTableModel) getModel();
        int row = rowAtPoint(e.getPoint());
        PatientVisitModel pvt = (PatientVisitModel) model.getObject(row);
        return pvt != null ? pvt.getPatient().getKanaName() : null;
    }
}
