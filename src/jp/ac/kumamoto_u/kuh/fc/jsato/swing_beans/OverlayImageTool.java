/*
 * OverlayImageTool.java
 *
 * Created on 2001/09/03, 13:22
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * @author  Junzo SATO
 * @copyright   Copyright (c) 2001, Junzo SATO. All rights reserved.
 */

public abstract class OverlayImageTool extends java.lang.Object {
    protected OverlayImagePanelBean panel;
    protected Vector draggedPoints = new Vector();
    protected Color penColor = Color.red;
    
    /** Holds value of property penSize. */
    private float penSize = 2.0f;
    
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    /** Creates new OverlayImageTool */
    public OverlayImageTool(OverlayImagePanelBean pnl) {
        panel = pnl;
    }

    public void setPenColor(Color col) {
        penColor = col;
    }
    
    public void startDragging(Point startPt, Color col) {
        if (panel.hasOverlay() == false) {
            panel.createOverlay();
        } else {
            // store current overlay to srcImg
            //
            // this is an option...
            // if you'd like to keep srcImg clean and to controll refreshing srcImg,
            // comment out the following line.
            panel.storeOverlay();
        }
        
        penColor = col;
        
        // save starting point
        draggedPoints.add(startPt);//////////////////////////////
    }
    
    public void whileDragging(Point oldPt, Point newPt) {
        // add current point
        draggedPoints.add(newPt);//////////////////////////////
    }
    
    public void stopDragging(Point endPt) {
        // keep last point
        draggedPoints.add(endPt);/////////////////////////////

        // clear all points in the list
        draggedPoints.removeAllElements();
    }
    
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /** Getter for property penSize.
     * @return Value of property penSize.
     */
    public float getPenSize() {
        return penSize;
    }
    
    /** Setter for property penSize.
     * @param penSize New value of property penSize.
     */
    public void setPenSize(float penSize) {
        float oldPenSize = this.penSize;
        this.penSize = penSize;
        propertyChangeSupport.firePropertyChange("penSize", new Float(oldPenSize), new Float(penSize));
    }
    
}
