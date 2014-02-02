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
    
    @Override
    public void mouseClicked(MouseEvent e) {
        beep();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
}