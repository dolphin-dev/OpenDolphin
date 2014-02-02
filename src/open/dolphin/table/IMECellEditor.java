package open.dolphin.table;

import javax.swing.*;
import java.awt.event.*;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.AutoRomanListener;

/**
 * IMECellEditor
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class IMECellEditor extends DefaultCellEditor {
    
    private static final long serialVersionUID = 6940297554018543284L;

	/** Creates a new instance of DTableCellEditor */
    public IMECellEditor(final JTextField tf, final int clickCount, final boolean on) {
        
        super(tf);
        
        int ccts = clickCount == 1 ? 1 : 2;
        setClickCountToStart(ccts);
        
        if (on) {

            // IME ‚ðON‚É‚·‚é Windows ‚Ì‚Ý‚É—LŒø
            tf.addFocusListener(AutoKanjiListener.getInstance());
        
        } else {
            // IME ‚ðOFF‚É‚·‚é Windows ‚Ì‚Ý‚É—LŒø
            tf.addFocusListener(AutoRomanListener.getInstance());
        }
    }  
}
