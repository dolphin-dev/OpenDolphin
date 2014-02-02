package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;


public class BlockGlass extends JComponent implements MouseListener {
    
    public BlockGlass() {
    }
    
    public void block() {
        this.addMouseListener(this);
        this.setVisible(true);
    }
    
    public void unblock() {
        this.removeMouseListener(this);
        this.setVisible(false);
    }
    
    private void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    public void mouseClicked(MouseEvent e) {
        beep();
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
}