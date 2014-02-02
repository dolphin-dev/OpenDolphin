package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;


public class BlockGlass extends JComponent implements MouseListener {
    
    private static final long serialVersionUID = -8073675105800821246L;
    
    public BlockGlass() {
    }
    
    public void block() {
        addMouseListener(this);
        setVisible(true);
    }
    
    public void unblock() {
        removeMouseListener(this);
        setVisible(false);
    }
    
    private void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    public void mouseClicked(MouseEvent e) {
        beep();
    }
    
    public void mousePressed(MouseEvent e) {
        //beep();
    }
    
    public void mouseReleased(MouseEvent e) {
        //beep();
    }
    
    public void mouseEntered(MouseEvent e) {
        //beep();
    }
    
    public void mouseExited(MouseEvent e) {
        //beep();
    }
}