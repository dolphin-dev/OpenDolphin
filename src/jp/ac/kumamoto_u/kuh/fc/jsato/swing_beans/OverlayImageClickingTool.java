/*
 * OverlayImageClickingTool.java
 *
 * Created on 2002/04/10, 14:01
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */

public abstract class OverlayImageClickingTool extends java.lang.Object {
    protected OverlayImagePanelBean panel;
    protected Vector clickedPoints = new Vector();
    protected Color penColor = Color.red;
    
    /** Holds value of property penSize. */
    private float penSize = 2.0f;
    
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    /** Creates new OverlayImageTool */
    public OverlayImageClickingTool(OverlayImagePanelBean pnl) {
        panel = pnl;
    }

    public void setPenColor(Color col) {
        penColor = col;
    }
    
    public boolean clicked(Point clickedPoint, Color col) {
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
        clickedPoints.add(clickedPoint);

        
        
        // remove all points
        clickedPoints.removeAllElements();
        
        return true;
    }
    
    public boolean doubleClicked(Point clickedPoint, Color col) {
        // remove all points
        clickedPoints.removeAllElements();

        return true;
    }
    
    public void interruptClickingTool() {
        // remove all points
        clickedPoints.removeAllElements();
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