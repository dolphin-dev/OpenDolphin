/*
 * StampModelEditor.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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

import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *　個々の StampEditor の root クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class StampModelEditor extends JPanel implements IStampModelEditor, ComponentListener {
    
    PropertyChangeSupport boundSupport;
    boolean isValidModel;
    String title;
    IStampEditorDialog context;
    
    /** Creates new StampModelEditor */
    public StampModelEditor() {
        boundSupport = new PropertyChangeSupport(this);
        addComponentListener(this);
    }
    
    public IStampEditorDialog getContext() {
        return context;
    }
    
    public void setContext(IStampEditorDialog context) {
        this.context = context;
    }
    
    public void start() {
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public boolean isValidModel() {
        return isValidModel;
    }
    
    public void setValidModel(boolean b) {
        boolean old = isValidModel;
        isValidModel = b;
        boundSupport.firePropertyChange("validData", old, isValidModel);
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        StringBuilder buf = new StringBuilder();
        buf.append(val);
        buf.append(ClientContext.getString("application.title.editorText"));
        buf.append(ClientContext.getString("application.title.separator"));
        buf.append(ClientContext.getString("application.title"));
        this.title = buf.toString();
    }
    
    public void dispose() {
    }
    
    public abstract Object getValue();
    
    public abstract void setValue(Object value);
    
    public void componentHidden(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
    }
    
    public void componentResized(ComponentEvent e) {
        Dimension dim = getSize();
        System.out.println("width=" + dim.width + " , height=" + dim.height);
    }
    
    public void componentShown(ComponentEvent e) {
    }
}