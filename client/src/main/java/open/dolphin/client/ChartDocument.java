package open.dolphin.client;

import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * チャートドキュメントが実装するインターフェイス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public interface ChartDocument  {
    
    public static final String CHART_DOC_DID_SAVE = "ChartDocDidSave";
	
    public String getTitle();
    
    public void setTitle(String title);
    
    public ImageIcon getIconInfo(Chart ctx);
        
    public Chart getContext();
    
    public void setContext(Chart ctx);

    public JPanel getUI();
    
    public void start();
    
    public void stop();
    
    public void enter();
    
    public void save();
    
    public void print();
    
    public boolean isDirty();
    
    public void setDirty(boolean dirty);
    
//minagawa^ Chart（インスペクタ画面）の closebox 押下に対応するため
    //保存終了を通知する機構
    public void addPropertyChangeListener(String prop, PropertyChangeListener l);
    public void removePropertyChangeListener(String prop, PropertyChangeListener l);
    public boolean isChartDocDidSave();
    public void setChartDocDidSave(boolean b);
//minagawa$    
    
}