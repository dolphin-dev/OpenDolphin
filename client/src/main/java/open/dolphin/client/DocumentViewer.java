package open.dolphin.client;

import javax.swing.JScrollPane;
import open.dolphin.infomodel.DocInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface DocumentViewer extends ChartDocument {
    
    public void historyPeriodChanged();
    
    public void showDocuments(DocInfoModel[] docs, JScrollPane scroller);

}
