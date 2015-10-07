package open.dolphin.client;

import java.awt.event.MouseEvent;
import javax.swing.JTable;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.table.ListTableModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class DocHistoryTipsTable extends JTable {
    
    @Override
    public String getToolTipText(MouseEvent e) {
        
        ListTableModel<DocInfoModel> model = (ListTableModel<DocInfoModel>)getModel();
        int row = rowAtPoint(e.getPoint());
        DocInfoModel info = model.getObject(row);
        java.util.ResourceBundle clBundle = ClientContext.getClaimBundle();
        return (info!=null && info.getDocType().equals(IInfoModel.DOCTYPE_KARTE) && (
                info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_ROSAI_PREFIX")) ||
                info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_JIBAISEKI_PREFIX")) ||
                info.getHealthInsurance().startsWith(clBundle.getString("INSURANCE_SELF_PREFIX")))) ? info.getHealthInsuranceDesc() : null;
    }
}
