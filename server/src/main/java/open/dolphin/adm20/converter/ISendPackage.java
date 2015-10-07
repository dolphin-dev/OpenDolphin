package open.dolphin.adm20.converter;

import java.util.List;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.DocumentModel;

/**
 *
 * @author kazushi Minagawa
 */
public class ISendPackage {
    
    private IChartEvent chartEvent;
    
    private IDocument document;
    
    private IDiagnosisSendWrapper diagnosisSendWrapper;
    
    private List<String> deletedDiagnosis;

    public IChartEvent getChartEvent() {
        return chartEvent;
    }

    public void setChartEvent(IChartEvent chartEventModel) {
        this.chartEvent = chartEventModel;
    }
    
    public IDocument getDocument() {
        return document;
    }

    public void setDocument(IDocument document) {
        this.document = document;
    }

    public IDiagnosisSendWrapper getDiagnosisSendWrapper() {
        return diagnosisSendWrapper;
    }

    public void setDiagnosisSendWrapper(IDiagnosisSendWrapper diagnosisSendWrapper) {
        this.diagnosisSendWrapper = diagnosisSendWrapper;
    }

    public List<String> getDeletedDiagnosis() {
        return deletedDiagnosis;
    }

    public void setDeletedDiagnosis(List<String> deletedDiagnosis) {
        this.deletedDiagnosis = deletedDiagnosis;
    }
    
    public ChartEventModel chartEventModel() {
        if (getChartEvent()!=null) {
            return getChartEvent().toModel();
        }
        return null;
    }

    public DocumentModel documentModel() {
        if (getDocument()!=null) {
            return getDocument().toModel();
        }
        return null;
    }
    
    public DiagnosisSendWrapper diagnosisSendWrapperModel() {
        if (this.getDiagnosisSendWrapper()!=null) {
            return this.getDiagnosisSendWrapper().toModel();
        }
        return null;
    }
    
    public List<String> deletedDiagnsis() {  
        if (getDeletedDiagnosis()!=null && getDeletedDiagnosis().size()>0) {
            return getDeletedDiagnosis();
        }
        return null;
    }
}
