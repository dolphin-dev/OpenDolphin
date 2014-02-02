/*
 * StatusPanel.java
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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

import javax.swing.*;
import java.awt.*;

/**
 * Chart plugin で共通に利用するステータスパネル。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StatusPanel extends JPanel {
    
    private final int STATUS_WIDTH = 200;
    private final int STATUS_HEIGHT = 21;
    
    private JProgressBar progressBar;
    
    private JLabel statusLabel;
        
    /** Creates a new instance of StatusPanel */
    public StatusPanel() {
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(Box.createRigidArea(new Dimension(12,0)));
        
        statusLabel = new JLabel();
        Dimension dim = new Dimension(STATUS_WIDTH, STATUS_HEIGHT);
        statusLabel.setPreferredSize(dim);
        statusLabel.setMaximumSize(dim);
        statusLabel.setMinimumSize(dim);
        this.add(statusLabel);
        
        this.add(Box.createRigidArea(new Dimension(17,0)));
               
        this.add(Box.createHorizontalGlue());
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setBorderPainted(false);
        this.add(progressBar);
        
        //this.add(Box.createHorizontalGlue());
        
        this.add(Box.createRigidArea(new Dimension(11,0)));
        
    }
    
    public void setMessage(String msg) {
        statusLabel.setText(msg);
    }
        
    public void start() {
        progressBar.setIndeterminate(true);
    }
    
    public void start(String startMsg) {
        setMessage(startMsg);
        start();   
    }
    
    public void stop() {
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
    }
    
    public void stop(String stopMsg) {
        setMessage(stopMsg);
        stop();
    }
}