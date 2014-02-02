package open.dolphin.client;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * MouseDragDetecter
 *
 * @author Minagawa, Kazushi
 *
 */
public class MouseDragDetecter extends MouseMotionAdapter {
    
    private MouseEvent firstMouseEvent = null;
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
        if (firstMouseEvent == null) {
            firstMouseEvent = e;
            //e.consume();
            return;
            
        } else {
            // e.consume();
            
            //If they are holding down the control key, COPY rather than MOVE
            int ctrlMask = InputEvent.CTRL_DOWN_MASK;
            int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
                TransferHandler.COPY : TransferHandler.MOVE;
            
            int dx = Math.abs(e.getX() - firstMouseEvent.getX());
            int dy = Math.abs(e.getY() - firstMouseEvent.getY());
            //Arbitrarily define a 5-pixel shift as the
            //official beginning of a drag.
            if (dx > 5 || dy > 5) {
                //This is a drag, not a click.
                JComponent c = (JComponent)e.getSource();
                //Tell the transfer handler to initiate the drag.
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, firstMouseEvent, action);
                firstMouseEvent = null;
            }
        }
    }
}
