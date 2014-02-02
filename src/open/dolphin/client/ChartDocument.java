package open.dolphin.client;

import javax.swing.JPanel;


/**
 * チャートドキュメントが実装するインターフェイス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public interface ChartDocument  {
	
    public String getTitle();
    
    public void setTitle(String title);
        
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
    
}