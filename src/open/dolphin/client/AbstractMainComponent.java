package open.dolphin.client;

import java.util.concurrent.Callable;
import javax.swing.JPanel;

/**
 * Main Window コンポーネントプラグインの抽象クラス。
 * 具象クラスは start()、stop() を実装する。
 */
public abstract class AbstractMainComponent implements MainComponent {
    
    private String name;
    
    private String icon;
    
    private MainWindow context;
    
    private JPanel ui;
    
    public AbstractMainComponent() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public MainWindow getContext() {
        return context;
    }
    
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel ui) {
        this.ui = ui;
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
