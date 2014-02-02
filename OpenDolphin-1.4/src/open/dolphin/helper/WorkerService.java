package open.dolphin.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WorkerService implements PropertyChangeListener {
    
    protected SimpleWorker worker;
    
    protected void startProgress() {
    }
    
    protected void stopProgress() {
    }
    
    protected void progress(int value) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                stopProgress();
                worker.removePropertyChangeListener(this);
            } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                startProgress();
            }
        } else if ("progress".equals(evt.getPropertyName())) {
            progress(((Integer)evt.getNewValue()).intValue());
        }
    }

    public void execute(SimpleWorker w) {
        this.worker = w;
        this.worker.addPropertyChangeListener(this);
        this.worker.execute();
    }
}
