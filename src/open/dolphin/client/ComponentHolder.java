package open.dolphin.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.Position;

/**
 * IComponentHolder
 *
 * @author  Kauzshi Minagawa
 */
public interface ComponentHolder extends PropertyChangeListener, KarteComposite {
    
    public static final int TT_STAMP = 0;
    
    public static final int TT_IMAGE = 1;
    
    public int getContentType();
    
    public KartePane getKartePane();
    
    public boolean isSelected();
    
    public void setSelected(boolean b);
    
    public void edit();
    
    public void propertyChange(PropertyChangeEvent e);
    
    public void setEntry(Position start, Position end);
    
    public int getStartPos();
    
    public int getEndPos();

}