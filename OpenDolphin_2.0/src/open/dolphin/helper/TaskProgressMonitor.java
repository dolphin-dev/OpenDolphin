package open.dolphin.helper;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * @author Kazushi Minagawa.
 */
public class TaskProgressMonitor implements PropertyChangeListener {

    private static final String STARTED = "started";
    private static final String DONE = "done";
    private static final String MESSAGE = "message";
    private static final String PROGRESS = "progress";

    private Task task;
    private TaskMonitor taskMonitor;
    private ProgressMonitor monitor;

    public TaskProgressMonitor(Task task, TaskMonitor taskMonitor,
            Component c, Object message, String note,
            int min, int max) {
        this.task = task;
        this.taskMonitor = taskMonitor;
        this.taskMonitor.addPropertyChangeListener(TaskProgressMonitor.this);
        monitor = new ProgressMonitor(c, message, note, min, max);
        monitor.setMillisToDecideToPopup(300);
        monitor.setMillisToPopup(300);
        monitor.setProgress(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String propertyName = e.getPropertyName();

        if (STARTED.equals(propertyName)) {
            
        } else if (DONE.equals(propertyName)) {
            monitor.close();
            taskMonitor.removePropertyChangeListener(this);
        
        } else if (MESSAGE.equals(propertyName)) {
	    String text = (String)(e.getNewValue());
            monitor.setNote(text);
	
        } else if (PROGRESS.equals(propertyName)) {
            if (!monitor.isCanceled()) {
                int value = (Integer)(e.getNewValue());
                monitor.setProgress(value);
            } else {
                task.cancel(true);
            }
	}
    }
}
