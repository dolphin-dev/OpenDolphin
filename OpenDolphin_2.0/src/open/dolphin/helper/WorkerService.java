package open.dolphin.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class WorkerService implements PropertyChangeListener {

    private static final String STATE = "state";
    private static final String PROGRESS = "progress";
    
    protected SimpleWorker worker;
    
    protected void startProgress() {
    }
    
    protected void stopProgress() {
    }
    
    protected void progress(int value) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (STATE.equals(evt.getPropertyName())) {
            if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                stopProgress();
                worker.removePropertyChangeListener(this);
            } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                startProgress();
            }
        } else if (PROGRESS.equals(evt.getPropertyName())) {
            progress(((Integer)evt.getNewValue()).intValue());
        }
    }

    public void execute(SimpleWorker w) {
        this.worker = w;
        this.worker.addPropertyChangeListener(this);
        this.worker.execute();
    }
}
