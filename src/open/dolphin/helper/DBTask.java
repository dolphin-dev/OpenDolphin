package open.dolphin.helper;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import org.apache.log4j.Logger;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class DBTask<T> {
    
    protected Chart context;
    protected long timeout = 60*1000L;
    protected JProgressBar progressBar;
    protected BlockGlass blockGlass;
    protected Logger logger;
    
    public DBTask(Chart context) {
        this.context = context;
        progressBar = this.context.getStatusPanel().getProgressBar();
        JFrame f = this.context.getFrame();
        Component c = f.getGlassPane();
        if (c instanceof BlockGlass) {
            blockGlass = (BlockGlass) c;
        }
        logger = ClientContext.getBootLogger();
    }
    
    protected abstract T doInBackground() throws Exception;
    
    protected void succeeded(T result) {
    }
    
    protected void cancelled() {
        logger.debug("DBTask cancelled");
    }
    
    protected void timeout() {
        JFrame parent = context.getFrame();
        StringBuilder sb = new StringBuilder();
        sb.append("データベースアクセスにタイムアウトが生じました。");
        sb.append("\n");
        sb.append("リトライをお願いします。");
        String title = "DBタスク";
        JOptionPane.showMessageDialog(
                parent,
                sb.toString(),
                ClientContext.getFrameTitle(title),
                JOptionPane.WARNING_MESSAGE);
        logger.debug("DBTask timeout");
    }
    
    protected void failed(Throwable cause) {
        logger.warn("DBTask failed");
        logger.warn(cause);
    }
    
    private void startProgress() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (blockGlass != null) {
                    blockGlass.setVisible(true);
                }
                context.getDocumentHistory().blockHistoryTable(true);
                progressBar.setIndeterminate(true);
            }
        });
    }
    
    private void stopProgress() {
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        if (blockGlass != null) {
            blockGlass.setVisible(false);
        }
        context.getDocumentHistory().blockHistoryTable(false);
    }
    
    private void taskDone(final T result) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                stopProgress();
                succeeded(result);
            }
        });
    }
    
    private void taskFailed(final Throwable cause) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                stopProgress();
                failed(cause);
            }
        });
    }
    
    private void taskTimeout() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                stopProgress();
                timeout();
            }
        });
    }
    
    public void execute() {
        
        Runnable r = new Runnable() {
            
            public void run() {
                startProgress();
                try {
                    Callable c = new Callable<T>() {
                        public T call() throws Exception {
                            return doInBackground();
                        }
                    };
                    FutureTask task = new FutureTask<T>(c);
                    new Thread(task).start();
                    T result = (T) task.get(timeout, TimeUnit.SECONDS);
                    taskDone(result);
                } catch (InterruptedException ex) {
                    logger.warn(ex);
                } catch (ExecutionException ex) {
                    taskFailed(ex);
                } catch (TimeoutException ex) {
                    taskTimeout();
                } catch (Exception ex) {
                    taskFailed(ex);
                }
            }            
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
}
