package open.dolphin.client;

import java.awt.event.MouseEvent;
import javax.swing.JTable;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.table.ListTableModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AddressTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {
        
        ListTableModel<PatientModel> model = (ListTableModel<PatientModel>) getModel();
        int row = rowAtPoint(e.getPoint());
        PatientModel pvt = model.getObject(row);
        return pvt != null ? pvt.contactAddress() : null;
    }
}
