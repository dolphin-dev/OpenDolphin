package open.dolphin.client;

import java.util.concurrent.Callable;

/**
 * MainWindow の Tool プラグインクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface MainTool extends MainService {
    
    public void enter();
    
    public Callable<Boolean> getStartingTask();
    
    public Callable<Boolean> getStoppingTask();
    
}
