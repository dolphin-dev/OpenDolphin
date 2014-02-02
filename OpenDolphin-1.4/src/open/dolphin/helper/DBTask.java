package open.dolphin.helper;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class DBTask<T, V> extends javax.swing.SwingWorker {

    protected static final String TITLE = "DBタスク";

    protected static final String ERROR_ACCESS = "データベースアクセスエラー";

    protected static final String ERROR_TIMEOUT = "データベースアクセスにタイムアウトが生じました。";
    
    protected Chart context;

    protected boolean timeout;

    protected int taskLength = 90*1000;

    protected int delay = 10*1000;

    protected int taskDone;

    protected Timer timer;
    
    public DBTask(Chart context) {

        super();

        this.context = context;

        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                        stopProgress();
                        DBTask.this.removePropertyChangeListener(this);
                    } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                        startProgress();
                    }
                }
            }
        });

        timer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                taskDone+=delay;
                if (taskDone >= taskLength) {
                    DBTask.this.timeout();
                }
            }
        });
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
        this.cancel(true);
    }
    
    protected void startProgress() {
        Component c = context.getFrame().getGlassPane();
        if (c instanceof BlockGlass) {
            ((BlockGlass) c).setVisible(true);
        }
        context.getDocumentHistory().blockHistoryTable(true);
        context.getStatusPanel().getProgressBar().setIndeterminate(true);
        timer.start();
    }
    
    protected void stopProgress() {
        Component c = context.getFrame().getGlassPane();
        if (c instanceof BlockGlass) {
            ((BlockGlass) c).setVisible(false);
        }
        context.getDocumentHistory().blockHistoryTable(false);
        context.getStatusPanel().getProgressBar().setIndeterminate(false);
        context.getStatusPanel().getProgressBar().setValue(0);
        timer.stop();
        context = null; //
    }
    
    protected void failed(Throwable e) {
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

    protected void timeout() {
        JFrame parent = context.getFrame();
        StringBuilder sb = new StringBuilder();
        sb.append(ERROR_TIMEOUT);
        JOptionPane.showMessageDialog(
                parent,
                sb.toString(),
                ClientContext.getFrameTitle(TITLE),
                JOptionPane.WARNING_MESSAGE);
    }

    protected void succeeded(T result) {
    }

    protected void cancelled() {
    }

    protected void interrupted(Throwable e) {
    }


    @Override
    protected void done() {
        
        if (isTimeout()) {
            timeout();
            return;

        } else if (isCancelled()) {
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
