package open.dolphin.helper;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;


public class TaskProgressMonitor implements PropertyChangeListener {

    private Task task;
    private TaskMonitor taskMonitor;
    private ProgressMonitor monitor;

    public TaskProgressMonitor(Task task, TaskMonitor taskMonitor,
            Component c, Object message, String note,
            int min, int max) {
        this.task = task;
        this.taskMonitor = taskMonitor;
        this.taskMonitor.addPropertyChangeListener(this);
        monitor = new ProgressMonitor(c, message, note, min, max);
        monitor.setMillisToDecideToPopup(300);
        monitor.setMillisToPopup(300);
        monitor.setProgress(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String propertyName = e.getPropertyName();

        if ("started".equals(propertyName)) {
            
        } else if ("done".equals(propertyName)) {
            monitor.close();
            taskMonitor.removePropertyChangeListener(this);
        
        } else if ("message".equals(propertyName)) {
	    String text = (String)(e.getNewValue());
            monitor.setNote(text);
	
        } else if ("progress".equals(propertyName)) {
            if (!monitor.isCanceled()) {
                int value = (Integer)(e.getNewValue());
                monitor.setProgress(value);
            } else {
                task.cancel(true);
            }
	}
    }
}
