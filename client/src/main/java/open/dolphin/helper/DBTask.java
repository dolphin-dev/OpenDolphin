package open.dolphin.helper;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @param <T>
 * @param <V>
 */
public abstract class DBTask<T, V> extends javax.swing.SwingWorker {

    protected static final String STATE = "state";

    protected final String TITLE;

    protected final String ERROR_ACCESS;
    
    protected Chart context;

    protected PropertyChangeListener pl;
    
    public DBTask(Chart context) {

        super();

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(DBTask.class);
        TITLE = bundle.getString("title.task");
        ERROR_ACCESS = bundle.getString("error.access");
        
        this.context = context;

        pl = (PropertyChangeEvent evt) -> {
            if (STATE.equals(evt.getPropertyName())) {
                if (SwingWorker.StateValue.DONE==evt.getNewValue()) {
                    stopProgress();
                    DBTask.this.removePropertyChangeListener(pl);
                } else if (SwingWorker.StateValue.STARTED==evt.getNewValue()) {
                    startProgress();
                }
            }
        };

        this.addPropertyChangeListener(pl);
    }
    
    protected void startProgress() {
        Component c = null;
        if (context!=null && context.getFrame()!=null && context.getFrame().getGlassPane()!=null) {
            c = context.getFrame().getGlassPane();
        }
        if (c!=null && c instanceof BlockGlass) {
            ((BlockGlass) c).setVisible(true);
        }
        context.getDocumentHistory().blockHistoryTable(true);
        context.getStatusPanel().getProgressBar().setIndeterminate(true);
    }
    
    protected void stopProgress() {
        Component c = null;
        if (context!=null && context.getFrame()!=null && context.getFrame().getGlassPane()!=null) {
            c = context.getFrame().getGlassPane();
        }
        if (c !=null && c instanceof BlockGlass) {
            ((BlockGlass) c).setVisible(false);
        }
        context.getDocumentHistory().blockHistoryTable(false);
        context.getStatusPanel().getProgressBar().setIndeterminate(false);
        context.getStatusPanel().getProgressBar().setValue(0);
        //context = null; //
    }
    
    protected void failed(Throwable e) {
        e.printStackTrace(System.err);
        StringBuilder why = new StringBuilder();
        why.append(ERROR_ACCESS);
        why.append("\n");
        Throwable cause = e.getCause();
        if (cause != null) {
            why.append(cause.getMessage());
        } else {
            why.append(e.getMessage());
        }
        Window parent = SwingUtilities.getWindowAncestor(context.getFrame());
        JOptionPane.showMessageDialog(parent, why.toString(), ClientContext.getFrameTitle(TITLE), JOptionPane.WARNING_MESSAGE);
    }

    protected void succeeded(T result) {
    }

    protected void cancelled() {
    }

    protected void interrupted(Throwable e) {
    }


    @Override
    protected void done() {
        
        if (isCancelled()) {
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
