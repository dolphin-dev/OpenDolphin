package open.dolphin.client;

import java.util.concurrent.Callable;

/**
 * MainWindow Toolプラグインの抽象クラス。
 * 具象クラスは start()、stop() を実装する。
 */
public abstract class AbstractMainTool implements MainTool {
    
    private String name;
    
    private MainWindow context;
    
    
    public AbstractMainTool() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public MainWindow getContext() {
        return context;
    }
    
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    public void enter() {
    }
    
    public Callable<Boolean> getStartingTask() {
        return null;
    }
    
    public Callable<Boolean> getStoppingTask() {
        return null;
    }
    
    public abstract void start();
    
    public abstract void stop();
}
