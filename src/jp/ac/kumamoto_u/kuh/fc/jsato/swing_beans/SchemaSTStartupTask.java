/*
 * SchemaSTStartupTask.java
 *
 * Created on 2002/07/09, 8:57
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
public class SchemaSTStartupTask {
    
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;
    
    private SchemaStockTable panel = null;
    private String schemaFolder = null;
    
    /** Creates new SchemaSTStartupTask */
    public SchemaSTStartupTask(SchemaStockTable panel, String schemaFolder) {
        
        //Compute length of task...
        lengthOfTask = 1000;
        
        this.panel = panel;
        this.schemaFolder = schemaFolder;
    }

    /**
     * Called to start the task.
     */
    public void go() {
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
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        current = lengthOfTask;
    }

    /**
     * Called to find out if the task has completed.
     */
    public boolean done() {
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
        public ActualTask() {
            //statMessage = "ŽdŽ–’†...";
            //try {Thread.sleep(100);} catch (Exception e){}
            
            if (schemaFolder != null) {
                // load image files in this directory
                panel.loadSchema(schemaFolder);
            } else {
                panel.loadDefaultSchema();
            }
            panel.validate();
            
            stop();
            return;
        }
    }// EOF class ActualTask
}