package open.dolphin.client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;


public class TaskTimerMonitor implements PropertyChangeListener, ActionListener {

    private Task task;
    private TaskMonitor taskMonitor;
    private ProgressMonitor monitor;
    private Timer timer;
    private int current = 0;
    private int max;

    public TaskTimerMonitor(Task task, TaskMonitor taskMonitor,
            Component c, Object message, String note,
            int delay, int maxEsitimation) {
        this.task = task;
        this.taskMonitor = taskMonitor;
        max = maxEsitimation / delay;
        monitor = new ProgressMonitor(c, message, note, 0, max);
        timer = new Timer(delay, this);
    }

    public void propertyChange(PropertyChangeEvent e) {

        String propertyName = e.getPropertyName();

        if ("started".equals(propertyName)) {
            if (!timer.isRunning()) {
                timer.start();
            }

        } else if ("done".equals(propertyName)) {
            timer.stop();
            monitor.close();
            taskMonitor.removePropertyChangeListener(this);
        
        } else if ("message".equals(propertyName)) {
	    String text = (String)(e.getNewValue());
	
        } else if ("progress".equals(propertyName)) {
	    int value = (Integer)(e.getNewValue());
            monitor.setProgress(value);
//	    progressBar.setEnabled(true);
//	    progressBar.setIndeterminate(false);
//	    progressBar.setValue(value);
	}
    }

    public void actionPerformed(ActionEvent e) {
        if (monitor.isCanceled()) {
            task.cancel(true);
        } else if (current > max) {
            task.cancel(true);
        } else {
            monitor.setProgress(current++);
        }
    }
}
