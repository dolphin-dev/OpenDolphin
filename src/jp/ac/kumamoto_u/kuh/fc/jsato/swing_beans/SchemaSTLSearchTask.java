/*
 * SchemaSTLSearchTask.java
 *
 * Created on 2002/07/02, 16:10
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import swingworker.*;
import netscape.ldap.*;
import java.util.*;
/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class SchemaSTLSearchTask {
    
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;
    private SchemaStockTableLDAP panel = null;
    
    /** Creates new SchemaSTLSearchTask */
    public SchemaSTLSearchTask(SchemaStockTableLDAP panel) {
        
        //Compute length of task...
        lengthOfTask = 1000;
        
        this.panel = panel;
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
            
            panel.constructTable();
            
            stop();
            return;
        }
    }// EOF class ActualTask
}