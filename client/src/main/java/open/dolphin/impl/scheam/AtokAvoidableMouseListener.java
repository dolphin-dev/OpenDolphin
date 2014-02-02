package open.dolphin.impl.scheam;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * ATOK 2011 On でショートカットでツールを選んだあと
 * 最初の mousePressed が ATOK に取られて無視されるのの workaround
 * 
 * @author pns
 */
public class AtokAvoidableMouseListener implements MouseListener, MouseMotionListener {
    
    private StateMgr stateMgr;
    private boolean pressed = false;

    public AtokAvoidableMouseListener(StateMgr stateMgr) {
        this.stateMgr = stateMgr;
    }

    public void mouseClicked(MouseEvent e) {
        stateMgr.setMouseEvent(e);
        stateMgr.mouseClicked(e);
    }
    
    public void mousePressed(MouseEvent e) {
        pressed = true;
        stateMgr.setMouseEvent(e);
        stateMgr.mouseDown(e.getPoint());
    }
    
    public void mouseReleased(MouseEvent e) {
        // mousePressed されないで released された
        if (!pressed) {
            mousePressed(e);
            mouseClicked(e);
        }
        pressed = false;
        stateMgr.setMouseEvent(e);
        stateMgr.mouseUp(e.getPoint());
    }
    
    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
        // mousePressed されないで drag された
        if (!pressed) {
            mousePressed(e);
        }
        stateMgr.mouseDragged(e.getPoint());
    }
    
    public void mouseMoved(MouseEvent e) {}
}
