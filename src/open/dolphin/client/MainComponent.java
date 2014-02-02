package open.dolphin.client;

import javax.swing.JPanel;

/**
 *
 * @author kazm
 */
public interface MainComponent extends MainTool {
    
    public String getIcon();
    
    public void setIcon(String icon);
    
    public JPanel getUI();
    
    public void setUI(JPanel panel);
    
}
