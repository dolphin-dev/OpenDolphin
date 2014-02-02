package open.dolphin.helper;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class SimpleWorker<T, Void> extends SwingWorker<T, Void>{
    
    public SimpleWorker() {
        super();
    }
    
    protected void succeeded(T result) {
    }
    
    protected void cancelled() {
    }
    
    protected void failed(Throwable cause) {
    }

    protected void interrupte(Throwable cause) {
    }

    /**
     * タスクを実行する。
     */
    @Override
    protected void done() {

        if (isCancelled()){
            cancelled();
            return;
        }
        
        try {

            succeeded((T) get());

        } catch (InterruptedException ex) {
            interrupte(ex);

        } catch (ExecutionException ex) {
            failed(ex);

        } catch (Exception ex) {
            failed(ex);
        }
    }
}
