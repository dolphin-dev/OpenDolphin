/*
 * OrderEditorDialog.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.order;

import javax.swing.*;

import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class OrderEditorDialog extends JDialog implements Runnable {
          
    /** button text */
    private static final String OK_TEXT          = "ƒJƒ‹ƒe‚É“WŠJ(O)";
    private String okButtonText = OK_TEXT;
    private static final String CANCEL_TEXT      = 
        (String)UIManager.get("OptionPane.cancelButtonText") + "(C)";
    
    /** command buttons */
    final JButton okButton;
    final JButton cancelButton;
    
    PropertyChangeSupport boundSupport;
    Object value;
    
    /** Creates new OrderEditorDialog */
    public OrderEditorDialog() {
        
        super((Frame)null, true);     // create a modal dialog
        
        okButton = new JButton(okButtonText);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                value = getValue();
                notifyValue();
                close();
            }
        });
        
        cancelButton = new JButton(CANCEL_TEXT);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                value = null;
                notifyValue();
                close();
            }
        });
        
        boundSupport = new PropertyChangeSupport(this);
        
        JPanel panel = createComponent();
        getContentPane().add(panel, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                value = null;
                notifyValue();
                close();
            }
        });
        
        pack();
        Point loc = DesignFactory.getCenterLoc(getWidth(), getHeight());
        setLocation(loc.x, loc.y);
    }
    
    public void run() {
        show();
        notifyValue();
    }    
          
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void remopvePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }    
    
    public String getOkButtonText() {
        return okButtonText;
    }
    
    protected abstract JPanel createComponent();
    
    public abstract Object getValue();
    
    public abstract void setValue(Object val);    
    
    public void setOkButtonText(String text) {
        okButtonText = text + "(O)";
        okButton.setText(okButtonText);
        okButton.setMnemonic('O');
    }
            
    protected void notifyValue() {
        boundSupport.firePropertyChange("value", null, value);
    }
      
    protected void close() {
        setVisible(false);
        dispose();
    }
}