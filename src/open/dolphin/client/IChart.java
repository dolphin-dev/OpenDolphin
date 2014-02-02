package open.dolphin.client;

import javax.swing.JFrame;

import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;

public interface IChart extends IMainWindowPlugin {
    
    public enum NewKarteOption {BROWSER_NEW, BROWSER_COPY_NEW, BROWSER_MODIFY, EDITOR_NEW, EDITOR_COPY_NEW, EDITOR_MODIFY};
    
    public enum NewKarteMode {EMPTY_NEW, APPLY_RP, ALL_COPY};
    
    public KarteBean getKarte();
    
    public void setKarte(KarteBean karte);
    
    public PatientModel getPatient();
    
    public PatientVisitModel getPatientVisit();
    
    public void setPatientVisit(PatientVisitModel model);
    
//    public void setClaimSent(boolean b);
//    
//    public boolean isClaimSent();
    
    public int getChartState();
    
    public void setChartState(int state);
    
    public boolean isReadOnly();
    
    public void setReadOnly(boolean b);
    
    public void close();
    
    public JFrame getFrame();
    
    public IStatusPanel getStatusPanel();
    
    public void setStatusPanel(IStatusPanel statusPanel);
    
    public ChartMediator getChartMediator();
    
    public DocumentHistory getDocumentHistory();
    
    public void showDocument(int index);
    
    public boolean isDirty();
    
}
