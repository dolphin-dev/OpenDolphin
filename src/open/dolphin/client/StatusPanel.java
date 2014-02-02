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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Chart plugin で共通に利用するステータスパネル。
 *
 * @author  Kazushi Minagawa
 */
public class StatusPanel extends JPanel implements IStatusPanel {
    
    private static final long serialVersionUID = 4125423462560140072L;
    
    private static final int DEFAULT_HEIGHT = 23;
    
    private JLabel messageLable;
    private UltraSonicProgressLabel ultraSonic;
    private JLabel leftLabel;
    private JLabel rightLabel;
    
    /**
     * Creates a new instance of StatusPanel
     */
    public StatusPanel() {
        
        messageLable = new JLabel("");
        ultraSonic = new UltraSonicProgressLabel();
        leftLabel = new JLabel("");
        rightLabel = new JLabel("");
        Font font = GUIFactory.createSmallFont();
        leftLabel.setFont(font);
        rightLabel.setFont(font);
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        info.add(ultraSonic);
        info.add(Box.createHorizontalStrut(3));
        info.add(leftLabel);
        info.add(new SeparatorPanel());
        info.add(rightLabel);
        info.add(Box.createHorizontalStrut(11));
        this.setLayout(new BorderLayout());
        this.add(info, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(getWidth(), DEFAULT_HEIGHT));
    }
    
    public void setMessage(String msg) {
        messageLable.setText(msg);
    }
    
    private BlockGlass getBlock() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null && window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            Component cmp = frame.getGlassPane();
            if (cmp != null && cmp instanceof BlockGlass) {
                return (BlockGlass) cmp;
            }
        }
        return null;
    }
    
    public void start() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.block();
        }
        ultraSonic.start();
    }
    
    public void start(String startMsg) {
        setMessage(startMsg);
        start();
    }
    
    public void stop() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.unblock();
        }
        ultraSonic.stop();
    }
    
    public void stop(String stopMsg) {
        setMessage(stopMsg);
        stop();
    }
    
    public void setRightInfo(String info) {
        rightLabel.setText(info);
    }
    
    public void setLeftInfo(String info) {
        leftLabel.setText(info);
    }
}

