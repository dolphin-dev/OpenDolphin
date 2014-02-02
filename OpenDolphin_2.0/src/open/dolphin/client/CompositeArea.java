package open.dolphin.client;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.ActionMap;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author Kazushi Minagawa.
 */
public class CompositeArea extends JTextArea implements KarteComposite, CaretListener {
    
    private boolean hasSelection;
    private ActionMap map;
    
    /** Creates a new instance of CompositeArea */
    public CompositeArea() {
        this.addCaretListener(CompositeArea.this);
    }
    
    public CompositeArea(int row, int col) {
        super(row, col);
        this.addCaretListener(CompositeArea.this);
    }
    
    @Override
    public void enter(ActionMap map) {  
        this.map = map;
        map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
    }
    
    @Override
    public void exit(ActionMap map) {
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void caretUpdate(CaretEvent e) {
        boolean newSelection =  (e.getDot() != e.getMark()) ? true : false;
        if (newSelection != hasSelection) {
            hasSelection = newSelection;
            map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
            map.get(GUIConst.ACTION_CUT).setEnabled(hasSelection);
            map.get(GUIConst.ACTION_COPY).setEnabled(hasSelection);
        }
    }
    
    private boolean canPaste() {
        
        boolean ret = false;
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return false;
        }
        
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return true;
        }
        
        return false;
    }
}
