package open.dolphin.client.impl;

import javax.swing.*;

import open.dolphin.infomodel.AppointmentModel;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 * AppointLabel
 * 
 * @author  Kauzshi Minagawa, Digital Globe, Inc.
 */
public class AppointLabel extends JLabel implements DragGestureListener,DragSourceListener {
    
    private static final long serialVersionUID = 2843710174202998473L;
	
    private DragSource dragSource;
    
    /** Creates a new instance of AppointLabel */
    public AppointLabel(String text, Icon icon, int align) {
        
        super(text, icon, align);
        
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent event) {
        
        AppointmentModel appo = new AppointmentModel();
        appo.setName(this.getText());
        Transferable t = new AppointEntryTransferable(appo);
        Cursor cursor = DragSource.DefaultCopyDrop;

        // Starts the drag
        dragSource.startDrag(event, cursor, t, this);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent event) { 
    }

    @Override
    public void dragEnter(DragSourceDragEvent event) {
    }

    @Override
    public void dragOver(DragSourceDragEvent event) {
    }
    
    @Override
    public void dragExit(DragSourceEvent event) {
    }    

    @Override
    public void dropActionChanged ( DragSourceDragEvent event) {
    }       
}