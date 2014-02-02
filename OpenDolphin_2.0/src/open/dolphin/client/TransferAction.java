package open.dolphin.client;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author Kazushi Minagawa.
 */
public class TransferAction implements ActionListener {
    
    private JComponent comp;
    private TransferHandler handler;
    private Transferable tr;
    
    public TransferAction(JComponent comp, TransferHandler handler, Transferable tr) {
        this.comp = comp;
        this.handler = handler;
        this.tr = tr;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        handler.importData(comp, tr);
    }
}
