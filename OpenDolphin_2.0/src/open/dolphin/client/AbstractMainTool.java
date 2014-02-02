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
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public MainWindow getContext() {
        return context;
    }
    
    @Override
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    @Override
    public void enter() {
    }
    
    @Override
    public Callable<Boolean> getStartingTask() {
        return null;
    }
    
    @Override
    public Callable<Boolean> getStoppingTask() {
        return null;
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
}
