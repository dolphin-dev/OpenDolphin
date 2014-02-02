
package open.dolphin.client;

import java.awt.event.MouseEvent;
import javax.swing.JTable;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.table.ObjectReflectTableModel;

/**
 *
 * @author kazm
 */
public class AddressTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {
        
        ObjectReflectTableModel model = (ObjectReflectTableModel) getModel();
        int row = rowAtPoint(e.getPoint());
        PatientModel pvt = (PatientModel) model.getObject(row);
        return pvt != null ? pvt.contactAddress() : null;
    }
}
