package open.dolphin.client;

import java.awt.print.PageFormat;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenuBar;
import open.dolphin.helper.MenuSupport;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * アプリケーションのメインウインドウインターフェイスクラス。
 *
 * @author Minagawa, Kazushi. Digital Globe, Inc.
 */
public interface MainWindow {
    
    public HashMap<String, MainService> getProviders();
    
    public void setProviders(HashMap<String, MainService> providers);
    
    public JMenuBar getMenuBar();
    
    public MenuSupport getMenuSupport();
    
    public void registerActions(ActionMap actions);
    
    public Action getAction(String name);
    
    public void enabledAction(String name, boolean b);
    
    public void openKarte(PatientVisitModel pvt);
    
    public void addNewPatient();
    
    public void block();
    
    public void unblock();
    
    public BlockGlass getGlassPane();
    
    public MainService getPlugin(String name);
    
    public PageFormat getPageFormat();
    
    public void showStampBox();
    
    public void showSchemaBox();
}
