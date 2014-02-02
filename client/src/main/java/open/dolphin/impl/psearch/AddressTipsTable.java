package open.dolphin.impl.psearch;

import java.awt.event.MouseEvent;
import javax.swing.JTable;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.table.ListTableSorter;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika, table sorter
 */
public class AddressTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {
        
        // 
        ////ListTableModel<PatientModel> model = (ListTableModel<PatientModel>) getModel();
        //ListTableSorter sorter = (ListTableSorter) getModel();
        //int row = rowAtPoint(e.getPoint());
        ////PatientModel pvt = model.getObject(row);
        //PatientModel pvt = (PatientModel) sorter.getObject(row);
        //return pvt != null ? pvt.contactAddress() : null;
        ListTableSorter sorter = (ListTableSorter)getModel();
        int row = rowAtPoint(e.getPoint());
        Object o = sorter.getObject(row);
        if (o instanceof PatientVisitModel) {
            PatientVisitModel pvt = (PatientVisitModel)o;
            return (pvt!=null && pvt.getPatientModel()!=null) ? pvt.getPatientModel().contactAddress() : null;
        }
        else if (o instanceof PatientModel) {
            PatientModel pvt = (PatientModel)o;
            return (pvt!=null) ? pvt.contactAddress() : null;
        }
        return null;
    }
}
