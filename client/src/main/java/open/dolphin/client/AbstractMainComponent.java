package open.dolphin.client;

import java.util.concurrent.Callable;
import javax.swing.JPanel;

/**
 * Main Window プラグインの抽象クラス。
 * 来院リスト、患者検索、ラボレシーバ等
 * 具象クラスは start()、stop() を実装する。
 */
public abstract class AbstractMainComponent implements MainComponent {
    
    private String name;
    
    private String icon;
    
    private MainWindow context;
    
    private JPanel ui;
    
    public AbstractMainComponent() {
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
    public String getIcon() {
        return icon;
    }
    
    @Override
    public void setIcon(String icon) {
        this.icon = icon;
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
    public JPanel getUI() {
        return ui;
    }
    
    @Override
    public void setUI(JPanel ui) {
        this.ui = ui;
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
