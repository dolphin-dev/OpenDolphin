/*
 * AboutDialog.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import open.dolphin.util.DesignFactory;

/**
 * About dialog
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AboutDialog extends JDialog {

    /** Creates new AboutDialog */
    public AboutDialog(Frame f, String title, String imageFile) {
    	
        super(f, title, true);
        
        StringBuffer buf = new StringBuffer();
        buf.append(ClientContext.getString("productString"));
        buf.append("  Ver.");
        buf.append(ClientContext.getString("version"));
        String version = buf.toString();
        String[] copyright = ClientContext.getStringArray("copyrightString");
        
        Object[] message = new Object[] {
            ClientContext.getImageIcon(imageFile), 
            version,
            copyright[0],
			copyright[1],
        };
        String[] options = {"•Â‚¶‚é"};
        JOptionPane optionPane = new JOptionPane(message,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 null,
                                                 options,
                                                 options[0]);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                    close();
                }
            }
        });
        this.getContentPane().add(optionPane, BorderLayout.CENTER);
        this.pack();
        Point loc = DesignFactory.getCenterLoc(this.getWidth(), this.getHeight());
        this.setLocation(loc);
        this.setVisible(true);
    }
    
    private void close() {
        this.setVisible(false);
        this.dispose();
    }
}