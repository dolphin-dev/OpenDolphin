/*
 * AnimationLabel.java
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
package open.dolphin.util;

import javax.swing.*;
import java.awt.event.*;

/**
 * JLabel to perform animation.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AnimationLabel extends JLabel implements ActionListener {
    
    int stillFrame = -1;
    int fps;
    int frameNumber;
    int delay;
    Timer timer;
    ImageIcon[] frames;

    public AnimationLabel() {
    }
    
    public int getFPS() {
        return fps;
    }
    
    public void setFPS(int val) {
        fps = val;
    }
    
    public int getStillFrame() {
        return stillFrame;
    }
    
    public void setStillFrame(int val) {
        stillFrame = val > 0 ? val -1 : -1;
        if (frames != null) {
            setStillIcon();
        }
    }
    
    public void setup(ImageIcon[] frames) {
        
        this.frames = frames;

        //How many milliseconds between frames?
        delay = (fps > 0) ? (1000 / fps) : 100;

        //Set up a timer that calls this object's action handler
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
    }
    
    public void setStillIcon() {
        if ((stillFrame > -1) && (stillFrame < frames.length)) {
            setIcon(frames[stillFrame]);
        }
        else {
            setIcon(null);
        }
    }

    public synchronized void start() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public synchronized void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
        frameNumber = -1;
        setStillIcon();
    }

    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
        frameNumber++;

        //Display it.
        setIcon(frames[frameNumber % frames.length]);
    }
}