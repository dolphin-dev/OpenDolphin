package open.dolphin.client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

/**
 * ComponentHolder
 *
 * @author  Kazushi Minagawa
 */
public abstract class AbstractComponentHolder extends JLabel {  //implements MouseListener, MouseMotionListener {
    
    protected static final Color SELECTED_BORDER = new Color(255, 0, 153);
    
    /** Creates new ComponentHolder */
    public AbstractComponentHolder() {
        
        this.putClientProperty("karteCompositor", AbstractComponentHolder.this);
        this.setFocusable(true);
        
        // Double Click
        this.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // StampEditor から戻った後に動作しないため
                boolean focus = requestFocusInWindow();

                if (!focus) {
                    requestFocus();
                }

                if (e.getClickCount()==2 && (!e.isPopupTrigger())) {
                    edit();
                }
            }
        });
        
        // Dragg
        DragDetect dt = new DragDetect();
        this.addMouseListener(dt);
        this.addMouseMotionListener(dt);
        
        // Popup
        this.addMouseListener(new PopupListner());
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ActionMap map = this.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }
    
    class DragDetect implements MouseListener, MouseMotionListener {
    
        private MouseEvent firstMouseEvent;
        
        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) {
            firstMouseEvent = e;
            e.consume();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (firstMouseEvent != null) {

                e.consume();

                //If they are holding down the control key, COPY rather than MOVE
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                ? TransferHandler.COPY
                        : TransferHandler.MOVE;

                int dx = Math.abs(e.getX() - firstMouseEvent.getX());
                int dy = Math.abs(e.getY() - firstMouseEvent.getY());

                if (dx > 5 || dy > 5) {
                    JComponent c = (JComponent) e.getSource();
                    TransferHandler handler = c.getTransferHandler();
                    handler.exportAsDrag(c, firstMouseEvent, action);
                    firstMouseEvent = null;
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) { }

        @Override
        public void mouseClicked(MouseEvent me) {}
    }
    
    public abstract void edit();
    
    class PopupListner extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount()!=2) {
                mabeShowPopup(e);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount()!=2) {
                mabeShowPopup(e);
            }
        }
    }
    
    public abstract void mabeShowPopup(MouseEvent e);
}