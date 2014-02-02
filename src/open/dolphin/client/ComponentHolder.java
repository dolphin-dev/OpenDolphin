/*
 * ComponentHolder.java
 * Copyright (C) 2004-2006 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.event.MouseAdapter;
import javax.swing.*;

import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * ComponentHolder
 *
 * @author  Kazushi Minagawa
 */
public abstract class ComponentHolder extends JLabel implements FocusListener, MouseListener, MouseMotionListener {
    
    private static final long serialVersionUID = -5692511768525867413L;
    
    protected MouseEvent firstMouseEvent;
    
    /** Creates new ComponentHolder */
    public ComponentHolder() {
        this.setFocusable(true);
        this.addFocusListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(new PopupListner());
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ActionMap map = this.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }
    
    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
        if (e.getClickCount() == 2) {
            edit();
        }
    }
    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) {
        firstMouseEvent = e;
        e.consume();
    }
    public void mouseReleased(MouseEvent e) {
    }
    
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
    
    public void mouseMoved(MouseEvent e) { }
    
    public abstract void focusGained(FocusEvent e);
    
    public abstract void focusLost(FocusEvent e);
    
    public abstract void edit();
    
    class PopupListner extends MouseAdapter {
        
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }
    }
    
    public abstract void mabeShowPopup(MouseEvent e);
}