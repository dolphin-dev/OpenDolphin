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

/**
 *
 * @author Kazushi Minagawa.
 */
public class TaskTimerMonitor implements PropertyChangeListener, ActionListener {

    private static final String STARTED = "started";
    private static final String DONE = "done";
    private static final String MESSAGE = "message";
    private static final String PROGRESS = "progress";

    private Task task;
    private TaskMonitor taskMonitor;
    private ProgressMonitor monitor;
    private Timer timer;
    private int current = 0;
    private int max;

    public TaskTimerMonitor(Task task,
                            TaskMonitor taskMonitor,
                            Component c,
                            Object message,
                            String note,
                            int delay,
                            int maxEsitimation) {
        this.task = task;
        this.taskMonitor = taskMonitor;
        this.taskMonitor.addPropertyChangeListener(TaskTimerMonitor.this);
        max = maxEsitimation / delay;
        monitor = new ProgressMonitor(c, message, note, 0, max);
        timer = new Timer(delay, this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {

        String propertyName = e.getPropertyName();

        if (STARTED.equals(propertyName)) {
            if (!timer.isRunning()) {
                timer.start();
            }

        } else if (DONE.equals(propertyName)) {
            timer.stop();
            monitor.close();
            taskMonitor.removePropertyChangeListener(this);
        
        } else if (MESSAGE.equals(propertyName)) {
	    String text = (String)(e.getNewValue());
            monitor.setNote(text);
	
        } else if (PROGRESS.equals(propertyName)) {
	    int value = (Integer)(e.getNewValue());
            monitor.setProgress(value);
	}
    }

    @Override
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
