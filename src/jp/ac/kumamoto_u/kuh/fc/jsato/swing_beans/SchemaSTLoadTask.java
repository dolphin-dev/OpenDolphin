/*
 * SchemaSTLoadTask.java
 *
 * Created on 2002/07/03, 10:16
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import swingworker.*;
import netscape.ldap.*;
import java.util.*;

import java.awt.*;
/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class SchemaSTLoadTask {
    
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;
    
    private SchemaStockTableLDAP panel = null;
    private Vector v = null;
    
    /** Creates new SchemaSTLoadTask */
    public SchemaSTLoadTask(SchemaStockTableLDAP panel, Vector v) {
        
        //Compute length of task...
        lengthOfTask = 1000;
        
        this.panel = panel;
        this.v = v;
    }

    /**
     * Called to start the task.
     */
    void go() {
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    /**
     * Called to find out how much work needs to be done.
     */
    int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called to find out how much has been done.
     */
    int getCurrent() {
        return current;
    }

    void stop() {
        current = lengthOfTask;
    }

    /**
     * Called to find out if the task has completed.
     */
    boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

    public String getMessage() {
        return statMessage;
    }
    
    public void setMessage(String message) {
        statMessage = message;
    }
    
    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    
    class ActualTask {
        ActualTask() {          
            //statMessage = "ŽdŽ–’†...";
            //try {Thread.sleep(100);} catch (Exception e){}

            
            int IMAGE_POSITION = 6;
            Image jpg = panel.loadJpeg(v);
            v.remove(IMAGE_POSITION);
            v.add(IMAGE_POSITION, jpg);

            
            stop();
            return;
        }
    }// EOF class ActualTask
}