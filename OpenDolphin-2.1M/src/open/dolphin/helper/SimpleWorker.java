package open.dolphin.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class SimpleWorker<T, Void> extends SwingWorker<T, Void> {

    private static final String STATE = "state";
    private static final String PROGRESS = "progress";
    PropertyChangeListener pcl;
    
    public SimpleWorker() {
        
        pcl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (STATE.equals(pce.getPropertyName())) {
                    if (SwingWorker.StateValue.STARTED == pce.getNewValue()) {
                        startProgress();
                    }
                    else if(SwingWorker.StateValue.DONE == pce.getNewValue()) {
                        stopProgress();
                        SimpleWorker.this.removePropertyChangeListener(pcl);
                    }
                } else if (PROGRESS.equals(pce.getPropertyName())) {
                    progress(((Integer)pce.getNewValue()).intValue());
                }
            }
        };
        this.addPropertyChangeListener(pcl);
    }

    protected void startProgress() {
    }

    protected void stopProgress() {
    }

    protected void progress(int value) {
    }
    
    protected void succeeded(T result) {
    }
    
    protected void cancelled() {
    }
    
    protected void failed(Throwable cause) {
    }

    protected void interrupted(Throwable cause) {
    }

    @Override
    protected void done() {

        if (isCancelled()){
            cancelled();
            return;
        }
        
        try {

            succeeded((T) get());

        } catch (InterruptedException ex) {
            interrupted(ex);

        } catch (ExecutionException ex) {
            failed(ex);
        }
    }
}
